package org.example.config.web3.tag;

import org.example.annotation.tag.BizContext;

/**
 * web3 私有线程工具类
 * @author 杨镇宇
 * @date 2026/3/19 23:19
 * @version 1.0
 */

public class Web3NodeUtils {
    public static final String TENANT_TAG = "web3-node-tag"; //存储在私有线程的内容的key
    public static final String TAG_TYPE = "web3-type"; //表示是交易类还是查询类的key

    public Web3NodeUtils() {

    }


    public static String getChannelTag() {
        if (!BizContext.isInit()) {
            BizContext.init();
        }

        return BizContext.contain(TENANT_TAG) && BizContext.get(TENANT_TAG) != null ? BizContext.get(TENANT_TAG).toString() : null;
    }
    // 判断类型:0 - 查询类、1 - 交易类
    public static String getChannelType() {
        if (!BizContext.isInit()) {
            BizContext.init();
        }

        return BizContext.contain(TAG_TYPE) && BizContext.get(TAG_TYPE) != null ? BizContext.get(TAG_TYPE).toString() : null;
    }

    /**
     * channelTag 链key
     * flag 0:查询类、1:交易类
     * @param channelTag
     * @param flag
     */
    public static void updateChannelTag(String channelTag,String flag) {
        updateChannelTag(channelTag, false,flag);
    }
    public static void updateChannelTag(String channelTag, boolean forceCheck,String flag) {
        if (!forceCheck || channelTag != null && !channelTag.equals("")) {
            if (!BizContext.isInit()) {
                BizContext.init();
            }

            BizContext.put(TENANT_TAG, channelTag);
            BizContext.put(TAG_TYPE, null == flag?"0":flag);

        } else {
            Thread thread = Thread.currentThread();
            throw new RuntimeException("当前线程[" + thread.getName() + "]更新的tag为空");
        }
    }

    public static void clear(){
        BizContext.clear();
    }
}
