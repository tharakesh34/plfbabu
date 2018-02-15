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
 * FileName    		:  GeneralDepartment.java                                                   * 	  
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

import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>GeneralDepartment table</b>.<br>
 *
 */
public class GeneralDepartment extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 3367080990398812311L;

	private String genDepartment;
	private String genDeptDesc;
	private boolean newRecord;
	private String lovValue;
	private GeneralDepartment befImage;
	private LoggedInUser userDetails;
	private boolean genDeptIsActive;

	public boolean isNew() {
		return isNewRecord();
	}

	public GeneralDepartment() {
		super();
	}

	public GeneralDepartment(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return genDepartment;
	}
	public void setId (String id) {
		this.genDepartment = id;
	}
	
	public String getGenDepartment() {
		return genDepartment;
	}
	public void setGenDepartment(String genDepartment) {
		this.genDepartment = genDepartment;
	}
	
	public String getGenDeptDesc() {
		return genDeptDesc;
	}
	public void setGenDeptDesc(String genDeptDesc) {
		this.genDeptDesc = genDeptDesc;
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

	public GeneralDepartment getBefImage(){
		return this.befImage;
	}
	public void setBefImage(GeneralDepartment beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	public boolean isGenDeptIsActive() {
		return genDeptIsActive;
	}

	public void setGenDeptIsActive(boolean genDeptIsActive) {
		this.genDeptIsActive = genDeptIsActive;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
