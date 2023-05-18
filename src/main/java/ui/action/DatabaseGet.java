package ui.action;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import database.Database;
import database.struct.QueryResult;
import ui.UI;
import ui.component.AbstractComponent;
import ui.component.Table;
import ui.component.Tree;
import ui.listener.TreeSelected;
import ui.struct.ComponentType;
import ui.struct.AttributeType;
import ui.struct.FontStyleType;
import ui.struct.ScrollType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
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
    public void execute(boolean logFlag) {
        AbstractComponent component;
        AttributeType attributeType;
        String sql;
        ComponentType componentType = ComponentType.fromString(who.eval(action.get("component_type").toString()));
        String componentId = who.eval(action.get("component_id").toString());
        component = UI.getComponent(componentType, componentId);
        attributeType = AttributeType.fromString(who.eval(action.get("component_attribute").toString()));
        sql = who.eval(action.get("sql").toString());

        lock.lock();
        QueryResult qr = Database.Get(sql);
        if (qr == null) {
            return;
        }
        if (logFlag) {
            logger.info(String.format("[ACTION] [DATABASE_GET] [AFTER]: %s.%s = \"%s\"",component, attributeType, sql));
            logger.info(qr);
        }

        switch (attributeType) {
            case SCROLL:
                component.setScroll(ScrollType.fromString(qr.getOneElement(0,0)));
                break;
            case BACKGROUND:
                component.setBackground(Util.parseColor(qr.getOneElement(0,0)));
                break;
            case VISIBLE:
                component.setVisible(Boolean.parseBoolean(qr.getOneElement(0,0)));
                break;
            case TITLE:
                component.setTitle(qr.getOneElement(0,0));
                break;
            case TEXT:
                component.setText(qr.getOneElement(0,0));
                break;
            case COLUMN_WIDTH:
                component.setColumnWidth(Integer.parseInt(qr.getOneElement(0,0)));
                break;
            case ROW_HEIGHT:
                component.setRowHeight(Integer.parseInt(qr.getOneElement(0,0)));
                break;
            case EDITABLE:
                component.setEditable(Boolean.parseBoolean(qr.getOneElement(0,0)));
                break;
            case SIZE:
                String[] size = qr.getOneColumn(0);
                component.setSize(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
                break;
            case FONT:
                String[] font = qr.getOneColumn(0);
                component.setFont(new Font(font[0], FontStyleType.fromString(font[1]).ordinal(), Integer.parseInt(font[2])));
                break;
            case COLUMN_NAMES:
                component.setColumnNames(qr.getOneColumn(0));
                break;
            case DIRS:
                component.setDirs(qr.getOneColumn(0));
                break;
            case USER_VALS:
                component.setUserVals(qr.getOneColumn(0));
                break;
            case CONTENT:
                if (component.getType() == ComponentType.TABLE) {
                    DefaultTableModel model = ((Table)component).getModel();
                    model.setRowCount(0);
                    String[] columnNames = component.getColumnNames();
                    for (int i = 0; i < qr.getRowCount(); i++) {
                        String[] row = new String[columnNames.length];
                        for (int j = 0; j < columnNames.length; j++) {
                            row[j] = qr.getOneElement(i, columnNames[j]);
                        }
                        model.addRow(row);
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
                    for (int i = 0; i < qr.getRowCount(); i++) {
                        String dir = qr.getOneElement(i, "dir");
                        String file = qr.getOneElement(i, "file");
                        for (int j = 0; j < dirs.length; j++) {
                            if (dir.equalsIgnoreCase(dirs[j])) {
                                nodes[j].add(new DefaultMutableTreeNode(file));
                            }
                        }
                    }
                    TreeSelected treeSelectedListener = ((Tree)component).removeTreeSelectedListener();
                    ((Tree)component).getTreeModel().reload();
                    ((Tree)component).setTreeSelectedListener(treeSelectedListener);
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
