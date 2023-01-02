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
            // 重新构建Rules
            this.ruleMap.clear();
            this.resolverMap.clear();
            buildRules(appConfig.getRuleFile(), null);
            // 清除旧数据
            this.originalMsgMap.clear();
            this.sendIndexQue.clear();
            this.chgGenerator.reset();
            this.ctxFixer.reset();

            // reset后从validMsgIndexLimit开始接收
            this.validMsgIndexLimit = PlatformCtxServer.getInstance().getMsgIndex().get() + 1L;
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
        JSONObject msgJsonObj = JSONObject.parseObject(msg);
        long msgIndex = Long.parseLong(msgJsonObj.getString("index"));
        //如果无法获取到锁，说明正在reset
        if(!resetLock.tryLock()){
            logger.debug(appConfig.getAppName() + "-CtxServer ignores: " + msg);
            return;
        }

        try{
            //判断是否是reset之前的消息
            if(msgIndex < validMsgIndexLimit) {
                logger.debug(appConfig.getAppName() + "-CtxServer ignores: " + msg);
                return;
            }

            logger.debug(appConfig.getAppName() + "-CtxServer recv: " + msg);
            //System.out.printf("%s-CtxServer recv %s %n",appConfig.getAppName(), msg);

            Message originalMsg = MessageHandler.jsonObject2Message(msgJsonObj);

            if(originalMsg == null){
                return;
            }

            addOriginalMsg(originalMsg);
            addSendIndex(originalMsg.getIndex());
            serverStatistics.increaseReceivedMsgNum();
            if(appConfig.isCtxServerOn()){
                List<Map.Entry<List<ContextChange>, ChgListType>> changesList = chgGenerator.generateChanges(originalMsg.getContextMap());
                checker.check(changesList);
            }
            else{
                for(String contextId : originalMsg.getContextMap().keySet()){
                    ctxFixer.addFixedContext(contextId, MessageHandler.cloneContext(originalMsg.getContextMap().get(contextId)));
                }
            }
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
            Message originalMsg = getOriginalMsg(sendIndex);
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
            else{
                logger.debug(appConfig.getAppName() + "-CtxServer drop incomplete msg " + pubJSONObj.toJSONString());
            }

            ctxFixer.getSendingMsgMap().remove(sendIndex);
            originalMsgMap.remove(sendIndex);
            sendIndexQue.poll();
        }
    }
}
