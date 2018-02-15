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
 * FileName    		:  OtherBankFinanceType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-04-2015    														*
 *                                                                  						*
 * Modified Date    :  03-04-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-04-2015       Pennant	                 0.1                                            * 
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

import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>OtherBankFinanceType table</b>.<br>
 *
 */
public class OtherBankFinanceType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String finType;
	private String finTypeDesc;
	private boolean active;
	private boolean newRecord;
	private String lovValue;
	private OtherBankFinanceType befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public OtherBankFinanceType() {
		super();
	}

	public OtherBankFinanceType(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return finType;
	}
	
	public void setId (String id) {
		this.finType = id;
	}
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	
		
	
	public String getFinTypeDesc() {
		return finTypeDesc;
	}
	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}
	
	
		
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
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

	public OtherBankFinanceType getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(OtherBankFinanceType beforeImage){
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
