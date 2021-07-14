package com.pennanttech.ws.model.finance;

import java.io.Serializable;
import java.util.List;

import com.pennant.backend.model.WSReturnStatus;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "finReference", "disbResponse", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinAdvPaymentDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement
	private String finReference;
	@XmlElement
	private List<DisbResponse> disbResponse;
	@XmlElement
	private WSReturnStatus returnStatus;

	public FinAdvPaymentDetail() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public List<DisbResponse> getDisbResponse() {
		return disbResponse;
	}

	public void setDisbResponse(List<DisbResponse> disbResponse) {
		this.disbResponse = disbResponse;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
