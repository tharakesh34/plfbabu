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

/**
 * Model class for the <b>CustomerEMail table</b>.<br>
 * 
 */
public class InterfaceCustomerEMail implements Serializable {

	private static final long serialVersionUID = 2510508100335229581L;

	private long custID = Long.MIN_VALUE;
	private String custEMailTypeCode;
	private int custEMailPriority;
	private String custEMail;
	private String recordType;
	private String lovDescCustCIF;
	private String lovDescCustEMailTypeCode;

	public InterfaceCustomerEMail() {

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public String getLovDescCustEMailTypeCode() {
		return lovDescCustEMailTypeCode;
	}

	public void setLovDescCustEMailTypeCode(String lovDescCustEMailTypeCode) {
		this.lovDescCustEMailTypeCode = lovDescCustEMailTypeCode;
	}

}
