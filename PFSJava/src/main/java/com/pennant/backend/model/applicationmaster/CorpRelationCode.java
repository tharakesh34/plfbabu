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
 * FileName    		:  CorpRelationCode.java                                                   * 	  
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
package com.pennant.backend.model.applicationmaster;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CorpRelationCode table</b>.<br>
 *
 */
public class CorpRelationCode extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 3953179174572717642L;

	private String corpRelationCode;
	private String corpRelationDesc;
	private boolean corpRelationIsActive;
	private boolean newRecord;
	private String lovValue;
	private CorpRelationCode befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public CorpRelationCode() {
		super();
	}

	public CorpRelationCode(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return corpRelationCode;
	}
	public void setId (String id) {
		this.corpRelationCode = id;
	}
	
	public String getCorpRelationCode() {
		return corpRelationCode;
	}
	public void setCorpRelationCode(String corpRelationCode) {
		this.corpRelationCode = corpRelationCode;
	}
	
	public String getCorpRelationDesc() {
		return corpRelationDesc;
	}
	public void setCorpRelationDesc(String corpRelationDesc) {
		this.corpRelationDesc = corpRelationDesc;
	}
	
	public boolean isCorpRelationIsActive() {
		return corpRelationIsActive;
	}
	public void setCorpRelationIsActive(boolean corpRelationIsActive) {
		this.corpRelationIsActive = corpRelationIsActive;
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

	public CorpRelationCode getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CorpRelationCode beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
