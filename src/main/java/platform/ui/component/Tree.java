package platform.ui.component;

import com.alibaba.fastjson.JSONObject;
import platform.ui.struct.ComponentType;

import javax.swing.*;
import java.awt.*;

public class Tree extends AbstractComponent {
    JTree tree;
    public Tree(ComponentType type, String id) {
        super(type, id);
        tree = new JTree();
        tree.setRootVisible(false);
        setBaseComponent(tree);
    }

    @Override
    public void setProperty(JSONObject jo) {

    }
}
