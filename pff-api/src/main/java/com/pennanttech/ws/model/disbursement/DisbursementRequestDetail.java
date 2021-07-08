package com.pennanttech.ws.model.disbursement;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;

@XmlType(propOrder = { "disbDetail", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "disbursement")
public class DisbursementRequestDetail {

	@XmlElementWrapper(name = "disbursements")
	@XmlElement(name = "disbDetail")
	private List<DisbursementRequest> disbDetail;
	@XmlElement
	private WSReturnStatus returnStatus;

	public List<DisbursementRequest> getDisbDetail() {
		return disbDetail;
	}

	public void setDisbDetail(List<DisbursementRequest> disbDetail) {
		this.disbDetail = disbDetail;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
