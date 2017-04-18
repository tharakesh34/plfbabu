package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FinReceiptHeader {

	private long receiptID = 0;// Auto Generated Sequence
	private Date receiptDate;
	private String receiptType;
	private String recAgainst;
	private String reference;
	private String receiptPurpose;
	private String receiptMode;
	private String excessAdjustTo;
	private String allocationType;
	private BigDecimal receiptAmount = BigDecimal.ZERO;
	private String effectSchdMethod;

	private List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>(1);
	private List<FinExcessAmount> excessAmounts = new ArrayList<FinExcessAmount>(1);
	private List<ReceiptAllocationDetail> allocations = new ArrayList<ReceiptAllocationDetail>(1);
	
	public FinReceiptHeader() {
		
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

	public Date getReceiptDate() {
		return receiptDate;
	}
	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getReceiptType() {
		return receiptType;
	}
	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	public String getRecAgainst() {
		return recAgainst;
	}
	public void setRecAgainst(String recAgainst) {
		this.recAgainst = recAgainst;
	}

	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public String getReceiptPurpose() {
		return receiptPurpose;
	}
	public void setReceiptPurpose(String receiptPurpose) {
		this.receiptPurpose = receiptPurpose;
	}

	public String getExcessAdjustTo() {
		return excessAdjustTo;
	}
	public void setExcessAdjustTo(String excessAdjustTo) {
		this.excessAdjustTo = excessAdjustTo;
	}

	public String getAllocationType() {
		return allocationType;
	}
	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}
	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getEffectSchdMethod() {
		return effectSchdMethod;
	}
	public void setEffectSchdMethod(String effectSchdMethod) {
		this.effectSchdMethod = effectSchdMethod;
	}

	public List<FinReceiptDetail> getReceiptDetails() {
		return receiptDetails;
	}
	public void setReceiptDetails(List<FinReceiptDetail> receiptDetails) {
		this.receiptDetails = receiptDetails;
	}

	public String getReceiptMode() {
		return receiptMode;
	}
	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

	public List<FinExcessAmount> getExcessAmounts() {
		return excessAmounts;
	}
	public void setExcessAmounts(List<FinExcessAmount> excessAmounts) {
		this.excessAmounts = excessAmounts;
	}

	public List<ReceiptAllocationDetail> getAllocations() {
		return allocations;
	}
	public void setAllocations(List<ReceiptAllocationDetail> allocations) {
		this.allocations = allocations;
	}

}
