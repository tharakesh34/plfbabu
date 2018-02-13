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
 * FileName    		:  Salutation.java                                                   * 	  
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
 * Model class for the <b>Salutation table</b>.<br>
 * 
 */
public class Salutation extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -7274254261685023513L;

	private String salutationCode;
	private String saluationDesc;
	private boolean salutationIsActive;
	private String salutationGenderCode;
	private boolean newRecord;
	private String lovValue;
	private Salutation befImage;
	private LoggedInUser userDetails;
	private boolean systemDefault;

	public boolean isNew() {
		return isNewRecord();
	}

	public Salutation() {
		super();
	}

	public Salutation(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return salutationCode;
	}

	public void setId(String id) {
		this.salutationCode = id;
	}

	public String getSalutationCode() {
		return salutationCode;
	}

	public void setSalutationCode(String salutationCode) {
		this.salutationCode = salutationCode;
	}

	public String getSaluationDesc() {
		return saluationDesc;
	}

	public void setSaluationDesc(String saluationDesc) {
		this.saluationDesc = saluationDesc;
	}

	public boolean isSalutationIsActive() {
		return salutationIsActive;
	}

	public void setSalutationIsActive(boolean salutationIsActive) {
		this.salutationIsActive = salutationIsActive;
	}

	public String getSalutationGenderCode() {
		return salutationGenderCode;
	}

	public void setSalutationGenderCode(String salutationGenderCode) {
		this.salutationGenderCode = salutationGenderCode;
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

	public Salutation getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Salutation beforeImage) {
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
