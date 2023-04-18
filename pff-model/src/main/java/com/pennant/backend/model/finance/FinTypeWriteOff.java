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

	private long id = Long.MIN_VALUE;
	private String loanType;
	private String finTypeDesc;
	private LoggedInUser userDetails;
	private FinTypeWriteOff befImage;
	private String pslCode;
	private int dpdDays = 0;
	private String pslCodeDesc;
	private int keyvalue = 0;
	@XmlTransient
	private List<FinTypeWriteOff> loanTypeWriteOffMapping = new ArrayList<FinTypeWriteOff>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("auditDetailMap");
		excludeFields.add("loanTypeKonckOffMapping");
		excludeFields.add("pslCodeDesc");
		excludeFields.add("finTypeDesc");
		excludeFields.add("keyvalue");
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

	public FinTypeWriteOff getBefImage() {
		return befImage;
	}

	public void setBefImage(FinTypeWriteOff befImage) {
		this.befImage = befImage;
	}

	private static final long serialVersionUID = 1L;

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public int getKeyvalue() {
		return keyvalue;
	}

	public void setKeyvalue(int keyvalue) {
		this.keyvalue = keyvalue;
	}
}
