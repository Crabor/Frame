package platform.service.cxt.CMID.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.config.CtxServerConfig;
import platform.service.cxt.CMID.change.ChangeHandler;
import platform.service.cxt.CMID.change.ChangebasedChangeHandler;
import platform.service.cxt.CMID.checker.*;
import platform.service.cxt.CMID.node.STNode;
import platform.service.cxt.CMID.pattern.Pattern;
import platform.service.cxt.CMID.scheduler.BatchScheduler;
import platform.service.cxt.CMID.scheduler.GEASOptScheduler;
import platform.service.cxt.CMID.scheduler.GEAScheduler;
import platform.service.cxt.CMID.scheduler.Scheduler;

import platform.service.cxt.CMID.util.Accuracy;
import platform.service.cxt.CMID.util.LogFileHelper;

import platform.config.CMIDConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import platform.service.cxt.CMID.checker.Checker;
import platform.service.cxt.CMID.checker.CheckerType;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class AbstractCheckerBuilder implements CheckerType {
    private static final Log logger = LogFactory.getLog(AbstractCheckerBuilder.class);

    protected List<Checker> checkerList;

    protected List<Pattern> patternList;

    /*调度checker的策略*/
    protected Scheduler scheduler;

    protected String dataFilePath; //context data

    /*所有pattern*/
    protected Map<String, Pattern> patternMap;

    protected Map<String, Checker> checkerMap;

    private int checkType = ECC_TYPE;

    private int scheduleType;


    private int taskNum = 8;

    private ExecutorService checkExecutorService;

    protected ChangeHandler changeHandler;

    protected String changeHandlerType;


    private String oracleFilePath;

    protected int port;

    public AbstractCheckerBuilder(CtxServerConfig config) {
        CMIDConfig cmidConfig = config.getCMIDConfig();
        //check type
        String technique = cmidConfig.getCtxChecker();
        if (technique == null) {
            logger.info("配置文件解析失败：缺少technique配置项");
            System.exit(1);
        } else if ("pcc".equals(technique.toLowerCase())) {
            this.checkType = PCC_TYPE;
        } else if ("ecc".equals(technique.toLowerCase())) {
            this.checkType = ECC_TYPE;
        } else if ("con-c".equals(technique.toLowerCase())) {
            this.checkType = CON_TYPE;
        } else if ("cpcc".equals(technique.toLowerCase())) {
            this.checkType = CONPCC_TYPE;
        } else {
            logger.info("配置文件解析失败：technique配置项配置值" + technique + "无效");
            System.exit(1);
        }

        this.checkExecutorService = Executors.newFixedThreadPool(taskNum);


        //pattern
        String patternFilePath = cmidConfig.getPatternFilePath();

        if (patternFilePath == null) {
            logger.info("配置文件解析失败：缺少patternFilePath配置项");
            System.exit(1);
        } else if (!isFileExists(patternFilePath)) {
            logger.info("配置文件解析失败：Pattern文件" + patternFilePath + "不存在");
            System.exit(1);
        }
        parsePatternFile(patternFilePath);

        //rule
        String ruleFilePath = cmidConfig.getRuleFilePath();
        if (ruleFilePath == null) {
            logger.info("配置文件解析失败：缺少ruleFilePath配置项");
            System.exit(1);
        } else if (!isFileExists(ruleFilePath)) {
            logger.info("配置文件解析失败：Rule文件" + ruleFilePath + "不存在");
            System.exit(1);
        }
        parseRuleFile(ruleFilePath);

        //log
        String logFilePath = cmidConfig.getLogFilePath();
        if (logFilePath == null) {
            logger.info("配置文件解析失败：缺少logFilePath配置项");
            System.exit(1);
        }
        LogFileHelper.initLogger(logFilePath);

        //oracle
        this.oracleFilePath = cmidConfig.getOracleFilePath();


        //schedule
        String schedule = cmidConfig.getCtxScheduler();

        //change handler
        this.changeHandlerType = cmidConfig.getChangeHandlerType();
        if (this.changeHandlerType == null) {
            logger.info("配置文件解析失败：缺少changeHandlerType配置项");
            System.exit(1);
        } else if (!changeHandlerType.equals("static-change-based") && !changeHandlerType.equals("dynamic-change-based")) {
            logger.info("配置文件解析失败：changeHandlerType配置项配置值" + this.changeHandlerType + "无效");
            System.exit(1);
        }

        if (schedule == null) {
            logger.info("配置文件解析失败：缺少schedule配置项");
            System.exit(1);
        } else if ("immed".equals(schedule.toLowerCase()) || "imd".equals(schedule.toLowerCase())) {
            this.scheduler = new BatchScheduler(1);
            this.scheduleType = 1;
        } else if (schedule.toLowerCase().matches("batch-[0-9]+")) {
            this.scheduler = new BatchScheduler(Integer.parseInt(schedule.split("-")[1]));
            this.scheduleType = Integer.parseInt(schedule.split("-")[1]);
        } else if ("geas-ori".equals(schedule.toLowerCase())) {
            this.scheduler = new GEAScheduler(this.checkerList);
            this.scheduleType = 0;
        } else if ("geas-opt".equals(schedule.toLowerCase())) {
            this.scheduler = new GEASOptScheduler(this.checkerList);
            this.scheduleType = -2;
        } else {
            this.scheduleType = -1;
            logger.info("配置文件解析失败：schedule配置项配置值" + schedule + "无效");
            System.exit(1);
        }


        logger.info("检测技术：" + technique);
        logger.info("调度策略：" + schedule);

        //change handle
        configChangeHandler();

        /*
        if (changeHandlerType.contains("static")) {
            //context file path
            this.dataFilePath = cmidConfig.getDataFile();

            if (this.dataFilePath == null) {
                logger.info("配置文件解析失败：缺少dataFilePath配置项");
                System.exit(1);
            } else if (!isFileExists(this.dataFilePath)) {
                logger.info("数据文件解析失败：数据文件" + this.dataFilePath + "不存在");
                System.exit(1);

            }
        }*/

        logger.info("配置文件解析成功");
    }




    private boolean isFileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    private void configChangeHandler() {
        if(this.changeHandlerType.contains("change-based")) {
            this.changeHandler = new ChangebasedChangeHandler(patternMap, checkerMap, scheduler, checkerList);
        }
    }

    private void parsePatternFile(String patternFilePath) {
        this.patternMap = new ConcurrentHashMap<>();
        this.patternList = new CopyOnWriteArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(patternFilePath);

            NodeList patterns = document.getElementsByTagName("pattern");
            logger.info("Pattern文件为" + patternFilePath + "，总共" + patterns.getLength() + "个patterns");
            for (int i = 0; i < patterns.getLength(); i++) {
                Node patNode = patterns.item(i);
                NodeList childNodes = patNode.getChildNodes();

                Map<String, Boolean> member = new HashMap<>();

                String id = null;
                member.put("id", false);

                long freshness = 0L;
                member.put("freshness", false);

                String category = null;
                member.put("category", false);

                String subject = null;
                member.put("subject", false);

                String predicate = null;
                member.put("predicate", false);

                String object = null;
                member.put("object", false);

                String site = null;
                member.put("site", false);

                for(int j = 0; j < childNodes.getLength(); j++) {
                    if (childNodes.item(j).getNodeName().startsWith("#")) {
                        continue;
                    }
                    switch (childNodes.item(j).getNodeName()) {
                        case "id":
                            member.put("id", true);
                            id = childNodes.item(j).getTextContent();
                            break;
                        case "freshness":
                            try {
                                member.put("freshness", true);
                                freshness = Long.parseLong(childNodes.item(j).getTextContent());
                            } catch (NumberFormatException e) {
                                logger.info("配置文件解析失败：Pattern文件中的freshness配置值" + childNodes.item(j).getTextContent() + "无效");
                                System.exit(1);
                            }
                            break;
                        case "category":
                            member.put("category", true);
                            category = childNodes.item(j).getTextContent();
                            break;
                        case "subject":
                            member.put("subject", true);
                            subject = childNodes.item(j).getTextContent();
                            break;
                        case "predicate":
                            member.put("predicate", true);
                            predicate = childNodes.item(j).getTextContent();
                            break;
                        case "object":
                            member.put("object", true);
                            object = childNodes.item(j).getTextContent();
                            break;
                        case "site":
                            member.put("site", true);
                            site = childNodes.item(j).getTextContent();
                            break;
                        default:
                            logger.info("配置文件解析失败：Pattern文件" + patternFilePath + "存在非法的pattern标识符" + childNodes.item(j).getNodeName());
                            System.exit(1);
                    }
                }

                for(String key : member.keySet()) {
                    if (!member.get(key)) {
                        logger.info("配置文件解析失败：Pattern文件" + patternFilePath + "缺少pattern标识符" + key);
                        System.exit(1);
                    }
                }
                Pattern pattern = new Pattern(id, freshness, category, subject, predicate, object, site);
                patternList.add(pattern);
                patternMap.put(id, pattern);
            }

            /* for(String key : patternMap.keySet()) {
                System.out.println("[DEBUG] " + patternMap.get(key));
            } */

        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            logger.info("配置文件解析失败：Pattern文件" + patternFilePath + "不存在");
            System.exit(1);
        }

        if (patternMap.isEmpty()) {
            logger.info(" 配置文件解析失败：Pattern文件" + patternFilePath + "没有pattern");
            System.exit(1);
        }
    }

    private void parseRuleFile(String ruleFilePath) {
        this.checkerList = new CopyOnWriteArrayList<>();
        this.checkerMap = new ConcurrentHashMap<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(ruleFilePath);

            NodeList ruleList = document.getElementsByTagName("rule");
            logger.info("Rule文件为" + ruleFilePath + "，总共" + ruleList.getLength() + "条rules");

            for(int i = 0; i < ruleList.getLength(); i++){
                STNode treeHead = new STNode();

                Node ruleNode = ruleList.item(i);

                Node idNode = ruleNode.getChildNodes().item(1);
                Node formulaNode = ruleNode.getChildNodes().item(3);

                Map<String,STNode> stMap = new HashMap<>();
                buildSyntaxTree(formulaNode.getChildNodes(), treeHead, stMap, ruleFilePath);

                assert treeHead.hasChildNodes():"[INFO] Create syntax tree failed !";

                STNode root = (STNode)treeHead.getFirstChild();
                root.setParentTreeNode(null);

                Checker checker = null;
                if(checkType == PCC_TYPE) {
                    checker = new PccChecker(idNode.getTextContent(), root, this.patternMap, stMap);
                }
                else if (checkType == ECC_TYPE){
                    checker = new EccChecker(idNode.getTextContent(), root, this.patternMap, stMap);
                } else if(checkType == CON_TYPE){ //CON-C
                    checker = new ConChecker(idNode.getTextContent(), root, this.patternMap, stMap, taskNum, checkExecutorService);
                } else if(checkType == CONPCC_TYPE) {
                    checker = new ConPccChecker(idNode.getTextContent(), root, this.patternMap, stMap, taskNum, checkExecutorService);
                }

                checkerList.add(checker);
                for(String key : stMap.keySet()) {
                    checkerMap.put(stMap.get(key).getContextSetName(), checker);
                }

                //System.out.println("[DEBUG] " + checker.getName());
                //checker.printSyntaxTree();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            logger.info("配置文件解析失败：Rule文件" + ruleFilePath + "不存在");
            System.exit(1);
        }

        if (checkerList.isEmpty()) {
            logger.info("配置文件解析失败：Rule文件" + ruleFilePath + "没有rule");
            System.exit(1);
        }
    }

    protected List<String> fileReader(String filePath) {
        List<String> list = new ArrayList<>();
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fr);

            String change;
            while ((change = bufferedReader.readLine()) != null) {
                list.add(change);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private void buildSyntaxTree(NodeList list, STNode root, Map<String,STNode> stMap, String ruleFilePath) {
        for(int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE && !list.item(i).getNodeName().equals("param")) {
                Element e = (Element)list.item(i);
                STNode stNode = null;
                String nodeName = e.getNodeName();

                switch (nodeName) {
                    case "forall":
                        stNode = new STNode(nodeName, STNode.UNIVERSAL_NODE, e.getAttribute("in"));
                        stMap.put(e.getAttribute("in"), stNode);
                        break;
                    case "exists":
                        stNode = new STNode(nodeName, STNode.EXISTENTIAL_NODE, e.getAttribute("in"));
                        stMap.put(e.getAttribute("in"),stNode);
                        break;
                    case "and":
                        stNode = new STNode(nodeName, STNode.AND_NODE);
                        break;
                    case "not":
                        stNode = new STNode(nodeName, STNode.NOT_NODE);
                        break;
                    case "implies":
                        stNode = new STNode(nodeName, STNode.IMPLIES_NODE);
                        break;
                    case "bfunction":
                        stNode = new STNode(e.getAttribute("name"), STNode.BFUNC_NODE);
                        break;
                    default:
                        logger.info("配置文件解析失败：Rule文件" + ruleFilePath +  "存在非法的一致性规则标识符" + nodeName);
                        System.exit(1);
                        break;
                }

                buildSyntaxTree(e.getChildNodes(), stNode, stMap, ruleFilePath);
                root.addChildeNode(stNode);
            }
        }
    }

    public void shutdown() {
        checkExecutorService.shutdown();
        if(checkType == GAIN_TYPE) {
            /*GPUContextMemory.getInstance(contexts).free();
            for (Checker checker : checkerList) {
                checker.reset();
            }*/
        }
    }


    protected void accuracy(String logFilePath) {
        if (this.oracleFilePath != null) {
            Accuracy.main(new String[]{logFilePath, this.oracleFilePath});
        }
    }


    protected int computeWorkload() {
        int workload = 0;
        for(Checker checker: checkerList) {
            workload += checker.getWorkload();
        }
        return  workload;
    }
}
