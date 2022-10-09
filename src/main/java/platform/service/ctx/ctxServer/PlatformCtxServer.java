package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import platform.config.AppConfig;
import platform.config.Configuration;
import platform.config.CtxServerConfig;
import platform.config.SubConfig;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.message.Message;
import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.ctxChecker.CheckerStarter;
import platform.service.ctx.sensorStatitic.PlatformSensorStatistics;

import java.util.*;

public class PlatformCtxServer extends AbstractCtxServer {
    private Thread t;

    private final Map<String, SubConfig> appSubConfigMap;

    private final Map<Long, Message> fixedMsgMap;

    private static final class CtxBaseServerHolder {
        private static final PlatformCtxServer instance = new PlatformCtxServer();
    }

    public static PlatformCtxServer getInstance() {
        return CtxBaseServerHolder.instance;
    }

    public PlatformCtxServer() {
        this.appSubConfigMap = new HashMap<>();
        this.sensorStatistics = new PlatformSensorStatistics();
        this.patternMap = new HashMap<>();
        this.ruleMap = new HashMap<>();
        this.resolverMap = new HashMap<>();
        this.fixedMsgMap = new TreeMap<>((Long::compareTo));
    }

    @Override
    public void init() {
        for(AppConfig appConfig : Configuration.getListOfAppObj()){
            for(SubConfig subConfig : appConfig.getSubConfigs()){
                if(subConfig.channel.equals("sensor")){
                    appSubConfigMap.put(appConfig.getAppName(), subConfig);
                }
            }
        }
        this.sensorStatistics.registerSensor("stupid", "taxis");
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
        JSONObject jsonObject = filterMessage(msg);
        if(jsonObject.keySet().isEmpty()) {
            return;
        }

        if(CtxServerConfig.getInstance().isServerOn()){
            Message message = MessageHandler.jsonObject2Message(jsonObject);
            chgGenerator.generateChanges(message.getContextMap());
        }
        else{
            //TODO()
        }
    }


    @Override
    public void run() {
        while(true){
            try {
                Map.Entry<String, Context> fixedContext = ctxFixer.getFixedContextQue().take();
                System.out.println(fixedContext);
//                String contextId = fixedContext.getKey();
//                Context context = fixedContext.getValue();
//                long index = Long.parseLong(contextId.substring(contextId.lastIndexOf("_")) + 1);
//                Message message = fixedMsgMap.getOrDefault(index, new Message(index));
//                message.addContext(contextId, context);
//                fixedMsgMap.put(index, message);


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
