package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FinanceProfitDetail implements Serializable {

	private static final long	serialVersionUID		= 6601637251967752181L;

	private String				finReference;
	private long				custId					= Long.MIN_VALUE;
	private String				finBranch;
	private String				finType;
	private Date				lastMdfDate;
	private BigDecimal			totalPftSchd			= BigDecimal.ZERO;
	private BigDecimal			totalPftCpz				= BigDecimal.ZERO;
	private BigDecimal			totalPftPaid			= BigDecimal.ZERO;
	private BigDecimal			totalPftBal				= BigDecimal.ZERO;
	private BigDecimal			totalPftPaidInAdv		= BigDecimal.ZERO;
	private BigDecimal			totalAdvPftSchd			= BigDecimal.ZERO;		//New Field 
	private BigDecimal			totalRbtSchd			= BigDecimal.ZERO;		//New Field 
	private BigDecimal			totalpriSchd			= BigDecimal.ZERO;
	private BigDecimal			totalPriPaid			= BigDecimal.ZERO;
	private BigDecimal			totalPriPaidInAdv		= BigDecimal.ZERO;		//New Field 
	private BigDecimal			totalPriBal				= BigDecimal.ZERO;
	//	Till date
	private BigDecimal			tdSchdPft				= BigDecimal.ZERO;
	private BigDecimal			tdPftCpz				= BigDecimal.ZERO;
	private BigDecimal			tdSchdPftPaid			= BigDecimal.ZERO;
	private BigDecimal			tdSchdPftBal			= BigDecimal.ZERO;
	private BigDecimal			tdSchdAdvPft			= BigDecimal.ZERO;		//New Field 
	private BigDecimal			tdSchdRbt				= BigDecimal.ZERO;		//New Field
	private BigDecimal			tdPftAccrued			= BigDecimal.ZERO;		//TO BE Deleted
	private BigDecimal			tdPftAccrueSusp			= BigDecimal.ZERO;		//TO BE Deleted
	private BigDecimal			tdPftAmortized			= BigDecimal.ZERO;
	private BigDecimal			tdPftAmortizedNormal	= BigDecimal.ZERO;		//New Field 
	private BigDecimal			tdPftAmortizedPD		= BigDecimal.ZERO;		//New Field 
	private BigDecimal			tdPftAmortizedSusp		= BigDecimal.ZERO;
	private BigDecimal			tdSchdPri				= BigDecimal.ZERO;
	private BigDecimal			tdSchdPriPaid			= BigDecimal.ZERO;
	private BigDecimal			tdSchdPriBal			= BigDecimal.ZERO;
	private BigDecimal			acrTillLBD				= BigDecimal.ZERO;		//TO BE Deleted
	private BigDecimal			acrTillNBD				= BigDecimal.ZERO;		//TO BE Deleted
	private BigDecimal			acrTodayToNBD			= BigDecimal.ZERO;		//TO BE Deleted
	private BigDecimal			amzTillNBD				= BigDecimal.ZERO;		//TO BE Deleted
	//After posting
	private BigDecimal			amzTillLBD				= BigDecimal.ZERO;
	private BigDecimal			amzTillLBDNormal		= BigDecimal.ZERO;		//new
	private BigDecimal			amzTillLBDPD			= BigDecimal.ZERO;		//new
	private BigDecimal			amzTillLBDPIS			= BigDecimal.ZERO;		//new
	private BigDecimal			amzTodayToNBD			= BigDecimal.ZERO;
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
	private BigDecimal			FinAmount				= BigDecimal.ZERO;
	private BigDecimal			DownPayment				= BigDecimal.ZERO;
	private BigDecimal			CurReducingRate			= BigDecimal.ZERO;
	private BigDecimal			curFlatRate				= BigDecimal.ZERO;
	private BigDecimal			EarlyPaidAmt			= BigDecimal.ZERO;		//Duplicate with TotalPriPaidInAdv
	private BigDecimal			ODPrincipal				= BigDecimal.ZERO;
	private BigDecimal			ODProfit				= BigDecimal.ZERO;
	private BigDecimal			CRBODPrincipal			= BigDecimal.ZERO;		//TO BE Deleted
	private BigDecimal			CRBODProfit				= BigDecimal.ZERO;		//TO BE Deleted
	private BigDecimal			PenaltyPaid				= BigDecimal.ZERO;
	private BigDecimal			PenaltyDue				= BigDecimal.ZERO;
	private BigDecimal			PenaltyWaived			= BigDecimal.ZERO;

	private Date				NSchdDate;
	private BigDecimal			NSchdPri				= BigDecimal.ZERO;
	private BigDecimal			NSchdPft				= BigDecimal.ZERO;
	private BigDecimal			NSchdPriDue				= BigDecimal.ZERO;
	private BigDecimal			NSchdPftDue				= BigDecimal.ZERO;

	private BigDecimal			AccruePft				= BigDecimal.ZERO;		//TO BE Deleted
	private BigDecimal			EarnedPft				= BigDecimal.ZERO;		//TO BE Deleted
	private BigDecimal			Unearned				= BigDecimal.ZERO;		//TO BE Deleted
	private boolean				PftInSusp;
	private BigDecimal			SuspPft					= BigDecimal.ZERO;		//TO BE Deleted
	private BigDecimal			SuspPftAccrueTsfd		= BigDecimal.ZERO;		//TO BE Deleted
	private BigDecimal			PftAccrueTsfd			= BigDecimal.ZERO;
	private BigDecimal			prvPftAccrueTsfd		= BigDecimal.ZERO;		//TO BE Deleted
	private String				FinStatus;
	private String				FinStsReason;
	private String				FinWorstStatus;
	private BigDecimal			insPaidAmt				= BigDecimal.ZERO;		//New Table
	private BigDecimal			AdminPaidAmt			= BigDecimal.ZERO;		//New Table
	private BigDecimal			insCal					= BigDecimal.ZERO;		//New Table
	private int					NOInst					= 0;
	private int					NOPaidInst				= 0;
	private int					NOODInst				= 0;
	private int					CRBODInst				= 0;					//New Table
	private String				FinAccount;
	private String				FinAcType;
	private String				DisbAccountId;
	private String				DisbActCcy;
	private String				RepayAccountId;
	private String				FinCustPftAccount;
	private String				IncomeAccount;									//Pending
	private String				UEIncomeSuspAccount;							//Pending
	private String				FinCommitmentRef;
	private boolean				FinIsActive;
	private int					NORepayments			= 0;
	private Date				firstRepayDate;
	private BigDecimal			FirstRepayAmt			= BigDecimal.ZERO;
	private BigDecimal			LastRepayAmt			= BigDecimal.ZERO;
	private int					oDDays					= 0;
	private int					CRBODDays				= 0;					//New Table
	private Date				firstODDate;
	private Date				lastODDate;
	private Date				cRBFirstODDate;									//New Table
	private Date				cRBLastODDate;									//New Table
	private String				closingStatus;
	private String				finCategory;

	private Date				lastRpySchDate;
	private Date				nextRpySchDate;
	private BigDecimal			lastRpySchPri			= BigDecimal.ZERO;
	private BigDecimal			lastRpySchPft			= BigDecimal.ZERO;
	private Date				latestRpyDate;
	private BigDecimal			latestRpyPri			= BigDecimal.ZERO;
	private BigDecimal			latestRpyPft			= BigDecimal.ZERO;
	private Date				latestWriteOffDate;
	private BigDecimal			totalWriteoff			= BigDecimal.ZERO;
	// Depreciation
	private BigDecimal			accumulatedDepPri		= BigDecimal.ZERO;
	private BigDecimal			depreciatePri			= BigDecimal.ZERO;
	//others
	private BigDecimal			disburse				= BigDecimal.ZERO;
	private BigDecimal			downpay					= BigDecimal.ZERO;

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

	public BigDecimal getTdPftAccrued() {
		return tdPftAccrued;
	}

	public void setTdPftAccrued(BigDecimal tdPftAccrued) {
		this.tdPftAccrued = tdPftAccrued;
	}

	public BigDecimal getTdPftAccrueSusp() {
		return tdPftAccrueSusp;
	}

	public void setTdPftAccrueSusp(BigDecimal tdPftAccrueSusp) {
		this.tdPftAccrueSusp = tdPftAccrueSusp;
	}

	public BigDecimal getTdPftAmortized() {
		return tdPftAmortized;
	}

	public void setTdPftAmortized(BigDecimal tdPftAmortized) {
		this.tdPftAmortized = tdPftAmortized;
	}

	public BigDecimal getTdPftAmortizedSusp() {
		return tdPftAmortizedSusp;
	}

	public void setTdPftAmortizedSusp(BigDecimal tdPftAmortizedSusp) {
		this.tdPftAmortizedSusp = tdPftAmortizedSusp;
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

	public BigDecimal getAcrTillNBD() {
		return acrTillNBD;
	}

	public void setAcrTillNBD(BigDecimal acrTillNBD) {
		this.acrTillNBD = acrTillNBD;
	}

	public BigDecimal getAcrTodayToNBD() {
		return acrTodayToNBD;
	}

	public void setAcrTodayToNBD(BigDecimal acrTodayToNBDate) {
		this.acrTodayToNBD = acrTodayToNBDate;
	}

	public BigDecimal getAmzTillNBD() {
		return amzTillNBD;
	}

	public void setAmzTillNBD(BigDecimal amzTillNBD) {
		this.amzTillNBD = amzTillNBD;
	}

	public BigDecimal getAmzTillLBD() {
		return amzTillLBD;
	}

	public void setAmzTillLBD(BigDecimal amzTillLBD) {
		this.amzTillLBD = amzTillLBD;
	}

	public BigDecimal getAmzTodayToNBD() {
		return amzTodayToNBD;
	}

	public void setAmzTodayToNBD(BigDecimal amzTodayToNBDate) {
		this.amzTodayToNBD = amzTodayToNBDate;
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

	public BigDecimal getEarlyPaidAmt() {
		return EarlyPaidAmt;
	}

	public void setEarlyPaidAmt(BigDecimal earlyPaidAmt) {
		EarlyPaidAmt = earlyPaidAmt;
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

	public BigDecimal getAccruePft() {
		return AccruePft;
	}

	public void setAccruePft(BigDecimal accruePft) {
		AccruePft = accruePft;
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

	public BigDecimal getSuspPft() {
		return SuspPft;
	}

	public void setSuspPft(BigDecimal suspPft) {
		SuspPft = suspPft;
	}

	public BigDecimal getPftAccrueTsfd() {
		return PftAccrueTsfd;
	}

	public void setPftAccrueTsfd(BigDecimal pftAccrueTsfd) {
		PftAccrueTsfd = pftAccrueTsfd;
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

	public BigDecimal getInsPaidAmt() {
		return insPaidAmt;
	}

	public void setInsPaidAmt(BigDecimal insPaidAmt) {
		this.insPaidAmt = insPaidAmt;
	}

	public BigDecimal getAdminPaidAmt() {
		return AdminPaidAmt;
	}

	public void setAdminPaidAmt(BigDecimal adminPaidAmt) {
		AdminPaidAmt = adminPaidAmt;
	}

	public BigDecimal getInsCal() {
		return insCal;
	}

	public void setInsCal(BigDecimal insCal) {
		this.insCal = insCal;
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

	public int getNORepayments() {
		return NORepayments;
	}

	public void setNORepayments(int nORepayments) {
		NORepayments = nORepayments;
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

	public BigDecimal getLastRepayAmt() {
		return LastRepayAmt;
	}

	public void setLastRepayAmt(BigDecimal lastRepayAmt) {
		LastRepayAmt = lastRepayAmt;
	}

	public int getoDDays() {
		return oDDays;
	}

	public void setoDDays(int oDDays) {
		this.oDDays = oDDays;
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

	public BigDecimal getPrvPftAccrueTsfd() {
		return prvPftAccrueTsfd;
	}

	public void setPrvPftAccrueTsfd(BigDecimal prvPftAccrueTsfd) {
		this.prvPftAccrueTsfd = prvPftAccrueTsfd;
	}

	public BigDecimal getSuspPftAccrueTsfd() {
		return SuspPftAccrueTsfd;
	}

	public void setSuspPftAccrueTsfd(BigDecimal suspPftAccrueTsfd) {
		SuspPftAccrueTsfd = suspPftAccrueTsfd;
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

	public BigDecimal getCRBODPrincipal() {
		return CRBODPrincipal;
	}

	public void setCRBODPrincipal(BigDecimal cRBODPrincipal) {
		CRBODPrincipal = cRBODPrincipal;
	}

	public BigDecimal getCRBODProfit() {
		return CRBODProfit;
	}

	public void setCRBODProfit(BigDecimal cRBODProfit) {
		CRBODProfit = cRBODProfit;
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

	public BigDecimal getTdPftAmortizedNormal() {
		return tdPftAmortizedNormal;
	}

	public void setTdPftAmortizedNormal(BigDecimal tdPftAmortizedNormal) {
		this.tdPftAmortizedNormal = tdPftAmortizedNormal;
	}

	public BigDecimal getTdPftAmortizedPD() {
		return tdPftAmortizedPD;
	}

	public void setTdPftAmortizedPD(BigDecimal tdPftAmortizedPD) {
		this.tdPftAmortizedPD = tdPftAmortizedPD;
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

}
