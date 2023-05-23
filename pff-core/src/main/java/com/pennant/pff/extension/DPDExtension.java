package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class DPDExtension {
	private DPDExtension() {
		super();
	}

	public static final boolean VARTUAL_DPD = getValueAsBoolean("VARTUAL_DPD", true);
	public static final boolean EXCLUDE_VD_PART_PAYMENT = getValueAsBoolean("EXCLUDE_VD_PART_PAYMENT", false);
	public static final int DPD_STRING_LENGTH = getValueAsInt("DPD_STRING_LENGTH", 5);
	public static final int DPD_STRING_CALCULATION_ON = getValueAsInt("DPD_STRING_CALCULATION_ON", 0);

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.DPD, key, defaultValue);
	}

	private static int getValueAsInt(String key, int defaultValue) {
		return FeatureExtension.getValueAsInt(Module.DPD, key, defaultValue);
	}
}
