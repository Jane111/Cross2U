package com.cross2u.indent.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 日期格式转换工具类
 *
 * @date 2015年5月19日
 * @email fenghao2015@hotmail.com
 */
public class TimeUtil {

    /**
     * 获取详细信息：年-月-日 时:分:秒 星期几
     *
     * @param dt
     * @return
     */
    public static String toLongDateString(Date dt) {
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E ");
        return myFmt.format(dt);
    }

    /**
     * 获取格式：年月日时分秒14位
     *
     * @param dt
     * @return
     */
    public static String toSpecificDateString(Date dt) {
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMddHHmmss");
        return myFmt.format(dt);
    }

    /**
     * 获取日期：yy-MM-dd HH:mm:ss
     *
     * @param dt
     * @return
     */
    public static String toShortDateString(Date dt) {
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return myFmt.format(dt);
    }

    /**
     * 获取具体时间：时分秒毫秒
     *
     * @param dt
     * @return
     */
    public static String toLongTimeString(Date dt) {
        SimpleDateFormat myFmt = new SimpleDateFormat("HH:mm:ss:SSSS");
        return myFmt.format(dt);
    }

    /**
     * 获取时间：年/月/日 时:分
     *
     * @param dt
     * @return
     */
    public static String toShortTimeString(Date dt) {
        SimpleDateFormat myFmt = new SimpleDateFormat("yy/MM/dd HH:mm");
        return myFmt.format(dt);
    }

    /**
     * 仅获取年月日
     *
     * @param dt
     * @return
     */
    public static String toOnlyDateString(Date dt) {
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
        return myFmt.format(dt);
    }

    /**
     * 仅获取星期几
     *
     * @param dt
     * @return
     */
    public static String toOnlyWeekString(Date dt) {
        SimpleDateFormat myFmt = new SimpleDateFormat("E");
        return myFmt.format(dt);
    }

    /**
     * 将字符串转换为日期格式：yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String stringToDataFormat(String date) {
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return myFmt.format(date);
    }

    public static Date stringToDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date stringToDate(String date, String sformat) {
        SimpleDateFormat format = new SimpleDateFormat(sformat);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 日期加一天
     *
     * @param date
     * @return
     */
    public static String dateAddOneDay(String date) {
        Date d = stringToDate(date, "yyyy-MM-dd");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(d);
        calendar.add(Calendar.DAY_OF_MONTH, 1);// 把日期往后增加一天.整数往后推,负数往前移动
        return toOnlyDateString(calendar.getTime());
    }

    /**
     * 日期加n天
     *
     * @param date
     * @return
     */
    public static String dateAddNDay(String date,Integer days) {
        Date d = stringToDate(date, "yyyy-MM-dd");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(d);
        calendar.add(Calendar.DAY_OF_MONTH, days);// 把日期往后增加n天.整数往后推,负数往前移动
        return toOnlyDateString(calendar.getTime());
    }

    /**
     * 将字符串日期转换成为oracle能够存储的日期
     *
     * @param date
     * @param sformat
     * @return
     */
    public static Timestamp toSqlDateOfOracle(String date, String sformat) {
        return new Timestamp(TimeUtil.stringToDate(date, sformat).getTime());
    }

    /**
     * 将日期转换成数据库可以存储的时间类型
     * @param date
     * @return
     */
    public static Timestamp dateToTimestamp(Date date)
    {
        return new Timestamp(new Date().getTime());
    }

    public static void main(String[] args) {

        Date now = new Date();
        System.out.println(now);
        System.out.println(TimeUtil.dateAddOneDay(toOnlyDateString(now)));
    }

    /**
     * 获取上课的时间
     *
     * @return
     * @throws ParseException
     * */
    public static Date getTermStartTime(int id) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Record rec = Db.findById("termstarttime", id);
        String time =  sdf.format((Date)rec.get("time"));
        Date date = sdf.parse(time);
        return date;
    }

    /**
     * 将Date类型数据按照一定格式转换成Date类型数据
     *
     * @param date
     * @param timeFormat
     * @return
     * @throws ParseException
     */
    public static Date parseToDate(Date date, String timeFormat)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        return sdf.parse(sdf.format(date));
    }

    /**
     * 根据自定义的时间格式将时间转换成字符串输出
     *
     * @param date
     * @param timeFormat
     * @return
     */
    public static String DateToString(Date date, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(date);
    }


}
