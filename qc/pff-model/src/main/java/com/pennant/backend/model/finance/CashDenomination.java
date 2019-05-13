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
 * FileName    		:  CashDenomination.java                                                * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-07-2018    														*
 *                                                                  						*
 * Modified Date    :  09-07-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-07-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CashDenomination table</b>.<br>
 *
 */
@XmlType(propOrder = { "id", "moduleCode", "denomination", "count", "amount" })
@XmlAccessorType(XmlAccessType.FIELD)
public class CashDenomination extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private int seqNo = 0;
	private long movementId = 0;
	private String denomination;
	private int count = 0;
	private BigDecimal amount = BigDecimal.ZERO;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private CashDenomination befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public CashDenomination() {
		super();
	}

	public CashDenomination(long movemetId) {
		super();
		this.setMovementId(movemetId);
	}

	public CashDenomination(String denomination) {
		super();
		this.denomination = denomination;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public CashDenomination getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CashDenomination beforeImage) {
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

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public long getMovementId() {
		return movementId;
	}

	public void setMovementId(long movementId) {
		this.movementId = movementId;
	}
}
