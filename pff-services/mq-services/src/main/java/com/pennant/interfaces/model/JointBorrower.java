package com.pennant.interfaces.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "JointBorrower")
public class JointBorrower {

	private String customerNo;
	private String customerName;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	@XmlElement(name = "CustomerNo")
	public String getCustomerNo() {
		return this.customerNo;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@XmlElement(name = "CustomerName")
	public String getCustomerName() {
		return this.customerName;
	}

}
