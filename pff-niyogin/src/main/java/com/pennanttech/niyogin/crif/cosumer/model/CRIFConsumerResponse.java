package com.pennanttech.niyogin.crif.cosumer.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "statusCode", "message", "loanDetailsData" })
@XmlRootElement(name = "crifConsumerResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CRIFConsumerResponse implements Serializable {

	private static final long serialVersionUID = 7694950473382895161L;

	private String				statusCode;
	private String				message;
	@XmlElement(name = "data")
	private List<LoanDetailsData>	loanDetailsData;

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<LoanDetailsData> getLoanDetailsData() {
		return loanDetailsData;
	}

	public void setLoanDetailsData(List<LoanDetailsData> loanDetailsData) {
		this.loanDetailsData = loanDetailsData;
	}

	@Override
	public String toString() {
		return "CRIFConsumerResponse [statusCode=" + statusCode + ", message=" + message + "]";
	}
	
}
