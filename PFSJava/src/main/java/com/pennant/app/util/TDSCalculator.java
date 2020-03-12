package com.pennant.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.CalculationConstants;

public class TDSCalculator {

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
}
