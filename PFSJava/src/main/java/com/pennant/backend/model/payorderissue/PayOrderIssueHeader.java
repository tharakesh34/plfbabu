package com.pennant.backend.model.payorderissue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class PayOrderIssueHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 384180539764860246L;

	private long finID;
	private String finReference;
	private BigDecimal totalPOAmount = BigDecimal.ZERO;
	private int totalPOCount;
	private BigDecimal issuedPOAmount = BigDecimal.ZERO;
	private int issuedPOCount;
	private BigDecimal pODueAmount = BigDecimal.ZERO;
	private int pODueCount;
	private PayOrderIssueHeader befImage;
	private LoggedInUser userDetails;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private List<FinAdvancePayments> finAdvancePaymentsList = new ArrayList<FinAdvancePayments>();
	private FinanceMain financeMain;
	private BigDecimal finAssetValue = BigDecimal.TEN;

	// others
	private String custCIF;
	private String custShrtName;
	private Date requestDate;
	private String finType;
	private String finTypeDesc;
	private String finCcy;

	private boolean loanApproved;
	private boolean finIsActive;
	private boolean alwMultiPartyDisb;

	private List<FinanceDisbursement> financeDisbursements;
	private List<FinanceDisbursement> approvedFinanceDisbursements;
	private List<VASRecording> vASRecordings = new ArrayList<VASRecording>();
	private DocumentDetails documentDetails;

	private List<FinCovenantType> covenantTypeList = new ArrayList<FinCovenantType>();
	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>(1);
	private List<FinFeeDetail> finFeeDetailList = new ArrayList<FinFeeDetail>(1);
	private List<JointAccountDetail> jointAccountDetails = new ArrayList<JointAccountDetail>(1);

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
		excludeFields.add("finAdvancePaymentsList");
		excludeFields.add("loanApproved");
		excludeFields.add("financeDisbursements");
		excludeFields.add("approvedFinanceDisbursements");
		excludeFields.add("documentDetails");
		excludeFields.add("finIsActive");
		excludeFields.add("financeMain");
		excludeFields.add("alwMultiPartyDisb");
		excludeFields.add("vASRecordings");
		excludeFields.add("finAssetValue");
		excludeFields.add("jointAccountDetails");

		return excludeFields;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
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

	public BigDecimal getTotalPOAmount() {
		return totalPOAmount;
	}

	public void setTotalPOAmount(BigDecimal totalPOAmount) {
		this.totalPOAmount = totalPOAmount;
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

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
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

	public List<FinanceDisbursement> getApprovedFinanceDisbursements() {
		return approvedFinanceDisbursements;
	}

	public void setApprovedFinanceDisbursements(List<FinanceDisbursement> approvedFinanceDisbursements) {
		this.approvedFinanceDisbursements = approvedFinanceDisbursements;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public boolean isAlwMultiPartyDisb() {
		return alwMultiPartyDisb;
	}

	public void setAlwMultiPartyDisb(boolean alwMultiPartyDisb) {
		this.alwMultiPartyDisb = alwMultiPartyDisb;
	}

	public DocumentDetails getDocumentDetails() {
		return documentDetails;
	}

	public void setDocumentDetails(DocumentDetails documentDetails) {
		this.documentDetails = documentDetails;
	}

	public List<FinCovenantType> getCovenantTypeList() {
		return covenantTypeList;
	}

	public void setCovenantTypeList(List<FinCovenantType> covenantTypeList) {
		this.covenantTypeList = covenantTypeList;
	}

	public List<DocumentDetails> getDocumentDetailsList() {
		return documentDetailsList;
	}

	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
		this.documentDetailsList = documentDetailsList;
	}

	public List<FinFeeDetail> getFinFeeDetailList() {
		return finFeeDetailList;
	}

	public void setFinFeeDetailList(List<FinFeeDetail> finFeeDetailList) {
		this.finFeeDetailList = finFeeDetailList;
	}

	public List<VASRecording> getvASRecordings() {
		return vASRecordings;
	}

	public void setvASRecordings(List<VASRecording> vASRecordings) {
		this.vASRecordings = vASRecordings;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public List<JointAccountDetail> getJointAccountDetails() {
		return jointAccountDetails;
	}

	public void setJointAccountDetails(List<JointAccountDetail> jointAccountDetails) {
		this.jointAccountDetails = jointAccountDetails;
	}

}
