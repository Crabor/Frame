package platform.service.cxt.WebConnector;

import java.io.Serializable;

public class CtxRuntimeStatus implements Serializable {
    private long recv;
    private long checked;
    private long filter;
    private long send;
    public CtxRuntimeStatus(){
        recv = checked = filter = send = 0;
    }
    public void addRecv(){
        recv ++;
    }
    public void addChk(){
        checked ++;
    }
    public void addChk(int num){
        checked = checked + num;
    }
    public void addfilter(){
        filter ++;
    }
    public void addfilter(int num){
        filter = filter + num;
    }
    public void addSend(){
        send ++;
    }

    public String toString(){
        return "{\"recv\": "+recv +", \"checked\": " + checked/4 +", \"filter\": " + filter+ ", \"send\": "+send+"}";
    }
}
