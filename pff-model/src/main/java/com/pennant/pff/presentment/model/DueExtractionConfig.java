package com.pennant.pff.presentment.model;

import java.sql.Timestamp;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class DueExtractionConfig extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 2198471029043076055L;

	private long iD;
	private long monthID;
	private long instrumentID;
	private Date dueDate;
	private Date extractionDate;
	private boolean modified;
	private long createdBy;
	private Timestamp createdOn;
	private long approvedBy;
	private Timestamp approvedOn;
	private boolean active;

	private String instrumentCode;
	private int configureDays;

	public DueExtractionConfig() {
		super();
	}

	public long getID() {
		return iD;
	}

	public void setID(long iD) {
		this.iD = iD;
	}

	public long getMonthID() {
		return monthID;
	}

	public void setMonthID(long monthID) {
		this.monthID = monthID;
	}

	public long getInstrumentID() {
		return instrumentID;
	}

	public void setInstrumentID(long instrumentID) {
		this.instrumentID = instrumentID;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getExtractionDate() {
		return extractionDate;
	}

	public void setExtractionDate(Date extractionDate) {
		this.extractionDate = extractionDate;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
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

	public long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getInstrumentCode() {
		return instrumentCode;
	}

	public void setInstrumentCode(String instrumentCode) {
		this.instrumentCode = instrumentCode;
	}

	public int getConfigureDays() {
		return configureDays;
	}

	public void setConfigureDays(int configureDays) {
		this.configureDays = configureDays;
	}

}
