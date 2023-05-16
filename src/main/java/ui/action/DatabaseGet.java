package ui.action;

import com.alibaba.fastjson.JSONObject;
import database.Database;
import ui.UI;
import ui.component.AbstractComponent;
import ui.component.Table;
import ui.struct.ComponentType;
import ui.struct.AttributeType;

import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseGet extends AbstractAction {
    Lock lock = new ReentrantLock();

    public DatabaseGet(AbstractComponent who, JSONObject action) {
        super(who, action);
    }

    @Override
    public void execute() {
        AbstractComponent component;
        AttributeType attributeType;
        String sql;
        ComponentType componentType = ComponentType.fromString(who.eval(action.get("component_type").toString()));
        String componentId = who.eval(action.get("component_id").toString());
        component = UI.getComponent(componentType, componentId);
        attributeType = AttributeType.fromString(who.eval(action.get("component_attribute").toString()));
        sql = who.eval(action.get("sql").toString());

        lock.lock();
        ResultSet rs = Database.Get(sql);
        if (rs == null) {
            return;
        }
        switch (attributeType) {
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
