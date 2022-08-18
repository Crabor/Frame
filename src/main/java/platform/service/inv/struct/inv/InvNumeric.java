package platform.service.inv.struct.inv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.opencsv.CSVReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.service.cxt.CMID.checker.EccChecker;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvNumeric extends InvAbstract {
    private static final Log logger = LogFactory.getLog(InvNumeric.class);
    private double min;
    private double max;

    public InvNumeric() {
        min = 0;
        max = 0;
    }

    @Override
    public boolean isViolated(double value) {
        //TODO:仅为演示
//        return value < min;
        return value < min || value > max;
    }

    @Override
    public double getDiff(double value) {
        double ret = 0;
        if (value < min) {
            ret = value - min;
        } else if (value > max) {
            ret = value - max;
        }
        return ret;
    }


    @Override
    public void setInv() {
        String traceFileName = "output/trace/csv/" + appName + "-" + "line" + lineNumber + "-" + "grp" + group + ".csv";
        try {
            CSVReader reader = new CSVReader(new FileReader(traceFileName));
            String[] names = reader.readNext();
            List<List<Double>> traces = new ArrayList<>();
            for (int i = 0; i < names.length; i++) {
                traces.add(new ArrayList<>());
            }
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                for (int i = 0; i < nextLine.length; i++) {
                    traces.get(i).add(Double.valueOf(nextLine[i]));
                }
            }

            JSONObject jo = new JSONObject();
            //gen inv
            for (int i = 0; i < names.length; i++) {
                double _min = Collections.min(traces.get(i));
                double _max = Collections.max(traces.get(i));
                if (names[i].equals(varName)) {
                    min = _min;
                    max = _max;
                }
                String invStr = "" + _min + " <= " + names[i] + " <= " + _max;
                JSONArray ja = new JSONArray();
                ja.add(invStr);
                JSONObject jo1 = new JSONObject();
                jo1.put("numeric", ja);
                jo.put(names[i], jo1);
            }

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(invFileName, false)));
            out.write(JSON.toJSONString(jo, SerializerFeature.PrettyFormat));
            out.close();

            logger.info(invFileName + ":");
            logger.info(JSON.toJSONString(jo, SerializerFeature.PrettyFormat));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getInv() {
        File invFile = new File(invFileName);
        try {
            String str = FileUtils.readFileToString(invFile,"UTF-8");
            JSONObject jo = JSONObject.parseObject(str);
            JSONObject obj = (JSONObject) jo.get(varName);
            JSONArray ja = (JSONArray) obj.get("numeric");
            String invStr = ja.getString(0);
            min = Double.parseDouble(invStr.substring(0, invStr.indexOf("<=") - 1));
            max = Double.parseDouble(invStr.substring(invStr.lastIndexOf("<=") + 3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "{" + min + "~" + max + '}';
    }
}
