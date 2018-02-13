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
 * FileName    		:  InterestRateBasisCode.java                                                   * 	  
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

package com.pennant.backend.model.staticparms;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>InterestRateBasisCode table</b>.<br>
 * 
 */
public class InterestRateBasisCode extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 3132970492211301519L;
	
	private String intRateBasisCode;
	private String intRateBasisDesc;
	private boolean intRateBasisIsActive;
	private boolean newRecord;
	private String lovValue;
	private InterestRateBasisCode befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public InterestRateBasisCode() {
		super();
	}

	public InterestRateBasisCode(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return intRateBasisCode;
	}
	public void setId(String id) {
		this.intRateBasisCode = id;
	}

	public String getIntRateBasisCode() {
		return intRateBasisCode;
	}
	public void setIntRateBasisCode(String intRateBasisCode) {
		this.intRateBasisCode = intRateBasisCode;
	}

	public String getIntRateBasisDesc() {
		return intRateBasisDesc;
	}
	public void setIntRateBasisDesc(String intRateBasisDesc) {
		this.intRateBasisDesc = intRateBasisDesc;
	}

	public boolean isIntRateBasisIsActive() {
		return intRateBasisIsActive;
	}
	public void setIntRateBasisIsActive(boolean intRateBasisIsActive) {
		this.intRateBasisIsActive = intRateBasisIsActive;
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

	public InterestRateBasisCode getBefImage() {
		return this.befImage;
	}
	public void setBefImage(InterestRateBasisCode beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
