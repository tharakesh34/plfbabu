package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.HashMap;

public class AEAmountCodes {
	
	private String 		finType;
	
	private BigDecimal	accrue				= BigDecimal.ZERO;
	private BigDecimal	dAccrue				= BigDecimal.ZERO;
	private BigDecimal	accrueBal			= BigDecimal.ZERO;

	private BigDecimal	accrueS				= BigDecimal.ZERO;
	private BigDecimal	dAccrueS			= BigDecimal.ZERO;

	private BigDecimal	amz					= BigDecimal.ZERO;
	private BigDecimal	dAmz				= BigDecimal.ZERO;
	private BigDecimal	amzBal				= BigDecimal.ZERO;

	private BigDecimal	amzNRM				= BigDecimal.ZERO;
	private BigDecimal	dAmzNRM				= BigDecimal.ZERO;

	private BigDecimal	amzPD				= BigDecimal.ZERO;
	private BigDecimal	dAmzPD				= BigDecimal.ZERO;

	private BigDecimal	amzS				= BigDecimal.ZERO;
	private BigDecimal	dAmzS				= BigDecimal.ZERO;

	private BigDecimal	disburse			= BigDecimal.ZERO;
	private BigDecimal	downpay				= BigDecimal.ZERO;
	private BigDecimal	pft					= BigDecimal.ZERO;
	private BigDecimal	pftAB				= BigDecimal.ZERO;
	private BigDecimal	pftAP				= BigDecimal.ZERO;
	private BigDecimal	cpzChg				= BigDecimal.ZERO;
	private BigDecimal	cpzTot				= BigDecimal.ZERO;
	private BigDecimal	cpzPrv				= BigDecimal.ZERO;
	private BigDecimal	cpzCur				= BigDecimal.ZERO;
	private BigDecimal	cpzNxt				= BigDecimal.ZERO;

	private BigDecimal	pftChg				= BigDecimal.ZERO;
	private BigDecimal	pftS				= BigDecimal.ZERO;
	private BigDecimal	pftSB				= BigDecimal.ZERO;
	private BigDecimal	pftSP				= BigDecimal.ZERO;
	private BigDecimal	pri					= BigDecimal.ZERO;
	private BigDecimal	priAB				= BigDecimal.ZERO;
	private BigDecimal	priAP				= BigDecimal.ZERO;
	private BigDecimal	priS				= BigDecimal.ZERO;
	private BigDecimal	priSB				= BigDecimal.ZERO;
	private BigDecimal	priSP				= BigDecimal.ZERO;
	private BigDecimal	rpPft				= BigDecimal.ZERO;
	private BigDecimal	rpTds				= BigDecimal.ZERO;
	private BigDecimal	pftDuePaid			= BigDecimal.ZERO;
	private BigDecimal	priDuePaid			= BigDecimal.ZERO;
	private BigDecimal	accruedPaid			= BigDecimal.ZERO;
	private BigDecimal	unAccruedPaid		= BigDecimal.ZERO;
	private BigDecimal	futurePriPaid		= BigDecimal.ZERO;
	private BigDecimal	lastSchPftPaid		= BigDecimal.ZERO;
	private BigDecimal	lastSchPftWaived	= BigDecimal.ZERO;
	
	private BigDecimal	rpPri				= BigDecimal.ZERO;
	private BigDecimal	rpTot				= BigDecimal.ZERO;
	private BigDecimal	instpft				= BigDecimal.ZERO;
	private BigDecimal	instpri				= BigDecimal.ZERO;
	private BigDecimal	insttot				= BigDecimal.ZERO;
	private BigDecimal	refund				= BigDecimal.ZERO;
	private BigDecimal	rebate				= BigDecimal.ZERO;
	private BigDecimal	insRefund			= BigDecimal.ZERO;
	private BigDecimal	InsPay				= BigDecimal.ZERO;
	private BigDecimal	schFeePay			= BigDecimal.ZERO;
	private BigDecimal	suplRentPay			= BigDecimal.ZERO;
	private BigDecimal	incrCostPay			= BigDecimal.ZERO;
	private BigDecimal	woPayAmt			= BigDecimal.ZERO;
	private int			ODDays				= 0;
	private int			daysFromFullyPaid	= 0;
	private int			ODInst				= 0;
	private int			paidInst			= 0;

	private BigDecimal	provAmt				= BigDecimal.ZERO;
	private BigDecimal	provDue				= BigDecimal.ZERO;
	private BigDecimal	suspNow				= BigDecimal.ZERO;
	private BigDecimal	suspRls				= BigDecimal.ZERO;
	private BigDecimal	penalty				= BigDecimal.ZERO;
	private BigDecimal	waiver				= BigDecimal.ZERO;

	private BigDecimal	penaltyPaid			= BigDecimal.ZERO;
	private BigDecimal	penaltyDue			= BigDecimal.ZERO;
	private BigDecimal	penaltyWaived		= BigDecimal.ZERO;
	private BigDecimal	accrueTsfd			= BigDecimal.ZERO;
	private BigDecimal	prvAccrueTsfd		= BigDecimal.ZERO;

	private BigDecimal	totalWriteoff		= BigDecimal.ZERO;
	private BigDecimal	excessBal			= BigDecimal.ZERO;
	private BigDecimal	toExcessAmt			= BigDecimal.ZERO;
	private BigDecimal	toEmiAdvance		= BigDecimal.ZERO;

	private BigDecimal	downpayB			= BigDecimal.ZERO;
	private BigDecimal	downpayS			= BigDecimal.ZERO;
	private BigDecimal	FeeChargeAmt		= BigDecimal.ZERO;
	private BigDecimal	InsuranceAmt		= BigDecimal.ZERO;

	private BigDecimal	addFeeToFinance		= BigDecimal.ZERO;
	private BigDecimal	paidFee				= BigDecimal.ZERO;
	private BigDecimal	bpi					= BigDecimal.ZERO;
	private BigDecimal	deductFeeDisb		= BigDecimal.ZERO;
	private BigDecimal	deductInsDisb		= BigDecimal.ZERO;
	private BigDecimal	disbInstAmt			= BigDecimal.ZERO;

	private BigDecimal	priWaived			= BigDecimal.ZERO;
	private BigDecimal	pftWaived			= BigDecimal.ZERO;
	private BigDecimal	feeWaived			= BigDecimal.ZERO;
	private BigDecimal	insWaived			= BigDecimal.ZERO;
	private BigDecimal	pftDueWaived		= BigDecimal.ZERO;
	private BigDecimal	priDueWaived		= BigDecimal.ZERO;
	private BigDecimal	accrueWaived		= BigDecimal.ZERO;
	private BigDecimal	unAccrueWaived		= BigDecimal.ZERO;
	private BigDecimal	futurePriWaived		= BigDecimal.ZERO;
	private BigDecimal	dueTds				= BigDecimal.ZERO;
	private BigDecimal	lastSchTds		= BigDecimal.ZERO;

	//For Disbursement Instructions used in SUBHEAD
	private String		partnerBankAcType;
	private String		partnerBankAc;

	//Commitment
	private BigDecimal	cmtAmt				= BigDecimal.ZERO;
	private BigDecimal	chgAmt				= BigDecimal.ZERO;
	private BigDecimal	cmtAvl				= BigDecimal.ZERO;
	private BigDecimal	cmtUAmt				= BigDecimal.ZERO;
	private BigDecimal	cmtUOth				= BigDecimal.ZERO;
	
	// Present mode
	private String		receiptMode;
	
	// VAS Fees
	private BigDecimal	deductVasDisb				= BigDecimal.ZERO;
	private BigDecimal	addVasToFinance				= BigDecimal.ZERO;
	private BigDecimal	vasFeeWaived				= BigDecimal.ZERO;
	private BigDecimal	paidVasFee					= BigDecimal.ZERO;

	public AEAmountCodes() {
		super();
	}

	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		getDeclaredFieldValues(map);

		return map;
	}

	public HashMap<String, Object> getDeclaredFieldValues(HashMap<String, Object> map) {

		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				//"ae_" Should be in small case only, if we want to change the case we need to update the configuration fields as well.
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

	public BigDecimal getInstpri() {
		return instpri;
	}

	public void setInstpri(BigDecimal instpri) {
		this.instpri = instpri;
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

	public BigDecimal getCmtAmt() {
		return cmtAmt;
	}

	public void setCmtAmt(BigDecimal cmtAmt) {
		this.cmtAmt = cmtAmt;
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

	public BigDecimal getChgAmt() {
		return chgAmt;
	}

	public void setChgAmt(BigDecimal chgAmt) {
		this.chgAmt = chgAmt;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public BigDecimal getRpTds() {
		return rpTds;
	}

	public void setRpTds(BigDecimal rpTds) {
		this.rpTds = rpTds;
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

	public BigDecimal getAccruedPaid() {
		return accruedPaid;
	}

	public void setAccruedPaid(BigDecimal accruedPaid) {
		this.accruedPaid = accruedPaid;
	}

	public BigDecimal getFuturePriPaid() {
		return futurePriPaid;
	}

	public void setFuturePriPaid(BigDecimal futurePriPaid) {
		this.futurePriPaid = futurePriPaid;
	}

	public BigDecimal getAccrueWaived() {
		return accrueWaived;
	}

	public void setAccrueWaived(BigDecimal accrueWaived) {
		this.accrueWaived = accrueWaived;
	}

	public BigDecimal getFuturePriWaived() {
		return futurePriWaived;
	}

	public void setFuturePriWaived(BigDecimal futurePriWaived) {
		this.futurePriWaived = futurePriWaived;
	}

	public BigDecimal getPftDuePaid() {
		return pftDuePaid;
	}

	public void setPftDuePaid(BigDecimal pftDuePaid) {
		this.pftDuePaid = pftDuePaid;
	}

	public BigDecimal getUnAccruedPaid() {
		return unAccruedPaid;
	}

	public void setUnAccruedPaid(BigDecimal unAccruedPaid) {
		this.unAccruedPaid = unAccruedPaid;
	}

	public BigDecimal getPftDueWaived() {
		return pftDueWaived;
	}

	public void setPftDueWaived(BigDecimal pftDueWaived) {
		this.pftDueWaived = pftDueWaived;
	}

	public BigDecimal getUnAccrueWaived() {
		return unAccrueWaived;
	}

	public void setUnAccrueWaived(BigDecimal unAccrueWaived) {
		this.unAccrueWaived = unAccrueWaived;
	}

	public BigDecimal getPriDuePaid() {
		return priDuePaid;
	}

	public void setPriDuePaid(BigDecimal priDuePaid) {
		this.priDuePaid = priDuePaid;
	}

	public BigDecimal getPriDueWaived() {
		return priDueWaived;
	}

	public void setPriDueWaived(BigDecimal priDueWaived) {
		this.priDueWaived = priDueWaived;
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
	
}
