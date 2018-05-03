package com.pennanttech.ws.model.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"cif","customerPhoneNumber","phoneTypeCode", "returnStatus" })
public class CustPhoneNumber {
	
	@XmlElement
	private String cif;
	
	@XmlElement(name = "phone")
	private CustomerPhoneNumber customerPhoneNumber;
	
	@XmlElement
	private String phoneTypeCode;
	
	@XmlElement
	private WSReturnStatus returnStatus;
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
	}
	public CustomerPhoneNumber getCustomerPhoneNumber() {
		return customerPhoneNumber;
	}
	public void setCustomerPhoneNumber(CustomerPhoneNumber customerPhoneNumber) {
		this.customerPhoneNumber = customerPhoneNumber;
	}
	public String getPhoneTypeCode() {
		return phoneTypeCode;
	}
	public void setPhoneTypeCode(String phoneTypeCode) {
		this.phoneTypeCode = phoneTypeCode;
	}
	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}
	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}
