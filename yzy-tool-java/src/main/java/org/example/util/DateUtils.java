package org.example.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期工具类
 * DateUtils类提供了多种日期格式化和转换工具，支持UTC和本地时间（GMT+8）的格式化，适用于日志记录和时间转换。
 * <p>
 * 支持以下功能：
 * <ul>
 *     <li>将日期转换为UTC时间字符串</li>
 *     <li>将日期转换为本地时间字符串（GMT+8）</li>
 *     <li>将日期转换为不带分隔符的字符串（yyyyMMdd格式）</li>
 *     <li>将UTC时间字符串转换为本地时间字符串</li>
 *     <li>日期字符串转Date对象</li>
 * </ul>
 * <p>
 * 本类使用了ThreadLocal来避免多线程环境下的SimpleDateFormat线程安全问题。
 * @author 杨镇宇
 * @date 2025/2/14 15:00
 * @version 1.0
 */
@Slf4j
public class DateUtils {

    private static final ThreadLocal<SimpleDateFormat> UTC_DATE_FORMATTER = ThreadLocal.withInitial(() -> {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    });

    private static final ThreadLocal<SimpleDateFormat> GM8_DATE_FORMATTER = ThreadLocal.withInitial(() -> {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return format;
    });

    private static final ThreadLocal<SimpleDateFormat> LOCAL_DATE_FORMATTER = ThreadLocal.withInitial(() -> {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return format;
    });

    private static final ThreadLocal<SimpleDateFormat> LOCAL_DATE_TIME_FORMATTER = ThreadLocal.withInitial(() ->
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    private static final ThreadLocal<SimpleDateFormat> LOCAL_DATE_TIME_FORMATTER_NO_CHAR = ThreadLocal.withInitial(() ->
            new SimpleDateFormat("yyyyMMdd"));

    /**
     * 将UTC时间转换为字符串形式，格式为 "yyyy-MM-dd'T'HH:mm:ss'Z'"（UTC时区）
     * @param date 要转换的日期
     * @return UTC格式的时间字符串
     */
    public static String toUTCDateString(Date date) {
        return UTC_DATE_FORMATTER.get().format(date);
    }

    /**
     * 将UTC时间戳转换为字符串形式
     * @param timestamp UTC时间戳
     * @return UTC格式的时间字符串
     */
    public static String toUTCDateString(long timestamp) {
        return toUTCDateString(new Date(timestamp));
    }

    /**
     * 将东八区（GMT+8）时间转换为字符串格式，格式为 "yyyy-MM-dd'T'HH:mm:ss'Z'"
     * @param date 要转换的日期
     * @return 东八区格式的时间字符串
     */
    public static String toLocalDateString(Date date) {
        return GM8_DATE_FORMATTER.get().format(date);
    }

    /**
     * 将毫秒时间戳转换为东八区的日期格式字符串（yyyy-MM-dd）
     * @param timeInMills 毫秒时间戳
     * @return 东八区格式的日期字符串
     */
    public static String toLocalDate(long timeInMills) {
        return LOCAL_DATE_FORMATTER.get().format(new Date(timeInMills));
    }

    /**
     * 将日期转换为 "yyyyMMdd" 格式的字符串（没有字符分隔符）
     * @param date 要转换的日期
     * @return 没有字符分隔符的日期字符串
     */
    public static String toLocalNoCharDate(Date date) {
        return LOCAL_DATE_TIME_FORMATTER_NO_CHAR.get().format(date);
    }

    /**
     * 将UTC时间字符串转换为本地时间字符串（GMT+8）
     * @param utcTimeString UTC时间字符串
     * @return 本地时间字符串（GMT+8）
     */
    public static String toLocalDateTime(String utcTimeString) {
        try {
            Date time = UTC_DATE_FORMATTER.get().parse(utcTimeString);
            if (null != time) {
                return LOCAL_DATE_TIME_FORMATTER.get().format(time);
            }
        } catch (ParseException e) {
            log.warn("parse utc to local date error", e);
        }
        return utcTimeString;
    }

    /**
     * 将日期字符串转换为Date对象（东八区格式）
     * @param date 日期字符串
     * @return 转换后的Date对象
     */
    public static Date toDate(String date) {
        try {
            return LOCAL_DATE_FORMATTER.get().parse(date);
        } catch (ParseException e) {
            log.warn("parse date error, using default", e);
        }
        return new Date(); // 如果解析失败，返回当前时间
    }

    /**
     * 将东八区日期转换为字符串形式（yyyy-MM-dd格式）
     * @param date 要转换的日期
     * @return 东八区格式的日期字符串
     */
    public static String toDateString(Date date) {
        return LOCAL_DATE_FORMATTER.get().format(date);
    }

    /**
     * 将东八区时间转换为字符串形式（yyyy-MM-dd HH:mm:ss格式）
     * @param date 要转换的日期
     * @return 东八区格式的时间字符串
     */
    public static String toDateTimeString(Date date) {
        return LOCAL_DATE_TIME_FORMATTER.get().format(date);
    }

    public static void main(String[] args) {
        Date now = new Date();

        // 测试 toUTCDateString
        System.out.println("UTC Date String: " + DateUtils.toUTCDateString(now)); //UTC Date String: 2025-02-14T07:48:41Z

        // 测试 toLocalDateString
        System.out.println("Local Date String: " + DateUtils.toLocalDateString(now)); //Local Date String: 2025-02-14T15:48:41Z

        // 测试 toLocalDate
        System.out.println("Local Date: " + DateUtils.toLocalDate(now.getTime())); //Local Date: 2025-02-14

        // 测试 toLocalNoCharDate
        System.out.println("Local No Char Date: " + DateUtils.toLocalNoCharDate(now)); // Local No Char Date: 20250214

        // 测试 toLocalDateTime (UTC -> Local)
        String utcTime = DateUtils.toUTCDateString(now);
        System.out.println("UTC Time: " + utcTime); //UTC Time: 2025-02-14T07:48:41Z
        System.out.println("Local DateTime: " + DateUtils.toLocalDateTime(utcTime)); // Local DateTime: 2025-02-14 15:48:41

        // 测试 toDate (String to Date)
        String localDateStr = DateUtils.toLocalDateString(now);
        Date dateFromString = DateUtils.toDate(localDateStr);
        System.out.println("Date from String: " + dateFromString); // Date from String: Fri Feb 14 00:00:00 CST 2025

        // 测试 toDateString and toDateTimeString
        System.out.println("Date String: " + DateUtils.toDateString(now)); // Date String: 2025-02-14
        System.out.println("DateTime String: " + DateUtils.toDateTimeString(now)); // DateTime String: 2025-02-14 15:48:41
    }
}
