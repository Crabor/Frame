package platform.testUI;

import javax.swing.*;

public class test1 {
    public static void main(String[] args) {
        //创建一个只有一个Button的窗口
        JFrame frame = new JFrame("Hello World");
        JButton button = new JButton("Press me");
        frame.getContentPane().add(button);
        frame.pack();
        frame.setVisible(true);
    }
}
