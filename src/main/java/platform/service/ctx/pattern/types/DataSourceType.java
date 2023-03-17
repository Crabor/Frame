package platform.service.ctx.pattern.types;

public enum DataSourceType {
    SENSOR,
    PATTERN;

    public static DataSourceType fromString(String resultStr){
        for(DataSourceType result : DataSourceType.values()){
            if(result.name().equalsIgnoreCase(resultStr)){
                return result;
            }
        }

        throw new IllegalArgumentException("No constant with text " + resultStr + " found");
    }
}
