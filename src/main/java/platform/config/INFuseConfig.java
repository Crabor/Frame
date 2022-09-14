package platform.config;

public class INFuseConfig {

    private String dataFile;
    private String bfuncFile;
    private String outPutFilePath; //logFilePath
    private String ruleFilePath;
    private String patternFilePath;
    private String CtxFixer;
    private String CtxCleaner; //approach

    public INFuseConfig(String dataFile, String bfuncFile, String outPutFilePath, String ruleFilePath, String patternFilePath, String ctxFixer, String ctxCleaner) {
        this.dataFile = dataFile;
        this.bfuncFile = bfuncFile;
        this.outPutFilePath = outPutFilePath;
        this.ruleFilePath = ruleFilePath;
        this.patternFilePath = patternFilePath;
        CtxFixer = ctxFixer;
        CtxCleaner = ctxCleaner;
    }

    public String getDataFile() {
        return dataFile;
    }

    public String getBfuncFile() {
        return bfuncFile;
    }

    public String getOutPutFilePath() {
        return outPutFilePath;
    }

    public String getRuleFilePath() {
        return ruleFilePath;
    }

    public String getPatternFilePath() {
        return patternFilePath;
    }

    public String getCtxFixer() {
        return CtxFixer;
    }

    public String getCtxCleaner() {
        return CtxCleaner;
    }
}
