package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FinancePremiumDetail implements Serializable {
	
    private static final long serialVersionUID = -8467549476051111629L;
    
	private String finReference;
	private String issueNumber;
	private int noOfUnits;
	private BigDecimal faceValue;
	private String premiumType;
	private BigDecimal premiumValue;
	private BigDecimal pricePerUnit;
	private BigDecimal yieldValue;
	private Date lastCouponDate;
	private BigDecimal accruedProfit;
	private Date purchaseDate;
	private BigDecimal fairValuePerUnit;
	private BigDecimal fairValueAmount;
	
	public FinancePremiumDetail() {
	
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	
	public String getIssueNumber() {
    	return issueNumber;
    }
	public void setIssueNumber(String issueNumber) {
    	this.issueNumber = issueNumber;
    }
	
	public int getNoOfUnits() {
    	return noOfUnits;
    }
	public void setNoOfUnits(int noOfUnits) {
    	this.noOfUnits = noOfUnits;
    }
	
	public BigDecimal getFaceValue() {
    	return faceValue;
    }
	public void setFaceValue(BigDecimal faceValue) {
    	this.faceValue = faceValue;
    }
	
	public String getPremiumType() {
    	return premiumType;
    }
	public void setPremiumType(String premiumType) {
    	this.premiumType = premiumType;
    }
	
	public BigDecimal getPremiumValue() {
    	return premiumValue;
    }
	public void setPremiumValue(BigDecimal premiumValue) {
    	this.premiumValue = premiumValue;
    }
	
	public BigDecimal getPricePerUnit() {
    	return pricePerUnit;
    }
	public void setPricePerUnit(BigDecimal pricePerUnit) {
    	this.pricePerUnit = pricePerUnit;
    }
	
	public BigDecimal getYieldValue() {
    	return yieldValue;
    }
	public void setYieldValue(BigDecimal yieldValue) {
    	this.yieldValue = yieldValue;
    }
	
	public Date getLastCouponDate() {
    	return lastCouponDate;
    }
	public void setLastCouponDate(Date lastCouponDate) {
    	this.lastCouponDate = lastCouponDate;
    }
	
	public BigDecimal getAccruedProfit() {
    	return accruedProfit;
    }
	public void setAccruedProfit(BigDecimal accruedProfit) {
    	this.accruedProfit = accruedProfit;
    }
	
	public Date getPurchaseDate() {
    	return purchaseDate;
    }
	public void setPurchaseDate(Date purchaseDate) {
    	this.purchaseDate = purchaseDate;
    }
	
	public BigDecimal getFairValuePerUnit() {
    	return fairValuePerUnit;
    }
	public void setFairValuePerUnit(BigDecimal fairValuePerUnit) {
    	this.fairValuePerUnit = fairValuePerUnit;
    }
	
	public BigDecimal getFairValueAmount() {
    	return fairValueAmount;
    }
	public void setFairValueAmount(BigDecimal fairValueAmount) {
    	this.fairValueAmount = fairValueAmount;
    }
	
}
