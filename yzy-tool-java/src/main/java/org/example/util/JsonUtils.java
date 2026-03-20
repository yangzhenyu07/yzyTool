package org.example.util;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtils {
    private static ObjectMapper jsonMapper = new ObjectMapper();

    public JsonUtils() {
    }

    public static String toJson(Object data) {
        try {
            return jsonMapper.writeValueAsString(data);
        } catch (JsonProcessingException var2) {
            throw new RuntimeException("json序列化异常", var2);
        }
    }

    public static <T> T toObject(String json, Class<T> clz) {
        try {
            return jsonMapper.readValue(json.getBytes(), clz);
        } catch (JsonParseException var3) {
            throw new RuntimeException("json转换异常:" + json, var3);
        } catch (JsonMappingException var4) {
            throw new RuntimeException("json映射异常:" + json, var4);
        } catch (IOException var5) {
            throw new RuntimeException("jsonI/O异常:" + json, var5);
        }
    }

    public static <T> T toObject(String json, TypeReference<T> typeRef) {
        try {
            return jsonMapper.readValue(json.getBytes(), typeRef);
        } catch (JsonParseException var3) {
            throw new RuntimeException("json转换异常:" + json, var3);
        } catch (JsonMappingException var4) {
            throw new RuntimeException("json映射异常:" + json, var4);
        } catch (IOException var5) {
            throw new RuntimeException("jsonI/O异常:" + json, var5);
        }
    }

    public static boolean isJsonString(String json) {
        boolean isJsonString = false;
        if (json.startsWith("{") && json.endsWith("}") || json.startsWith("[") && json.endsWith("]")) {
            isJsonString = true;
        }

        return isJsonString;
    }
}
