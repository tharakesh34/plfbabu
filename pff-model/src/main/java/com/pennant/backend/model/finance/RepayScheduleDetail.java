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
 * * FileName : WIFFinanceScheduleDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RepayScheduleDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private long finID;
	private String finReference = null;
	private Date schDate;
	private Date defSchdDate;
	private String schdFor = "";

	// Profit Amount
	private BigDecimal profitSchd = BigDecimal.ZERO;
	private BigDecimal profitSchdPaid = BigDecimal.ZERO;
	private BigDecimal profitSchdBal = BigDecimal.ZERO;
	private BigDecimal profitSchdPayNow = BigDecimal.ZERO;
	private BigDecimal tdsSchdPayNow = BigDecimal.ZERO;
	private BigDecimal pftSchdWaivedNow = BigDecimal.ZERO;

	// Profit Amount
	private BigDecimal latePftSchd = BigDecimal.ZERO;
	private BigDecimal latePftSchdPaid = BigDecimal.ZERO;
	private BigDecimal latePftSchdBal = BigDecimal.ZERO;
	private BigDecimal latePftSchdPayNow = BigDecimal.ZERO;
	private BigDecimal latePftSchdWaivedNow = BigDecimal.ZERO;

	// Principal Amount
	private BigDecimal principalSchd = BigDecimal.ZERO;
	private BigDecimal principalSchdPaid = BigDecimal.ZERO;
	private BigDecimal principalSchdBal = BigDecimal.ZERO;
	private BigDecimal principalSchdPayNow = BigDecimal.ZERO;
	private BigDecimal priSchdWaivedNow = BigDecimal.ZERO;

	// Scheduled Fee Amount
	private BigDecimal schdFee = BigDecimal.ZERO;
	private BigDecimal schdFeePaid = BigDecimal.ZERO;
	private BigDecimal schdFeeBal = BigDecimal.ZERO;
	private BigDecimal schdFeePayNow = BigDecimal.ZERO;
	private BigDecimal schdFeeWaivedNow = BigDecimal.ZERO;

	private Long lppTaxHeaderId;
	private Long lpiTaxHeaderId;

	private BigDecimal refundMax = BigDecimal.ZERO;
	private BigDecimal refundReq = BigDecimal.ZERO;
	private int daysLate = 0;
	private int daysEarly = 0;
	private boolean allowRefund = false;
	private BigDecimal repayNet = BigDecimal.ZERO;
	private BigDecimal repayBalance = BigDecimal.ZERO;
	private int schdIndex = 0;

	private boolean allowWaiver = false;
	private BigDecimal penaltyAmt = BigDecimal.ZERO;
	private BigDecimal penaltyPayNow = BigDecimal.ZERO;
	private BigDecimal maxWaiver = BigDecimal.ZERO;
	private BigDecimal waivedAmt = BigDecimal.ZERO;

	private String chargeType = "";
	private long linkedTranId = 0;
	private long repayID = 0;// Only setting from Repay Header
	private int repaySchID = 0;
	private Date valueDate;

	// Profit waiver
	private long waiverId;
	private Long taxHeaderId;
	private TaxHeader taxHeader;

	public RepayScheduleDetail() {
	    super();
	}

	public RepayScheduleDetail copyEntity() {
		RepayScheduleDetail entity = new RepayScheduleDetail();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setSchDate(this.schDate);
		entity.setDefSchdDate(this.defSchdDate);
		entity.setSchdFor(this.schdFor);
		entity.setProfitSchd(this.profitSchd);
		entity.setProfitSchdPaid(this.profitSchdPaid);
		entity.setProfitSchdBal(this.profitSchdBal);
		entity.setProfitSchdPayNow(this.profitSchdPayNow);
		entity.setTdsSchdPayNow(this.tdsSchdPayNow);
		entity.setPftSchdWaivedNow(this.pftSchdWaivedNow);
		entity.setLatePftSchd(this.latePftSchd);
		entity.setLatePftSchdPaid(this.latePftSchdPaid);
		entity.setLatePftSchdBal(this.latePftSchdBal);
		entity.setLatePftSchdPayNow(this.latePftSchdPayNow);
		entity.setLatePftSchdWaivedNow(this.latePftSchdWaivedNow);
		entity.setPrincipalSchd(this.principalSchd);
		entity.setPrincipalSchdPaid(this.principalSchdPaid);
		entity.setPrincipalSchdBal(this.principalSchdBal);
		entity.setPrincipalSchdPayNow(this.principalSchdPayNow);
		entity.setPriSchdWaivedNow(this.priSchdWaivedNow);
		entity.setSchdFee(this.schdFee);
		entity.setSchdFeePaid(this.schdFeePaid);
		entity.setSchdFeeBal(this.schdFeeBal);
		entity.setSchdFeePayNow(this.schdFeePayNow);
		entity.setSchdFeeWaivedNow(this.schdFeeWaivedNow);
		entity.setLppTaxHeaderId(this.lppTaxHeaderId);
		entity.setLpiTaxHeaderId(this.lpiTaxHeaderId);
		entity.setRefundMax(this.refundMax);
		entity.setRefundReq(this.refundReq);
		entity.setDaysLate(this.daysLate);
		entity.setDaysEarly(this.daysEarly);
		entity.setAllowRefund(this.allowRefund);
		entity.setRepayNet(this.repayNet);
		entity.setRepayBalance(this.repayBalance);
		entity.setSchdIndex(this.schdIndex);
		entity.setAllowWaiver(this.allowWaiver);
		entity.setPenaltyAmt(this.penaltyAmt);
		entity.setPenaltyPayNow(this.penaltyPayNow);
		entity.setMaxWaiver(this.maxWaiver);
		entity.setWaivedAmt(this.waivedAmt);
		entity.setChargeType(this.chargeType);
		entity.setLinkedTranId(this.linkedTranId);
		entity.setRepayID(this.repayID);
		entity.setRepaySchID(this.repaySchID);
		entity.setValueDate(this.valueDate);
		entity.setWaiverId(this.waiverId);
		entity.setTaxHeaderId(this.taxHeaderId);
		entity.setTaxHeader(this.taxHeader);
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

	public String getSchdFor() {
		return schdFor;
	}

	public void setSchdFor(String schdFor) {
		this.schdFor = schdFor;
	}

	public BigDecimal getProfitSchd() {
		return profitSchd;
	}

	public void setProfitSchd(BigDecimal profitSchd) {
		this.profitSchd = profitSchd;
	}

	public BigDecimal getProfitSchdPaid() {
		return profitSchdPaid;
	}

	public void setProfitSchdPaid(BigDecimal profitSchdPaid) {
		this.profitSchdPaid = profitSchdPaid;
	}

	public BigDecimal getProfitSchdBal() {
		return profitSchdBal;
	}

	public void setProfitSchdBal(BigDecimal profitSchdBal) {
		this.profitSchdBal = profitSchdBal;
	}

	public BigDecimal getProfitSchdPayNow() {
		return profitSchdPayNow;
	}

	public void setProfitSchdPayNow(BigDecimal profitSchdPayNow) {
		this.profitSchdPayNow = profitSchdPayNow;
	}

	public BigDecimal getPrincipalSchd() {
		return principalSchd;
	}

	public void setPrincipalSchd(BigDecimal principalSchd) {
		this.principalSchd = principalSchd;
	}

	public BigDecimal getPrincipalSchdPaid() {
		return principalSchdPaid;
	}

	public void setPrincipalSchdPaid(BigDecimal principalSchdPaid) {
		this.principalSchdPaid = principalSchdPaid;
	}

	public BigDecimal getPrincipalSchdBal() {
		return principalSchdBal;
	}

	public void setPrincipalSchdBal(BigDecimal principalSchdBal) {
		this.principalSchdBal = principalSchdBal;
	}

	public BigDecimal getPrincipalSchdPayNow() {
		return principalSchdPayNow;
	}

	public void setPrincipalSchdPayNow(BigDecimal principalSchdPayNow) {
		this.principalSchdPayNow = principalSchdPayNow;
	}

	public BigDecimal getRefundMax() {
		return refundMax;
	}

	public void setRefundMax(BigDecimal refundMax) {
		this.refundMax = refundMax;
	}

	public BigDecimal getRefundReq() {
		return refundReq;
	}

	public void setRefundReq(BigDecimal refundReq) {
		this.refundReq = refundReq;
	}

	public int getDaysLate() {
		return daysLate;
	}

	public void setDaysLate(int daysLate) {
		this.daysLate = daysLate;
	}

	public int getDaysEarly() {
		return daysEarly;
	}

	public void setDaysEarly(int daysEarly) {
		this.daysEarly = daysEarly;
	}

	public boolean isAllowRefund() {
		return allowRefund;
	}

	public void setAllowRefund(boolean allowRefund) {
		this.allowRefund = allowRefund;
	}

	public BigDecimal getRepayNet() {
		return repayNet;
	}

	public void setRepayNet(BigDecimal repayNet) {
		this.repayNet = repayNet;
	}

	public BigDecimal getRepayBalance() {
		return repayBalance;
	}

	public void setRepayBalance(BigDecimal repayBalance) {
		this.repayBalance = repayBalance;
	}

	public int getSchdIndex() {
		return schdIndex;
	}

	public void setSchdIndex(int schdIndex) {
		this.schdIndex = schdIndex;
	}

	public boolean isAllowWaiver() {
		return allowWaiver;
	}

	public void setAllowWaiver(boolean allowWaiver) {
		this.allowWaiver = allowWaiver;
	}

	public BigDecimal getPenaltyAmt() {
		return penaltyAmt;
	}

	public void setPenaltyAmt(BigDecimal penaltyAmt) {
		this.penaltyAmt = penaltyAmt;
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

	public String getChargeType() {
		return chargeType;
	}

	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public BigDecimal getPenaltyPayNow() {
		return penaltyPayNow;
	}

	public void setPenaltyPayNow(BigDecimal penaltyPayNow) {
		this.penaltyPayNow = penaltyPayNow;
	}

	public BigDecimal getSchdFee() {
		return schdFee;
	}

	public void setSchdFee(BigDecimal schdFee) {
		this.schdFee = schdFee;
	}

	public BigDecimal getSchdFeePaid() {
		return schdFeePaid;
	}

	public void setSchdFeePaid(BigDecimal schdFeePaid) {
		this.schdFeePaid = schdFeePaid;
	}

	public BigDecimal getSchdFeeBal() {
		return schdFeeBal;
	}

	public void setSchdFeeBal(BigDecimal schdFeeBal) {
		this.schdFeeBal = schdFeeBal;
	}

	public BigDecimal getSchdFeePayNow() {
		return schdFeePayNow;
	}

	public void setSchdFeePayNow(BigDecimal schdFeePayNow) {
		this.schdFeePayNow = schdFeePayNow;
	}

	public long getRepayID() {
		return repayID;
	}

	public void setRepayID(long repayID) {
		this.repayID = repayID;
	}

	public BigDecimal getLatePftSchd() {
		return latePftSchd;
	}

	public void setLatePftSchd(BigDecimal latePftSchd) {
		this.latePftSchd = latePftSchd;
	}

	public BigDecimal getLatePftSchdPaid() {
		return latePftSchdPaid;
	}

	public void setLatePftSchdPaid(BigDecimal latePftSchdPaid) {
		this.latePftSchdPaid = latePftSchdPaid;
	}

	public BigDecimal getLatePftSchdBal() {
		return latePftSchdBal;
	}

	public void setLatePftSchdBal(BigDecimal latePftSchdBal) {
		this.latePftSchdBal = latePftSchdBal;
	}

	public BigDecimal getLatePftSchdPayNow() {
		return latePftSchdPayNow;
	}

	public void setLatePftSchdPayNow(BigDecimal latePftSchdPayNow) {
		this.latePftSchdPayNow = latePftSchdPayNow;
	}

	public int getRepaySchID() {
		return repaySchID;
	}

	public void setRepaySchID(int repaySchID) {
		this.repaySchID = repaySchID;
	}

	public BigDecimal getPftSchdWaivedNow() {
		return pftSchdWaivedNow;
	}

	public void setPftSchdWaivedNow(BigDecimal pftSchdWaivedNow) {
		this.pftSchdWaivedNow = pftSchdWaivedNow;
	}

	public BigDecimal getLatePftSchdWaivedNow() {
		return latePftSchdWaivedNow;
	}

	public void setLatePftSchdWaivedNow(BigDecimal latePftSchdWaivedNow) {
		this.latePftSchdWaivedNow = latePftSchdWaivedNow;
	}

	public BigDecimal getPriSchdWaivedNow() {
		return priSchdWaivedNow;
	}

	public void setPriSchdWaivedNow(BigDecimal priSchdWaivedNow) {
		this.priSchdWaivedNow = priSchdWaivedNow;
	}

	public BigDecimal getSchdFeeWaivedNow() {
		return schdFeeWaivedNow;
	}

	public void setSchdFeeWaivedNow(BigDecimal schdFeeWaivedNow) {
		this.schdFeeWaivedNow = schdFeeWaivedNow;
	}

	public BigDecimal getTdsSchdPayNow() {
		return tdsSchdPayNow;
	}

	public void setTdsSchdPayNow(BigDecimal tdsSchdPayNow) {
		this.tdsSchdPayNow = tdsSchdPayNow;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public long getWaiverId() {
		return waiverId;
	}

	public void setWaiverId(long waiverId) {
		this.waiverId = waiverId;
	}

	public Long getTaxHeaderId() {
		return taxHeaderId;
	}

	public void setTaxHeaderId(Long taxHeaderId) {
		this.taxHeaderId = taxHeaderId;
	}

	public TaxHeader getTaxHeader() {
		return taxHeader;
	}

	public void setTaxHeader(TaxHeader taxHeader) {
		this.taxHeader = taxHeader;
	}

	public Long getLppTaxHeaderId() {
		return lppTaxHeaderId;
	}

	public void setLppTaxHeaderId(Long lppTaxHeaderId) {
		this.lppTaxHeaderId = lppTaxHeaderId;
	}

	public Long getLpiTaxHeaderId() {
		return lpiTaxHeaderId;
	}

	public void setLpiTaxHeaderId(Long lpiTaxHeaderId) {
		this.lpiTaxHeaderId = lpiTaxHeaderId;
	}

}