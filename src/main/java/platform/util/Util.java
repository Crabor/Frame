package platform.util;

import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import  org.apache.commons.logging.Log;
import  org.apache.commons.logging.LogFactory;

public class Util {
    public static String getSimpleName(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
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

    public static void main(String[] args) {
    }
}
