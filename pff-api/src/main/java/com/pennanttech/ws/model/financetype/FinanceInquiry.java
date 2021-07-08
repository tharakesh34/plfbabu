package com.pennanttech.ws.model.financetype;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;

@XmlType(propOrder = { "finance", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceInquiry {
	@XmlElementWrapper(name = "finances")
	@XmlElement(name = "finance")
	private List<FinInquiryDetail> finance;
	@XmlElement
	private WSReturnStatus returnStatus;

	public List<FinInquiryDetail> getFinance() {
		return finance;
	}

	public void setFinance(List<FinInquiryDetail> finance) {
		this.finance = finance;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
