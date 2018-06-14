package com.pennanttech.niyogin.holdfinance.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "loanReference", "cif", "customerName", "holdCategory", "holdReasons", "remarks" })
@XmlRootElement(name = "holdfinancerequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class HoldFinanceRequest {
	private String				loanReference;
	private String				cif;
	private String				customerName;
	private String				holdCategory;
	private List<HoldReason>	holdReasons;
	private String				remarks;

	public String getLoanReference() {
		return loanReference;
	}

	public void setLoanReference(String loanReference) {
		this.loanReference = loanReference;
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

	public String getHoldCategory() {
		return holdCategory;
	}

	public void setHoldCategory(String holdCategory) {
		this.holdCategory = holdCategory;
	}

	public List<HoldReason> getHoldReasons() {
		return holdReasons;
	}

	public void setHoldReasons(List<HoldReason> holdReasons) {
		this.holdReasons = holdReasons;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
