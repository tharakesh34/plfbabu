package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class ReceiptAllocationDetail {

	private long receiptID = 0;
	private int allocationID = 0;
	private String allocationType;
	private String allocationTo;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	
	public ReceiptAllocationDetail() {
		
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getReceiptID() {
		return receiptID;
	}
	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
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
