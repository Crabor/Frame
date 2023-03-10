package platform.service.ctx.pattern;

import platform.service.ctx.pattern.matcher.AbstractMatcher;
import platform.service.ctx.pattern.types.DataSourceType;
import platform.service.ctx.pattern.types.FreshnessType;

import java.util.HashSet;
import java.util.Set;

public class Pattern {
    private String patternId;
    private FreshnessType freshnessType;
    private String freshnessValue;
    private DataSourceType dataSourceType;
    private final Set<String> dataSourceSet;
    private AbstractMatcher matcher;

    public Pattern() {
        this.dataSourceSet = new HashSet<>();
    }

    public void addDataSource(String dataSource) {this.dataSourceSet.add(dataSource);}

    public void setPatternId(String patternId) {
        this.patternId = patternId;
    }

    public void setFreshnessType(FreshnessType freshnessType) {
        this.freshnessType = freshnessType;
    }

    public void setFreshnessValue(String freshnessValue) {
        this.freshnessValue = freshnessValue;
    }

    public void setDataSourceType(DataSourceType dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public void setMatcher(AbstractMatcher matcher) {
        this.matcher = matcher;
    }

    public String getPatternId() {
        return patternId;
    }

    public FreshnessType getFreshnessType() {
        return freshnessType;
    }

    public String getFreshnessValue() {
        return freshnessValue;
    }

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    public Set<String> getDataSourceSet() {
        return dataSourceSet;
    }

    public AbstractMatcher getMatcher() {
        return matcher;
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "patternId='" + patternId + '\'' +
                ", freshnessType='" + freshnessType + '\'' +
                ", freshnessValue='" + freshnessValue + '\'' +
                ", dataSourceType='" + dataSourceType + '\'' +
                ", dataSourceList=" + dataSourceSet +
                ", matcher=" + matcher +
                '}';
    }
}
