package com.pennant.backend.model.finance;

import java.math.BigDecimal;

import com.pennant.backend.model.Entity;

public class FinExcessAmount implements Entity {

	private long excessID = 0;
	private String finReference;
	private String amountType;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal utilisedAmt = BigDecimal.ZERO;
	private BigDecimal reservedAmt = BigDecimal.ZERO;
	private BigDecimal balanceAmt = BigDecimal.ZERO;
	
	public FinExcessAmount() {
		
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public long getId() {
		return excessID;
	}

	@Override
	public void setId(long id) {
		this.excessID = id;
	}
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

	public BigDecimal getUtilisedAmt() {
		return utilisedAmt;
	}
	public void setUtilisedAmt(BigDecimal utilisedAmt) {
		this.utilisedAmt = utilisedAmt;
	}

	public BigDecimal getReservedAmt() {
		return reservedAmt;
	}
	public void setReservedAmt(BigDecimal reservedAmt) {
		this.reservedAmt = reservedAmt;
	}

	public BigDecimal getBalanceAmt() {
		return balanceAmt;
	}
	public void setBalanceAmt(BigDecimal balanceAmt) {
		this.balanceAmt = balanceAmt;
	}
	
}
