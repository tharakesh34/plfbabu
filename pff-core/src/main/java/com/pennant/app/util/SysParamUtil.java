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
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : SysParamUtil.java *
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
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.service.GlobalVariableService;
import com.pennanttech.pennapps.service.SysParamService;

/**
 * A suite of utilities surrounding the use of the system parameters that contain information about the environment for
 * the system.
 */
public class SysParamUtil {
	private static SysParamService systemParameterService;
	private static List<GlobalVariable> globalVariablesList = null;
	private static GlobalVariableService globalVariableService;

	/**
	 * Enumerates the system parameter codes.
	 */
	public enum Param {
		APP_DATE("APP_DATE"), APP_DFT_CURR("APP_DFT_CURR"), APP_VALUEDATE("APP_VALUEDATE");

		private final String code;

		private Param(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	/**
	 * Returns a {@link java.util.Date} object that represents the application date.
	 * 
	 * @return A {@link java.util.Date} that represents the application date.
	 */
	public static Date getAppDate() {
		return SysParamUtil.getValueAsDate(SysParamUtil.Param.APP_DATE.getCode());
	}

	/**
	 * Returns the string representation with the specified date format pattern of the application date.
	 * 
	 * @param dateFormat The format describing the date and time pattern.
	 * @return The formatted date string of the application date.
	 */
	public static String getAppDate(DateFormat dateFormat) {
		return DateUtil.format(getAppDate(), dateFormat);
	}

	/**
	 * Returns the string representation with the specified date format pattern of the application date.
	 * 
	 * @param dateFormat The format describing the date and time pattern.
	 * @return The formatted date string of the application date.
	 */
	public static String getAppDate(String dateFormat) {
		return DateUtil.format(getAppDate(), dateFormat);
	}

	/**
	 * Returns a {@link java.util.Date} object that represents the value date.
	 * 
	 * @return A {@link java.util.Date} that represents the value date.
	 * 
	 * 
	 */
	public static java.util.Date getAppValueDate() {
		return SysParamUtil.getValueAsDate(SysParamUtil.Param.APP_VALUEDATE.getCode());
	}

	public static String getAppValueDate(DateFormat dateFormat) {
		return DateUtil.format(SysParamUtil.getValueAsDate(SysParamUtil.Param.APP_VALUEDATE.getCode()), dateFormat);
	}

	/**
	 * Returns a {@link java.util.String} object that represents the application Currency.
	 */
	public static String getAppCurrency() {

		if (!ImplementationConstants.ALLOW_MULTI_CCY) {
			return ImplementationConstants.BASE_CCY;
		}

		return SysParamUtil.getValueAsString(Param.APP_DFT_CURR.getCode());
	}

	/**
	 * Convenience method for getting the value of a system parameter.
	 * 
	 * @param code The code of the parameter to access.
	 * @return The value of the parameter.
	 * @throws IllegalArgumentException - If the given code is <code>null</code>.
	 */
	public static Object getValue(String code) {
		if (code == null) {
			throw new IllegalArgumentException();
		}

		return getSystemParameterValue(getParamByID(code));
	}

	/**
	 * Convenience method for getting the value of a system parameter as a String.
	 * 
	 * @param code The code of the parameter to access.
	 * @return The value of the parameter as a String. If the parameter is empty, an empty String "" is returned.
	 * @throws IllegalArgumentException - If the given code is <code>null</code>.
	 */
	public static String getValueAsString(String code) {
		if (code == null) {
			throw new IllegalArgumentException();
		}

		Object value = getSystemParameterValue(getParamByID(code));

		if (value == null) {
			return null;
		}

		return value.toString();
	}

	/**
	 * This method will return the either {@link #getAppDate} or {@link #getAppValueDate} base on the System parameter
	 * <code>SET_POSTDATE_TO</code>
	 * 
	 * @return either {@link #getAppDate} or {@link #getAppValueDate}
	 */
	public static Date getPostDate() {
		String setPostingDateTo = getValueAsString(SMTParameterConstants.SET_POSTDATE_TO);

		if (!StringUtils.equals(setPostingDateTo, Param.APP_DATE.getCode())) {
			return getAppDate();
		} else {
			return getAppValueDate();
		}

	}

	public static boolean isAllowed(String code) {
		String valueAsString = getValueAsString(code);
		if (valueAsString == null) {
			return false;
		}

		return PennantConstants.YES.equalsIgnoreCase(valueAsString);
	}

	/**
	 * Convenience method for getting the value of a system parameter as a Date.
	 * 
	 * @param code The code of the parameter to access.
	 * @return The value of the parameter as a Date. If the parameter is empty, <code>null</code> is returned.
	 * @throws IllegalArgumentException - If the given code is <code>null</code>.
	 */
	public static Date getValueAsDate(String code) {
		if (code == null) {
			throw new IllegalArgumentException();
		}

		return (Date) getSystemParameterValue(getParamByID(code));
	}

	/**
	 * Returns the string representation with the specified pattern of the value date.
	 * 
	 * @param pattern The pattern describing the date and time format.
	 * @return The formatted date string of the value date.
	 * 
	 * @return A {@link java.util.Date} that represents the Next business date.
	 */
	public static String getValueDate(String pattern) {
		return DateUtil.format(SysParamUtil.getValueAsDate(SysParamUtil.Param.APP_VALUEDATE.getCode()), pattern);
	}

	/**
	 * Returns a {@link java.util.Date} object that represents the Next business date.
	 * 
	 * @return A {@link java.util.Date} that represents the Next business date.
	 */
	public static java.util.Date getNextBusinessdate() {
		return SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT);
	}

	/**
	 * Returns a {@link java.util.Date} object that represents the Next business date.
	 * 
	 * @return A {@link java.util.Date} that represents the Next business date.
	 */
	public static java.util.Date getLastBusinessdate() {
		return SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_LAST);
	}

	/**
	 * Convenience method for getting the value of a system parameter as an integer value.
	 * 
	 * @param code The code of the parameter to access.
	 * @return The value of the parameter as an integer value.
	 * @throws IllegalArgumentException - If the given code is <code>null</code>.
	 * @throws NumberFormatException    - If the parameter does not contain a parsable integer.
	 */
	public static int getValueAsInt(String code) {
		if (code == null) {
			throw new IllegalArgumentException();
		}

		return Integer.parseInt(getValueAsString(code));
	}

	/**
	 * Convenience method for getting the value of a system parameter as an Bigdecimal value.
	 * 
	 * @param code The code of the parameter to access.
	 * @return The value of the parameter as an Bigdecimal value.
	 * @throws IllegalArgumentException - If the given code is <code>null</code>.
	 * @throws NumberFormatException    - If the parameter does not contain a parsable decimal.
	 */
	public static BigDecimal getValueAsBigDecimal(String code) {
		if (code == null) {
			throw new IllegalArgumentException();
		}

		BigDecimal value = BigDecimal.ZERO;
		Object object = SysParamUtil.getValue(code);
		if (object != null) {
			value = (BigDecimal) object;
		}
		return value;
	}

	public static java.util.Date getDerivedAppDate() {
		java.util.Date appDate = SysParamUtil.getValueAsDate(SysParamUtil.Param.APP_DATE.getCode());

		java.util.Date sysDate = null;
		String prodEnv = SysParamUtil.getValueAsString("IS_PROD_ENV");
		if (StringUtils.equals(prodEnv, PennantConstants.YES)) {
			sysDate = DateUtil.getSysDate();
		} else {
			sysDate = SysParamUtil.getValueAsDate("SYS_DATE");
		}

		if (DateUtil.compare(DateUtil.getMonthEnd(appDate), appDate) == 0 && DateUtil.compare(sysDate, appDate) > 0) {
			appDate = sysDate;

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(appDate);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			appDate = calendar.getTime();
		}

		return appDate;
	}

	public static void updateParamDetails(String code, String value) {
		setParmDetails(code, value);
		systemParameterService.update(code, value, "");
	}

	private static Map<String, PFSParameter> parmDetails = null;

	public static void setParmDetails(String code, String value) {
		if (parmDetails != null) {
			PFSParameter pfsParameter = parmDetails.get(code);
			if (pfsParameter != null) {
				parmDetails.remove(code);
				pfsParameter.setSysParmValue(value);
				parmDetails.put(code, pfsParameter);
			}
		}
	}

	/**
	 * Get the List of System Parameters
	 * 
	 * @return HashMap
	 */
	public static List<GlobalVariable> getGlobaVariableList() {
		if (globalVariablesList == null) {
			globalVariablesList = getGlobalVariableService().getGlobalVariables();
		}
		return globalVariablesList;
	}

	/**
	 * Method for get the Record Data of PFSParameter
	 * 
	 * @param parmCode
	 * @return
	 */
	public static PFSParameter getSystemParameterObject(String parmCode) {
		return getParamByID(parmCode);
	}

	/**
	 * Get the System Parameter Value
	 * 
	 * @param ParmCode (PFSParameter)
	 * 
	 * @return object
	 */
	private static Object getSystemParameterValue(PFSParameter parameter) {
		Object object = null;
		if (parameter != null) {
			String parmType = StringUtils.trimToEmpty(parameter.getSysParmType()).trim();
			String strValue = parameter.getSysParmValue();

			if ("Date".equalsIgnoreCase(parmType)) {
				object = DateUtil.getSqlDate(DateUtil.parse(strValue, DateFormat.FULL_DATE));
			} else if ("Double".equalsIgnoreCase(parmType)) {
				BigDecimal doubleValue = new BigDecimal(strValue);
				object = doubleValue.divide(BigDecimal.valueOf(Math.pow(10, parameter.getSysParmDec())));
			} else {
				object = strValue;
			}
		}
		return object;
	}

	private static PFSParameter getParamByID(String code) {
		return systemParameterService.getParameter(StringUtils.trimToEmpty(code));
	}

	public static void setSystemParameterService(SysParamService systemParameterService) {
		SysParamUtil.systemParameterService = systemParameterService;
	}

	public static GlobalVariableService getGlobalVariableService() {
		return globalVariableService;
	}

	public static void setGlobalVariableService(GlobalVariableService globalVariableService) {
		SysParamUtil.globalVariableService = globalVariableService;
	}
}
