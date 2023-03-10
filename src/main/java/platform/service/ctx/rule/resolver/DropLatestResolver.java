package platform.service.ctx.rule.resolver;

import java.util.*;

public class DropLatestResolver extends AbstractResolver{

    public DropLatestResolver() {
        this.resolverStrategy = ResolverStrategy.DROP_LATEST;
    }

    @Override
    public Set<Map.Entry<String, HashMap<String, String>>> resolve(Set<HashMap<String, Map.Entry<String, HashMap<String, String>>>> linkSet) {
        Set<Map.Entry<String, HashMap<String, String>>> flatContextSet = new HashSet<>();

        String latestCtxId = null;
        for(HashMap<String, Map.Entry<String, HashMap<String, String>>> link : linkSet){
            for(Map.Entry<String, HashMap<String, String>> ctx : link.values()){
                String ctxId = ctx.getKey();
                if(latestCtxId == null){
                    latestCtxId = ctxId;
                }
                else{
                    int latestIndex = Integer.parseInt(latestCtxId.split("_")[1]);
                    int index = Integer.parseInt(ctxId.split("_")[1]);
                    if(index > latestIndex){
                        latestCtxId = ctxId;
                    }
                }
            }
        }
        flatContextSet.add(new AbstractMap.SimpleEntry<>(latestCtxId, null));

        return flatContextSet;
    }

}
