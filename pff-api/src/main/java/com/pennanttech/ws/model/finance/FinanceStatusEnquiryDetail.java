package com.pennanttech.ws.model.finance;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinanceStatusEnquiry;

@XmlType(propOrder = { "financeStatusEnquiryList", "returnStatus" })
@XmlAccessorType(XmlAccessType.FIELD)
public class FinanceStatusEnquiryDetail {
	@XmlElementWrapper(name = "finance")
	@XmlElement(name = "financeStatusEnquiryDetail")
	private List<FinanceStatusEnquiry> financeStatusEnquiryList;
	private WSReturnStatus returnStatus;

	public List<FinanceStatusEnquiry> getFinanceStatusEnquiryList() {
		return financeStatusEnquiryList;
	}

	public void setFinanceStatusEnquiryList(List<FinanceStatusEnquiry> financeStatusEnquiryList) {
		this.financeStatusEnquiryList = financeStatusEnquiryList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
