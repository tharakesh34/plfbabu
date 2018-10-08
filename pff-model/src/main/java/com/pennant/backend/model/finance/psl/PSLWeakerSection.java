package com.pennant.backend.model.finance.psl;

import java.io.Serializable;

public class PSLWeakerSection implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;
	private String description;

	public PSLWeakerSection() {
		super();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
