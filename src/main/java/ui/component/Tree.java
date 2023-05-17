package ui.component;

import ui.struct.ComponentType;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

public class Tree extends AbstractComponent {
    JTree tree;
    DefaultMutableTreeNode root;
    DefaultTreeModel treeModel;
    String[] dirs;

    public Tree(ComponentType type, String id) {
        super(type, id);
        root = new DefaultMutableTreeNode("root");
        tree = new JTree();
        treeModel = new DefaultTreeModel(root);
        tree.setModel(treeModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.expandPath(new TreePath(root.getPath()));
        setLinkComponent(tree);
        setBaseComponent(tree);
    }

    @Override
    public void setDirs(String[] dirs) {
        this.dirs = dirs;
        for (String dir : dirs) {
            root.add(new DefaultMutableTreeNode(dir));
        }
        treeModel.reload();
    }

    @Override
    public String[] getDirs() {
        return dirs;
    }

    @Override
    public String getSelectedItem() {
        return ((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject().toString();
    }

    @Override
    public String[] getSelectedPath() {
        TreePath treePath = tree.getSelectionPath();
        if (treePath == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
        //把treePath中的除了根节点各个节点添加到list中
        for (int i = 1; i < treePath.getPathCount(); i++) {
            list.add(treePath.getPathComponent(i).toString());
        }
        return list.toArray(new String[0]);
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }
}
