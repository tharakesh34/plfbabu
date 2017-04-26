package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

public class ManualAdviseMovements {
	
	private long movementID = Long.MIN_VALUE;
	private long adviseID = Long.MIN_VALUE;
	private long payAgainstID = Long.MIN_VALUE;
	private Date movementDate;
	private BigDecimal movementAmount;
	private BigDecimal paidAmount;
	private BigDecimal waivedAmount;
	private String status;
	
	// Getters and Setters
	
	public long getMovementID() {
		return movementID;
	}
	public void setMovementID(long movementID) {
		this.movementID = movementID;
	}
	
	public long getAdviseID() {
		return adviseID;
	}
	public void setAdviseID(long adviseID) {
		this.adviseID = adviseID;
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
	public long getPayAgainstID() {
		return payAgainstID;
	}
	public void setPayAgainstID(long payAgainstID) {
		this.payAgainstID = payAgainstID;
	}

}
