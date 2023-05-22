package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class FeeExtension {
	private FeeExtension() {
		super();
	}

	public static boolean FEE_SERVICEING_STAMPIN_ON_ORG;
	public static boolean ADD_FEEINFTV_ONCALC;
	public static boolean ALLOW_FEES_RECALCULATE;
	public static boolean ALLOW_PAID_FEE_SCHEDULE_METHOD;
	public static boolean UPFRONT_FEE_REVERSAL_REQ;
	public static boolean ALLOW_SINGLE_FEE_CONFIG;
	public static boolean FEE_ODC_DISABLE;

	static {
		FEE_SERVICEING_STAMPIN_ON_ORG = getValueAsBoolean("FEE_SERVICEING_STAMPIN_ON_ORG", false);
		ADD_FEEINFTV_ONCALC = getValueAsBoolean("ADD_FEEINFTV_ONCALC", true);
		ALLOW_FEES_RECALCULATE = getValueAsBoolean("ALLOW_FEES_RECALCULATE", true);
		ALLOW_PAID_FEE_SCHEDULE_METHOD = getValueAsBoolean("ALLOW_PAID_FEE_SCHEDULE_METHOD", false);
		UPFRONT_FEE_REVERSAL_REQ = getValueAsBoolean("UPFRONT_FEE_REVERSAL_REQ", false);
		ALLOW_SINGLE_FEE_CONFIG = getValueAsBoolean("ALLOW_SINGLE_FEE_CONFIG", false);
		FEE_ODC_DISABLE = getValueAsBoolean("FEE_ODC_DISABLE", true);
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.FEE, key, defaultValue);
	}
}
