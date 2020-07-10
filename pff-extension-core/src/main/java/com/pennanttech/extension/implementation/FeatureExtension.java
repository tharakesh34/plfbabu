package com.pennanttech.extension.implementation;

import java.util.HashMap;
import java.util.Map;

public class FeatureExtension implements IFeatureExtension {
	static Map<String, Object> customConstants = new HashMap<>();

	/**
	 * <p>
	 * Override the implementation constants. Here the constant name should match with implementation constant variable
	 * name.
	 * </p>
	 * 
	 * example
	 * <p>
	 * <code>customConstants.put("ALLOW_FINACTYPES", false);</code>
	 * <code>customConstants.put("CLIENT_NAME", "Pennant Technologies Private Limited");</code>
	 * </p>
	 */
	public FeatureExtension() {
		super();

		/* Override the implementation constants here as specified in example. */

		customConstants.put("AUTO_EOD_REQUIRED", false);

		customConstants.put("ALLOW_IND_AS", true);

		customConstants.put("ALLOW_AUTO_KNOCK_OFF", true);

		customConstants.put("ALLOW_ADV_INT_EMI", true);

		customConstants.put("ALLOW_DSF_CASHCLT", true);

		customConstants.put("ALLOW_TDS_ON_FEE", true);

		customConstants.put("ALLOW_OD_LOANS", true);

		customConstants.put("ALLOW_CD_LOANS", true);

		customConstants.put("ALLOW_SCHOOL_ORG", false);

		customConstants.put("ALLOW_SAMPLING", false);

	}

	@Override
	public Map<String, Object> getCustomConstants() {
		return customConstants;
	}
}
