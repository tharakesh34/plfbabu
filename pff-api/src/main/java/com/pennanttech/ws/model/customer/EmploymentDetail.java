package com.pennanttech.ws.model.customer;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "cif", "employment", "employementId", "returnStatus" })

public class EmploymentDetail {
	@XmlElement
	private String cif;
	@XmlElement
	private CustomerEmploymentDetail employment;
	@XmlElement
	private long employementId;
	@XmlElement
	private WSReturnStatus returnStatus;

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public CustomerEmploymentDetail getCustomerEmploymentDetail() {
		return employment;
	}

	public void setCustomerEmploymentDetail(CustomerEmploymentDetail customerEmploymentDetail) {
		this.employment = customerEmploymentDetail;
	}

	public long getEmployementId() {
		return employementId;
	}

	public void setEmployementId(long employementId) {
		this.employementId = employementId;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}
