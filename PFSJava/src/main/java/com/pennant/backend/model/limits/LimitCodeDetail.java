package com.pennant.backend.model.limits;

import java.io.Serializable;

public class LimitCodeDetail implements Serializable {

	private static final long serialVersionUID = -9216555094010059742L;

	private long limitId = Long.MIN_VALUE;
	private String limitCode;
	private String limitDesc;

	public LimitCodeDetail() {
	    super();
	}

	public LimitCodeDetail(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return limitId;
	}

	public void setId(long id) {
		this.limitId = id;
	}

	public long getLimitId() {
		return limitId;
	}

	public void setLimitId(long limitId) {
		this.limitId = limitId;
	}

	public String getLimitCode() {
		return limitCode;
	}

	public void setLimitCode(String limitCode) {
		this.limitCode = limitCode;
	}

	public String getLimitDesc() {
		return limitDesc;
	}

	public void setLimitDesc(String limitDesc) {
		this.limitDesc = limitDesc;
	}
}
