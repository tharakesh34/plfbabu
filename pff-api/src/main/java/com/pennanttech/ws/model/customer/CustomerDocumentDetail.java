package com.pennanttech.ws.model.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerDocument;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"cif","customerDocument","custDocCategory", "returnStatus" })

public class CustomerDocumentDetail {
	@XmlElement
	private String cif;
	
	@XmlElement(name="document")
	private CustomerDocument customerDocument;
	
	@XmlElement(name = "docCategory")
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
