package common.util;

import com.alibaba.fastjson.JSONObject;
import platform.communication.socket.CmdRet;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.ServiceType;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

// 工具类
public class Util {
    public static String getSimpleName(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static String getSimpleFileName(String name) {
        return name.substring(name.lastIndexOf("/") + 1);
    }

    public static String randomJSONCarData(){
        int max = 120;
        return "{\"front\":" + Math.random()*max +
                ", \"back\":" + Math.random()*max +
                ", \"left\":" + Math.random()*max +
                ", \"right\":" + Math.random()*max +"}";
    }

    public static boolean isTraceFile(String fileName) {
        return fileName.matches(".*/?[\\w\\.]+-line\\d+-grp\\d+\\.[a-zA-Z]+$");
    }

    public static Tuple3<String, Integer, Integer> getAppNameLineNumberGroup(String fileName) {
        int index0 = fileName.lastIndexOf('/');
        int index1 = fileName.indexOf('-');
        int index2 = fileName.indexOf('-', index1 + 1);
        int index3 = fileName.lastIndexOf('.');
        String appName = fileName.substring(index0 + 1, index1);
        Integer lineNumber = Integer.parseInt(fileName.substring(index1 + 5, index2));
        Integer group = Integer.parseInt(fileName.substring(index2 + 4, index3));
        return Tuples.of(appName, lineNumber, group);
    }

    public static <V> V getMaxValue(Map<Integer, V> m) {
        final int[] max = {Integer.MIN_VALUE};
        m.forEach((k, v) -> {
            if (k > max[0]) {
                max[0] = k;
            }
        });
        return m.get(max[0]);
    }

    public static <V> Integer getMaxKey(Map<Integer, V> m) {
        final int[] max = {Integer.MIN_VALUE};
        m.forEach((k, v) -> {
            if (k > max[0]) {
                max[0] = k;
            }
        });
        return max[0];
    }

    public static <V> List<List<V>> mapListToListList(Map<Integer, List<V>> m) {
        List<List<V>> ret = new ArrayList<>();
        m.forEach((k, v) -> {
            ret.add(v);
        });
        return ret;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static String makeFirstCharUpperCase(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void clearInfoForFile(String fileName) {
        File file =new File(fileName);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            if(file.isDirectory()){
                File[] files = file.listFiles();
                for (File file1 : files) {
                    clearInfoForFile(file1.getAbsolutePath());
                }
            }else{
                FileWriter fileWriter =new FileWriter(file);
                fileWriter.write("");
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double limit(double min, double max, double value) {
        if (value > 0) {
            return Math.min(value, max);
        } else {
            return Math.max(value, min);
        }
    }

    public static String formatCommand(String cmd, String args) {
        JSONObject jo = new JSONObject();
        jo.put("cmd", cmd);
        jo.put("args", args);
        return jo.toJSONString();
    }

    public static CmdRet decodeCommandRet(String msg) {
        return new CmdRet(msg);
    }

    public static String collectionToString(Collection<String> collection, String delimiter) {
        String[] setArray = collection.toArray(new String[0]);
        StringBuilder ret = new StringBuilder(setArray[0]);
        for (int i = 1 ; i < setArray.length; i++) {
            ret.append(delimiter).append(setArray[i]);
        }
        return ret.toString();
    }
    
    public static String stringArrayToString(String[] strings, String delimiter) {
        StringBuilder ret = new StringBuilder(strings[0]);
        for (int i = 1 ; i < strings.length; i++) {
            ret.append(delimiter).append(strings[i]);
        }
        return ret.toString();
    }

    public static String formatToJsonStringExcept(String[] keys, String[] values, String except) {
        JSONObject jo = new JSONObject();
        for (int i = 0; i < keys.length; i++) {
            if (values[i].equals(except)) {
                continue;
            }
            jo.put(keys[i], values[i]);
        }
        return jo.toJSONString();
    }
    
    public static String formatToJsonString(String key, String value) {
        JSONObject jo = new JSONObject(1);
        jo.put(key, value);
        return jo.toJSONString();
    }

    public static String keysValuesToJsonString(String[] keys, String[] values) {
        JSONObject jo = new JSONObject();
        for (int i = 0; i < keys.length; i++) {
            jo.put(keys[i], values[i]);
        }
        return jo.toJSONString();
    }

//    public static CmdType parseString(String cmdTypeString) {
//        CmdType ret = null;
//        if (cmdTypeString.equalsIgnoreCase("SOCKET_SENSOR_ON")) {
//            ret = CmdType.SOCKET_SENSOR_ON;
//        } else if (cmdTypeString.equalsIgnoreCase("SOCKET_SENSOR_OFF")) {
//            ret = CmdType.SOCKET_SENSOR_OFF;
//        } else if (cmdTypeString.equalsIgnoreCase("SOCKET_SENSOR_ALIVE")) {
//            ret = CmdType.SOCKET_SENSOR_ALIVE;
//        } else if (cmdTypeString.equalsIgnoreCase("SOCKET_SENSOR_GET")) {
//            ret = CmdType.SOCKET_SENSOR_GET;
//        } else if (cmdTypeString.equalsIgnoreCase("SOCKET_ACTUATOR_ON")) {
//            ret = CmdType.SOCKET_ACTUATOR_ON;
//        } else if (cmdTypeString.equalsIgnoreCase("SOCKET_ACTUATOR_OFF")) {
//            ret = CmdType.SOCKET_ACTUATOR_OFF;
//        } else if (cmdTypeString.equalsIgnoreCase("SOCKET_ACTUATOR_ALIVE")) {
//            ret = CmdType.SOCKET_ACTUATOR_ALIVE;
//        } else if (cmdTypeString.equalsIgnoreCase("SOCKET_ACTUATOR_SET")) {
//            ret = CmdType.SOCKET_ACTUATOR_SET;
//        } else if (cmdTypeString.equalsIgnoreCase("SOCKET_CHANNEL_MSG")) {
//            ret = CmdType.SOCKET_CHANNEL_MSG;
//        } else if (cmdTypeString.equalsIgnoreCase("CTX_RESET")) {
//            ret = CmdType.CTX_RESET;
//        }
//        return ret;
//    }
    public static String readFileContent(String fileName, String replace_line_feed) {
        Path path = Paths.get(fileName);
        String content = null;
        try {
            content = Files.lines(path).collect(Collectors.joining(replace_line_feed));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String readFileContent(String fileName) {
        return readFileContent(fileName, "");
    }

    public static void writeFileContent(String dir, String name, String content, String replace_line_feed) {
        File file = new File(dir);
//        System.out.println(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        Path filePath = Path.of(dir + "/" + name);
        String realContent = content.replace(replace_line_feed, "\n");
        try {
            Files.writeString(filePath, realContent,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ServiceType parseServiceType(String type) {
        ServiceType ret = null;
        if (type.equalsIgnoreCase("ctx")) {
            ret = ServiceType.CTX;
        } else if (type.equalsIgnoreCase("inv")) {
            ret = ServiceType.INV;
        }
        return ret;
    }

    public static CmdType parseCmdType(String type) {
        CmdType ret = null;
        if (type.equalsIgnoreCase("start")) {
            ret = CmdType.START;
        } else if (type.equalsIgnoreCase("stop")) {
            ret = CmdType.STOP;
        } else if (type.equalsIgnoreCase("reset")) {
            ret = CmdType.RESET;
        }
        return ret;
    }

    // 定义计算最大公约数的方法
    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    // 定义计算最小公倍数的方法
    public static int lcm(int a, int b) {
        return (a * b) / gcd(a, b);
    }

    // 定义计算整型列表中所有元素最小公倍数的方法
    public static int lcmOfCollection(Collection<Integer> nums) {
        int ret = 1;
        for (int num : nums) {
            ret = lcm(ret, num);
        }
        return ret;
    }

    public static double distance(double[] a, double[] b) {
        double ret = 0;
        for (int i = 0; i < a.length; i++) {
            ret += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(ret);
    }

    public static double[] toDoubleArray(Collection<Object> collection) {
        double[] ret = new double[collection.size()];
        int i = 0;
        for (Object o : collection) {
            ret[i++] = Double.parseDouble(o.toString());
        }
        return ret;
    }

    public static List<Integer> toIntegerList(String str, String delimiter) {
        List<Integer> ret = new ArrayList<>();
        String[] strs = str.split(delimiter);
        for (String s : strs) {
            ret.add(Integer.parseInt(s));
        }
        return ret;
    }

    public static String toString(List<Integer> list, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(lcmOfCollection(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
    }
}
