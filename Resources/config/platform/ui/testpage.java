import javax.swing.JFrame;

public class testpage extends JFrame {

    public testpage() {
        setTitle("Example Frame");
        setSize(400, 300);
        setLayout(new FlowLayout());

        JTextField textField1 = new JTextField();
        textField1.setBounds(50, 50, 200, 30);
        textField1.setText("Placeholder Text");
        add(textField1);

        JButton button1 = new JButton();
        button1.setBounds(100, 100, 100, 50);
        button1.setText("Click Me!");
        add(button1);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}