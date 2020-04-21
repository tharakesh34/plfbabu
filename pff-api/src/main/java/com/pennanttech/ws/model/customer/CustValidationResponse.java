package com.pennanttech.ws.model.customer;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "customerPhoneNumber", "cif", "returnStatus" })
public class CustValidationResponse {

	@XmlElement(name = "phone")
	private List<CustomerPhoneNumber> customerPhoneNumber = null;
	@XmlElement
	private String cif;

	@XmlElement
	private WSReturnStatus returnStatus;

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<CustomerPhoneNumber> getCustomerPhoneNumber() {
		return customerPhoneNumber;
	}

	public void setCustomerPhoneNumber(List<CustomerPhoneNumber> customerPhoneNumber) {
		this.customerPhoneNumber = customerPhoneNumber;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}
}
