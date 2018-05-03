package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class FinExcessMovement {

	private long excessID = 0;
	private long receiptID = 0;
	private String movementType;
	private String tranType;
	private BigDecimal amount = BigDecimal.ZERO;
	
	public FinExcessMovement() {
		
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

	public String getMovementType() {
		return movementType;
	}
	public void setMovementType(String movementType) {
		this.movementType = movementType;
	}

	public String getTranType() {
		return tranType;
	}
	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
