package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class FinReceiptAllocationDetail {

	private long repayID = 0;
	private int allocationID = 0;
	private String allocationType;
	private String allocationTo;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	
	public FinReceiptAllocationDetail() {
		
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getRepayID() {
		return repayID;
	}
	public void setRepayID(long repayID) {
		this.repayID = repayID;
	}

	public int getAllocationID() {
		return allocationID;
	}
	public void setAllocationID(int allocationID) {
		this.allocationID = allocationID;
	}

	public String getAllocationType() {
		return allocationType;
	}
	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public String getAllocationTo() {
		return allocationTo;
	}
	public void setAllocationTo(String allocationTo) {
		this.allocationTo = allocationTo;
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

}
