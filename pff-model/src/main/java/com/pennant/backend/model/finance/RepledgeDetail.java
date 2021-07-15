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
 * FileName    		:  FinanceMain.java                                                 	* 	  
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
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import javax.xml.bind.annotation.XmlTransient;

public class RepledgeDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String finReference;
	private Date valueDate;
	private String promotionCode;
	private long promotionSeqId = 0;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal deductFeeDisb = BigDecimal.ZERO;
	private boolean gdrAvailable = false;
	private String packetNumber;
	private String rackNumber;
	private long howAquired;
	private long whenAquired;
	private String dmaCode;
	private String finPurpose;
	private String finPurposeDesc;
	private String dmaCodeDesc;
	private boolean newRecord;
	private String lovValue;
	private String transactionType;
	private String repledgeRef;
	private BigDecimal receivableAmount = BigDecimal.ZERO;
	private BigDecimal waiverRejected = BigDecimal.ZERO;
	private BigDecimal realizeAdjusted = BigDecimal.ZERO;
	private RepledgeDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private byte[] docImage;
	private BigDecimal eligibleAmt = BigDecimal.ZERO;

	public boolean isNew() {
		return isNewRecord();
	}

	public RepledgeDetail() {
		super();
	}

	public RepledgeDetail(String id) {
		super();
		this.setId(id);
	}

	public RepledgeDetail copyEntity() {
		RepledgeDetail entity = new RepledgeDetail();
		entity.setFinReference(this.finReference);
		entity.setValueDate(this.valueDate);
		entity.setPromotionCode(this.promotionCode);
		entity.setPromotionSeqId(this.promotionSeqId);
		entity.setFinAmount(this.finAmount);
		entity.setDeductFeeDisb(this.deductFeeDisb);
		entity.setGdrAvailable(this.gdrAvailable);
		entity.setPacketNumber(this.packetNumber);
		entity.setRackNumber(this.rackNumber);
		entity.setHowAquired(this.howAquired);
		entity.setWhenAquired(this.whenAquired);
		entity.setDmaCode(this.dmaCode);
		entity.setFinPurpose(this.finPurpose);
		entity.setFinPurposeDesc(this.finPurposeDesc);
		entity.setDmaCodeDesc(this.dmaCodeDesc);
		entity.setNewRecord(this.newRecord);
		entity.setLovValue(this.lovValue);
		entity.setTransactionType(this.transactionType);
		entity.setRepledgeRef(this.repledgeRef);
		entity.setReceivableAmount(this.receivableAmount);
		entity.setWaiverRejected(this.waiverRejected);
		entity.setRealizeAdjusted(this.realizeAdjusted);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setDocImage(this.docImage);
		entity.setEligibleAmt(this.eligibleAmt);
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
		return entity;
	}

	public String getId() {
		return finReference;
	}

	public void setId(String id) {
		this.finReference = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public long getPromotionSeqId() {
		return promotionSeqId;
	}

	public void setPromotionSeqId(long promotionSeqId) {
		this.promotionSeqId = promotionSeqId;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public String getPacketNumber() {
		return packetNumber;
	}

	public void setPacketNumber(String packetNumber) {
		this.packetNumber = packetNumber;
	}

	public String getRackNumber() {
		return rackNumber;
	}

	public void setRackNumber(String rackNumber) {
		this.rackNumber = rackNumber;
	}

	public long getHowAquired() {
		return howAquired;
	}

	public void setHowAquired(long howAquired) {
		this.howAquired = howAquired;
	}

	public long getWhenAquired() {
		return whenAquired;
	}

	public void setWhenAquired(long whenAquired) {
		this.whenAquired = whenAquired;
	}

	public String getDmaCode() {
		return dmaCode;
	}

	public void setDmaCode(String dmaCode) {
		this.dmaCode = dmaCode;
	}

	public String getFinPurpose() {
		return finPurpose;
	}

	public void setFinPurpose(String finPurpose) {
		this.finPurpose = finPurpose;
	}

	public String getFinPurposeDesc() {
		return finPurposeDesc;
	}

	public void setFinPurposeDesc(String finPurposeDesc) {
		this.finPurposeDesc = finPurposeDesc;
	}

	public String getDmaCodeDesc() {
		return dmaCodeDesc;
	}

	public void setDmaCodeDesc(String dmaCodeDesc) {
		this.dmaCodeDesc = dmaCodeDesc;
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

	public RepledgeDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(RepledgeDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public BigDecimal getDeductFeeDisb() {
		return deductFeeDisb;
	}

	public void setDeductFeeDisb(BigDecimal deductFeeDisb) {
		this.deductFeeDisb = deductFeeDisb;
	}

	public boolean isGdrAvailable() {
		return gdrAvailable;
	}

	public void setGdrAvailable(boolean gdrAvailable) {
		this.gdrAvailable = gdrAvailable;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public BigDecimal getReceivableAmount() {
		return receivableAmount;
	}

	public void setReceivableAmount(BigDecimal receivableAmount) {
		this.receivableAmount = receivableAmount;
	}

	public String getRepledgeRef() {
		return repledgeRef;
	}

	public void setRepledgeRef(String repledgeRef) {
		this.repledgeRef = repledgeRef;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public BigDecimal getWaiverRejected() {
		return waiverRejected;
	}

	public void setWaiverRejected(BigDecimal waiverRejected) {
		this.waiverRejected = waiverRejected;
	}

	public BigDecimal getRealizeAdjusted() {
		return realizeAdjusted;
	}

	public void setRealizeAdjusted(BigDecimal realizeAdjusted) {
		this.realizeAdjusted = realizeAdjusted;
	}

	public BigDecimal getEligibleAmt() {
		return eligibleAmt;
	}

	public void setEligibleAmt(BigDecimal eligibleAmt) {
		this.eligibleAmt = eligibleAmt;
	}
}
