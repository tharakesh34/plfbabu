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

import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinanceApplicationCode table</b>.<br>
 * 
 */
public class BankDetail extends AbstractWorkflowEntity implements java.io.Serializable {
	private static final long serialVersionUID = -6305409759684865400L;

	private String bankCode;
	private String bankName;
	private String bankShortCode;
	private boolean active;
	private int accNoLength;
	private boolean newRecord;
	private String lovValue;
	private BankDetail befImage;
	private LoggedInUser userDetails;
	
	public BankDetail() {
		super();
	}
	
	public BankDetail(String id) {
		super();
		this.setId(id);
	}

	public boolean isNew() {
		return isNewRecord();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return bankCode;
	}
	public void setId(String id) {
		this.bankCode = id;
	}

	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
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

	public BankDetail getBefImage() {
		return this.befImage;
	}
	public void setBefImage(BankDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public int getAccNoLength() {
		return accNoLength;
	}

	public void setAccNoLength(int accNoLength) {
		this.accNoLength = accNoLength;
	}
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getBankShortCode() {
		return bankShortCode;
	}

	public void setBankShortCode(String bankShortCode) {
		this.bankShortCode = bankShortCode;
	}
	
}
