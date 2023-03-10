package platform.service.ctx.ctxServer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import platform.communication.pubsub.AbstractSubscriber;
import platform.service.ctx.ctxChecker.CheckerStarter;
import platform.service.ctx.item.Item;
import platform.service.ctx.item.ItemManager;
import platform.service.ctx.pattern.PatternManager;
import platform.service.ctx.pattern.matcher.FunctionMatcher;
import platform.service.ctx.pattern.matcher.PrimaryKeyMatcher;
import platform.service.ctx.pattern.Pattern;
import platform.service.ctx.pattern.types.DataSourceType;
import platform.service.ctx.pattern.types.FreshnessType;
import platform.service.ctx.rule.RuleManager;
import platform.service.ctx.rule.resolver.*;
import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.constraint.formulas.*;
import platform.service.ctx.ctxChecker.constraint.runtime.RuntimeNode;
import platform.service.ctx.statistics.ServerStatistics;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public abstract class AbstractCtxServer extends AbstractSubscriber implements Runnable{
    protected Thread t;

    protected PatternManager patternManager;

    protected RuleManager ruleManager;

    protected ItemManager itemManager;

    protected ChangeGenerator changeGenerator;

    protected CheckerStarter checker;

    protected CtxFixer ctxFixer;

    protected ServerStatistics serverStatistics;


    public abstract void init();

    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }

    public void restart() {
        t = new Thread(this, getClass().getName());
        t.start();
    }


    public PatternManager getPatternManager() {
        return patternManager;
    }

    public RuleManager getRuleManager() {
        return ruleManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public ChangeGenerator getChangeGenerator() {
        return changeGenerator;
    }

    public CheckerStarter getChecker() {
        return checker;
    }

    public CtxFixer getCtxFixer() {
        return ctxFixer;
    }

    public ServerStatistics getServerStatistics() {
        return serverStatistics;
    }
}
