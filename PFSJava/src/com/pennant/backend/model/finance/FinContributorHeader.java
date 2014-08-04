package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

public class FinContributorHeader implements Serializable {
	
    private static final long serialVersionUID = -3924845567358313810L;

	private String finReference;
	private int minContributors;
	private int maxContributors;
	private BigDecimal minContributionAmt;
	private BigDecimal maxContributionAmt;
	private int curContributors;
	private BigDecimal curContributionAmt;
	private BigDecimal curBankInvestment;
	private BigDecimal avgMudaribRate;
	private boolean alwContributorsToLeave;
	private boolean alwContributorsToJoin;
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private FinContributorHeader befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	
	private List<FinContributorDetail> contributorDetailList = new ArrayList<FinContributorDetail>();
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinContributorHeader() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("FinContributorHeader");
	}

	public FinContributorHeader(String id) {
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("contributorDetailList");
		
		return excludeFields;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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

	public int getMinContributors() {
    	return minContributors;
    }
	public void setMinContributors(int minContributors) {
    	this.minContributors = minContributors;
    }

	public int getMaxContributors() {
    	return maxContributors;
    }
	public void setMaxContributors(int maxContributors) {
    	this.maxContributors = maxContributors;
    }

	public BigDecimal getMinContributionAmt() {
    	return minContributionAmt;
    }
	public void setMinContributionAmt(BigDecimal minContributionAmt) {
    	this.minContributionAmt = minContributionAmt;
    }

	public BigDecimal getMaxContributionAmt() {
    	return maxContributionAmt;
    }
	public void setMaxContributionAmt(BigDecimal maxContributionAmt) {
    	this.maxContributionAmt = maxContributionAmt;
    }

	public int getCurContributors() {
    	return curContributors;
    }
	public void setCurContributors(int curContributors) {
    	this.curContributors = curContributors;
    }

	public BigDecimal getCurContributionAmt() {
    	return curContributionAmt;
    }
	public void setCurContributionAmt(BigDecimal curContributionAmt) {
    	this.curContributionAmt = curContributionAmt;
    }

	public BigDecimal getCurBankInvestment() {
    	return curBankInvestment;
    }
	public void setCurBankInvestment(BigDecimal curBankInvestment) {
    	this.curBankInvestment = curBankInvestment;
    }

	public BigDecimal getAvgMudaribRate() {
    	return avgMudaribRate;
    }
	public void setAvgMudaribRate(BigDecimal avgMudaribRate) {
    	this.avgMudaribRate = avgMudaribRate;
    }

	public boolean isAlwContributorsToLeave() {
    	return alwContributorsToLeave;
    }
	public void setAlwContributorsToLeave(boolean alwContributorsToLeave) {
    	this.alwContributorsToLeave = alwContributorsToLeave;
    }

	public boolean isAlwContributorsToJoin() {
    	return alwContributorsToJoin;
    }
	public void setAlwContributorsToJoin(boolean alwContributorsToJoin) {
    	this.alwContributorsToJoin = alwContributorsToJoin;
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

	public FinContributorHeader getBefImage() {
    	return befImage;
    }
	public void setBefImage(FinContributorHeader befImage) {
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
	public boolean equals(FinContributorHeader detail) {
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

		if (obj instanceof FinContributorHeader) {
			FinContributorHeader detail = (FinContributorHeader) obj;
			return equals(detail);
		}
		return false;
	}

	public void setContributorDetailList(List<FinContributorDetail> contributorDetailList) {
	    this.contributorDetailList = contributorDetailList;
    }

	public List<FinContributorDetail> getContributorDetailList() {
	    return contributorDetailList;
    }

}
