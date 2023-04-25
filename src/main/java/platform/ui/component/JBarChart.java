package platform.ui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.*;

public class JBarChart extends JPanel {

    private ArrayList<Integer> data;  // 存储条形图数据的数组
    private Color barColor;  // 条形图颜色
    private int maxValue;  // 数据中的最大值，用于计算比例
    private int padding = 20;  // 组件边距

    public JBarChart(ArrayList<Integer> data, Color barColor) {
        this.data = data;
        this.barColor = barColor;

        // 计算最大值
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) > maxValue) {
                maxValue = data.get(i);
            }
        }
    }

    public void setData(ArrayList<Integer> data) {
        this.data = data;
        // 更新最大值
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) > maxValue) {
                maxValue = data.get(i);
            }
        }
        repaint();  // 刷新UI
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制背景
        g.setColor(Color.WHITE);
        g.fillRect(padding, padding, getWidth() - 2 * padding, getHeight() - 2 * padding);

        // 绘制条形图
        g.setColor(barColor);
        int barWidth = (getWidth() - 2 * padding) / data.size() - 2;
        for (int i = 0; i < data.size(); i++) {
            int barHeight = (int) ((double)data.get(i) / maxValue * (getHeight() - 2 * padding));
            g.fillRect(padding + i * (barWidth + 2), getHeight() - barHeight - padding, barWidth, barHeight);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 200);  // 组件的首选大小
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("JBarChart Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ArrayList<Integer> data = new ArrayList<>();
        data.add(10);
        data.add(25);
        data.add(30);
        data.add(20);
        data.add(15);

        JBarChart barChart = new JBarChart(data, Color.BLUE);
        frame.getContentPane().add(barChart);
        frame.pack();
        frame.setVisible(true);
        // Test dynamic updating of pie chart data
        try {
            Thread.sleep(3000); // wait for 3 seconds
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        // 动态修改数据
        data.set(2, 50);
        barChart.setData(data);


    }
}
