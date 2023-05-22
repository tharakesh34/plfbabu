package com.pennant.backend.model.messages;

import java.util.Date;

public class OfflineUsersMessagesBackup implements java.io.Serializable {

	private static final long serialVersionUID = -5899557096728410836L;

	private String fromUsrID;
	private String toUsrID;
	private Date sendTime;
	private String message;

	public OfflineUsersMessagesBackup() {
	    super();
	}

	public String getFromUsrID() {
		return fromUsrID;
	}

	public void setFromUsrID(String fromUsrID) {
		this.fromUsrID = fromUsrID;
	}

	public String getToUsrID() {
		return toUsrID;
	}

	public void setToUsrID(String toUsrID) {
		this.toUsrID = toUsrID;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
