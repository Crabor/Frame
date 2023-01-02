package platform.service.ctx.ctxChecker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.service.ctx.ctxServer.ChgListType;
import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.ctxChecker.context.ContextPool;
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

    private AbstractCtxServer ctxServer;
    private ContextPool contextPool;
    private String bfuncFile;

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

        String technique = ctxValidator.split("\\+")[0];
        String schedule = ctxValidator.split("\\+")[1];

        switch (technique) {
            case "ECC":
                this.checker = new ECC(ctxServer.getRuleMap(), this.contextPool, bfuncInstance);
                break;
            case "ConC":
                this.checker = new ConC(ctxServer.getRuleMap(), this.contextPool, bfuncInstance);
                break;
            case "PCC":
                this.checker = new PCC(ctxServer.getRuleMap(), this.contextPool, bfuncInstance);
                break;
            case "INFUSE_base":
                this.checker = new BASE(ctxServer.getRuleMap(), this.contextPool, bfuncInstance);
                break;
            case "INFUSE_C":
                this.checker = new INFUSE_C(ctxServer.getRuleMap(), this.contextPool, bfuncInstance);
                break;
        }

        switch (schedule){
            case "IMD":
                this.scheduler = new IMD(contextPool, checker);
                break;
            case "GEAS_ori":
                this.scheduler = new GEAS_ori(ctxServer.getRuleMap(), contextPool, checker);
                break;
            case "GEAS_opt_s":
                this.scheduler = new GEAS_opt_s(ctxServer.getRuleMap(), contextPool, checker);
                break;
            case "GEAS_opt_c":
                this.scheduler = new GEAS_opt_c(ctxServer.getRuleMap(), contextPool, checker);
                break;
            case "INFUSE_S":
                this.scheduler = new INFUSE_S(ctxServer.getRuleMap(), contextPool, checker);
                break;
        }

        //check init
        this.checker.checkInit();
    }

    private void preprocess(){
        for(Rule rule : ctxServer.getRuleMap().values()){
            contextPool.PoolInit(rule);
            //S-condition
            rule.DeriveSConditions();
            //DIS
            rule.DeriveRCRESets();
        }

        for(String pattern_id : ctxServer.getPatternMap().keySet()){
            contextPool.ThreeSetsInit(pattern_id);
        }
    }

    private Object loadBfuncFile() throws Exception {
        if(bfuncFile == null || bfuncFile.equals("")){
            return null;
        }
        Path bfuncPath = Paths.get(bfuncFile).toAbsolutePath();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{ bfuncPath.getParent().toFile().toURI().toURL()});
        Class<?> c = classLoader.loadClass(bfuncPath.getFileName().toString().substring(0, bfuncPath.getFileName().toString().length() - 6));
        Constructor<?> constructor = c.getConstructor();
        return constructor.newInstance();
    }


    public void check(List<Map.Entry<List<ContextChange>, ChgListType>> changesList){
        for(Map.Entry<List<ContextChange>, ChgListType> changesEntry : changesList){
            List<ContextChange> changeList = changesEntry.getKey();
            for(ContextChange change : changeList){
                try{
                    this.scheduler.doSchedule(change);
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

            if(ctxServer.getResolverType() == ResolverType.INTIME){
                ChgListType chgListType = changesEntry.getValue();

                if(chgListType == ChgListType.GENERATE){
                    Set<String> droppedCtxIdSet = new HashSet<>();
                    List<ContextChange> resolveChgList = ctxServer.getCtxFixer().resolveInconsistenciesInTime(checker.getTempRuleLinksMap());
                    checker.getTempRuleLinksMap().clear();
                    for(ContextChange change : resolveChgList){
                        try {
                            this.scheduler.doSchedule(change);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        if(change.getChangeType() == ContextChange.ChangeType.DELETION){
                            droppedCtxIdSet.add(change.getContext().getContextId());
                        }
                        else if(change.getChangeType() == ContextChange.ChangeType.ADDITION){
                            droppedCtxIdSet.remove(change.getContext().getContextId());
                        }
                    }

                    //当上下文被完全删除后，将上下文认为已经修复,准备发送。
                    HashMap<Context, Set<String>> activateCtxMap = contextPool.getActivateCtxMap();
                    Iterator<Context> iterator = activateCtxMap.keySet().iterator();
                    while(iterator.hasNext()) {
                        Context context = iterator.next();
                        Set<String> patternIdSets = activateCtxMap.get(context);
                        if(patternIdSets.isEmpty()){
                            if(droppedCtxIdSet.contains(context.getContextId())){
                                ctxServer.getCtxFixer().addFixedContext(context.getContextId(), null);
                            }
                            else{
                                ctxServer.getCtxFixer().addFixedContext(context.getContextId(), MessageHandler.cloneContext(context));
                            }
                            iterator.remove();
                        }
                    }
                }
                else{
                    //TODO
                    //当上下文被完全删除后，将上下文认为已经修复,准备发送。
                    HashMap<Context, Set<String>> activateCtxMap = contextPool.getActivateCtxMap();
                    Iterator<Context> iterator = activateCtxMap.keySet().iterator();
                    while(iterator.hasNext()) {
                        Context context = iterator.next();
                        Set<String> patternIdSets = activateCtxMap.get(context);
                        if(patternIdSets.isEmpty()){
                            ctxServer.getCtxFixer().addFixedContext(context.getContextId(), MessageHandler.cloneContext(context));
                            iterator.remove();
                        }
                    }
                }
                checker.getTempRuleLinksMap().clear();
            }
            else{
                assert ctxServer.getResolverType() == ResolverType.DELAY;
                //将生成的link丢给fixer
                ctxServer.getCtxFixer().storeInconsistenciesForDelayResolving(checker.getTempRuleLinksMap());
                checker.getTempRuleLinksMap().clear();

                //当上下文被完全删除后，开始修复该上下文
                HashMap<Context, Set<String>> activateCtxMap = contextPool.getActivateCtxMap();
                Iterator<Context> iterator = activateCtxMap.keySet().iterator();
                while(iterator.hasNext()) {
                    Context context = iterator.next();
                    Set<String> patternIdSets = activateCtxMap.get(context);
                    if(patternIdSets.isEmpty()){
                        ctxServer.getCtxFixer().fixContext(context);
                        iterator.remove();
                    }
                }
            }

        }
    }

    /*

    @Override
    public void run() {
        while (true) {
            if(Thread.interrupted()){
                return;
            }
            Map.Entry<List<ContextChange>, ChgListType> chgEntry = ctxServer.changeBufferConsumer();
            if(chgEntry == null){
                return; // changeBufferConsumer is interrupted.
            }
            List<ContextChange> changeList = chgEntry.getKey();
            while(!changeList.isEmpty()){
                ContextChange chg = changeList.get(0);
                changeList.remove(0);
                //logger.debug(Thread.currentThread().getId() + " " + chg);
                try {
                    this.scheduler.doSchedule(chg);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if(chg.getChangeType() == ContextChange.ChangeType.ADDITION){
                    ctxServer.getServerStatistics().increaseReceivedCtxNum(chg.getPatternId());
                }
                else if(chg.getChangeType() == ContextChange.ChangeType.DELETION){
                    ctxServer.getServerStatistics().increaseCheckedCtxNum(chg.getPatternId());
                }
            }

            if(ctxServer.getResolverType() == ResolverType.INTIME){
                ChgListType chgListType = chgEntry.getValue();
                if(chgListType == ChgListType.GENERATE){
                    List<ContextChange> resolveChgList = ctxServer.getCtxFixer().resolveInconsistenciesInTime(checker.getTempRuleLinksMap());
                    while(!resolveChgList.isEmpty()){
                        ContextChange chg = resolveChgList.get(0);
                        resolveChgList.remove(0);
                        //logger.debug(Thread.currentThread().getId() + " " + chg);
                        try {
                            this.scheduler.doSchedule(chg);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    checker.getTempRuleLinksMap().clear();
                }

                //当上下文被完全删除后，将上下文认为已经修复,准备发送。
                HashMap<Context, Set<String>> activateCtxMap = contextPool.getActivateCtxMap();
                Iterator<Context> iterator = activateCtxMap.keySet().iterator();
                while(iterator.hasNext()) {
                    Context context = iterator.next();
                    Set<String> patternIdSets = activateCtxMap.get(context);
                    if(patternIdSets.isEmpty()){
                        ctxServer.getCtxFixer().addFixedContext(context.getContextId(), MessageHandler.cloneContext(context));
                        iterator.remove();
                    }
                }
            }
            else{
                assert ctxServer.getResolverType() == ResolverType.DELAY;
                //将生成的link丢给fixer
                ctxServer.getCtxFixer().storeInconsistenciesForDelayResolving(checker.getTempRuleLinksMap());
                checker.getTempRuleLinksMap().clear();

                //当上下文被完全删除后，开始修复该上下文
                HashMap<Context, Set<String>> activateCtxMap = contextPool.getActivateCtxMap();
                Iterator<Context> iterator = activateCtxMap.keySet().iterator();
                while(iterator.hasNext()) {
                    Context context = iterator.next();
                    Set<String> patternIdSets = activateCtxMap.get(context);
                    if(patternIdSets.isEmpty()){
                        ctxServer.getCtxFixer().fixContext(context);
                        iterator.remove();
                    }
                }
            }

        }
    }

    public void reset(){
        t.interrupt();
        while(t.isInterrupted());
    }

     */
}
