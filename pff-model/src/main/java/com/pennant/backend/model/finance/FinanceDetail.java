/**
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinanceDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-03-2011 * * Modified Date :
 * 22-03-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-03-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.beneficiary.FinBeneficiary;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.financialsummary.DealRecommendationMerits;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceDetails;
import com.pennant.backend.model.finance.financialsummary.RecommendationNotes;
import com.pennant.backend.model.finance.financialsummary.RisksAndMitigants;
import com.pennant.backend.model.finance.financialsummary.SanctionConditions;
import com.pennant.backend.model.finance.financialsummary.SynopsisDetails;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.financemanagement.FinNomineeDetail;
import com.pennant.backend.model.financemanagement.StorageDetail;
import com.pennant.backend.model.isradetail.ISRADetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.spreadsheet.SpreadSheet;
import com.pennanttech.finance.tds.cerificate.model.TanAssignment;
import com.pennanttech.model.lien.LienHeader;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.eod.collateral.reval.model.CollateralRevaluation;
import com.pennanttech.pff.model.external.interfacedetails.InterfaceServiceDetails;

@XmlType(propOrder = { "finReference", "stp", "processStage", "finScheduleData", "foreClosureDetails",
		"customerDetails", "advancePaymentsList", "mandate", "jointAccountDetailList", "gurantorsDetailList",
		"documentDetailsList", "covenantTypeList", "collateralAssignmentList", "finFlagsDetails", "finFeeDetails",
		"returnDataSetList", "collateralSetup", "financeTaxDetails", "extendedDetails", "receiptAllocations",
		"finOCRHeader", "returnStatus", "receiptProcessingAmt", "returnStatus" })
@XmlRootElement(name = "finance")
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 3947699402597772444L;

	private long finID;
	@XmlElement
	private String finReference;
	@XmlElement(name = "financeSchedule")
	private FinScheduleData finScheduleData = new FinScheduleData();
	private boolean newRecord = false;
	private LoggedInUser userDetails;
	private FinanceDetail befImage;
	private boolean lovDescIsQDE;
	private boolean isExtSource = false;
	@XmlTransient
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>(1);
	private CustomerEligibilityCheck customerEligibilityCheck;
	private TATDetail tatDetail;
	private CollateralSetup collSetup;
	private List<FinanceCheckListReference> financeCheckList = new ArrayList<>(1);
	private List<FinanceReferenceDetail> checkList = new ArrayList<>(1);
	private Map<Long, Long> lovDescSelAnsCountMap = new HashMap<>(1);
	private List<FinanceReferenceDetail> finRefDetailsList;
	private List<FinanceReferenceDetail> aggrementList = new ArrayList<>(1);
	private List<FinanceReferenceDetail> eligibilityRuleList = new ArrayList<>(1);
	private List<FinanceEligibilityDetail> finElgRuleList = new ArrayList<>(1);
	private List<FinanceEligibilityDetail> elgRuleList = new ArrayList<>(1);
	private List<FinanceReferenceDetail> scoringGroupList = new ArrayList<>(1);
	private List<ScoringMetrics> finScoringMetricList = new ArrayList<>(1);
	private List<ScoringMetrics> nonFinScoringMetricList = new ArrayList<>(1);
	private Map<Long, List<ScoringMetrics>> scoringMetrics = new HashMap<>(1);
	private Map<Long, List<ScoringSlab>> scoringSlabs = new HashMap<>(1);
	private List<FinanceScoreHeader> finScoreHeaderList = new ArrayList<>(1);
	private Map<Long, List<FinanceScoreDetail>> scoreDetailListMap = new HashMap<>(1);
	private List<Rule> feeCharges = new ArrayList<>(1);
	private List<FinTypeFees> finTypeFeesList = new ArrayList<>();
	@XmlElementWrapper(name = "transactions")
	@XmlElement(name = "transaction")
	private List<ReturnDataSet> returnDataSetList = new ArrayList<>(1);
	private List<TransactionEntry> cmtFinanceEntries = new ArrayList<>(1);
	private List<ReturnDataSet> cmtDataSetList = new ArrayList<>(1);
	private List<TransactionEntry> stageTransactionEntries = new ArrayList<>(1);
	private List<ReturnDataSet> stageAccountingList = new ArrayList<>(1);
	@XmlElementWrapper(name = "documents")
	@XmlElement(name = "document")
	private List<DocumentDetails> documentDetailsList = new ArrayList<>(1);
	@XmlElementWrapper(name = "guarantors")
	@XmlElement(name = "guarantor")
	private List<GuarantorDetail> gurantorsDetailList = new ArrayList<>(1);
	@XmlElementWrapper(name = "coApplicants")
	@XmlElement(name = "coApplicant")
	private List<JointAccountDetail> jointAccountDetailList = new ArrayList<>(1);
	private List<FinanceDeviations> financeDeviations = new ArrayList<>();
	private List<FinanceDeviations> approvedFinanceDeviations = new ArrayList<>();
	private List<FinanceDeviations> manualDeviations = new ArrayList<>();
	private List<FinanceDeviations> approvedManualDeviations = new ArrayList<>();
	private List<FinCollaterals> financeCollaterals = new ArrayList<>(1);
	@XmlElementWrapper(name = "collaterals")
	@XmlElement(name = "collateral")
	private List<CollateralAssignment> collateralAssignmentList = new ArrayList<>(1);
	private List<FinAssetTypes> finAssetTypesList = new ArrayList<>(1);
	private List<ExtendedFieldRender> extendedFieldRenderList = new ArrayList<>(1);
	private List<FinBlacklistCustomer> finBlacklistCustomer = new ArrayList<>();
	private List<FinanceDedup> finDedupDetails = new ArrayList<>();
	private List<CustomerDedup> customerDedupList = new ArrayList<>();
	@XmlElement(name = "disbursement")
	private List<FinAdvancePayments> advancePaymentsList = new ArrayList<>();
	private List<FeePaymentDetail> feePaymentDetailList = new ArrayList<>();
	@XmlElementWrapper(name = "covenants")
	@XmlElement(name = "covenant")
	private List<FinCovenantType> covenantTypeList = new ArrayList<>(1);
	private Covenant covenant;
	@XmlElement(name = "covenantDetails")
	private List<Covenant> covenants = new ArrayList<>();
	@XmlElement(name = "customer")
	private CustomerDetails customerDetails;
	private StorageDetail storageDetail;
	private WIFCustomer customer;
	private Promotion promotion;
	private FinanceMaintenance financeMaintenance;
	private String accountingEventCode;
	private String moduleDefiner = "";
	private boolean actionSave = false;
	private boolean dataFetchComplete = false;
	private BigDecimal score = BigDecimal.ZERO;
	private BigDecimal custPOS = BigDecimal.ZERO;
	private Date valueDate;
	private int maxAge = 0;
	private int minAge = 0;
	private boolean directFinalApprove = false;
	private boolean repledgeProcess = false;
	private BigDecimal repledgeAmount = BigDecimal.ZERO;
	private long uploadReceiptId;
	private ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
	private ExtendedFieldRender extendedFieldRender;
	private transient Map<String, Object> lovDescExtendedFieldValues = new HashMap<>(1);
	private FinWriteoffPayment finwriteoffPayment;
	private FinRepayHeader finRepayHeader;
	@XmlElement(name = "mandateDetail")
	private Mandate mandate;
	@XmlElement(name = "securityMandateDetail")
	private Mandate securityMandate;
	@XmlElement(name = "taxDetail")
	private FinanceTaxDetail financeTaxDetail;
	@XmlElementWrapper(name = "financeFlags")
	@XmlElement(name = "financeFlag")
	private List<FinFlagsDetail> finFlagsDetails = new ArrayList<>();
	@XmlElementWrapper(name = "fees")
	@XmlElement(name = "fee")
	private List<FinFeeDetail> finFeeDetails;
	@XmlElement
	private WSReturnStatus returnStatus;
	@XmlElement
	private Boolean stp = true;
	@XmlElement
	private String processStage;
	@XmlElementWrapper(name = "collateralDetails")
	@XmlElement(name = "collateralDetail")
	private List<CollateralSetup> collaterals;
	@XmlElementWrapper(name = "foreClosures")
	@XmlElement(name = "foreClosure")
	private List<ForeClosure> foreClosureDetails;
	@XmlElementWrapper(name = "extendedDetails")
	@XmlElement(name = "extendedDetail")
	private List<ExtendedField> extendedDetails;
	@XmlElement
	private ChequeHeader chequeHeader;
	@XmlElement
	private ReasonHeader reasonHeader;
	@XmlElementWrapper(name = "legalDetails")
	@XmlElement(name = "legalDetail")
	private List<LegalDetail> legalDetailsList = new ArrayList<>();
	private Verification fiVerification;
	private Verification tvVerification;
	private Verification lvVerification;
	private Verification rcuVerification;
	private Verification pdVerification;
	@XmlElement
	private Verification legalVetting;
	@XmlElement
	private PSLDetail pslDetail;
	private boolean fiApprovalTab = false;
	private boolean fiInitTab = false;
	private boolean tvApprovalTab = false;
	private boolean tvInitTab = false;
	private boolean lvApprovalTab = false;
	private boolean lvInitTab = false;
	private boolean rcuApprovalTab = false;
	private boolean rcuInitTab = false;
	private boolean pdDetailTab = false;
	private Map<String, String> showTabDetailMap = new HashMap<>();
	@XmlElement
	private Long receiptId = Long.MIN_VALUE;
	private boolean samplingInitiator;
	private boolean samplingApprover;
	private Sampling sampling;
	private boolean legalInitiator;
	private Map<String, String> dataMap = new HashMap<>();
	private boolean sufficientScore;
	private boolean secondaryDedup = false;
	private FinNomineeDetail finNomineeDetail;
	private FinBeneficiary finBeneficiary;
	private boolean upFrentFee;
	private FinOption finOption;
	private List<FinOption> finOptions = new ArrayList<>();
	@XmlElementWrapper(name = "interfaceDetailList")
	@XmlElement(name = "interfaceDetailList")
	private List<InterfaceServiceDetails> interfaceDetailList = new ArrayList<>();
	private boolean validateUpfrontFees;
	private CollateralRevaluation collateralRevaluation = new CollateralRevaluation();
	private LMSServiceLog lmsServiceLog = new LMSServiceLog();
	private PaymentTransaction paymentTransaction;
	private boolean pdApprovalTab = false;
	private boolean pdInitTab = false;
	private Date appDate = null;
	private CreditReviewData creditReviewData;
	private AdvancePaymentDetail advancePaymentDetail;
	private Map<String, Object> creditRevDataMap = new HashMap<>();
	private boolean financialSummaryTab = true;
	private List<RisksAndMitigants> risksAndMitigantsList = new ArrayList<>(1);
	private List<SanctionConditions> sanctionDetailsList = new ArrayList<>(1);
	private List<DealRecommendationMerits> dealRecommendationMeritsDetailsList = new ArrayList<>(1);
	private List<DueDiligenceDetails> dueDiligenceDetailsList = new ArrayList<>(1);
	private List<RecommendationNotes> recommendationNoteList = new ArrayList<>(1);
	private DueDiligenceDetails dueDiligenceDetails;
	private String orderStatus;
	private SynopsisDetails synopsisDetails;
	private SpreadSheet spreadSheet = new SpreadSheet();
	private boolean spreadSheetloaded = false;
	private List<CollateralAssignment> tempCollateralAssignmentList = new ArrayList<>();
	@XmlElement
	private Boolean disbStp = false;
	private PricingDetail pricingDetail;
	private List<FinFeeConfig> finFeeConfigList;

	@XmlElement
	private BigDecimal receiptProcessingAmt;
	private long receiptTransactionId;
	private String paymentMode;

	// used for Interfaces
	private Long usrID;
	private String usrLogin;
	@XmlElement
	private FinOCRHeader finOCRHeader;
	private boolean vettingApprovalTab = false;
	private boolean vettingInitTab = false;
	private PMAY pmay;

	// Documents uploaded in DMS for already approved collateral, those documents need to synch PLF.
	private List<CollateralSetup> dmsCollateralDocuments;
	// Required only for the Covenants API
	@XmlElement
	private Boolean isOrigination = false;
	private List<LinkedFinances> linkedFinancesList;
	private Map<String, BigDecimal> gstPercentages = new HashMap<>();
	private ExtendedFieldExtension extendedFieldExtension = null;
	private List<TanAssignment> tanAssignments = new ArrayList<>();
	private ISRADetail israDetail = null;
	private LienHeader lienHeader;

	public FinanceDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("orderStatus");
		excludeFields.add("receiptProcessingAmt");
		excludeFields.add("isOrigination");
		excludeFields.add("gstPercentages");
		excludeFields.add("israDetail");
		excludeFields.add("lienHeader");

		return excludeFields;
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

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public FinanceDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(FinanceDetail befImage) {
		this.befImage = befImage;
	}

	public boolean isLovDescIsQDE() {
		return lovDescIsQDE;
	}

	public void setLovDescIsQDE(boolean lovDescIsQDE) {
		this.lovDescIsQDE = lovDescIsQDE;
	}

	public boolean isExtSource() {
		return isExtSource;
	}

	public void setExtSource(boolean isExtSource) {
		this.isExtSource = isExtSource;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public CustomerEligibilityCheck getCustomerEligibilityCheck() {
		return customerEligibilityCheck;
	}

	public void setCustomerEligibilityCheck(CustomerEligibilityCheck customerEligibilityCheck) {
		this.customerEligibilityCheck = customerEligibilityCheck;
	}

	public TATDetail getTatDetail() {
		return tatDetail;
	}

	public void setTatDetail(TATDetail tatDetail) {
		this.tatDetail = tatDetail;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public CollateralSetup getCollSetup() {
		return collSetup;
	}

	public void setCollSetup(CollateralSetup collSetup) {
		this.collSetup = collSetup;
	}

	public List<FinanceCheckListReference> getFinanceCheckList() {
		return financeCheckList;
	}

	public void setFinanceCheckList(List<FinanceCheckListReference> financeCheckList) {
		this.financeCheckList = financeCheckList;
	}

	public List<FinanceReferenceDetail> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<FinanceReferenceDetail> checkList) {
		this.checkList = checkList;
	}

	public Map<Long, Long> getLovDescSelAnsCountMap() {
		return lovDescSelAnsCountMap;
	}

	public void setLovDescSelAnsCountMap(Map<Long, Long> lovDescSelAnsCountMap) {
		this.lovDescSelAnsCountMap = lovDescSelAnsCountMap;
	}

	public List<FinanceReferenceDetail> getFinRefDetailsList() {
		return finRefDetailsList;
	}

	public void setFinRefDetailsList(List<FinanceReferenceDetail> finRefDetailsList) {
		this.finRefDetailsList = finRefDetailsList;
	}

	public List<FinanceReferenceDetail> getAggrementList() {
		return aggrementList;
	}

	public void setAggrementList(List<FinanceReferenceDetail> aggrementList) {
		this.aggrementList = aggrementList;
	}

	public List<FinanceReferenceDetail> getEligibilityRuleList() {
		return eligibilityRuleList;
	}

	public void setEligibilityRuleList(List<FinanceReferenceDetail> eligibilityRuleList) {
		this.eligibilityRuleList = eligibilityRuleList;
	}

	public List<FinanceEligibilityDetail> getFinElgRuleList() {
		return finElgRuleList;
	}

	public void setFinElgRuleList(List<FinanceEligibilityDetail> finElgRuleList) {
		this.finElgRuleList = finElgRuleList;
	}

	public List<FinanceEligibilityDetail> getElgRuleList() {
		return elgRuleList;
	}

	public void setElgRuleList(List<FinanceEligibilityDetail> elgRuleList) {
		this.elgRuleList = elgRuleList;
	}

	public List<FinanceReferenceDetail> getScoringGroupList() {
		return scoringGroupList;
	}

	public void setScoringGroupList(List<FinanceReferenceDetail> scoringGroupList) {
		this.scoringGroupList = scoringGroupList;
	}

	public List<ScoringMetrics> getFinScoringMetricList() {
		return finScoringMetricList;
	}

	public void setFinScoringMetricList(List<ScoringMetrics> finScoringMetricList) {
		this.finScoringMetricList = finScoringMetricList;
	}

	public List<ScoringMetrics> getNonFinScoringMetricList() {
		return nonFinScoringMetricList;
	}

	public void setNonFinScoringMetricList(List<ScoringMetrics> nonFinScoringMetricList) {
		this.nonFinScoringMetricList = nonFinScoringMetricList;
	}

	public Map<Long, List<ScoringMetrics>> getScoringMetrics() {
		return scoringMetrics;
	}

	public void setScoringMetrics(Map<Long, List<ScoringMetrics>> scoringMetrics) {
		this.scoringMetrics = scoringMetrics;
	}

	public Map<Long, List<ScoringSlab>> getScoringSlabs() {
		return scoringSlabs;
	}

	public void setScoringSlabs(Map<Long, List<ScoringSlab>> scoringSlabs) {
		this.scoringSlabs = scoringSlabs;
	}

	public List<FinanceScoreHeader> getFinScoreHeaderList() {
		return finScoreHeaderList;
	}

	public void setFinScoreHeaderList(List<FinanceScoreHeader> finScoreHeaderList) {
		this.finScoreHeaderList = finScoreHeaderList;
	}

	public Map<Long, List<FinanceScoreDetail>> getScoreDetailListMap() {
		return scoreDetailListMap;
	}

	public void setScoreDetailListMap(Map<Long, List<FinanceScoreDetail>> scoreDetailListMap) {
		this.scoreDetailListMap = scoreDetailListMap;
	}

	public List<Rule> getFeeCharges() {
		return feeCharges;
	}

	public void setFeeCharges(List<Rule> feeCharges) {
		this.feeCharges = feeCharges;
	}

	public List<FinTypeFees> getFinTypeFeesList() {
		return finTypeFeesList;
	}

	public void setFinTypeFeesList(List<FinTypeFees> finTypeFeesList) {
		this.finTypeFeesList = finTypeFeesList;
	}

	public List<ReturnDataSet> getReturnDataSetList() {
		return returnDataSetList;
	}

	public void setReturnDataSetList(List<ReturnDataSet> returnDataSetList) {
		this.returnDataSetList = returnDataSetList;
	}

	public List<TransactionEntry> getCmtFinanceEntries() {
		return cmtFinanceEntries;
	}

	public void setCmtFinanceEntries(List<TransactionEntry> cmtFinanceEntries) {
		this.cmtFinanceEntries = cmtFinanceEntries;
	}

	public List<ReturnDataSet> getCmtDataSetList() {
		return cmtDataSetList;
	}

	public void setCmtDataSetList(List<ReturnDataSet> cmtDataSetList) {
		this.cmtDataSetList = cmtDataSetList;
	}

	public List<TransactionEntry> getStageTransactionEntries() {
		return stageTransactionEntries;
	}

	public void setStageTransactionEntries(List<TransactionEntry> stageTransactionEntries) {
		this.stageTransactionEntries = stageTransactionEntries;
	}

	public List<ReturnDataSet> getStageAccountingList() {
		return stageAccountingList;
	}

	public void setStageAccountingList(List<ReturnDataSet> stageAccountingList) {
		this.stageAccountingList = stageAccountingList;
	}

	public List<DocumentDetails> getDocumentDetailsList() {
		return documentDetailsList;
	}

	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
		this.documentDetailsList = documentDetailsList;
	}

	public List<GuarantorDetail> getGurantorsDetailList() {
		return gurantorsDetailList;
	}

	public void setGurantorsDetailList(List<GuarantorDetail> gurantorsDetailList) {
		this.gurantorsDetailList = gurantorsDetailList;
	}

	public List<JointAccountDetail> getJointAccountDetailList() {
		return jointAccountDetailList;
	}

	public void setJointAccountDetailList(List<JointAccountDetail> jointAccountDetailList) {
		this.jointAccountDetailList = jointAccountDetailList;
	}

	public List<FinanceDeviations> getFinanceDeviations() {
		return financeDeviations;
	}

	public void setFinanceDeviations(List<FinanceDeviations> financeDeviations) {
		this.financeDeviations = financeDeviations;
	}

	public List<FinanceDeviations> getApprovedFinanceDeviations() {
		return approvedFinanceDeviations;
	}

	public void setApprovedFinanceDeviations(List<FinanceDeviations> approvedFinanceDeviations) {
		this.approvedFinanceDeviations = approvedFinanceDeviations;
	}

	public List<FinanceDeviations> getManualDeviations() {
		return manualDeviations;
	}

	public void setManualDeviations(List<FinanceDeviations> manualDeviations) {
		this.manualDeviations = manualDeviations;
	}

	public List<FinanceDeviations> getApprovedManualDeviations() {
		return approvedManualDeviations;
	}

	public void setApprovedManualDeviations(List<FinanceDeviations> approvedManualDeviations) {
		this.approvedManualDeviations = approvedManualDeviations;
	}

	public List<FinCollaterals> getFinanceCollaterals() {
		return financeCollaterals;
	}

	public void setFinanceCollaterals(List<FinCollaterals> financeCollaterals) {
		this.financeCollaterals = financeCollaterals;
	}

	public List<CollateralAssignment> getCollateralAssignmentList() {
		return collateralAssignmentList;
	}

	public void setCollateralAssignmentList(List<CollateralAssignment> collateralAssignmentList) {
		this.collateralAssignmentList = collateralAssignmentList;
	}

	public List<FinAssetTypes> getFinAssetTypesList() {
		return finAssetTypesList;
	}

	public void setFinAssetTypesList(List<FinAssetTypes> finAssetTypesList) {
		this.finAssetTypesList = finAssetTypesList;
	}

	public List<ExtendedFieldRender> getExtendedFieldRenderList() {
		return extendedFieldRenderList;
	}

	public void setExtendedFieldRenderList(List<ExtendedFieldRender> extendedFieldRenderList) {
		this.extendedFieldRenderList = extendedFieldRenderList;
	}

	public List<FinBlacklistCustomer> getFinBlacklistCustomer() {
		return finBlacklistCustomer;
	}

	public void setFinBlacklistCustomer(List<FinBlacklistCustomer> finBlacklistCustomer) {
		this.finBlacklistCustomer = finBlacklistCustomer;
	}

	public List<FinanceDedup> getFinDedupDetails() {
		return finDedupDetails;
	}

	public void setFinDedupDetails(List<FinanceDedup> finDedupDetails) {
		this.finDedupDetails = finDedupDetails;
	}

	public List<CustomerDedup> getCustomerDedupList() {
		return customerDedupList;
	}

	public void setCustomerDedupList(List<CustomerDedup> customerDedupList) {
		this.customerDedupList = customerDedupList;
	}

	public List<FinAdvancePayments> getAdvancePaymentsList() {
		return advancePaymentsList;
	}

	public void setAdvancePaymentsList(List<FinAdvancePayments> advancePaymentsList) {
		this.advancePaymentsList = advancePaymentsList;
	}

	public List<FeePaymentDetail> getFeePaymentDetailList() {
		return feePaymentDetailList;
	}

	public void setFeePaymentDetailList(List<FeePaymentDetail> feePaymentDetailList) {
		this.feePaymentDetailList = feePaymentDetailList;
	}

	public List<FinCovenantType> getCovenantTypeList() {
		return covenantTypeList;
	}

	public void setCovenantTypeList(List<FinCovenantType> covenantTypeList) {
		this.covenantTypeList = covenantTypeList;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}

	public StorageDetail getStorageDetail() {
		return storageDetail;
	}

	public void setStorageDetail(StorageDetail storageDetail) {
		this.storageDetail = storageDetail;
	}

	public WIFCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(WIFCustomer customer) {
		this.customer = customer;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	public FinanceMaintenance getFinanceMaintenance() {
		return financeMaintenance;
	}

	public void setFinanceMaintenance(FinanceMaintenance financeMaintenance) {
		this.financeMaintenance = financeMaintenance;
	}

	public String getAccountingEventCode() {
		return accountingEventCode;
	}

	public void setAccountingEventCode(String accountingEventCode) {
		this.accountingEventCode = accountingEventCode;
	}

	public String getModuleDefiner() {
		return moduleDefiner;
	}

	public void setModuleDefiner(String moduleDefiner) {
		this.moduleDefiner = moduleDefiner;
	}

	public boolean isActionSave() {
		return actionSave;
	}

	public void setActionSave(boolean actionSave) {
		this.actionSave = actionSave;
	}

	public boolean isDataFetchComplete() {
		return dataFetchComplete;
	}

	public void setDataFetchComplete(boolean dataFetchComplete) {
		this.dataFetchComplete = dataFetchComplete;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public BigDecimal getCustPOS() {
		return custPOS;
	}

	public void setCustPOS(BigDecimal custPOS) {
		this.custPOS = custPOS;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public int getMinAge() {
		return minAge;
	}

	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public boolean isDirectFinalApprove() {
		return directFinalApprove;
	}

	public void setDirectFinalApprove(boolean directFinalApprove) {
		this.directFinalApprove = directFinalApprove;
	}

	public boolean isRepledgeProcess() {
		return repledgeProcess;
	}

	public void setRepledgeProcess(boolean repledgeProcess) {
		this.repledgeProcess = repledgeProcess;
	}

	public BigDecimal getRepledgeAmount() {
		return repledgeAmount;
	}

	public void setRepledgeAmount(BigDecimal repledgeAmount) {
		this.repledgeAmount = repledgeAmount;
	}

	public long getUploadReceiptId() {
		return uploadReceiptId;
	}

	public void setUploadReceiptId(long uploadReceiptId) {
		this.uploadReceiptId = uploadReceiptId;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public Map<String, Object> getLovDescExtendedFieldValues() {
		return lovDescExtendedFieldValues;
	}

	public void setLovDescExtendedFieldValues(Map<String, Object> lovDescExtendedFieldValues) {
		this.lovDescExtendedFieldValues = lovDescExtendedFieldValues;
	}

	public FinWriteoffPayment getFinwriteoffPayment() {
		return finwriteoffPayment;
	}

	public void setFinwriteoffPayment(FinWriteoffPayment finwriteoffPayment) {
		this.finwriteoffPayment = finwriteoffPayment;
	}

	public FinRepayHeader getFinRepayHeader() {
		return finRepayHeader;
	}

	public void setFinRepayHeader(FinRepayHeader finRepayHeader) {
		this.finRepayHeader = finRepayHeader;
	}

	public Mandate getMandate() {
		return mandate;
	}

	public void setMandate(Mandate mandate) {
		this.mandate = mandate;
	}

	public FinanceTaxDetail getFinanceTaxDetail() {
		return financeTaxDetail;
	}

	public void setFinanceTaxDetail(FinanceTaxDetail financeTaxDetail) {
		this.financeTaxDetail = financeTaxDetail;
	}

	public List<FinFlagsDetail> getFinFlagsDetails() {
		return finFlagsDetails;
	}

	public void setFinFlagsDetails(List<FinFlagsDetail> finFlagsDetails) {
		this.finFlagsDetails = finFlagsDetails;
	}

	public List<FinFeeDetail> getFinFeeDetails() {
		return finFeeDetails;
	}

	public void setFinFeeDetails(List<FinFeeDetail> finFeeDetails) {
		this.finFeeDetails = finFeeDetails;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public Boolean isStp() {
		return stp;
	}

	public void setStp(Boolean stp) {
		this.stp = stp;
	}

	public String getProcessStage() {
		return processStage;
	}

	public void setProcessStage(String processStage) {
		this.processStage = processStage;
	}

	public List<CollateralSetup> getCollaterals() {
		return collaterals;
	}

	public void setCollaterals(List<CollateralSetup> collaterals) {
		this.collaterals = collaterals;
	}

	public List<ForeClosure> getForeClosureDetails() {
		return foreClosureDetails;
	}

	public void setForeClosureDetails(List<ForeClosure> foreClosureDetails) {
		this.foreClosureDetails = foreClosureDetails;
	}

	public List<ExtendedField> getExtendedDetails() {
		return extendedDetails;
	}

	public void setExtendedDetails(List<ExtendedField> extendedDetails) {
		this.extendedDetails = extendedDetails;
	}

	public ChequeHeader getChequeHeader() {
		return chequeHeader;
	}

	public void setChequeHeader(ChequeHeader chequeHeader) {
		this.chequeHeader = chequeHeader;
	}

	public ReasonHeader getReasonHeader() {
		return reasonHeader;
	}

	public void setReasonHeader(ReasonHeader reasonHeader) {
		this.reasonHeader = reasonHeader;
	}

	public List<LegalDetail> getLegalDetailsList() {
		return legalDetailsList;
	}

	public void setLegalDetailsList(List<LegalDetail> legalDetailsList) {
		this.legalDetailsList = legalDetailsList;
	}

	public Verification getFiVerification() {
		return fiVerification;
	}

	public void setFiVerification(Verification fiVerification) {
		this.fiVerification = fiVerification;
	}

	public Verification getTvVerification() {
		return tvVerification;
	}

	public void setTvVerification(Verification tvVerification) {
		this.tvVerification = tvVerification;
	}

	public Verification getLvVerification() {
		return lvVerification;
	}

	public void setLvVerification(Verification lvVerification) {
		this.lvVerification = lvVerification;
	}

	public Verification getRcuVerification() {
		return rcuVerification;
	}

	public void setRcuVerification(Verification rcuVerification) {
		this.rcuVerification = rcuVerification;
	}

	public PSLDetail getPslDetail() {
		return pslDetail;
	}

	public void setPslDetail(PSLDetail pslDetail) {
		this.pslDetail = pslDetail;
	}

	public boolean isFiApprovalTab() {
		return fiApprovalTab;
	}

	public void setFiApprovalTab(boolean fiApprovalTab) {
		this.fiApprovalTab = fiApprovalTab;
	}

	public boolean isFiInitTab() {
		return fiInitTab;
	}

	public void setFiInitTab(boolean fiInitTab) {
		this.fiInitTab = fiInitTab;
	}

	public boolean isTvApprovalTab() {
		return tvApprovalTab;
	}

	public void setTvApprovalTab(boolean tvApprovalTab) {
		this.tvApprovalTab = tvApprovalTab;
	}

	public boolean isTvInitTab() {
		return tvInitTab;
	}

	public void setTvInitTab(boolean tvInitTab) {
		this.tvInitTab = tvInitTab;
	}

	public boolean isLvApprovalTab() {
		return lvApprovalTab;
	}

	public void setLvApprovalTab(boolean lvApprovalTab) {
		this.lvApprovalTab = lvApprovalTab;
	}

	public boolean isLvInitTab() {
		return lvInitTab;
	}

	public void setLvInitTab(boolean lvInitTab) {
		this.lvInitTab = lvInitTab;
	}

	public boolean isRcuApprovalTab() {
		return rcuApprovalTab;
	}

	public void setRcuApprovalTab(boolean rcuApprovalTab) {
		this.rcuApprovalTab = rcuApprovalTab;
	}

	public boolean isRcuInitTab() {
		return rcuInitTab;
	}

	public void setRcuInitTab(boolean rcuInitTab) {
		this.rcuInitTab = rcuInitTab;
	}

	public Map<String, String> getShowTabDetailMap() {
		return showTabDetailMap;
	}

	public void setShowTabDetailMap(Map<String, String> showTabDetailMap) {
		this.showTabDetailMap = showTabDetailMap;
	}

	public Long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
	}

	public boolean isSamplingInitiator() {
		return samplingInitiator;
	}

	public void setSamplingInitiator(boolean samplingInitiator) {
		this.samplingInitiator = samplingInitiator;
	}

	public boolean isSamplingApprover() {
		return samplingApprover;
	}

	public void setSamplingApprover(boolean samplingApprover) {
		this.samplingApprover = samplingApprover;
	}

	public Sampling getSampling() {
		return sampling;
	}

	public void setSampling(Sampling sampling) {
		this.sampling = sampling;
	}

	public boolean isLegalInitiator() {
		return legalInitiator;
	}

	public void setLegalInitiator(boolean legalInitiator) {
		this.legalInitiator = legalInitiator;
	}

	public Map<String, String> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, String> dataMap) {
		this.dataMap = dataMap;
	}

	public boolean isSufficientScore() {
		return sufficientScore;
	}

	public void setSufficientScore(boolean sufficientScore) {
		this.sufficientScore = sufficientScore;
	}

	public boolean isSecondaryDedup() {
		return secondaryDedup;
	}

	public void setSecondaryDedup(boolean secondaryDedup) {
		this.secondaryDedup = secondaryDedup;
	}

	public FinNomineeDetail getFinNomineeDetail() {
		return finNomineeDetail;
	}

	public void setFinNomineeDetail(FinNomineeDetail finNomineeDetail) {
		this.finNomineeDetail = finNomineeDetail;
	}

	public FinBeneficiary getFinBeneficiary() {
		return finBeneficiary;
	}

	public void setFinBeneficiary(FinBeneficiary finBeneficiary) {
		this.finBeneficiary = finBeneficiary;
	}

	// Additional supporting methods
	public void setScoringMetrics(Long id, List<ScoringMetrics> scoringMetrics) {
		if (this.scoringMetrics == null) {
			this.scoringMetrics = new HashMap<>();
		} else {
			if (this.scoringMetrics.containsKey(id)) {
				this.scoringMetrics.remove(id);
			}
		}
		this.scoringMetrics.put(id, scoringMetrics);
	}

	public void setScoringSlabs(Long id, List<ScoringSlab> scoringSlabs) {
		if (this.scoringSlabs == null) {
			this.scoringSlabs = new HashMap<>();
		} else {
			if (this.scoringSlabs.containsKey(id)) {
				this.scoringSlabs.remove(id);
			}
		}
		this.scoringSlabs.put(id, scoringSlabs);
	}

	public void setLovDescExtendedFieldValues(String string, Object object) {
		if (lovDescExtendedFieldValues.containsKey(string)) {
			lovDescExtendedFieldValues.remove(string);
		}
		this.lovDescExtendedFieldValues.put(string, object);
	}

	public boolean isUpFrentFee() {
		return upFrentFee;
	}

	public void setUpFrentFee(boolean upFrentFee) {
		this.upFrentFee = upFrentFee;
	}

	public Covenant getCovenant() {
		return covenant;
	}

	public void setCovenant(Covenant covenant) {
		this.covenant = covenant;
	}

	public List<Covenant> getCovenants() {
		return covenants;
	}

	public void setCovenants(List<Covenant> covenants) {
		this.covenants = covenants;
	}

	public List<FinOption> getFinOptions() {
		return finOptions;
	}

	public void setFinOptions(List<FinOption> finOptions) {
		this.finOptions = finOptions;
	}

	public FinOption getFinOption() {
		return finOption;
	}

	public void setFinOption(FinOption finOption) {
		this.finOption = finOption;
	}

	public List<InterfaceServiceDetails> getInterfaceDetailList() {
		return interfaceDetailList;
	}

	public void setInterfaceDetailList(List<InterfaceServiceDetails> interfaceDetailList) {
		this.interfaceDetailList = interfaceDetailList;
	}

	public boolean isValidateUpfrontFees() {
		return validateUpfrontFees;
	}

	public void setValidateUpfrontFees(boolean validateUpfrontFees) {
		this.validateUpfrontFees = validateUpfrontFees;
	}

	public CollateralRevaluation getCollateralRevaluation() {
		return collateralRevaluation;
	}

	public void setCollateralRevaluation(CollateralRevaluation collateralRevaluation) {
		this.collateralRevaluation = collateralRevaluation;
	}

	public LMSServiceLog getLmsServiceLog() {
		return lmsServiceLog;
	}

	public void setLmsServiceLog(LMSServiceLog lmsServiceLog) {
		this.lmsServiceLog = lmsServiceLog;
	}

	public boolean isPdDetailTab() {
		return pdDetailTab;
	}

	public void setPdDetailTab(boolean pdDetailTab) {
		this.pdDetailTab = pdDetailTab;
	}

	public Verification getPdVerification() {
		return pdVerification;
	}

	public void setPdVerification(Verification pdVerification) {
		this.pdVerification = pdVerification;
	}

	public boolean isPdApprovalTab() {
		return pdApprovalTab;
	}

	public void setPdApprovalTab(boolean pdApprovalTab) {
		this.pdApprovalTab = pdApprovalTab;
	}

	public boolean isPdInitTab() {
		return pdInitTab;
	}

	public void setPdInitTab(boolean pdInitTab) {
		this.pdInitTab = pdInitTab;
	}

	public CreditReviewData getCreditReviewData() {
		return creditReviewData;
	}

	public void setCreditReviewData(CreditReviewData creditReviewData) {
		this.creditReviewData = creditReviewData;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public AdvancePaymentDetail getAdvancePaymentDetail() {
		return advancePaymentDetail;
	}

	public void setAdvancePaymentDetail(AdvancePaymentDetail advancePaymentDetail) {
		this.advancePaymentDetail = advancePaymentDetail;
	}

	public Long getUsrID() {
		return usrID;
	}

	public void setUsrID(Long usrID) {
		this.usrID = usrID;
	}

	public String getUsrLogin() {
		return usrLogin;
	}

	public void setUsrLogin(String usrLogin) {
		this.usrLogin = usrLogin;
	}

	public Map<String, Object> getCreditRevDataMap() {
		return creditRevDataMap;
	}

	public void setCreditRevDataMap(Map<String, Object> creditRevDataMap) {
		this.creditRevDataMap = creditRevDataMap;
	}

	public boolean isFinancialSummaryTab() {
		return financialSummaryTab;
	}

	public void setFinancialSummaryTab(boolean financialSummaryTab) {
		this.financialSummaryTab = financialSummaryTab;
	}

	public PaymentTransaction getPaymentTransaction() {
		return paymentTransaction;
	}

	public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
		this.paymentTransaction = paymentTransaction;
	}

	public List<RisksAndMitigants> getRisksAndMitigantsList() {
		return risksAndMitigantsList;
	}

	public void setRisksAndMitigantsList(List<RisksAndMitigants> risksAndMitigantsList) {
		this.risksAndMitigantsList = risksAndMitigantsList;
	}

	public List<SanctionConditions> getSanctionDetailsList() {
		return sanctionDetailsList;
	}

	public void setSanctionDetailsList(List<SanctionConditions> sanctionDetailsList) {
		this.sanctionDetailsList = sanctionDetailsList;
	}

	public List<DealRecommendationMerits> getDealRecommendationMeritsDetailsList() {
		return dealRecommendationMeritsDetailsList;
	}

	public void setDealRecommendationMeritsDetailsList(
			List<DealRecommendationMerits> dealRecommendationMeritsDetailsList) {
		this.dealRecommendationMeritsDetailsList = dealRecommendationMeritsDetailsList;
	}

	public List<DueDiligenceDetails> getDueDiligenceDetailsList() {
		return dueDiligenceDetailsList;
	}

	public void setDueDiligenceDetailsList(List<DueDiligenceDetails> dueDiligenceDetailsList) {
		this.dueDiligenceDetailsList = dueDiligenceDetailsList;
	}

	public DueDiligenceDetails getDueDiligenceDetails() {
		return dueDiligenceDetails;
	}

	public void setDueDiligenceDetails(DueDiligenceDetails dueDiligenceDetails) {
		this.dueDiligenceDetails = dueDiligenceDetails;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public List<RecommendationNotes> getRecommendationNoteList() {
		return recommendationNoteList;
	}

	public void setRecommendationNoteList(List<RecommendationNotes> recommendationNoteList) {
		this.recommendationNoteList = recommendationNoteList;
	}

	public SynopsisDetails getSynopsisDetails() {
		return synopsisDetails;
	}

	public void setSynopsisDetails(SynopsisDetails synopsisDetails) {
		this.synopsisDetails = synopsisDetails;
	}

	public boolean isSpreadSheetloaded() {
		return spreadSheetloaded;
	}

	public void setSpreadSheetloaded(boolean spreadSheetloaded) {
		this.spreadSheetloaded = spreadSheetloaded;
	}

	public SpreadSheet getSpreadSheet() {
		return spreadSheet;
	}

	public void setSpreadSheet(SpreadSheet spreadSheet) {
		this.spreadSheet = spreadSheet;
	}

	/**
	 * @return the tempCollateralAssignmentList
	 */
	public List<CollateralAssignment> getTempCollateralAssignmentList() {
		return tempCollateralAssignmentList;
	}

	/**
	 * @param tempCollateralAssignmentList the tempCollateralAssignmentList to set
	 */
	public void setTempCollateralAssignmentList(List<CollateralAssignment> tempCollateralAssignmentList) {
		this.tempCollateralAssignmentList = tempCollateralAssignmentList;
	}

	public Boolean isDisbStp() {
		return disbStp;
	}

	public void setDisbStp(Boolean disbStp) {
		this.disbStp = disbStp;
	}

	public FinOCRHeader getFinOCRHeader() {
		return finOCRHeader;
	}

	public void setFinOCRHeader(FinOCRHeader finOCRHeader) {
		this.finOCRHeader = finOCRHeader;
	}

	public Verification getLegalVetting() {
		return legalVetting;
	}

	public void setLegalVetting(Verification legalVetting) {
		this.legalVetting = legalVetting;
	}

	public boolean isVettingApprovalTab() {
		return vettingApprovalTab;
	}

	public void setVettingApprovalTab(boolean vettingApprovalTab) {
		this.vettingApprovalTab = vettingApprovalTab;
	}

	public boolean isVettingInitTab() {
		return vettingInitTab;
	}

	public void setVettingInitTab(boolean vettingInitTab) {
		this.vettingInitTab = vettingInitTab;
	}

	public PMAY getPmay() {
		return pmay;
	}

	public void setPmay(PMAY pmay) {
		this.pmay = pmay;
	}

	public PricingDetail getPricingDetail() {
		return pricingDetail;
	}

	public void setPricingDetail(PricingDetail pricingDetail) {
		this.pricingDetail = pricingDetail;
	}

	public List<FinFeeConfig> getFinFeeConfigList() {
		return finFeeConfigList;
	}

	public void setFinFeeConfigList(List<FinFeeConfig> finFeeConfigList) {
		this.finFeeConfigList = finFeeConfigList;
	}

	public List<CollateralSetup> getDmsCollateralDocuments() {
		return dmsCollateralDocuments;
	}

	public void setDmsCollateralDocuments(List<CollateralSetup> dmsCollateralDocuments) {
		this.dmsCollateralDocuments = dmsCollateralDocuments;
	}

	public BigDecimal getReceiptProcessingAmt() {
		return receiptProcessingAmt;
	}

	public void setReceiptProcessingAmt(BigDecimal receiptProcessingAmt) {
		this.receiptProcessingAmt = receiptProcessingAmt;
	}

	public Boolean isOrigination() {
		return isOrigination;
	}

	public void setOrigination(Boolean isOrigination) {
		this.isOrigination = isOrigination;
	}

	public List<LinkedFinances> getLinkedFinancesList() {
		return linkedFinancesList;
	}

	public void setLinkedFinancesList(List<LinkedFinances> linkedFinancesList) {
		this.linkedFinancesList = linkedFinancesList;
	}

	public Map<String, BigDecimal> getGstPercentages() {
		return gstPercentages;
	}

	public void setGstPercentages(Map<String, BigDecimal> gstPercentages) {
		this.gstPercentages = gstPercentages;
	}

	public ExtendedFieldExtension getExtendedFieldExtension() {
		return extendedFieldExtension;
	}

	public void setExtendedFieldExtension(ExtendedFieldExtension extendedFieldExtension) {
		this.extendedFieldExtension = extendedFieldExtension;
	}

	public List<TanAssignment> getTanAssignments() {
		return tanAssignments;
	}

	public void setTanAssignments(List<TanAssignment> tanAssignments) {
		this.tanAssignments = tanAssignments;
	}

	public ISRADetail getIsraDetail() {
		return israDetail;
	}

	public void setIsraDetail(ISRADetail israDetail) {
		this.israDetail = israDetail;
	}

	public long getReceiptTransactionId() {
		return receiptTransactionId;
	}

	public void setReceiptTransactionId(long receiptTransactionId) {
		this.receiptTransactionId = receiptTransactionId;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Mandate getSecurityMandate() {
		return securityMandate;
	}

	public void setSecurityMandate(Mandate securityMandate) {
		this.securityMandate = securityMandate;
	}

	public LienHeader getLienHeader() {
		return lienHeader;
	}

	public void setLienHeader(LienHeader lienHeader) {
		this.lienHeader = lienHeader;
	}

}
