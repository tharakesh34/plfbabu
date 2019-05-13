package com.pennanttech.ws.model.manualAdvice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinTaxDetails;

@XmlType(propOrder = { "adviseId", "finTaxDetails", "returnStatus" })
@XmlRootElement(name = "ManualAdviseResponse")
@XmlAccessorType(XmlAccessType.NONE)
public class ManualAdviseResponse {

	@XmlElement
	private long adviseId = Long.MIN_VALUE;

	@XmlElement(name = "finGSTDetail")
	private FinTaxDetails finTaxDetails;

	@XmlElement
	private WSReturnStatus returnStatus;

	public long getAdviseId() {
		return adviseId;
	}

	public void setAdviseId(long adviseId) {
		this.adviseId = adviseId;
	}

	public FinTaxDetails getFinTaxDetails() {
		return finTaxDetails;
	}

	public void setFinTaxDetails(FinTaxDetails finTaxDetails) {
		this.finTaxDetails = finTaxDetails;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
