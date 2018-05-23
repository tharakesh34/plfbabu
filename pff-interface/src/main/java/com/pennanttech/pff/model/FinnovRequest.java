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
 *
 * FileName    		:  FinnovCibilEnquiryProcess.java										*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  23-05-2018															*
 *                                                                  
 * Modified Date    :  23-05-2018															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2018       Pennant	                 1.0          Created as part of Finnov 
 * 														  Profectus integration			    * 
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
package com.pennanttech.pff.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "enquiryAmount", "panCardNo", "gender", "dateOfBirth", "name", "currentAddress" })
@XmlRootElement(name = "Finnov")
@XmlAccessorType(XmlAccessType.FIELD)
public class FinnovRequest {
	
	@XmlElement(name = "enquiryAmount")
	private int enquiryAmount;

	@XmlElement(name = "panCardNo")
	private String panCardNo;

	@XmlElement(name = "name")
	private String name;
	
	@XmlElement(name = "gender")
	private String gender;
	
	@XmlElement(name = "dateOfBirth")
	private String dateOfBirth;

	@XmlElement(name = "currentAddress")
	private AddressDetails currentAddress;

	public int getEnquiryAmount() {
		return enquiryAmount;
	}

	public void setEnquiryAmount(int enquiryAmount) {
		this.enquiryAmount = enquiryAmount;
	}

	public String getPanCardNo() {
		return panCardNo;
	}

	public void setPanCardNo(String panCardNo) {
		this.panCardNo = panCardNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public AddressDetails getCurrentAddress() {
		return currentAddress;
	}

	public void setCurrentAddress(AddressDetails currentAddress) {
		this.currentAddress = currentAddress;
	}
}
