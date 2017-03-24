/**
\ * Copyright 2011 - Pennant Technologies
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

/**
 * Model class for the <b>CustomerPhoneNumber table</b>.<br>
 * 
 */
public class InterfaceCustomerPhoneNumber implements Serializable {

	private static final long serialVersionUID = 446666398394441656L;
	
	private long phoneCustID = Long.MIN_VALUE;
	private String phoneTypeCode;
	private String phoneCountryCode;
	private String phoneAreaCode;
	private String phoneNumber;
	private String recordType;
	private String lovDescCustCIF;

	public InterfaceCustomerPhoneNumber() {

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getPhoneCustID() {
		return phoneCustID;
	}

	public void setPhoneCustID(long phoneCustID) {
		this.phoneCustID = phoneCustID;
	}

	public String getPhoneTypeCode() {
		return phoneTypeCode;
	}

	public void setPhoneTypeCode(String phoneTypeCode) {
		this.phoneTypeCode = phoneTypeCode;
	}

	public String getPhoneCountryCode() {
		return phoneCountryCode;
	}

	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}

	public String getPhoneAreaCode() {
		return phoneAreaCode;
	}

	public void setPhoneAreaCode(String phoneAreaCode) {
		this.phoneAreaCode = phoneAreaCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

}
