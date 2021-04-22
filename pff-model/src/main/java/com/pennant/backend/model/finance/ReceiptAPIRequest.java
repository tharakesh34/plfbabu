package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.Date;

/**
 * @author kavya.n
 *
 */
public class ReceiptAPIRequest implements Serializable {
	private static final long serialVersionUID = 7595477558333079672L;

	private long ID;
	private long messageId;
	private long receiptId;
	private Date requestTime;
	private String responseCode;
	private String status;
	private int retryCount;
	private Date retryOn;

	public ReceiptAPIRequest() {
		super();
	}

	public long getID() {
		return ID;
	}

	public void setID(long id) {
		ID = id;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public Date getRetryOn() {
		return retryOn;
	}

	public void setRetryOn(Date retryOn) {
		this.retryOn = retryOn;
	}

}
