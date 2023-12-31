package com.pennanttech.ws.model.customer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "cif", "customerName", "blocklimit", "expiryDate", "actualLimit", "customerPhoneNumber",
		"returnStatus" })
public class CustValidationResponse {

	@XmlElement(name = "phone")
	private List<CustomerPhoneNumber> customerPhoneNumber = null;
	@XmlElement
	private String cif;

	@XmlElement
	private String customerName;

	@XmlElement
	private boolean blocklimit;

	@XmlElement
	private WSReturnStatus returnStatus;

	@XmlElement
	private Date expiryDate;

	@XmlElement(name = "availableLimit")
	private BigDecimal actualLimit = BigDecimal.ZERO;

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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public boolean isBlocklimit() {
		return blocklimit;
	}

	public void setBlocklimit(boolean blocklimit) {
		this.blocklimit = blocklimit;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public BigDecimal getActualLimit() {
		return actualLimit;
	}

	public void setActualLimit(BigDecimal actualLimit) {
		this.actualLimit = actualLimit;
	}
}
