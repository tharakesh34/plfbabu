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
 * Model class for the <b>CustomerAddres table</b>.<br>
 * 
 */
public class InterfaceCustomerAddress {

	private long custID = Long.MIN_VALUE;
	private String lovDescCustShrtName;
	private String custAddrType;
	private String lovDescCustAddrTypeName;
	private String custAddrHNbr;
	private String custFlatNbr;
	private String custAddrStreet;
	private String custAddrLine1;
	private String custAddrLine2;
	private String custPOBox;
	private String custAddrCountry;
	private String lovDescCustAddrCountryName;
	private String custAddrProvince;
	private String lovDescCustAddrProvinceName;
	private String custAddrCity;
	private String lovDescCustAddrCityName;
	private String custAddrZIP;
	private String custAddrPhone;
	private Timestamp custAddrFrom;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;

	public InterfaceCustomerAddress() {

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

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getCustAddrType() {
		return custAddrType;
	}

	public void setCustAddrType(String custAddrType) {
		this.custAddrType = custAddrType;
	}

	public String getLovDescCustAddrTypeName() {
		return lovDescCustAddrTypeName;
	}

	public void setLovDescCustAddrTypeName(String lovDescCustAddrTypeName) {
		this.lovDescCustAddrTypeName = lovDescCustAddrTypeName;
	}

	public String getCustAddrHNbr() {
		return custAddrHNbr;
	}

	public void setCustAddrHNbr(String custAddrHNbr) {
		this.custAddrHNbr = custAddrHNbr;
	}

	public String getCustFlatNbr() {
		return custFlatNbr;
	}

	public void setCustFlatNbr(String custFlatNbr) {
		this.custFlatNbr = custFlatNbr;
	}

	public String getCustAddrStreet() {
		return custAddrStreet;
	}

	public void setCustAddrStreet(String custAddrStreet) {
		this.custAddrStreet = custAddrStreet;
	}

	public String getCustAddrLine1() {
		return custAddrLine1;
	}

	public void setCustAddrLine1(String custAddrLine1) {
		this.custAddrLine1 = custAddrLine1;
	}

	public String getCustAddrLine2() {
		return custAddrLine2;
	}

	public void setCustAddrLine2(String custAddrLine2) {
		this.custAddrLine2 = custAddrLine2;
	}

	public String getCustPOBox() {
		return custPOBox;
	}

	public void setCustPOBox(String custPOBox) {
		this.custPOBox = custPOBox;
	}

	public String getCustAddrCountry() {
		return custAddrCountry;
	}

	public void setCustAddrCountry(String custAddrCountry) {
		this.custAddrCountry = custAddrCountry;
	}

	public String getLovDescCustAddrCountryName() {
		return lovDescCustAddrCountryName;
	}

	public void setLovDescCustAddrCountryName(String lovDescCustAddrCountryName) {
		this.lovDescCustAddrCountryName = lovDescCustAddrCountryName;
	}

	public String getCustAddrProvince() {
		return custAddrProvince;
	}

	public void setCustAddrProvince(String custAddrProvince) {
		this.custAddrProvince = custAddrProvince;
	}

	public String getLovDescCustAddrProvinceName() {
		return lovDescCustAddrProvinceName;
	}

	public void setLovDescCustAddrProvinceName(
			String lovDescCustAddrProvinceName) {
		this.lovDescCustAddrProvinceName = lovDescCustAddrProvinceName;
	}

	public String getCustAddrCity() {
		return custAddrCity;
	}

	public void setCustAddrCity(String custAddrCity) {
		this.custAddrCity = custAddrCity;
	}

	public String getLovDescCustAddrCityName() {
		return lovDescCustAddrCityName;
	}

	public void setLovDescCustAddrCityName(String lovDescCustAddrCityName) {
		this.lovDescCustAddrCityName = lovDescCustAddrCityName;
	}

	public String getCustAddrZIP() {
		return custAddrZIP;
	}

	public void setCustAddrZIP(String custAddrZIP) {
		this.custAddrZIP = custAddrZIP;
	}

	public String getCustAddrPhone() {
		return custAddrPhone;
	}

	public void setCustAddrPhone(String custAddrPhone) {
		this.custAddrPhone = custAddrPhone;
	}

	public Timestamp getCustAddrFrom() {
		return custAddrFrom;
	}

	public void setCustAddrFrom(Timestamp custAddrFrom) {
		this.custAddrFrom = custAddrFrom;
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
