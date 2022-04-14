package platform.service.cxt.CMID.checker;

import platform.service.cxt.CMID.context.Context;
import platform.service.cxt.CMID.node.CCTNode;
import platform.service.cxt.CMID.node.STNode;
import platform.service.cxt.CMID.pattern.Pattern;
import platform.service.cxt.CMID.util.LinkHelper;
import platform.service.cxt.CMID.util.LogFileHelper;
import platform.service.cxt.CMID.util.RuleInfoHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by njucjc on 2017/10/7.
 */
public class EccChecker extends Checker{

    public EccChecker(String name, STNode stRoot, Map<String, Pattern> patternMap, Map<String, STNode> stMap) {
        super(name, stRoot, patternMap, stMap);
    }

    public EccChecker(Checker checker) {
        super(checker);
    }

    protected EccChecker() {}
    /**
     *
     * @return violated link
     */
    @Override
    public boolean doCheck() {

        checkTimes++;
        clearCCTMap();
        removeCriticalNode(stRoot, cctRoot);
        cctRoot = new CCTNode(stRoot.getNodeName(), stRoot.getNodeType());
        buildCCT(stRoot, cctRoot);
        List<Context> param = new CopyOnWriteArrayList<>();
        evaluation(cctRoot, param);

        boolean value = true;

        clearCriticalSet();

        if (!cctRoot.getNodeValue()) {
            String [] links = LinkHelper.splitLinks(cctRoot.getLink());
            for (String link : links) {

                addCriticalSet(link);

                if (addIncLink(link)) {
                    LogFileHelper.getLogger().info(getName() + " " + link, false);
                    LogFileHelper.getLogger().info(RuleInfoHelper.translate(getName(), link), false);
                    LogFileHelper.getLogger().info("",false);
                }
            }

            this.maxLinkSize = this.maxLinkSize > links.length ? this.maxLinkSize : links.length;
            value = false;
        }

        return value;

    }

}
