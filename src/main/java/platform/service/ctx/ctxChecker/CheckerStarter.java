package platform.service.ctx.ctxChecker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.service.ctx.ctxChecker.context.*;
import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.middleware.checkers.*;
import platform.service.ctx.ctxChecker.middleware.schedulers.*;
import platform.service.ctx.ctxServer.AbstractCtxServer;
import platform.service.ctx.rule.resolver.ResolverType;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class CheckerStarter {
    private Log logger = LogFactory.getLog(this.getClass());

    private final AbstractCtxServer ctxServer;
    private final ContextPool contextPool;
    private final String bfuncFile;

    private Scheduler scheduler;
    private Checker checker;

    public CheckerStarter(AbstractCtxServer ctxServer, String bfuncFile, String ctxValidator) {
        this.ctxServer = ctxServer;
        this.bfuncFile = bfuncFile;
        this.contextPool = new ContextPool();

        preprocess();

        Object bfuncInstance = null;
        try {
            bfuncInstance = loadBfuncFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String technique = "";
        String schedule = "";
        if(ctxValidator.equals("INFUSE")){
            technique = "INFUSE_C";
            schedule = "INFUSE_S";
        }
        else{
            technique = ctxValidator.split("_")[0];
            schedule = ctxValidator.split("_")[1];
        }

        switch (technique) {
            case "ECC":
                this.checker = new ECC(ctxServer.getRuleManager().getRuleMap(), this.contextPool, bfuncInstance);
                break;
            case "ConC":
                this.checker = new ConC(ctxServer.getRuleManager().getRuleMap(), this.contextPool, bfuncInstance);
                break;
            case "PCC":
                this.checker = new PCC(ctxServer.getRuleManager().getRuleMap(), this.contextPool, bfuncInstance);
                break;
            case "INFUSE_C":
                this.checker = new INFUSE_C(ctxServer.getRuleManager().getRuleMap(), this.contextPool, bfuncInstance);
                break;
            default:
                assert false;
        }

        switch (schedule){
            case "IMD":
                this.scheduler = new IMD(contextPool, checker);
                break;
            case "GEAS":
                this.scheduler = new GEAS_ori(ctxServer.getRuleManager().getRuleMap(), contextPool, checker);
                break;
            case "INFUSE_S":
                this.scheduler = new INFUSE_S(ctxServer.getRuleManager().getRuleMap(), contextPool, checker);
                break;
            default:
                assert false;
        }

        //check init
        this.checker.checkInit();
    }

    private void preprocess(){
        for(Rule rule : ctxServer.getRuleManager().getRuleMap().values()){
            contextPool.PoolInit(rule);
            //S-condition
            rule.DeriveSConditions();
            //DIS
            rule.DeriveRCRESets();
        }

        for(String pattern_id : ctxServer.getPatternManager().getPatternMap().keySet()){
            contextPool.ThreeSetsInit(pattern_id);
        }
    }

    private Object loadBfuncFile() throws Exception {
        if(bfuncFile == null || bfuncFile.equals("")){
            return null;
        }
        Path bfuncPath = Paths.get(bfuncFile).toAbsolutePath();
        try(URLClassLoader classLoader = new URLClassLoader(new URL[]{ bfuncPath.getParent().toFile().toURI().toURL()})){
            Class<?> c = classLoader.loadClass(bfuncPath.getFileName().toString().substring(0, bfuncPath.getFileName().toString().length() - 6));
            Constructor<?> constructor = c.getConstructor();
            return constructor.newInstance();
        }
    }

    public void check(Context context){
        List<ChangeBatch> changeBatchList = ctxServer.getChangeGenerator().generateChangeBatches(context);
        for(ChangeBatch changeBatch : changeBatchList){
            List<ContextChange> changeList = changeBatch.getChangeList();
            ChangeBatchType changeBatchType = changeBatch.getBatchType();

            //调用检测引擎检测这一个batch
            for(ContextChange change : changeList){
                try{
                    scheduler.doSchedule(change);
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
                if(change.getChangeType() == ContextChange.ChangeType.ADDITION){
                    ctxServer.getServerStatistics().increaseReceivedCtxNum(change.getPatternId());
                }
                else if(change.getChangeType() == ContextChange.ChangeType.DELETION){
                    ctxServer.getServerStatistics().increaseCheckedCtxNum(change.getPatternId());
                }
            }

            //对于该batch检测结束
            if(ctxServer.getRuleManager().getResolverType() == ResolverType.IN_TIME){
                if(changeBatchType == ChangeBatchType.GENERATE){

                    //根据报告的incs进行resolve
                    ctxServer.getItemManager().updateItemsViolations(checker.getLinksForSingleCheck());
                    List<ContextChange> resolveChangeList = ctxServer.getCtxFixer().resolveViolationsInTime(checker.getLinksForSingleCheck());
                    Set<String> droppedContextIdSet = new HashSet<>();
                    for(ContextChange change : resolveChangeList){
                        try {
                            this.scheduler.doSchedule(change);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        if(change.getChangeType() == ContextChange.ChangeType.DELETION){
                            droppedContextIdSet.add(change.getContext().getContextId());
                        }
                        else if(change.getChangeType() == ContextChange.ChangeType.ADDITION){
                            droppedContextIdSet.remove(change.getContext().getContextId());
                        }
                    }

                    //Intime： 检测完后应当立即发送context，而不是等待context被delete之后
                    if(droppedContextIdSet.contains(context.getContextId())){
                        ctxServer.getItemManager().addValidatedItem(Long.parseLong(context.getContextId().split("_")[1]), null);
                    }
                    else{
                        ctxServer.getItemManager().addValidatedItem(Long.parseLong(context.getContextId().split("_")[1]), context);
                    }

//                    //当curContext不再存在于pattern中后，可以被发送
//                    HashMap<Context, Set<String>> activateCtxMap = contextPool.getActivateCtxMap();
//                    Iterator<Context> iterator = activateCtxMap.keySet().iterator();
//                    while(iterator.hasNext()) {
//                        Context curContext = iterator.next();
//                        Set<String> patternIdSets = activateCtxMap.get(curContext);
//                        if(patternIdSets.isEmpty()){
//                            if(droppedContextIdSet.contains(curContext.getContextId())){
//                                ctxServer.getItemManager().addValidatedItem(Long.parseLong(curContext.getContextId().split("_")[1]), null);
//
//                            }
//                            else{
//                                ctxServer.getItemManager().addValidatedItem(Long.parseLong(curContext.getContextId().split("_")[1]), curContext);
//                            }
//                            iterator.remove();
//                        }
//                    }
                }
                else{
                    //TODO: overdue的Batch产生的incs如何resolve? 目前是不处理.
                    //当curContext不再存在于pattern中后，需要从originalItemMap中删除对应的item
                    HashMap<Context, Set<String>> activateCtxMap = contextPool.getActivateCtxMap();
                    Iterator<Context> iterator = activateCtxMap.keySet().iterator();
                    while(iterator.hasNext()) {
                        Context curContext = iterator.next();
                        Set<String> patternIdSets = activateCtxMap.get(curContext);
                        if(patternIdSets.isEmpty()){
                            ctxServer.getItemManager().removeItem(Long.parseLong(curContext.getContextId().split("_")[1]));
                            iterator.remove();
                        }
                    }
                }
                checker.getLinksForSingleCheck().clear();
                checker.getLinksForBatchChecks().clear();
            }
            else{
                assert false;
                //TODO: 延迟resolve如何进行
//                assert ctxServer.getResolverType() == ResolverType.DELAY;
//                //将生成的link丢给fixer
//                ctxServer.getCtxFixer().storeInconsistenciesForDelayResolving(checker.getRule2LinkSet());
//                checker.getRule2LinkSet().clear();
//
//                //当上下文被完全删除后，开始修复该上下文
//                HashMap<Context, Set<String>> activateCtxMap = contextPool.getActivateCtxMap();
//                Iterator<Context> iterator = activateCtxMap.keySet().iterator();
//                while(iterator.hasNext()) {
//                    Context curContext = iterator.next();
//                    Set<String> patternIdSets = activateCtxMap.get(curContext);
//                    if(patternIdSets.isEmpty()){
//                        ctxServer.getCtxFixer().fixContext(curContext);
//                        iterator.remove();
//                    }
//                }
            }

        }
    }


}
