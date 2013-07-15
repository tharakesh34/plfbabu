package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.Date;

public class FeeRule {

	private String finReference;
	private Date schDate;
	private int feeOrder;
	private String feeCode;
	private String feeCodeDesc;
	private BigDecimal feeAmount= new BigDecimal(0);
	private boolean addFeeCharges = false;
	
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
	
	public BigDecimal getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}
	
	public void setAddFeeCharges(boolean addFeeCharges) {
	    this.addFeeCharges = addFeeCharges;
    }
	public boolean isAddFeeCharges() {
	    return addFeeCharges;
    }
	
}