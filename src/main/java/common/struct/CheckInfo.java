package common.struct;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.struct.enumeration.CheckResult;

import java.util.Arrays;

public class CheckInfo {
    String name;
    int lineNumber;
    int iterId;
    long checkTime;
    CheckResult result;

    public CheckInfo(String name, int lineNumber, int iterId, long checkTime, CheckResult result) {
        this.name = name;
        this.lineNumber = lineNumber;
        this.iterId = iterId;
        this.checkTime = checkTime;
        this.result = result;
    }

    @Override
    public String toString() {
        JSONObject infoJson = new JSONObject();
        infoJson.put("name", name);
        infoJson.put("line_number", lineNumber);
        infoJson.put("iter_id", iterId);
        infoJson.put("check_time", checkTime);
        infoJson.put("result", result.toString());
        return infoJson.toJSONString();
    }

    public static CheckInfo fromString(String json) {
        JSONObject infoJson = JSONObject.parseObject(json);
        return new CheckInfo(
                infoJson.getString("name"),
                infoJson.getInteger("line_number"),
                infoJson.getInteger("iter_id"),
                infoJson.getLong("check_time"),
                CheckResult.fromString(infoJson.getString("result")));
    }
}
