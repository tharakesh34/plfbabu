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

public class RepayScheduleDetail {

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
	
	// Insurance Amount
	private BigDecimal schdIns = BigDecimal.ZERO;
	private BigDecimal schdInsPaid = BigDecimal.ZERO;
	private BigDecimal schdInsBal = BigDecimal.ZERO;
	private BigDecimal schdInsPayNow = BigDecimal.ZERO;
	private BigDecimal schdInsWaivedNow = BigDecimal.ZERO;
	
	// Supplementary Rent
	private BigDecimal schdSuplRent = BigDecimal.ZERO;
	private BigDecimal schdSuplRentPaid = BigDecimal.ZERO;
	private BigDecimal schdSuplRentBal = BigDecimal.ZERO;
	private BigDecimal schdSuplRentPayNow = BigDecimal.ZERO;
	private BigDecimal schdSuplRentWaivedNow = BigDecimal.ZERO;
	
	// Increased Cost Amount
	private BigDecimal schdIncrCost = BigDecimal.ZERO;
	private BigDecimal schdIncrCostPaid = BigDecimal.ZERO;
	private BigDecimal schdIncrCostBal = BigDecimal.ZERO;
	private BigDecimal schdIncrCostPayNow = BigDecimal.ZERO;
	private BigDecimal schdIncrCostWaivedNow = BigDecimal.ZERO;
	
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

	private BigDecimal paidPenaltyCGST = BigDecimal.ZERO;
	private BigDecimal paidPenaltySGST = BigDecimal.ZERO;
	private BigDecimal paidPenaltyUGST = BigDecimal.ZERO;
	private BigDecimal paidPenaltyIGST = BigDecimal.ZERO;
	
	private String chargeType = "";
	private long linkedTranId = 0;
	private long repayID = 0;// Only setting from Repay Header
	private int repaySchID = 0;

	
	public RepayScheduleDetail() {
		
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

	public BigDecimal getSchdIns() {
		return schdIns;
	}

	public void setSchdIns(BigDecimal schdIns) {
		this.schdIns = schdIns;
	}

	public BigDecimal getSchdInsPaid() {
		return schdInsPaid;
	}

	public void setSchdInsPaid(BigDecimal schdInsPaid) {
		this.schdInsPaid = schdInsPaid;
	}

	public BigDecimal getSchdInsBal() {
		return schdInsBal;
	}

	public void setSchdInsBal(BigDecimal schdInsBal) {
		this.schdInsBal = schdInsBal;
	}

	public BigDecimal getSchdInsPayNow() {
		return schdInsPayNow;
	}

	public void setSchdInsPayNow(BigDecimal schdInsPayNow) {
		this.schdInsPayNow = schdInsPayNow;
	}

	public BigDecimal getSchdSuplRent() {
		return schdSuplRent;
	}

	public void setSchdSuplRent(BigDecimal schdSuplRent) {
		this.schdSuplRent = schdSuplRent;
	}

	public BigDecimal getSchdSuplRentPaid() {
		return schdSuplRentPaid;
	}

	public void setSchdSuplRentPaid(BigDecimal schdSuplRentPaid) {
		this.schdSuplRentPaid = schdSuplRentPaid;
	}

	public BigDecimal getSchdSuplRentBal() {
		return schdSuplRentBal;
	}

	public void setSchdSuplRentBal(BigDecimal schdSuplRentBal) {
		this.schdSuplRentBal = schdSuplRentBal;
	}

	public BigDecimal getSchdSuplRentPayNow() {
		return schdSuplRentPayNow;
	}

	public void setSchdSuplRentPayNow(BigDecimal schdSuplRentPayNow) {
		this.schdSuplRentPayNow = schdSuplRentPayNow;
	}

	public BigDecimal getSchdIncrCost() {
		return schdIncrCost;
	}

	public void setSchdIncrCost(BigDecimal schdIncrCost) {
		this.schdIncrCost = schdIncrCost;
	}

	public BigDecimal getSchdIncrCostPaid() {
		return schdIncrCostPaid;
	}

	public void setSchdIncrCostPaid(BigDecimal schdIncrCostPaid) {
		this.schdIncrCostPaid = schdIncrCostPaid;
	}

	public BigDecimal getSchdIncrCostBal() {
		return schdIncrCostBal;
	}

	public void setSchdIncrCostBal(BigDecimal schdIncrCostBal) {
		this.schdIncrCostBal = schdIncrCostBal;
	}

	public BigDecimal getSchdIncrCostPayNow() {
		return schdIncrCostPayNow;
	}

	public void setSchdIncrCostPayNow(BigDecimal schdIncrCostPayNow) {
		this.schdIncrCostPayNow = schdIncrCostPayNow;
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

	public BigDecimal getSchdInsWaivedNow() {
		return schdInsWaivedNow;
	}
	public void setSchdInsWaivedNow(BigDecimal schdInsWaivedNow) {
		this.schdInsWaivedNow = schdInsWaivedNow;
	}

	public BigDecimal getSchdSuplRentWaivedNow() {
		return schdSuplRentWaivedNow;
	}
	public void setSchdSuplRentWaivedNow(BigDecimal schdSuplRentWaivedNow) {
		this.schdSuplRentWaivedNow = schdSuplRentWaivedNow;
	}

	public BigDecimal getSchdIncrCostWaivedNow() {
		return schdIncrCostWaivedNow;
	}
	public void setSchdIncrCostWaivedNow(BigDecimal schdIncrCostWaivedNow) {
		this.schdIncrCostWaivedNow = schdIncrCostWaivedNow;
	}

	public BigDecimal getTdsSchdPayNow() {
		return tdsSchdPayNow;
	}

	public void setTdsSchdPayNow(BigDecimal tdsSchdPayNow) {
		this.tdsSchdPayNow = tdsSchdPayNow;
	}

	public BigDecimal getPaidPenaltyCGST() {
		return paidPenaltyCGST;
	}

	public void setPaidPenaltyCGST(BigDecimal paidPenaltyCGST) {
		this.paidPenaltyCGST = paidPenaltyCGST;
	}

	public BigDecimal getPaidPenaltySGST() {
		return paidPenaltySGST;
	}

	public void setPaidPenaltySGST(BigDecimal paidPenaltySGST) {
		this.paidPenaltySGST = paidPenaltySGST;
	}

	public BigDecimal getPaidPenaltyUGST() {
		return paidPenaltyUGST;
	}

	public void setPaidPenaltyUGST(BigDecimal paidPenaltyUGST) {
		this.paidPenaltyUGST = paidPenaltyUGST;
	}

	public BigDecimal getPaidPenaltyIGST() {
		return paidPenaltyIGST;
	}

	public void setPaidPenaltyIGST(BigDecimal paidPenaltyIGST) {
		this.paidPenaltyIGST = paidPenaltyIGST;
	}
	
}
