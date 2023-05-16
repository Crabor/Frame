package ui.component;

import ui.struct.ComponentType;

import javax.swing.*;

public class Label extends AbstractComponent {
    JLabel label;

    public Label(ComponentType type, String id) {
        super(type, id);
        label = new JLabel();
        setLinkComponent(label);
        setBaseComponent(label);
    }

    @Override
    public void setText(String text) {
        label.setText(text);
    }

    @Override
    public String getText() {
        return label.getText();
    }
}
