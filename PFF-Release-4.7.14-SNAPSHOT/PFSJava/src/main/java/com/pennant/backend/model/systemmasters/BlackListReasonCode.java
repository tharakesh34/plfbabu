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
 * FileName    		:  BlackListReasonCode.java                                                   * 	  
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

package com.pennant.backend.model.systemmasters;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>BlackListReasonCode table</b>.<br>
 * 
 */
public class BlackListReasonCode extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 8931984426058505787L;

	private String bLRsnCode;
	private String bLRsnDesc;
	private boolean bLIsActive;
	private boolean newRecord;
	private String lovValue;
	private BlackListReasonCode befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public BlackListReasonCode() {
		super();
	}

	public BlackListReasonCode(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return bLRsnCode;
	}
	public void setId(String id) {
		this.bLRsnCode = id;
	}

	public String getBLRsnCode() {
		return bLRsnCode;
	}
	public void setBLRsnCode(String bLRsnCode) {
		this.bLRsnCode = bLRsnCode;
	}

	public String getBLRsnDesc() {
		return bLRsnDesc;
	}
	public void setBLRsnDesc(String bLRsnDesc) {
		this.bLRsnDesc = bLRsnDesc;
	}

	public boolean isBLIsActive() {
		return bLIsActive;
	}
	public void setBLIsActive(boolean bLIsActive) {
		this.bLIsActive = bLIsActive;
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

	public BlackListReasonCode getBefImage() {
		return this.befImage;
	}
	public void setBefImage(BlackListReasonCode beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
