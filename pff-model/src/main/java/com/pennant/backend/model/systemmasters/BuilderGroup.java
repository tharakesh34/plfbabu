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
 * * FileName : BuilderGroup.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-05-2017 * * Modified Date :
 * 17-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>BuilderGroup table</b>.<br>
 *
 */
@XmlType(propOrder = { "id", "name", "segmentation" })
@XmlAccessorType(XmlAccessType.FIELD)
public class BuilderGroup extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String name;
	private String segmentation;
	private String segmentationName;
	private String fieldCode;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private BuilderGroup befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private String peDeveloperId;
	private String city;
	private String cityName;
	private String province;
	private String provinceName;
	private Long pinCodeId;
	private String poBox;
	private String areaName;
	private BigDecimal expLmtOnAmt = BigDecimal.ZERO;
	private BigDecimal expLmtOnNoOfUnits = BigDecimal.ZERO;
	private BigDecimal currExpUnits = BigDecimal.ZERO;
	private BigDecimal currExpAmt = BigDecimal.ZERO;

	public BuilderGroup() {
		super();
	}

	public BuilderGroup(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("segmentationName");
		excludeFields.add("fieldCode");
		excludeFields.add("cityName");
		excludeFields.add("provinceName");
		excludeFields.add("poBox");
		excludeFields.add("areaName");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBuilderGroupId() {
		return id;
	}

	public void setBuilderGroupId(long builderGroupId) {
		this.id = builderGroupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSegmentation() {
		return segmentation;
	}

	public void setSegmentation(String segmentation) {
		this.segmentation = segmentation;
	}

	public String getSegmentationName() {
		return this.segmentationName;
	}

	public void setSegmentationName(String segmentationName) {
		this.segmentationName = segmentationName;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public BuilderGroup getBefImage() {
		return this.befImage;
	}

	public void setBefImage(BuilderGroup beforeImage) {
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

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getPeDeveloperId() {
		return peDeveloperId;
	}

	public void setPeDeveloperId(String peDeveloperId) {
		this.peDeveloperId = peDeveloperId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public Long getPinCodeId() {
		return pinCodeId;
	}

	public void setPinCodeId(Long pinCodeId) {
		this.pinCodeId = pinCodeId;
	}

	public String getPoBox() {
		return poBox;
	}

	public void setPoBox(String poBox) {
		this.poBox = poBox;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public BigDecimal getExpLmtOnAmt() {
		return expLmtOnAmt;
	}

	public void setExpLmtOnAmt(BigDecimal expLmtOnAmt) {
		this.expLmtOnAmt = expLmtOnAmt;
	}

	public BigDecimal getExpLmtOnNoOfUnits() {
		return expLmtOnNoOfUnits;
	}

	public void setExpLmtOnNoOfUnits(BigDecimal expLmtOnNoOfUnits) {
		this.expLmtOnNoOfUnits = expLmtOnNoOfUnits;
	}

	public BigDecimal getCurrExpUnits() {
		return currExpUnits;
	}

	public void setCurrExpUnits(BigDecimal currExpUnits) {
		this.currExpUnits = currExpUnits;
	}

	public BigDecimal getCurrExpAmt() {
		return currExpAmt;
	}

	public void setCurrExpAmt(BigDecimal currExpAmt) {
		this.currExpAmt = currExpAmt;
	}

}
