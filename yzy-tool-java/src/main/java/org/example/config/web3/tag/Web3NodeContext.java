package org.example.config.web3.tag;

import java.util.HashMap;
import java.util.Map;

/**
* @author 杨镇宇
* @date 2026/3/19 23:18
* @version 1.0
*/

public class Web3NodeContext {

    private static final ThreadLocal<Web3NodeContext.Data> holder = new ThreadLocal<>();

    public Web3NodeContext() {
    }

    public static void init() {
        if (holder.get() == null) {
            holder.set(new Web3NodeContext.Data());
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
