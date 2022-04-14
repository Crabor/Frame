package platform.service.cxt.Config;

import com.alibaba.fastjson.JSONObject;

public class CMIDConfig {
    private String dataFile;
    private String changeHandlerType;
    private String logFilePath;
    private String ruleFilePath;
    private String patternFilePath;
    private String oracleFilePath;
    private String CtxFixer;
    private String CtxChecker;
    private String CtxScheduler;

    private static CMIDConfig INSTANCE = null;

    public static CMIDConfig getInstace(){
        return INSTANCE;
    }
    public static CMIDConfig getInstace(String ctxChecker, String ctxScheduler, String ctxFixer, String dataFile, String changeHandlerType, String logFilePath,
                                            String ruleFilePath, String patternFilePath){
        INSTANCE = new CMIDConfig(ctxChecker, ctxScheduler, ctxFixer, dataFile, changeHandlerType, logFilePath,
               ruleFilePath, patternFilePath);
        return INSTANCE;
    }
    CMIDConfig(String ctxChecker, String ctxScheduler, String ctxFixer,String dataFile, String changeHandlerType, String logFilePath,
               String ruleFilePath, String patternFilePath){
        this.CtxChecker = ctxChecker;
        this.CtxScheduler = ctxScheduler;
        this.CtxFixer = ctxFixer;
        this.dataFile = dataFile;
        this.changeHandlerType = changeHandlerType;
        this.logFilePath = logFilePath;
        this.ruleFilePath = ruleFilePath;
        this.patternFilePath = patternFilePath;
    }

    public String getCtxFixer() {
        return CtxFixer;
    }

    public void setCtxFixer(String ctxFixer) {
        CtxFixer = ctxFixer;
    }

    public String getCtxChecker() {
        return CtxChecker;
    }

    public void setCtxChecker(String ctxChecker) {
        CtxChecker = ctxChecker;
    }

    public String getCtxScheduler() {
        return CtxScheduler;
    }

    public void setCtxScheduler(String ctxScheduler) {
        CtxScheduler = ctxScheduler;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getChangeHandlerType() {
        return changeHandlerType;
    }

    public void setChangeHandlerType(String changeHandlerType) {
        this.changeHandlerType = changeHandlerType;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public String getRuleFilePath() {
        return ruleFilePath;
    }

    public void setRuleFilePath(String ruleFilePath) {
        this.ruleFilePath = ruleFilePath;
    }

    public String getPatternFilePath() {
        return patternFilePath;
    }

    public void setPatternFilePath(String patternFilePath) {
        this.patternFilePath = patternFilePath;
    }

    public static CMIDConfig getINSTANCE() {
        return INSTANCE;
    }

    public static void setINSTANCE(CMIDConfig INSTANCE) {
        CMIDConfig.INSTANCE = INSTANCE;
    }

    public String getOracleFilePath() {
        return oracleFilePath;
    }

    public void setOracleFilePath(String oracleFilePath) {
        this.oracleFilePath = oracleFilePath;
    }
}
