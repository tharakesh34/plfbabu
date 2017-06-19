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
 *																							*
 * FileName    		:  RateCalculation.java                                                 * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;


public class RateCalculation {
	private static final Logger	logger					= Logger.getLogger(ScheduleCalculator.class);

	/**
	 * http://stackoverflow.com/questions/36789967/java-program-to-calculate-xirr-without-using-excel-or-any-other-library
	 * 
	 * @param payments
	 * @param repayDates
	 * @return
	 */
	public static BigDecimal calculateXIRR(List<BigDecimal> payments, List<Date> repayDates) {

		logger.debug("Entering");
		//Formula SUM(Payment(i) / ((1+XIRR) ** ((Date(i) - Date(0))/365))  = 0. Guess XIRR to achieve this
		BigDecimal xIRRLow = BigDecimal.ZERO;
		BigDecimal xIRRHigh = new BigDecimal(9999.9999);
		BigDecimal xirr = BigDecimal.ZERO;
		BigDecimal number2 = new BigDecimal(2);
		BigDecimal big100 = new BigDecimal(100);
		BigDecimal tolarance = new BigDecimal(1);

		
		BigDecimal payment = BigDecimal.ZERO;
		Date dateStart = repayDates.get(0);
		int days = 0;

		for (int i = 0; i < 50; i++) {
			xirr = xIRRLow.add(xIRRHigh).divide(number2);
			xirr = xirr.setScale(9, RoundingMode.HALF_DOWN);

			if (xirr.compareTo(xIRRHigh) == 0 || xirr.compareTo(xIRRLow) == 0) {
				logger.debug("Leaving");
				return xirr;
			}

			BigDecimal netOfValue = BigDecimal.ZERO;
			BigDecimal payOfValue = BigDecimal.ZERO;
			BigDecimal divisor = BigDecimal.ZERO;

			for (int j = 0; j < payments.size(); j++) {
				payment = payments.get(j);
				days = DateUtility.getDaysBetween(dateStart, repayDates.get(j));
				divisor = BigDecimal.valueOf(Math.pow((xirr.divide(big100).doubleValue() + 1.0), (days / 365.0)));
				payOfValue = payment.divide(divisor,9,RoundingMode.HALF_DOWN);
				netOfValue = netOfValue.add(payOfValue);
			}

			netOfValue = netOfValue.setScale(0, RoundingMode.HALF_DOWN);
			
			if (netOfValue.abs().compareTo(tolarance)<=0) {
				logger.debug("Leaving");
				return xirr;
			}

			if (netOfValue.compareTo(BigDecimal.ZERO) < 0) {
				xIRRHigh = xirr;
			} else {
				xIRRLow = xirr;
			}

		}

		logger.debug("Leaving");
		return xirr;
	}
}