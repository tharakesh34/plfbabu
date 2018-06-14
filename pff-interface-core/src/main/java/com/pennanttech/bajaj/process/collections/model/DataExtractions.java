package com.pennanttech.bajaj.process.collections.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataExtractions {
	
	private long extractionId = Long.MIN_VALUE;
	private Date extractionDate;
	private String interfaceName;
	private int progress;
	private Date startTime;
	private Date endTime;
	private long lastMntBy;
	
	private List<CollectionFinances> collectionFinancesList = new ArrayList<CollectionFinances>();
	
	/**
	 * default constructor
	 */
	public DataExtractions() {
		super();
	}

	public long getExtractionId() {
		return extractionId;
	}

	public void setExtractionId(long extractionId) {
		this.extractionId = extractionId;
	}
	
	public Date getExtractionDate() {
		return extractionDate;
	}

	public void setExtractionDate(Date extractionDate) {
		this.extractionDate = extractionDate;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public List<CollectionFinances> getCollectionFinancesList() {
		return collectionFinancesList;
	}

	public void setCollectionFinancesList(List<CollectionFinances> collectionFinancesList) {
		this.collectionFinancesList = collectionFinancesList;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}
}
