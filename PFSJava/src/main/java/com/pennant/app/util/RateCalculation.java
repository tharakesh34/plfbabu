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

public class RateCalculation {

	public static final double tol = 0.001;    

	/**
	 * http://stackoverflow.com/questions/36789967/java-program-to-calculate-xirr-without-using-excel-or-any-other-library
	 * @param payments
	 * @param repayDates
	 * @return
	 */
	public static BigDecimal calculateXIRR(List<BigDecimal> payments, List<Date> repayDates) {

		BigDecimal xirr = new BigDecimal(0.1);	// guess
		BigDecimal xirr_One = BigDecimal.ZERO;
		BigDecimal xirr_Res = BigDecimal.ZERO;
		double err = 1E+100;

		while (err > tol) {

			BigDecimal schAmount = BigDecimal.ZERO;
			Date finStartDate = repayDates.get(0);
			Date repayDate; 
			double dateDiff;
			BigDecimal fValue = BigDecimal.ZERO;
			BigDecimal fDerivative = BigDecimal.ZERO;

			for (int i = 0; i < payments.size(); i++) {

				repayDate = repayDates.get(i);
				schAmount = payments.get(i);
				dateDiff = DateUtility.getDaysBetween(finStartDate, repayDate) * -1 ;

				fValue = fValue.add(schAmount.multiply(BigDecimal.valueOf(Math.pow((xirr.doubleValue() + 1.0), (dateDiff / 365.0)))));
				fDerivative = fDerivative.add(schAmount.multiply(BigDecimal.valueOf((1.0 / 365.0) * dateDiff * Math.pow((xirr.doubleValue() + 1.0), (dateDiff / 365.0) - 1.0))));
			}

			xirr_One = xirr.subtract(fValue.divide(fDerivative, 15, RoundingMode.HALF_DOWN));
			err = Math.abs(xirr_One.subtract(xirr).doubleValue());
			xirr = xirr_One;
		}
		xirr_Res = xirr.multiply(new BigDecimal(100));
		
		if(xirr_Res == null){
			xirr_Res = BigDecimal.ZERO;
		}else if(xirr_Res.compareTo(new BigDecimal(9999))>0){
			xirr_Res = new BigDecimal(9999);
		}
		xirr_Res = xirr_Res.setScale(9,RoundingMode.HALF_DOWN);
		return xirr_Res; // Percentage
	}

	/**
	 * https://apache.googlesource.com/poi/+/4d81d34d5d566cb22f21999e653a5829cc678ed5/src/java/org/apache/poi/ss/formula/functions/Irr.java
	 * @param payments
	 * @return
	 */
	public static BigDecimal calculateIRR(List<BigDecimal> payments) {

		int maxIterationCount = 20;
		double absoluteAccuracy = 1E-7;
		BigDecimal guess = BigDecimal.ZERO;;
		BigDecimal irr;
		int i = 0;

		while (i < maxIterationCount) {

			// the value of the function (NPV) and its derivation can be calculated in the same loop
			BigDecimal fValue = BigDecimal.ZERO;
			BigDecimal fDerivative = BigDecimal.ZERO;

			for (int k = 0; k < payments.size(); k++) {

				fValue = fValue.add(payments.get(k).divide(BigDecimal.valueOf(Math.pow(1.0 + guess.doubleValue(), k)), 15, RoundingMode.HALF_DOWN));
				fDerivative = fDerivative.add(BigDecimal.valueOf(-k).multiply(payments.get(k).divide(BigDecimal.valueOf(Math.pow(1.0 + guess.doubleValue(), k + 1)), 15, RoundingMode.HALF_DOWN)));
			}

			// the essence of the NewtonRaphson Method
			irr = guess.subtract(fValue.divide(fDerivative, 15, RoundingMode.HALF_DOWN));

			if (Math.abs(irr.subtract(guess).doubleValue()) <= absoluteAccuracy) {
				return irr.multiply(new BigDecimal(100)); // Percentage
			}
			guess = irr;
			++i;
		}
		// maximum number of iterations is exceeded
		return new BigDecimal(Double.NaN);
	}
}