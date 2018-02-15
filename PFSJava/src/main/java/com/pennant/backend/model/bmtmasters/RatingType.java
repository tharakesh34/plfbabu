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
 * FileName    		:  RatingType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>RatingType table</b>.<br>
 *
 */
public class RatingType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -7747391180611594985L;

	private String ratingType;
	private String ratingTypeDesc;
	private boolean valueType;
	private int valueLen;
	private boolean ratingIsActive;
	private boolean newRecord;
	private String lovValue;
	private RatingType befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public RatingType() {
		super();
	}

	public RatingType(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return ratingType;
	}
	public void setId (String id) {
		this.ratingType = id;
	}

	public String getRatingType() {
		return ratingType;
	}
	public void setRatingType(String ratingType) {
		this.ratingType = ratingType;
	}

	public String getRatingTypeDesc() {
		return ratingTypeDesc;
	}
	public void setRatingTypeDesc(String ratingTypeDesc) {
		this.ratingTypeDesc = ratingTypeDesc;
	}

	public boolean isValueType() {
		return valueType;
	}
	public void setValueType(boolean valueType) {
		this.valueType = valueType;
	}

	public int getValueLen() {
		return valueLen;
	}
	public void setValueLen(int valueLen) {
		this.valueLen = valueLen;
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

	public RatingType getBefImage(){
		return this.befImage;
	}
	public void setBefImage(RatingType beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
