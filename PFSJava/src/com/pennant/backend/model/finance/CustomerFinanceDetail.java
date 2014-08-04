package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.Notes;

/**
 * Model class for the <b>Customer table</b>.<br>
 *
 */
public class CustomerFinanceDetail {
	
	private long custId = Long.MIN_VALUE;
	private String finReference;
	private String finBranch;
 	private String custCIF;
	private String custShrtName;
	private String custDocType;
	private String custDocTitle;
	private String phoneNumber;

	private String phoneTypeCode;
	private String lovDescPhoneTypeCodeName;
	private String phoneCountryCode;
	private String lovDescPhoneCountryName;
	private String phoneAreaCode;
	
	private String custEMailTypeCode;
	private String lovDescCustEMailTypeCode;
	private int custEMailPriority;
	private String custEMail;
	public String deptDesc;
	
	private String finType;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private Date finStartDate;
	private int ccyFormat;
	private String lastMntByUser;
	private String finCcy;
	private String finTypeDesc;
	
 	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;

	private CustomerFinanceDetail befImage;
 	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String roleDesc="";
	private String nextRoleCode= "";
	private String nextRoleDesc= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	
	private List<AuditTransaction> auditTransactionsList;
	private List<Notes> notesList;
	
  
	public CustomerFinanceDetail(){
		super();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
 	
	public long getCustId() {
    	return custId;
    }

	public void setCustId(long custId) {
    	this.custId = custId;
    }

	public String getFinReference() {
    	return finReference;
    }

	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }

	public String getFinBranch() {
    	return finBranch;
    }

	public void setFinBranch(String finBranch) {
    	this.finBranch = finBranch;
    }

	public String getCustCIF() {
    	return custCIF;
    }

	public void setCustCIF(String custCIF) {
    	this.custCIF = custCIF;
    }

	public String getCustShrtName() {
    	return custShrtName;
    }

	public void setCustShrtName(String custShrtName) {
    	this.custShrtName = custShrtName;
    }

	public String getCustDocType() {
    	return custDocType;
    }

	public void setCustDocType(String custDocType) {
    	this.custDocType = custDocType;
    }

	public String getCustDocTitle() {
    	return custDocTitle;
    }

	public void setCustDocTitle(String custDocTitle) {
    	this.custDocTitle = custDocTitle;
    }

	public String getPhoneNumber() {
    	return phoneNumber;
    }

	public void setPhoneNumber(String phoneNumber) {
    	this.phoneNumber = phoneNumber;
    }

	public String getPhoneTypeCode() {
    	return phoneTypeCode;
    }

	public void setPhoneTypeCode(String phoneTypeCode) {
    	this.phoneTypeCode = phoneTypeCode;
    }

	public String getLovDescPhoneTypeCodeName() {
    	return lovDescPhoneTypeCodeName;
    }

	public void setLovDescPhoneTypeCodeName(String lovDescPhoneTypeCodeName) {
    	this.lovDescPhoneTypeCodeName = lovDescPhoneTypeCodeName;
    }

	public String getPhoneCountryCode() {
    	return phoneCountryCode;
    }

	public void setPhoneCountryCode(String phoneCountryCode) {
    	this.phoneCountryCode = phoneCountryCode;
    }

	public String getLovDescPhoneCountryName() {
    	return lovDescPhoneCountryName;
    }

	public void setLovDescPhoneCountryName(String lovDescPhoneCountryName) {
    	this.lovDescPhoneCountryName = lovDescPhoneCountryName;
    }

	public String getPhoneAreaCode() {
    	return phoneAreaCode;
    }

	public void setPhoneAreaCode(String phoneAreaCode) {
    	this.phoneAreaCode = phoneAreaCode;
    }

	public String getCustEMailTypeCode() {
    	return custEMailTypeCode;
    }

	public void setCustEMailTypeCode(String custEMailTypeCode) {
    	this.custEMailTypeCode = custEMailTypeCode;
    }

	public String getLovDescCustEMailTypeCode() {
    	return lovDescCustEMailTypeCode;
    }

	public void setLovDescCustEMailTypeCode(String lovDescCustEMailTypeCode) {
    	this.lovDescCustEMailTypeCode = lovDescCustEMailTypeCode;
    }

	public int getCustEMailPriority() {
    	return custEMailPriority;
    }

	public void setCustEMailPriority(int custEMailPriority) {
    	this.custEMailPriority = custEMailPriority;
    }

	public String getCustEMail() {
    	return custEMail;
    }

	public void setCustEMail(String custEMail) {
    	this.custEMail = custEMail;
    }

	public String getDeptDesc() {
    	return deptDesc;
    }

	public void setDeptDesc(String deptDesc) {
    	this.deptDesc = deptDesc;
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

	public CustomerFinanceDetail getBefImage() {
    	return befImage;
    }

	public void setBefImage(CustomerFinanceDetail befImage) {
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

	public String getRoleDesc() {
    	return roleDesc;
    }

	public void setRoleDesc(String roleDesc) {
    	this.roleDesc = roleDesc;
    }

	public String getNextRoleCode() {
    	return nextRoleCode;
    }

	public void setNextRoleCode(String nextRoleCode) {
    	this.nextRoleCode = nextRoleCode;
    }

	public String getNextRoleDesc() {
    	return nextRoleDesc;
    }

	public void setNextRoleDesc(String nextRoleDesc) {
    	this.nextRoleDesc = nextRoleDesc;
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

	public long getWorkflowId() {
    	return workflowId;
    }

	public void setWorkflowId(long workflowId) {
    	this.workflowId = workflowId;
    }

	public boolean isChanged() {
		boolean changed =false;
		
		if(befImage==null){
			changed=true;
		}else{
			
		}
		return changed;
	}

	public List<AuditTransaction> getAuditTransactionsList() {
    	return auditTransactionsList;
    }

	public void setAuditTransactionsList(List<AuditTransaction> auditTransactionsList) {
    	this.auditTransactionsList = auditTransactionsList;
    }

	public List<Notes> getNotesList() {
    	return notesList;
    }

	public void setNotesList(List<Notes> notesList) {
    	this.notesList = notesList;
    }

	public void setFinType(String finType) {
	    this.finType = finType;
    }

	public String getFinType() {
	    return finType;
    }

	public void setFinAmount(BigDecimal finAmount) {
	    this.finAmount = finAmount;
    }

	public BigDecimal getFinAmount() {
	    return finAmount;
    }

	public void setFinStartDate(Date finStartDate) {
	    this.finStartDate = finStartDate;
    }

	public Date getFinStartDate() {
	    return finStartDate;
    }

	public void setCcyFormat(int ccyFormat) {
	    this.ccyFormat = ccyFormat;
    }

	public int getCcyFormat() {
	    return ccyFormat;
    }

	public void setLastMntByUser(String lastMntByUser) {
	    this.lastMntByUser = lastMntByUser;
    }

	public String getLastMntByUser() {
	    return lastMntByUser;
    }

	public void setFinCcy(String finCcy) {
	    this.finCcy = finCcy;
    }

	public String getFinCcy() {
	    return finCcy;
    }

	public void setFinTypeDesc(String finTypeDesc) {
	    this.finTypeDesc = finTypeDesc;
    }

	public String getFinTypeDesc() {
	    return finTypeDesc;
    }

}
