package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

public class AEAmountCodes {

	private String		finReference;
	private String		finType;
	private String		finEvent;
	private Date		postDate;
	private Date		valueDate;
	private Date		schdDate;
	private String		branch;
	private String		ccy;
	private long		custID;
	private boolean		newRecord			= false;
	private String		moduleDefiner;
	private String		disbAccountID;

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

	//Summary Details
	private BigDecimal	penaltyPaid			= BigDecimal.ZERO;
	private BigDecimal	penaltyDue			= BigDecimal.ZERO;
	private BigDecimal	penaltyWaived		= BigDecimal.ZERO;
	private BigDecimal	accrueTsfd			= null;
	private BigDecimal	prvAccrueTsfd		= BigDecimal.ZERO;

	private BigDecimal	totalWriteoff		= BigDecimal.ZERO;
	private BigDecimal	excessAmt			= BigDecimal.ZERO;
	private BigDecimal	emiInAdvance		= BigDecimal.ZERO;
	private BigDecimal	payableAdvise		= BigDecimal.ZERO;
	private BigDecimal	excessBal			= BigDecimal.ZERO;

	private BigDecimal	downpayB			= BigDecimal.ZERO;
	private BigDecimal	downpayS			= BigDecimal.ZERO;
	private BigDecimal	FeeChargeAmt		= BigDecimal.ZERO;
	private BigDecimal	InsuranceAmt		= BigDecimal.ZERO;

	private BigDecimal	addFeeToFinance		= BigDecimal.ZERO;
	private BigDecimal	paidFee				= BigDecimal.ZERO;
	private BigDecimal	bpi					= BigDecimal.ZERO;
	private BigDecimal	cmtAmt				= BigDecimal.ZERO;
	private BigDecimal	deductFeeDisb		= BigDecimal.ZERO;
	private BigDecimal	deductInsDisb		= BigDecimal.ZERO;
	private BigDecimal	disbInstAmt		    = BigDecimal.ZERO;
	
	private BigDecimal	priWaived		= BigDecimal.ZERO;
	private BigDecimal	pftWaived		= BigDecimal.ZERO;
	private BigDecimal	feeWaived		= BigDecimal.ZERO;
	private BigDecimal	insWaived		= BigDecimal.ZERO;
	

	public AEAmountCodes() {

	}

	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		getDeclaredFieldValues(map);

		return map;
	}
	
	public HashMap<String, Object> getDeclaredFieldValues(HashMap<String, Object> map) {
		
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				//"ft_" Should be in small case only, if we want to change the case we need to update the configuration fields as well.
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

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getAccrue() {
		return accrue;
	}

	public void setAccrue(BigDecimal accrue) {
		this.accrue = accrue;
	}

	public BigDecimal getAccrueS() {
		return accrueS;
	}

	public void setAccrueS(BigDecimal accrueS) {
		this.accrueS = accrueS;
	}

	public BigDecimal getDAccrue() {
		return dAccrue;
	}

	public void setDAccrue(BigDecimal dAccrue) {
		this.dAccrue = dAccrue;
	}

	public BigDecimal getAmz() {
		return amz;
	}

	public void setAmz(BigDecimal amz) {
		this.amz = amz;
	}

	public BigDecimal getAmzS() {
		return amzS;
	}

	public void setAmzS(BigDecimal amzS) {
		this.amzS = amzS;
	}

	public BigDecimal getdAmz() {
		return dAmz;
	}

	public void setdAmz(BigDecimal dAmz) {
		this.dAmz = dAmz;
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

	public BigDecimal getPftChg() {
		return pftChg;
	}

	public void setPftChg(BigDecimal pftChg) {
		this.pftChg = pftChg;
	}

	public BigDecimal getCpzChg() {
		return cpzChg;
	}

	public void setCpzChg(BigDecimal cpzChg) {
		this.cpzChg = cpzChg;
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

	public BigDecimal getProvAmt() {
		return provAmt;
	}

	public void setProvAmt(BigDecimal provAmt) {
		this.provAmt = provAmt;
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

	public BigDecimal getdAccrue() {
		return dAccrue;
	}

	public void setdAccrue(BigDecimal dAccrue) {
		this.dAccrue = dAccrue;
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

	public void setAccrueTsfd(BigDecimal accrueTsfd) {
		this.accrueTsfd = accrueTsfd;
	}

	public BigDecimal getAccrueTsfd() {
		return accrueTsfd;
	}

	public BigDecimal getInsRefund() {
		return insRefund;
	}

	public void setInsRefund(BigDecimal insRefund) {
		this.insRefund = insRefund;
	}

	public BigDecimal getTotalWriteoff() {
		return totalWriteoff;
	}

	public void setTotalWriteoff(BigDecimal totalWriteoff) {
		this.totalWriteoff = totalWriteoff;
	}

	public BigDecimal getPrvAccrueTsfd() {
		return prvAccrueTsfd;
	}

	public void setPrvAccrueTsfd(BigDecimal prvAccrueTsfd) {
		this.prvAccrueTsfd = prvAccrueTsfd;
	}

	public int getPaidInst() {
		return paidInst;
	}

	public void setPaidInst(int paidInst) {
		this.paidInst = paidInst;
	}

	public BigDecimal getInsPay() {
		return InsPay;
	}

	public void setInsPay(BigDecimal InsPay) {
		this.InsPay = InsPay;
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

	public BigDecimal getdAccrueS() {
		return dAccrueS;
	}

	public void setdAccrueS(BigDecimal dAccrueS) {
		this.dAccrueS = dAccrueS;
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

	public BigDecimal getdAmzS() {
		return dAmzS;
	}

	public void setdAmzS(BigDecimal dAmzS) {
		this.dAmzS = dAmzS;
	}

	public BigDecimal getExcessAmt() {
		return excessAmt;
	}

	public void setExcessAmt(BigDecimal excessAmt) {
		this.excessAmt = excessAmt;
	}

	public BigDecimal getEmiInAdvance() {
		return emiInAdvance;
	}

	public void setEmiInAdvance(BigDecimal emiInAdvance) {
		this.emiInAdvance = emiInAdvance;
	}

	public BigDecimal getPayableAdvise() {
		return payableAdvise;
	}

	public void setPayableAdvise(BigDecimal payableAdvise) {
		this.payableAdvise = payableAdvise;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getSchdDate() {
		return schdDate;
	}

	public void setSchdDate(Date schdDate) {
		this.schdDate = schdDate;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getModuleDefiner() {
		return moduleDefiner;
	}

	public void setModuleDefiner(String moduleDefiner) {
		this.moduleDefiner = moduleDefiner;
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

	public String getDisbAccountID() {
		return disbAccountID;
	}

	public void setDisbAccountID(String disbAccountID) {
		this.disbAccountID = disbAccountID;
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

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
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

	public BigDecimal getAddFeeToFinance() {
		return addFeeToFinance;
	}

	public void setAddFeeToFinance(BigDecimal addFeeToFinance) {
		this.addFeeToFinance = addFeeToFinance;
	}

	public BigDecimal getBpi() {
		return bpi;
	}

	public void setBpi(BigDecimal bpi) {
		this.bpi = bpi;
	}

	public BigDecimal getCmtAmt() {
		return cmtAmt;
	}

	public void setCmtAmt(BigDecimal cmtAmt) {
		this.cmtAmt = cmtAmt;
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

	public BigDecimal getAmzBal() {
		return amzBal;
	}

	public void setAmzBal(BigDecimal amzBal) {
		this.amzBal = amzBal;
	}

	public BigDecimal getAccrueBal() {
		return accrueBal;
	}

	public void setAccrueBal(BigDecimal accrueBal) {
		this.accrueBal = accrueBal;
	}

	public BigDecimal getExcessBal() {
		return excessBal;
	}

	public void setExcessBal(BigDecimal excessBal) {
		this.excessBal = excessBal;
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

	public BigDecimal getPaidFee() {
		return paidFee;
	}

	public void setPaidFee(BigDecimal paidFee) {
		this.paidFee = paidFee;
	}
	
}
