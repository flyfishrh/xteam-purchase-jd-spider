package com.flyfish.jdspider.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 默认日期格式
     */
    public static final String YEAR_MONTH_DAY_FORMAT = "yyyy-MM-dd";
    /**
     * 默认时间格式
     */
    public static final String TIME_FORMAT = "HH:mm:ss";
    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 精确到分钟时间格式
     */
    public static final String MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
    /**
     * 年月格式
     */
    public static final String YEAR_MONTH = "yyyy-MM";

    /**
     * 无分隔符格式
     */
    public static final String DATETIME_FORMAT_NO_SPLIT = "yyyyMMddHHmmss";

    //各种非标准日期格式

    private static final String NS_YEAR_MONTH = "yyyy年MM月";
    private static final String NS_YEAR_MONTH_DAY = "yyyy年MM月dd日";
    private static final String NS_TIME = "yyyy.MM.dd HH:mm:ss";
    private static final String NS_TIME2 = "yyyy/MM/dd HH:mm:ss";
    private static final String NS_MINUTE = "yyyy.MM.dd HH:mm";
    private static final String NS_MINUTE2 = "yyyy年MM月dd日 HH:mm";
    //20130220

    public static final String NS_DAY_ALL_NUM = "yyyyMMdd";

    /**
     * 默认日期匹配格式
     */
    public static final String DATE_PATTERN = "\\d{4}\\-\\d{1,2}-\\d{1,2}";
    /**
     * 默认日期时间匹配格式
     */
    public static final String DATETIME_PATTERN = "\\d{4}\\-\\d{1,2}-\\d{1,2}\\p{javaWhitespace}\\d{1,2}:\\d{1,2}:\\d{1,2}";
    /**
     * 精确到分钟时间匹配格式
     */
    public static final String MINUTE_PATTERN = "\\d{4}\\-\\d{1,2}-\\d{1,2}\\p{javaWhitespace}\\d{1,2}:\\d{1,2}";

    public static void main(String[] args) {
        System.out.println(isExpire(parseDateTime("2017-07-20 00:10:00"),15));
    }

    public static Date parseNonStandardDate(String date) {
        try {
            if (date.contains("-")) {
                if (!date.contains(":"))
                    date = date + " 00:00:00";
                String[] size = date.split(":");
                if (size.length == 2)
                    date = date + ":00";
                return parseDate(date, DEFAULT_DATETIME_FORMAT);
            }
            if (date.contains("年") && date.contains("月") && date.contains("日") && date.contains(":"))
                return parseDate(date, NS_MINUTE2);
            if (date.contains("年") && date.contains("月") && date.contains("日"))
                return parseDate(date, NS_YEAR_MONTH_DAY);
            if (date.contains("年") && date.contains("月"))
                return parseDate(date, NS_YEAR_MONTH);
            if (date.contains("/")) {
                if (!date.contains(":"))
                    date = date + " 00:00:00";
                return parseDate(date, NS_TIME2);
            }
            if (date.contains(".") && date.contains(":")) {
                String[] size = date.split(":");
                if (size.length == 2)
                    date = date + ":00";
                return parseDate(date, NS_TIME);
            }


            if (date.length() == 8 && StringUtils.isNumeric(date)) {
                //20130220
                return parseDate(date, NS_DAY_ALL_NUM);
            }
        } catch (Exception e) {
            logger.error("格式化日期错误:" + date, e);
        }

        return null;
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static int compareDate(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return 0;
        } else if (date1 == null) {
            return -1;
        } else if (date2 == null) {
            return 1;
        } else {
            return date1.compareTo(date2);
        }
    }

    /**
     * 将yyyy-MM-dd格式的字符串转换为日期对象
     *
     * @param date yyyy-MM-dd格式字符串
     * @return 转换后的日期对象，无法转换时返回null
     */
    public static Date parseDate2YearMonthDay(String date) {
        if (!matchesPattern(date, DATE_PATTERN)) return null;
        return parseDate(date, YEAR_MONTH_DAY_FORMAT);
    }

    /**
     * 将yyyy-MM-dd格式的字符串转换为时间戳对象
     *
     * @param date yyyy-MM-dd格式字符串
     * @return 转换后的时间戳对象，无法转换时返回null
     */
    public static Timestamp parseDay2Timestamp(String date) {
        if (!matchesPattern(date, DATE_PATTERN)) return null;
        return new Timestamp(parseDate(date, YEAR_MONTH_DAY_FORMAT).getTime());
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss格式的字符串转换为日期对象
     *
     * @param datetime yyyy-MM-dd HH:mm:ss格式字符串
     * @return 转换后的日期对象，无法转换时返回null
     */
    public static Date parseDateTime(String datetime) {
        if (!matchesPattern(datetime, DATETIME_PATTERN)) return null;
        return parseDate(datetime, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss格式的字符串转换为时间戳期对象
     *
     * @param datetime yyyy-MM-dd HH:mm:ss格式字符串
     * @return 转换后的时间戳对象，无法转换时返回null
     */
    public static Timestamp parseTimeStamp(String datetime) {
        if (!matchesPattern(datetime, DATETIME_PATTERN)) return null;
        return new Timestamp(parseDate(datetime, DEFAULT_DATETIME_FORMAT).getTime());
    }

    /**
     * 将指定格式的字符串对象转换为日期对象
     *
     * @param date    字符串
     * @param pattern 指定的格式
     * @return 转换后的日期，无法转换时返回null
     */
    public static Date verifyAndGetDate(String date, String pattern) {
        return verifyAndGetDate(date, pattern, null);
    }

    /**
     * 将指定格式的字符串对象转换为日期对象
     *
     * @param date    字符串
     * @param pattern 指定的格式
     * @param defVal  默认返回值
     * @return 转换后的日期，无法转换时返回defVal指定值
     */
    public static Date verifyAndGetDate(String date, String pattern, Date defVal) {
        if (date == null || pattern == null) return null;
        if (date.endsWith("-00") || date.endsWith("-0")) return null;
        Date ret = parseDate(date, pattern);
        return (ret == null) ? defVal : ret;
    }

    /**
     * 根据指定的格式格式将传入字符串转化为日期对象
     *
     * @param date   传入字符串
     * @param format 指定格式
     * @return 格式化后日期对象
     */
    public static Date parseDate(String date, String format) {
        try {
            return DateTimeFormat.forPattern(format).parseLocalDateTime(date).toDate();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检测输入字符串是否与指定格式匹配
     *
     * @param input   待检测字符串
     * @param pattern 检测格式
     * @return <li>true：匹配</li>
     * <li>false：不匹配</li>
     */
    public static boolean matchesPattern(String input, String pattern) {
        return (input != null) && (input.matches(pattern));
    }

    /**
     * 将日期对象格式化成yyyy-mm-dd类型的字符串
     *
     * @param date 日期对象
     * @return 格式化后的字符串，无法格式化时，返回null
     */
    public static String formatDate(Date date) {
        return formatDateToString(date, YEAR_MONTH_DAY_FORMAT);
    }

    /**
     * 将日期对象格式化成HH:mm:ss类型的字符串
     *
     * @param date 日期对象
     * @return 格式化后的字符串，无法格式化时，返回null
     */
    public static String formatTimeOfDay(Date date) {
        return formatDateToString(date, TIME_FORMAT);
    }

    /**
     * 将日期对象格式化成yyyy-MM-dd HH:mm:ss类型的字符串
     *
     * @param date 日期对象
     * @return 格式化后的字符串，无法格式化时，返回null
     */
    public static String formatDateTime(Date date) {
        return formatDateToString(date, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * 将日期对象格式化成yyyy-MM
     *
     * @param date 日期对象
     * @return 格式化后的字符串，无法格式化时，返回null
     */
    public static String formatDateYM(Date date) {
        return formatDateToString(date, YEAR_MONTH);
    }

    /**
     * 将日期对象格式化成yyyy-MM-dd HH:mm类型的字符串
     *
     * @param date 日期对象
     * @return 格式化后的字符串，无法格式化时，返回null
     */
    public static String formatMinuteDate(Date date) {
        return formatDateToString(date, MINUTE_FORMAT);
    }

    /**
     * 将日期对象格式化成指定的格式字符串
     *
     * @param date   日期对象
     * @param format 格式
     * @return 格式化后的字符串，无法格式化时，返回null
     */
    public static String formatDateToString(Date date, String format) {
        if (date == null || StringUtils.isBlank(format)) {
            return null;
        }
        try {
            return DateTimeFormat.forPattern(format).print(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 取得指定日期所在月的最后一天日期对象
     *
     * @param d 指定日期
     * @return 指定日期当月的最后一天日期对象，如指定日期为null时，返回当前月的最后一天日期对象
     */
    public static Date getLastDayObjectInMonth(Date d) {
        return new DateTime(d.getTime()).dayOfMonth().withMaximumValue().toDate();
    }

    /**
     * 取得指定日期所在月的最后一天日期值
     *
     * @param d 指定日期
     * @return 当月的最后一天日期值，如指定日期为null时，返回当前月的最后一天日期值
     * @see #getLastDayObjectInMonth(Date)
     */
    public static int getLastDayInMonth(Date d) {
        return new DateTime(d.getTime()).dayOfMonth().getMaximumValue();
    }


    /**
     * yyyy年MM月dd日格式转 yyyy-MM-dd HH:mm:ss
     *
     * @param dateTime
     * @return
     */
    public static String getDateStr(String dateTime) {
        String ret = null;
        ret = formatDateTime(parseNonStandardDate(dateTime));
        return ret == null ? "入网时间转换失败" : ret;
    }

    public static String changeFormat(String dateTime, String srcFormat, String targetFormat) {
        try {
            return DateTimeFormat.forPattern(srcFormat).parseLocalTime(dateTime).toString(targetFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * yyyy年MM月dd日格式转 yyyy-MM-dd HH:mm:ss  (当且仅当没有时分秒的时候补充时分秒)
     *
     * @param dateTime
     * @return
     */
    public static String getDateStrHMS(String dateTime) {
        if (!dateTime.contains(":")) {
            return getDateStr(dateTime);
        }
        if (dateTime.contains("/")) {
            return changeFormat(dateTime, NS_TIME2, DEFAULT_DATETIME_FORMAT);
        }
        if (dateTime.contains("年")
                && dateTime.contains("-")) {
            dateTime = dateTime.replace("年", "-");
        }
        return dateTime;
    }

    /**
     * yyyy.MM.dd HH:mm格式转 yyyy-MM-dd HH:mm:ss
     *
     * @param dateTime
     * @return
     */
    public static String formatDateMinute(String dateTime) {
        String ret = changeFormat(dateTime, NS_MINUTE, DEFAULT_DATETIME_FORMAT);
        return ret == null ? formatDate(new Date()) : ret;
    }

    /**
     * yyyy-MM-dd HH:mm 或 yyyy.MM.dd HH:mm
     * 格式转 yyyy-MM-dd HH:mm:ss
     *
     * @param dateTime
     * @return
     */
    public static String getDateMinStr(String dateTime) {
        String ret = null;
        if (dateTime.contains("-")) {
            ret = changeFormat(dateTime, MINUTE_FORMAT, DEFAULT_DATETIME_FORMAT);
        } else {
            ret = changeFormat(dateTime, NS_MINUTE, DEFAULT_DATETIME_FORMAT);
        }
        return ret == null ? formatDate(new Date()) : ret;
    }

    public static String formatFeedDate(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        long day1 = timestamp.getTime() / 1000 / 86400;
        long day2 = System.currentTimeMillis() / 1000 / 86400;
        String ret = formatDateToString(timestamp, NS_MINUTE2);
        if (day1 == day2) {
            return "今天" + ret.substring(11, ret.length());
        }
        return ret;
    }

    //将以秒为单位的整数转换为×小时×分×秒 [TY insert 2007-03-22]
    public static String transferSec2Hms(int second) {
        int hour = second / 3600;
        int minute = (second % 3600) / 60;
        int sec = (second % 3600) % 60;
        return String.format("%s小时%s分%s秒", hour, minute, sec);
    }

    public static long getDateDiffNum(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        long days = diff / (24 * 60 * 60 * 1000);
        return days;
    }

    /**
     * 两个日期比较
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static int compareDate(String DATE1, String DATE2) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt1 = df.parse(DATE1);
        Date dt2 = df.parse(DATE2);
        if (dt1.getTime() > dt2.getTime()) {
            return 1;
        } else if (dt1.getTime() < dt2.getTime()) {
            return -1;
        } else {
            return 0;
        }

    }

    /**
     * 获取当前时间yyyy-MM-dd HH:mm:ss
     *
     * @return
     * @throws Exception
     */
    public static String getNow() throws Exception {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());
    }

    /**
     * 当前时间与给定日期的相差日 与给定限制做对比
     * 0两者差值小于限定
     * 1两者差值相等
     * 2两者差值大于限定
     * @param date
     * @param limit
     * @return
     */
    public static int isExpire(Date date, int limit) {
        DateTime dt1 = new DateTime(formatDate(date));
        DateTime dt2 = new DateTime(formatDate(new Date()));
        int between = Days.daysBetween(dt1, dt2).getDays();
        if (between < limit) {
            return 0;
        } else if (between == limit) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * 获取昨天时间yyyy-MM-dd HH:mm:ss
     *
     * @return
     * @throws Exception
     */
    public static String getYesterday(String dateFormat){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1); //得到前一天
        Date date = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(date);
    }

    /**
     * 获取今日时间
     * @param dateFormat
     * @return
     */
    public static String getToday(String dateFormat) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(new Date());
    }

    /**
     * 获取指定天数
     * @param days  相对于当前的天数
     * @param dateFormat    时间格式化
     * @return
     */
    public static String getAppointDay(int days, String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        Date date = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(date);
    }
}
