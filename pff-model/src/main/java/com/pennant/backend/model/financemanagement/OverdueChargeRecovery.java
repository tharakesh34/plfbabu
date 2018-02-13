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
 * FileName    		:  OverdueChargeRecovery.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-05-2012    														*
 *                                                                  						*
 * Modified Date    :  11-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-05-2012       Pennant	                 0.1                                            * 
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

import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>OverdueChargeRecovery table</b>.<br>
 * 
 */
public class OverdueChargeRecovery extends AbstractWorkflowEntity {

	private static final long		serialVersionUID			= 128728346836978541L;

	private String					finReference;
	private Date					finODSchdDate;
	private String					finODFor;
	private Date					movementDate;
	private int						seqNo						= 0;
	private int						oDDays						= 0;
	private BigDecimal				finCurODAmt					= BigDecimal.ZERO;
	private BigDecimal				finCurODPri					= BigDecimal.ZERO;
	private BigDecimal				finCurODPft					= BigDecimal.ZERO;
	private String					penaltyType;
	private String					penaltyCalOn;
	private BigDecimal				penaltyAmtPerc				= BigDecimal.ZERO;
	private BigDecimal				penalty						= BigDecimal.ZERO;
	private BigDecimal				maxWaiver					= BigDecimal.ZERO;		//FIXME TO be deleted
	private BigDecimal				waivedAmt					= BigDecimal.ZERO;		//FIXME TO be deleted
	private BigDecimal				penaltyPaid					= BigDecimal.ZERO;		//FIXME TO be deleted
	private BigDecimal				penaltyBal					= BigDecimal.ZERO;		//FIXME TO be deleted

	private boolean					rcdCanDel					= false;

	//Screen Level Maintenance
	private boolean					newRecord					= false;
	private String					lovValue;
	private OverdueChargeRecovery	befImage;

	@XmlTransient
	private LoggedInUser			userDetails;

	private String					finCcy;
	private String					lovDescCustCIF;
	private String					lovDescCustShrtName;
	private Date					lovDescFinStartDate;
	private Date					lovDescMaturityDate;
	private BigDecimal				lovDescFinAmount			= BigDecimal.ZERO;
	private BigDecimal				lovDescCurFinAmt			= BigDecimal.ZERO;
	private BigDecimal				lovDescCurSchPriDue			= BigDecimal.ZERO;
	private BigDecimal				lovDescCurSchPftDue			= BigDecimal.ZERO;
	private BigDecimal				lovDescTotOvrDueChrg		= BigDecimal.ZERO;
	private BigDecimal				lovDescTotOvrDueChrgWaived	= BigDecimal.ZERO;
	private BigDecimal				lovDescTotOvrDueChrgPaid	= BigDecimal.ZERO;
	private BigDecimal				lovDescTotOvrDueChrgBal		= BigDecimal.ZERO;
	private BigDecimal				pendingODC					= BigDecimal.ZERO;
	private BigDecimal				totWaived					= BigDecimal.ZERO;

	public boolean isNew() {
		return isNewRecord();
	}

	public OverdueChargeRecovery() {
		super();
	}

	public OverdueChargeRecovery(String id) {
		super();
		this.setId(id);
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
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getFinODSchdDate() {
		return finODSchdDate;
	}

	public void setFinODSchdDate(Date finODSchdDate) {
		this.finODSchdDate = finODSchdDate;
	}

	public String getFinODFor() {
		return finODFor;
	}

	public void setFinODFor(String finODFor) {
		this.finODFor = finODFor;
	}

	public Date getMovementDate() {
		return movementDate;
	}

	public void setMovementDate(Date movementDate) {
		this.movementDate = movementDate;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public int getODDays() {
		return oDDays;
	}

	public void setODDays(int oDDays) {
		this.oDDays = oDDays;
	}

	public BigDecimal getFinCurODAmt() {
		return finCurODAmt;
	}

	public void setFinCurODAmt(BigDecimal finCurODAmt) {
		this.finCurODAmt = finCurODAmt;
	}

	public BigDecimal getFinCurODPri() {
		return finCurODPri;
	}

	public void setFinCurODPri(BigDecimal finCurODPri) {
		this.finCurODPri = finCurODPri;
	}

	public BigDecimal getFinCurODPft() {
		return finCurODPft;
	}

	public void setFinCurODPft(BigDecimal finCurODPft) {
		this.finCurODPft = finCurODPft;
	}

	public String getPenaltyType() {
		return penaltyType;
	}

	public void setPenaltyType(String penaltyType) {
		this.penaltyType = penaltyType;
	}

	public String getPenaltyCalOn() {
		return penaltyCalOn;
	}

	public void setPenaltyCalOn(String penaltyCalOn) {
		this.penaltyCalOn = penaltyCalOn;
	}

	public BigDecimal getPenaltyAmtPerc() {
		return penaltyAmtPerc;
	}

	public void setPenaltyAmtPerc(BigDecimal penaltyAmtPerc) {
		this.penaltyAmtPerc = penaltyAmtPerc;
	}

	public BigDecimal getPenalty() {
		return penalty;
	}

	public void setPenalty(BigDecimal penalty) {
		this.penalty = penalty;
	}

	public BigDecimal getMaxWaiver() {
		return maxWaiver;
	}

	public void setMaxWaiver(BigDecimal maxWaiver) {
		this.maxWaiver = maxWaiver;
	}

	public BigDecimal getWaivedAmt() {
		return waivedAmt;
	}

	public void setWaivedAmt(BigDecimal waivedAmt) {
		this.waivedAmt = waivedAmt;
	}

	public BigDecimal getPenaltyPaid() {
		return penaltyPaid;
	}

	public void setPenaltyPaid(BigDecimal penaltyPaid) {
		this.penaltyPaid = penaltyPaid;
	}

	public BigDecimal getPenaltyBal() {
		return penaltyBal;
	}

	public void setPenaltyBal(BigDecimal penaltyBal) {
		this.penaltyBal = penaltyBal;
	}

	public boolean isRcdCanDel() {
		return rcdCanDel;
	}

	public void setRcdCanDel(boolean rcdCanDel) {
		this.rcdCanDel = rcdCanDel;
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

	public OverdueChargeRecovery getBefImage() {
		return this.befImage;
	}

	public void setBefImage(OverdueChargeRecovery beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public Date getLovDescFinStartDate() {
		return lovDescFinStartDate;
	}

	public void setLovDescFinStartDate(Date lovDescFinStartDate) {
		this.lovDescFinStartDate = lovDescFinStartDate;
	}

	public Date getLovDescMaturityDate() {
		return lovDescMaturityDate;
	}

	public void setLovDescMaturityDate(Date lovDescMaturityDate) {
		this.lovDescMaturityDate = lovDescMaturityDate;
	}

	public BigDecimal getLovDescFinAmount() {
		return lovDescFinAmount;
	}

	public void setLovDescFinAmount(BigDecimal lovDescFinAmount) {
		this.lovDescFinAmount = lovDescFinAmount;
	}

	public BigDecimal getLovDescCurFinAmt() {
		return lovDescCurFinAmt;
	}

	public void setLovDescCurFinAmt(BigDecimal lovDescCurFinAmt) {
		this.lovDescCurFinAmt = lovDescCurFinAmt;
	}

	public BigDecimal getLovDescCurSchPriDue() {
		return lovDescCurSchPriDue;
	}

	public void setLovDescCurSchPriDue(BigDecimal lovDescCurSchPriDue) {
		this.lovDescCurSchPriDue = lovDescCurSchPriDue;
	}

	public BigDecimal getLovDescCurSchPftDue() {
		return lovDescCurSchPftDue;
	}

	public void setLovDescCurSchPftDue(BigDecimal lovDescCurSchPftDue) {
		this.lovDescCurSchPftDue = lovDescCurSchPftDue;
	}

	public BigDecimal getLovDescTotOvrDueChrg() {
		return lovDescTotOvrDueChrg;
	}

	public void setLovDescTotOvrDueChrg(BigDecimal lovDescTotOvrDueChrg) {
		this.lovDescTotOvrDueChrg = lovDescTotOvrDueChrg;
	}

	public BigDecimal getLovDescTotOvrDueChrgWaived() {
		return lovDescTotOvrDueChrgWaived;
	}

	public void setLovDescTotOvrDueChrgWaived(BigDecimal lovDescTotOvrDueChrgWaived) {
		this.lovDescTotOvrDueChrgWaived = lovDescTotOvrDueChrgWaived;
	}

	public BigDecimal getLovDescTotOvrDueChrgPaid() {
		return lovDescTotOvrDueChrgPaid;
	}

	public void setLovDescTotOvrDueChrgPaid(BigDecimal lovDescTotOvrDueChrgPaid) {
		this.lovDescTotOvrDueChrgPaid = lovDescTotOvrDueChrgPaid;
	}

	public BigDecimal getLovDescTotOvrDueChrgBal() {
		return lovDescTotOvrDueChrgBal;
	}

	public void setLovDescTotOvrDueChrgBal(BigDecimal lovDescTotOvrDueChrgBal) {
		this.lovDescTotOvrDueChrgBal = lovDescTotOvrDueChrgBal;
	}

	public BigDecimal getPendingODC() {
		return pendingODC;
	}

	public void setPendingODC(BigDecimal pendingODC) {
		this.pendingODC = pendingODC;
	}

	public BigDecimal getTotWaived() {
		return totWaived;
	}

	public void setTotWaived(BigDecimal totWaived) {
		this.totWaived = totWaived;
	}

	// Methods added for Bajaj Demo
	// R=Recovery if(rcdCanDel==1), C=Collected if(rcdCanDel<>1)
	public String getFinODCRecoverySts() {
		return isRcdCanDel() ? "R" : "C";
	}

	public BigDecimal getFinODCMaxWaiver() {
		return getMaxWaiver();
	}

	public void setFinODCWaived(BigDecimal waivedAmt) {
		setWaivedAmt(waivedAmt);
	}

	public BigDecimal getFinODCPaid() {
		return getPenaltyPaid();
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}
}
