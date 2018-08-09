package com.pennant.backend.model.finance;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinanceDeviations extends AbstractWorkflowEntity implements java.io.Serializable, Entity {
	private static final long	serialVersionUID	= 8456523350616062070L;

	private long				deviationId			= Long.MIN_VALUE;
	private String				finReference;
	private String				module;
	private String				deviationCode;

	private String				deviationType;
	private String				deviationValue;
	private String				userRole;
	private String				delegationRole;
	private String				approvalStatus;
	private Timestamp			deviationDate;
	private String				deviationUserId;
	private String				delegatedUserId;
	private String				deviationCategory;
	private boolean				deviProcessed=false;
	private String				remarks;
	private boolean				newRecord			= false;
	private FinanceDeviations	befImage;
	private boolean				approved;

	private String				custCIF;
	private String				custShrtName;
	private long				custID;
	@XmlTransient
	private LoggedInUser		userDetails;
	private String				deviationCodeName;
	private String				deviationCodeDesc;
	private String deviationDesc;
	private long				severity;
	private String				severityCode;
	private String				severityName;
	private boolean 			markDeleted;

	public FinanceDeviations() {
		super();

		this.deviationDate = new Timestamp(System.currentTimeMillis());
		this.approvalStatus = "";
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custCIF");
		excludeFields.add("custID");
		excludeFields.add("custShrtName");
		excludeFields.add("deviationCodeName");
		excludeFields.add("deviationCodeDesc");
		excludeFields.add("severity");
		excludeFields.add("severityName");
		excludeFields.add("severityCode");
		excludeFields.add("approved");

		excludeFields.add("recordStatus");
		excludeFields.add("roleCode");
		excludeFields.add("nextRoleCode");
		excludeFields.add("taskId");
		excludeFields.add("nextTaskId");
		excludeFields.add("recordType");
		excludeFields.add("workflowId");
		excludeFields.add("userAction");
		excludeFields.add("version");
		excludeFields.add("lastMntBy");
		excludeFields.add("lastMntOn");
		excludeFields.add("deviationDesc");

		return excludeFields;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getDeviationCode() {
		return deviationCode;
	}

	public void setDeviationCode(String deviationCode) {
		this.deviationCode = deviationCode;
	}

	public String getDeviationType() {
		return deviationType;
	}

	public void setDeviationType(String deviationType) {
		this.deviationType = deviationType;
	}

	public String getDeviationValue() {
		return deviationValue;
	}

	public void setDeviationValue(String deviationValue) {
		this.deviationValue = deviationValue;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getDelegationRole() {
		return delegationRole;
	}

	public void setDelegationRole(String delegationRole) {
		this.delegationRole = delegationRole;
	}

	public String getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getDeviationUserId() {
		return deviationUserId;
	}

	public void setDeviationUserId(String deviationUserId) {
		this.deviationUserId = deviationUserId;
	}

	public String getDelegatedUserId() {
		return delegatedUserId;
	}

	public void setDelegatedUserId(String delegatedUserId) {
		this.delegatedUserId = delegatedUserId;
	}

	public Timestamp getDeviationDate() {
		return deviationDate;
	}

	public void setDeviationDate(Timestamp deviationDate) {
		this.deviationDate = deviationDate;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getDeviationId() {
		return deviationId;
	}

	public void setDeviationId(long deviationId) {
		this.deviationId = deviationId;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	@Override
	public boolean isNew() {
		return newRecord;
	}

	@Override
	public long getId() {
		return deviationId;
	}

	@Override
	public void setId(long id) {
		this.deviationId = id;
	}

	public FinanceDeviations getBefImage() {
		return befImage;
	}

	public void setBefImage(FinanceDeviations befImage) {
		this.befImage = befImage;
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

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getDeviationCodeDesc() {
		return deviationCodeDesc;
	}

	public void setDeviationCodeDesc(String deviationCodeDesc) {
		this.deviationCodeDesc = deviationCodeDesc;
	}

	public String getDeviationCodeName() {
		return deviationCodeName;
	}

	public void setDeviationCodeName(String deviationCodeName) {
		this.deviationCodeName = deviationCodeName;
	}

	public String getSeverityCode() {
		return severityCode;
	}

	public void setSeverityCode(String severityCode) {
		this.severityCode = severityCode;
	}

	public String getSeverityName() {
		return severityName;
	}

	public void setSeverityName(String severityName) {
		this.severityName = severityName;
	}

	public long getSeverity() {
		return severity;
	}

	public void setSeverity(long severity) {
		this.severity = severity;
	}

	public boolean isDeviProcessed() {
		return deviProcessed;
	}

	public void setDeviProcessed(boolean deviProcessed) {
		this.deviProcessed = deviProcessed;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isMarkDeleted() {
		return markDeleted;
	}

	public void setMarkDeleted(boolean markDeleted) {
		this.markDeleted = markDeleted;
	}

	public String getDeviationCategory() {
		return deviationCategory;
	}

	public void setDeviationCategory(String deviationCategory) {
		this.deviationCategory = deviationCategory;
	}

	public String getDeviationDesc() {
		return deviationDesc;
	}

	public void setDeviationDesc(String deviationDesc) {
		this.deviationDesc = deviationDesc;
	}
}
