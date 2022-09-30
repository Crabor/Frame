package platform.service.ctx.Patterns;

import java.util.ArrayList;
import java.util.List;

public class Pattern {
    private String patternId;
    private String freshnessType;
    private String freshnessValue;
    private String dataSourceType;
    private final List<String> dataSourceList;
    private AbstractMatcher matcher;

    public Pattern() {
        this.dataSourceList = new ArrayList<>();
    }


    public void addDataSource(String dataSource) {this.dataSourceList.add(dataSource);}

    public void setPatternId(String patternId) {
        this.patternId = patternId;
    }

    public void setFreshnessType(String freshnessType) {
        this.freshnessType = freshnessType;
    }

    public void setFreshnessValue(String freshnessValue) {
        this.freshnessValue = freshnessValue;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public void setMatcher(AbstractMatcher matcher) {
        this.matcher = matcher;
    }

    public String getPatternId() {
        return patternId;
    }

    public String getFreshnessType() {
        return freshnessType;
    }

    public String getFreshnessValue() {
        return freshnessValue;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public List<String> getDataSourceList() {
        return dataSourceList;
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
                ", dataSourceList=" + dataSourceList +
                ", matcher=" + matcher +
                '}';
    }
}
