package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class PartnerBankExtension {
	private PartnerBankExtension() {
		super();
	}

	public static final boolean BRANCH_WISE_MAPPING = getValueAsBoolean("BRANCH_WISE_MAPPING", false);
	public static final String BRANCH_OR_CLUSTER = getValueAsString("BRANCH_OR_CLUSTER", "B");
	public static final String CLUSTER_TYPE = getValueAsString("CLUSTER_TYPE", "");

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.PARTNERBANK, key, defaultValue);
	}

	private static String getValueAsString(String key, String defaultValue) {
		return FeatureExtension.getValueAsString(Module.PARTNERBANK, key, defaultValue);
	}

}
