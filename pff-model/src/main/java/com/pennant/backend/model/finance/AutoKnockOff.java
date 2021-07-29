package com.pennant.backend.model.finance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import javax.xml.bind.annotation.XmlTransient;

public class AutoKnockOff extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String code;
	private String description;
	private String executionDays;
	private boolean active;
	private LoggedInUser userDetails;
	private AutoKnockOff befImage;
	@XmlTransient
	private String lovValue;
	private String finreference;
	private String finType;
	private String finTypeDesc;
	private String feeTypeCode;
	private String knockOffOrder;
	private int feeOrder;

	private List<AutoKnockOffFeeMapping> mappingList = new ArrayList<>();
	private Map<String, List<AuditDetail>> lovDescAuditDetailMap = new HashMap<>();

	public AutoKnockOff() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("finreference");
		excludeFields.add("finType");
		excludeFields.add("finTypeDesc");
		excludeFields.add("feeTypeCode");
		excludeFields.add("knockOffOrder");
		excludeFields.add("feeOrder");

		return excludeFields;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExecutionDays() {
		return executionDays;
	}

	public void setExecutionDays(String executionDays) {
		this.executionDays = executionDays;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<AutoKnockOffFeeMapping> getMappingList() {
		return mappingList;
	}

	public void setMappingList(List<AutoKnockOffFeeMapping> mappingList) {
		this.mappingList = mappingList;
	}

	public AutoKnockOff getBefImage() {
		return befImage;
	}

	public void setBefImage(AutoKnockOff befImage) {
		this.befImage = befImage;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public Map<String, List<AuditDetail>> getLovDescAuditDetailMap() {
		return lovDescAuditDetailMap;
	}

	public void setLovDescAuditDetailMap(Map<String, List<AuditDetail>> lovDescAuditDetailMap) {
		this.lovDescAuditDetailMap = lovDescAuditDetailMap;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFinreference() {
		return finreference;
	}

	public void setFinreference(String finreference) {
		this.finreference = finreference;
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

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getKnockOffOrder() {
		return knockOffOrder;
	}

	public void setKnockOffOrder(String knocOffOrder) {
		this.knockOffOrder = knocOffOrder;
	}

	public int getFeeOrder() {
		return feeOrder;
	}

	public void setFeeOrder(int feeOrder) {
		this.feeOrder = feeOrder;
	}

}
