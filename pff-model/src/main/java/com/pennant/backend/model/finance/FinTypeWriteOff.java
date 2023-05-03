package com.pennant.backend.model.finance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinTypeWriteOff extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String loanType;
	private String finTypeDesc;
	private LoggedInUser userDetails;
	private FinTypeWriteOff befImage;
	private String pslCode;
	private int dpdDays = 0;
	private String pslCodeDesc;
	private int keyvalue = 0;
	private long createdBy;
	private Timestamp createdOn;
	private Timestamp approvedOn;
	private Long approvedBy;
	@XmlTransient
	private List<FinTypeWriteOff> loanTypeWriteOffMapping = new ArrayList<>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

	public FinTypeWriteOff() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("auditDetailMap");
		excludeFields.add("pslCodeDesc");
		excludeFields.add("finTypeDesc");
		excludeFields.add("keyvalue");
		excludeFields.add("loanTypeWriteOffMapping");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public FinTypeWriteOff getBefImage() {
		return befImage;
	}

	public void setBefImage(FinTypeWriteOff befImage) {
		this.befImage = befImage;
	}

	public String getPslCode() {
		return pslCode;
	}

	public void setPslCode(String pslCode) {
		this.pslCode = pslCode;
	}

	public int getDpdDays() {
		return dpdDays;
	}

	public void setDpdDays(int dpdDays) {
		this.dpdDays = dpdDays;
	}

	public String getPslCodeDesc() {
		return pslCodeDesc;
	}

	public void setPslCodeDesc(String pslCodeDesc) {
		this.pslCodeDesc = pslCodeDesc;
	}

	public int getKeyvalue() {
		return keyvalue;
	}

	public void setKeyvalue(int keyvalue) {
		this.keyvalue = keyvalue;
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

	public List<FinTypeWriteOff> getLoanTypeWriteOffMapping() {
		return loanTypeWriteOffMapping;
	}

	public void setLoanTypeWriteOffMapping(List<FinTypeWriteOff> loanTypeWriteOffMapping) {
		this.loanTypeWriteOffMapping = loanTypeWriteOffMapping;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

}
