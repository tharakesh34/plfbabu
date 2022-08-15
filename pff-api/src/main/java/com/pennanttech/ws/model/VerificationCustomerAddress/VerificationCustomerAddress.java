/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : VerificationCustomerAddress.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-02-2021 * *
 * Modified Date : 08-02-2021 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-02-2021 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennanttech.ws.model.VerificationCustomerAddress;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerAddres;

@XmlType(propOrder = { "primaryAddress", "coApplicants", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "customerAddress")
public class VerificationCustomerAddress {
	@XmlElementWrapper(name = "primaryAddress")
	@XmlElement(name = "primaryAddress")
	private List<CustomerAddres> addressList;

	@XmlElementWrapper(name = "coApplicants")
	@XmlElement(name = "coApplicant")
	private List<CustomerAddres> coApplicants;

	@XmlElement
	private WSReturnStatus returnStatus;

	public void setAddressList(List<CustomerAddres> addressList) {
		this.addressList = addressList;
	}

	public List<CustomerAddres> getAddressList() {
		return addressList;
	}

	public List<CustomerAddres> getCoApplicants() {
		return coApplicants;
	}

	public void setCoApplicants(List<CustomerAddres> coApplicants) {
		this.coApplicants = coApplicants;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
