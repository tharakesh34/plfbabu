package com.pennant.backend.model.Notifications;

import java.io.Serializable;

public class SystemNotificationAttributes implements Serializable {
	static final long serialVersionUID = 1L;

	private long Id;
	private long notificationId;
	private String name;
	private String type;
	private String format;
	private boolean attribute;

	public SystemNotificationAttributes() {
		super();
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(long notificationId) {
		this.notificationId = notificationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isAttribute() {
		return attribute;
	}

	public void setAttribute(boolean attribute) {
		this.attribute = attribute;
	}

}
