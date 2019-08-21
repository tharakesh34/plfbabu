package com.pennanttech.ws.model.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerGST;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "cif", "customerGST", "Id", "returnStatus" })
public class CustomerGstInfoDetail {
	@XmlElement
	private String cif;
	@XmlElementWrapper(name = "CustomerGST")
	@XmlElement
	private CustomerGST CustomerGST;
	@XmlElement
	private long Id = Long.MIN_VALUE;
	@XmlElement
	private WSReturnStatus returnStatus;

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public CustomerGST getCustomerGST() {
		return CustomerGST;
	}

	public void setCustomerGST(CustomerGST customerGST) {
		CustomerGST = customerGST;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
