package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;

public class FeeExtension {
	private FeeExtension() {
		super();
	}

	private static final String MODULE = "FEE";

	public static boolean FEE_SERVICEING_STAMPIN_ON_ORG;
	public static boolean ADD_FEEINFTV_ONCALC;
	public static boolean ALLOW_FEES_RECALCULATE;
	public static boolean ALLOW_PAID_FEE_SCHEDULE_METHOD;
	public static boolean UPFRONT_FEE_REVERSAL_REQ;
	public static boolean ALLOW_SINGLE_FEE_CONFIG;

	static {
		FEE_SERVICEING_STAMPIN_ON_ORG = getValueAsBoolean("FEE_SERVICEING_STAMPIN_ON_ORG", false);
		ADD_FEEINFTV_ONCALC = getValueAsBoolean("ADD_FEEINFTV_ONCALC", true);
		ALLOW_FEES_RECALCULATE = getValueAsBoolean("ALLOW_FEES_RECALCULATE", true);
		ALLOW_PAID_FEE_SCHEDULE_METHOD = getValueAsBoolean("ALLOW_PAID_FEE_SCHEDULE_METHOD", false);
		UPFRONT_FEE_REVERSAL_REQ = getValueAsBoolean("UPFRONT_FEE_REVERSAL_REQ", false);
		ALLOW_SINGLE_FEE_CONFIG = getValueAsBoolean("ALLOW_SINGLE_FEE_CONFIG", false);
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(MODULE, key, defaultValue);
	}
}
