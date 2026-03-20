package org.example.util;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.compress.utils.Lists;
import org.example.dto.TSchedulerLockHistoryDTO;
import org.example.vo.TSchedulerLockHistoryVo;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * 对象处理工具
* @author 杨镇宇
* @date 2025/2/14 11:18
* @version 1.0
*/

public class BeanUtil extends BeanUtils {
    public BeanUtil(){

    }

    /**
     * 单个copy
     */
    public static <T> T propertiesCopy(Object source,Class<T> clazz){
        if (ObjectUtil.isNull(source)){
            return null;
        }
        try {
            T obj = clazz.newInstance();
            BeanUtils.copyProperties(source,obj);
            return obj;
        } catch (InstantiationException  | IllegalAccessException e)  {
           throw new RuntimeException(e);
        }

    }

    /**
     * List 中对象的copy
     */
    public static <T> List<T> collectionCopy(Collection source,Class<T> tClass){
        if (ObjectUtil.isNull(source)){
            return Lists.newArrayList();
        }
        List<T> list = Lists.newArrayList();
        for (Object next : source) {
            list.add(propertiesCopy(next, tClass));
        }
        return list;

    }

    /**
     * Object 对象 转 Map
     * @param o
     * @return
     */

    public static Map<String,String> toJSONMap(Object o){
        if (ObjectUtil.isNotNull(o)){
            String s = JSON.toJSONString(o);
            JSONObject jsonObject = JSON.parseObject(s);
            HashMap<String,String> res = new HashMap<>(jsonObject.size());
            jsonObject.forEach((k,v) ->{
                if (k.toLowerCase().endsWith("password")){
                    res.put(k,"******");
                }else {
                    res.put(k,JSON.toJSONString(v));
                }
            });
            return res;
        }
        return Collections.emptyMap();
    }

    public static void main(String[] args) {
        TSchedulerLockHistoryDTO dto = new TSchedulerLockHistoryDTO();
        dto.setErrorMessage("0000");
        TSchedulerLockHistoryVo vo = propertiesCopy(dto, TSchedulerLockHistoryVo.class);
        System.out.println("单个copy:"+toJSONMap(vo));
        List<TSchedulerLockHistoryDTO> lockHistoryDTOS = Lists.newArrayList();
        lockHistoryDTOS.add(dto);
        List<TSchedulerLockHistoryVo> tSchedulerLockHistoryVos = collectionCopy(lockHistoryDTOS, TSchedulerLockHistoryVo.class);
        System.out.println("List 中对象的copy:"+tSchedulerLockHistoryVos);

    }


}
