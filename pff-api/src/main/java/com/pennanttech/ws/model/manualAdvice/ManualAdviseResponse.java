package com.pennanttech.ws.model.manualAdvice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.ws.model.finance.TaxDetail;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "adviseId", "finTaxDetails", "returnStatus" })
@XmlRootElement(name = "ManualAdviseResponse")
@XmlAccessorType(XmlAccessType.NONE)
public class ManualAdviseResponse {

	@XmlElement
	private long adviseId = Long.MIN_VALUE;

	@JsonProperty("finGSTDetail")
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
