package com.example.dell.dkcwallet.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author yiyang
 */
public class TimeUtils {
    public static String getTimeStamp(){
        return ""+System.currentTimeMillis();
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
