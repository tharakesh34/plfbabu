package com.pennant.backend.model.rmtmasters;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class LoanTypeLetterMapping extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String finType;
	private String finTypeDesc;
	private String letterType;
	private boolean autoGeneration;
	private String letterMode;
	private long emailTemplateId;
	private String emailTemplate;
	private long agreementCodeId;
	private String agreementCode;
	private long createdBy;
	private Timestamp createdOn;
	private Timestamp approvedOn;
	private Long approvedBy;
	private boolean active;
	private int keyValue = 0;

	private List<LoanTypeLetterMapping> loanTypeLetterMappingList = new ArrayList<>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
	private LoanTypeLetterMapping befImage;
	private LoggedInUser userDetails;

	public LoanTypeLetterMapping() {
		super();
	}

	public LoanTypeLetterMapping(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("auditDetailMap");
		excludeFields.add("finTypeDesc");
		excludeFields.add("keyValue");
		excludeFields.add("active");
		excludeFields.add("emailTemplate");
		excludeFields.add("agreementCode");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getLetterType() {
		return letterType;
	}

	public void setLetterType(String letterType) {
		this.letterType = letterType;
	}

	public boolean isAutoGeneration() {
		return autoGeneration;
	}

	public void setAutoGeneration(boolean autoGeneration) {
		this.autoGeneration = autoGeneration;
	}

	public String getLetterMode() {
		return letterMode;
	}

	public void setLetterMode(String letterMode) {
		this.letterMode = letterMode;
	}

	public long getEmailTemplateId() {
		return emailTemplateId;
	}

	public void setEmailTemplateId(long emailTemplateId) {
		this.emailTemplateId = emailTemplateId;
	}

	public String getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(String emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public long getAgreementCodeId() {
		return agreementCodeId;
	}

	public void setAgreementCodeId(long agreementCodeId) {
		this.agreementCodeId = agreementCodeId;
	}

	public String getAgreementCode() {
		return agreementCode;
	}

	public void setAgreementCode(String agreementCode) {
		this.agreementCode = agreementCode;
	}

	public LoanTypeLetterMapping getBefImage() {
		return befImage;
	}

	public void setBefImage(LoanTypeLetterMapping befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(int keyValue) {
		this.keyValue = keyValue;
	}

	public List<LoanTypeLetterMapping> getLoanTypeLetterMappingList() {
		return loanTypeLetterMappingList;
	}

	public void setLoanTypeLetterMappingList(List<LoanTypeLetterMapping> loanTypeLetterMappingList) {
		this.loanTypeLetterMappingList = loanTypeLetterMappingList;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}
}