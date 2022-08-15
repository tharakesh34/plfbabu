package com.pennanttech.pff.overdraft.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class OverdraftLimit extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long finID;
	private String finReference;
	private BigDecimal actualLimit = BigDecimal.ZERO;
	private BigDecimal monthlyLimit = BigDecimal.ZERO;
	private BigDecimal actualLimitBal = BigDecimal.ZERO;
	private BigDecimal monthlyLimitBal = BigDecimal.ZERO;
	private boolean blockLimit;
	private String blockType;
	private boolean newRecord;
	private long createdBy;
	private Timestamp createdOn;
	private LoggedInUser userDetails;
	private OverdraftLimit befImage;

	private Timestamp prevMntOn;
	private String custCIF;
	private boolean finIsActive;

	public OverdraftLimit() {
		super();
	}

	public OverdraftLimit copyEntity() {
		OverdraftLimit entity = new OverdraftLimit();

		entity.setId(this.id);
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setActualLimit(this.actualLimit);
		entity.setMonthlyLimit(this.monthlyLimit);
		entity.setActualLimitBal(this.actualLimitBal);
		entity.setMonthlyLimitBal(this.monthlyLimitBal);
		entity.setBlockLimit(this.blockLimit);
		entity.setBlockType(this.blockType);
		entity.setNewRecord(this.newRecord);
		entity.setCreatedBy(this.createdBy);
		entity.setCreatedOn(this.createdOn);
		entity.setUserDetails(this.userDetails);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setPrevMntOn(this.prevMntOn);
		entity.setCustCIF(this.custCIF);
		entity.setFinIsActive(this.finIsActive);
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
		excludeFields.add("prevMntOn");
		excludeFields.add("custCIF");
		excludeFields.add("finIsActive");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public BigDecimal getActualLimit() {
		return actualLimit;
	}

	public void setActualLimit(BigDecimal actualLimit) {
		this.actualLimit = actualLimit;
	}

	public BigDecimal getMonthlyLimit() {
		return monthlyLimit;
	}

	public void setMonthlyLimit(BigDecimal monthlyLimit) {
		this.monthlyLimit = monthlyLimit;
	}

	public BigDecimal getActualLimitBal() {
		return actualLimitBal;
	}

	public void setActualLimitBal(BigDecimal actualLimitBal) {
		this.actualLimitBal = actualLimitBal;
	}

	public BigDecimal getMonthlyLimitBal() {
		return monthlyLimitBal;
	}

	public void setMonthlyLimitBal(BigDecimal monthlyLimitBal) {
		this.monthlyLimitBal = monthlyLimitBal;
	}

	public boolean isBlockLimit() {
		return blockLimit;
	}

	public void setBlockLimit(boolean blockLimit) {
		this.blockLimit = blockLimit;
	}

	public String getBlockType() {
		return blockType;
	}

	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public OverdraftLimit getBefImage() {
		return befImage;
	}

	public void setBefImage(OverdraftLimit befImage) {
		this.befImage = befImage;
	}

	public Timestamp getPrevMntOn() {
		return prevMntOn;
	}

	public void setPrevMntOn(Timestamp prevMntOn) {
		this.prevMntOn = prevMntOn;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

}
