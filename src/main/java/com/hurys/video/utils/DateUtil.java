package com.hurys.video.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author YYK
 * @version 1.0
 * @date 2020/5/22 10:18
 * @Description: 时间转换工具
 */
@Component
@Log4j2
public class DateUtil {

    private static final String  FORMATTYPE1 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 自定义格式时间转换
     * @param formatType
     * @return
     */
    public String getNowTime(String formatType) {
        String result = "";
        try {
            //y 代表年
            //M 代表月
            //d 代表日
            //H 代表24进制的小时
            //h 代表12进制的小时
            //m 代表分钟
            //s 代表秒
            //S 代表毫秒
            SimpleDateFormat sdf =new SimpleDateFormat(formatType );
            Date d= new Date();
            result = sdf.format(d);
        }catch (Exception e){
            e.printStackTrace();
            log.error("时间转换失败！" + e.getMessage());
        }
        return result;
    }

    /***
     * 获取当前时间（默认格式 "yyyy-MM-dd HH:mm:ss"）
     * @return
     */
    public static String getNowTime() {
        //y 代表年
        //M 代表月
        //d 代表日
        //H 代表24进制的小时
        //h 代表12进制的小时
        //m 代表分钟
        //s 代表秒
        //S 代表毫秒
        SimpleDateFormat sdf =new SimpleDateFormat(FORMATTYPE1);
        Date d= new Date();
        return sdf.format(d);
    }

    /**
     * 时间相减
     * @param time1
     * @param time2
     * @return
     */
    public static JSONObject DateDifferent(Date time1, Date time2) {
        JSONObject result = new JSONObject();
        try {
            //入参时间
            Calendar calendar1 = Calendar.getInstance();
            //开始时间
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTime(time1);
            calendar2.setTime(time2);
            long timeInMillis1 = calendar1.getTimeInMillis();
            long timeInMillis2 = calendar2.getTimeInMillis();
            long diff = timeInMillis1 - timeInMillis2;
            Date diffDate = new Date(diff);
            long day = diff/(24*60*60*1000);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(day);
            stringBuffer.append("天 ");
            SimpleDateFormat formatter =new SimpleDateFormat("HH小时 mm分 ss秒" );
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            stringBuffer.append(formatter.format(diffDate));
            result.put("Time",stringBuffer);
            result.put("TimeNum",diff);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 格式化时间范围
     * @param params
     * @return
     */
    public static String formatRangeTime(long params){
        String result = "";
        try {
            Date date = new Date(params);
            long day = params/(24*60*60*1000);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(day);
            stringBuffer.append("天 ");
            SimpleDateFormat formatter =new SimpleDateFormat("HH小时 mm分 ss秒" );
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            stringBuffer.append(formatter.format(date));
            result = stringBuffer.toString();
        }catch (Exception e){
            log.error("格式化时间范围失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     * @param strDate
     * @return
     */
   public static Date getDataTime(String strDate){
       Date date = null;
       try {
           SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           ParsePosition pos = new ParsePosition(0);
           date = formatter.parse(strDate, pos);
       }catch (Exception e){
           e.printStackTrace();
       }
        return date;
   }

    /**
     * 时间比较，入参时间和当前时间比较
     * @param time
     * @param min
     * @return
     */
    public static boolean compareTimeToNow(Date time,int min){
        boolean flag = false;
        try {
            //开始时间
            Calendar nowTime = Calendar.getInstance();
            nowTime.setTime(new Date());
            //比较时间
            Calendar compareTime = Calendar.getInstance();
            compareTime.setTime(time);
            long timeInMillis1 = nowTime.getTimeInMillis();
            long timeInMillis2 = compareTime.getTimeInMillis();
            long diff = timeInMillis1 - timeInMillis2;
            return  diff / (60 * 1000) > min;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 时间相减
     * @param time1
     * @param time2
     * @return
     */
    public static long DateDifferent(String time1, String time2) {
        long result = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMATTYPE1);
        try {
            Date date1 = simpleDateFormat.parse(time1);
            Date date2 = simpleDateFormat.parse(time2);
            //入参时间
            Calendar calendar1 = Calendar.getInstance();
            //开始时间
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTime(date1);
            calendar2.setTime(date2);
            long timeInMillis1 = calendar1.getTimeInMillis();
            long timeInMillis2 = calendar2.getTimeInMillis();
            long diff = timeInMillis1 - timeInMillis2;
            result = diff/(60*1000);
        }catch (Exception e){
            log.error("获取时间差失败：" + e.getMessage());
        }
        return result;
    }


    public static void main(String args[]){
        Date dataTime1 = getDataTime("2021-01-22 12:16:00");
        Date dataTime2 = getDataTime("2021-01-22 10:16:00");
        JSONObject jsonObject = DateDifferent(dataTime1, dataTime2);
    }
}
