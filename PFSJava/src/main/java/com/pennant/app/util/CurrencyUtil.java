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
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.model.applicationmaster.Currency;

/**
 * A suite of utilities surrounding the use of the Currency that contain
 * information about the environment for the system.
 */
public class CurrencyUtil {
	private final static Logger					logger				= Logger.getLogger(CurrencyUtil.class);

	private static CurrencyDAO					currencyDAO;
	private static HashMap<String, Currency>	currrecnyDetails	= null;

	/**
	 * Initialization of <b>SysParamUtil</b> class.
	 * 
	 */
	public static void Init() {
		currrecnyDetails = null;
		getParmList();
	}

	/**
	 * Get the List of System urrencies
	 * 
	 * @return HashMap
	 */
	public static HashMap<String, Currency> getParmList() {
		logger.debug("Entering");
		final List<Currency> currencies = getCurrencyDAO().getCurrencyList();
		if (currencies != null) {
			currrecnyDetails = new HashMap<String, Currency>(currencies.size());
			for (int i = 0; i < currencies.size(); i++) {
				currrecnyDetails.put(currencies.get(i).getCcyCode(), currencies.get(i));
			}
		}
		logger.debug("Leaving");
		return currrecnyDetails;
	}

	public static void setCurrencyDetails(String code, Currency currency) {
		logger.debug("Entering");
		if (currrecnyDetails != null) {
			currrecnyDetails.remove(code);
			currrecnyDetails.put(code, currency);
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
		return currrecnyDetails.get(ccy);
	}

	/**
	 * Method for get minimum currency unit's of Currency
	 * 
	 * @param parmCode
	 * @return
	 */
	public static int getFormat(String ccy) {
		
		if(ccy == null){
			ccy = SysParamUtil.getAppCurrency();
		}
		
		Currency currecny = currrecnyDetails.get(ccy);
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
		
		if(ccy == null){
			ccy = SysParamUtil.getAppCurrency();
		}
		
		Currency currecny = currrecnyDetails.get(ccy);
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
		Currency currecny = currrecnyDetails.get(ccy);
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
		Currency currecny = currrecnyDetails.get(ccy);
		if (currecny != null) {
			return currecny.getCcySpotRate();
		}
		return BigDecimal.ZERO;
	}

	public static CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		CurrencyUtil.currencyDAO = currencyDAO;
	}
}
