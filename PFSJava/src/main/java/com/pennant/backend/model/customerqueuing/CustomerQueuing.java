package com.pennant.backend.model.customerqueuing;

import java.io.Serializable;
import java.util.Date;

public class CustomerQueuing implements Serializable {

	private static final long serialVersionUID = 6724157480105725958L;

	private long id;
	private long custID;
	private String CoreBankId;
	private Date eodDate;
	private int threadId;
	private int progress;
	private Date startTime;
	private Date endTime;
	private boolean loanExist;
	private boolean limitRebuild;
	private boolean eodProcess;
	private boolean active;

	public CustomerQueuing() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCoreBankId() {
		return CoreBankId;
	}

	public void setCoreBankId(String coreBankId) {
		CoreBankId = coreBankId;
	}

	public Date getEodDate() {
		return eodDate;
	}

	public void setEodDate(Date eodDate) {
		this.eodDate = eodDate;
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

	public boolean isEodProcess() {
		return eodProcess;
	}

	public void setEodProcess(boolean eodProcess) {
		this.eodProcess = eodProcess;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
