package ui.component;

import ui.struct.ComponentType;

import javax.swing.*;

public class Button extends AbstractComponent {
    JButton button;

    public Button(ComponentType type, String id) {
        super(type, id);
        button = new JButton();
        setLinkComponent(button);
        setBaseComponent(button);
    }

    @Override
    public void setText(String text) {
        button.setText(text);
    }

    @Override
    public String getText() {
        return button.getText();
    }
}
