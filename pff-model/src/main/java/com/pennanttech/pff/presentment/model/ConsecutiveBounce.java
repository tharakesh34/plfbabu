package com.pennanttech.pff.presentment.model;

import java.io.Serializable;
import java.util.Date;

public class ConsecutiveBounce implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private long mandateID;
	private long bounceID;
	private Date lastBounceDate;
	private int bounceCount;
	private Date createdOn;
	private Date lastMnton;

	public ConsecutiveBounce() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMandateID() {
		return mandateID;
	}

	public void setMandateID(long mandateID) {
		this.mandateID = mandateID;
	}

	public long getBounceID() {
		return bounceID;
	}

	public void setBounceID(long bounceID) {
		this.bounceID = bounceID;
	}

	public Date getLastBounceDate() {
		return lastBounceDate;
	}

	public void setLastBounceDate(Date lastBounceDate) {
		this.lastBounceDate = lastBounceDate;
	}

	public int getBounceCount() {
		return bounceCount;
	}

	public void setBounceCount(int bounceCount) {
		this.bounceCount = bounceCount;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastMnton() {
		return lastMnton;
	}

	public void setLastMnton(Date lastMnton) {
		this.lastMnton = lastMnton;
	}

}
