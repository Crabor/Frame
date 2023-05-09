package ui.component;

import com.alibaba.fastjson.JSONObject;
import ui.struct.ComponentType;

public class Image extends AbstractComponent {


    protected Image(ComponentType type, String id) {
        super(type, id);
    }

    @Override
    public void setProperty(JSONObject jo) {

    }
}
