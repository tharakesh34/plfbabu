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
 *
 * FileName : PrimaryAccount.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 02-08-2019 *
 * 
 * Modified Date : 02-08-2019 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-08-2019 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model;

import java.sql.Timestamp;
import java.util.Date;

public class PrimaryAccount {

	private String panNumber;
	private String custFName;
	private String custMName;
	private String custLName;
	private long id;
	private String type;
	private String documentName;
	private String documentNumber;
	private String issuedCountry;
	private String issuedAuthority;
	private Date issuedOn;
	private Date expiryDate;
	private Timestamp verifiedOn;
	private boolean verified;
	private String remarks;

	private String accountNumber;

	public PrimaryAccount() {
		super();
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getIssuedCountry() {
		return issuedCountry;
	}

	public void setIssuedCountry(String issuedCountry) {
		this.issuedCountry = issuedCountry;
	}

	public String getIssuedAuthority() {
		return issuedAuthority;
	}

	public void setIssuedAuthority(String issuedAuthority) {
		this.issuedAuthority = issuedAuthority;
	}

	public Date getIssuedOn() {
		return issuedOn;
	}

	public void setIssuedOn(Date issuedOn) {
		this.issuedOn = issuedOn;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getCustFName() {
		return custFName;
	}

	public void setCustFName(String custFName) {
		this.custFName = custFName;
	}

	public String getCustMName() {
		return custMName;
	}

	public void setCustMName(String custMName) {
		this.custMName = custMName;
	}

	public String getCustLName() {
		return custLName;
	}

	public void setCustLName(String custLName) {
		this.custLName = custLName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Timestamp getVerifiedOn() {
		return verifiedOn;
	}

	public void setVerifiedOn(Timestamp verifiedOn) {
		this.verifiedOn = verifiedOn;
	}

}
