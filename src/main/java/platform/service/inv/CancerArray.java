package platform.service.inv;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import reactor.util.annotation.Nullable;

import java.util.*;

public class CancerArray implements List<CancerObject> {
    private List<CancerObject> list;
    
    public CancerArray() {
        list = new ArrayList<>();
    }
    
    public CancerArray(List<CancerObject> list) {
        this.list = list;
    }

    public void add(int index, CancerObject element) {
        list.add(index, element);
    }

    public boolean add(CancerObject o) {
        return list.add(o);
    }

    public boolean addAll(Collection<? extends CancerObject> c) {
        return list.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends CancerObject> c) {
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

    public CancerObject get(int index) {
        return list.get(index);
    }

    @Nullable
    public CancerObject get(String name) {
        for (CancerObject co : list) {
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

    public Iterator<CancerObject> iterator() {
        return list.iterator();
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    public ListIterator<CancerObject> listIterator() {
        return list.listIterator();
    }

    public ListIterator<CancerObject> listIterator(int index) {
        return list.listIterator(index);
    }

    public CancerObject remove(int index) {
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

    public CancerObject set(int index, CancerObject element) {
        return list.set(index, element);
    }

    public int size() {
        return list.size();
    }
    
    public List<CancerObject> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    public static CancerArray fromJsonObjectString(String jsonObjectString) {
        String appName = Thread.currentThread().getStackTrace()[2].getClassName();
        JSONObject jo = JSONObject.parseObject(jsonObjectString);
        CancerArray ca = new CancerArray();
        for (String key : jo.keySet()) {
            CancerObject co = CancerObject.get(appName, key);
            co.setValue(jo.getDouble(key));
            ca.add(co);
        }
        return ca;
    }

    public static CancerArray[] fromJsonArrayString(String jsonArrayString) {
        String appName = Thread.currentThread().getStackTrace()[2].getClassName();
        JSONArray ja = JSONArray.parseArray(jsonArrayString);
        CancerArray[] cas = new CancerArray[ja.size()];
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo = ja.getJSONObject(i);
            CancerArray ca = new CancerArray();
            for (String key : jo.keySet()) {
                CancerObject co = CancerObject.get(appName, key);
                co.setValue(jo.getDouble(key));
                ca.add(co);
            }
            cas[i] = ca;
        }
        return cas;
    }

    public String check() {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        StringBuilder jsonArray = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            if (i == 0) {
                jsonArray.append("[");
            } else {
                jsonArray.append(",");
            }
            jsonArray.append(get(i).check(lineNumber));
        }
        jsonArray.append("]");
        return jsonArray.toString();
    }
}
