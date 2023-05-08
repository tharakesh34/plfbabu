package com.pennant.backend.model.blacklist;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class FinBlacklistCustomer implements Serializable {

	private static final long serialVersionUID = 1L;

	private long finID;
	private String finReference;
	private String custCIF;
	private String custFName;
	private String custLName;
	private String custShrtName;
	private Date custDOB;
	private String custCRCPR;
	private String custPassportNo;
	private String MobileNumber;
	private String custNationality;
	private String employer;
	private String watchListRule;
	private boolean override;
	private String overrideUser;

	private String overridenby;
	private boolean newBlacklistRecord = true;
	private String custCtgType;

	// Audit Purpose Fields
	private long lastMntBy;
	private String roleCode;
	private String recordStatus;
	private String source;
	private String reasonCode;
	private String sourceCIF;

	public FinBlacklistCustomer() {
	    super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custCtgType");
		excludeFields.add("overridenby");
		excludeFields.add("newBlacklistRecord");
		return excludeFields;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustFName() {
		return custFName;
	}

	public void setCustFName(String custFName) {
		this.custFName = custFName;
	}

	public String getCustLName() {
		return custLName;
	}

	public void setCustLName(String custLName) {
		this.custLName = custLName;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public Date getCustDOB() {
		return custDOB;
	}

	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}

	public String getCustPassportNo() {
		return custPassportNo;
	}

	public void setCustPassportNo(String custPassportNo) {
		this.custPassportNo = custPassportNo;
	}

	public String getCustNationality() {
		return custNationality;
	}

	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getWatchListRule() {
		return watchListRule;
	}

	public void setWatchListRule(String watchListRule) {
		this.watchListRule = watchListRule;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public String getCustCtgType() {
		return custCtgType;
	}

	public void setCustCtgType(String custCtgType) {
		this.custCtgType = custCtgType;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getOverrideUser() {
		return overrideUser;
	}

	public void setOverrideUser(String overrideUser) {
		this.overrideUser = overrideUser;
	}

	public String getMobileNumber() {
		return MobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		MobileNumber = mobileNumber;
	}

	public String getOverridenby() {
		return overridenby;
	}

	public void setOverridenby(String overridenby) {
		this.overridenby = overridenby;
	}

	public boolean isNewBlacklistRecord() {
		return newBlacklistRecord;
	}

	public void setNewBlacklistRecord(boolean newBlacklistRecord) {
		this.newBlacklistRecord = newBlacklistRecord;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getSourceCIF() {
		return sourceCIF;
	}

	public void setSourceCIF(String sourceCIF) {
		this.sourceCIF = sourceCIF;
	}

}
