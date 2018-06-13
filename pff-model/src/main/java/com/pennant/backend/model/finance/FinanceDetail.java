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
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinanceDetail.java	                                                * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-03-2011    														*
 *                                                                  						*
 * Modified Date    :  22-03-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-03-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.pff.verification.model.Verification;

@XmlType(propOrder = { "finReference", "stp", "processStage", "finScheduleData", "foreClosureDetails",
		"customerDetails", "advancePaymentsList", "mandate", "jountAccountDetailList", "gurantorsDetailList",
		"documentDetailsList", "covenantTypeList", "collateralAssignmentList", "finFlagsDetails", "finFeeDetails",
		"returnDataSetList", "collateralSetup", "financeTaxDetails", "returnStatus" })
@XmlRootElement(name = "finance")
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceDetail implements java.io.Serializable {
	private static final long serialVersionUID = 3947699402597772444L;

	@XmlElement
	private String finReference;
	@XmlElement(name="financeSchedule")
	private FinScheduleData finScheduleData = new FinScheduleData();

	private boolean newRecord = false;
	private LoggedInUser userDetails;
	private FinanceDetail befImage;
	private boolean lovDescIsQDE;
	private boolean isExtSource = false;

	@XmlTransient
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>(1);
	private FinContributorHeader finContributorHeader;
	private CustomerEligibilityCheck customerEligibilityCheck;

	private IndicativeTermDetail indicativeTermDetail;
	
	private EtihadCreditBureauDetail etihadCreditBureauDetail;
	private BundledProductsDetail bundledProductsDetail;
	private TATDetail tatDetail;
	private FinAssetEvaluation finAssetEvaluation;

	private List<FinanceCheckListReference> financeCheckList = new ArrayList<FinanceCheckListReference>(1);
	private List<FinanceReferenceDetail> checkList = new ArrayList<FinanceReferenceDetail>(1);
	private Map<Long, Long> lovDescSelAnsCountMap = new HashMap<Long, Long>(1);
	private List<FinanceReferenceDetail> finRefDetailsList;
	
	private List<FinanceReferenceDetail> aggrementList = new ArrayList<FinanceReferenceDetail>(1);
	
	private List<FinanceReferenceDetail> eligibilityRuleList = new ArrayList<FinanceReferenceDetail>(1);
	private List<FinanceEligibilityDetail> finElgRuleList = new ArrayList<FinanceEligibilityDetail>(1);
	private List<FinanceEligibilityDetail> elgRuleList = new ArrayList<FinanceEligibilityDetail>(1);
	
	//Scoring Details Purpose
	private List<FinanceReferenceDetail> scoringGroupList = new ArrayList<FinanceReferenceDetail>(1);
	private List<ScoringMetrics> finScoringMetricList = new ArrayList<ScoringMetrics>(1);
	private List<ScoringMetrics> nonFinScoringMetricList = new ArrayList<ScoringMetrics>(1);
	private HashMap<Long, List<ScoringMetrics>> scoringMetrics = new HashMap<Long, List<ScoringMetrics>>(1);
	private HashMap<Long, List<ScoringSlab>> scoringSlabs = new HashMap<Long, List<ScoringSlab>>(1);
	private List<FinanceScoreHeader> finScoreHeaderList = new ArrayList<FinanceScoreHeader>(1);
	private HashMap<Long, List<FinanceScoreDetail>> scoreDetailListMap = new HashMap<Long, List<FinanceScoreDetail>>(1);
	
	private List<Rule> feeCharges = new ArrayList<Rule>(1);	
	private List<FinTypeFees> finTypeFeesList = new ArrayList<FinTypeFees>();
	
	@XmlElementWrapper(name= "transactions")
	@XmlElement(name="transaction")
	private List<ReturnDataSet> returnDataSetList = new ArrayList<ReturnDataSet>(1);
	
	private List<TransactionEntry> cmtFinanceEntries = new ArrayList<TransactionEntry>(1);
	private List<ReturnDataSet> cmtDataSetList = new ArrayList<ReturnDataSet>(1);
	
	private List<TransactionEntry> stageTransactionEntries = new ArrayList<TransactionEntry>(1);
	private List<ReturnDataSet> stageAccountingList = new ArrayList<ReturnDataSet>(1);
	
	@XmlElementWrapper(name="documents")
	@XmlElement(name="document")
	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>(1);
	
	@XmlElementWrapper(name="guarantors")
	@XmlElement(name="guarantor")
	private List<GuarantorDetail> gurantorsDetailList = new ArrayList<GuarantorDetail>(1);
	
	@XmlElementWrapper(name="coApplicants")
	@XmlElement(name ="coApplicant")
	private List<JointAccountDetail> jountAccountDetailList = new ArrayList<JointAccountDetail>(1);
	private List<ContractorAssetDetail> contractorAssetDetails = new ArrayList<ContractorAssetDetail>(1);
	private List<FinanceDeviations> financeDeviations=new ArrayList<FinanceDeviations>();
	private List<FinanceDeviations> approvedFinanceDeviations=new ArrayList<FinanceDeviations>();
	private List<FinanceDeviations> manualDeviations=new ArrayList<FinanceDeviations>();
	private List<FinanceDeviations> approvedManualDeviations=new ArrayList<FinanceDeviations>();
	
	
	private List<FinCollaterals> financeCollaterals = new ArrayList<FinCollaterals>(1);
	@XmlElementWrapper(name="collaterals")
	@XmlElement(name="collateral")
	private List<CollateralAssignment> collateralAssignmentList = new ArrayList<CollateralAssignment>(1);
	private List<FinAssetTypes> finAssetTypesList = new ArrayList<FinAssetTypes>(1);
	private List<ExtendedFieldRender> extendedFieldRenderList = new ArrayList<ExtendedFieldRender>(1);

	// Dedupe Check List Details 
	private List<FinBlacklistCustomer> finBlacklistCustomer = null;
	private List<FinanceDedup> finDedupDetails = null; 
	private List<PoliceCase> dedupPoliceCaseDetails = null;
	private List<CustomerDedup> customerDedupList = null;
	
	// Advance Payments
	@XmlElement(name="disbursement")
	private List<FinAdvancePayments> advancePaymentsList = null;
	private List<FeePaymentDetail> feePaymentDetailList = null;

	// Covenant Types
	@XmlElementWrapper(name="covenants")
	@XmlElement(name="covenant")
	private List<FinCovenantType> covenantTypeList = null;
	
	// Rollover Finance Details
	private RolledoverFinanceHeader rolledoverFinanceHeader = null;
	
	// Customer Details
	@XmlElement(name="customer")
	private CustomerDetails customerDetails;
	private WIFCustomer customer;
	
	private String accountingEventCode;
	private String moduleDefiner = "";
	private boolean actionSave = false;
	private boolean dataFetchComplete = false;
	private String userAction;
	private BigDecimal score = BigDecimal.ZERO;
	
	//Additional Fields
	//**********************************************************************
	private ExtendedFieldHeader						extendedFieldHeader;
	private ExtendedFieldRender						extendedFieldRender;
	
	private HashMap<String, Object> lovDescExtendedFieldValues = new HashMap<String, Object>(1);
	//WriteoffPayment
	private FinWriteoffPayment finwriteoffPayment;
	
	private AgreementFieldDetails agreementFieldDetails; 
	private FinRepayHeader finRepayHeader; 
	
	@XmlElement(name="mandateDetail")
	private Mandate mandate;
	@XmlElement(name="taxDetail")
	private FinanceTaxDetail financeTaxDetails;
	
	@XmlElementWrapper(name="financeFlags")
	@XmlElement(name="financeFlag")
	private List<FinFlagsDetail> finFlagsDetails;
	@XmlElementWrapper(name="fees")
	@XmlElement(name="fee")
	List<FinFeeDetail> finFeeDetails;
	
	@XmlElement
	private WSReturnStatus returnStatus = null;
	@XmlElement
	private boolean stp=true;
	@XmlElement
	private String processStage;
	@XmlElementWrapper(name="collateralDetails")
	@XmlElement(name="collateralDetail")
	private List<CollateralSetup> collateralSetup;
	
	// API Foreclosure letter statement purpose
	@XmlElementWrapper(name="foreClosures")
	@XmlElement(name="foreClosure")
	private List<ForeClosure> foreClosureDetails;

	//API ExtendedDetails
	@XmlElementWrapper(name="extendedDetails")
	@XmlElement(name="extendedDetail")
	private List<ExtendedField> extendedDetails = null;
	
	private ChequeHeader chequeHeader = null;
	
	// Reason Details
	private ReasonHeader reasonHeader;
	
	//FI Verification module
	private Verification fiVerification;
	private Verification tvVerification;
	private Verification lvVerification;
	private Verification rcuVerification;
	private boolean fiApprovalTab =false;
	private boolean fiInitTab = false;
	private boolean tvApprovalTab =false;
	private boolean tvInitTab = false;
	private boolean lvApprovalTab = false;
	private boolean lvInitTab = false;
	private boolean rcuApprovalTab = false;
	private boolean rcuInitTab = false;
	private Map<String, String> showTabDetailMap = new HashMap<>();
	
	public FinanceDetail() {
		
	}
	
	public HashMap<String, Object> getLovDescExtendedFieldValues() {
		return lovDescExtendedFieldValues;
	}

	public void setLovDescExtendedFieldValues(String string, Object object) {
		
		if (lovDescExtendedFieldValues.containsKey(string)) {
			lovDescExtendedFieldValues.remove(string);
		}
		this.lovDescExtendedFieldValues.put(string, object);
	}
	
	
	private boolean sufficientScore;
	

	//***********************************************************************

	// Getter and Setter methods

	public boolean isNewRecord() {
		return newRecord;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public FinanceDetail getBefImage() {
		return this.befImage;
	}
	public void setBefImage(FinanceDetail befImage) {
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

	

	public boolean isNew() {
		return isNewRecord();
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

	public CustomerEligibilityCheck getCustomerEligibilityCheck() {
		return customerEligibilityCheck;
	}
	public void setCustomerEligibilityCheck(CustomerEligibilityCheck customerEligibilityCheck) {
		this.customerEligibilityCheck = customerEligibilityCheck;
	}

	public void setFinanceCheckList(List<FinanceCheckListReference> financeCheckList) {
		this.financeCheckList = financeCheckList;
	}
	public List<FinanceCheckListReference> getFinanceCheckList() {
		return financeCheckList;
	}

	public void setFinRefDetailsList(List<FinanceReferenceDetail> finRefDetailsList) {
		this.finRefDetailsList = finRefDetailsList;
	}
	public List<FinanceReferenceDetail> getFinRefDetailsList() {
		return finRefDetailsList;
	}

	public void setLovDescSelAnsCountMap(Map<Long, Long> lovDescSelAnsCountMap) {
		this.lovDescSelAnsCountMap = lovDescSelAnsCountMap;
	}
	public Map<Long, Long> getLovDescSelAnsCountMap() {
		return lovDescSelAnsCountMap;
	}

	public List<FinanceReferenceDetail> getCheckList() {
		return checkList;
	}
	public void setCheckList(List<FinanceReferenceDetail> checkList) {
		this.checkList = checkList;
	}

	public List<FinanceReferenceDetail> getAggrementList() {
		return aggrementList;
	}
	public void setAggrementList(List<FinanceReferenceDetail> aggrementList) {
		this.aggrementList = aggrementList;
	}

	public void setFinContributorHeader(FinContributorHeader finContributorHeader) {
		this.finContributorHeader = finContributorHeader;
	}
	public FinContributorHeader getFinContributorHeader() {
		return finContributorHeader;
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

	public HashMap<Long, List<ScoringMetrics>> getScoringMetrics() {
		return scoringMetrics;
	}

	public void setScoringMetrics(Long id, List<ScoringMetrics> scoringMetrics) {
		if (this.scoringMetrics == null) {
			this.scoringMetrics = new HashMap<Long, List<ScoringMetrics>>();
		} else {
			if (this.scoringMetrics.containsKey(id)) {
				this.scoringMetrics.remove(id);
			}
		}
		this.scoringMetrics.put(id, scoringMetrics);
	}
	
	public void setScoringMetrics(HashMap<Long, List<ScoringMetrics>> scoringMetrics) {
		if (this.scoringMetrics == null) {
			this.scoringMetrics = new HashMap<Long, List<ScoringMetrics>>();
		} 
		this.scoringMetrics = scoringMetrics;
	}

	public HashMap<Long, List<ScoringSlab>> getScoringSlabs() {
		return scoringSlabs;
	}

	public void setScoringSlabs(Long id, List<ScoringSlab> scoringSlabs) {
		if (this.scoringSlabs == null) {
			this.scoringSlabs = new HashMap<Long, List<ScoringSlab>>();
		} else {
			if (this.scoringSlabs.containsKey(id)) {
				this.scoringSlabs.remove(id);
			}
		}
		this.scoringSlabs.put(id, scoringSlabs);
	}
	
	public void setScoringSlabs(HashMap<Long, List<ScoringSlab>> scoringSlabs) {
		if (this.scoringSlabs == null) {
			this.scoringSlabs = new HashMap<Long, List<ScoringSlab>>();
		} 
		this.scoringSlabs = scoringSlabs;
	}

	public void setActionSave(boolean actionSave) {
		this.actionSave = actionSave;
	}
	public boolean isActionSave() {
		return actionSave;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}
	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public void setStageTransactionEntries(List<TransactionEntry> stageTransactionEntries) {
	    this.stageTransactionEntries = stageTransactionEntries;
    }
	public List<TransactionEntry> getStageTransactionEntries() {
	    return stageTransactionEntries;
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

	public void setStageAccountingList(List<ReturnDataSet> stageAccountingList) {
	    this.stageAccountingList = stageAccountingList;
    }
	public List<ReturnDataSet> getStageAccountingList() {
	    return stageAccountingList;
    }

	public String getAccountingEventCode() {
		return accountingEventCode;
	}
	public void setAccountingEventCode(String accountingEventCode) {
		this.accountingEventCode = accountingEventCode;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}
	public String getUserAction() {
		return userAction;
	}

	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
	    this.documentDetailsList = documentDetailsList;
    }
	public List<DocumentDetails> getDocumentDetailsList() {
	    return documentDetailsList;
    }

	public void setFinScoringMetricList(List<ScoringMetrics> finScoringMetricList) {
	    this.finScoringMetricList = finScoringMetricList;
    }
	public List<ScoringMetrics> getFinScoringMetricList() {
	    return finScoringMetricList;
    }

	public void setNonFinScoringMetricList(List<ScoringMetrics> nonFinScoringMetricList) {
	    this.nonFinScoringMetricList = nonFinScoringMetricList;
    }
	public List<ScoringMetrics> getNonFinScoringMetricList() {
	    return nonFinScoringMetricList;
    }

	public void setScoreDetailListMap(HashMap<Long, List<FinanceScoreDetail>> scoreDetailListMap) {
	    this.scoreDetailListMap = scoreDetailListMap;
    }
	public HashMap<Long, List<FinanceScoreDetail>> getScoreDetailListMap() {
	    return scoreDetailListMap;
    }

	public void setFinScoreHeaderList(List<FinanceScoreHeader> finScoreHeaderList) {
	    this.finScoreHeaderList = finScoreHeaderList;
    }
	public List<FinanceScoreHeader> getFinScoreHeaderList() {
	    return finScoreHeaderList;
    }

	public List<GuarantorDetail> getGurantorsDetailList() {
    	return gurantorsDetailList;
    }
	public void setGurantorsDetailList(List<GuarantorDetail> gurantorsDetailList) {
    	this.gurantorsDetailList = gurantorsDetailList;
    }

	public List<JointAccountDetail> getJountAccountDetailList() {
    	return jountAccountDetailList;
    }
	public void setJountAccountDetailList(List<JointAccountDetail> jountAccountDetailList) {
    	this.jountAccountDetailList = jountAccountDetailList;
    }


	public List<ContractorAssetDetail> getContractorAssetDetails() {
    	return contractorAssetDetails;
    }
	public void setContractorAssetDetails(List<ContractorAssetDetail> contractorAssetDetails) {
    	this.contractorAssetDetails = contractorAssetDetails;
    }

	public boolean isSufficientScore() {
    	return sufficientScore;
    }
	public void setSufficientScore(boolean sufficientScore) {
    	this.sufficientScore = sufficientScore;
    }

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}
	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}

	public WIFCustomer getCustomer() {
    	return customer;
    }
	public void setCustomer(WIFCustomer customer) {
    	this.customer = customer;
    }

	public void setIndicativeTermDetail(IndicativeTermDetail indicativeTermDetail) {
	    this.indicativeTermDetail = indicativeTermDetail;
    }
	public IndicativeTermDetail getIndicativeTermDetail() {
	    return indicativeTermDetail;
    }

	public void setDataFetchComplete(boolean dataFetchComplete) {
	    this.dataFetchComplete = dataFetchComplete;
    }
	public boolean isDataFetchComplete() {
	    return dataFetchComplete;
    }

	public EtihadCreditBureauDetail getEtihadCreditBureauDetail() {
		return etihadCreditBureauDetail;
	}
	public void setEtihadCreditBureauDetail(EtihadCreditBureauDetail etihadCreditBureauDetail) {
		this.etihadCreditBureauDetail = etihadCreditBureauDetail;
	}

	public BundledProductsDetail getBundledProductsDetail() {
		return bundledProductsDetail;
	}
	public void setBundledProductsDetail(BundledProductsDetail bundledProductsDetail) {
		this.bundledProductsDetail = bundledProductsDetail;
	}

	public FinAssetEvaluation getFinAssetEvaluation() {
		return finAssetEvaluation;
	}
	public void setFinAssetEvaluation(FinAssetEvaluation finAssetEvaluation) {
		this.finAssetEvaluation = finAssetEvaluation;
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

	public List<PoliceCase> getDedupPoliceCaseDetails() {
	    return dedupPoliceCaseDetails;
    }
	public void setDedupPoliceCaseDetails(List<PoliceCase> dedupPoliceCaseDetails) {
	    this.dedupPoliceCaseDetails = dedupPoliceCaseDetails;
    }

	public List<CustomerDedup> getCustomerDedupList() {
	    return customerDedupList;
    }
	public void setCustomerDedupList(List<CustomerDedup> customerDedupList) {
	    this.customerDedupList = customerDedupList;
    }

	public BigDecimal getScore() {
	    return score;
    }
	public void setScore(BigDecimal score) {
	    this.score = score;
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
	public void setApprovedFinanceDeviations(List<FinanceDeviations> approvedfinanceDeviations) {
	    this.approvedFinanceDeviations = approvedfinanceDeviations;
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
	public void setCollateralAssignmentList(
			List<CollateralAssignment> collateralAssignmentList) {
		this.collateralAssignmentList = collateralAssignmentList;
	}

	public String getModuleDefiner() {
	    return moduleDefiner;
    }
	public void setModuleDefiner(String moduleDefiner) {
	    this.moduleDefiner = moduleDefiner;
    }

	public RolledoverFinanceHeader getRolledoverFinanceHeader() {
	    return rolledoverFinanceHeader;
    }
	public void setRolledoverFinanceHeader(RolledoverFinanceHeader rolledoverFinanceHeader) {
	    this.rolledoverFinanceHeader = rolledoverFinanceHeader;
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

	public TATDetail getTatDetail() {
		return tatDetail;
	}

	public void setTatDetail(TATDetail tatDetail) {
		this.tatDetail = tatDetail;
	}

	public FinWriteoffPayment getFinwriteoffPayment() {
		return finwriteoffPayment;
	}

	public void setFinwriteoffPayment(FinWriteoffPayment finwriteoffPayment) {
		this.finwriteoffPayment = finwriteoffPayment;
	}
	
	public AgreementFieldDetails getAgreementFieldDetails() {
		return agreementFieldDetails;
	}

	public void setAgreementFieldDetails(AgreementFieldDetails agreementFieldDetails) {
		this.agreementFieldDetails = agreementFieldDetails;
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

	public List<FinFlagsDetail> getFinFlagsDetails() {
		return finFlagsDetails;
	}

	public void setFinFlagsDetails(List<FinFlagsDetail> finFlagsDetails) {
		this.finFlagsDetails = finFlagsDetails;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}
	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
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
	public List<FinFeeDetail> getFinFeeDetails() {
		return finFeeDetails;
	}
	public void setFinFeeDetails(List<FinFeeDetail> finFeeDetails) {
		this.finFeeDetails = finFeeDetails;
	}

	public List<CollateralSetup> getCollateralSetup() {
		return collateralSetup;
	}

	public void setCollateralSetup(List<CollateralSetup> collateralSetup) {
		this.collateralSetup = collateralSetup;
	}

	public List<ForeClosure> getForeClosureDetails() {
		return foreClosureDetails;
	}

	public void setForeClosureDetails(List<ForeClosure> foreClosureDetails) {
		this.foreClosureDetails = foreClosureDetails;
	}

	public FinanceTaxDetail getFinanceTaxDetails() {
		return financeTaxDetails;
	}

	public void setFinanceTaxDetails(FinanceTaxDetail financeTaxDetails) {
		this.financeTaxDetails = financeTaxDetails;
	}

	public boolean isStp() {
		return stp;
	}

	public void setStp(boolean stp) {
		this.stp = stp;
	}
	public String getProcessStage() {
		return processStage;
	}

	public void setProcessStage(String processStage) {
		this.processStage = processStage;
	}

	/**
	 * @return the extendedFieldRender
	 */
	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	/**
	 * @param extendedFieldRender the extendedFieldRender to set
	 */
	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
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

	public Verification getFiVerification() {
		return fiVerification;
	}

	public void setFiVerification(Verification fiVerification) {
		this.fiVerification = fiVerification;
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

	
}
