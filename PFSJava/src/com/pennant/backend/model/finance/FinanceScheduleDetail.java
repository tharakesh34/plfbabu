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
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>WIFFinanceScheduleDetail table</b>.<br>
 * 
 */
public class FinanceScheduleDetail implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String finReference = null;
	private int schSeq;
	private Date schDate;
	private Date defSchdDate;
	private long logKey;
	private boolean pftOnSchDate = false;
	private boolean cpzOnSchDate = false;
	private boolean repayOnSchDate = false;
	private boolean rvwOnSchDate = false;
	private boolean disbOnSchDate = false;
	private boolean defered = false;
	private boolean deferedPay = false;
	private boolean downpaymentOnSchDate = false;
	private BigDecimal balanceForPftCal = BigDecimal.ZERO;
	private String baseRate = "";
	private String splRate = "";
	private BigDecimal mrgRate = BigDecimal.ZERO;
	private BigDecimal actRate = BigDecimal.ZERO;
	private BigDecimal calculatedRate = BigDecimal.ZERO;
	private int noOfDays;
	private boolean calOnIndRate = false;
	private BigDecimal dayFactor = BigDecimal.ZERO;
	private BigDecimal profitCalc = BigDecimal.ZERO;
	private BigDecimal profitSchd = BigDecimal.ZERO;
	private BigDecimal principalSchd = BigDecimal.ZERO;
	private BigDecimal repayAmount = BigDecimal.ZERO;
	private BigDecimal profitBalance = BigDecimal.ZERO;
	private BigDecimal disbAmount = BigDecimal.ZERO;
	private BigDecimal downPaymentAmount = BigDecimal.ZERO;
	private BigDecimal feeChargeAmt = BigDecimal.ZERO;
	private BigDecimal refundOrWaiver = BigDecimal.ZERO;
	private BigDecimal cpzAmount = BigDecimal.ZERO;
	private BigDecimal defRepaySchd = BigDecimal.ZERO;
	private BigDecimal defProfitSchd = BigDecimal.ZERO;
	private BigDecimal defPrincipalSchd = BigDecimal.ZERO;
	private BigDecimal closingBalance = BigDecimal.ZERO;
	private BigDecimal profitFraction = BigDecimal.ZERO;
	private BigDecimal prvRepayAmount = BigDecimal.ZERO;
	private BigDecimal defProfitBal = BigDecimal.ZERO;
	private BigDecimal defPrincipalBal = BigDecimal.ZERO;
	private BigDecimal schdPftPaid = BigDecimal.ZERO;
	private BigDecimal schdPriPaid = BigDecimal.ZERO;
	private boolean schPftPaid = false;
	private boolean schPriPaid = false;
	private BigDecimal defProfit = BigDecimal.ZERO;
	private BigDecimal defPrincipal = BigDecimal.ZERO;
	private BigDecimal defSchdPftPaid = BigDecimal.ZERO;
	private BigDecimal defSchdPriPaid = BigDecimal.ZERO;
	private boolean defSchPftPaid = false;
	private boolean defSchPriPaid = false;
	private String schdMethod = null;
	private String specifier;
	private BigDecimal earlyPaid = BigDecimal.ZERO;
	private BigDecimal earlyPaidBal = BigDecimal.ZERO;
	private BigDecimal writeoffPrincipal = BigDecimal.ZERO;
	private BigDecimal writeoffProfit = BigDecimal.ZERO;
	
	private BigDecimal orgPft = BigDecimal.ZERO;
	private BigDecimal orgPri = BigDecimal.ZERO;
	private BigDecimal orgEndBal = BigDecimal.ZERO;
	private BigDecimal orgPlanPft = BigDecimal.ZERO;
	
	//Fee Details on Schedule Basis
	private BigDecimal feeSchd = BigDecimal.ZERO;
	private BigDecimal schdFeePaid = BigDecimal.ZERO;
	private BigDecimal schdFeeOS = BigDecimal.ZERO;
	private BigDecimal takafulFeeSchd = BigDecimal.ZERO;
	private BigDecimal schdTakafulFeePaid = BigDecimal.ZERO;
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private FinanceScheduleDetail befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	
	private boolean repayComplete = false;
	private ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();

	public FinanceScheduleDetail(Date schDate, boolean repayOnSchDate, BigDecimal actRate) {
		this.schDate = schDate;
		this.repayOnSchDate = repayOnSchDate;
		this.actRate = actRate;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public FinanceScheduleDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("WIFFinanceScheduleDetail");
	}

	public FinanceScheduleDetail(String id) {
		this.setId(id);
	}

	// Getter and Setter methods

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

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = DateUtility.getDate(DateUtility.formatUtilDate(schDate,
		        PennantConstants.dateFormat));
	}

	public int getSchSeq() {
		return schSeq;
	}

	public void setSchSeq(int schSeq) {
		this.schSeq = schSeq;
	}

	public boolean isPftOnSchDate() {
		return pftOnSchDate;
	}

	public void setPftOnSchDate(boolean pftOnSchDate) {
		this.pftOnSchDate = pftOnSchDate;
	}

	public boolean isCpzOnSchDate() {
		return cpzOnSchDate;
	}

	public void setCpzOnSchDate(boolean cpzOnSchDate) {
		this.cpzOnSchDate = cpzOnSchDate;
	}

	public boolean isRepayOnSchDate() {
		return repayOnSchDate;
	}

	public void setRepayOnSchDate(boolean repayOnSchDate) {
		this.repayOnSchDate = repayOnSchDate;
	}

	public boolean isRvwOnSchDate() {
		return rvwOnSchDate;
	}

	public void setRvwOnSchDate(boolean rvwOnSchDate) {
		this.rvwOnSchDate = rvwOnSchDate;
	}

	public boolean isDisbOnSchDate() {
		return disbOnSchDate;
	}

	public void setDisbOnSchDate(boolean disbOnSchDate) {
		this.disbOnSchDate = disbOnSchDate;
	}

	public boolean isDownpaymentOnSchDate() {
		return downpaymentOnSchDate;
	}

	public void setDownpaymentOnSchDate(boolean downpaymentOnSchDate) {
		this.downpaymentOnSchDate = downpaymentOnSchDate;
	}

	public BigDecimal getBalanceForPftCal() {
		return balanceForPftCal;
	}

	public void setBalanceForPftCal(BigDecimal balanceForPftCal) {
		this.balanceForPftCal = balanceForPftCal;
	}

	public String getBaseRate() {
		return baseRate;
	}

	public void setBaseRate(String baseRate) {
		this.baseRate = baseRate;
	}

	public String getSplRate() {
		return splRate;
	}

	public void setSplRate(String splRate) {
		this.splRate = splRate;
	}

	public BigDecimal getMrgRate() {
		return mrgRate;
	}

	public void setMrgRate(BigDecimal mrgRate) {
		this.mrgRate = mrgRate;
	}

	public BigDecimal getActRate() {
		return actRate;
	}

	public void setActRate(BigDecimal actRate) {
		this.actRate = actRate;
	}
	
	public BigDecimal getCalculatedRate() {
    	return calculatedRate;
    }
	public void setCalculatedRate(BigDecimal calculatedRate) {
    	this.calculatedRate = calculatedRate;
    }

	public int getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(int noOfDays) {
		this.noOfDays = noOfDays;
	}

	public boolean isCalOnIndRate() {
		return calOnIndRate;
	}

	public void setCalOnIndRate(boolean calOnIndRate) {
		this.calOnIndRate = calOnIndRate;
	}

	public BigDecimal getDayFactor() {
		return dayFactor;
	}

	public void setDayFactor(BigDecimal dayFactor) {
		this.dayFactor = dayFactor.setScale(9, RoundingMode.DOWN);
	}

	public BigDecimal getProfitCalc() {
		return profitCalc;
	}

	public void setProfitCalc(BigDecimal profitCalc) {
		this.profitCalc = profitCalc;
	}

	public BigDecimal getProfitSchd() {
		return profitSchd;
	}

	public void setProfitSchd(BigDecimal profitSchd) {
		this.profitSchd = profitSchd;
	}

	public BigDecimal getPrincipalSchd() {
		return principalSchd;
	}

	public void setPrincipalSchd(BigDecimal principalSchd) {
		this.principalSchd = principalSchd;
	}

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	public BigDecimal getProfitBalance() {
		return profitBalance;
	}

	public void setProfitBalance(BigDecimal profitBalance) {
		this.profitBalance = profitBalance;
	}

	public BigDecimal getDisbAmount() {
		return disbAmount;
	}

	public void setDisbAmount(BigDecimal disbAmount) {
		this.disbAmount = disbAmount;
	}

	public BigDecimal getDownPaymentAmount() {
		return downPaymentAmount;
	}

	public void setDownPaymentAmount(BigDecimal downPaymentAmount) {
		this.downPaymentAmount = downPaymentAmount;
	}

	public BigDecimal getCpzAmount() {
		return cpzAmount;
	}

	public void setCpzAmount(BigDecimal cpzAmount) {
		this.cpzAmount = cpzAmount;
	}

	public BigDecimal getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}

	public BigDecimal getProfitFraction() {
		return profitFraction;
	}

	public void setProfitFraction(BigDecimal profitFraction) {
		this.profitFraction = profitFraction.setScale(9, RoundingMode.DOWN);
		;
	}

	public BigDecimal getPrvRepayAmount() {
		return prvRepayAmount;
	}

	public void setPrvRepayAmount(BigDecimal prvRepayAmount) {
		this.prvRepayAmount = prvRepayAmount;
	}

	public BigDecimal getSchdPriPaid() {
		return schdPriPaid;
	}

	public void setSchdPriPaid(BigDecimal schdPriPaid) {
		this.schdPriPaid = schdPriPaid;
	}

	/**
	 * @return the defSchdDate
	 */
	public Date getDefSchdDate() {
		return defSchdDate;
	}

	/**
	 * @param defSchdDate
	 *            the defSchdDate to set
	 */
	public void setDefSchdDate(Date defSchdDate) {
		this.defSchdDate = DateUtility.getDate(DateUtility.formatUtilDate(defSchdDate,
		        PennantConstants.dateFormat));
	}

	/**
	 * @return the defered
	 */
	public boolean isDefered() {
		return defered;
	}

	/**
	 * @param defered
	 *            the defered to set
	 */
	public void setDefered(boolean defered) {
		this.defered = defered;
	}

	/**
	 * @return the deferedPay
	 */
	public boolean isDeferedPay() {
		return deferedPay;
	}

	/**
	 * @param deferedPay
	 *            the deferedPay to set
	 */
	public void setDeferedPay(boolean deferedPay) {
		this.deferedPay = deferedPay;
	}

	/**
	 * @return the defRepaySchd
	 */
	public BigDecimal getDefRepaySchd() {
		return defRepaySchd;
	}

	/**
	 * @param defRepaySchd
	 *            the defRepaySchd to set
	 */
	public void setDefRepaySchd(BigDecimal defRepaySchd) {
		this.defRepaySchd = defRepaySchd;
	}

	/**
	 * @return the defProfitSchd
	 */
	public BigDecimal getDefProfitSchd() {
		return defProfitSchd;
	}

	/**
	 * @param defProfitSchd
	 *            the defProfitSchd to set
	 */
	public void setDefProfitSchd(BigDecimal defProfitSchd) {
		this.defProfitSchd = defProfitSchd;
	}

	/**
	 * @return the defPrincipalSchd
	 */
	public BigDecimal getDefPrincipalSchd() {
		return defPrincipalSchd;
	}

	/**
	 * @param defPrincipalSchd
	 *            the defPrincipalSchd to set
	 */
	public void setDefPrincipalSchd(BigDecimal defPrincipalSchd) {
		this.defPrincipalSchd = defPrincipalSchd;
	}

	/**
	 * @return the defProfitBal
	 */
	public BigDecimal getDefProfitBal() {
		return defProfitBal;
	}

	/**
	 * @param defProfitBal
	 *            the defProfitBal to set
	 */
	public void setDefProfitBal(BigDecimal defProfitBal) {
		this.defProfitBal = defProfitBal;
	}

	/**
	 * @return the defPrincipalBal
	 */
	public BigDecimal getDefPrincipalBal() {
		return defPrincipalBal;
	}

	/**
	 * @param defPrincipalBal
	 *            the defPrincipalBal to set
	 */
	public void setDefPrincipalBal(BigDecimal defPrincipalBal) {
		this.defPrincipalBal = defPrincipalBal;
	}

	/**
	 * @return the schdPftPaid
	 */
	public BigDecimal getSchdPftPaid() {
		return schdPftPaid;
	}

	/**
	 * @param schdPftPaid
	 *            the schdPftPaid to set
	 */
	public void setSchdPftPaid(BigDecimal schdPftPaid) {
		this.schdPftPaid = schdPftPaid;
	}

	public boolean isSchPftPaid() {
		return schPftPaid;
	}

	public void setSchPftPaid(boolean schPftPaid) {
		this.schPftPaid = schPftPaid;
	}

	public boolean isSchPriPaid() {
		return schPriPaid;
	}

	public void setSchPriPaid(boolean schPriPaid) {
		this.schPriPaid = schPriPaid;
	}

	/**
	 * @return the defProfit
	 */
	public BigDecimal getDefProfit() {
		return defProfit;
	}

	/**
	 * @param defProfit
	 *            the defProfit to set
	 */
	public void setDefProfit(BigDecimal defProfit) {
		this.defProfit = defProfit;
	}

	/**
	 * @return the defPrincipal
	 */
	public BigDecimal getDefPrincipal() {
		return defPrincipal;
	}

	/**
	 * @param defPrincipal
	 *            the defPrincipal to set
	 */
	public void setDefPrincipal(BigDecimal defPrincipal) {
		this.defPrincipal = defPrincipal;
	}

	public BigDecimal getDefSchdPftPaid() {
		return defSchdPftPaid;
	}

	public void setDefSchdPftPaid(BigDecimal defSchdPftPaid) {
		this.defSchdPftPaid = defSchdPftPaid;
	}

	public BigDecimal getDefSchdPriPaid() {
		return defSchdPriPaid;
	}

	public void setDefSchdPriPaid(BigDecimal defSchdPriPaid) {
		this.defSchdPriPaid = defSchdPriPaid;
	}
	
	public boolean isDefSchPftPaid() {
    	return defSchPftPaid;
    }
	public void setDefSchPftPaid(boolean defSchPftPaid) {
    	this.defSchPftPaid = defSchPftPaid;
    }

	public boolean isDefSchPriPaid() {
    	return defSchPriPaid;
    }
	public void setDefSchPriPaid(boolean defSchPriPaid) {
    	this.defSchPriPaid = defSchPriPaid;
    }

	public String getSchdMethod() {
		return schdMethod;
	}

	public void setSchdMethod(String schdMethod) {
		this.schdMethod = schdMethod;
	}

	public String getSpecifier() {
		return specifier;
	}
	public void setSpecifier(String specifier) {
		this.specifier = specifier;
	}
	
	public BigDecimal getEarlyPaid() {
    	return earlyPaid;
    }
	public void setEarlyPaid(BigDecimal earlyPaid) {
    	this.earlyPaid = earlyPaid;
    }

	public BigDecimal getEarlyPaidBal() {
    	return earlyPaidBal;
    }
	public void setEarlyPaidBal(BigDecimal earlyPaidBal) {
    	this.earlyPaidBal = earlyPaidBal;
    }
	
	public BigDecimal getWriteoffPrincipal() {
    	return writeoffPrincipal;
    }
	public void setWriteoffPrincipal(BigDecimal writeoffPrincipal) {
    	this.writeoffPrincipal = writeoffPrincipal;
    }

	public BigDecimal getWriteoffProfit() {
    	return writeoffProfit;
    }
	public void setWriteoffProfit(BigDecimal writeoffProfit) {
    	this.writeoffProfit = writeoffProfit;
    }

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
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

	public FinanceScheduleDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinanceScheduleDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId == 0) {
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	// Overidden Equals method to handle the comparision
	public boolean equals(FinanceScheduleDetail financeScheduleDetail) {
		return getId() == financeScheduleDetail.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinanceScheduleDetail) {
			FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) obj;
			return equals(financeScheduleDetail);
		}
		return false;
	}

	public ArrayList<ErrorDetails> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(ArrayList<ErrorDetails> errorDetails) {
		this.errorDetails = errorDetails;
	}

	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
	    this.feeChargeAmt = feeChargeAmt;
    }
	public BigDecimal getFeeChargeAmt() {
	    return feeChargeAmt;
    }
	
	public BigDecimal getRefundOrWaiver() {
    	return refundOrWaiver;
    }
	public void setRefundOrWaiver(BigDecimal refundOrWaiver) {
    	this.refundOrWaiver = refundOrWaiver;
    }

	public boolean isRepayComplete() {
    	return repayComplete;
    }
	public void setRepayComplete(boolean repayComplete) {
    	this.repayComplete = repayComplete;
    }

	public void setLogKey(long logKey) {
	    this.logKey = logKey;
    }
	public long getLogKey() {
	    return logKey;
    }

	public BigDecimal getOrgPft() {
		return orgPft;
	}
	public void setOrgPft(BigDecimal orgPft) {
		this.orgPft = orgPft;
	}

	public BigDecimal getOrgPri() {
		return orgPri;
	}
	public void setOrgPri(BigDecimal orgPri) {
		this.orgPri = orgPri;
	}

	public BigDecimal getOrgEndBal() {
		return orgEndBal;
	}
	public void setOrgEndBal(BigDecimal orgEndBal) {
		this.orgEndBal = orgEndBal;
	}

	public BigDecimal getOrgPlanPft() {
		return orgPlanPft;
	}
	public void setOrgPlanPft(BigDecimal orgPlanPft) {
		this.orgPlanPft = orgPlanPft;
	}

	public BigDecimal getFeeSchd() {
		return feeSchd;
	}
	public void setFeeSchd(BigDecimal feeSchd) {
		this.feeSchd = feeSchd;
	}

	public BigDecimal getSchdFeePaid() {
		return schdFeePaid;
	}
	public void setSchdFeePaid(BigDecimal schdFeePaid) {
		this.schdFeePaid = schdFeePaid;
	}

	public BigDecimal getSchdFeeOS() {
		return schdFeeOS;
	}
	public void setSchdFeeOS(BigDecimal schdFeeOS) {
		this.schdFeeOS = schdFeeOS;
	}

	public BigDecimal getTakafulFeeSchd() {
		return takafulFeeSchd;
	}
	public void setTakafulFeeSchd(BigDecimal takafulFeeSchd) {
		this.takafulFeeSchd = takafulFeeSchd;
	}

	public BigDecimal getSchdTakafulFeePaid() {
		return schdTakafulFeePaid;
	}
	public void setSchdTakafulFeePaid(BigDecimal schdTakafulFeePaid) {
		this.schdTakafulFeePaid = schdTakafulFeePaid;
	}

}
