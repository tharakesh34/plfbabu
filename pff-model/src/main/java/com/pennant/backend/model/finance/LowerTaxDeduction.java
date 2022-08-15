package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class LowerTaxDeduction extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -3026443763391506067L;

	private long id = Long.MIN_VALUE;
	private long finID;
	private String finReference;
	private int seqNo;
	private BigDecimal percentage = BigDecimal.ZERO;
	private Date startDate;
	private Date endDate;
	private BigDecimal limitAmt = BigDecimal.ZERO;
	private long finMaintainId;

	public LowerTaxDeduction() {
		super();
	}

	public LowerTaxDeduction copyEntity() {
		LowerTaxDeduction entity = new LowerTaxDeduction();
		entity.setId(this.id);
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setSeqNo(this.seqNo);
		entity.setPercentage(this.percentage);
		entity.setStartDate(this.startDate);
		entity.setEndDate(this.endDate);
		entity.setLimitAmt(this.limitAmt);
		entity.setFinMaintainId(this.finMaintainId);
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

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
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

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getLimitAmt() {
		return limitAmt;
	}

	public void setLimitAmt(BigDecimal limitAmt) {
		this.limitAmt = limitAmt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFinMaintainId() {
		return finMaintainId;
	}

	public void setFinMaintainId(long finMaintainId) {
		this.finMaintainId = finMaintainId;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		// TODO Auto-generated method stub

	}

	public Object getBefImage() {
		// TODO Auto-generated method stub
		return null;
	}
}
