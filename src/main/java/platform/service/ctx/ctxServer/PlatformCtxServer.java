package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.app.App;
import platform.config.AppConfig;
import platform.config.Configuration;
import platform.config.CtxServerConfig;
import platform.config.SubConfig;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.message.Message;
import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.ctxChecker.CheckerStarter;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class PlatformCtxServer extends AbstractCtxServer {
    private Thread t;

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
        this.msgIndex = new AtomicLong();
    }

    @Override
    public void init() {
        SensorStatistics.getInstance().registerSensor("platform.testunitycar.MySyncApp", "taxis");
        if(Configuration.getCtxServerConfig().isServerOn()){
            buildPatterns(CtxServerConfig.getInstance().getBasePatternFile(), CtxServerConfig.getInstance().getBaseMfuncFile());
            buildRules(CtxServerConfig.getInstance().getBaseRuleFile());
            this.chgGenerator = new ChgGenerator(PlatformCtxServer.getInstance());
            this.chgGenerator.start();
            this.ctxFixer = new CtxFixer(PlatformCtxServer.getInstance());
            Thread baseChecker = new Thread(new CheckerStarter(
                    PlatformCtxServer.getInstance(), CtxServerConfig.getInstance().getBaseBfuncFile(), CtxServerConfig.getInstance().getCtxValidator())
            );
            baseChecker.start();
        }
    }

    @Override
    public void onMessage(String channel, String msg) {
        logger.debug("ctx recv: " + msg);
        JSONObject msgJsonObj = JSON.parseObject(msg);
        msgJsonObj.put("index", String.valueOf(msgIndex.getAndIncrement()));
        filterMessage(msgJsonObj);

        if(msgJsonObj.keySet().size() == 1) {
            assert msgJsonObj.containsKey("index");
            return;
        }

        Message originalMsg = MessageHandler.jsonObject2Message(msgJsonObj);
        addOriginalMsg(originalMsg);

        if(CtxServerConfig.getInstance().isServerOn()){
            chgGenerator.generateChanges(originalMsg.getContextMap());
        }
        else{
            //TODO()
        }
    }

    private void publishAndClean(Message fixingMsg){
        long index = fixingMsg.getIndex();
        Message originalMsg = getOriginalMsg(index);
        //查看是否这条信息的所有context都已收齐
        Set<String> originalMsgContextIds = originalMsg.getContextMap().keySet();
        Set<String> fixingMsgContextIds = fixingMsg.getContextMap().keySet();
        if(originalMsgContextIds.containsAll(fixingMsgContextIds) && fixingMsgContextIds.containsAll(originalMsgContextIds)){
            //发送相应的信息
            for(AppConfig appConfig : Configuration.getListOfAppObj()){
                String appName = appConfig.getAppName();
                Set<String> sensorInfos = originalMsg.getSensorInfos(appName);
                if(sensorInfos == null)
                    continue;
                String pubMsgStr = MessageHandler.buildPubMsgStr(fixingMsg, sensorInfos);
                SubConfig sensorPubConfig = null;
                for(SubConfig subConfig : appConfig.getSubConfigs()){
                    if(subConfig.channel.equals("sensor")){
                        sensorPubConfig = subConfig;
                    }
                }
                assert sensorPubConfig != null;
                publish("sensor", sensorPubConfig.groupId, sensorPubConfig.priorityId, pubMsgStr);
            }
            //删除消息
            originalMsgSet.remove(index);
            fixingMsgSet.remove(index);
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                Map.Entry<String, Context> fixedContext = ctxFixer.getFixedContextQue().take();
                long index = Long.parseLong(fixedContext.getKey().substring(fixedContext.getKey().lastIndexOf("_") + 1));
                Message fixingMsg = getOrPutDefaultFixingMsg(index);
                fixingMsg.addContext(fixedContext.getKey(), fixedContext.getValue());
                publishAndClean(fixingMsg);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }

}
