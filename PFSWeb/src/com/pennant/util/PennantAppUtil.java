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
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  PennantAppUtil.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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

package com.pennant.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Listbox;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.InterestRateType;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.model.staticparms.RepaymentMethod;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Field;
import com.pennant.search.Filter;

public class PennantAppUtil {
	private static ArrayList<ValueLabel> arrDashBoardtype=null;
	private static ArrayList<ValueLabel> chartDimensions=null;
	
	/*
	 * public static ArrayList<ValueLabel> getGenderList() { ArrayList<ValueLabel> genderList = new
	 * ArrayList<ValueLabel>(); genderList.add(new ValueLabel("1",Labels.getLabel("label_Select_Male")));
	 * genderList.add(new ValueLabel("2",Labels.getLabel("label_Select_Female"))); genderList.add(new
	 * ValueLabel("3",Labels.getLabel("label_Select_None"))); return genderList; }
	 */

	public static ArrayList<ValueLabel> getAccountPurpose() {
		ArrayList<ValueLabel> accountPurposeList = new ArrayList<ValueLabel>();
		accountPurposeList.add(new ValueLabel("", Labels.getLabel("common.Select")));
		accountPurposeList.add(new ValueLabel("M", Labels.getLabel("label_Movement")));
		accountPurposeList.add(new ValueLabel("F", Labels.getLabel("label_Finance")));
		accountPurposeList.add(new ValueLabel("D", Labels.getLabel("label_Deposit")));
		accountPurposeList.add(new ValueLabel("G", Labels.getLabel("label_GL")));
		accountPurposeList.add(new ValueLabel("P", Labels.getLabel("label_PL")));
		accountPurposeList.add(new ValueLabel("C", Labels.getLabel("label_Contingent")));
		accountPurposeList.add(new ValueLabel("O", Labels.getLabel("label_Other_Internal")));

		return accountPurposeList;
	}

	public static ArrayList<ValueLabel> getYesNo() {
		ArrayList<ValueLabel> yesNoList = new ArrayList<ValueLabel>();
		yesNoList.add(new ValueLabel("Y", Labels.getLabel("common.Yes")));
		yesNoList.add(new ValueLabel("N", Labels.getLabel("common.No")));
		return yesNoList;
	}

	public static ArrayList<ValueLabel> getTranType() {
		ArrayList<ValueLabel> tranType = new ArrayList<ValueLabel>(2);
		tranType.add(new ValueLabel(PennantConstants.CREDIT, Labels.getLabel("common.Credit")));
		tranType.add(new ValueLabel(PennantConstants.DEBIT, Labels.getLabel("common.Debit")));
		return tranType;
	}

	public static ArrayList<ValueLabel> getTransactionalAccount(boolean isRIA) {
		ArrayList<ValueLabel> tranType = new ArrayList<ValueLabel>(7);
		tranType.add(new ValueLabel(PennantConstants.DISB, Labels.getLabel("label_Customer_Disbursement_Account")));
		tranType.add(new ValueLabel(PennantConstants.REPAY, Labels.getLabel("label_Repayment_Account")));
		tranType.add(new ValueLabel(PennantConstants.GLNPL, Labels.getLabel("label_GLNPL_Account")));
		
		if(isRIA){
			tranType.add(new ValueLabel(PennantConstants.INVSTR, Labels.getLabel("label_Investor_Account")));
		}
		
		tranType.add(new ValueLabel(PennantConstants.CUSTSYS, Labels.getLabel("label_CustomerSystem_Account")));
		tranType.add(new ValueLabel(PennantConstants.FIN, Labels.getLabel("label_Customer_Loan_Account")));
		tranType.add(new ValueLabel(PennantConstants.COMMIT, Labels.getLabel("label_Commitment_Account")));
		return tranType;
	}

	public static ArrayList<ValueLabel> getNotesType() {
		ArrayList<ValueLabel> purposeList = new ArrayList<ValueLabel>();
		purposeList.add(new ValueLabel("C", Labels.getLabel("label_CIF")));
		purposeList.add(new ValueLabel("A", Labels.getLabel("label_Account")));
		purposeList.add(new ValueLabel("L", Labels.getLabel("label_Loan")));
		return purposeList;
	}

	public static ArrayList<ValueLabel> getWeekName() {
		ArrayList<ValueLabel> purposeList = new ArrayList<ValueLabel>();
		purposeList.add(new ValueLabel("1", Labels.getLabel("label_SUNDAY")));
		purposeList.add(new ValueLabel("2", Labels.getLabel("label_MONDAY")));
		purposeList.add(new ValueLabel("3", Labels.getLabel("label_TUESDAY")));
		purposeList.add(new ValueLabel("4", Labels.getLabel("label_WEDNESDAY")));
		purposeList.add(new ValueLabel("5", Labels.getLabel("label_THURSDAY")));
		purposeList.add(new ValueLabel("6", Labels.getLabel("label_FRIDAY")));
		purposeList.add(new ValueLabel("7", Labels.getLabel("label_SATURDAY")));
		return purposeList;
	}

	public static ArrayList<ValueLabel> getSysParamType() {
		ArrayList<ValueLabel> parmTypeList = new ArrayList<ValueLabel>();
		parmTypeList.add(new ValueLabel("String", Labels.getLabel("label_String")));
		parmTypeList.add(new ValueLabel("Double", Labels.getLabel("label_Double")));
		parmTypeList.add(new ValueLabel("Date", Labels.getLabel("label_Date")));
		parmTypeList.add(new ValueLabel("List", Labels.getLabel("label_List")));
		return parmTypeList;
	}

	public static ArrayList<ValueLabel> getLovFieldType() {
		ArrayList<ValueLabel> lovFieldTypeList = new ArrayList<ValueLabel>();
		lovFieldTypeList.add(new ValueLabel("String", Labels.getLabel("label_String")));
		lovFieldTypeList.add(new ValueLabel("Double", Labels.getLabel("label_Double")));
		lovFieldTypeList.add(new ValueLabel("Integer", Labels.getLabel("label_Integer")));
		return lovFieldTypeList;
	}

	public static ArrayList<ValueLabel> getRecordStatus() {
		ArrayList<ValueLabel> genderList = new ArrayList<ValueLabel>();
		genderList.add(new ValueLabel("Save", "Save"));
		return genderList;
	}

	public static ArrayList<ValueLabel> getRightType() {
		ArrayList<ValueLabel> arrRightType = new ArrayList<ValueLabel>();
		arrRightType.add(new ValueLabel("", Labels.getLabel("common.Select")));
		arrRightType.add(new ValueLabel("0", Labels.getLabel("label_Select_Menu")));
		arrRightType.add(new ValueLabel("1", Labels.getLabel("label_Select_Page")));
		arrRightType.add(new ValueLabel("2", Labels.getLabel("label_Select_Component")));
		arrRightType.add(new ValueLabel("3", Labels.getLabel("label_Select_Field")));
		return arrRightType;
	}

	/**
	 * Method for Getting CustomerCategory type
	 * */
	public static ArrayList<ValueLabel> getCategoryType() {
		ArrayList<ValueLabel> categoryTypeList = new ArrayList<ValueLabel>();
		categoryTypeList.add(new ValueLabel("C", Labels.getLabel("label_Corporate")));
		categoryTypeList.add(new ValueLabel("I", Labels.getLabel("label_Individual")));

		return categoryTypeList;
	}

	/**
	 * Method for Getting Salutation Gender Code
	 * */
	public static ArrayList<ValueLabel> getSalutationGenderCode() {
		ArrayList<ValueLabel> genderCodeList = new ArrayList<ValueLabel>();
		genderCodeList.add(new ValueLabel("MALE", Labels.getLabel("label_Male")));
		genderCodeList.add(new ValueLabel("FEMALE", Labels.getLabel("label_Female")));
		genderCodeList.add(new ValueLabel("OTH", Labels.getLabel("label_Others")));
		return genderCodeList;
	}

	public static ArrayList<ValueLabel> getFrequency() {
		ArrayList<ValueLabel> arrRightType = new ArrayList<ValueLabel>();
		arrRightType.add(new ValueLabel("Y", Labels.getLabel("label_Select_Yearly")));
		arrRightType.add(new ValueLabel("H", Labels.getLabel("label_Select_HalfYearly")));
		arrRightType.add(new ValueLabel("Q", Labels.getLabel("label_Select_Quarterly")));
		arrRightType.add(new ValueLabel("M", Labels.getLabel("label_Select_Monthly")));
		arrRightType.add(new ValueLabel("F", Labels.getLabel("label_Select_Fortnightly")));
		arrRightType.add(new ValueLabel("W", Labels.getLabel("label_Select_Weekly")));
		arrRightType.add(new ValueLabel("D", Labels.getLabel("label_Select_Daily")));
		return arrRightType;
	}

	public static ArrayList<ValueLabel> getFrequencyDetails(char frequency) {
		ArrayList<ValueLabel> arrfrqMonth = new ArrayList<ValueLabel>();
		switch (frequency) {
		case 'Y':
			arrfrqMonth.add(new ValueLabel("01", Labels.getLabel("label_Select_Jan")));
			arrfrqMonth.add(new ValueLabel("02", Labels.getLabel("label_Select_Feb")));
			arrfrqMonth.add(new ValueLabel("03", Labels.getLabel("label_Select_Mar")));
			arrfrqMonth.add(new ValueLabel("04", Labels.getLabel("label_Select_Apr")));
			arrfrqMonth.add(new ValueLabel("05", Labels.getLabel("label_Select_May")));
			arrfrqMonth.add(new ValueLabel("06", Labels.getLabel("label_Select_Jun")));
			arrfrqMonth.add(new ValueLabel("07", Labels.getLabel("label_Select_Jly")));
			arrfrqMonth.add(new ValueLabel("08", Labels.getLabel("label_Select_Aug")));
			arrfrqMonth.add(new ValueLabel("09", Labels.getLabel("label_Select_Sep")));
			arrfrqMonth.add(new ValueLabel("10", Labels.getLabel("label_Select_Oct")));
			arrfrqMonth.add(new ValueLabel("11", Labels.getLabel("label_Select_Nov")));
			arrfrqMonth.add(new ValueLabel("12", Labels.getLabel("label_Select_Dec")));
			break;
		case 'H':
			arrfrqMonth.add(new ValueLabel("01", Labels.getLabel("label_Select_H1")));
			arrfrqMonth.add(new ValueLabel("02", Labels.getLabel("label_Select_H2")));
			arrfrqMonth.add(new ValueLabel("03", Labels.getLabel("label_Select_H3")));
			arrfrqMonth.add(new ValueLabel("04", Labels.getLabel("label_Select_H4")));
			arrfrqMonth.add(new ValueLabel("05", Labels.getLabel("label_Select_H5")));
			arrfrqMonth.add(new ValueLabel("06", Labels.getLabel("label_Select_H6")));
			break;
		case 'Q':
			arrfrqMonth.add(new ValueLabel("01", Labels.getLabel("label_Select_Q1")));
			arrfrqMonth.add(new ValueLabel("02", Labels.getLabel("label_Select_Q2")));
			arrfrqMonth.add(new ValueLabel("03", Labels.getLabel("label_Select_Q3")));
			arrfrqMonth.add(new ValueLabel("04", Labels.getLabel("label_Select_Q4")));
			break;
		case 'M':
			arrfrqMonth.add(new ValueLabel("00", Labels.getLabel("label_Select_Monthly")));
			break;
		case 'F':
			arrfrqMonth.add(new ValueLabel("00", Labels.getLabel("label_Select_Fortnightly")));
			break;
		case 'W':
			arrfrqMonth.add(new ValueLabel("00", Labels.getLabel("label_Select_Weekly")));
			break;
		case 'D':
			arrfrqMonth.add(new ValueLabel("00", Labels.getLabel("label_Select_Daily")));
			break;
		}

		return arrfrqMonth;
	}

	@SuppressWarnings("static-access")
	public static ArrayList<ValueLabel> getFrqdays(String frqCode) {
		ArrayList<ValueLabel> arrDays = new ArrayList<ValueLabel>();

		if (frqCode != null && frqCode.trim().length() >= 3) {
			char frequency = frqCode.charAt(0);
			int frqMonth = Integer.parseInt(frqCode.substring(1, 3));
			int days = 0;

			switch (frequency) {
			case 'Y':
				Calendar calendar = Calendar.getInstance();
				calendar.set(calendar.YEAR, frqMonth - 1, 01);
				days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				break;
			case 'F':
				days = 14;
				break;
			case 'W':
				days = 7;
				break;
			case 'D':
				days = 1;
				break;
			default:
				days = 31;
			}

			for (int i = 1; i <= days; i++) {
				String strValue = StringUtils.leftPad(String.valueOf(i), 2, '0');
				arrDays.add(new ValueLabel(strValue, strValue));
			}
		}

		return arrDays;
	}

	public static ArrayList<ValueLabel> getLanguage() {
		ArrayList<ValueLabel> languageList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Language> searchObject = new JdbcSearchObject<Language>(Language.class);
		searchObject.addSort("LngCode", false);

		List<Language> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel languageLabel = new ValueLabel(String.valueOf(appList.get(i).getLngCode()), appList.get(i).getLngDesc());
			languageList.add(languageLabel);
		}
		return languageList;
	}

	public static String getAmountFormate(int dec) {
		String formateString = PennantConstants.defaultAmountFormate;

		switch (dec) {
		case 0:
			formateString = PennantConstants.amountFormate0;
			break;
		case 1:
			formateString = PennantConstants.amountFormate1;
			break;
		case 2:
			formateString = PennantConstants.amountFormate2;
			break;
		case 3:
			formateString = PennantConstants.amountFormate3;
			break;
		case 4:
			formateString = PennantConstants.amountFormate4;
			break;
		}
		return formateString;
	}

	public static Listbox setRecordType(Listbox recordType) {
		recordType.appendItem("", "");
		recordType.appendItem(PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_NEW), PennantConstants.RECORD_TYPE_NEW);
		recordType.appendItem(PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_UPD), PennantConstants.RECORD_TYPE_UPD);
		recordType.appendItem(PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_DEL), PennantConstants.RECORD_TYPE_DEL);
		recordType.setSelectedIndex(0);
		return recordType;
	}

	public static String getlabelDesc(String value, List<ValueLabel> list) {

		for (int i = 0; i < list.size(); i++) {

			if (list.get(i).getValue().equalsIgnoreCase(value)) {
				return list.get(i).getLabel();
			}
		}
		return "";
	}

	public static String getValueDesc(String label, List<ValueLabel> list) {

		for (int i = 0; i < list.size(); i++) {

			if (list.get(i).getLabel().equalsIgnoreCase(label)) {
				return list.get(i).getValue();
			}
		}
		return "";
	}

	public static BigDecimal unFormateAmount(BigDecimal amount, int dec) {

		if (amount == null) {
			return new BigDecimal(0);
		}
		BigInteger bigInteger = amount.multiply(new BigDecimal(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}

	public static BigDecimal formateAmount(BigDecimal amount, int dec) {
		BigDecimal bigDecimal = new BigDecimal(0);

		if (amount != null) {
			bigDecimal = amount.divide(new BigDecimal(Math.pow(10, dec)));
		}
		return bigDecimal;
	}

	public static String amountFormate(BigDecimal amount, int dec) {
		BigDecimal bigDecimal = new BigDecimal(0);
		if (amount != null) {
			bigDecimal = amount.divide(new BigDecimal(Math.pow(10, dec)));
		}

		return formatAmount(bigDecimal, dec, false);
	}

	public static String formatAmount(BigDecimal value, int decPos, boolean debitCreditSymbol) {

		if (value != null && value.compareTo(new BigDecimal("0")) != 0) {
			DecimalFormat df = new DecimalFormat();
			StringBuffer sb = new StringBuffer("###,###,###,###");
			boolean negSign = false;

			if (decPos > 0) {
				sb.append('.');
				for (int i = 0; i < decPos; i++) {
					sb.append('0');
				}

				if (value.compareTo(new BigDecimal("0")) == -1) {
					negSign = true;
					value = value.multiply(new BigDecimal("-1"));
				}

				if (negSign) {
					value = value.multiply(new BigDecimal("-1"));
				}
			}

			if (debitCreditSymbol) {
				String s = sb.toString();
				sb.append(" 'Cr';").append(s).append(" 'Dr'");
			}

			df.applyPattern(sb.toString());
			return df.format(value).toString();
		} else {
			String string = "0.";
			for (int i = 0; i < decPos; i++) {
				string += "0";
			}
			return string;
		}

	}

	public static String formatRate(double value, int decPos) {
		StringBuffer sb = new StringBuffer("###,###,###,###");

		if (decPos > 0) {
			sb.append('.');
			for (int i = 0; i < decPos; i++) {
				sb.append('0');
			}
		}
		if (value != 0) {
			String subString = String.valueOf(value).substring(String.valueOf(value).indexOf('.'));
			if (subString.length() > 3 && !String.valueOf(String.valueOf(value).charAt(String.valueOf(value).indexOf('.') + 3)).equals("E")
			        && Integer.parseInt(String.valueOf(String.valueOf(value).charAt(String.valueOf(value).indexOf('.') + 3))) == 0) {
				java.text.DecimalFormat df = new java.text.DecimalFormat();
				df.applyPattern(sb.toString());
				return df.format(value).toString();
			} else {
				return String.valueOf(value);
			}
		} else {
			String string = "0.";
			for (int i = 0; i < decPos; i++) {
				string += "0";
			}
			return string;

		}
	}

	public static String formateLong(long longValue) {
		StringBuffer sb = new StringBuffer("###,###,###,###");
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		df.applyPattern(sb.toString());
		return df.format(longValue).toString();
	}

	public static String formateInt(int intValue) {

		StringBuffer sb = new StringBuffer("###,###,###,###");
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		df.applyPattern(sb.toString());
		return df.format(intValue).toString();
	}

	public static String getCcyFormate(int minCCY) {
		String formateString = PennantConstants.defaultAmountFormate;
		formateString = getAmountFormate(getCcyDec(minCCY));
		return formateString;
	}

	public static int getCcyDec(int minCCY) {
		int decPos = PennantConstants.defaultCCYDecPos;
		switch (minCCY) {
		case 1:
			decPos = 0;
			break;
		case 10:
			decPos = 1;
			break;
		case 100:
			decPos = 2;
			break;
		case 1000:
			decPos = 3;
			break;
		case 10000:
			decPos = 4;
			break;
		}
		return decPos;
	}
	
	public static BigDecimal getPercentageValue(BigDecimal amount, BigDecimal percent) {
		BigDecimal bigDecimal = new BigDecimal(0);

		if (amount != null) {
			bigDecimal = (amount.multiply(unFormateAmount(percent,2).divide(
					new BigDecimal(100)))).divide(new BigDecimal(100),RoundingMode.HALF_DOWN);
		}
		return bigDecimal;
	}

	public static String formateDate(Date date, String dateFormate) {
		String formatedDate = null;
		if (StringUtils.trimToEmpty(dateFormate).equals("")) {
			dateFormate = PennantConstants.dateFormat;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(dateFormate);

		if (date != null) {
			formatedDate = formatter.format(date);
		}

		return formatedDate;

	}

	public static Date getDate(Timestamp timestamp) {
		Date date = null;
		if (timestamp != null) {
			date = new Date(timestamp.getTime());
		}
		return date;
	}

	public static Timestamp getTimestamp(Date date) {
		Timestamp timestamp = null;

		if (date != null) {
			timestamp = new Timestamp(date.getTime());
		}
		return timestamp;
	}

	public static Time getTime(Date date) {
		Time time = null;

		if (date != null) {
			time = new Time(date.getTime());
		}
		return time;
	}

	// Added on 22082011 for Diary Notes
	public static ArrayList<ValueLabel> getFreqType() {
		ArrayList<ValueLabel> purposeList = new ArrayList<ValueLabel>();
		purposeList.add(new ValueLabel("D00", Labels.getLabel("label_Once")));
		purposeList.add(new ValueLabel("D01", Labels.getLabel("label_Dialy")));
		purposeList.add(new ValueLabel("D07", Labels.getLabel("label_Weekly")));
		purposeList.add(new ValueLabel("D15", Labels.getLabel("label_FortNight")));
		purposeList.add(new ValueLabel("M01", Labels.getLabel("label_Monthly")));
		purposeList.add(new ValueLabel("M03", Labels.getLabel("label_Quarterly")));
		purposeList.add(new ValueLabel("M06", Labels.getLabel("label_HalfYearly")));
		purposeList.add(new ValueLabel("M12", Labels.getLabel("label_Yearly")));
		return purposeList;
	}

	public static ArrayList<ValueLabel> getFieldTypeList() {
		ArrayList<ValueLabel> fieldTypeList = new ArrayList<ValueLabel>();
		fieldTypeList.add(new ValueLabel("S", Labels.getLabel("label_Select_String")));
		fieldTypeList.add(new ValueLabel("N", Labels.getLabel("label_Select_Numetic")));
		fieldTypeList.add(new ValueLabel("D", Labels.getLabel("label_Select_Date")));
		return fieldTypeList;
	}

	public static ArrayList<ValueLabel> getDedupModuleRoles() {
		ArrayList<ValueLabel> fieldTypeList = new ArrayList<ValueLabel>();
		return fieldTypeList;
	}

	public static ArrayList<ValueLabel> getAppCodes() {
		ArrayList<ValueLabel> appCodeList = new ArrayList<ValueLabel>();
		appCodeList.add(new ValueLabel("", Labels.getLabel("common.Select")));
		appCodeList.add(new ValueLabel("1", Labels.getLabel("PLF_LowerCase")));
		return appCodeList;

	}

	/**
	 * List of Operator code for RepaymentRuleTypes and Fee Details
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getRuleOperator() {
		ArrayList<ValueLabel> ruleOperatorList = new ArrayList<ValueLabel>();

		ruleOperatorList.add(new ValueLabel(" + ", Labels.getLabel("label_Addition")));
		ruleOperatorList.add(new ValueLabel(" - ", Labels.getLabel("label_Substraction")));
		ruleOperatorList.add(new ValueLabel(" * ", Labels.getLabel("label_Multiplication")));
		ruleOperatorList.add(new ValueLabel(" / ", Labels.getLabel("label_Divison")));
		ruleOperatorList.add(new ValueLabel(" ( ", Labels.getLabel("label_OpenBracket")));
		ruleOperatorList.add(new ValueLabel(" ) ", Labels.getLabel("label_CloseBracket")));

		return ruleOperatorList;
	}
	
	public static ArrayList<ValueLabel> getChargeTypes() {
		ArrayList<ValueLabel> chargeTypeList = new ArrayList<ValueLabel>();

		chargeTypeList.add(new ValueLabel("D", Labels.getLabel("label_Dummy")));
		chargeTypeList.add(new ValueLabel("F", Labels.getLabel("label_Fees")));
		chargeTypeList.add(new ValueLabel("C", Labels.getLabel("label_Charge")));

		return chargeTypeList;
	}

	/**
	 * List of Statement code for RepaymentRuleTypes
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getRuleStatements() {
		ArrayList<ValueLabel> ruleOperatorList = new ArrayList<ValueLabel>();
		ruleOperatorList.add(new ValueLabel(" if (expression){ \n statement1 \n statement2 \n }", PennantJavaUtil.getLabel("IF")));
		ruleOperatorList.add(new ValueLabel(" if (expression){ \n statement1 \n statement2 \n } \n else{ \n statement3 \n }", PennantJavaUtil.getLabel("IF- ELSE")));
		ruleOperatorList.add(new ValueLabel(" return; ", PennantJavaUtil.getLabel("RETURN")));
		return ruleOperatorList;
	}

	public static ArrayList<ValueLabel> getMathOperator() {
		ArrayList<ValueLabel> MathOperator = new ArrayList<ValueLabel>();
		MathOperator.add(new ValueLabel(" += ", PennantJavaUtil.getLabel("Add and assign")));
		MathOperator.add(new ValueLabel(" -= ", PennantJavaUtil.getLabel("Subtract and assign")));
		MathOperator.add(new ValueLabel(" *= ", PennantJavaUtil.getLabel("Multiply and assign")));
		MathOperator.add(new ValueLabel(" /= ", PennantJavaUtil.getLabel("Divide and assign")));
		MathOperator.add(new ValueLabel(" %= ", PennantJavaUtil.getLabel("Modulus and assign")));
		MathOperator.add(new ValueLabel(" + ", PennantJavaUtil.getLabel("Addition")));
		MathOperator.add(new ValueLabel(" - ", PennantJavaUtil.getLabel("Subtraction")));
		MathOperator.add(new ValueLabel(" * ", PennantJavaUtil.getLabel("Multiplication")));
		MathOperator.add(new ValueLabel(" / ", PennantJavaUtil.getLabel("Division")));
		MathOperator.add(new ValueLabel(" % ", PennantJavaUtil.getLabel("Modulus (Remainder of division)")));
		MathOperator.add(new ValueLabel(" ++ ", PennantJavaUtil.getLabel("Increment")));
		MathOperator.add(new ValueLabel(" -- ", PennantJavaUtil.getLabel("Decrement")));
		MathOperator.add(new ValueLabel(" == ", PennantJavaUtil.getLabel("Is identical (Is equal to and is of same type)")));
		MathOperator.add(new ValueLabel(" === ", PennantJavaUtil.getLabel("Is not equal to")));
		MathOperator.add(new ValueLabel(" != ", PennantJavaUtil.getLabel("Is Equal")));
		MathOperator.add(new ValueLabel(" !== ", PennantJavaUtil.getLabel("Is not identical")));
		MathOperator.add(new ValueLabel(" > ", PennantJavaUtil.getLabel("Greater than")));
		MathOperator.add(new ValueLabel(" >= ", PennantJavaUtil.getLabel("Greater than or equal to")));
		MathOperator.add(new ValueLabel(" < ", PennantJavaUtil.getLabel("Less than")));
		MathOperator.add(new ValueLabel(" <= ", PennantJavaUtil.getLabel("Less than or equal to")));
		MathOperator.add(new ValueLabel(" && ", PennantJavaUtil.getLabel("And")));
		MathOperator.add(new ValueLabel(" || ", PennantJavaUtil.getLabel("Or")));
		MathOperator.add(new ValueLabel(" ! ", PennantJavaUtil.getLabel("Not")));
		MathOperator.add(new ValueLabel(" = ", PennantJavaUtil.getLabel("Assignment")));
		MathOperator.add(new ValueLabel(" + ", PennantJavaUtil.getLabel("Concatenate of strings")));
		MathOperator.add(new ValueLabel(" ++ ", PennantJavaUtil.getLabel("Concatenate and assignment of strings")));
		MathOperator.add(new ValueLabel(" ?: ", PennantJavaUtil.getLabel("Ternary operator")));

		return MathOperator;
	}

	public static ArrayList<ValueLabel> getMathBasicOperator() {
		ArrayList<ValueLabel> MathOperator = new ArrayList<ValueLabel>();
		MathOperator.add(new ValueLabel(" += ", PennantJavaUtil.getLabel("Add and assign")));
		MathOperator.add(new ValueLabel(" -= ", PennantJavaUtil.getLabel("Subtract and assign")));
		MathOperator.add(new ValueLabel(" *= ", PennantJavaUtil.getLabel("Multiply and assign")));
		MathOperator.add(new ValueLabel(" /= ", PennantJavaUtil.getLabel("Divide and assign")));
		MathOperator.add(new ValueLabel(" %= ", PennantJavaUtil.getLabel("Modulus and assign")));
		MathOperator.add(new ValueLabel(" + ", PennantJavaUtil.getLabel("Addition")));
		MathOperator.add(new ValueLabel(" - ", PennantJavaUtil.getLabel("Subtraction")));
		MathOperator.add(new ValueLabel(" * ", PennantJavaUtil.getLabel("Multiplication")));
		MathOperator.add(new ValueLabel(" / ", PennantJavaUtil.getLabel("Division")));
		MathOperator.add(new ValueLabel(" % ", PennantJavaUtil.getLabel("Modulus (Remainder of division)")));
		MathOperator.add(new ValueLabel(" = ", PennantJavaUtil.getLabel("Assignment")));
		return MathOperator;
	}

	public static ArrayList<ValueLabel> getStatements() {

		ArrayList<ValueLabel> statementList = new ArrayList<ValueLabel>();
		statementList.add(new ValueLabel(" if (expression){ \n statement1 \n statement2 \n }", PennantJavaUtil.getLabel("IF")));
		statementList.add(new ValueLabel(" if (expression){ \n statement1 \n statement2 \n } \n else{ \n statement3 \n }", PennantJavaUtil.getLabel("IF- ELSE")));
		statementList.add(new ValueLabel(" switch(n){ \n case 1: \n execute code block 1 \n break;  \n default: \n code to be executed if n is different from case 1 and 2 \n }",
		        PennantJavaUtil.getLabel("SWITCH")));
		statementList.add(new ValueLabel(" return; ", PennantJavaUtil.getLabel("RETURN")));
		statementList.add(new ValueLabel(" for (variable=startvalue;variable<=endvalue;variable=variable+increment) \n { \n acode to be executed }", PennantJavaUtil
		        .getLabel("FOR")));
		statementList.add(new ValueLabel(" while (variable<=endvalue) \n { \n code to be executed \n }", PennantJavaUtil.getLabel("WHILE")));
		statementList.add(new ValueLabel(" do  { \n code to be executed \n }while (variable<=endvalue); ", PennantJavaUtil.getLabel("DO/WHILE")));
		statementList.add(new ValueLabel(" for (variable in object) \n { \n code to be executed \n }", PennantJavaUtil.getLabel("FOR/IN ")));
		return statementList;

	}

	public static ArrayList<ValueLabel> getScheduleMethod() {
		ArrayList<ValueLabel> schMthdList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<ScheduleMethod> searchObject = new JdbcSearchObject<ScheduleMethod>(ScheduleMethod.class);
		searchObject.addSort("SchdMethod", false);

		List<ScheduleMethod> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel schMthdLabel = new ValueLabel(String.valueOf(appList.get(i).getSchdMethod()), appList.get(i).getSchdMethodDesc());
			schMthdList.add(schMthdLabel);
		}
		return schMthdList;
	}

	public static ArrayList<ValueLabel> getProfitDaysBasis() {
		ArrayList<ValueLabel> pftDaysList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<InterestRateBasisCode> searchObject = new JdbcSearchObject<InterestRateBasisCode>(InterestRateBasisCode.class);
		searchObject.addSort("IntRateBasisCode", false);

		List<InterestRateBasisCode> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftDaysLabel = new ValueLabel(String.valueOf(appList.get(i).getIntRateBasisCode()), appList.get(i).getIntRateBasisDesc());
			pftDaysList.add(pftDaysLabel);
		}
		return pftDaysList;
	}

	public static ArrayList<ValueLabel> getProfitRateTypes() {
		ArrayList<ValueLabel> pftRateList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<InterestRateType> searchObject = new JdbcSearchObject<InterestRateType>(InterestRateType.class);
		searchObject.addSort("IntRateTypeCode", false);

		List<InterestRateType> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftRateLabel = new ValueLabel(String.valueOf(appList.get(i).getIntRateTypeCode()), appList.get(i).getIntRateTypeDesc());
			pftRateList.add(pftRateLabel);
		}
		return pftRateList;
	}

	public static ArrayList<ValueLabel> getRepayMethods() {
		ArrayList<ValueLabel> repayMthdList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<RepaymentMethod> searchObject = new JdbcSearchObject<RepaymentMethod>(RepaymentMethod.class);
		searchObject.addSort("RepayMethod", false);

		List<RepaymentMethod> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel repayMthdLabel = new ValueLabel(String.valueOf(appList.get(i).getRepayMethod()), appList.get(i).getRepayMethodDesc());
			repayMthdList.add(repayMthdLabel);
		}
		return repayMthdList;
	}

	public static ArrayList<ValueLabel> getDepositRestrictedTo() {
		ArrayList<ValueLabel> depositRestrictedTo = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<LovFieldDetail> searchObject = new JdbcSearchObject<LovFieldDetail>(LovFieldDetail.class);
		searchObject.addSort("FieldCodeValue", false);
		searchObject.addFilter(new Filter("FieldCode", "DRESTO", Filter.OP_EQUAL));
		List<LovFieldDetail> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel repayMthdLabel = new ValueLabel(String.valueOf(appList.get(i).getFieldCodeId()), appList.get(i).getFieldCodeValue() + "-" + appList.get(i).getValueDesc());
			depositRestrictedTo.add(repayMthdLabel);
		}
		return depositRestrictedTo;
	}

	public static ArrayList<ValueLabel> getReviewRateAppliedPeriods() {
		ArrayList<ValueLabel> reviewRateAppliedPeriodsList = new ArrayList<ValueLabel>(4);
		//reviewRateAppliedPeriodsList.add(new ValueLabel("INCPRP", Labels.getLabel("label_Include_Past_Review_Periods")));
		reviewRateAppliedPeriodsList.add(new ValueLabel("RVWUPR", Labels.getLabel("label_Current_Future_Unpaid_Review_Periods")));
		reviewRateAppliedPeriodsList.add(new ValueLabel("RVWALL", Labels.getLabel("label_All_Current_Future_Review_Periods")));
		return reviewRateAppliedPeriodsList;
	}

	public static ArrayList<ValueLabel> getSchCalCodes() {

		ArrayList<ValueLabel> schCalCodesList = new ArrayList<ValueLabel>();
		schCalCodesList.add(new ValueLabel("CURPRD", Labels.getLabel("label_Current_Period")));
		schCalCodesList.add(new ValueLabel("TILLMDT", Labels.getLabel("label_Till_Maturity")));
		schCalCodesList.add(new ValueLabel("ADJMDT", Labels.getLabel("label_Adj_To_Maturity")));
		schCalCodesList.add(new ValueLabel("TILLDATE", Labels.getLabel("label_Till_Date")));
		schCalCodesList.add(new ValueLabel("ADDTERM", Labels.getLabel("label_Add_Terms")));
		schCalCodesList.add(new ValueLabel("ADDLAST", Labels.getLabel("label_Add_Last")));
		schCalCodesList.add(new ValueLabel("ADJTERMS", Labels.getLabel("label_Adj_Terms")));

		return schCalCodesList;

	}

	/**
	 * Method for getting List of RuleTypes For RepaymentRules
	 * 
	 * @return
	 */
	public static List<ValueLabel> getRuleTypes() {
		ArrayList<ValueLabel> ruleTypeList = new ArrayList<ValueLabel>();
		ruleTypeList.add(new ValueLabel("EREPAY", Labels.getLabel("label_EREPAY")));
		ruleTypeList.add(new ValueLabel("ESETTLE", Labels.getLabel("label_ESETTLE")));
		return ruleTypeList;
	}

	/**
	 * Method for getting List of ScreenCodes For FinanceWorkFlowDef
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getScreenCodes() {

		ArrayList<ValueLabel> screenCodesList = new ArrayList<ValueLabel>();
		screenCodesList.add(new ValueLabel("DDE", Labels.getLabel("label_DDE")));
		screenCodesList.add(new ValueLabel("QDE", Labels.getLabel("label_QDE")));

		return screenCodesList;

	}

	public static ArrayList<ValueLabel> getAnswer() {
		ArrayList<ValueLabel> answerList = new ArrayList<ValueLabel>();
		answerList.add(new ValueLabel("A", Labels.getLabel("label_QuestionDialog_AnswerA.value")));
		answerList.add(new ValueLabel("B", Labels.getLabel("label_QuestionDialog_AnswerB.value")));
		answerList.add(new ValueLabel("C", Labels.getLabel("label_QuestionDialog_AnswerC.value")));
		answerList.add(new ValueLabel("D", Labels.getLabel("label_QuestionDialog_AnswerD.value")));
		return answerList;
	}

	/**
	 * Method for Returning List of Financial Years using in Balance Sheet
	 * Details
	 * 
	 * @return
	 */
	public static List<ValueLabel> getFinancialYears() {
		ArrayList<ValueLabel> FinancialYearList = new ArrayList<ValueLabel>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int currentYear = calendar.get(Calendar.YEAR);
		int nextYear = currentYear % 100;

		for (int i = 3; i >= 0; i--) {

			String financialYear = "";
			if (nextYear - i + 1 < 10) {
				financialYear = (currentYear - i) + "/0" + (nextYear - i + 1);
			} else {
				financialYear = (currentYear - i) + "/" + (nextYear - i + 1);
			}
			FinancialYearList.add(new ValueLabel(financialYear, financialYear));
		}
		return FinancialYearList;
	}

	/**
	 * Method for getting List of AcHeadCode For RMTAccountTypes
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getInternalAcHeadCodes() {

		ArrayList<ValueLabel> internalAcHeadCodes = new ArrayList<ValueLabel>();
		internalAcHeadCodes.add(new ValueLabel("8", Labels.getLabel("label_EIGHT")));
		internalAcHeadCodes.add(new ValueLabel("9", Labels.getLabel("label_NINE")));

		return internalAcHeadCodes;

	}

	/**
	 * Method for getting List of AcHeadCode For RMTAccountTypes
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getCustomerSystemAcHeadCodes() {

		ArrayList<ValueLabel> customerSystemAcHeadCodes = new ArrayList<ValueLabel>();
		customerSystemAcHeadCodes.add(new ValueLabel("6", Labels.getLabel("label_SIX")));
		customerSystemAcHeadCodes.add(new ValueLabel("7", Labels.getLabel("label_SEVEN")));

		return customerSystemAcHeadCodes;

	}

	/**
	 * Method for getting List of Additional Field List For ExtendedFieldDetails
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getAdditionalFieldList() {

		ArrayList<ValueLabel> fieldSelection = new ArrayList<ValueLabel>();
		fieldSelection.add(new ValueLabel("Country", Labels.getLabel("label_CPCountry")));
		fieldSelection.add(new ValueLabel("City", Labels.getLabel("label_PCCity")));
		fieldSelection.add(new ValueLabel("Department", Labels.getLabel("label_CustEmpDept")));
		fieldSelection.add(new ValueLabel("Designation", Labels.getLabel("label_CustEmpDesg")));

		return fieldSelection;

	}

	/**
	 * Method for getting List of Additional Field Type List For
	 * ExtendedFieldDetails
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getFieldType() {

		ArrayList<ValueLabel> fieldType = new ArrayList<ValueLabel>();
		fieldType.add(new ValueLabel("TXT", Labels.getLabel("label_TXT")));
		fieldType.add(new ValueLabel("AMT", Labels.getLabel("label_AMT")));
		fieldType.add(new ValueLabel("DLIST", Labels.getLabel("label_DLIST")));
		fieldType.add(new ValueLabel("SLIST", Labels.getLabel("label_SLIST")));
		fieldType.add(new ValueLabel("DMLIST", Labels.getLabel("label_DMLIST")));
		fieldType.add(new ValueLabel("DATE", Labels.getLabel("label_DATE")));
		fieldType.add(new ValueLabel("DATETIME", Labels.getLabel("label_DATETIME")));
		fieldType.add(new ValueLabel("TIME", Labels.getLabel("label_TIME")));
		fieldType.add(new ValueLabel("RATE", Labels.getLabel("label_RATE")));
		fieldType.add(new ValueLabel("NUMERIC", Labels.getLabel("label_NUMERIC")));
		fieldType.add(new ValueLabel("RADIO", Labels.getLabel("label_RADIO")));
		fieldType.add(new ValueLabel("PRCT", Labels.getLabel("label_PRCT")));
		fieldType.add(new ValueLabel("CHKB", Labels.getLabel("label_CHKB")));
		fieldType.add(new ValueLabel("MTXT", Labels.getLabel("label_MTXT")));
		//fieldType.add(new ValueLabel("SHEAD", Labels.getLabel("label_SHEAD")));

		return fieldType;

	}

	/**
	 * Method for getting List of Additional Field Columns For
	 * ExtendedFieldDetails
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getFieldColumn() {

		ArrayList<ValueLabel> fieldColumn = new ArrayList<ValueLabel>();
		fieldColumn.add(new ValueLabel("LEFT", Labels.getLabel("label_Left")));
		fieldColumn.add(new ValueLabel("RIGHT", Labels.getLabel("label_Right")));

		return fieldColumn;

	}

	/**
	 * Method for getting List of Module Names 
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getModuleList() {

		String exclude_Modules = "AccountEngineEvent,AccountEngineRule,ApplicationDetails,AuditHeader,"
		        + "BasicFinanceType,CRBaseRateCode,CarLoanFor,CarUsage,DedupFields,DashboardConfiguration,"
		        + "DRBaseRateCode,FinanceMarginSlab,FinanceReferenceDetail,Frequency,GlobalVariable,"
		        + "HolidayMaster,LovFieldCode,LovFieldDetail,Notes,PFSParameter,Question,ReportList,"
		        + "ScoringSlab,ScoringType,WorkFlowDetails,PropertyType,MortgPropertyRelation,"
		        + "OwnerShipType,Ownership,Calender,ExtendedFieldDetail,HolidayDetails,WeekendDetails";

		ArrayList<ValueLabel> moduleName = new ArrayList<ValueLabel>();
		String moduleNames = PennantJavaUtil.getModuleMap().keySet().toString();
		moduleNames = moduleNames.substring(1, moduleNames.length() - 1);

		String[] modules = moduleNames.split(",");
		Arrays.sort(modules);

		for (int i = 0; i < modules.length; i++) {
			if (!exclude_Modules.contains(modules[i].trim())) {
				moduleName.add(new ValueLabel(modules[i].trim(), modules[i].trim()));
			}
		}
		return moduleName;
	}

	public static ArrayList<ValueLabel> getRuleDecider() {
		ArrayList<ValueLabel> arrRightType = new ArrayList<ValueLabel>();
		arrRightType.add(new ValueLabel(PennantConstants.CLAAMT, Labels.getLabel("label_TransactionEntryDialog_CalAmount.value")));
		arrRightType.add(new ValueLabel(PennantConstants.FEES, Labels.getLabel("label_TransactionEntryDialog_FeeCode.value")));
		return arrRightType;
	}

	public static ArrayList<ValueLabel> getReportListName() {
		ArrayList<ValueLabel> reportListName = new ArrayList<ValueLabel>();
		reportListName.add(new ValueLabel("List04", "ReportList04"));
		reportListName.add(new ValueLabel("List05", "ReportList05"));
		reportListName.add(new ValueLabel("List06", "ReportList06"));
		reportListName.add(new ValueLabel("List07", "ReportList07"));
		reportListName.add(new ValueLabel("List08", "ReportList08"));
		reportListName.add(new ValueLabel("List09", "ReportList09"));
		reportListName.add(new ValueLabel("List10", "ReportList10"));
		reportListName.add(new ValueLabel("Others", " "));

		return reportListName;
	}

	public static int getReportListColumns(String reportName) {
		reportName = StringUtils.trimToEmpty(reportName);

		if (reportName.equals("ReportList04")) {
			return 4;
		} else if (reportName.equals("ReportList05")) {
			return 5;
		} else if (reportName.equals("ReportList06")) {
			return 6;
		} else if (reportName.equals("ReportList07")) {
			return 7;
		} else if (reportName.equals("ReportList08")) {
			return 8;
		} else if (reportName.equals("ReportList09")) {
			return 9;
		} else if (reportName.equals("ReportList10")) {
			return 10;
		} else if (reportName.equals("ReportList11")) {
			return 11;
		} else if (reportName.equals("ReportList12")) {
			return 12;
		} else if (reportName.equals("ReportList13")) {
			return 13;
		} else if (reportName.equals("ReportList14")) {
			return 14;
		} else if (reportName.equals("ReportList15")) {
			return 15;
		}

		return 0;
	}

	public static ArrayList<ValueLabel> getChartDimensions() {
		if(chartDimensions==null){
			chartDimensions = new ArrayList<ValueLabel>();
			chartDimensions.add(new ValueLabel("2D", Labels.getLabel("label_Select_2D")));
			chartDimensions.add(new ValueLabel("3D", Labels.getLabel("label_Select_3D")));
		}
		return chartDimensions;
	}

	public static ArrayList<ValueLabel> getDashBoardType() {
		
		if(arrDashBoardtype==null){
			arrDashBoardtype = new ArrayList<ValueLabel>();
			arrDashBoardtype.add(new ValueLabel("", Labels.getLabel("common.Select")));
			arrDashBoardtype.add(new ValueLabel("bar", Labels.getLabel("label_Select_Bar")));
			arrDashBoardtype.add(new ValueLabel("column", Labels.getLabel("label_Select_Column")));
			arrDashBoardtype.add(new ValueLabel("line", Labels.getLabel("label_Select_Line")));
			arrDashBoardtype.add(new ValueLabel("area", Labels.getLabel("label_Select_Area")));
			arrDashBoardtype.add(new ValueLabel("pie", Labels.getLabel("label_Select_Pie")));
			arrDashBoardtype.add(new ValueLabel("Staked", Labels.getLabel("label_Select_Staked")));
			arrDashBoardtype.add(new ValueLabel("funnel", Labels.getLabel("label_Select_Funnel")));
			arrDashBoardtype.add(new ValueLabel("pyramid", Labels.getLabel("label_Select_Pyramid")));
			//arrDashBoardtype.add(new ValueLabel("Cylinder", Labels.getLabel("label_Select_Cylinder")));
			arrDashBoardtype.add(new ValueLabel("AGauge", Labels.getLabel("label_Select_AngularGauge")));
			//arrDashBoardtype.add(new ValueLabel("LGauge", Labels.getLabel("label_Select_HLinearGauge")));
		}
		return arrDashBoardtype;
	}

	public static ArrayList<ValueLabel> getDashBoardName() {
		ArrayList<ValueLabel> arrDashBoardtype = new ArrayList<ValueLabel>();
		arrDashBoardtype.add(new ValueLabel("Recordsinqueue", "Records in queue"));
		arrDashBoardtype.add(new ValueLabel("news", "News"));

		return arrDashBoardtype;
	}

	public static ArrayList<ValueLabel> getSeriesType() {
		ArrayList<ValueLabel> arrSeriesType = new ArrayList<ValueLabel>();
		arrSeriesType.add(new ValueLabel("", Labels.getLabel("common.Select")));
		arrSeriesType.add(new ValueLabel("monthly", Labels.getLabel("label_Select_monthly")));
		arrSeriesType.add(new ValueLabel("yearly", Labels.getLabel("label_Select_yearly")));

		return arrSeriesType;
	}

	public static ArrayList<ValueLabel> getDashboards() {
		ArrayList<ValueLabel> arrDashboards = new ArrayList<ValueLabel>();
		arrDashboards.add(new ValueLabel("DashBoard1", "DashBoard1"));
		arrDashboards.add(new ValueLabel("DashBoard2", "DashBoard2"));

		return arrDashboards;
	}

	public static ArrayList<ValueLabel> getWaiverDecider() {
		ArrayList<ValueLabel> waiverDeciders = new ArrayList<ValueLabel>();
		waiverDeciders.add(new ValueLabel("F", "Fees"));
		waiverDeciders.add(new ValueLabel("R", "Refund"));
		return waiverDeciders;
	}

	public static ArrayList<ValueLabel> getCarColors() {
		ArrayList<ValueLabel> carColors = new ArrayList<ValueLabel>();
		carColors.add(new ValueLabel("Silver", "Silver"));
		carColors.add(new ValueLabel("Black", "Black"));
		carColors.add(new ValueLabel("White", "White"));
		carColors.add(new ValueLabel("Red", "Red"));
		carColors.add(new ValueLabel("Blue", "Blue"));
		carColors.add(new ValueLabel("Brown", "Brown"));
		carColors.add(new ValueLabel("Green", "Green"));
		carColors.add(new ValueLabel("Yellow", "Yellow"));
		carColors.add(new ValueLabel("Gold", "Gold"));
		carColors.add(new ValueLabel("Beige", "Beige"));
		return carColors;
	}

	public static ArrayList<ValueLabel> getAddTermCodes() {
		ArrayList<ValueLabel> schCalCodesList = new ArrayList<ValueLabel>();
		schCalCodesList.add(new ValueLabel("MATURITY", Labels.getLabel("label_Maturity")));
		schCalCodesList.add(new ValueLabel("LAST REPAY", Labels.getLabel("label_LastRepay")));
		return schCalCodesList;
	}
	public static ArrayList<ValueLabel> getScheduleOn() {
		ArrayList<ValueLabel> schCalCodesList = new ArrayList<ValueLabel>();
		schCalCodesList.add(new ValueLabel(CalculationConstants.EARLYPAY_NOEFCT, Labels.getLabel("lable_No_Effect")));
		schCalCodesList.add(new ValueLabel(CalculationConstants.EARLYPAY_ADJMUR, Labels.getLabel("lable_Adjust_To_Maturity")));
		schCalCodesList.add(new ValueLabel(CalculationConstants.EARLYPAY_ADMPFI, Labels.getLabel("lable_Profit_Intact")));
		schCalCodesList.add(new ValueLabel(CalculationConstants.EARLYPAY_RECRPY, Labels.getLabel("lable_Recalculate_Schedule")));
		schCalCodesList.add(new ValueLabel(CalculationConstants.EARLYPAY_RECPFI, Labels.getLabel("lable_Recalculate_Intact")));
		return schCalCodesList;
	}
	
	public static ArrayList<ValueLabel> getODCChargeType() {
		ArrayList<ValueLabel> chargeType = new ArrayList<ValueLabel>();
		chargeType.add(new ValueLabel(PennantConstants.FLAT, Labels.getLabel("label_Flat")));
		chargeType.add(new ValueLabel(PennantConstants.PERCENTAGE, Labels.getLabel("label_Percentage")));
		return chargeType;
	}
	
	public static ArrayList<ValueLabel> getODCCalculatedOn() {
		ArrayList<ValueLabel> calculatedOn = new ArrayList<ValueLabel>();
		calculatedOn.add(new ValueLabel(PennantConstants.STOT, Labels.getLabel("label_ScheduleTotalBalance")));
		calculatedOn.add(new ValueLabel(PennantConstants.SPRI, Labels.getLabel("label_SchedulePrincipalBalance")));
		calculatedOn.add(new ValueLabel(PennantConstants.SPFT, Labels.getLabel("label_SchduleProfitBalance")));
		return calculatedOn;
	}
	
	public static ArrayList<ValueLabel> getODCChargeFor() {
		ArrayList<ValueLabel> finOdFor = new ArrayList<ValueLabel>();
		finOdFor.add(new ValueLabel(PennantConstants.SCHEDULE, "Schedule"));
		finOdFor.add(new ValueLabel(PennantConstants.DEFERED, "Deffered"));
		return finOdFor;		
	}
	
	/**
	 * Method for Enquiry List in Finance Enquiry List Screen
	 * @return
	 */
	public static ArrayList<ValueLabel> getEnquiryFilters() {
		ArrayList<ValueLabel> enquiries = new ArrayList<ValueLabel>();
		enquiries.add(new ValueLabel("ALLFIN", Labels.getLabel("label_AllFinances")));
		enquiries.add(new ValueLabel("ACTFIN", Labels.getLabel("label_ActiveFinances")));
		enquiries.add(new ValueLabel("MATFIN", Labels.getLabel("label_MaturityFinances")));
		enquiries.add(new ValueLabel("ODCFIN",  Labels.getLabel("label_OverDueFinances")));
		enquiries.add(new ValueLabel("SUSFIN", Labels.getLabel("label_SuspendFinances")));
		enquiries.add(new ValueLabel("GPFIN",  Labels.getLabel("label_GracePeriodFinances")));
		return enquiries;		
	}
	
	/**
	 * Method for Enquiry List in Finance Enquiry Dialog List Screen
	 * @return
	 */
	public static ArrayList<ValueLabel> getEnquiryTypes() {
		ArrayList<ValueLabel> enquiries = new ArrayList<ValueLabel>();
		enquiries.add(new ValueLabel("FINENQ", Labels.getLabel("label_FinanceEnquiry")));
		enquiries.add(new ValueLabel("SCHENQ", Labels.getLabel("label_ScheduleEnquiry")));
		enquiries.add(new ValueLabel("DOCENQ", Labels.getLabel("label_DocumentEnquiry")));
		enquiries.add(new ValueLabel("PSTENQ", Labels.getLabel("label_PostingsEnquiry")));
		enquiries.add(new ValueLabel("RPYENQ",  Labels.getLabel("label_RepaymentEnuiry")));
		enquiries.add(new ValueLabel("ODCENQ", Labels.getLabel("label_OverdueEnquiry")));
		enquiries.add(new ValueLabel("SUSENQ",  Labels.getLabel("label_SuspenseEnquiry")));
		//enquiries.add(new ValueLabel("CFSENQ",  Labels.getLabel("label_CustomerFinanceSummary")));
		//enquiries.add(new ValueLabel("CASENQ",  Labels.getLabel("label_CustomerAccountSummary")));
		return enquiries;		
	}
	
	public static ArrayList<ValueLabel> getTemplateFormat(){
		ArrayList<ValueLabel>  templateFormatList = new ArrayList<ValueLabel>(2);
		templateFormatList.add(new ValueLabel(PennantConstants.TEMPLATE_FORMAT_PLAIN, PennantJavaUtil.getLabel("common.template.format.plain")));
		templateFormatList.add(new ValueLabel(PennantConstants.TEMPLATE_FORMAT_HTML, PennantJavaUtil.getLabel("common.template.format.html")));
		return templateFormatList ;
	}
	
	public static ArrayList<ValueLabel> getRuleReturnType() {
		ArrayList<ValueLabel> returnTypeList = new ArrayList<ValueLabel>();
		returnTypeList.add(new ValueLabel("S", Labels.getLabel("label_String")));
		returnTypeList.add(new ValueLabel("D", Labels.getLabel("label_Decimal")));
		returnTypeList.add(new ValueLabel("I", Labels.getLabel("label_Integer")));
		returnTypeList.add(new ValueLabel("B", Labels.getLabel("label_Boolean")));
		return returnTypeList;
	}
	
	// Collateralitem
	public static ArrayList<ValueLabel> getLocations() {
		ArrayList<ValueLabel> locations = new ArrayList<ValueLabel>();
		locations.add(new ValueLabel("FINENQ", Labels.getLabel("label_FinanceEnquiry")));

		return locations;		
	}
	public static ArrayList<ValueLabel> getDepartments() {
		ArrayList<ValueLabel> departments = new ArrayList<ValueLabel>();
		departments.add(new ValueLabel("FINENQ", Labels.getLabel("label_HYCLC")));

		return departments;		
	}
	public static ArrayList<ValueLabel> getFrequencies() {
		ArrayList<ValueLabel> frequencies = new ArrayList<ValueLabel>();
		frequencies.add(new ValueLabel("Y01", "Y01"));

		return frequencies;		
	}
	public static ArrayList<ValueLabel> getInsuranceRequired() {
		ArrayList<ValueLabel> insuranceRequired = new ArrayList<ValueLabel>();
		insuranceRequired.add(new ValueLabel("N", "N"));
		insuranceRequired.add(new ValueLabel("Y", "Y"));
		
		return insuranceRequired;		
	}
	
	public static ArrayList<ValueLabel> getDocumentTypes() {
		ArrayList<ValueLabel> pftRateList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<DocumentType> searchObject = new JdbcSearchObject<DocumentType>(DocumentType.class);
		searchObject.addTabelName("BMTDocumentTypes_AView");

		List<DocumentType> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftRateLabel = new ValueLabel(String.valueOf(appList.get(i).getDocTypeCode()), appList.get(i).getDocTypeDesc());
			pftRateList.add(pftRateLabel);
		}
		return pftRateList;
	}
	
	public static Currency getCuurencyBycode(String ccyCode) {
		JdbcSearchObject<Currency> jdbcSearchObject = new JdbcSearchObject<Currency>(Currency.class);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		jdbcSearchObject.addFilterEqual("CcyCode", ccyCode);
		List<Currency> currencies = pagedListService.getBySearchObject(jdbcSearchObject);
		if (currencies != null && currencies.size() > 0) {
			return currencies.get(0);
		}
		return null;
	}
	
	
	public static ArrayList<ValueLabel> getInterestRateType() {
		ArrayList<ValueLabel> interestRateTypeList = new ArrayList<ValueLabel>();
		interestRateTypeList.add(new ValueLabel("R",Labels.getLabel("label_Reduce")));
		interestRateTypeList.add(new ValueLabel("F", Labels.getLabel("label_Flat")));
		interestRateTypeList.add(new ValueLabel("C", Labels.getLabel("label_Flat_Convert_Reduce")));
		interestRateTypeList.add(new ValueLabel("M", Labels.getLabel("label_Rate_Calc_Maturity")));
		return interestRateTypeList;
	}
	
	/**
	 * To convert the custome columns from a column seperated list to  FIELD array  
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public static Field[] getCustomColumns(String columns) throws Exception {
 		StringTokenizer fieldsStr = new StringTokenizer(columns, ",");
		Field[] fields = new Field[fieldsStr.countTokens()];

		int i =0;
		while (fieldsStr.hasMoreTokens()) {
			fields[i] = new Field(fieldsStr.nextToken());
			i++;
		}	
		return fields;
	}
	
}
