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

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CustomerDetails.java                                                 * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.interfaces.model;

import java.util.List;

/**
 * Model class for the <b>Customer table</b>.<br>
 * 
 */
public class CustomerDetails implements java.io.Serializable {

	private static final long serialVersionUID = 6260708266071405881L;

	private String custCIF;
	private Customer customer;
	private CustEmployeeDetail custEmployeeDetail;
	private List<CustomerRating> ratingsList;
	private List<CustomerEmploymentDetail> employmentDetailsList;
	private List<CustomerDocument> customerDocumentsList;
	private List<CustomerAddres> addressList;
	private List<CustomerPhoneNumber> customerPhoneNumList;
	private List<CustomerEMail> customerEMailList;
	private CoreCustomer coreCustomer;
	private String coreReferenceNum;

	public CustomerDetails() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CustEmployeeDetail getCustEmployeeDetail() {
		return custEmployeeDetail;
	}

	public void setCustEmployeeDetail(CustEmployeeDetail custEmployeeDetail) {
		this.custEmployeeDetail = custEmployeeDetail;
	}

	public List<CustomerRating> getRatingsList() {
		return ratingsList;
	}

	public void setRatingsList(List<CustomerRating> ratingsList) {
		this.ratingsList = ratingsList;
	}

	public List<CustomerEmploymentDetail> getEmploymentDetailsList() {
		return employmentDetailsList;
	}

	public void setEmploymentDetailsList(
			List<CustomerEmploymentDetail> employmentDetailsList) {
		this.employmentDetailsList = employmentDetailsList;
	}

	public List<CustomerDocument> getCustomerDocumentsList() {
		return customerDocumentsList;
	}

	public void setCustomerDocumentsList(
			List<CustomerDocument> customerDocumentsList) {
		this.customerDocumentsList = customerDocumentsList;
	}

	public List<CustomerAddres> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<CustomerAddres> addressList) {
		this.addressList = addressList;
	}

	public List<CustomerPhoneNumber> getCustomerPhoneNumList() {
		return customerPhoneNumList;
	}

	public void setCustomerPhoneNumList(
			List<CustomerPhoneNumber> customerPhoneNumList) {
		this.customerPhoneNumList = customerPhoneNumList;
	}

	public List<CustomerEMail> getCustomerEMailList() {
		return customerEMailList;
	}

	public void setCustomerEMailList(List<CustomerEMail> customerEMailList) {
		this.customerEMailList = customerEMailList;
	}

	public String getCoreReferenceNum() {
		return coreReferenceNum;
	}

	public void setCoreReferenceNum(String coreReferenceNum) {
		this.coreReferenceNum = coreReferenceNum;
	}

	public CoreCustomer getCoreCustomer() {
		return coreCustomer;
	}

	public void setCoreCustomer(CoreCustomer coreCustomer) {
		this.coreCustomer = coreCustomer;
	}

}
