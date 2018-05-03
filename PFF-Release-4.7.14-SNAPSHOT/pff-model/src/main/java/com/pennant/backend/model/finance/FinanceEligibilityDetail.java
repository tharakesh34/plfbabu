package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

public class FinanceEligibilityDetail implements Serializable {
	
    private static final long serialVersionUID = 4194235332884338495L;
    
	private String finReference;
	private long elgRuleCode;
	private String elgRuleValue;
	private String lovDescElgRuleCode;
	private String lovDescElgRuleCodeDesc;
	private String ruleResultType;
	private String ruleResult;
	private boolean canOverride = false;
	private int overridePerc;
	private boolean userOverride = false;
	private boolean execute = false;
	private boolean eligible = true;
	private boolean eligibleWithDevaition = false;
	private boolean allowDeviation;
	private String recordType;
	
	//Auditing purpose
	private long lastMntBy;
	private Timestamp lastMntOn;
	private String roleCode;
	private String recordStatus;
	
	public FinanceEligibilityDetail() {
		
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("elgRuleValue");
		excludeFields.add("execute");
		excludeFields.add("eligible");
		excludeFields.add("allowDeviation");
		excludeFields.add("eligibleWithDevaition");
		excludeFields.add("recordType");
		return excludeFields;
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
	
	public long getElgRuleCode() {
    	return elgRuleCode;
    }
	public void setElgRuleCode(long elgRuleCode) {
    	this.elgRuleCode = elgRuleCode;
    }
	
	public String getLovDescElgRuleCode() {
    	return lovDescElgRuleCode;
    }
	public void setLovDescElgRuleCode(String lovDescElgRuleCode) {
    	this.lovDescElgRuleCode = lovDescElgRuleCode;
    }
	
	public String getLovDescElgRuleCodeDesc() {
    	return lovDescElgRuleCodeDesc;
    }
	public void setLovDescElgRuleCodeDesc(String lovDescElgRuleCodeDesc) {
    	this.lovDescElgRuleCodeDesc = lovDescElgRuleCodeDesc;
    }
	
	public String getRuleResultType() {
    	return ruleResultType;
    }
	public void setRuleResultType(String ruleResultType) {
    	this.ruleResultType = ruleResultType;
    }
	
	public String getRuleResult() {
    	return ruleResult;
    }
	public void setRuleResult(String ruleResult) {
    	this.ruleResult = ruleResult;
    }
	
	public boolean isCanOverride() {
    	return canOverride;
    }
	public void setCanOverride(boolean canOverride) {
    	this.canOverride = canOverride;
    }
	
	public int getOverridePerc() {
    	return overridePerc;
    }
	public void setOverridePerc(int overridePerc) {
    	this.overridePerc = overridePerc;
    }
	
	public boolean isUserOverride() {
    	return userOverride;
    }
	public void setUserOverride(boolean userOverride) {
    	this.userOverride = userOverride;
    }
	
	public boolean isExecute() {
    	return execute;
    }
	public void setExecute(boolean execute) {
    	this.execute = execute;
    }
	
	public BigDecimal getOverrideResult(){
		return new BigDecimal(getRuleResult()).add(new BigDecimal(
				getRuleResult()).multiply(new BigDecimal(getOverridePerc())).divide(
						new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
	}
	
	public boolean isEligible() {
    	return eligible;
    }
	public void setEligible(boolean eligible) {
    	this.eligible = eligible;
    }
	
	public String getElgRuleValue() {
    	return elgRuleValue;
    }
	public void setElgRuleValue(String elgRuleValue) {
    	this.elgRuleValue = elgRuleValue;
    }
	
	public long getLastMntBy() {
    	return lastMntBy;
    }
	public void setLastMntBy(long lastMntBy) {
    	this.lastMntBy = lastMntBy;
    }
	
	public Timestamp getLastMntOn() {
    	return lastMntOn;
    }
	public void setLastMntOn(Timestamp lastMntOn) {
    	this.lastMntOn = lastMntOn;
    }

	public String getRoleCode() {
	    return roleCode;
    }
	public void setRoleCode(String roleCode) {
	    this.roleCode = roleCode;
    }

	public String getRecordStatus() {
	    return recordStatus;
    }
	public void setRecordStatus(String recordStatus) {
	    this.recordStatus = recordStatus;
    }

	public boolean isAllowDeviation() {
	    return allowDeviation;
    }

	public void setAllowDeviation(boolean allowDeviation) {
	    this.allowDeviation = allowDeviation;
    }

	public boolean isEligibleWithDevaition() {
	    return eligibleWithDevaition;
    }
	public void setEligibleWithDevaition(boolean eligibleWithDevaition) {
	    this.eligibleWithDevaition = eligibleWithDevaition;
    }

	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	

}
