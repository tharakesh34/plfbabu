package com.pennant.backend.dao.tds.receivables;

public enum TdsReceivablesTxnStatus {
	RECEIPTCANCEL("RC", "ReceiptId"), ADJUSTMENTCANCEL("AC", "TxnId"), RECEIVABLECANCEL("CC", "ReceivableId");

	private String code;
	private String columnName;

	private TdsReceivablesTxnStatus(String code, String columnName) {
		this.code = code;
		this.columnName = columnName;
	}

	public String getCode() {
		return code;
	}

	public String getColumnName() {
		return columnName;
	}

}
