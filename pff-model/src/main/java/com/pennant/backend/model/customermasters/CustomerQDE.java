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
 * * FileName : CustomerQDE.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-07-2011 * * Modified Date :
 * 29-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.util.Date;

/**
 * Model class for the <b>Customer QDE</b>.<br>
 *
 */
public class CustomerQDE implements Serializable {
	private static final long serialVersionUID = -7955038659330328410L;

	private String custCIF;
	private String custCoreBank;
	private String custCtgCode;
	private String lovDescCustCtgCodeName;
	private String custTypeCode;
	private String lovDescCustTypeCodeName;
	private String custSalutationCode;
	private String lovDescCustSalutationCodeName;
	private String custFName;
	private String custMName;
	private String custLName;
	private String custShrtName = "";
	private Date custDOB;
	private String custPassportNo = "";
	private String custTradeLicenceNum;
	private String custVisaNum;
	private String custParentCountry;
	private String lovDescCustParentCountryName;

	/*
	 * Default Constructor
	 */
	public CustomerQDE() {
		super();
	}

	public CustomerQDE(String custCIF, String custCoreBank, String custCtgCode, String lovDescCustCtgCodeName,
			String custTypeCode, String lovDescCustTypeCodeName, String custSalutationCode,
			String lovDescCustSalutationCodeName, String custFName, String custMName, String custLName,
			String custShrtName, Date custDOB, String custPassportNo, String custTradeLicenceNum, String custVisaNum,
			String custParentCountry, String lovDescCustParentCountryName) {

		this.setCustCIF(custCIF);
		this.setCustCoreBank(custCoreBank);
		this.setCustCtgCode(custCtgCode);
		this.setLovDescCustCtgCodeName(lovDescCustCtgCodeName);
		this.setCustTypeCode(custTypeCode);
		this.setLovDescCustTypeCodeName(lovDescCustTypeCodeName);
		this.setCustSalutationCode(custSalutationCode);
		this.setLovDescCustSalutationCodeName(lovDescCustSalutationCodeName);
		this.setCustFName(custFName);
		this.setCustMName(custMName);
		this.setCustLName(custLName);
		this.setCustShrtName(custShrtName);
		this.setCustDOB(custDOB);
		this.setCustPassportNo(custPassportNo);
		this.setCustTradeLicenceNum(custTradeLicenceNum);
		this.setCustVisaNum(custVisaNum);
		this.setCustParentCountry(custParentCountry);
		this.setLovDescCustParentCountryName(lovDescCustParentCountryName);
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public String getLovDescCustCtgCodeName() {
		return lovDescCustCtgCodeName;
	}

	public void setLovDescCustCtgCodeName(String lovDescCustCtgCodeName) {
		this.lovDescCustCtgCodeName = lovDescCustCtgCodeName;
	}

	public String getCustTypeCode() {
		return custTypeCode;
	}

	public void setCustTypeCode(String custTypeCode) {
		this.custTypeCode = custTypeCode;
	}

	public String getLovDescCustTypeCodeName() {
		return lovDescCustTypeCodeName;
	}

	public void setLovDescCustTypeCodeName(String lovDescCustTypeCodeName) {
		this.lovDescCustTypeCodeName = lovDescCustTypeCodeName;
	}

	public String getCustSalutationCode() {
		return custSalutationCode;
	}

	public void setCustSalutationCode(String custSalutationCode) {
		this.custSalutationCode = custSalutationCode;
	}

	public String getLovDescCustSalutationCodeName() {
		return lovDescCustSalutationCodeName;
	}

	public void setLovDescCustSalutationCodeName(String lovDescCustSalutationCodeName) {
		this.lovDescCustSalutationCodeName = lovDescCustSalutationCodeName;
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

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public Date getCustDOB() {
		return custDOB;
	}

	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public String getCustPassportNo() {
		return custPassportNo;
	}

	public void setCustPassportNo(String custPassportNo) {
		this.custPassportNo = custPassportNo;
	}

	public String getCustTradeLicenceNum() {
		return custTradeLicenceNum;
	}

	public void setCustTradeLicenceNum(String custTradeLicenceNum) {
		this.custTradeLicenceNum = custTradeLicenceNum;
	}

	public String getCustVisaNum() {
		return custVisaNum;
	}

	public void setCustVisaNum(String custVisaNum) {
		this.custVisaNum = custVisaNum;
	}

	public void setCustParentCountry(String custParentCountry) {
		this.custParentCountry = custParentCountry;
	}

	public String getCustParentCountry() {
		return custParentCountry;
	}

	public void setLovDescCustParentCountryName(String lovDescCustParentCountryName) {
		this.lovDescCustParentCountryName = lovDescCustParentCountryName;
	}

	public String getLovDescCustParentCountryName() {
		return lovDescCustParentCountryName;
	}
}
