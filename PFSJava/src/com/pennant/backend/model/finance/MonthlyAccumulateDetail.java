package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class MonthlyAccumulateDetail implements Serializable {

    private static final long serialVersionUID = -684680761469984700L; 
	
    private String finReference;
    private Date monthEndDate;
    private Date monthStartDate;
    private BigDecimal pftAccrued = BigDecimal.ZERO;
    private BigDecimal pftTsfd = BigDecimal.ZERO; // (Present Month Accrued + Total Pft Paid) - Previous Month Transfered
    private BigDecimal suspPftAccrued = BigDecimal.ZERO;
    private BigDecimal suspPftTsfd = BigDecimal.ZERO; // Present Month Suspense Accrued - Previous Month Suspense Accrued
    private BigDecimal accumulatedDepPri = BigDecimal.ZERO;
    private BigDecimal depreciatePri = BigDecimal.ZERO;
    
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
 	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
 	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
    
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	
	public Date getMonthEndDate() {
    	return monthEndDate;
    }
	public void setMonthEndDate(Date monthEndDate) {
    	this.monthEndDate = monthEndDate;
    }
	
	public BigDecimal getPftAccrued() {
    	return pftAccrued;
    }
	public void setPftAccrued(BigDecimal pftAccrued) {
    	this.pftAccrued = pftAccrued;
    }
	
	public BigDecimal getPftTsfd() {
    	return pftTsfd;
    }
	public void setPftTsfd(BigDecimal pftTsfd) {
    	this.pftTsfd = pftTsfd;
    }
	
	public BigDecimal getSuspPftAccrued() {
    	return suspPftAccrued;
    }
	public void setSuspPftAccrued(BigDecimal suspPftAccrued) {
    	this.suspPftAccrued = suspPftAccrued;
    }
	
	public BigDecimal getSuspPftTsfd() {
    	return suspPftTsfd;
    }
	public void setSuspPftTsfd(BigDecimal suspPftTsfd) {
    	this.suspPftTsfd = suspPftTsfd;
    }
	
	public BigDecimal getAccumulatedDepPri() {
    	return accumulatedDepPri;
    }
	public void setAccumulatedDepPri(BigDecimal accumulatedDepPri) {
    	this.accumulatedDepPri = accumulatedDepPri;
    }
	
	public BigDecimal getDepreciatePri() {
    	return depreciatePri;
    }
	public void setDepreciatePri(BigDecimal depreciatePri) {
    	this.depreciatePri = depreciatePri;
    }
	public Date getMonthStartDate() {
	    return monthStartDate;
    }
	public void setMonthStartDate(Date monthStartDate) {
	    this.monthStartDate = monthStartDate;
    }
    
}
