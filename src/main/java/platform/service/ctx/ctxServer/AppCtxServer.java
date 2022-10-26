package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import platform.config.AppConfig;
import platform.config.SubConfig;
import platform.service.ctx.ctxChecker.CheckerStarter;
import platform.service.ctx.message.Message;
import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.statistics.ServerStatistics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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
    protected void publishAndClean(long indexLimit) {
        Iterator<Long> iterator = fixingMsgSet.keySet().iterator();
        while(iterator.hasNext()){
            long index = iterator.next();
            if(index > indexLimit){
                break;
            }
            else{
                Message originalMsg = getOriginalMsg(index);
                Message fixingMsg = getOrPutDefaultFixingMsg(index);
                //查看是否这条信息所有context都已收齐
                Set<String> originalMsgContextIds = originalMsg.getContextMap().keySet();
                Set<String> fixingMsgContextIds = fixingMsg.getContextMap().keySet();
                if(originalMsgContextIds.containsAll(fixingMsgContextIds) && fixingMsgContextIds.containsAll(originalMsgContextIds)){
                    serverStatistics.increaseCheckedAndResolvedMsgNum();
                    //发送消息
                    String pubMsgStr = MessageHandler.buildPubMsgStrWithoutIndex(fixingMsg, originalMsg.getSensorInfos(appConfig.getAppName()));
                    SubConfig sensorPubConfig = null;
                    for(SubConfig subConfig : appConfig.getSubConfigs()){
                        if(subConfig.channel.equals("sensor")){
                            sensorPubConfig = subConfig;
                        }
                    }
                    assert sensorPubConfig != null;
                    publish("sensor", sensorPubConfig.groupId, sensorPubConfig.priorityId, pubMsgStr);
                    serverStatistics.increaseSentMsgNum();

                    //删除消息
                    originalMsgSet.remove(index);
                    iterator.remove();
                }
                else{
                    break;
                }
            }
        }
    }

}
