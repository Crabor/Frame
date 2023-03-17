package platform.service.ctx.pattern.types;

public enum MatcherType {
    PRIMARY_KEY,
    FUNCTION;

    public static MatcherType fromString(String resultStr){
        for(MatcherType result : MatcherType.values()){
            if(result.name().equalsIgnoreCase(resultStr)){
                return result;
            }
        }
        throw new IllegalArgumentException("No constant with text " + resultStr + " found");
    }
}
