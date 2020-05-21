package com.pennanttech.backend.model.external.control;

import java.io.Serializable;
import java.util.Date;

public class PushPullControl implements Serializable {
	private static final long serialVersionUID = -2237757755125286461L;

	private long iD;
	private String name;
	private String type;
	private String status;
	private Date lastRunDate;

	public PushPullControl() {
		super();
	}

	public long getID() {
		return iD;
	}

	public void setID(long iD) {
		this.iD = iD;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastRunDate() {
		return lastRunDate;
	}

	public void setLastRunDate(Date lastRunDate) {
		this.lastRunDate = lastRunDate;
	}

}
