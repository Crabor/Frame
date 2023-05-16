package ui.action;

import com.alibaba.fastjson.JSONObject;
import database.Database;
import ui.component.AbstractComponent;

public class DatabaseSet extends AbstractAction {
    public DatabaseSet(AbstractComponent who, JSONObject action) {
        super(who, action);
    }

    @Override
    public void execute() {
        String sql = who.eval(action.get("sql").toString());
        Database.Set(sql);
    }
}
