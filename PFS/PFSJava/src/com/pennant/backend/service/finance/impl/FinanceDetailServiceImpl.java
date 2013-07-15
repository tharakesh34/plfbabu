/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinanceDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.Interface.service.PostingsInterfaceService;
import com.pennant.app.model.CustomerCalData;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.AccountEngineExecutionRIA;
import com.pennant.app.util.AccountProcessUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FinanceProfitDetailFiller;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.CheckListDAO;
import com.pennant.backend.dao.applicationmaster.CheckListDetailDAO;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.dao.finance.DefermentHeaderDAO;
import com.pennant.backend.dao.finance.FinAgreementDetailDAO;
import com.pennant.backend.dao.finance.FinBillingDetailDAO;
import com.pennant.backend.dao.finance.FinBillingHeaderDAO;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.finance.FinContributorHeaderDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceScoreHeaderDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.lmtmasters.CarLoanDetailDAO;
import com.pennant.backend.dao.lmtmasters.EducationalExpenseDAO;
import com.pennant.backend.dao.lmtmasters.EducationalLoanDAO;
import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.lmtmasters.HomeLoanDetailDAO;
import com.pennant.backend.dao.lmtmasters.MortgageLoanDetailDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.ScoringMetricsDAO;
import com.pennant.backend.dao.rmtmasters.ScoringSlabDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerScoringCheck;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinBillingDetail;
import com.pennant.backend.model.finance.FinBillingHeader;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.lmtmasters.EducationalExpense;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.validation.CarLoanDetailValidation;
import com.pennant.backend.service.finance.validation.EduExpenseDetailValidation;
import com.pennant.backend.service.finance.validation.EducationalLoanDetailValidation;
import com.pennant.backend.service.finance.validation.FinAgreementDetailValidation;
import com.pennant.backend.service.finance.validation.FinBillingDetailValidation;
import com.pennant.backend.service.finance.validation.FinContributorDetailValidation;
import com.pennant.backend.service.finance.validation.FinanceCheckListValidation;
import com.pennant.backend.service.finance.validation.HomeLoanDetailValidation;
import com.pennant.backend.service.finance.validation.MortgageLoanDetailValidation;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.vo.CustomerLimit;
import com.pennant.document.generator.TemplateEngine;

/**
 * Service implementation for methods that depends on <b>FinanceMain</b>.<br>
 * 
 */
public class FinanceDetailServiceImpl extends GenericService<FinanceDetail> implements
        FinanceDetailService {

	private final static Logger logger = Logger.getLogger(FinanceDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	
	private FinanceMainDAO financeMainDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private DefermentDetailDAO defermentDetailDAO;
	private DefermentHeaderDAO defermentHeaderDAO;
	private RepayInstructionDAO repayInstructionDAO;
	
	private FinContributorHeaderDAO finContributorHeaderDAO;
	private FinContributorDetailDAO finContributorDetailDAO;
	
	private FinBillingHeaderDAO finBillingHeaderDAO;
	private FinBillingDetailDAO finBillingDetailDAO;
	
	private CarLoanDetailDAO carLoanDetailDAO;
	private EducationalLoanDAO educationalLoanDAO;
	private EducationalExpenseDAO educationalExpenseDAO;
	private HomeLoanDetailDAO homeLoanDetailDAO;
	private MortgageLoanDetailDAO mortgageLoanDetailDAO;
	
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private FinanceScoreHeaderDAO financeScoreHeaderDAO; 
	private ScoringMetricsDAO scoringMetricsDAO;
	private ScoringSlabDAO scoringSlabDAO;
	private FinAgreementDetailDAO finAgreementDetailDAO;
	private FinanceCheckListReferenceDAO financeCheckListReferenceDAO;
	private CheckListDetailDAO checkListDetailDAO;
	private CheckListDAO checkListDAO;
	
	private RuleDAO ruleDAO;
	private PostingsDAO postingsDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;
	private ExtendedFieldDetailDAO extendedFieldDetailDAO;
	
	private CustomerDAO customerDAO;
	private AccountingSetDAO accountingSetDAO;
	private TransactionEntryDAO transactionEntryDAO;
	private PostingsInterfaceService postingsInterfaceService;
	private FinanceProfitDetailFiller financeProfitDetailFiller;
	private AccountEngineExecution engineExecution;
	private AccountEngineExecutionRIA engineExecutionRIA;
	private AccountProcessUtil accountProcessUtil;
	private PostingsPreparationUtil	postingsPreparationUtil;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	
	private AccountTypeDAO accountTypeDAO;
	private CurrencyDAO currencyDAO;
	private CustomerLimitIntefaceService custLimitIntefaceService;
	private CommitmentDAO commitmentDAO;
	private CommitmentMovementDAO commitmentMovementDAO;

	// Declaring Classes For validation Assets.
	private CarLoanDetailValidation carLoanDetailValidation;
	private HomeLoanDetailValidation homeLoanDetailValidation;
	private MortgageLoanDetailValidation mortgageLoanDetailValidation;
	private EducationalLoanDetailValidation educationalLoanDetailValidation;
	private EduExpenseDetailValidation eduExpenseDetailValidation;
	
	private FinanceCheckListValidation financeCheckListValidation;
	private FinContributorDetailValidation finContributorDetailValidation;
	private FinBillingDetailValidation finBillingDetailValidation;
	private FinAgreementDetailValidation finAgreementDetailValidation;

	private String excludeFields = "calculateRepay,equalRepay,eventFromDate,eventToDate,increaseTerms,"
	        + "allowedDefRpyChange,availedDefRpyChange,allowedDefFrqChange,availedDefFrqChange,recalFromDate,recalToDate,excludeDeferedDates,"
	        + "financeScheduleDetails,disbDate, disbursementDetails,repayInstructions, rateChanges, defermentHeaders,addTermAfter,"
	        + "defermentDetails,scheduleMap,reqTerms,errorDetails,carLoanDetail,educationalLoan,homeLoanDetail,"
	        + "mortgageLoanDetail,proceedDedup,actionSave, finRvwRateApplFor,finGrcRvwRateApplFor,curDisbursementAmt";

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public DefermentDetailDAO getDefermentDetailDAO() {
		return defermentDetailDAO;
	}
	public void setDefermentDetailDAO(DefermentDetailDAO defermentDetailDAO) {
		this.defermentDetailDAO = defermentDetailDAO;
	}

	public DefermentHeaderDAO getDefermentHeaderDAO() {
		return defermentHeaderDAO;
	}
	public void setDefermentHeaderDAO(DefermentHeaderDAO defermentHeaderDAO) {
		this.defermentHeaderDAO = defermentHeaderDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public String getExcludeFields() {
		return excludeFields;
	}
	public void setExcludeFields(String excludeFields) {
		this.excludeFields = excludeFields;
	}

	public CarLoanDetailDAO getCarLoanDetailDAO() {
		return carLoanDetailDAO;
	}
	public void setCarLoanDetailDAO(CarLoanDetailDAO carLoanDetailDAO) {
		this.carLoanDetailDAO = carLoanDetailDAO;
	}

	public EducationalLoanDAO getEducationalLoanDAO() {
		return educationalLoanDAO;
	}
	public void setEducationalLoanDAO(EducationalLoanDAO educationalLoanDAO) {
		this.educationalLoanDAO = educationalLoanDAO;
	}

	public EducationalExpenseDAO getEducationalExpenseDAO() {
		return educationalExpenseDAO;
	}
	public void setEducationalExpenseDAO(EducationalExpenseDAO educationalExpenseDAO) {
		this.educationalExpenseDAO = educationalExpenseDAO;
	}

	public HomeLoanDetailDAO getHomeLoanDetailDAO() {
		return homeLoanDetailDAO;
	}
	public void setHomeLoanDetailDAO(HomeLoanDetailDAO homeLoanDetailDAO) {
		this.homeLoanDetailDAO = homeLoanDetailDAO;
	}

	public void setMortgageLoanDetailDAO(MortgageLoanDetailDAO mortgageLoanDetailDAO) {
		this.mortgageLoanDetailDAO = mortgageLoanDetailDAO;
	}
	public MortgageLoanDetailDAO getMortgageLoanDetailDAO() {
		return mortgageLoanDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}
	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinAgreementDetailDAO(FinAgreementDetailDAO finAgreementDetailDAO) {
	    this.finAgreementDetailDAO = finAgreementDetailDAO;
    }
	public FinAgreementDetailDAO getFinAgreementDetailDAO() {
	    return finAgreementDetailDAO;
    }

	public void setFinanceCheckListReferenceDAO(
	        FinanceCheckListReferenceDAO financeCheckListReferenceDAO) {
		this.financeCheckListReferenceDAO = financeCheckListReferenceDAO;
	}
	public FinanceCheckListReferenceDAO getFinanceCheckListReferenceDAO() {
		return financeCheckListReferenceDAO;
	}

	public void setCheckListDetailDAO(CheckListDetailDAO checkListDetailDAO) {
		this.checkListDetailDAO = checkListDetailDAO;
	}
	public CheckListDetailDAO getCheckListDetailDAO() {
		return checkListDetailDAO;
	}

	public void setCheckListDAO(CheckListDAO checkListDAO) {
		this.checkListDAO = checkListDAO;
	}
	public CheckListDAO getCheckListDAO() {
		return checkListDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}
	
	public AccountingSetDAO getAccountingSetDAO() {
    	return accountingSetDAO;
    }
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
    	this.accountingSetDAO = accountingSetDAO;
    }
	
	public ExtendedFieldHeaderDAO getExtendedFieldHeaderDAO() {
    	return extendedFieldHeaderDAO;
    }
	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
    	this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
    }

	public ExtendedFieldDetailDAO getExtendedFieldDetailDAO() {
		return extendedFieldDetailDAO;
	}
	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
	}

	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}
	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return profitDetailsDAO;
	}
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}
	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}
	
	public RuleDAO getRuleDAO() {
    	return ruleDAO;
    }
	public void setRuleDAO(RuleDAO ruleDAO) {
    	this.ruleDAO = ruleDAO;
    }
	
	public FinanceScoreHeaderDAO getFinanceScoreHeaderDAO() {
    	return financeScoreHeaderDAO;
    }
	public void setFinanceScoreHeaderDAO(FinanceScoreHeaderDAO financeScoreHeaderDAO) {
    	this.financeScoreHeaderDAO = financeScoreHeaderDAO;
    }

	public void setPostingsInterfaceService(PostingsInterfaceService postingsInterfaceService) {
		this.postingsInterfaceService = postingsInterfaceService;
	}
	public PostingsInterfaceService getPostingsInterfaceService() {
		return postingsInterfaceService;
	}
	
	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}
	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public void setEngineExecutionRIA(AccountEngineExecutionRIA engineExecutionRIA) {
	    this.engineExecutionRIA = engineExecutionRIA;
    }
	public AccountEngineExecutionRIA getEngineExecutionRIA() {
	    return engineExecutionRIA;
    }

	public void setAccountProcessUtil(AccountProcessUtil accountProcessUtil) {
		this.accountProcessUtil = accountProcessUtil;
	}
	public AccountProcessUtil getAccountProcessUtil() {
		return accountProcessUtil;
	}
	
	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinanceProfitDetailFiller getFinanceProfitDetailFiller() {
		return financeProfitDetailFiller;
	}
	public void setFinanceProfitDetailFiller(FinanceProfitDetailFiller financeProfitDetailFiller) {
		this.financeProfitDetailFiller = financeProfitDetailFiller;
	}
	
	public CustomerLimitIntefaceService getCustLimitIntefaceService() {
    	return custLimitIntefaceService;
    }
	public void setCustLimitIntefaceService(CustomerLimitIntefaceService custLimitIntefaceService) {
    	this.custLimitIntefaceService = custLimitIntefaceService;
    }
	
	public CommitmentDAO getCommitmentDAO() {
    	return commitmentDAO;
    }
	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
    	this.commitmentDAO = commitmentDAO;
    }
	
	public CommitmentMovementDAO getCommitmentMovementDAO() {
    	return commitmentMovementDAO;
    }
	public void setCommitmentMovementDAO(CommitmentMovementDAO commitmentMovementDAO) {
    	this.commitmentMovementDAO = commitmentMovementDAO;
    }
	
	public AccountTypeDAO getAccountTypeDAO() {
    	return accountTypeDAO;
    }
	public void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
    	this.accountTypeDAO = accountTypeDAO;
    }
	
	public CurrencyDAO getCurrencyDAO() {
    	return currencyDAO;
    }
	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
    	this.currencyDAO = currencyDAO;
    }
	
	public void setScoringMetricsDAO(ScoringMetricsDAO scoringMetricsDAO) {
		this.scoringMetricsDAO = scoringMetricsDAO;
	}
	public ScoringMetricsDAO getScoringMetricsDAO() {
		return scoringMetricsDAO;
	}

	public ScoringSlabDAO getScoringSlabDAO() {
		return scoringSlabDAO;
	}
	public void setScoringSlabDAO(ScoringSlabDAO scoringSlabDAO) {
		this.scoringSlabDAO = scoringSlabDAO;
	}

	public void setFinContributorHeaderDAO(FinContributorHeaderDAO finContributorHeaderDAO) {
	    this.finContributorHeaderDAO = finContributorHeaderDAO;
    }
	public FinContributorHeaderDAO getFinContributorHeaderDAO() {
	    return finContributorHeaderDAO;
    }

	public void setFinContributorDetailDAO(FinContributorDetailDAO finContributorDetailDAO) {
	    this.finContributorDetailDAO = finContributorDetailDAO;
    }
	public FinContributorDetailDAO getFinContributorDetailDAO() {
	    return finContributorDetailDAO;
    }
	
	public void setFinBillingHeaderDAO(FinBillingHeaderDAO finBillingHeaderDAO) {
		this.finBillingHeaderDAO = finBillingHeaderDAO;
	}
	public FinBillingHeaderDAO getFinBillingHeaderDAO() {
		return finBillingHeaderDAO;
	}
	
	public void setFinBillingDetailDAO(FinBillingDetailDAO finBillingDetailDAO) {
		this.finBillingDetailDAO = finBillingDetailDAO;
	}
	public FinBillingDetailDAO getFinBillingDetailDAO() {
		return finBillingDetailDAO;
	}
    
    public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
    	return financeRepaymentsDAO;
    }
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
    	this.financeRepaymentsDAO = financeRepaymentsDAO;
    }
	
	public CarLoanDetailValidation getCarLoanDetailValidation() {
		if (carLoanDetailValidation == null) {
			this.carLoanDetailValidation = new CarLoanDetailValidation(carLoanDetailDAO);
		}
		return this.carLoanDetailValidation;
	}

	public HomeLoanDetailValidation getHomeLoanDetailValidation() {
		if (homeLoanDetailValidation == null) {
			this.homeLoanDetailValidation = new HomeLoanDetailValidation(homeLoanDetailDAO);
		}
		return this.homeLoanDetailValidation;
	}

	public MortgageLoanDetailValidation getMortgageLoanDetailValidation() {
		if (mortgageLoanDetailValidation == null) {
			this.mortgageLoanDetailValidation = new MortgageLoanDetailValidation(
			        mortgageLoanDetailDAO);
		}
		return this.mortgageLoanDetailValidation;
	}

	public EducationalLoanDetailValidation getEducationalLoanDetailValidation() {
		if (educationalLoanDetailValidation == null) {
			this.educationalLoanDetailValidation = new EducationalLoanDetailValidation(
			        educationalLoanDAO);
		}
		return this.educationalLoanDetailValidation;
	}

	public EduExpenseDetailValidation getEduExpenseDetailValidation() {
		if (eduExpenseDetailValidation == null) {
			this.eduExpenseDetailValidation = new EduExpenseDetailValidation(educationalExpenseDAO);
		}
		return this.eduExpenseDetailValidation;
	}

	public FinanceCheckListValidation getFinanceCheckListValidation() {
		if (financeCheckListValidation == null) {
			this.financeCheckListValidation = new FinanceCheckListValidation(
			        financeCheckListReferenceDAO);
		}
		return this.financeCheckListValidation;
	}

	public FinContributorDetailValidation getContributorValidation(){
		if(finContributorDetailValidation==null){
			this.finContributorDetailValidation = new FinContributorDetailValidation(finContributorDetailDAO);
		}
		return this.finContributorDetailValidation;
	}
	
	public FinBillingDetailValidation getBillingValidation(){
		if(finBillingDetailValidation==null){
			this.finBillingDetailValidation = new FinBillingDetailValidation(finBillingDetailDAO);
		}
		return this.finBillingDetailValidation;
	}
	
	public FinAgreementDetailValidation getAgreementDetailValidation(){
		if(finAgreementDetailValidation==null){
			this.finAgreementDetailValidation = new FinAgreementDetailValidation(finAgreementDetailDAO);
		}
		return this.finAgreementDetailValidation;
	}

	@Override
	public FinanceDetail getFinanceDetail(boolean isWIF) {
		logger.debug("Entering");
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.getFinScheduleData().setFinanceMain(getFinanceMainDAO().getFinanceMain(isWIF));
		logger.debug("Leaving");
		return financeDetail;
	}

	@Override
	public FinanceDetail getNewFinanceDetail(boolean isWIF) {
		logger.debug("Entering");
		FinanceDetail financeDetail = getFinanceDetail(isWIF);
		financeDetail.setNewRecord(true);
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailById method.
	 * 
	 * @param finReference
	 *            (String)
	 * @return FinanceDetail
	 */
	public FinanceDetail getFinanceDetailById(String finReference, boolean isWIF,
	        String eventCodeRef) {
		logger.debug("Entering");
		
		//Finance Details
		FinanceDetail financeDetail = getFinanceDetailById(finReference, "_View", isWIF);
		
		if (!isWIF && financeDetail.getFinScheduleData().getFinanceMain() != null) {
			//Finance Accounting Fee Charge Details
			financeDetail.getFinScheduleData().setFeeRules(getPostingsDAO().getFeeChargesByFinRef(finReference, "_View"));

			//Finance Contributor Details
			if(financeDetail.getFinScheduleData().getFinanceType().isAllowRIAInvestment()){
				financeDetail.setFinContributorHeader(getFinContributorHeaderDAO().getFinContributorHeaderById(finReference, "_View"));
				if(financeDetail.getFinContributorHeader() != null){
					financeDetail.getFinContributorHeader().setContributorDetailList(
							getFinContributorDetailDAO().getFinContributorDetailByFinRef(finReference, "_View"));
				}
			}
			
			if(financeDetail.getFinScheduleData().getFinanceType() != null &&
					"ISTISNA".equals(financeDetail.getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
				
				financeDetail.setFinBillingHeader(getFinBillingHeaderDAO().getFinBillingHeaderById(finReference, "_View"));
				if(financeDetail.getFinBillingHeader() != null){
					financeDetail.getFinBillingHeader().setBillingDetailList(
							getFinBillingDetailDAO().getFinBillingDetailByFinRef(finReference, "_View"));
				}
			}

			//Finance Document Details
			financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference, "_View"));

			//Finance Reference Details List
			financeDetail = getFinanceReferenceDetails(financeDetail, financeDetail
					.getFinScheduleData().getFinanceMain().getNextRoleCode(), "DDE", eventCodeRef);
		}
		
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for get Finance Details 
	 */
	@Override
	public FinanceDetail getStaticFinanceDetailById(String finReference, String type) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = getFinanceDetail(false);
		financeDetail.getFinScheduleData().setFinReference(finReference);
		financeDetail.getFinScheduleData().setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));

		if (financeDetail.getFinScheduleData().getFinanceMain() != null) {

			//Finance Type Details
			financeDetail.getFinScheduleData().setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(
			        financeDetail.getFinScheduleData().getFinanceMain().getFinType(), type));

			//Finance Schedule Details List
			financeDetail.getFinScheduleData().setFinanceScheduleDetails(
			        getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));

			//Finance Disbursement Details List
			financeDetail.getFinScheduleData().setDisbursementDetails(
			        getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));

			//Finance Differment Header Details List
			financeDetail.getFinScheduleData().setDefermentHeaders(
			        getDefermentHeaderDAO().getDefermentHeaders(finReference, type, false));

			//Finance Differment Details List
			financeDetail.getFinScheduleData().setDefermentDetails(
			        getDefermentDetailDAO().getDefermentDetails(finReference, type, false));

			//Finance Repayment Instruction Details List
			financeDetail.getFinScheduleData().setRepayInstructions(
			        getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

			// Retrieving Fiannce Asset details based upon 'AssetCode'
			if (financeDetail.getFinScheduleData().getFinanceType() != null) {
				
				String assetCode = financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName();

				if (assetCode.equalsIgnoreCase(PennantConstants.CARLOAN)) {

					financeDetail.setCarLoanDetail(getCarLoanDetailDAO().getCarLoanDetailByID(finReference, type));

				} else if (assetCode.equalsIgnoreCase(PennantConstants.EDULOAN)) {

					financeDetail.setEducationalLoan(getEducationalLoanDAO().getEducationalLoanByID(finReference, type));
					if (financeDetail.getEducationalLoan() != null) {
						financeDetail.getEducationalLoan().setEduExpenseList(
						        getEducationalExpenseDAO().getEducationalExpenseByEduLoanId(finReference, type));
					}
					
				} else if (assetCode.equalsIgnoreCase(PennantConstants.HOMELOAN)) {

					financeDetail.setHomeLoanDetail(getHomeLoanDetailDAO().getHomeLoanDetailByID(finReference, type));

				} else if (assetCode.equalsIgnoreCase(PennantConstants.MORTLOAN)) {

					financeDetail.setMortgageLoanDetail(getMortgageLoanDetailDAO().getMortgageLoanDetailById(finReference, type));
				}

				// Fetching the Additional Field Details
				ExtendedFieldHeader fieldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName("FASSET", 
						financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName(), "_AView");
				if(fieldHeader != null){
					financeDetail.setExtendedFieldHeader(fieldHeader);
					financeDetail.getExtendedFieldHeader().setExtendedFieldDetails(getExtendedFieldDetailDAO().getExtendedFieldDetailBySubModule(
							financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName(), "_AView"));

					if (financeDetail.getExtendedFieldHeader().getExtendedFieldDetails().size() > 0) {
						
						String tableName = "";
						if(PennantStaticListUtil.getModuleName().containsKey("FASSET")){
							tableName = PennantStaticListUtil.getModuleName().get("FASSET").get(
									financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName());
						}
						
						HashMap<String, Object> map = (HashMap<String, Object>) getExtendedFieldDetailDAO().retrive(
								tableName,  financeDetail.getFinScheduleData().getFinReference(), "_Temp");
						if (map != null) {
							financeDetail.getLovDescExtendedFieldValues().putAll(map);
						}
					}
				}
			}
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * getApprovedFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailById method . with
	 * parameter id and type as blank. it fetches the approved records from the FinanceMain.
	 * 
	 * @param finReference
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getApprovedFinanceDetailById(String finReference, boolean isWIF) {
		return getFinanceDetailById(finReference, "_AView", isWIF);
	}

	/**
	 * Method to fetch finance details by id from given table type
	 * 
	 * @param finReference
	 *            (String)
	 * @param type
	 *            (String)
	 * @return FinanceDetail
	 * */
	private FinanceDetail getFinanceDetailById(String finReference, String type, boolean isWIF) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = getFinanceDetail(isWIF);
		financeDetail.getFinScheduleData().setFinReference(finReference);
		financeDetail.getFinScheduleData().setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, isWIF));

		if (financeDetail.getFinScheduleData().getFinanceMain() != null) {

			//Finance Type Details
			financeDetail.getFinScheduleData().setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(
			         financeDetail.getFinScheduleData().getFinanceMain().getFinType(), "_AView"));

			//Finance Schedule Details
			financeDetail.getFinScheduleData().setFinanceScheduleDetails(
			        getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, isWIF));

			//Finance Disbursement Details
			financeDetail.getFinScheduleData().setDisbursementDetails(
			        getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, isWIF));

			//Finance Deferment Header Details
			financeDetail.getFinScheduleData().setDefermentHeaders(
			        getDefermentHeaderDAO().getDefermentHeaders(finReference, type, isWIF));

			//Finance Deferment Details
			financeDetail.getFinScheduleData().setDefermentDetails(
			        getDefermentDetailDAO().getDefermentDetails(finReference, type, isWIF));

			//Finance Repayment Instruction Details
			financeDetail.getFinScheduleData().setRepayInstructions(
			        getRepayInstructionDAO().getRepayInstructions(finReference, type, isWIF));

			// Retrieving asset details
			if (!isWIF && financeDetail.getFinScheduleData().getFinanceType() != null) {
				
				String assetCode = financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName();

				if (assetCode.equalsIgnoreCase(PennantConstants.CARLOAN)) {

					financeDetail.setCarLoanDetail(getCarLoanDetailDAO().getCarLoanDetailByID(finReference, type));

				} else if (assetCode.equalsIgnoreCase(PennantConstants.EDULOAN)) {

					financeDetail.setEducationalLoan(getEducationalLoanDAO().getEducationalLoanByID(finReference, type));
					if (financeDetail.getEducationalLoan() != null) {
						financeDetail.getEducationalLoan().setEduExpenseList(
						        getEducationalExpenseDAO().getEducationalExpenseByEduLoanId(finReference, type));
					}
					
				} else if (assetCode.equalsIgnoreCase(PennantConstants.HOMELOAN)) {

					financeDetail.setHomeLoanDetail(getHomeLoanDetailDAO().getHomeLoanDetailByID(finReference, type));

				} else if (assetCode.equalsIgnoreCase(PennantConstants.MORTLOAN)) {

					financeDetail.setMortgageLoanDetail(getMortgageLoanDetailDAO().getMortgageLoanDetailById(finReference, type));
				}
			}
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for Fetching Finance Reference Details List by using FinReference
	 */
	@Override
	public FinanceDetail getFinanceReferenceDetails(FinanceDetail financeDetail, String userRole,
	        String screenCode, String eventCode) {
		logger.debug("Entering");
		
		//Finance Reference Details
		String finType = financeDetail.getFinScheduleData().getFinanceMain().getFinType();
		
		String nextRoleCode = userRole;
		if(!financeDetail.getFinScheduleData().getFinanceMain().isNewRecord()){
			nextRoleCode = financeDetail.getFinScheduleData().getFinanceMain().getNextRoleCode();
		}

		//Finance Agreement Details
		financeDetail.setAggrementList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(
				finType, nextRoleCode , "_AAView"));
		
		//Finance Agreement Details Fetching & Preparation
		//=======================================
		String agrIds = null;
		if(financeDetail.getAggrementList() != null && financeDetail.getAggrementList().size() > 0){
			
			agrIds = "";
			Map<Long, Boolean> keyExistencyCheckMap = new HashMap<Long, Boolean>();
			
			for (FinanceReferenceDetail finRefDetail : financeDetail.getAggrementList()) {
				agrIds = agrIds + finRefDetail.getFinRefId() +",";
				keyExistencyCheckMap.put(finRefDetail.getFinRefId(), false);
            }
			
			if(agrIds.endsWith(",")){
				agrIds = agrIds.substring(0, agrIds.length()-1);
			}
			
			financeDetail.setFinAgrDetailList(getFinAgreementDetailDAO().getFinAgreementDetailList(
					financeDetail.getFinScheduleData().getFinReference(), finType, true, agrIds, "_View"));
			
			Map<Long,FinAgreementDetail> existAgrDetailList = new HashMap<Long, FinAgreementDetail>();
			for (FinAgreementDetail agrDetail : financeDetail.getFinAgrDetailList()) {
				if(keyExistencyCheckMap.containsKey(agrDetail.getAgrId())){
					keyExistencyCheckMap.put(agrDetail.getAgrId(), true);
					existAgrDetailList.put(agrDetail.getAgrId(), agrDetail);
				}
            }
			
			List<FinAgreementDetail> mandInputAgrs = new ArrayList<FinAgreementDetail>();
			for (FinanceReferenceDetail finRefDetail : financeDetail.getAggrementList()) {
				if(!keyExistencyCheckMap.get(finRefDetail.getFinRefId())){
					FinAgreementDetail detail = new FinAgreementDetail();
					detail.setNewRecord(true);
					detail.setAgrId(finRefDetail.getFinRefId());
					detail.setFinType(finRefDetail.getFinType());
					detail.setLovDescAgrName(finRefDetail.getLovDescNamelov());
					detail.setLovDescMandInput(finRefDetail.getAllowInputInStage().contains(nextRoleCode));
					mandInputAgrs.add(detail);
				}else{
					FinAgreementDetail agrDetail = existAgrDetailList.get(finRefDetail.getFinRefId());
					agrDetail.setLovDescMandInput(finRefDetail.getAllowInputInStage().contains(nextRoleCode));
					agrDetail.setLovDescAgrName(finRefDetail.getLovDescNamelov());
					mandInputAgrs.add(agrDetail);
				}
            }
			
			//Finalize Agreement Details
			//=======================================
			financeDetail.setFinAgrDetailList(mandInputAgrs);		
			
		}
		
		// Accounting Set Details
		//=======================================
		
		boolean isCustExist = true;
		String ctgType = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCtgTypeName());
		if("".equals(ctgType)){
			isCustExist = false;
		}
		
		if (StringUtils.trimToEmpty(eventCode).equals("")) {
			if (financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate()
			        .after(new Date())) {
				eventCode = "ADDDBSF";
			} else {
				eventCode = "ADDDBSP";
			}
		}
		String accSetId = returnAccountingSetid(eventCode, financeDetail.getFinScheduleData().getFinanceType());

		//Finance Accounting Posting Details
		if(isCustExist){
			financeDetail.setTransactionEntries(getTransactionEntryDAO().getListTransactionEntryById(
					Long.valueOf(accSetId), "_AEView", true));
		}
		
		//Finance Commitment Accounting Posting Details
		if(financeDetail.getFinScheduleData().getFinanceType().isFinCommitmentReq() &&
				!StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getFinCommitmentRef()).equals("")){
			
			long accountingSetId = getAccountingSetDAO().getAccountingSetId("CMTDISB", "CMTDISB");
			if (accountingSetId != 0) {
				financeDetail.setCmtFinanceEntries(getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView",true));
			}
		}

		//Finance Fee Charge Details
		financeDetail.setFeeCharges(getTransactionEntryDAO().getListFeeChargeRules( Long.valueOf(accSetId),
				(eventCode.startsWith("ADDDBS") ? "ADDDBS" : eventCode),  "_AView"));

		//Finance Stage Accounting Posting Details
		if(isCustExist){
			financeDetail.setStageTransactionEntries(getTransactionEntryDAO().getListTransactionEntryByRefType(
					finType, "5", nextRoleCode, "_AEView", true));
		}
		
		//Finance Eligibility Rule Details List
		//=======================================
		if(isCustExist){
			financeDetail.setEligibilityRuleList(getFinanceReferenceDetailDAO().getFinRefDetByRoleAndFinType(
					finType, userRole, null, "_AEView"));

			if (financeDetail.getEligibilityRuleList().size() != 0
					|| financeDetail.getScoringGroupList().size() != 0
					&& financeDetail.getFinScheduleData().getFinanceMain().getCustID() != 0) {
				doFillCustEligibilityData(financeDetail);
			}
		}
		
		//Finance Scoring Module Details List
		//=======================================
		List<String> groupIds = null;
		if(isCustExist){
			financeDetail.setFinScoreHeaderList(getFinanceScoreHeaderDAO().getFinScoreHeaderList(
					financeDetail.getFinScheduleData().getFinReference(), "_View"));
			if(financeDetail.getFinScoreHeaderList() != null && financeDetail.getFinScoreHeaderList().size() > 0){
				List<Long> headerIds = new ArrayList<Long>();
				 groupIds = new ArrayList<String>();
				for (FinanceScoreHeader header : financeDetail.getFinScoreHeaderList()) {
					headerIds.add(header.getHeaderId());
					groupIds.add(String.valueOf(header.getGroupId()));
				}
				financeDetail.getScoreDetailListMap().clear();
				for (FinanceScoreDetail scoreDetail : getFinanceScoreHeaderDAO().getFinScoreDetailList(headerIds, "_View")) {
					List<FinanceScoreDetail> scoreDetailList = new ArrayList<FinanceScoreDetail>();
					if(financeDetail.getScoreDetailListMap().containsKey(scoreDetail.getHeaderId())){
						scoreDetailList = financeDetail.getScoreDetailListMap().get(scoreDetail.getHeaderId());
						financeDetail.getScoreDetailListMap().remove(scoreDetail.getHeaderId());
					}
					scoreDetailList.add(scoreDetail);
					financeDetail.getScoreDetailListMap().put(scoreDetail.getHeaderId(), scoreDetailList);
				}
			}
		}
		
		if(isCustExist && "C".equals(ctgType)
				&& (financeDetail.getFinScoreHeaderList() != null && financeDetail.getFinScoreHeaderList().size() > 0)){
			isCustExist = false;
		}
		
		if(isCustExist){
			fetchScoringDetails(financeDetail, ctgType, finType, userRole,groupIds);
		}

		//Check List Details
		//=======================================
		if (isCustExist && financeDetail.getFinScheduleData().getFinReference() != null) {
			financeDetail.setCheckList(getCheckListByFinRef(finType));
			financeDetail.setFinanceCheckList(getFinanceCheckListReferenceDAO()
			        .getFinanceCheckListReferenceByFinRef(financeDetail.getFinScheduleData().getFinReference(), "_View"));
		}

		// Fetching Finance Asset Additional Fields
		//=======================================
		if (!screenCode.equals("QDE")) {
			
			String assetCode = financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName();
			
			ExtendedFieldHeader fieldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName("FASSET", assetCode, "_View");
			if(fieldHeader != null){
				financeDetail.setExtendedFieldHeader(fieldHeader);
				financeDetail.getExtendedFieldHeader().setExtendedFieldDetails(
						getExtendedFieldDetailDAO().getExtendedFieldDetailBySubModule(assetCode, "_View"));

				if (financeDetail.getExtendedFieldHeader().getExtendedFieldDetails().size() > 0 && 
						!StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinReference()).equals("")) {
					
					String tableName = "";
					if(PennantStaticListUtil.getModuleName().containsKey("FASSET")){
						tableName = PennantStaticListUtil.getModuleName().get("FASSET").get(assetCode);
					}
					
					HashMap<String, Object> map = (HashMap<String, Object>) getExtendedFieldDetailDAO().retrive(
							tableName, financeDetail.getFinScheduleData().getFinReference(), "_Temp");
					if (map != null) {
						financeDetail.getLovDescExtendedFieldValues().putAll(map);
					}
				}
			}
		}
		
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for testing Finance Reference is Already Exist or not
	 */
	@Override
	public boolean isFinReferenceExits(String financeReference, String tableType, boolean isWIF) {
		logger.debug("Entering");
		if (isWIF) {
			tableType = "";
		}
		logger.debug("Leaving");
		return getFinanceMainDAO().isFinReferenceExists(financeReference, tableType, isWIF);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinanceMain/FinanceMain_Temp by
	 * using FinanceMainDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using FinanceMainDAO's update method 3) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException 
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean isWIF) throws AccountNotFoundException {
		logger.debug("Entering");
		
		auditHeader = businessValidation(auditHeader, "saveOrUpdate", isWIF);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		String tableType = "";
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		final String rcdType = financeMain.getRecordType();
		
		if (financeMain.isWorkflow()) {
			tableType = "_TEMP";
		}
		
		if(financeDetail.getStageAccountingList()!= null && financeDetail.getStageAccountingList().size() > 0){

			List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
			long linkedTranId = Long.MIN_VALUE;
			AEAmountCodes amountCodes = null;
			AEAmounts aeAmounts = new AEAmounts();
			Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
			financeMain.setRecordType(rcdType);
			DataSet dataSet = aeAmounts.createDataSet(financeMain, "STAGE", 
					financeMain.getFinStartDate(), financeMain.getFinStartDate());

			amountCodes = aeAmounts.procAEAmounts(financeMain, financeDetail.getFinScheduleData()
					.getFinanceScheduleDetails(), new FinanceProfitDetail(), financeMain.getFinStartDate());

			try {
				list.addAll(getEngineExecution().getStageExecResults(dataSet, amountCodes,
						"Y", financeMain.getRoleCode(), null));
			} catch (Exception e) {
				logger.error(e);
			}

			// Method for validating Postings with interface program and
			// return results
			if (list.get(0).getLinkedTranId() == Long.MIN_VALUE) {
				linkedTranId = getPostingsDAO().getLinkedTransId(list.get(0));
			} else {
				linkedTranId = list.get(0).getLinkedTranId();
			}
			list = getPostingsInterfaceService().doFillPostingDetails(list,
					financeMain.getFinBranch(), linkedTranId, "Y");			
			if (list != null && list.size() > 0) {
				ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
				for (int i = 0; i < list.size(); i++) {
					ReturnDataSet set = list.get(i);
					set.setLinkedTranId(linkedTranId);
					set.setPostDate(curBDay);
					if (!("0000".equals(set.getErrorId()) || "".equals(set.getErrorId()))) {
						errorDetails.add(new ErrorDetails(set.getAccountType(), set.getErrorId(),
								"E", set.getErrorMsg(), new String[] {}, new String[] {}));
					} else {
						set.setPostStatus("S");
					}
				}
				auditHeader.setErrorList(errorDetails);
				if (errorDetails.size() > 0 && auditHeader.getErrorMessage().size() > 0) {
					return auditHeader;
				}
				
				// save Postings
				getPostingsDAO().saveBatch(list, "", false);
			}
			
		}

		// Extended Field Details Save And Update
		if (financeMain.isNew()) {
			getFinanceMainDAO().save(financeMain, tableType, isWIF);
		} else {
			getFinanceMainDAO().update(financeMain, tableType, isWIF);
		}
		
		// Save Contributor Header Details
		if (financeDetail.getFinContributorHeader() != null) {
			
			FinContributorHeader contributorHeader = financeDetail.getFinContributorHeader();
			contributorHeader.setWorkflowId(0);
			String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader());
			if (contributorHeader.isNewRecord()) {
				getFinContributorHeaderDAO().save(contributorHeader, tableType);
			} else {
				getFinContributorHeaderDAO().update(contributorHeader, tableType);
			}
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
			        fields[1], financeDetail.getFinContributorHeader().getBefImage(), financeDetail
			                .getFinContributorHeader()));
			
			if(contributorHeader.getContributorDetailList() !=null && contributorHeader.getContributorDetailList().size()>0){
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Contributor");
				details = processingContributorList(details,tableType,contributorHeader.getFinReference());
				auditDetails.addAll(details);
			}
		}
		
		// Save Billing Header Details
		if (financeDetail.getFinBillingHeader() != null) {
			
			FinBillingHeader billingHeader = financeDetail.getFinBillingHeader();
			billingHeader.setWorkflowId(0);
			String[] fields = PennantJavaUtil.getFieldDetails(new FinBillingHeader());
			if (billingHeader.isNewRecord()) {
				getFinBillingHeaderDAO().save(billingHeader, tableType);
			} else {
				getFinBillingHeaderDAO().update(billingHeader, tableType);
			}
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
			        fields[1], financeDetail.getFinBillingHeader().getBefImage(), financeDetail
			                .getFinBillingHeader()));
			
			if(billingHeader.getBillingDetailList() !=null && billingHeader.getBillingDetailList().size()>0){
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Billing");
				details = processingBillingList(details,tableType,billingHeader.getFinReference());
				auditDetails.addAll(details);
			}
		}
		
		// Save schedule details
		if (!financeDetail.isNewRecord()) {
			listDeletion(financeDetail.getFinScheduleData(), tableType, isWIF);
			listSave(financeDetail.getFinScheduleData(), tableType, isWIF);
			saveFeeChargeList(financeDetail.getFinScheduleData(),tableType);
		} else {
			listSave(financeDetail.getFinScheduleData(), tableType, isWIF);
			saveFeeChargeList(financeDetail.getFinScheduleData(),tableType);
		}
		
		//Finance Scoring Details
		if(financeDetail.getFinScoreHeaderList() != null && 
				financeDetail.getFinScoreHeaderList().size() > 0 ){
			
			for (FinanceScoreHeader header : financeDetail.getFinScoreHeaderList()) {
				header.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				long headerId = getFinanceScoreHeaderDAO().saveHeader(header, "");
				if(financeDetail.getScoreDetailListMap().containsKey(header.getGroupId())){
					List<FinanceScoreDetail> ScoreDetailList = financeDetail.getScoreDetailListMap().get(header.getGroupId());
					for (FinanceScoreDetail detail : ScoreDetailList) {
						detail.setHeaderId(headerId);
                    }
					getFinanceScoreHeaderDAO().saveDetailList(ScoreDetailList, "");
				}
            }
		}
		
		//Finance Agreement List Details
		if(financeDetail.getFinAgrDetailList() !=null && financeDetail.getFinAgrDetailList().size()>0){
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("AgreementDetail");
			details = processingAgrDetailList(details,tableType,financeMain.getFinReference());
			auditDetails.addAll(details);
		}
		
		// Save Document Details
		if (financeDetail.getDocumentDetailsList() != null
		        && financeDetail.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType,
			        financeMain.getFinReference());
			auditDetails.addAll(details);
		}
		
		//Finance Check List Details
		if (financeDetail.getFinanceCheckList() != null
		        && financeDetail.getFinanceCheckList().size() > 0) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("checkListDetails");
			details = processCheckListDetails(details, tableType, financeMain.getFinReference());
			auditDetails.addAll(details);
		}

		// Save asset details
		if (financeDetail.getCarLoanDetail() != null) {
			CarLoanDetail carLoanDetail = financeDetail.getCarLoanDetail();
			carLoanDetail.setWorkflowId(0);
			String[] fields = PennantJavaUtil.getFieldDetails(new CarLoanDetail());
			if (carLoanDetail.isNewRecord()) {
				getCarLoanDetailDAO().save(carLoanDetail, tableType);
			} else {
				getCarLoanDetailDAO().update(carLoanDetail, tableType);
			}
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
			        fields[1], financeDetail.getCarLoanDetail().getBefImage(), financeDetail.getCarLoanDetail()));
		}
		
		if (financeDetail.getEducationalLoan() != null) {
			EducationalLoan educationalLoan = financeDetail.getEducationalLoan();
			educationalLoan.setWorkflowId(0);
			String[] fields = PennantJavaUtil.getFieldDetails(new EducationalLoan());
			if (educationalLoan.isNewRecord()) {
				getEducationalLoanDAO().save(educationalLoan, tableType);
			} else {
				getEducationalLoanDAO().update(educationalLoan, tableType);
			}
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
			        fields[1], financeDetail.getEducationalLoan().getBefImage(), financeDetail.getEducationalLoan()));

			// Retrieving List of Audit Details For educational expenserelated
			// modules
			if (educationalLoan.getEduExpenseList() != null
			        && educationalLoan.getEduExpenseList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("EduExpense");
				details = processingEduExpenseList(details, tableType, educationalLoan.getLoanRefNumber());
				auditDetails.addAll(details);
			}
		}
		if (financeDetail.getHomeLoanDetail() != null) {
			HomeLoanDetail homeLoanDetail = financeDetail.getHomeLoanDetail();
			homeLoanDetail.setWorkflowId(0);
			String[] fields = PennantJavaUtil.getFieldDetails(new HomeLoanDetail());
			if (homeLoanDetail.isNewRecord()) {
				getHomeLoanDetailDAO().save(homeLoanDetail, tableType);
			} else {
				getHomeLoanDetailDAO().update(homeLoanDetail, tableType);
			}
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
			        fields[1], financeDetail.getHomeLoanDetail().getBefImage(), financeDetail.getHomeLoanDetail()));
		}
		if (financeDetail.getMortgageLoanDetail() != null) {
			MortgageLoanDetail mortgageLoanDetail = financeDetail.getMortgageLoanDetail();
			mortgageLoanDetail.setWorkflowId(0);
			String[] fields = PennantJavaUtil.getFieldDetails(new MortgageLoanDetail());
			if (mortgageLoanDetail.isNewRecord()) {
				getMortgageLoanDetailDAO().save(mortgageLoanDetail, tableType);
			} else {
				getMortgageLoanDetailDAO().update(mortgageLoanDetail, tableType);
			}
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
			        fields[1], financeDetail.getMortgageLoanDetail().getBefImage(), financeDetail.getMortgageLoanDetail()));
		}

		//Additional Field Details Save / Update
		doSaveAddlFieldDetails(financeDetail, tableType);

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
		        fields[1], financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		// Generate agreements
		if (!isWIF && !financeDetail.isActionSave()) {
			generateAgreements(financeDetail, financeMain);
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinanceDetail
	 *            (financeDetail)
	 * @return financeDetail
	 */
	@Override
	public FinanceDetail refresh(FinanceDetail financeDetail) {
		logger.debug("Entering");
		getFinanceMainDAO().refresh(financeDetail.getFinScheduleData().getFinanceMain());
		getFinanceMainDAO().initialize(financeDetail.getFinScheduleData().getFinanceMain());
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinanceMain by using FinanceMainDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader, boolean isWIF) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "delete", isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		//Finance SubChild List And Reference Details List Deletion
		listDeletion(financeDetail.getFinScheduleData(), "", isWIF);
		
		//Additional Field Details Deletion
		doDeleteAddlFieldDetails(financeDetail, "");
		
		//Finance Deletion
		getFinanceMainDAO().delete(financeMain, "", isWIF);

		if (!isWIF) {
			auditDetails.addAll(getListAuditDetails(assetDeletion(financeDetail, "", auditHeader.getAuditTranType())));
			auditDetails.addAll(getListAuditDetails(listDeletion_CheckList(financeDetail, "",auditHeader.getAuditTranType())));
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,
			        fields[0], fields[1], financeMain.getBefImage(), financeMain));
			auditHeader.setAuditDetails(auditDetails);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using getFinanceMainDAO().save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using getFinanceMainDAO().update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"_Temp" 4) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinanceMain by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader, boolean isWIF)
	        throws AccountNotFoundException {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove", isWIF);
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
		BeanUtils.copyProperties((FinanceDetail) aAuditHeader.getAuditDetail().getModelData(),
		        financeDetail);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		financeMain.setFinApprovedDate(curBDay);
		final String rcdType = financeMain.getRecordType();
		CarLoanDetail carLoanDetail = financeDetail.getCarLoanDetail();
		EducationalLoan educationalLoan = financeDetail.getEducationalLoan();
		HomeLoanDetail homeLoanDetail = financeDetail.getHomeLoanDetail();
		MortgageLoanDetail mortgageLoanDetail = financeDetail.getMortgageLoanDetail();

		AuditHeader auditHeader = new AuditHeader();
		BeanUtils.copyProperties(aAuditHeader, auditHeader);
		
		// Finance Accounting Posting Details Process
		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
		long linkedTranId = Long.MIN_VALUE;
		AEAmountCodes amountCodes = null;
		BigDecimal cmtPostAmt = BigDecimal.ZERO;
		Commitment commitment = null;
		AEAmounts aeAmounts = new AEAmounts();
		financeMain.setRecordType(rcdType);
		DataSet dataSet = aeAmounts.createDataSet(financeMain, financeDetail.getAccountingEventCode(), 
				financeMain.getFinStartDate(), financeMain.getFinStartDate());

		amountCodes = aeAmounts.procAEAmounts(financeMain, financeDetail.getFinScheduleData()
		        .getFinanceScheduleDetails(), new FinanceProfitDetail(), financeMain.getFinStartDate());
		
		if(financeDetail.getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(financeDetail.getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
				if(financeDetail.getFinBillingHeader() != null && 
						financeDetail.getFinBillingHeader().getPreContrOrDeffCost().compareTo(BigDecimal.ZERO) > 0){
					amountCodes.setDEFFEREDCOST(financeDetail.getFinBillingHeader().getPreContrOrDeffCost());
				}
		}
		
		try {
			if(!financeDetail.getFinScheduleData().getFinanceType().isAllowRIAInvestment()){
				list = getEngineExecution().getAccEngineExecResults(dataSet, amountCodes, "Y", null);//TODO
				if(financeDetail.getStageAccountingList()!= null && financeDetail.getStageAccountingList().size() > 0){
					
					dataSet.setFinEvent("STAGE");
					list.addAll(getEngineExecution().getStageExecResults(dataSet, amountCodes,
							"Y", financeMain.getRoleCode(), null));
				}
			}else{
				List<AEAmountCodesRIA> riaDetailList = new ArrayList<AEAmountCodesRIA>();
				if(financeDetail.getFinContributorHeader() != null && 
						financeDetail.getFinContributorHeader().getContributorDetailList() != null &&
						financeDetail.getFinContributorHeader().getContributorDetailList().size() > 0){
					riaDetailList = getEngineExecutionRIA().prepareRIADetails(
							financeDetail.getFinContributorHeader().getContributorDetailList(), dataSet.getFinReference());
				}
				list = getEngineExecutionRIA().getAccEngineExecResults(dataSet, amountCodes, "Y", riaDetailList);
			}
			
			// Finance Commitment Reference Posting Details
			if(!StringUtils.trimToEmpty(financeMain.getFinCommitmentRef()).equals("")){
				commitment = getCommitmentDAO().getCommitmentById(financeMain.getFinCommitmentRef(), "");
				
				AECommitment aeCommitment = new AECommitment();
				aeCommitment.setCMTAMT(commitment.getCmtAmount());
				aeCommitment.setCHGAMT(commitment.getCmtCharges());
				aeCommitment.setDISBURSE(financeMain.getFinAmount());
				aeCommitment.setRPPRI(BigDecimal.ZERO);
				
				list.addAll(getEngineExecution().getCommitmentExecResults(aeCommitment, commitment, "CMTDISB", "Y", null));
			}
			
		} catch (Exception e) {
			logger.error(e);
		}

		// Method for validating Postings with interface program and
		// return results
		if (list.get(0).getLinkedTranId() == Long.MIN_VALUE) {
			linkedTranId = getPostingsDAO().getLinkedTransId(list.get(0));
		} else {
			linkedTranId = list.get(0).getLinkedTranId();
		}
		
		list = getPostingsInterfaceService().doFillPostingDetails(list, financeMain.getFinBranch(), linkedTranId, "Y");			
		if (list != null && list.size() > 0) {
			ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
			boolean isFetchFinAc = false;
			boolean isFetchCistIntAc = false;
			for (int i = 0; i < list.size(); i++) {
				ReturnDataSet set = list.get(i);
				set.setLinkedTranId(linkedTranId);
				set.setPostDate(curBDay);
				if (!("0000".equals(set.getErrorId()) || "".equals(set.getErrorId()))) {
					errorDetails.add(new ErrorDetails(set.getAccountType(), set.getErrorId(),
					        "E", set.getErrorMsg(), new String[] {}, new String[] {}));
				} else {
					set.setPostStatus("S");
					if("CMTDISB".equals(set.getFinEvent())){
						cmtPostAmt = set.getPostAmount();
					}
				}
				if (!isFetchFinAc && set.getAccountType().equals(
				         financeDetail.getFinScheduleData().getFinanceType().getFinAcType())) {
					isFetchFinAc = true;
					financeMain.setFinAccount(set.getAccount());
				}
				if (!isFetchCistIntAc && set.getAccountType().equals(
				         financeDetail.getFinScheduleData().getFinanceType().getPftPayAcType())) {
					isFetchCistIntAc = true;
					financeMain.setFinCustPftAccount(set.getAccount());
				}
				
			}
			auditHeader.setErrorList(errorDetails);
			if (errorDetails.size() > 0 && auditHeader.getErrorMessage().size() > 0) {
				return auditHeader;
			}
		}
		
		if(commitment != null && cmtPostAmt.compareTo(BigDecimal.ZERO) > 0){
			getCommitmentDAO().updateCommitmentAmounts(commitment.getCmtReference(), cmtPostAmt);
			CommitmentMovement movement = prepareCommitMovement(commitment, financeMain, cmtPostAmt, linkedTranId);
			if(movement != null){
				getCommitmentMovementDAO().save(movement, "");
			}
		}
		
		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinanceMainDAO().delete(financeMain, "", isWIF);
			listDeletion(financeDetail.getFinScheduleData(), "", isWIF);
			
			//Additional Field Details Deletion
			doDeleteAddlFieldDetails(financeDetail, "");
			
			auditDetails.addAll(getListAuditDetails(assetDeletion(financeDetail, "", tranType)));
			auditDetails.addAll(getListAuditDetails(listDeletion_CheckList(financeDetail, "", auditHeader.getAuditTranType())));
			auditDetails.addAll(getListAuditDetails(listDeletion_FinAgr(financeDetail, "", auditHeader.getAuditTranType())));
			auditDetails.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "", auditHeader.getAuditTranType())));
			auditDetails.addAll(getListAuditDetails(listDeletion_FinBilling(financeDetail, "", auditHeader.getAuditTranType())));
			
		} else {
			financeMain.setRoleCode("");
			financeMain.setNextRoleCode("");
			financeMain.setTaskId("");
			financeMain.setNextTaskId("");
			financeMain.setWorkflowId(0);

			if (!financeDetail.isExtSource()) {
				if (carLoanDetail != null) {
					carLoanDetail.setRoleCode("");
					carLoanDetail.setNextRoleCode("");
					carLoanDetail.setTaskId("");
					carLoanDetail.setNextTaskId("");
					carLoanDetail.setWorkflowId(0);
				}
				if (educationalLoan != null) {
					educationalLoan.setRoleCode("");
					educationalLoan.setNextRoleCode("");
					educationalLoan.setTaskId("");
					educationalLoan.setNextTaskId("");
					educationalLoan.setWorkflowId(0);
				}
				if (homeLoanDetail != null) {
					homeLoanDetail.setRoleCode("");
					homeLoanDetail.setNextRoleCode("");
					homeLoanDetail.setTaskId("");
					homeLoanDetail.setNextTaskId("");
					homeLoanDetail.setWorkflowId(0);
				}
				if (mortgageLoanDetail != null) {
					mortgageLoanDetail.setRoleCode("");
					mortgageLoanDetail.setNextRoleCode("");
					mortgageLoanDetail.setTaskId("");
					mortgageLoanDetail.setNextTaskId("");
					mortgageLoanDetail.setWorkflowId(0);
				}
			}

			if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeMain.setRecordType("");
				getFinanceMainDAO().save(financeMain, "", isWIF);
				
				// Save Contributor Header Details
				if (financeDetail.getFinContributorHeader() != null) {
					FinContributorHeader contributorHeader = financeDetail.getFinContributorHeader();
					String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader());
					getFinContributorHeaderDAO().save(contributorHeader, "");
					auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
					        fields[1], financeDetail.getFinContributorHeader().getBefImage(), financeDetail.getFinContributorHeader()));
				}
				
				// Save Billing Header Details
				if (financeDetail.getFinBillingHeader() != null) {
					FinBillingHeader billingHeader = financeDetail.getFinBillingHeader();
					String[] fields = PennantJavaUtil.getFieldDetails(new FinBillingHeader());
					getFinBillingHeaderDAO().save(billingHeader, "");
					auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
					        fields[1], financeDetail.getFinBillingHeader().getBefImage(), financeDetail.getFinBillingHeader()));
				}
				
				//Schedule Details
				listSave(financeDetail.getFinScheduleData(), "", isWIF);
				
				//Fee Charge Details
				saveFeeChargeList(financeDetail.getFinScheduleData(),"");

				if (!financeDetail.isExtSource()) {
					if (carLoanDetail != null) {
						carLoanDetail.setRecordType("");
						getCarLoanDetailDAO().save(carLoanDetail, "");
					}
					if (educationalLoan != null) {
						educationalLoan.setRecordType("");
						getEducationalLoanDAO().save(educationalLoan, "");
						if (educationalLoan.getEduExpenseList() != null && educationalLoan.getEduExpenseList().size() > 0) {
							List<AuditDetail> details = financeDetail.getAuditDetailMap().get("EduExpense");
							details = processingEduExpenseList(details, "", educationalLoan.getLoanRefNumber());
							auditDetails.addAll(details);
						}
					}
					if (homeLoanDetail != null) {
						homeLoanDetail.setRecordType("");
						getHomeLoanDetailDAO().save(homeLoanDetail, "");
					}
					if (mortgageLoanDetail != null) {
						mortgageLoanDetail.setRecordType("");
						getMortgageLoanDetailDAO().save(mortgageLoanDetail, "");
					}
				}
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeMain.setRecordType("");
				getFinanceMainDAO().update(financeMain, "", isWIF);

				// ScheduleDetails delete and save
				listDeletion(financeDetail.getFinScheduleData(), "", isWIF);
				listSave(financeDetail.getFinScheduleData(), "", isWIF);
				
				//Fee Charge Details
				saveFeeChargeList(financeDetail.getFinScheduleData(),"");
				
				// Save assets
				if (carLoanDetail != null) {
					carLoanDetail.setRecordType("");
					getCarLoanDetailDAO().update(carLoanDetail, "");
				}
				if (educationalLoan != null) {
					educationalLoan.setRecordType("");
					getEducationalLoanDAO().update(educationalLoan, "");
					if (educationalLoan.getEduExpenseList() != null && educationalLoan.getEduExpenseList().size() > 0) {
						List<AuditDetail> details = financeDetail.getAuditDetailMap().get("EduExpense");
						details = processingEduExpenseList(details, "", educationalLoan.getLoanRefNumber());
						auditDetails.addAll(details);
					}
				}
				if (homeLoanDetail != null) {
					homeLoanDetail.setRecordType("");
					getHomeLoanDetailDAO().update(homeLoanDetail, "");
				}
				if (mortgageLoanDetail != null) {
					mortgageLoanDetail.setRecordType("");
					getMortgageLoanDetailDAO().update(mortgageLoanDetail, "");
				}
			}

			if (!financeDetail.isExtSource()) {
				
				// Save Contributor Header Details
				if (financeDetail.getFinContributorHeader() != null) {
					FinContributorHeader contributorHeader = financeDetail.getFinContributorHeader();
					if(contributorHeader.getContributorDetailList() !=null &&  contributorHeader.getContributorDetailList().size()>0){
						List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Contributor");
						details = processingContributorList(details,"",contributorHeader.getFinReference());
						auditDetails.addAll(details);
					}
				}
				
				// Save Billing Header Details
				if (financeDetail.getFinBillingHeader() != null) {
					FinBillingHeader billingHeader = financeDetail.getFinBillingHeader();
					if(billingHeader.getBillingDetailList() !=null &&  billingHeader.getBillingDetailList().size()>0){
						List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Billing");
						details = processingBillingList(details,"",billingHeader.getFinReference());
						auditDetails.addAll(details);
					}
				}
				
				// Save Document Details
				if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
					details = processingDocumentDetailsList(details, "", financeMain.getFinReference());
					auditDetails.addAll(details);
					listDocDeletion(financeDetail, "_Temp");
				}
				if (financeDetail.getFinanceCheckList() != null && financeDetail.getFinanceCheckList().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("checkListDetails");
					details = processCheckListDetails(details, "", financeMain.getFinReference());
					auditDetails.addAll(details);
				}
				
				if(financeDetail.getFinAgrDetailList() !=null && financeDetail.getFinAgrDetailList().size()>0){
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("AgreementDetail");
					details = processingAgrDetailList(details,"",financeMain.getFinReference());
					auditDetails.addAll(details);
				}
				
				//Additional Field Details Save / Update
				doSaveAddlFieldDetails(financeDetail, "");
			}
			
			// save Postings
			if(list != null && list.size()>0){
				getPostingsDAO().saveBatch(list, "", false);
			}
			
			//Update disbursement details
			getFinanceDisbursementDAO().updateLinkedTranId(financeMain.getFinReference(), linkedTranId, "");

			// Save/Update Finance Profit Details
			FinanceProfitDetail profitDetail = doSave_PftDetails(amountCodes, financeMain);

			//Account Details Update
			getAccountProcessUtil().procAccountUpdate(list , profitDetail.getTdPftAccrued(), financeMain.getCustID());

			// Generate agreements
			if (!financeDetail.isExtSource()) {
				generateAgreements(financeDetail, financeMain);
			}
		}
		
		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);

		if (!financeDetail.isExtSource()) {
			getFinanceMainDAO().delete(financeMain, "_TEMP", isWIF);

			// ScheduleDetails delete
			listDeletion(financeDetail.getFinScheduleData(), "_TEMP", isWIF);
			
			//Additional Field Details Deletion in _Temp Table
			doDeleteAddlFieldDetails(financeDetail, "_Temp");
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			auditDetailList.addAll(getListAuditDetails(assetDeletion(financeDetail, "_TEMP", auditHeader.getAuditTranType())));
			auditDetailList.addAll(getListAuditDetails(listDeletion_CheckList(financeDetail, "_TEMP", auditHeader.getAuditTranType())));
			auditDetailList.addAll(getListAuditDetails(listDeletion_FinAgr(financeDetail, "_TEMP", auditHeader.getAuditTranType())));
			auditDetailList.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "_TEMP", auditHeader.getAuditTranType())));
			auditDetailList.addAll(getListAuditDetails(listDeletion_FinBilling(financeDetail, "_TEMP", auditHeader.getAuditTranType())));

			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1,
			        fields[0], fields[1], financeMain.getBefImage(), financeMain));
			auditHeader.setAuditDetails(auditDetailList);
			// Adding audit as deleted from TEMP table
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
		        fields[1], financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		// Adding audit as Insert/Update/deleted into main table
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}
	
	private CommitmentMovement prepareCommitMovement(Commitment commitment, FinanceMain financeMain, 
			BigDecimal postAmount, long linkedtranId){
		
		CommitmentMovement movement = new CommitmentMovement();
		
		movement.setCmtReference(commitment.getCmtReference());
		movement.setFinReference(financeMain.getFinReference());
		movement.setFinBranch(financeMain.getFinBranch());
		movement.setFinType(financeMain.getFinType());
		movement.setMovementDate((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE"));
		movement.setMovementOrder(1);
		movement.setMovementType("AD");
		movement.setMovementAmount(postAmount);
		movement.setCmtAmount(commitment.getCmtAmount());
		movement.setCmtUtilizedAmount(commitment.getCmtUtilizedAmount().add(postAmount));
		movement.setCmtAvailable(commitment.getCmtAvailable().subtract(postAmount));
		movement.setCmtCharges(BigDecimal.ZERO);
		movement.setLinkedTranId(linkedtranId);
		movement.setVersion(0);
		movement.setLastMntBy(financeMain.getLastMntBy());
		movement.setLastMntOn(financeMain.getLastMntOn());
		movement.setRecordStatus("Approved");
		movement.setRoleCode("");
		movement.setNextRoleCode("");
		movement.setTaskId("");
		movement.setNextTaskId("");
		movement.setRecordType("");
		movement.setWorkflowId(0);
		
		return movement;
		
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceMainDAO().delete with parameters financeMain,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader, boolean isWIF) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doReject", isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		// ScheduleDetails deletion
		listDeletion(financeDetail.getFinScheduleData(), "_TEMP", isWIF);

		//Additional Field Details Deletion
		doDeleteAddlFieldDetails(financeDetail, "_TEMP");
		
		getFinanceMainDAO().delete(financeMain, "_TEMP", isWIF);
		
		// Asset deletion
		if (!isWIF) {
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));
			auditDetails.addAll(getListAuditDetails(assetDeletion(financeDetail, "_TEMP", auditHeader.getAuditTranType())));
			auditDetails.addAll(getListAuditDetails(listDeletion_CheckList(financeDetail, "_TEMP", auditHeader.getAuditTranType())));
			auditDetails.addAll(getListAuditDetails(listDeletion_FinAgr(financeDetail, "_TEMP", auditHeader.getAuditTranType())));
			auditDetails.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "_TEMP", auditHeader.getAuditTranType())));
			auditDetails.addAll(getListAuditDetails(listDeletion_FinBilling(financeDetail, "_TEMP", auditHeader.getAuditTranType())));
			auditHeader.setAuditDetails(auditDetails);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * Method for Update Finance into BlackList Condition (WRITEOFF)
	 */
	@Override
    public void updateFinBlackListStatus(String finReference) {
	   getFinanceMainDAO().updateFinBlackListStatus(finReference);//FIXME === Add writeoff field rather than BlockList
    }

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinanceMainDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean isWIF) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, isWIF);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		if (!isWIF) {
			auditHeader = getAuditDetails(auditHeader, method);
		}
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = financeDetail.getFinScheduleData().getFinanceMain().getUserDetails().getUsrLanguage();

		if (!financeDetail.isExtSource()) {

			if (!isWIF && !financeDetail.isLovDescIsQDE()) {
				
				String assetCode = financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName();
				if (assetCode.equalsIgnoreCase(PennantConstants.CARLOAN)) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CarLoanDetail");
					details = getCarLoanDetailValidation().carLoanDetailListValidation(details, method, usrLanguage);
					auditDetails.addAll(details);
				}

				if (assetCode.equalsIgnoreCase(PennantConstants.EDULOAN)) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("EducationalLoan");
					details = getEducationalLoanDetailValidation().eduLoanDetailListValidation(details, method, usrLanguage);
					auditDetails.addAll(details);
					if (financeDetail.getEducationalLoan().getEduExpenseList() != null
					        && financeDetail.getEducationalLoan().getEduExpenseList().size() > 0) {
						List<AuditDetail> expDetails = financeDetail.getAuditDetailMap().get("EduExpense");
						details = getEduExpenseDetailValidation().eduExpenseDetailListValidation(expDetails, method, usrLanguage);
						auditDetails.addAll(expDetails);
					}
				}

				if (assetCode.equalsIgnoreCase(PennantConstants.HOMELOAN)) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("HomeLoanDetail");
					details = getHomeLoanDetailValidation().homeLoanDetailListValidation(details, method, usrLanguage);
					auditDetails.addAll(details);
				}

				if (assetCode.equalsIgnoreCase(PennantConstants.MORTLOAN)) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("MortgageLoanDetail");
					details = getMortgageLoanDetailValidation().mortgageLoanDetailListValidation(details, method, usrLanguage);
					auditDetails.addAll(details);
				}
			}

			//Finance Check List Details
			if (financeDetail.getFinanceCheckList() != null && financeDetail.getFinanceCheckList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("checkListDetails");
				details = getFinanceCheckListValidation().finCheckListDetailListValidation(details, method, usrLanguage);
				auditDetails.addAll(details);
			}
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Validate Finance Object
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @param isWIF
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method,
	        boolean isWIF) {
		logger.debug("Entering");
		
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		
		if (!isWIF) {
			if (!financeDetail.getUserAction().equalsIgnoreCase("Resubmit")) {
				if ((StringUtils.equalsIgnoreCase(method, "saveOrUpdate") && !financeDetail.isActionSave()) || 
						StringUtils.equalsIgnoreCase(method, "doApprove")) {
					if (!(financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD) ||
							financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL))
					        && financeDetail.getCheckList().size() > 0)
						auditDetail = validation_CheckList(auditDetail, usrLanguage, method);
				}
			}
		}

		FinanceMain tempFinanceMain = null;
		if (financeMain.isWorkflow()) {
			tempFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "_Temp", isWIF);
		}
		FinanceMain befFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "", isWIF);
		FinanceMain old_FinanceMain = financeMain.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = financeMain.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (financeMain.isNew()) { // for New record or new record into work flow

			if (!financeMain.isWorkflow()) {// With out Work flow only new
				// records
				if (befFinanceMain != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (befFinanceMain != null || tempFinanceMain != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						        PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceMain == null || tempFinanceMain != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!financeMain.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befFinanceMain == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (old_FinanceMain != null
					        && !old_FinanceMain.getLastMntOn().equals(befFinanceMain.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
						        .equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							        PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							        PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempFinanceMain == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinanceMain != null && old_FinanceMain != null
				        && !old_FinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !financeMain.isWorkflow()) {
			auditDetail.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType = "";

		if (method.equals("saveOrUpdate") || method.equals("doApprove")
		        || method.equals("doReject")) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (!financeDetail.isExtSource()) {
			
			//Finance Contribution Details
			if(financeDetail.getFinContributorHeader() != null && 
					financeDetail.getFinContributorHeader().getContributorDetailList() !=null 
					&& financeDetail.getFinContributorHeader().getContributorDetailList().size()>0){
				auditDetailMap.put("Contributor", setContributorAuditData(financeDetail.getFinContributorHeader(),auditTranType,method));
				auditDetails.addAll(auditDetailMap.get("Contributor"));
			}
			
			//Finance Billing Details
			if(financeDetail.getFinBillingHeader() != null && 
					financeDetail.getFinBillingHeader().getBillingDetailList() !=null 
					&& financeDetail.getFinBillingHeader().getBillingDetailList().size()>0){
				auditDetailMap.put("Billing", setBillingAuditData(financeDetail.getFinBillingHeader(),auditTranType,method));
				auditDetails.addAll(auditDetailMap.get("Billing"));
			}
			
			//Finance Document Details
			if (financeDetail.getDocumentDetailsList() != null
			        && financeDetail.getDocumentDetailsList() != null
			        && financeDetail.getDocumentDetailsList().size() > 0) {
				auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(financeDetail, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
			}
			
			//Finance Agreement Details
			if(financeDetail.getFinAgrDetailList() != null && 
					financeDetail.getFinAgrDetailList().size()>0){
				auditDetailMap.put("AgreementDetail", setAgreementDetailAuditData(financeDetail,auditTranType,method));
				auditDetails.addAll(auditDetailMap.get("AgreementDetail"));
			}
			
			//Finance Asset Details
			if (financeDetail.getCarLoanDetail() != null) {
				List<AuditDetail> details = new ArrayList<AuditDetail>();
				CarLoanDetail carLoanDetail = financeDetail.getCarLoanDetail();
				carLoanDetail.setWorkflowId(financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId());
				String[] fields = PennantJavaUtil.getFieldDetails(new CarLoanDetail());
				details.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], carLoanDetail.getBefImage(), carLoanDetail));
				auditDetailMap.put("CarLoanDetail", details);
				auditDetails.addAll(auditDetailMap.get("CarLoanDetail"));
			}
			
			if (financeDetail.getEducationalLoan() != null) {
				List<AuditDetail> details = new ArrayList<AuditDetail>();
				EducationalLoan educationalLoan = financeDetail.getEducationalLoan();
				educationalLoan.setWorkflowId(financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId());
				String[] fields = PennantJavaUtil.getFieldDetails(new EducationalLoan());
				details.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], educationalLoan.getBefImage(), educationalLoan));
				auditDetailMap.put("EducationalLoan", details);
				auditDetails.addAll(auditDetailMap.get("EducationalLoan"));

				if (educationalLoan.getEduExpenseList() != null && educationalLoan.getEduExpenseList().size() > 0) {
					auditDetailMap.put("EduExpense", setEduExpenseAuditData(educationalLoan, auditTranType, method));
					auditDetails.addAll(auditDetailMap.get("EduExpense"));
				}
			}
			
			if (financeDetail.getHomeLoanDetail() != null) {
				List<AuditDetail> details = new ArrayList<AuditDetail>();
				HomeLoanDetail homeLoanDetail = financeDetail.getHomeLoanDetail();
				homeLoanDetail.setWorkflowId(financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId());
				String[] fields = PennantJavaUtil.getFieldDetails(new HomeLoanDetail());
				details.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], homeLoanDetail.getBefImage(), homeLoanDetail));
				auditDetailMap.put("HomeLoanDetail", details);
				auditDetails.addAll(auditDetailMap.get("HomeLoanDetail"));
			}
			
			if (financeDetail.getMortgageLoanDetail() != null) {
				List<AuditDetail> details = new ArrayList<AuditDetail>();
				MortgageLoanDetail mortgageLoanDetail = financeDetail.getMortgageLoanDetail();
				mortgageLoanDetail.setWorkflowId(financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId());
				String[] fields = PennantJavaUtil.getFieldDetails(new MortgageLoanDetail());
				details.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], mortgageLoanDetail.getBefImage(), mortgageLoanDetail));
				auditDetailMap.put("MortgageLoanDetail", details);
				auditDetails.addAll(auditDetailMap.get("MortgageLoanDetail"));
			}

			//Finance Check List Details
			if (StringUtils.equals(method, "saveOrUpdate")) {
				if (financeDetail.getFinanceCheckList() != null && financeDetail.getFinanceCheckList().size() > 0) {
					auditDetailMap.put("checkListDetails", setCheckListAuditData(financeDetail, auditTranType, method));
					auditDetails.addAll(auditDetailMap.get("checkListDetails"));
				}
			} else {
				String tableType = "_Temp";
				if (financeDetail.getFinScheduleData().getFinanceMain().getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
					tableType = "";
				}
				financeDetail.setFinanceCheckList(getFinanceCheckListReferenceByFinRef(
				        financeDetail.getFinScheduleData().getFinReference(), tableType));
				if (financeDetail.getFinanceCheckList() != null && financeDetail.getFinanceCheckList().size() > 0) {
					auditDetailMap.put("checkListDetails", setCheckListAuditData(financeDetail, auditTranType, method));
					auditDetails.addAll(auditDetailMap.get("checkListDetails"));
				}
			}
		}
		
		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeDetail);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	/**
	 * Method to delete loan asset
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param isWIF
	 * @return auditList
	 * */
	public List<AuditDetail> assetDeletion(FinanceDetail financeDetail, String tableType,
	        String auditTranType) {
		logger.debug("Entering ");
		
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		CarLoanDetail carLoanDetail = financeDetail.getCarLoanDetail();
		EducationalLoan educationalLoan = financeDetail.getEducationalLoan();
		EducationalExpense eduExpense = null;
		HomeLoanDetail homeLoanDetail = financeDetail.getHomeLoanDetail();
		MortgageLoanDetail mortgageLoanDetail = financeDetail.getMortgageLoanDetail();

		if (carLoanDetail != null) {
			String[] carLoanFields = PennantJavaUtil.getFieldDetails(new CarLoanDetail());
			getCarLoanDetailDAO().delete(carLoanDetail, tableType);
			auditList.add(new AuditDetail(auditTranType, 1, carLoanFields[0], carLoanFields[1],
			        financeDetail.getCarLoanDetail().getBefImage(), financeDetail.getCarLoanDetail()));
		}
		if (educationalLoan != null) {
			String[] eduLoanFields = PennantJavaUtil.getFieldDetails(new EducationalLoan());
			getEducationalLoanDAO().delete(educationalLoan, tableType);
			auditList.add(new AuditDetail(auditTranType, 1, eduLoanFields[0], eduLoanFields[1],
			        financeDetail.getEducationalLoan().getBefImage(), financeDetail.getEducationalLoan()));
			if (educationalLoan.getEduExpenseList() != null
			        && educationalLoan.getEduExpenseList().size() > 0) {
				String[] fields = PennantJavaUtil.getFieldDetails(new EducationalExpense());
				for (int i = 0; i < educationalLoan.getEduExpenseList().size(); i++) {
					eduExpense = educationalLoan.getEduExpenseList().get(i);
					if (!eduExpense.getRecordType().equals("")) {
						auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						        eduExpense.getBefImage(), eduExpense));
					}
				}
				getEducationalExpenseDAO().delete(
				        financeDetail.getFinScheduleData().getFinReference(), tableType);
			}
		}
		if (homeLoanDetail != null) {
			String[] homeLoanFields = PennantJavaUtil.getFieldDetails(new HomeLoanDetail());
			getHomeLoanDetailDAO().delete(homeLoanDetail, tableType);
			auditList.add(new AuditDetail(auditTranType, 1, homeLoanFields[0], homeLoanFields[1],
			        financeDetail.getHomeLoanDetail().getBefImage(), financeDetail.getHomeLoanDetail()));
		}
		if (mortgageLoanDetail != null) {
			String[] mortgageLoanFields = PennantJavaUtil.getFieldDetails(new MortgageLoanDetail());
			getMortgageLoanDetailDAO().delete(mortgageLoanDetail, tableType);
			auditList.add(new AuditDetail(auditTranType, 1, mortgageLoanFields[0], mortgageLoanFields[1],
					financeDetail.getMortgageLoanDetail().getBefImage(),financeDetail.getMortgageLoanDetail()));
		}
		logger.debug("Leaving ");
		return auditList;
	}

	/**
	 * Method to delete schedule, disbursement, deferementheader, defermentdetail,repayinstruction, ratechanges lists.
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param isWIF
	 */
	public void listDeletion(FinScheduleData finDetail, String tableType, boolean isWIF) {
		logger.debug("Entering ");

		getFinanceScheduleDetailDAO().deleteByFinReference(finDetail.getFinReference(), tableType, isWIF);
		getFinanceDisbursementDAO().deleteByFinReference(finDetail.getFinReference(), tableType, isWIF);
		getDefermentHeaderDAO().deleteByFinReference(finDetail.getFinReference(), tableType, isWIF);
		getDefermentDetailDAO().deleteByFinReference(finDetail.getFinReference(), tableType, isWIF);
		getRepayInstructionDAO().deleteByFinReference(finDetail.getFinReference(), tableType, isWIF);
		
		//Fee Charge Details
		if(!isWIF){
			getPostingsDAO().deleteChargesBatch(finDetail.getFinReference(), tableType);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method to save what if inquiry lists
	 */
	public void listSave(FinScheduleData finDetail, String tableType, boolean isWIF) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < finDetail.getFinanceScheduleDetails().size(); i++) {
			finDetail.getFinanceScheduleDetails().get(i).setLastMntBy(finDetail.getFinanceMain().getLastMntBy());
			finDetail.getFinanceScheduleDetails().get(i).setFinReference(finDetail.getFinReference());
			int seqNo = 0;

			if (mapDateSeq.containsKey(finDetail.getFinanceScheduleDetails().get(i).getSchDate())) {
				seqNo = mapDateSeq.get(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
				mapDateSeq.remove(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(finDetail.getFinanceScheduleDetails().get(i).getSchDate(), seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setSchSeq(seqNo);
		}
		getFinanceScheduleDetailDAO().saveList(finDetail.getFinanceScheduleDetails(), tableType, isWIF);

		// Finance Disbursement Details
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		for (int i = 0; i < finDetail.getDisbursementDetails().size(); i++) {
			finDetail.getDisbursementDetails().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
			int seqNo = 0;

			if (mapDateSeq.containsKey(finDetail.getDisbursementDetails().get(i).getDisbDate())) {
				seqNo = mapDateSeq.get(finDetail.getDisbursementDetails().get(i).getDisbDate());
				mapDateSeq.remove(finDetail.getDisbursementDetails().get(i).getDisbDate());
			} else {
				seqNo = seqNo + 1;
			}

			mapDateSeq.put(finDetail.getDisbursementDetails().get(i).getDisbDate(), seqNo);
			finDetail.getDisbursementDetails().get(i).setDisbSeq(seqNo);
			finDetail.getDisbursementDetails().get(i).setDisbAccountId(finDetail.getFinanceMain().getDisbAccountId());
			finDetail.getDisbursementDetails().get(i).setDisbIsActive(true);
			finDetail.getDisbursementDetails().get(i).setDisbDisbursed(true);
		}
		getFinanceDisbursementDAO().saveList(finDetail.getDisbursementDetails(), tableType, isWIF);

		//Finance Defferment Header Details
		for (int i = 0; i < finDetail.getDefermentHeaders().size(); i++) {
			finDetail.getDefermentHeaders().get(i).setFinReference(finDetail.getFinReference());
		}
		getDefermentHeaderDAO().saveList(finDetail.getDefermentHeaders(), tableType, isWIF);

		//Finance Defferment Details
		for (int i = 0; i < finDetail.getDefermentDetails().size(); i++) {
			finDetail.getDefermentDetails().get(i).setFinReference(finDetail.getFinReference());
		}
		getDefermentDetailDAO().saveList(finDetail.getDefermentDetails(), tableType, isWIF);

		//Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinReference());
		}
		getRepayInstructionDAO().saveList(finDetail.getRepayInstructions(), tableType, isWIF);
		
		logger.debug("Leaving ");
	}
	
	/**
	 * Method for saving List of Fee Charge details
	 * @param finDetail
	 * @param tableType
	 */
	private void saveFeeChargeList(FinScheduleData finScheduleData, String tableType){
		logger.debug("Entering");
		
		if(finScheduleData.getFeeRules() != null && finScheduleData.getFeeRules().size() > 0){
			//Finance Fee Charge Details
			for (int i = 0; i < finScheduleData.getFeeRules().size(); i++) {
				finScheduleData.getFeeRules().get(i).setFinReference(finScheduleData.getFinReference());
			}
			getPostingsDAO().saveChargesBatch(finScheduleData.getFeeRules(), tableType);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Common Method for Finance schedule list validation
	 * 
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 * @throws InterruptedException
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {

			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
				String[] fields = PennantJavaUtil.getFieldDetails(object, excludeFields);

				try {

					rcdType = object.getClass().getMethod("getRecordType", null).invoke(object, null).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					        || rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (!(transType.equals(""))) {
						// check and change below line for Complete code
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses())
						        .invoke(object, object.getClass().getClasses());
						auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i))
						        .getAuditSeq(), fields[0], fields[1], befImg, object));
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

	/**
	 * Method For Preparing List of AuditDetails for Educational expenses
	 * 
	 * @param auditDetails
	 * @param type
	 * @param eduCationLoanID
	 * @return
	 */
	private List<AuditDetail> processingEduExpenseList(List<AuditDetail> auditDetails, String type,
	        String eduCationLoanID) {
		logger.debug("Entering ");
		
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			EducationalExpense educationalExpense = (EducationalExpense) auditDetails.get(i)
			        .getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			educationalExpense.setLoanRefNumber(eduCationLoanID);
			if (type.equals("")) {
				approveRec = true;
				educationalExpense.setVersion(educationalExpense.getVersion() + 1);
				educationalExpense.setRoleCode("");
				educationalExpense.setNextRoleCode("");
				educationalExpense.setTaskId("");
				educationalExpense.setNextTaskId("");
			}

			educationalExpense.setWorkflowId(0);

			if (educationalExpense.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (educationalExpense.isNewRecord()) {
				saveRecord = true;
				if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (educationalExpense.getRecordType().equalsIgnoreCase(
				        PennantConstants.RCD_DEL)) {
					educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (educationalExpense.isNew()) {
					saveRecord = true;
				} else
					updateRecord = true;
			}

			if (approveRec) {
				rcdType = educationalExpense.getRecordType();
				recordStatus = educationalExpense.getRecordStatus();
				educationalExpense.setRecordType("");
				educationalExpense.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {

				educationalExpenseDAO.save(educationalExpense, type);
			}

			if (updateRecord) {
				educationalExpenseDAO.update(educationalExpense, type);
			}

			if (deleteRecord) {
				educationalExpenseDAO.delete(educationalExpense, type);
			}

			if (approveRec) {
				educationalExpense.setRecordType(rcdType);
				educationalExpense.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(educationalExpense);
		}
		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param educationalLoan
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setEduExpenseAuditData(EducationalLoan educationalLoan,
	        String auditTranType, String method) {
		logger.debug("Entering ");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new EducationalExpense());

		for (int i = 0; i < educationalLoan.getEduExpenseList().size(); i++) {

			EducationalExpense educationalExpense = educationalLoan.getEduExpenseList().get(i);
			educationalExpense.setWorkflowId(educationalLoan.getWorkflowId());
			educationalExpense.setLoanRefNumber((educationalLoan.getLoanRefNumber()));

			boolean isRcdType = false;

			if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				educationalExpense.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
				        || educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			educationalExpense.setRecordStatus(educationalLoan.getRecordStatus());
			educationalExpense.setUserDetails(educationalLoan.getUserDetails());
			educationalExpense.setLastMntOn(educationalLoan.getLastMntOn());
			educationalExpense.setLastMntBy(educationalLoan.getLastMntBy());
			if (!educationalExpense.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        educationalExpense.getBefImage(), educationalExpense));
			}

		}
		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * This method fetches list<FinanceReferenceDetail> and 
	 * set the checkListDetails to each FinanceReferenceDetail
	 * 
	 * @param id
	 * @param type
	 * @return finRefDetailList
	 */
	public List<FinanceReferenceDetail> getCheckListByFinRef(final String finType) {
		logger.debug("Entering ");
		List<FinanceReferenceDetail> finRefDetailList = getFinanceReferenceDetailDAO()
		        .getFinanceReferenceDetail(finType, "", "_AQView");
		if (finRefDetailList != null) {
			for (FinanceReferenceDetail finRefDetail : finRefDetailList) {
				finRefDetail.setLovDesccheckListDetail(getCheckListDetailDAO()
				        .getCheckListDetailByChkList(finRefDetail.getFinRefId(), "_AView"));
			}
		}
		logger.debug("Leaving ");
		return finRefDetailList;
	}

	/**
	 * Setting checkList audit details
	 * 
	 * @param finMain
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCheckListAuditData(FinanceDetail financeDetail,
	        String auditTranType, String method) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		for (int i = 0; i < financeDetail.getFinanceCheckList().size(); i++) {
			FinanceCheckListReference finChekListRef = financeDetail.getFinanceCheckList().get(i);
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finChekListRef.getRecordType().equals(PennantConstants.RCD_ADD)) {
					auditTranType = PennantConstants.TRAN_ADD;
					finChekListRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finChekListRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_UPD;
				} else {
					auditTranType = PennantConstants.RCD_DEL;
				}
			}
			if (StringUtils.trimToEmpty(method).equals("doApprove")) {
				finChekListRef.setRecordType(PennantConstants.RCD_ADD);
			}
			finChekListRef.setRecordStatus("");
			finChekListRef.setUserDetails(financeDetail.getFinScheduleData().getFinanceMain().getUserDetails());
			finChekListRef.setLastMntOn(financeDetail.getFinScheduleData().getFinanceMain().getLastMntOn());
			finChekListRef.setLastMntBy(financeDetail.getFinScheduleData().getFinanceMain().getLastMntBy());
			finChekListRef.setWorkflowId(financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId());
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceCheckListReference(),excludeFields);
			if (!StringUtils.equals(finChekListRef.getRecordType().trim(), "")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        finChekListRef.getBefImage(), finChekListRef));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * processing CheckListDetails
	 * 
	 * @param checkList
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processCheckListDetails(List<AuditDetail> auditDetails, String type,
	        String finReference) {
		logger.debug("Entering ");
		
		for (int i = 0; i < auditDetails.size(); i++) {
			FinanceCheckListReference finChecklistRef = (FinanceCheckListReference) auditDetails.get(i).getModelData();
			finChecklistRef.setWorkflowId(0);
			if (type.equals("")) {
				finChecklistRef.setVersion(finChecklistRef.getVersion() + 1);
				finChecklistRef.setRoleCode("");
				finChecklistRef.setNextRoleCode("");
				finChecklistRef.setTaskId("");
				finChecklistRef.setNextTaskId("");
			}
			if (finChecklistRef.getRecordType().equals(PennantConstants.RCD_ADD)) {
				getFinanceCheckListReferenceDAO().save(finChecklistRef, type);
			} else if (finChecklistRef.getRecordType().equals(PennantConstants.RCD_DEL)) {
				getFinanceCheckListReferenceDAO().delete(finChecklistRef, type);
			} else if (finChecklistRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				getFinanceCheckListReferenceDAO().update(finChecklistRef, type);
			}

			auditDetails.get(i).setModelData(finChecklistRef);

		}
		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Method to validate checklist
	 * 
	 * @param auditDetail
	 *            (AuditDetail)
	 * @param usrLanguage
	 *            (String)
	 * @param method
	 *            (String)
	 * @return auditDetail
	 */

	private AuditDetail validation_CheckList(AuditDetail auditDetail, String usrLanguage,
	        String method) {
		logger.debug("Entering ");
		
		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		String[] errParm = new String[3];
		String[] valueParm = new String[1];

		if (financeDetail.getFinRefDetailsList() != null) {
			for (FinanceReferenceDetail aFinRefDetail : financeDetail.getFinRefDetailsList()) {

				if (financeDetail.getLovDescSelAnsCountMap().containsKey(Long.valueOf(aFinRefDetail.getFinRefId()))) {
					if ((financeDetail.getLovDescSelAnsCountMap().get(Long.valueOf(
							aFinRefDetail.getFinRefId()))) > (aFinRefDetail.getLovDescCheckMaxCount())
					        || (financeDetail.getLovDescSelAnsCountMap().get(Long.valueOf(
					        		aFinRefDetail.getFinRefId()))) < (aFinRefDetail.getLovDescCheckMinCount())) {
						
						errParm[0] = "" + aFinRefDetail.getLovDescCheckMinCount();
						errParm[1] = "" + aFinRefDetail.getLovDescCheckMaxCount();
						errParm[2] = aFinRefDetail.getLovDescRefDesc();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						        PennantConstants.KEY_FIELD, "F0001", errParm, valueParm), usrLanguage));
					}
				} else {
					errParm[0] = "" + aFinRefDetail.getLovDescCheckMinCount();
					errParm[1] = "" + aFinRefDetail.getLovDescCheckMaxCount();
					errParm[2] = aFinRefDetail.getLovDescRefDesc();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "F0001", errParm, valueParm), usrLanguage));
				}
			}
		}
		logger.debug("Leaving ");
		return auditDetail;
	}

	/**
	 * Method for Calculate Customer Live Data using Customer Details
	 * 
	 * @param CustomerCalData
	 * @param type
	 * @return FinanceMain
	 */
	@Override
	public CustomerCalData getCalculatedData(CustomerCalData calData, String type) {
		logger.debug("Entering ");
		return getFinanceMainDAO().calculateData(calData, type);
	}

	/**
	 * 
	 * <br>
	 * IN FinanceDetailServiceImpl.java
	 * 
	 * @param financeDetail
	 */
	private void doFillCustEligibilityData(FinanceDetail financeDetail) {
		logger.debug("Entering ");
		
		Customer customer = getCustomerDAO().getCustomerByID(
		        financeDetail.getFinScheduleData().getFinanceMain().getCustID(), "");
		
		if (customer != null) {
			
			// Eligibility object
			financeDetail.setCustomerEligibilityCheck(new CustomerEligibilityCheck());
			BeanUtils.copyProperties(customer, financeDetail.getCustomerEligibilityCheck());
			financeDetail.getCustomerEligibilityCheck().setCustAge(
			        DateUtility.getYearsBetween(customer.getCustDOB(), DateUtility.today()));
			CustomerCalData calData = new CustomerCalData();
			calData.setCustCIF(customer.getCustCIF());
			calData.setCustID(customer.getCustID());
			calData.setFinType(financeDetail.getFinScheduleData().getFinanceMain().getFinType());
			// Set Calculated values for Eligibility
			calData = getCalculatedData(calData, "");
			BeanUtils.copyProperties(calData, financeDetail.getCustomerEligibilityCheck());

			financeDetail.getCustomerEligibilityCheck().setReqFinType(
			        financeDetail.getFinScheduleData().getFinanceMain().getFinType());
			financeDetail.getCustomerEligibilityCheck().setReqFinStartDate(
			        financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate());
			financeDetail.getCustomerEligibilityCheck().setReqFinAmount(
			        financeDetail.getFinScheduleData().getFinanceMain().getFinAmount());
			financeDetail.getCustomerEligibilityCheck().setReqTerms(
			        financeDetail.getFinScheduleData().getFinanceMain().getNumberOfTerms());
			financeDetail.getCustomerEligibilityCheck().setReqFinccy(
			        financeDetail.getFinScheduleData().getFinanceMain().getFinCcy());
			financeDetail.getCustomerEligibilityCheck().setReqFinRepay(
			        financeDetail.getFinScheduleData().getFinanceMain().getFinRepaymentAmount());
			financeDetail.getCustomerEligibilityCheck().setReqMaturity(
			        financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate());
			financeDetail.getCustomerEligibilityCheck().setReqProduct(
			        financeDetail.getFinScheduleData().getFinanceType().getLovDescProductCodeName());
			financeDetail.getCustomerEligibilityCheck().setReqCampaign("");
			financeDetail.getCustomerEligibilityCheck().setCustRepayTot(
			        financeDetail.getCustomerEligibilityCheck().getCustRepayOther().add(financeDetail.getCustomerEligibilityCheck().getCustRepayBank()));
			
			// Scoring object
			financeDetail.setCustomerScoringCheck(new CustomerScoringCheck());
			BeanUtils.copyProperties(financeDetail.getCustomerEligibilityCheck(), financeDetail.getCustomerScoringCheck());
			// Set Calculated values for Scoring
			BeanUtils.copyProperties(calData, financeDetail.getCustomerScoringCheck());

		}
		logger.debug("Leaving ");
	}

	/**
	 * Method to get FinanceCheckListReference By FinReference
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<FinanceCheckListReference> getFinanceCheckListReferenceByFinRef(final String id,
	        String type) {
		return getFinanceCheckListReferenceDAO().getFinanceCheckListReferenceByFinRef(id, type);
	}

	/**
	 * This method prepare audit details for FinanceCheckListReference list and deletes the list
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param auditTranType
	 * @return
	 */
	private List<AuditDetail> listDeletion_CheckList(FinanceDetail finDetail, String tableType,
	        String auditTranType) {
		logger.debug("Entering ");
		
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceCheckListReference(), excludeFields);
		if (finDetail.getFinanceCheckList() != null && finDetail.getFinanceCheckList().size() > 0) {

			for (int i = 0; i < finDetail.getFinanceCheckList().size(); i++) {
				FinanceCheckListReference finCheckListRef = finDetail.getFinanceCheckList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finCheckListRef.getBefImage(), finCheckListRef));
			}
			getFinanceCheckListReferenceDAO().delete(finDetail.getFinanceCheckList().get(0).getFinReference(), tableType);
		}
		logger.debug("Leaving ");
		return auditList;
	}
	
	private List<AuditDetail> listDeletion_FinAgr(FinanceDetail finDetail, String tableType,
			String auditTranType) {
		logger.debug("Entering ");
		
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinAgreementDetail(), "");
		if (finDetail.getFinAgrDetailList() != null && finDetail.getFinAgrDetailList().size() > 0) {
			
			for (int i = 0; i < finDetail.getFinAgrDetailList().size(); i++) {
				FinAgreementDetail finAgrDetail = finDetail.getFinAgrDetailList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finAgrDetail.getBefImage(), finAgrDetail));
			}
			getFinAgreementDetailDAO().deleteByFinRef(finDetail.getFinAgrDetailList().get(0).getFinReference(), tableType);
		}
		logger.debug("Leaving ");
		return auditList;
	}
	
	private List<AuditDetail> listDeletion_FinContributor(FinanceDetail finDetail, String tableType,
			String auditTranType) {
		logger.debug("Entering ");
		
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if(finDetail.getFinContributorHeader() != null){
			String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader(), "");
			FinContributorHeader contributorHeader = finDetail.getFinContributorHeader();
			auditList.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], contributorHeader.getBefImage(), contributorHeader));
			
			getFinContributorHeaderDAO().delete(contributorHeader.getFinReference(), tableType);

			String[] fields1 = PennantJavaUtil.getFieldDetails(new FinContributorDetail(), "");
			if (contributorHeader.getContributorDetailList() != null && contributorHeader.getContributorDetailList().size() > 0) {

				for (int i = 0; i < contributorHeader.getContributorDetailList().size(); i++) {
					FinContributorDetail contributorDetail = contributorHeader.getContributorDetailList().get(i);
					auditList.add(new AuditDetail(auditTranType, i + 1, fields1[0], fields1[1], contributorDetail.getBefImage(), contributorDetail));
				}
				getFinBillingDetailDAO().deleteByFinRef(contributorHeader.getContributorDetailList().get(0).getFinReference(), tableType);
			}
		}
		logger.debug("Leaving ");
		return auditList;
	}
	
	private List<AuditDetail> listDeletion_FinBilling(FinanceDetail finDetail, String tableType,
			String auditTranType) {
		logger.debug("Entering ");
		
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if(finDetail.getFinBillingHeader() != null){
			String[] fields = PennantJavaUtil.getFieldDetails(new FinBillingHeader(), "");
			FinBillingHeader billingHeader = finDetail.getFinBillingHeader();
			auditList.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], billingHeader.getBefImage(), billingHeader));
			getFinBillingHeaderDAO().delete(billingHeader.getFinReference(), tableType);
			
			String[] fields1 = PennantJavaUtil.getFieldDetails(new FinBillingDetail(), "");
			if (billingHeader.getBillingDetailList() != null && billingHeader.getBillingDetailList().size() > 0) {

				for (int i = 0; i < billingHeader.getBillingDetailList().size(); i++) {
					FinBillingDetail billingDetail = billingHeader.getBillingDetailList().get(i);
					auditList.add(new AuditDetail(auditTranType, i + 1, fields1[0], fields1[1], billingDetail.getBefImage(), billingDetail));
				}
				getFinBillingDetailDAO().deleteByFinRef(billingHeader.getBillingDetailList().get(0).getFinReference(), tableType);
			}
		}
		logger.debug("Leaving ");
		return auditList;
	}

	/**
	 * Method for Maintaining List of Schedule Details in Work table by User
	 * 
	 * @param financeScheduleDetail
	 * @param financeScheduleDetails
	 */
	public void maintainWorkSchedules(String finReference, long userId,
	        List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering ");
		getFinanceScheduleDetailDAO().deleteFromWork(finReference, userId);
		for (int i = 0; i < financeScheduleDetails.size(); i++) {
			FinanceScheduleDetail detail = financeScheduleDetails.get(i);
			detail.setFinReference(finReference);
			detail.setLastMntBy(userId);
			getFinanceScheduleDetailDAO().save(detail, "_Work", false);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Get AccountingSet Id based on event code.<br>
	 * 
	 * @param eventCode
	 * @param financeType
	 * @return
	 */
	private String returnAccountingSetid(String eventCode, FinanceType financeType) {
		logger.debug("Entering ");
		
		// Execute entries depend on Finance Event
		String accountingSetId = "";
		if (eventCode.equals("ADDDBSF")) {
			accountingSetId = financeType.getFinAEAddDsbFD();
		} else if (eventCode.equals("ADDDBSN")) {
			accountingSetId = financeType.getFinAEAddDsbFDA();
		} else if (eventCode.equals("ADDDBSP")) {
			accountingSetId = financeType.getFinAEAddDsbOD();
		} else if (eventCode.equals("AMZ")) {
			accountingSetId = financeType.getFinAEAmzNorm();
		} else if (eventCode.equals("AMZSUSP")) {
			accountingSetId = financeType.getFinAEAmzSusp();
		} else if (eventCode.equals("DEFRPY")) {
			accountingSetId = financeType.getFinDefRepay();
		} else if (eventCode.equals("DEFFRQ")) {
			accountingSetId = financeType.getFinDeffreq();
		} else if (eventCode.equals("EARLYPAY")) {
			accountingSetId = financeType.getFinAEEarlyPay();
		} else if (eventCode.equals("EARLYSTL")) {
			accountingSetId = financeType.getFinAEEarlySettle();
		} else if (eventCode.equals("LATEPAY")) {
			accountingSetId = financeType.getFinLatePayRule();
		} else if (eventCode.equals("M_AMZ")) {
			accountingSetId = financeType.getFinToAmz();
		} else if (eventCode.equals("M_NONAMZ")) {
			accountingSetId = financeType.getFinAEToNoAmz();
		} else if (eventCode.equals("RATCHG")) {
			accountingSetId = financeType.getFinAERateChg();
		} else if (eventCode.equals("REPAY")) {
			accountingSetId = financeType.getFinAERepay();
		} else if (eventCode.equals("WRITEOFF")) {
			accountingSetId = financeType.getFinAEWriteOff();
		} else if (eventCode.equals("SCDCHG")) {
			accountingSetId = financeType.getFinSchdChange();
		}
		
		logger.debug("Leaving");
		return accountingSetId;
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public FinScheduleData getFinSchDataByFinRef(String finReference, String type) {
		logger.debug("Entering");
		
		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));
		finSchData.setDefermentHeaders(getDefermentHeaderDAO().getDefermentHeaders(finReference, type, false));
		finSchData.setDefermentDetails(getDefermentDetailDAO().getDefermentDetails(finReference, type, false));
		finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(finSchData.getFinanceMain().getFinType(), type));
		finSchData.setFeeRules(getPostingsDAO().getFeeChargesByFinRef(finReference, ""));
		finSchData.setRepayDetails(getFinanceRepaymentsByFinRef(finReference));
		
		logger.debug("Leaving");
		return finSchData;
	}
	
	/**
	 * Method to get Schedule related data.
	 * 
	 * @param financeReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public FinScheduleData getFinSchDataById(String financeReference, String type) {
		logger.debug("Entering");
		
		FinScheduleData finSchData = new FinScheduleData();
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(financeReference, type, false);
		finSchData.setFinanceMain(financeMain);
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(financeReference, type, false));
		
		//Finance Summary Details Preparation
		final Date curBussDate =  (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		FinanceSummary summary = new FinanceSummary();
		summary.setFinReference(financeMain.getFinReference());
		summary.setSchDate(curBussDate);
		
		if(financeMain.isAllowGrcPeriod() && curBussDate.compareTo(financeMain.getNextGrcPftDate()) <= 0){
			summary.setNextSchDate(financeMain.getNextGrcPftDate());
		}else if(financeMain.getNextRepayDate().compareTo(financeMain.getNextRepayPftDate()) < 0) {
			summary.setNextSchDate(financeMain.getNextRepayDate());
		}else {
			summary.setNextSchDate(financeMain.getNextRepayPftDate());
		}
		
		summary = getFinanceScheduleDetailDAO().getFinanceSummaryDetails(summary);
		summary = getPostingsDAO().getTotalFeeCharges(summary);
		finSchData.setFinanceSummary(summary);
		
		logger.debug("Leaving");
		return finSchData;
	}
	
	/**
	 * Method for getting Fiannce Contributo Header Details
	 */
	public FinContributorHeader getFinContributorHeaderById(String finReference) {
		logger.debug("Entering");
		FinContributorHeader header = getFinContributorHeaderDAO().getFinContributorHeaderById(finReference, "_AView");
		if(header != null){
			header.setContributorDetailList(getFinContributorDetailDAO().getFinContributorDetailByFinRef(finReference, "_AView"));
		}
		logger.debug("Leaving");
		return header;
	}
	/**
	 * Method to save profit details
	 * 
	 * @param finScheduleData
	 * */
	private FinanceProfitDetail doSave_PftDetails(AEAmountCodes aeAmountCodes, FinanceMain financeMain) {
		logger.debug("Entering");
		// Save/Update Finance Profit Details
		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();

		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");

		finProfitDetails.setFinReference(financeMain.getFinReference());
		finProfitDetails.setCustId(financeMain.getCustID());
		finProfitDetails.setFinBranch(financeMain.getFinBranch());
		finProfitDetails.setFinType(financeMain.getFinType());

		finProfitDetails = getFinanceProfitDetailFiller().prepareFinPftDetails(aeAmountCodes, finProfitDetails, curBDay);
		if (getProfitDetailsDAO().getFinProfitDetailsById(financeMain.getFinReference()) == null) {
			getProfitDetailsDAO().save(finProfitDetails);
		} else {
			getProfitDetailsDAO().update(finProfitDetails);
		}
		logger.debug("Leaving");
		return finProfitDetails;
	}

	/**
	 * Method for fetching Agreement Details
	 */
	@Override
	public AgreementDetail getAgreementDetail(FinanceMain main, HashMap<String, Object> extendedFields) {
		logger.debug("Entering");
		
		AgreementDetail detail = new AgreementDetail();
		detail.setFinanceStartDate(DateUtility.formatUtilDate(main.getFinStartDate(), PennantConstants.dateFormate));
		detail.setDisbursementAmt(PennantApplicationUtil.amountFormate(main.getFinAmount(), main.getLovDescFinFormatter()));
		detail.setDisbursementAccount(main.getDisbAccountId());
		if (main.getFinContractDate() != null) {
			detail.setDayOfContarctDate(Integer.toString(DateUtility.getDay(main.getFinContractDate())));
			detail.setMonthOfContarctDate(new SimpleDateFormat("MMMM").format(main.getFinContractDate()));
			detail.setYearOfContarctDate(Integer.toString(DateUtility.getYear(main.getFinContractDate())));
		}
		detail.setCustSalutation(StringUtils.trimToEmpty(main.getLovDescSalutationName()));
		detail.setCustFullName(StringUtils.trimToEmpty(main.getLovDescCustFName()) + " "
		        + StringUtils.trimToEmpty(main.getLovDescCustLName()));
		detail.setCustCIF(main.getLovDescCustCIF());
		if (SystemParameterDetails.getSystemParameterValue("BANK_NAME") != null) {
			detail.setBankName(SystemParameterDetails.getSystemParameterValue("BANK_NAME").toString());
		}
		detail.setFinCcy(main.getFinCcy());
		detail.setFinAmount(PennantApplicationUtil.amountFormate(main.getFinAmount(), main.getLovDescFinFormatter()));
		if (main.getFinContractDate() != null) {
			detail.setContractDate(new SimpleDateFormat(PennantConstants.dateFormat).format(main.getFinContractDate()));
		}
		if (extendedFields.containsKey("AgentName")) {
			detail.setAgentName(extendedFields.get("AgentName").toString());
			if (extendedFields.get("AgentLoc") != null) {
				detail.setAgentAddr1(extendedFields.get("AgentLoc").toString());
			}
			if (extendedFields.get("AgentLoc2") != null) {
				detail.setAgentAddr2(extendedFields.get("AgentLoc2").toString());
			}
			if (extendedFields.get("AgentCity") != null) {
				detail.setAgentCity(extendedFields.get("AgentCity").toString());
			}
			if (extendedFields.get("AgentCountry") != null) {
				detail.setAgentCountry(extendedFields.get("AgentCountry").toString());
			}
		} else {
			detail.setAgentName(detail.getCustFullName() == null ? "" : detail.getCustFullName());
			detail.setAgentAddr1(main.getLovDescCustAddrLine1() == null ? "" : main
			        .getLovDescCustAddrLine1());
			detail.setAgentAddr2(main.getLovDescCustAddrLine2() == null ? "" : main
			        .getLovDescCustAddrLine2());
			detail.setAgentCity(main.getLovDescCustAddrCity() == null ? "" : main
			        .getLovDescCustAddrCity());
			detail.setAgentCountry(main.getLovDescCustAddrCountry() == null ? "" : main
			        .getLovDescCustAddrCountry());
		}
		logger.debug("Leaving");
		return detail;
	}

	/**
	 * Method for Generating Agreement Details List
	 * @param detail
	 * @param main
	 * @return
	 */
	private boolean generateAgreements(FinanceDetail detail, FinanceMain main) {
		logger.debug("Entering");

		boolean result = false;
		List<FinanceReferenceDetail> list = detail.getAggrementList();
		FinanceReferenceDetail data = null;
		TemplateEngine engine = null;
		String name = "";

		if (list != null && list.size() > 0) {
			try {
				AgreementDetail agreement = getAgreementDetail(main, detail.getLovDescExtendedFieldValues());
				engine = new TemplateEngine();
				engine.setDocumentSite(SystemParameterDetails.getSystemParameterValue("FINANCE_AGREEMENTS_PDF_PATH").toString());
				
				for (int i = 0; i < list.size(); i++) {
					data = list.get(i);
					name = main.getFinReference() + "_" + data.getLovDescCodelov() + ".pdf";
					if (PennantConstants.server_OperatingSystem.equals("WINDOWS")) {
						engine.setTemplateSite(data.getLovDescAggReportPath());
					} else {
						engine.setDocumentSite(SystemParameterDetails.getSystemParameterValue("LINUX_AGREEMENTS_PDF_PATH").toString());
						engine.setTemplateSite(SystemParameterDetails.getSystemParameterValue("LINUX_AGGREMENTS_TEMPLATES_PATH").toString());
					}
					engine.setTemplate(data.getLovDescAggReportName());
					engine.loadTemplate();
					engine.mergeFields(agreement);
					engine.saveDocument(name, data.isLovDescRegenerate());
				}

				result = true;
			} catch (Exception exception) {
				logger.error(exception);
			} finally {
				if (engine != null) {
					engine.close();
					engine = null;
				}
			}
		}

		logger.debug("Leaving");
		return result;
	}

	@Override
    public List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent,
            boolean showZeroBal) {
		return getPostingsDAO().getPostingsByFinRefAndEvent(finReference, finEvent, showZeroBal);
    }
	
	
	/**
	 * Method to CheckLimits
	 * 
	 * @param AuditHeader
	 * 
	 * 1. Check limit category exists or not for the account type, if not exists set limitValid = true other
	 *            wise goto next step. 
	 * 2. Fetch customer limits from core banking. 
	 * 3. If the limits not available set the ErrMessage. 
	 * 4. If available limit is less than finance amount, set warning message if the user
	 *            have the permission 'override Limits' otherwise set Error message.
	 * 
	 * */
	public AuditHeader doCheckLimits(AuditHeader auditHeader) {
		logger.debug("Entering");

		CustomerLimit custLimit = null;
		String[] errParm = new String[2];
		String[] valueParm = new String[2];
			
		List<CustomerLimit> list = null;
		
		FinanceDetail finDetails =  (FinanceDetail)auditHeader.getAuditDetail().getModelData();				
		AccountType accountType = getAccountTypeDAO().getAccountTypeById(finDetails.getFinScheduleData().getFinanceType().getFinAcType(), "");  
		
		if(!StringUtils.trimToEmpty(accountType.getAcLmtCategory()).equals("")) {
			custLimit = new CustomerLimit();
			custLimit.setCustMnemonic(finDetails.getFinScheduleData().getFinanceMain().getLovDescCustCIF());
			custLimit.setLimitCategory(accountType.getAcLmtCategory());
			custLimit.setCustLocation(" ");
		} else {
			finDetails.getFinScheduleData().getFinanceMain().setLimitValid(true);
        	auditHeader.getAuditDetail().setModelData(finDetails);
        	logger.debug("Leaving");
        	return auditHeader;
		}
		
		try{
			list = getCustLimitIntefaceService().fetchLimitDetails(custLimit);
		} catch (Exception e) {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, e.getMessage(), null));
			logger.debug("Exception "+e.getMessage());
			logger.debug("Leaving");
			return auditHeader;
		}
		
		if(list != null && list.size() == 0) {
			valueParm[0] = accountType.getAcLmtCategory();
			errParm[0] = PennantJavaUtil.getLabel("label_LimtCategorys")+ ":" +valueParm[0];

			valueParm[1] = finDetails.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
			errParm[1] = "For " + PennantJavaUtil.getLabel("label_IdCustID")+ ":" + valueParm[1];
			
			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails("Limit","41002", errParm,valueParm),
					finDetails.getUserDetails().getUsrLanguage() ));
			logger.debug("Leaving");
			return auditHeader;
		} else {	
			for (CustomerLimit customerLimit:list) {								
	            if(customerLimit.getLimitAmount() == null || customerLimit.getLimitAmount().equals(new BigDecimal(0))){
	    			valueParm[0] = customerLimit.getLimitCategory();
	    			errParm[0] = PennantJavaUtil.getLabel("label_LimtCategory")+ ":" +valueParm[0];
	    			valueParm[1] = finDetails.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
	    			errParm[1] = "For " + PennantJavaUtil.getLabel("label_IdCustID")+ ":" + valueParm[1];
	    			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails("Limit","41002", errParm,valueParm),
	    					finDetails.getUserDetails().getUsrLanguage() ));
	            	return auditHeader;
	            	
	            }else {           	
	            	
	            	Currency fCurrency = getCurrencyDAO().getCurrencyById(finDetails.getFinScheduleData().getFinanceMain().getFinCcy(), "");
	            	BigDecimal finAmount = calculateExchangeRate(finDetails.getFinScheduleData().getFinanceMain().getFinAmount(),fCurrency);
	            	
	            	Currency lCurrency = null;
					if(!StringUtils.trimToEmpty(customerLimit.getLimitCurrency()).equals(fCurrency.getCcyCode())){
						lCurrency = getCurrencyDAO().getCurrencyById(customerLimit.getLimitCurrency(), "");
					}else{
						lCurrency = fCurrency;
					}
	            	
	            	BigDecimal availAmount =  calculateExchangeRate(customerLimit.getAvailAmount(),lCurrency);
					
	            	if(availAmount != null && availAmount.compareTo(finAmount)<0 ){
	            		valueParm[0] = PennantApplicationUtil.amountFormate(finDetails.getFinScheduleData().getFinanceMain().getFinAmount(),
	            				lCurrency.getCcyEditField() == 0 ? PennantConstants.defaultCCYDecPos : lCurrency.getCcyEditField());
	            		errParm[0] = valueParm[0];
	            		valueParm[1] = PennantApplicationUtil.amountFormate(customerLimit.getLimitAmount(), 
	            				fCurrency.getCcyEditField() == 0 ? PennantConstants.defaultCCYDecPos : fCurrency.getCcyEditField());;
	            		errParm[1] = valueParm[1];
	            		String errorCode = "E0035";
	            		if(finDetails.getFinScheduleData().getFinanceType().isOverrideLimit()){
	            			errorCode = "W0035";
	            		}
	            		auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails("Limit",errorCode, errParm,valueParm),
	            				finDetails.getUserDetails().getUsrLanguage() ));
	            		logger.debug("Leaving");
	            		return auditHeader;
	            	}
	            }
            }	
			finDetails.getFinScheduleData().getFinanceMain().setLimitValid(true);
    		auditHeader.getAuditDetail().setModelData(finDetails);
		}
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * Method for Calculating Exchange Rate for Fiannce Schedule Calculation
	 * @param amount
	 * @param aCurrency
	 * @return
	 */
	private BigDecimal calculateExchangeRate(BigDecimal amount, Currency aCurrency) {
		if(SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR").equals(aCurrency.getCcyCode())) {
			return amount;
		} else {
			if (amount == null) {
				amount = new BigDecimal("0");
			}
			
			if (aCurrency != null) {
				amount = amount.multiply(aCurrency.getCcySpotRate());
			}
		}
		return amount;
	}
	
	// Finance Agreement Details List Maintainance

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * @param agreementDetail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setAgreementDetailAuditData(FinanceDetail detail,String auditTranType,String method) {
		logger.debug("Leaving");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinAgreementDetail(""));
		
		List<FinAgreementDetail> oldAgrDetailList = null;
		if(method.equals("doApprove")){
			String agrIds = "";
			for (FinAgreementDetail agrDetail : detail.getFinAgrDetailList()) {
				agrIds = agrIds+agrDetail.getAgrId()+",";
			}
			
			if(agrIds.endsWith(",")){
				agrIds = agrIds.substring(0, agrIds.length()-1);
			}

			oldAgrDetailList = getFinAgreementDetailDAO().getFinAgreementDetailList(
					detail.getFinScheduleData().getFinReference(),
					detail.getFinScheduleData().getFinanceType().getFinType(),false,agrIds, "_Temp");

			if(detail.getFinAgrDetailList() != null){
				detail.getFinAgrDetailList().addAll(oldAgrDetailList);
			}else{
				detail.setFinAgrDetailList(oldAgrDetailList);
			}
		}

		for (int i = 0; i < detail.getFinAgrDetailList().size(); i++) {

			FinAgreementDetail agreementDetail = detail.getFinAgrDetailList().get(i);
			agreementDetail.setWorkflowId(detail.getWorkflowId());
			if (agreementDetail.getAgrId() <= 0) {
				agreementDetail.setFinReference(agreementDetail.getFinReference());
			}

			boolean isRcdType= false;

			if (agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				agreementDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType=true;
			}else if (agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				agreementDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType=true;
			}else if (agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				agreementDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType=true;
			}

			if(method.equals("saveOrUpdate") && (isRcdType==true)){
				agreementDetail.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}

			agreementDetail.setRecordStatus(detail.getFinScheduleData().getFinanceMain().getRecordStatus());
			agreementDetail.setUserDetails(detail.getFinScheduleData().getFinanceMain().getUserDetails());
			agreementDetail.setLastMntOn(detail.getFinScheduleData().getFinanceMain().getLastMntOn());

			if(!agreementDetail.getRecordType().equals("")){
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], agreementDetail.getBefImage(), agreementDetail));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Agreement Details
	 * @param auditDetails
	 * @param type
	 * @param AgrId
	 * @return
	 */
	private List<AuditDetail> processingAgrDetailList(List<AuditDetail> auditDetails, String type,String finReference) {
		logger.debug("Entering");
		
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinAgreementDetail agreementDetail = (FinAgreementDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec=false;
			String rcdType ="";	
			String recordStatus ="";
			if (type.equals("")) {
				approveRec=true;
				agreementDetail.setRoleCode("");
				agreementDetail.setNextRoleCode("");
				agreementDetail.setTaskId("");
				agreementDetail.setNextTaskId("");
			}

			agreementDetail.setWorkflowId(0);

			if (agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(agreementDetail.isNewRecord()){
				saveRecord=true;
				if (agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					agreementDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					agreementDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					agreementDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			}else if (agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (agreementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if(approveRec){
					deleteRecord=true;
				}else if(agreementDetail.isNew()){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}
			if(approveRec){
				rcdType= agreementDetail.getRecordType();
				recordStatus = agreementDetail.getRecordStatus();
				agreementDetail.setRecordType("");
				agreementDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if (StringUtils.trimToEmpty(agreementDetail.getFinReference()).equals("")) {
					agreementDetail.setFinReference(finReference);
				}
				finAgreementDetailDAO.save(agreementDetail, type);
			}

			if (updateRecord) {
				finAgreementDetailDAO.update(agreementDetail, type);
			}

			if (deleteRecord) {
				finAgreementDetailDAO.delete(agreementDetail, type);
			}

			if(approveRec){
				agreementDetail.setRecordType(rcdType);
				agreementDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(agreementDetail);
		}
		logger.debug("Leaving");
		return auditDetails;

	}

	//Contributor Details List Maintainance
	
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * @param contributorHeader
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setContributorAuditData(FinContributorHeader contributorHeader,String auditTranType,String method) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorDetail(""));

		for (int i = 0; i < contributorHeader.getContributorDetailList().size(); i++) {

			FinContributorDetail contributorDetail = contributorHeader.getContributorDetailList().get(i);
			contributorDetail.setWorkflowId(contributorHeader.getWorkflowId());
			if (contributorDetail.getCustID() <= 0) {
				contributorDetail.setFinReference(contributorHeader.getFinReference());
			}

			boolean isRcdType= false;

			if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType=true;
			}else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType=true;
			}else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType=true;
			}

			if(method.equals("saveOrUpdate") && (isRcdType==true)){
				contributorDetail.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}

			contributorDetail.setRecordStatus(contributorHeader.getRecordStatus());
			contributorDetail.setUserDetails(contributorHeader.getUserDetails());
			contributorDetail.setLastMntOn(contributorHeader.getLastMntOn());

			if(!contributorDetail.getRecordType().equals("")){
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], contributorDetail.getBefImage(), contributorDetail));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Contributor Details
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingContributorList(List<AuditDetail> auditDetails, String type,String finReference) {
		logger.debug("Entering");
		
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinContributorDetail contributorDetail = (FinContributorDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec=false;
			String rcdType ="";	
			String recordStatus ="";
			if (type.equals("")) {
				approveRec=true;
				contributorDetail.setRoleCode("");
				contributorDetail.setNextRoleCode("");
				contributorDetail.setTaskId("");
				contributorDetail.setNextTaskId("");
			}

			contributorDetail.setWorkflowId(0);

			if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(contributorDetail.isNewRecord()){
				saveRecord=true;
				if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			}else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if(approveRec){
					deleteRecord=true;
				}else if(contributorDetail.isNew()){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}
			if(approveRec){
				rcdType= contributorDetail.getRecordType();
				recordStatus = contributorDetail.getRecordStatus();
				contributorDetail.setRecordType("");
				contributorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if (StringUtils.trimToEmpty(contributorDetail.getFinReference()).equals("")) {
					contributorDetail.setFinReference(finReference);
				}
				finContributorDetailDAO.save(contributorDetail, type);
			}

			if (updateRecord) {
				finContributorDetailDAO.update(contributorDetail, type);
			}

			if (deleteRecord) {
				finContributorDetailDAO.delete(contributorDetail, type);
			}

			if(approveRec){
				contributorDetail.setRecordType(rcdType);
				contributorDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(contributorDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}
	

	//Billing Details List Maintainance
	
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * @param billingHeader
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setBillingAuditData(FinBillingHeader billingHeader,String auditTranType,String method) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinBillingDetail());

		for (int i = 0; i < billingHeader.getBillingDetailList().size(); i++) {

			FinBillingDetail billingDetail = billingHeader.getBillingDetailList().get(i);
			billingDetail.setWorkflowId(billingHeader.getWorkflowId());
			billingDetail.setFinReference(billingHeader.getFinReference());

			boolean isRcdType= false;

			if (billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				billingDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType=true;
			}else if (billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				billingDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType=true;
			}else if (billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				billingDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType=true;
			}

			if(method.equals("saveOrUpdate") && (isRcdType==true)){
				billingDetail.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}

			billingDetail.setRecordStatus(billingHeader.getRecordStatus());
			billingDetail.setUserDetails(billingHeader.getUserDetails());
			billingDetail.setLastMntOn(billingHeader.getLastMntOn());

			if(!billingDetail.getRecordType().equals("")){
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], billingDetail.getBefImage(), billingDetail));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Billing Details
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingBillingList(List<AuditDetail> auditDetails, String type,String finReference) {
		logger.debug("Entering");
		
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinBillingDetail billingDetail = (FinBillingDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec=false;
			String rcdType ="";	
			String recordStatus ="";
			if (type.equals("")) {
				approveRec=true;
				billingDetail.setRoleCode("");
				billingDetail.setNextRoleCode("");
				billingDetail.setTaskId("");
				billingDetail.setNextTaskId("");
			}

			billingDetail.setWorkflowId(0);

			if (billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(billingDetail.isNewRecord()){
				saveRecord=true;
				if (billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					billingDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					billingDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					billingDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			}else if (billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (billingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if(approveRec){
					deleteRecord=true;
				}else if(billingDetail.isNew()){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}
			if(approveRec){
				rcdType= billingDetail.getRecordType();
				recordStatus = billingDetail.getRecordStatus();
				billingDetail.setRecordType("");
				billingDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if (StringUtils.trimToEmpty(billingDetail.getFinReference()).equals("")) {
					billingDetail.setFinReference(finReference);
				}
				finBillingDetailDAO.save(billingDetail, type);
			}

			if (updateRecord) {
				finBillingDetailDAO.update(billingDetail, type);
			}

			if (deleteRecord) {
				finBillingDetailDAO.delete(billingDetail, type);
			}

			if(approveRec){
				billingDetail.setRecordType(rcdType);
				billingDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(billingDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	@Override
    public void updateCustCIF(long custID, String finReference) {
	   getFinanceMainDAO().updateCustCIF(custID, finReference);
	    
    }
	
	//Document Details List Maintainance

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setDocumentDetailsAuditData(FinanceDetail detail,
	        String auditTranType, String method) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new DocumentDetails());

		for (int i = 0; i < detail.getDocumentDetailsList().size(); i++) {

			DocumentDetails documentDetails = detail.getDocumentDetailsList().get(i);
			documentDetails.setWorkflowId(detail.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				documentDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
				        || documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			documentDetails.setRecordStatus(detail.getFinScheduleData().getFinanceMain().getRecordStatus());
			documentDetails.setUserDetails(detail.getFinScheduleData().getFinanceMain().getUserDetails());
			documentDetails.setLastMntOn(detail.getFinScheduleData().getFinanceMain().getLastMntOn());

			if (!documentDetails.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        documentDetails.getBefImage(), documentDetails));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Contributor Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails,
	        String type, String finReference) {
		logger.debug("Entering");
		
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (type.equals("")) {
				approveRec = true;
				documentDetails.setRoleCode("");
				documentDetails.setNextRoleCode("");
				documentDetails.setTaskId("");
				documentDetails.setNextTaskId("");
			}

			documentDetails.setWorkflowId(0);

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (documentDetails.isNewRecord()) {
				saveRecord = true;
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (documentDetails.getRecordType().equalsIgnoreCase(
				        PennantConstants.RCD_DEL)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (documentDetails.getRecordType().equalsIgnoreCase(
				        PennantConstants.RCD_UPD)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (documentDetails.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (documentDetails.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (documentDetails.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = documentDetails.getRecordType();
				recordStatus = documentDetails.getRecordStatus();
				documentDetails.setRecordType("");
				documentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if (StringUtils.trimToEmpty(documentDetails.getReferenceId()).equals("")) {
					documentDetails.setReferenceId(finReference);
				}
				getDocumentDetailsDAO().save(documentDetails, type);
			}

			if (updateRecord) {
				getDocumentDetailsDAO().update(documentDetails, type);
			}

			if (deleteRecord) {
				getDocumentDetailsDAO().delete(documentDetails, type);
			}

			if (approveRec) {
				documentDetails.setRecordType(rcdType);
				documentDetails.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(documentDetails);
		}
		logger.debug("Leaving");
		return auditDetails;

	}

	public void listDocDeletion(FinanceDetail custDetails, String tableType) {
		getDocumentDetailsDAO().deleteList(new ArrayList<DocumentDetails>(custDetails.getDocumentDetailsList()), tableType);
	}

	@Override
    public List<FinAgreementDetail> getFinAgrByFinRef(String finReference, String type) {
	    return getFinAgreementDetailDAO().getFinAgrByFinRef(finReference, type);
    }

	@Override
    public List<DocumentDetails> getFinDocByFinRef(String finReference, String type) {
	    return getDocumentDetailsDAO().getDocDetailsByFinRef(finReference, type);
    }

	@Override
    public FinAgreementDetail getFinAgrDetailByAgrId(String finReference,long agrId) {
	    return getFinAgreementDetailDAO().getFinAgreementDetailById(finReference, agrId, "_AView");
    }

	@Override
    public DocumentDetails getFinDocDetailByDocId(long docId) {
	    return getDocumentDetailsDAO().getDocumentDetailsById(docId, "");
    }
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ Agreement Details ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * Method for Save/ Update Additional Field Details
	 */
	private void doSaveAddlFieldDetails(FinanceDetail financeDetail, String tableType){
		logger.debug("Entering");
		
		if (financeDetail.getLovDescExtendedFieldValues() != null && financeDetail.getLovDescExtendedFieldValues().size() > 0) {
			
			String tableName = "";
			if(PennantStaticListUtil.getModuleName().containsKey("FASSET")){
				tableName = PennantStaticListUtil.getModuleName().get("FASSET").get(
						financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName());
			}

			if (!getExtendedFieldDetailDAO().isExist(tableName, 
					financeDetail.getFinScheduleData().getFinReference(), tableType)) {
				getExtendedFieldDetailDAO().saveAdditional(financeDetail.getFinScheduleData().getFinReference(),
						financeDetail.getLovDescExtendedFieldValues(), tableType, tableName);
			} else {
				getExtendedFieldDetailDAO().updateAdditional(financeDetail.getLovDescExtendedFieldValues(),
						financeDetail.getFinScheduleData().getFinReference(), tableType,tableName);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Delete Additional Field Details
	 */
	private void doDeleteAddlFieldDetails(FinanceDetail financeDetail, String tableType){
		logger.debug("Entering");
		
		if (financeDetail.getLovDescExtendedFieldValues() != null && financeDetail.getLovDescExtendedFieldValues().size() > 0) {
			
			String tableName = "";
			if(PennantStaticListUtil.getModuleName().containsKey("FASSET")){
				tableName = PennantStaticListUtil.getModuleName().get("FASSET").get(
						financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName());
			}
			
			getExtendedFieldDetailDAO().deleteAdditional(financeDetail.getFinScheduleData().getFinReference(),
					tableName,tableType);
		}
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++ Cust Related Finance Details ++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public FinanceDetail fetchFinCustDetails(FinanceDetail financeDetail, String ctgType,
			String finType,  String userRole, List<String> groupIds){
		logger.debug("Entering");
		
		String eventCode = "";
		if (financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate().after(new Date())) {
			eventCode = "ADDDBSF";
		} else {
			eventCode = "ADDDBSP";
		}
		String accSetId = returnAccountingSetid(eventCode, financeDetail.getFinScheduleData().getFinanceType());

		//Finance Accounting Posting Details
		financeDetail.setTransactionEntries(getTransactionEntryDAO().getListTransactionEntryById(
				Long.valueOf(accSetId), "_AEView", true));

		//Finance Stage Accounting Posting Details
		financeDetail.setStageTransactionEntries(getTransactionEntryDAO().getListTransactionEntryByRefType(
				finType, "5", userRole, "_AEView", true));
		
		//Finance Eligibility Rule Details List
		financeDetail.setEligibilityRuleList(getFinanceReferenceDetailDAO().getFinRefDetByRoleAndFinType(
				finType, userRole, null, "_AEView"));
		
		//Finance Scoring Details
		financeDetail = fetchScoringDetails(financeDetail, ctgType, finType, userRole,groupIds);
		
		//Check List Details
		financeDetail.setCheckList(getCheckListByFinRef(finType));
		financeDetail.setFinanceCheckList(getFinanceCheckListReferenceDAO()
		        .getFinanceCheckListReferenceByFinRef(financeDetail.getFinScheduleData().getFinReference(), "_View"));

		logger.debug("Leaving");
		return financeDetail;
	}
	
	/**
	 * Method for Fetching Scoring Module Details based Upon Customer Category type Code
	 * @param financeDetail
	 * @param finType
	 * @param userRole
	 * @return
	 */
	private FinanceDetail fetchScoringDetails(FinanceDetail financeDetail, String ctgType,
			String finType,  String userRole, List<String> groupIds){
		logger.debug("Entering");
		
		financeDetail.getScoringMetrics().clear();
		financeDetail.getScoringSlabs().clear();
		financeDetail.setScoringGroupList(null);
		financeDetail.setFinScoringMetricList(null);
		financeDetail.setNonFinScoringMetricList(null);
		
		//Finance Scoring Metric/Group Rule Details List
		if("I".equals(ctgType)){
			financeDetail.setScoringGroupList(getFinanceReferenceDetailDAO().getFinRefDetByRoleAndFinType(
					finType, userRole,groupIds, "_ASGView"));

			if (financeDetail.getScoringGroupList().size() != 0) {
				for (int i = 0; i < financeDetail.getScoringGroupList().size(); i++) {
					
					//Scoring Slab Details List
					List<ScoringSlab> scoringSlabsList = getScoringSlabDAO().getScoringSlabsByScoreGrpId(
							financeDetail.getScoringGroupList().get(i).getFinRefId(), "_AView");
					
					financeDetail.setScoringSlabs(financeDetail.getScoringGroupList().get(i).getFinRefId(), 
							scoringSlabsList);

					//Scoring Metric Details For Retail Customers
					List<ScoringMetrics> scoringMetricslist = getScoringMetricsDAO().getScoringMetricsByScoreGrpId(
							financeDetail.getScoringGroupList().get(i).getFinRefId(), "R", "_AView");
					
					financeDetail.setScoringMetrics(financeDetail.getScoringGroupList().get(i).getFinRefId(),
							scoringMetricslist);
					
				}
			}
			
		}else if("C".equals(ctgType)){
			
			List<FinanceReferenceDetail> scoringGroupList = getFinanceReferenceDetailDAO().getFinRefDetByRoleAndFinType(
					finType, userRole, groupIds, "_ACSGView");
			
			financeDetail.setScoringGroupList(scoringGroupList);

			if (scoringGroupList.size() != 0) {
				
				for (int i = 0; i < scoringGroupList.size(); i++) {
					
					//Scoring Slab Details List
					List<ScoringSlab> scoringSlabsList = getScoringSlabDAO().getScoringSlabsByScoreGrpId(
							scoringGroupList.get(i).getFinRefId(), "_AView");
					
					financeDetail.setScoringSlabs(scoringGroupList.get(i).getFinRefId(), scoringSlabsList);

					//Corporate Scoring Group for Financial Details
					List<ScoringMetrics> finScoringMetricList = getScoringMetricsDAO().getScoringMetricsByScoreGrpId(
							scoringGroupList.get(i).getFinRefId(), "F", "_AView");
					
					financeDetail.setFinScoringMetricList(finScoringMetricList);
					if(finScoringMetricList != null && finScoringMetricList.size() > 0){

						List<Long> metricIdList = new ArrayList<Long>();
						for (ScoringMetrics metric : finScoringMetricList) {
							metricIdList.add(metric.getScoringId());
						}

						List<Rule> ruleList = getRuleDAO().getRulesByFinScoreGroup(metricIdList, "F", "");
						ScoringMetrics metric = null;
						for (Rule rule : ruleList) {
							metric = new ScoringMetrics();
							metric.setScoringId(rule.getRuleId());
							metric.setLovDescScoringCode(rule.getRuleCode());
							metric.setLovDescScoringCodeDesc(rule.getRuleCodeDesc());
							metric.setLovDescSQLRule(rule.getSQLRule());
							List<ScoringMetrics> subMetricList = null;
							if(financeDetail.getScoringMetrics().containsKey(rule.getGroupId())){
								subMetricList = financeDetail.getScoringMetrics().get(rule.getGroupId());
							}else{
								subMetricList = new ArrayList<ScoringMetrics>();
							}
							subMetricList.add(metric);
							financeDetail.setScoringMetrics(rule.getGroupId(),subMetricList);
						}
					}
					
					//Non - Financial Scoring Metric Details
					List<ScoringMetrics> nonFinScoringMetricslist = getScoringMetricsDAO().getScoringMetricsByScoreGrpId(
							scoringGroupList.get(i).getFinRefId(), "N" ,"_AView");
					
					financeDetail.setNonFinScoringMetricList(nonFinScoringMetricslist);
					if(nonFinScoringMetricslist != null && nonFinScoringMetricslist.size() > 0){
						
						List<Long> metricIdList = new ArrayList<Long>();
						for (ScoringMetrics metric : nonFinScoringMetricslist) {
							metricIdList.add(metric.getScoringId());
						}
						
						List<NFScoreRuleDetail> ruleList = getRuleDAO().getNFRulesByNFScoreGroup(metricIdList, "N", "");
						ScoringMetrics metric = null;
						for (NFScoreRuleDetail rule : ruleList) {
							metric = new ScoringMetrics();
							metric.setScoringId(rule.getNFRuleId());
							metric.setLovDescScoringCode(String.valueOf(rule.getNFRuleId()));
							metric.setLovDescScoringCodeDesc(rule.getNFRuleDesc());
							metric.setLovDescMetricMaxPoints(rule.getMaxScore());
							List<ScoringMetrics> subMetricList = null;
							if(financeDetail.getScoringMetrics().containsKey(rule.getGroupId())){
								subMetricList = financeDetail.getScoringMetrics().get(rule.getGroupId());
							}else{
								subMetricList = new ArrayList<ScoringMetrics>();
							}
							subMetricList.add(metric);
							financeDetail.setScoringMetrics(rule.getGroupId(),subMetricList);
						}
					}
				}
			}
		}
		
		logger.debug("Leaving");
		return financeDetail;
	}
	
	/**
	 * Method for Fetching Scoring Metric List
	 */
	@Override
	public List<ScoringMetrics> getScoringMetricsList(long id, String type) {
		return getScoringMetricsDAO().getScoringMetricsByScoreGrpId(id, "R", "_AView");
	}
	
	/**
	 * Method to get FinanceRepayments By FinReference
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<FinanceRepayments> getFinanceRepaymentsByFinRef(final String id) {
		logger.debug("Entering ");
		return getFinanceRepaymentsDAO().getFinRepayListByFinRef(id, "");
	}
	
	@Override
	public boolean inActivateFinance(String finReference,LoginUserDetails userDetails) {
		try {
			//FIXME
			if (getPostingsPreparationUtil().processFinCanclPostings(finReference)) {
				String tranType = PennantConstants.TRAN_WF;
				FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, "",  false);
				financeMain.setFinIsActive(false);
				financeMain.setClosingStatus("C");
				String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
				AuditDetail auditDetail =new AuditDetail(tranType, 1, fields[0],fields[1], financeMain.getBefImage(), financeMain);
				AuditHeader auditHeader = new AuditHeader(finReference, null, null, null,auditDetail, userDetails, null);
				getFinanceMainDAO().updateCancelStatus(financeMain, "");
				getAuditHeaderDAO().addAudit(auditHeader);
				return true;
			}
		} catch (Exception e) {
			return false;
		} 
		return false;
	}

}
