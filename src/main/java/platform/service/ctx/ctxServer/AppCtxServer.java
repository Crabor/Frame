package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import platform.config.AppConfig;
import platform.config.SubConfig;
import platform.service.ctx.ctxChecker.CheckerStarter;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.ctxChecker.middleware.checkers.Checker;
import platform.service.ctx.message.Message;
import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.statistics.ServerStatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class AppCtxServer extends AbstractCtxServer{

    private final AtomicLong atomicLong;
    private final AppConfig appConfig;
    private final ReentrantLock resetLock = new ReentrantLock();

    public AppCtxServer(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.patternMap = new HashMap<>();
        this.ruleMap = new HashMap<>();
        this.resolverMap = new HashMap<>();
        this.serverStatistics = new ServerStatistics();
        this.atomicLong = new AtomicLong();
    }

    public void reset(){
        //停止发送数据
        t.interrupt();
        while(t.isInterrupted());
        //停止接收数据
        resetLock.lock();
        try {
            // 重新构建Rules
            this.ruleMap.clear();
            this.resolverMap.clear();
            buildRules(appConfig.getRuleFile(), null);
            // 清除旧数据
            this.originalMsgMap.clear();
            this.sendIndexQue.clear();
            this.chgGenerator.reset();
            this.ctxFixer.reset();

            // 重启服务
            this.restart();
            this.checker = new CheckerStarter(this, appConfig.getBfuncFile(), appConfig.getCtxValidator().toString());
        } finally {
            resetLock.unlock();
        }
    }

    @Override
    public void init() {
        buildPatterns(appConfig.getPatternFile(), appConfig.getMfuncFile());
        buildRules(appConfig.getRuleFile(), null); //currently only-use drop-latest
        this.chgGenerator = new ChgGenerator(this);
        this.checker = new CheckerStarter(this, appConfig.getBfuncFile(), appConfig.getCtxValidator().toString());
        this.ctxFixer = new CtxFixer(this);
    }

    @Override
    public void onMessage(String channel, String msg) {
        //如果无法获取到锁，说明正在reset
        if(!resetLock.tryLock()){
            logger.debug(appConfig.getAppName() + "-CtxServer ignores: " + msg);
            return;
        }

        try{
            logger.debug(appConfig.getAppName() + "-CtxServer recv: " + msg);
            //System.out.printf("%s-CtxServer recv %s %n",appConfig.getAppName(), msg);

            JSONObject msgJsonObj = JSONObject.parseObject(msg);
            long msgIndex = atomicLong.getAndIncrement();
            Message originalMsg = MessageHandler.buildMsg(msgIndex, channel, msgJsonObj);

            addOriginalMsg(originalMsg);
            addSendIndex(originalMsg.getIndex());
            serverStatistics.increaseReceivedMsgNum();

            assert appConfig.isCtxServerOn();
            List<Map.Entry<List<ContextChange>, ChgListType>> changesList = chgGenerator.generateChanges(originalMsg.getContextMap());
            checker.check(changesList);
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
            if(sendIndexQue.isEmpty())
                continue;
            long sendIndex = sendIndexQue.peek();
            if(!ctxFixer.getSendingMsgMap().containsKey(sendIndex))
                continue;
            Message sendingMsg = ctxFixer.getSendingMsgMap().get(sendIndex);
            //发送消息
            //每个msg只有一个sensor (框架2.1)
            Map.Entry<String, JSONObject> pubMsgObj = MessageHandler.buildPubMsgObj(sendingMsg);
            if(pubMsgObj != null){
                logger.debug(appConfig.getAppName() + "-CtxServer pub " + pubMsgObj.getValue().toJSONString()  +" from " + pubMsgObj.getKey() + " to " + appConfig.getAppName());
                publish(pubMsgObj.getKey(), appConfig.getGrpId(), 0, pubMsgObj.getValue().toJSONString());
                serverStatistics.increaseSentMsgNum();
            }
            ctxFixer.getSendingMsgMap().remove(sendIndex);
            originalMsgMap.remove(sendIndex);
            sendIndexQue.poll();
        }
    }
}
