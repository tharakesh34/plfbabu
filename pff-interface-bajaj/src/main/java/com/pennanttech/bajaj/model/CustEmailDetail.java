package com.pennanttech.bajaj.model;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonPropertyOrder({ "appscore", "appscoreDP" })
@JsonSerialize
public class CustEmailDetail {

	@XmlElement
	private String	emailType;
	@XmlElement
	private String	emailId;

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
}
