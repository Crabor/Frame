package ui.component;

import ui.struct.ComponentType;

import javax.swing.*;

public class TextField extends AbstractComponent {
    JTextArea textArea;

    public TextField(ComponentType type, String id) {
        super(type, id);
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        setLinkComponent(textArea);
        setBaseComponent(textArea);
    }

    @Override
    public void setText(String text) {
        textArea.setText(text);
    }

    @Override
    public String getText() {
        return textArea.getText();
    }

    @Override
    public void setColumnWidth(int columnWidth) {
        textArea.setColumns(columnWidth);
    }

    @Override
    public String getColumnWidth() {
        return String.valueOf(textArea.getColumns());
    }

    @Override
    public void setRowHeight(int rowHeight) {
        textArea.setRows(rowHeight);
    }

    @Override
    public String getRowHeight() {
        return String.valueOf(textArea.getRows());
    }

    @Override
    public void setEditable(boolean editable) {
        textArea.setEditable(editable);
    }

    @Override
    public String getEditable() {
        return String.valueOf(textArea.isEditable());
    }
}
