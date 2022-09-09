package platform.service.ctx.ctxChecker.INFuse.Contexts;

import platform.service.ctx.ctxChecker.INFuse.Patterns.PatternHandler;

import java.text.SimpleDateFormat;
import java.util.List;

public class PlatContextHandler extends ContextHandler{

    public PlatContextHandler(PatternHandler patternHandler) {
        super(patternHandler);
    }

    @Override
    public void generateChanges(String line, List<ContextChange> changeList) throws Exception {
        //convert string to context changes
        //+,11,pat_right,5.264847945235763,2022-04-20 05:51:45
        Context context = buildContext(line);
        ContextChange contextChange = new ContextChange();
        String[] parts = line.split(",");
        if(parts[0].equalsIgnoreCase("+")){
            contextChange.setChange_type(ContextChange.Change_Type.ADDITION);
        }
        else{
            contextChange.setChange_type(ContextChange.Change_Type.DELETION);
        }
        contextChange.setPattern_id(parts[2]);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        contextChange.setTimeStamp(simpleDateFormat.parse(parts[4]).getTime());
        contextChange.setContext(context);
        changeList.add(contextChange);
    }

    //+,11,pat_right,5.264847945235763,2022-04-20 05:51:45
    public Context buildContext(String line){
        Context retCtx = null;
        if(line != null && !line.equalsIgnoreCase("")){
            String[] parts = line.split(",");
            retCtx = new Context();
            retCtx.setCtx_id("ctx_" + parts[1]);
            retCtx.getCtx_fields().put("value", parts[3]);
        }
        return retCtx;
    }
}
