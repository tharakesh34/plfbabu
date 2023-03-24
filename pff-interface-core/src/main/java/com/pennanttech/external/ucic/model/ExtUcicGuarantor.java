package com.pennanttech.external.ucic.model;

import java.math.BigDecimal;

public class ExtUcicGuarantor {
	private String guarantorCif;
	private BigDecimal finId;
	private String finreference;

	public String getGuarantorCif() {
		return guarantorCif;
	}

	public void setGuarantorCif(String guarantorCif) {
		this.guarantorCif = guarantorCif;
	}

	public BigDecimal getFinId() {
		return finId;
	}

	public void setFinId(BigDecimal finId) {
		this.finId = finId;
	}

	public String getFinreference() {
		return finreference;
	}

	public void setFinreference(String finreference) {
		this.finreference = finreference;
	}

}
