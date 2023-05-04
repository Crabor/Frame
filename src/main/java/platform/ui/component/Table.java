package platform.ui.component;

import com.alibaba.fastjson.JSONObject;
import platform.ui.struct.ComponentType;

import javax.swing.*;
import java.awt.*;

public class Table extends AbstractComponent {
    JTable table;

    public Table(ComponentType type, String id) {
        super(type, id);
        table = new JTable();
        setBaseComponent(table);
    }

    @Override
    public void setProperty(JSONObject jo) {

    }
}
