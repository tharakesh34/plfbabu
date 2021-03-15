package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

public class FinExcessMovement {

	private long excessID = 0;
	/** it hold the presentment id in case of movement from presentment **/
	private Long receiptID;
	private String movementType;
	private String tranType;
	private BigDecimal amount = BigDecimal.ZERO;
	private String movementFrom;
	private Date schDate;

	public FinExcessMovement() {
		super();
	}

	public FinExcessMovement copyEntity() {
		FinExcessMovement entity = new FinExcessMovement();
		entity.setExcessID(this.excessID);
		entity.setReceiptID(this.receiptID);
		entity.setMovementType(this.movementType);
		entity.setTranType(this.tranType);
		entity.setAmount(this.amount);
		entity.setMovementFrom(this.movementFrom);
		entity.setSchDate(this.schDate);
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

	public Long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(Long receiptID) {
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

	public String getMovementFrom() {
		return movementFrom;
	}

	public void setMovementFrom(String movementFrom) {
		this.movementFrom = movementFrom;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

}
