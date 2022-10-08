package platform.service.ctx.ctxChecker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.ctxChecker.context.ContextPool;
import platform.service.ctx.ctxChecker.middleware.checkers.*;
import platform.service.ctx.ctxChecker.middleware.schedulers.*;
import platform.service.ctx.ctxServer.AbstractCtxServer;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class CheckerStarter implements Runnable{

    private static final Log logger = LogFactory.getLog(CheckerStarter.class);

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
        Path bfuncPath = Paths.get(bfuncFile).toAbsolutePath();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{ bfuncPath.getParent().toFile().toURI().toURL()});
        Class<?> c = classLoader.loadClass(bfuncPath.getFileName().toString().substring(0, bfuncPath.getFileName().toString().length() - 6));
        Constructor<?> constructor = c.getConstructor();
        return constructor.newInstance();
    }

    @Override
    public void run() {
        logger.info("begin to check contexts");
        int count = 0;
        long timeSum = 0L;


        while (true) {
            List<ContextChange> changeList = ctxServer.changeBufferConsumer();
            System.out.println(changeList);
            while(!changeList.isEmpty()){
                ContextChange chg = changeList.get(0);
                changeList.remove(0);
                try {
                    this.scheduler.doSchedule(chg);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            //将生成的link丢给fixer
            ctxServer.getCtxFixer().filterInconsistencies(checker.getTempRuleLinksMap());
            System.out.println(checker.getTempRuleLinksMap());
            checker.getTempRuleLinksMap().clear();

            //当上下文被完全删除后，开始修复该上下文
            HashMap<Context, Set<String>> activateCtxMap = contextPool.getActivateCtxMap();
            Iterator<Context> iterator = activateCtxMap.keySet().iterator();
            while(iterator.hasNext()) {
                Context context = iterator.next();
                Set<String> patternIdSets = activateCtxMap.get(context);
                if(patternIdSets.isEmpty()){
                    //ctxServer.getFixer().fixContext(context);
                    iterator.remove();
                }
            }

//            long startTime = System.nanoTime();
//            for ( : changes) {
//                changeList.clear();
//                try {
//                    contextHandler.generateChanges(chgStr, changeList);
//                    while(!changeList.isEmpty()){
//                        ContextChange chg = changeList.get(0);
//                        changeList.remove(0);
//                        this.scheduler.doSchedule(chg);
//                    }
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//
//                if(chgStr.startsWith("+")) {
//                    msgStatistics.addChk();
//                    String[] parts = chgStr.split(","); //+,11,pat_right,5.264847945235763,2022-04-20 05:51:45
//                    long index = Long.parseLong(parts[1]);
//                    addcheckMsgID(String.valueOf(index));
//                    String pat = parts[2];
//                    checkPointLog.put(pat, index);
//                    CtxStatistics.get(pat.replace("pat_","")).addChecked();
//                }
//            }
//            long endTime = System.nanoTime(); //获取结束时间
//            timeSum += (endTime - startTime);
//            count = changes.size() + count;
//
//            List<String> errorMsgIDs = new ArrayList<>();
//
//            Map<String, Set<Link>> tempRuleLinksMap = checker.getTempRuleLinksMap();
//            for(String rule_id : tempRuleLinksMap.keySet()){
//                for(Link link : tempRuleLinksMap.get(rule_id))
//                {
//                    Set<Map.Entry<String, Context>> vaSet = link.getVaSet();
//                    for(Map.Entry<String, Context> va : vaSet){
//                        errorMsgIDs.add(va.getValue().getCtx_id().substring(4));
//                    }
//                }
//            }
//
//            ContextManager.adderrorMsgIDList(errorMsgIDs);
//            checker.getTempRuleLinksMap().clear();
//            long process_Tag = minInCheckPointLog();
//            ContextManager.fixMsgElementsUntil(process_Tag);
        }

    }

}
