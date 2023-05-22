package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class FinanceScoreDetail implements Serializable {

	private static final long serialVersionUID = 5107165955623363431L;

	private long headerId = Long.MIN_VALUE;
	private long subGroupId = Long.MIN_VALUE;
	private String subGrpCodeDesc;
	private long ruleId = Long.MIN_VALUE;
	private String ruleCode;
	private String ruleCodeDesc;
	private BigDecimal maxScore;
	private BigDecimal execScore;
	private String categoryType;
	private long lastMntBy;
	private String roleCode;
	private String recordStatus;
	private String recordType = "NEW";// FIXME:How to use constant-PennantConstants.RECORD_TYPE_NEW;
	private FinanceScoreDetail befImage;

	public FinanceScoreDetail() {
	    super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("subGrpCodeDesc");
		excludeFields.add("ruleCode");
		excludeFields.add("ruleCodeDesc");
		excludeFields.add("categoryType");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public long getSubGroupId() {
		return subGroupId;
	}

	public void setSubGroupId(long subGroupId) {
		this.subGroupId = subGroupId;
	}

	public String getSubGrpCodeDesc() {
		return subGrpCodeDesc;
	}

	public void setSubGrpCodeDesc(String subGrpCodeDesc) {
		this.subGrpCodeDesc = subGrpCodeDesc;
	}

	public long getRuleId() {
		return ruleId;
	}

	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getRuleCodeDesc() {
		return ruleCodeDesc;
	}

	public void setRuleCodeDesc(String ruleCodeDesc) {
		this.ruleCodeDesc = ruleCodeDesc;
	}

	public BigDecimal getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(BigDecimal maxScore) {
		this.maxScore = maxScore;
	}

	public BigDecimal getExecScore() {
		return execScore;
	}

	public void setExecScore(BigDecimal execScore) {
		this.execScore = execScore;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
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

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public FinanceScoreDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(FinanceScoreDetail befImage) {
		this.befImage = befImage;
	}

}
