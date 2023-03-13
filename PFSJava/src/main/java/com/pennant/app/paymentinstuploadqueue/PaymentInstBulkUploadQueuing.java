package com.pennant.app.paymentinstuploadqueue;

import java.io.Serializable;
import java.util.Date;

public class PaymentInstBulkUploadQueuing implements Serializable {

	private static final long serialVersionUID = 6724157480105725958L;

	private long uploadHeaderId;
	private long uploadDetailId;
	private String promotionCode;
	private String jsonObject;
	private Date eodDate;
	private int threadId;
	private int progress;
	private Date startTime;
	private Date endTime;
	private boolean eodProcess;
	private String errorLog;

	// setter and getters
	public long getUploadHeaderId() {
		return uploadHeaderId;
	}

	public void setUploadHeaderId(long uploadHeaderId) {
		this.uploadHeaderId = uploadHeaderId;
	}

	public long getUploadDetailId() {
		return uploadDetailId;
	}

	public void setUploadDetailId(long uploadDetailId) {
		this.uploadDetailId = uploadDetailId;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
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
