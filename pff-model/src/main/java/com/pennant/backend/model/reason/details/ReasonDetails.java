package com.pennant.backend.model.reason.details;

import java.io.Serializable;

import com.pennant.backend.model.Entity;

public class ReasonDetails implements Serializable, Entity {
	private static final long serialVersionUID = 1L;

	private long headerId;
	private long reasonId;

	@Override
	public boolean isNew() {
		return false;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public long getReasonId() {
		return reasonId;
	}

	public void setReasonId(long reasonId) {
		this.reasonId = reasonId;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public void setId(long id) {

	}

}
