package com.pennanttech.pff.constants;


public enum QueryModule {
	LOAN_ORIGINATION(1, "Loan Origination"),
	SAMPLING(2, "Sampling"),
	LEGAL_VERIFICATION(3, "Legal verification");

	private final Integer key;
	private final String value;

	private QueryModule(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
	public static QueryModule getModuleType(Integer key) {
		for (QueryModule type : values()) {
			if ((type.getKey()) == key) {
				return type;
			}
		}
		return null;
	}

}
