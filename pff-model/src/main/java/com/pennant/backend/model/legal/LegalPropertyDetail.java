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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>LegalPropertyDetail table</b>.<br>
 *
 */

@XmlAccessorType(XmlAccessType.NONE)
public class LegalPropertyDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long legalPropertyId = Long.MIN_VALUE;
	private long legalId = Long.MIN_VALUE;
	private int seqNum = 0;
	private String legalReference;
	@XmlElement
	private String scheduleType;
	private String scheduleTypeName;
	@XmlElement
	private String propertySchedule;
	@XmlElement
	private String propertyType;
	private String propertyTypeName;
	@XmlElement
	private String northBy;
	@XmlElement
	private String southBy;
	@XmlElement
	private String eastBy;
	@XmlElement
	private String westBy;
	@XmlElement
	private BigDecimal measurement = BigDecimal.ZERO;
	@XmlElement
	private String registrationOffice;
	@XmlElement
	private String registrationDistrict;
	@XmlElement
	private String propertyOwner;
	private String listApplicantNames;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private LegalPropertyDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	@XmlElement
	private String urbanLandCeiling;
	@XmlElement
	private String minorshareInvolved;
	@XmlElement
	private String propertyIsGramanatham;
	@XmlElement
	private String propertyReleased;
	@XmlElement
	private String propOriginalsAvailable;
	@XmlElement
	private String propertyIsAgricultural;
	@XmlElement
	private String nocObtainedFromLPA;
	@XmlElement
	private String anyMortgagePending;

	@XmlElement
	private String northSideEastByWest;
	@XmlElement
	private String southSideWestByEast;
	@XmlElement
	private String eastSideNorthBySouth;
	@XmlElement
	private String westSideSouthByNorth;

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
		excludeFields.add("seqNum");
		excludeFields.add("listApplicantNames");
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

	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	public String getListApplicantNames() {
		return listApplicantNames;
	}

	public void setListApplicantNames(String listApplicantNames) {
		this.listApplicantNames = listApplicantNames;
	}

	public String getUrbanLandCeiling() {
		return urbanLandCeiling;
	}

	public void setUrbanLandCeiling(String urbanLandCeiling) {
		this.urbanLandCeiling = urbanLandCeiling;
	}

	public String getMinorshareInvolved() {
		return minorshareInvolved;
	}

	public void setMinorshareInvolved(String minorshareInvolved) {
		this.minorshareInvolved = minorshareInvolved;
	}

	public String getPropertyIsGramanatham() {
		return propertyIsGramanatham;
	}

	public void setPropertyIsGramanatham(String propertyIsGramanatham) {
		this.propertyIsGramanatham = propertyIsGramanatham;
	}

	public String getPropertyReleased() {
		return propertyReleased;
	}

	public void setPropertyReleased(String propertyReleased) {
		this.propertyReleased = propertyReleased;
	}

	public String getPropOriginalsAvailable() {
		return propOriginalsAvailable;
	}

	public void setPropOriginalsAvailable(String propOriginalsAvailable) {
		this.propOriginalsAvailable = propOriginalsAvailable;
	}

	public String getPropertyIsAgricultural() {
		return propertyIsAgricultural;
	}

	public void setPropertyIsAgricultural(String propertyIsAgricultural) {
		this.propertyIsAgricultural = propertyIsAgricultural;
	}

	public String getNocObtainedFromLPA() {
		return nocObtainedFromLPA;
	}

	public void setNocObtainedFromLPA(String nocObtainedFromLPA) {
		this.nocObtainedFromLPA = nocObtainedFromLPA;
	}

	public String getAnyMortgagePending() {
		return anyMortgagePending;
	}

	public void setAnyMortgagePending(String anyMortgagePending) {
		this.anyMortgagePending = anyMortgagePending;
	}

	public String getNorthSideEastByWest() {
		return northSideEastByWest;
	}

	public void setNorthSideEastByWest(String northSideEastByWest) {
		this.northSideEastByWest = northSideEastByWest;
	}

	public String getSouthSideWestByEast() {
		return southSideWestByEast;
	}

	public void setSouthSideWestByEast(String southSideWestByEast) {
		this.southSideWestByEast = southSideWestByEast;
	}

	public String getEastSideNorthBySouth() {
		return eastSideNorthBySouth;
	}

	public void setEastSideNorthBySouth(String eastSideNorthBySouth) {
		this.eastSideNorthBySouth = eastSideNorthBySouth;
	}

	public String getWestSideSouthByNorth() {
		return westSideSouthByNorth;
	}

	public void setWestSideSouthByNorth(String westSideSouthByNorth) {
		this.westSideSouthByNorth = westSideSouthByNorth;
	}
}
