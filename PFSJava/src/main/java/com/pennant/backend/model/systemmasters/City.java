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
 * FileName    		:  City.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.systemmasters;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>City table</b>.<br>
 *
 */
public class City extends AbstractWorkflowEntity {
		
	private static final long serialVersionUID = -306657295035931426L;
	
	private String pCCountry;
	private String lovDescPCCountryName;
	private String pCProvince;
	private String lovDescPCProvinceName;
	private String pCCity;
	private String pCCityName;
	private String pCCityClassification;
	private String bankRefNo;
	private boolean newRecord;
	private String lovValue;
	private City befImage;
	private LoggedInUser userDetails;
	private boolean cityIsActive;
	private String pinCode;
	private String areaName;
	private String taxStateCode;	//Added for GSTIN
	
	public boolean isNew() {
		return isNewRecord();
	}

	public City() {
		super();
	}

	public City(String id) {
		super();
		this.setId(id);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("pinCode");
			excludeFields.add("areaName");
			excludeFields.add("taxStateCode");
	return excludeFields;
	}
	
	public String getId() {
		return pCCity;
	}	
	public void setId (String id) {
		this.pCCity = id;
	}
	
	public String getPCCountry() {
		return pCCountry;
	}
	public void setPCCountry(String pCCountry) {
		this.pCCountry = pCCountry;
	}

	public String getLovDescPCCountryName() {
		return this.lovDescPCCountryName;
	}
	public void setLovDescPCCountryName(String lovDescPCCountryName) {
		this.lovDescPCCountryName= lovDescPCCountryName;
	}
	
	public String getPCProvince() {
		return pCProvince;
	}
	public void setPCProvince(String pCProvince) {
		this.pCProvince = pCProvince;
	}	

	public String getLovDescPCProvinceName() {
		return this.lovDescPCProvinceName;
	}
	public void setLovDescPCProvinceName(String lovDescPCProvinceName) {
		this.lovDescPCProvinceName = lovDescPCProvinceName;
	}
	
	public String getPCCity() {
		return pCCity;
	}
	public void setPCCity(String pCCity) {
		this.pCCity = pCCity;
	}
	
	public String getPCCityName() {
		return pCCityName;
	}
	public void setPCCityName(String pCCityName) {
		this.pCCityName = pCCityName;
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

	public City getBefImage(){
		return this.befImage;
	}	
	public void setBefImage(City beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getpCCityClassification() {
		return pCCityClassification;
	}

	public void setpCCityClassification(String pCCityClassification) {
		this.pCCityClassification = pCCityClassification;
	}

	public String getBankRefNo() {
		return bankRefNo;
	}

	public void setBankRefNo(String bankRefNo) {
		this.bankRefNo = bankRefNo;
	}
	public boolean isCityIsActive() {
		return cityIsActive;
	}

	public void setCityIsActive(boolean cityIsActive) {
		this.cityIsActive = cityIsActive;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getTaxStateCode() {
		return taxStateCode;
	}

	public void setTaxStateCode(String taxStateCode) {
		this.taxStateCode = taxStateCode;
	}
}
