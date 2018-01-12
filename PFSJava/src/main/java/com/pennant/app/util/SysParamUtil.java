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
 * FileName    		:  SysParamUtil.java													*                           
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
package com.pennant.app.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.smtmasters.PFSParameterService;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * A suite of utilities surrounding the use of the system parameters that contain information about the environment for
 * the system.
 */
public class SysParamUtil {
	private static final Logger logger = Logger.getLogger(SysParamUtil.class);
	
	private static PFSParameterService systemParameterService;
	private static List<GlobalVariable> globalVariablesList = null;
	
	/**
	 * Enumerates the system parameter codes.
	 */
	public enum Param {
		APP_DATE("APP_DATE"), APP_DFT_CURR("APP_DFT_CURR"), AUTOHUNTING("AUTOHUNTING"), APP_VALUEDATE("APP_VALUEDATE");

		private final String code;

		private Param(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	/**
	 * Returns a {@link java.util.String} object that represents the application
	 * Currency.
	 */
	public static String getAppCurrency() {
		return SysParamUtil.getValueAsString(Param.APP_DFT_CURR.getCode());
	}
	
	/**
	 * Convenience method for getting the value of a system parameter.
	 * 
	 * @param code
	 *            The code of the parameter to access.
	 * @return The value of the parameter.
	 * @throws IllegalArgumentException
	 *             - If the given code is <code>null</code>.
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
	 * @param code
	 *            The code of the parameter to access.
	 * @return The value of the parameter as a String. If the parameter is empty, an empty String "" is returned.
	 * @throws IllegalArgumentException
	 *             - If the given code is <code>null</code>.
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
	 * Convenience method for getting the value of a system parameter as a Date.
	 * 
	 * @param code
	 *            The code of the parameter to access.
	 * @return The value of the parameter as a Date. If the parameter is empty, <code>null</code> is returned.
	 * @throws IllegalArgumentException
	 *             - If the given code is <code>null</code>.
	 */
	public static Date getValueAsDate(String code) {
		if (code == null) {
			throw new IllegalArgumentException();
		}

		return (Date) getSystemParameterValue(getParamByID(code));
	}

	/**
	 * Convenience method for getting the value of a system parameter as an integer value.
	 * 
	 * @param code
	 *            The code of the parameter to access.
	 * @return The value of the parameter as an integer value.
	 * @throws IllegalArgumentException
	 *             - If the given code is <code>null</code>.
	 * @throws NumberFormatException
	 *             - If the parameter does not contain a parsable integer.
	 */
	public static int getValueAsInt(String code) {
		if (code == null) {
			throw new IllegalArgumentException();
		}

		return Integer.parseInt(getValueAsString(code));
	}

	public static void updateParamDetails(String code, String value) {
		logger.debug(Literal.ENTERING);
		systemParameterService.update(code, value, "");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Get the List of System Parameters
	 * 
	 * @return HashMap
	 */
	public static List<GlobalVariable> getGlobaVariableList() {
		logger.debug("Entering");

		if (globalVariablesList == null) {
			globalVariablesList = systemParameterService.getGlobaVariables();
		}
		logger.debug("Leaving");
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
	 * @param ParmCode
	 *            (PFSParameter)
	 * 
	 * @return object
	 */
	private static Object getSystemParameterValue(PFSParameter parameter) {
		Object object = null;
		if (parameter != null) {
			String parmType = StringUtils.trimToEmpty(parameter.getSysParmType()).trim();
			String strValue = parameter.getSysParmValue();

			if ("Date".equalsIgnoreCase(parmType)) {
				object = DateUtility.getDBDate(strValue);
			} else if ("Double".equalsIgnoreCase(parmType)) {
				BigDecimal doubleValue = new BigDecimal(strValue);
				object = doubleValue.divide(BigDecimal.valueOf(Math.pow(10, parameter.getSysParmDec())));
			} else {
				object = strValue;
			}
		}
		return object;
	}
			
	private static PFSParameter getParamByID(String code){
		return systemParameterService.getParameterByCode(StringUtils.trimToEmpty(code));
	}

	public static void setSystemParameterService(PFSParameterService systemParameterService) {
		SysParamUtil.systemParameterService = systemParameterService;
	}	
}
