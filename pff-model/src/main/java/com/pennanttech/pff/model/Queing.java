package com.pennanttech.pff.model;

import java.io.Serializable;

public class Queing implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private long resetId;

	public Queing() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getResetId() {
		return resetId;
	}

	public void setResetId(long resetId) {
		this.resetId = resetId;
	}

}
