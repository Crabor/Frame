package platform.service.ctx.Patterns;

import java.util.ArrayList;
import java.util.List;

public class Pattern {
    private String patternId;
    private String freshnessType;
    private String freshnessValue;
    private final List<String> sensorList;
    private AbstractMatcher matcher;

    public Pattern() {
        this.sensorList = new ArrayList<>();
    }

    public void setPatternId(String patternId) {
        this.patternId = patternId;
    }

    public void setFreshnessType(String freshnessType) {
        this.freshnessType = freshnessType;
    }

    public void setFreshnessValue(String freshnessValue) {
        this.freshnessValue = freshnessValue;
    }

    public void addSensor(String sensor){
        this.sensorList.add(sensor);
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

    public List<String> getSensorList() {
        return sensorList;
    }

    public AbstractMatcher getMatcher() {
        return matcher;
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "pattern_id='" + patternId + '\'' +
                ", freshnessType='" + freshnessType + '\'' +
                ", freshnessValue='" + freshnessValue + '\'' +
                ", sensorList=" + sensorList +
                ", matcher=" + matcher +
                '}';
    }
}
