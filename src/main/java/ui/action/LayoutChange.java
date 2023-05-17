package ui.action;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import ui.UI;
import ui.component.AbstractComponent;
import ui.component.AbstractLayout;
import ui.struct.AlignType;
import ui.struct.ComponentType;

import java.util.Arrays;

public class LayoutChange extends AbstractAction {
    public LayoutChange(AbstractComponent who, JSONObject action) {
        super(who, action);
    }

    @Override
    public void execute() {
        AbstractLayout layout;
        AbstractComponent component;
        int[] position;
        AlignType align = AlignType.CENTER;
        ComponentType layoutType = ComponentType.fromString(who.eval(action.get("layout_type").toString()));
        String layoutId = who.eval(action.get("layout_id").toString());
        layout = (AbstractLayout) UI.getComponent(layoutType, layoutId);
        ComponentType componentType =
                ComponentType.fromString(who.eval(action.get("component_type").toString()));
        String componentId = who.eval(action.get("component_id").toString());
        component = UI.getComponent(componentType, componentId);
        position = Util.stringArrayToIntArray(who.eval(Util.jsonArrayToStringArray(action.getJSONArray("position"))));
        try {
            align = AlignType.fromString(who.eval(action.get("align").toString()));
        } catch (Exception ignored) {}

        //TODO: 原组件被剔除，新组件加入
        layout.setComponent(component, position[0], position[1], position[2], position[3], align, false);
        layout.repaint();
        logger.info(String.format("[LAYOUT_CHANGE]: %s.setComponentPosition(%s, %d, %d, %d, %d, %s)", layout, component,
                position[0], position[1], position[2], position[3], align));
    }
}
