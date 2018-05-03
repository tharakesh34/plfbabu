package com.pennant.coreinterface.model.chequeverification;

import java.io.Serializable;

public class ChequeStatus implements Serializable {

	private static final long serialVersionUID = 7769822879805495569L;

	public ChequeStatus() {
		super();
	}

	private String chequeNo;
	private String validity;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}
}
