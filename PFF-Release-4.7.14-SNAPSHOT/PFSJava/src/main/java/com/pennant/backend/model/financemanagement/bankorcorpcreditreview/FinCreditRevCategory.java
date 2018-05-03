package com.pennant.backend.model.financemanagement.bankorcorpcreditreview;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinCreditRevCategory extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 3557119742009775415L;
	private long categoryId;
	private String  categoryDesc;	
	private String creditRevCode;
	private String remarks;
	private boolean brkdowndsply;
	private boolean changedsply;
	private int noOfyears ;	
	private int categorySeque;
	private boolean newRecord = false;
	private String lovValue;
	private FinCreditRevCategory befImage;
	private LoggedInUser userDetails;
	
	public FinCreditRevCategory() {
		super();
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

	public FinCreditRevCategory getBefImage() {
		return this.befImage;
	}
	public void setBefImage(FinCreditRevCategory beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	
	public String getCategoryDesc() {
    	return categoryDesc;
    }
	public void setCategoryDesc(String categoryDesc) {
    	this.categoryDesc = categoryDesc;
    }
	public String getCreditRevCode() {
    	return creditRevCode;
    }
	public void setCreditRevCode(String creditRevCode) {
    	this.creditRevCode = creditRevCode;
    }
	public int getNoOfyears() {
    	return noOfyears;
    }
	public void setNoOfyears(int noOfyears) {
    	this.noOfyears = noOfyears;
    }
	public int getCategorySeque() {
    	return categorySeque;
    }
	public void setCategorySeque(int categorySeque) {
    	this.categorySeque = categorySeque;
    }
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	public void setCategoryId(long categoryId) {
	    this.categoryId = categoryId;
    }
	public long getCategoryId() {
	    return categoryId;
    }
	public void setRemarks(String remarks) {
	    this.remarks = remarks;
    }
	public String getRemarks() {
	    return remarks;
    }
	
	public boolean isBrkdowndsply() {
    	return brkdowndsply;
    }
	public void setBrkdowndsply(boolean brkdowndsply) {
    	this.brkdowndsply = brkdowndsply;
    }
	public boolean isChangedsply() {
    	return changedsply;
    }
	public void setChangedsply(boolean changedsply) {
    	this.changedsply = changedsply;
    }
}
