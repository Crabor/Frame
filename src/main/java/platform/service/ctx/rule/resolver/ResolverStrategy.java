package platform.service.ctx.rule.resolver;

public enum ResolverStrategy {
    DROP_LATEST,
    DROP_ALL,
    CUSTOMIZED;


    public static ResolverStrategy fromString(String resultStr){
        for(ResolverStrategy result : ResolverStrategy.values()){
            if(result.name().equalsIgnoreCase(resultStr)){
                return result;
            }
        }
        throw new IllegalArgumentException("No constant with text " + resultStr + " found");
    }
}
