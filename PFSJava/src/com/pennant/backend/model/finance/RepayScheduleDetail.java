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
	private BigDecimal profitSchd = BigDecimal.ZERO;
	private BigDecimal profitSchdPaid = BigDecimal.ZERO;
	private BigDecimal profitSchdBal = BigDecimal.ZERO;
	private BigDecimal profitSchdPayNow = BigDecimal.ZERO;
	private BigDecimal principalSchd = BigDecimal.ZERO;
	private BigDecimal principalSchdPaid = BigDecimal.ZERO;
	private BigDecimal principalSchdBal = BigDecimal.ZERO;
	private BigDecimal principalSchdPayNow = BigDecimal.ZERO;
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
}
