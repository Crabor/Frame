package platform.service.ctx.item;

public enum ItemState {
    DROPPED,
    FIXED,

    INIT;

    public static ItemState fromString(String resultStr){
        for(ItemState itemState : ItemState.values()){
            if(itemState.name().equalsIgnoreCase(resultStr)){
                return itemState;
            }
        }
        throw new IllegalArgumentException("No constant with text " + resultStr + " found");
    }
}
