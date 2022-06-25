package com.kjy.care.widget;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 康明 2014-9-29- 16:15
 *
 * @author Administrator
 */
public class DateTimeUtil {

    ///////////////////////////获取当前时间/////////////////////////////

    /**
     * 获取当前时间   yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static Timestamp getSQLTime() {
        SimpleDateFormat formatter_f = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date currentTime_f = new Date();
        Timestamp      new_date_f    = Timestamp.valueOf(formatter_f.format(currentTime_f));
        return new_date_f;
    }

    ///////////////////////////获取当前时间/////////////////////////////

    /**
     * 格式化时间 yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static Timestamp PartSQLTime(String time) {
        SimpleDateFormat formatter_f = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date currentTime_f = new Date();
        try {
            currentTime_f = formatter_f.parse(time);

        } catch (Exception e) {
            System.out.println("DateTimeUtil->PartSQLTime:" + e.getMessage());
            //ExceptionLog.Write("DateTimeUtil->PartSQLTime", e.getMessage());
        }
        Timestamp new_date_f = Timestamp.valueOf(formatter_f.format(currentTime_f));
        return new_date_f;
    }


///////////////////////////获取当前时间/////////////////////////////

    /**
     * 格式化时间 yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static Date PartDateTime(String time) {
        SimpleDateFormat formatter_f = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date currentTime_f = new Date();
        try {
            currentTime_f = formatter_f.parse(time);

        } catch (Exception e) {
            System.out.println("DateTimeUtil->PartSQLTime:" + e.getMessage());
//ExceptionLog.Write("DateTimeUtil->PartSQLTime", e.getMessage());
        }

        return currentTime_f;
    }


///////////////////////////获取当前时间///转化为 字符串//////////////////////////

    /**
     * 将时间转换成字符串 yyyyMMddHHmmssSSS
     *
     * @return
     */
    public static String getdateRenameFileName() {
        SimpleDateFormat formatter_f = new SimpleDateFormat(
                "yyyyMMddHHmmssSSS");
        Date currentTime_f = new Date(); // 得到当前系统时间
        String         new_date_f    = formatter_f.format(currentTime_f); // 将日期时间格式化
        return new_date_f;
    }


///////////////////////////获取当前时间///转化为 字符串//////////////////////////

    /**
     * 将时间转换成短字符串 yyyyMMdd
     *
     * @return
     */
    public static String getShortdateRenameFileName() {
        SimpleDateFormat formatter_f = new SimpleDateFormat(
                "yyyyMMdd");
        Date currentTime_f = new Date(); // 得到当前系统时间
        String         new_date_f    = formatter_f.format(currentTime_f); // 将日期时间格式化
        return new_date_f;
    }


    //2014/10/13 09:39 肖月 更新内容:查询健康报告历史记录时间
    public static String getShortdateFileName(Date date) {
        SimpleDateFormat formatter_f = new SimpleDateFormat(
                "yyyyMMdd");
        String new_date_f = formatter_f.format(date); // 将日期时间格式化
        return new_date_f;
    }


    /**
     * 获取 增加或减少天数 后的日期  yyyy-MM-dd
     * @param date
     * @param day
     * @return
     */
    public static String GetPreDate(Date date, int day) {
        // Date date = new Date();
        // System.out.println((new SimpleDateFormat("yyyy-MM-dd")).format(date));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        // System.out.println((new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime()));
        //  return cal.getTime();
        return (new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime());

    }

    /**
     * 获取 增加或减少天数 后的日期
     * @param date
     * @param day
     * @return
     */
    public static Date getDate(Date date, int day) {
        Date     res = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        res = cal.getTime();
        return res;
    }

    /**
     * 获取当前日期，格式化  yyyy-MM-dd
     *
     * @return
     */
    public static String getNowDate() {
        Date date = new Date();
        return (new SimpleDateFormat("yyyy-MM-dd")).format(date);
    }

    /**
     * 获取当前日期，格式化  yyyy年MM月dd日
     *
     * @return
     */
    public static String getChinaNowDate() {
        Date date = new Date();
        return (new SimpleDateFormat("yyyy年MM月dd日")).format(date);
    }


    //201410/13 09:56 肖月 更新内容：查询健康报告显示时间
    public static String getChinaHisDate(Date date) {
        return (new SimpleDateFormat("yyyy年MM月dd日")).format(date);
    }


    /**
     * 格式化时间，格式化 yyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String partDate(Date date) {

        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
    }


    /**
     * 检查字符串是否满足 格式   yyyy-MM-dd HH:mm:ss
     *
     * @param checkValue
     * @return
     */
    public static boolean checkDate(String checkValue) {
        // String checkValue = "2007-02-29";
        String eL = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
        //  Pattern p = Pattern.compile(eL);
        // Matcher m = p.matcher(checkValue);
        //boolean flag = m.matches(); 
        boolean flag = false;
        flag = checkValue.matches(eL);
        return flag;
    }

    /**
     * 判断是否整数
     *
     * @param checkValue
     * @return
     */
    public static boolean checkint(String checkValue) {
        // String checkValue = "2007-02-29";
        String eL = "^-?[1-9]\\d*$";

        // Pattern p = Pattern.compile(eL);
        // Matcher m = p.matcher(checkValue);
        // boolean flag = m.matches();
        boolean flag = false;
        flag = checkValue.matches(eL);
        return flag;
    }


    /**
     * 计算年龄
     *
     * @param date
     * @return
     */
    public static int calcAge(String date) {
        int age = 0;

        //把 S 参数 的 年月日 分组
//        String date = DateTimeUtil.PartSQLTime(s).toString();
        date = date.substring(0, 10);
        String[] datelist = date.split("-");

        //把 当前 时间 年月日  分组
        String now = DateTimeUtil.getSQLTime().toString();
        now = now.substring(0, 10);
        String[] nowlist = now.split("-");
        //根据年计算年龄
        age = Integer.parseInt(nowlist[0]) - Integer.parseInt(datelist[0]);


        //比较年
        if (Integer.parseInt(nowlist[0]) > Integer.parseInt(datelist[0])) {

            //月相等
            if (Integer.parseInt(nowlist[1]) == Integer.parseInt(datelist[1])) {
                //比较日
                if (Integer.parseInt(nowlist[2]) < Integer.parseInt(datelist[2])) {
                    age--;
                }

            } else {
                //比较月
                if (Integer.parseInt(nowlist[1]) < Integer.parseInt(datelist[1])) {
                    age--;
                }
            }
        }
        return age;
    }


    /**
     * 计算相差多少个月
     *
     * @param s 输入出生年月日
     * @return
     */
    public static int difMoth(String s) {
        int age = 0;
        int moth = 0;

        //把 S 参数 的 年月日 分组
        String date = DateTimeUtil.PartSQLTime(s).toString();
        date = date.substring(0, 10);
        String[] datelist = date.split("-");

        //把 当前 时间 年月日  分组
        String now = DateTimeUtil.getSQLTime().toString();
        now = now.substring(0, 10);
        String[] nowlist = now.split("-");
        //根据年计算年龄
        age = Integer.parseInt(nowlist[0]) - Integer.parseInt(datelist[0]);


        //比较年
        if (Integer.parseInt(nowlist[0]) > Integer.parseInt(datelist[0])) {

            //月相等
            if (Integer.parseInt(nowlist[1]) == Integer.parseInt(datelist[1])) {
                //比较日
                if (Integer.parseInt(nowlist[2]) < Integer.parseInt(datelist[2])) {

                    age--;
                }

            } else {
                //比较月
                if (Integer.parseInt(nowlist[1]) < Integer.parseInt(datelist[1])) {

                    age--;
                }


            }
        }


        if (Integer.parseInt(nowlist[1]) < Integer.parseInt(datelist[1])) {

            moth = 12 + Integer.parseInt(nowlist[1]) - Integer.parseInt(datelist[1]);
        } else {
            moth = Integer.parseInt(nowlist[1]) - Integer.parseInt(datelist[1]);
        }


        moth = age * 12 + moth;


        return moth;
    }


    /**
     * 通过 日期获取周几 中文
     * @param date
     * @return
     */
    public static String getDay(Date date) {
        // 再转换为时间

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推

        String str = "";
        str = new SimpleDateFormat("EEEE").format(c.getTime());
        if ("1".equals(str)) {
            str = "星期日";
        } else if ("2".equals(str)) {
            str = "星期一";
        } else if ("3".equals(str)) {
            str = "星期二";
        } else if ("4".equals(str)) {
            str = "星期三";
        } else if ("5".equals(str)) {
            str = "星期四";
        } else if ("6".equals(str)) {
            str = "星期五";
        } else if ("7".equals(str)) {
            str = "星期六";
        }
        return str;
    }

    /**
     * 通过 日期获取周几 中文
     * @param date
     * @return
     */
    public static String getWeek(Date date) {
        // 再转换为时间

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推

        String str = "";
        str = new SimpleDateFormat("EEEE").format(c.getTime());
        if ("1".equals(str)) {
            str = "星期日";
        } else if ("2".equals(str)) {
            str = "星期一";
        } else if ("3".equals(str)) {
            str = "星期二";
        } else if ("4".equals(str)) {
            str = "星期三";
        } else if ("5".equals(str)) {
            str = "星期四";
        } else if ("6".equals(str)) {
            str = "星期五";
        } else if ("7".equals(str)) {
            str = "星期六";
        }
        return str;
    }


    public static int getWeekNumber(Date date) {
        // 再转换为时间
        int      result = -1;
        Calendar c      = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推

        String str = "";
        str = new SimpleDateFormat("EEEE").format(c.getTime());
        if ("星期日".equals(str)) {
            result = 1;
        } else if ("星期一".equals(str)) {
            result = 2;
        } else if ("星期二".equals(str)) {
            result = 3;
        } else if ("星期三".equals(str)) {
            result = 4;
        } else if ("星期四".equals(str)) {
            result = 5;
        } else if ("星期五".equals(str)) {
            result = 6;
        } else if ("星期六".equals(str)) {
            result = 7;
        }
        return result;
    }

    /**
     * 2.4G 智能手表日期
     *
     * @param date
     * @return
     */
    public static String getWacthWeekNumber(Date date) {
        // 再转换为时间
        int      result = -1;
        Calendar c      = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推

        String str = "";
        str = new SimpleDateFormat("EEEE").format(c.getTime());
        if ("星期一".equals(str)) {
            result = 1;
        } else if ("星期二".equals(str)) {
            result = 2;
        } else if ("星期三".equals(str)) {
            result = 3;
        } else if ("星期四".equals(str)) {
            result = 4;
        } else if ("星期五".equals(str)) {
            result = 5;
        } else if ("星期六".equals(str)) {
            result = 6;
        } else if ("星期日".equals(str)) {
            result = 7;
        }
        return "0" + result;
    }

    /**
     * 获得当前系统时间  hh:mm:ss
     *
     * @param
     * @return
     */
    public static String getCurrentTime() {
        String time    = "";
        long   sysTime = System.currentTimeMillis();
        /*CharSequence sysTimeStr = DateFormat.format("hh:mm:ss", sysTime);
		time = sysTimeStr.toString();*/

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        time = sdf.format(new Date());
        return time;
    }

    /**
     * 通过日期获取第几周 中文
     * @param date  日期
     * @return
     */
    public static String getWeekByDate( Date date) {
        if(date == null){return "";}
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);


        String weekOfMonth = "";
        //Calendar calendar= Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        //获取当前时间为本月的第几周
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        //获取当前时间为本周的第几天
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 7;
            week = week - 1;
        } else {
            day = day - 1;
        }
        switch (week) {
            case 1:
                weekOfMonth = "一";
                break;
            case 2:
                weekOfMonth = "二";
                break;
            case 3:
                weekOfMonth = "三";
                break;
            case 4:
                weekOfMonth = "四";
                break;
            case 5:
                weekOfMonth = "五";
                break;
        }
        String mt = "";
        String dm = "";
        if (month < 10) {
            mt = "0" + month;
        } else {
            mt = month + "";
        }

        if (dayOfMonth < 10) {
            dm = "0" + dayOfMonth;
        } else {
            dm = dayOfMonth + "";
        }

        return  "第" + weekOfMonth + "周";
    }

    public static String getWeek(int num) {
        Date     date     = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, num * 7);
        Date   date1       = calendar.getTime();
        String weekOfMonth = "";
        //Calendar calendar= Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        //获取当前时间为本月的第几周 
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        //获取当前时间为本周的第几天
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 7;
            week = week - 1;
        } else {
            day = day - 1;
        }
        switch (week) {
            case 1:
                weekOfMonth = "一";
                break;
            case 2:
                weekOfMonth = "二";
                break;
            case 3:
                weekOfMonth = "三";
                break;
            case 4:
                weekOfMonth = "四";
                break;
            case 5:
                weekOfMonth = "五";
                break;
        }
        String mt = "";
        String dm = "";
        if (month < 10) {
            mt = "0" + month;
        } else {
            mt = month + "";
        }

        if (dayOfMonth < 10) {
            dm = "0" + dayOfMonth;
        } else {
            dm = dayOfMonth + "";
        }

        return year + "-" + mt + "-" + dm + " 第" + weekOfMonth + "周";
    }

    public static String getWeekAndDate(int num) {
        Date     date     = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, num * 7);
        Date   date1       = calendar.getTime();
        String weekOfMonth = "";
        //Calendar calendar= Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        //获取当前时间为本月的第几周 
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        //获取当前时间为本周的第几天
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 7;
            week = week - 1;
        } else {
            day = day - 1;
        }
        switch (week) {
            case 1:
                weekOfMonth = "一";
                break;
            case 2:
                weekOfMonth = "二";
                break;
            case 3:
                weekOfMonth = "三";
                break;
            case 4:
                weekOfMonth = "四";
                break;
            case 5:
                weekOfMonth = "五";
                break;
        }
        String mt = "";
        String dm = "";
        if (month < 10) {
            mt = "0" + month;
        } else {
            mt = month + "";
        }

        if (dayOfMonth < 10) {
            dm = "0" + dayOfMonth;
        } else {
            dm = dayOfMonth + "";
        }

        return year + "-" + mt + "-" + dm + " " + getWeek(date1) + " 第" + weekOfMonth + "周";
    }

    public static String getWeekAndDate(int num, String dateString) {
        Date     date     = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, num * 7);
        Date   date1       = calendar.getTime();
        String weekOfMonth = "";
        //Calendar calendar= Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        //获取当前时间为本月的第几周
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        //获取当前时间为本周的第几天
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 7;
            week = week - 1;
        } else {
            day = day - 1;
        }
        switch (week) {
            case 1:
                weekOfMonth = "一";
                break;
            case 2:
                weekOfMonth = "二";
                break;
            case 3:
                weekOfMonth = "三";
                break;
            case 4:
                weekOfMonth = "四";
                break;
            case 5:
                weekOfMonth = "五";
                break;
        }
        String mt = "";
        String dm = "";
        if (month < 10) {
            mt = "0" + month;
        } else {
            mt = month + "";
        }

        if (dayOfMonth < 10) {
            dm = "0" + dayOfMonth;
        } else {
            dm = dayOfMonth + "";
        }

        return dateString + " "+ getWeek(date1) + " 第" + weekOfMonth + "周";
    }

    public static String getWeekAndDay(int num) {
        Date     date     = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, num);
        Date   date1       = calendar.getTime();
        String weekOfMonth = "";
        //Calendar calendar= Calendar.getInstance();
        int    year   = calendar.get(Calendar.YEAR);
        int    month1 = calendar.get(Calendar.MONTH) + 1;
        String month  = "";
        if (month1 < 10) {
            month = "0" + month1;
        } else {
            month = "" + month1;
        }
        int    dayOfMonth1 = calendar.get(Calendar.DAY_OF_MONTH);
        String dayOfMonth  = "";
        if (dayOfMonth1 < 10) {
            dayOfMonth = "0" + dayOfMonth1;
        } else {
            dayOfMonth = "" + dayOfMonth1;
        }
        //获取当前时间为本月的第几周 
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        //获取当前时间为本周的第几天
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 7;
            //week=week-1; 
        } else {
            day = day - 1;
        }
        switch (week) {
            case 1:
                weekOfMonth = "一";
                break;
            case 2:
                weekOfMonth = "二";
                break;
            case 3:
                weekOfMonth = "三";
                break;
            case 4:
                weekOfMonth = "四";
                break;
            case 5:
                weekOfMonth = "五";
                break;
        }
        return year + "/" + month + "/" + dayOfMonth + " " + getWeek(date1) + " 第" + weekOfMonth + "周";
    }

    public static String getWeekNoDay(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, num);
        Date   date1       = calendar.getTime();
        String weekOfMonth = "";
        //Calendar calendar= Calendar.getInstance();
        int    year   = calendar.get(Calendar.YEAR);
        int    month1 = calendar.get(Calendar.MONTH) + 1;
        String month  = "";
        if (month1 < 10) {
            month = "0" + month1;
        } else {
            month = "" + month1;
        }
        int    dayOfMonth1 = calendar.get(Calendar.DAY_OF_MONTH);
        String dayOfMonth  = "";
        if (dayOfMonth1 < 10) {
            dayOfMonth = "0" + dayOfMonth1;
        } else {
            dayOfMonth = "" + dayOfMonth1;
        }
        //获取当前时间为本月的第几周 
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        //获取当前时间为本周的第几天
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 7;
            //week=week-1; 
        } else {
            day = day - 1;
        }
        switch (week) {
            case 1:
                weekOfMonth = "一";
                break;
            case 2:
                weekOfMonth = "二";
                break;
            case 3:
                weekOfMonth = "三";
                break;
            case 4:
                weekOfMonth = "四";
                break;
            case 5:
                weekOfMonth = "五";
                break;
        }
        return year + "/" + month + "/" + dayOfMonth + " " + getWeek(date1);
    }

    public static boolean isWeekSame(Date date, Date standar) {
        boolean  res       = false;
        Calendar calendar  = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar.setTime(date);
        calendar1.setTime(standar);
        int week1 = calendar.get(Calendar.WEEK_OF_MONTH);
        int week2 = calendar1.get(Calendar.WEEK_OF_MONTH);
        if (week1 == week2) {
            res = true;
        }
        return res;
    }


    /**
     * 判断两个日期是否属于同一周
     * standar 通过此值获取该周的开始和结束日期
     * date 需要判断的日期
     */
    public static boolean compareDate(String standar, String date) {
        boolean  res         = false;
        Date     dateStandar = getDate(standar);    //当前显示在界面的日期
        Calendar c           = Calendar.getInstance();
        c.setTime(dateStandar);

        int count = c.get(Calendar.DAY_OF_WEEK);
        int count_before = count * (-1) + 1;
        int count_after = 7 - count;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStandar);
        calendar.add(Calendar.DATE, count_before);
        Date date1 = calendar.getTime();

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(dateStandar);
        calendar1.add(Calendar.DATE, count_after);
        Date date2 = calendar1.getTime();

        Date datex = getDate(date);    //传入日期

        if (compare_date(PartDate1(datex), PartDate1(date1)) >= 0 && compare_date(PartDate1(datex), PartDate1(date2)) <= 0) {
            res = true;
        }
        return res;
    }

    /**
     * standar 通过此值获取该周的开始和结束日期
     */
    public static String[] getFristEndDate(String standar) {
        String   res[]       = new String[]{"", ""};
        Date     dateStandar = getDate(standar);    //当前显示在界面的日期
        Calendar c           = Calendar.getInstance();
        c.setTime(dateStandar);

        int count = c.get(Calendar.DAY_OF_WEEK);
        int count_before = count * (-1) + 1;
        int count_after = 7 - count;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStandar);
        calendar.add(Calendar.DATE, count_before);
        Date date1 = calendar.getTime();

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(dateStandar);
        calendar1.add(Calendar.DATE, count_after);
        Date date2 = calendar1.getTime();

        res[0] = PartDate1(date1);
        res[1] = PartDate1(date2);
        return res;
    }

    /**
     * standar 通过此值获取该周的开始和结束日期
     */
    public static String[] getFristAfterEndDate(String standar) {
        String   res[]       = new String[]{"", ""};
        Date     dateStandar = getDate(standar);    //当前显示在界面的日期
        Calendar c           = Calendar.getInstance();
        c.setTime(dateStandar);

        int count = c.get(Calendar.DAY_OF_WEEK);
        int count_before = count * (-1) + 1;
        int count_after = 7 - count;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStandar);
        calendar.add(Calendar.DATE, count_before);
        Date date1 = calendar.getTime();

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(dateStandar);
        calendar1.add(Calendar.DATE, count_after + 1);
        Date date2 = calendar1.getTime();

        res[0] = PartDate1(date1);
        res[1] = PartDate1(date2);
        return res;
    }

    public static String PartDate1(Date date) {

        return (new SimpleDateFormat("yyyy-MM-dd")).format(date);
    }

    public static int compare_date(String DATE1, String DATE2) {


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static Date getDate(String time) {
        Date             date = null;
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDateWithDay(String time) {
        Date             date = null;
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 把时间转换为小时
     */
    public static String surplusToStrng(int between) {
        if (between == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        between = between * 60;
        long day1 = between / (24 * 3600);
        long hour1 = between % (24 * 3600) / 3600;
        long minute1 = between % 3600 / 60;
        long second1 = between % 60;
        if (day1 != 0) {
            sb.append(day1 + "天");
        }
        if (hour1 != 0) {
            sb.append(hour1 + "小时");
        }
        if (minute1 != 0) {
            sb.append(minute1 + "分");
        }
        return sb.toString();
    }

    //获取两个时间差，结果为分钟
    public static int getBetween(String dateStart, String dateStop) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date             d1     = null;
        Date             d2     = null;
        int              res    = -1;
        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //毫秒ms
            long diff = d2.getTime() - d1.getTime();
            long diffMinutes = diff / (60 * 1000);
            res = (int) diffMinutes;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }

    //获取两个时间差，结果为分钟
    public static int getBetween1(String dateStart, String dateStop) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date             d1     = null;
        Date             d2     = null;
        int              res    = -1;
        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //毫秒ms
            long diff = d2.getTime() - d1.getTime();
            long diffMinutes = diff / (60 * 1000);
            res = (int) diffMinutes;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }

    public static Calendar getCalendarTime(String str) {
        SimpleDateFormat sdf      = new SimpleDateFormat("yyyy-MM-dd");
        Calendar         calendar = null;
        try {
            Date date = sdf.parse(str);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return calendar;
    }

    public static String getCalendarTime(Calendar cd) {
        String           time = "";
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = sdf.format(cd.getTime());
        return time;
    }

    /**
     * date1 为基准时间，date2 为所需比对时间
     */
    public static int getWeekDuration(String date1, String date2) {
        int              duration  = 0;
        SimpleDateFormat sdf       = new SimpleDateFormat("yyyy-MM-dd");
        Calendar         calendarl = Calendar.getInstance();
        Calendar         calendar2 = Calendar.getInstance();
        try {
            calendarl.setTime(sdf.parse(date1));
            calendar2.setTime(sdf.parse(date2));
            int week1 = calendarl.get(Calendar.WEEK_OF_YEAR);
            int week2 = calendar2.get(Calendar.WEEK_OF_YEAR);
            duration = week2 - week1;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return duration;
    }

    public static boolean compare(String standar) {
        boolean res          = false;
        Date    date_standar = getDate(standar);
        Date    date         = new Date();
        if (date_standar.before(date)) {
            res = true;
        }
        return res;
    }


    /**
     * 字符串的日期格式的天数差值
     *
     * @param smdate 标准日期
     * @param bdate  比较日期
     * @return
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar         cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }


    /**
     * 得到一天是一年中的第几周
     *
     * @param date
     * @return
     */
    public static int getWeekFromYear(String date) {
        Calendar         cal    = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            cal.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        return week;
    }

    /***将字符串转为时间戳 */
    public static Date getStringToDate(String time) {
        SimpleDateFormat sf   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date             date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }
}
