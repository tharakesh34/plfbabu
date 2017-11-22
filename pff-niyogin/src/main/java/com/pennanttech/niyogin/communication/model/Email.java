package com.pennanttech.niyogin.communication.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "from", "to", "subject", "body", "returnCode", "returnText" })
@XmlRootElement(name = "email")
@XmlAccessorType(XmlAccessType.FIELD)
public class Email implements Serializable {

	private static final long serialVersionUID = 3734972682696376918L;
	private String from;
	private String to;
	private String subject;
	private String body;
	@XmlElement(name = "status")
	private String returnCode;
	@XmlElement(name = "message")
	private String returnText;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	@Override
	public String toString() {
		return "Email [from=" + from + ", to=" + to + ", subject=" + subject + ", body=" + body + ", returnCode="
				+ returnCode + ", returnText=" + returnText + "]";
	}

}
