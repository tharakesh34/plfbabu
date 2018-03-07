package com.pennant.backend.model.customerqueuing;

import java.io.Serializable;
import java.util.Date;

public class CustomerGroupQueuing implements Serializable {

	private static final long	serialVersionUID	= 6724157480105725958L;

	private long				groupId;
	private Date				eodDate;
	private Date				startTime;
	private Date				endTime;
	private int					progress;
	private String				errorLog;
	private String				status;
	
	private boolean				eodProcess;
	
	//Unused variable for bean parameter. Temporary
	private boolean				active;
	
	public CustomerGroupQueuing() {
		super();
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Date getEodDate() {
		return eodDate;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void setEodDate(Date eodDate) {
		this.eodDate = eodDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isEodProcess() {
		return eodProcess;
	}

	public void setEodProcess(boolean eodProcess) {
		this.eodProcess = eodProcess;
	}
}
