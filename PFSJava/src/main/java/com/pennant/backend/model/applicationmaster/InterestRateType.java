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
 * FileName    		:  InterestRateType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>InterestRateType table</b>.<br>
 *
 */
public class InterestRateType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -4364815634437557784L;

	private String intRateTypeCode;
	private String intRateTypeDesc;
	private boolean intRateTypeIsActive;
	private boolean newRecord;
	private String lovValue;
	private InterestRateType befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public InterestRateType() {
		super();
	}

	public InterestRateType(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return intRateTypeCode;
	}
	public void setId (String id) {
		this.intRateTypeCode = id;
	}
	
	public String getIntRateTypeCode() {
		return intRateTypeCode;
	}
	public void setIntRateTypeCode(String intRateTypeCode) {
		this.intRateTypeCode = intRateTypeCode;
	}
	
	public String getIntRateTypeDesc() {
		return intRateTypeDesc;
	}
	public void setIntRateTypeDesc(String intRateTypeDesc) {
		this.intRateTypeDesc = intRateTypeDesc;
	}
	
	public boolean isIntRateTypeIsActive() {
		return intRateTypeIsActive;
	}
	public void setIntRateTypeIsActive(boolean intRateTypeIsActive) {
		this.intRateTypeIsActive = intRateTypeIsActive;
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

	public InterestRateType getBefImage(){
		return this.befImage;
	}
	public void setBefImage(InterestRateType beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
