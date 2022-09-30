package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import platform.service.ctx.Contexts.Context;
import platform.service.ctx.Contexts.ContextChange;
import platform.service.ctx.Patterns.Pattern;

import java.util.ArrayList;
import java.util.List;

public class ChgGenerator {
    private final AbstractCtxServer server;
    private final 

    public ChgGenerator(AbstractCtxServer server){
        this.server = server;
    }

    public void generateChanges(JSONObject jsonObject){
        /*
            1. buildContexts
            2. patternMatch
            3. generateChanges
            4. writeChanges
         */
    }

    //每一个sensor对应一个context（element）
    private List<Context> buildContexts(JSONObject jsonObject){
        return null;
    }

    //调用pattern中的matcher的match方法
    private boolean match(Pattern pattern, Context context){
        return false;
    }

    private List<ContextChange> generate(Pattern pattern, Context context){
        List<ContextChange> changeList = new ArrayList<>();
        //TODO()
        return changeList;
    }

}
