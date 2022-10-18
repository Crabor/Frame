package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import platform.config.SubConfig;
import platform.service.ctx.ctxChecker.CheckerStarter;
import platform.service.ctx.message.Message;
import platform.service.ctx.message.MessageHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class AppCtxServer extends AbstractCtxServer{
    private final CtxInteractor ctxInteractor;

    public AppCtxServer(CtxInteractor ctxInteractor) {
        this.ctxInteractor = ctxInteractor;
        this.patternMap = new HashMap<>();
        this.ruleMap = new HashMap<>();
        this.resolverMap = new HashMap<>();
    }

    @Override
    public void init() {
        buildPatterns(ctxInteractor.getPatternFile(), ctxInteractor.getMfuncFile());
        buildRules(ctxInteractor.getRuleFile());
        this.chgGenerator = new ChgGenerator(this);
        this.chgGenerator.start();
        this.ctxFixer = new CtxFixer(this);
        Thread checker =new Thread(new CheckerStarter(
                this, ctxInteractor.getBfuncFile(), ctxInteractor.getCtxValidator())
        );
        checker.start();
    }

    @Override
    public void onMessage(String channel, String msg) {
        logger.debug(ctxInteractor.getAppConfig().getAppName() + "-CtxServer recv: " + msg);

        JSONObject msgJsonObj = JSONObject.parseObject(msg);
        Message originalMsg = MessageHandler.jsonObject2Message(msgJsonObj);

        if(originalMsg == null){
            return;
        }

        addOriginalMsg(originalMsg);
        if(ctxInteractor.isCtxServerOn()){
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
                    //发送消息
                    String pubMsgStr = MessageHandler.buildPubMsgStrWithoutIndex(fixingMsg, originalMsg.getSensorInfos(ctxInteractor.getAppConfig().getAppName()));
                    SubConfig sensorPubConfig = null;
                    for(SubConfig subConfig : ctxInteractor.getAppConfig().getSubConfigs()){
                        if(subConfig.channel.equals("sensor")){
                            sensorPubConfig = subConfig;
                        }
                    }
                    assert sensorPubConfig != null;
                    publish("sensor", sensorPubConfig.groupId, sensorPubConfig.priorityId, pubMsgStr);
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
