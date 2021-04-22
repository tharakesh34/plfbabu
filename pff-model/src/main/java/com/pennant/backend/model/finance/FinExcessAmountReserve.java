package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;

public class FinExcessAmountReserve implements Serializable {
	private static final long serialVersionUID = 2348967326702773559L;

	private long excessID = 0;
	private long receiptSeqID = 0;
	private String paymentType;
	private BigDecimal reservedAmt = BigDecimal.ZERO;

	public FinExcessAmountReserve() {
		super();
	}

	public FinExcessAmountReserve copyEntity() {
		FinExcessAmountReserve entity = new FinExcessAmountReserve();
		entity.setExcessID(this.excessID);
		entity.setReceiptSeqID(this.receiptSeqID);
		entity.setPaymentType(this.paymentType);
		entity.setReservedAmt(this.reservedAmt);
		return entity;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getExcessID() {
		return excessID;
	}

	public void setExcessID(long excessID) {
		this.excessID = excessID;
	}

	public BigDecimal getReservedAmt() {
		return reservedAmt;
	}

	public void setReservedAmt(BigDecimal reservedAmt) {
		this.reservedAmt = reservedAmt;
	}

	public long getReceiptSeqID() {
		return receiptSeqID;
	}

	public void setReceiptSeqID(long receiptSeqID) {
		this.receiptSeqID = receiptSeqID;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

}
