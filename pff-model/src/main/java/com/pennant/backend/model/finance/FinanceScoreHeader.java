package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class FinanceScoreHeader implements Serializable {
	private static final long serialVersionUID = -453344106804424915L;

	private long headerId = Long.MIN_VALUE;
	private long finID;
	private String finReference;
	private long groupId = Long.MIN_VALUE;
	private String groupCode;
	private String groupCodeDesc;
	private int minScore;
	private boolean override;
	private int overrideScore;
	private String creditWorth;
	private long lastMntBy;
	private String roleCode;
	private String recordStatus;
	private long custId;

	public FinanceScoreHeader() {
	    super();
	}

	public long getId() {
		return headerId;
	}

	public void setId(long id) {
		this.headerId = id;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("groupCode");
		excludeFields.add("groupCodeDesc");
		return excludeFields;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
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

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupCodeDesc() {
		return groupCodeDesc;
	}

	public void setGroupCodeDesc(String groupCodeDesc) {
		this.groupCodeDesc = groupCodeDesc;
	}

	public int getMinScore() {
		return minScore;
	}

	public void setMinScore(int minScore) {
		this.minScore = minScore;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public int getOverrideScore() {
		return overrideScore;
	}

	public void setOverrideScore(int overrideScore) {
		this.overrideScore = overrideScore;
	}

	public String getCreditWorth() {
		return creditWorth;
	}

	public void setCreditWorth(String creditWorth) {
		this.creditWorth = creditWorth;
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

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

}
