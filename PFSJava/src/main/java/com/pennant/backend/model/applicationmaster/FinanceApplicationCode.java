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
 * FileName    		:  FinanceApplicationCode.java                                                   * 	  
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

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>FinanceApplicationCode table</b>.<br>
 * 
 */
public class FinanceApplicationCode extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -6305409759684865400L;

	private String finAppType;
	private String finAppDesc;
	private boolean finAppIsActive;
	private boolean newRecord;
	private String lovValue;
	private FinanceApplicationCode befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public FinanceApplicationCode() {
		super();
	}

	public FinanceApplicationCode(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return finAppType;
	}
	public void setId(String id) {
		this.finAppType = id;
	}

	public String getFinAppType() {
		return finAppType;
	}
	public void setFinAppType(String finAppType) {
		this.finAppType = finAppType;
	}

	public String getFinAppDesc() {
		return finAppDesc;
	}
	public void setFinAppDesc(String finAppDesc) {
		this.finAppDesc = finAppDesc;
	}

	public boolean isFinAppIsActive() {
		return finAppIsActive;
	}
	public void setFinAppIsActive(boolean finAppIsActive) {
		this.finAppIsActive = finAppIsActive;
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

	public FinanceApplicationCode getBefImage() {
		return this.befImage;
	}
	public void setBefImage(FinanceApplicationCode beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
