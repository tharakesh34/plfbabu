package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;

public class LPPExtension {

	private LPPExtension() {
		super();
	}

	public static boolean LPP_DUE_CREATION_REQ;

	static {
		LPP_DUE_CREATION_REQ = getValueAsBoolean("LPP_DUE_CREATION_REQ", false);
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.LPP, key, defaultValue);
	}

}
