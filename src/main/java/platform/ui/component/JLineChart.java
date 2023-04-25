package platform.ui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

/**
 * JLineChart是一个折线图组件，可以动态地修改数据并实时反馈到UI上。
 */
public class JLineChart extends JPanel {
    private static final long serialVersionUID = 1L;

    // 存储折线图的数据点
    private List<Double> data = new ArrayList<>();

    // 折线颜色
    private Color lineColor = Color.blue;

    // 纵轴最大值和最小值
    private double minValue = 0;
    private double maxValue = 100;

    // 横轴和纵轴的标签
    private String xAxisLabel = "";
    private String yAxisLabel = "";

    /**
     * 构造方法，初始化折线图组件。
     */
    public JLineChart() {
        setPreferredSize(new Dimension(400, 300)); // 设置默认大小
    }

    /**
     * 设置折线颜色。
     * @param color 折线颜色
     */
    public void setLineColor(Color color) {
        this.lineColor = color;
        repaint();
    }

    /**
     * 设置纵轴范围。
     * @param minValue 纵轴最小值
     * @param maxValue 纵轴最大值
     */
    public void setRange(double minValue, double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        repaint();
    }

    /**
     * 设置横轴标签。
     * @param label 横轴标签
     */
    public void setXAxisLabel(String label) {
        this.xAxisLabel = label;
    }

    /**
     * 设置纵轴标签。
     * @param label 纵轴标签
     */
    public void setYAxisLabel(String label) {
        this.yAxisLabel = label;
    }

    /**
     * 添加一个数据点。
     * @param value 数据值
     */
    public void addDataPoint(double value) {
        data.add(value);
        repaint(); // 重新绘制折线图
    }

    /**
     * 清空所有数据点。
     */
    public void clearData() {
        data.clear();
        repaint(); // 重新绘制折线图
    }

    /**
     * 绘制折线图。
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data.isEmpty()) {
            return; // 如果没有数据，不进行绘制
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 开启抗锯齿

        int width = getWidth();
        int height = getHeight();

        // 绘制坐标轴
        g2d.drawLine(50, height - 70, width - 20, height - 70); // X轴
        g2d.drawLine(50, height - 70, 50, 20); // Y轴

//        // 绘制坐标轴箭头
//        int arrowSize = 6; // 箭头大小
//        int xx1 = width - arrowSize - 20;
//        int yy1 = height - 70 - arrowSize / 2;
//        int yy2 = height - 70 + arrowSize / 2;
//        g2d.fillPolygon(new int[] {xx1, xx1 - arrowSize, xx1 - arrowSize}, new int[] {yy1, yy2, yy1 + arrowSize}, 3); //X轴箭头
//        xx1 = 50 + arrowSize / 2;
//        int yy = 30 + arrowSize;
//        g2d.fillPolygon(new int[] {xx1, xx1 - arrowSize / 2, xx1 + arrowSize / 2}, new int[] {yy, yy + arrowSize, yy + arrowSize}, 3); // Y轴箭头


        // 绘制坐标轴标签
        g2d.drawString(xAxisLabel, width / 2, height - 30);
        g2d.rotate(Math.toRadians(-90), 20, height / 2);
        g2d.drawString(yAxisLabel, 20, height / 2);

        // 恢复坐标系
        g2d.rotate(Math.toRadians(90), 20, height / 2);

        // 计算数据点之间的距离和刻度值
        int numPoints = data.size();
        double xScale = (double) (width - 70) / (numPoints - 1);
        double yScale = (double) (height - 100) / (maxValue - minValue);

        // 绘制数据点连线
        g2d.setColor(lineColor);
        for (int i = 0; i < numPoints - 1; i++) {
            int x1 = (int) (i * xScale) + 50;
            int y1 = (int) ((maxValue - data.get(i)) * yScale) + 30;
            int x2 = (int) ((i + 1) * xScale) + 50;
            int y2 = (int) ((maxValue - data.get(i + 1)) * yScale) + 30;
            g2d.drawLine(x1, y1, x2, y2);
        }

        // 绘制数据点
        g2d.setColor(Color.red);
        for (int i = 0; i < numPoints; i++) {
            int x = (int) (i * xScale) + 50;
            int y = (int) ((maxValue - data.get(i)) * yScale) + 30;
            g2d.fillOval(x - 3, y - 3, 6, 6);
        }

        // 绘制纵轴刻度值和标签
        g2d.setColor(Color.black);
        for (double value = minValue; value <= maxValue; value += (maxValue - minValue) / 10) {
            int y = (int) ((maxValue - value) * yScale) + 30;
            g2d.drawLine(45, y, 50, y);
            String label = String.format("%.1f", value);
            g2d.drawString(label, 40 - g2d.getFontMetrics().stringWidth(label), y + 5);
        }

        // 绘制横轴刻度值和标签
        g2d.setColor(Color.black);
        for (int i = 0; i < numPoints; i++) {
            int x = (int) (i * xScale) + 50;
            g2d.drawLine(x, height - 65, x, height - 70);
            String label = Integer.toString(i + 1);
            g2d.drawString(label, x - g2d.getFontMetrics().stringWidth(label) / 2, height - 50);
        }
    }

    public static void main(String[] args) {
        // 创建一个JFrame窗口并设置参数
        JFrame frame = new JFrame("JLineChart Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // 创建一个JLineChart组件并添加到窗口中
        JLineChart chart = new JLineChart();
        chart.setLineColor(Color.red); // 设置折线颜色为红色
        chart.setRange(0, 10); // 设置纵轴范围为0到10
        chart.setXAxisLabel("X Axis"); // 设置横轴标签
        chart.setYAxisLabel("Y Axis"); // 设置纵轴标签
        frame.add(chart);

        // 添加一些数据点并重新绘制折线图
        chart.addDataPoint(2);
        chart.addDataPoint(4);
        chart.addDataPoint(6);
        chart.addDataPoint(8);
        chart.addDataPoint(5);

        // 显示窗口
        frame.setVisible(true);

        // Test dynamic updating of pie chart data
        try {
            Thread.sleep(3000); // wait for 3 seconds
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        chart.addDataPoint(7);
        chart.addDataPoint(9);
        chart.addDataPoint(3);
        chart.addDataPoint(1);
    }
}