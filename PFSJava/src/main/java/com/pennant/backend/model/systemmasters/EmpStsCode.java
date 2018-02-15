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
 * FileName    		:  EmpStsCode.java                                                   * 	  
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
 * Model class for the <b>EmpStsCode table</b>.<br>
 *
 */
public class EmpStsCode extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 5510099428934198554L;
	
	private String empStsCode;
	private String empStsDesc;
	private boolean empStsIsActive;
	private boolean newRecord;
	private String lovValue;
	private EmpStsCode befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public EmpStsCode() {
		super();
	}

	public EmpStsCode(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return empStsCode;
	}	
	public void setId (String id) {
		this.empStsCode = id;
	}
	
	public String getEmpStsCode() {
		return empStsCode;
	}
	public void setEmpStsCode(String empStsCode) {
		this.empStsCode = empStsCode;
	}
	
	public String getEmpStsDesc() {
		return empStsDesc;
	}
	public void setEmpStsDesc(String empStsDesc) {
		this.empStsDesc = empStsDesc;
	}
	
	public boolean isEmpStsIsActive() {
		return empStsIsActive;
	}
	public void setEmpStsIsActive(boolean empStsIsActive) {
		this.empStsIsActive = empStsIsActive;
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

	public EmpStsCode getBefImage(){
		return this.befImage;
	}
	public void setBefImage(EmpStsCode beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
