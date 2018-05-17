package com.example.dell.dkcwallet.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author yiyang
 */
public class TimeUtils {
    public static String getTimeStamp(){
        //return ""+System.currentTimeMillis();
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        //System.setProperty("user.timezone", "GMT");//设置时区
        //System.setProperty("user.timezone", "Asia/Shanghai");//设置时区
        //long start = System.currentTimeMillis();
        int cha = DateTool.getDiffTimeZoneRawOffset("GMT");
        Log.i("cha",cha+"");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.i("a1",sdf.format(calendar.getTime()));
        String rt = null;
        try {
            rt = sdf.parse(sdf.format(calendar.getTime())).getTime()+cha+"";
            Log.i("a2",rt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return rt;
    }

    /**
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    /**
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s){
        String res = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
            res = date.getTime()+"";
            Log.i("dcz2:",res);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }
        public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
            }
          SimpleDateFormat sdf = new SimpleDateFormat(format);
          return sdf.format(new Date(Long.valueOf(seconds+"000")));
        }
    public static String stampToDay(long l){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date(l));
    }

    public static String stampToDate(long l){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(l));
    }

    public static String getToday() {
        return getDay(new Date());
    }
    /**
     * 获得对应的年月日
     */
    public static String getDay(Date date) {
        String dayStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return dayStr;
    }
}
