package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class PaymentFee {

	private String ruleCode = "";
	private String ruleCodeDesc;
	private String waiverDecider;
	private boolean waiver;
	private BigDecimal waiverPerc;
	private BigDecimal excAmount;
	private BigDecimal maxAmount;
	public String getRuleCode() {
    	return ruleCode;
    }
	public void setRuleCode(String ruleCode) {
    	this.ruleCode = ruleCode;
    }
	public String getRuleCodeDesc() {
    	return ruleCodeDesc;
    }
	public void setRuleCodeDesc(String ruleCodeDesc) {
    	this.ruleCodeDesc = ruleCodeDesc;
    }
	public String getWaiverDecider() {
    	return waiverDecider;
    }
	public void setWaiverDecider(String waiverDecider) {
    	this.waiverDecider = waiverDecider;
    }
	public boolean isWaiver() {
    	return waiver;
    }
	public void setWaiver(boolean waiver) {
    	this.waiver = waiver;
    }
	public BigDecimal getWaiverPerc() {
    	return waiverPerc;
    }
	public void setWaiverPerc(BigDecimal waiverPerc) {
    	this.waiverPerc = waiverPerc;
    }
	public BigDecimal getExcAmount() {
    	return excAmount;
    }
	public void setExcAmount(BigDecimal excAmount) {
    	this.excAmount = excAmount;
    }
	public BigDecimal getMaxAmount() {
    	return maxAmount;
    }
	public void setMaxAmount(BigDecimal maxAmount) {
    	this.maxAmount = maxAmount;
    } 
	
	
}
