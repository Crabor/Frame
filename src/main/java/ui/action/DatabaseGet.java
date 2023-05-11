package ui.action;

import com.alibaba.fastjson.JSONObject;
import database.Database;
import ui.UI;
import ui.component.AbstractComponent;
import ui.component.Table;
import ui.struct.ComponentType;
import ui.struct.PropertyType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseGet implements Action {
    AbstractComponent component;
    PropertyType propertyType;
    String sql;
    Lock lock = new ReentrantLock();

    public DatabaseGet(JSONObject action) {
        ComponentType componentType = ComponentType.fromString(action.getString("component_type"));
        String componentId = action.getString("component_id");
        component = UI.getComponent(componentType, componentId);
        propertyType = PropertyType.fromString(action.getString("property"));
        sql = action.getString("sql");
    }

    @Override
    public void execute() {
        lock.lock();
        ResultSet rs = Database.Get(sql);
        if (rs == null) {
            return;
        }
        switch (propertyType) {
            case CONTENT:
                if (component.getType() == ComponentType.TABLE) {
                    try {
                        DefaultTableModel model = ((Table)component).getModel();
                        model.setRowCount(0);
                        String[] columnNames = ((Table)component).getColumnNames();
                        while (rs.next()) {
                            Object[] row = new Object[columnNames.length];
                            for (int i = 0; i < columnNames.length; i++) {
                                row[i] = rs.getString(columnNames[i]);
                            }
                            model.addRow(row);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
                //TODO: 其他类型
        }
        lock.unlock();
    }
}
