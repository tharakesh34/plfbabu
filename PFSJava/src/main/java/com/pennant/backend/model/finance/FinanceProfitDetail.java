package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FinanceProfitDetail implements Serializable {

	private static final long	serialVersionUID	= 6601637251967752181L;

	private String				finReference;
	private long				custId				= Long.MIN_VALUE;
	private String				finBranch;
	private String				finType;
	private Date				lastMdfDate;
	private BigDecimal			totalPftSchd		= BigDecimal.ZERO;
	private BigDecimal			totalPftCpz			= BigDecimal.ZERO;
	private BigDecimal			totalPftPaid		= BigDecimal.ZERO;
	private BigDecimal			totalPftBal			= BigDecimal.ZERO;
	private BigDecimal			totalPftPaidInAdv	= BigDecimal.ZERO;
	private BigDecimal			totalAdvPftSchd		= BigDecimal.ZERO;		//New Field 
	private BigDecimal			totalRbtSchd		= BigDecimal.ZERO;		//New Field 
	private BigDecimal			totalpriSchd		= BigDecimal.ZERO;
	private BigDecimal			totalPriPaid		= BigDecimal.ZERO;
	private BigDecimal			totalPriPaidInAdv	= BigDecimal.ZERO;		//New Field 
	private BigDecimal			totalPriBal			= BigDecimal.ZERO;
	//	Till date
	private BigDecimal			tdSchdPft			= BigDecimal.ZERO;
	private BigDecimal			tdPftCpz			= BigDecimal.ZERO;
	private BigDecimal			tdSchdPftPaid		= BigDecimal.ZERO;
	private BigDecimal			tdSchdPftBal		= BigDecimal.ZERO;
	private BigDecimal			tdSchdAdvPft		= BigDecimal.ZERO;		//New Field 
	private BigDecimal			tdSchdRbt			= BigDecimal.ZERO;		//New Field
	private BigDecimal			pftAmz				= BigDecimal.ZERO;
	private BigDecimal			pftAmzNormal		= BigDecimal.ZERO;		//New Field 
	private BigDecimal			pftAmzPD			= BigDecimal.ZERO;		//New Field 
	private BigDecimal			pftAmzSusp			= BigDecimal.ZERO;
	private BigDecimal			tdSchdPri			= BigDecimal.ZERO;
	private BigDecimal			tdSchdPriPaid		= BigDecimal.ZERO;
	private BigDecimal			tdSchdPriBal		= BigDecimal.ZERO;
	//After posting
	private BigDecimal			amzTillLBD			= BigDecimal.ZERO;
	private BigDecimal			amzTillLBDNormal	= BigDecimal.ZERO;		//new
	private BigDecimal			amzTillLBDPD		= BigDecimal.ZERO;		//new
	private BigDecimal			amzTillLBDPIS		= BigDecimal.ZERO;		//new

	// others
	private String				RepayFrq;
	private String				CustCIF;
	private String				FinCcy;
	private String				FinPurpose;
	private Date				FinContractDate;
	private Date				FinApprovedDate;
	private Date				FinStartDate;
	private Date				MaturityDate;
	private Date				FullPaidDate;
	private BigDecimal			FinAmount			= BigDecimal.ZERO;
	private BigDecimal			DownPayment			= BigDecimal.ZERO;
	private BigDecimal			CurReducingRate		= BigDecimal.ZERO;
	private BigDecimal			curFlatRate			= BigDecimal.ZERO;
	private BigDecimal			ODPrincipal			= BigDecimal.ZERO;
	private BigDecimal			ODProfit			= BigDecimal.ZERO;
	private BigDecimal			PenaltyPaid			= BigDecimal.ZERO;
	private BigDecimal			PenaltyDue			= BigDecimal.ZERO;
	private BigDecimal			PenaltyWaived		= BigDecimal.ZERO;

	private Date				NSchdDate;
	private BigDecimal			NSchdPri			= BigDecimal.ZERO;
	private BigDecimal			NSchdPft			= BigDecimal.ZERO;
	private BigDecimal			NSchdPriDue			= BigDecimal.ZERO;
	private BigDecimal			NSchdPftDue			= BigDecimal.ZERO;

	private boolean				PftInSusp;

	private String				FinStatus;
	private String				FinStsReason;
	private String				FinWorstStatus;
	private int					NOInst				= 0;
	private int					NOPaidInst			= 0;
	private int					NOODInst			= 0;
	private String				FinAccount;
	private String				FinAcType;
	private String				DisbAccountId;
	private String				DisbActCcy;
	private String				RepayAccountId;
	private String				FinCustPftAccount;
	private String				IncomeAccount;								//Pending
	private String				UEIncomeSuspAccount;						//Pending
	private String				FinCommitmentRef;
	private boolean				FinIsActive;
	private Date				firstRepayDate;
	private BigDecimal			FirstRepayAmt		= BigDecimal.ZERO;
	private BigDecimal			finalRepayAmt		= BigDecimal.ZERO;
	private int					curODDays			= 0;
	private int					dueBucket			= 0;

	private Date				firstODDate;
	private Date				prvODDate;
	private String				closingStatus;
	private String				finCategory;
	private Date				prvRpySchDate;
	private BigDecimal			prvRpySchPri		= BigDecimal.ZERO;
	private BigDecimal			prvRpySchPft		= BigDecimal.ZERO;
	private Date				latestRpyDate;
	private BigDecimal			latestRpyPri		= BigDecimal.ZERO;
	private BigDecimal			latestRpyPft		= BigDecimal.ZERO;
	private BigDecimal			totalWriteoff		= BigDecimal.ZERO;
	// Depreciation
	private BigDecimal			accumulatedDepPri	= BigDecimal.ZERO;
	private BigDecimal			depreciatePri		= BigDecimal.ZERO;
	//others
	private BigDecimal			disburse			= BigDecimal.ZERO;
	private BigDecimal			downpay				= BigDecimal.ZERO;

	private BigDecimal			acrTillLBD			= BigDecimal.ZERO;
	private BigDecimal			EarnedPft			= BigDecimal.ZERO;
	private BigDecimal			Unearned			= BigDecimal.ZERO;
	private BigDecimal			pftAccrued			= BigDecimal.ZERO;
	private BigDecimal			pftAccrueSusp		= BigDecimal.ZERO;

	//New fields on 14APR17. If possible rearrange the fields to keep relevent fields together
	private int					maxODDays			= 0;
	private boolean				calPftOnPD			= false;
	private String				pftOnPDMethod		= "";
	private BigDecimal			pftOnPDMrg			= BigDecimal.ZERO;
	private BigDecimal			totPftOnPD			= BigDecimal.ZERO;
	private BigDecimal			totPftOnPDPaid		= BigDecimal.ZERO;
	private BigDecimal			totPftOnPDWaived	= BigDecimal.ZERO;
	private BigDecimal			totPftOnPDDue		= BigDecimal.ZERO;
	private BigDecimal			acrSuspTillLBD		= BigDecimal.ZERO;
	private BigDecimal			prvMthAmz			= BigDecimal.ZERO;
	private BigDecimal			prvMthAmzNrm		= BigDecimal.ZERO;
	private BigDecimal			prvMthAmzPD			= BigDecimal.ZERO;
	private BigDecimal			prvMthAmzSusp		= BigDecimal.ZERO;
	private BigDecimal			prvMthAcr			= BigDecimal.ZERO;
	private BigDecimal			prvMthAcrSusp		= BigDecimal.ZERO;
	private Date				firstDisbDate;
	private Date				latestDisbDate;
	private int					futureInst			= 0;
	private int					remainingTenor		= 0;
	private int					totalTenor			= 0;
	private BigDecimal			excessAmt			= BigDecimal.ZERO;
	private BigDecimal			emiInAdvance		= BigDecimal.ZERO;
	private BigDecimal			payableAdvise		= BigDecimal.ZERO;
	private BigDecimal			excessAmtResv		= BigDecimal.ZERO;
	private BigDecimal			emiInAdvanceResv	= BigDecimal.ZERO;
	private BigDecimal			payableAdviseResv	= BigDecimal.ZERO;
	private String				productCategory;

	//Newly Added Fields for SOA 
	private BigDecimal			futureRpyPri		= BigDecimal.ZERO;
	private BigDecimal			futureRpyPft		= BigDecimal.ZERO;
	private BigDecimal			totChargesPaid		= BigDecimal.ZERO;
	private BigDecimal			linkedFinRef		= BigDecimal.ZERO;
	private BigDecimal			closedlinkedFinRef	= BigDecimal.ZERO;
	private BigDecimal			bounceAmt			= BigDecimal.ZERO;
	private BigDecimal			bounceAmtDue		= BigDecimal.ZERO;
	private BigDecimal			bounceAmtPaid		= BigDecimal.ZERO;
	private BigDecimal			upfrontFee			= BigDecimal.ZERO;
	private BigDecimal			lastDisburseDate	= BigDecimal.ZERO;
	private BigDecimal			receivableAdvise	= BigDecimal.ZERO;
	private BigDecimal			excessAmtBal		= BigDecimal.ZERO;
	private BigDecimal			emiInAdvanceBal		= BigDecimal.ZERO;
	private BigDecimal			receivableAdviseBal	= BigDecimal.ZERO;
	private BigDecimal			payableAdviseBal	= BigDecimal.ZERO;

	public FinanceProfitDetail() {

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

	public String getFinAccount() {
		return FinAccount;
	}

	public void setFinAccount(String finAccount) {
		FinAccount = finAccount;
	}

	public String getFinAcType() {
		return FinAcType;
	}

	public void setFinAcType(String finAcType) {
		FinAcType = finAcType;
	}

	public String getDisbAccountId() {
		return DisbAccountId;
	}

	public void setDisbAccountId(String disbAccountId) {
		DisbAccountId = disbAccountId;
	}

	public String getDisbActCcy() {
		return DisbActCcy;
	}

	public void setDisbActCcy(String disbActCcy) {
		DisbActCcy = disbActCcy;
	}

	public String getRepayAccountId() {
		return RepayAccountId;
	}

	public void setRepayAccountId(String repayAccountId) {
		RepayAccountId = repayAccountId;
	}

	public String getFinCustPftAccount() {
		return FinCustPftAccount;
	}

	public void setFinCustPftAccount(String finCustPftAccount) {
		FinCustPftAccount = finCustPftAccount;
	}

	public String getIncomeAccount() {
		return IncomeAccount;
	}

	public void setIncomeAccount(String incomeAccount) {
		IncomeAccount = incomeAccount;
	}

	public String getUEIncomeSuspAccount() {
		return UEIncomeSuspAccount;
	}

	public void setUEIncomeSuspAccount(String uEIncomeSuspAccount) {
		UEIncomeSuspAccount = uEIncomeSuspAccount;
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

	public BigDecimal getTotalAdvPftSchd() {
		return totalAdvPftSchd;
	}

	public void setTotalAdvPftSchd(BigDecimal totalAdvPftSchd) {
		this.totalAdvPftSchd = totalAdvPftSchd;
	}

	public BigDecimal getTotalRbtSchd() {
		return totalRbtSchd;
	}

	public void setTotalRbtSchd(BigDecimal totalRbtSchd) {
		this.totalRbtSchd = totalRbtSchd;
	}

	public BigDecimal getTotalPriPaidInAdv() {
		return totalPriPaidInAdv;
	}

	public void setTotalPriPaidInAdv(BigDecimal totalPriPaidInAdv) {
		this.totalPriPaidInAdv = totalPriPaidInAdv;
	}

	public BigDecimal getTdSchdAdvPft() {
		return tdSchdAdvPft;
	}

	public void setTdSchdAdvPft(BigDecimal tdSchdAdvPft) {
		this.tdSchdAdvPft = tdSchdAdvPft;
	}

	public BigDecimal getTdSchdRbt() {
		return tdSchdRbt;
	}

	public void setTdSchdRbt(BigDecimal tdSchdRbt) {
		this.tdSchdRbt = tdSchdRbt;
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

}
