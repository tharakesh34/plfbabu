package com.pennanttech.ws.model.customer;

public class AgreementRequest {
	private String finReference;
	private String cif;
	private String agreementType;
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
