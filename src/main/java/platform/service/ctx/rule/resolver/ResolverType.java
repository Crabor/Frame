package platform.service.ctx.rule.resolver;

public enum ResolverType {
    IN_TIME,
    DELAY;

    public static ResolverType fromString(String resultStr){
        for(ResolverType result : ResolverType.values()){
            if(result.name().equalsIgnoreCase(resultStr)){
                return  result;
            }
        }
        throw new IllegalArgumentException("No constant with text " + resultStr + " found");
    }
}


