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
 * * FileName : Entity.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-06-2017 * * Modified Date :
 * 15-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Entity table</b>.<br>
 *
 */
@XmlType(propOrder = { "entityCode", "entityDesc", "pANNumber", "country", "stateCode", "cityCode", "pinCode",
		"active" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Entity extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "entity")
	private String entityCode;
	private String entityDesc;
	private String pANNumber;
	private String country;
	private String countryName;
	private String stateCode;
	private String stateCodeName;
	private String cityCode;
	private String cityCodeName;
	private String pinCode;
	private String pinCodeName;
	private String entityAddrLine1;
	private String entityAddrLine2;
	private String entityAddrHNbr;
	private String entityFlatNbr;
	private String entityAddrStreet;
	private String entityPOBox;
	private String provinceName;
	private String cityName;
	private boolean gstinAvailable;
	private boolean active;
	@XmlTransient
	private String lovValue;

	private String cINNumber;
	private Long pinCodeId;

	public String getEntityAddrLine1() {
		return entityAddrLine1;
	}

	public void setEntityAddrLine1(String entityAddrLine1) {
		this.entityAddrLine1 = entityAddrLine1;
	}

	public String getEntityAddrLine2() {
		return entityAddrLine2;
	}

	public void setEntityAddrLine2(String entityAddrLine2) {
		this.entityAddrLine2 = entityAddrLine2;
	}

	public String getEntityAddrHNbr() {
		return entityAddrHNbr;
	}

	public void setEntityAddrHNbr(String entityAddrHNbr) {
		this.entityAddrHNbr = entityAddrHNbr;
	}

	public String getEntityFlatNbr() {
		return entityFlatNbr;
	}

	public void setEntityFlatNbr(String entityFlatNbr) {
		this.entityFlatNbr = entityFlatNbr;
	}

	public String getEntityAddrStreet() {
		return entityAddrStreet;
	}

	public void setEntityAddrStreet(String entityAddrStreet) {
		this.entityAddrStreet = entityAddrStreet;
	}

	@XmlTransient
	private Entity befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public Entity() {
		super();
	}

	public Entity(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("countryName");
		excludeFields.add("stateCodeName");
		excludeFields.add("cityCodeName");
		excludeFields.add("pinCodeName");
		excludeFields.add("provinceName");
		excludeFields.add("cityName");

		return excludeFields;
	}

	public String getId() {
		return entityCode;
	}

	public void setId(String id) {
		this.entityCode = id;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public String getPANNumber() {
		return pANNumber;
	}

	public void setPANNumber(String pANNumber) {
		this.pANNumber = pANNumber;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getStateCodeName() {
		return this.stateCodeName;
	}

	public void setStateCodeName(String stateCodeName) {
		this.stateCodeName = stateCodeName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityCodeName() {
		return this.cityCodeName;
	}

	public void setCityCodeName(String cityCodeName) {
		this.cityCodeName = cityCodeName;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getPinCodeName() {
		return this.pinCodeName;
	}

	public void setPinCodeName(String pinCodeName) {
		this.pinCodeName = pinCodeName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public Entity getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Entity beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getEntityPOBox() {
		return entityPOBox;
	}

	public void setEntityPOBox(String entityPOBox) {
		this.entityPOBox = entityPOBox;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getcINNumber() {
		return cINNumber;
	}

	public void setcINNumber(String cINNumber) {
		this.cINNumber = cINNumber;
	}

	public boolean isGstinAvailable() {
		return gstinAvailable;
	}

	public void setGstinAvailable(boolean gstinAvailable) {
		this.gstinAvailable = gstinAvailable;
	}

	public Long getPinCodeId() {
		return pinCodeId;
	}

	public void setPinCodeId(Long pinCodeId) {
		this.pinCodeId = pinCodeId;
	}
}