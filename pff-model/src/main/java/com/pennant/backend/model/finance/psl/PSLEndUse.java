package com.pennant.backend.model.finance.psl;

import java.io.Serializable;

public class PSLEndUse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String purposeCode;
	private String code;
	private String description;

	public PSLEndUse() {
		super();
	}

	public String getPurposeCode() {
		return purposeCode;
	}

	public void setPurposeCode(String purposeCode) {
		this.purposeCode = purposeCode;
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
