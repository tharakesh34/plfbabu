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

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  LegalPropertyDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-06-2018    														*
 *                                                                  						*
 * Modified Date    :  16-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-06-2018       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.model.legal;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>LegalPropertyDetail table</b>.<br>
 *
 */
@XmlType(propOrder = { "legalPropertyId", "legalReference", "scheduleType", "propertySchedule", "propertyType",
		"northBy", "southBy", "eastBy", "westBy", "measurement", "registrationOffice", "registrationDistrict",
		"propertyOwner" })
@XmlAccessorType(XmlAccessType.FIELD)
public class LegalPropertyDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long legalPropertyId = Long.MIN_VALUE;
	private long legalId = Long.MIN_VALUE;
	private String legalReference;
	private String scheduleType;
	private String scheduleTypeName;
	private String propertySchedule;
	private String propertyType;
	private String propertyTypeName;
	private String northBy;
	private String southBy;
	private String eastBy;
	private String westBy;
	private BigDecimal measurement = BigDecimal.ZERO;
	private String registrationOffice;
	private String registrationDistrict;
	private String propertyOwner;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private LegalPropertyDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public LegalPropertyDetail() {
		super();
	}

	public LegalPropertyDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("scheduleTypeName");
		excludeFields.add("propertyTypeName");
		excludeFields.add("legalReference");
		return excludeFields;
	}

	public long getId() {
		return legalPropertyId;
	}

	public void setId(long id) {
		this.legalPropertyId = id;
	}

	public long getLegalPropertyId() {
		return legalPropertyId;
	}

	public void setLegalPropertyId(long legalPropertyId) {
		this.legalPropertyId = legalPropertyId;
	}

	public String getLegalReference() {
		return legalReference;
	}

	public void setLegalReference(String legalReference) {
		this.legalReference = legalReference;
	}

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getScheduleTypeName() {
		return this.scheduleTypeName;
	}

	public void setScheduleTypeName(String scheduleTypeName) {
		this.scheduleTypeName = scheduleTypeName;
	}

	public String getPropertySchedule() {
		return propertySchedule;
	}

	public void setPropertySchedule(String propertySchedule) {
		this.propertySchedule = propertySchedule;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public String getPropertyTypeName() {
		return this.propertyTypeName;
	}

	public void setPropertyTypeName(String propertyTypeName) {
		this.propertyTypeName = propertyTypeName;
	}

	public String getNorthBy() {
		return northBy;
	}

	public void setNorthBy(String northBy) {
		this.northBy = northBy;
	}

	public String getSouthBy() {
		return southBy;
	}

	public void setSouthBy(String southBy) {
		this.southBy = southBy;
	}

	public String getEastBy() {
		return eastBy;
	}

	public void setEastBy(String eastBy) {
		this.eastBy = eastBy;
	}

	public String getWestBy() {
		return westBy;
	}

	public void setWestBy(String westBy) {
		this.westBy = westBy;
	}

	public BigDecimal getMeasurement() {
		return measurement;
	}

	public void setMeasurement(BigDecimal measurement) {
		this.measurement = measurement;
	}

	public String getRegistrationOffice() {
		return registrationOffice;
	}

	public void setRegistrationOffice(String registrationOffice) {
		this.registrationOffice = registrationOffice;
	}

	public String getRegistrationDistrict() {
		return registrationDistrict;
	}

	public void setRegistrationDistrict(String registrationDistrict) {
		this.registrationDistrict = registrationDistrict;
	}

	public String getPropertyOwner() {
		return propertyOwner;
	}

	public void setPropertyOwner(String propertyOwner) {
		this.propertyOwner = propertyOwner;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public LegalPropertyDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(LegalPropertyDetail beforeImage) {
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

	public long getLegalId() {
		return legalId;
	}

	public void setLegalId(long legalId) {
		this.legalId = legalId;
	}

}
