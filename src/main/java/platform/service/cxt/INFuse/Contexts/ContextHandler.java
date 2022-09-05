package platform.service.cxt.INFuse.Contexts;

import platform.service.cxt.INFuse.Patterns.PatternHandler;

import java.util.List;

public abstract class ContextHandler {
    private final PatternHandler patternHandler;

    public ContextHandler(PatternHandler patternHandler) {
        this.patternHandler = patternHandler;
    }

    public PatternHandler getPatternHandler() {
        return patternHandler;
    }

    public abstract void generateChanges(String line, List<ContextChange> changeList) throws Exception;
}
