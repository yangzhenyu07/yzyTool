package org.example.util;

import java.text.NumberFormat;

/**
 * 数字格式化
* @author 杨镇宇
* @date 2025/2/14 14:18
* @version 1.0
*/

public class FormatUtils {
    private static final NumberFormat FORMATTER = NumberFormat.getInstance();
    private static final NumberFormat ROUND_FORMATTER = NumberFormat.getInstance();

    static {
        FORMATTER.setMaximumFractionDigits(2);
        ROUND_FORMATTER.setMaximumFractionDigits(0);

    }

    /**
     * 四舍五入保留两位小数
     * @param value
     * @return
     */
    public static String formatDouble(double value){
        return FORMATTER.format(value);
    }

    /**
     * 四舍五入保留整数
     * @param value
     * @return
     */
    public static String round(double value){
        return ROUND_FORMATTER.format(value);
    }

    /**
     * 转化为百分比字符串，最高100%
     * @param value
     * @return
     */
    public static String percent(double value){
        if (1 < value){
            value = 1;
        }
        return FORMATTER.format(value * 100)+"%";
    }

    /**
     * 原有数字后面添加%
     * @param value
     * @return
     */
    public static String percentOnlyAdd(double value){
        return ROUND_FORMATTER.format(value)+"%";
    }

    public static void main(String[] args) {
        System.out.println("四舍五入保留两位小数: "+formatDouble(45.56823));
        System.out.println("四舍五入保留整数: "+round(45.56823));
        System.out.println("转化为百分比字符串，最高100%: "+percent(0.4556823));
        System.out.println("原有数字后面添加%: "+percentOnlyAdd(45.56823));

    }
}
