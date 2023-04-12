package com.pennanttech.pff.schdule;

public enum RepaymentStatus {
	PAID("Paid"),

	PARTIALLY_PAID("Partially Paid"),

	UNPAID("Unpaid");

	private String repaymentStatus;

	private RepaymentStatus(String repaymentStatus) {
		this.repaymentStatus = repaymentStatus;
	}

	public String repaymentStatus() {
		return this.repaymentStatus;
	}
}
