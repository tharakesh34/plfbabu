package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class ReceiptTaxDetail {
	private long		receiptSeqID			= 0;
	private long		receiptID			= 0;
	private String  	taxComponent;
	private BigDecimal 	paidCGST = BigDecimal.ZERO;
	private BigDecimal 	paidSGST = BigDecimal.ZERO;
	private BigDecimal 	paidUGST = BigDecimal.ZERO;
	private BigDecimal 	paidIGST = BigDecimal.ZERO;
	private BigDecimal 	totalGST = BigDecimal.ZERO;
	
	public long getReceiptSeqID() {
		return receiptSeqID;
	}
	
	public void setReceiptSeqID(long receiptSeqID) {
		this.receiptSeqID = receiptSeqID;
	}
	
	public String getTaxComponent() {
		return taxComponent;
	}
	
	public void setTaxComponent(String taxComponent) {
		this.taxComponent = taxComponent;
	}
	
	public BigDecimal getPaidCGST() {
		return paidCGST;
	}
	
	public void setPaidCGST(BigDecimal paidCGST) {
		this.paidCGST = paidCGST;
	}
	
	public BigDecimal getPaidSGST() {
		return paidSGST;
	}
	
	public void setPaidSGST(BigDecimal paidSGST) {
		this.paidSGST = paidSGST;
	}
	
	public BigDecimal getPaidUGST() {
		return paidUGST;
	}
	
	public void setPaidUGST(BigDecimal paidUGST) {
		this.paidUGST = paidUGST;
	}
	
	public BigDecimal getPaidIGST() {
		return paidIGST;
	}
	
	public void setPaidIGST(BigDecimal paidIGST) {
		this.paidIGST = paidIGST;
	}
	
	public BigDecimal getTotalGST() {
		return totalGST;
	}
	
	public void setTotalGST(BigDecimal totalGST) {
		this.totalGST = totalGST;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

}
