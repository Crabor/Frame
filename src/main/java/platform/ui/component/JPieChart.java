package platform.ui.component;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class JPieChart extends JPanel {

    private ArrayList<Slice> slices = new ArrayList<>();

    public void addSlice(Slice slice) {
        this.slices.add(slice);
        repaint();
    }

    public void removeSlice(Slice slice) {
        this.slices.remove(slice);
        repaint();
    }

    public void clearSlices() {
        this.slices.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawPie((Graphics2D) g, getWidth(), getHeight(), slices);
    }

    protected void drawPie(Graphics2D g, int width, int height, ArrayList<Slice> slices) {
        double total = 0.0D;
        for (Slice s : slices) {
            total += s.value;
        }
        if (total == 0.0D) {
            return;
        }
        double curValue = 0.0D;
        int startAngle = 0;
        for (int i = 0; i < slices.size(); i++) {
            startAngle = (int) Math.round(curValue * 360 / total);
            int arcAngle = (int) Math.round(slices.get(i).value * 360 / total);
            g.setColor(slices.get(i).color);
            g.fillArc(0, 0, width - 1, height - 1, startAngle, arcAngle);
            curValue += slices.get(i).value;
        }
    }


    public static class Slice {
        public double value;
        public Color color;

        public Slice(double value, Color color) {
            this.value = value;
            this.color = color;
        }
    }

    public static void main(String[] args) {
        JPieChart pieChart = new JPieChart();
        pieChart.setPreferredSize(new Dimension(300, 300));
        pieChart.addSlice(new JPieChart.Slice(50.0D, Color.RED));
        pieChart.addSlice(new JPieChart.Slice(75.0D, Color.GREEN));
        pieChart.addSlice(new JPieChart.Slice(100.0D, Color.BLUE));

        JFrame frame = new JFrame("Pie Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(pieChart);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Test dynamic updating of pie chart data
        try {
            Thread.sleep(3000); // wait for 3 seconds
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        pieChart.clearSlices();
        pieChart.addSlice(new JPieChart.Slice(20.0D, Color.YELLOW));
        pieChart.addSlice(new JPieChart.Slice(80.0D, Color.CYAN));
    }
}
