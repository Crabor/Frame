package ui.component;

import ui.struct.ComponentType;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

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

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }
}
