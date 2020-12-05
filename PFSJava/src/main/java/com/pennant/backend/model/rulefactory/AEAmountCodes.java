package com.pennant.backend.model.rulefactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.pennant.app.constants.AccountConstants;

public class AEAmountCodes implements Serializable {
	private static final long serialVersionUID = 1L;

	private String finType;

	private BigDecimal accrue = BigDecimal.ZERO;
	private BigDecimal dAccrue = BigDecimal.ZERO;
	private BigDecimal accrueBal = BigDecimal.ZERO;

	private BigDecimal accrueS = BigDecimal.ZERO;
	private BigDecimal dAccrueS = BigDecimal.ZERO;

	private BigDecimal amz = BigDecimal.ZERO;
	private BigDecimal dAmz = BigDecimal.ZERO;
	private BigDecimal dAmzPr = BigDecimal.ZERO;
	private BigDecimal amzBal = BigDecimal.ZERO;
	private BigDecimal uAmz = BigDecimal.ZERO;
	private BigDecimal uLpi = BigDecimal.ZERO;

	private BigDecimal amzNRM = BigDecimal.ZERO;
	private BigDecimal dAmzNRM = BigDecimal.ZERO;

	private BigDecimal amzPD = BigDecimal.ZERO;
	private BigDecimal dAmzPD = BigDecimal.ZERO;

	private BigDecimal amzS = BigDecimal.ZERO;
	private BigDecimal dAmzS = BigDecimal.ZERO;

	private BigDecimal disburse = BigDecimal.ZERO;
	private BigDecimal downpay = BigDecimal.ZERO;
	private BigDecimal advanceEMI = BigDecimal.ZERO;
	private BigDecimal pft = BigDecimal.ZERO;
	private BigDecimal pftAB = BigDecimal.ZERO;
	private BigDecimal pftAP = BigDecimal.ZERO;
	private BigDecimal cpzChg = BigDecimal.ZERO;
	private BigDecimal cpzTot = BigDecimal.ZERO;
	private BigDecimal cpzPrv = BigDecimal.ZERO;
	private BigDecimal cpzCur = BigDecimal.ZERO;
	private BigDecimal cpzNxt = BigDecimal.ZERO;

	private BigDecimal pftChg = BigDecimal.ZERO;
	private BigDecimal pftS = BigDecimal.ZERO;
	private BigDecimal pftSB = BigDecimal.ZERO;
	private BigDecimal pftSP = BigDecimal.ZERO;
	private BigDecimal pri = BigDecimal.ZERO;
	private BigDecimal priAB = BigDecimal.ZERO;
	private BigDecimal priAP = BigDecimal.ZERO;
	private BigDecimal priS = BigDecimal.ZERO;
	private BigDecimal priSB = BigDecimal.ZERO;
	private BigDecimal priSP = BigDecimal.ZERO;
	private BigDecimal rpPft = BigDecimal.ZERO;
	private BigDecimal rpPftPr = BigDecimal.ZERO;
	private BigDecimal priPr = BigDecimal.ZERO;
	private BigDecimal priSPr = BigDecimal.ZERO;

	private BigDecimal rpTds = BigDecimal.ZERO;
	private BigDecimal pftDuePaid = BigDecimal.ZERO;
	private BigDecimal priDuePaid = BigDecimal.ZERO;
	private BigDecimal accruedPaid = BigDecimal.ZERO;
	private BigDecimal unAccruedPaid = BigDecimal.ZERO;
	private BigDecimal futurePriPaid = BigDecimal.ZERO;
	private BigDecimal lastSchPftPaid = BigDecimal.ZERO;
	private BigDecimal lastSchPftWaived = BigDecimal.ZERO;

	private BigDecimal rpPri = BigDecimal.ZERO;
	private BigDecimal rpTot = BigDecimal.ZERO;
	private BigDecimal rpTotPr = BigDecimal.ZERO;
	private BigDecimal instpft = BigDecimal.ZERO;
	private BigDecimal instpftPr = BigDecimal.ZERO;
	private BigDecimal instTds = BigDecimal.ZERO;
	private BigDecimal instpri = BigDecimal.ZERO;
	private BigDecimal instpriPr = BigDecimal.ZERO;
	private BigDecimal instcpz = BigDecimal.ZERO;
	private BigDecimal insttot = BigDecimal.ZERO;
	private BigDecimal refund = BigDecimal.ZERO;
	private BigDecimal rebate = BigDecimal.ZERO;
	private BigDecimal insRefund = BigDecimal.ZERO;
	private BigDecimal InsPay = BigDecimal.ZERO;
	private BigDecimal schFeePay = BigDecimal.ZERO;
	private BigDecimal suplRentPay = BigDecimal.ZERO;
	private BigDecimal incrCostPay = BigDecimal.ZERO;
	private BigDecimal woPayAmt = BigDecimal.ZERO;
	private int ODDays = 0;
	private int daysFromFullyPaid = 0;
	private int ODInst = 0;
	private int paidInst = 0;

	private BigDecimal provAmt = BigDecimal.ZERO;
	private BigDecimal provDue = BigDecimal.ZERO;
	private BigDecimal suspNow = BigDecimal.ZERO;
	private BigDecimal suspRls = BigDecimal.ZERO;
	private BigDecimal penalty = BigDecimal.ZERO;
	private BigDecimal waiver = BigDecimal.ZERO;
	private BigDecimal provAsst = BigDecimal.ZERO;

	private BigDecimal penaltyPaid = BigDecimal.ZERO;
	private BigDecimal penaltyRcv = BigDecimal.ZERO;
	private BigDecimal penaltyDue = BigDecimal.ZERO;
	private BigDecimal penaltyWaived = BigDecimal.ZERO;
	private BigDecimal accrueTsfd = BigDecimal.ZERO;
	private BigDecimal prvAccrueTsfd = BigDecimal.ZERO;

	private BigDecimal totalWriteoff = BigDecimal.ZERO;
	private BigDecimal excessBal = BigDecimal.ZERO;
	private BigDecimal toExcessAmt = BigDecimal.ZERO;
	private BigDecimal toEmiAdvance = BigDecimal.ZERO;

	private BigDecimal downpayB = BigDecimal.ZERO;
	private BigDecimal downpayS = BigDecimal.ZERO;
	private BigDecimal FeeChargeAmt = BigDecimal.ZERO;
	private BigDecimal InsuranceAmt = BigDecimal.ZERO;

	private BigDecimal addFeeToFinance = BigDecimal.ZERO;
	private BigDecimal paidFee = BigDecimal.ZERO;
	private BigDecimal bpi = BigDecimal.ZERO;
	private BigDecimal bpiTds = BigDecimal.ZERO;
	private boolean bpiToAdvInt = false;
	private BigDecimal deductFeeDisb = BigDecimal.ZERO;
	private BigDecimal deductInsDisb = BigDecimal.ZERO;
	private BigDecimal disbInstAmt = BigDecimal.ZERO;

	private BigDecimal priWaived = BigDecimal.ZERO;
	private BigDecimal pftWaived = BigDecimal.ZERO;
	private BigDecimal feeWaived = BigDecimal.ZERO;
	private BigDecimal insWaived = BigDecimal.ZERO;
	private BigDecimal pftDueWaived = BigDecimal.ZERO;
	private BigDecimal priDueWaived = BigDecimal.ZERO;
	private BigDecimal accrueWaived = BigDecimal.ZERO;
	private BigDecimal unAccrueWaived = BigDecimal.ZERO;
	private BigDecimal futurePriWaived = BigDecimal.ZERO;
	private BigDecimal dueTds = BigDecimal.ZERO;
	private BigDecimal lastSchTds = BigDecimal.ZERO;
	private BigDecimal accruedTds = BigDecimal.ZERO;
	private BigDecimal unAccruedTds = BigDecimal.ZERO;

	// For Disbursement Instructions used in SUBHEAD
	private String partnerBankAcType;
	private String partnerBankAc;
	// For GL code
	private String productCode;
	private String dealerCode;

	// Late Payment Interest Fields
	private BigDecimal lpi = BigDecimal.ZERO;
	private BigDecimal lpiDue = BigDecimal.ZERO;
	private BigDecimal lpiPaid = BigDecimal.ZERO;
	private BigDecimal lpiWaived = BigDecimal.ZERO;
	private BigDecimal dLPIAmz = BigDecimal.ZERO;
	private BigDecimal dGSTLPIAmz = BigDecimal.ZERO;

	// Late Payment Penalty Fields
	private BigDecimal dLPPAmz = BigDecimal.ZERO;
	private BigDecimal dGSTLPPAmz = BigDecimal.ZERO;

	// Write-Off Details
	private BigDecimal priWriteOff = BigDecimal.ZERO;
	private BigDecimal pftWriteOff = BigDecimal.ZERO;

	// Commitment
	private BigDecimal cmtAmt = BigDecimal.ZERO;
	private BigDecimal chgAmt = BigDecimal.ZERO;
	private BigDecimal cmtAvl = BigDecimal.ZERO;
	private BigDecimal cmtUAmt = BigDecimal.ZERO;
	private BigDecimal cmtUOth = BigDecimal.ZERO;

	// Present mode
	private String receiptMode;

	// VAS Fees
	private BigDecimal deductVasDisb = BigDecimal.ZERO;
	private BigDecimal addVasToFinance = BigDecimal.ZERO;
	private BigDecimal vasFeeWaived = BigDecimal.ZERO;
	private BigDecimal paidVasFee = BigDecimal.ZERO;
	private BigDecimal refundVasFee = BigDecimal.ZERO;

	private BigDecimal imdAmount = BigDecimal.ZERO;

	// Cash Management
	private BigDecimal transfer = BigDecimal.ZERO;
	private String postingType = AccountConstants.ACCOUNT_EVENT_POSTINGTYPE_LOAN;
	private String userBranch = "";
	private String repledge = "";
	private BigDecimal repledgeAmt = BigDecimal.ZERO;
	private String paymentType = "";
	private boolean quickDisb = false;
	private String cashAcExecuted = "N";

	// Subvention Details
	private BigDecimal dSvnAmz = BigDecimal.ZERO;
	private BigDecimal disbSvnAmount = BigDecimal.ZERO;
	private BigDecimal subVentionAmount = BigDecimal.ZERO;

	// Assignments
	private BigDecimal assignmentPerc = BigDecimal.ZERO;
	private BigDecimal assignPriAmount = BigDecimal.ZERO;
	private BigDecimal assignPftAmount = BigDecimal.ZERO;
	private BigDecimal assignODAmount = BigDecimal.ZERO;
	private BigDecimal assignExcessAmt = BigDecimal.ZERO;
	private BigDecimal assignEMIAdvAmt = BigDecimal.ZERO;
	private BigDecimal assignPartPayment = BigDecimal.ZERO;
	private BigDecimal assignPaidPriAmt = BigDecimal.ZERO;

	private BigDecimal ppAmount = BigDecimal.ZERO;

	// Additional Fields Added in AmountCodes
	private String businessvertical = "";
	private boolean alwflexi = false;
	private String finbranch = "";
	private String entitycode = "";

	// Advance EMI/Interest changes
	private boolean intAdv = false;
	private BigDecimal intAdjusted = BigDecimal.ZERO;
	private BigDecimal intTdsAdjusted = BigDecimal.ZERO;
	private BigDecimal emiAdjusted = BigDecimal.ZERO;
	private BigDecimal emiTdsAdjusted = BigDecimal.ZERO;
	private BigDecimal intDue = BigDecimal.ZERO;
	private BigDecimal priAdjusted = BigDecimal.ZERO;
	private BigDecimal emiDue = BigDecimal.ZERO;
	private BigDecimal dGapAmz = BigDecimal.ZERO;
	private BigDecimal svAmount = BigDecimal.ZERO;
	private BigDecimal cbAmount = BigDecimal.ZERO;
	private BigDecimal dbdAmount = BigDecimal.ZERO;
	private BigDecimal instChg = BigDecimal.ZERO;
	private BigDecimal instIntChg = BigDecimal.ZERO;
	private BigDecimal instPriChg = BigDecimal.ZERO;
	private BigDecimal pastCpzChg = BigDecimal.ZERO;

	// OEM Subvention amount code
	private BigDecimal oemSbvAmount = BigDecimal.ZERO;
	private BigDecimal advInst = BigDecimal.ZERO;

	private BigDecimal vasInstAmt = BigDecimal.ZERO;

	public AEAmountCodes() {
		super();
	}

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> map = new HashMap<String, Object>();
		getDeclaredFieldValues(map);

		return map;
	}

	public Map<String, Object> getDeclaredFieldValues(Map<String, Object> map) {

		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				// "ae_" Should be in small case only, if we want to change the
				// case we need to update the configuration fields as well.
				map.put("ae_" + this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}

		return map;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public BigDecimal getAccrue() {
		return accrue;
	}

	public void setAccrue(BigDecimal accrue) {
		this.accrue = accrue;
	}

	public BigDecimal getdAccrue() {
		return dAccrue;
	}

	public void setdAccrue(BigDecimal dAccrue) {
		this.dAccrue = dAccrue;
	}

	public BigDecimal getAccrueBal() {
		return accrueBal;
	}

	public void setAccrueBal(BigDecimal accrueBal) {
		this.accrueBal = accrueBal;
	}

	public BigDecimal getAccrueS() {
		return accrueS;
	}

	public void setAccrueS(BigDecimal accrueS) {
		this.accrueS = accrueS;
	}

	public BigDecimal getdAccrueS() {
		return dAccrueS;
	}

	public void setdAccrueS(BigDecimal dAccrueS) {
		this.dAccrueS = dAccrueS;
	}

	public BigDecimal getAmz() {
		return amz;
	}

	public void setAmz(BigDecimal amz) {
		this.amz = amz;
	}

	public BigDecimal getdAmz() {
		return dAmz;
	}

	public void setdAmz(BigDecimal dAmz) {
		this.dAmz = dAmz;
	}

	public BigDecimal getAmzBal() {
		return amzBal;
	}

	public void setAmzBal(BigDecimal amzBal) {
		this.amzBal = amzBal;
	}

	public BigDecimal getuAmz() {
		return uAmz;
	}

	public void setuAmz(BigDecimal uAmz) {
		this.uAmz = uAmz;
	}

	public BigDecimal getuLpi() {
		return uLpi;
	}

	public void setuLpi(BigDecimal uLpi) {
		this.uLpi = uLpi;
	}

	public BigDecimal getAmzNRM() {
		return amzNRM;
	}

	public void setAmzNRM(BigDecimal amzNRM) {
		this.amzNRM = amzNRM;
	}

	public BigDecimal getdAmzNRM() {
		return dAmzNRM;
	}

	public void setdAmzNRM(BigDecimal dAmzNRM) {
		this.dAmzNRM = dAmzNRM;
	}

	public BigDecimal getAmzPD() {
		return amzPD;
	}

	public void setAmzPD(BigDecimal amzPD) {
		this.amzPD = amzPD;
	}

	public BigDecimal getdAmzPD() {
		return dAmzPD;
	}

	public void setdAmzPD(BigDecimal dAmzPD) {
		this.dAmzPD = dAmzPD;
	}

	public BigDecimal getAmzS() {
		return amzS;
	}

	public void setAmzS(BigDecimal amzS) {
		this.amzS = amzS;
	}

	public BigDecimal getdAmzS() {
		return dAmzS;
	}

	public void setdAmzS(BigDecimal dAmzS) {
		this.dAmzS = dAmzS;
	}

	public BigDecimal getDisburse() {
		return disburse;
	}

	public void setDisburse(BigDecimal disburse) {
		this.disburse = disburse;
	}

	public BigDecimal getDownpay() {
		return downpay;
	}

	public void setDownpay(BigDecimal downpay) {
		this.downpay = downpay;
	}

	public BigDecimal getAdvanceEMI() {
		return advanceEMI;
	}

	public void setAdvanceEMI(BigDecimal advanceEMI) {
		this.advanceEMI = advanceEMI;
	}

	public BigDecimal getPft() {
		return pft;
	}

	public void setPft(BigDecimal pft) {
		this.pft = pft;
	}

	public BigDecimal getPftAB() {
		return pftAB;
	}

	public void setPftAB(BigDecimal pftAB) {
		this.pftAB = pftAB;
	}

	public BigDecimal getPftAP() {
		return pftAP;
	}

	public void setPftAP(BigDecimal pftAP) {
		this.pftAP = pftAP;
	}

	public BigDecimal getCpzChg() {
		return cpzChg;
	}

	public void setCpzChg(BigDecimal cpzChg) {
		this.cpzChg = cpzChg;
	}

	public BigDecimal getCpzTot() {
		return cpzTot;
	}

	public void setCpzTot(BigDecimal cpzTot) {
		this.cpzTot = cpzTot;
	}

	public BigDecimal getCpzPrv() {
		return cpzPrv;
	}

	public void setCpzPrv(BigDecimal cpzPrv) {
		this.cpzPrv = cpzPrv;
	}

	public BigDecimal getCpzCur() {
		return cpzCur;
	}

	public void setCpzCur(BigDecimal cpzCur) {
		this.cpzCur = cpzCur;
	}

	public BigDecimal getCpzNxt() {
		return cpzNxt;
	}

	public void setCpzNxt(BigDecimal cpzNxt) {
		this.cpzNxt = cpzNxt;
	}

	public BigDecimal getPftChg() {
		return pftChg;
	}

	public void setPftChg(BigDecimal pftChg) {
		this.pftChg = pftChg;
	}

	public BigDecimal getPftS() {
		return pftS;
	}

	public void setPftS(BigDecimal pftS) {
		this.pftS = pftS;
	}

	public BigDecimal getPftSB() {
		return pftSB;
	}

	public void setPftSB(BigDecimal pftSB) {
		this.pftSB = pftSB;
	}

	public BigDecimal getPftSP() {
		return pftSP;
	}

	public void setPftSP(BigDecimal pftSP) {
		this.pftSP = pftSP;
	}

	public BigDecimal getPri() {
		return pri;
	}

	public void setPri(BigDecimal pri) {
		this.pri = pri;
	}

	public BigDecimal getPriAB() {
		return priAB;
	}

	public void setPriAB(BigDecimal priAB) {
		this.priAB = priAB;
	}

	public BigDecimal getPriAP() {
		return priAP;
	}

	public void setPriAP(BigDecimal priAP) {
		this.priAP = priAP;
	}

	public BigDecimal getPriS() {
		return priS;
	}

	public void setPriS(BigDecimal priS) {
		this.priS = priS;
	}

	public BigDecimal getPriSB() {
		return priSB;
	}

	public void setPriSB(BigDecimal priSB) {
		this.priSB = priSB;
	}

	public BigDecimal getPriSP() {
		return priSP;
	}

	public void setPriSP(BigDecimal priSP) {
		this.priSP = priSP;
	}

	public BigDecimal getRpPft() {
		return rpPft;
	}

	public void setRpPft(BigDecimal rpPft) {
		this.rpPft = rpPft;
	}

	public BigDecimal getRpTds() {
		return rpTds;
	}

	public void setRpTds(BigDecimal rpTds) {
		this.rpTds = rpTds;
	}

	public BigDecimal getPftDuePaid() {
		return pftDuePaid;
	}

	public void setPftDuePaid(BigDecimal pftDuePaid) {
		this.pftDuePaid = pftDuePaid;
	}

	public BigDecimal getPriDuePaid() {
		return priDuePaid;
	}

	public void setPriDuePaid(BigDecimal priDuePaid) {
		this.priDuePaid = priDuePaid;
	}

	public BigDecimal getAccruedPaid() {
		return accruedPaid;
	}

	public void setAccruedPaid(BigDecimal accruedPaid) {
		this.accruedPaid = accruedPaid;
	}

	public BigDecimal getUnAccruedPaid() {
		return unAccruedPaid;
	}

	public void setUnAccruedPaid(BigDecimal unAccruedPaid) {
		this.unAccruedPaid = unAccruedPaid;
	}

	public BigDecimal getFuturePriPaid() {
		return futurePriPaid;
	}

	public void setFuturePriPaid(BigDecimal futurePriPaid) {
		this.futurePriPaid = futurePriPaid;
	}

	public BigDecimal getLastSchPftPaid() {
		return lastSchPftPaid;
	}

	public void setLastSchPftPaid(BigDecimal lastSchPftPaid) {
		this.lastSchPftPaid = lastSchPftPaid;
	}

	public BigDecimal getLastSchPftWaived() {
		return lastSchPftWaived;
	}

	public void setLastSchPftWaived(BigDecimal lastSchPftWaived) {
		this.lastSchPftWaived = lastSchPftWaived;
	}

	public BigDecimal getRpPri() {
		return rpPri;
	}

	public void setRpPri(BigDecimal rpPri) {
		this.rpPri = rpPri;
	}

	public BigDecimal getRpTot() {
		return rpTot;
	}

	public void setRpTot(BigDecimal rpTot) {
		this.rpTot = rpTot;
	}

	public BigDecimal getInstpft() {
		return instpft;
	}

	public void setInstpft(BigDecimal instpft) {
		this.instpft = instpft;
	}

	public BigDecimal getInstTds() {
		return instTds;
	}

	public void setInstTds(BigDecimal instTds) {
		this.instTds = instTds;
	}

	public BigDecimal getInstpri() {
		return instpri;
	}

	public void setInstpri(BigDecimal instpri) {
		this.instpri = instpri;
	}

	public BigDecimal getInstcpz() {
		return instcpz;
	}

	public void setInstcpz(BigDecimal instcpz) {
		this.instcpz = instcpz;
	}

	public BigDecimal getInsttot() {
		return insttot;
	}

	public void setInsttot(BigDecimal insttot) {
		this.insttot = insttot;
	}

	public BigDecimal getRefund() {
		return refund;
	}

	public void setRefund(BigDecimal refund) {
		this.refund = refund;
	}

	public BigDecimal getRebate() {
		return rebate;
	}

	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public BigDecimal getInsRefund() {
		return insRefund;
	}

	public void setInsRefund(BigDecimal insRefund) {
		this.insRefund = insRefund;
	}

	public BigDecimal getInsPay() {
		return InsPay;
	}

	public void setInsPay(BigDecimal insPay) {
		InsPay = insPay;
	}

	public BigDecimal getSchFeePay() {
		return schFeePay;
	}

	public void setSchFeePay(BigDecimal schFeePay) {
		this.schFeePay = schFeePay;
	}

	public BigDecimal getSuplRentPay() {
		return suplRentPay;
	}

	public void setSuplRentPay(BigDecimal suplRentPay) {
		this.suplRentPay = suplRentPay;
	}

	public BigDecimal getIncrCostPay() {
		return incrCostPay;
	}

	public void setIncrCostPay(BigDecimal incrCostPay) {
		this.incrCostPay = incrCostPay;
	}

	public BigDecimal getWoPayAmt() {
		return woPayAmt;
	}

	public void setWoPayAmt(BigDecimal woPayAmt) {
		this.woPayAmt = woPayAmt;
	}

	public int getODDays() {
		return ODDays;
	}

	public void setODDays(int oDDays) {
		ODDays = oDDays;
	}

	public int getDaysFromFullyPaid() {
		return daysFromFullyPaid;
	}

	public void setDaysFromFullyPaid(int daysFromFullyPaid) {
		this.daysFromFullyPaid = daysFromFullyPaid;
	}

	public int getODInst() {
		return ODInst;
	}

	public void setODInst(int oDInst) {
		ODInst = oDInst;
	}

	public int getPaidInst() {
		return paidInst;
	}

	public void setPaidInst(int paidInst) {
		this.paidInst = paidInst;
	}

	public BigDecimal getProvAmt() {
		return provAmt;
	}

	public void setProvAmt(BigDecimal provAmt) {
		this.provAmt = provAmt;
	}

	public BigDecimal getProvDue() {
		return provDue;
	}

	public void setProvDue(BigDecimal provDue) {
		this.provDue = provDue;
	}

	public BigDecimal getSuspNow() {
		return suspNow;
	}

	public void setSuspNow(BigDecimal suspNow) {
		this.suspNow = suspNow;
	}

	public BigDecimal getSuspRls() {
		return suspRls;
	}

	public void setSuspRls(BigDecimal suspRls) {
		this.suspRls = suspRls;
	}

	public BigDecimal getPenalty() {
		return penalty;
	}

	public void setPenalty(BigDecimal penalty) {
		this.penalty = penalty;
	}

	public BigDecimal getWaiver() {
		return waiver;
	}

	public void setWaiver(BigDecimal waiver) {
		this.waiver = waiver;
	}

	public BigDecimal getPenaltyPaid() {
		return penaltyPaid;
	}

	public void setPenaltyPaid(BigDecimal penaltyPaid) {
		this.penaltyPaid = penaltyPaid;
	}

	public BigDecimal getPenaltyRcv() {
		return penaltyRcv;
	}

	public void setPenaltyRcv(BigDecimal penaltyRcv) {
		this.penaltyRcv = penaltyRcv;
	}

	public BigDecimal getPenaltyDue() {
		return penaltyDue;
	}

	public void setPenaltyDue(BigDecimal penaltyDue) {
		this.penaltyDue = penaltyDue;
	}

	public BigDecimal getPenaltyWaived() {
		return penaltyWaived;
	}

	public void setPenaltyWaived(BigDecimal penaltyWaived) {
		this.penaltyWaived = penaltyWaived;
	}

	public BigDecimal getAccrueTsfd() {
		return accrueTsfd;
	}

	public void setAccrueTsfd(BigDecimal accrueTsfd) {
		this.accrueTsfd = accrueTsfd;
	}

	public BigDecimal getPrvAccrueTsfd() {
		return prvAccrueTsfd;
	}

	public void setPrvAccrueTsfd(BigDecimal prvAccrueTsfd) {
		this.prvAccrueTsfd = prvAccrueTsfd;
	}

	public BigDecimal getTotalWriteoff() {
		return totalWriteoff;
	}

	public void setTotalWriteoff(BigDecimal totalWriteoff) {
		this.totalWriteoff = totalWriteoff;
	}

	public BigDecimal getExcessBal() {
		return excessBal;
	}

	public void setExcessBal(BigDecimal excessBal) {
		this.excessBal = excessBal;
	}

	public BigDecimal getToExcessAmt() {
		return toExcessAmt;
	}

	public void setToExcessAmt(BigDecimal toExcessAmt) {
		this.toExcessAmt = toExcessAmt;
	}

	public BigDecimal getToEmiAdvance() {
		return toEmiAdvance;
	}

	public void setToEmiAdvance(BigDecimal toEmiAdvance) {
		this.toEmiAdvance = toEmiAdvance;
	}

	public BigDecimal getDownpayB() {
		return downpayB;
	}

	public void setDownpayB(BigDecimal downpayB) {
		this.downpayB = downpayB;
	}

	public BigDecimal getDownpayS() {
		return downpayS;
	}

	public void setDownpayS(BigDecimal downpayS) {
		this.downpayS = downpayS;
	}

	public BigDecimal getFeeChargeAmt() {
		return FeeChargeAmt;
	}

	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
		FeeChargeAmt = feeChargeAmt;
	}

	public BigDecimal getInsuranceAmt() {
		return InsuranceAmt;
	}

	public void setInsuranceAmt(BigDecimal insuranceAmt) {
		InsuranceAmt = insuranceAmt;
	}

	public BigDecimal getAddFeeToFinance() {
		return addFeeToFinance;
	}

	public void setAddFeeToFinance(BigDecimal addFeeToFinance) {
		this.addFeeToFinance = addFeeToFinance;
	}

	public BigDecimal getPaidFee() {
		return paidFee;
	}

	public void setPaidFee(BigDecimal paidFee) {
		this.paidFee = paidFee;
	}

	public BigDecimal getBpi() {
		return bpi;
	}

	public void setBpi(BigDecimal bpi) {
		this.bpi = bpi;
	}

	public BigDecimal getDeductFeeDisb() {
		return deductFeeDisb;
	}

	public void setDeductFeeDisb(BigDecimal deductFeeDisb) {
		this.deductFeeDisb = deductFeeDisb;
	}

	public BigDecimal getDeductInsDisb() {
		return deductInsDisb;
	}

	public void setDeductInsDisb(BigDecimal deductInsDisb) {
		this.deductInsDisb = deductInsDisb;
	}

	public BigDecimal getDisbInstAmt() {
		return disbInstAmt;
	}

	public void setDisbInstAmt(BigDecimal disbInstAmt) {
		this.disbInstAmt = disbInstAmt;
	}

	public BigDecimal getPriWaived() {
		return priWaived;
	}

	public void setPriWaived(BigDecimal priWaived) {
		this.priWaived = priWaived;
	}

	public BigDecimal getPftWaived() {
		return pftWaived;
	}

	public void setPftWaived(BigDecimal pftWaived) {
		this.pftWaived = pftWaived;
	}

	public BigDecimal getFeeWaived() {
		return feeWaived;
	}

	public void setFeeWaived(BigDecimal feeWaived) {
		this.feeWaived = feeWaived;
	}

	public BigDecimal getInsWaived() {
		return insWaived;
	}

	public void setInsWaived(BigDecimal insWaived) {
		this.insWaived = insWaived;
	}

	public BigDecimal getPftDueWaived() {
		return pftDueWaived;
	}

	public void setPftDueWaived(BigDecimal pftDueWaived) {
		this.pftDueWaived = pftDueWaived;
	}

	public BigDecimal getPriDueWaived() {
		return priDueWaived;
	}

	public void setPriDueWaived(BigDecimal priDueWaived) {
		this.priDueWaived = priDueWaived;
	}

	public BigDecimal getAccrueWaived() {
		return accrueWaived;
	}

	public void setAccrueWaived(BigDecimal accrueWaived) {
		this.accrueWaived = accrueWaived;
	}

	public BigDecimal getUnAccrueWaived() {
		return unAccrueWaived;
	}

	public void setUnAccrueWaived(BigDecimal unAccrueWaived) {
		this.unAccrueWaived = unAccrueWaived;
	}

	public BigDecimal getFuturePriWaived() {
		return futurePriWaived;
	}

	public void setFuturePriWaived(BigDecimal futurePriWaived) {
		this.futurePriWaived = futurePriWaived;
	}

	public BigDecimal getDueTds() {
		return dueTds;
	}

	public void setDueTds(BigDecimal dueTds) {
		this.dueTds = dueTds;
	}

	public BigDecimal getLastSchTds() {
		return lastSchTds;
	}

	public void setLastSchTds(BigDecimal lastSchTds) {
		this.lastSchTds = lastSchTds;
	}

	public BigDecimal getAccruedTds() {
		return accruedTds;
	}

	public void setAccruedTds(BigDecimal accruedTds) {
		this.accruedTds = accruedTds;
	}

	public BigDecimal getUnAccruedTds() {
		return unAccruedTds;
	}

	public void setUnAccruedTds(BigDecimal unAccruedTds) {
		this.unAccruedTds = unAccruedTds;
	}

	public String getPartnerBankAcType() {
		return partnerBankAcType;
	}

	public void setPartnerBankAcType(String partnerBankAcType) {
		this.partnerBankAcType = partnerBankAcType;
	}

	public String getPartnerBankAc() {
		return partnerBankAc;
	}

	public void setPartnerBankAc(String partnerBankAc) {
		this.partnerBankAc = partnerBankAc;
	}

	public BigDecimal getLpi() {
		return lpi;
	}

	public void setLpi(BigDecimal lpi) {
		this.lpi = lpi;
	}

	public BigDecimal getLpiDue() {
		return lpiDue;
	}

	public void setLpiDue(BigDecimal lpiDue) {
		this.lpiDue = lpiDue;
	}

	public BigDecimal getLpiPaid() {
		return lpiPaid;
	}

	public void setLpiPaid(BigDecimal lpiPaid) {
		this.lpiPaid = lpiPaid;
	}

	public BigDecimal getLpiWaived() {
		return lpiWaived;
	}

	public void setLpiWaived(BigDecimal lpiWaived) {
		this.lpiWaived = lpiWaived;
	}

	public BigDecimal getdLPIAmz() {
		return dLPIAmz;
	}

	public void setdLPIAmz(BigDecimal dLPIAmz) {
		this.dLPIAmz = dLPIAmz;
	}

	public BigDecimal getdGSTLPIAmz() {
		return dGSTLPIAmz;
	}

	public void setdGSTLPIAmz(BigDecimal dGSTLPIAmz) {
		this.dGSTLPIAmz = dGSTLPIAmz;
	}

	public BigDecimal getdLPPAmz() {
		return dLPPAmz;
	}

	public void setdLPPAmz(BigDecimal dLPPAmz) {
		this.dLPPAmz = dLPPAmz;
	}

	public BigDecimal getdGSTLPPAmz() {
		return dGSTLPPAmz;
	}

	public void setdGSTLPPAmz(BigDecimal dGSTLPPAmz) {
		this.dGSTLPPAmz = dGSTLPPAmz;
	}

	public BigDecimal getPriWriteOff() {
		return priWriteOff;
	}

	public void setPriWriteOff(BigDecimal priWriteOff) {
		this.priWriteOff = priWriteOff;
	}

	public BigDecimal getPftWriteOff() {
		return pftWriteOff;
	}

	public void setPftWriteOff(BigDecimal pftWriteOff) {
		this.pftWriteOff = pftWriteOff;
	}

	public BigDecimal getCmtAmt() {
		return cmtAmt;
	}

	public void setCmtAmt(BigDecimal cmtAmt) {
		this.cmtAmt = cmtAmt;
	}

	public BigDecimal getChgAmt() {
		return chgAmt;
	}

	public void setChgAmt(BigDecimal chgAmt) {
		this.chgAmt = chgAmt;
	}

	public BigDecimal getCmtAvl() {
		return cmtAvl;
	}

	public void setCmtAvl(BigDecimal cmtAvl) {
		this.cmtAvl = cmtAvl;
	}

	public BigDecimal getCmtUAmt() {
		return cmtUAmt;
	}

	public void setCmtUAmt(BigDecimal cmtUAmt) {
		this.cmtUAmt = cmtUAmt;
	}

	public BigDecimal getCmtUOth() {
		return cmtUOth;
	}

	public void setCmtUOth(BigDecimal cmtUOth) {
		this.cmtUOth = cmtUOth;
	}

	public String getReceiptMode() {
		return receiptMode;
	}

	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

	public BigDecimal getDeductVasDisb() {
		return deductVasDisb;
	}

	public void setDeductVasDisb(BigDecimal deductVasDisb) {
		this.deductVasDisb = deductVasDisb;
	}

	public BigDecimal getAddVasToFinance() {
		return addVasToFinance;
	}

	public void setAddVasToFinance(BigDecimal addVasToFinance) {
		this.addVasToFinance = addVasToFinance;
	}

	public BigDecimal getVasFeeWaived() {
		return vasFeeWaived;
	}

	public void setVasFeeWaived(BigDecimal vasFeeWaived) {
		this.vasFeeWaived = vasFeeWaived;
	}

	public BigDecimal getPaidVasFee() {
		return paidVasFee;
	}

	public void setPaidVasFee(BigDecimal paidVasFee) {
		this.paidVasFee = paidVasFee;
	}

	public BigDecimal getImdAmount() {
		return imdAmount;
	}

	public void setImdAmount(BigDecimal imdAmount) {
		this.imdAmount = imdAmount;
	}

	public BigDecimal getTransfer() {
		return transfer;
	}

	public void setTransfer(BigDecimal transfer) {
		this.transfer = transfer;
	}

	public String getPostingType() {
		return postingType;
	}

	public void setPostingType(String postingType) {
		this.postingType = postingType;
	}

	public String getUserBranch() {
		return userBranch;
	}

	public void setUserBranch(String userBranch) {
		this.userBranch = userBranch;
	}

	public String getRepledge() {
		return repledge;
	}

	public void setRepledge(String repledge) {
		this.repledge = repledge;
	}

	public BigDecimal getRepledgeAmt() {
		return repledgeAmt;
	}

	public void setRepledgeAmt(BigDecimal repledgeAmt) {
		this.repledgeAmt = repledgeAmt;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public boolean isQuickDisb() {
		return quickDisb;
	}

	public void setQuickDisb(boolean quickDisb) {
		this.quickDisb = quickDisb;
	}

	public String getCashAcExecuted() {
		return cashAcExecuted;
	}

	public void setCashAcExecuted(String cashAcExecuted) {
		this.cashAcExecuted = cashAcExecuted;
	}

	public BigDecimal getdSvnAmz() {
		return dSvnAmz;
	}

	public void setdSvnAmz(BigDecimal dSvnAmz) {
		this.dSvnAmz = dSvnAmz;
	}

	public BigDecimal getDisbSvnAmount() {
		return disbSvnAmount;
	}

	public void setDisbSvnAmount(BigDecimal disbSvnAmount) {
		this.disbSvnAmount = disbSvnAmount;
	}

	public BigDecimal getAssignmentPerc() {
		return assignmentPerc;
	}

	public void setAssignmentPerc(BigDecimal assignmentPerc) {
		this.assignmentPerc = assignmentPerc;
	}

	public BigDecimal getAssignPriAmount() {
		return assignPriAmount;
	}

	public void setAssignPriAmount(BigDecimal assignPriAmount) {
		this.assignPriAmount = assignPriAmount;
	}

	public BigDecimal getAssignPftAmount() {
		return assignPftAmount;
	}

	public void setAssignPftAmount(BigDecimal assignPftAmount) {
		this.assignPftAmount = assignPftAmount;
	}

	public BigDecimal getAssignODAmount() {
		return assignODAmount;
	}

	public void setAssignODAmount(BigDecimal assignODAmount) {
		this.assignODAmount = assignODAmount;
	}

	public BigDecimal getAssignExcessAmt() {
		return assignExcessAmt;
	}

	public void setAssignExcessAmt(BigDecimal assignExcessAmt) {
		this.assignExcessAmt = assignExcessAmt;
	}

	public BigDecimal getAssignEMIAdvAmt() {
		return assignEMIAdvAmt;
	}

	public void setAssignEMIAdvAmt(BigDecimal assignEMIAdvAmt) {
		this.assignEMIAdvAmt = assignEMIAdvAmt;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public BigDecimal getAssignPaidPriAmt() {
		return assignPaidPriAmt;
	}

	public void setAssignPaidPriAmt(BigDecimal assignPaidPriAmt) {
		this.assignPaidPriAmt = assignPaidPriAmt;
	}

	public String getDealerCode() {
		return dealerCode;
	}

	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}

	public BigDecimal getAssignPartPayment() {
		return assignPartPayment;
	}

	public void setAssignPartPayment(BigDecimal assignPartPayment) {
		this.assignPartPayment = assignPartPayment;
	}

	public BigDecimal getSubVentionAmount() {
		return subVentionAmount;
	}

	public void setSubVentionAmount(BigDecimal subVentionAmount) {
		this.subVentionAmount = subVentionAmount;
	}

	public String getBusinessvertical() {
		return businessvertical;
	}

	public void setBusinessvertical(String businessvertical) {
		this.businessvertical = businessvertical;
	}

	public boolean isAlwflexi() {
		return alwflexi;
	}

	public void setAlwflexi(boolean alwflexi) {
		this.alwflexi = alwflexi;
	}

	public String getFinbranch() {
		return finbranch;
	}

	public void setFinbranch(String finbranch) {
		this.finbranch = finbranch;
	}

	public String getEntitycode() {
		return entitycode;
	}

	public void setEntitycode(String entitycode) {
		this.entitycode = entitycode;
	}

	public BigDecimal getPpAmount() {
		return ppAmount;
	}

	public void setPpAmount(BigDecimal ppAmount) {
		this.ppAmount = ppAmount;
	}

	public boolean isIntAdv() {
		return intAdv;
	}

	public void setIntAdv(boolean intAdv) {
		this.intAdv = intAdv;
	}

	public BigDecimal getIntAdjusted() {
		return intAdjusted;
	}

	public void setIntAdjusted(BigDecimal intAdjusted) {
		this.intAdjusted = intAdjusted;
	}

	public BigDecimal getIntDue() {
		return intDue;
	}

	public void setIntDue(BigDecimal intDue) {
		this.intDue = intDue;
	}

	public BigDecimal getIntTdsAdjusted() {
		return intTdsAdjusted;
	}

	public void setIntTdsAdjusted(BigDecimal intTdsAdjusted) {
		this.intTdsAdjusted = intTdsAdjusted;
	}

	public BigDecimal getEmiDue() {
		return emiDue;
	}

	public void setEmiDue(BigDecimal emiDue) {
		this.emiDue = emiDue;
	}

	public BigDecimal getPriAdjusted() {
		return priAdjusted;
	}

	public void setPriAdjusted(BigDecimal priAdjusted) {
		this.priAdjusted = priAdjusted;
	}

	public BigDecimal getBpiTds() {
		return bpiTds;
	}

	public void setBpiTds(BigDecimal bpiTds) {
		this.bpiTds = bpiTds;
	}

	public boolean isBpiToAdvInt() {
		return bpiToAdvInt;
	}

	public void setBpiToAdvInt(boolean bpiToAdvInt) {
		this.bpiToAdvInt = bpiToAdvInt;
	}

	public BigDecimal getEmiAdjusted() {
		return emiAdjusted;
	}

	public void setEmiAdjusted(BigDecimal emiAdjusted) {
		this.emiAdjusted = emiAdjusted;
	}

	public BigDecimal getEmiTdsAdjusted() {
		return emiTdsAdjusted;
	}

	public void setEmiTdsAdjusted(BigDecimal emiTdsAdjusted) {
		this.emiTdsAdjusted = emiTdsAdjusted;
	}

	public BigDecimal getdGapAmz() {
		return dGapAmz;
	}

	public void setdGapAmz(BigDecimal dGapAmz) {
		this.dGapAmz = dGapAmz;
	}

	public BigDecimal getSvAmount() {
		return svAmount;
	}

	public void setSvAmount(BigDecimal svAmount) {
		this.svAmount = svAmount;
	}

	public BigDecimal getCbAmount() {
		return cbAmount;
	}

	public void setCbAmount(BigDecimal cbAmount) {
		this.cbAmount = cbAmount;
	}

	public BigDecimal getOemSbvAmount() {
		return oemSbvAmount;
	}

	public void setOemSbvAmount(BigDecimal oemSbvAmount) {
		this.oemSbvAmount = oemSbvAmount;
	}

	public BigDecimal getDbdAmount() {
		return dbdAmount;
	}

	public void setDbdAmount(BigDecimal dbdAmount) {
		this.dbdAmount = dbdAmount;
	}

	public BigDecimal getInstChg() {
		return instChg;
	}

	public void setInstChg(BigDecimal instChg) {
		this.instChg = instChg;
	}

	public BigDecimal getInstIntChg() {
		return instIntChg;
	}

	public void setInstIntChg(BigDecimal instIntChg) {
		this.instIntChg = instIntChg;
	}

	public BigDecimal getInstPriChg() {
		return instPriChg;
	}

	public void setInstPriChg(BigDecimal instPriChg) {
		this.instPriChg = instPriChg;
	}

	public BigDecimal getPastCpzChg() {
		return pastCpzChg;
	}

	public void setPastCpzChg(BigDecimal pastCpzChg) {
		this.pastCpzChg = pastCpzChg;
	}

	public BigDecimal getRefundVasFee() {
		return refundVasFee;
	}

	public void setRefundVasFee(BigDecimal refundVasFee) {
		this.refundVasFee = refundVasFee;
	}

	public BigDecimal getProvAsst() {
		return provAsst;
	}

	public void setProvAsst(BigDecimal provAsst) {
		this.provAsst = provAsst;
	}

	public BigDecimal getRpPftPr() {
		return rpPftPr;
	}

	public void setRpPftPr(BigDecimal rpPftPr) {
		this.rpPftPr = rpPftPr;
	}

	public BigDecimal getPriPr() {
		return priPr;
	}

	public void setPriPr(BigDecimal priPr) {
		this.priPr = priPr;
	}

	public BigDecimal getPriSPr() {
		return priSPr;
	}

	public void setPriSPr(BigDecimal priSPr) {
		this.priSPr = priSPr;
	}

	public BigDecimal getRpTotPr() {
		return rpTotPr;
	}

	public void setRpTotPr(BigDecimal rpTotPr) {
		this.rpTotPr = rpTotPr;
	}

	public BigDecimal getdAmzPr() {
		return dAmzPr;
	}

	public void setdAmzPr(BigDecimal dAmzPr) {
		this.dAmzPr = dAmzPr;
	}

	public BigDecimal getInstpftPr() {
		return instpftPr;
	}

	public void setInstpftPr(BigDecimal instpftPr) {
		this.instpftPr = instpftPr;
	}

	public BigDecimal getInstpriPr() {
		return instpriPr;
	}

	public void setInstpriPr(BigDecimal instpriPr) {
		this.instpriPr = instpriPr;
	}

	public BigDecimal getVasInstAmt() {
		return vasInstAmt;
	}

	public void setVasInstAmt(BigDecimal vasInstAmt) {
		this.vasInstAmt = vasInstAmt;
	}

	public BigDecimal getAdvInst() {
		return advInst;
	}

	public void setAdvInst(BigDecimal advInst) {
		this.advInst = advInst;
	}
}
