package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.pennant.backend.model.eventproperties.EventProperties;

public class FinanceProfitDetail implements Serializable {
	private static final long serialVersionUID = 6601637251967752181L;

	private long finID;
	private String finReference;
	private long custId = Long.MIN_VALUE;
	private String finBranch;
	private String finType;
	private Date lastMdfDate;
	private BigDecimal totalPftSchd = BigDecimal.ZERO;
	private BigDecimal totalPftCpz = BigDecimal.ZERO;
	private BigDecimal totalPftPaid = BigDecimal.ZERO;
	private BigDecimal totalPftBal = BigDecimal.ZERO;
	private BigDecimal totalPftPaidInAdv = BigDecimal.ZERO;
	private BigDecimal totalpriSchd = BigDecimal.ZERO;
	private BigDecimal totalPriPaid = BigDecimal.ZERO;
	private BigDecimal totalPriPaidInAdv = BigDecimal.ZERO; // New Field
	private BigDecimal totalPriBal = BigDecimal.ZERO;
	// Till date
	private BigDecimal tdSchdPft = BigDecimal.ZERO;
	private BigDecimal tdPftCpz = BigDecimal.ZERO;
	private BigDecimal tdSchdPftPaid = BigDecimal.ZERO;
	private BigDecimal tdSchdPftBal = BigDecimal.ZERO;
	private BigDecimal pftAmz = BigDecimal.ZERO;
	private BigDecimal pftAmzNormal = BigDecimal.ZERO; // New Field
	private BigDecimal pftAmzPD = BigDecimal.ZERO; // New Field
	private BigDecimal pftAmzSusp = BigDecimal.ZERO;
	private BigDecimal tdSchdPri = BigDecimal.ZERO;
	private BigDecimal tdSchdPriPaid = BigDecimal.ZERO;
	private BigDecimal tdSchdPriBal = BigDecimal.ZERO;
	// After posting
	private BigDecimal amzTillLBD = BigDecimal.ZERO;
	private BigDecimal lpiTillLBD = BigDecimal.ZERO;
	private BigDecimal gstLpiTillLBD = BigDecimal.ZERO;
	private BigDecimal lppTillLBD = BigDecimal.ZERO;
	private BigDecimal gstLppTillLBD = BigDecimal.ZERO;
	private BigDecimal amzTillLBDNormal = BigDecimal.ZERO; // new
	private BigDecimal amzTillLBDPD = BigDecimal.ZERO; // new
	private BigDecimal amzTillLBDPIS = BigDecimal.ZERO; // new

	// others
	private String RepayFrq;
	private String CustCIF;
	private String FinCcy;
	private String FinPurpose;
	private Date FinContractDate;
	private Date FinApprovedDate;
	private Date FinStartDate;
	private Date MaturityDate;
	private Date FullPaidDate;
	private BigDecimal FinAmount = BigDecimal.ZERO;
	private BigDecimal DownPayment = BigDecimal.ZERO;
	private BigDecimal advanceEMI = BigDecimal.ZERO;
	private BigDecimal CurReducingRate = BigDecimal.ZERO;
	private BigDecimal curFlatRate = BigDecimal.ZERO;
	private BigDecimal ODPrincipal = BigDecimal.ZERO;
	private BigDecimal ODProfit = BigDecimal.ZERO;
	private BigDecimal PenaltyPaid = BigDecimal.ZERO;
	private BigDecimal PenaltyDue = BigDecimal.ZERO;
	private BigDecimal PenaltyWaived = BigDecimal.ZERO;

	private Date NSchdDate;
	private BigDecimal NSchdPri = BigDecimal.ZERO;
	private BigDecimal NSchdPft = BigDecimal.ZERO;
	private BigDecimal NSchdPriDue = BigDecimal.ZERO;
	private BigDecimal NSchdPftDue = BigDecimal.ZERO;

	private boolean PftInSusp;
	private boolean provision;

	private String FinStatus;
	private String FinStsReason;
	private String FinWorstStatus;
	private int NOInst = 0;
	private int NOPaidInst = 0;
	private int NOODInst = 0;
	private String FinCommitmentRef;
	private boolean FinIsActive;
	private Date firstRepayDate;
	private BigDecimal FirstRepayAmt = BigDecimal.ZERO;
	private BigDecimal finalRepayAmt = BigDecimal.ZERO;
	private int curODDays = 0;
	private int actualODDays = 0;
	private int dueBucket = 0;

	private Date firstODDate;
	private Date prvODDate;
	private String closingStatus;
	private String finCategory;
	private Date prvRpySchDate;
	private BigDecimal prvRpySchPri = BigDecimal.ZERO;
	private BigDecimal prvRpySchPft = BigDecimal.ZERO;
	private Date latestRpyDate;
	private BigDecimal latestRpyPri = BigDecimal.ZERO;
	private BigDecimal latestRpyPft = BigDecimal.ZERO;
	private BigDecimal totalWriteoff = BigDecimal.ZERO;
	// Depreciation
	private BigDecimal accumulatedDepPri = BigDecimal.ZERO;
	private BigDecimal depreciatePri = BigDecimal.ZERO;
	// others
	// private BigDecimal disburse = BigDecimal.ZERO;
	// private BigDecimal downpay = BigDecimal.ZERO;

	private BigDecimal acrTillLBD = BigDecimal.ZERO;
	private BigDecimal EarnedPft = BigDecimal.ZERO;
	private BigDecimal Unearned = BigDecimal.ZERO;
	private BigDecimal pftAccrued = BigDecimal.ZERO;
	private BigDecimal pftAccrueSusp = BigDecimal.ZERO;

	// New fields on 14APR17. If possible rearrange the fields to keep relevent fields together
	private int maxODDays = 0;
	private boolean calPftOnPD = false;
	private String pftOnPDMethod = "";
	private BigDecimal pftOnPDMrg = BigDecimal.ZERO;
	private BigDecimal totPftOnPD = BigDecimal.ZERO;
	private BigDecimal totPftOnPDPaid = BigDecimal.ZERO;
	private BigDecimal totPftOnPDWaived = BigDecimal.ZERO;
	private BigDecimal totPftOnPDDue = BigDecimal.ZERO;
	private BigDecimal acrSuspTillLBD = BigDecimal.ZERO;
	private BigDecimal prvMthAmz = BigDecimal.ZERO;
	private BigDecimal prvMthAmzNrm = BigDecimal.ZERO;
	private BigDecimal prvMthAmzPD = BigDecimal.ZERO;
	private BigDecimal prvMthAmzSusp = BigDecimal.ZERO;
	private BigDecimal prvMthAcr = BigDecimal.ZERO;
	private BigDecimal prvMthAcrSusp = BigDecimal.ZERO;
	private Date firstDisbDate;
	private Date latestDisbDate;
	private int futureInst = 0;
	private int remainingTenor = 0;
	private int totalTenor = 0;
	private BigDecimal excessAmt = BigDecimal.ZERO;
	private BigDecimal emiInAdvance = BigDecimal.ZERO;
	private BigDecimal payableAdvise = BigDecimal.ZERO;
	private BigDecimal excessAmtResv = BigDecimal.ZERO;
	private BigDecimal emiInAdvanceResv = BigDecimal.ZERO;
	private BigDecimal payableAdviseResv = BigDecimal.ZERO;
	private String productCategory;

	// Newly Added Fields for SOA
	private BigDecimal futureRpyPri = BigDecimal.ZERO;
	private BigDecimal futureRpyPft = BigDecimal.ZERO;
	private BigDecimal totChargesPaid = BigDecimal.ZERO;
	private BigDecimal linkedFinRef = BigDecimal.ZERO;
	private BigDecimal closedlinkedFinRef = BigDecimal.ZERO;
	private BigDecimal bounceAmt = BigDecimal.ZERO;
	private BigDecimal bounceAmtDue = BigDecimal.ZERO;
	private BigDecimal bounceAmtPaid = BigDecimal.ZERO;
	private BigDecimal upfrontFee = BigDecimal.ZERO;
	private BigDecimal lastDisburseDate = BigDecimal.ZERO;
	private BigDecimal receivableAdvise = BigDecimal.ZERO;
	private BigDecimal excessAmtBal = BigDecimal.ZERO;
	private BigDecimal emiInAdvanceBal = BigDecimal.ZERO;
	private BigDecimal receivableAdviseBal = BigDecimal.ZERO;
	private BigDecimal payableAdviseBal = BigDecimal.ZERO;
	private BigDecimal maxRpyAmount = BigDecimal.ZERO;
	private BigDecimal cpzPosted = BigDecimal.ZERO;
	private BigDecimal curCpzBalance = BigDecimal.ZERO;

	// External Fields For Amortization Purpose
	private BigDecimal lpiAmount = BigDecimal.ZERO;
	private BigDecimal gstLpiAmount = BigDecimal.ZERO;
	private BigDecimal lppAmount = BigDecimal.ZERO;
	private BigDecimal gstLppAmount = BigDecimal.ZERO;

	// Gold Loan Paid Status (For Report Purpose and avoid multiple queries)
	private String glPaymentSts;

	// Subvention Details
	private BigDecimal svnAcrTillLBD = BigDecimal.ZERO;
	private BigDecimal svnPftAmount = BigDecimal.ZERO;
	private boolean svnAcrCalReq = true;

	// Number of Auto Increments Grace End
	private int NOAutoIncGrcEnd = 0;

	// Assignments (For Reporting purpose we are saving)
	private BigDecimal assignBPI1 = BigDecimal.ZERO;
	private BigDecimal assignBPI2 = BigDecimal.ZERO;

	private String baseRateCode;
	private String splRateCode;
	private BigDecimal baseRate = BigDecimal.ZERO;
	private BigDecimal splRate = BigDecimal.ZERO;
	private BigDecimal mrgRate = BigDecimal.ZERO;

	private BigDecimal tdTdsAmount = BigDecimal.ZERO;
	private BigDecimal tdTdsPaid = BigDecimal.ZERO;
	private BigDecimal tdTdsBal = BigDecimal.ZERO;
	private BigDecimal tdsAccrued = BigDecimal.ZERO;

	private BigDecimal totalSvnAmount = BigDecimal.ZERO;
	private int noInstEarlyStl;

	private BigDecimal gapIntAmz = BigDecimal.ZERO;
	private BigDecimal gapIntAmzLbd = BigDecimal.ZERO;
	private BigDecimal prvMthGapIntAmz = BigDecimal.ZERO;
	private BigDecimal svAmount = BigDecimal.ZERO;
	private BigDecimal cbAmount = BigDecimal.ZERO;

	// IND AS Amortization
	private String aMZMethod;
	private boolean writeoffLoan = false;

	private int extODDays = 0;
	private String curDPDString;

	private EventProperties eventProperties = new EventProperties();

	public FinanceProfitDetail() {
		super();
	}

	public FinanceProfitDetail copyEntity() {
		FinanceProfitDetail entity = new FinanceProfitDetail();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setCustId(this.custId);
		entity.setFinBranch(this.finBranch);
		entity.setFinType(this.finType);
		entity.setLastMdfDate(this.lastMdfDate);
		entity.setTotalPftSchd(this.totalPftSchd);
		entity.setTotalPftCpz(this.totalPftCpz);
		entity.setTotalPftPaid(this.totalPftPaid);
		entity.setTotalPftBal(this.totalPftBal);
		entity.setTotalPftPaidInAdv(this.totalPftPaidInAdv);
		entity.setTotalpriSchd(this.totalpriSchd);
		entity.setTotalPriPaid(this.totalPriPaid);
		entity.setTotalPriPaidInAdv(this.totalPriPaidInAdv);
		entity.setTotalPriBal(this.totalPriBal);
		entity.setTdSchdPft(this.tdSchdPft);
		entity.setTdPftCpz(this.tdPftCpz);
		entity.setTdSchdPftPaid(this.tdSchdPftPaid);
		entity.setTdSchdPftBal(this.tdSchdPftBal);
		entity.setPftAmz(this.pftAmz);
		entity.setPftAmzNormal(this.pftAmzNormal);
		entity.setPftAmzPD(this.pftAmzPD);
		entity.setPftAmzSusp(this.pftAmzSusp);
		entity.setTdSchdPri(this.tdSchdPri);
		entity.setTdSchdPriPaid(this.tdSchdPriPaid);
		entity.setTdSchdPriBal(this.tdSchdPriBal);
		entity.setAmzTillLBD(this.amzTillLBD);
		entity.setLpiTillLBD(this.lpiTillLBD);
		entity.setGstLpiTillLBD(this.gstLpiTillLBD);
		entity.setLppTillLBD(this.lppTillLBD);
		entity.setGstLppTillLBD(this.gstLppTillLBD);
		entity.setAmzTillLBDNormal(this.amzTillLBDNormal);
		entity.setAmzTillLBDPD(this.amzTillLBDPD);
		entity.setAmzTillLBDPIS(this.amzTillLBDPIS);
		entity.setRepayFrq(this.RepayFrq);
		entity.setCustCIF(this.CustCIF);
		entity.setFinCcy(this.FinCcy);
		entity.setFinPurpose(this.FinPurpose);
		entity.setFinContractDate(this.FinContractDate);
		entity.setFinApprovedDate(this.FinApprovedDate);
		entity.setFinStartDate(this.FinStartDate);
		entity.setMaturityDate(this.MaturityDate);
		entity.setFullPaidDate(this.FullPaidDate);
		entity.setFinAmount(this.FinAmount);
		entity.setDownPayment(this.DownPayment);
		entity.setAdvanceEMI(this.advanceEMI);
		entity.setCurReducingRate(this.CurReducingRate);
		entity.setCurFlatRate(this.curFlatRate);
		entity.setODPrincipal(this.ODPrincipal);
		entity.setODProfit(this.ODProfit);
		entity.setPenaltyPaid(this.PenaltyPaid);
		entity.setPenaltyDue(this.PenaltyDue);
		entity.setPenaltyWaived(this.PenaltyWaived);
		entity.setNSchdDate(this.NSchdDate);
		entity.setNSchdPri(this.NSchdPri);
		entity.setNSchdPft(this.NSchdPft);
		entity.setNSchdPriDue(this.NSchdPriDue);
		entity.setNSchdPftDue(this.NSchdPftDue);
		entity.setPftInSusp(this.PftInSusp);
		entity.setProvision(this.provision);
		entity.setFinStatus(this.FinStatus);
		entity.setFinStsReason(this.FinStsReason);
		entity.setFinWorstStatus(this.FinWorstStatus);
		entity.setNOInst(this.NOInst);
		entity.setNOPaidInst(this.NOPaidInst);
		entity.setNOODInst(this.NOODInst);
		entity.setFinCommitmentRef(this.FinCommitmentRef);
		entity.setFinIsActive(this.FinIsActive);
		entity.setFirstRepayDate(this.firstRepayDate);
		entity.setFirstRepayAmt(this.FirstRepayAmt);
		entity.setFinalRepayAmt(this.finalRepayAmt);
		entity.setCurODDays(this.curODDays);
		entity.setActualODDays(this.actualODDays);
		entity.setDueBucket(this.dueBucket);
		entity.setFirstODDate(this.firstODDate);
		entity.setPrvODDate(this.prvODDate);
		entity.setClosingStatus(this.closingStatus);
		entity.setFinCategory(this.finCategory);
		entity.setPrvRpySchDate(this.prvRpySchDate);
		entity.setPrvRpySchPri(this.prvRpySchPri);
		entity.setPrvRpySchPft(this.prvRpySchPft);
		entity.setLatestRpyDate(this.latestRpyDate);
		entity.setLatestRpyPri(this.latestRpyPri);
		entity.setLatestRpyPft(this.latestRpyPft);
		entity.setTotalWriteoff(this.totalWriteoff);
		entity.setAccumulatedDepPri(this.accumulatedDepPri);
		entity.setDepreciatePri(this.depreciatePri);
		entity.setAcrTillLBD(this.acrTillLBD);
		entity.setEarnedPft(this.EarnedPft);
		entity.setUnearned(this.Unearned);
		entity.setPftAccrued(this.pftAccrued);
		entity.setPftAccrueSusp(this.pftAccrueSusp);
		entity.setMaxODDays(this.maxODDays);
		entity.setCalPftOnPD(this.calPftOnPD);
		entity.setPftOnPDMethod(this.pftOnPDMethod);
		entity.setPftOnPDMrg(this.pftOnPDMrg);
		entity.setTotPftOnPD(this.totPftOnPD);
		entity.setTotPftOnPDPaid(this.totPftOnPDPaid);
		entity.setTotPftOnPDWaived(this.totPftOnPDWaived);
		entity.setTotPftOnPDDue(this.totPftOnPDDue);
		entity.setAcrSuspTillLBD(this.acrSuspTillLBD);
		entity.setPrvMthAmz(this.prvMthAmz);
		entity.setPrvMthAmzNrm(this.prvMthAmzNrm);
		entity.setPrvMthAmzPD(this.prvMthAmzPD);
		entity.setPrvMthAmzSusp(this.prvMthAmzSusp);
		entity.setPrvMthAcr(this.prvMthAcr);
		entity.setPrvMthAcrSusp(this.prvMthAcrSusp);
		entity.setFirstDisbDate(this.firstDisbDate);
		entity.setLatestDisbDate(this.latestDisbDate);
		entity.setFutureInst(this.futureInst);
		entity.setRemainingTenor(this.remainingTenor);
		entity.setTotalTenor(this.totalTenor);
		entity.setExcessAmt(this.excessAmt);
		entity.setEmiInAdvance(this.emiInAdvance);
		entity.setPayableAdvise(this.payableAdvise);
		entity.setExcessAmtResv(this.excessAmtResv);
		entity.setEmiInAdvanceResv(this.emiInAdvanceResv);
		entity.setPayableAdviseResv(this.payableAdviseResv);
		entity.setProductCategory(this.productCategory);
		entity.setFutureRpyPri(this.futureRpyPri);
		entity.setFutureRpyPft(this.futureRpyPft);
		entity.setTotChargesPaid(this.totChargesPaid);
		entity.setLinkedFinRef(this.linkedFinRef);
		entity.setClosedlinkedFinRef(this.closedlinkedFinRef);
		entity.setBounceAmt(this.bounceAmt);
		entity.setBounceAmtDue(this.bounceAmtDue);
		entity.setBounceAmtPaid(this.bounceAmtPaid);
		entity.setUpfrontFee(this.upfrontFee);
		entity.setLastDisburseDate(this.lastDisburseDate);
		entity.setReceivableAdvise(this.receivableAdvise);
		entity.setExcessAmtBal(this.excessAmtBal);
		entity.setEmiInAdvanceBal(this.emiInAdvanceBal);
		entity.setReceivableAdviseBal(this.receivableAdviseBal);
		entity.setPayableAdviseBal(this.payableAdviseBal);
		entity.setMaxRpyAmount(this.maxRpyAmount);
		entity.setCpzPosted(this.cpzPosted);
		entity.setCurCpzBalance(this.curCpzBalance);
		entity.setLpiAmount(this.lpiAmount);
		entity.setGstLpiAmount(this.gstLpiAmount);
		entity.setLppAmount(this.lppAmount);
		entity.setGstLppAmount(this.gstLppAmount);
		entity.setGlPaymentSts(this.glPaymentSts);
		entity.setSvnAcrTillLBD(this.svnAcrTillLBD);
		entity.setSvnPftAmount(this.svnPftAmount);
		entity.setSvnAcrCalReq(this.svnAcrCalReq);
		entity.setNOAutoIncGrcEnd(this.NOAutoIncGrcEnd);
		entity.setAssignBPI1(this.assignBPI1);
		entity.setAssignBPI2(this.assignBPI2);
		entity.setBaseRateCode(this.baseRateCode);
		entity.setSplRateCode(this.splRateCode);
		entity.setBaseRate(this.baseRate);
		entity.setSplRate(this.splRate);
		entity.setMrgRate(this.mrgRate);
		entity.setTdTdsAmount(this.tdTdsAmount);
		entity.setTdTdsPaid(this.tdTdsPaid);
		entity.setTdTdsBal(this.tdTdsBal);
		entity.setTdsAccrued(this.tdsAccrued);
		entity.setTotalSvnAmount(this.totalSvnAmount);
		entity.setNoInstEarlyStl(this.noInstEarlyStl);
		entity.setGapIntAmz(this.gapIntAmz);
		entity.setGapIntAmzLbd(this.gapIntAmzLbd);
		entity.setPrvMthGapIntAmz(this.prvMthGapIntAmz);
		entity.setSvAmount(this.svAmount);
		entity.setCbAmount(this.cbAmount);
		entity.setAMZMethod(this.aMZMethod);
		entity.setWriteoffLoan(this.writeoffLoan);
		entity.setExtODDays(this.extODDays);
		entity.setEventProperties(this.eventProperties);
		entity.setWriteoffLoan(this.writeoffLoan);
		entity.setExtODDays(this.extODDays);
		entity.setCurDPDString(this.curDPDString);

		return entity;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public Date getLastMdfDate() {
		return lastMdfDate;
	}

	public void setLastMdfDate(Date lastMdfDate) {
		this.lastMdfDate = lastMdfDate;
	}

	public BigDecimal getTotalPftSchd() {
		return totalPftSchd;
	}

	public void setTotalPftSchd(BigDecimal totalPftSchd) {
		this.totalPftSchd = totalPftSchd;
	}

	public BigDecimal getTotalPftCpz() {
		return totalPftCpz;
	}

	public void setTotalPftCpz(BigDecimal totalPftCpz) {
		this.totalPftCpz = totalPftCpz;
	}

	public BigDecimal getTotalPftPaid() {
		return totalPftPaid;
	}

	public void setTotalPftPaid(BigDecimal totalPftPaid) {
		this.totalPftPaid = totalPftPaid;
	}

	public BigDecimal getTotalPftBal() {
		return totalPftBal;
	}

	public void setTotalPftBal(BigDecimal totalPftBal) {
		this.totalPftBal = totalPftBal;
	}

	public BigDecimal getTotalPftPaidInAdv() {
		return totalPftPaidInAdv;
	}

	public void setTotalPftPaidInAdv(BigDecimal totalPftPaidInAdv) {
		this.totalPftPaidInAdv = totalPftPaidInAdv;
	}

	public BigDecimal getTotalPriPaid() {
		return totalPriPaid;
	}

	public void setTotalPriPaid(BigDecimal totalPriPaid) {
		this.totalPriPaid = totalPriPaid;
	}

	public BigDecimal getTotalPriBal() {
		return totalPriBal;
	}

	public void setTotalPriBal(BigDecimal totalPriBal) {
		this.totalPriBal = totalPriBal;
	}

	public BigDecimal getTdSchdPft() {
		return tdSchdPft;
	}

	public void setTdSchdPft(BigDecimal tdSchdPft) {
		this.tdSchdPft = tdSchdPft;
	}

	public BigDecimal getTdPftCpz() {
		return tdPftCpz;
	}

	public void setTdPftCpz(BigDecimal tdPftCpz) {
		this.tdPftCpz = tdPftCpz;
	}

	public BigDecimal getTdSchdPftPaid() {
		return tdSchdPftPaid;
	}

	public void setTdSchdPftPaid(BigDecimal tdSchdPftPaid) {
		this.tdSchdPftPaid = tdSchdPftPaid;
	}

	public BigDecimal getTdSchdPftBal() {
		return tdSchdPftBal;
	}

	public void setTdSchdPftBal(BigDecimal tdSchdPftBal) {
		this.tdSchdPftBal = tdSchdPftBal;
	}

	public BigDecimal getPftAccrued() {
		return pftAccrued;
	}

	public void setPftAccrued(BigDecimal pftAccrued) {
		this.pftAccrued = pftAccrued;
	}

	public BigDecimal getPftAccrueSusp() {
		return pftAccrueSusp;
	}

	public void setPftAccrueSusp(BigDecimal pftAccrueSusp) {
		this.pftAccrueSusp = pftAccrueSusp;
	}

	public BigDecimal getPftAmz() {
		return pftAmz;
	}

	public void setPftAmz(BigDecimal pftAmz) {
		this.pftAmz = pftAmz;
	}

	public BigDecimal getPftAmzSusp() {
		return pftAmzSusp;
	}

	public void setPftAmzSusp(BigDecimal pftAmzSusp) {
		this.pftAmzSusp = pftAmzSusp;
	}

	public BigDecimal getTdSchdPri() {
		return tdSchdPri;
	}

	public void setTdSchdPri(BigDecimal tdSchdPri) {
		this.tdSchdPri = tdSchdPri;
	}

	public BigDecimal getTdSchdPriPaid() {
		return tdSchdPriPaid;
	}

	public void setTdSchdPriPaid(BigDecimal tdSchdPriPaid) {
		this.tdSchdPriPaid = tdSchdPriPaid;
	}

	public BigDecimal getTdSchdPriBal() {
		return tdSchdPriBal;
	}

	public void setTdSchdPriBal(BigDecimal tdSchdPriBal) {
		this.tdSchdPriBal = tdSchdPriBal;
	}

	public BigDecimal getAcrTillLBD() {
		return acrTillLBD;
	}

	public void setAcrTillLBD(BigDecimal acrTillLBD) {
		this.acrTillLBD = acrTillLBD;
	}

	public BigDecimal getAmzTillLBD() {
		return amzTillLBD;
	}

	public void setAmzTillLBD(BigDecimal amzTillLBD) {
		this.amzTillLBD = amzTillLBD;
	}

	public String getRepayFrq() {
		return RepayFrq;
	}

	public void setRepayFrq(String repayFrq) {
		RepayFrq = repayFrq;
	}

	public String getCustCIF() {
		return CustCIF;
	}

	public void setCustCIF(String custCIF) {
		CustCIF = custCIF;
	}

	public String getFinCcy() {
		return FinCcy;
	}

	public void setFinCcy(String finCcy) {
		FinCcy = finCcy;
	}

	public String getFinPurpose() {
		return FinPurpose;
	}

	public void setFinPurpose(String finPurpose) {
		FinPurpose = finPurpose;
	}

	public Date getFinContractDate() {
		return FinContractDate;
	}

	public void setFinContractDate(Date finContractDate) {
		FinContractDate = finContractDate;
	}

	public Date getFinApprovedDate() {
		return FinApprovedDate;
	}

	public void setFinApprovedDate(Date finApprovedDate) {
		FinApprovedDate = finApprovedDate;
	}

	public Date getFinStartDate() {
		return FinStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		FinStartDate = finStartDate;
	}

	public Date getMaturityDate() {
		return MaturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		MaturityDate = maturityDate;
	}

	public Date getFullPaidDate() {
		return FullPaidDate;
	}

	public void setFullPaidDate(Date fullPaidDate) {
		FullPaidDate = fullPaidDate;
	}

	public BigDecimal getFinAmount() {
		return FinAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		FinAmount = finAmount;
	}

	public BigDecimal getDownPayment() {
		return DownPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		DownPayment = downPayment;
	}

	public BigDecimal getCurReducingRate() {
		return CurReducingRate;
	}

	public void setCurReducingRate(BigDecimal curReducingRate) {
		CurReducingRate = curReducingRate;
	}

	public BigDecimal getCurFlatRate() {
		return curFlatRate;
	}

	public void setCurFlatRate(BigDecimal curFlatRate) {
		this.curFlatRate = curFlatRate;
	}

	public BigDecimal getTotalpriSchd() {
		return totalpriSchd;
	}

	public void setTotalpriSchd(BigDecimal totalpriSchd) {
		this.totalpriSchd = totalpriSchd;
	}

	public BigDecimal getODPrincipal() {
		return ODPrincipal;
	}

	public void setODPrincipal(BigDecimal oDPrincipal) {
		ODPrincipal = oDPrincipal;
	}

	public BigDecimal getODProfit() {
		return ODProfit;
	}

	public void setODProfit(BigDecimal oDProfit) {
		ODProfit = oDProfit;
	}

	public BigDecimal getPenaltyPaid() {
		return PenaltyPaid;
	}

	public void setPenaltyPaid(BigDecimal penaltyPaid) {
		PenaltyPaid = penaltyPaid;
	}

	public BigDecimal getPenaltyDue() {
		return PenaltyDue;
	}

	public void setPenaltyDue(BigDecimal penaltyDue) {
		PenaltyDue = penaltyDue;
	}

	public BigDecimal getPenaltyWaived() {
		return PenaltyWaived;
	}

	public void setPenaltyWaived(BigDecimal penaltyWaived) {
		PenaltyWaived = penaltyWaived;
	}

	public Date getNSchdDate() {
		return NSchdDate;
	}

	public void setNSchdDate(Date nSchdDate) {
		NSchdDate = nSchdDate;
	}

	public BigDecimal getNSchdPri() {
		return NSchdPri;
	}

	public void setNSchdPri(BigDecimal nSchdPri) {
		NSchdPri = nSchdPri;
	}

	public BigDecimal getNSchdPft() {
		return NSchdPft;
	}

	public void setNSchdPft(BigDecimal nSchdPft) {
		NSchdPft = nSchdPft;
	}

	public BigDecimal getNSchdPriDue() {
		return NSchdPriDue;
	}

	public void setNSchdPriDue(BigDecimal nSchdPriDue) {
		NSchdPriDue = nSchdPriDue;
	}

	public BigDecimal getNSchdPftDue() {
		return NSchdPftDue;
	}

	public void setNSchdPftDue(BigDecimal nSchdPftDue) {
		NSchdPftDue = nSchdPftDue;
	}

	public BigDecimal getEarnedPft() {
		return EarnedPft;
	}

	public void setEarnedPft(BigDecimal earnedPft) {
		EarnedPft = earnedPft;
	}

	public BigDecimal getUnearned() {
		return Unearned;
	}

	public void setUnearned(BigDecimal unearned) {
		Unearned = unearned;
	}

	public boolean isPftInSusp() {
		return PftInSusp;
	}

	public void setPftInSusp(boolean pftInSusp) {
		PftInSusp = pftInSusp;
	}

	public String getFinStatus() {
		return FinStatus;
	}

	public void setFinStatus(String finStatus) {
		FinStatus = finStatus;
	}

	public String getFinStsReason() {
		return FinStsReason;
	}

	public void setFinStsReason(String finStsReason) {
		FinStsReason = finStsReason;
	}

	public String getFinWorstStatus() {
		return FinWorstStatus;
	}

	public void setFinWorstStatus(String finWorstStatus) {
		FinWorstStatus = finWorstStatus;
	}

	public int getNOInst() {
		return NOInst;
	}

	public void setNOInst(int nOInst) {
		NOInst = nOInst;
	}

	public int getNOPaidInst() {
		return NOPaidInst;
	}

	public void setNOPaidInst(int nOPaidInst) {
		NOPaidInst = nOPaidInst;
	}

	public int getNOODInst() {
		return NOODInst;
	}

	public void setNOODInst(int nOODInst) {
		NOODInst = nOODInst;
	}

	public String getFinCommitmentRef() {
		return FinCommitmentRef;
	}

	public void setFinCommitmentRef(String finCommitmentRef) {
		FinCommitmentRef = finCommitmentRef;
	}

	public boolean getFinIsActive() {
		return FinIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		FinIsActive = finIsActive;
	}

	public Date getFirstRepayDate() {
		return firstRepayDate;
	}

	public void setFirstRepayDate(Date firstRepayDate) {
		this.firstRepayDate = firstRepayDate;
	}

	public BigDecimal getFirstRepayAmt() {
		return FirstRepayAmt;
	}

	public void setFirstRepayAmt(BigDecimal firstRepayAmt) {
		FirstRepayAmt = firstRepayAmt;
	}

	public BigDecimal getFinalRepayAmt() {
		return finalRepayAmt;
	}

	public void setFinalRepayAmt(BigDecimal finalRepayAmt) {
		this.finalRepayAmt = finalRepayAmt;
	}

	public int getCurODDays() {
		return curODDays;
	}

	public void setCurODDays(int oDDays) {
		this.curODDays = oDDays;
	}

	public Date getFirstODDate() {
		return firstODDate;
	}

	public void setFirstODDate(Date firstODDate) {
		this.firstODDate = firstODDate;
	}

	public Date getPrvODDate() {
		return prvODDate;
	}

	public void setPrvODDate(Date prvODDate) {
		this.prvODDate = prvODDate;
	}

	public String getClosingStatus() {
		return closingStatus;
	}

	public void setClosingStatus(String closingStatus) {
		this.closingStatus = closingStatus;
	}

	public String getFinCategory() {
		return finCategory;
	}

	public void setFinCategory(String finCategory) {
		this.finCategory = finCategory;
	}

	public Date getPrvRpySchDate() {
		return prvRpySchDate;
	}

	public void setPrvRpySchDate(Date prvRpySchDate) {
		this.prvRpySchDate = prvRpySchDate;
	}

	public BigDecimal getPrvRpySchPri() {
		return prvRpySchPri;
	}

	public void setPrvRpySchPri(BigDecimal prvRpySchPri) {
		this.prvRpySchPri = prvRpySchPri;
	}

	public BigDecimal getPrvRpySchPft() {
		return prvRpySchPft;
	}

	public void setPrvRpySchPft(BigDecimal prvRpySchPft) {
		this.prvRpySchPft = prvRpySchPft;
	}

	public Date getLatestRpyDate() {
		return latestRpyDate;
	}

	public void setLatestRpyDate(Date latestRpyDate) {
		this.latestRpyDate = latestRpyDate;
	}

	public BigDecimal getLatestRpyPri() {
		return latestRpyPri;
	}

	public void setLatestRpyPri(BigDecimal latestRpyPri) {
		this.latestRpyPri = latestRpyPri;
	}

	public BigDecimal getLatestRpyPft() {
		return latestRpyPft;
	}

	public void setLatestRpyPft(BigDecimal latestRpyPft) {
		this.latestRpyPft = latestRpyPft;
	}

	public BigDecimal getTotalWriteoff() {
		return totalWriteoff;
	}

	public void setTotalWriteoff(BigDecimal totalWriteoff) {
		this.totalWriteoff = totalWriteoff;
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

	public BigDecimal getTotalPriPaidInAdv() {
		return totalPriPaidInAdv;
	}

	public void setTotalPriPaidInAdv(BigDecimal totalPriPaidInAdv) {
		this.totalPriPaidInAdv = totalPriPaidInAdv;
	}

	public BigDecimal getPftAmzNormal() {
		return pftAmzNormal;
	}

	public void setPftAmzNormal(BigDecimal pftAmzNormal) {
		this.pftAmzNormal = pftAmzNormal;
	}

	public BigDecimal getPftAmzPD() {
		return pftAmzPD;
	}

	public void setPftAmzPD(BigDecimal pftAmzPD) {
		this.pftAmzPD = pftAmzPD;
	}

	/*
	 * public BigDecimal getDisburse() { return disburse; }
	 * 
	 * public void setDisburse(BigDecimal disburse) { this.disburse = disburse; }
	 */

	/*
	 * public BigDecimal getDownpay() { return downpay; }
	 * 
	 * public void setDownpay(BigDecimal downpay) { this.downpay = downpay; }
	 */

	public BigDecimal getAmzTillLBDNormal() {
		return amzTillLBDNormal;
	}

	public void setAmzTillLBDNormal(BigDecimal amzTillLBDNormal) {
		this.amzTillLBDNormal = amzTillLBDNormal;
	}

	public BigDecimal getAmzTillLBDPD() {
		return amzTillLBDPD;
	}

	public void setAmzTillLBDPD(BigDecimal amzTillLBDPD) {
		this.amzTillLBDPD = amzTillLBDPD;
	}

	public BigDecimal getAmzTillLBDPIS() {
		return amzTillLBDPIS;
	}

	public void setAmzTillLBDPIS(BigDecimal amzTillLBDPIS) {
		this.amzTillLBDPIS = amzTillLBDPIS;
	}

	public int getMaxODDays() {
		return maxODDays;
	}

	public void setMaxODDays(int maxODDays) {
		this.maxODDays = maxODDays;
	}

	public boolean isCalPftOnPD() {
		return calPftOnPD;
	}

	public void setCalPftOnPD(boolean calPftOnPD) {
		this.calPftOnPD = calPftOnPD;
	}

	public String getPftOnPDMethod() {
		return pftOnPDMethod;
	}

	public void setPftOnPDMethod(String pftOnPDMethod) {
		this.pftOnPDMethod = pftOnPDMethod;
	}

	public BigDecimal getPftOnPDMrg() {
		return pftOnPDMrg;
	}

	public void setPftOnPDMrg(BigDecimal pftOnPDMrg) {
		this.pftOnPDMrg = pftOnPDMrg;
	}

	public BigDecimal getTotPftOnPD() {
		return totPftOnPD;
	}

	public void setTotPftOnPD(BigDecimal totPftOnPD) {
		this.totPftOnPD = totPftOnPD;
	}

	public BigDecimal getTotPftOnPDPaid() {
		return totPftOnPDPaid;
	}

	public void setTotPftOnPDPaid(BigDecimal totPftOnPDPaid) {
		this.totPftOnPDPaid = totPftOnPDPaid;
	}

	public BigDecimal getTotPftOnPDWaived() {
		return totPftOnPDWaived;
	}

	public void setTotPftOnPDWaived(BigDecimal totPftOnPDWaived) {
		this.totPftOnPDWaived = totPftOnPDWaived;
	}

	public BigDecimal getTotPftOnPDDue() {
		return totPftOnPDDue;
	}

	public void setTotPftOnPDDue(BigDecimal totPftOnPDDue) {
		this.totPftOnPDDue = totPftOnPDDue;
	}

	public BigDecimal getAcrSuspTillLBD() {
		return acrSuspTillLBD;
	}

	public void setAcrSuspTillLBD(BigDecimal acrSuspTillLBD) {
		this.acrSuspTillLBD = acrSuspTillLBD;
	}

	public BigDecimal getPrvMthAmz() {
		return prvMthAmz;
	}

	public void setPrvMthAmz(BigDecimal prvMthAmz) {
		this.prvMthAmz = prvMthAmz;
	}

	public BigDecimal getPrvMthAmzNrm() {
		return prvMthAmzNrm;
	}

	public void setPrvMthAmzNrm(BigDecimal prvMthAmzNrm) {
		this.prvMthAmzNrm = prvMthAmzNrm;
	}

	public BigDecimal getPrvMthAmzPD() {
		return prvMthAmzPD;
	}

	public void setPrvMthAmzPD(BigDecimal prvMthAmzPD) {
		this.prvMthAmzPD = prvMthAmzPD;
	}

	public BigDecimal getPrvMthAmzSusp() {
		return prvMthAmzSusp;
	}

	public void setPrvMthAmzSusp(BigDecimal prvMthAmzSusp) {
		this.prvMthAmzSusp = prvMthAmzSusp;
	}

	public BigDecimal getPrvMthAcr() {
		return prvMthAcr;
	}

	public void setPrvMthAcr(BigDecimal prvMthAcr) {
		this.prvMthAcr = prvMthAcr;
	}

	public BigDecimal getPrvMthAcrSusp() {
		return prvMthAcrSusp;
	}

	public void setPrvMthAcrSusp(BigDecimal prvMthAcrSusp) {
		this.prvMthAcrSusp = prvMthAcrSusp;
	}

	public Date getFirstDisbDate() {
		return firstDisbDate;
	}

	public void setFirstDisbDate(Date firstDisbDate) {
		this.firstDisbDate = firstDisbDate;
	}

	public Date getLatestDisbDate() {
		return latestDisbDate;
	}

	public void setLatestDisbDate(Date latestDisbDate) {
		this.latestDisbDate = latestDisbDate;
	}

	public int getFutureInst() {
		return futureInst;
	}

	public void setFutureInst(int futureInst) {
		this.futureInst = futureInst;
	}

	public int getRemainingTenor() {
		return remainingTenor;
	}

	public void setRemainingTenor(int remainingTenor) {
		this.remainingTenor = remainingTenor;
	}

	public int getTotalTenor() {
		return totalTenor;
	}

	public void setTotalTenor(int totalTenor) {
		this.totalTenor = totalTenor;
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

	public BigDecimal getExcessAmtResv() {
		return excessAmtResv;
	}

	public void setExcessAmtResv(BigDecimal excessAmtResv) {
		this.excessAmtResv = excessAmtResv;
	}

	public BigDecimal getEmiInAdvanceResv() {
		return emiInAdvanceResv;
	}

	public void setEmiInAdvanceResv(BigDecimal emiInAdvanceResv) {
		this.emiInAdvanceResv = emiInAdvanceResv;
	}

	public BigDecimal getPayableAdviseResv() {
		return payableAdviseResv;
	}

	public void setPayableAdviseResv(BigDecimal payableAdviseResv) {
		this.payableAdviseResv = payableAdviseResv;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public BigDecimal getFutureRpyPri() {
		return futureRpyPri;
	}

	public void setFutureRpyPri(BigDecimal futureRpyPri) {
		this.futureRpyPri = futureRpyPri;
	}

	public BigDecimal getFutureRpyPft() {
		return futureRpyPft;
	}

	public void setFutureRpyPft(BigDecimal futureRpyPft) {
		this.futureRpyPft = futureRpyPft;
	}

	public BigDecimal getTotChargesPaid() {
		return totChargesPaid;
	}

	public void setTotChargesPaid(BigDecimal totChargesPaid) {
		this.totChargesPaid = totChargesPaid;
	}

	public BigDecimal getLinkedFinRef() {
		return linkedFinRef;
	}

	public void setLinkedFinRef(BigDecimal linkedFinRef) {
		this.linkedFinRef = linkedFinRef;
	}

	public BigDecimal getClosedlinkedFinRef() {
		return closedlinkedFinRef;
	}

	public void setClosedlinkedFinRef(BigDecimal closedlinkedFinRef) {
		this.closedlinkedFinRef = closedlinkedFinRef;
	}

	public BigDecimal getBounceAmt() {
		return bounceAmt;
	}

	public void setBounceAmt(BigDecimal bounceAmt) {
		this.bounceAmt = bounceAmt;
	}

	public BigDecimal getBounceAmtDue() {
		return bounceAmtDue;
	}

	public void setBounceAmtDue(BigDecimal bounceAmtDue) {
		this.bounceAmtDue = bounceAmtDue;
	}

	public BigDecimal getBounceAmtPaid() {
		return bounceAmtPaid;
	}

	public void setBounceAmtPaid(BigDecimal bounceAmtPaid) {
		this.bounceAmtPaid = bounceAmtPaid;
	}

	public BigDecimal getUpfrontFee() {
		return upfrontFee;
	}

	public void setUpfrontFee(BigDecimal upfrontFee) {
		this.upfrontFee = upfrontFee;
	}

	public BigDecimal getLastDisburseDate() {
		return lastDisburseDate;
	}

	public void setLastDisburseDate(BigDecimal lastDisburseDate) {
		this.lastDisburseDate = lastDisburseDate;
	}

	public BigDecimal getExcessAmtBal() {
		return excessAmtBal;
	}

	public void setExcessAmtBal(BigDecimal excessAmtBal) {
		this.excessAmtBal = excessAmtBal;
	}

	public BigDecimal getEmiInAdvanceBal() {
		return emiInAdvanceBal;
	}

	public void setEmiInAdvanceBal(BigDecimal emiInAdvanceBal) {
		this.emiInAdvanceBal = emiInAdvanceBal;
	}

	public BigDecimal getReceivableAdviseBal() {
		return receivableAdviseBal;
	}

	public void setReceivableAdviseBal(BigDecimal receivableAdviseBal) {
		this.receivableAdviseBal = receivableAdviseBal;
	}

	public BigDecimal getPayableAdviseBal() {
		return payableAdviseBal;
	}

	public void setPayableAdviseBal(BigDecimal payableAdviseBal) {
		this.payableAdviseBal = payableAdviseBal;
	}

	public BigDecimal getReceivableAdvise() {
		return receivableAdvise;
	}

	public void setReceivableAdvise(BigDecimal receivableAdvise) {
		this.receivableAdvise = receivableAdvise;
	}

	public int getDueBucket() {
		return dueBucket;
	}

	public void setDueBucket(int dueBucket) {
		this.dueBucket = dueBucket;
	}

	public int getActualODDays() {
		return actualODDays;
	}

	public void setActualODDays(int actualODDays) {
		this.actualODDays = actualODDays;
	}

	public BigDecimal getMaxRpyAmount() {
		return maxRpyAmount;
	}

	public void setMaxRpyAmount(BigDecimal maxRpyAmount) {
		this.maxRpyAmount = maxRpyAmount;
	}

	public BigDecimal getCpzPosted() {
		return cpzPosted;
	}

	public void setCpzPosted(BigDecimal cpzPosted) {
		this.cpzPosted = cpzPosted;
	}

	public BigDecimal getCurCpzBalance() {
		return curCpzBalance;
	}

	public void setCurCpzBalance(BigDecimal curCpzBalance) {
		this.curCpzBalance = curCpzBalance;
	}

	public BigDecimal getLpiTillLBD() {
		return lpiTillLBD;
	}

	public void setLpiTillLBD(BigDecimal lpiTillLBD) {
		this.lpiTillLBD = lpiTillLBD;
	}

	public BigDecimal getLppTillLBD() {
		return lppTillLBD;
	}

	public void setLppTillLBD(BigDecimal lppTillLBD) {
		this.lppTillLBD = lppTillLBD;
	}

	public BigDecimal getLpiAmount() {
		return lpiAmount;
	}

	public void setLpiAmount(BigDecimal lpiAmount) {
		this.lpiAmount = lpiAmount;
	}

	public BigDecimal getLppAmount() {
		return lppAmount;
	}

	public void setLppAmount(BigDecimal lppAmount) {
		this.lppAmount = lppAmount;
	}

	public BigDecimal getGstLpiTillLBD() {
		return gstLpiTillLBD;
	}

	public void setGstLpiTillLBD(BigDecimal gstLpiTillLBD) {
		this.gstLpiTillLBD = gstLpiTillLBD;
	}

	public BigDecimal getGstLppTillLBD() {
		return gstLppTillLBD;
	}

	public void setGstLppTillLBD(BigDecimal gstLppTillLBD) {
		this.gstLppTillLBD = gstLppTillLBD;
	}

	public BigDecimal getGstLpiAmount() {
		return gstLpiAmount;
	}

	public void setGstLpiAmount(BigDecimal gstLpiAmount) {
		this.gstLpiAmount = gstLpiAmount;
	}

	public BigDecimal getGstLppAmount() {
		return gstLppAmount;
	}

	public void setGstLppAmount(BigDecimal gstLppAmount) {
		this.gstLppAmount = gstLppAmount;
	}

	public BigDecimal getAdvanceEMI() {
		return advanceEMI;
	}

	public void setAdvanceEMI(BigDecimal advanceEMI) {
		this.advanceEMI = advanceEMI;
	}

	public String getGlPaymentSts() {
		return glPaymentSts;
	}

	public void setGlPaymentSts(String glPaymentSts) {
		this.glPaymentSts = glPaymentSts;
	}

	public BigDecimal getSvnAcrTillLBD() {
		return svnAcrTillLBD;
	}

	public void setSvnAcrTillLBD(BigDecimal svnAcrTillLBD) {
		this.svnAcrTillLBD = svnAcrTillLBD;
	}

	public boolean isSvnAcrCalReq() {
		return svnAcrCalReq;
	}

	public void setSvnAcrCalReq(boolean svnAcrCalReq) {
		this.svnAcrCalReq = svnAcrCalReq;
	}

	public BigDecimal getSvnPftAmount() {
		return svnPftAmount;
	}

	public void setSvnPftAmount(BigDecimal svnPftAmount) {
		this.svnPftAmount = svnPftAmount;
	}

	public int getNOAutoIncGrcEnd() {
		return NOAutoIncGrcEnd;
	}

	public void setNOAutoIncGrcEnd(int nOAutoIncGrcEnd) {
		NOAutoIncGrcEnd = nOAutoIncGrcEnd;
	}

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> fieldsAndValuesMap = new HashMap<String, Object>();

		getDeclaredFieldValues(fieldsAndValuesMap);

		return fieldsAndValuesMap;
	}

	/**
	 * 
	 * @param fieldsAndValuesMap
	 */
	public void getDeclaredFieldValues(Map<String, Object> map) {
		map.put("fpt_finReference", this.finReference);
		map.put("fpt_custId", this.custId);
		map.put("fpt_finBranch", this.finBranch);
		map.put("fpt_finType", this.finType);
		map.put("fpt_lastMdfDate", this.lastMdfDate);
		map.put("fpt_totalPftSchd", this.totalPftSchd);
		map.put("fpt_totalPftCpz", this.totalPftCpz);
		map.put("fpt_totalPftPaid", this.totalPftPaid);
		map.put("fpt_totalPftBal", this.totalPftBal);
		map.put("fpt_totalPftPaidInAdv", this.totalPftPaidInAdv);
		map.put("fpt_totalpriSchd", this.totalpriSchd);
		map.put("fpt_totalPriPaid", this.totalPriPaid);
		map.put("fpt_totalPriPaidInAdv", this.totalPriPaidInAdv);
		map.put("fpt_totalPriBal", this.totalPriBal);
		map.put("fpt_tdSchdPft", this.tdSchdPft);
		map.put("fpt_tdPftCpz", this.tdPftCpz);
		map.put("fpt_tdSchdPftPaid", this.tdSchdPftPaid);
		map.put("fpt_tdSchdPftBal", this.tdSchdPftBal);
		map.put("fpt_pftAmz", this.pftAmz);
		map.put("fpt_pftAmzNormal", this.pftAmzNormal);
		map.put("fpt_pftAmzPD", this.pftAmzPD);
		map.put("fpt_pftAmzSusp", this.pftAmzSusp);
		map.put("fpt_tdSchdPri", this.tdSchdPri);
		map.put("fpt_tdSchdPriPaid", this.tdSchdPriPaid);
		map.put("fpt_tdSchdPriBal", this.tdSchdPriBal);
		map.put("fpt_amzTillLBD", this.amzTillLBD);
		map.put("fpt_lpiTillLBD", this.lpiTillLBD);
		map.put("fpt_gstLpiTillLBD", this.gstLpiTillLBD);
		map.put("fpt_lppTillLBD", this.lppTillLBD);
		map.put("fpt_gstLppTillLBD", this.gstLppTillLBD);
		map.put("fpt_amzTillLBDNormal", this.amzTillLBDNormal);
		map.put("fpt_amzTillLBDPD", this.amzTillLBDPD);
		map.put("fpt_amzTillLBDPIS", this.amzTillLBDPIS);
		map.put("fpt_RepayFrq", this.RepayFrq);
		map.put("fpt_CustCIF", this.CustCIF);
		map.put("fpt_FinCcy", this.FinCcy);
		map.put("fpt_FinPurpose", this.FinPurpose);
		map.put("fpt_FinContractDate", this.FinContractDate);
		map.put("fpt_FinApprovedDate", this.FinApprovedDate);
		map.put("fpt_FinStartDate", this.FinStartDate);
		map.put("fpt_MaturityDate", this.MaturityDate);
		map.put("fpt_FullPaidDate", this.FullPaidDate);
		map.put("fpt_FinAmount", this.FinAmount);
		map.put("fpt_DownPayment", this.DownPayment);
		map.put("fpt_advanceEMI", this.advanceEMI);
		map.put("fpt_CurReducingRate", this.CurReducingRate);
		map.put("fpt_curFlatRate", this.curFlatRate);
		map.put("fpt_ODPrincipal", this.ODPrincipal);
		map.put("fpt_ODProfit", this.ODProfit);
		map.put("fpt_PenaltyPaid", this.PenaltyPaid);
		map.put("fpt_PenaltyDue", this.PenaltyDue);
		map.put("fpt_PenaltyWaived", this.PenaltyWaived);
		map.put("fpt_NSchdDate", this.NSchdDate);
		map.put("fpt_NSchdPri", this.NSchdPri);
		map.put("fpt_NSchdPft", this.NSchdPft);
		map.put("fpt_NSchdPriDue", this.NSchdPriDue);
		map.put("fpt_NSchdPftDue", this.NSchdPftDue);
		map.put("fpt_PftInSusp", this.PftInSusp);
		map.put("fpt_provision", this.provision);
		map.put("fpt_FinStatus", this.FinStatus);
		map.put("fpt_FinStsReason", this.FinStsReason);
		map.put("fpt_FinWorstStatus", this.FinWorstStatus);
		map.put("fpt_NOInst", this.NOInst);
		map.put("fpt_NOPaidInst", this.NOPaidInst);
		map.put("fpt_NOODInst", this.NOODInst);
		map.put("fpt_FinCommitmentRef", this.FinCommitmentRef);
		map.put("fpt_FinIsActive", this.FinIsActive);
		map.put("fpt_firstRepayDate", this.firstRepayDate);
		map.put("fpt_FirstRepayAmt", this.FirstRepayAmt);
		map.put("fpt_finalRepayAmt", this.finalRepayAmt);
		map.put("fpt_curODDays", this.curODDays);
		map.put("fpt_actualODDays", this.actualODDays);
		map.put("fpt_dueBucket", this.dueBucket);
		map.put("fpt_firstODDate", this.firstODDate);
		map.put("fpt_prvODDate", this.prvODDate);
		map.put("fpt_closingStatus", this.closingStatus);
		map.put("fpt_finCategory", this.finCategory);
		map.put("fpt_prvRpySchDate", this.prvRpySchDate);
		map.put("fpt_prvRpySchPri", this.prvRpySchPri);
		map.put("fpt_prvRpySchPft", this.prvRpySchPft);
		map.put("fpt_latestRpyDate", this.latestRpyDate);
		map.put("fpt_latestRpyPri", this.latestRpyPri);
		map.put("fpt_latestRpyPft", this.latestRpyPft);
		map.put("fpt_totalWriteoff", this.totalWriteoff);
		map.put("fpt_accumulatedDepPri", this.accumulatedDepPri);
		map.put("fpt_depreciatePri", this.depreciatePri);
		map.put("fpt_acrTillLBD", this.acrTillLBD);
		map.put("fpt_EarnedPft", this.EarnedPft);
		map.put("fpt_Unearned", this.Unearned);
		map.put("fpt_pftAccrued", this.pftAccrued);
		map.put("fpt_pftAccrueSusp", this.pftAccrueSusp);
		map.put("fpt_maxODDays", this.maxODDays);
		map.put("fpt_calPftOnPD", this.calPftOnPD);
		map.put("fpt_pftOnPDMethod", this.pftOnPDMethod);
		map.put("fpt_pftOnPDMrg", this.pftOnPDMrg);
		map.put("fpt_totPftOnPD", this.totPftOnPD);
		map.put("fpt_totPftOnPDPaid", this.totPftOnPDPaid);
		map.put("fpt_totPftOnPDWaived", this.totPftOnPDWaived);
		map.put("fpt_totPftOnPDDue", this.totPftOnPDDue);
		map.put("fpt_acrSuspTillLBD", this.acrSuspTillLBD);
		map.put("fpt_prvMthAmz", this.prvMthAmz);
		map.put("fpt_prvMthAmzNrm", this.prvMthAmzNrm);
		map.put("fpt_prvMthAmzPD", this.prvMthAmzPD);
		map.put("fpt_prvMthAmzSusp", this.prvMthAmzSusp);
		map.put("fpt_prvMthAcr", this.prvMthAcr);
		map.put("fpt_prvMthAcrSusp", this.prvMthAcrSusp);
		map.put("fpt_firstDisbDate", this.firstDisbDate);
		map.put("fpt_latestDisbDate", this.latestDisbDate);
		map.put("fpt_futureInst", this.futureInst);
		map.put("fpt_remainingTenor", this.remainingTenor);
		map.put("fpt_totalTenor", this.totalTenor);
		map.put("fpt_excessAmt", this.excessAmt);
		map.put("fpt_emiInAdvance", this.emiInAdvance);
		map.put("fpt_payableAdvise", this.payableAdvise);
		map.put("fpt_excessAmtResv", this.excessAmtResv);
		map.put("fpt_emiInAdvanceResv", this.emiInAdvanceResv);
		map.put("fpt_payableAdviseResv", this.payableAdviseResv);
		map.put("fpt_productCategory", this.productCategory);
		map.put("fpt_futureRpyPri", this.futureRpyPri);
		map.put("fpt_futureRpyPft", this.futureRpyPft);
		map.put("fpt_totChargesPaid", this.totChargesPaid);
		map.put("fpt_linkedFinRef", this.linkedFinRef);
		map.put("fpt_closedlinkedFinRef", this.closedlinkedFinRef);
		map.put("fpt_bounceAmt", this.bounceAmt);
		map.put("fpt_bounceAmtDue", this.bounceAmtDue);
		map.put("fpt_bounceAmtPaid", this.bounceAmtPaid);
		map.put("fpt_upfrontFee", this.upfrontFee);
		map.put("fpt_lastDisburseDate", this.lastDisburseDate);
		map.put("fpt_receivableAdvise", this.receivableAdvise);
		map.put("fpt_excessAmtBal", this.excessAmtBal);
		map.put("fpt_emiInAdvanceBal", this.emiInAdvanceBal);
		map.put("fpt_receivableAdviseBal", this.receivableAdviseBal);
		map.put("fpt_payableAdviseBal", this.payableAdviseBal);
		map.put("fpt_maxRpyAmount", this.maxRpyAmount);
		map.put("fpt_cpzPosted", this.cpzPosted);
		map.put("fpt_curCpzBalance", this.curCpzBalance);
		map.put("fpt_lpiAmount", this.lpiAmount);
		map.put("fpt_gstLpiAmount", this.gstLpiAmount);
		map.put("fpt_lppAmount", this.lppAmount);
		map.put("fpt_gstLppAmount", this.gstLppAmount);
		map.put("fpt_glPaymentSts", this.glPaymentSts);
		map.put("fpt_svnAcrTillLBD", this.svnAcrTillLBD);
		map.put("fpt_svnPftAmount", this.svnPftAmount);
		map.put("fpt_svnAcrCalReq", this.svnAcrCalReq);
		map.put("fpt_NOAutoIncGrcEnd", this.NOAutoIncGrcEnd);
		map.put("fpt_assignBPI1", this.assignBPI1);
		map.put("fpt_assignBPI2", this.assignBPI2);
		map.put("fpt_baseRateCode", this.baseRateCode);
		map.put("fpt_splRateCode", this.splRateCode);
		map.put("fpt_baseRate", this.baseRate);
		map.put("fpt_splRate", this.splRate);
		map.put("fpt_mrgRate", this.mrgRate);
		map.put("fpt_tdTdsAmount", this.tdTdsAmount);
		map.put("fpt_tdTdsPaid", this.tdTdsPaid);
		map.put("fpt_tdTdsBal", this.tdTdsBal);
		map.put("fpt_tdsAccrued", this.tdsAccrued);
		map.put("fpt_totalSvnAmount", this.totalSvnAmount);
		map.put("fpt_noInstEarlyStl", this.noInstEarlyStl);
		map.put("fpt_gapIntAmz", this.gapIntAmz);
		map.put("fpt_gapIntAmzLbd", this.gapIntAmzLbd);
		map.put("fpt_prvMthGapIntAmz", this.prvMthGapIntAmz);
		map.put("fpt_svAmount", this.svAmount);
		map.put("fpt_cbAmount", this.cbAmount);
		map.put("fpt_aMZMethod", this.aMZMethod);
		map.put("fpt_writeoffLoan", this.writeoffLoan);
		map.put("fpt_extODDays", this.extODDays);
		map.put("fpt_eventProperties", this.eventProperties);

	}

	public BigDecimal getAssignBPI1() {
		return assignBPI1;
	}

	public void setAssignBPI1(BigDecimal assignBPI1) {
		this.assignBPI1 = assignBPI1;
	}

	public BigDecimal getAssignBPI2() {
		return assignBPI2;
	}

	public void setAssignBPI2(BigDecimal assignBPI2) {
		this.assignBPI2 = assignBPI2;
	}

	public String getBaseRateCode() {
		return baseRateCode;
	}

	public void setBaseRateCode(String baseRateCode) {
		this.baseRateCode = baseRateCode;
	}

	public String getSplRateCode() {
		return splRateCode;
	}

	public void setSplRateCode(String splRateCode) {
		this.splRateCode = splRateCode;
	}

	public BigDecimal getSplRate() {
		return splRate;
	}

	public void setSplRate(BigDecimal splRate) {
		this.splRate = splRate;
	}

	public BigDecimal getMrgRate() {
		return mrgRate;
	}

	public void setMrgRate(BigDecimal mrgRate) {
		this.mrgRate = mrgRate;
	}

	public BigDecimal getTdTdsAmount() {
		return tdTdsAmount;
	}

	public void setTdTdsAmount(BigDecimal tdTdsAmount) {
		this.tdTdsAmount = tdTdsAmount;
	}

	public BigDecimal getTdTdsPaid() {
		return tdTdsPaid;
	}

	public void setTdTdsPaid(BigDecimal tdTdsPaid) {
		this.tdTdsPaid = tdTdsPaid;
	}

	public BigDecimal getTdTdsBal() {
		return tdTdsBal;
	}

	public void setTdTdsBal(BigDecimal tdTdsBal) {
		this.tdTdsBal = tdTdsBal;
	}

	public BigDecimal getTdsAccrued() {
		return tdsAccrued;
	}

	public void setTdsAccrued(BigDecimal tdsAccrued) {
		this.tdsAccrued = tdsAccrued;
	}

	public int getNoInstEarlyStl() {
		return noInstEarlyStl;
	}

	public void setNoInstEarlyStl(int noInstEarlyStl) {
		this.noInstEarlyStl = noInstEarlyStl;
	}

	public BigDecimal getBaseRate() {
		return baseRate;
	}

	public void setBaseRate(BigDecimal baseRate) {
		this.baseRate = baseRate;
	}

	public BigDecimal getTotalSvnAmount() {
		return totalSvnAmount;
	}

	public void setTotalSvnAmount(BigDecimal totalSvnAmount) {
		this.totalSvnAmount = totalSvnAmount;
	}

	public String getAMZMethod() {
		return aMZMethod;
	}

	public void setAMZMethod(String aMZMethod) {
		this.aMZMethod = aMZMethod;
	}

	public BigDecimal getGapIntAmz() {
		return gapIntAmz;
	}

	public void setGapIntAmz(BigDecimal gapIntAmz) {
		this.gapIntAmz = gapIntAmz;
	}

	public BigDecimal getGapIntAmzLbd() {
		return gapIntAmzLbd;
	}

	public void setGapIntAmzLbd(BigDecimal gapIntAmzLbd) {
		this.gapIntAmzLbd = gapIntAmzLbd;
	}

	public BigDecimal getPrvMthGapIntAmz() {
		return prvMthGapIntAmz;
	}

	public void setPrvMthGapIntAmz(BigDecimal prvMthGapIntAmz) {
		this.prvMthGapIntAmz = prvMthGapIntAmz;
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

	public boolean isProvision() {
		return provision;
	}

	public void setProvision(boolean provision) {
		this.provision = provision;
	}

	public EventProperties getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(EventProperties eventProperties) {
		this.eventProperties = eventProperties;
	}

	public boolean isWriteoffLoan() {
		return writeoffLoan;
	}

	public void setWriteoffLoan(boolean writeoffLoan) {
		this.writeoffLoan = writeoffLoan;
	}

	public int getExtODDays() {
		return extODDays;
	}

	public void setExtODDays(int extODDays) {
		this.extODDays = extODDays;
	}

	public String getCurDPDString() {
		return curDPDString;
	}

	public void setCurDPDString(String curDPDString) {
		this.curDPDString = curDPDString;
	}

}
