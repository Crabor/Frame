package platform.service.ctx.item;

import com.alibaba.fastjson.JSONObject;
import common.struct.SensorData;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.ctxServer.AbstractCtxServer;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class ItemManager {

    private final AbstractCtxServer ctxServer;
    private final AtomicLong atomicLong;

    private final ConcurrentHashMap<Long, Item> originalItemMap;

    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> channel2IndexQue;

    private final ConcurrentHashMap<Long, Item> validatedItemMap;

    public ItemManager(AbstractCtxServer ctxServer) {
        this.ctxServer = ctxServer;
        this.atomicLong = new AtomicLong(0);
        this.originalItemMap = new ConcurrentHashMap<>();
        this.channel2IndexQue = new ConcurrentHashMap<>();
        this.validatedItemMap = new ConcurrentHashMap<>();
    }

    //框架2.1
    public void createIndexQue(String sensorName){
        channel2IndexQue.put(sensorName, new ConcurrentLinkedQueue<>());
    }

    public void removeIndexQue(String sensorName){
        channel2IndexQue.remove(sensorName);
    }

    public void addIndexIntoQue(String sensorName, long itemIndex){
        channel2IndexQue.get(sensorName).add(itemIndex);
    }

    public Item addItem(String sensorName, SensorData sensorData){
        //初始化一个item
        long itemIndex = atomicLong.getAndIncrement();
        Item item = new Item(itemIndex);
        item.addContext(buildContext(itemIndex, sensorName, sensorData));

        //将item加入originalItemMap
        originalItemMap.put(itemIndex, item);

        //将itemIndex加入channel2IndexQue
        addIndexIntoQue(sensorName, itemIndex);

        return item;
    }

    public Item getItem(long itemIndex){
        return originalItemMap.get(itemIndex);
    }

    public void removeItem(long itemIndex){
        originalItemMap.remove(itemIndex);
    }

    public void addValidatedItem(long itemIndex, Context context){
        Item validatedItem = new Item(itemIndex);
        validatedItem.addContext(cloneContext(context));
        validatedItemMap.put(itemIndex, validatedItem);
        ctxServer.getServerStatistics().increaseCheckedAndResolvedMsgNum();
    }

    public Item getValidatedItem(long itemIndex){
        return validatedItemMap.get(itemIndex);
    }

    public void removeValidatedItem(long itemIndex){
        validatedItemMap.remove(itemIndex);
    }

    public Map.Entry<String, JSONObject> buildValidatedItemJsonObj(final Item validatedMsg){
        Context context = validatedMsg.getContext();
        if(context == null){
            return null;
        }
        else{
            JSONObject dataObj = new JSONObject();
            for(String field : context.getContextFields().keySet()){
                dataObj.put(field, context.getContextFields().get(field));
            }
            return new AbstractMap.SimpleEntry<>(context.getContextId().split("_")[0], dataObj);
        }
    }

    public AbstractCtxServer getCtxServer() {
        return ctxServer;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> getChannel2IndexQue() {
        return channel2IndexQue;
    }

    public ConcurrentHashMap<Long, Item> getOriginalItemMap() {
        return originalItemMap;
    }

    public ConcurrentHashMap<Long, Item> getValidatedItemMap() {
        return validatedItemMap;
    }

    public void reset(){
        originalItemMap.clear();
        for(ConcurrentLinkedQueue<Long> que : channel2IndexQue.values()){
            que.clear();
        }
        validatedItemMap.clear();
    }

    private Context buildContext(long index, String sensorName, SensorData sensorData){
        Context context = new Context();
        context.setContextId(sensorName + "_" + index);
        for(String filed : sensorData.getAllData().keySet()){
            context.getContextFields().put(filed, String.valueOf(sensorData.getAllData().get(filed)));
        }
        return context;
    }

    private Context cloneContext(final Context context){
        if(context == null){
            return null;
        }
        Context retContext = new Context();
        retContext.setContextId(context.getContextId());
        for(Map.Entry<String, String> field : context.getContextFields().entrySet()){
            retContext.getContextFields().put(field.getKey(), field.getValue());
        }
        return retContext;
    }
}
