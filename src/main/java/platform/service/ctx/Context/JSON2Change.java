package platform.service.ctx.Context;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSON2Change {

    JSON2Change(int index,String s){}
    public static List<Change> ConvertJSON (String s){
        List<Change> changeList = new ArrayList<>();
        JSONObject object = JSONObject.parseObject(s);
        for(Map.Entry entry: object.entrySet()){
            Change temp = new Change(Change.ADDITION, String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            changeList.add(temp);
            System.out.println(temp.toString());
        }
        return changeList;
    }
    public static void main(String[] args){
        ConvertJSON("{\"front\":6.872090687793384, \"back\":5.456504262554081, \"left\":9.032111470566882, \"right\":22.6266064994054 }");
    }
}
