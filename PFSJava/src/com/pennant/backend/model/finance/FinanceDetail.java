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
 * * FileName : FinanceMain.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * * Modified Date :
 * 15-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.finance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerScoringCheck;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanHeader;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.GenGoodsLoanDetail;
import com.pennant.backend.model.lmtmasters.GoodsLoanDetail;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.model.lmtmasters.SharesDetail;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;

public class FinanceDetail implements java.io.Serializable {

	private static final long serialVersionUID = 3947699402597772444L;

	private FinScheduleData finScheduleData = new FinScheduleData();

	private boolean newRecord = false;
	private LoginUserDetails userDetails;
	private long workflowId = 0;
	private FinanceDetail befImage;
	private boolean lovDescIsQDE;
	private boolean isExtSource = false;

	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private FinContributorHeader finContributorHeader;
	private FinBillingHeader finBillingHeader;
	private CustomerEligibilityCheck customerEligibilityCheck;
	private CustomerScoringCheck customerScoringCheck;

	private CarLoanDetail carLoanDetail;
	private EducationalLoan educationalLoan;
	private HomeLoanDetail homeLoanDetail;
	private MortgageLoanDetail mortgageLoanDetail;
	private List<GoodsLoanDetail> goodsLoanDetails;
	private List<GenGoodsLoanDetail> genGoodsLoanDetails;
	private CommidityLoanHeader commidityLoanHeader;
	private List<CommidityLoanDetail> commidityLoanDetails;
	private List<SharesDetail> sharesDetails;
	private IndicativeTermDetail indicativeTermDetail;
	private FinancePremiumDetail premiumDetail;

	private List<FinanceCheckListReference> financeCheckList = new ArrayList<FinanceCheckListReference>();
	private List<FinanceReferenceDetail> checkList = new ArrayList<FinanceReferenceDetail>();
	private Map<Long, Long> lovDescSelAnsCountMap = new HashMap<Long, Long>();
	private List<FinanceReferenceDetail> finRefDetailsList;
	
	private List<FinanceReferenceDetail> aggrementList = new ArrayList<FinanceReferenceDetail>();
	//private List<FinAgreementDetail> finAgrDetailList = new ArrayList<FinAgreementDetail>();
	
	private List<FinanceReferenceDetail> eligibilityRuleList = new ArrayList<FinanceReferenceDetail>();
	private List<FinanceEligibilityDetail> finElgRuleList = new ArrayList<FinanceEligibilityDetail>();
	private List<FinanceEligibilityDetail> elgRuleList = new ArrayList<FinanceEligibilityDetail>();
	
	//Scoring Details Purpose
	private List<FinanceReferenceDetail> scoringGroupList = new ArrayList<FinanceReferenceDetail>();
	private List<ScoringMetrics> finScoringMetricList = new ArrayList<ScoringMetrics>();
	private List<ScoringMetrics> nonFinScoringMetricList = new ArrayList<ScoringMetrics>();
	private HashMap<Long, List<ScoringMetrics>> scoringMetrics = new HashMap<Long, List<ScoringMetrics>>();
	private HashMap<Long, List<ScoringSlab>> scoringSlabs = new HashMap<Long, List<ScoringSlab>>();
	private List<FinanceScoreHeader> finScoreHeaderList = new ArrayList<FinanceScoreHeader>();
	private HashMap<Long, List<FinanceScoreDetail>> scoreDetailListMap = new HashMap<Long, List<FinanceScoreDetail>>();
	
	private List<Rule> feeCharges = new ArrayList<Rule>();	
	
	private List<TransactionEntry> transactionEntries = new ArrayList<TransactionEntry>();
	private List<ReturnDataSet> returnDataSetList = new ArrayList<ReturnDataSet>();
	
	private List<TransactionEntry> cmtFinanceEntries = new ArrayList<TransactionEntry>();
	private List<ReturnDataSet> cmtDataSetList = new ArrayList<ReturnDataSet>();
	
	private List<TransactionEntry> stageTransactionEntries = new ArrayList<TransactionEntry>();
	private List<ReturnDataSet> stageAccountingList = new ArrayList<ReturnDataSet>();
	
	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();
	
	private List<GuarantorDetail> gurantorsDetailList = new ArrayList<GuarantorDetail>();
	private List<JointAccountDetail> jountAccountDetailList = new ArrayList<JointAccountDetail>();
	private List<ContractorAssetDetail> contractorAssetDetails = new ArrayList<ContractorAssetDetail>();
	
	private WIFCustomer customer;
	
	private String accountingEventCode;
	private boolean actionSave = false;
	private boolean dataFetchComplete = false;
	private String userAction;
	
	//Additional Fields
	//**********************************************************************
	private ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
	private HashMap<String, Object> lovDescExtendedFieldValues = new HashMap<String, Object>();

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

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isWorkflow() {
		if (this.workflowId == 0) {
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinanceDetail) {
			FinanceDetail financeDetail = (FinanceDetail) obj;
			return equals(financeDetail);
		}
		return false;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}
	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public CarLoanDetail getCarLoanDetail() {
		return carLoanDetail;
	}
	public void setCarLoanDetail(CarLoanDetail carLoanDetail) {
		this.carLoanDetail = carLoanDetail;
	}

	public EducationalLoan getEducationalLoan() {
		return educationalLoan;
	}
	public void setEducationalLoan(EducationalLoan educationalLoan) {
		this.educationalLoan = educationalLoan;
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

	public void setHomeLoanDetail(HomeLoanDetail homeLoanDetail) {
		this.homeLoanDetail = homeLoanDetail;
	}
	public HomeLoanDetail getHomeLoanDetail() {
		return homeLoanDetail;
	}

	public void setMortgageLoanDetail(MortgageLoanDetail mortgageLoanDetail) {
		this.mortgageLoanDetail = mortgageLoanDetail;
	}
	public MortgageLoanDetail getMortgageLoanDetail() {
		return mortgageLoanDetail;
	}
	public void setGoodsLoanDetails(List<GoodsLoanDetail> goodsLoanDetails) {
	    this.goodsLoanDetails = goodsLoanDetails;
    }

	public List<GoodsLoanDetail> getGoodsLoanDetails() {
	    return goodsLoanDetails;
    }

	public void setCommidityLoanHeader(CommidityLoanHeader commidityLoanHeader) {
	    this.commidityLoanHeader = commidityLoanHeader;
    }
	public CommidityLoanHeader getCommidityLoanHeader() {
	    return commidityLoanHeader;
    }

	public List<CommidityLoanDetail> getCommidityLoanDetails() {
    	return commidityLoanDetails;
    }
	public void setCommidityLoanDetails(List<CommidityLoanDetail> commidityLoanDetails) {
    	this.commidityLoanDetails = commidityLoanDetails;
    }

	public CustomerEligibilityCheck getCustomerEligibilityCheck() {
		return customerEligibilityCheck;
	}
	public void setCustomerEligibilityCheck(CustomerEligibilityCheck customerEligibilityCheck) {
		this.customerEligibilityCheck = customerEligibilityCheck;
	}

	public CustomerScoringCheck getCustomerScoringCheck() {
		return customerScoringCheck;
	}
	public void setCustomerScoringCheck(CustomerScoringCheck customerScoringCheck) {
		this.customerScoringCheck = customerScoringCheck;
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

//	public void setFinAgrDetailList(List<FinAgreementDetail> finAgrDetailList) {
//		this.finAgrDetailList = finAgrDetailList;
//	}
//	public List<FinAgreementDetail> getFinAgrDetailList() {
//		return finAgrDetailList;
//	}

	public void setFinContributorHeader(FinContributorHeader finContributorHeader) {
		this.finContributorHeader = finContributorHeader;
	}
	public FinContributorHeader getFinContributorHeader() {
		return finContributorHeader;
	}
	
	public FinBillingHeader getFinBillingHeader() {
    	return finBillingHeader;
	}
	public void setFinBillingHeader(FinBillingHeader finBillingHeader) {
    	this.finBillingHeader = finBillingHeader;
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

	public List<TransactionEntry> getTransactionEntries() {
		return transactionEntries;
	}
	public void setTransactionEntries(List<TransactionEntry> transactionEntries) {
		this.transactionEntries = transactionEntries;
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

	public List<SharesDetail> getSharesDetails() {
    	return sharesDetails;
    }
	public void setSharesDetails(List<SharesDetail> sharesDetails) {
    	this.sharesDetails = sharesDetails;
    }

	public void setGenGoodsLoanDetails(List<GenGoodsLoanDetail> genGoodsLoanDetails) {
	    this.genGoodsLoanDetails = genGoodsLoanDetails;
    }
	public List<GenGoodsLoanDetail> getGenGoodsLoanDetails() {
	    return genGoodsLoanDetails;
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

	public FinancePremiumDetail getPremiumDetail() {
	    return premiumDetail;
    }

	public void setPremiumDetail(FinancePremiumDetail premiumDetail) {
	    this.premiumDetail = premiumDetail;
    }

}
