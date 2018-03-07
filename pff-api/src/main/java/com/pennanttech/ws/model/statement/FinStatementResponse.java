package com.pennanttech.ws.model.statement;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "customer", "finance","finReference","docImage", "returnStatus" })
public class FinStatementResponse {
	@XmlElement
	private CustomerDetails		customer;
	@XmlElement
	private List<FinanceDetail>	finance	= null;
	@XmlElement
	private WSReturnStatus		returnStatus;
	@XmlElement
	private String 				finReference;
	@XmlElement(name="docContent")
	private byte[] docImage;


	public CustomerDetails getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerDetails customer) {
		this.customer = customer;
	}

	public List<FinanceDetail> getFinance() {
		return finance;
	}

	public void setFinance(List<FinanceDetail> finance) {
		this.finance = finance;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}
}
