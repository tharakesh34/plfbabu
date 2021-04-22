package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.Entity;

public class FinAssetAmtMovement implements Entity, java.io.Serializable {
	private static final long serialVersionUID = -2091525494536161888L;

	private long finServiceInstID;
	private String finReference;
	private Date movementDate;
	private long movementOrder;
	private String movementType;
	private BigDecimal movementAmount;
	private BigDecimal availableAmt = BigDecimal.ZERO;
	private BigDecimal disbursedAmt = BigDecimal.ZERO;
	private BigDecimal sanctionedAmt = BigDecimal.ZERO;
	private BigDecimal revisedSanctionedAmt = BigDecimal.ZERO;
	private long lastMntBy;
	private Timestamp lastMntOn;

	public FinAssetAmtMovement() {
		super();
	}

	public FinAssetAmtMovement(long id) {
		super();
	}

	// setters / getters

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getMovementDate() {
		return movementDate;
	}

	public void setMovementDate(Date movementDate) {
		this.movementDate = movementDate;
	}

	public long getMovementOrder() {
		return movementOrder;
	}

	public void setMovementOrder(long movementOrder) {
		this.movementOrder = movementOrder;
	}

	public String getMovementType() {
		return movementType;
	}

	public void setMovementType(String movementType) {
		this.movementType = movementType;
	}

	public BigDecimal getMovementAmount() {
		return movementAmount;
	}

	public void setMovementAmount(BigDecimal movementAmount) {
		this.movementAmount = movementAmount;
	}

	public BigDecimal getAvailableAmt() {
		return availableAmt;
	}

	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}

	public BigDecimal getSanctionedAmt() {
		return sanctionedAmt;
	}

	public void setSanctionedAmt(BigDecimal sanctionedAmt) {
		this.sanctionedAmt = sanctionedAmt;
	}

	public BigDecimal getRevisedSanctionedAmt() {
		return revisedSanctionedAmt;
	}

	public void setRevisedSanctionedAmt(BigDecimal revisedSanctionedAmt) {
		this.revisedSanctionedAmt = revisedSanctionedAmt;
	}

	public long getFinServiceInstID() {
		return finServiceInstID;
	}

	public void setFinServiceInstID(long finServiceInstID) {
		this.finServiceInstID = finServiceInstID;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public void setId(long id) {
	}

	public BigDecimal getDisbursedAmt() {
		return disbursedAmt;
	}

	public void setDisbursedAmt(BigDecimal disbursedAmt) {
		this.disbursedAmt = disbursedAmt;
	}
}
