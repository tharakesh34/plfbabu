package com.pennant.backend.model.customermasters;


public class LimitCustomer {
	
	private long custID ;
	//LimitRule
	private String ruleCode;
	
	
	
	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}
	public String getRuleCode() {
		return ruleCode;
	}
	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}
}
