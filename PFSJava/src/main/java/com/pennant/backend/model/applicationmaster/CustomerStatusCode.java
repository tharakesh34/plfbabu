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
 * FileName    		:  CustomerStatusCode.java                                                   * 	  
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

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>CustomerStatusCode table</b>.<br>
 * 
 */
public class CustomerStatusCode extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3232567361919439445L;

	private String custStsCode;
	private String custStsDescription;
	private boolean custStsIsActive;
	private int dueDays;
	private boolean suspendProfit;
	private boolean newRecord;
	private String lovValue;
	private CustomerStatusCode befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerStatusCode() {
		super();
	}

	public CustomerStatusCode(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return custStsCode;
	}
	public void setId(String id) {
		this.custStsCode = id;
	}

	public String getCustStsCode() {
		return custStsCode;
	}
	public void setCustStsCode(String custStsCode) {
		this.custStsCode = custStsCode;
	}

	public String getCustStsDescription() {
		return custStsDescription;
	}
	public void setCustStsDescription(String custStsDescription) {
		this.custStsDescription = custStsDescription;
	}

	public boolean isCustStsIsActive() {
		return custStsIsActive;
	}
	public void setCustStsIsActive(boolean custStsIsActive) {
		this.custStsIsActive = custStsIsActive;
	}
	
	public int getDueDays() {
    	return dueDays;
    }
	public void setDueDays(int dueDays) {
    	this.dueDays = dueDays;
    }

	public boolean isSuspendProfit() {
    	return suspendProfit;
    }
	public void setSuspendProfit(boolean suspendProfit) {
    	this.suspendProfit = suspendProfit;
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

	public CustomerStatusCode getBefImage() {
		return this.befImage;
	}
	public void setBefImage(CustomerStatusCode beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
