package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoginUserDetails;

public class FinanceStepPolicyDetail implements Serializable {

    private static final long serialVersionUID = -2343217039719002642L;
    
	private String finReference;
	private int stepNo;
	private BigDecimal tenorSplitPerc = BigDecimal.ZERO;
	private int installments;
	private BigDecimal rateMargin = BigDecimal.ZERO;
	private BigDecimal emiSplitPerc = BigDecimal.ZERO;
	private BigDecimal steppedEMI = BigDecimal.ZERO;
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private FinanceStepPolicyDetail befImage;
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

	public FinanceStepPolicyDetail() {
	}

	public FinanceStepPolicyDetail(String id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		return new HashSet<String>();
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

	public int getStepNo() {
		return stepNo;
	}
	public void setStepNo(int stepNo) {
		this.stepNo = stepNo;
	}

	public int getInstallments() {
		return installments;
	}
	public void setInstallments(int installments) {
		this.installments = installments;
	}

	public BigDecimal getSteppedEMI() {
		return steppedEMI;
	}
	public void setSteppedEMI(BigDecimal steppedEMI) {
		this.steppedEMI = steppedEMI;
	}

	public BigDecimal getTenorSplitPerc() {
		return tenorSplitPerc;
	}
	public void setTenorSplitPerc(BigDecimal tenorSplitPerc) {
		this.tenorSplitPerc = tenorSplitPerc;
	}
	
	public BigDecimal getRateMargin() {
		return rateMargin;
	}
	public void setRateMargin(BigDecimal rateMargin) {
		this.rateMargin = rateMargin;
	}

	public BigDecimal getEmiSplitPerc() {
		return emiSplitPerc;
	}
	public void setEmiSplitPerc(BigDecimal emiSplitPerc) {
		this.emiSplitPerc = emiSplitPerc;
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

	public FinanceStepPolicyDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FinanceStepPolicyDetail beforeImage){
		this.befImage=beforeImage;
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
	public boolean equals(FinanceStepPolicyDetail stepPolicyDetail) {
		return getId() == stepPolicyDetail.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinanceStepPolicyDetail) {
			FinanceStepPolicyDetail stepPolicyDetail = (FinanceStepPolicyDetail) obj;
			return equals(stepPolicyDetail);
		}
		return false;
	}

}
