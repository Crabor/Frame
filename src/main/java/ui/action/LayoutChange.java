package ui.action;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import ui.UI;
import ui.component.AbstractComponent;
import ui.component.AbstractLayout;
import ui.struct.AlignType;
import ui.struct.ComponentType;

public class LayoutChange implements Action {
    AbstractLayout layout;
    AbstractComponent component;
    int[] position;
    AlignType align = AlignType.CENTER;

    public LayoutChange(JSONObject action) {
        ComponentType layoutType = ComponentType.fromString(action.getString("layout_type"));
        String layoutId = action.getString("layout_id");
        layout = (AbstractLayout) UI.getComponent(layoutType, layoutId);
        ComponentType componentType = ComponentType.fromString(action.getString("component_type"));
        String componentId = action.getString("component_id");
        component = UI.getComponent(componentType, componentId);
        position = Util.jsonArrayToIntArray(action.getJSONArray("position"));
        try {
            align = AlignType.fromString(action.getString("align"));
        } catch (Exception ignored) {}
    }

    @Override
    public void execute() {
        //TODO: 原组件被剔除，新组件加入
        layout.setComponent(component, position[0], position[1], position[2], position[3], align, true);
        layout.repaint();
    }
}
