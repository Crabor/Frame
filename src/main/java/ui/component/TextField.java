package ui.component;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import ui.struct.ComponentType;
import ui.struct.FontStyleType;

import javax.swing.*;
import java.awt.*;

public class TextField extends AbstractComponent {
    JTextArea textArea;

    public TextField(ComponentType type, String id) {
        super(type, id);
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        setBaseComponent(textArea);
    }

    @Override
    public void setText(String text) {
        textArea.setText(text);
    }

    @Override
    public void setColumnWidth(int columnWidth) {
        textArea.setColumns(columnWidth);
    }

    @Override
    public void setRowHeight(int rowHeight) {
        textArea.setRows(rowHeight);
    }

    @Override
    public void setEditable(boolean editable) {
        textArea.setEditable(editable);
    }
}
