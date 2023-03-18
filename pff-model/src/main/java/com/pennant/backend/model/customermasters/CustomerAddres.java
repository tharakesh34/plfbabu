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
 * * FileName : CustomerAddres.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified Date
 * : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.customermasters;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CustomerAddres table</b>.<br>
 *
 */
@XmlType(propOrder = { "custAddrType", "custAddrHNbr", "custFlatNbr", "custAddrStreet", "custAddrLine1",
		"custAddrLine2", "custPOBox", "custAddrCity", "custAddrProvince", "custAddrCountry", "custAddrZIP",
		"custAddrFrom", "typeOfResidence", "custAddrPriority" })
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerAddres extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3309604710675073740L;

	private long custAddressId = Long.MIN_VALUE;
	private long custID = Long.MIN_VALUE;
	@XmlElement
	private String lovDescCustShrtName;
	@XmlElement(name = "addrType")
	private String custAddrType;
	private String lovDescCustAddrTypeName;
	@XmlElement(name = "buildingNo")
	private String custAddrHNbr;
	@XmlElement(name = "FlatNo")
	private String custFlatNbr;
	@XmlElement(name = "Street")
	private String custAddrStreet;
	@XmlElement(name = "addrLine1")
	private String custAddrLine1;
	@XmlElement(name = "addrLine2")
	private String custAddrLine2;
	@XmlElement(name = "poBox")
	private String custPOBox;
	@XmlElement(name = "country")
	private String custAddrCountry;
	@XmlElement(name = "countryName")
	private String lovDescCustAddrCountryName;
	@XmlElement(name = "state")
	private String custAddrProvince;
	@XmlElement(name = "stateName")
	private String lovDescCustAddrProvinceName;
	@XmlElement(name = "city")
	private String custAddrCity;
	@XmlElement(name = "cityName")
	private String lovDescCustAddrCityName;
	@XmlElement(name = "pinCode")
	private String custAddrZIP;
	private String custAddrPhone;
	private String lovValue;
	private CustomerAddres befImage;
	private LoggedInUser userDetails;
	@XmlElement(name = "residingFrom")
	private Timestamp custAddrFrom;
	private String lovDescCustRecordType;
	@XmlElement
	private String lovDescCustCIF;
	@XmlElement(name = "priority")
	private int custAddrPriority;
	@XmlElement
	private String typeOfResidence;

	private String sourceId;

	private String cityRefNo;
	private String stateRefNo;
	private String lovDescCustAddrZip;
	private String custAddrLine3;
	private String custAddrLine4;
	@XmlElement(name = "district")
	private String custDistrict;
	@XmlElement(name = "districtName")
	private String lovDescCustDistrictName;
	@XmlElement
	private Long pinCodeId;

	public CustomerAddres() {
		super();
	}

	public CustomerAddres(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("sourceId");
		excludeFields.add("cityRefNo");
		excludeFields.add("stateRefNo");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return custID;
	}

	public void setId(long id) {
		this.custID = id;
	}

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
		return this.lovDescCustAddrTypeName;
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
		return this.lovDescCustAddrCountryName;
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
		return this.lovDescCustAddrProvinceName;
	}

	public void setLovDescCustAddrProvinceName(String lovDescCustAddrProvinceName) {
		this.lovDescCustAddrProvinceName = lovDescCustAddrProvinceName;
	}

	public String getCustAddrCity() {
		return custAddrCity;
	}

	public void setCustAddrCity(String custAddrCity) {
		this.custAddrCity = custAddrCity;
	}

	public String getLovDescCustAddrCityName() {
		return this.lovDescCustAddrCityName;
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

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CustomerAddres getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CustomerAddres beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public void setLoginDetails(LoggedInUser userDetails) {
		setLastMntBy(userDetails.getUserId());
		this.userDetails = userDetails;

	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public int getCustAddrPriority() {
		return custAddrPriority;
	}

	public void setCustAddrPriority(int custAddrPriority) {
		this.custAddrPriority = custAddrPriority;
	}

	public String getTypeOfResidence() {
		return typeOfResidence;
	}

	public void setTypeOfResidence(String typeOfResidence) {
		this.typeOfResidence = typeOfResidence;
	}

	public String getCityRefNo() {
		return cityRefNo;
	}

	public void setCityRefNo(String cityRefNo) {
		this.cityRefNo = cityRefNo;
	}

	public String getStateRefNo() {
		return stateRefNo;
	}

	public void setStateRefNo(String stateRefNo) {
		this.stateRefNo = stateRefNo;
	}

	public String getLovDescCustAddrZip() {
		return lovDescCustAddrZip;
	}

	public void setLovDescCustAddrZip(String lovDescCustAddrZip) {
		this.lovDescCustAddrZip = lovDescCustAddrZip;
	}

	public String getCustDistrict() {
		return custDistrict;
	}

	public void setCustDistrict(String custDistrict) {
		this.custDistrict = custDistrict;
	}

	public String getCustAddrLine3() {
		return custAddrLine3;
	}

	public void setCustAddrLine3(String custAddrLine3) {
		this.custAddrLine3 = custAddrLine3;
	}

	public String getCustAddrLine4() {
		return custAddrLine4;
	}

	public void setCustAddrLine4(String custAddrLine4) {
		this.custAddrLine4 = custAddrLine4;
	}

	public long getCustAddressId() {
		return custAddressId;
	}

	public void setCustAddressId(long custAddressId) {
		this.custAddressId = custAddressId;
	}

	public Long getPinCodeId() {
		return pinCodeId;
	}

	public void setPinCodeId(Long pinCodeId) {
		this.pinCodeId = pinCodeId;
	}

	public String getLovDescCustDistrictName() {
		return lovDescCustDistrictName;
	}

	public void setLovDescCustDistrictName(String lovDescCustDistrictName) {
		this.lovDescCustDistrictName = lovDescCustDistrictName;
	}

}
