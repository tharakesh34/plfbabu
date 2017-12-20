package com.pennanttech.niyogin.legaldesk.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "stampPaperData", "signersInfo", "formData" })
@XmlRootElement(name = "LegalDesk")
@XmlAccessorType(XmlAccessType.FIELD)
public class LegalDeskRequest {

	@XmlElement(name = "stamp_paper_data")
	private StampPaperData	stampPaperData;

	@XmlElement(name = "signers_info")
	private SignersInfo		signersInfo;

	@XmlElement(name = "FormData")
	private FormData		formData;

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

}
