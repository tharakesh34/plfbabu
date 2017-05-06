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
 * FileName    		:  PresentmentDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.financemanagement;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class PresentmentDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long presentmentId = Long.MIN_VALUE;
	private String presentmentRef;
	private String finReference;
	private Date schDate;
	private Date defSchdDate;
	private long mandateId;
	private BigDecimal schAmtDue;
	private BigDecimal schPriDue;
	private BigDecimal schPftDue;
	private BigDecimal schFeeDue;
	private BigDecimal schInsDue;
	private BigDecimal schPenaltyDue;
	private BigDecimal advanceAmt;
	private BigDecimal tDSAmount;
	private long excessID;
	private BigDecimal adviseAmt;
	private int excludeReason;
	private BigDecimal presentmentAmt;
	private int emiNo;
	private int schSeq;
	private long bounceID;
	private String status;
	private boolean newRecord = false;
	private String customerName;
	private String finTypeDesc;
	private String mandateType;
	private String mandateStatus;
	private Date mandateExpiryDate;
	private String finCcy;
	private String ecsReturn;
	private long receiptID;

	

	public HashMap<String, Object> getDeclaredFieldValues() {
		
		HashMap<String, Object> presentmentMap = new HashMap<String, Object>();
		
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				// "pr_" Should be in small case only, if we want to change the case we need to update the configuration fields as well.
				presentmentMap.put("pr_" + this.getClass().getDeclaredFields()[i].getName(), this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return presentmentMap;
	}
	
	public String getMandateStatus() {
		return mandateStatus;
	}

	public void setMandateStatus(String mandateStatus) {
		this.mandateStatus = mandateStatus;
	}

	@XmlTransient
	private PresentmentDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	@Override
	public boolean isNew() {
		return false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPresentmentId() {
		return presentmentId;
	}

	public void setPresentmentId(long presentmentId) {
		this.presentmentId = presentmentId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public Date getDefSchdDate() {
		return defSchdDate;
	}

	public void setDefSchdDate(Date defSchdDate) {
		this.defSchdDate = defSchdDate;
	}

	public BigDecimal getSchAmtDue() {
		return schAmtDue;
	}

	public void setSchAmtDue(BigDecimal schAmtDue) {
		this.schAmtDue = schAmtDue;
	}

	public BigDecimal getSchPriDue() {
		return schPriDue;
	}

	public void setSchPriDue(BigDecimal schPriDue) {
		this.schPriDue = schPriDue;
	}

	public BigDecimal getSchPftDue() {
		return schPftDue;
	}

	public void setSchPftDue(BigDecimal schPftDue) {
		this.schPftDue = schPftDue;
	}

	public BigDecimal getSchFeeDue() {
		return schFeeDue;
	}

	public void setSchFeeDue(BigDecimal schFeeDue) {
		this.schFeeDue = schFeeDue;
	}

	public BigDecimal getSchInsDue() {
		return schInsDue;
	}

	public void setSchInsDue(BigDecimal schInsDue) {
		this.schInsDue = schInsDue;
	}

	public BigDecimal getSchPenaltyDue() {
		return schPenaltyDue;
	}

	public void setSchPenaltyDue(BigDecimal schPenaltyDue) {
		this.schPenaltyDue = schPenaltyDue;
	}

	public BigDecimal getAdvanceAmt() {
		return advanceAmt;
	}

	public void setAdvanceAmt(BigDecimal advanceAmt) {
		this.advanceAmt = advanceAmt;
	}

	public BigDecimal getAdviseAmt() {
		return adviseAmt;
	}

	public void setAdviseAmt(BigDecimal adviseAmt) {
		this.adviseAmt = adviseAmt;
	}

	public int getExcludeReason() {
		return excludeReason;
	}

	public void setExcludeReason(int excludeReason) {
		this.excludeReason = excludeReason;
	}

	public BigDecimal getPresentmentAmt() {
		return presentmentAmt;
	}

	public void setPresentmentAmt(BigDecimal presentmentAmt) {
		this.presentmentAmt = presentmentAmt;
	}

	public int getEmiNo() {
		return emiNo;
	}

	public void setEmiNo(int emiNo) {
		this.emiNo = emiNo;
	}

	public int getSchSeq() {
		return schSeq;
	}

	public void setSchSeq(int schSeq) {
		this.schSeq = schSeq;
	}

	public long getBounceID() {
		return bounceID;
	}

	public void setBounceID(long bounceID) {
		this.bounceID = bounceID;
	}

	public BigDecimal gettDSAmount() {
		return tDSAmount;
	}

	public void settDSAmount(BigDecimal tDSAmount) {
		this.tDSAmount = tDSAmount;
	}

	public long getExcessID() {
		return excessID;
	}

	public void setExcessID(long excessID) {
		this.excessID = excessID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public PresentmentDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(PresentmentDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getMandateType() {
		return mandateType;
	}

	public void setMandateType(String mandateType) {
		this.mandateType = mandateType;
	}

	public Date getMandateExpiryDate() {
		return mandateExpiryDate;
	}

	public void setMandateExpiryDate(Date mandateExpiryDate) {
		this.mandateExpiryDate = mandateExpiryDate;
	}

	public long getMandateId() {
		return mandateId;
	}

	public void setMandateId(long mandateId) {
		this.mandateId = mandateId;
	}

	public String getPresentmentRef() {
		return presentmentRef;
	}

	public void setPresentmentRef(String presentmentRef) {
		this.presentmentRef = presentmentRef;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getEcsReturn() {
		return ecsReturn;
	}

	public void setEcsReturn(String ecsReturn) {
		this.ecsReturn = ecsReturn;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

}
