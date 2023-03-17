package platform.service.ctx.pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import platform.service.ctx.ctxServer.AbstractCtxServer;
import platform.service.ctx.pattern.matcher.FunctionMatcher;
import platform.service.ctx.pattern.matcher.PrimaryKeyMatcher;
import platform.service.ctx.pattern.types.DataSourceType;
import platform.service.ctx.pattern.types.FreshnessType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class PatternManager {
    private final AbstractCtxServer ctxServer;

    private final HashMap<String, Pattern> patternMap;

    public PatternManager(AbstractCtxServer ctxServer) {
        this.ctxServer = ctxServer;
        this.patternMap = new HashMap<>();
    }

    public void buildPatterns(String patternFile, String mfuncFile){
        if(patternFile == null || patternFile.equals("")){
            return;
        }

        Object mfuncInstance = loadMfuncFile(mfuncFile);
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File(patternFile));
            List<Element> patternElements = document.getRootElement().elements();
            for(Element patternElement :  patternElements){
                List<Element> labelElements = patternElement.elements();
                assert labelElements.size() == 3 || labelElements.size() == 4;
                Pattern pattern = new Pattern();
                //patternId
                assert labelElements.get(0).getName().equals("id");
                pattern.setPatternId(labelElements.get(0).getText());
                //freshness
                assert labelElements.get(1).getName().equals("freshness");
                List<Element> freshnessElements = labelElements.get(1).elements();
                assert freshnessElements.size() == 2;
                assert freshnessElements.get(0).getName().equals("type");
                assert freshnessElements.get(1).getName().equals("value");
                pattern.setFreshnessType(FreshnessType.fromString(freshnessElements.get(0).getText()));
                pattern.setFreshnessValue(freshnessElements.get(1).getText());
                //dataSource
                assert labelElements.get(2).getName().equals("dataSource");
                List<Element> dataSourceElements = labelElements.get(2).elements();
                assert dataSourceElements.size() == 2;
                assert dataSourceElements.get(0).getName().equals("type");
                assert dataSourceElements.get(1).getName().equals("sourceList");
                pattern.setDataSourceType(DataSourceType.fromString(dataSourceElements.get(0).getText()));
                List<Element> sourceElements = dataSourceElements.get(1).elements();
                for(Element sourceElement : sourceElements){
                    assert sourceElement.getName().equals("source");
                    pattern.addDataSource(sourceElement.getText());
                }
                //matcher (optional)
                if(labelElements.size() == 4){
                    assert labelElements.get(3).getName().equals("matcher");
                    List<Element> matcherElements = labelElements.get(3).elements();
                    assert matcherElements.get(0).getName().equals("type");
                    String matcherType = matcherElements.get(0).getText();
                    if(matcherType.equals("primaryKey")){
                        assert matcherElements.get(1).getName().equals("primaryKey");
                        PrimaryKeyMatcher primaryKeyMatcher = new PrimaryKeyMatcher(matcherElements.get(1).getText());
                        assert matcherElements.get(2).getName().equals("optionalValueList");
                        List<Element> optionalValueElements = matcherElements.get(2).elements();
                        for(Element optionalValueElement : optionalValueElements){
                            assert optionalValueElement.getName().equals("value");
                            primaryKeyMatcher.addOptionalValue(optionalValueElement.getText());
                        }
                        pattern.setMatcher(primaryKeyMatcher);
                    }
                    else if(matcherType.equals("function")){
                        assert matcherElements.get(1).getName().equals("functionName");
                        FunctionMatcher functionMatcher = new FunctionMatcher(matcherElements.get(1).getText(), mfuncInstance);
                        //extraArgumentList (optional)
                        if(matcherElements.size() == 3){
                            assert matcherElements.get(2).getName().equals("extraArgumentList");
                            List<Element> extraArgElements = matcherElements.get(2).elements();
                            for(Element extraArgElement : extraArgElements){
                                assert extraArgElement.getName().equals("argument");
                                functionMatcher.addExtraArg(extraArgElement.getText());
                            }
                        }
                        pattern.setMatcher(functionMatcher);
                    }
                    else{
                        assert false;
                    }
                }
                patternMap.put(pattern.getPatternId(), pattern);
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private Object loadMfuncFile(String mfuncFile) {
        if(mfuncFile == null || mfuncFile.equals("")){
            return null;
        }
        Object mfuncInstance;
        Path mfuncPath = Paths.get(mfuncFile).toAbsolutePath();
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{mfuncPath.getParent().toFile().toURI().toURL()})) {
            Class<?> clazz = classLoader.loadClass(mfuncPath.getFileName().toString().split("\\.")[0]);
            Constructor<?> constructor = clazz.getConstructor();
            mfuncInstance = constructor.newInstance();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return mfuncInstance;
    }

    public HashMap<String, Pattern> getPatternMap() {
        return patternMap;
    }

    public AbstractCtxServer getCtxServer() {
        return ctxServer;
    }

    public void reset(String patternFile, String mfuncFile){
        this.patternMap.clear();
        buildPatterns(patternFile, mfuncFile);
    }
}
