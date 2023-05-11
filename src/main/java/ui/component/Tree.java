package ui.component;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import ui.struct.ComponentType;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class Tree extends AbstractComponent {
    JTree tree;
    DefaultMutableTreeNode root;

    public Tree(ComponentType type, String id) {
        super(type, id);
        root = new DefaultMutableTreeNode("root");
        tree = new JTree();
        tree.setRootVisible(false);
        setBaseComponent(tree);
    }

    @Override
    public void setDirs(String[] dirs) {
        for (String dir : dirs) {
            root.add(new DefaultMutableTreeNode(dir));
        }
        tree.setModel(new DefaultTreeModel(root));
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
}
