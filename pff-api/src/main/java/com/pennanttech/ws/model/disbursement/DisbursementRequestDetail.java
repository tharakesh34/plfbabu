package com.pennanttech.ws.model.disbursement;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
