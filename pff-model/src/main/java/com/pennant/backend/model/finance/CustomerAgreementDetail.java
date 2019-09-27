package com.pennant.backend.model.finance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerEMail;

public class CustomerAgreementDetail {

	private String custShrtName;
	private String custCRCPR;
	private CustomerAddres custPermanentAddres;
	private CustomerAddres custCurrentAddres;
	private String custCIF;
	private CustomerEMail customerEMail;
	private String customerPhoneNumber;
	private Map<String, Object> extendedFields = new HashMap<>();
	private Date appDate;
	private String dob;
	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}

	public CustomerAddres getCustPermanentAddres() {
		return custPermanentAddres;
	}

	public void setCustPermanentAddres(CustomerAddres custPermanentAddres) {
		this.custPermanentAddres = custPermanentAddres;
	}

	public CustomerAddres getCustCurrentAddres() {
		return custCurrentAddres;
	}

	public void setCustCurrentAddres(CustomerAddres custCurrentAddres) {
		this.custCurrentAddres = custCurrentAddres;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public CustomerEMail getCustomerEMail() {
		return customerEMail;
	}

	public void setCustomerEMail(CustomerEMail customerEMail) {
		this.customerEMail = customerEMail;
	}

	public Map<String, Object> getExtendedFields() {
		return extendedFields;
	}

	public void setExtendedFields(Map<String, Object> extendedFields) {
		this.extendedFields = extendedFields;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getCustomerPhoneNumber() {
		return customerPhoneNumber;
	}

	public void setCustomerPhoneNumber(String customerPhoneNumber) {
		this.customerPhoneNumber = customerPhoneNumber;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

}
