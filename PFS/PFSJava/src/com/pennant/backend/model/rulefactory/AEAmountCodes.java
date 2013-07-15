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
	private BigDecimal refund = BigDecimal.ZERO;
	private BigDecimal cpzTot = BigDecimal.ZERO;
	private BigDecimal cpzPrv = BigDecimal.ZERO;
	private BigDecimal cpzCur = BigDecimal.ZERO;
	private BigDecimal cpzNxt = BigDecimal.ZERO;
	private BigDecimal pftInAdv = BigDecimal.ZERO;
	private BigDecimal provAmt = BigDecimal.ZERO;

	private int elpDays;
	private int elpMnts;
	private int elpTerms;
	private int ttlDays;
	private int ttlMnts;
	private int ttlTerms;
	private int ODDays;
	private int daysFromFullyPaid;
	private int ODInst;

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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public BigDecimal getRefund() {
		return refund;
	}
	public void setRefund(BigDecimal refund) {
		this.refund = refund;
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
    	PROVDUE = pROVDUE;
    }

	public BigDecimal getSUSPNOW() {
		return SUSPNOW;
	}
	public void setSUSPNOW(BigDecimal sUSPNOW) {
		SUSPNOW = sUSPNOW;
	}

	public BigDecimal getSUSPRLS() {
		return SUSPRLS;
	}
	public void setSUSPRLS(BigDecimal sUSPRLS) {
		SUSPRLS = sUSPRLS;
	}

	public BigDecimal getPENALTY() {
		return PENALTY;
	}
	public void setPENALTY(BigDecimal pENALTY) {
		PENALTY = pENALTY;
	}

	public BigDecimal getWAIVER() {
		return WAIVER;
	}
	public void setWAIVER(BigDecimal wAIVER) {
		WAIVER = wAIVER;
	}

	public void setODCPLShare(BigDecimal oDCPLShare) {
		ODCPLShare = oDCPLShare;
	}
	public BigDecimal getODCPLShare() {
		return ODCPLShare;
	}
	
	public BigDecimal getCLAIMAMT() {
    	return CLAIMAMT;
    }
	public void setCLAIMAMT(BigDecimal cLAIMAMT) {
    	CLAIMAMT = cLAIMAMT;
    }
	
	public BigDecimal getDEFFEREDCOST() {
    	return DEFFEREDCOST;
    }
	public void setDEFFEREDCOST(BigDecimal dEFFEREDCOST) {
    	DEFFEREDCOST = dEFFEREDCOST;
    }
	
	public BigDecimal getCURRETBILL() {
    	return CURRETBILL;
    }
	public void setCURRETBILL(BigDecimal cURRETBILL) {
    	CURRETBILL = cURRETBILL;
    }
	
	public BigDecimal getTTLRETBILL() {
    	return TTLRETBILL;
    }
	public void setTTLRETBILL(BigDecimal tTLRETBILL) {
    	TTLRETBILL = tTLRETBILL;
    }

}
