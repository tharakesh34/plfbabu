package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

public class FinRepayHeader {

	private String finReference;
	private Date valueDate;
	private BigDecimal repayAmount;
	private String finEvent;
	private BigDecimal priAmount;
	private BigDecimal pftAmount;
	private BigDecimal totalRefund;
	private BigDecimal totalWaiver;
	private BigDecimal insRefund;
	private String repayAccountId;
	private String earlyPayEffMtd;
	private Date earlyPayDate;
	private boolean schdRegenerated;
	private long linkedTranId = 0;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	
	public Date getValueDate() {
    	return valueDate;
    }
	public void setValueDate(Date valueDate) {
    	this.valueDate = valueDate;
    }
	
	public BigDecimal getRepayAmount() {
    	return repayAmount;
    }
	public void setRepayAmount(BigDecimal repayAmount) {
    	this.repayAmount = repayAmount;
    }
	
	public String getFinEvent() {
    	return finEvent;
    }
	public void setFinEvent(String finEvent) {
    	this.finEvent = finEvent;
    }
	
	public BigDecimal getPriAmount() {
    	return priAmount;
    }
	public void setPriAmount(BigDecimal priAmount) {
    	this.priAmount = priAmount;
    }
	
	public BigDecimal getPftAmount() {
    	return pftAmount;
    }
	public void setPftAmount(BigDecimal pftAmount) {
    	this.pftAmount = pftAmount;
    }
	
	public BigDecimal getTotalRefund() {
    	return totalRefund;
    }
	public void setTotalRefund(BigDecimal totalRefund) {
    	this.totalRefund = totalRefund;
    }
	
	public BigDecimal getTotalWaiver() {
    	return totalWaiver;
    }
	public void setTotalWaiver(BigDecimal totalWaiver) {
    	this.totalWaiver = totalWaiver;
    }
	
	public BigDecimal getInsRefund() {
    	return insRefund;
    }
	public void setInsRefund(BigDecimal insRefund) {
    	this.insRefund = insRefund;
    }
	
	public String getRepayAccountId() {
    	return repayAccountId;
    }
	public void setRepayAccountId(String repayAccountId) {
    	this.repayAccountId = repayAccountId;
    }
	
	public String getEarlyPayEffMtd() {
    	return earlyPayEffMtd;
    }
	public void setEarlyPayEffMtd(String earlyPayEffMtd) {
    	this.earlyPayEffMtd = earlyPayEffMtd;
    }
	
	public Date getEarlyPayDate() {
		return earlyPayDate;
	}
	public void setEarlyPayDate(Date earlyPayDate) {
		this.earlyPayDate = earlyPayDate;
	}

	public boolean isSchdRegenerated() {
    	return schdRegenerated;
    }
	public void setSchdRegenerated(boolean schdRegenerated) {
    	this.schdRegenerated = schdRegenerated;
    }
	public long getLinkedTranId() {
	    return linkedTranId;
    }
	public void setLinkedTranId(long linkedTranId) {
	    this.linkedTranId = linkedTranId;
    }
	
}
