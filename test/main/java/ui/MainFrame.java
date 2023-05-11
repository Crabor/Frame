package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFrame extends JFrame implements ActionListener {
    private JPanel mainPanel, subPanel1, subPanel2;
    private JButton button1, button2;
    private JLabel label1, label2, label3;
    private JTextField textField1, textField2, textField3, textField4;

    public MainFrame() {
        super("Main Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // create main panel
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 10, 10, 10);

        // create buttons
        button1 = new JButton("Hello");
        button1.addActionListener(this);
        mainPanel.add(button1, c);

        c.gridy++;
        button2 = new JButton("World");
        button2.addActionListener(this);
        mainPanel.add(button2, c);

        // create sub panel 1
        subPanel1 = new JPanel(new GridBagLayout());
        label1 = new JLabel("Hello");
        subPanel1.add(label1);
        label2 = new JLabel("Hello");
        subPanel1.add(label2);
        label3 = new JLabel("Hello");
        subPanel1.add(label3);

        // create sub panel 2
        subPanel2 = new JPanel(new GridBagLayout());
        textField1 = new JTextField("World");
        subPanel2.add(textField1);
        textField2 = new JTextField("World");
        subPanel2.add(textField2);
        textField3 = new JTextField("World");
        subPanel2.add(textField3);
        textField4 = new JTextField("World");
        subPanel2.add(textField4);

        // add main panel to frame
        add(mainPanel);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            mainPanel.remove(subPanel2);
            mainPanel.add(subPanel1, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
            mainPanel.revalidate();
            mainPanel.repaint();
        } else if (e.getSource() == button2) {
            mainPanel.remove(subPanel1);
            mainPanel.add(subPanel2, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}