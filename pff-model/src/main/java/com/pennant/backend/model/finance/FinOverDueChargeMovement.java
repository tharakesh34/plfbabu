package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FinOverDueChargeMovement implements Serializable {
	private static final long serialVersionUID = 2921034763683596978L;

	private long id = 0;
	private long chargeId;
	private Long receiptID;
	private Long waiverID;
	private Date movementDate;
	private BigDecimal movementAmount = BigDecimal.ZERO;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private String status;

	public FinOverDueChargeMovement() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getChargeId() {
		return chargeId;
	}

	public void setChargeId(long chargeId) {
		this.chargeId = chargeId;
	}

	public Long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(Long receiptID) {
		this.receiptID = receiptID;
	}

	public Long getWaiverID() {
		return waiverID;
	}

	public void setWaiverID(Long waiverID) {
		this.waiverID = waiverID;
	}

	public Date getMovementDate() {
		return movementDate;
	}

	public void setMovementDate(Date movementDate) {
		this.movementDate = movementDate;
	}

	public BigDecimal getMovementAmount() {
		return movementAmount;
	}

	public void setMovementAmount(BigDecimal movementAmount) {
		this.movementAmount = movementAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getWaivedAmount() {
		return waivedAmount;
	}

	public void setWaivedAmount(BigDecimal waivedAmount) {
		this.waivedAmount = waivedAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
