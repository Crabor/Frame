package platform.service.cxt.Context;

import platform.config.Configuration;

import java.util.LinkedList;
import java.util.Queue;

public class ContextBuffer {
    private String contextName;

    private Queue<Context> cleanQueue;
    private Queue<Context> rawQueue;
    private int BUFFER_RAW_MAX;
    private int BUFFER_CLEAN_MAX;
    ContextBuffer(int BUFFER_raw_SIZE, int BUFFER_clean_SIZE){
        cleanQueue = new LinkedList<>();
        rawQueue = new LinkedList<>();
        BUFFER_RAW_MAX = BUFFER_raw_SIZE;
        BUFFER_CLEAN_MAX = BUFFER_clean_SIZE;
    }
    public Context pollFirstRawData(){
        return rawQueue.poll(); //returns null if it's an empty queue
    }
    public Context pollFirstCleanData(){
        Context c = cleanQueue.poll();
        if (c!= null) {
            try {
                ContextManager.getChangeInvoked().put(new Change<String>(Change.DELETION,c).toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return c; //returns null if it's an empty queue
    }
    public void insertRawData(Context context){
        if(context == null)
            return;
        while (rawQueue.size() >= BUFFER_RAW_MAX) {
            rawQueue.poll();
        }
        rawQueue.add(context);
        //System.out.println("Add: "+context.toString());
    }
    public void insertCleanData(Context context){
        if(context == null)
            return;
        while (cleanQueue.size() >= BUFFER_CLEAN_MAX) {
            Context c = cleanQueue.poll();
            if (c!= null)
                try {
                    ContextManager.getChangeInvoked().put(new Change<String>(Change.DELETION,c).toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
        cleanQueue.add(context);

        if (context!= null)
            try {
                ContextManager.getChangeInvoked().put(new Change<String>(Change.ADDITION,context).toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        //System.out.println("Add: "+context.toString());
    }

    public Queue<Context> getRawQueue() {
        return rawQueue;
    }

    public Queue<Context> getCleanQueue() {
        return cleanQueue;
    }

    public int returnBufferSize(){
        if (Configuration.getCtxServerConfig().isCtxCleanOn == true)
            return cleanQueue.size();
        else
            return rawQueue.size();
    }
    public void displayAllRawInQueue(){
        for(Context c: rawQueue)
            System.out.println(c.toString());
    }
    public void displayAllCleanInQueue(){
        for(Context c: cleanQueue)
            System.out.println(c.toString());
    }
}
