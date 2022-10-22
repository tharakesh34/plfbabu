package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class DPDExtension {
	private DPDExtension() {
		super();
	}

	public static boolean VARTUAL_DPD;
	public static boolean EXCLUDE_VD_PART_PAYMENT;

	static {
		VARTUAL_DPD = getValueAsBoolean("VARTUAL_DPD", true);
		EXCLUDE_VD_PART_PAYMENT = getValueAsBoolean("EXCLUDE_VD_PART_PAYMENT", false);
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.DPD, key, defaultValue);
	}
}
