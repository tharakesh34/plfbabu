package com.pennant.pff.knockoff;

public enum KnockOffType {

	MANUAL("M"),

	AUTO("A"),

	CROSS_LOAN("C");

	private String code;

	private KnockOffType(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
