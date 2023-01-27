package platform.service.inv;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import platform.service.inv.struct.CheckInfo;
import reactor.util.annotation.Nullable;

import java.util.*;

public class CheckArray implements List<CheckObject> {
    private List<CheckObject> list;

    public CheckArray() {
        list = new ArrayList<>();
    }

    public CheckArray(List<CheckObject> list) {
        this.list = list;
    }

    public void add(int index, CheckObject element) {
        list.add(index, element);
    }

    public boolean add(CheckObject o) {
        return list.add(o);
    }

    public boolean addAll(Collection<? extends CheckObject> c) {
        return list.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends CheckObject> c) {
        return list.addAll(index, c);
    }

    public void clear() {
        list.clear();
    }

    public boolean contains(Object o) {
        return list.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    public CheckObject get(int index) {
        return list.get(index);
    }

    @Nullable
    public CheckObject get(String name) {
        for (CheckObject co : list) {
            if (co.getName().equals(name)) {
                return co;
            }
        }
        return null;
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public Iterator<CheckObject> iterator() {
        return list.iterator();
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    public ListIterator<CheckObject> listIterator() {
        return list.listIterator();
    }

    public ListIterator<CheckObject> listIterator(int index) {
        return list.listIterator(index);
    }

    public CheckObject remove(int index) {
        return list.remove(index);
    }

    public boolean remove(Object o) {
        return list.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    public CheckObject set(int index, CheckObject element) {
        return list.set(index, element);
    }

    public int size() {
        return list.size();
    }

    public List<CheckObject> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    public static CheckArray fromJsonObjectString(String jsonObjectString) {
        String appName = Thread.currentThread().getStackTrace()[2].getClassName();
        JSONObject jo = JSONObject.parseObject(jsonObjectString);
        CheckArray ca = new CheckArray();
        for (String key : jo.keySet()) {
            CheckObject co = CheckObject.get(appName, key);
            co.setValue(jo.getDouble(key));
            ca.add(co);
        }
        return ca;
    }

    public static CheckArray[] fromJsonArrayString(String jsonArrayString) {
        String appName = Thread.currentThread().getStackTrace()[2].getClassName();
        JSONArray ja = JSONArray.parseArray(jsonArrayString);
        CheckArray[] cas = new CheckArray[ja.size()];
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo = ja.getJSONObject(i);
            CheckArray ca = new CheckArray();
            for (String key : jo.keySet()) {
                CheckObject co = CheckObject.get(appName, key);
                co.setValue(jo.getDouble(key));
                ca.add(co);
            }
            cas[i] = ca;
        }
        return cas;
    }

    public CheckInfo[] check() {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        CheckInfo[] checkInfos = new CheckInfo[size()];
        for (int i = 0; i < size(); i++) {
            checkInfos[i] = get(i).check(lineNumber);
        }
        return checkInfos;
    }
}
