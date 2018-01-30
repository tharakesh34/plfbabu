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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.model.RateDetail;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.applicationmaster.SplRateDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class RateUtil implements Serializable {

	private static final long	serialVersionUID	= 3360714221070313516L;
	private static Logger		logger				= Logger.getLogger(RateUtil.class);

	private static BaseRateDAO	baseRateDAO;
	private static SplRateDAO	splRateDAO;

	/**
	 * To Calculate Effective Rate Based on Base rate and Special rate Codes.
	 * 
	 * parameters are base rate code , Special rate code , Date and action. date will compared as less than or equal to
	 * the given date returns Base rate,Special rate ,Reference Rate in a HashMap
	 */
	public static RateDetail getRefRate(RateDetail rateDetail) {
		logger.debug("Entering");
		boolean error = false;

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();

		if (rateDetail == null) {
			rateDetail = new RateDetail();
			errorDetails.add(new ErrorDetail(" ", "30561",
					new String[] { PennantJavaUtil.getLabel("label_BaseRate") }, new String[] { "null" }));
			error = true;
		}

		if (!error
				&& (StringUtils.isEmpty(StringUtils.trimToEmpty(rateDetail.getBaseRateCode())) && StringUtils
						.isEmpty(StringUtils.trimToEmpty(rateDetail.getSplRateCode())))) {
			errorDetails.add(new ErrorDetail("Rate", "30559", new String[] {
					PennantJavaUtil.getLabel("label_BaseRate"), PennantJavaUtil.getLabel("label_SplRateCode") },
					new String[] { "null" }));
			error = true;
		}

		if (!error && rateDetail.getValueDate() == null) {
			errorDetails.add(new ErrorDetail("Date", "30559", new String[] { PennantJavaUtil.getLabel("label_Date") },
					new String[] { "null" }));
			error = true;
		}

		if (!error) {
			String[] valueParm = new String[2];
			valueParm[1] = DateUtility.formatUtilDate(rateDetail.getValueDate(), PennantConstants.DBDateFormat);

			String[] errorParm = new String[2];
			errorParm[1] = PennantJavaUtil.getLabel("label_Date") + ": " + valueParm[1];

			if (StringUtils.isNotEmpty(rateDetail.getBaseRateCode())
					&& StringUtils.isNotEmpty(rateDetail.getCurrency())) {
				BaseRate baseRate = getBaseRateDAO().getBaseRateByType(rateDetail.getBaseRateCode(),
						rateDetail.getCurrency(), rateDetail.getValueDate());
				if (baseRate == null) {
					rateDetail.setBaseRefRate(BigDecimal.ZERO);
				} else {
					rateDetail.setBaseRefRate(baseRate.getBRRate());
				}
			} else {
				rateDetail.setBaseRefRate(BigDecimal.ZERO);
			}

			if (rateDetail.getBaseRefRate() == null) {

				valueParm[0] = rateDetail.getBaseRateCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_BaseRate") + ": " + valueParm[0];

				errorDetails.add(new ErrorDetail("BaseRate", "41002", errorParm, valueParm));
				rateDetail.setBaseRefRate(BigDecimal.ZERO);
				error = true;
			}

			if (!error) {
				if (StringUtils.isNotBlank(rateDetail.getSplRateCode())) {
					BigDecimal splRate = getSplRateDAO().getSplRateByID(rateDetail.getSplRateCode(),
							rateDetail.getValueDate()).getSRRate();
					rateDetail.setSplRefRate(splRate == null ? BigDecimal.ZERO: splRate);
				} else {
					rateDetail.setSplRefRate(BigDecimal.ZERO);
				}

				if (rateDetail.getSplRefRate() == null) {
					valueParm[0] = rateDetail.getSplRateCode();
					errorParm[0] = PennantJavaUtil.getLabel("label_SplRateCode") + ": " + valueParm[0];
					errorDetails.add(new ErrorDetail("SplRate", "41002", errorParm, valueParm));
					rateDetail.setBaseRefRate(BigDecimal.ZERO);
					error = true;
				}
			}
		}

		if (error) {
			errorDetails = ErrorUtil.getErrorDetails(errorDetails, SessionUserDetails.getUserLanguage());
		} else {
			// NET RATE.
			// FOR Loans: Base Rate - Special Rate + Margin
			// FOR Deposits: Base Rate + Special Rates - Margin
			rateDetail.setNetRefRateDeposit(rateDetail.getBaseRefRate().add(rateDetail.getSplRefRate())
					.subtract(rateDetail.getMargin()));
			rateDetail.setNetRefRateLoan(rateDetail.getBaseRefRate().subtract(rateDetail.getSplRefRate())
					.add(rateDetail.getMargin()));
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
	 *            BaseRateCode currency splRateCode SplRateCode Decimalbox rateBox
	 */
	public static RateDetail rates(String baseRateCode, String currency, String splRateCode, BigDecimal margin,
			BigDecimal minRate, BigDecimal maxRate) {
		logger.debug("Entering");

		RateDetail rate = new RateDetail();
		rate.setBaseRateCode(baseRateCode);
		rate.setCurrency(currency);
		rate.setSplRateCode(splRateCode);
		rate.setMargin(margin);
		rate.setValueDate(DateUtility.getAppDate());
		rate = getRefRate(rate);
		rate.setNetRefRateLoan(getEffRate(rate.getNetRefRateLoan(), minRate, maxRate));
		logger.debug("Leaving");

		return rate;
	}

	/**
	 * Calculates The Rate and Shows the value in the rateBox
	 * 
	 * @param baseRateCode
	 *            BaseRateCode splRateCode SplRateCode Decimalbox rateBox
	 */
	public static RateDetail rates(String baseRateCode, String currency, String splRateCode, BigDecimal margin,
			Date valueDate, BigDecimal minRate, BigDecimal maxRate) {
		logger.debug("Entering");
		RateDetail rate = new RateDetail();
		rate.setBaseRateCode(baseRateCode);
		rate.setCurrency(currency);
		rate.setSplRateCode(splRateCode);
		rate.setMargin(margin);
		rate.setValueDate(valueDate);
		rate = getRefRate(rate);
		rate.setNetRefRateLoan(getEffRate(rate.getNetRefRateLoan(), minRate, maxRate));
		logger.debug("Leaving");
		return rate;
	}

	/**
	 * Calculates The Rate and Shows the value in the rateBox
	 * 
	 * @param baseRateCode
	 *            BaseRateCode splRateCode SplRateCode Decimalbox rateBox
	 */
	public static BigDecimal ratesFromLoadedData(FinScheduleData finScheduleData, int iSchd) {
		logger.debug("Entering");
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		BigDecimal minRate = BigDecimal.ZERO;
		BigDecimal maxRate = BigDecimal.ZERO;
		BigDecimal netRefRate = BigDecimal.ZERO;
		BigDecimal calRate = BigDecimal.ZERO;
		BigDecimal baseRateVal = BigDecimal.ZERO;
		BigDecimal splRateVal = BigDecimal.ZERO;

		//Set Minimum and Maximum Rate
		if (finSchdDetails.get(iSchd).getSchDate().compareTo(finMain.getGrcPeriodEndDate()) < 0) {
			minRate = finMain.getGrcMinRate();
			maxRate = finMain.getGrcMaxRate();
		} else {
			minRate = finMain.getRpyMinRate();
			maxRate = finMain.getRpyMaxRate();
		}

		//Check Reference OR Not
		if (StringUtils.isBlank(finSchdDetails.get(iSchd).getBaseRate())) {
			netRefRate = finSchdDetails.get(iSchd).getActRate();
			calRate = getEffRate(netRefRate, minRate, maxRate);
			logger.debug("Leaving");
			return calRate;
		}

		//Get Base Rate
		List<BaseRate> baseRates = finScheduleData.getBaseRates();

		for (int i = 0; i < baseRates.size(); i++) {
			if (baseRates.get(i).getBREffDate().compareTo(finSchdDetails.get(iSchd).getSchDate()) > 0) {
				break;
			}

			if (!StringUtils.equals(finSchdDetails.get(iSchd).getBaseRate(), baseRates.get(i).getBRType())) {
				continue;
			}

			baseRateVal = baseRates.get(i).getBRRate();
		}

		//Get Special Rate
		List<SplRate> splRates = finScheduleData.getSplRates();

		for (int i = 0; i < splRates.size(); i++) {
			if (splRates.get(i).getSREffDate().compareTo(finSchdDetails.get(iSchd).getSchDate()) > 0) {
				break;
			}

			if (!StringUtils.equals(finSchdDetails.get(iSchd).getSplRate(), splRates.get(i).getSRType())) {
				continue;
			}

			splRateVal = splRates.get(i).getSRRate();
		}
		
		netRefRate = baseRateVal.subtract(splRateVal).add(finSchdDetails.get(iSchd).getMrgRate() == null? 
				BigDecimal.ZERO : finSchdDetails.get(iSchd).getMrgRate());
		calRate = getEffRate(netRefRate, minRate, maxRate);

		logger.debug("Leaving");
		return calRate;
	}

	/**
	 * Method for Validating minimum & maximum Rates requested by User for Reference Rates
	 * 
	 * @param effRate
	 * @param minRate
	 * @param maxRate
	 * @return
	 */
	public static BigDecimal getEffRate(BigDecimal effRate, BigDecimal minRate, BigDecimal maxRate) {
		logger.debug("Entering");

		if (minRate == null) {
			minRate = BigDecimal.ZERO;
		}
		if (maxRate == null) {
			maxRate = BigDecimal.ZERO;
		}

		if (effRate.compareTo(BigDecimal.ZERO) < 0) {
			effRate = BigDecimal.ZERO;
		}

		if (minRate.compareTo(BigDecimal.ZERO) == 0 && maxRate.compareTo(BigDecimal.ZERO) == 0) {
			logger.debug("Leaving");
			return effRate;
		}

		if (minRate.compareTo(BigDecimal.ZERO) != 0 && minRate.compareTo(effRate) > 0) {
			effRate = minRate;
		}

		if (maxRate.compareTo(BigDecimal.ZERO) != 0 && maxRate.compareTo(effRate) < 0) {
			effRate = maxRate;
		}

		logger.debug("Leaving");
		return effRate;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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
