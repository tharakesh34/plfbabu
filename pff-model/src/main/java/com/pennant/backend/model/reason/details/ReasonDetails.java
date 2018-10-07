package com.pennant.backend.model.reason.details;

import java.io.Serializable;

public class ReasonDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	private long headerId;
	private long reasonId;

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

	public long getId() {
		return 0;
	}

	public void setId(long id) {

	}

}
