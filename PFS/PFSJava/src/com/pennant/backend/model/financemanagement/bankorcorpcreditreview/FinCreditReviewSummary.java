package com.pennant.backend.model.financemanagement.bankorcorpcreditreview;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.pennant.backend.model.Entity;

import com.pennant.backend.model.LoginUserDetails;

public class FinCreditReviewSummary implements java.io.Serializable ,Entity{

	private static final long serialVersionUID = 3557119742009775415L;
	private long summaryId  = Long.MIN_VALUE;;
	private long detailId;
	private String subCategoryCode;
	private BigDecimal itemValue;	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private FinCreditReviewSummary befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	private BigDecimal lovDescConversionRate;
	private String lovDescBankName;
	private int lovDescNoOfShares;
	private BigDecimal lovDescMarketPrice;
	
	
	public BigDecimal getLovDescConversionRate() {
    	return lovDescConversionRate;
    }

	public void setLovDescConversionRate(BigDecimal lovDescConversionRate) {
    	this.lovDescConversionRate = lovDescConversionRate;
    }

	public String getLovDescBankName() {
    	return lovDescBankName;
    }

	public void setLovDescBankName(String lovDescBankName) {
    	this.lovDescBankName = lovDescBankName;
    }

	public FinCreditReviewSummary(){}
	
	public boolean isNew() {
		return isNewRecord();
	}
	public long getSummaryId() {
    	return this.summaryId;
    }
	public void setSummaryId(long summaryId) {
    	this.summaryId = summaryId;
    }
	public long getDetailId() {
    	return detailId;
    }
	public void setDetailId(long detailId) {
    	this.detailId = detailId;
    }	
	public BigDecimal getItemValue() {
    	return itemValue;
    }
	public void setItemValue(BigDecimal itemValue) {
    	this.itemValue = itemValue;
    }
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
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
	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
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

	public FinCreditReviewSummary getBefImage() {
		return this.befImage;
	}
	public void setBefImage(FinCreditReviewSummary beforeImage) {
		this.befImage = beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getNextRoleCode() {
		return nextRoleCode;
	}
	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}
	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId == 0) {
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	@Override
    public long getId() {
	   
	    return this.summaryId;
    }
	@Override
    public void setId(long id) {
	    this.summaryId = id;
    }

	public void setSubCategoryCode(String subCategoryCode) {
	    this.subCategoryCode = subCategoryCode;
    }

	public String getSubCategoryCode() {
	    return subCategoryCode;
    }

	public void setLovDescNoOfShares(int lovDescNoOfShares) {
	    this.lovDescNoOfShares = lovDescNoOfShares;
    }

	public int getLovDescNoOfShares() {
	    return lovDescNoOfShares;
    }

	public void setLovDescMarketPrice(BigDecimal lovDescMarketPrice) {
	    this.lovDescMarketPrice = lovDescMarketPrice;
    }

	public BigDecimal getLovDescMarketPrice() {
	    return lovDescMarketPrice;
    }

}
