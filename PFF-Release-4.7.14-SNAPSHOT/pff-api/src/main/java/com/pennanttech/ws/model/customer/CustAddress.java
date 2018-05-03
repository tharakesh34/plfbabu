package com.pennanttech.ws.model.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerAddres;
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"cif","customerAddres","addrType", "returnStatus" })
public class CustAddress {
	
	@XmlElement
	private String cif;
	
	@XmlElement(name="address")
	private CustomerAddres customerAddres;
	
	@XmlElement
	private String addrType ;
	
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
	public String getAddrType() {
		return addrType;
	}
	public void setAddrType(String addrType) {
		this.addrType = addrType;
	}
	public CustomerAddres getCustomerAddres() {
		return customerAddres;
	}
	public void setCustomerAddres(CustomerAddres customerAddres) {
		this.customerAddres = customerAddres;
	}
}
