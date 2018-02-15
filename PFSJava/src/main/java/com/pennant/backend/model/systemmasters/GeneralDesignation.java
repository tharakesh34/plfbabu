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
 * FileName    		:  GeneralDesignation.java                                                   * 	  
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

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>GeneralDesignation table</b>.<br>
 * 
 */
public class GeneralDesignation extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 5253784889807463490L;

	private String genDesignation;
	private String genDesgDesc;
	private boolean newRecord;
	private String lovValue;
	private GeneralDesignation befImage;
	private LoggedInUser userDetails;
	private boolean genDesgIsActive;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public GeneralDesignation() {
		super();
	}

	public GeneralDesignation(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return genDesignation;
	}
	public void setId(String id) {
		this.genDesignation = id;
	}

	public String getGenDesignation() {
		return genDesignation;
	}
	public void setGenDesignation(String genDesignation) {
		this.genDesignation = genDesignation;
	}

	public String getGenDesgDesc() {
		return genDesgDesc;
	}
	public void setGenDesgDesc(String genDesgDesc) {
		this.genDesgDesc = genDesgDesc;
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

	public GeneralDesignation getBefImage() {
		return this.befImage;
	}
	public void setBefImage(GeneralDesignation beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public boolean isGenDesgIsActive() {
		return genDesgIsActive;
	}
	public void setGenDesgIsActive(boolean genDesgIsActive) {
		this.genDesgIsActive = genDesgIsActive;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
