package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class EodFinProfitDetail implements Serializable {

    private static final long serialVersionUID = 6601637251967752181L;
    
	private String 		finReference;
	private long 		custId = Long.MIN_VALUE;
	private String 		finBranch;
	private String 		finType;
	private Date 		lastMdfDate;
	private BigDecimal 	totalPftSchd = BigDecimal.ZERO;
	private BigDecimal 	totalPftCpz = BigDecimal.ZERO;
	private BigDecimal 	totalPftPaid = BigDecimal.ZERO;
	private BigDecimal 	totalPftBal = BigDecimal.ZERO;
	private BigDecimal	totalPftPaidInAdv = BigDecimal.ZERO;
	private BigDecimal 	totalPriPaid = BigDecimal.ZERO;
	private BigDecimal 	totalPriBal = BigDecimal.ZERO;
	private BigDecimal 	tdSchdPft = BigDecimal.ZERO;
	private BigDecimal 	tdPftCpz = BigDecimal.ZERO;
	private BigDecimal 	tdSchdPftPaid = BigDecimal.ZERO;
	private BigDecimal 	tdSchdPftBal = BigDecimal.ZERO;
	private BigDecimal 	tdPftAccrued = BigDecimal.ZERO;
	private BigDecimal 	tdPftAccrueSusp = BigDecimal.ZERO;
	private BigDecimal 	tdPftAmortized = BigDecimal.ZERO;
	private BigDecimal 	tdPftAmortizedSusp = BigDecimal.ZERO;
	private BigDecimal 	tdSchdPri = BigDecimal.ZERO;
	private BigDecimal 	tdSchdPriPaid = BigDecimal.ZERO;
	private BigDecimal 	tdSchdPriBal = BigDecimal.ZERO;
	private BigDecimal 	acrTillLBD = BigDecimal.ZERO;
	private BigDecimal 	acrTillNBD = BigDecimal.ZERO;
	private BigDecimal 	acrTodayToNBD = BigDecimal.ZERO;
	private BigDecimal 	amzTillNBD = BigDecimal.ZERO;
	private BigDecimal 	amzTillLBD = BigDecimal.ZERO;
	private BigDecimal 	amzTodayToNBD = BigDecimal.ZERO;
	
	private String     	repayFrq;
	private String     	custCIF;
	private String     	finCcy;
	private String     	finPurpose;
	private Date       	finContractDate;
	private Date       	finApprovedDate;
	private Date       	finStartDate;
	private Date       	maturityDate;
	private Date       	fullPaidDate;
	private BigDecimal 	finAmount =  BigDecimal.ZERO;
	private BigDecimal 	downPayment =  BigDecimal.ZERO;
	private BigDecimal 	curReducingRate =  BigDecimal.ZERO;
	private BigDecimal 	curFlatRate =  BigDecimal.ZERO;
	private BigDecimal 	totalpriSchd =  BigDecimal.ZERO;
	private BigDecimal 	earlyPaidAmt =  BigDecimal.ZERO;
	private BigDecimal 	oDPrincipal =  BigDecimal.ZERO;
	private BigDecimal 	oDProfit =  BigDecimal.ZERO;
	private BigDecimal 	penaltyPaid =  BigDecimal.ZERO;
	private BigDecimal 	penaltyDue =  BigDecimal.ZERO;
	private BigDecimal 	penaltyWaived =  BigDecimal.ZERO;
	private Date       	nSchdDate;
	private BigDecimal 	nSchdPri =  BigDecimal.ZERO;
	private BigDecimal 	nSchdPft =  BigDecimal.ZERO;
	private BigDecimal 	nSchdPriDue =  BigDecimal.ZERO;
	private BigDecimal 	nSchdPftDue =  BigDecimal.ZERO;
	private BigDecimal 	accruePft =  BigDecimal.ZERO;
	private BigDecimal 	earnedPft =  BigDecimal.ZERO;
	private BigDecimal 	unearned =  BigDecimal.ZERO;
	private boolean    	pftInSusp;
	private BigDecimal 	suspPft =  BigDecimal.ZERO;
	private BigDecimal 	pftAccrueTsfd =  BigDecimal.ZERO;
	private String     	finStatus;
	private String     	finStsReason;
	private String     	finWorstStatus;
	private BigDecimal 	takafulPaidAmt =  BigDecimal.ZERO;
	private BigDecimal 	adminPaidAmt =  BigDecimal.ZERO;
	private BigDecimal  takafulInsCal =  BigDecimal.ZERO;
	private long       	nOInst =  Long.MIN_VALUE;
	private long       	nOPaidInst =  Long.MIN_VALUE;
	private long       	nOODInst =  Long.MIN_VALUE;
	private String     	finAccount;
	private String     	finAcType;
	private String     	disbAccountId;
	private String     	disbActCcy;
	private String     	repayAccountId;
	private String     	finCustPftAccount;
	private String     	incomeAccount;
	private String     	uEIncomeSuspAccount;
	private String     	finCommitmentRef;
	private boolean     finIsActive;
	private long       	nORepayments =  Long.MIN_VALUE;
	private Date 		firstRepayDate;
	private BigDecimal 	firstRepayAmt =  BigDecimal.ZERO;
	private BigDecimal 	lastRepayAmt =  BigDecimal.ZERO;
	private int 	   	oDDays = 0;
	private Date 	   	firstODDate;
	private Date 	   	lastODDate;
	
	private String     	closingStatus;
	private String     	finCategory;
	private Date 	   	lastRpySchDate;
	private Date 	   	nextRpySchDate;
	private BigDecimal 	lastRpySchPri =  BigDecimal.ZERO;
	private BigDecimal 	lastRpySchPft =  BigDecimal.ZERO;
	private Date       	latestRpyDate;
	private BigDecimal 	latestRpyPri = BigDecimal.ZERO;
	private BigDecimal 	latestRpyPft = BigDecimal.ZERO;
	private Date       	latestWriteOffDate;
	private BigDecimal 	totalWriteoff = BigDecimal.ZERO;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
    	return repayFrq;
    }
	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
    }
	
	public String getCustCIF() {
    	return custCIF;
    }
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
    }
	
	public String getFinCcy() {
    	return finCcy;
    }
	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
    }
	
	public String getFinPurpose() {
    	return finPurpose;
    }
	public void setFinPurpose(String finPurpose) {
		this.finPurpose = finPurpose;
    }
	
	public Date getFinContractDate() {
    	return finContractDate;
    }
	public void setFinContractDate(Date finContractDate) {
		this.finContractDate = finContractDate;
    }
	
	public Date getFinApprovedDate() {
    	return finApprovedDate;
    }
	public void setFinApprovedDate(Date finApprovedDate) {
		this.finApprovedDate = finApprovedDate;
    }
	
	public Date getFinStartDate() {
    	return finStartDate;
    }
	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
    }
	
	public Date getMaturityDate() {
    	return maturityDate;
    }
	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
    }
	
	public Date getFullPaidDate() {
    	return fullPaidDate;
    }
	public void setFullPaidDate(Date fullPaidDate) {
		this.fullPaidDate = fullPaidDate;
    }
	
	public BigDecimal getFinAmount() {
    	return finAmount;
    }
	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
    }
	
	public BigDecimal getDownPayment() {
    	return downPayment;
    }
	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
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
	
	public BigDecimal getTotalpriSchd() {
    	return totalpriSchd;
    }
	public void setTotalpriSchd(BigDecimal totalpriSchd) {
		this.totalpriSchd = totalpriSchd;
    }
	
	public BigDecimal getEarlyPaidAmt() {
    	return earlyPaidAmt;
    }
	public void setEarlyPaidAmt(BigDecimal earlyPaidAmt) {
		this.earlyPaidAmt = earlyPaidAmt;
    }
	
	public BigDecimal getODPrincipal() {
    	return oDPrincipal;
    }
	public void setODPrincipal(BigDecimal oDPrincipal) {
		this.oDPrincipal = oDPrincipal;
    }
	
	public BigDecimal getODProfit() {
    	return oDProfit;
    }
	public void setODProfit(BigDecimal oDProfit) {
		this.oDProfit = oDProfit;
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
	
	public Date getNSchdDate() {
    	return nSchdDate;
    }
	public void setNSchdDate(Date nSchdDate) {
		this.nSchdDate = nSchdDate;
    }
	
	public BigDecimal getNSchdPri() {
    	return nSchdPri;
    }
	public void setNSchdPri(BigDecimal nSchdPri) {
		this.nSchdPri = nSchdPri;
    }
	
	public BigDecimal getNSchdPft() {
    	return nSchdPft;
    }
	public void setNSchdPft(BigDecimal nSchdPft) {
		this.nSchdPft = nSchdPft;
    }
	
	public BigDecimal getNSchdPriDue() {
    	return nSchdPriDue;
    }
	public void setNSchdPriDue(BigDecimal nSchdPriDue) {
		this.nSchdPriDue = nSchdPriDue;
    }
	
	public BigDecimal getNSchdPftDue() {
    	return nSchdPftDue;
    }
	public void setNSchdPftDue(BigDecimal nSchdPftDue) {
		this.nSchdPftDue = nSchdPftDue;
    }
	
	public BigDecimal getAccruePft() {
    	return accruePft;
    }
	public void setAccruePft(BigDecimal accruePft) {
		this.accruePft = accruePft;
    }
	
	public BigDecimal getEarnedPft() {
    	return earnedPft;
    }
	public void setEarnedPft(BigDecimal earnedPft) {
		this.earnedPft = earnedPft;
    }
	
	public BigDecimal getUnearned() {
    	return unearned;
    }
	public void setUnearned(BigDecimal unearned) {
		this.unearned = unearned;
    }
	
	public boolean getPftInSusp() {
    	return pftInSusp;
    }
	public void setPftInSusp(boolean pftInSusp) {
		this.pftInSusp = pftInSusp;
    }
	
	public BigDecimal getSuspPft() {
    	return suspPft;
    }
	public void setSuspPft(BigDecimal suspPft) {
		this.suspPft = suspPft;
    }
	
	public BigDecimal getPftAccrueTsfd() {
    	return pftAccrueTsfd;
    }
	public void setPftAccrueTsfd(BigDecimal pftAccrueTsfd) {
		this.pftAccrueTsfd = pftAccrueTsfd;
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
	
	public String getFinWorstStatus() {
    	return finWorstStatus;
    }
	public void setFinWorstStatus(String finWorstStatus) {
		this.finWorstStatus = finWorstStatus;
    }
	
	public BigDecimal getTakafulPaidAmt() {
    	return takafulPaidAmt;
    }
	public void setTakafulPaidAmt(BigDecimal tAKAFULPaidAmt) {
    	takafulPaidAmt = tAKAFULPaidAmt;
    }
	
	public BigDecimal getAdminPaidAmt() {
    	return adminPaidAmt;
    }
	public void setAdminPaidAmt(BigDecimal adminPaidAmt) {
		this.adminPaidAmt = adminPaidAmt;
    }
	
	public BigDecimal getTakafulInsCal() {
    	return takafulInsCal;
    }
	public void setTakafulInsCal(BigDecimal tAKAFULInsCal) {
		takafulInsCal = tAKAFULInsCal;
    }
	
	public long getNOInst() {
    	return nOInst;
    }
	public void setNOInst(long nOInst) {
		this.nOInst = nOInst;
    }
	
	public long getNOPaidInst() {
    	return nOPaidInst;
    }
	public void setNOPaidInst(long nOPaidInst) {
		this.nOPaidInst = nOPaidInst;
    }
	
	public long getNOODInst() {
    	return nOODInst;
    }
	public void setNOODInst(long nOODInst) {
		this.nOODInst = nOODInst;
    }
	
	public String getFinAccount() {
    	return finAccount;
    }
	public void setFinAccount(String finAccount) {
		this.finAccount = finAccount;
    }
	
	public String getFinAcType() {
    	return finAcType;
    }
	public void setFinAcType(String finAcType) {
		this.finAcType = finAcType;
    }
	
	public String getDisbAccountId() {
    	return disbAccountId;
    }
	public void setDisbAccountId(String disbAccountId) {
		this.disbAccountId = disbAccountId;
    }
	
	public String getDisbActCcy() {
    	return disbActCcy;
    }
	public void setDisbActCcy(String disbActCcy) {
		this.disbActCcy = disbActCcy;
    }
	
	public String getRepayAccountId() {
    	return repayAccountId;
    }
	public void setRepayAccountId(String repayAccountId) {
		this.repayAccountId = repayAccountId;
    }
	
	public String getFinCustPftAccount() {
    	return finCustPftAccount;
    }
	public void setFinCustPftAccount(String finCustPftAccount) {
		this.finCustPftAccount = finCustPftAccount;
    }
	
	public String getIncomeAccount() {
    	return incomeAccount;
    }
	public void setIncomeAccount(String incomeAccount) {
		this.incomeAccount = incomeAccount;
    }
	
	public String getUEIncomeSuspAccount() {
    	return uEIncomeSuspAccount;
    }
	public void setUEIncomeSuspAccount(String uEIncomeSuspAccount) {
		this.uEIncomeSuspAccount = uEIncomeSuspAccount;
    }
	
	public String getFinCommitmentRef() {
    	return finCommitmentRef;
    }
	public void setFinCommitmentRef(String finCommitmentRef) {
		this.finCommitmentRef = finCommitmentRef;
    }
	
	public boolean isFinIsActive() {
    	return finIsActive;
    }
	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
    }
	
	public long getNORepayments() {
    	return nORepayments;
    }
	public void setNORepayments(long nORepayments) {
		this.nORepayments = nORepayments;
    }
	
	public Date getFirstRepayDate() {
		return firstRepayDate;
	}
	public void setFirstRepayDate(Date firstRepayDate) {
		this.firstRepayDate = firstRepayDate;
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
}
