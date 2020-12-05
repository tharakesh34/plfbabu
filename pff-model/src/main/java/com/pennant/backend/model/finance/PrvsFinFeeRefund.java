package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class PrvsFinFeeRefund {
	private long feeId;
	//With GST
	private BigDecimal totRefundAmount = BigDecimal.ZERO;
	//GST
	private BigDecimal totRefundAmtGST = BigDecimal.ZERO;
	//Without GST
	private BigDecimal totRefundAmtOriginal = BigDecimal.ZERO;
	//TDS
	private BigDecimal totRefundAmtTDS = BigDecimal.ZERO;

	public long getFeeId() {
		return feeId;
	}

	public void setFeeId(long feeId) {
		this.feeId = feeId;
	}

	public BigDecimal getTotRefundAmount() {
		return totRefundAmount == null ? BigDecimal.ZERO : totRefundAmount;
	}

	public void setTotRefundAmount(BigDecimal totRefundAmount) {
		this.totRefundAmount = totRefundAmount;
	}

	public BigDecimal getTotRefundAmtGST() {
		return totRefundAmtGST == null ? BigDecimal.ZERO : totRefundAmtGST;
	}

	public void setTotRefundAmtGST(BigDecimal totRefundAmtGST) {
		this.totRefundAmtGST = totRefundAmtGST;
	}

	public BigDecimal getTotRefundAmtTDS() {
		return totRefundAmtTDS == null ? BigDecimal.ZERO : totRefundAmtTDS;
	}

	public void setTotRefundAmtTDS(BigDecimal totRefundAmtTDS) {
		this.totRefundAmtTDS = totRefundAmtTDS;
	}

	public BigDecimal getTotRefundAmtOriginal() {
		return totRefundAmtOriginal == null ? BigDecimal.ZERO : totRefundAmtOriginal;
	}

	public void setTotRefundAmtOriginal(BigDecimal totRefundAmtOriginal) {
		this.totRefundAmtOriginal = totRefundAmtOriginal;
	}

}
