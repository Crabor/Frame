package platform.testUI;

import java.awt.Color;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class test {

    public static void main(String[] args) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("A", new Double(20));
        dataset.setValue("B", new Double(30));
        dataset.setValue("C", new Double(50));

        JFreeChart chart = ChartFactory.createPieChart(
                "Dynamic Pie Chart Example",
                dataset,
                true,
                true,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("A", Color.RED);
        plot.setSectionPaint("B", Color.BLUE);
        plot.setSectionPaint("C", Color.GREEN);

        JFrame frame = new JFrame("Dynamic Pie Chart Example");
        ChartPanel chartPanel = new ChartPanel(chart);
        frame.getContentPane().add(chartPanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        //模拟动态更新饼图数据
        for (int i = 0; i < 10; i++) {
            dataset.setValue("A", Math.random() * 100);
            dataset.setValue("B", Math.random() * 100);
            dataset.setValue("C", Math.random() * 100);
            try {
                Thread.sleep(1000); //等待1秒钟
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
