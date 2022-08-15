package com.pennanttech.pff.receipt;

public enum ReceiptPurpose {
	NON(-1, "NON"), SCHDRPY(0, "SchdlRepayment"), EARLYRPY(1, "EarlyPayment"), EARLYSETTLE(2, "EarlySettlement"),
	EARLYSTLENQ(3, "EarlySettlementEnq"), RESTRUCTURE(4, "Restructure");

	private int index;
	private String code;

	private ReceiptPurpose(int index, String code) {
		this.index = index;
		this.code = code;
	}

	public int index() {
		return index;
	}

	public String code() {
		return code;
	}

	public static ReceiptPurpose purpose(String code) {
		ReceiptPurpose[] values = ReceiptPurpose.values();

		for (ReceiptPurpose rp : values) {
			if (rp.code().equals(code)) {
				return rp;
			}
		}

		return null;
	}
}
