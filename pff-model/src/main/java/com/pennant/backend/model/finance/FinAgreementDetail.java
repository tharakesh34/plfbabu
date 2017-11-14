package com.pennant.backend.model.finance;

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class FinAgreementDetail extends AbstractWorkflowEntity {

    private static final long serialVersionUID = -4590527012741917367L;
    
	private String finReference;
	private long agrId = Long.MIN_VALUE;
	private String finType;
	private String agrName;
	private String lovDescAgrName;
	private byte[] agrContent;
	private boolean lovDescMandInput;

	private boolean newRecord=false;
	private String lovValue;
	private FinAgreementDetail befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public FinAgreementDetail() {
		super();
	}

	public FinAgreementDetail(String id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
		return new HashSet<String>();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return finReference;
	}
	public void setId (String id) {
		this.finReference = id;
	}

	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }

	public long getAgrId() {
    	return agrId;
    }
	public void setAgrId(long agrId) {
    	this.agrId = agrId;
    }

	public void setFinType(String finType) {
	    this.finType = finType;
    }
	public String getFinType() {
	    return finType;
    }
	
	public String getAgrName() {
    	return agrName;
    }
	public void setAgrName(String agrName) {
    	this.agrName = agrName;
    }

	public void setLovDescAgrName(String lovDescAgrName) {
	    this.lovDescAgrName = lovDescAgrName;
    }
	public String getLovDescAgrName() {
	    return lovDescAgrName;
    }

	public byte[] getAgrContent() {
    	return agrContent;
    }
	public void setAgrContent(byte[] agrContent) {
    	this.agrContent = agrContent;
    }

	public void setLovDescMandInput(boolean lovDescMandInput) {
	    this.lovDescMandInput = lovDescMandInput;
    }
	public boolean isLovDescMandInput() {
	    return lovDescMandInput;
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

	public FinAgreementDetail getBefImage() {
    	return befImage;
    }
	public void setBefImage(FinAgreementDetail befImage) {
    	this.befImage = befImage;
    }

	public LoggedInUser getUserDetails() {
    	return userDetails;
    }
	public void setUserDetails(LoggedInUser userDetails) {
    	this.userDetails = userDetails;
    }
}
