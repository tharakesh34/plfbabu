package com.pennant.backend.model.dedup.external;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class CustEmailDetail {

	@XmlElement
	private String emailType;
	@XmlElement
	private String emailId;

	public String getEmailType() {
		return emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@Override
	public String toString() {
		return "CustEmailDetail [emailType=" + emailType + ", emailId=" + emailId + "]";
	}

}
