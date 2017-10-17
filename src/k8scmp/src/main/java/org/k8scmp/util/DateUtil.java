package org.k8scmp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class DateUtil {

	public static String dateFormatToMillis(Date date){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");   
	    return sDateFormat.format(date);   
	}
    
	public static String dateFormatToMonth(Date date){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");   
	    return sDateFormat.format(date);   
	}
	
	public static String dateFormatToDay(Date date){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");   
	    return sDateFormat.format(date);   
	}
	
	public static String dateFormat(Date date){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");   
	    return sDateFormat.format(date);   
	}
	
	public static String stringToMillis(String timeString, TimeZone timeZone) throws ParseException {
		 if (timeString == null) {
	            return null;
	        }
	        int decimalIndex = timeString.indexOf(".") + 1;
	        String formatString;
	        if (decimalIndex > 0) {
	            formatString = timeString.substring(0, decimalIndex);
	            int cnt = 0;
	            while (decimalIndex < timeString.length() - 1 && cnt < 3) {
	                formatString += timeString.substring(decimalIndex, decimalIndex + 1);
	                decimalIndex++;
	                cnt++;
	            }
	            while (cnt < 3) {
	                formatString += "0";
	                cnt++;
	            }
	            formatString += "Z";
	        } else {
	            formatString = timeString.substring(0, timeString.length() - 1) + ".000Z";
	        }
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	        sdf.setTimeZone(timeZone);
	        Date date = sdf.parse(formatString);
	       return dateFormatToMillis(date);
	}
//	public static void main(String[] args) {
//		System.out.println(dateFormat(new Date()));
//	}
	
    // transformation for "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" default system timezone to Unix timestamp milliseconds
    public static long string2timestamp(String timeString) throws ParseException {
        int decimalIndex = timeString.indexOf(".") + 1;
        String formatString;
        if (decimalIndex > 0) {
            formatString = timeString.substring(0, decimalIndex);
            int cnt = 0;
            while (decimalIndex < timeString.length() - 1 && cnt < 3) {
                formatString += timeString.substring(decimalIndex, decimalIndex + 1);
                decimalIndex++;
                cnt++;
            }
            while (cnt < 3) {
                formatString += "0";
                cnt++;
            }
            formatString += "Z";
        } else {
            formatString = timeString.substring(0, timeString.length() - 1) + ".000Z";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = sdf.parse(formatString);
        return date.getTime();
    }

    // transformation for "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" specific timezone to Unix timestamp milliseconds
    public static long string2timestamp(String timeString, TimeZone timeZone) throws ParseException {
        if (timeString == null) {
            return 0;
        }
        int decimalIndex = timeString.indexOf(".") + 1;
        String formatString;
        if (decimalIndex > 0) {
            formatString = timeString.substring(0, decimalIndex);
            int cnt = 0;
            while (decimalIndex < timeString.length() - 1 && cnt < 3) {
                formatString += timeString.substring(decimalIndex, decimalIndex + 1);
                decimalIndex++;
                cnt++;
            }
            while (cnt < 3) {
                formatString += "0";
                cnt++;
            }
            formatString += "Z";
        } else {
            formatString = timeString.substring(0, timeString.length() - 1) + ".000Z";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(timeZone);
        Date date = sdf.parse(formatString);
        return date.getTime();
    }
}
