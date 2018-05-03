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

package com.pennanttech.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;

/**
 * Model class for the <b>Customer table</b>.<br>
 * 
 */
@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.NONE)
public class DedupCustomerDetail implements java.io.Serializable {
	private static final long			serialVersionUID	= 1L;

	private long						custID;
	private boolean						newRecord			= false;

	@XmlElement(name = "cif")
	private String						custCIF;

	private String						sourceId;
	
	private String finReference;
	private String finType;
	private String applicationNo;
	private String accountNumber;
	
	@XmlElement(name = "personalInfo")
	private Customer					customer;

	@XmlElementWrapper(name = "documents")
	@XmlElement(name = "document")
	private List<CustomerDocument>		customerDocumentsList;

	@XmlElementWrapper(name = "addresses")
	@XmlElement(name = "address")
	private List<CustomerAddres>		addressList;

	@XmlElementWrapper(name = "phones")
	@XmlElement(name = "phone")
	private List<CustomerPhoneNumber>	customerPhoneNumList;

	@XmlElementWrapper(name = "emails")
	@XmlElement(name = "email")
	private List<CustomerEMail>			customerEMailList;

	@XmlElement
	private WSReturnStatus				returnStatus		= null;

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<CustomerDocument> getCustomerDocumentsList() {
		return customerDocumentsList;
	}

	public void setCustomerDocumentsList(List<CustomerDocument> customerDocumentsList) {
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

	public void setCustomerPhoneNumList(List<CustomerPhoneNumber> customerPhoneNumList) {
		this.customerPhoneNumList = customerPhoneNumList;
	}

	public List<CustomerEMail> getCustomerEMailList() {
		return customerEMailList;
	}

	public void setCustomerEMailList(List<CustomerEMail> customerEMailList) {
		this.customerEMailList = customerEMailList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@Override
	public String toString() {
		return "DedupCustomerDetail [custID=" + custID + ", newRecord="
				+ newRecord + ", custCIF=" + custCIF + ", sourceId=" + sourceId
				+ ", finReference=" + finReference + ", finType=" + finType
				+ ", applicationNo=" + applicationNo + ", accountNumber="
				+ accountNumber + ", customer=" + customer
				+ ", customerDocumentsList=" + customerDocumentsList
				+ ", addressList=" + addressList + ", customerPhoneNumList="
				+ customerPhoneNumList + ", customerEMailList="
				+ customerEMailList + ", returnStatus=" + returnStatus + "]";
	}

	
}
