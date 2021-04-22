package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;

public class ManualAdviseReserve implements Serializable {
	private static final long serialVersionUID = -7770933426665736780L;

	private long adviseID = 0;
	private long receiptSeqID = 0;
	private BigDecimal reservedAmt = BigDecimal.ZERO;

	public ManualAdviseReserve() {
		super();
	}

	public ManualAdviseReserve copyEntity() {
		ManualAdviseReserve entity = new ManualAdviseReserve();
		entity.setAdviseID(this.adviseID);
		entity.setReceiptSeqID(this.receiptSeqID);
		entity.setReservedAmt(this.reservedAmt);
		return entity;
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
