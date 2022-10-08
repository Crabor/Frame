package platform.service.ctx.ctxChecker.contexts;



import platform.service.ctx.rules.Rule;

import java.util.*;

public class ContextPool {

    //存储现在有效的context, rule_id to Map<pattern_id to set>
    private final HashMap<String, HashMap<String, Set<Context>>> Pool;

    //ctx to pattern_id: 决定何时启动修复策略
    private final HashMap<Context, Set<String>> activateCtxMap;

    // pattern_id to set
    private final HashMap<String, Set<Context>> DelSets;
    private final HashMap<String, Set<Context>> AddSets;
    private final HashMap<String, Set<Context>> UpdSets;

    public ContextPool() {
        Pool = new HashMap<>();
        DelSets = new HashMap<>();
        AddSets = new HashMap<>();
        UpdSets = new HashMap<>();
        activateCtxMap = new HashMap<>();
    }

    public void PoolInit(Rule rule){
            HashMap<String, Set<Context>> map = new HashMap<>();
            for(String pattern_id : rule.getRelatedPatterns()){
                map.put(pattern_id, new HashSet<>());
            }
            Pool.put(rule.getRule_id(), map);
    }

    public void  ThreeSetsInit(String pattern_id){
        DelSets.put(pattern_id, new HashSet<>());
        AddSets.put(pattern_id, new HashSet<>());
        UpdSets.put(pattern_id, new HashSet<>());
    }

    public Set<Context> GetAddSet(String pattern_id){
        return AddSets.get(pattern_id);
    }

    public Set<Context> GetDelSet(String pattern_id){
        return DelSets.get(pattern_id);
    }

    public Set<Context> GetUpdSet(String pattern_id){
        return UpdSets.get(pattern_id);
    }

    public Set<Context> GetPoolSet(String rule_id, String pattern_id){
        return Pool.get(rule_id).get(pattern_id);
    }

    public HashMap<Context, Set<String>> getActivateCtxMap() {
        return activateCtxMap;
    }

    public int GetAddSetSize(String pattern_id){
        return AddSets.get(pattern_id).size();
    }

    public int GetDelSetSize(String pattern_id){
        return DelSets.get(pattern_id).size();
    }

    public int GetUpdSetSize(String pattern_id){
        return UpdSets.get(pattern_id).size();
    }

    public int GetPoolSetSize(String rule_id, String pattern_id){
        return Pool.get(rule_id).get(pattern_id).size();
    }

    //ECC PCC CON-C
    public void ApplyChange(String rule_id, ContextChange contextChange){
        if(contextChange.getChangeType() == ContextChange.ChangeType.ADDITION){
            Pool.get(rule_id).get(contextChange.getPatternId()).add(contextChange.getContext());

            activateCtxMap.computeIfAbsent(contextChange.getContext(), k -> new HashSet<>());
            Objects.requireNonNull(activateCtxMap.computeIfPresent(contextChange.getContext(), (k, v) -> v)).add(contextChange.getPatternId());
        }
        else{
            Pool.get(rule_id).get(contextChange.getPatternId()).remove(contextChange.getContext());

            assert activateCtxMap.containsKey(contextChange.getContext());
            activateCtxMap.get(contextChange.getContext()).remove(contextChange.getPatternId());
        }
    }

    //CPCC method 2
    public void ApplyChanges(Rule rule, List<ContextChange> batch) {
        //init DelSet, AddSet, and ModSet
        for(String pattern_id : rule.getRelatedPatterns()){
            DelSets.get(pattern_id).clear();
            AddSets.get(pattern_id).clear();
            UpdSets.get(pattern_id).clear();
        }

        //update DelSets, AddSets, and ModSets
        for(ContextChange contextChange : batch){
            String pattern_id = contextChange.getPatternId();
            if(!rule.getRelatedPatterns().contains(pattern_id))
                continue;
            Set<Context> DelSet = DelSets.get(pattern_id);
            Set<Context> AddSet = AddSets.get(pattern_id);
            Set<Context> ModSet = UpdSets.get(pattern_id);
            if(contextChange.getChangeType() == ContextChange.ChangeType.ADDITION){
                Pool.get(rule.getRule_id()).get(pattern_id).add(contextChange.getContext());

                activateCtxMap.computeIfAbsent(contextChange.getContext(), k -> new HashSet<>());
                Objects.requireNonNull(activateCtxMap.computeIfPresent(contextChange.getContext(), (k, v) -> v)).add(contextChange.getPatternId());

                if(DelSet.contains(contextChange.getContext())){
                    DelSet.remove(contextChange.getContext());
                    ModSet.add(contextChange.getContext());
                }
                else{
                    AddSet.add(contextChange.getContext());
                }
            }
            else if(contextChange.getChangeType() == ContextChange.ChangeType.DELETION){
                Pool.get(rule.getRule_id()).get(pattern_id).remove(contextChange.getContext());

                assert activateCtxMap.containsKey(contextChange.getContext());
                activateCtxMap.get(contextChange.getContext()).remove(contextChange.getPatternId());

                if(AddSet.contains(contextChange.getContext())){
                    AddSet.remove(contextChange.getContext());
                }
                else if(ModSet.contains(contextChange.getContext())){
                    ModSet.remove(contextChange.getContext());
                    DelSet.add(contextChange.getContext());
                }
                else{
                    DelSet.add(contextChange.getContext());
                }
            }
            else {
                System.out.println("Error");
                System.exit(1);
            }
        }

    }

    //PCCM CPCC method 1
    public void ApplyChangeWithSets(String rule_id, ContextChange contextChange){
        Set<Context> DelS = DelSets.get(contextChange.getPatternId());
        Set<Context> AddS = AddSets.get(contextChange.getPatternId());
        Set<Context> ModS = UpdSets.get(contextChange.getPatternId());
        if(contextChange.getChangeType() == ContextChange.ChangeType.ADDITION){
            Pool.get(rule_id).get(contextChange.getPatternId()).add(contextChange.getContext());

            activateCtxMap.computeIfAbsent(contextChange.getContext(), k -> new HashSet<>());
            Objects.requireNonNull(activateCtxMap.computeIfPresent(contextChange.getContext(), (k, v) -> v)).add(contextChange.getPatternId());

            if(DelS.contains(contextChange.getContext())){
                DelS.remove(contextChange.getContext());
                ModS.add(contextChange.getContext());
            }else{
                AddS.add(contextChange.getContext());
            }
        }
        else{
            Pool.get(rule_id).get(contextChange.getPatternId()).remove(contextChange.getContext());

            assert activateCtxMap.containsKey(contextChange.getContext());
            activateCtxMap.get(contextChange.getContext()).remove(contextChange.getPatternId());

            if(AddS.contains(contextChange.getContext())){
                AddS.remove(contextChange.getContext());
            }
            else if(ModS.contains(contextChange.getContext())){
                ModS.remove(contextChange.getContext());
                DelS.add(contextChange.getContext());
            }
            else{
                DelS.add(contextChange.getContext());
            }
        }
    }


}
