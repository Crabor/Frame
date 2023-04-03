package platform.service.inv;

import com.alibaba.fastjson.JSONObject;
import common.struct.CtxServiceConfig;
import common.struct.InvServiceConfig;
import common.struct.ServiceConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.config.AppConfig;
import platform.config.Configuration;
import platform.communication.pubsub.Publisher;
import common.struct.enumeration.CmdType;
import common.util.Util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class PlatformInvServer {
    private static final Log logger = LogFactory.getLog(PlatformInvServer.class);
    private static final Map<String, AppInvServer> appInvServerMap = new HashMap<>();
    private static final ReentrantLock lock = new ReentrantLock();

    static {
        File dir = new File("output/platform/service/inv/");
        Util.deleteDir(dir);
        dir.mkdirs();
    }

    // 构造方法私有化
    private PlatformInvServer() {}

    public static boolean call(String appName, CmdType cmd, InvServiceConfig config) {
        //TODO
        AppConfig appConfig = Configuration.getAppsConfig().get(appName);
        if (appConfig == null) {
            return false;
        }

        if (appInvServerMap.containsKey(appName)) {
            if (cmd == CmdType.START) {
                return false;
            } else if (cmd == CmdType.STOP) {
//                appInvServerMap.get(appName).stop();
                appInvServerMap.remove(appName);
                appConfig.setInvServer(null);
                return true;
            }
        } else {
            if (cmd == CmdType.STOP) {
                return false;
            } else if (cmd == CmdType.START) {
                AppInvServer appInvServer = new AppInvServer(appConfig, config);
                appInvServerMap.put(appName, appInvServer);
                appConfig.setInvServer(appInvServer);
                appInvServer.start();
//                System.out.println("start appInvServer");
                return true;
            }
        }
        return false;
    }

    public static AppInvServer getAppInvServer(String appName) {
        return appInvServerMap.get(appName);
    }

    public static void lockDaikon() {
        lock.lock();
    }

    public static void unlockDaikon() {
        lock.unlock();
    }
}
