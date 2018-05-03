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
 * FileName    		:  BaseRateCode.java                                                   * 	  
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
package com.pennant.backend.model.applicationmaster;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>BaseRateCode table</b>.<br>
 *
 */
public class CostOfFundCode extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -1288421928415683359L;
	
	private String cofCode;
	private String cofDesc;
	private boolean newRecord;
	private String lovValue;
	private CostOfFundCode befImage;
	private LoggedInUser userDetails;
	private boolean active;

	public boolean isNew() {
		return isNewRecord();
	}

	public String getCofCode() {
		return cofCode;
	}

	public void setCofCode(String cofCode) {
		this.cofCode = cofCode;
	}

	public String getCofDesc() {
		return cofDesc;
	}

	public void setCofDesc(String cofDesc) {
		this.cofDesc = cofDesc;
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

	public CostOfFundCode getBefImage() {
		return befImage;
	}

	public void setBefImage(CostOfFundCode befImage) {
		this.befImage = befImage;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public CostOfFundCode() {
		super();
	}

	public CostOfFundCode(String id) {
		super();
		this.setId(id);
	}
	
	public String getId() {
		return cofCode;
	}
	public void setId (String id) {
		this.cofCode = id;
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	
	
}
