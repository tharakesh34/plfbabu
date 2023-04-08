/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinODDetails.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 08-05-2012 * * Modified Date :
 * 08-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.finance;

import java.io.Serializable;
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
public class FinODDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	private long finID;
	private String finReference;
	@XmlElement(name = "odDate")
	private Date finODSchdDate;
	private String finODFor;
	private String finBranch;
	private String finType;
	private long custID;
	private Date finODTillDate;
	@XmlElement(name = "odAmount")
	private BigDecimal finCurODAmt = BigDecimal.ZERO;
	private BigDecimal finCurODPri = BigDecimal.ZERO;
	private BigDecimal finCurODPft = BigDecimal.ZERO;
	private BigDecimal finMaxODAmt = BigDecimal.ZERO;
	private BigDecimal finMaxODPri = BigDecimal.ZERO;
	private BigDecimal finMaxODPft = BigDecimal.ZERO;
	private int graceDays = 0;
	private boolean incGraceDays;
	private int finCurODDays = 0;

	private BigDecimal totWaived = BigDecimal.ZERO;
	@XmlElement(name = "odCharge")
	private BigDecimal totPenaltyAmt = BigDecimal.ZERO;
	@XmlElement(name = "odChargePaid")
	private BigDecimal totPenaltyPaid = BigDecimal.ZERO;
	private BigDecimal totPenaltyBal = BigDecimal.ZERO;

	private BigDecimal priPenalty = BigDecimal.ZERO;
	private BigDecimal pftPenalty = BigDecimal.ZERO;
	private BigDecimal priPenaltyPaid = BigDecimal.ZERO;
	private BigDecimal pftPenaltyPaid = BigDecimal.ZERO;
	private BigDecimal priPenaltyWaived = BigDecimal.ZERO;
	private BigDecimal pftPenaltyWaived = BigDecimal.ZERO;
	private BigDecimal priPenaltyBal = BigDecimal.ZERO;
	private BigDecimal pftPenaltyBal = BigDecimal.ZERO;
	private BigDecimal pastDuePri = BigDecimal.ZERO;
	private BigDecimal actPriPenBal = BigDecimal.ZERO;
	private BigDecimal actPftPenBal = BigDecimal.ZERO;

	@XmlElement(name = "odPft")
	private BigDecimal lPIAmt = BigDecimal.ZERO;
	@XmlElement(name = "odPftPaid")
	private BigDecimal lPIPaid = BigDecimal.ZERO;
	private BigDecimal lPIBal = BigDecimal.ZERO;
	private BigDecimal lPIWaived = BigDecimal.ZERO;

	private Date finLMdfDate;

	// Overdue Penalty Details
	private boolean applyODPenalty;
	private boolean oDIncGrcDays;
	@XmlElement(name = "chargeType")
	private String oDChargeType;
	private int oDGraceDays = 0;
	private String oDChargeCalOn;
	private BigDecimal oDChargeAmtOrPerc = BigDecimal.ZERO;
	private boolean oDAllowWaiver;
	private BigDecimal oDMaxWaiverPerc = BigDecimal.ZERO;
	private BigDecimal oDMinCapAmount = BigDecimal.ZERO;
	private BigDecimal payableAmount = BigDecimal.ZERO;
	private String oDRuleCode;
	private boolean lpCpz = false;
	private BigDecimal lpCpzAmount = BigDecimal.ZERO;
	private BigDecimal lpCurCpzBal = BigDecimal.ZERO;
	private boolean lockODRecalCal;

	private BigDecimal paidNow = BigDecimal.ZERO;
	private BigDecimal waivedNow = BigDecimal.ZERO;

	private BigDecimal curOverdraftTxnChrg = BigDecimal.ZERO;
	private BigDecimal maxOverdraftTxnChrg = BigDecimal.ZERO;
	private Long presentmentID;
	private Date odGrcDate;
	private BigDecimal lppDueAmt = BigDecimal.ZERO;
	private Date lppDueTillDate;
	private BigDecimal lpiDueAmt = BigDecimal.ZERO;
	private Date lpiDueTillDate;
	private BigDecimal odMinAmount = BigDecimal.ZERO;

	/*
	 * These fields used in bulk upload (Ex: EOD) rcdAction = "" No action Required rcdAction = "I" record to be
	 * inserted rcdAction = "U" record to be updated rcdAction = "D" record to be deleted
	 * 
	 */
	private String rcdAction = "";

	public FinODDetails() {
		super();
	}

	public FinODDetails copyEntity() {
		FinODDetails entity = new FinODDetails();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setFinODSchdDate(this.finODSchdDate);
		entity.setFinODFor(this.finODFor);
		entity.setFinBranch(this.finBranch);
		entity.setFinType(this.finType);
		entity.setCustID(this.custID);
		entity.setFinODTillDate(this.finODTillDate);
		entity.setFinCurODAmt(this.finCurODAmt);
		entity.setFinCurODPri(this.finCurODPri);
		entity.setFinCurODPft(this.finCurODPft);
		entity.setFinMaxODAmt(this.finMaxODAmt);
		entity.setFinMaxODPri(this.finMaxODPri);
		entity.setFinMaxODPft(this.finMaxODPft);
		entity.setGraceDays(this.graceDays);
		entity.setIncGraceDays(this.incGraceDays);
		entity.setFinCurODDays(this.finCurODDays);
		entity.setTotWaived(this.totWaived);
		entity.setTotPenaltyAmt(this.totPenaltyAmt);
		entity.setTotPenaltyPaid(this.totPenaltyPaid);
		entity.setTotPenaltyBal(this.totPenaltyBal);
		entity.setLPIAmt(this.lPIAmt);
		entity.setLPIPaid(this.lPIPaid);
		entity.setLPIBal(this.lPIBal);
		entity.setLPIWaived(this.lPIWaived);
		entity.setFinLMdfDate(this.finLMdfDate);
		entity.setApplyODPenalty(this.applyODPenalty);
		entity.setODIncGrcDays(this.oDIncGrcDays);
		entity.setODChargeType(this.oDChargeType);
		entity.setODGraceDays(this.oDGraceDays);
		entity.setODChargeCalOn(this.oDChargeCalOn);
		entity.setODChargeAmtOrPerc(this.oDChargeAmtOrPerc);
		entity.setODAllowWaiver(this.oDAllowWaiver);
		entity.setODMaxWaiverPerc(this.oDMaxWaiverPerc);
		entity.setoDMinCapAmount(this.oDMinCapAmount);
		entity.setPayableAmount(this.payableAmount);
		entity.setODRuleCode(this.oDRuleCode);
		entity.setLpCpz(this.lpCpz);
		entity.setLpCpzAmount(this.lpCpzAmount);
		entity.setLpCurCpzBal(this.lpCurCpzBal);
		entity.setLockODRecalCal(this.lockODRecalCal);
		entity.setPaidNow(this.paidNow);
		entity.setWaivedNow(this.waivedNow);
		entity.setRcdAction(this.rcdAction);
		entity.setPresentmentID(this.presentmentID);
		entity.setCurOverdraftTxnChrg(this.curOverdraftTxnChrg);
		entity.setMaxOverdraftTxnChrg(this.maxOverdraftTxnChrg);
		entity.setPriPenalty(this.priPenalty);
		entity.setPftPenalty(this.pftPenalty);
		entity.setPriPenaltyPaid(this.priPenaltyPaid);
		entity.setPriPenaltyWaived(this.priPenaltyWaived);
		entity.setPftPenaltyPaid(this.pftPenaltyPaid);
		entity.setPftPenaltyWaived(this.pftPenaltyWaived);
		entity.setPriPenaltyBal(this.priPenaltyBal);
		entity.setPftPenaltyBal(this.pftPenaltyBal);
		entity.setActPriPenBal(this.actPriPenBal);
		entity.setActPftPenBal(this.actPftPenBal);
		entity.setPastDuePri(this.pastDuePri);
		entity.setOdGrcDate(this.odGrcDate);
		entity.setLppDueAmt(this.lppDueAmt);
		entity.setLppDueTillDate(this.lppDueTillDate);
		entity.setLpiDueAmt(this.lpiDueAmt);
		entity.setLpiDueTillDate(this.lpiDueTillDate);
		entity.setOdMinAmount(this.odMinAmount);

		return entity;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
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

	public BigDecimal getoDMinCapAmount() {
		return oDMinCapAmount;
	}

	public void setoDMinCapAmount(BigDecimal oDMinCapAmount) {
		this.oDMinCapAmount = oDMinCapAmount;
	}

	public String getODRuleCode() {
		return oDRuleCode;
	}

	public void setODRuleCode(String oDRuleCode) {
		this.oDRuleCode = oDRuleCode;
	}

	public BigDecimal getPayableAmount() {
		return payableAmount;
	}

	public void setPayableAmount(BigDecimal payableAmount) {
		this.payableAmount = payableAmount;
	}

	public boolean isLpCpz() {
		return lpCpz;
	}

	public void setLpCpz(boolean lpCpz) {
		this.lpCpz = lpCpz;
	}

	public BigDecimal getLpCpzAmount() {
		return lpCpzAmount;
	}

	public void setLpCpzAmount(BigDecimal lpCpzAmount) {
		this.lpCpzAmount = lpCpzAmount;
	}

	public BigDecimal getLpCurCpzBal() {
		return lpCurCpzBal;
	}

	public void setLpCurCpzBal(BigDecimal lpCurCpzBal) {
		this.lpCurCpzBal = lpCurCpzBal;
	}

	public boolean isLockODRecalCal() {
		return lockODRecalCal;
	}

	public void setLockODRecalCal(boolean lockODRecalCal) {
		this.lockODRecalCal = lockODRecalCal;
	}

	public BigDecimal getPaidNow() {
		return paidNow;
	}

	public void setPaidNow(BigDecimal paidNow) {
		this.paidNow = paidNow;
	}

	public BigDecimal getWaivedNow() {
		return waivedNow;
	}

	public void setWaivedNow(BigDecimal waivedNow) {
		this.waivedNow = waivedNow;
	}

	public Long getPresentmentID() {
		return presentmentID;
	}

	public void setPresentmentID(Long presentmentID) {
		this.presentmentID = presentmentID;
	}

	public BigDecimal getCurOverdraftTxnChrg() {
		return curOverdraftTxnChrg;
	}

	public void setCurOverdraftTxnChrg(BigDecimal curOverdraftTxnChrg) {
		this.curOverdraftTxnChrg = curOverdraftTxnChrg;
	}

	public BigDecimal getMaxOverdraftTxnChrg() {
		return maxOverdraftTxnChrg;
	}

	public void setMaxOverdraftTxnChrg(BigDecimal maxOverdraftTxnChrg) {
		this.maxOverdraftTxnChrg = maxOverdraftTxnChrg;
	}

	public BigDecimal getPriPenalty() {
		return priPenalty;
	}

	public void setPriPenalty(BigDecimal priPenalty) {
		this.priPenalty = priPenalty;
	}

	public BigDecimal getPftPenalty() {
		return pftPenalty;
	}

	public void setPftPenalty(BigDecimal pftPenalty) {
		this.pftPenalty = pftPenalty;
	}

	public BigDecimal getPftPenaltyPaid() {
		return pftPenaltyPaid;
	}

	public void setPftPenaltyPaid(BigDecimal pftPenaltyPaid) {
		this.pftPenaltyPaid = pftPenaltyPaid;
	}

	public BigDecimal getPriPenaltyWaived() {
		return priPenaltyWaived;
	}

	public void setPriPenaltyWaived(BigDecimal priPenaltyWaived) {
		this.priPenaltyWaived = priPenaltyWaived;
	}

	public BigDecimal getPftPenaltyWaived() {
		return pftPenaltyWaived;
	}

	public void setPftPenaltyWaived(BigDecimal pftPenaltyWaived) {
		this.pftPenaltyWaived = pftPenaltyWaived;
	}

	public BigDecimal getPriPenaltyBal() {
		return priPenaltyBal;
	}

	public void setPriPenaltyBal(BigDecimal priPenaltyBal) {
		this.priPenaltyBal = priPenaltyBal;
	}

	public BigDecimal getPftPenaltyBal() {
		return pftPenaltyBal;
	}

	public void setPftPenaltyBal(BigDecimal pftPenaltyBal) {
		this.pftPenaltyBal = pftPenaltyBal;
	}

	public BigDecimal getPastDuePri() {
		return pastDuePri;
	}

	public void setPastDuePri(BigDecimal pastDuePri) {
		this.pastDuePri = pastDuePri;
	}

	public BigDecimal getPriPenaltyPaid() {
		return priPenaltyPaid;
	}

	public void setPriPenaltyPaid(BigDecimal priPenaltyPaid) {
		this.priPenaltyPaid = priPenaltyPaid;
	}

	public BigDecimal getActPriPenBal() {
		return actPriPenBal;
	}

	public void setActPriPenBal(BigDecimal actPriPenBal) {
		this.actPriPenBal = actPriPenBal;
	}

	public BigDecimal getActPftPenBal() {
		return actPftPenBal;
	}

	public void setActPftPenBal(BigDecimal actPftPenBal) {
		this.actPftPenBal = actPftPenBal;
	}

	public Date getOdGrcDate() {
		return odGrcDate;
	}

	public void setOdGrcDate(Date odGrcDate) {
		this.odGrcDate = odGrcDate;
	}

	public BigDecimal getLppDueAmt() {
		return lppDueAmt;
	}

	public void setLppDueAmt(BigDecimal lppDueAmt) {
		this.lppDueAmt = lppDueAmt;
	}

	public Date getLppDueTillDate() {
		return lppDueTillDate;
	}

	public void setLppDueTillDate(Date lppDueTillDate) {
		this.lppDueTillDate = lppDueTillDate;
	}

	public BigDecimal getLpiDueAmt() {
		return lpiDueAmt;
	}

	public void setLpiDueAmt(BigDecimal lpiDueAmt) {
		this.lpiDueAmt = lpiDueAmt;
	}

	public Date getLpiDueTillDate() {
		return lpiDueTillDate;
	}

	public void setLpiDueTillDate(Date lpiDueTillDate) {
		this.lpiDueTillDate = lpiDueTillDate;
	}

	public BigDecimal getOdMinAmount() {
		return odMinAmount;
	}

	public void setOdMinAmount(BigDecimal odMinAmount) {
		this.odMinAmount = odMinAmount;
	}
}
