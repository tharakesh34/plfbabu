package com.pennant.backend.model.finance;

import java.math.BigDecimal;

import com.pennant.backend.model.Entity;

public class ReceiptAllocationDetail implements Entity{
    
	private long receiptAllocationid = Long.MIN_VALUE;
	private long receiptID = 0;
	private int allocationID = 0;
	private String allocationType;
	private String typeDesc;
	private long allocationTo;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal paidGST = BigDecimal.ZERO;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	
	public ReceiptAllocationDetail() {
		
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//


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

	public long getAllocationTo() {
		return allocationTo;
	}
	public void setAllocationTo(long allocationTo) {
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

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}
	
	public long getReceiptAllocationid() {
		return receiptAllocationid;
	}
	public void setReceiptAllocationid(long receiptAllocationid) {
		this.receiptAllocationid = receiptAllocationid;
	}

	public boolean isNew() {
		return false;
	}

	public long getId() {
		return receiptID;
	}

	public void setId(long id) {
		this.receiptID=id;
	}

	public BigDecimal getPaidGST() {
		return paidGST;
	}

	public void setPaidGST(BigDecimal paidGST) {
		this.paidGST = paidGST;
	}
	
}
