package platform.service.ctx.statistics;

import platform.service.ctx.ctxChecker.constraint.runtime.Link;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerStatistics {

    static class MsgStatistics{
        private long receivedNum;
        private long checkedAndResolvedNum;
        private long sentNum;

        public void increaseReceivedNumber(){
            receivedNum++;
        }

        public void increaseCheckedAndResolvedNum(){
            checkedAndResolvedNum++;
        }

        public void increaseSentNumber(){
            sentNum++;
        }

        public String toJsonString(){
            return "{\"receivedNum\": " + receivedNum + ", \"checkedAndResolvedNum\": " + checkedAndResolvedNum + ", \"sentNum\": " + sentNum + "}";
        }
    }

    static class RuleStatistics{
        private final Map<String, Set<Link>> rule2LinksMap;

        public RuleStatistics() {
            this.rule2LinksMap = new HashMap<>();
        }

        public void addLinks(String ruleId, Set<Link> linkSet){
            rule2LinksMap.computeIfAbsent(ruleId, k -> new HashSet<>());
            rule2LinksMap.get(ruleId).addAll(linkSet);
        }

        public String toJsonString(){
            StringBuilder stringBuilder = new StringBuilder("{");
            for(String ruleId : rule2LinksMap.keySet()){
                stringBuilder.append("\"").append(ruleId).append("\": ").append(rule2LinksMap.get(ruleId).size()).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("}");
            return stringBuilder.toString();
        }

    }

    static class PatternStatistics {
        static class PatStatistics{
            private long receivedCtxNum;
            private long checkedCtxNum;
            private long problematicCtxNum;

            public void increaseReceivedCtxNum(){
                receivedCtxNum++;
            }

            public void increaseCheckedCtxNum(){
                checkedCtxNum++;
            }

            public void increaseProblematicCtxNum(){ problematicCtxNum++; }

            public String toJsonString(){
                return "{\"receivedCtxNum\": " + receivedCtxNum + ", \"checkedCtxNum\": " + checkedCtxNum +
                        ", \"problematicCtxNum\": " + problematicCtxNum + ", \"cleanNum\": " + (checkedCtxNum - problematicCtxNum) + "}";
            }
        }
        private final Map<String, PatStatistics> patStatisticsMap;

        public PatternStatistics() {
            this.patStatisticsMap = new HashMap<>();
        }

        public void increaseReceivedCtxNum(String patternId){
            patStatisticsMap.computeIfAbsent(patternId, k -> new PatStatistics());
            patStatisticsMap.get(patternId).increaseReceivedCtxNum();
        }

        public void increaseCheckedCtxNum(String patternId){
            patStatisticsMap.computeIfAbsent(patternId, k -> new PatStatistics());
            patStatisticsMap.get(patternId).increaseCheckedCtxNum();
        }

        public void increaseProblematicCtxNum(String patternId){
            patStatisticsMap.computeIfAbsent(patternId, k -> new PatStatistics());
            patStatisticsMap.get(patternId).increaseProblematicCtxNum();
        }

        public String toJsonString(){
            StringBuilder stringBuilder = new StringBuilder("{");
            for(String patternId : patStatisticsMap.keySet()){
                stringBuilder.append("\"").append(patternId).append("\": ").append(patStatisticsMap.get(patternId).toJsonString()).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("}");
            return stringBuilder.toString();
        }
    }

    private final MsgStatistics msgStatistics;
    private final RuleStatistics ruleStatistics;
    private final PatternStatistics patternStatistics;

    public ServerStatistics() {
        msgStatistics = new MsgStatistics();
        ruleStatistics = new RuleStatistics();
        patternStatistics = new PatternStatistics();
    }

    public void increaseReceivedMsgNum(){
        msgStatistics.increaseReceivedNumber();
    }

    public void increaseCheckedAndResolvedMsgNum(){
        msgStatistics.increaseCheckedAndResolvedNum();
    }

    public void increaseSentMsgNum(){
        msgStatistics.increaseSentNumber();
    }

    public void addLinks(String ruleId, Set<Link> linkSet){
        ruleStatistics.addLinks(ruleId, linkSet);
    }

    public void increaseReceivedCtxNum(String patternId){
        patternStatistics.increaseReceivedCtxNum(patternId);
    }

    public void increaseCheckedCtxNum(String patternId){
        patternStatistics.increaseCheckedCtxNum(patternId);
    }

    public void increaseProblematicCtxNum(String patternId){
        patternStatistics.increaseProblematicCtxNum(patternId);
    }

    public String toJsonString(String serverName){
        return "{\"" + serverName + "\": {" +
                "\"msgStatistics\": " + msgStatistics.toJsonString() + ", " +
                "\"ruleStatistics\": " + ruleStatistics.toJsonString() + ", " +
                "\"patternStatistics\": " + patternStatistics.toJsonString() + "} }";
    }

}
