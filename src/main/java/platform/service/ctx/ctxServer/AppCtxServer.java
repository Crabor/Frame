package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import platform.config.AppConfig;
import platform.config.SubConfig;
import platform.service.ctx.ctxChecker.CheckerStarter;
import platform.service.ctx.ctxChecker.middleware.checkers.Checker;
import platform.service.ctx.message.Message;
import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.statistics.ServerStatistics;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class AppCtxServer extends AbstractCtxServer{
    private final AppConfig appConfig;
    private long validMsgIndexLimit;
    private final ReentrantLock resetLock = new ReentrantLock();

    public AppCtxServer(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.validMsgIndexLimit = 0L;
        this.patternMap = new HashMap<>();
        this.ruleMap = new HashMap<>();
        this.resolverMap = new HashMap<>();
        this.serverStatistics = new ServerStatistics();
    }

    public void reset(){
        //停止发送数据
        t.interrupt();
        while(t.isInterrupted());
        //停止接收数据
        resetLock.lock();
        try {
            // 停止chgGenerator
            this.chgGenerator.reset();
            // 停止checker
            this.checker.reset();
            // 重新构建Pattern和Rules
            this.patternMap.clear();
            buildPatterns(appConfig.getPatternFile(), appConfig.getMfuncFile());
            this.ruleMap.clear();
            this.resolverMap.clear();
            buildRules(appConfig.getRuleFile());
            // 清除旧数据
            this.originalMsgMap.clear();
            this.changeBuffer.clear();
            this.ctxFixer.reset();

            // reset后从validMsgIndexLimit开始接收
            this.validMsgIndexLimit = PlatformCtxServer.getInstance().getMsgIndex().get() + 1L;
            toSendIndex.set(this.validMsgIndexLimit);
            // 重启服务
            this.restart();
            this.chgGenerator.restart();
            this.checker = new CheckerStarter(this, appConfig.getBfuncFile(), appConfig.getCtxValidator());
            this.checker.start();
        } finally {
            resetLock.unlock();
        }
    }

    @Override
    public void init() {
        buildPatterns(appConfig.getPatternFile(), appConfig.getMfuncFile());
        buildRules(appConfig.getRuleFile());
        this.chgGenerator = new ChgGenerator(this);
        this.chgGenerator.start();
        this.ctxFixer = new CtxFixer(this);
        this.checker = new CheckerStarter(this, appConfig.getBfuncFile(), appConfig.getCtxValidator());
        this.checker.start();
    }

    @Override
    public void onMessage(String channel, String msg) {
        JSONObject msgJsonObj = JSONObject.parseObject(msg);
        long msgIndex = Long.parseLong(msgJsonObj.getString("index"));
        //如果无法获取到锁，说明正在reset
        if(!resetLock.tryLock()){
            skippedSendIndex.add(msgIndex);
            logger.debug(appConfig.getAppName() + "-CtxServer ignores: " + msg);
            return;
        }
        resetLock.unlock();

        //判断是否是reset之前的消息
        if(msgIndex < validMsgIndexLimit) {
            skippedSendIndex.add(msgIndex);
            logger.debug(appConfig.getAppName() + "-CtxServer ignores: " + msg);
            return;
        }

        logger.debug(appConfig.getAppName() + "-CtxServer recv: " + msg);
        //System.out.printf("%s-CtxServer recv %s %n",appConfig.getAppName(), msg);


        Message originalMsg = MessageHandler.jsonObject2Message(msgJsonObj);

        if(originalMsg == null){
            skippedSendIndex.add(msgIndex);
            return;
        }

        addOriginalMsg(originalMsg);
        serverStatistics.increaseReceivedMsgNum();
        if(appConfig.isCtxServerOn()){
            chgGenerator.generateChanges(originalMsg.getContextMap());
        }
        else{
            for(String contextId : originalMsg.getContextMap().keySet()){
                ctxFixer.addFixedContext(contextId, MessageHandler.cloneContext(originalMsg.getContextMap().get(contextId)));
            }
        }
    }

    @Override
    public void run() {
        while(true){
            if(Thread.interrupted()){
                return;
            }
            while(skippedSendIndex.contains(toSendIndex.get())){
                skippedSendIndex.remove(toSendIndex.get());
                toSendIndex.getAndIncrement();
            }
            if(!ctxFixer.getSendingMsgMap().containsKey(toSendIndex.get()))
                continue;
            Message sendingMsg = ctxFixer.getSendingMsgMap().get(toSendIndex.get());
            Message originalMsg = getOriginalMsg(sendingMsg.getIndex());
            //发送消息
            JSONObject pubJSONObj = MessageHandler.buildPubJSONObjWithoutIndex(sendingMsg, originalMsg.getSensorInfos(appConfig.getAppName()));
            //only pub non-null context Msg
            if(!pubJSONObj.containsValue("")){
                SubConfig sensorPubConfig = null;
                for(SubConfig subConfig : appConfig.getSubConfigs()){
                    if(subConfig.channel.equals("sensor")){
                        sensorPubConfig = subConfig;
                    }
                }
                assert sensorPubConfig != null;
                logger.debug(appConfig.getAppName() + "-CtxServer pub " + pubJSONObj.toJSONString() +" to " + appConfig.getAppName());
                //System.out.printf("%s-CtxServer publish %s to {sensor, %d, %d} %n", appConfig.getAppName(), pubMsgStr, sensorPubConfig.groupId, sensorPubConfig.priorityId);
                publish("sensor", sensorPubConfig.groupId, sensorPubConfig.priorityId, pubJSONObj.toJSONString());
                serverStatistics.increaseSentMsgNum();
            }

            ctxFixer.getSendingMsgMap().remove(toSendIndex.get());
            originalMsgMap.remove(toSendIndex.get());
            toSendIndex.getAndIncrement();
        }
    }
}
