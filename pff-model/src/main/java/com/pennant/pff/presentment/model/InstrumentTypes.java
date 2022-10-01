package com.pennant.pff.presentment.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.pennant.pff.presentment.model.DueExtractionConfig;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class InstrumentTypes extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 2198471029043076055L;

	private long iD;
	private String code;
	private String description;
	private boolean internal;
	private boolean enabled;
	private boolean autoExtraction;
	private int extractionDays;
	private long createdBy;
	private Timestamp createdOn;
	private long approvedBy;
	private Timestamp approvedOn;
	private boolean active;
	private List<DueExtractionConfig> mapping = new ArrayList<>();

	public InstrumentTypes() {
		super();
	}

	public long getID() {
		return iD;
	}

	public void setID(long iD) {
		this.iD = iD;
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

	public boolean isInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAutoExtraction() {
		return autoExtraction;
	}

	public void setAutoExtraction(boolean autoExtraction) {
		this.autoExtraction = autoExtraction;
	}

	public int getExtractionDays() {
		return extractionDays;
	}

	public void setExtractionDays(int extractionDays) {
		this.extractionDays = extractionDays;
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

	public List<DueExtractionConfig> getMapping() {
		return mapping;
	}

	public void setMapping(List<DueExtractionConfig> mapping) {
		this.mapping = mapping;
	}

}
