package com.pennanttech.pff.model;

import java.math.BigDecimal;
import java.util.Date;

public class CollectionFinanceDetail {

	private int id;
	private String loanReference;
	private String custCif;
	private String loanType;
	private String loanTypeDesc;
	private String currency;
	private String productCode;
	private String productDesc;
	private String branchCode;
	private String branchName;
	private Date finStartDate;
	private Date maturityDate;
	private int NOInst = 0;
	private int NOPaidInst = 0;
	private int NOODInst = 0;
	private Date firstRepayDate;
	private BigDecimal firstrepayamount;
	private Date NSchdDate;
	private BigDecimal NSchdPri = BigDecimal.ZERO;
	private BigDecimal NSchdPft = BigDecimal.ZERO;
	private BigDecimal NSchdPriDue = BigDecimal.ZERO;
	private BigDecimal NSchdPftDue = BigDecimal.ZERO;
	private int totOutStandingAmt;
	private Date overDueDate;
	private int curODDays = 0;
	private int actualODDays = 0;
	private int dueBucket = 0;
	private BigDecimal ODPrincipal = BigDecimal.ZERO;
	private BigDecimal ODProfit = BigDecimal.ZERO;
	private BigDecimal PenaltyPaid = BigDecimal.ZERO;
	private BigDecimal PenaltyDue = BigDecimal.ZERO;
	private BigDecimal PenaltyWaived = BigDecimal.ZERO;
	private BigDecimal bounceCharges;
	private String FinStatus;
	private String FinStsReason;
	private String FinStsWorst;
	private boolean FinActive;
	private char recordStatus;

	public CollectionFinanceDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	// Getter And Setter

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLoanReference() {
		return loanReference;
	}

	public void setLoanReference(String loanReference) {
		this.loanReference = loanReference;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getLoanTypeDesc() {
		return loanTypeDesc;
	}

	public void setLoanTypeDesc(String loanTypeDesc) {
		this.loanTypeDesc = loanTypeDesc;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
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

	public Date getFirstRepayDate() {
		return firstRepayDate;
	}

	public void setFirstRepayDate(Date firstRepayDate) {
		this.firstRepayDate = firstRepayDate;
	}

	public BigDecimal getFirstrepayamount() {
		return firstrepayamount;
	}

	public void setFirstrepayamount(BigDecimal firstrepayamount) {
		this.firstrepayamount = firstrepayamount;
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

	public int getTotOutStandingAmt() {
		return totOutStandingAmt;
	}

	public void setTotOutStandingAmt(int totOutStandingAmt) {
		this.totOutStandingAmt = totOutStandingAmt;
	}

	public Date getOverDueDate() {
		return overDueDate;
	}

	public void setOverDueDate(Date overDueDate) {
		this.overDueDate = overDueDate;
	}

	public int getCurODDays() {
		return curODDays;
	}

	public void setCurODDays(int curODDays) {
		this.curODDays = curODDays;
	}

	public int getActualODDays() {
		return actualODDays;
	}

	public void setActualODDays(int actualODDays) {
		this.actualODDays = actualODDays;
	}

	public int getDueBucket() {
		return dueBucket;
	}

	public void setDueBucket(int dueBucket) {
		this.dueBucket = dueBucket;
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

	public BigDecimal getBounceCharges() {
		return bounceCharges;
	}

	public void setBounceCharges(BigDecimal bounceCharges) {
		this.bounceCharges = bounceCharges;
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

	public String getFinStsWorst() {
		return FinStsWorst;
	}

	public void setFinStsWorst(String finStsWorst) {
		FinStsWorst = finStsWorst;
	}

	public boolean isFinActive() {
		return FinActive;
	}

	public void setFinActive(boolean finActive) {
		FinActive = finActive;
	}

	public char getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(char recordStatus) {
		this.recordStatus = recordStatus;
	}

}
