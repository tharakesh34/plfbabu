package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.Date;

public class FeeRule {

	private String finReference;
	private Date schDate;
	private String feeCode;
	private int SeqNo;
	private int feeOrder;
	private String feeCodeDesc;
	private boolean allowWaiver = false;
	private BigDecimal waiverPerc= BigDecimal.ZERO;
	private BigDecimal feeAmount= BigDecimal.ZERO;
	private BigDecimal waiverAmount= BigDecimal.ZERO;
	private BigDecimal paidAmount= BigDecimal.ZERO;
	private boolean addFeeCharges = false;
	private boolean isNewFee = false;
	private boolean excludeFromRpt = false;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setFinReference(String finReference) {
	    this.finReference = finReference;
    }
	public String getFinReference() {
	    return finReference;
    }
	
	public void setSchDate(Date schDate) {
	    this.schDate = schDate;
    }
	public Date getSchDate() {
	    return schDate;
    }
	
	public void setFeeOrder(int feeOrder) {
	    this.feeOrder = feeOrder;
    }
	public int getFeeOrder() {
	    return feeOrder;
    }
	public String getFeeCode() {
		return feeCode;
	}
	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}
	
	public String getFeeCodeDesc() {
		return feeCodeDesc;
	}
	public void setFeeCodeDesc(String feeCodeDesc) {
		this.feeCodeDesc = feeCodeDesc;
	}
	
	public boolean isAllowWaiver() {
    	return allowWaiver;
    }
	public void setAllowWaiver(boolean allowWaiver) {
    	this.allowWaiver = allowWaiver;
    }
	
	public BigDecimal getWaiverPerc() {
    	return waiverPerc;
    }
	public void setWaiverPerc(BigDecimal waiverPerc) {
    	this.waiverPerc = waiverPerc;
    }
	
	public BigDecimal getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}
	
	public BigDecimal getWaiverAmount() {
    	return waiverAmount;
    }
	public void setWaiverAmount(BigDecimal waiverAmount) {
    	this.waiverAmount = waiverAmount;
    }
	
	public BigDecimal getPaidAmount() {
    	return paidAmount;
    }
	public void setPaidAmount(BigDecimal paidAmount) {
    	this.paidAmount = paidAmount;
    }
	
	public void setAddFeeCharges(boolean addFeeCharges) {
	    this.addFeeCharges = addFeeCharges;
    }
	public boolean isAddFeeCharges() {
	    return addFeeCharges;
    }
	
	public void setSeqNo(int seqNo) {
	    SeqNo = seqNo;
    }
	public int getSeqNo() {
	    return SeqNo;
    }
	
	public void setNewFee(boolean isNewFee) {
	    this.isNewFee = isNewFee;
    }
	public boolean isNewFee() {
	    return isNewFee;
    }
	public boolean isExcludeFromRpt() {
    	return excludeFromRpt;
    }
	public void setExcludeFromRpt(boolean excludeFromRpt) {
    	this.excludeFromRpt = excludeFromRpt;
    }
	
}