package com.pennanttech.ws.model.manualAdvice;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.ws.model.finance.TaxDetail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "adviseId", "finTaxDetails", "returnStatus" })
@XmlRootElement(name = "ManualAdviseResponse")
@XmlAccessorType(XmlAccessType.NONE)
public class ManualAdviseResponse {

	@XmlElement
	private long adviseId = Long.MIN_VALUE;

	@XmlElement(name = "finGSTDetail")
	private TaxDetail taxDetail;

	@XmlElement
	private WSReturnStatus returnStatus;

	public long getAdviseId() {
		return adviseId;
	}

	public void setAdviseId(long adviseId) {
		this.adviseId = adviseId;
	}

	public TaxDetail getTaxDetail() {
		return taxDetail;
	}

	public void setTaxDetail(TaxDetail taxDetail) {
		this.taxDetail = taxDetail;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
