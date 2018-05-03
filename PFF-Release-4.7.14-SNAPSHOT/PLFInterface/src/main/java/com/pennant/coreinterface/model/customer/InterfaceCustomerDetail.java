/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */
package com.pennant.coreinterface.model.customer;

import java.io.Serializable;
import java.util.List;

/**
 * Model class for the <b>Customer table</b>.<br>
 * 
 */
public class InterfaceCustomerDetail implements Serializable {

	private static final long serialVersionUID = 5924309510247559865L;
	
	private String custCIF;
	private InterfaceCustomer customer;
	private InterfaceCustEmployeeDetail custEmployeeDetail;
	private List<InterfaceCustomerRating> ratingsList;
	private List<InterfaceCustomerEmploymentDetail> employmentDetailsList;
	private List<InterfaceCustomerDocument> customerDocumentsList;
	private List<InterfaceCustomerAddress> addressList;
	private List<InterfaceCustomerPhoneNumber> customerPhoneNumList;
	private List<InterfaceCustomerEMail> customerEMailList;
	private List<InterfaceCustomerIdentity> customerIdentityList;
	private List<InterfaceShareHolder> shareHolderList;
	private InterfaceCoreCustomer interfaceCoreCustomer;

	public InterfaceCustomerDetail() {
		
	}
	
	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public InterfaceCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(InterfaceCustomer customer) {
		this.customer = customer;
	}

	public InterfaceCustEmployeeDetail getCustEmployeeDetail() {
		return custEmployeeDetail;
	}

	public void setCustEmployeeDetail(InterfaceCustEmployeeDetail custEmployeeDetail) {
		this.custEmployeeDetail = custEmployeeDetail;
	}

	public List<InterfaceCustomerRating> getRatingsList() {
		return ratingsList;
	}

	public void setRatingsList(List<InterfaceCustomerRating> ratingsList) {
		this.ratingsList = ratingsList;
	}

	public List<InterfaceCustomerEmploymentDetail> getEmploymentDetailsList() {
		return employmentDetailsList;
	}

	public void setEmploymentDetailsList(List<InterfaceCustomerEmploymentDetail> employmentDetailsList) {
		this.employmentDetailsList = employmentDetailsList;
	}

	public List<InterfaceCustomerDocument> getCustomerDocumentsList() {
		return customerDocumentsList;
	}

	public void setCustomerDocumentsList(
			List<InterfaceCustomerDocument> customerDocumentsList) {
		this.customerDocumentsList = customerDocumentsList;
	}

	public List<InterfaceCustomerAddress> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<InterfaceCustomerAddress> addressList) {
		this.addressList = addressList;
	}

	public List<InterfaceCustomerPhoneNumber> getCustomerPhoneNumList() {
		return customerPhoneNumList;
	}

	public void setCustomerPhoneNumList(List<InterfaceCustomerPhoneNumber> customerPhoneNumList) {
		this.customerPhoneNumList = customerPhoneNumList;
	}

	public List<InterfaceCustomerEMail> getCustomerEMailList() {
		return customerEMailList;
	}

	public void setCustomerEMailList(List<InterfaceCustomerEMail> customerEMailList) {
		this.customerEMailList = customerEMailList;
	}

	public List<InterfaceCustomerIdentity> getCustomerIdentityList() {
		return customerIdentityList;
	}

	public void setCustomerIdentityList(
			List<InterfaceCustomerIdentity> customerIdentityList) {
		this.customerIdentityList = customerIdentityList;
	}

	public List<InterfaceShareHolder> getShareHolderList() {
		return shareHolderList;
	}

	public void setShareHolderList(List<InterfaceShareHolder> shareHolderList) {
		this.shareHolderList = shareHolderList;
	}
	
	public InterfaceCoreCustomer getInterfaceCoreCustomer() {
		return interfaceCoreCustomer;
	}

	public void setInterfaceCoreCustomer(InterfaceCoreCustomer interfaceCoreCustomer) {
		this.interfaceCoreCustomer = interfaceCoreCustomer;
	}
	
}
