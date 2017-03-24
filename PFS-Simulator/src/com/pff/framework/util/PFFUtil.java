package com.pff.framework.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class PFFUtil {
	

   //PHONE TYPES
	public static final String REQ_CHANNELID   ="FOS";// For PCML REQUEASTCHANNELID input value
	public static final String ESTPHONENO="ESTPHN";
	public static final String ESTFAXNO="ESTFAX";
	public static final String ESTMOBILENO="ESTMOB";
	public static final String ESTOTHERPHONENO="OTHERPHN";
	public static final String ESTOTHERFAXNO="OTHERFAX";
	public static final String ESTOTHERMOBILENO="OTHERMOB";
	public static final String SMSMOBILENO="SMSMOB";
	public static final String OFFICEPHONENO="OFFICE";
	public static final String OFFICEFAXNO="FAX";
	public static final String OFFICEMOBILENO="OFFICEMOB";
	public static final String RESIDENCEPHONENO="HOMEPHN";
	public static final String RESIDENCEFAXNO="HOMEFAX";
	public static final String RESIDENCEMOBILENO="MOBILE";
	public static final String HCPHONENO="HC_PHN";
	public static final String HCFAXNO="HC_FAX";
	public static final String HCMOBILENO="HC_MOB";
	public static final String HCCONTACTNUMBER="HC_CONTACT";
	
	//MAIL TYPES
	public static final String FAXINDEMITY="INDEMFAX";
	public static final String ESTEMAILADDRESS="ESTMAIL";
	public static final String ESTOTHEREMAILADDRESS="EOTHMAIL";
	public static final String INDEMITYFAXNUMBER="INDEMFAX";
	public static final String EMAILINDEMITY="INDEMAIL";
	public static final String OFFICEEMAILADDRESS="OFFICE";
	public static final String RESIDENCEEMAILADDRESS="PERSON1";
	public static final String INDEMITYEMAIADDRESS="INDEMAILADR";
		
	//ADDRESS TYPES
	public static final String OFFICEADDRS="OFFICE";
	public static final String RESIDENCEADDRS="HOME_RC";
	public static final String HOMECOUNTRYADDRS="HOME_PC";
	public static final String ESTOTHERADDRS="WORK";
	public static final String ESTMAINADDRS="OFFICE";

	
	//
	public static final String RESERVE="RESERVE";
	public static final String CONFIRM="CONFIRM";
	public static final String CANCEL_RESERVE="CANCEL_RESERVE";
	public static final String CANCEL_UTILIZATION="CANCEL_UTILIZATION";
	public static final String OVERRIDE_RESERVE="OVERRIDE_RESERVE";
	public static Date getDate(String reqDate) throws ParseException {
		SimpleDateFormat formatter  = new SimpleDateFormat("yyyyMMdd");        
		Date date  = formatter.parse(reqDate);
		return date;

	}
	
	


	public static Date convertFromAS400(BigDecimal as400Date) throws ParseException{

		String pcDate = "";
		BigDecimal dateInt = null;

		if (as400Date != null){
			if  (new BigDecimal(0).compareTo(as400Date) !=0) {
				dateInt = new BigDecimal(19000000).add(as400Date);
				pcDate = dateInt.toString();
			}
			else if (as400Date.equals("9999999")) 	
			{
				pcDate = "";
			}	

		}

		if(!StringUtils.trimToEmpty(pcDate).equals("")){
			return getDate(pcDate);
		}

		return null;
	}
	
	/**
	 *  Format date into "dd-MM-YYYY"
	 * @param dateValue
	 * @return
	 */
	
	
	
	public static String dateFormatter(String dateValue) {
		String date = dateValue.substring(0, 10);
		return date;
	}
	
	public static BigDecimal cDateToAS400(String pcDate){
		BigDecimal as400Date= null;
		BigDecimal dateInt = null;
		
		if(pcDate==null)
			return null;

		if (!pcDate.trim().equals("")) {
			dateInt = 	new BigDecimal(pcDate.substring(0,4) + pcDate.substring(5,7) + pcDate.substring(8,10));
			as400Date = new BigDecimal(19000000).subtract(dateInt);
			as400Date = new BigDecimal(-1).multiply(as400Date);
		}
		else
			as400Date = null;
			
		
	return as400Date;
	}
	
	public static String getCurrentDateTime() {
		SimpleDateFormat formatter  = new SimpleDateFormat ("yyyy-MM-dd-HH.mm.ss");
		String dateString = formatter.format(new java.util.Date());
		return dateString;
	}
	public static String getCurrentDate(String dateFormat) {
		SimpleDateFormat formatter  = new SimpleDateFormat (dateFormat);        
		String dateString = formatter.format(new java.util.Date());
		return dateString;
	}
	public static BigDecimal formatBigDecAmount(BigDecimal value, int decPos) {
		String inputAmount		= null;

		if(value != null && value.compareTo(new BigDecimal(0)) !=0 ) {

			final String   format   =   "#####0";
			DecimalFormat   df       =   new DecimalFormat();
			StringBuffer    sb       =   new StringBuffer(format);
			boolean        negSign  =   false;	


			if(decPos>0) {
				sb.append('.');
				for(int i=0;i<decPos;i++) {
					sb.append('0');
				}

				if(value.compareTo(new BigDecimal(0))==-1) {
					negSign =   true;
					value   =   value.multiply(new BigDecimal(-1));               
				}

				String num=value.toString();               
				if(num.length()<decPos) {
					for(int i=0; i<decPos; i++) {                
						num = "0"+num;    
					}
				}

				int len=num.length();
				value=new BigDecimal(num.substring(0,len-decPos) + '.' +num.substring(len-decPos));

				if(negSign==true) {
					value=value.multiply(new BigDecimal(-1));
				}
			}

			df.applyPattern(sb.toString());
			inputAmount =df.format(value).toString(); 
		} else {
			String string = "0.";
			for(int i=0; i<decPos; i++) {                
				string +="0";                
			}
			inputAmount =string;        
		}
		return new BigDecimal(inputAmount);
	}

	public static String getDateFormate(Date date) {
		if(date!=null){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			return formatter.format(date);
		}
		return null;
	}
	


}