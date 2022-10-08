package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import platform.config.Configuration;
import platform.config.CtxServerConfig;
import platform.service.ctx.message.Message;
import platform.service.ctx.message.MessageBuilder;
import platform.service.ctx.ctxChecker.CheckerStarter;
import platform.service.ctx.sensorStatitic.BaseSensorStatistics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class BaseCtxServer extends AbstractCtxServer {
    private Thread t;
    private final LinkedBlockingQueue<Message> msgBuffer;

    private static final class CtxBaseServerHolder {
        private static final BaseCtxServer instance = new BaseCtxServer();
    }

    public static BaseCtxServer getInstance() {
        return CtxBaseServerHolder.instance;
    }

    public BaseCtxServer() {
        this.sensorStatistics = new BaseSensorStatistics();
        this.patternMap = new HashMap<>();
        this.ruleMap = new HashMap<>();
        this.resolverMap = new HashMap<>();
        this.msgBuffer =  new LinkedBlockingQueue<>();
    }

    @Override
    public void init() {
        this.sensorStatistics.registerSensor("stupid", "taxis");
        if(Configuration.getCtxServerConfig().isServerOn()){
            buildPatterns(CtxServerConfig.getInstance().getBasePatternFile(), CtxServerConfig.getInstance().getBaseMfuncFile());
            buildRules(CtxServerConfig.getInstance().getBaseRuleFile());
            this.chgGenerator = new ChgGenerator(BaseCtxServer.getInstance());
            this.chgGenerator.start();
            this.ctxFixer = new CtxFixer(BaseCtxServer.getInstance());
            Thread baseChecker = new Thread(new CheckerStarter(
                    BaseCtxServer.getInstance(), CtxServerConfig.getInstance().getBaseBfuncFile(), CtxServerConfig.getInstance().getCtxValidator())
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

        Message message = MessageBuilder.jsonObject2Message(jsonObject);
        msgBuffer.offer(message);

        if(CtxServerConfig.getInstance().isServerOn()){
            chgGenerator.generateChanges(message.getContextMap());
        }
        else{
            //TODO()
        }
    }


    @Override
    public void run() {
//        while(true){
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//            List<ContextChange> changeList = changeBufferConsumer();
//            System.out.println(System.currentTimeMillis() + " " + changeList);
//        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }

}
