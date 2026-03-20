package org.example.util;

import java.util.UUID;

/**
* @author 杨镇宇
* @date 2024/12/17 16:40
* @version 1.0
*/

public class UuidUtil {
    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
