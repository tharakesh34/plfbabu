/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 *
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : FrequencyUtil.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.model.FrequencyDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Validates the Frequency code and returns the FrequencyDetails object with any errors.
 */
public class FrequencyUtil implements Serializable {
	private static final long serialVersionUID = -1464410860290217531L;

	private static final int[] frqMthDays = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	public static final String Y01 = "01";
	public static final String Y02 = "02";
	public static final String Y03 = "03";
	public static final String Y04 = "04";
	public static final String Y05 = "05";
	public static final String Y06 = "06";
	public static final String Y07 = "07";
	public static final String Y08 = "08";
	public static final String Y09 = "09";
	public static final String Y10 = "10";
	public static final String Y11 = "11";
	public static final String Y12 = "12";

	public static final String H01 = "01";
	public static final String H02 = "02";
	public static final String H03 = "03";
	public static final String H04 = "04";
	public static final String H05 = "05";
	public static final String H06 = "06";

	public static final String Q01 = "01";
	public static final String Q02 = "02";
	public static final String Q03 = "03";
	public static final String Q04 = "04";

	public static final String B01 = "01";
	public static final String B02 = "02";

	public static final String M00 = "00";

	private static String[] getYearlyConstants() {
		return new String[] { Labels.getLabel("label_Select_Jan"), Labels.getLabel("label_Select_Feb"),
				Labels.getLabel("label_Select_Mar"), Labels.getLabel("label_Select_Apr"),
				Labels.getLabel("label_Select_May"), Labels.getLabel("label_Select_Jun"),
				Labels.getLabel("label_Select_Jul"), Labels.getLabel("label_Select_Aug"),
				Labels.getLabel("label_Select_Sep"), Labels.getLabel("label_Select_Oct"),
				Labels.getLabel("label_Select_Nov"), Labels.getLabel("label_Select_Dec") };
	}

	private static String[] getHalfyearlyconstants() {
		return new String[] { Labels.getLabel("label_Select_H1"), Labels.getLabel("label_Select_H2"),
				Labels.getLabel("label_Select_H3"), Labels.getLabel("label_Select_H4"),
				Labels.getLabel("label_Select_H5"), Labels.getLabel("label_Select_H6") };
	}

	private static String[] getQuarterlyconstants() {
		return new String[] { Labels.getLabel("label_Select_Q1"), Labels.getLabel("label_Select_Q2"),
				Labels.getLabel("label_Select_Q3") };
	}

	private static String[] getBimonthlyconstants() {
		return new String[] { Labels.getLabel("label_Select_B1"), Labels.getLabel("label_Select_B2") };
	}

	private static String[] getBiWeeklyconstants() {
		return new String[] { "Every 1/3 Monday", "Every 1/3 Tuesday", "Every 1/3 Wednesday", "Every 1/3 Thursday",
				"Every 1/3 Friday", "Every 1/3 Saturday", "Every 1/3 Sunday", "Every 2/4 Monday", "Every 2/4 Tuesday",
				"Every 2/4 Wednesday", "Every 2/4 Thursday", "Every 2/4 Friday", "Every 2/4 Saturday",
				"Every 2/4 Sunday" };
	}

	private static String[] getWeeklyconstants() {
		return new String[] { Labels.getLabel("label_Select_W1"), Labels.getLabel("label_Select_W2"),
				Labels.getLabel("label_Select_W3"), Labels.getLabel("label_Select_W4"),
				Labels.getLabel("label_Select_W5"), Labels.getLabel("label_Select_W6"),
				Labels.getLabel("label_Select_W7") };
	}

	private static List<ValueLabel> frequencyCodes = null;

	private static ValueLabel getValueLabel(String value, String labelKey) {
		return new ValueLabel(value, Labels.getLabel(labelKey));
	}

	public static List<ValueLabel> getFrequency() {

		if (CollectionUtils.isNotEmpty(frequencyCodes)) {
			return frequencyCodes;
		}

		frequencyCodes = new ArrayList<>();

		frequencyCodes.add(getValueLabel(FrequencyCodeTypes.FRQ_YEARLY, "label_Select_Yearly"));

		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_BR_INRST_RVW_FRQ_FRQCODEVAL_REQ)) {
			frequencyCodes.add(getValueLabel(FrequencyCodeTypes.FRQ_2YEARLY, "label_Select_2Yearly"));
			frequencyCodes.add(getValueLabel(FrequencyCodeTypes.FRQ_3YEARLY, "label_Select_3Yearly"));
		}

		frequencyCodes.add(getValueLabel(FrequencyCodeTypes.FRQ_HALF_YEARLY, "label_Select_HalfYearly"));
		frequencyCodes.add(getValueLabel(FrequencyCodeTypes.FRQ_QUARTERLY, "label_Select_Quarterly"));
		frequencyCodes.add(getValueLabel(FrequencyCodeTypes.FRQ_BIMONTHLY, "label_Select_BiMonthly"));
		frequencyCodes.add(getValueLabel(FrequencyCodeTypes.FRQ_MONTHLY, "label_Select_Monthly"));
		frequencyCodes.add(getValueLabel(FrequencyCodeTypes.FRQ_FORTNIGHTLY, "label_Select_Fortnightly"));
		if (ImplementationConstants.FRQ_15DAYS_REQ) {
			frequencyCodes.add(getValueLabel(FrequencyCodeTypes.FRQ_15DAYS, "label_Select_15DAYS"));
		}
		frequencyCodes.add(getValueLabel(FrequencyCodeTypes.FRQ_BIWEEKLY, "label_Select_BiWeekly"));
		frequencyCodes.add(getValueLabel(FrequencyCodeTypes.FRQ_WEEKLY, "label_Select_Weekly"));
		frequencyCodes.add(getValueLabel(FrequencyCodeTypes.FRQ_DAILY, "label_Select_Daily"));
		return frequencyCodes;
	}

	public static String getRepayFrequencyLabel(String frequency) {
		String repayFrequency = "";

		switch (StringUtils.substring(frequency, 0, 1)) {
		case FrequencyCodeTypes.FRQ_YEARLY:
			repayFrequency = Labels.getLabel("label_Select_Yearly");
			break;
		case FrequencyCodeTypes.FRQ_2YEARLY:
			repayFrequency = Labels.getLabel("label_Select_2Yearly");
			break;
		case FrequencyCodeTypes.FRQ_3YEARLY:
			repayFrequency = Labels.getLabel("label_Select_3Yearly");
			break;
		case FrequencyCodeTypes.FRQ_HALF_YEARLY:
			repayFrequency = Labels.getLabel("label_Select_HalfYearly");
			break;
		case FrequencyCodeTypes.FRQ_QUARTERLY:
			repayFrequency = Labels.getLabel("label_Select_Quarterly");
			break;
		case FrequencyCodeTypes.FRQ_BIMONTHLY:
			repayFrequency = Labels.getLabel("label_Select_BiMonthly");
			break;
		case FrequencyCodeTypes.FRQ_MONTHLY:
			repayFrequency = Labels.getLabel("label_Select_Monthly");
			break;
		case FrequencyCodeTypes.FRQ_FORTNIGHTLY:
			repayFrequency = Labels.getLabel("label_Select_Fortnightly");
			break;
		case FrequencyCodeTypes.FRQ_15DAYS:
			repayFrequency = Labels.getLabel("label_Select_15DAYS");
			break;
		case FrequencyCodeTypes.FRQ_BIWEEKLY:
			repayFrequency = Labels.getLabel("label_Select_BiWeekly");
			break;
		case FrequencyCodeTypes.FRQ_DAILY:
			repayFrequency = Labels.getLabel("label_Select_Daily");
			break;
		case FrequencyCodeTypes.FRQ_WEEKLY:
			repayFrequency = Labels.getLabel("label_Select_Weekly");
			break;

		default:
			break;
		}

		return repayFrequency;

	}

	public static ArrayList<ValueLabel> getFrequencyDetails(String frequency) {
		return getFrequencyDetails(getCharFrequencyCode(frequency));
	}

	public static ArrayList<ValueLabel> getFrequencyDetails(char frequency) {
		ArrayList<ValueLabel> arrfrqMonth = new ArrayList<ValueLabel>();
		switch (frequency) {
		case 'Y':
			arrfrqMonth.add(new ValueLabel(Y01, Labels.getLabel("label_Select_Jan")));
			arrfrqMonth.add(new ValueLabel(Y02, Labels.getLabel("label_Select_Feb")));
			arrfrqMonth.add(new ValueLabel(Y03, Labels.getLabel("label_Select_Mar")));
			arrfrqMonth.add(new ValueLabel(Y04, Labels.getLabel("label_Select_Apr")));
			arrfrqMonth.add(new ValueLabel(Y05, Labels.getLabel("label_Select_May")));
			arrfrqMonth.add(new ValueLabel(Y06, Labels.getLabel("label_Select_Jun")));
			arrfrqMonth.add(new ValueLabel(Y07, Labels.getLabel("label_Select_Jly")));
			arrfrqMonth.add(new ValueLabel(Y08, Labels.getLabel("label_Select_Aug")));
			arrfrqMonth.add(new ValueLabel(Y09, Labels.getLabel("label_Select_Sep")));
			arrfrqMonth.add(new ValueLabel(Y10, Labels.getLabel("label_Select_Oct")));
			arrfrqMonth.add(new ValueLabel(Y11, Labels.getLabel("label_Select_Nov")));
			arrfrqMonth.add(new ValueLabel(Y12, Labels.getLabel("label_Select_Dec")));
			break;
		case '2':
			arrfrqMonth.add(new ValueLabel(Y01, Labels.getLabel("label_Select_2Jan")));
			arrfrqMonth.add(new ValueLabel(Y02, Labels.getLabel("label_Select_2Feb")));
			arrfrqMonth.add(new ValueLabel(Y03, Labels.getLabel("label_Select_2Mar")));
			arrfrqMonth.add(new ValueLabel(Y04, Labels.getLabel("label_Select_2Apr")));
			arrfrqMonth.add(new ValueLabel(Y05, Labels.getLabel("label_Select_2May")));
			arrfrqMonth.add(new ValueLabel(Y06, Labels.getLabel("label_Select_2Jun")));
			arrfrqMonth.add(new ValueLabel(Y07, Labels.getLabel("label_Select_2Jly")));
			arrfrqMonth.add(new ValueLabel(Y08, Labels.getLabel("label_Select_2Aug")));
			arrfrqMonth.add(new ValueLabel(Y09, Labels.getLabel("label_Select_2Sep")));
			arrfrqMonth.add(new ValueLabel(Y10, Labels.getLabel("label_Select_2Oct")));
			arrfrqMonth.add(new ValueLabel(Y11, Labels.getLabel("label_Select_2Nov")));
			arrfrqMonth.add(new ValueLabel(Y12, Labels.getLabel("label_Select_2Dec")));
			break;
		case '3':
			arrfrqMonth.add(new ValueLabel(Y01, Labels.getLabel("label_Select_3Jan")));
			arrfrqMonth.add(new ValueLabel(Y02, Labels.getLabel("label_Select_3Feb")));
			arrfrqMonth.add(new ValueLabel(Y03, Labels.getLabel("label_Select_3Mar")));
			arrfrqMonth.add(new ValueLabel(Y04, Labels.getLabel("label_Select_3Apr")));
			arrfrqMonth.add(new ValueLabel(Y05, Labels.getLabel("label_Select_3May")));
			arrfrqMonth.add(new ValueLabel(Y06, Labels.getLabel("label_Select_3Jun")));
			arrfrqMonth.add(new ValueLabel(Y07, Labels.getLabel("label_Select_3Jly")));
			arrfrqMonth.add(new ValueLabel(Y08, Labels.getLabel("label_Select_3Aug")));
			arrfrqMonth.add(new ValueLabel(Y09, Labels.getLabel("label_Select_3Sep")));
			arrfrqMonth.add(new ValueLabel(Y10, Labels.getLabel("label_Select_3Oct")));
			arrfrqMonth.add(new ValueLabel(Y11, Labels.getLabel("label_Select_3Nov")));
			arrfrqMonth.add(new ValueLabel(Y12, Labels.getLabel("label_Select_3Dec")));
			break;
		case 'H':
			arrfrqMonth.add(new ValueLabel(H01, Labels.getLabel("label_Select_H1")));
			arrfrqMonth.add(new ValueLabel(H02, Labels.getLabel("label_Select_H2")));
			arrfrqMonth.add(new ValueLabel(H03, Labels.getLabel("label_Select_H3")));
			arrfrqMonth.add(new ValueLabel(H04, Labels.getLabel("label_Select_H4")));
			arrfrqMonth.add(new ValueLabel(H05, Labels.getLabel("label_Select_H5")));
			arrfrqMonth.add(new ValueLabel(H06, Labels.getLabel("label_Select_H6")));
			break;
		case 'Q':
			arrfrqMonth.add(new ValueLabel(Q01, Labels.getLabel("label_Select_Q1")));
			arrfrqMonth.add(new ValueLabel(Q02, Labels.getLabel("label_Select_Q2")));
			arrfrqMonth.add(new ValueLabel(Q03, Labels.getLabel("label_Select_Q3")));
			arrfrqMonth.add(new ValueLabel(Q04, Labels.getLabel("label_Select_Q4")));
			break;
		case 'B':
			arrfrqMonth.add(new ValueLabel(B01, Labels.getLabel("label_Select_B1")));
			arrfrqMonth.add(new ValueLabel(B02, Labels.getLabel("label_Select_B2")));
			break;
		case 'M':
			arrfrqMonth.add(new ValueLabel(M00, Labels.getLabel("label_Select_Monthly")));
			break;
		case 'F':
			arrfrqMonth.add(new ValueLabel(M00, Labels.getLabel("label_Select_Fortnightly")));
			break;
		case 'T':
			arrfrqMonth.add(new ValueLabel(M00, Labels.getLabel("label_Select_15DAYS")));
			break;
		case 'X':
			arrfrqMonth.add(new ValueLabel(M00, Labels.getLabel("label_Select_BiWeekly")));
			break;
		case 'W':
			arrfrqMonth.add(new ValueLabel(M00, Labels.getLabel("label_Select_Weekly")));
			break;
		case 'D':
			arrfrqMonth.add(new ValueLabel(M00, Labels.getLabel("label_Select_Daily")));
			break;
		default:
			break;

		}

		return arrfrqMonth;
	}

	public static ArrayList<ValueLabel> getFrqdays(String frequency) {
		ArrayList<ValueLabel> arrDays = new ArrayList<ValueLabel>();

		if (frequency != null && frequency.trim().length() >= 3) {
			char frqCode = getCharFrequencyCode(frequency);
			int frqMonth = getIntFrequencyMth(frequency);
			int days = 0;

			switch (frqCode) {
			case 'Y':
			case '2':
			case '3':
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, frqMonth - 1, 1);
				days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				break;
			case 'X':
			case 'F':
				days = 14;
				break;
			case 'T':
				days = 15;
				break;
			case 'W':
				days = 7;
				break;
			case 'D':
				days = 1;
				break;
			default:
				days = 31;
				break;
			}

			for (int i = 1; i <= days; i++) {
				String strValue = StringUtils.leftPad(String.valueOf(i), 2, '0');
				arrDays.add(new ValueLabel(strValue, strValue));
			}
		}

		return arrDays;
	}

	public static char getCharFrequencyCode(String frequency) {
		return getFrequencyCode(frequency).charAt(0);
	}

	public static String getFrequencyCode(String frequency) {
		if (StringUtils.trimToEmpty(frequency).length() > 0) {
			return frequency.trim().substring(0, 1);
		}
		return " ";
	}

	public static int getIntFrequencyMth(String frequency) {
		return Integer.parseInt(getFrequencyMth(frequency));

	}

	public static String getFrequencyMth(String frequency) {
		String frqMth = "00";

		if (StringUtils.trimToEmpty(frequency).length() == 2) {
			return StringUtils.leftPad(frequency.trim().substring(1), 2, '0');
		} else if (StringUtils.trimToEmpty(frequency).length() >= 3) {
			return frequency.trim().substring(1, 3);
		}
		return frqMth;
	}

	public static int getIntFrequencyDay(String frequency) {
		return Integer.parseInt(getFrequencyDay(frequency));
	}

	public static String getFrequencyDay(String frequency) {
		if (StringUtils.trimToEmpty(frequency).length() >= 3) {
			return StringUtils.leftPad(frequency.trim().substring(3), 2, '0');
		}

		return "00";
	}

	public static FrequencyDetails getFrequencyDetail(String code) {
		FrequencyDetails frequency = new FrequencyDetails(code);
		validateFrequency(frequency);

		return frequency;
	}

	public static ErrorDetail validateFrequency(String code) {
		FrequencyDetails frequency = new FrequencyDetails(code);
		validateFrequency(frequency);
		return frequency.getErrorDetails();
	}

	/*
	 * Parse the Frequency and set any errors. Validate Frequency code,month and day and set any errors.
	 * 
	 * @Parm FrequencyDetails
	 * 
	 * @return FrequencyDetails
	 */
	public static void validateFrequency(FrequencyDetails frequency) {
		parseDetails(frequency);

		if (frequency.getErrorDetails() == null) {
			validateFreqCode(frequency);
		}
	}

	private static void validateFreqCode(FrequencyDetails frequency) {
		ErrorDetail error = null;

		char frqCode = frequency.getFrequencyCode().charAt(0);

		int frequencyMonth = frequency.getFrequencyMonth();
		int frequencyDay = frequency.getFrequencyDay();
		String frqDesc = null;
		String label = null;

		switch (frqCode) {
		case 'Y':
			error = validMonthDay(1, 12, 1, frqMthDays[frequencyMonth - 1], frequency);

			label = Labels.getLabel("label_Select_Yearly");

			frqDesc = label + "," + getYearlyConstants()[frequencyMonth - 1] + " " + frequencyDay;

			break;
		case '2':
			error = validMonthDay(1, 12, 1, frqMthDays[frequencyMonth - 1], frequency);

			label = Labels.getLabel("label_Select_2Yearly");

			frqDesc = label + "," + getYearlyConstants()[frequencyMonth - 1] + " " + frequencyDay;
			break;
		case '3':
			error = validMonthDay(1, 12, 1, frqMthDays[frequencyMonth - 1], frequency);

			label = Labels.getLabel("label_Select_3Yearly");

			frqDesc = label + "," + getYearlyConstants()[frequencyMonth - 1] + " " + frequencyDay;

			break;
		case 'H':

			error = validMonthDay(1, 6, 1, 31, frequency);

			label = Labels.getLabel("label_Select_HalfYearly");

			frqDesc = label + "," + getHalfyearlyconstants()[frequencyMonth - 1] + " " + frequencyDay;

			break;
		case 'Q':

			error = validMonthDay(1, 4, 1, 31, frequency);

			label = Labels.getLabel("label_Select_Quarterly");

			frqDesc = label + "," + getQuarterlyconstants()[frequencyMonth - 1] + " " + frequencyDay;

			break;
		case 'B':

			error = validMonthDay(1, 2, 1, 31, frequency);
			label = Labels.getLabel("label_Select_BiMonthly");

			frqDesc = label + "," + getBimonthlyconstants()[frequencyMonth - 1] + " " + frequencyDay;

			break;
		case 'M':

			error = validMonthDay(0, 0, 1, 31, frequency);

			label = Labels.getLabel("label_Select_Monthly");

			frqDesc = label + "," + frequencyDay;
			break;

		case 'F':

			error = validMonthDay(0, 0, 1, 15, frequency);
			label = Labels.getLabel("label_Select_Fortnightly");

			frqDesc = label + "," + frequencyDay;
			break;
		case 'T':

			error = validMonthDay(0, 0, 1, 15, frequency);
			label = Labels.getLabel("label_Select_15DAYS");

			frqDesc = label + "," + frequencyDay;
			break;

		case 'X':

			error = validMonthDay(0, 0, 1, 14, frequency);
			label = Labels.getLabel("label_Select_BiWeekly");

			frqDesc = label + "," + getBiWeeklyconstants()[frequencyDay - 1] + " " + frequencyDay;
			break;

		case 'W':

			error = validMonthDay(0, 0, 1, 7, frequency);
			label = Labels.getLabel("label_Select_Weekly");

			frqDesc = label + "," + getWeeklyconstants()[frequencyDay - 1];
			break;

		case 'D':

			error = validMonthDay(0, 0, 0, 0, frequency);
			label = Labels.getLabel("label_Select_Daily");
			frqDesc = label;
			break;
		default:
			error = new ErrorDetail(PennantConstants.ERR_9999, "Invalid Frequency Code", null);
		}

		if (error == null) {
			frequency.setFrequencyDescription(frqDesc);
		}

		frequency.setErrorDetails(error);
	}

	private static ErrorDetail getErrorDetail(String errorField, String errorCode, String[] errParm,
			String[] valueParm) {
		return ErrorUtil.getErrorDetail(new ErrorDetail(errorField, errorCode, errParm, valueParm),
				SessionUserDetails.getUserLanguage());
	}

	private static void parseDetails(FrequencyDetails frequency) {
		String[] errParm = new String[1];

		String code = frequency.getFrequency();

		String[] valueParm = new String[] { code };

		if (StringUtils.isBlank(code)) {
			errParm[0] = " ";
			frequency.setErrorDetails(getErrorDetail("Frequency", "51001", errParm, valueParm));

			return;
		}

		if (StringUtils.trimToEmpty(code).length() != 5) {
			errParm[0] = code;
			frequency.setErrorDetails(getErrorDetail("Frequency", "51001", errParm, valueParm));

			return;
		}

		frequency.setFrequencyCode(getFrequencyCode(code));

		try {
			frequency.setFrequencyMonth(getIntFrequencyMth(code));
		} catch (NumberFormatException nfe) {
			errParm[0] = Labels.getLabel("common.Month") + ":" + getFrequencyMth(code);
			frequency.setErrorDetails(getErrorDetail("Frequency", "51001", errParm, valueParm));

			return;
		}

		try {
			frequency.setFrequencyDay(getIntFrequencyDay(code));
		} catch (NumberFormatException nfe) {
			errParm[0] = Labels.getLabel("common.Day") + ":" + getFrequencyDay(code);
			frequency.setErrorDetails(getErrorDetail("Frequency", "51001", errParm, valueParm));

			return;
		}

	}

	private static ErrorDetail validMonthDay(int startMth, int endMth, int startDay, int endDay,
			FrequencyDetails frequencyDetail) {

		String[] errParm = new String[1];
		if (frequencyDetail.getFrequencyMonth() < startMth || frequencyDetail.getFrequencyMonth() > endMth) {
			errParm[0] = Labels.getLabel("common.Day") + ":" + frequencyDetail.getFrequencyDay();
			return getErrorDetail("Frequency", "51001", errParm, new String[] { frequencyDetail.getFrequency() });
		} else {

			if (frequencyDetail.getFrequencyDay() < startDay || frequencyDetail.getFrequencyDay() > endDay) {
				errParm[0] = Labels.getLabel("common.Month") + ":" + frequencyDetail.getFrequencyMonth();
				return getErrorDetail("Frequency", "51001", errParm, new String[] { frequencyDetail.getFrequency() });
			}
		}

		return null;

	}

	/**
	 * Method for Checking two frequency codes, whether codes are equal or not
	 * 
	 * @param frequency1
	 * @param frequency2
	 * @return
	 */
	public static boolean isFrqCodeMatch(String frequency1, String frequency2) {

		final FrequencyDetails freqDetails1 = getFrequencyDetail(frequency1);
		if (freqDetails1.getErrorDetails() != null) {
			return false;
		}

		final FrequencyDetails freqDetails2 = getFrequencyDetail(frequency2);
		if (freqDetails2.getErrorDetails() != null) {
			return false;
		}

		if (StringUtils.equals(freqDetails1.getFrequency(), freqDetails2.getFrequency())) {
			return true;
		}

		switch (freqDetails1.getCharFrequencyCode()) {
		case 'D':
			return true;
		case 'W':

			switch (freqDetails2.getCharFrequencyCode()) {
			case 'W':
				if (freqDetails1.getFrequencyDay() == freqDetails2.getFrequencyDay()) {
					return true;
				}
			case 'X':
				if ((freqDetails1.getFrequencyDay() == freqDetails2.getFrequencyDay())
						|| (freqDetails2.getFrequencyDay() == (freqDetails1.getFrequencyDay() + 7))) {
					return true;
				}
			}

			return false;

		case 'F':

			// Deviate from Other coding used
			int daysToAdd = 0;
			String mainFrqString = freqDetails2.toString().substring(0, 1);

			if ("W".equals(mainFrqString) || "X".equals(mainFrqString)) {
				return false;
			}

			if (freqDetails2.getFrequencyDay() == 15) {
				daysToAdd = 16;
			} else {
				daysToAdd = 16;
			}

			if (freqDetails1.getFrequencyDay() == freqDetails2.getFrequencyDay()) {
				return true;
			}

			if (freqDetails1.getFrequencyDay() == freqDetails2.getFrequencyDay() + daysToAdd) {
				return true;
			}

			return false;

		case 'M':

			switch (freqDetails2.getCharFrequencyCode()) {
			case 'M':
				if (freqDetails1.getFrequencyDay() == freqDetails2.getFrequencyDay()) {
					return true;
				}
			case 'Q':
				if (freqDetails1.getFrequencyDay() == freqDetails2.getFrequencyDay()) {
					return true;
				}
			case 'B':
				if (freqDetails1.getFrequencyDay() == freqDetails2.getFrequencyDay()) {
					return true;
				}
			case 'H':
				if (freqDetails1.getFrequencyDay() == freqDetails2.getFrequencyDay()) {
					return true;
				}
			case 'Y':
				if (freqDetails1.getFrequencyDay() == freqDetails2.getFrequencyDay()) {
					return true;
				}
			}

			return false;

		case 'B':

			if (freqDetails1.getFrequencyDay() == freqDetails2.getFrequencyDay()) {
				switch (freqDetails2.getCharFrequencyCode()) {
				case 'H':
					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth()) {
						return true;
					}

					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth() + 6) {
						return true;
					}

				case 'Y':
					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth()) {
						return true;
					}

					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth() + 3) {
						return true;
					}

					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth() + 6) {
						return true;
					}

					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth() + 9) {
						return true;
					}

				}
			}
			return false;

		case 'Q':

			if (freqDetails1.getFrequencyDay() == freqDetails2.getFrequencyDay()) {
				switch (freqDetails2.getCharFrequencyCode()) {
				case 'H':
					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth()) {
						return true;
					}

					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth() + 3) {
						return true;
					}

				case 'Y':
					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth()) {
						return true;
					}

					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth() + 2) {
						return true;
					}

					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth() + 4) {
						return true;
					}

					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth() + 6) {
						return true;
					}

					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth() + 8) {
						return true;
					}

					if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth() + 10) {
						return true;
					}

				}
			}
			return false;

		case 'H':
			if (freqDetails2.getFrequencyCode().equals(FrequencyCodeTypes.FRQ_YEARLY)
					&& freqDetails1.getFrequencyDay() == freqDetails2.getFrequencyDay()) {
				if (freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth()
						|| freqDetails2.getFrequencyMonth() == freqDetails1.getFrequencyMonth() + 6) {
					return true;
				}
			}
			return false;

		default:
			return false;
		}
	}

	// Method of Validating date for the Selection of Year
	public static boolean validateDate(int freqDay1, int freqDay2, int maxDaysOfMonth) {

		if (freqDay1 == freqDay2) {
			return true;
		} else if (freqDay1 > freqDay2 && freqDay2 == maxDaysOfMonth) {
			return true;
		}
		return false;
	}

	public static FrequencyDetails getNextDate(String frequency, int terms, Date baseDate, String handlerType,
			boolean includeBaseDate) {
		FrequencyDetails frequencyDetails = getFrequencyDetail(frequency);
		String[] errParm = new String[1];

		if (frequencyDetails.getErrorDetails() != null) {
			return frequencyDetails;
		}

		if (terms <= 0) {
			errParm[0] = ":" + terms;
			frequencyDetails
					.setErrorDetails(getErrorDetail("Terms", "51003", errParm, new String[] { String.valueOf(terms) }));
			return frequencyDetails;
		}

		frequencyDetails.setTerms(terms);

		switch (frequencyDetails.getFrequencyCode().charAt(0)) {
		case 'Y':
			return getQHYSchedule(terms, baseDate, frequencyDetails, handlerType, 12, includeBaseDate);
		case '2':
			return getQHYSchedule(terms, baseDate, frequencyDetails, handlerType, 24, includeBaseDate);
		case '3':
			return getQHYSchedule(terms, baseDate, frequencyDetails, handlerType, 36, includeBaseDate);
		case 'H':
			return getQHYSchedule(terms, baseDate, frequencyDetails, handlerType, 6, includeBaseDate);
		case 'Q':
			return getQHYSchedule(terms, baseDate, frequencyDetails, handlerType, 3, includeBaseDate);
		case 'B':
			return getQHYSchedule(terms, baseDate, frequencyDetails, handlerType, 2, includeBaseDate);
		case 'M':
			return getQHYSchedule(terms, baseDate, frequencyDetails, handlerType, 1, includeBaseDate);
		case 'F':
			return getFortnightlySchedule(terms, baseDate, frequencyDetails, handlerType, 14, includeBaseDate);
		case 'T':
			/*
			 * Satish K : going with getFortnightlySchedule method only, since a parameter for days given. so changed
			 * the days to 15 which will meet the requirement.may be we need to rename the method
			 */
			return getFortnightlySchedule(terms, baseDate, frequencyDetails, handlerType, 15, includeBaseDate);
		case 'X':
			return getBiWeeklySchedule(terms, baseDate, frequencyDetails, handlerType, includeBaseDate);
		case 'W':
			return getWeeklySchedule(terms, baseDate, frequencyDetails, handlerType, includeBaseDate);
		case 'D':
			return getDailySchedule(terms, baseDate, frequencyDetails, handlerType, includeBaseDate);
		}
		return frequencyDetails;
	}

	public static FrequencyDetails getNextDate(String frequency, int terms, Date baseDate, String handlerType,
			boolean includeBaseDate, int requestedMinDays) {

		int days = 0;
		int count = 1;
		FrequencyDetails frequencyDetails;
		Date startDate = baseDate;
		do {
			frequencyDetails = getNextDate(frequency, terms, startDate, handlerType,
					(count == 1 ? includeBaseDate : false));
			days = DateUtil.getDaysBetween(baseDate, frequencyDetails.getNextFrequencyDate());
			startDate = frequencyDetails.getNextFrequencyDate();
			count = count + 1;
		} while (days <= requestedMinDays && requestedMinDays != 0);

		return frequencyDetails;
	}

	private static FrequencyDetails getQHYSchedule(int terms, Date date, FrequencyDetails frequencyDetails,
			String handlerType, int increment, boolean includeBaseDate) {

		List<Calendar> calendarList = new ArrayList<Calendar>();

		Calendar baseDate = Calendar.getInstance();
		baseDate.setTime(date);
		Calendar freqDate = Calendar.getInstance();
		freqDate.set(1, 0, 1);

		int startTerm = 0;
		int month = frequencyDetails.getFrequencyMonth();
		int day = frequencyDetails.getFrequencyDay();
		Calendar firstDate = Calendar.getInstance();

		if (includeBaseDate) {
			startTerm = 1;
			calendarList.add((Calendar) baseDate.clone());
		} else {
			startTerm = 0;
		}

		if ("M".equals(frequencyDetails.getFrequencyCode())) {
			firstDate.set(baseDate.get(Calendar.YEAR), baseDate.get(Calendar.MONTH), 1);
		} else {
			firstDate.set(baseDate.get(Calendar.YEAR), month - 1, 1);
		}

		for (int i = startTerm; i < terms; i++) {

			while (DateUtil.compare(freqDate.getTime(), baseDate.getTime()) != 1) {

				int maxdays = firstDate.getActualMaximum(Calendar.DAY_OF_MONTH);

				if (day > maxdays) {
					freqDate.set(firstDate.get(Calendar.YEAR), firstDate.get(Calendar.MONTH), maxdays);
				} else {
					freqDate.set(firstDate.get(Calendar.YEAR), firstDate.get(Calendar.MONTH), day);
				}

				firstDate.add(Calendar.MONTH, increment);
			}

			baseDate.set(freqDate.get(Calendar.YEAR), freqDate.get(Calendar.MONTH), day);
			firstDate.set(freqDate.get(Calendar.YEAR), freqDate.get(Calendar.MONTH), 1);
			freqDate = BusinessCalendar.getBusinessDate("", handlerType, freqDate.getTime());
			calendarList.add((Calendar) freqDate.clone());
		}

		frequencyDetails.setNextFrequencyDate(DateUtil.getDatePart(calendarList.get(0).getTime()));
		frequencyDetails.setScheduleList(calendarList);
		return frequencyDetails;
	}

	private static FrequencyDetails getFortnightlySchedule(int terms, Date date, FrequencyDetails frequencyDetails,
			String handlerType, int increment, boolean includeBaseDate) {

		List<Calendar> calendarList = new ArrayList<Calendar>();
		Calendar baseDate = Calendar.getInstance();
		baseDate.setTime(date);

		int day = frequencyDetails.getFrequencyDay();

		int startTerm = 0;

		if (includeBaseDate) {
			startTerm = 1;
			calendarList.add((Calendar) baseDate.clone());
		} else {
			startTerm = 0;
		}

		for (int i = startTerm; i < terms; i++) {

			baseDate = BusinessCalendar.getBusinessDate("", handlerType, baseDate.getTime());

			int nday = baseDate.get(Calendar.DAY_OF_MONTH);
			int nMONTH = baseDate.get(Calendar.MONTH);
			int nYEAR = baseDate.get(Calendar.YEAR);

			if (nday > increment) {

				if (nMONTH == Calendar.DECEMBER) {
					nMONTH = Calendar.JANUARY;
					nYEAR = nYEAR + 1;
				} else {
					nMONTH = nMONTH + 1;
				}

				if (nMONTH == Calendar.FEBRUARY) {
					int maxdays = baseDate.getActualMaximum(Calendar.DAY_OF_MONTH);
					nday = maxdays;
				}

				nday = day;

			} else {
				nday = day + increment;
				if (nMONTH == Calendar.FEBRUARY) {
					int maxdays = baseDate.getActualMaximum(Calendar.DAY_OF_MONTH);
					if (nday > maxdays) {
						nday = maxdays;
					}
				}
			}

			baseDate.set(nYEAR, nMONTH, nday);
			calendarList.add((Calendar) baseDate.clone());
		}

		frequencyDetails.setScheduleList(calendarList);
		frequencyDetails.setNextFrequencyDate(DateUtil.getDatePart(calendarList.get(0).getTime()));

		return frequencyDetails;

	}

	private static FrequencyDetails getWeeklySchedule(int terms, Date date, FrequencyDetails frequencyDetails,
			String handlerType, boolean includeBaseDate) {

		List<Calendar> calendarList = new ArrayList<Calendar>();
		Calendar baseDate = Calendar.getInstance();
		baseDate.setTime(date);

		int startTerm = 0;
		int day = frequencyDetails.getFrequencyDay();
		int dayOfWeek = baseDate.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek == 0) {
			dayOfWeek = 7;
		}

		int daysToAdd = day - dayOfWeek;
		if (daysToAdd <= 0) {
			daysToAdd += 7;
		}

		if (includeBaseDate) {
			startTerm = 1;
			calendarList.add((Calendar) baseDate.clone());
		} else {
			startTerm = 0;
		}

		baseDate.add(Calendar.DAY_OF_MONTH, daysToAdd);

		for (int i = startTerm; i < terms; i++) {
			baseDate = BusinessCalendar.getBusinessDate("", handlerType, baseDate.getTime());
			calendarList.add((Calendar) baseDate.clone());
			baseDate.add(Calendar.DAY_OF_MONTH, 7);
		}

		frequencyDetails.setScheduleList(calendarList);
		frequencyDetails.setNextFrequencyDate(DateUtil.getDatePart(calendarList.get(0).getTime()));
		return frequencyDetails;

	}

	private static FrequencyDetails getBiWeeklySchedule(int terms, Date date, FrequencyDetails frequencyDetails,
			String handlerType, boolean includeBaseDate) {

		int i = 0;
		List<Calendar> calendarList = new ArrayList<Calendar>();
		Calendar baseDate = Calendar.getInstance();
		baseDate.setTime(date);
		int actualTerms = 0;
		int day = frequencyDetails.getFrequencyDay();
		int dayOfWeek = baseDate.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek == 0) {
			dayOfWeek = 7;
		}

		int daysToAdd = day - dayOfWeek;
		daysToAdd = daysToAdd <= 7 ? daysToAdd : (daysToAdd - 7);

		if (daysToAdd <= 0) {
			daysToAdd += 7;
		}

		if (includeBaseDate) {
			actualTerms = terms - 1;
			calendarList.add((Calendar) baseDate.clone());
		} else {
			actualTerms = terms;
		}

		baseDate.add(Calendar.DAY_OF_MONTH, daysToAdd);

		if (day > 7) {
			do {
				if (baseDate.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 2
						|| baseDate.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 4) {
					baseDate = BusinessCalendar.getBusinessDate("", handlerType, baseDate.getTime());
					calendarList.add((Calendar) baseDate.clone());
					i++;
				}
				baseDate.add(Calendar.DAY_OF_MONTH, 7);

			} while (i < actualTerms);
		}

		if (day < 7) {
			do {
				if (baseDate.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 1
						|| baseDate.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 3) {
					baseDate = BusinessCalendar.getBusinessDate("", handlerType, baseDate.getTime());
					calendarList.add((Calendar) baseDate.clone());
					i++;
				}
				baseDate.add(Calendar.DAY_OF_MONTH, 7);

			} while (i < terms);
		}

		frequencyDetails.setScheduleList(calendarList);
		frequencyDetails.setNextFrequencyDate(DateUtil.getDatePart(calendarList.get(0).getTime()));
		return frequencyDetails;

	}

	private static FrequencyDetails getDailySchedule(int terms, Date date, FrequencyDetails frequencyDetails,
			String handlerType, boolean includeBaseDate) {

		int startTerm = 0;
		List<Calendar> calendarList = new ArrayList<Calendar>();
		Calendar baseDate = Calendar.getInstance();
		baseDate.setTime(date);

		if (includeBaseDate) {
			startTerm = 1;
			calendarList.add((Calendar) baseDate.clone());
		} else {
			startTerm = 0;
		}

		for (int i = startTerm; i < terms; i++) {
			baseDate.add(Calendar.DAY_OF_MONTH, 1);
			baseDate = BusinessCalendar.getBusinessDate("", handlerType, baseDate.getTime());
			calendarList.add((Calendar) baseDate.clone());
		}
		frequencyDetails.setScheduleList(calendarList);
		frequencyDetails.setNextFrequencyDate(DateUtil.getDatePart(calendarList.get(0).getTime()));
		return frequencyDetails;

	}

	/**
	 * Method for Checking a frequency code and Date, whether those are equal or not
	 * 
	 * @param frequency
	 * @param date
	 * @return
	 */
	public static boolean isFrqDate(String frequency, Date date) {

		final FrequencyDetails freqDetails = getFrequencyDetail(frequency);
		if (freqDetails.getErrorDetails() != null) {
			return false;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		int maxDaysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DATE);

		switch (freqDetails.getFrequencyCode().charAt(0)) {

		case 'D':

			return true;

		case 'W':

			if (weekDay == freqDetails.getFrequencyDay()) {
				return true;
			}
			return false;
		case 'X':

			if ((weekDay == freqDetails.getFrequencyDay())
					|| (weekDay == (freqDetails.getFrequencyDay() - 7)) && (dayOfMonth <= 28)) {
				return true;
			}
			return false;

		case 'F':
			int daysToAdd = 0;
			if (dayOfMonth == 15) {
				daysToAdd = 16;
			} else {
				daysToAdd = 15;
			}

			if (dayOfMonth == freqDetails.getFrequencyDay()
					|| (dayOfMonth + daysToAdd) == freqDetails.getFrequencyDay()) {
				return true;
			}

			return false;
		case 'T':
			int daysAdd = 0;
			if (dayOfMonth > 15) {
				daysAdd = -15;
			} else {
				daysAdd = 15;
			}

			if (dayOfMonth == freqDetails.getFrequencyDay()
					|| (dayOfMonth + daysAdd) == freqDetails.getFrequencyDay()) {
				return true;
			}

			return false;
		case 'M':

			return validateDate(freqDetails.getFrequencyDay(), day, maxDaysOfMonth);

		case 'B':

			if ((freqDetails.getFrequencyMonth() == 1) && (month % 2 == freqDetails.getFrequencyMonth())) {
				return validateDate(freqDetails.getFrequencyDay(), day, maxDaysOfMonth);
			} else if ((freqDetails.getFrequencyMonth() == 2) && (month % 3 == 0)) {
				return validateDate(freqDetails.getFrequencyDay(), day, maxDaysOfMonth);
			}
			return false;

		case 'Q':

			if ((freqDetails.getFrequencyMonth() == 1 || freqDetails.getFrequencyMonth() == 2)
					&& (month % 3 == freqDetails.getFrequencyMonth())) {
				return validateDate(freqDetails.getFrequencyDay(), day, maxDaysOfMonth);
			} else if ((freqDetails.getFrequencyMonth() == 3) && (month % 3 == 0)) {
				return validateDate(freqDetails.getFrequencyDay(), day, maxDaysOfMonth);
			}
			return false;
		case 'H':

			if ((freqDetails.getFrequencyMonth() == 1 || freqDetails.getFrequencyMonth() == 2
					|| freqDetails.getFrequencyMonth() == 3 || freqDetails.getFrequencyMonth() == 4
					|| freqDetails.getFrequencyMonth() == 5) && (month % 6 == freqDetails.getFrequencyMonth())) {

				return validateDate(freqDetails.getFrequencyDay(), day, maxDaysOfMonth);
			} else if ((freqDetails.getFrequencyMonth() == 6) && (month % 6 == 0)) {
				return validateDate(freqDetails.getFrequencyDay(), day, maxDaysOfMonth);
			}
			return false;
		case 'Y':

			if ((freqDetails.getFrequencyMonth() == 1 || freqDetails.getFrequencyMonth() == 2
					|| freqDetails.getFrequencyMonth() == 3 || freqDetails.getFrequencyMonth() == 4
					|| freqDetails.getFrequencyMonth() == 5 || freqDetails.getFrequencyMonth() == 6
					|| freqDetails.getFrequencyMonth() == 7 || freqDetails.getFrequencyMonth() == 8
					|| freqDetails.getFrequencyMonth() == 9 || freqDetails.getFrequencyMonth() == 10
					|| freqDetails.getFrequencyMonth() == 11) && (month % 12 == freqDetails.getFrequencyMonth())) {

				return validateDate(freqDetails.getFrequencyDay(), day, maxDaysOfMonth);
			} else if ((freqDetails.getFrequencyMonth() == 12) && (month % 12 == 0)) {
				return validateDate(freqDetails.getFrequencyDay(), day, maxDaysOfMonth);
			}
			return false;
		default:
			return false;
		}

	}

	public static FrequencyDetails getTerms(String frequency, Date startDate, Date endDate, boolean includeStartDate,
			boolean includeEndDate) {
		return getTerms(frequency, startDate, endDate, includeStartDate, includeEndDate,
				HolidayHandlerTypes.getHolidayHandler(""));
	}

	public static FrequencyDetails getTerms(String code, Date startDate, Date endDate, boolean includeStartDate,
			boolean includeEndDate, String holidayHandlerTypes) {

		List<Calendar> scheduleList = new ArrayList<Calendar>();
		Calendar calDate = Calendar.getInstance();
		int terms = 0;
		Date tempDate = startDate;
		int cont = -1;
		String[] errParm = new String[2];

		FrequencyDetails frequency = new FrequencyDetails(code);
		validateFrequency(frequency);

		if (frequency.getErrorDetails() != null) {
			return frequency;
		}

		if (startDate.after(endDate) || (startDate.equals(endDate) && !includeStartDate)) {
			errParm[0] = startDate.toString();
			errParm[1] = endDate.toString();
			frequency.setErrorDetails(getErrorDetail("Start Date", "51002", errParm, errParm));
			return frequency;
		}

		if (includeStartDate) {
			calDate.setTime(startDate);
			scheduleList.add((Calendar) calDate.clone());
			terms++;
			cont = DateUtil.compare(tempDate, endDate);
		}

		while (cont == -1) {
			tempDate = getNextDate(code, 1, tempDate, holidayHandlerTypes, false, 0).getNextFrequencyDate();
			calDate.setTime(tempDate);
			cont = DateUtil.compare(tempDate, endDate);
			if (cont == 0) {
				scheduleList.add((Calendar) calDate.clone());
				terms++;
				break;
			}
			if (cont == 1) {
				if (includeEndDate) {
					calDate.setTime(endDate);
					scheduleList.add((Calendar) calDate.clone());
					terms++;
				}
				break;
			}

			scheduleList.add((Calendar) calDate.clone());
			terms++;
		}

		frequency.setTerms(terms);
		frequency.setScheduleList(scheduleList);
		frequency.setNextFrequencyDate(scheduleList.get(0).getTime());

		return frequency;
	}

	public static String getMonthFrqValue(String monthValue, String frqCode) {
		String mth = "";
		int month = Integer.parseInt(monthValue);
		if ("Q".equals(frqCode)) {
			if (month == 1 || month == 4 || month == 7 || month == 10) {
				mth = Q01;
			} else if (month == 2 || month == 5 || month == 8 || month == 11) {
				mth = Q02;
			} else if (month == 3 || month == 6 || month == 9 || month == 12) {
				mth = Q03;
			}
		} else if ("H".equals(frqCode)) {
			if (month == 1 || month == 7) {
				mth = H01;
			} else if (month == 2 || month == 8) {
				mth = H02;
			} else if (month == 3 || month == 9) {
				mth = H03;
			} else if (month == 4 || month == 10) {
				mth = H04;
			} else if (month == 5 || month == 11) {
				mth = H05;
			} else if (month == 6 || month == 12) {
				mth = H06;
			}
		} else if ("B".equals(frqCode)) {
			if (month == 1 || month == 3 || month == 5 || month == 7 || month == 9 || month == 11) {
				mth = B01;
			} else {
				mth = B02;
			}
		}
		return mth;
	}

	/**
	 * Method for Validating Frequencies as Frequency 1 must be less than or Equal to Frequency 2
	 * 
	 * @param frequency1
	 * @param frequency2
	 * @return
	 */
	public static String validateFrequencies(String frequency1, String frequency2) {

		if (frequency1.equals(frequency2)) {
			return "";
		}

		char frqCode1 = frequency1.charAt(0);
		char frqCode2 = frequency2.charAt(0);

		int frqMnt1 = Integer.parseInt(frequency1.substring(1, 3));
		int frqMnt2 = Integer.parseInt(frequency2.substring(1, 3));

		int frqDay1 = Integer.parseInt(frequency1.substring(3, 5));
		int frqDay2 = Integer.parseInt(frequency2.substring(3, 5));

		if (frqCode1 == frqCode2) {
			if (frqMnt1 == frqMnt2) {
				if (frqDay1 != frqDay2) {
					return FrequencyCodeTypes.INVALID_DATE;
				}
			} else {
				return FrequencyCodeTypes.INVALID_MONTH;
			}
		} else {

			switch (frqCode1) {
			case 'D':
				return "";
			case 'W':
				if (frqCode2 != 'X') {
					return FrequencyCodeTypes.INVALID_CODE;
				}
				break;
			case 'X':
				if ((frqDay2 % 14) != frqDay1) {
					return FrequencyCodeTypes.INVALID_DATE;
				}

				// TODO: Verify whether it is required or not
				if (frqCode2 == 'D' || frqCode2 == 'M' || frqCode2 == 'F') {
					return FrequencyCodeTypes.INVALID_CODE;
				}
				break;

			case 'F':
				if (frqDay1 == 15 || frqDay1 == 31) {
					if (frqDay2 != 15 && frqDay2 != 31) {
						return FrequencyCodeTypes.INVALID_DATE;
					}
				}

				if ((frqDay2 % 15) != frqDay1) {
					return FrequencyCodeTypes.INVALID_DATE;
				}

				// TODO: Verify whether it is required or not
				if (frqCode2 == 'D' || frqCode2 == 'M' || frqCode2 == 'F') {
					return FrequencyCodeTypes.INVALID_CODE;
				}
				break;
			case 'T':
				if (frqDay1 == 15 || frqDay1 == 31) {
					if (frqDay2 != 15 && frqDay2 != 31) {
						return FrequencyCodeTypes.INVALID_DATE;
					}
				}

				if ((frqDay2 % 15) != frqDay1) {
					return FrequencyCodeTypes.INVALID_DATE;
				}

				// TODO: Verify whether it is required or not
				if (frqCode2 == 'D' || frqCode2 == 'M' || frqCode2 == 'F') {
					return FrequencyCodeTypes.INVALID_CODE;
				}
				break;
			case 'M':
				if (frqDay1 != frqDay2) {
					return FrequencyCodeTypes.INVALID_DATE;
				}

				if (frqCode2 != 'M' && frqCode2 != 'B' && frqCode2 == 'Q' && frqCode2 == 'H' && frqCode2 == 'Y') {
					return FrequencyCodeTypes.INVALID_CODE;
				}

				break;
			case 'Q':
				if (frqCode2 != 'Q' && frqCode2 != 'H' && frqCode2 != 'Y') {
					return FrequencyCodeTypes.INVALID_CODE;
				}
				if (frqCode2 == 'Q' && frqMnt1 != frqMnt2) {
					return FrequencyCodeTypes.INVALID_MONTH;
				} else if (frqCode2 == 'H') {
					if (frqMnt1 != (frqMnt2 % 3)) {
						return FrequencyCodeTypes.INVALID_MONTH;
					}
				} else if (frqCode2 == 'Y') {
					if (frqMnt1 != (frqMnt2 % 3)) {
						return FrequencyCodeTypes.INVALID_MONTH;
					}
				}
				if (frqDay1 != frqDay2) {
					return FrequencyCodeTypes.INVALID_DATE;
				}
				break;
			case 'H':
				if (frqCode2 != 'H' && frqCode2 != 'Y') {
					return FrequencyCodeTypes.INVALID_CODE;
				}
				if (frqCode2 == 'H' && frqMnt1 != frqMnt2) {
					return FrequencyCodeTypes.INVALID_MONTH;
				} else if (frqCode2 == 'Y') {
					if (frqMnt1 != (frqMnt2 % 6)) {
						return FrequencyCodeTypes.INVALID_MONTH;
					}
				}
				if (frqDay1 != frqDay2) {
					return FrequencyCodeTypes.INVALID_DATE;
				}
				break;
			case 'Y':
				if (frqCode2 != 'Y') {
					return FrequencyCodeTypes.INVALID_CODE;
				}
				if (frqMnt1 != frqMnt2) {
					return FrequencyCodeTypes.INVALID_MONTH;
				}
				if (frqDay1 != frqDay2) {
					return FrequencyCodeTypes.INVALID_DATE;
				}
				break;
			case '2':
				if (frqCode2 != '2') {
					return FrequencyCodeTypes.INVALID_CODE;
				}
				if (frqMnt1 != frqMnt2) {
					return FrequencyCodeTypes.INVALID_MONTH;
				}
				if (frqDay1 != frqDay2) {
					return FrequencyCodeTypes.INVALID_DATE;
				}
				break;
			case '3':
				if (frqCode2 != '3') {
					return FrequencyCodeTypes.INVALID_CODE;
				}
				if (frqMnt1 != frqMnt2) {
					return FrequencyCodeTypes.INVALID_MONTH;
				}
				if (frqDay1 != frqDay2) {
					return FrequencyCodeTypes.INVALID_DATE;
				}
				break;
			}
		}

		return "";
	}
}
