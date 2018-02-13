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
 * FileName    		:  Industry.java                                                   * 	  
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

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Industry table</b>.<br>
 * 
 */
public class Industry extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 8560791291211426411L;

	private String industryCode;
	private String subSectorCode;
	private String lovDescSubSectorCodeName;
	private String industryDesc;
	//private BigDecimal industryLimit;
	private boolean industryIsActive;
	private boolean newRecord;
	private String lovValue;
	private Industry befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public Industry() {
		super();
	}

	public Industry(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return industryCode;
	}
	public void setId(String id) {
		this.industryCode = id;
	}

	public String getIndustryCode() {
		return industryCode;
	}
	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}

	public String getIndustryDesc() {
		return industryDesc;
	}
	public void setIndustryDesc(String industryDesc) {
		this.industryDesc = industryDesc;
	}

	/*public BigDecimal getIndustryLimit() {
		return industryLimit;
	}
	public void setIndustryLimit(BigDecimal industryLimit) {
		this.industryLimit = industryLimit;
	}*/

	public boolean isIndustryIsActive() {
		return industryIsActive;
	}
	public void setIndustryIsActive(boolean industryIsActive) {
		this.industryIsActive = industryIsActive;
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

	public Industry getBefImage() {
		return this.befImage;
	}
	public void setBefImage(Industry beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setSubSectorCode(String subSectorCode) {
		this.subSectorCode = subSectorCode;
	}

	public String getSubSectorCode() {
		return subSectorCode;
	}

	public void setLovDescSubSectorCodeName(String lovDescSubSectorCodeName) {
		this.lovDescSubSectorCodeName = lovDescSubSectorCodeName;
	}

	public String getLovDescSubSectorCodeName() {
		return lovDescSubSectorCodeName;
	}
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
