package com.pennant.pff.settlement.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class SettlementSchedule extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 4143719150678156593L;

	private long settlementHeaderID = Long.MIN_VALUE;
	private long settlementDetailID = Long.MIN_VALUE;
	private Date settlementInstalDate;
	private BigDecimal settlementAmount = BigDecimal.ZERO;
	private SettlementSchedule befImage;
	private LoggedInUser userDetails;
	private String sourceId;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

	public SettlementSchedule() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("sourceId");
		excludeFields.add("settlement");
		excludeFields.add("settlementScheduleList");
		excludeFields.add("auditDetailMap");
		return excludeFields;
	}

	public SettlementSchedule copyEntity() {
		SettlementSchedule entity = new SettlementSchedule();

		entity.setSettlementHeaderID(this.settlementHeaderID);
		entity.setSettlementDetailID(this.settlementDetailID);
		entity.setSettlementInstalDate(this.settlementInstalDate);
		entity.setSettlementAmount(this.settlementAmount);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setSourceId(this.sourceId);
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());

		return entity;
	}

	public long getSettlementHeaderID() {
		return settlementHeaderID;
	}

	public void setSettlementHeaderID(long settlementHeaderID) {
		this.settlementHeaderID = settlementHeaderID;
	}

	public long getSettlementDetailID() {
		return settlementDetailID;
	}

	public void setSettlementDetailID(long settlementDetailID) {
		this.settlementDetailID = settlementDetailID;
	}

	public Date getSettlementInstalDate() {
		return settlementInstalDate;
	}

	public void setSettlementInstalDate(Date settlementInstalDate) {
		this.settlementInstalDate = settlementInstalDate;
	}

	public BigDecimal getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public SettlementSchedule getBefImage() {
		return befImage;
	}

	public void setBefImage(SettlementSchedule befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setLoginDetails(LoggedInUser userDetails) {
		setLastMntBy(userDetails.getUserId());
		this.userDetails = userDetails;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

}
