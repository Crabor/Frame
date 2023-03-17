package platform.service.inv.algorithm;


import platform.service.inv.struct.SegInfo;

import java.util.*;

public class KMeans {

    int k; // 指定划分的簇数
    double mu; // 迭代终止条件，当各个新质心相对于老质心偏移量小于mu时终止迭代
    double[][] center; // 上一次各簇质心的位置
    int len;//向量的秩
    Map<Integer, List<Integer>> grps;
    List<SegInfo> objects;

    public KMeans(List<SegInfo> objects, int k, double mu, int len) {
        this.k = k;
        this.mu = mu;
        this.len = len;
        center = new double[k][];
        for (int i = 0; i < k; i++)
            center[i] = new double[len];
        this.objects = objects;
    }

    public double calEuraDist(double[] vec1, double[] vec2, int len) {
        double dist = 0;
        for (int i = 0; i < len; i++) {
            double temp = Math.pow((vec1[i] - vec2[i]), 2);
            dist += temp;
        }
        return Math.sqrt(dist);
    }

    public double calEuraDist(Double[] vec1, double[] vec2, int len) {
        double dist = 0;
        for (int i = 0; i < len; i++) {
            double temp = Math.pow((vec1[i] - vec2[i]), 2);
            dist += temp;
        }
        return Math.sqrt(dist);
    }

    public double calEuraDist(List<Double> vec1, double[] vec2, int len) {
        double dist = 0;
        for (int i = 0; i < len; i++) {
            double temp = Math.pow((vec1.get(i) - vec2[i]), 2);
            dist += temp;
        }
        return Math.sqrt(dist);
    }

    // 初始化k个质心，每个质心是len维的向量，每维均在left--right之间
    public void initCenter(int len, List<SegInfo> objects) {
        Random random = new Random(System.currentTimeMillis());
        int[] count = new int[k]; // 记录每个簇有多少个元素
        for (SegInfo object : objects) {
            int id = random.nextInt(10000) % k;
            count[id]++;
            Double[] ecxt = object.eCxt.values().toArray(new Double[0]);
            for (int i = 0; i < len; i++)
                center[id][i] += ecxt[i];
        }
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < len; j++) {
                center[i][j] /= count[i];
            }
        }
    }

    // 把数据集中的每个点归到离它最近的那个质心
    public void classify(List<SegInfo> objects, int len) {
        for (SegInfo object : objects) {
            int index = 0;
            double neardist = Double.MAX_VALUE;
            for (int i = 0; i < k; i++) {
                Double[] ecxt = object.eCxt.values().toArray(new Double[0]);
                double dist = calEuraDist(ecxt, center[i], len);
                if (dist < neardist) {
                    neardist = dist;
                    index = i;
                }
            }
            object.gid = index;
        }
    }

    // 重新计算每个簇的质心，并判断终止条件是否满足，如果不满足更新各簇的质心,如果满足就返回true.len是数据的维数
    public boolean calNewCenter(List<SegInfo> objects, int len) {
//        try {
//            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("CtxServerOutput.txt/kmeans-center.txt", true)));
//            out.write("center" + iter++ + ":\n");
//            for (int i = 0; i < k; i ++) {
//                for (int j = 0; j < len; j++) {
//                    out.write(String.format("%.2f ", center[i][j]));
//                }
//                out.write("\n");
//            }
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        boolean end = true;
        int[] count = new int[k]; // 记录每个簇有多少个元素
        double[][] sum = new double[k][];
        for (int i = 0; i < k; i++)
            sum[i] = new double[len];
        for (SegInfo object : objects) {
            int id = object.gid;
            count[id]++;
            Double[] ecxt = object.eCxt.values().toArray(new Double[0]);
            for (int i = 0; i < len; i++)
                sum[id][i] += ecxt[i];
        }
        for (int i = 0; i < k; i++) {
            if (count[i] != 0) {
                for (int j = 0; j < len; j++) {
                    sum[i][j] /= count[i];
                }
            } else {// 簇中不包含任何点,及时调整质心，并直接退出函数
                int a = (i + 1) % k;
                int b = (i + 3) % k;
                int c = (i + 5) % k;
                for (int j = 0; j < len; j++) {
                    center[i][j] = (center[a][j] + center[b][j] + center[c][j]) / 3;
                }
                return false;
            }
        }
        for (int i = 0; i < k; i++) {
            // 只要有一个质心需要移动的距离超过了mu，就返回false
            if (calEuraDist(sum[i], center[i], len) >= mu) {
                end = false;
                break;
            }
        }
        if (!end) {
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < len; j++) {
                    center[i][j] = sum[i][j];
                }
            }
        }
        return end;
    }

    public void run() {
        initCenter(len, objects);
        classify(objects, len);
        while (!calNewCenter(objects, len)) {
            classify(objects, len);
        }
        grps = new HashMap<>();
        for (SegInfo segInfo : objects) {
            List<Integer> grp = grps.get(segInfo.gid);
            if (grp == null) {
                grps.put(segInfo.gid, new ArrayList<>(List.of(segInfo.iterId)));
            } else {
                grp.add(segInfo.iterId);
            }
        }
    }

    public Map<Integer, List<Integer>> getGrps() {
        return grps;
    }

//    public static void main(String[] args) {
//        List<SegInfo> datasource = new ArrayList<>();
//        int len = datasource.get(0).eCxt.size();
//        // 划分为6个簇，质心移动小于1E-8时终止迭代，重复运行7次
//        KMeans km = new KMeans(4, 1E-10, 7, len);
//        System.out.println(km.run(datasource, len));
//    }
}