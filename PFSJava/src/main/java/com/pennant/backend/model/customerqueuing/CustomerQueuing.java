package com.pennant.backend.model.customerqueuing;

import java.io.Serializable;
import java.util.Date;

public class CustomerQueuing implements Serializable {

	private static final long	serialVersionUID	= 6724157480105725958L;

	private long				custID;
	private Date				eodDate;
	private String				threadId;

	private String				progress;
	
//	private Date				startTime;
//	private Date				endTime;
//	private String				errorLog;
//	private String				status;
	public CustomerQueuing() {
		super();
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

//	public String getErrorLog() {
//		return errorLog;
//	}
//
//	public void setErrorLog(String errorLog) {
//		this.errorLog = errorLog;
//	}
//
//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//	public Date getStartTime() {
//		return startTime;
//	}
//
//	public void setStartTime(Date startTime) {
//		this.startTime = startTime;
//	}
//
//	public Date getEndTime() {
//		return endTime;
//	}
//
//	public void setEndTime(Date endTime) {
//		this.endTime = endTime;
//	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public Date getEodDate() {
		return eodDate;
	}

	public void setEodDate(Date eodDate) {
		this.eodDate = eodDate;
	}
}
