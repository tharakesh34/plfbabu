package com.pennant.backend.model.reports;

import java.math.BigDecimal;

public class AvailFinance{
	
	private String finCommitmentRef ="";
	private String finReference ="";
	private String finCcy ="";
	private String finAmount ="";
	private String drawnPrinciple ="";
	private String finAmtBHD ="";
	private String outStandingBal ="";
	private String lastRepay ="";
	private String maturityDate ="";
	private String profitRate ="";
	private String repayFrq ="";
	private String status ="";
	private BigDecimal ccySpotRate = BigDecimal.ZERO;
	private int ccyEditField = 0;
	private String finDivision = "";
	private String finDivisionDesc = "";
	
	public String getFinCommitmentRef() {
    	return finCommitmentRef;
    }
	public void setFinCommitmentRef(String finCommitmentRef) {
    	this.finCommitmentRef = finCommitmentRef;
    }
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	public String getFinCcy() {
    	return finCcy;
    }
	public void setFinCcy(String finCcy) {
    	this.finCcy = finCcy;
    }
	public String getFinAmount() {
    	return finAmount;
    }
	public void setFinAmount(String finAmount) {
    	this.finAmount = finAmount;
    }
	public String getDrawnPrinciple() {
    	return drawnPrinciple;
    }
	public void setDrawnPrinciple(String drawnPrinciple) {
    	this.drawnPrinciple = drawnPrinciple;
    }
	public String getFinAmtBHD() {
    	return finAmtBHD;
    }
	public void setFinAmtBHD(String finAmtBHD) {
    	this.finAmtBHD = finAmtBHD;
    }
	public String getOutStandingBal() {
    	return outStandingBal;
    }
	public void setOutStandingBal(String outStandingBal) {
    	this.outStandingBal = outStandingBal;
    }
	public String getLastRepay() {
    	return lastRepay;
    }
	public void setLastRepay(String lastRepay) {
    	this.lastRepay = lastRepay;
    }
	public String getMaturityDate() {
    	return maturityDate;
    }
	public void setMaturityDate(String maturityDate) {
    	this.maturityDate = maturityDate;
    }
	public String getProfitRate() {
    	return profitRate;
    }
	public void setProfitRate(String profitRate) {
    	this.profitRate = profitRate;
    }
	public String getRepayFrq() {
    	return repayFrq;
    }
	public void setRepayFrq(String repayFrq) {
    	this.repayFrq = repayFrq;
    }
	public String getStatus() {
    	return status;
    }
	public void setStatus(String status) {
    	this.status = status;
    }
	public BigDecimal getCcySpotRate() {
    	return ccySpotRate;
    }
	public void setCcySpotRate(BigDecimal ccySpotRate) {
    	this.ccySpotRate = ccySpotRate;
    }
	public int getCcyEditField() {
    	return ccyEditField;
    }
	public void setCcyEditField(int ccyEditField) {
    	this.ccyEditField = ccyEditField;
    }
	public String getFinDivision() {
    	return finDivision;
    }
	public void setFinDivision(String finDivision) {
    	this.finDivision = finDivision;
    }
	public String getFinDivisionDesc() {
	    return finDivisionDesc;
    }
	public void setFinDivisionDesc(String finDivisionDesc) {
	    this.finDivisionDesc = finDivisionDesc;
    }
	
}
