package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import platform.app.App;
import platform.config.AppConfig;
import platform.config.Configuration;
import platform.config.CtxServerConfig;
import platform.config.SubConfig;
import platform.service.ctx.message.Message;
import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.ctxChecker.CheckerStarter;
import platform.service.ctx.statistics.ServerStatistics;
import platform.struct.CmdType;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

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
        buildPatterns(CtxServerConfig.getInstance().getBasePatternFile(), CtxServerConfig.getInstance().getBaseMfuncFile());
        buildRules(CtxServerConfig.getInstance().getBaseRuleFile());
        this.chgGenerator = new ChgGenerator(PlatformCtxServer.getInstance());
        this.chgGenerator.start();
        this.ctxFixer = new CtxFixer(PlatformCtxServer.getInstance());
        this.checker = new CheckerStarter(PlatformCtxServer.getInstance(), CtxServerConfig.getInstance().getBaseBfuncFile(), CtxServerConfig.getInstance().getCtxValidator());
        this.checker.start();
    }

    @Override
    public void onMessage(String channel, String msg) {
        logger.debug("platCtxServer recv: " + msg);
        //System.out.println("platCtxServer recv: " + msg);

        JSONObject msgJsonObj = JSONObject.parseObject(msg);
        msgJsonObj.put("index", String.valueOf(msgIndex.getAndIncrement()));
        filterMessage(msgJsonObj);

        if (msgJsonObj.keySet().size() == 1) {
            skippedSendIndex.add(msgIndex.get());
            assert msgJsonObj.containsKey("index");
            return;
        }

        Message originalMsg = MessageHandler.jsonObject2Message(msgJsonObj);

        if (originalMsg == null) {
            skippedSendIndex.add(msgIndex.get());
            return;
        }

        addOriginalMsg(originalMsg);
        serverStatistics.increaseReceivedMsgNum();

        if (CtxServerConfig.getInstance().isServerOn()) {
            chgGenerator.generateChanges(originalMsg.getContextMap());
        } else {
            for (String contextId : originalMsg.getContextMap().keySet()) {
                ctxFixer.addFixedContext(contextId, MessageHandler.cloneContext(originalMsg.getContextMap().get(contextId)));
            }
        }
    }

    @Override
    public void run() {
        while(true){
            while(skippedSendIndex.contains(toSendIndex.get())){
                skippedSendIndex.remove(toSendIndex.get()); // avoid OOM
                toSendIndex.getAndIncrement();
            }
            if(!ctxFixer.getSendingMsgMap().containsKey(toSendIndex.get()))
                continue;
            Message sendingMsg = ctxFixer.getSendingMsgMap().get(toSendIndex.get());
            Message originalMsg = getOriginalMsg(sendingMsg.getIndex());
            for(AppConfig appConfig : Configuration.getAppsConfig().values()){
                String appName = appConfig.getAppName();
                Set<String> sensorInfos = originalMsg.getSensorInfos(appName);
                if(sensorInfos == null)
                    continue;
                JSONObject pubJSONObj = MessageHandler.buildPubJSONObjWithIndex(sendingMsg, sensorInfos);
                SubConfig sensorPubConfig = null;
                for(SubConfig subConfig : appConfig.getSubConfigs()){
                    if(subConfig.channel.equals("sensor")){
                        sensorPubConfig = subConfig;
                    }
                }
                assert sensorPubConfig != null;
                logger.debug("PlatformCtxServer pub " + pubJSONObj.toJSONString() + " to " + appName + "-CtxServer");
                publish("sensor", sensorPubConfig.groupId, pubJSONObj.toJSONString());
            }
            serverStatistics.increaseSentMsgNum();
            ctxFixer.getSendingMsgMap().remove(toSendIndex.get());
            originalMsgMap.remove(toSendIndex.get());
            toSendIndex.getAndIncrement();
        }
    }

    public static Object call(String appName, CmdType cmd, Object[] args) {
        if(cmd == CmdType.RESET){
            logger.debug("CtxServer for " + appName + " resetting");
            Configuration.getAppsConfig().get(appName).resetCtxServer();
            logger.debug("CtxServer for " + appName + " restarted");
            return "CtxServer restarted";
        }
        else{
            //TODO
            return null;
        }
    }

    public AtomicLong getMsgIndex() {
        return msgIndex;
    }
}
