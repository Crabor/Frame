package platform.config;

public class INFuseConfig {


    private final String ruleFilePath;
    private final String bfuncFile;
    private final String patternFilePath;
    private final String mfuncFile;
    private final String CtxFixer;
    private final String CtxCleaner; //approach

    public INFuseConfig(String ruleFilePath, String bfuncFile, String patternFilePath, String mfuncFile, String ctxFixer, String ctxCleaner) {
        this.bfuncFile = bfuncFile;
        this.ruleFilePath = ruleFilePath;
        this.patternFilePath = patternFilePath;
        this.mfuncFile = mfuncFile;
        CtxFixer = ctxFixer;
        CtxCleaner = ctxCleaner;
    }

    public String getBfuncFile() {
        return bfuncFile;
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
