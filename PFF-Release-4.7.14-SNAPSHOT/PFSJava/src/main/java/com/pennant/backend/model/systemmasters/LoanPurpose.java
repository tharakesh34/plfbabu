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
 * FileName    		:  LoanPurpose.java                                                   * 	  
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
 * Model class for the <b>AddressType table</b>.<br>
 *
 */
public class LoanPurpose extends AbstractWorkflowEntity {
	private static final long	serialVersionUID	= -3761541301075338850L;

	private String				loanPurposeCode;
	private String				loanPurposeDesc;
	private boolean				loanPurposeIsActive;
	private boolean				newRecord;
	private String				lovValue;
	private LoanPurpose			befImage;
	private LoggedInUser		userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public LoanPurpose() {
		super();
	}

	public LoanPurpose(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return loanPurposeCode;
	}

	public void setId(String id) {
		this.loanPurposeCode = id;
	}

	public String getLoanPurposeCode() {
		return loanPurposeCode;
	}

	public void setLoanPurposeCode(String loanPurposeCode) {
		this.loanPurposeCode = loanPurposeCode;
	}

	public String getLoanPurposeDesc() {
		return loanPurposeDesc;
	}

	public void setLoanPurposeDesc(String loanPurposeDesc) {
		this.loanPurposeDesc = loanPurposeDesc;
	}

	public boolean isLoanPurposeIsActive() {
		return loanPurposeIsActive;
	}

	public void setLoanPurposeIsActive(boolean loanPurposeIsActive) {
		this.loanPurposeIsActive = loanPurposeIsActive;
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

	public LoanPurpose getBefImage() {
		return this.befImage;
	}

	public void setBefImage(LoanPurpose beforeImage) {
		this.befImage = beforeImage;
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
