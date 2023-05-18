package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MyFrame extends JFrame {
    private JTable table;

    public MyFrame() {
        super("My Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Create a table with some data
        String[] columnNames = {"Name", "Age", "Gender"};
        Object[][] data = {
                {"John", 25, "Male"},
                {"Jane", 30, "Female"},
                {"Bob", 40, "Male"},
                {"Alice", 35, "Female"}
        };
        table = new JTable(data, columnNames);

        // Wrap the table in a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        // Add the scroll pane to a panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add the panel to the frame
        getContentPane().add(panel);

        // Add a component listener to the frame to listen for size changes
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Dimension newSize = e.getComponent().getSize();
                table.setPreferredSize(newSize);
                scrollPane.setPreferredSize(newSize);
            }
        });
    }

    public static void main(String[] args) {
        MyFrame frame = new MyFrame();
        frame.setVisible(true);
    }
}