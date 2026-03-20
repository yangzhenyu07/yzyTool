package org.example.annotation.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* @description: TODO
* @author 杨镇宇
* @date 2024/12/18 10:20
* @version 1.0
*/
public enum SchedulerTaskEnum {
    TEST_ONE_TASK("testOneTask","测试A定时器"),
    TEST_TWO_TASK("testTwoTask","测试B定时器"),
    TEST_THREE_TASK("testThreeTask","测试C定时器"),
    TEST_FOUR_TASK("testFourTask","测试D定时器"),
    DEFINED("to be defined","未定义");


    /**
     * key
     */
    private String key;

    /**
     * 内容
     */
    private String value;

    SchedulerTaskEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getKey(SchedulerTaskEnum schedulerTaskEnum){
        return schedulerTaskEnum.key;
    }
    /**
     * 根据code 查询 desc
     * */
    public static  String getValue(String key){
        if(StringUtils.isEmpty(key)) {
            return null;
        }
        List<String> list  = new ArrayList<>();
        Arrays.stream(SchedulerTaskEnum.values()).filter(t->t.key.equals(key)).forEach(v->{
            list.add(v.value);
        });

        return list.size()>0?list.get(0):"";
    }
}
