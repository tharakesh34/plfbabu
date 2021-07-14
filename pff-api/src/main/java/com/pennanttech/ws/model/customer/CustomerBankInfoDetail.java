package com.pennanttech.ws.model.customer;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerBankInfo;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "cif", "customerBankInfo", "bankId", "returnStatus" })

public class CustomerBankInfoDetail {
	@XmlElement
	private String cif;
	@XmlElement
	private CustomerBankInfo customerBankInfo;
	@XmlElement
	private long bankId = Long.MIN_VALUE;
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

	public CustomerBankInfo getCustomerBankInfo() {
		return customerBankInfo;
	}

	public void setCustomerBankInfo(CustomerBankInfo customerBankInfo) {
		this.customerBankInfo = customerBankInfo;
	}

	public long getBankId() {
		return bankId;
	}

	public void setBankId(long bankId) {
		this.bankId = bankId;
	}

}
