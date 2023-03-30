package platform.ui;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;

public class AnalogClock extends JPanel implements Runnable {
    private Thread runner = null;
    private final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");

    public static void main(String[] args) {
        JFrame f = new JFrame("Analog Clock");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container content = f.getContentPane();
        AnalogClock ac = new AnalogClock();
        content.add(ac, BorderLayout.CENTER);
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        ac.start();
    }

    public AnalogClock() {
        setPreferredSize(new Dimension(300, 300));
        setBackground(Color.white);
    }

    public void start() {
        if (runner == null) {
            runner = new Thread(this);
            runner.start();
        }
    }

    public void stop() {
        if (runner != null) {
            runner.interrupt();
            runner = null;
        }
    }

    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawHand(double angle, int radius, Graphics2D g2) {
        double rad = Math.toRadians(angle);
        int x = (int)(radius * Math.cos(rad));
        int y = (int)(radius * Math.sin(rad));
        g2.drawLine(getWidth() / 2, getHeight() / 2, getWidth() / 2 + x, getHeight() / 2 + y);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int radius = Math.min(getWidth(), getHeight()) / 2;
        g2.setColor(Color.black);
        g2.drawOval(getWidth() / 2 - radius, getHeight() / 2 - radius, radius * 2, radius * 2);
        Calendar now = Calendar.getInstance();
        float hour = now.get(Calendar.HOUR);
        float minute = now.get(Calendar.MINUTE);
        float second = now.get(Calendar.SECOND);
        float frac = minute / 60.0f;
        float hourAngle = (hour + frac) * 30.0f - 90.0f;
        float minuteAngle = minute * 6.0f - 90.0f;
        float secondAngle = second * 6.0f;
        g2.setColor(Color.blue);
        drawHand(hourAngle, radius / 2, g2);
        drawHand(minuteAngle, radius * 3 / 4, g2);
        g2.setColor(Color.red);
        drawHand(secondAngle, radius - 10, g2);
        g2.setColor(Color.black);
        g2.drawString(sdf.format(now.getTime()), 10, getHeight() - 10);
    }
}
