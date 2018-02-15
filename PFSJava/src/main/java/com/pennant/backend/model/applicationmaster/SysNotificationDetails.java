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
 * FileName    		:  ChequePurpose.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ChequePurpose table</b>.<br>
 *
 */
public class SysNotificationDetails extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long sysNotificationId = Long.MIN_VALUE;
	private String finReference;
	private String finBranch;
	private BigDecimal finCurODAmt = BigDecimal.ZERO;
	private int finCurODDays;
	private String finCcy;
	private String custCIF;
	private long custID;
	private String custShrtName;
	private String finCurODAmtInStr;
	private String finPurpose;

	//Common Fields
	private boolean newRecord = false;
	private SysNotificationDetails befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public SysNotificationDetails() {
		super();
	}

	public SysNotificationDetails(long id) {
		super();
		this.setId(id);
	}

	public long getId() {
		return sysNotificationId;
	}

	public void setId(long id) {
		this.sysNotificationId = id;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	public SysNotificationDetails getBefImage() {
		return befImage;
	}
	public void setBefImage(SysNotificationDetails befImage) {
		this.befImage = befImage;
	}
	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getSysNotificationId() {
		return sysNotificationId;
	}


	public void setSysNotificationId(long sysNotificationId) {
		this.sysNotificationId = sysNotificationId;
	}


	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public BigDecimal getFinCurODAmt() {
		return finCurODAmt;
	}

	public void setFinCurODAmt(BigDecimal finCurODAmt) {
		this.finCurODAmt = finCurODAmt;
	}

	public int getFinCurODDays() {
		return finCurODDays;
	}

	public void setFinCurODDays(int finCurODDays) {
		this.finCurODDays = finCurODDays;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}
	public String getFinCurODAmtInStr() {
		return finCurODAmtInStr;
	}

	public void setFinCurODAmtInStr(String finCurODAmtInStr) {
		this.finCurODAmtInStr = finCurODAmtInStr;
	}

	public String getFinPurpose() {
		return finPurpose;
	}

	public void setFinPurpose(String finPurpose) {
		this.finPurpose = finPurpose;
	}
}