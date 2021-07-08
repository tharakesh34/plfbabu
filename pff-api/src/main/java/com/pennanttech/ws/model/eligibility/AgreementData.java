package com.pennanttech.ws.model.eligibility;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.WSReturnStatus;

@XmlAccessorType(XmlAccessType.NONE)
public class AgreementData implements Serializable {
	private static final long serialVersionUID = 1L;

	private String cif;
	@XmlElement
	private String finReference;
	private String agreementName;
	@XmlElement
	private String agreementType;
	private String reportName;
	@XmlElement
	private byte[] docContent;
	@XmlElement
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

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
}
