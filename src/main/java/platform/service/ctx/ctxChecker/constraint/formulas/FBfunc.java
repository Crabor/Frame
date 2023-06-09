package platform.service.ctx.ctxChecker.constraint.formulas;

import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.constraint.runtime.LGUtils;
import platform.service.ctx.ctxChecker.constraint.runtime.Link;
import platform.service.ctx.ctxChecker.constraint.runtime.RuntimeNode;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.ctxChecker.middleware.checkers.Checker;
import platform.service.ctx.ctxChecker.middleware.schedulers.Scheduler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FBfunc extends Formula {

    private String var;
    private String func = null;  // Function name
    private HashMap<String, String> params = new HashMap<>();

    //constructor
    public FBfunc(String _func) {
        this.setFormula_type(Formula_Type.BFUNC);
        this.func = _func;
        this.setAffected(false);
    }

    public void addParam(String pos, String var) {
        assert params.get(pos) == null;
        params.put(pos, var);
    }

    // getter and setter
    @Override
    public Formula_Type getFormula_type() {
        return super.getFormula_type();
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public String getFunc() {
        return func;
    }

    @Override
    public void setFormula_type(Formula_Type formula_type) {
        super.setFormula_type(formula_type);
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "FBfunc{" +
                "func=" + func +
                '}';
    }

    @Override
    public void output(int offset) {
        for(int i = 0; i < offset; ++i)
            System.out.print(" ");
        System.out.println("bfunc: " + this.func + "  affected: " + this.isAffected());
        this.params.forEach((k,v) -> {
            for(int i = 0; i < offset+2; ++i)
                System.out.print(" ");
            System.out.println("pos: " + k + " var: " + v);
        });
    }

    @Override
    public Formula FormulaClone() {
        FBfunc fBfunc = new FBfunc(this.func);
        HashMap<String, String> tmpParams = new HashMap<>(this.params);
        fBfunc.setParams(tmpParams);
        return fBfunc;
    }

    //S-condition
    @Override
    public void DeriveIncPlusSet(Set<Map.Entry<ContextChange.ChangeType, String>> incPlusSet) {
        //Do nothing
    }

    @Override
    public void DeriveIncMinusSet(Set<Map.Entry<ContextChange.ChangeType, String>> incMinusSet) {
        //Do nothing
    }

    //C-condition
    @Override
    public boolean EvaluationAndEqualSideEffect(RuntimeNode curNode, Formula originFormula, String var, ContextChange delChange, ContextChange addChange, boolean canConcurrent, Scheduler scheduler) {
        if(var == null){
            return true;
        }
        curNode.getVarEnv().remove(var);
        curNode.getVarEnv().put(var, addChange.getContext());

        boolean newTruth = false;
        newTruth = bfuncCaller(curNode.getVarEnv(), scheduler.getChecker());
        curNode.setOptTruth(curNode.isTruth());
        curNode.setTruth(newTruth);
        return true;
    }

    @Override
    public void sideEffectResolution(RuntimeNode curNode, Formula originFormula, String var, ContextChange delChange, ContextChange addChange, boolean canConcurrent, Scheduler scheduler) {
        if(var != null){
            curNode.setTruth(curNode.isOptTruth());
            curNode.setOptTruth(false);
            curNode.getVarEnv().remove(var);
            curNode.getVarEnv().put(var, delChange.getContext());
        }
    }

    //DIS
    @Override
    public void DeriveRCRESets(boolean from) {

    }

    //PCC
    @Override
    public boolean UpdateAffectedWithOneChange(ContextChange contextChange, Checker checker) {
        return false;
    }

    //PCCM && CPCC
    @Override
    public boolean UpdateAffectedWithChanges(Checker checker) {
        return false;
    }
    @Override
    public void CleanAffected() {
        this.setAffected(false);
    }

    //CPCC_NB
    @Override
    public void UpdateCanConcurrent_CPCC_NB(boolean canConcurrent, Rule rule, Checker checker) {

    }

    @Override
    public void CleanAffectedAndCanConcurrent() {
        this.setAffected(false);
    }

    /*
                                        ECC PCC
                                     */
    @Override
    public void CreateBranches_ECCPCC(String rule_id, RuntimeNode curNode, Formula originFormula, Checker checker) {
        //do nothing
    }

    /*
        ECC
     */
    @Override
    public boolean TruthEvaluation_ECC(RuntimeNode curNode, Formula originFormula, Checker checker) {
        boolean result = false;
        result = bfuncCaller(curNode.getVarEnv(), checker);
        curNode.setTruth(result);
        return result;
    }

    @Override
    public Set<Link> LinksGeneration_ECC(RuntimeNode curNode, Formula originFormula, Checker checker) {
        LGUtils lgUtils = new LGUtils();
        Set<Link> result = new HashSet<>(1);
        curNode.setLinks(result);
        return curNode.getLinks();
    }

    /*
        PCC
     */

    @Override
    public void ModifyBranch_PCC(String rule_id, RuntimeNode curNode, Formula originFormula, ContextChange contextChange, Checker checker) {
        //do nothing
    }

    @Override
    public boolean TruthEvaluation_PCC(RuntimeNode curNode, Formula originFormula, ContextChange contextChange, Checker checker) {
        if(originFormula.isAffected()){
            this.TruthEvaluation_ECC(curNode, originFormula, checker);
            return curNode.isTruth();
        }
        else{
            return curNode.isTruth();
        }
    }

    @Override
    public Set<Link> LinksGeneration_PCC(RuntimeNode curNode, Formula originFormula, ContextChange contextChange, Checker checker) {
        LGUtils lgUtils = new LGUtils();
        Set<Link> result = new HashSet<>(1);
        curNode.setLinks(result);
        return curNode.getLinks();
    }

    /*
        ConC
     */
    @Override
    public void CreateBranches_ConC(String rule_id, RuntimeNode curNode, Formula originFormula, boolean canConcurrent, Checker checker) {
        //do nothing
    }

    @Override
    public boolean TruthEvaluation_ConC(RuntimeNode curNode, Formula originFormula, boolean canConcurrent, Checker checker) {
        boolean result = false;
        result = bfuncCaller(curNode.getVarEnv(), checker);
        curNode.setTruth(result);
        return result;
    }

    @Override
    public Set<Link> LinksGeneration_ConC(RuntimeNode curNode, Formula originFormula, boolean canConcurrent, Checker checker) {
        LGUtils lgUtils = new LGUtils();
        Set<Link> result = new HashSet<>(1);
        curNode.setLinks(result);
        return curNode.getLinks();
    }

    /*
        PCCM
     */

    @Override
    public void ModifyBranch_PCCM(String rule_id, RuntimeNode curNode, Formula originFormula, ContextChange contextChange, Checker checker) {

    }

    @Override
    public boolean TruthEvaluation_PCCM(RuntimeNode curNode, Formula originFormula, Checker checker) {
        if(originFormula.isAffected()){
            this.TruthEvaluation_ECC(curNode, originFormula, checker);
            return curNode.isTruth();
        }
        else{
            return curNode.isTruth();
        }
    }

    @Override
    public Set<Link> LinksGeneration_PCCM(RuntimeNode curNode, Formula originFormula, Checker checker) {
        LGUtils lgUtils = new LGUtils();
        Set<Link> result = new HashSet<>(1);
        curNode.setLinks(result);
        return curNode.getLinks();
    }

    /*
        CPCC_NB
     */

    @Override
    public void CreateBranches_CPCC_NB(Rule rule, RuntimeNode curNode, Formula originFormula, Checker checker) {

    }

    @Override
    public void ModifyBranch_CPCC_NB(Rule rule, RuntimeNode curNode, Formula originFormula, Checker checker) {

    }

    @Override
    public boolean TruthEvaluationCom_CPCC_NB(RuntimeNode curNode, Formula originFormula, Checker checker) {
        boolean result = false;
        result = bfuncCaller(curNode.getVarEnv(), checker);
        curNode.setTruth(result);
        curNode.setVirtualTruth(result ? RuntimeNode.Virtual_Truth_Type.TRUE : RuntimeNode.Virtual_Truth_Type.FALSE);
        return result;
    }

    @Override
    public boolean TruthEvaluationPar_CPCC_NB(RuntimeNode curNode, Formula originFormula, Checker checker) {
        if(originFormula.isAffected()){
            this.TruthEvaluationCom_CPCC_NB(curNode, originFormula, checker);
            curNode.setVirtualTruth(curNode.isTruth() ? RuntimeNode.Virtual_Truth_Type.TRUE : RuntimeNode.Virtual_Truth_Type.FALSE);
            return curNode.isTruth();
        }
        else{
            return curNode.isTruth();
        }
    }

    @Override
    public Set<Link> LinksGeneration_CPCC_NB(RuntimeNode curNode, Formula originFormula, Checker checker) {
        Set<Link> result = new HashSet<>(1);
        curNode.setLinks(result);
        return curNode.getLinks();
    }

    /*
        CPCC_BASE
     */

    @Override
    public void ModifyBranch_BASE(String rule_id, RuntimeNode curNode, Formula originFormula, ContextChange contextChange, Checker checker) {
        //do nothing
    }

    @Override
    public boolean TruthEvaluation_BASE(RuntimeNode curNode, Formula originFormula, ContextChange contextChange, Checker checker) {
        if(originFormula.isAffected()){
            this.TruthEvaluation_ECC(curNode, originFormula, checker);
            return curNode.isTruth();
        }
        else{
            return curNode.isTruth();
        }
    }

    @Override
    public Set<Link> LinksGeneration_BASE(RuntimeNode curNode, Formula originFormula, ContextChange contextChange, Checker checker) {
        LGUtils lgUtils = new LGUtils();
        Set<Link> result = new HashSet<>(1);
        curNode.setLinks(result);
        return curNode.getLinks();
    }




    public boolean bfuncCaller(HashMap<String, Context> varEnv, Checker checker){
        //varEnv: {"v1" : {"ctx_id" : "ctx_1", "fields" : {"value": 123.213}}}
        Map<String, Map<String, String>> vcMap = new HashMap<>();
        for(String pos : params.keySet()){
            HashMap<String, String> ctxInfos = new HashMap<>();
            Context context = varEnv.get(params.get(pos));

            ctxInfos.put("ctx_id", context.getContextId());
            for(String attriName : context.getContextFields().keySet()){
                ctxInfos.put(attriName, context.getContextFields().get(attriName));
            }
            vcMap.put(params.get(pos), ctxInfos);
        }

        boolean result = false;
        try {
            Object bfuncInstance = checker.getBfuncInstance();
            Method m = bfuncInstance.getClass().getMethod("bfunc", String.class, Class.forName("java.util.Map"));
            result = (boolean) m.invoke(bfuncInstance,func, vcMap);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}

