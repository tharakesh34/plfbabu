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
 * FileName    		:  MaritalStatusCode.java                                                   * 	  
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
 * Model class for the <b>MaritalStatusCode table</b>.<br>
 * 
 */
public class MaritalStatusCode extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 8985724694282857916L;

	private String maritalStsCode;
	private String maritalStsDesc;
	private boolean maritalStsIsActive;
	private boolean newRecord;
	private String lovValue;
	private MaritalStatusCode befImage;
	private LoggedInUser userDetails;
	private boolean systemDefault;
	
	
	public boolean isNew() {
		return isNewRecord();
	}

	public MaritalStatusCode() {
		super();
	}

	public MaritalStatusCode(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return maritalStsCode;
	}
	public void setId(String id) {
		this.maritalStsCode = id;
	}

	public String getMaritalStsCode() {
		return maritalStsCode;
	}
	public void setMaritalStsCode(String maritalStsCode) {
		this.maritalStsCode = maritalStsCode;
	}

	public String getMaritalStsDesc() {
		return maritalStsDesc;
	}
	public void setMaritalStsDesc(String maritalStsDesc) {
		this.maritalStsDesc = maritalStsDesc;
	}

	public boolean isMaritalStsIsActive() {
		return maritalStsIsActive;
	}
	public void setMaritalStsIsActive(boolean maritalStsIsActive) {
		this.maritalStsIsActive = maritalStsIsActive;
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

	public MaritalStatusCode getBefImage() {
		return this.befImage;
	}
	public void setBefImage(MaritalStatusCode beforeImage) {
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
