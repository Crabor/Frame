package ui.component;

import ui.struct.ComponentType;
import ui.struct.ScrollType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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
        setLinkComponent(scrollPane);
        setBaseComponent(table);
    }

    @Override
    public void setColumnNames(String[] columnNames) {
        model = new DefaultTableModel(columnNames, 0);
        this.columnNames = columnNames;
        table.setModel(model);
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    @Override
    public void setScroll(ScrollType type) {
        if (type == ScrollType.VERTICAL) {
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        } else if (type == ScrollType.HORIZONTAL) {
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        } else if (type == ScrollType.BOTH) {
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        } else if (type == ScrollType.NONE) {
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }
    }

    @Override
    public String getScroll() {
        String ret;
        boolean horizontal = scrollPane.getHorizontalScrollBarPolicy() != ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
        boolean vertical = scrollPane.getVerticalScrollBarPolicy() != ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
        if (horizontal && vertical) {
            ret = "both";
        } else if (horizontal) {
            ret = "horizontal";
        } else if (vertical) {
            ret = "vertical";
        } else {
            ret = "none";
        }

        return ret;
    }

    public DefaultTableModel getModel() {
        return model;
    }
}
