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
 * FileName    		:  GroupStatusCode.java                                                   * 	  
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

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>GroupStatusCode table</b>.<br>
 * 
 */
public class GroupStatusCode extends AbstractWorkflowEntity {
	
	private static final long serialVersionUID = -5087295833493236885L;

	private String grpStsCode;
	private String grpStsDescription;
	private boolean grpStsIsActive;
	private boolean newRecord;
	private String lovValue;
	private GroupStatusCode befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public GroupStatusCode() {
		super();
	}

	public GroupStatusCode(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return grpStsCode;
	}
	public void setId(String id) {
		this.grpStsCode = id;
	}

	public String getGrpStsCode() {
		return grpStsCode;
	}
	public void setGrpStsCode(String grpStsCode) {
		this.grpStsCode = grpStsCode;
	}

	public String getGrpStsDescription() {
		return grpStsDescription;
	}
	public void setGrpStsDescription(String grpStsDescription) {
		this.grpStsDescription = grpStsDescription;
	}

	public boolean isGrpStsIsActive() {
		return grpStsIsActive;
	}
	public void setGrpStsIsActive(boolean grpStsIsActive) {
		this.grpStsIsActive = grpStsIsActive;
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

	public GroupStatusCode getBefImage() {
		return this.befImage;
	}
	public void setBefImage(GroupStatusCode beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
