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
 * FileName    		:  FinODDetails.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-05-2012    														*
 *                                                                  						*
 * Modified Date    :  08-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-05-2012       Pennant	                 0.1                                            * 
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Model class for the <b>FinODDetails table</b>.<br>
 *
 */
@XmlType(propOrder = { "finODSchdDate", "finCurODAmt", "totPenaltyAmt", "totPenaltyPaid", "lPIAmt", "lPIPaid",
		"oDChargeType" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinODDetails implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	private String finReference;
	@XmlElement(name="odDate")
	private Date finODSchdDate;
	private String finODFor;
	private String finBranch;
	private String finType;
	private long custID;
	private Date finODTillDate;
	@XmlElement(name="odAmount")
	private BigDecimal finCurODAmt = BigDecimal.ZERO;
	private BigDecimal finCurODPri = BigDecimal.ZERO;
	private BigDecimal finCurODPft = BigDecimal.ZERO;
	private BigDecimal finMaxODAmt = BigDecimal.ZERO;
	private BigDecimal finMaxODPri = BigDecimal.ZERO;
	private BigDecimal finMaxODPft = BigDecimal.ZERO;
	private int graceDays=0;
	private boolean incGraceDays;
	private int finCurODDays=0;
	
	private BigDecimal totWaived = BigDecimal.ZERO;
	@XmlElement(name="odCharge")
	private BigDecimal totPenaltyAmt = BigDecimal.ZERO;
	@XmlElement(name="odChargePaid")
	private BigDecimal totPenaltyPaid = BigDecimal.ZERO;
	private BigDecimal totPenaltyBal = BigDecimal.ZERO;
	
	@XmlElement(name="odPft")
	private BigDecimal lPIAmt = BigDecimal.ZERO;
	@XmlElement(name="odPftPaid")
	private BigDecimal lPIPaid = BigDecimal.ZERO;
	private BigDecimal lPIBal = BigDecimal.ZERO;
	private BigDecimal lPIWaived = BigDecimal.ZERO;
	
	private Date finLMdfDate;
	
	//Overdue Penalty Details
	private boolean applyODPenalty;
	private boolean oDIncGrcDays;
	@XmlElement(name="chargeType")
	private String oDChargeType;
	private int oDGraceDays=0;
	private String oDChargeCalOn;
	private BigDecimal oDChargeAmtOrPerc = BigDecimal.ZERO;
	private boolean oDAllowWaiver;
	private BigDecimal oDMaxWaiverPerc = BigDecimal.ZERO;
	
	/*
	 * These fields used in bulk upload (Ex: EOD)
	 * rcdAction = "" No action Required
	 * rcdAction = "I" record to be inserted
	 * rcdAction = "U" record to be updated
	 * rcdAction = "D" record to be deleted
	 * 
	 */ 
	private String rcdAction = "";
	
	public FinODDetails() {
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return finReference;
	}
	public void setId (String id) {
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
	
	public String getFinBranch() {
    	return finBranch;
    }
	public void setFinBranch(String finBranch) {
    	this.finBranch = finBranch;
    }
	
	public String getFinType() {
    	return finType;
    }
	public void setFinType(String finType) {
    	this.finType = finType;
    }
	
	public long getCustID() {
    	return custID;
    }
	public void setCustID(long custID) {
    	this.custID = custID;
    }
	
	public Date getFinODTillDate() {
    	return finODTillDate;
    }
	public void setFinODTillDate(Date finODTillDate) {
    	this.finODTillDate = finODTillDate;
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
	
	public BigDecimal getFinMaxODAmt() {
    	return finMaxODAmt;
    }
	public void setFinMaxODAmt(BigDecimal finMaxODAmt) {
    	this.finMaxODAmt = finMaxODAmt;
    }
	
	public BigDecimal getFinMaxODPri() {
    	return finMaxODPri;
    }
	public void setFinMaxODPri(BigDecimal finMaxODPri) {
    	this.finMaxODPri = finMaxODPri;
    }
	
	public BigDecimal getFinMaxODPft() {
    	return finMaxODPft;
    }
	public void setFinMaxODPft(BigDecimal finMaxODPft) {
    	this.finMaxODPft = finMaxODPft;
    }
	
	public int getGraceDays() {
    	return graceDays;
    }
	public void setGraceDays(int graceDays) {
    	this.graceDays = graceDays;
    }
	
	public boolean isIncGraceDays() {
    	return incGraceDays;
    }
	public void setIncGraceDays(boolean incGraceDays) {
    	this.incGraceDays = incGraceDays;
    }
	
	public int getFinCurODDays() {
    	return finCurODDays;
    }
	public void setFinCurODDays(int finCurODDays) {
    	this.finCurODDays = finCurODDays;
    }
	
	public BigDecimal getTotPenaltyAmt() {
    	return totPenaltyAmt;
    }
	public void setTotPenaltyAmt(BigDecimal totPenaltyAmt) {
    	this.totPenaltyAmt = totPenaltyAmt;
    }
	
	public BigDecimal getTotWaived() {
    	return totWaived;
    }
	public void setTotWaived(BigDecimal totWaived) {
    	this.totWaived = totWaived;
    }
	
	public BigDecimal getTotPenaltyPaid() {
    	return totPenaltyPaid;
    }
	public void setTotPenaltyPaid(BigDecimal totPenaltyPaid) {
    	this.totPenaltyPaid = totPenaltyPaid;
    }
	
	public BigDecimal getTotPenaltyBal() {
    	return totPenaltyBal;
    }
	public void setTotPenaltyBal(BigDecimal totPenaltyBal) {
    	this.totPenaltyBal = totPenaltyBal;
    }
	
	public Date getFinLMdfDate() {
    	return finLMdfDate;
    }
	public void setFinLMdfDate(Date finLMdfDate) {
    	this.finLMdfDate = finLMdfDate;
    }
	
	public boolean isApplyODPenalty() {
    	return applyODPenalty;
    }
	public void setApplyODPenalty(boolean applyODPenalty) {
    	this.applyODPenalty = applyODPenalty;
    }
	
	public boolean isODIncGrcDays() {
    	return oDIncGrcDays;
    }
	public void setODIncGrcDays(boolean oDIncGrcDays) {
    	this.oDIncGrcDays = oDIncGrcDays;
    }
	
	public String getODChargeType() {
    	return oDChargeType;
    }
	public void setODChargeType(String oDChargeType) {
    	this.oDChargeType = oDChargeType;
    }
	
	public int getODGraceDays() {
    	return oDGraceDays;
    }
	public void setODGraceDays(int oDGraceDays) {
    	this.oDGraceDays = oDGraceDays;
    }
	
	public String getODChargeCalOn() {
    	return oDChargeCalOn;
    }
	public void setODChargeCalOn(String oDChargeCalOn) {
    	this.oDChargeCalOn = oDChargeCalOn;
    }
	
	public BigDecimal getODChargeAmtOrPerc() {
    	return oDChargeAmtOrPerc;
    }
	public void setODChargeAmtOrPerc(BigDecimal oDChargeAmtOrPerc) {
    	this.oDChargeAmtOrPerc = oDChargeAmtOrPerc;
    }
	
	public boolean isODAllowWaiver() {
    	return oDAllowWaiver;
    }
	public void setODAllowWaiver(boolean oDAllowWaiver) {
    	this.oDAllowWaiver = oDAllowWaiver;
    }
	
	public BigDecimal getODMaxWaiverPerc() {
    	return oDMaxWaiverPerc;
    }
	public void setODMaxWaiverPerc(BigDecimal oDMaxWaiverPerc) {
    	this.oDMaxWaiverPerc = oDMaxWaiverPerc;
    }
//	
	public BigDecimal getLPIAmt() {
		return lPIAmt;
	}

	public void setLPIAmt(BigDecimal lPIAmt) {
		this.lPIAmt = lPIAmt;
	}

	public BigDecimal getLPIPaid() {
		return lPIPaid;
	}

	public void setLPIPaid(BigDecimal lPIPaid) {
		this.lPIPaid = lPIPaid;
	}

	public BigDecimal getLPIBal() {
		return lPIBal;
	}

	public void setLPIBal(BigDecimal lPIBal) {
		this.lPIBal = lPIBal;
	}

	public BigDecimal getLPIWaived() {
		return lPIWaived;
	}

	public void setLPIWaived(BigDecimal lPIWaived) {
		this.lPIWaived = lPIWaived;
	}

	public String getRcdAction() {
		return rcdAction;
	}

	public void setRcdAction(String rcdAction) {
		this.rcdAction = rcdAction;
	}

}
