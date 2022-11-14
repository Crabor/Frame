package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import platform.config.AppConfig;
import platform.config.SubConfig;
import platform.service.ctx.ctxChecker.CheckerStarter;
import platform.service.ctx.message.Message;
import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.statistics.ServerStatistics;

import java.util.HashMap;

public class AppCtxServer extends AbstractCtxServer{
    private final AppConfig appConfig;

    public AppCtxServer(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.patternMap = new HashMap<>();
        this.ruleMap = new HashMap<>();
        this.resolverMap = new HashMap<>();
        this.serverStatistics = new ServerStatistics();
    }

    @Override
    public void init() {
        buildPatterns(appConfig.getPatternFile(), appConfig.getMfuncFile());
        buildRules(appConfig.getRuleFile());
        this.chgGenerator = new ChgGenerator(this);
        this.chgGenerator.start();
        this.ctxFixer = new CtxFixer(this);
        Thread checker =new Thread(new CheckerStarter(
                this, appConfig.getBfuncFile(), appConfig.getCtxValidator())
        );
        checker.start();
    }

    @Override
    public void onMessage(String channel, String msg) {
        logger.debug(appConfig.getAppName() + "-CtxServer recv: " + msg);
        //System.out.printf("%s-CtxServer recv %s %n",appConfig.getAppName(), msg);

        JSONObject msgJsonObj = JSONObject.parseObject(msg);
        Message originalMsg = MessageHandler.jsonObject2Message(msgJsonObj);

        if(originalMsg == null){
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
            if(!ctxFixer.getSendingMsgMap().containsKey(toSendIndex))
                continue;
            Message sendingMsg = ctxFixer.getSendingMsgMap().get(toSendIndex);
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
                //System.out.printf("%s-CtxServer publish %s to {sensor, %d, %d} %n", appConfig.getAppName(), pubMsgStr, sensorPubConfig.groupId, sensorPubConfig.priorityId);
                publish("sensor", sensorPubConfig.groupId, sensorPubConfig.priorityId, pubJSONObj.toJSONString());
                serverStatistics.increaseSentMsgNum();
            }

            ctxFixer.getSendingMsgMap().remove(toSendIndex);
            originalMsgMap.remove(toSendIndex);
            toSendIndex++;
        }
    }
}
