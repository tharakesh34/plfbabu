package com.pennanttech.niyogin.legaldesk.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "custCIF", "finReference","apiType", "stampPaperData", "signersInfo", "formData" })
@XmlRootElement(name = "LegalDesk")
@XmlAccessorType(XmlAccessType.FIELD)
public class LegalDeskRequest implements Serializable{

	private static final long serialVersionUID = 235931532331924087L;

	@XmlElement(name = "CIF")
	private String			custCIF;
	private String			finReference;
	@XmlElement(name = "stamp_paper_data")
	private StampPaperData	stampPaperData;

	@XmlElement(name = "signers_info")
	private SignersInfo		signersInfo;

	@XmlElement(name = "FormData")
	private FormData		formData;
	@XmlElement(name="api_type")
	private String		 apiType;

	
	public StampPaperData getStampPaperData() {
		return stampPaperData;
	}

	public void setStampPaperData(StampPaperData stampPaperData) {
		this.stampPaperData = stampPaperData;
	}

	public SignersInfo getSignersInfo() {
		return signersInfo;
	}

	public void setSignersInfo(SignersInfo signersInfo) {
		this.signersInfo = signersInfo;
	}

	public FormData getFormData() {
		return formData;
	}

	public void setFormData(FormData formData) {
		this.formData = formData;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

}
