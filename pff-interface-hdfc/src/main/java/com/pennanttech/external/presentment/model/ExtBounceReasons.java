package com.pennanttech.external.presentment.model;

import java.util.HashMap;
import java.util.Map;

public class ExtBounceReasons {

	public static ExtBounceReasons bounceReasons;

	// to changes to list
	public Map<String, ExtBounceReason> bounceData = new HashMap<String, ExtBounceReason>();

	public static ExtBounceReasons getInstance() {
		if (bounceReasons == null) {
			bounceReasons = new ExtBounceReasons();
		}
		return bounceReasons;
	}

	public Map<String, ExtBounceReason> getBounceData() {
		return bounceData;
	}

	public void setBounceData(Map<String, ExtBounceReason> bounceData) {
		this.bounceData = bounceData;
	}

}
