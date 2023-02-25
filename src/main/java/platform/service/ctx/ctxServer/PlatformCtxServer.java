package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import common.struct.CtxServiceConfig;
import common.struct.ServiceConfig;
import platform.config.AppConfig;
import platform.config.Configuration;
import platform.config.CtxServerConfig;
import platform.config.SubConfig;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.message.Message;
import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.ctxChecker.CheckerStarter;
import platform.service.ctx.statistics.ServerStatistics;
import common.struct.enumeration.CmdType;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class PlatformCtxServer extends AbstractCtxServer {

    //如果支持新增ctx，那应该多设置一个index
    private final AtomicLong msgIndex;

    private static final class CtxBaseServerHolder {
        private static final PlatformCtxServer instance = new PlatformCtxServer();
    }

    public static PlatformCtxServer getInstance() {
        return CtxBaseServerHolder.instance;
    }

    public PlatformCtxServer() {
        this.patternMap = new HashMap<>();
        this.ruleMap = new HashMap<>();
        this.resolverMap = new HashMap<>();
        this.serverStatistics = new ServerStatistics();
        this.msgIndex = new AtomicLong();
    }

    @Override
    public void init() {
        /*
        buildPatterns(CtxServerConfig.getInstance().getBasePatternFile(), CtxServerConfig.getInstance().getBaseMfuncFile());
        buildRules(CtxServerConfig.getInstance().getBaseRuleFile(), null); //only drop-latest for built-in rules
        this.chgGenerator = new ChgGenerator(PlatformCtxServer.getInstance());
        this.checker = new CheckerStarter(PlatformCtxServer.getInstance(), CtxServerConfig.getInstance().getBaseBfuncFile(), CtxServerConfig.getInstance().getCtxValidator());
        this.ctxFixer = new CtxFixer(PlatformCtxServer.getInstance());
         */
    }

    @Override
    public void onMessage(String channel, String msg) {
        /*
        //sensorName = front-back
        //value = 10-30
        logger.debug("platCtxServer recv: " + msg);
        //System.out.println("platCtxServer recv: " + msg);

        JSONObject msgJsonObj = JSONObject.parseObject(msg);
        msgJsonObj.put("index", String.valueOf(msgIndex.getAndIncrement()));
        filterMessage(msgJsonObj);

        if (msgJsonObj.keySet().size() == 1) {
            assert msgJsonObj.containsKey("index");
            return;
        }

        Message originalMsg = MessageHandler.jsonObject2Message(msgJsonObj);

        if (originalMsg == null) {
            return;
        }

        addOriginalMsg(originalMsg);
        addSendIndex(originalMsg.getIndex());
        serverStatistics.increaseReceivedMsgNum();

        if (CtxServerConfig.getInstance().isServerOn()) {
            List<Map.Entry<List<ContextChange>, ChgListType>> changesList = chgGenerator.generateChanges(originalMsg.getContextMap());
            checker.check(changesList);
        } else {
            for (String contextId : originalMsg.getContextMap().keySet()) {
                ctxFixer.addFixedContext(contextId, MessageHandler.cloneContext(originalMsg.getContextMap().get(contextId)));
            }
        }
         */
    }

    @Override
    public void run() {
        /*
        while(true){
            if(sendIndexQue.isEmpty())
                continue;
            long sendIndex = sendIndexQue.peek();
            if(!ctxFixer.getSendingMsgMap().containsKey(sendIndex))
                continue;
            Message sendingMsg = ctxFixer.getSendingMsgMap().get(sendIndex);
            Message originalMsg = getOriginalMsg(sendIndex);
            for(AppConfig appConfig : Configuration.getAppsConfig().values()){
                String appName = appConfig.getAppName();
                Set<String> sensorInfos = originalMsg.getSensorInfos(appName);
                if(sensorInfos == null)
                    continue;
                JSONObject pubJSONObj = MessageHandler.buildPubJSONObjWithIndex(sendingMsg, sensorInfos);
                for(String sensorName : pubJSONObj.keySet()){
                    if(sensorInfos.contains(sensorName)){
                        logger.debug("PlatformCtxServer pub " + pubJSONObj.getString(sensorName) + " to " + appName + "-CtxServer");
                        publish(sensorName, appConfig.getGrpId(), 0, pubJSONObj.getString(sensorName));
                    }
                }
            }
            serverStatistics.increaseSentMsgNum();

            ctxFixer.getSendingMsgMap().remove(sendIndex);
            originalMsgMap.remove(sendIndex);
            sendIndexQue.poll();
        }
         */
    }

    public static boolean call(String appName, CmdType cmd, CtxServiceConfig config) {
        AppConfig appConfig = Configuration.getAppsConfig().get(appName);
        if (config != null) {
            appConfig.setCtxServiceConfig(config);
        }
        if(cmd == CmdType.START){
            appConfig.initCtxServer();
            return true;
        }
        else if(cmd == CmdType.RESET){
            appConfig.resetCtxServer();
            return true;
        }
        else{
            //TODO
            return false;
        }
    }

    public AtomicLong getMsgIndex() {
        return msgIndex;
    }
}
