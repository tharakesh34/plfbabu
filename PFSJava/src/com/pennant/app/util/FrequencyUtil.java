/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 *
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  FrequencyUtil.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.model.FrequencyDetails;
import com.pennant.backend.dao.ErrorDetailsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;

/**
 * Validates the Frequency code and returns the FrequencyDetails object with any errors.
 */
public class FrequencyUtil implements Serializable {

    private static final long serialVersionUID = -1464410860290217531L;
    
	private static ErrorDetailsDAO errorDetailsDAO;
	
	private static final int[] frqMthDays = {31,29,31,30,31,30,31,31,30,31,30,31};
	
	public static String Y01="01";
	public static String Y02="02";
	public static String Y03="03";
	public static String Y04="04";
	public static String Y05="05";
	public static String Y06="06";
	public static String Y07="07";
	public static String Y08="08";
	public static String Y09="09";
	public static String Y10="10";
	public static String Y11="11";
	public static String Y12="12";
	
	public static String H01="01";
	public static String H02="02";
	public static String H03="03";
	public static String H04="04";
	public static String H05="05";
	public static String H06="06";

	public static String Q01="01";
	public static String Q02="02";
	public static String Q03="03";
	public static String Q04="04";

	public static String M00="00";
	
	private static String[] getYearlyConstants() {
		return new String[] {
				Labels.getLabel("label_Select_Jan"),Labels.getLabel("label_Select_Feb"),
				Labels.getLabel("label_Select_Mar"),Labels.getLabel("label_Select_Apr"),
				Labels.getLabel("label_Select_May"),Labels.getLabel("label_Select_Jun"),
				Labels.getLabel("label_Select_Jul"),Labels.getLabel("label_Select_Aug"),
				Labels.getLabel("label_Select_Sep"),Labels.getLabel("label_Select_Oct"),
				Labels.getLabel("label_Select_Nov"),Labels.getLabel("label_Select_Dec")
		};
	}
	
	private static String[] getHalfyearlyconstants() {
		return new String[]{
				Labels.getLabel("label_Select_H1"),Labels.getLabel("label_Select_H2"),
				Labels.getLabel("label_Select_H3"),Labels.getLabel("label_Select_H4"),
				Labels.getLabel("label_Select_H5"),Labels.getLabel("label_Select_H6")
		};
	}

	private static String[] getQuarterlyconstants() {
		return new String[]{
				Labels.getLabel("label_Select_Q1"),
				Labels.getLabel("label_Select_Q2"),
				Labels.getLabel("label_Select_Q3")
		};
	}

	private static String[] getFortnightlyconstants() {
		return new String[]{
				"Every 1/3 Monday","Every 1/3 Tuesday","Every 1/3 Wednesday",
				"Every 1/3 Thursday","Every 1/3 Friday" ,
				"Every 1/3 Saturday","Every 1/3 Sunday", "Every 2/4 Monday",
				"Every 2/4 Tuesday","Every 2/4 Wednesday",
				"Every 2/4 Thursday","Every 2/4 Friday",
				"Every 2/4 Saturday","Every 2/4 Sunday"
		};
	}

	private static String[] getWeeklyconstants() {
		return new String[]{
				Labels.getLabel("label_Select_W1"),Labels.getLabel("label_Select_W2"),
				Labels.getLabel("label_Select_W3"),Labels.getLabel("label_Select_W4"),
				Labels.getLabel("label_Select_W5"),Labels.getLabel("label_Select_W6"),
				Labels.getLabel("label_Select_W5")													
		};
	}

	public static ArrayList<ValueLabel> getFrequency(){
		ArrayList<ValueLabel> frequencyCode= new ArrayList<ValueLabel>();
		frequencyCode.add(new ValueLabel(FrequencyCodeTypes.FRQ_YEARLY,
				Labels.getLabel("label_Select_Yearly")));
		frequencyCode.add(new ValueLabel(FrequencyCodeTypes.FRQ_HALF_YEARLY,
				Labels.getLabel("label_Select_HalfYearly")));
		frequencyCode.add(new ValueLabel(FrequencyCodeTypes.FRQ_QUARTERLY,
				Labels.getLabel("label_Select_Quarterly")));
		frequencyCode.add(new ValueLabel(FrequencyCodeTypes.FRQ_MONTHLY,
				Labels.getLabel("label_Select_Monthly")));
		frequencyCode.add(new ValueLabel(FrequencyCodeTypes.FRQ_FORTNIGHTLY,
				Labels.getLabel("label_Select_Fortnightly")));
		frequencyCode.add(new ValueLabel(FrequencyCodeTypes.FRQ_WEEKLY,
				Labels.getLabel("label_Select_Weekly")));
		frequencyCode.add(new ValueLabel(FrequencyCodeTypes.FRQ_DAILY,
				Labels.getLabel("label_Select_Daily")));
		return frequencyCode;
	} 

	public static ArrayList<ValueLabel> getFrequencyDetails(String frequency){
		return getFrequencyDetails(getCharFrequencyCode(frequency));
	}
	
	public static ArrayList<ValueLabel> getFrequencyDetails(char frequency){
		ArrayList<ValueLabel> arrfrqMonth= new ArrayList<ValueLabel>();
		switch(frequency){
			case 'Y':
				arrfrqMonth.add(new ValueLabel(Y01,Labels.getLabel("label_Select_Jan")));
				arrfrqMonth.add(new ValueLabel(Y02,Labels.getLabel("label_Select_Feb")));
				arrfrqMonth.add(new ValueLabel(Y03,Labels.getLabel("label_Select_Mar")));
				arrfrqMonth.add(new ValueLabel(Y04,Labels.getLabel("label_Select_Apr")));
				arrfrqMonth.add(new ValueLabel(Y05,Labels.getLabel("label_Select_May")));
				arrfrqMonth.add(new ValueLabel(Y06,Labels.getLabel("label_Select_Jun")));
				arrfrqMonth.add(new ValueLabel(Y07,Labels.getLabel("label_Select_Jly")));
				arrfrqMonth.add(new ValueLabel(Y08,Labels.getLabel("label_Select_Aug")));
				arrfrqMonth.add(new ValueLabel(Y09,Labels.getLabel("label_Select_Sep")));
				arrfrqMonth.add(new ValueLabel(Y10,Labels.getLabel("label_Select_Oct")));
				arrfrqMonth.add(new ValueLabel(Y11,Labels.getLabel("label_Select_Nov")));
				arrfrqMonth.add(new ValueLabel(Y12,Labels.getLabel("label_Select_Dec")));
			break;
			case 'H':
				arrfrqMonth.add(new ValueLabel(H01,Labels.getLabel("label_Select_H1")));
				arrfrqMonth.add(new ValueLabel(H02,Labels.getLabel("label_Select_H2")));
				arrfrqMonth.add(new ValueLabel(H03,Labels.getLabel("label_Select_H3")));
				arrfrqMonth.add(new ValueLabel(H04,Labels.getLabel("label_Select_H4")));
				arrfrqMonth.add(new ValueLabel(H05,Labels.getLabel("label_Select_H5")));
				arrfrqMonth.add(new ValueLabel(H06,Labels.getLabel("label_Select_H6")));
				break;
			case 'Q':
				arrfrqMonth.add(new ValueLabel(Q01,Labels.getLabel("label_Select_Q1")));
				arrfrqMonth.add(new ValueLabel(Q02,Labels.getLabel("label_Select_Q2")));
				arrfrqMonth.add(new ValueLabel(Q03,Labels.getLabel("label_Select_Q3")));
				arrfrqMonth.add(new ValueLabel(Q04,Labels.getLabel("label_Select_Q4")));
				break;
			case 'M'	:
				arrfrqMonth.add(new ValueLabel(M00,Labels.getLabel("label_Select_Monthly")));	
				break;
			case 'F'	:
				arrfrqMonth.add(new ValueLabel(M00,Labels.getLabel("label_Select_Fortnightly")));
				break;
			case 'W':
				arrfrqMonth.add(new ValueLabel(M00,Labels.getLabel("label_Select_Weekly")));
				break;
			case 'D':
				arrfrqMonth.add(new ValueLabel(M00,Labels.getLabel("label_Select_Daily")));
				break;
		}

		return arrfrqMonth;
	} 

	public static ArrayList<ValueLabel> getFrqdays(String frequency){
		ArrayList<ValueLabel> arrDays= new ArrayList<ValueLabel>();

		if (frequency!=null && frequency.trim().length()>=3){
			char frqCode 	= getCharFrequencyCode(frequency);
			int frqMonth	=getIntFrequencyMth(frequency);
			int days=0;

			switch(frqCode){
				case 'Y':
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.YEAR,frqMonth-1,01);
					days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					break;
				case 'F':
					days =14; 
					break;
				case 'W':
					days =7; 
					break;
				case 'D':
					days =1; 
					break;
				default:
					days =31;
			}

			for (int i = 1; i <= days; i++) {
				String strValue = StringUtils.leftPad(String.valueOf(i), 2,'0');
				arrDays.add(new ValueLabel(strValue,strValue));	
			}	
		}

		return arrDays;
	}

	
	public static char getCharFrequencyCode(String frequency) {
		return getFrequencyCode(frequency).charAt(0) ;
	}
	
	public static String getFrequencyCode(String frequency) {
		if(StringUtils.trimToEmpty(frequency).length()>0){
			return frequency.trim().substring(0,1);
		}
		return " ";
	}

	public static int getIntFrequencyMth(String frequency) {
		return Integer.parseInt(getFrequencyMth(frequency) );
		
	}
	
	public static String getFrequencyMth(String frequency) {
		String frqMth="00";

		if(StringUtils.trimToEmpty(frequency).length()==2){
			return StringUtils.leftPad(frequency.trim().substring(1), 2, '0');
		}else  if(StringUtils.trimToEmpty(frequency).length()>=3){
			return frequency.trim().substring(1,3);
		}
		return frqMth;
	}

	public static int getIntFrequencyDay(String frequency) {
		return Integer.parseInt(getFrequencyDay(frequency) );
	}

	public static String getFrequencyDay(String frequency) {
		if(StringUtils.trimToEmpty(frequency).length()>=3){
			return StringUtils.leftPad(frequency.trim().substring(3), 2, '0');
		}

		return "00";
	}

	public static FrequencyDetails getFrequencyDetail(String  frequency){
		return validateFrequency(new FrequencyDetails(frequency));
	}

	public static ErrorDetails validateFrequency(String  frequency){
		FrequencyDetails frequencyDetails = validateFrequency(new FrequencyDetails(frequency));
		return frequencyDetails.getErrorDetails();
	}
	
	/*
	 * Parse the Frequency and set any errors.
	 * Validate Frequency code,month and day and set any errors.
	 * @Parm FrequencyDetails
	 * @return FrequencyDetails
	 * 	
	 */
	public static FrequencyDetails validateFrequency(FrequencyDetails frequencyDetail){
		FrequencyDetails frequencyDetails = parseDetails(frequencyDetail);
		
		if(frequencyDetails.getErrorDetails()==null)
		{
			frequencyDetails = validateFreqCode(frequencyDetails);
		}

		return frequencyDetails;				

	}

	private static FrequencyDetails validateFreqCode(FrequencyDetails frequencyDetail)	{

		switch (frequencyDetail.getFrequencyCode().charAt(0)) {
			case 'Y':
	
				frequencyDetail.setErrorDetails(validMonthDay(1,12,1, 
						frqMthDays[frequencyDetail.getFrequencyMonth()-1], frequencyDetail));
	
				if(frequencyDetail.getErrorDetails()!=null){
					return frequencyDetail;
				}
	
				frequencyDetail.setFrequencyDescription(Labels.getLabel("label_Select_Yearly") + "," + 
						getYearlyConstants()[frequencyDetail.getFrequencyMonth()-1] + " " + 
						frequencyDetail.getFrequencyDay());
				break;
	
			case 'H':
	
				frequencyDetail.setErrorDetails(validMonthDay(1,6,1,31, frequencyDetail));
				if(frequencyDetail.getErrorDetails()!=null){
					return frequencyDetail;
				}
	
				frequencyDetail.setFrequencyDescription(Labels.getLabel("label_Select_HalfYearly") + "," +
						getHalfyearlyconstants()[frequencyDetail.getFrequencyMonth()-1] + " " +
						frequencyDetail.getFrequencyDay());
				break;
	
			case 'Q':
	
				frequencyDetail.setErrorDetails(validMonthDay(1,4,1,31, frequencyDetail));
				if(frequencyDetail.getErrorDetails()!=null){
					return frequencyDetail;
				}
	
				frequencyDetail.setFrequencyDescription(Labels.getLabel("label_Select_Quarterly") + "," + 
						getQuarterlyconstants()[frequencyDetail.getFrequencyMonth()-1] + " " +
						frequencyDetail.getFrequencyDay());
				break;
	
			case 'M':
	
				frequencyDetail.setErrorDetails(validMonthDay(0,0,1,31, frequencyDetail));
				if(frequencyDetail.getErrorDetails()!=null){
					return frequencyDetail;
				}
	
				frequencyDetail.setFrequencyDescription(Labels.getLabel("label_Select_Monthly") + "," + 
						frequencyDetail.getFrequencyDay());
				break;
	
			case 'F':
	
				frequencyDetail.setErrorDetails(validMonthDay(0,0,1,14, frequencyDetail));
				if(frequencyDetail.getErrorDetails()!=null){
					return frequencyDetail;
				}
	
				frequencyDetail.setFrequencyDescription(Labels.getLabel("label_Select_Fortnightly") + "," + 
						getFortnightlyconstants()[frequencyDetail.getFrequencyDay()-1] + " " + 
						frequencyDetail.getFrequencyDay());
				break;
	
			case 'W':
	
				frequencyDetail.setErrorDetails(validMonthDay(0,0,1,7, frequencyDetail));
				if(frequencyDetail.getErrorDetails()!=null){
					return frequencyDetail;
				}
	
				frequencyDetail.setFrequencyDescription(Labels.getLabel("label_Select_Weekly") + "," + 
						getWeeklyconstants()[frequencyDetail.getFrequencyDay()-1]);
				break;				
	
			case 'D':
	
				frequencyDetail.setErrorDetails(validMonthDay(0,0,0,0, frequencyDetail));
				if(frequencyDetail.getErrorDetails()!=null){
					return frequencyDetail;
				}
				frequencyDetail.setFrequencyDescription(Labels.getLabel("label_Select_Daily"));
				break;
			default:
				frequencyDetail.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
						"Invalid Frequency Code",null));
			}

		return frequencyDetail;
	}	

	private static ErrorDetails getErrorDetail(String errorField,String errorCode, 
			String[] errParm,String[] valueParm){
		return ErrorUtil.getErrorDetail(new ErrorDetails(errorField,errorCode,
				errParm,valueParm), SessionUserDetails.getUserLanguage());
	}

	private static FrequencyDetails parseDetails(FrequencyDetails frequencyDetails){
		String[] errParm = new String[2];

		if(frequencyDetails==null)
		{
			frequencyDetails = new FrequencyDetails();
			errParm[0] = " ";
			frequencyDetails.setErrorDetails(getErrorDetail("Frequency","51001",
					errParm,new String[]{" "}));
			return frequencyDetails;

		}

		if(StringUtils.trimToEmpty(frequencyDetails.getFrequency()).equals(""))
		{
			errParm[0] = " ";
			frequencyDetails.setErrorDetails(getErrorDetail("Frequency","51001",
					errParm,new String[]{frequencyDetails.getFrequency()}));
			return frequencyDetails;
		}


		if(StringUtils.trimToEmpty(frequencyDetails.getFrequency()).length()!=5)
		{
			errParm[0] = frequencyDetails.getFrequency();
			frequencyDetails.setErrorDetails(getErrorDetail("Frequency","51001",
					errParm,new String[]{frequencyDetails.getFrequency()}));
			return frequencyDetails;
		}

		frequencyDetails.setFrequencyCode(getFrequencyCode(frequencyDetails.getFrequency()));

		try
		{
			frequencyDetails.setFrequencyMonth(getIntFrequencyMth(frequencyDetails.getFrequency()));
		}
		catch (NumberFormatException nfe)
		{
			errParm[0] = Labels.getLabel("common.Month") + ":" + getFrequencyMth(
					frequencyDetails.getFrequency());
			frequencyDetails.setErrorDetails(getErrorDetail("Frequency","51001",
					errParm,new String[]{frequencyDetails.getFrequency()}));
			return frequencyDetails;
		}

		try
		{
			frequencyDetails.setFrequencyDay(getIntFrequencyDay(frequencyDetails.getFrequency()));
		}
		catch (NumberFormatException nfe)
		{
			errParm[0] = Labels.getLabel("common.Day") + ":" + getFrequencyDay(
					frequencyDetails.getFrequency());
			frequencyDetails.setErrorDetails(getErrorDetail("Frequency","51001",
					errParm,new String[]{frequencyDetails.getFrequency()}));
			return frequencyDetails;
		}

		return frequencyDetails;
	}


	private static ErrorDetails  validMonthDay(int startMth, int endMth,int startDay,
			int endDay,FrequencyDetails frequencyDetail){

		String[] errParm= new String[1];
		if(frequencyDetail.getFrequencyMonth() <startMth || frequencyDetail.getFrequencyMonth() > endMth)
		{
			errParm[0] = Labels.getLabel("common.Day") + ":" + frequencyDetail.getFrequencyDay();
			return getErrorDetail("Frequency","51001",errParm,new String[]{frequencyDetail.getFrequency()});
		}else{

			if(frequencyDetail.getFrequencyDay() <startDay || frequencyDetail.getFrequencyDay() > endDay)
			{
				errParm[0] = Labels.getLabel("common.Month") + ":" + frequencyDetail.getFrequencyMonth();
				return getErrorDetail("Frequency","51001",errParm,
						new String[]{frequencyDetail.getFrequency()});
			}
		}

		return null;

	}

	/**
	 * Method for Checking two frequency codes, whether codes are equal or not
	 * @param frequency1
	 * @param frequency2
	 * @return
	 */
	public static boolean isFrqCodeMatch(String  frequency1,String  frequency2){

		final FrequencyDetails freqDetails1 = getFrequencyDetail(frequency1);
		if(freqDetails1.getErrorDetails() != null){
			return false;
		}

		final FrequencyDetails freqDetails2 = getFrequencyDetail(frequency2);
		if(freqDetails2.getErrorDetails() != null){
			return false;
		}

		if(freqDetails1.getFrequency()==freqDetails2.getFrequency()){
			return true;
		}

		switch (freqDetails1.getCharFrequencyCode()) {
			case 'D':
				return true;
			case 'W':
	
				switch(freqDetails2.getCharFrequencyCode()){
				case 'W':
					if(freqDetails1.getFrequencyDay()==freqDetails2.getFrequencyDay()){
						return true;
					}	
				case 'F':
					if((freqDetails1.getFrequencyDay()==freqDetails2.getFrequencyDay()) || 
							(freqDetails2.getFrequencyDay()==(freqDetails1.getFrequencyDay()+7))){
						return true;
					}
				}
	
				return false;
	
			case 'M':
	
				switch(freqDetails2.getCharFrequencyCode()){
				case 'M':
					if(freqDetails1.getFrequencyDay()==freqDetails2.getFrequencyDay()){
						return true;
					}
				case 'Q':
					if(freqDetails1.getFrequencyDay()==freqDetails2.getFrequencyDay()){
						return true;
					}
				case 'H':
					if(freqDetails1.getFrequencyDay()==freqDetails2.getFrequencyDay()){
						return true;
					}
				case 'Y':
					if(freqDetails1.getFrequencyDay()==freqDetails2.getFrequencyDay()){
						return true;
					}
				}
	
				return false;
	
			case 'Q':
	
				if(freqDetails1.getFrequencyDay()==freqDetails2.getFrequencyDay()){
					switch(freqDetails2.getCharFrequencyCode()){	
					case 'H':
						if(freqDetails2.getFrequencyMonth()==freqDetails1.getFrequencyMonth()){
							return true;
						}
	
						if(freqDetails2.getFrequencyMonth()==freqDetails1.getFrequencyMonth()+3){
							return true;
						}
	
					case 'Y':
						if(freqDetails2.getFrequencyMonth()==freqDetails1.getFrequencyMonth()){
							return true;
						}
	
						if(freqDetails2.getFrequencyMonth()==freqDetails1.getFrequencyMonth()+3){
							return true;
						}
	
						if(freqDetails2.getFrequencyMonth()==freqDetails1.getFrequencyMonth()+6){
							return true;
						}
	
						if(freqDetails2.getFrequencyMonth()==freqDetails1.getFrequencyMonth()+9){
							return true;
						}
	
					}
				}
				return false;
	
			case 'H':
				if(freqDetails2.getFrequencyCode().equals(FrequencyCodeTypes.FRQ_YEARLY) && 
						freqDetails1.getFrequencyDay()==freqDetails2.getFrequencyDay()){
					if(freqDetails2.getFrequencyMonth()==freqDetails1.getFrequencyMonth() || 
							freqDetails2.getFrequencyMonth()==freqDetails1.getFrequencyMonth()+6){
						return true;
					}
				}
				return false;
	
			default :
				return false;
		}
	}
	
	//Method of Validating date for the Selection of Year
	public static boolean validateDate(int freqDay1, int freqDay2,int maxDaysOfMonth){

		if(freqDay1 == freqDay2){
			return true;
		}else if(freqDay1 > freqDay2 && freqDay2==maxDaysOfMonth){
			return true;
		}
		return false;
	}
	
	public static FrequencyDetails getNextDate(String frequency, int terms, 
			Date baseDate,String handlerType, boolean includeBaseDate){
		
		FrequencyDetails frequencyDetails = getFrequencyDetail(frequency);
		String[] errParm = new String[1];

		if(frequencyDetails.getErrorDetails() != null){
			return frequencyDetails;
		}

		if(terms <= 0){
			errParm[0] = ":" + terms;
			frequencyDetails.setErrorDetails(getErrorDetail("Terms","51003",
					errParm,new String[]{String.valueOf(terms)}));
			return frequencyDetails;
		}


		frequencyDetails.setTerms(terms);

		switch (frequencyDetails.getFrequencyCode().charAt(0)){
			case 'Y'  :
				return getQHYSchedule(terms,baseDate,frequencyDetails,handlerType,12,includeBaseDate);
			case 'Q' :
				return getQHYSchedule(terms,baseDate,frequencyDetails,handlerType,03,includeBaseDate);
			case 'H' :
				return getQHYSchedule(terms,baseDate,frequencyDetails,handlerType,06,includeBaseDate);
			case 'M' :
				return getQHYSchedule(terms,baseDate,frequencyDetails,handlerType,01,includeBaseDate);
			case 'W' :
				return getWeeklySchedule(terms,baseDate,frequencyDetails,handlerType,includeBaseDate);
			case 'F' :
				return getFortnightlySchedule(terms,baseDate,frequencyDetails,handlerType,includeBaseDate);
			case 'D' :
				return getDailySchedule(terms,baseDate,frequencyDetails,handlerType,includeBaseDate);
		}
		return frequencyDetails;
	}
	
	private static FrequencyDetails  getQHYSchedule(int terms,Date date,
			FrequencyDetails frequencyDetails,String handlerType,int increment, boolean includeBaseDate ){	

		List<Calendar> calendarList = new ArrayList<Calendar>();

		Calendar baseDate = Calendar.getInstance();
		baseDate.setTime(date);
		Calendar freqDate = Calendar.getInstance();
		freqDate.set(1,0,1);

		int startTerm = 0;
		int month = frequencyDetails.getFrequencyMonth();
		int day = frequencyDetails.getFrequencyDay();
		Calendar firstDate = Calendar.getInstance();

		if(includeBaseDate){
			startTerm = 1;
			calendarList.add((Calendar) baseDate.clone());
		}else {
			startTerm = 0;
		}

		if(frequencyDetails.getFrequencyCode().equals("M")){
			firstDate.set(baseDate.get(Calendar.YEAR),baseDate.get(Calendar.MONTH),1);
		}else{
			firstDate.set(baseDate.get(Calendar.YEAR),(month-1),1);
		}

		for(int i=startTerm; i<terms; i++){

			while(DateUtility.compare(freqDate.getTime(), baseDate.getTime()) != 1){

				int maxdays = firstDate.getActualMaximum(Calendar.DAY_OF_MONTH);

				if(day > maxdays){
					freqDate.set(firstDate.get(Calendar.YEAR),firstDate.get(Calendar.MONTH),maxdays);
				}else{
					freqDate.set(firstDate.get(Calendar.YEAR),firstDate.get(Calendar.MONTH),day);
				}
				
				firstDate.add(Calendar.MONTH,increment);
			} 

			baseDate.set(freqDate.get(Calendar.YEAR),freqDate.get(Calendar.MONTH),day);
			firstDate.set(freqDate.get(Calendar.YEAR),freqDate.get(Calendar.MONTH),1);
			freqDate = HolidayUtil.getBusinessDate("", handlerType, freqDate.getTime());
			calendarList.add((Calendar) freqDate.clone());
		}

		frequencyDetails.setNextFrequencyDate(calendarList.get(0).getTime());
		frequencyDetails.setScheduleList(calendarList);		
		return frequencyDetails;
	}
	
	private static FrequencyDetails getWeeklySchedule(int terms,Date date,
			FrequencyDetails frequencyDetails,String handlerType,boolean includeBaseDate) {
		
		List<Calendar> calendarList = new ArrayList<Calendar>();
		Calendar baseDate = Calendar.getInstance();
		baseDate.setTime(date);
		
		int startTerm = 0;
		int day = frequencyDetails.getFrequencyDay();
		int dayOfWeek = (baseDate.get(Calendar.DAY_OF_WEEK)-1);
		if(dayOfWeek==0){
			dayOfWeek=7;
		}
		
		int daysToAdd = (day-dayOfWeek);
		if(daysToAdd <= 0){
			daysToAdd += 7; 
		}
				
		if(includeBaseDate){
			startTerm = 1;
			calendarList.add((Calendar) baseDate.clone());
		}else{
			startTerm = 0;
		}
		
		baseDate.add(Calendar.DAY_OF_MONTH,(daysToAdd));
			
		for(int i =startTerm; i<terms; i++){
			baseDate = HolidayUtil.getBusinessDate("", handlerType, baseDate.getTime());
			calendarList.add((Calendar) baseDate.clone());
			baseDate.add(Calendar.DAY_OF_MONTH,7);
		}
		
		frequencyDetails.setScheduleList(calendarList);	
		frequencyDetails.setNextFrequencyDate(calendarList.get(0).getTime());
		return frequencyDetails;
	
	}
	
	private static FrequencyDetails getFortnightlySchedule(int terms,Date date,
			FrequencyDetails frequencyDetails,String handlerType,boolean includeBaseDate) {
		
		int i=0;
		List<Calendar> calendarList = new ArrayList<Calendar>();
		Calendar baseDate = Calendar.getInstance();
		baseDate.setTime(date);
		int actualTerms = 0; 			
		int day = frequencyDetails.getFrequencyDay();
		int dayOfWeek = (baseDate.get(Calendar.DAY_OF_WEEK)-1);
		if(dayOfWeek==0){
			dayOfWeek=7;
		}
		
		int daysToAdd = (day-dayOfWeek);
		daysToAdd = daysToAdd <= 7 ? daysToAdd :(daysToAdd-7);
		
		if(daysToAdd <= 0){
			daysToAdd += 7; 
		}		
		
		if(includeBaseDate){
			actualTerms = terms - 1;
			calendarList.add((Calendar) baseDate.clone());
		}else{
			actualTerms = terms;
		}

		baseDate.add(Calendar.DAY_OF_MONTH,(daysToAdd));
		
		if(day > 7){
			do {
				if (baseDate.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 2 ||  
						baseDate.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 4) {
					baseDate = HolidayUtil.getBusinessDate("", handlerType, baseDate.getTime());
					calendarList.add((Calendar) baseDate.clone());
					i++;
				}
				baseDate.add(Calendar.DAY_OF_MONTH,7);

			}while(i<actualTerms);
		}	
	
		if(day < 7){
			do{
				if (baseDate.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 1 ||  
						baseDate.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 3) {
					baseDate = HolidayUtil.getBusinessDate("", handlerType, baseDate.getTime());
					calendarList.add((Calendar) baseDate.clone());
					i++;
				}
				baseDate.add(Calendar.DAY_OF_MONTH,7);

			}while(i<terms);
		}	
		
		frequencyDetails.setScheduleList(calendarList);	
		frequencyDetails.setNextFrequencyDate(calendarList.get(0).getTime());
		return frequencyDetails;
	
	}
	
	private static FrequencyDetails getDailySchedule(int terms,Date date, 
			FrequencyDetails frequencyDetails,String handlerType,boolean includeBaseDate) {

		int startTerm = 0;
		List<Calendar> calendarList = new ArrayList<Calendar>();
		Calendar baseDate = Calendar.getInstance();
		baseDate.setTime(date);

		if(includeBaseDate) {
			startTerm = 1;
			calendarList.add((Calendar) baseDate.clone());
		} else {
			startTerm = 0;
		}

		for(int i =startTerm; i<terms; i++) {
			baseDate.add(Calendar.DAY_OF_MONTH,1);
			baseDate = HolidayUtil.getBusinessDate("", handlerType, baseDate.getTime());
			calendarList.add((Calendar) baseDate.clone());
		}
		frequencyDetails.setScheduleList(calendarList);	
		frequencyDetails.setNextFrequencyDate(calendarList.get(0).getTime());
		return frequencyDetails;

	}

	/**
	 * Method for Checking a frequency code and Date, whether those are equal or not
	 * @param frequency
	 * @param date
	 * @return
	 */
	public static boolean isFrqDate(String  frequency,Date date){

		final FrequencyDetails freqDetails = getFrequencyDetail(frequency);
		if(freqDetails.getErrorDetails() != null){
			return false;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
		int maxDaysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);

		switch (freqDetails.getFrequencyCode().charAt(0)) {

			case 'D':
	
				return true;
					
			case 'W':
	
				if(weekDay == freqDetails.getFrequencyDay()){
					return true;
				}
				return false;	
			case 'F':
	
				if((weekDay == freqDetails.getFrequencyDay()) || 
						(weekDay == (freqDetails.getFrequencyDay()-7)) && (dayOfMonth<=28)){
						return true;
				}
				return false;
			case 'M':
	
				return validateDate(freqDetails.getFrequencyDay(),day,maxDaysOfMonth);
	
			case 'Q':
	
				if((freqDetails.getFrequencyMonth()==01 || freqDetails.getFrequencyMonth()==02) && 
						(month%3 == freqDetails.getFrequencyMonth())){
						return validateDate(freqDetails.getFrequencyDay(),day,maxDaysOfMonth);
				}else if((freqDetails.getFrequencyMonth()==03) && (month%3 == 0)){
						return validateDate(freqDetails.getFrequencyDay(),day,maxDaysOfMonth);
				}
				return false;
			case 'H':
	
				if((freqDetails.getFrequencyMonth()==01 || freqDetails.getFrequencyMonth()==02 || 
						freqDetails.getFrequencyMonth()==03 || freqDetails.getFrequencyMonth()==04 || 
						freqDetails.getFrequencyMonth()==05) && (month%6 == freqDetails.getFrequencyMonth())){
					
						return validateDate(freqDetails.getFrequencyDay(),day,maxDaysOfMonth);
				}else if((freqDetails.getFrequencyMonth()==06) && (month%6 == 0)){
						return validateDate(freqDetails.getFrequencyDay(),day,maxDaysOfMonth);
				}
				return false;
			case 'Y':
	
				if((freqDetails.getFrequencyMonth()==01 || freqDetails.getFrequencyMonth()==02 || 
						freqDetails.getFrequencyMonth()==03 || freqDetails.getFrequencyMonth()==04 || 
						freqDetails.getFrequencyMonth()==05 || freqDetails.getFrequencyMonth()==06 || 
						freqDetails.getFrequencyMonth()==07 || freqDetails.getFrequencyMonth()==8 || 
						freqDetails.getFrequencyMonth()==9 || freqDetails.getFrequencyMonth()==10 || 
						freqDetails.getFrequencyMonth()==11) && 
						(month%12 == freqDetails.getFrequencyMonth())){
	
						return validateDate(freqDetails.getFrequencyDay(),day,maxDaysOfMonth);
				}else if((freqDetails.getFrequencyMonth()==12) && (month%12 == 0)){
						return validateDate(freqDetails.getFrequencyDay(),day,maxDaysOfMonth);
				}
				return false;
			default:
				return false;
		}

	}
	
	public static FrequencyDetails getTerms(String frequency,Date startDate, 
			Date endDate, boolean includeStartDate, boolean includeEndDate) {
		
		List<Calendar> scheduleList = new ArrayList<Calendar>();
		Calendar calDate = Calendar.getInstance();
		int terms=0;
		Date tempDate = startDate;
		int cont = -1;
		String[] errParm = new String[2];
		
		FrequencyDetails frequencyDetails = validateFrequency(new FrequencyDetails(frequency));
		
		if(frequencyDetails.getErrorDetails()!=null){
			return frequencyDetails;
		}
		
		if(startDate.after(endDate) || (startDate.equals(endDate) && includeStartDate == false) ){
			errParm[0] = startDate.toString();
			errParm[1] = endDate.toString();
			frequencyDetails.setErrorDetails(getErrorDetail("Start Date","51002",errParm,errParm));
			return frequencyDetails;
		}
		
		if(includeStartDate){
			calDate.setTime(startDate);
			scheduleList.add((Calendar) calDate.clone());
			terms++;
			cont = DateUtility.compare(tempDate, endDate);
		}
		
		while(cont == -1){
			tempDate = (getNextDate(frequency, 1, tempDate, 
					HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate());
			calDate.setTime(tempDate);
			cont = DateUtility.compare(tempDate, endDate);
			if (cont == 0){
				scheduleList.add((Calendar) calDate.clone());
				terms++;
				break;
			}
			if (cont == 1){
				if(includeEndDate){
					calDate.setTime(endDate);
					scheduleList.add((Calendar) calDate.clone());
					terms++;
				}
				break;
			}
			
			scheduleList.add((Calendar) calDate.clone());
			terms++;
		}
		
		frequencyDetails.setTerms(terms);
		frequencyDetails.setScheduleList(scheduleList);
		frequencyDetails.setNextFrequencyDate(scheduleList.get(0).getTime());
		
		return frequencyDetails;
	}
	
	public static String getMonthFrqValue(String monthValue, String frqCode) {
		String mth ="";
		int month = Integer.parseInt(monthValue);
		if("Q".equals(frqCode)) {
			if(month==1 || month==4 || month==7 || month==10 ) {
				mth = Q01;
			}else if(month==2 || month==5 || month==8 || month==11 ) {
				mth = Q02;
			}else if(month==3 || month==6 || month==9 || month==12 ) {
				mth = Q03;
			}
		}else if("H".equals(frqCode)) {
			if(month==1 || month==7) {
				mth = H01;
			}else if(month==2 || month==8 ) {
				mth = H02;
			}else if(month==3 || month==9 ) {
				mth = H03;
			}else if(month==4 || month==10 ) {
				mth = H04;
			}else if(month==5 || month==11 ) {
				mth = H05;
			}else if(month==6 || month==12 ) {
				mth = H06;
			}
		}
		return mth;
	}

	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public static ErrorDetailsDAO getErrorDetailsDAO() {
		return errorDetailsDAO;
	}
	public void setErrorDetailsDAO(ErrorDetailsDAO errorDetailsDAO) {
		FrequencyUtil.errorDetailsDAO = errorDetailsDAO;
	}
}
