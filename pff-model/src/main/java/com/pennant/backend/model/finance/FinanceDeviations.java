package com.pennant.backend.model.finance;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "deviationId", "finReference", "module", "deviationCode", "deviationType", "deviationValue",
		"userRole", "delegationRole", "approvalStatus", "deviationDate", "deviationUserId", "delegatedUserId",
		"deviationCategory", "deviProcessed", "remarks", "approved", "deviationCodeName", "deviationCodeDesc",
		"deviationDesc", "severity", "severityCode", "severityName", "markDeleted", "raisedUser" })
@XmlRootElement(name = "financeDeviations")
@XmlAccessorType(XmlAccessType.FIELD)
public class FinanceDeviations extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 8456523350616062070L;

	@XmlElement
	private long deviationId = Long.MIN_VALUE;
	private long finID;
	@XmlElement
	private String finReference;
	@XmlElement
	private String module;
	@XmlElement
	private String deviationCode;
	@XmlElement
	private String deviationType;
	@XmlElement
	private String deviationValue;
	@XmlElement
	private String userRole;
	@XmlElement
	private String delegationRole;
	@XmlElement
	private String approvalStatus;
	@XmlElement
	private Timestamp deviationDate;
	@XmlElement
	private String deviationUserId;
	@XmlElement
	private String delegatedUserId;
	@XmlElement
	private String deviationCategory;
	@XmlElement
	private boolean deviProcessed = false;
	@XmlElement
	private String remarks;
	private String mitigants;
	private FinanceDeviations befImage;
	@XmlElement
	private boolean approved;

	private String custCIF;
	private String custShrtName;
	private long custID;
	private LoggedInUser userDetails;
	@XmlElement
	private String deviationCodeName;
	@XmlElement
	private String deviationCodeDesc;
	@XmlElement
	private String deviationDesc;
	@XmlElement
	private long severity;
	@XmlElement
	private String severityCode;
	@XmlElement
	private String severityName;
	@XmlElement
	private boolean markDeleted;
	@XmlElement
	private String raisedUser;

	public FinanceDeviations() {
		super();

		this.deviationDate = new Timestamp(System.currentTimeMillis());
		this.approvalStatus = "";
	}

	public FinanceDeviations(String deviationCode, String deviationDesc, String module, String deviationCategory) {
		this();

		this.deviationCode = deviationCode;
		this.deviationDesc = deviationDesc;
		this.module = module;
		this.deviationCategory = deviationCategory;
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

	public long getId() {
		return deviationId;
	}

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

	public String getRaisedUser() {
		return raisedUser;
	}

	public void setRaisedUser(String raisedUser) {
		this.raisedUser = raisedUser;
	}

	public String getMitigants() {
		return mitigants;
	}

	public void setMitigants(String mitigants) {
		this.mitigants = mitigants;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

}
