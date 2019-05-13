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

package com.pennant.interfaceservice.model;

import java.sql.Timestamp;

/**
 * Model class for the <b>CustomerEMail table</b>.<br>
 * 
 */
public class InterfaceCustomerEMail {

	private long custID = Long.MIN_VALUE;
	private String custEMailTypeCode;
	private String lovDescCustEMailTypeCode;
	private int custEMailPriority;
	private String custEMail;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private String lovValue;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;

	public InterfaceCustomerEMail() {

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustEMailTypeCode() {
		return custEMailTypeCode;
	}

	public void setCustEMailTypeCode(String custEMailTypeCode) {
		this.custEMailTypeCode = custEMailTypeCode;
	}

	public String getLovDescCustEMailTypeCode() {
		return lovDescCustEMailTypeCode;
	}

	public void setLovDescCustEMailTypeCode(String lovDescCustEMailTypeCode) {
		this.lovDescCustEMailTypeCode = lovDescCustEMailTypeCode;
	}

	public int getCustEMailPriority() {
		return custEMailPriority;
	}

	public void setCustEMailPriority(int custEMailPriority) {
		this.custEMailPriority = custEMailPriority;
	}

	public String getCustEMail() {
		return custEMail;
	}

	public void setCustEMail(String custEMail) {
		this.custEMail = custEMail;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
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

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}
}
