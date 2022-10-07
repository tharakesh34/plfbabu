package com.pennant.pff.presentment.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class DueExtractionHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 2198471029043076055L;

	private long iD;
	private String extractionMonth;
	private long createdBy;
	private Timestamp createdOn;
	private long approvedBy;
	private Timestamp approvedOn;
	private boolean active;
	private Map<Long, InstrumentTypes> instruments = new HashMap<>();
	private List<DueExtractionConfig> config = new ArrayList<>();

	public DueExtractionHeader() {
		super();
	}

	public long getID() {
		return iD;
	}

	public void setID(long iD) {
		this.iD = iD;
	}

	public String getExtractionMonth() {
		return extractionMonth;
	}

	public void setExtractionMonth(String extractionMonth) {
		this.extractionMonth = extractionMonth;
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

	public Map<Long, InstrumentTypes> getInstruments() {
		return instruments;
	}

	public void setInstruments(Map<Long, InstrumentTypes> instruments) {
		this.instruments = instruments;
	}

	public List<DueExtractionConfig> getConfig() {
		return config;
	}

	public void setConfig(List<DueExtractionConfig> config) {
		this.config = config;
	}

}
