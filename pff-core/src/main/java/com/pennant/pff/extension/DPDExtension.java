package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class DPDExtension {
	private DPDExtension() {
		super();
	}

	public static boolean VARTUAL_DPD;
	public static boolean EXCLUDE_VD_PART_PAYMENT;
	public static int DPD_STRING_LENGTH;

	static {
		VARTUAL_DPD = getValueAsBoolean("VARTUAL_DPD", true);
		EXCLUDE_VD_PART_PAYMENT = getValueAsBoolean("EXCLUDE_VD_PART_PAYMENT", false);
		DPD_STRING_LENGTH = getValueAsInt("DPD_STRING_LENGTH", 5);
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.DPD, key, defaultValue);
	}

	private static int getValueAsInt(String key, int defaultValue) {
		return FeatureExtension.getValueAsInt(Module.DPD, key, defaultValue);
	}
}
