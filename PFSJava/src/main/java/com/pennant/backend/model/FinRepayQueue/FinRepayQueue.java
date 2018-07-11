package com.pennant.backend.model.FinRepayQueue;

import java.math.BigDecimal;
import java.util.Date;

public class FinRepayQueue {

	private String		finReference;
	private Date		rpyDate;
	private String		finRpyFor;
	private int			finPriority;
	private String		finType;
	private String		Branch;
	private long		customerID;
	
	private BigDecimal	schdPft					= BigDecimal.ZERO;
	private BigDecimal	schdPri					= BigDecimal.ZERO;
	private BigDecimal	schdPftPaid				= BigDecimal.ZERO;
	private BigDecimal	schdPriPaid				= BigDecimal.ZERO;
	private BigDecimal	schdPftBal				= BigDecimal.ZERO;
	private BigDecimal	schdPriBal				= BigDecimal.ZERO;
	private boolean		schdIsPftPaid;
	private boolean		schdIsPriPaid;

	private BigDecimal	schdPftPayNow			= BigDecimal.ZERO;
	private BigDecimal	schdTdsPayNow			= BigDecimal.ZERO;
	private BigDecimal	schdPriPayNow			= BigDecimal.ZERO;
	private BigDecimal	schdPftWaivedNow		= BigDecimal.ZERO;
	private BigDecimal	schdPriWaivedNow		= BigDecimal.ZERO;

	// Scheduled Fee Amount
	private BigDecimal	schdFee					= BigDecimal.ZERO;
	private BigDecimal	schdFeePaid				= BigDecimal.ZERO;
	private BigDecimal	schdFeeBal				= BigDecimal.ZERO;
	private BigDecimal	schdFeePayNow			= BigDecimal.ZERO;
	private BigDecimal	schdFeeWaivedNow			= BigDecimal.ZERO;

	// Insurance Amount
	private BigDecimal	schdIns				= BigDecimal.ZERO;
	private BigDecimal	schdInsPaid			= BigDecimal.ZERO;
	private BigDecimal	schdInsBal			= BigDecimal.ZERO;
	private BigDecimal	schdInsPayNow		= BigDecimal.ZERO;
	private BigDecimal	schdInsWaivedNow		= BigDecimal.ZERO;

	// Supplementary Rent
	private BigDecimal	schdSuplRent			= BigDecimal.ZERO;
	private BigDecimal	schdSuplRentPaid		= BigDecimal.ZERO;
	private BigDecimal	schdSuplRentBal			= BigDecimal.ZERO;
	private BigDecimal	schdSuplRentPayNow		= BigDecimal.ZERO;
	private BigDecimal	schdSuplRentWaivedNow		= BigDecimal.ZERO;

	// Increased Cost Amount
	private BigDecimal	schdIncrCost			= BigDecimal.ZERO;
	private BigDecimal	schdIncrCostPaid		= BigDecimal.ZERO;
	private BigDecimal	schdIncrCostBal			= BigDecimal.ZERO;
	private BigDecimal	schdIncrCostPayNow		= BigDecimal.ZERO;
	private BigDecimal	schdIncrCostWaivedNow		= BigDecimal.ZERO;

	// External Fields Used for EOD process
	private BigDecimal	refundAmount			= BigDecimal.ZERO;
	private boolean		rcdNotExist				= false;

	private BigDecimal	acrTillLBD				= BigDecimal.ZERO;
	private BigDecimal	pftAmzSusp				= BigDecimal.ZERO;
	private BigDecimal	amzTillLBD				= BigDecimal.ZERO;
	private String		linkedFinRef;
	private String		finStatus;
	private String		finStsReason;
	private String		finEvent;
	
	// penalty calculations
	private String		profitDaysBasis;
	private String		chargeType				= "";
	private BigDecimal	waivedAmount			= BigDecimal.ZERO;
	private BigDecimal	penaltyPayNow			= BigDecimal.ZERO;
	private BigDecimal	penaltyBal				= BigDecimal.ZERO;
	private BigDecimal	paidPenaltyCGST			= BigDecimal.ZERO;
	private BigDecimal	paidPenaltySGST			= BigDecimal.ZERO;
	private BigDecimal	paidPenaltyUGST			= BigDecimal.ZERO;
	private BigDecimal	paidPenaltyIGST			= BigDecimal.ZERO;
	
	// late pay profit calculations
	private BigDecimal	schdRate				= BigDecimal.ZERO;
	private BigDecimal	latePayPftPayNow		= BigDecimal.ZERO;
	private BigDecimal	latePayPftWaivedNow		= BigDecimal.ZERO;
	private BigDecimal	latePayPftBal			= BigDecimal.ZERO;

	//Advised profit Rates
	private BigDecimal advProfit = BigDecimal.ZERO;
	private BigDecimal rebate = BigDecimal.ZERO;
	

	public FinRepayQueue() {

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getRpyDate() {
		return rpyDate;
	}

	public void setRpyDate(Date rpyDate) {
		this.rpyDate = rpyDate;
	}

	public String getFinRpyFor() {
		return finRpyFor;
	}

	public void setFinRpyFor(String finRpyFor) {
		this.finRpyFor = finRpyFor;
	}

	public int getFinPriority() {
		return finPriority;
	}

	public void setFinPriority(int finPriority) {
		this.finPriority = finPriority;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getBranch() {
		return Branch;
	}

	public void setBranch(String branch) {
		Branch = branch;
	}

	public long getCustomerID() {
		return customerID;
	}

	public void setCustomerID(long customerID) {
		this.customerID = customerID;
	}

	public BigDecimal getSchdPft() {
		return schdPft;
	}

	public void setSchdPft(BigDecimal schdPft) {
		this.schdPft = schdPft;
	}

	public BigDecimal getSchdPri() {
		return schdPri;
	}

	public void setSchdPri(BigDecimal schdPri) {
		this.schdPri = schdPri;
	}

	public BigDecimal getSchdPftPaid() {
		return schdPftPaid;
	}

	public void setSchdPftPaid(BigDecimal schdPftPaid) {
		this.schdPftPaid = schdPftPaid;
	}

	public BigDecimal getSchdPriPaid() {
		return schdPriPaid;
	}

	public void setSchdPriPaid(BigDecimal schdPriPaid) {
		this.schdPriPaid = schdPriPaid;
	}

	public BigDecimal getSchdPftBal() {
		return schdPftBal;
	}

	public void setSchdPftBal(BigDecimal schdPftBal) {
		this.schdPftBal = schdPftBal;
	}

	public BigDecimal getSchdPriBal() {
		return schdPriBal;
	}

	public void setSchdPriBal(BigDecimal schdPriBal) {
		this.schdPriBal = schdPriBal;
	}

	public boolean isSchdIsPftPaid() {
		return schdIsPftPaid;
	}

	public void setSchdIsPftPaid(boolean schdIsPftPaid) {
		this.schdIsPftPaid = schdIsPftPaid;
	}

	public boolean isSchdIsPriPaid() {
		return schdIsPriPaid;
	}

	public void setSchdIsPriPaid(boolean schdIsPriPaid) {
		this.schdIsPriPaid = schdIsPriPaid;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public boolean isRcdNotExist() {
		return rcdNotExist;
	}

	public void setRcdNotExist(boolean rcdNotExist) {
		this.rcdNotExist = rcdNotExist;
	}

	public BigDecimal getPenaltyPayNow() {
		return penaltyPayNow;
	}

	public void setPenaltyPayNow(BigDecimal penaltyPayNow) {
		this.penaltyPayNow = penaltyPayNow;
	}

	public BigDecimal getWaivedAmount() {
		return waivedAmount;
	}

	public void setWaivedAmount(BigDecimal waivedAmount) {
		this.waivedAmount = waivedAmount;
	}

	public String getChargeType() {
		return chargeType;
	}

	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}

	public BigDecimal getSchdPftPayNow() {
		return schdPftPayNow;
	}

	public void setSchdPftPayNow(BigDecimal schdPftPayNow) {
		this.schdPftPayNow = schdPftPayNow;
	}

	public BigDecimal getSchdPriPayNow() {
		return schdPriPayNow;
	}

	public void setSchdPriPayNow(BigDecimal schdPriPayNow) {
		this.schdPriPayNow = schdPriPayNow;
	}

	public BigDecimal getPenaltyBal() {
		return penaltyBal;
	}

	public void setPenaltyBal(BigDecimal penaltyBal) {
		this.penaltyBal = penaltyBal;
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

	public BigDecimal getAcrTillLBD() {
		return acrTillLBD;
	}

	public void setAcrTillLBD(BigDecimal acrTillLBD) {
		this.acrTillLBD = acrTillLBD;
	}

	public BigDecimal getPftAmzSusp() {
		return pftAmzSusp;
	}

	public void setPftAmzSusp(BigDecimal pftAmzSusp) {
		this.pftAmzSusp = pftAmzSusp;
	}

	public BigDecimal getAmzTillLBD() {
		return amzTillLBD;
	}

	public void setAmzTillLBD(BigDecimal amzTillLBD) {
		this.amzTillLBD = amzTillLBD;
	}

	public String getLinkedFinRef() {
		return linkedFinRef;
	}
	public void setLinkedFinRef(String linkedFinRef) {
		this.linkedFinRef = linkedFinRef;
	}

	public String getFinStatus() {
		return finStatus;
	}
	public void setFinStatus(String finStatus) {
		this.finStatus = finStatus;
	}

	public String getFinStsReason() {
		return finStsReason;
	}
	public void setFinStsReason(String finStsReason) {
		this.finStsReason = finStsReason;
	}

	public String getFinEvent() {
		return finEvent;
	}
	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public String getProfitDaysBasis() {
		return profitDaysBasis;
	}
	public void setProfitDaysBasis(String profitDaysBasis) {
		this.profitDaysBasis = profitDaysBasis;
	}

	public BigDecimal getSchdRate() {
		return schdRate;
	}
	public void setSchdRate(BigDecimal schdRate) {
		this.schdRate = schdRate;
	}

	public BigDecimal getLatePayPftPayNow() {
		return latePayPftPayNow;
	}
	public void setLatePayPftPayNow(BigDecimal latePayPftPayNow) {
		this.latePayPftPayNow = latePayPftPayNow;
	}

	public BigDecimal getLatePayPftBal() {
		return latePayPftBal;
	}
	public void setLatePayPftBal(BigDecimal latePayPftBal) {
		this.latePayPftBal = latePayPftBal;
	}

	public BigDecimal getAdvProfit() {
		return advProfit;
	}
	public void setAdvProfit(BigDecimal advProfit) {
		this.advProfit = advProfit;
	}

	public BigDecimal getRebate() {
		return rebate;
	}
	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public BigDecimal getSchdPftWaivedNow() {
		return schdPftWaivedNow;
	}
	public void setSchdPftWaivedNow(BigDecimal schdPftWaivedNow) {
		this.schdPftWaivedNow = schdPftWaivedNow;
	}

	public BigDecimal getSchdPriWaivedNow() {
		return schdPriWaivedNow;
	}
	public void setSchdPriWaivedNow(BigDecimal schdPriWaivedNow) {
		this.schdPriWaivedNow = schdPriWaivedNow;
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

	public BigDecimal getLatePayPftWaivedNow() {
		return latePayPftWaivedNow;
	}
	public void setLatePayPftWaivedNow(BigDecimal latePayPftWaivedNow) {
		this.latePayPftWaivedNow = latePayPftWaivedNow;
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

	public BigDecimal getSchdTdsPayNow() {
		return schdTdsPayNow;
	}

	public void setSchdTdsPayNow(BigDecimal schdTdsPayNow) {
		this.schdTdsPayNow = schdTdsPayNow;
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