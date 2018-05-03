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
import java.sql.Timestamp;
import java.util.Date;

/**
 * Model class for the <b>CustomerDocument table</b>.<br>
 * 
 */
public class InterfaceCustomerDocument implements Serializable {

	private static final long serialVersionUID = 6313156284826291908L;
	
	private long custID = Long.MIN_VALUE;
	private String lovDescCustShrtName;
	private String custDocType;
	private String custDocName;
	private String custDocCategory;
	private String lovDescCustDocCategory;
	private String custDocTitle;
	private String custDocSysName;
	private Timestamp custDocRcvdOn;
	private Date custDocExpDate;
	private Date custDocIssuedOn;
	private String custDocIssuedCountry;
	private String lovDescCustDocIssuedCountry;
	private boolean custDocIsVerified;
	private long custDocVerifiedBy;
	private boolean custDocIsAcrive;
	private byte[] custDocImage;
	private String lovValue;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	private String lovDescCustDocVerifiedBy;

	public InterfaceCustomerDocument() {

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

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getCustDocType() {
		return custDocType;
	}

	public void setCustDocType(String custDocType) {
		this.custDocType = custDocType;
	}

	public String getCustDocName() {
		return custDocName;
	}

	public void setCustDocName(String custDocName) {
		this.custDocName = custDocName;
	}

	public String getCustDocCategory() {
		return custDocCategory;
	}

	public void setCustDocCategory(String custDocCategory) {
		this.custDocCategory = custDocCategory;
	}

	public String getLovDescCustDocCategory() {
		return lovDescCustDocCategory;
	}

	public void setLovDescCustDocCategory(String lovDescCustDocCategory) {
		this.lovDescCustDocCategory = lovDescCustDocCategory;
	}

	public String getCustDocTitle() {
		return custDocTitle;
	}

	public void setCustDocTitle(String custDocTitle) {
		this.custDocTitle = custDocTitle;
	}

	public String getCustDocSysName() {
		return custDocSysName;
	}

	public void setCustDocSysName(String custDocSysName) {
		this.custDocSysName = custDocSysName;
	}

	public Timestamp getCustDocRcvdOn() {
		return custDocRcvdOn;
	}

	public void setCustDocRcvdOn(Timestamp custDocRcvdOn) {
		this.custDocRcvdOn = custDocRcvdOn;
	}

	public Date getCustDocExpDate() {
		return custDocExpDate;
	}

	public void setCustDocExpDate(Date custDocExpDate) {
		this.custDocExpDate = custDocExpDate;
	}

	public Date getCustDocIssuedOn() {
		return custDocIssuedOn;
	}

	public void setCustDocIssuedOn(Date custDocIssuedOn) {
		this.custDocIssuedOn = custDocIssuedOn;
	}

	public String getCustDocIssuedCountry() {
		return custDocIssuedCountry;
	}

	public void setCustDocIssuedCountry(String custDocIssuedCountry) {
		this.custDocIssuedCountry = custDocIssuedCountry;
	}

	public String getLovDescCustDocIssuedCountry() {
		return lovDescCustDocIssuedCountry;
	}

	public void setLovDescCustDocIssuedCountry(
			String lovDescCustDocIssuedCountry) {
		this.lovDescCustDocIssuedCountry = lovDescCustDocIssuedCountry;
	}

	public boolean isCustDocIsVerified() {
		return custDocIsVerified;
	}

	public void setCustDocIsVerified(boolean custDocIsVerified) {
		this.custDocIsVerified = custDocIsVerified;
	}

	public long getCustDocVerifiedBy() {
		return custDocVerifiedBy;
	}

	public void setCustDocVerifiedBy(long custDocVerifiedBy) {
		this.custDocVerifiedBy = custDocVerifiedBy;
	}

	public boolean isCustDocIsAcrive() {
		return custDocIsAcrive;
	}

	public void setCustDocIsAcrive(boolean custDocIsAcrive) {
		this.custDocIsAcrive = custDocIsAcrive;
	}

	public byte[] getCustDocImage() {
		return custDocImage;
	}

	public void setCustDocImage(byte[] custDocImage) {
		this.custDocImage = custDocImage;
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

	public String getLovDescCustDocVerifiedBy() {
		return lovDescCustDocVerifiedBy;
	}

	public void setLovDescCustDocVerifiedBy(String lovDescCustDocVerifiedBy) {
		this.lovDescCustDocVerifiedBy = lovDescCustDocVerifiedBy;
	}

}
