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
 * FileName    		:  Country.java                                                   * 	  
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

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Country table</b>.<br>
 * 
 */
public class Country extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 3557119742009775415L;

	private String countryCode;
	private String countryDesc;
	private BigDecimal countryParentLimit;
	private BigDecimal countryResidenceLimit;
	private BigDecimal countryRiskLimit;
	private boolean countryIsActive;
	private boolean newRecord;
	private String lovValue;
	private Country befImage;
	private LoggedInUser userDetails;
	private boolean systemDefault;

	public boolean isNew() {
		return isNewRecord();
	}

	public Country() {
		super();
	}

	public Country(String id) {
		super();
		this.setId(id);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return countryCode;
	}
	public void setId(String id) {
		this.countryCode = id;
	}

	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryDesc() {
		return countryDesc;
	}
	public void setCountryDesc(String countryDesc) {
		this.countryDesc = countryDesc;
	}

	public BigDecimal getCountryParentLimit() {
		return countryParentLimit;
	}
	public void setCountryParentLimit(BigDecimal countryParentLimit) {
		this.countryParentLimit = countryParentLimit;
	}

	public BigDecimal getCountryResidenceLimit() {
		return countryResidenceLimit;
	}
	public void setCountryResidenceLimit(BigDecimal countryResidenceLimit) {
		this.countryResidenceLimit = countryResidenceLimit;
	}

	public BigDecimal getCountryRiskLimit() {
		return countryRiskLimit;
	}
	public void setCountryRiskLimit(BigDecimal countryRiskLimit) {
		this.countryRiskLimit = countryRiskLimit;
	}

	public boolean isCountryIsActive() {
		return countryIsActive;
	}
	public void setCountryIsActive(boolean countryIsActive) {
		this.countryIsActive = countryIsActive;
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

	public Country getBefImage() {
		return this.befImage;
	}
	public void setBefImage(Country beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isSystemDefault() {
	    return systemDefault;
    }

	public void setSystemDefault(boolean systemDefault) {
	    this.systemDefault = systemDefault;
    }
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
