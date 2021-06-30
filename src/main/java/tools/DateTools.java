/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author alilo
 */
public class DateTools {
    public static final Date TODAY = new Date();
    public static DateFormat SHORT_DATE = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRENCH);
    public static DateFormat SHORT_TIME = new SimpleDateFormat("HH:mm");
    
    public static long MAX_TIME = 253402124400000L; // Thu Dec 30 23:00:00 WET 9999 GMT+1
    
    public static Date getMaxJavaDate(){
        return new Date(MAX_TIME);
    }
    
    public static Date getMinJavaDate(){
        return new Date(0);
    }
    
    public static boolean isMaxJavaTime(Date date){
        return date.getTime() >= MAX_TIME;
    }
    
    public static java.sql.Date getMaxSqlDate(){
        return new java.sql.Date(MAX_TIME);
    }
    
    public static java.sql.Date getMinSqlDate(){
        return new java.sql.Date(0);
    }
    
    public static boolean isMaxSqlTime(java.sql.Date date){
        return date.getTime() >= MAX_TIME;
    }
    
    public static java.sql.Date getSqlDate(Date javaDate){
        return new java.sql.Date(javaDate.getTime());
    }
    
    public static java.sql.Time getSqlTime(Date javaDate){
        return new java.sql.Time(javaDate.getTime());
    }
    
    public static String format(Date date, DateFormat format){
        return format.format(date);
    }
    
    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.set(9999, 11, 3, 0, 0, 0);
        System.out.println("date : "+ c.getTime()+ "  time: "+ c.getTime().getTime());
        System.out.println("is Max date:"+ DateTools.isMaxJavaTime(c.getTime()));
        //c = Calendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        c.set(9999, 11, 31, 0, 0, 0);
        System.out.println("date : "+ c.getTime()+ "  time: "+ c.getTime().getTime());
        System.out.println("is Max date:"+ DateTools.isMaxJavaTime(c.getTime()));
        
        //System.out.println("is max sql date:"+ DateTools.isMaxSqlTime(DateTools.getMaxSqlDate()));
        
        System.out.println("get max java date:"+ DateTools.getMaxJavaDate());
        System.out.println("get max sql date:"+ DateTools.getMaxSqlDate());
        
    }
}
