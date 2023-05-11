package ui.component;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import ui.struct.ComponentType;
import ui.struct.FontStyleType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;

public class Table extends AbstractComponent {
    JScrollPane scrollPane;
    JTable table;
    String[] columnNames;
    DefaultTableModel model;

    public Table(ComponentType type, String id) {
        super(type, id);
        table = new JTable();
        scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setBaseComponent(scrollPane);
    }

    @Override
    public void setColumnNames(String[] columnNames) {
        model = new DefaultTableModel(columnNames, 0);
        this.columnNames = columnNames;
        table.setModel(model);
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public DefaultTableModel getModel() {
        return model;
    }
}
