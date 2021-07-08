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
 * FileName    		:  VerificationDetails.java                                     * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-02-2021    														*
 *                                                                  						*
 * Modified Date    :  08-02-2021    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-02-2021       PENNANT	                 0.1                                            * 
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
package com.pennanttech.ws.model.VerificationCustomerAddress;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.pff.verification.model.Verification;

@XmlType(propOrder = { "VerificationDetailsList", "primaryCustomerAddress", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "customerAddress")
public class VerificationDetails {
	@XmlElementWrapper(name = "VerificationDetailList")
	@XmlElement(name = "VerificationDetailsList")
	private List<Verification> VerificationDetailsList;
	@XmlElementWrapper(name = "customerAddressDetail")
	@XmlElement(name = "customerAddressDetails")
	private List<CustomerAddres> primaryCustomerAddress;

	@XmlElementWrapper(name = "coapplicantsAddress")
	@XmlElement(name = "coapplicantsAddresses")
	private List<CustomerAddres> coApplicantsAddress;
	@XmlElementWrapper(name = "customerDocumentList")
	@XmlElement(name = "customerDocumentsList")
	private List<CustomerDocument> customerDocumentsList;
	@XmlElementWrapper(name = "loanDocumentList")
	@XmlElement(name = "loanDocumentsList")
	private List<DocumentDetails> loanDocumentsList;
	@XmlElementWrapper(name = "collateralsDocumentList")
	@XmlElement(name = "collateralsDocumentsList")
	private List<DocumentDetails> collateralsDocumentsList;
	@XmlElementWrapper(name = "coApptDocumentList")
	@XmlElement(name = "coApptDocumentsList")
	private List<CustomerDocument> coApptDocumentsList;
	@XmlElement
	private WSReturnStatus returnStatus;

	public List<Verification> getVerificationDetailsList() {
		return VerificationDetailsList;
	}

	public void setVerificationDetailsList(List<Verification> verificationDetailsList) {
		VerificationDetailsList = verificationDetailsList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<CustomerAddres> getCoApplicantsAddress() {
		return coApplicantsAddress;
	}

	public void setCoApplicantsAddress(List<CustomerAddres> coApplicantsAddress) {
		this.coApplicantsAddress = coApplicantsAddress;
	}

	public List<CustomerAddres> getPrimaryCustomerAddress() {
		return primaryCustomerAddress;
	}

	public void setPrimaryCustomerAddress(List<CustomerAddres> primaryCustomerAddress) {
		this.primaryCustomerAddress = primaryCustomerAddress;
	}

	public List<CustomerDocument> getCoApptDocumentsList() {
		return coApptDocumentsList;
	}

	public void setCoApptDocumentsList(List<CustomerDocument> coApptDocumentsList) {
		this.coApptDocumentsList = coApptDocumentsList;
	}

	public List<CustomerDocument> getCustomerDocumentsList() {
		return customerDocumentsList;
	}

	public void setCustomerDocumentsList(List<CustomerDocument> customerDocumentsList) {
		this.customerDocumentsList = customerDocumentsList;
	}

	public List<DocumentDetails> getLoanDocumentsList() {
		return loanDocumentsList;
	}

	public void setLoanDocumentsList(List<DocumentDetails> loanDocumentsList) {
		this.loanDocumentsList = loanDocumentsList;
	}

	public List<DocumentDetails> getCollateralsDocumentsList() {
		return collateralsDocumentsList;
	}

	public void setCollateralsDocumentsList(List<DocumentDetails> collateralsDocumentsList) {
		this.collateralsDocumentsList = collateralsDocumentsList;
	}

}
