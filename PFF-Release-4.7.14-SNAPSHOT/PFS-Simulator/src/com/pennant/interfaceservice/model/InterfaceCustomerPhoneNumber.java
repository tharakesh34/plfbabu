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

package com.pennant.interfaceservice.model;


/**
 * Model class for the <b>CustomerPhoneNumber table</b>.<br>
 * 
 */
public class InterfaceCustomerPhoneNumber {

	private long phoneCustID = Long.MIN_VALUE;
	private String lovDescCustShrtName;
	private String phoneTypeCode;
	private String lovDescPhoneTypeCodeName;
	private String phoneCountryCode;
	private String lovDescPhoneCountryName;
	private String phoneAreaCode;
	private String phoneNumber;
	private String lovValue;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;

	public InterfaceCustomerPhoneNumber() {

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public long getPhoneCustID() {
		return phoneCustID;
	}

	public void setPhoneCustID(long phoneCustID) {
		this.phoneCustID = phoneCustID;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getPhoneTypeCode() {
		return phoneTypeCode;
	}

	public void setPhoneTypeCode(String phoneTypeCode) {
		this.phoneTypeCode = phoneTypeCode;
	}

	public String getLovDescPhoneTypeCodeName() {
		return lovDescPhoneTypeCodeName;
	}

	public void setLovDescPhoneTypeCodeName(String lovDescPhoneTypeCodeName) {
		this.lovDescPhoneTypeCodeName = lovDescPhoneTypeCodeName;
	}

	public String getPhoneCountryCode() {
		return phoneCountryCode;
	}

	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}

	public String getLovDescPhoneCountryName() {
		return lovDescPhoneCountryName;
	}

	public void setLovDescPhoneCountryName(String lovDescPhoneCountryName) {
		this.lovDescPhoneCountryName = lovDescPhoneCountryName;
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

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public String getLovDescCustRecordType() {
		return lovDescCustRecordType;
	}

	public void setLovDescCustRecordType(String lovDescCustRecordType) {
		this.lovDescCustRecordType = lovDescCustRecordType;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}
}
