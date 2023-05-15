package ui;
import org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceMistSilverLookAndFeel;

import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.*;

public class Walkthrough extends JFrame {
    public Walkthrough() {
        super("Sample app");
        this.setLayout(new FlowLayout());
        JButton button = new JButton("button");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    UIManager.setLookAndFeel(new RadianceMistSilverLookAndFeel());
                } catch (UnsupportedLookAndFeelException ex) {
                    throw new RuntimeException(ex);
                }
                //repaint
                SwingUtilities.updateComponentTreeUI(Walkthrough.this);
            }
        });
        this.add(button);
        this.add(new JCheckBox("check"));
        this.add(new JLabel("label"));

        this.setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR));
        this.setSize(new Dimension(250, 80));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new RadianceGraphiteLookAndFeel());
            } catch (Exception e) {
                System.out.println("Radiance Graphite failed to initialize");
            }
            Walkthrough w = new Walkthrough();
            w.setVisible(true);
        });
    }
}