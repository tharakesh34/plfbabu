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
 * FileName    		:  RateUtil.java													*                           
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.model.RateDetail;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.applicationmaster.SplRateDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class RateUtil implements Serializable {
	
    private static final long serialVersionUID = 3360714221070313516L;
    private static Logger logger = Logger.getLogger(RateUtil.class);
    
	private static BaseRateDAO baseRateDAO;
	private static SplRateDAO splRateDAO;

	/**
	 * To Calculate Effective Rate Based on Base rate and Special rate Codes.
	 * 
	 * parameters are base rate code , Special rate code , Date and action. 
	 * date will compared as less than or equal to
	 * the given date returns Base rate,Special rate ,Reference Rate in a HashMap
	 */
	public static RateDetail getRefRate(RateDetail rateDetail) {
		logger.debug("Entering");
		boolean error = false;

		ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();

		if (rateDetail == null) {
			errorDetails.add(new ErrorDetails(" ", "S0005", new String[] { PennantJavaUtil
			        .getLabel("label_BaseRate") }, new String[] { "null" }));
			error = true;
		}

		if (!error && (StringUtils.trimToEmpty(rateDetail.getBaseRateCode()).equals("") && 
				StringUtils.trimToEmpty(rateDetail.getSplRateCode()).equals(""))) {
			errorDetails.add(new ErrorDetails("Rate", "S0003", new String[] {
			        PennantJavaUtil.getLabel("label_BaseRate"),
			        PennantJavaUtil.getLabel("label_SplRateCode") }, new String[] { "null" }));
			error = true;
		}

		if (!error && rateDetail.getValueDate() == null) {
			errorDetails.add(new ErrorDetails("Date", "S0003", new String[] { PennantJavaUtil
			        .getLabel("label_Date") }, new String[] { "null" }));
			error = true;
		}

		if (!error) {
			String[] valueParm = new String[2];
			valueParm[1] = DateUtility.formatUtilDate(rateDetail.getValueDate(),
			        PennantConstants.DBDateFormat);

			String[] errorParm = new String[2];
			errorParm[1] = PennantJavaUtil.getLabel("label_Date") + ": " + valueParm[1];

			if (!StringUtils.trimToEmpty(rateDetail.getBaseRateCode()).equals("")) {
				rateDetail.setBaseRefRate(getBaseRateDAO().getBaseRateByType(
				        rateDetail.getBaseRateCode(), rateDetail.getValueDate()).getBRRate());
			} else {
				rateDetail.setBaseRefRate(BigDecimal.ZERO);
			}

			if (rateDetail.getBaseRefRate() == null) {

				valueParm[0] = rateDetail.getBaseRateCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_BaseRate") + ": " + valueParm[0];

				errorDetails.add(new ErrorDetails("BaseRate", "41002", errorParm, valueParm));
				rateDetail.setBaseRefRate(BigDecimal.ZERO);
				error = true;
			}

			if (!error) {
				if (!StringUtils.trimToEmpty(rateDetail.getSplRateCode()).equals("")) {
					rateDetail.setSplRefRate(getSplRateDAO().getSplRateByID(
					        rateDetail.getSplRateCode(), rateDetail.getValueDate()).getSRRate());
				} else {
					rateDetail.setSplRefRate(BigDecimal.ZERO);
				}

				if (rateDetail.getSplRefRate() == null) {
					valueParm[0] = rateDetail.getSplRateCode();
					errorParm[0] = PennantJavaUtil.getLabel("label_SplRateCode") + ": "
					        + valueParm[0];
					errorDetails.add(new ErrorDetails("SplRate", "41002", errorParm, valueParm));
					rateDetail.setBaseRefRate(BigDecimal.ZERO);
					error = true;
				}
			}
		}

		if (error) {
			errorDetails = ErrorUtil.getErrorDetails(errorDetails,
			        SessionUserDetails.getUserLanguage());
		} else {
			// NET RATE.
			// FOR Loans: Base Rate - Special Rate + Margin
			// FOR Deposits: Base Rate + Special Rates - Margin
			rateDetail.setNetRefRateDeposit(rateDetail.getBaseRefRate()
			        .add(rateDetail.getSplRefRate()).subtract(rateDetail.getMargin()));
			rateDetail.setNetRefRateLoan(rateDetail.getBaseRefRate()
			        .subtract(rateDetail.getSplRefRate()).add(rateDetail.getMargin()));
		}

		if (errorDetails.size() > 0) {
			rateDetail.setErrorDetails(errorDetails.get(0));
		}

		logger.debug("Leaving");
		return rateDetail;
	}

	/**
	 * Calculates The Rate and Shows the value in the rateBox
	 * 
	 * @param baseRateCode
	 *            BaseRateCode splRateCode SplRateCode Decimalbox rateBox
	 */
	public static RateDetail rates(String baseRateCode, String splRateCode, BigDecimal margin) {
		logger.debug("Entering");
		RateDetail rate = new RateDetail();
		rate.setBaseRateCode(baseRateCode);
		rate.setSplRateCode(splRateCode);
		rate.setMargin(margin);
		rate.setValueDate((Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR));
		logger.debug("Leaving");
		return getRefRate(rate);
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setBaseRateDAO(BaseRateDAO baseRateDAO) {
		RateUtil.baseRateDAO = baseRateDAO;
	}
	public static BaseRateDAO getBaseRateDAO() {
		return baseRateDAO;
	}

	public static SplRateDAO getSplRateDAO() {
		return splRateDAO;
	}
	public void setSplRateDAO(SplRateDAO splRateDAO) {
		RateUtil.splRateDAO = splRateDAO;
	}

}
