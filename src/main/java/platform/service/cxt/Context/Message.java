package platform.service.cxt.Context;

public class Message {
    long index;
    String msg;

    Message(long index, String msg){
        this.index = index;
        this.msg = msg;
    }
    public String getMsg(){
        return msg;
    }
    public long getIndex(){
        return index;
    }
}
