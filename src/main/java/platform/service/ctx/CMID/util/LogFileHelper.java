package platform.service.ctx.CMID.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/**
 * Created by njucjc on 2017/10/13.
 */
public class LogFileHelper {
    private static final Log logger = LogFactory.getLog(LogFileHelper.class);
    private LogFileHelper() {}
    private static Logger LOGGER;
    public static String logFilePath;
    public static void  initLogger(String log) {
        logFilePath = log;
        File file = new File(logFilePath);
        if (file.exists()) {
            file.delete();
        }
//        if (file.exists()) {
//            logger.info("日志文件" + "'" + logFilePath + "'" + "已存在，是否覆盖（Y/N）：");
//            Scanner in = new Scanner(System.in);
//            String str;
//            while (true) {
//                str = in.nextLine();
//                if("y".equals(str.toLowerCase())) {
//                    file.delete();
//                    break;
//                }
//                else if ("n".equals(str.toLowerCase())) {
//                    do {
//                        logger.info("请输入新的日志文件路径：");
//                        logFilePath = in.nextLine();
//                    } while (logFilePath.equals("") || new File(logFilePath).exists());
//                    break;
//                }
//                else {
//                    logger.info("是否覆盖，请输入（Y/N）：");
//                }
//            }
//        }
        logger.info("日志文件：" + logFilePath);
        LOGGER = new Logger(logFilePath, true);
    }

    public synchronized static Logger getLogger() {
        return LOGGER;
    }

    public static void main(String  args[]) {
        getLogger().info("Hello World.", false);
        LOGGER.info("123", false);
    }

}

