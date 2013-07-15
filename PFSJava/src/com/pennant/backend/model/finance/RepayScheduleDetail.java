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

import java.math.BigDecimal;
import java.util.Date;

public class RepayScheduleDetail implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String finReference = null;
	private Date schDate;
	private Date defSchdDate;
	private String schdFor = "";
	private BigDecimal profitSchd = new BigDecimal(0);
	private BigDecimal profitSchdPaid = new BigDecimal(0);
	private BigDecimal profitSchdBal = new BigDecimal(0);
	private BigDecimal profitSchdPayNow = new BigDecimal(0);
	private BigDecimal principalSchd = new BigDecimal(0);
	private BigDecimal principalSchdPaid = new BigDecimal(0);
	private BigDecimal principalSchdBal = new BigDecimal(0);
	private BigDecimal principalSchdPayNow = new BigDecimal(0);
	private BigDecimal refundMax = new BigDecimal(0);
	private BigDecimal refundReq = new BigDecimal(0);
	private BigDecimal refundDefault = new BigDecimal(0);
	private int dasyLate = 0;
	private int daysEarly = 0;
	private boolean allowRefund = false;
	private BigDecimal repayNet = new BigDecimal(0);
	private BigDecimal repayBalance = new BigDecimal(0);
	private int schdIndex = 0;

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

	public BigDecimal getRefundDefault() {
		return refundDefault;
	}

	public void setRefundDefault(BigDecimal refundDefault) {
		this.refundDefault = refundDefault;
	}

	public int getDasyLate() {
		return dasyLate;
	}

	public void setDasyLate(int dasyLate) {
		this.dasyLate = dasyLate;
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

}
