package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.Date;

public class AEAmountCodes {

	private String finReference;
	private Date lastRepayPftDate;
	private Date nextRepayPftDate;
	private Date lastRepayRvwDate;
	private Date nextRepayRvwDate;

	private int cPNoOfDays = 0;
	private int cpDaysTill = 0;
	private int daysDiff = 0;

	private BigDecimal astValO = BigDecimal.ZERO;
	private BigDecimal astValC = BigDecimal.ZERO;
	private BigDecimal accrue = BigDecimal.ZERO;
	private BigDecimal accrueS = BigDecimal.ZERO;
	private BigDecimal dAccrue = BigDecimal.ZERO;
	private BigDecimal nAccrue = BigDecimal.ZERO;
	private BigDecimal lAccrue = BigDecimal.ZERO;
	private BigDecimal amz = BigDecimal.ZERO;
	private BigDecimal amzS = BigDecimal.ZERO;
	private BigDecimal dAmz = BigDecimal.ZERO;
	private BigDecimal nAmz = BigDecimal.ZERO;
	private BigDecimal lAmz = BigDecimal.ZERO;
	private BigDecimal disburse = BigDecimal.ZERO;
	private BigDecimal downpay = BigDecimal.ZERO;
	private BigDecimal pft = BigDecimal.ZERO;
	private BigDecimal pftAB = BigDecimal.ZERO;
	private BigDecimal pftAP = BigDecimal.ZERO;
	private BigDecimal cpzChg = BigDecimal.ZERO;
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
	private BigDecimal rpPri = BigDecimal.ZERO;
	private BigDecimal rpTot = BigDecimal.ZERO;
	private BigDecimal instpft = BigDecimal.ZERO;
	private BigDecimal instpri = BigDecimal.ZERO;
	private BigDecimal insttot = BigDecimal.ZERO;
	private BigDecimal refund = BigDecimal.ZERO;
	private BigDecimal rebate = BigDecimal.ZERO;
	private BigDecimal insRefund = BigDecimal.ZERO;
	private BigDecimal InsPay = BigDecimal.ZERO;
	private BigDecimal schFeePay = BigDecimal.ZERO;
	private BigDecimal suplRentPay = BigDecimal.ZERO;
	private BigDecimal incrCostPay = BigDecimal.ZERO;
	private BigDecimal cpzTot = BigDecimal.ZERO;
	private BigDecimal cpzPrv = BigDecimal.ZERO;
	private BigDecimal cpzCur = BigDecimal.ZERO;
	private BigDecimal cpzNxt = BigDecimal.ZERO;
	private BigDecimal pftInAdv = BigDecimal.ZERO;
	private BigDecimal provAmt = BigDecimal.ZERO;
	private BigDecimal woPayAmt = BigDecimal.ZERO;
	private int elpDays = 0;
	private int elpMnts = 0;
	private int elpTerms = 0;
	private int ttlDays = 0;
	private int ttlMnts = 0;
	private int ttlTerms = 0;
	private int ODDays = 0;
	private int CRBODDays = 0;
	private int daysFromFullyPaid = 0;
	private int ODInst = 0;
	private int CRBODInst = 0;
	private int paidInst = 0;

	private BigDecimal PROVDUE = BigDecimal.ZERO;
	private BigDecimal SUSPNOW = BigDecimal.ZERO;
	private BigDecimal SUSPRLS = BigDecimal.ZERO;
	private BigDecimal PENALTY = BigDecimal.ZERO;
	private BigDecimal WAIVER = BigDecimal.ZERO;
	private BigDecimal ODCPLShare = BigDecimal.ZERO;
	
	//ISTISNA Details
	private BigDecimal CLAIMAMT = BigDecimal.ZERO;
	private BigDecimal DEFFEREDCOST = BigDecimal.ZERO;
	private BigDecimal CURRETBILL = BigDecimal.ZERO;
	private BigDecimal TTLRETBILL = BigDecimal.ZERO;
	
	//Summary Details
	private Date fullyPaidDate;
	private BigDecimal curReducingRate = BigDecimal.ZERO;
	private BigDecimal curFlatRate = BigDecimal.ZERO;
	private BigDecimal nextSchdPri = BigDecimal.ZERO;
	private BigDecimal nextSchdPft = BigDecimal.ZERO;
	private BigDecimal nextSchdPriBal = BigDecimal.ZERO;
	private BigDecimal nextSchdPftBal = BigDecimal.ZERO;
	private boolean isPftInSusp = false; 
	private String finWorstSts;
	private BigDecimal firstRepayAmt = BigDecimal.ZERO;
	private Date firstRepayDate;
	private BigDecimal lastRepayAmt = BigDecimal.ZERO;
	private BigDecimal priOD = BigDecimal.ZERO;
	private BigDecimal pftOD = BigDecimal.ZERO;
	private BigDecimal CRBPriOD = BigDecimal.ZERO;
	private BigDecimal CRBPftOD = BigDecimal.ZERO;
	private BigDecimal penaltyPaid = BigDecimal.ZERO;
	private BigDecimal penaltyDue = BigDecimal.ZERO;
	private BigDecimal penaltyWaived = BigDecimal.ZERO;
	private BigDecimal accrueTsfd = null;
	private BigDecimal prvAccrueTsfd = BigDecimal.ZERO;
	private Date firstODDate;
	private Date lastODDate;
	private Date cRBFirstODDate;
	private Date cRBLastODDate;
	
	private Date 	   lastRpySchDate;//Either Partially or Fully
	private Date 	   nextRpySchDate;//Either partially or Fully
	private BigDecimal lastRpySchPri =  BigDecimal.ZERO;// Last paid Schedule Principal
	private BigDecimal lastRpySchPft =  BigDecimal.ZERO;// Last paid Schedule Profit
	private Date       latestWriteOffDate; //latest WriteOff Schedule Date
	private BigDecimal totalWriteoff = BigDecimal.ZERO; //Total WriteOff Amount (P+P)
	
	//Depreciation Principal Amount
	private BigDecimal accumulatedDepPri = BigDecimal.ZERO;
	private BigDecimal depreciatePri = BigDecimal.ZERO;
	private boolean finisActive ;
	
	// Repay Account Selection Flags
	private boolean repayInAdv = false; 
	private boolean repayInPD = false; 
	private boolean repayInSusp = false; 
	
	//Fee Details
	private String custEmpSts;
	private boolean alwDPSP = false;
	private boolean salariedCustomer;
	private boolean rolloverFinance;
	private int guarantorCount = 0;
	private BigDecimal commissionRate = BigDecimal.ZERO;
	private String commissionType;
	private boolean ddaModified = false;
	private String assetProduct  ;
	private String assetPurpose   ;
	private boolean preApprovalExpired = false;
	private boolean preApprovalFinance = false;

	
	
	public AEAmountCodes() {
		
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

	public Date getLastRepayPftDate() {
		return lastRepayPftDate;
	}
	public void setLastRepayPftDate(Date lastRepayPftDate) {
		this.lastRepayPftDate = lastRepayPftDate;
	}

	public Date getNextRepayPftDate() {
		return nextRepayPftDate;
	}
	public void setNextRepayPftDate(Date nextRepayPftDate) {
		this.nextRepayPftDate = nextRepayPftDate;
	}

	public Date getLastRepayRvwDate() {
		return lastRepayRvwDate;
	}
	public void setLastRepayRvwDate(Date lastRepayRvwDate) {
		this.lastRepayRvwDate = lastRepayRvwDate;
	}

	public Date getNextRepayRvwDate() {
		return nextRepayRvwDate;
	}
	public void setNextRepayRvwDate(Date nextRepayRvwDate) {
		this.nextRepayRvwDate = nextRepayRvwDate;
	}

	public int getcPNoOfDays() {
		return cPNoOfDays;
	}
	public void setcPNoOfDays(int cPNoOfDays) {
		this.cPNoOfDays = cPNoOfDays;
	}

	public int getCPNoOfDays() {
		return cPNoOfDays;
	}
	public void setCPNoOfDays(int cPNoOfDays) {
		this.cPNoOfDays = cPNoOfDays;
	}

	public int getCpDaysTill() {
		return cpDaysTill;
	}
	public void setCpDaysTill(int cpDaysTill) {
		this.cpDaysTill = cpDaysTill;
	}

	public int getDaysDiff() {
		return daysDiff;
	}
	public void setDaysDiff(int daysDiff) {
		this.daysDiff = daysDiff;
	}

	public BigDecimal getAstValO() {
		return astValO;
	}
	public void setAstValO(BigDecimal astValO) {
		this.astValO = astValO;
	}

	public BigDecimal getAstValC() {
		return astValC;
	}
	public void setAstValC(BigDecimal astValC) {
		this.astValC = astValC;
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

	public BigDecimal getNAccrue() {
		return nAccrue;
	}
	public void setNAccrue(BigDecimal nAccrue) {
		this.nAccrue = nAccrue;
	}

	public BigDecimal getlAccrue() {
		return lAccrue;
	}
	public void setlAccrue(BigDecimal lAccrue) {
		this.lAccrue = lAccrue;
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
	
	public BigDecimal getnAmz() {
		return nAmz;
	}
	public void setnAmz(BigDecimal nAmz) {
		this.nAmz = nAmz;
	}

	public BigDecimal getlAmz() {
		return lAmz;
	}
	public void setlAmz(BigDecimal lAmz) {
		this.lAmz = lAmz;
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

	public BigDecimal getPftInAdv() {
		return pftInAdv;
	}
	public void setPftInAdv(BigDecimal pftInAdv) {
		this.pftInAdv = pftInAdv;
	}

	public BigDecimal getProvAmt() {
    	return provAmt;
    }
	public void setProvAmt(BigDecimal provAmt) {
    	this.provAmt = provAmt;
    }

	public int getElpDays() {
		return elpDays;
	}
	public void setElpDays(int elpDays) {
		this.elpDays = elpDays;
	}

	public int getElpMnts() {
		return elpMnts;
	}
	public void setElpMnts(int elpMnts) {
		this.elpMnts = elpMnts;
	}

	public int getElpTerms() {
		return elpTerms;
	}
	public void setElpTerms(int elpTerms) {
		this.elpTerms = elpTerms;
	}

	public int getTtlDays() {
		return ttlDays;
	}
	public void setTtlDays(int ttlDays) {
		this.ttlDays = ttlDays;
	}

	public int getTtlMnts() {
		return ttlMnts;
	}
	public void setTtlMnts(int ttlMnts) {
		this.ttlMnts = ttlMnts;
	}

	public int getTtlTerms() {
		return ttlTerms;
	}
	public void setTtlTerms(int ttlTerms) {
		this.ttlTerms = ttlTerms;
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
	
	public BigDecimal getPROVDUE() {
    	return PROVDUE;
    }
	public void setPROVDUE(BigDecimal pROVDUE) {
    	this.PROVDUE = pROVDUE;
    }

	public BigDecimal getSUSPNOW() {
		return SUSPNOW;
	}
	public void setSUSPNOW(BigDecimal sUSPNOW) {
		this.SUSPNOW = sUSPNOW;
	}

	public BigDecimal getSUSPRLS() {
		return SUSPRLS;
	}
	public void setSUSPRLS(BigDecimal sUSPRLS) {
		this.SUSPRLS = sUSPRLS;
	}

	public BigDecimal getPENALTY() {
		return PENALTY;
	}
	public void setPENALTY(BigDecimal pENALTY) {
		this.PENALTY = pENALTY;
	}

	public BigDecimal getWAIVER() {
		return WAIVER;
	}
	public void setWAIVER(BigDecimal wAIVER) {
		this.WAIVER = wAIVER;
	}

	public void setODCPLShare(BigDecimal oDCPLShare) {
		this.ODCPLShare = oDCPLShare;
	}
	public BigDecimal getODCPLShare() {
		return ODCPLShare;
	}
	
	public BigDecimal getCLAIMAMT() {
    	return CLAIMAMT;
    }
	public void setCLAIMAMT(BigDecimal cLAIMAMT) {
		this.CLAIMAMT = cLAIMAMT;
    }
	
	public BigDecimal getDEFFEREDCOST() {
    	return DEFFEREDCOST;
    }
	public void setDEFFEREDCOST(BigDecimal dEFFEREDCOST) {
		this.DEFFEREDCOST = dEFFEREDCOST;
    }
	
	public BigDecimal getCURRETBILL() {
    	return CURRETBILL;
    }
	public void setCURRETBILL(BigDecimal cURRETBILL) {
		this.CURRETBILL = cURRETBILL;
    }
	
	public BigDecimal getTTLRETBILL() {
    	return TTLRETBILL;
    }
	public void setTTLRETBILL(BigDecimal tTLRETBILL) {
		this.TTLRETBILL = tTLRETBILL;
    }
	public BigDecimal getdAccrue() {
    	return dAccrue;
    }
	public void setdAccrue(BigDecimal dAccrue) {
    	this.dAccrue = dAccrue;
    }
	public BigDecimal getnAccrue() {
    	return nAccrue;
    }
	public void setnAccrue(BigDecimal nAccrue) {
    	this.nAccrue = nAccrue;
    }
	public Date getFullyPaidDate() {
    	return fullyPaidDate;
    }
	public void setFullyPaidDate(Date fullyPaidDate) {
    	this.fullyPaidDate = fullyPaidDate;
    }
	public BigDecimal getCurReducingRate() {
    	return curReducingRate;
    }
	public void setCurReducingRate(BigDecimal curReducingRate) {
    	this.curReducingRate = curReducingRate;
    }
	public BigDecimal getCurFlatRate() {
    	return curFlatRate;
    }
	public void setCurFlatRate(BigDecimal curFlatRate) {
    	this.curFlatRate = curFlatRate;
    }
	public BigDecimal getNextSchdPri() {
    	return nextSchdPri;
    }
	public void setNextSchdPri(BigDecimal nextSchdPri) {
    	this.nextSchdPri = nextSchdPri;
    }
	public BigDecimal getNextSchdPft() {
    	return nextSchdPft;
    }
	public void setNextSchdPft(BigDecimal nextSchdPft) {
    	this.nextSchdPft = nextSchdPft;
    }
	public BigDecimal getNextSchdPriBal() {
    	return nextSchdPriBal;
    }
	public void setNextSchdPriBal(BigDecimal nextSchdPriBal) {
    	this.nextSchdPriBal = nextSchdPriBal;
    }
	public BigDecimal getNextSchdPftBal() {
    	return nextSchdPftBal;
    }
	public void setNextSchdPftBal(BigDecimal nextSchdPftBal) {
    	this.nextSchdPftBal = nextSchdPftBal;
    }
	public boolean isPftInSusp() {
    	return isPftInSusp;
    }
	public void setPftInSusp(boolean isPftInSusp) {
    	this.isPftInSusp = isPftInSusp;
    }
	public String getFinWorstSts() {
    	return finWorstSts;
    }
	public void setFinWorstSts(String finWorstSts) {
    	this.finWorstSts = finWorstSts;
    }
	public BigDecimal getFirstRepayAmt() {
    	return firstRepayAmt;
    }
	public void setFirstRepayAmt(BigDecimal firstRepayAmt) {
    	this.firstRepayAmt = firstRepayAmt;
    }
	public BigDecimal getLastRepayAmt() {
    	return lastRepayAmt;
    }
	public void setLastRepayAmt(BigDecimal lastRepayAmt) {
    	this.lastRepayAmt = lastRepayAmt;
    }
	public BigDecimal getPriOD() {
    	return priOD;
    }
	public void setPriOD(BigDecimal priOD) {
    	this.priOD = priOD;
    }
	public BigDecimal getPftOD() {
    	return pftOD;
    }
	public void setPftOD(BigDecimal pftOD) {
    	this.pftOD = pftOD;
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
	
	public Date getFirstODDate() {
    	return firstODDate;
    }
	public void setFirstODDate(Date firstODDate) {
    	this.firstODDate = firstODDate;
    }
	
	public Date getLastODDate() {
    	return lastODDate;
    }
	public void setLastODDate(Date lastODDate) {
    	this.lastODDate = lastODDate;
    }
	public Date getFirstRepayDate() {
	    return firstRepayDate;
    }
	public void setFirstRepayDate(Date firstRepayDate) {
	    this.firstRepayDate = firstRepayDate;
    }
	public BigDecimal getInsRefund() {
	    return insRefund;
    }
	public void setInsRefund(BigDecimal insRefund) {
	    this.insRefund = insRefund;
    }
	public Date getLastRpySchDate() {
    	return lastRpySchDate;
    }
	public void setLastRpySchDate(Date lastRpySchDate) {
    	this.lastRpySchDate = lastRpySchDate;
    }
	public Date getNextRpySchDate() {
    	return nextRpySchDate;
    }
	public void setNextRpySchDate(Date nextRpySchDate) {
    	this.nextRpySchDate = nextRpySchDate;
    }
	public BigDecimal getLastRpySchPri() {
    	return lastRpySchPri;
    }
	public void setLastRpySchPri(BigDecimal lastRpySchPri) {
    	this.lastRpySchPri = lastRpySchPri;
    }
	public BigDecimal getLastRpySchPft() {
    	return lastRpySchPft;
    }
	public void setLastRpySchPft(BigDecimal lastRpySchPft) {
    	this.lastRpySchPft = lastRpySchPft;
    }
	public Date getLatestWriteOffDate() {
    	return latestWriteOffDate;
    }
	public void setLatestWriteOffDate(Date latestWriteOffDate) {
    	this.latestWriteOffDate = latestWriteOffDate;
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
	public BigDecimal getAccumulatedDepPri() {
    	return accumulatedDepPri;
    }
	public void setAccumulatedDepPri(BigDecimal accumulatedDepPri) {
    	this.accumulatedDepPri = accumulatedDepPri;
    }
	public BigDecimal getDepreciatePri() {
	    return depreciatePri;
    }
	public void setDepreciatePri(BigDecimal depreciatePri) {
	    this.depreciatePri = depreciatePri;
    }
	
	public Date getCRBFirstODDate() {
	    return cRBFirstODDate;
    }
	public void setCRBFirstODDate(Date cRBFirstODDate) {
	    this.cRBFirstODDate = cRBFirstODDate;
    }
	
	public Date getCRBLastODDate() {
	    return cRBLastODDate;
    }
	public void setCRBLastODDate(Date cRBLastODDate) {
	    this.cRBLastODDate = cRBLastODDate;
    }
	public int getCRBODDays() {
	    return CRBODDays;
    }
	public void setCRBODDays(int cRBODDays) {
	    CRBODDays = cRBODDays;
    }
	public int getCRBODInst() {
	    return CRBODInst;
    }
	public void setCRBODInst(int cRBODInst) {
	    CRBODInst = cRBODInst;
    }
	public BigDecimal getCRBPriOD() {
		return CRBPriOD;
	}
	public void setCRBPriOD(BigDecimal cRBPriOD) {
		CRBPriOD = cRBPriOD;
	}
	public BigDecimal getCRBPftOD() {
		return CRBPftOD;
	}
	public void setCRBPftOD(BigDecimal cRBPftOD) {
		CRBPftOD = cRBPftOD;
	}
	
	public boolean isFinisActive() {
		return finisActive;
	}
	public void setFinisActive(boolean finisActive) {
		this.finisActive = finisActive;
	}
	
	public String getCustEmpSts() {
		return custEmpSts;
	}
	public void setCustEmpSts(String custEmpSts) {
		this.custEmpSts = custEmpSts;
	}
	
	public boolean isAlwDPSP() {
	    return alwDPSP;
    }
	public void setAlwDPSP(boolean alwDPSP) {
	    this.alwDPSP = alwDPSP;
    }
	
	public boolean isSalariedCustomer() {
	    return salariedCustomer;
    }
	public void setSalariedCustomer(boolean salariedCustomer) {
	    this.salariedCustomer = salariedCustomer;
    }
	public boolean isRolloverFinance() {
	    return rolloverFinance;
    }
	public void setRolloverFinance(boolean rolloverFinance) {
	    this.rolloverFinance = rolloverFinance;
    }

	public int getGuarantorCount() {
	    return guarantorCount;
    }
	public void setGuarantorCount(int guarantorCount) {
	    this.guarantorCount = guarantorCount;
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

	public BigDecimal getCommissionRate() {
		return commissionRate;
	}

	public void setCommissionRate(BigDecimal commissionRate) {
		this.commissionRate = commissionRate;
	}

	public BigDecimal getWoPayAmt() {
		return woPayAmt;
	}

	public void setWoPayAmt(BigDecimal woPayAmt) {
		this.woPayAmt = woPayAmt;
	}

	public boolean isDdaModified() {
		return ddaModified;
	}
	public void setDdaModified(boolean ddaModified) {
		this.ddaModified = ddaModified;
	}

	public String getCommissionType() {
		return commissionType;
	}

	public void setCommissionType(String commissionType) {
		this.commissionType = commissionType;
	}

	public String getAssetProduct() {
		return assetProduct;
	}
	public void setAssetProduct(String assetProduct) {
		this.assetProduct = assetProduct;
	}

	public String getAssetPurpose() {
		return assetPurpose;
	}
	public void setAssetPurpose(String assetPurpose) {
		this.assetPurpose = assetPurpose;
	}

	public boolean isPreApprovalExpired() {
		return preApprovalExpired;
	}
	public void setPreApprovalExpired(boolean preApprovalExpired) {
		this.preApprovalExpired = preApprovalExpired;
	}

	public boolean isPreApprovalFinance() {
		return preApprovalFinance;
	}
	public void setPreApprovalFinance(boolean preApprovalFinance) {
		this.preApprovalFinance = preApprovalFinance;
	}

	public boolean isRepayInAdv() {
		return repayInAdv;
	}
	public void setRepayInAdv(boolean repayInAdv) {
		this.repayInAdv = repayInAdv;
	}

	public boolean isRepayInPD() {
		return repayInPD;
	}
	public void setRepayInPD(boolean repayInPD) {
		this.repayInPD = repayInPD;
	}

	public boolean isRepayInSusp() {
		return repayInSusp;
	}
	public void setRepayInSusp(boolean repayInSusp) {
		this.repayInSusp = repayInSusp;
	}
	
}
