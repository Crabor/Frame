package ui.component;

import ui.listener.TreeSelected;
import ui.struct.ComponentType;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.ArrayList;

public class Tree extends AbstractComponent {
    JTree tree;
    DefaultMutableTreeNode root;
    DefaultTreeModel treeModel;
    String[] dirs;
    TreeSelected treeSelectedListener;

    public Tree(ComponentType type, String id) {
        super(type, id);
        root = new DefaultMutableTreeNode("root");
        tree = new JTree();
        treeModel = new DefaultTreeModel(root);
        tree.setModel(treeModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.expandPath(new TreePath(root.getPath()));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
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
        return tree.getLastSelectedPathComponent().toString();
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
        //TODO:添加五个空格
        for (int i = 0; i < 5; i++) {
            list.add("");
        }
        return list.toArray(new String[0]);
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public void setTreeSelectedListener(TreeSelected treeSelectedListener) {
        this.treeSelectedListener = treeSelectedListener;
        tree.addTreeSelectionListener(treeSelectedListener);
    }

    public TreeSelected removeTreeSelectedListener() {
        tree.removeTreeSelectionListener(treeSelectedListener);
        return treeSelectedListener;
    }
}
