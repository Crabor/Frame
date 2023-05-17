package ui.action;

import com.alibaba.fastjson.JSONObject;
import database.Database;
import ui.UI;
import ui.component.AbstractComponent;
import ui.component.Table;
import ui.component.Tree;
import ui.struct.ComponentType;
import ui.struct.AttributeType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
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

        //TODO: 完善输出
        logger.info(String.format("[DATABASE_GET]: %s.%s = \"%s\"",component, attributeType, sql));

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
                        String[] columnNames = component.getColumnNames();
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
                } else if (component.getType() == ComponentType.TREE) {
                    DefaultMutableTreeNode root = ((Tree)component).getRoot();
                    root.removeAllChildren();
                    String[] dirs = component.getDirs();
                    DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[dirs.length];
                    for (int i = 0; i < dirs.length; i++) {
                        nodes[i] = new DefaultMutableTreeNode(dirs[i]);
                        root.add(nodes[i]);
                    }
                    try {
                        while (rs.next()) {
                            for (int i = 0; i < dirs.length; i++) {
                                if (rs.getString("dir").equalsIgnoreCase(dirs[i])) {
                                    nodes[i].add(new DefaultMutableTreeNode(rs.getString("file")));
                                }
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    ((Tree)component).getTreeModel().reload();
                    JTree tree = (JTree)(component.getBaseComponent());
                    for (int i = 0; i < tree.getRowCount(); i++) {
                        tree.expandRow(i);
                    }
                }
                break;
                //TODO: 其他类型
        }
        lock.unlock();
    }
}
