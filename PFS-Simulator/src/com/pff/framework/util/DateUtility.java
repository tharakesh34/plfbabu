package com.pff.framework.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Used to specify Date type selection in <b>DateUtility</b> class.
 *
 */
public final class DateUtility {


	/**
	 * Parsing Util Date in required Format
	 * 
	 * @param date (String)
	 *  
	 * @param format (String)
	 * 
	 * @return java.Util.Date
	 */
	public static java.util.Date getUtilDate(String date, String format){  

		SimpleDateFormat df = new SimpleDateFormat(format);
		java.util.Date uDate =null;
		try{
			uDate = df.parse(date);
		}
		catch(ParseException pe){
			pe.printStackTrace();
		}
		return uDate;
	}  

	/**
	 * Format Util Date in required Format
	 * 
	 * @param date (Date)
	 *  
	 * @param format (String)
	 * 
	 * @return String
	 */
	public static String formatDate(java.util.Date date, String format){
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(date) + "";
	}
	
	public static java.util.Date convertDateFromAS400(BigDecimal as400Date){
		if (as400Date != null){
			if  (BigDecimal.ZERO.equals(as400Date)) {
				return getUtilDate("1900-01-01","yyyy-MM-dd");
			}else if (as400Date.equals(new BigDecimal(9999999))){
				return getUtilDate("2049-12-31","yyyy-MM-dd"); 
			}else{			
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				try {
					return df.parse(new BigDecimal(19000000).add(as400Date).toString());
				} catch (ParseException pe) {
					pe.printStackTrace();
				}
			}
		}
		
		return null;
	}
	/**
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String formateDate(java.util.Date date, String dateFormat) {
		String formatedDate = null;
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		if (date != null) {
			formatedDate = formatter.format(date);
		}
		return formatedDate;
	}
	
	public static String getTodayDateTime(){
	   String dateFormat = "MM/dd/yyyy hh:mm:ss";
	   java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(dateFormat);
	   Calendar objCalendar = Calendar.getInstance();	
	   return df.format(objCalendar.getTime());
	}
	
}
