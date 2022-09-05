package platform.service.cxt.INFuse.Contexts;

import platform.service.cxt.INFuse.Patterns.PatternHandler;

public class ContextHandlerFactory {

    public ContextHandler getContextHandler(String type, PatternHandler patternHandler){
        if(type == null){
            return null;
        }
        if(type.equalsIgnoreCase("taxi")){
            return new TaxiContextHandler(patternHandler);
        }
        else if(type.equalsIgnoreCase("test")){
            return new TestContextHandler(patternHandler);
        }
        else if(type.equalsIgnoreCase("plat")){
            return new PlatContextHandler(patternHandler);
        }
        return null;
    }
}
