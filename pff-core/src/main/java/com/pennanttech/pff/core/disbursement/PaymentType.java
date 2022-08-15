package com.pennanttech.pff.core.disbursement;

public enum PaymentType {
	IMPS("IMPS"), R("RTGS"), N("NEFT"), D("DD"), C("CHEQUE"), I("I"), TRANSFER("TRANSFER"), RTGS("R"), NEFT("N"),
	DD("D"), CHEQUE("C");

	private final String value;

	private PaymentType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
