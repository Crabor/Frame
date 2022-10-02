//package platform.service.ctx.ctxChecker.INFuse;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import platform.config.CtxServerConfig;
//import platform.config.INFuseConfig;
//import platform.service.ctx.Context_bk.ContextManager;
//import platform.service.ctx.ctxChecker.INFuse.Constraints.Rule;
//import platform.service.ctx.ctxChecker.INFuse.Constraints.RuleHandler;
//import platform.service.ctx.ctxChecker.INFuse.Constraints.Runtime.Link;
//import platform.service.ctx.ctxChecker.INFuse.Contexts.*;
//import platform.service.ctx.ctxChecker.INFuse.Middleware.Checkers.*;
//import platform.service.ctx.ctxChecker.INFuse.Middleware.Schedulers.*;
//import platform.service.ctx.ctxChecker.INFuse.Patterns.PatternHandler;
//import platform.service.ctx.ctxChecker.INFuse.Patterns.PatternHandlerFactory;
//
//import java.lang.reflect.Constructor;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.*;
//
//import static java.lang.Thread.sleep;
//import static platform.service.ctx.Context_bk.ContextManager.*;
//
//public class Starter implements Runnable{
//
//    private static final Log logger = LogFactory.getLog(Starter.class);
//    private static HashMap<String, Long> checkPointLog;
//
//    private RuleHandler ruleHandler;
//    private PatternHandler patternHandler;
//    private ContextHandler contextHandler;
//    private ContextPool contextPool;
//
//    private String ruleFile;
//    private String patternFile;
//    private String bfuncFile;
//
//
//    private Scheduler scheduler;
//    private Checker checker;
//
//    public Starter(CtxServerConfig config) {
//        INFuseConfig inFuseConfig = CtxServerConfig.getInFuseConfig();
//        this.ruleFile = inFuseConfig.getRuleFilePath();
//        this.patternFile = inFuseConfig.getPatternFilePath();
//        this.bfuncFile = inFuseConfig.getBfuncFile();
//        String approach = inFuseConfig.getCtxCleaner();
//
//        this.ruleHandler = new RuleHandler();
//        this.patternHandler = new PatternHandlerFactory().getPatternHandler("plat");
//        this.contextHandler = new ContextHandlerFactory().getContextHandler("plat", patternHandler);
//        this.contextPool = new ContextPool();
//
//
//        try {
//            buildRulesAndPatterns();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        Object bfuncInstance = null;
//        try {
//            bfuncInstance = loadBfuncFile();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        String  technique = approach.substring(0, approach.indexOf("+"));
//        String schedule = approach.substring(approach.indexOf("+") + 1);
//
//        switch (technique) {
//            case "ECC":
//                this.checker = new ECC(this.ruleHandler, this.contextPool, bfuncInstance);
//                break;
//            case "ConC":
//                this.checker = new ConC(this.ruleHandler, this.contextPool, bfuncInstance);
//                break;
//            case "PCC":
//                this.checker = new PCC(this.ruleHandler, this.contextPool, bfuncInstance);
//                break;
//            case "INFUSE_base":
//                this.checker = new BASE(this.ruleHandler, this.contextPool, bfuncInstance);
//                break;
//            case "INFUSE_C":
//                this.checker = new INFUSE_C(this.ruleHandler, this.contextPool, bfuncInstance);
//                break;
//        }
//
//        switch (schedule){
//            case "IMD":
//                this.scheduler = new IMD(ruleHandler, contextPool, checker);
//                break;
//            case "GEAS_ori":
//                this.scheduler = new GEAS_ori(ruleHandler, contextPool, checker);
//                break;
//            case "GEAS_opt_s":
//                this.scheduler = new GEAS_opt_s(ruleHandler, contextPool, checker);
//                break;
//            case "GEAS_opt_c":
//                this.scheduler = new GEAS_opt_c(ruleHandler, contextPool, checker);
//                break;
//            case "INFUSE_S":
//                this.scheduler = new INFUSE_S(ruleHandler, contextPool, checker);
//                break;
//        }
//
//        //check init
//        this.checker.checkInit();
//
//
//        //checkPoint init
//        checkPointLog = new HashMap<>();
//        for(String pattern_id : patternHandler.getPatternMap().keySet()){
//            checkPointLog.put(pattern_id, 0L);
//        }
//    }
//
//    private void buildRulesAndPatterns() throws Exception {
//        this.ruleHandler.buildRules(ruleFile);
//        this.patternHandler.buildPatterns(patternFile);
//
//        for(Rule rule : ruleHandler.getRuleList()){
//            contextPool.PoolInit(rule);
//            //S-condition
//            rule.DeriveSConditions();
//            //DIS
//            rule.DeriveRCRESets();
//        }
//
//        for(String pattern_id : patternHandler.getPatternMap().keySet()){
//            contextPool.ThreeSetsInit(pattern_id);
//        }
//    }
//
//    private Object loadBfuncFile() throws Exception {
//        Path bfuncPath = Paths.get(bfuncFile).toAbsolutePath();
//        URLClassLoader classLoader = new URLClassLoader(new URL[]{ bfuncPath.getParent().toFile().toURI().toURL()});
//        Class<?> c = classLoader.loadClass(bfuncPath.getFileName().toString().substring(0, bfuncPath.getFileName().toString().length() - 6));
//        Constructor<?> constructor = c.getConstructor();
//        return constructor.newInstance();
//    }
//
//    @Override
//    public void run() {
//        logger.info("开始一致性处理");
//        int count = 0;
//        long timeSum = 0L;
//        List<ContextChange> changeList = new ArrayList<>();
//
//        while (true) {
//            if (!CtxServerConfig.getInstace().isCtxCleanOn())
//                continue;
//            LinkedList<String> changes = ContextManager.getChangeInvokedElements();
//
//            long startTime = System.nanoTime();
//            for (String chgStr : changes) {
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
//        }
////        try {
////            this.scheduler.checkEnds();
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
//    }
//
//    public Long minInCheckPointLog(){
//        long temp = Long.MAX_VALUE;
//        for(Map.Entry<String, Long> entry: checkPointLog.entrySet()) {
//            Long value = entry.getValue();
//            if (temp > value)
//                temp = value;
//        }
//        return temp;
//    }
//}
