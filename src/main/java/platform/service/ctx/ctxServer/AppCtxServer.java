package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import common.struct.SensorData;
import common.struct.enumeration.SensorDataType;
import platform.communication.pubsub.Publisher;
import platform.config.AppConfig;
import platform.service.ctx.ctxChecker.CheckerStarter;
import platform.service.ctx.item.Item;
import platform.service.ctx.item.ItemManager;
import platform.service.ctx.pattern.PatternManager;
import platform.service.ctx.rule.RuleManager;
import platform.service.ctx.rule.resolver.ResolverType;
import platform.service.ctx.statistics.ServerStatistics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class AppCtxServer extends AbstractCtxServer{

    private final AppConfig appConfig;
    private final ReentrantLock resetLock = new ReentrantLock();

    public AppCtxServer(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public void init() {
        this.patternManager = new PatternManager(this);
        this.patternManager.buildPatterns(appConfig.getPatternFile(), appConfig.getMfuncFile());
        this.ruleManager = new RuleManager(this, ResolverType.IN_TIME);
        this.ruleManager.buildRules(appConfig.getRuleFile(), null);
        this.itemManager = new ItemManager(this);
        this.changeGenerator = new ChangeGenerator(this);
        this.checker = new CheckerStarter(this, appConfig.getBfuncFile(), appConfig.getCtxValidator().toString());
        this.ctxFixer = new CtxFixer(this);
        this.serverStatistics = new ServerStatistics();
    }

    public void reset(){
        //停止发送数据
        t.interrupt();
        while(t.isInterrupted());
        //停止接收数据
        resetLock.lock();
        try {
            this.patternManager.reset(appConfig.getPatternFile(), appConfig.getMfuncFile());
            this.ruleManager.reset(appConfig.getRuleFile(), null, ResolverType.IN_TIME);
            this.itemManager.reset();
            this.changeGenerator.reset();
            this.ctxFixer.reset();
            this.checker = new CheckerStarter(this, appConfig.getBfuncFile(), appConfig.getCtxValidator().toString());
            this.serverStatistics = new ServerStatistics();
            this.restart();
        } finally {
            resetLock.unlock();
        }
    }

    public void stop(){
        //停止发送数据
        t.interrupt();
        while(t.isInterrupted());
        //停止接收数据
        resetLock.lock();
        try{
            this.patternManager = null;
            this.ruleManager = null;
            this.itemManager = null;
            this.checker = null;
            this.changeGenerator = null;
            this.ctxFixer = null;
            this.serverStatistics = null;
        } finally {
            resetLock.unlock();
        }
    }


    @Override
    public void onMessage(String channel, String msg) {
        if(msg == null){
            return;
        }

        //如果无法获取到锁，说明正在reset/stop
        if(!resetLock.tryLock()){
            logger.debug(appConfig.getAppName() + "-CtxServer ignores: " + msg);
            return;
        }

        try{
            logger.debug(appConfig.getAppName() + "-CtxServer recv: " + msg);
            //System.out.printf("%s-CtxServer recv %s %n",appConfig.getAppName(), msg);

            SensorData sensorData = SensorData.fromJSONString(msg);
            assert sensorData.getType() == SensorDataType.MSG;
            Item item = itemManager.addItem(channel, sensorData);
            serverStatistics.increaseReceivedMsgNum();
            assert appConfig.isCtxServerOn();
            checker.check(item.getContext());

        } finally {
            resetLock.unlock();
        }
    }

    @Override
    public void run() {
        while(true){
            if(Thread.interrupted()){
                return;
            }
            for(String channel : itemManager.getChannel2IndexQue().keySet()){
                ConcurrentLinkedQueue<Long> indexQue = itemManager.getChannel2IndexQue().get(channel);
                if(indexQue.isEmpty()){
                    continue;
                }
                long sendIndex = indexQue.peek();
                if(!itemManager.getValidatedItemMap().containsKey(sendIndex)){
                    continue;
                }
                Item validatedItem = itemManager.getValidatedItemMap().get(sendIndex);
                //发送消息
                Map.Entry<String, JSONObject> validatedItemObj = itemManager.buildValidatedItemJsonObj(validatedItem);
                if(validatedItemObj == null){
                    //被drop了
                    //TODO()
                }
                else{
                    logger.debug(appConfig.getAppName() + "-CtxServer pub " + validatedItemObj.getValue().toJSONString()  +" from " + validatedItemObj.getKey() + " to " + appConfig.getAppName());
                    Publisher.publish(validatedItemObj.getKey(), appConfig.getGrpId(), 0, validatedItemObj.getValue().toJSONString());
                    serverStatistics.increaseSentMsgNum();
                }
                itemManager.removeValidatedItem(sendIndex);
                itemManager.removeItem(sendIndex);
                indexQue.poll();
            }
        }
    }
}
