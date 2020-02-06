package com.pennanttech.ws.model.eligibility;

import java.io.Serializable;

import com.pennant.backend.model.WSReturnStatus;

public class AgreementData implements Serializable {
	private static final long serialVersionUID = 1L;

	private String cif;
	private String agreementName;
	private String agreementType;
	private String reportName;
	private byte[] docContent;
	private WSReturnStatus returnStatus;

	public AgreementData() {
		super();
	}

	public byte[] getDocContent() {
		return docContent;
	}

	public void setDocContent(byte[] docContent) {
		this.docContent = docContent;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getAgreementName() {
		return agreementName;
	}

	public void setAgreementName(String agreementName) {
		this.agreementName = agreementName;
	}

	public String getAgreementType() {
		return agreementType;
	}

	public void setAgreementType(String agreementType) {
		this.agreementType = agreementType;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
