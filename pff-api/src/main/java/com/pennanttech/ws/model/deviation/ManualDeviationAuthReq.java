package com.pennanttech.ws.model.deviation;

import java.io.Serializable;

public class ManualDeviationAuthReq implements Serializable {
	private static final long serialVersionUID = 1L;

	private String finReference;
	private String manDevcode;

	public ManualDeviationAuthReq() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getManDevcode() {
		return manDevcode;
	}

	public void setManDevcode(String manDevcode) {
		this.manDevcode = manDevcode;
	}

}
