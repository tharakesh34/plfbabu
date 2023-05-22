package com.pennant.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;

public class TDSCalculator {

	public static String RPY_SCHD_DTLS = "RPY_SCHD_DTLS";
	public static String SCHD_DTLS = "SCHD_DTLS";
	public static String FM_DTLS = "FM_DTLS";

	private static String TDS_ROUNDING_MODE;
	private static int TDS_ROUNDING_TARGET;
	private static BigDecimal TDS_PERCENTAGE = BigDecimal.ZERO;
	private static final BigDecimal HUNDRED = new BigDecimal(100);

	public static BigDecimal getTDSAmount(BigDecimal tdsAmount) {
		initilizeAttributes();

		tdsAmount = (tdsAmount.multiply(getPercentage())).divide(HUNDRED, 0, RoundingMode.HALF_DOWN);
		tdsAmount = CalculationUtil.roundAmount(tdsAmount, getRoundingMode(), getRoundingTarget());

		return tdsAmount;
	}

	public static boolean isTDSApplicable(FinanceMain fm) {
		return fm.isTDSApplicable() && PennantConstants.TDS_AUTO.equals(fm.getTdsType());
	}

	public static boolean isTDSApplicable(FinanceMain fm, boolean tdsRequired) {
		return tdsRequired && isTDSApplicable(fm);
	}

	public static boolean isTDSApplicable(FinanceMain fm, FeeType feeType) {
		return isTDSApplicable(fm, feeType.isTdsReq());
	}

	public static boolean isTDSApplicable(FinanceMain fm, FinFeeDetail fee) {
		return isTDSApplicable(fm, fee.isTdsReq());
	}

	public static boolean isTDSApplicable(FinanceMain fm, FinanceScheduleDetail schd) {
		return schd.isTDSApplicable() && PennantConstants.TDS_AUTO.equals(fm.getTdsType());
	}

	private static String getRoundingMode() {
		if (TDS_ROUNDING_MODE == null) {
			TDS_ROUNDING_MODE = SysParamUtil.getValueAsString(CalculationConstants.TDS_ROUNDINGMODE);
		}
		return TDS_ROUNDING_MODE;
	}

	private static int getRoundingTarget() {
		if (TDS_ROUNDING_TARGET == 0) {
			TDS_ROUNDING_TARGET = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		}
		return TDS_ROUNDING_TARGET;
	}

	private static BigDecimal getPercentage() {
		if (TDS_PERCENTAGE.compareTo(BigDecimal.ZERO) == 0) {
			TDS_PERCENTAGE = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
		}

		return TDS_PERCENTAGE;
	}

	private static void initilizeAttributes() {
		if (StringUtils.isEmpty(TDS_ROUNDING_MODE) || TDS_ROUNDING_TARGET == 0
				|| TDS_PERCENTAGE.compareTo(BigDecimal.ZERO) == 0) {

			TDS_ROUNDING_MODE = SysParamUtil.getValueAsString(CalculationConstants.TDS_ROUNDINGMODE);
			TDS_ROUNDING_TARGET = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
			TDS_PERCENTAGE = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
		}
	}

	public static BigDecimal getPercentage(Date date) {
		if (date != null && ImplementationConstants.ALLOW_TDS_PERC_BASED_ON_YEAR) {

			if (getPreviousDate().compareTo(date) >= 0) {
				return new BigDecimal(7.5);
			} else {
				return new BigDecimal(10);
			}
		} else {
			if (TDS_PERCENTAGE.compareTo(BigDecimal.ZERO) == 0) {
				TDS_PERCENTAGE = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			}
		}

		return TDS_PERCENTAGE;
	}

	private static Date getPreviousDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2021);
		cal.set(Calendar.MONTH, 2);
		cal.set(Calendar.DAY_OF_MONTH, 31);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
}
