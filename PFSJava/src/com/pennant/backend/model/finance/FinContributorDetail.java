package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

public class FinContributorDetail implements Serializable, Entity {

    private static final long serialVersionUID = -7356577575758061061L;
    
	private String finReference;
	private long contributorBaseNo;
	private long custID;
	private String lovDescContributorCIF;
	private int lovDescFinFormatter;
	private String contributorName;
	private BigDecimal contributorInvest;
	private String investAccount;
	private Date investDate;
	private Date recordDate;
	private BigDecimal totalInvestPerc;
	private BigDecimal mudaribPerc;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private FinContributorDetail befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinContributorDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("FinContributorDetail");
	}

	public FinContributorDetail(String id) {
		this.setFinReference(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public long getId() {
		return contributorBaseNo;
	}
	public void setId (long id) {
		this.contributorBaseNo = id;
	}

	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }

	public long getContributorBaseNo() {
    	return contributorBaseNo;
    }
	public void setContributorBaseNo(long contributorBaseNo) {
    	this.contributorBaseNo = contributorBaseNo;
    }

	public void setCustID(long custID) {
	    this.custID = custID;
    }

	public long getCustID() {
	    return custID;
    }

	public String getLovDescContributorCIF() {
    	return lovDescContributorCIF;
    }
	public void setLovDescContributorCIF(String lovDescContributorCIF) {
    	this.lovDescContributorCIF = lovDescContributorCIF;
    }

	public void setLovDescFinFormatter(int lovDescFinFormatter) {
	    this.lovDescFinFormatter = lovDescFinFormatter;
    }
	public int getLovDescFinFormatter() {
	    return lovDescFinFormatter;
    }

	public String getContributorName() {
    	return contributorName;
    }
	public void setContributorName(String contributorName) {
    	this.contributorName = contributorName;
    }

	public BigDecimal getContributorInvest() {
    	return contributorInvest;
    }
	public void setContributorInvest(BigDecimal contributorInvest) {
    	this.contributorInvest = contributorInvest;
    }

	public String getInvestAccount() {
    	return investAccount;
    }
	public void setInvestAccount(String investAccount) {
    	this.investAccount = investAccount;
    }

	public Date getInvestDate() {
    	return investDate;
    }
	public void setInvestDate(Date investDate) {
    	this.investDate = investDate;
    }

	public BigDecimal getTotalInvestPerc() {
    	return totalInvestPerc;
    }
	public void setTotalInvestPerc(BigDecimal totalInvestPerc) {
    	this.totalInvestPerc = totalInvestPerc;
    }

	public BigDecimal getMudaribPerc() {
    	return mudaribPerc;
    }
	public void setMudaribPerc(BigDecimal mudaribPerc) {
    	this.mudaribPerc = mudaribPerc;
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
	public void setLastMntOn(Timestamp lastMntOn) {
    	this.lastMntOn = lastMntOn;
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

	public FinContributorDetail getBefImage() {
    	return befImage;
    }
	public void setBefImage(FinContributorDetail befImage) {
    	this.befImage = befImage;
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
		if (this.workflowId==0){
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
	
	// Overridden Equals method to handle the comparison
	public boolean equals(FinContributorDetail detail) {
		return getId() == detail.getId();
	}

	/**
	 * Check object is equal or not with Other object
	 * 
	 *  @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinContributorDetail) {
			FinContributorDetail detail = (FinContributorDetail) obj;
			return equals(detail);
		}
		return false;
	}

	public void setRecordDate(Date recordDate) {
	    this.recordDate = recordDate;
    }

	public Date getRecordDate() {
	    return recordDate;
    }


}
