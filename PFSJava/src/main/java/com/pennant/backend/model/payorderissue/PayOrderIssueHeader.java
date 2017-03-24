package com.pennant.backend.model.payorderissue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class PayOrderIssueHeader extends AbstractWorkflowEntity {
	private static final long					serialVersionUID		= 384180539764860246L;

	private String								finReference;
	private BigDecimal							totalPOAmount			= BigDecimal.ZERO;
	private int									totalPOCount;
	private BigDecimal							issuedPOAmount			= BigDecimal.ZERO;
	private int									issuedPOCount;
	private BigDecimal							pODueAmount				= BigDecimal.ZERO;
	private int									pODueCount;
	private boolean								newRecord				= false;
	private PayOrderIssueHeader					befImage;
	private LoggedInUser						userDetails;
	private HashMap<String, List<AuditDetail>>	auditDetailMap			= new HashMap<String, List<AuditDetail>>();
	private List<FinAdvancePayments>			finAdvancePaymentsList	= new ArrayList<FinAdvancePayments>();

	//others
	private String								custCIF;
	private String								custShrtName;
	private Date								requestDate;
	private String								finType;
	private String								finTypeDesc;
	private String								finCcy;
	private String								finBranch;
	private String								finBranchDesc;
	private String								profitDaysBasis;
	private int									numberOfTerms			= 0;
	private int									graceTerms				= 0;
	private Date								finStartDate;
	private Date								maturityDate;
	private BigDecimal							finAmount				= BigDecimal.ZERO;
	private BigDecimal							downPayBank				= BigDecimal.ZERO;
	private BigDecimal							downPaySupl				= BigDecimal.ZERO;
	private BigDecimal							feeChargeAmt			= BigDecimal.ZERO;
	private BigDecimal							finRepaymentAmount		= BigDecimal.ZERO;
	private BigDecimal							totalProfit				= BigDecimal.ZERO;
	private BigDecimal							effectiveRateOfReturn;
	private long								custID;
	private boolean								alwMultiPartyDisb;
	private boolean								quickDisb;
	private boolean								loanApproved;
	private boolean 							finIsActive;

	private List<FinanceDisbursement>			financeDisbursements;
	private List<FinFeeDetail>					finFeeDetails;

	//FIXME TO be removed from view	
	//	finCategory
	//	custEIDNumber
	//	mobileNumber
	//	financeDetail

	public boolean isNew() {
		return isNewRecord();
	}

	public PayOrderIssueHeader() {
		super();
	}

	public PayOrderIssueHeader(String finReference) {
		super();
		this.finReference = finReference;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custShrtName");
		excludeFields.add("custCIF");
		excludeFields.add("requestDate");
		excludeFields.add("finType");
		excludeFields.add("finTypeDesc");
		excludeFields.add("finCcy");
		excludeFields.add("finBranch");
		excludeFields.add("finBranchDesc");
		excludeFields.add("profitDaysBasis");
		excludeFields.add("numberOfTerms");
		excludeFields.add("graceTerms");
		excludeFields.add("finStartDate");
		excludeFields.add("maturityDate");
		excludeFields.add("finAmount");
		excludeFields.add("downPayBank");
		excludeFields.add("downPaySupl");
		excludeFields.add("feeChargeAmt");
		excludeFields.add("finRepaymentAmount");
		excludeFields.add("totalProfit");
		excludeFields.add("effectiveRateOfReturn");
		excludeFields.add("finAdvancePaymentsList");
		excludeFields.add("custID");
		excludeFields.add("alwMultiPartyDisb");
		excludeFields.add("quickDisb");
		excludeFields.add("loanApproved");
		excludeFields.add("financeDisbursements");
		excludeFields.add("finFeeDetails");
		excludeFields.add("finIsActive");
		return excludeFields;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public List<FinAdvancePayments> getFinAdvancePaymentsList() {
		return finAdvancePaymentsList;
	}

	public void setFinAdvancePaymentsList(List<FinAdvancePayments> poIssList) {
		this.finAdvancePaymentsList = poIssList;
	}

	public int getTotalPOCount() {
		return totalPOCount;
	}

	public void setTotalPOCount(int totalPOCount) {
		this.totalPOCount = totalPOCount;
	}

	public BigDecimal getIssuedPOAmount() {
		return issuedPOAmount;
	}

	public void setIssuedPOAmount(BigDecimal issuedPOAmount) {
		this.issuedPOAmount = issuedPOAmount;
	}

	public int getIssuedPOCount() {
		return issuedPOCount;
	}

	public void setIssuedPOCount(int issuedPOCount) {
		this.issuedPOCount = issuedPOCount;
	}

	public BigDecimal getpODueAmount() {
		return pODueAmount;
	}

	public void setpODueAmount(BigDecimal pODueAmount) {
		this.pODueAmount = pODueAmount;
	}

	public int getpODueCount() {
		return pODueCount;
	}

	public void setpODueCount(int pODueCount) {
		this.pODueCount = pODueCount;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinBranchDesc() {
		return finBranchDesc;
	}

	public void setFinBranchDesc(String finBranchDesc) {
		this.finBranchDesc = finBranchDesc;
	}

	public String getProfitDaysBasis() {
		return profitDaysBasis;
	}

	public void setProfitDaysBasis(String profitDaysBasis) {
		this.profitDaysBasis = profitDaysBasis;
	}

	public int getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public int getGraceTerms() {
		return graceTerms;
	}

	public void setGraceTerms(int graceTerms) {
		this.graceTerms = graceTerms;
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

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public BigDecimal getDownPayBank() {
		return downPayBank;
	}

	public void setDownPayBank(BigDecimal downPayBank) {
		this.downPayBank = downPayBank;
	}

	public BigDecimal getDownPaySupl() {
		return downPaySupl;
	}

	public void setDownPaySupl(BigDecimal downPaySupl) {
		this.downPaySupl = downPaySupl;
	}

	public BigDecimal getFeeChargeAmt() {
		return feeChargeAmt;
	}

	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
		this.feeChargeAmt = feeChargeAmt;
	}

	public BigDecimal getFinRepaymentAmount() {
		return finRepaymentAmount;
	}

	public void setFinRepaymentAmount(BigDecimal finRepaymentAmount) {
		this.finRepaymentAmount = finRepaymentAmount;
	}

	public BigDecimal getTotalProfit() {
		return totalProfit;
	}

	public void setTotalProfit(BigDecimal totalProfit) {
		this.totalProfit = totalProfit;
	}

	public BigDecimal getEffectiveRateOfReturn() {
		return effectiveRateOfReturn;
	}

	public void setEffectiveRateOfReturn(BigDecimal effectiveRateOfReturn) {
		this.effectiveRateOfReturn = effectiveRateOfReturn;
	}

	public BigDecimal getTotalPOAmount() {
		return totalPOAmount;
	}

	public void setTotalPOAmount(BigDecimal totalPOAmount) {
		this.totalPOAmount = totalPOAmount;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public PayOrderIssueHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(PayOrderIssueHeader befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public boolean isAlwMultiPartyDisb() {
		return alwMultiPartyDisb;
	}

	public void setAlwMultiPartyDisb(boolean alwMultiPartyDisb) {
		this.alwMultiPartyDisb = alwMultiPartyDisb;
	}

	public boolean isQuickDisb() {
		return quickDisb;
	}

	public void setQuickDisb(boolean quickDisb) {
		this.quickDisb = quickDisb;
	}

	public boolean isLoanApproved() {
		return loanApproved;
	}

	public void setLoanApproved(boolean loanApproved) {
		this.loanApproved = loanApproved;
	}

	public List<FinanceDisbursement> getFinanceDisbursements() {
		return financeDisbursements;
	}

	public void setFinanceDisbursements(List<FinanceDisbursement> financeDisbursements) {
		this.financeDisbursements = financeDisbursements;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public List<FinFeeDetail> getFinFeeDetails() {
		return finFeeDetails;
	}

	public void setFinFeeDetails(List<FinFeeDetail> finFeeDetails) {
		this.finFeeDetails = finFeeDetails;
	}
}
