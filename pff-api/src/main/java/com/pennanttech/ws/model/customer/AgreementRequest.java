package com.pennanttech.ws.model.customer;

import java.io.Serializable;

public class AgreementRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String finReference;
	private String cif;
	private String agreementType;

	public AgreementRequest() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getAgreementType() {
		return agreementType;
	}

	public void setAgreementType(String agreementType) {
		this.agreementType = agreementType;
	}

}
