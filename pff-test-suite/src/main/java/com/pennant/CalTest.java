package com.pennant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.PennantConstants;

/**
 * Process for Testing Days calculation on Interest basis codes
 *
 */
public class CalTest {
	
	public static void main(String[] args) {
		
		Date startDate = DateUtility.getUtilDate("29/01/2013", PennantConstants.dateFormat);
		Date endDate = DateUtility.getUtilDate("28/02/2013", PennantConstants.dateFormat);
		
		System.out.println("\n **** Calculation Values with start Date: 29/01/2013 & End Date : 28/02/2013 *** \n");
		System.out.println("----------------------------------------------------------------------------------");
		
		System.out.println("Method: 30U/360  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "30U/360").multiply(new BigDecimal(360)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: 30E/360  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "30E/360").multiply(new BigDecimal(360)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: 30E/360I  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "30E/360I").multiply(new BigDecimal(360)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: 30E+/360  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "30E+/360").multiply(new BigDecimal(360)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: A/A_360  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "A/A_360").multiply(new BigDecimal(360)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: A/A_365F  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "A/A_365F").multiply(new BigDecimal(365)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: A/A_365L  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "A/A_365L").multiply(new BigDecimal(365)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: A/A_AFB  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "A/A_AFB").setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: A/A_ICMA  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "A/A_ICMA").setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: A/A_ISDA  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "A/A_ISDA").setScale(0, RoundingMode.HALF_DOWN));
		
		startDate = DateUtility.getUtilDate("29/07/2013", PennantConstants.dateFormat);
		endDate = DateUtility.getUtilDate("31/07/2013", PennantConstants.dateFormat);
		
		System.out.println("\n **** Calculation Values with start Date: 29/07/2013 & End Date : 31/07/2013 *** \n");
		System.out.println("----------------------------------------------------------------------------------");
		
		System.out.println("Method: 30U/360  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "30U/360").multiply(new BigDecimal(360)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: 30E/360  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "30E/360").multiply(new BigDecimal(360)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: 30E/360I  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "30E/360I").multiply(new BigDecimal(360)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: 30E+/360  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "30E+/360").multiply(new BigDecimal(360)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: A/A_360  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "A/A_360").multiply(new BigDecimal(360)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: A/A_365F  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "A/A_365F").multiply(new BigDecimal(365)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: A/A_365L  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "A/A_365L").multiply(new BigDecimal(365)).setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: A/A_AFB  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "A/A_AFB").setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: A/A_ICMA  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "A/A_ICMA").setScale(0, RoundingMode.HALF_DOWN));
		System.out.println("Method: A/A_ISDA  ---> " + CalculationUtil.getInterestDays(startDate, endDate, "A/A_ISDA").setScale(0, RoundingMode.HALF_DOWN));

	}

}
