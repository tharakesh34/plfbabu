package com.pennant.backend.model.finance;

import java.io.Serializable;

public class TATNotificationCode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6255981029525749939L;

	private long tatNotificationId = Long.MIN_VALUE;
	private String tatNotificationCode;
	private String tatNotificationDesc;
	private String time;

	public TATNotificationCode() {
	    super();
	}

	public TATNotificationCode(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return tatNotificationId;
	}

	public void setId(long id) {
		this.tatNotificationId = id;
	}

	public long getTatNotificationId() {
		return tatNotificationId;
	}

	public void setTatNotificationId(long tatNotificationId) {
		this.tatNotificationId = tatNotificationId;
	}

	public String getTatNotificationCode() {
		return tatNotificationCode;
	}

	public void setTatNotificationCode(String tatNotificationCode) {
		this.tatNotificationCode = tatNotificationCode;
	}

	public String getTatNotificationDesc() {
		return tatNotificationDesc;
	}

	public void setTatNotificationDesc(String tatNotificationDesc) {
		this.tatNotificationDesc = tatNotificationDesc;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
