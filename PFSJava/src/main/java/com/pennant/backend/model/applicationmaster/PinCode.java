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
 * FileName    		:  PinCode.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-06-2017    														*
 *                                                                  						*
 * Modified Date    :  01-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>PinCode table</b>.<br>
 *
 */
public class PinCode extends AbstractWorkflowEntity  implements Entity {
private static final long serialVersionUID = 1L;

	private long pinCodeId = Long.MIN_VALUE;
	private String pinCode;
	private String city;
	private String pCCityName;
	private String areaName;
	private String pCProvince;
	private String pCCountry ;
	private String lovDescPCCountryName;
	private String lovDescPCProvinceName;
	private String gstin;
	private boolean active;
	private long groupId;
	private boolean serviceable;
	private boolean newRecord=false;
	private String lovValue;
	private PinCode befImage;
	private  LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public PinCode() {
		super();
	}

	public String getpCCityName() {
		return pCCityName;
	}

	public void setpCCityName(String pCCityName) {
		this.pCCityName = pCCityName;
	}

	public String getpCProvince() {
		return pCProvince;
	}

	public void setpCProvince(String pCProvince) {
		this.pCProvince = pCProvince;
	}

	public PinCode(long id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("pCCityName");
			excludeFields.add("pCProvince");
			excludeFields.add("lovDescPCProvinceName");
			excludeFields.add("pCCountry");
			excludeFields.add("gstin");
	return excludeFields;
	}

	public long getId() {
		return pinCodeId;
	}
	
	public void setId (long id) {
		this.pinCodeId = id;
	}
	public long getPinCodeId() {
		return pinCodeId;
	}
	public void setPinCodeId(long pinCodeId) {
		this.pinCodeId = pinCodeId;
	}
	
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
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

	public PinCode getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(PinCode beforeImage){
		this.befImage=beforeImage;
	}

	public  LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails( LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}


	public String getpCCountry() {
		return pCCountry;
	}

	public void setpCCountry(String pCCountry) {
		this.pCCountry = pCCountry;
	}

	public String getPCCityName() {
		return pCCityName;
	}
	public void setPCCityName(String pCCityName) {
		this.pCCityName = pCCityName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getPCProvince() {
		return pCProvince;
	}

	public void setPCProvince(String pCProvince) {
		this.pCProvince = pCProvince;
	}

	public String getLovDescPCProvinceName() {
		return lovDescPCProvinceName;
	}

	public void setLovDescPCProvinceName(String lovDescPCProvinceName) {
		this.lovDescPCProvinceName = lovDescPCProvinceName;
	}

	public String getLovDescPCCountryName() {
		return lovDescPCCountryName;
	}

	public void setLovDescPCCountryName(String lovDescPCCountryName) {
		this.lovDescPCCountryName = lovDescPCCountryName;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public boolean isServiceable() {
		return serviceable;
	}

	public void setServiceable(boolean serviceable) {
		this.serviceable = serviceable;
	}


}

