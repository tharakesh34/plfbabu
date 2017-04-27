package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class FinExcessAmountReserve {

	private long excessID = 0;
	private long receiptID = 0;
	private BigDecimal reservedAmt = BigDecimal.ZERO;
	
	public FinExcessAmountReserve() {
		
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

	public long getReceiptID() {
		return receiptID;
	}
	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public BigDecimal getReservedAmt() {
		return reservedAmt;
	}
	public void setReservedAmt(BigDecimal reservedAmt) {
		this.reservedAmt = reservedAmt;
	}

}
