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
 * FileName    		:  BranchCashLimit.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-01-2018    														*
 *                                                                  						*
 * Modified Date    :  29-01-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-01-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.cashmanagement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>BranchCashLimit table</b>.<br>
 *
 */
public class BranchCashLimit extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String branchCode;
	private String branchCodeName;
	private Date curLimitSetDate;
	private BigDecimal reOrderLimit;
	private BigDecimal cashLimit;
	private BigDecimal adHocCashLimit;
	private String remarks;
	private Date previousDate;
	private BigDecimal previousAmount = BigDecimal.ZERO;
	private String lovValue;
	private BranchCashLimit befImage;
	private LoggedInUser userDetails;
	private BranchCashDetail branchCashDetail = null;

	private BigDecimal branchCash;
	private BigDecimal adhocInitiationAmount;
	private BigDecimal adhocProcessingAmount;
	private BigDecimal adhocTransitAmount;
	private BigDecimal autoProcessingAmount;
	private BigDecimal autoTransitAmount;
	private BigDecimal reservedAmount;
	private Date lastEODDate;

	public boolean isNew() {
		return isNewRecord();
	}

	public BranchCashLimit() {
		super();
	}

	public BranchCashLimit(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("branchCodeName");
		excludeFields.add("branchCashDetail");
		excludeFields.add("branchCash");
		excludeFields.add("adhocInitiationAmount");
		excludeFields.add("adhocProcessingAmount");
		excludeFields.add("adhocTransitAmount");
		excludeFields.add("autoProcessingAmount");
		excludeFields.add("autoTransitAmount");
		excludeFields.add("reservedAmount");
		excludeFields.add("lastEODDate");

		return excludeFields;
	}

	public String getId() {
		return branchCode;
	}

	public void setId(String id) {
		this.branchCode = id;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchCodeName() {
		return this.branchCodeName;
	}

	public void setBranchCodeName(String branchCodeName) {
		this.branchCodeName = branchCodeName;
	}

	public Date getCurLimitSetDate() {
		return curLimitSetDate;
	}

	public void setCurLimitSetDate(Date curLimitSetDate) {
		this.curLimitSetDate = curLimitSetDate;
	}

	public BigDecimal getReOrderLimit() {
		return reOrderLimit;
	}

	public void setReOrderLimit(BigDecimal reOrderLimit) {
		this.reOrderLimit = reOrderLimit;
	}

	public BigDecimal getCashLimit() {
		return cashLimit;
	}

	public void setCashLimit(BigDecimal cashLimit) {
		this.cashLimit = cashLimit;
	}

	public BigDecimal getAdHocCashLimit() {
		return adHocCashLimit;
	}

	public void setAdHocCashLimit(BigDecimal adHocCashLimit) {
		this.adHocCashLimit = adHocCashLimit;
	}

	public String getRemarks() {
		return remarks;
	}

	public Date getPreviousDate() {
		return previousDate;
	}

	public void setPreviousDate(Date previousDate) {
		this.previousDate = previousDate;
	}

	public BigDecimal getPreviousAmount() {
		return previousAmount;
	}

	public void setPreviousAmount(BigDecimal previousAmount) {
		this.previousAmount = previousAmount;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public BranchCashLimit getBefImage() {
		return this.befImage;
	}

	public void setBefImage(BranchCashLimit beforeImage) {
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

	public BranchCashDetail getBranchCashDetail() {
		return branchCashDetail;
	}

	public void setBranchCashDetail(BranchCashDetail branchCashDetail) {
		this.branchCashDetail = branchCashDetail;
	}

	public BigDecimal getBranchCash() {

		if (branchCash == null) {
			branchCash = BigDecimal.ZERO;
		}
		return branchCash;
	}

	public void setBranchCash(BigDecimal branchCash) {
		this.branchCash = branchCash;
	}

	public BigDecimal getAdhocInitiationAmount() {
		if (adhocInitiationAmount == null) {
			adhocInitiationAmount = BigDecimal.ZERO;
		}
		return adhocInitiationAmount;
	}

	public void setAdhocInitiationAmount(BigDecimal adhocInitiationAmount) {
		this.adhocInitiationAmount = adhocInitiationAmount;
	}

	public BigDecimal getAdhocProcessingAmount() {
		if (adhocProcessingAmount == null) {
			adhocProcessingAmount = BigDecimal.ZERO;
		}
		return adhocProcessingAmount;
	}

	public void setAdhocProcessingAmount(BigDecimal adhocProcessingAmount) {
		this.adhocProcessingAmount = adhocProcessingAmount;
	}

	public BigDecimal getAdhocTransitAmount() {
		if (adhocTransitAmount == null) {
			adhocTransitAmount = BigDecimal.ZERO;
		}
		return adhocTransitAmount;
	}

	public void setAdhocTransitAmount(BigDecimal adhocTransitAmount) {
		this.adhocTransitAmount = adhocTransitAmount;
	}

	public BigDecimal getAutoProcessingAmount() {
		if (autoProcessingAmount == null) {
			autoProcessingAmount = BigDecimal.ZERO;
		}

		return autoProcessingAmount;
	}

	public void setAutoProcessingAmount(BigDecimal autoProcessingAmount) {
		this.autoProcessingAmount = autoProcessingAmount;
	}

	public BigDecimal getAutoTransitAmount() {
		if (autoTransitAmount == null) {
			autoTransitAmount = BigDecimal.ZERO;
		}
		return autoTransitAmount;
	}

	public void setAutoTransitAmount(BigDecimal autoTransitAmount) {
		this.autoTransitAmount = autoTransitAmount;
	}

	public BigDecimal getReservedAmount() {
		if (reservedAmount == null) {
			reservedAmount = BigDecimal.ZERO;
		}
		return reservedAmount;
	}

	public void setReservedAmount(BigDecimal reservedAmount) {
		this.reservedAmount = reservedAmount;
	}

	public Date getLastEODDate() {
		return lastEODDate;
	}

	public void setLastEODDate(Date lastEODDate) {
		this.lastEODDate = lastEODDate;
	}
}
