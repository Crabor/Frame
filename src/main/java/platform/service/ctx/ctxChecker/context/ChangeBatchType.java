package platform.service.ctx.ctxChecker.context;

public enum ChangeBatchType {
    OVERDUE,
    GENERATE;

    public static ChangeBatchType fromString(String resultStr){
        for(ChangeBatchType result : ChangeBatchType.values()){
            if(result.name().equalsIgnoreCase(resultStr)){
                return result;
            }
        }
        throw new IllegalArgumentException("No constant with text " + resultStr + " found");
    }
}
