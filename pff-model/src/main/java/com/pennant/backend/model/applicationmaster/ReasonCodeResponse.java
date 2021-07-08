package com.pennant.backend.model.applicationmaster;

import java.io.Serializable;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import com.pennant.backend.model.WSReturnStatus;

@XmlAccessorType(XmlAccessType.FIELD)
public class ReasonCodeResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	List<ReasonCode> reasonCode = null;
	private WSReturnStatus returnStatus;

	public ReasonCodeResponse() {
		super();
	}

	public List<ReasonCode> getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(List<ReasonCode> reasonCode) {
		this.reasonCode = reasonCode;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
