package com.pennanttech.pennapps.pff.lialibility;

public enum LiabilitySource {
	CUSTOMER(1, "Customer"), SAMPLING(2, "Sampling");

	private final Integer key;
	private final String value;

	private LiabilitySource(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
