package com.pennant.pff.interfaces.util;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.commons.lang.StringUtils;



/**
 * Used to specify Date type selection in <b>DateUtility</b> class.
 * 
 */
public final class DateUtility {
	public static java.util.Date getCurrentDtTm() {
		return new java.util.Date(System.currentTimeMillis());
	}

	public static Date parseDate(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		java.util.Date uDate = null;

		try {
			uDate = df.parse(date);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}

		return new Date(uDate.getTime());
	}

	public static java.util.Date parseDateTime(String date)
			throws ParseException {
		if (StringUtils.trimToNull(date) == null) {
			return null;
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

		return df.parse(date);
	}

	public static Date parseDate(String date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		java.util.Date uDate = null;

		try {
			uDate = df.parse(date);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}

		return new Date(uDate.getTime());
	}
	
	/**
	 *Converts the Date to GregorianCalendar
	 *
	 * @param date (Date)
	 * 
	 * @return GregorianCalendar
	 */
	public static GregorianCalendar convert(java.util.Date date){
		if(date == null) return null;
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return gc;
	}  
	
	/**
	 * Count Number of days between Util Dates
	 * 
	 * @param startDate (Date)
	 *  
	 * @param endDate (Date)
	 * 
	 * @return int
	 */
	public static int getDaysBetween(Date startDate, Date endDate){
		if(startDate == null || endDate == null) return -1;
		
		GregorianCalendar gc1 = convert(startDate);
		GregorianCalendar gc2 = convert(endDate);
		
		if(gc1.get(Calendar.YEAR)==gc2.get(Calendar.YEAR))
			return Math.abs(gc1.get(Calendar.DAY_OF_YEAR)-gc2.get(Calendar.DAY_OF_YEAR));
		
		long time1 = startDate.getTime();
		long time2 = endDate.getTime();
		long days = (time1 - time2)/(1000 * 60 * 60 * 24);
		
		return Math.abs((int)days);
	}
	
	/**
	 * Format date 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String formateDate( java.util.Date date, String dateFormat){		
		String formatedDate=null;
		if (StringUtils.trimToEmpty(dateFormat).equals("")){
			dateFormat="dd/MM/yyyy";
		}

		SimpleDateFormat formatter  = new SimpleDateFormat (dateFormat);    

		if(date  != null){		
			formatedDate = formatter.format(date);			
		}		
		return formatedDate;
	}
	
}
