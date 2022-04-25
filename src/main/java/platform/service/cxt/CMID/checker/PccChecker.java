package platform.service.cxt.CMID.checker;

import platform.service.cxt.CMID.context.Context;
import platform.service.cxt.CMID.node.CCTNode;
import platform.service.cxt.CMID.node.STNode;
import platform.service.cxt.CMID.pattern.Pattern;
import platform.service.cxt.CMID.util.LinkHelper;
import platform.service.cxt.CMID.util.LogFileHelper;
import platform.service.cxt.CMID.util.RuleInfoHelper;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by njucjc on 2017/10/7.
 */
public class PccChecker extends Checker{

    public PccChecker(String name, STNode stRoot, Map<String, Pattern> patternMap, Map<String, STNode> stMap) {
        super(name, stRoot, patternMap, stMap);
    }

    public  PccChecker(Checker checker) {
        super(checker);
    }

    protected PccChecker() {

    }
    /**
     *
     * @return violated link
     */
    @Override
    public boolean doCheck() {

        checkTimes++;
        List<Context> param = new CopyOnWriteArrayList<>();
        evaluation(cctRoot, param); //PCC计算

        boolean value = true;

        clearCriticalSet();

        if (!cctRoot.getNodeValue()) {
            String [] links = LinkHelper.splitLinks(cctRoot.getLink());
            for (String link : links) {

                addCriticalSet(link);

                if (addIncLink(link)) {
                    LogFileHelper.getLogger().info(getName() + " " + link,false);
                    LogFileHelper.getLogger().info(RuleInfoHelper.translate(getName(), link), false);
                    LogFileHelper.getLogger().info("",false);
                }
            }

            this.maxLinkSize = this.maxLinkSize < links.length ? links.length : this.maxLinkSize;
            value = false;
        }

        return value;
    }

    /**
     * 根据结点状态来判定是否需要重新计算value和link
     * @param cctRoot
     * @param param
     * @return
     */
    @Override
    protected boolean evaluation(CCTNode cctRoot, List<Context> param) {
        if(cctRoot.getNodeStatus() == CCTNode.NC_STATE) { //无需重算就直接返回
            return cctRoot.getNodeValue();
        }
        return super.evaluation(cctRoot, param);
    }

    @Override
    public void sCheck(List<Context> contextList) {
        CCTNode newRoot = new CCTNode(stRoot.getNodeName(), stRoot.getNodeType());
        build(stRoot, newRoot, 3);

        List<Context> param = new ArrayList<>();
        evaluation(newRoot, param);
    }
}