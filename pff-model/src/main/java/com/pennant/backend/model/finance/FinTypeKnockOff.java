package com.pennant.backend.model.finance;

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

import jakarta.xml.bind.annotation.XmlTransient;

public class FinTypeKnockOff extends AbstractWorkflowEntity {

	private long id = Long.MIN_VALUE;
	private String loanType;
	private String finTypeDesc;
	private LoggedInUser userDetails;
	private FinTypeKnockOff befImage;
	private long knockOffId = Long.MIN_VALUE;
	private int knockOffOrder = 0;
	private String knockOffCode;
	private int keyvalue = 0;

	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private List<FinTypeKnockOff> loanTypeKonckOffMapping = new ArrayList<FinTypeKnockOff>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("auditDetailMap");
		excludeFields.add("loanTypeKonckOffMapping");
		excludeFields.add("knockOffCode");
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

	public FinTypeKnockOff getBefImage() {
		return befImage;
	}

	public void setBefImage(FinTypeKnockOff befImage) {
		this.befImage = befImage;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	private static final long serialVersionUID = 1L;

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public List<FinTypeKnockOff> getLoanTypeKonckOffMapping() {
		return loanTypeKonckOffMapping;
	}

	public void setLoanTypeKonckOffMapping(List<FinTypeKnockOff> loanTypeKonckOffMapping) {
		this.loanTypeKonckOffMapping = loanTypeKonckOffMapping;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public long getKnockOffId() {
		return knockOffId;
	}

	public void setKnockOffId(long knockOffId) {
		this.knockOffId = knockOffId;
	}

	public int getKnockOffOrder() {
		return knockOffOrder;
	}

	public void setKnockOffOrder(int knockOffOrder) {
		this.knockOffOrder = knockOffOrder;
	}

	public String getKnockOffCode() {
		return knockOffCode;
	}

	public void setKnockOffCode(String knockOffCode) {
		this.knockOffCode = knockOffCode;
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
