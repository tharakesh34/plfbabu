package com.pennant.backend.model.finance;

import java.math.BigDecimal;

/**
 * Bean values only for Income Details against Tax For ="LPP/LPI" while on taking receipt.
 * Same can be utilized for Receipt cancellation also.
 */
public class FinTaxIncomeDetail {
	
	private long repayID;
	private String taxFor;
	private BigDecimal receivedAmount = BigDecimal.ZERO;
	private BigDecimal CGST = BigDecimal.ZERO;
	private BigDecimal SGST = BigDecimal.ZERO;
	private BigDecimal UGST = BigDecimal.ZERO;
	private BigDecimal IGST = BigDecimal.ZERO;
	
	public long getRepayID() {
		return repayID;
	}
	public void setRepayID(long repayID) {
		this.repayID = repayID;
	}
	
	public String getTaxFor() {
		return taxFor;
	}
	public void setTaxFor(String taxFor) {
		this.taxFor = taxFor;
	}
	
	public BigDecimal getReceivedAmount() {
		return receivedAmount;
	}
	public void setReceivedAmount(BigDecimal receivedAmount) {
		this.receivedAmount = receivedAmount;
	}
	
	public BigDecimal getCGST() {
		return CGST;
	}
	public void setCGST(BigDecimal cGST) {
		CGST = cGST;
	}
	
	public BigDecimal getSGST() {
		return SGST;
	}
	public void setSGST(BigDecimal sGST) {
		SGST = sGST;
	}
	
	public BigDecimal getUGST() {
		return UGST;
	}
	public void setUGST(BigDecimal uGST) {
		UGST = uGST;
	}
	
	public BigDecimal getIGST() {
		return IGST;
	}
	public void setIGST(BigDecimal iGST) {
		IGST = iGST;
	}
	
}
