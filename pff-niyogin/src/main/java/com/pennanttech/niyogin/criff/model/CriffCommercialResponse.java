package com.pennanttech.niyogin.criff.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "message", "statusCode", "tradelines" })
@XmlRootElement(name = "crifCommercialResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CriffCommercialResponse implements Serializable {

	private static final long	serialVersionUID	= -4015171015029335973L;

	private String				message;
	private String				statusCode;
	@XmlElement(name = "data")
	private List<TradeLine>		tradelines;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public List<TradeLine> getTradelines() {
		return tradelines;
	}

	public void setTradelines(List<TradeLine> tradelines) {
		this.tradelines = tradelines;
	}

	@Override
	public String toString() {
		return "CrifCommercialResponse [message=" + message + ", statusCode=" + statusCode + ", tradelines="
				+ tradelines + "]";
	}

}
