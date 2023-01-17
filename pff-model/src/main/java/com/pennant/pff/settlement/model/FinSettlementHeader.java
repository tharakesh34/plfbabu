package com.pennant.pff.settlement.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinSettlementHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private long settlementType;
	private String settlementStatus;
	private Date startDate;
	private Date otsDate;
	private Date endDate;
	private String settlementReason;
	private long settlementReasonId;
	private BigDecimal settlementAmount = BigDecimal.ZERO;
	private Date settlementEndAfterGrace;
	private long noOfGraceDays;
	private long finID;
	private String finReference;
	private String settlementTypeId;
	private String settlementCode;
	private String settlementReasonDesc;
	private String cancelReasonCode;
	private String cancelRemarks;
	private String lovValue;
	private FinSettlementHeader befImage;
	private LoggedInUser userDetails;
	private String sourceId;
	private List<SettlementSchedule> settlementScheduleList = new ArrayList<>();
	private List<SettlementAllocationDetail> settlementAllocationDetails = new ArrayList<>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
	private SettlementTypeDetail settlementTypeDetail;

	public FinSettlementHeader() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("settlementTypeId");
		excludeFields.add("settlementCode");
		excludeFields.add("settlementReasonDesc");
		excludeFields.add("settlementScheduleList");
		excludeFields.add("settlementAllocationDetails");
		excludeFields.add("auditDetailMap");
		excludeFields.add("settlementTypeDetail");
		excludeFields.add("sourceId");
		excludeFields.add("cancelRemarks");
		excludeFields.add("cancelReasonCode");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(long settlementType) {
		this.settlementType = settlementType;
	}

	public String getSettlementStatus() {
		return settlementStatus;
	}

	public void setSettlementStatus(String settlementStatus) {
		this.settlementStatus = settlementStatus;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getOtsDate() {
		return otsDate;
	}

	public void setOtsDate(Date otsDate) {
		this.otsDate = otsDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getSettlementReason() {
		return settlementReason;
	}

	public void setSettlementReason(String settlementReason) {
		this.settlementReason = settlementReason;
	}

	public BigDecimal getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public Date getSettlementEndAfterGrace() {
		return settlementEndAfterGrace;
	}

	public void setSettlementEndAfterGrace(Date settlementEndAfterGrace) {
		this.settlementEndAfterGrace = settlementEndAfterGrace;
	}

	public long getNoOfGraceDays() {
		return noOfGraceDays;
	}

	public void setNoOfGraceDays(long noOfGraceDays) {
		this.noOfGraceDays = noOfGraceDays;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinSettlementHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(FinSettlementHeader befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public String getSettlementTypeId() {
		return settlementTypeId;
	}

	public void setSettlementTypeId(String settlementTypeId) {
		this.settlementTypeId = settlementTypeId;
	}

	public String getSettlementCode() {
		return settlementCode;
	}

	public void setSettlementCode(String settlementCode) {
		this.settlementCode = settlementCode;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public SettlementTypeDetail getSettlementTypeDetail() {
		return settlementTypeDetail;
	}

	public void setSettlementTypeDetail(SettlementTypeDetail settlementTypeDetail) {
		this.settlementTypeDetail = settlementTypeDetail;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getCancelRemarks() {
		return cancelRemarks;
	}

	public void setCancelRemarks(String cancelRemarks) {
		this.cancelRemarks = cancelRemarks;
	}

	public String getCancelReasonCode() {
		return cancelReasonCode;
	}

	public void setCancelReasonCode(String cancelReasonCode) {
		this.cancelReasonCode = cancelReasonCode;
	}

	public List<SettlementAllocationDetail> getSettlementAllocationDetails() {
		return settlementAllocationDetails;
	}

	public void setSettlementAllocationDetails(List<SettlementAllocationDetail> settlementAllocationDetails) {
		this.settlementAllocationDetails = settlementAllocationDetails;
	}

	public List<SettlementSchedule> getSettlementScheduleList() {
		return settlementScheduleList;
	}

	public void setSettlementScheduleList(List<SettlementSchedule> settlementScheduleList) {
		this.settlementScheduleList = settlementScheduleList;
	}

	public long getSettlementReasonId() {
		return settlementReasonId;
	}

	public void setSettlementReasonId(long settlementReasonId) {
		this.settlementReasonId = settlementReasonId;
	}

	public String getSettlementReasonDesc() {
		return settlementReasonDesc;
	}

	public void setSettlementReasonDesc(String settlementReasonDesc) {
		this.settlementReasonDesc = settlementReasonDesc;
	}

}
