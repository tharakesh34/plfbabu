package com.pennant.app.feerefundqueue;

import java.io.Serializable;
import java.util.Date;

public class FeeRefundProcessQueuing implements Serializable {

	private static final long serialVersionUID = 6724157480105725958L;

	private long feeRefundHeaderId;
	private long feeRefundDetailId;
	private String finReference;
	private String jsonObject;
	private Date eodDate;
	private int threadId;
	private int progress;
	private Date startTime;
	private Date endTime;
	private boolean eodProcess;
	private String errorLog;

	// setter and getters
	public long getFeeRefundHeaderId() {
		return feeRefundHeaderId;
	}

	public void setFeeRefundHeaderId(long feeRefundHeaderId) {
		this.feeRefundHeaderId = feeRefundHeaderId;
	}

	public long getFeeRefundDetailId() {
		return feeRefundDetailId;
	}

	public void setFeeRefundDetailId(long feeRefundDetailId) {
		this.feeRefundDetailId = feeRefundDetailId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(String jsonObject) {
		this.jsonObject = jsonObject;
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

	public boolean isEodProcess() {
		return eodProcess;
	}

	public void setEodProcess(boolean eodProcess) {
		this.eodProcess = eodProcess;
	}

	public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

}
