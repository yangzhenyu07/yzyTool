package org.example.annotation.tag;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangzhenyu
 * @version 1.0
 * @description:
 * @date 2024/12/19 15:51
 */
public class BizContext {
    private static final ThreadLocal<Data> holder = new ThreadLocal<>();

    public BizContext() {
    }

    public static void init() {
        if (holder.get() == null) {
            holder.set(new BizContext.Data());
        }

    }

    public static boolean isInit() {
        return holder.get() != null;
    }

    public static void clearData() {
        holder.set(null);
    }

    public static void clear(){
        holder.remove();
    }

    public static void put(String key, Object val) {
        if (holder.get() != null) {
            holder.get().map.put(key, val);
        }

    }

    public static Object get(String key) {
        return holder.get() != null ? holder.get().map.get(key) : null;
    }

    public static void remove(String key) {
        if (holder.get() != null) {
            holder.get().map.remove(key);
        }

    }

    public static boolean contain(String key) {
        return holder.get() != null && holder.get().map.containsKey(key);
    }

    public static String dump() {
        return holder.get() != null ? holder.get().map.toString() : "{}";
    }

    private static class Data {
        private Map<String, Object> map;

        private Data() {
            this.map = new HashMap();
        }
    }
}
