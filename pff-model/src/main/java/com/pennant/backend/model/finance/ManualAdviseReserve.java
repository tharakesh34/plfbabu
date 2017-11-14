package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class ManualAdviseReserve {

	private long adviseID = 0;
	private long receiptSeqID = 0;
	private BigDecimal reservedAmt = BigDecimal.ZERO;
	
	public ManualAdviseReserve() {
		
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getAdviseID() {
		return adviseID;
	}
	public void setAdviseID(long adviseID) {
		this.adviseID = adviseID;
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

}
