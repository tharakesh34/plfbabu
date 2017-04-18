package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class FinExcessAmount {

	private long excessID = 0;
	private String finReference;
	private String amountType;
	private BigDecimal amount = BigDecimal.ZERO;
	
	public FinExcessAmount() {
		
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

	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getAmountType() {
		return amountType;
	}
	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
}
