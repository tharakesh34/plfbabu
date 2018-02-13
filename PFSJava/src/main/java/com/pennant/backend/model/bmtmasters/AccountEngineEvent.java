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
 * FileName    		:  AccountEngineEvent.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>AccountEngineEvent table</b>.<br>
 *
 */
public class AccountEngineEvent extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 105815151562496959L;

	private String aEEventCode = null;
	private String aEEventCodeDesc;
	private boolean active = false;
	private boolean newRecord = false;
	private boolean mandatory = false;
	private boolean oDApplicable = false;
	private String lovValue;
	private AccountEngineEvent befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public AccountEngineEvent() {
		super();
	}

	public AccountEngineEvent(String id) {
		super();
		this.setId(id);
	}

	public AccountEngineEvent(String id, String desc, boolean mandatory) {
		super();
		this.setId(id);
		this.setAEEventCodeDesc(desc);
		this.setMandatory(mandatory);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		//excludeFields.add("mandatory");

		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return aEEventCode;
	}

	public void setId(String id) {
		this.aEEventCode = id;
	}

	public String getAEEventCode() {
		return aEEventCode;
	}

	public void setAEEventCode(String aEEventCode) {
		this.aEEventCode = aEEventCode;
	}

	public String getAEEventCodeDesc() {
		return aEEventCodeDesc;
	}

	public void setAEEventCodeDesc(String aEEventCodeDesc) {
		this.aEEventCodeDesc = aEEventCodeDesc;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
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

	public AccountEngineEvent getBefImage() {
		return this.befImage;
	}

	public void setBefImage(AccountEngineEvent beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isoDApplicable() {
		return oDApplicable;
	}

	public void setoDApplicable(boolean oDApplicable) {
		this.oDApplicable = oDApplicable;
	}
}
