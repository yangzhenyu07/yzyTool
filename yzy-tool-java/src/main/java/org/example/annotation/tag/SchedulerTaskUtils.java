package org.example.annotation.tag;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yangzhenyu
 * @version 1.0
 * @description:
 * @date 2024/12/19 15:50
 */
public class SchedulerTaskUtils {
    private static final String SCF_TENANT_TAG = "scf-tenant-tag";
    private static final String SPLIT = "@@Y@@";
    public SchedulerTaskUtils() {
    }

    public static String getChannelTag() {
        if (!BizContext.isInit()) {
            BizContext.init();
        }

        return BizContext.contain(SCF_TENANT_TAG) && BizContext.get(SCF_TENANT_TAG) != null ? BizContext.get(SCF_TENANT_TAG).toString() : null;
    }
    public static String getSplit(){
        return SPLIT;
    }



    public static void updateChannelTag(TagEnum channelTag) {
        updateChannelTag(channelTag.getCode(), false);
    }
    public static void updateChannelTag(TagEnum channelTag,String msg) {
        String message= "{0}"+SPLIT+"{1}";

        if (TagEnum.SUCCESS_TAG.equals(channelTag)) {
            updateChannelTag(channelTag.getCode(), false);
        }else {
            updateChannelTag(MessageFormat.format(message, channelTag.getCode(),msg), false);
        }
    }

    public static void updateChannelTag(String channelTag, boolean forceCheck) {
        if (!forceCheck || channelTag != null && !channelTag.equals("")) {
            if (!BizContext.isInit()) {
                BizContext.init();
            }

            BizContext.put(SCF_TENANT_TAG, channelTag);
        } else {
            Thread thread = Thread.currentThread();
            throw new RuntimeException("当前线程[" + thread.getName() + "]更新的tag为空");
        }
    }

    public static void removeChannelTag() {
        if (BizContext.isInit()) {
            BizContext.remove(SCF_TENANT_TAG);
        }
    }

    public static Set<String> getAllTenants() {
        Set<String> tenantSet = new HashSet();
        TagEnum[] var1 = TagEnum.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            TagEnum tenantEnum = var1[var3];
            tenantSet.add(tenantEnum.getCode());
        }

        return tenantSet;
    }



}
