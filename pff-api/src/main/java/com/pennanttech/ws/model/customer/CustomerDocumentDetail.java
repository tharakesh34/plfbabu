package com.pennanttech.ws.model.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerDocument;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "cif", "customerDocument", "custDocCategory", "returnStatus" })

public class CustomerDocumentDetail {
	@XmlElement
	private String cif;

	@JsonProperty("document")
	private CustomerDocument customerDocument;

	@JsonProperty("docCategory")
	private String custDocCategory;

	@XmlElement
	private WSReturnStatus returnStatus;

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public CustomerDocument getCustomerDocument() {
		return customerDocument;
	}

	public void setCustomerDocument(CustomerDocument customerDocument) {
		this.customerDocument = customerDocument;
	}

	public String getCustDocCategory() {
		return custDocCategory;
	}

	public void setCustDocCategory(String custDocCategory) {
		this.custDocCategory = custDocCategory;
	}

}
