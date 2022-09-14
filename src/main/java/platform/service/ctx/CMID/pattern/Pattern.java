package platform.service.ctx.CMID.pattern;
import platform.service.ctx.CMID.context.Context;
import platform.service.ctx.CMID.util.TimestampHelper;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by njucjc on 2017/10/23.
 */
public class Pattern {
    private String id;
    private long freshness;
    private String category;
    private String subject;
    private String predicate;
    private String object;
    private String site;

    private List<Context> contextList;

    public Pattern(String id,
                   long freshness,
                   String category,
                   String subject,
                   String predicate,
                   String object,
                   String site) {
        this.id = id;
        this.freshness = freshness;
        this.category = category;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.site = site;

        this.contextList = new CopyOnWriteArrayList<>();
    }

    public long getFreshness() {
        return freshness;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public List<Context> getContextList() {
        return contextList;
    }

    /**
     * 判断一个context是否属于改pattern
     * @param context
     * @return
     */
    public boolean isBelong(Context context) {

        return true;
    }

    /**
     * 添加一个context到pattern集合
     * @param context
     * @return
     */
    public synchronized boolean addContext(Context context) {
        if(!isBelong(context)) {
            return false;
        }
        contextList.add(context);
        return true;
    }

    /**
     * 若第一个context的过期时间为timestamp则删除
     * @param timestamp 时间戳
     */
    public synchronized boolean deleteFirstByTime(long timestamp) {
        boolean isDel = false;
        for (Context context : contextList) {
            if(TimestampHelper.timestampDiff(context.getTimestamp(), timestamp) >= freshness) {
                isDel = true;
                contextList.remove(context);
            }
            else {
                break;
            }
        }
        return isDel;
    }

    /**
     * 获取到timestamp时刻，已经过时的context，并返回它们的过时时刻
     * @param timestamp
     * @return
     */
    public Set<Long> getOutOfDateTimes(long timestamp) {
        Set<Long> timeSet = new HashSet<>();
        for(Context context : contextList) {
            if(TimestampHelper.timestampDiff(context.getTimestamp(), timestamp) > freshness) {
                timeSet.add(TimestampHelper.plusMillis(context.getTimestamp(), freshness));
            }
            else {//找到第一个还未过期的context即可结束遍历
                break;
            }
        }
        return timeSet;
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "id=" + id +
                ", freshness=" + freshness +
                ", category=" + category +
                ", subject=" + subject +
                ", predicate=" + predicate +
                ", object=" + object +
                ", site=" + site +
                '}';
    }
}