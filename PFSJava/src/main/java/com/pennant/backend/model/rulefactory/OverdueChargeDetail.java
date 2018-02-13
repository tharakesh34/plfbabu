package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;



public class OverdueChargeDetail extends AbstractWorkflowEntity {
	
    private static final long serialVersionUID = -1032001125987199804L;
    
	private String oDCRuleCode = null;
	private String oDCCustCtg;
	private String lovDescODCCustCtgName;
	private String oDCType;
	private String lovDescODCTypeName;
	private String oDCOn;
	private BigDecimal oDCAmount;
	private int oDCGraceDays;
	private boolean oDCAllowWaiver;
	private BigDecimal oDCMaxWaiver;
	private boolean newRecord=false;
	private String lovValue;
	private OverdueChargeDetail befImage;
	private LoggedInUser userDetails;
	
	public OverdueChargeDetail() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public boolean isNew() {
		return isNewRecord();
	}
	
	public String getoDCRuleCode() {
    	return oDCRuleCode;
    }
	public void setoDCRuleCode(String oDCRuleCode) {
    	this.oDCRuleCode = oDCRuleCode;
    }
	
	public String getoDCCustCtg() {
    	return oDCCustCtg;
    }
	public void setoDCCustCtg(String oDCCustCtg) {
    	this.oDCCustCtg = oDCCustCtg;
    }
	
	public String getLovDescODCCustCtgName() {
    	return lovDescODCCustCtgName;
    }
	public void setLovDescODCCustCtgName(String lovDescODCCustCtgName) {
    	this.lovDescODCCustCtgName = lovDescODCCustCtgName;
    }
	
	public String getoDCType() {
    	return oDCType;
    }
	public void setoDCType(String oDCType) {
    	this.oDCType = oDCType;
    }
	
	public String getLovDescODCTypeName() {
    	return lovDescODCTypeName;
    }
	public void setLovDescODCTypeName(String lovDescODCTypeName) {
    	this.lovDescODCTypeName = lovDescODCTypeName;
    }
	
	public String getoDCOn() {
    	return oDCOn;
    }
	public void setoDCOn(String oDCOn) {
    	this.oDCOn = oDCOn;
    }
	
	public BigDecimal getoDCAmount() {
    	return oDCAmount;
    }
	public void setoDCAmount(BigDecimal oDCAmount) {
    	this.oDCAmount = oDCAmount;
    }
	
	public int getoDCGraceDays() {
    	return oDCGraceDays;
    }
	public void setoDCGraceDays(int oDCGraceDays) {
    	this.oDCGraceDays = oDCGraceDays;
    }
	
	public boolean isoDCAllowWaiver() {
    	return oDCAllowWaiver;
    }
	public void setoDCAllowWaiver(boolean oDCAllowWaiver) {
    	this.oDCAllowWaiver = oDCAllowWaiver;
    }
	
	public BigDecimal getoDCMaxWaiver() {
    	return oDCMaxWaiver;
    }
	public void setoDCMaxWaiver(BigDecimal oDCMaxWaiver) {
    	this.oDCMaxWaiver = oDCMaxWaiver;
    }
	
	public boolean isNewRecord() {
    	return newRecord;
    }
	public void setNewRecord(boolean newRecord) {
    	this.newRecord = newRecord;
    }
	
	public String getLovValue() {
    	return lovValue;
    }
	public void setLovValue(String lovValue) {
    	this.lovValue = lovValue;
    }
	
	public OverdueChargeDetail getBefImage() {
    	return befImage;
    }
	public void setBefImage(OverdueChargeDetail befImage) {
    	this.befImage = befImage;
    }
	
	public LoggedInUser getUserDetails() {
    	return userDetails;
    }
	public void setUserDetails(LoggedInUser userDetails) {
    	this.userDetails = userDetails;
    }
	
	public String getId() {
	    return oDCRuleCode;
    }
	public void setId(String id) {
		this.oDCRuleCode = id;
	    
    }

}
