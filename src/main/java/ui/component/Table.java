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
    DefaultTableModel model;

    public Table(ComponentType type, String id) {
        super(type, id);
        table = new JTable();
        scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(Color.WHITE);
        setBaseComponent(scrollPane);
    }

    @Override
    public void setProperty(JSONObject jo) {
        //background
        Color color = Color.WHITE;
        try {
            color = Util.parseColor(jo.getString("background"));
            logger.info(String.format("<%s,%s>.setBackground(%s)", type, id, jo.getString("background")));
        } catch (Exception ignored) {}
        table.setBackground(color);

        //font
        try {
            String[] fontSetting = Util.jsonArrayToStringArray(jo.getJSONArray("font"));
            String fontName = fontSetting[0];
            FontStyleType fontStyle = FontStyleType.fromString(fontSetting[1]);
            int fontSize = Integer.parseInt(fontSetting[2]);
            table.setFont(new Font(fontName, fontStyle.ordinal(), fontSize));
            logger.info(String.format("<%s,%s>.setFont(%s)", type, id, jo.getString("font")));
        } catch (Exception ignored) {}

        //columns
        try {
            String[] columnNames = Util.jsonArrayToStringArray(jo.getJSONArray("columns"));
//            //random rawdata
//            String[][] rawData = new String[10][columnNames.length];
//            for (int i = 0; i < 10; i++) {
//                for (int j = 0; j < columnNames.length; j++) {
//                    rawData[i][j] = String.format("%d,%d", i, j);
//                }
//            }
            model = new DefaultTableModel(columnNames, 0);
            table.setModel(model);
            logger.info(String.format("<%s,%s>.setColumnNames(%s)", type, id, jo.getString("columns")));
        } catch (Exception ignored) {}
    }
}
