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
 * FileName    		:  FinanceDisbursement.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinanceDisbursement table</b>.<br>
 *
 */
@XmlType(propOrder = { "disbAccountId", "disbType", "disbDate", "feeChargeAmt", "disbAmount" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceDisbursement extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -5230263039482884873L;

	private String finReference = null;
	@XmlElement
	private Date disbDate = null;
	private int disbSeq;
	private long logKey;
	@XmlElement
	private String disbType;
	private String disbDesc;
	private boolean autoDisb;
	@XmlElement
	private String disbAccountId;
	@XmlElement
	private BigDecimal disbAmount = BigDecimal.ZERO;
	private Date disbReqDate = null;
	@XmlElement
	private BigDecimal feeChargeAmt = BigDecimal.ZERO;
	private BigDecimal insuranceAmt = BigDecimal.ZERO;
	private boolean disbIsActive;
	private String disbStatus;
	private String disbRemarks;
	private boolean quickDisb;

	private long linkedTranId;
	private boolean newRecord = false;
	private String lovValue;
	private FinanceDisbursement befImage;
	private LoggedInUser userDetails;
	private boolean posted = false;
	private long instructionUID = Long.MIN_VALUE;
	private BigDecimal deductFeeDisb = BigDecimal.ZERO;
	private BigDecimal deductInsDisb = BigDecimal.ZERO;
	private transient List<SubventionScheduleDetail> subventionSchedules = null;
	private BigDecimal subventionAmount = BigDecimal.ZERO;

	public boolean isNew() {
		return isNewRecord();
	}

	public FinanceDisbursement() {
		super();
	}

	public FinanceDisbursement(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("posted");

		excludeFields.add("subventionSchedules");
		excludeFields.add("subventionAmount");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return finReference;
	}

	public void setId(String id) {
		this.finReference = id;
	}

	public String getFinReference() {
		return getId();
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getDisbDate() {
		return disbDate;
	}

	public void setDisbDate(Date disbDate) {
		this.disbDate = disbDate;
	}

	public String getDisbAccountId() {
		return disbAccountId;
	}

	public void setDisbAccountId(String disbAccountId) {
		this.disbAccountId = disbAccountId;
	}

	public String getDisbType() {
		return disbType;
	}

	public void setDisbType(String disbType) {
		this.disbType = disbType;
	}

	public boolean isAutoDisb() {
		return autoDisb;
	}

	public void setAutoDisb(boolean autoDisb) {
		this.autoDisb = autoDisb;
	}

	public int getDisbSeq() {
		return disbSeq;
	}

	public void setDisbSeq(int disbSeq) {
		this.disbSeq = disbSeq;
	}

	public String getDisbDesc() {
		return disbDesc;
	}

	public void setDisbDesc(String disbDesc) {
		this.disbDesc = disbDesc;
	}

	public BigDecimal getDisbAmount() {
		return disbAmount;
	}

	public void setDisbAmount(BigDecimal disbAmount) {
		this.disbAmount = disbAmount;
	}

	public Date getDisbReqDate() {
		return disbReqDate;
	}

	public void setDisbReqDate(Date disbReqDate) {
		this.disbReqDate = disbReqDate;
	}

	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
		this.feeChargeAmt = feeChargeAmt;
	}

	public BigDecimal getFeeChargeAmt() {
		return feeChargeAmt;
	}

	public boolean isDisbIsActive() {
		return disbIsActive;
	}

	public void setDisbIsActive(boolean disbIsActive) {
		this.disbIsActive = disbIsActive;
	}

	public String getDisbRemarks() {
		return disbRemarks;
	}

	public void setDisbRemarks(String disbRemarks) {
		this.disbRemarks = disbRemarks;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
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

	public FinanceDisbursement getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinanceDisbursement beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setLogKey(long logKey) {
		this.logKey = logKey;
	}

	public long getLogKey() {
		return logKey;
	}

	public boolean isQuickDisb() {
		return quickDisb;
	}

	public void setQuickDisb(boolean quickDisb) {
		this.quickDisb = quickDisb;
	}

	public BigDecimal getInsuranceAmt() {
		return insuranceAmt;
	}

	public void setInsuranceAmt(BigDecimal insuranceAmt) {
		this.insuranceAmt = insuranceAmt;
	}

	public String getDisbStatus() {
		return disbStatus;
	}

	public void setDisbStatus(String disbStatus) {
		this.disbStatus = disbStatus;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

	public long getInstructionUID() {
		return instructionUID;
	}

	public void setInstructionUID(long instructionUID) {
		this.instructionUID = instructionUID;
	}

	public List<SubventionScheduleDetail> getSubventionSchedules() {
		return subventionSchedules;
	}

	public void setSubventionSchedules(List<SubventionScheduleDetail> subventionSchedules) {
		this.subventionSchedules = subventionSchedules;
	}

	public BigDecimal getSubventionAmount() {
		return subventionAmount;
	}

	public void setSubventionAmount(BigDecimal subventionAmount) {
		this.subventionAmount = subventionAmount;
	}

	public BigDecimal getDeductFeeDisb() {
		return deductFeeDisb;
	}

	public void setDeductFeeDisb(BigDecimal deductFeeDisb) {
		this.deductFeeDisb = deductFeeDisb;
	}

	public BigDecimal getDeductInsDisb() {
		return deductInsDisb;
	}

	public void setDeductInsDisb(BigDecimal deductInsDisb) {
		this.deductInsDisb = deductInsDisb;
	}

}
