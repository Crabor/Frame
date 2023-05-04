package platform.testother;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;

public class Main3 {
    static int[][][] visit;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int m = br.readLine().charAt(0) - '0';
        int n = br.readLine().charAt(0) - '0';
        if (m > n) {
            System.out.println(0);
            return;
        }
        int[][] initPoints = new int[n][2];
        for (int i = 0; i < n; i++) {
            String[] line = br.readLine().split(" ");
            initPoints[i][0] = Integer.parseInt(line[0]);
            initPoints[i][1] = Integer.parseInt(line[1]);
        }
        int xMin, xMax, yMin, yMax;
        xMin = xMax = initPoints[0][0];
        yMin = yMax = initPoints[0][1];
        for (int i = 1; i < n; i++) {
            if (initPoints[i][0] < xMin) {
                xMin = initPoints[i][0];
            }
            if (initPoints[i][0] > xMax) {
                xMax = initPoints[i][0];
            }
            if (initPoints[i][1] < yMin) {
                yMin = initPoints[i][1];
            }
            if (initPoints[i][1] > yMax) {
                yMax = initPoints[i][1];
            }
        }
        int xLen = xMax - xMin;
        int yLen = yMax - yMin;
        int[][] points = new int[n][2];
        for (int i = 0; i < n; i++) {
            points[i][0] = initPoints[i][0] - xMin;
            points[i][1] = initPoints[i][1] - yMin;
        }
        visit = new int[n][xLen + 1][yLen + 1];
        for (int i = 0; i < n; i++) {
            visit[i][points[i][0]][points[i][1]] = 1;
        }
        //每个点都代表一种颜色，都过一天都会向左上、上、右上、左、右、左下、下、右下八个方向扩散一个单位，且可以和其他颜色叠加，求至少多少天后有一个位置的颜色树等于m？
        int day = 0;
        while(true) {
            for (int i = 0; i < n; i++) {
                day++;
                //扩散
                for (int j = 0; j < visit[i].length; j++) {
                    for (int k = 0; k < visit[i][j].length; k++) {
                        if (visit[i][j][k] == 1) {
                            if (j > 0) {
                                visit[i][j - 1][k] = 1;
                            }
                            if (j < xLen) {
                                visit[i][j + 1][k] = 1;
                            }
                            if (k > 0) {
                                visit[i][j][k - 1] = 1;
                            }
                            if (k < yLen) {
                                visit[i][j][k + 1] = 1;
                            }
                            if (j > 0 && k > 0) {
                                visit[i][j - 1][k - 1] = 1;
                            }
                            if (j > 0 && k < yLen) {
                                visit[i][j - 1][k + 1] = 1;
                            }
                            if (j < xLen && k > 0) {
                                visit[i][j + 1][k - 1] = 1;
                            }
                            if (j < xLen && k < yLen) {
                                visit[i][j + 1][k + 1] = 1;
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < xLen + 1; i++) {
                for (int j = 0; j < yLen + 1; j++) {
                    if (colorNum(i, j) == m) {
                        System.out.println(day);
                        return;
                    }
                }
            }
        }
    }

    public static int colorNum(int x, int y) {
        int sum = 0;
        for (int i = 0; i < visit.length; i++) {
            sum += visit[i][x][y];
        }
        return sum;
    }
}
