package com.pennant.backend.model.customerqueuing;

import java.io.Serializable;
import java.util.Date;

public class CustomerQueuing implements Serializable {

	private static final long	serialVersionUID	= 6724157480105725958L;

	private long				custID;
	private Date				eodDate;
	private int					threadId;
	private int					progress;

	private Date				startTime;
	private Date				endTime;
	
	private boolean				loanExist;
	private boolean				limitRebuild;

	private boolean				eodProcess;
	
	//Unused variable for bean parameter. Temporary
	private boolean				active;

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

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
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
	
	public boolean isLoanExist() {
		return loanExist;
	}

	public void setLoanExist(boolean loanExist) {
		this.loanExist = loanExist;
	}

	public boolean isLimitRebuild() {
		return limitRebuild;
	}

	public void setLimitRebuild(boolean limitRebuild) {
		this.limitRebuild = limitRebuild;
	}
}
