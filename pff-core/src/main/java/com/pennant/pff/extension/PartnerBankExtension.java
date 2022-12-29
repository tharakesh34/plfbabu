package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class PartnerBankExtension {
	private PartnerBankExtension() {
		super();
	}

	public static boolean BRANCH_WISE_MAPPING;
	public static String MAPPING;

	static {
		BRANCH_WISE_MAPPING = getValueAsBoolean("BRANCH_WISE_MAPPING", false);
		MAPPING = getValueAsString("MAPPING", "B");
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.PARTNERBANK, key, defaultValue);
	}

	private static String getValueAsString(String key, String defaultValue) {
		return FeatureExtension.getValueAsString(Module.PARTNERBANK, key, defaultValue);
	}

}
