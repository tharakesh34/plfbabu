package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

public class CustomerExtLiability{
	
	private long custID = Long.MIN_VALUE;
	private int liabilitySeq;
	private Date   finDate;
	private String finType;
	private String bankName;
	private String lovDescBankName;
	private String lovDescFinType;
	private BigDecimal 	originalAmount = BigDecimal.ZERO;
	private BigDecimal 	instalmentAmount = BigDecimal.ZERO;
	private BigDecimal 	outStandingBal = BigDecimal.ZERO;
	private String finStatus;
	private String lovDescFinStatus;

	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private CustomerExtLiability befImage;
	private LoginUserDetails userDetails;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;

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

	public CustomerExtLiability() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CustomerExtLiability");
	}

	public CustomerExtLiability(long id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		return excludeFields;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	
	public long getId() {
		return custID;
	}
	public void setId (long id) {
		this.custID = id;
	}
	
	
	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}

	public int getLiabilitySeq() {
		return liabilitySeq;
	}
	public void setLiabilitySeq(int liabilitySeq) {
		this.liabilitySeq = liabilitySeq;
	}

	public Date getFinDate() {
		return finDate;
	}
	public void setFinDate(Date finDate) {
		this.finDate = finDate;
	}

	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getLovDescBankName() {
		return lovDescBankName;
	}
	public void setLovDescBankName(String lovDescBankName) {
		this.lovDescBankName = lovDescBankName;
	}

	public String getLovDescFinType() {
		return lovDescFinType;
	}
	public void setLovDescFinType(String lovDescFinType) {
		this.lovDescFinType = lovDescFinType;
	}

	public BigDecimal getOriginalAmount() {
		return originalAmount;
	}
	public void setOriginalAmount(BigDecimal originalAmount) {
		this.originalAmount = originalAmount;
	}

	public BigDecimal getInstalmentAmount() {
		return instalmentAmount;
	}
	public void setInstalmentAmount(BigDecimal instalmentAmount) {
		this.instalmentAmount = instalmentAmount;
	}

	public BigDecimal getOutStandingBal() {
		return outStandingBal;
	}
	public void setOutStandingBal(BigDecimal outStandingBal) {
		this.outStandingBal = outStandingBal;
	}

	public String getFinStatus() {
		return finStatus;
	}
	public void setFinStatus(String finStatus) {
		this.finStatus = finStatus;
	}

	public String getLovDescFinStatus() {
		return lovDescFinStatus;
	}
	public void setLovDescFinStatus(String lovDescFinStatus) {
		this.lovDescFinStatus = lovDescFinStatus;
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

	public CustomerExtLiability getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerExtLiability beforeImage){
		this.befImage=beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescCustRecordType() {
		return lovDescCustRecordType;
	}
	public void setLovDescCustRecordType(String lovDescCustRecordType) {
		this.lovDescCustRecordType = lovDescCustRecordType;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}
	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
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
	
	public void setLoginDetails(LoginUserDetails userDetails){
		this.lastMntBy=userDetails.getLoginUsrID();
		this.userDetails=userDetails;
		
	}

	// Overridden Equals method to handle the comparison
	public boolean equals(CustomerExtLiability customerExtLiability) {
		if(getCustID() == customerExtLiability.getCustID()
				&& getLiabilitySeq() == customerExtLiability.getLiabilitySeq()){
			return true;
		}
		return false;
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

		if (obj instanceof CustomerExtLiability) {
			CustomerExtLiability customerExtLiability = (CustomerExtLiability) obj;
			return equals(customerExtLiability);
		}
		return false;
	}
	
	
}
