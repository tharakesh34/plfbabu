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
 * FileName    		:  FeeWaiverDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-11-2017   														*
 *                                                                  						*
 * Modified Date    :      														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-11-2017        Pennant	                 0.1                                            * 
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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>FeeWaiverDetails table</b>.<br>
 *
 */

public class FeeWaiverDetail extends AbstractWorkflowEntity implements Entity {

	private static final long	serialVersionUID	= -6234931333270161797L;

	private long				waiverDetailId		= Long.MIN_VALUE;
	private long				waiverId			= Long.MIN_VALUE;
	private long				adviseId			= Long.MIN_VALUE;
	private Date				finODSchdDate;
	private String				feeTypeCode;
	private String				feeTypeDesc;
	private BigDecimal			receivableAmount	= BigDecimal.ZERO;
	private BigDecimal			receivedAmount		= BigDecimal.ZERO;
	private BigDecimal			WaivedAmount		= BigDecimal.ZERO;
	private BigDecimal			balanceAmount		= BigDecimal.ZERO;
	private BigDecimal			currWaiverAmount	= BigDecimal.ZERO;
	private boolean				newRecord			= false;
	private String				lovValue;
	private FeeWaiverDetail		befImage;
	private LoggedInUser		userDetails;

	public FeeWaiverDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("feeTypeDesc");
		excludeFields.add("feeTypeCode");
		excludeFields.add("netBalance");

		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	

	public long getWaiverId() {
		return waiverId;
	}

	public long getWaiverDetailId() {
		return waiverDetailId;
	}

	public void setWaiverDetailId(long waiverDetailId) {
		this.waiverDetailId = waiverDetailId;
	}

	public void setWaiverId(long waiverId) {
		this.waiverId = waiverId;
	}

	public long getAdviseId() {
		return adviseId;
	}

	public void setAdviseId(long adviseId) {
		this.adviseId = adviseId;
	}

	public Date getFinODSchdDate() {
		return finODSchdDate;
	}

	public void setFinODSchdDate(Date finODSchdDate) {
		this.finODSchdDate = finODSchdDate;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getFeeTypeDesc() {
		return feeTypeDesc;
	}

	public void setFeeTypeDesc(String feeTypeDesc) {
		this.feeTypeDesc = feeTypeDesc;
	}

	public BigDecimal getReceivableAmount() {
		return receivableAmount;
	}

	public void setReceivableAmount(BigDecimal receivableAmount) {
		this.receivableAmount = receivableAmount;
	}

	public BigDecimal getReceivedAmount() {
		return receivedAmount;
	}

	public void setReceivedAmount(BigDecimal receivedAmount) {
		this.receivedAmount = receivedAmount;
	}

	public BigDecimal getWaivedAmount() {
		return WaivedAmount;
	}

	public void setWaivedAmount(BigDecimal WaivedAmount) {
		this.WaivedAmount = WaivedAmount;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public BigDecimal getCurrWaiverAmount() {
		return currWaiverAmount;
	}

	public void setCurrWaiverAmount(BigDecimal currWaiverAmount) {
		this.currWaiverAmount = currWaiverAmount;
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

	public FeeWaiverDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(FeeWaiverDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	@Override
	public long getId() {
		return this.waiverDetailId;
	}

	@Override
	public void setId(long id) {
		this.waiverDetailId = id;
	}

}
