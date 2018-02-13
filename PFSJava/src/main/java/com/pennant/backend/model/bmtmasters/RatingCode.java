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
 * FileName    		:  RatingCode.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.bmtmasters;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>RatingCode table</b>.<br>
 * 
 */
public class RatingCode extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 6288583358755707910L;

	private String ratingType;
	private String ratingCode;
	private String ratingCodeDesc;
	private boolean ratingIsActive;
	private boolean newRecord;
	private String lovValue;
	private RatingCode befImage;
	private LoggedInUser userDetails;
	private String lovDescRatingTypeName;

	public boolean isNew() {
		return isNewRecord();
	}

	public RatingCode() {
		super();
	}

	public RatingCode(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return ratingType;
	}
	public void setId(String id) {
		this.ratingType = id;
	}

	public String getRatingType() {
		return ratingType;
	}
	public void setRatingType(String ratingType) {
		this.ratingType = ratingType;
	}

	public String getLovDescRatingTypeName() {
		return this.lovDescRatingTypeName;
	}
	public void setLovDescRatingTypeName(String lovDescRatingTypeName) {
		this.lovDescRatingTypeName = lovDescRatingTypeName;
	}

	public String getRatingCode() {
		return ratingCode;
	}
	public void setRatingCode(String ratingCode) {
		this.ratingCode = ratingCode;
	}

	public String getRatingCodeDesc() {
		return ratingCodeDesc;
	}
	public void setRatingCodeDesc(String ratingCodeDesc) {
		this.ratingCodeDesc = ratingCodeDesc;
	}

	public boolean isRatingIsActive() {
		return ratingIsActive;
	}
	public void setRatingIsActive(boolean ratingIsActive) {
		this.ratingIsActive = ratingIsActive;
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

	public RatingCode getBefImage() {
		return this.befImage;
	}
	public void setBefImage(RatingCode beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
