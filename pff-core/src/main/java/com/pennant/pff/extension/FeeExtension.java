package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class FeeExtension {
	private FeeExtension() {
		super();
	}

	public static final boolean FEE_SERVICEING_STAMPIN_ON_ORG = getValueAsBoolean("FEE_SERVICEING_STAMPIN_ON_ORG",
			false);
	public static final boolean ADD_FEEINFTV_ONCALC = getValueAsBoolean("ADD_FEEINFTV_ONCALC", true);
	public static final boolean ALLOW_FEES_RECALCULATE = getValueAsBoolean("ALLOW_FEES_RECALCULATE", true);
	public static final boolean ALLOW_PAID_FEE_SCHEDULE_METHOD = getValueAsBoolean("ALLOW_PAID_FEE_SCHEDULE_METHOD",
			false);
	public static final boolean UPFRONT_FEE_REVERSAL_REQ = getValueAsBoolean("UPFRONT_FEE_REVERSAL_REQ", false);
	public static final boolean ALLOW_SINGLE_FEE_CONFIG = getValueAsBoolean("ALLOW_SINGLE_FEE_CONFIG", false);
	public static final boolean FEE_ODC_DISABLE = getValueAsBoolean("FEE_ODC_DISABLE", true);

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.FEE, key, defaultValue);
	}
}
