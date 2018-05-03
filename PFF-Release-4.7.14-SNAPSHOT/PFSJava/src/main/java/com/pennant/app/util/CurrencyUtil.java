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
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * A suite of utilities surrounding the use of the Currency that contain information about the environment for the
 * system.
 */
public class CurrencyUtil {
	private static final Logger					logger		= Logger.getLogger(CurrencyUtil.class);

	private static CurrencyDAO					currencyDAO;
	private static HashMap<String, Currency>	currencies	= new HashMap<>();

	/**
	 * Initialize the currency map with the list of currencies that are available in the system.
	 */
	public static void init() {
		logger.info(Literal.ENTERING);

		for (Currency currency : currencyDAO.getCurrencyList()) {
			currencies.put(currency.getCcyCode(), currency);
		}

		logger.info(Literal.LEAVING);
	}

	public static void register(Currency currency, String type) {
		logger.debug("Entering");

		if (PennantConstants.TRAN_DEL.equals(type)) {
			currencies.remove(currency.getCcyCode());
		} else {
			currencies.put(currency.getCcyCode(), currency);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for get the Record Data of Currency
	 * 
	 * @param parmCode
	 * @return
	 */
	public static Currency getCurrencyObject(String ccy) {
		return getCurrency(ccy);
	}

	/**
	 * Method for get minimum currency unit's of Currency
	 * 
	 * @param parmCode
	 * @return
	 */
	public static int getFormat(String ccy) {

		if (StringUtils.isEmpty(ccy)) {
			ccy = SysParamUtil.getAppCurrency();
		}

		Currency currecny = getCurrency(ccy);
		if (currecny != null) {
			return currecny.getCcyEditField();
		}
		return 0;
	}

	/**
	 * Method for get Currency Object Description for Display
	 * 
	 * @param parmCode
	 * @return
	 */
	public static String getCcyDesc(String ccy) {

		if (StringUtils.isEmpty(ccy)) {
			ccy = SysParamUtil.getAppCurrency();
		}

		Currency currecny = getCurrency(ccy);
		if (currecny != null) {
			return currecny.getCcyDesc();
		}
		return "";
	}

	/**
	 * Method for get minimum currency unit's of Currency
	 * 
	 * @param parmCode
	 * @return
	 */
	public static String getCcyNumber(String ccy) {
		Currency currecny = getCurrency(ccy);
		if (currecny != null) {
			return currecny.getCcyNumber();
		}
		return "";
	}

	/**
	 * Method for get the Exchange rate of Currency
	 * 
	 * @param parmCode
	 * @return
	 */
	public static BigDecimal getExChangeRate(String ccy) {
		Currency currecny = getCurrency(ccy);
		if (currecny != null) {
			return currecny.getCcySpotRate();
		}
		return BigDecimal.ZERO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		CurrencyUtil.currencyDAO = currencyDAO;
	}
	
	public static Currency getCurrency(String ccy) {
		if (StringUtils.isEmpty(ccy)) {
			ccy = SysParamUtil.getAppCurrency();
		}
		return currencyDAO.getCurrency(ccy);
	}
}
