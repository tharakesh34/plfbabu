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
 *
 * FileName    		:  GenericFinanceDetailService.java										*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 

 * 13-06-2018       Siva					 0.2        Stage Accounting Modifications      * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.InstallmentDueService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.AccountProcessUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.OverDueRecoveryPostingsUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.SuspensePostingUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.applicationmaster.FinIRRDetailsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.collateral.FinAssetTypeDAO;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.ext.ExtTablesDAO;
import com.pennant.backend.dao.finance.FinCollateralsDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinPlanEmiHolidayDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinStageAccountingLogDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.finance.SecondaryAccountDAO;
import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.dao.findedup.FinanceDedupeDAO;
import com.pennant.backend.dao.insurancedetails.FinInsurancesDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.policecase.PoliceCaseDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.FinFeeChargesDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinIRRDetails;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinStageAccountingLog;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinTaxDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.GSTInvoiceTxnDetails;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.SecondaryAccount;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.backend.model.finance.salary.FinSalariedPayment;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.CollateralAssignmentValidation;
import com.pennant.backend.service.collateral.impl.FinAssetTypesValidation;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.contractor.ContractorAssetDetailService;
import com.pennant.backend.service.finance.impl.FinInsuranceValidation;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.backend.service.mandate.FinMandateService;
import com.pennant.backend.service.payorderissue.impl.DisbursementPostings;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.core.TableType;

public abstract class GenericFinanceDetailService extends GenericService<FinanceDetail> {
	private static final Logger				logger	= Logger.getLogger(GenericFinanceDetailService.class);

	private AuditHeaderDAO					auditHeaderDAO;

	private CustomerStatusCodeDAO			customerStatusCodeDAO;
	private FinanceScheduleDetailDAO		financeScheduleDetailDAO;
	private FinanceDisbursementDAO			financeDisbursementDAO;
	private RepayInstructionDAO				repayInstructionDAO;
	private FinODPenaltyRateDAO				finODPenaltyRateDAO;
	private FinanceTypeDAO					financeTypeDAO;
	private DocumentDetailsDAO				documentDetailsDAO;
	private CustomerDocumentDAO				customerDocumentDAO;
	private CommitmentDAO					commitmentDAO;
	private PostingsDAO						postingsDAO;
	private FinanceProfitDetailDAO			profitDetailsDAO;
	private CommitmentMovementDAO			commitmentMovementDAO;
	private FinLogEntryDetailDAO			finLogEntryDetailDAO;
	private FinODDetailsDAO					finODDetailsDAO;
	private FinanceMainDAO					financeMainDAO;
	private FinanceRepaymentsDAO			financeRepaymentsDAO;
	private OverdueChargeRecoveryDAO		recoveryDAO;
	private FinanceSuspHeadDAO				financeSuspHeadDAO;
	private TransactionEntryDAO				transactionEntryDAO;
	private FinFeeChargesDAO				finFeeChargesDAO;
	private FinFeeScheduleDetailDAO			finFeeScheduleDetailDAO;
	private FinanceStepDetailDAO			financeStepDetailDAO;
	private BlackListCustomerDAO			blacklistCustomerDAO;
	private FinanceDedupeDAO				financeDedupeDAO;
	private PoliceCaseDAO					policeCaseDAO;
	private CustomerDedupDAO				customerDedupDAO;
	private FinStageAccountingLogDAO		finStageAccountingLogDAO;
	private FinCollateralsDAO				finCollateralsDAO;
	private FinTypeAccountingDAO			finTypeAccountingDAO;
	private FinPlanEmiHolidayDAO			finPlanEmiHolidayDAO;
	private FinFeeDetailDAO					finFeeDetailDAO;
	private FinanceReferenceDetailDAO		financeReferenceDetailDAO;

	// DocumentManagerDAO
	private DocumentManagerDAO				documentManagerDAO;

	// External Database
	private ExtTablesDAO					extTablesDAO;

	private AccountEngineExecution			engineExecution;
	private CustomerDAO						customerDAO;
	private AccountProcessUtil				accountProcessUtil;
	private PostingsPreparationUtil			postingsPreparationUtil;
	private FinStatusDetailDAO				finStatusDetailDAO;
	private SuspensePostingUtil				suspensePostingUtil;
	private OverDueRecoveryPostingsUtil		recoveryPostingsUtil;
	private FinanceDeviationsService		deviationDetailsService;
	private FinMandateService				finMandateService;
	private AccrualService 					accrualService;

	// Service Classes
	private EligibilityDetailService		eligibilityDetailService;
	private GuarantorDetailService			guarantorDetailService;
	private JointAccountDetailService		jointAccountDetailService;
	private AgreementDetailService			agreementDetailService;
	private ScoringDetailService			scoringDetailService;
	private CheckListDetailService			checkListDetailService;
	private ContractorAssetDetailService	contractorAssetDetailService;
	private CustomerDetailsService			customerDetailsService;
	private FinCollateralService			finCollateralService;
	private EtihadCreditBureauDetailService	etihadCreditBureauDetailService;
	private BundledProductsDetailService	bundledProductsDetailService;
	private FinAssetEvaluationService		finAssetEvaluationService;
	private FinAdvancePaymentsService		finAdvancePaymentsService;
	private FinFeeDetailService				finFeeDetailService;
	private FinCovenantTypeService			finCovenantTypeService;
	private RepaymentPostingsUtil			repayPostingUtil;
	private SecondaryAccountDAO				secondaryAccountDAO;
	private AgreementFieldsDetailService	agreementFieldsDetailService;
	private FinFlagDetailsDAO				finFlagDetailsDAO;
	private FinServiceInstrutionDAO			finServiceInstructionDAO;
	private CollateralAssignmentValidation	collateralAssignmentValidation;
	private CollateralAssignmentDAO			collateralAssignmentDAO;
	private FinInsurancesDAO				finInsurancesDAO;
	private FinInsuranceValidation			finInsuranceValidation;
	private FinAssetTypesValidation			finAssetTypesValidation;
	private FinAssetTypeDAO					finAssetTypeDAO;
	private DisbursementPostings			disbursementPostings;
	private InstallmentDueService 			installmentDueService;
	private FinIRRDetailsDAO               	finIRRDetailsDAO;
	
	// EOD Process Checking
	private CustomerQueuingDAO				customerQueuingDAO;
	
	//GST
	private GSTInvoiceTxnService			gstInvoiceTxnService;

	// Query Management
	private QueryDetailService				queryDetailService;
	
	public GenericFinanceDetailService() {
		super();
	}

	/**
	 * Method to delete Co-borrower & Guarantors Details
	 * 
	 * @param finDetail
	 * @param tableType
	 * @return auditList
	 * */
	public List<AuditDetail> jointGuarantorDeletion(FinanceDetail financeDetail, String tableType, String auditTranType) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		List<JointAccountDetail> jointAccountDetails = financeDetail.getJountAccountDetailList();
		List<GuarantorDetail> guarantorDetails = financeDetail.getGurantorsDetailList();

		if (jointAccountDetails != null && !jointAccountDetails.isEmpty()) {
			auditDetails.addAll(getJointAccountDetailService().delete(jointAccountDetails, tableType, auditTranType));
		}

		if (guarantorDetails != null) {
			getGuarantorDetailService().delete(guarantorDetails, tableType, auditTranType);
		}

		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param contributorHeader
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setContributorAuditData(FinContributorHeader contributorHeader, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorDetail(""));

		for (int i = 0; i < contributorHeader.getContributorDetailList().size(); i++) {

			FinContributorDetail contributorDetail = contributorHeader.getContributorDetailList().get(i);
			if (StringUtils.isEmpty(contributorDetail.getRecordType())) {
				continue;
			}

			contributorDetail.setWorkflowId(contributorHeader.getWorkflowId());
			contributorDetail.setFinReference(contributorHeader.getFinReference());

			boolean isRcdType = false;

			if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				contributorDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			contributorDetail.setRecordStatus(contributorHeader.getRecordStatus());
			contributorDetail.setUserDetails(contributorHeader.getUserDetails());
			contributorDetail.setLastMntOn(contributorHeader.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], contributorDetail
					.getBefImage(), contributorDetail));
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(FinanceDetail detail, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		DocumentDetails object = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < detail.getDocumentDetailsList().size(); i++) {
			DocumentDetails documentDetails = detail.getDocumentDetailsList().get(i);

			if (StringUtils.isEmpty(documentDetails.getRecordType())) {
				continue;
			}

			documentDetails.setWorkflowId(detail.getFinScheduleData().getFinanceMain().getWorkflowId());
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

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
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

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setCollateralAssignmentAuditData(FinanceDetail detail, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CollateralAssignment assignment = new CollateralAssignment();
		String[] fields = PennantJavaUtil.getFieldDetails(assignment, assignment.getExcludeFields());
		FinanceMain financeMain = detail.getFinScheduleData().getFinanceMain();

		for (int i = 0; i < detail.getCollateralAssignmentList().size(); i++) {
			CollateralAssignment collateralAssignment = detail.getCollateralAssignmentList().get(i);

			if (StringUtils.isEmpty(collateralAssignment.getRecordType())) {
				continue;
			}

			collateralAssignment.setWorkflowId(financeMain.getWorkflowId());
			boolean isRcdType = false;

			if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				collateralAssignment.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			collateralAssignment.setRecordStatus(financeMain.getRecordStatus());
			collateralAssignment.setUserDetails(financeMain.getUserDetails());
			collateralAssignment.setLastMntOn(financeMain.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], collateralAssignment
					.getBefImage(), collateralAssignment));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setFinAssetTypesAuditData(FinanceDetail detail, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinAssetTypes assignment = new FinAssetTypes();
		String[] fields = PennantJavaUtil.getFieldDetails(assignment, assignment.getExcludeFields());
		FinanceMain financeMain = detail.getFinScheduleData().getFinanceMain();

		for (int i = 0; i < detail.getFinAssetTypesList().size(); i++) {
			FinAssetTypes finAssetTypes = detail.getFinAssetTypesList().get(i);

			if (StringUtils.isEmpty(finAssetTypes.getRecordType())) {
				continue;
			}

			finAssetTypes.setWorkflowId(financeMain.getWorkflowId());
			boolean isRcdType = false;

			if (finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finAssetTypes.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finAssetTypes.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finAssetTypes.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finAssetTypes.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			finAssetTypes.setRecordStatus(financeMain.getRecordStatus());
			finAssetTypes.setUserDetails(financeMain.getUserDetails());
			finAssetTypes.setLastMntOn(financeMain.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finAssetTypes.getBefImage(),
					finAssetTypes));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setInsuranceDetailsAuditData(FinanceDetail detail, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinInsurances object = new FinInsurances();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < detail.getFinScheduleData().getFinInsuranceList().size(); i++) {
			FinInsurances insuranceDetails = detail.getFinScheduleData().getFinInsuranceList().get(i);

			if (StringUtils.isEmpty(insuranceDetails.getRecordType())) {
				continue;
			}

			insuranceDetails.setWorkflowId(detail.getFinScheduleData().getFinanceMain().getWorkflowId());
			boolean isRcdType = false;

			if (insuranceDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				insuranceDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (insuranceDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				insuranceDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (insuranceDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				insuranceDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				insuranceDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (insuranceDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (insuranceDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| insuranceDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			insuranceDetails.setRecordStatus(detail.getFinScheduleData().getFinanceMain().getRecordStatus());
			insuranceDetails.setUserDetails(detail.getFinScheduleData().getFinanceMain().getUserDetails());
			insuranceDetails.setLastMntOn(detail.getFinScheduleData().getFinanceMain().getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					insuranceDetails.getBefImage(), insuranceDetails));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	public List<AuditDetail> processFinInsuranceDetails(List<AuditDetail> auditDetails, String type,
			FinanceDetail detail, boolean isWif) {

		logger.debug("Entering");

		FinanceMain financeMain = detail.getFinScheduleData().getFinanceMain();

		for (int i = 0; i < auditDetails.size(); i++) {
			FinInsurances finInsurance = (FinInsurances) auditDetails.get(i).getModelData();
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;
			long insId = 0;

			if (StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)) {
				for (int j = 0; j < finInsurance.getFinSchFrqInsurances().size(); j++) {
					FinSchFrqInsurance finSchFrqInsurance = finInsurance.getFinSchFrqInsurances().get(j);
					finSchFrqInsurance.setLastMntBy(financeMain.getLastMntBy());
					finSchFrqInsurance.setLastMntOn(financeMain.getLastMntOn());
					finSchFrqInsurance.setRecordStatus(financeMain.getRecordStatus());
					finSchFrqInsurance.setRoleCode(financeMain.getRoleCode());
					finSchFrqInsurance.setNextRoleCode(financeMain.getNextRoleCode());
					finSchFrqInsurance.setRecordType(financeMain.getRecordType());
					finSchFrqInsurance.setWorkflowId(financeMain.getWorkflowId());
				}
			}

			boolean SaveList = false;
			boolean deleteList = false;

			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finInsurance.setRoleCode("");
				finInsurance.setNextRoleCode("");
				finInsurance.setTaskId("");
				finInsurance.setNextTaskId("");
			}
			finInsurance.setReference(detail.getFinScheduleData().getFinanceMain().getFinReference());
			finInsurance.setWorkflowId(0);

			if (finInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finInsurance.isNewRecord()) {
				saveRecord = true;
				if (finInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finInsurance.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finInsurance.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finInsurance.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (finInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finInsurance.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finInsurance.getRecordType();
				recordStatus = finInsurance.getRecordStatus();
				finInsurance.setRecordType("");
				finInsurance.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				insId = getFinInsurancesDAO().save(finInsurance, type, isWif);
				if (finInsurance.isInsuranceReq()
						&& StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)) {
					SaveList = true;
				}
			}

			if (updateRecord) {
				insId = finInsurance.getInsId();
				getFinInsurancesDAO().update(finInsurance, type, isWif);
				deleteList = true;
				if (finInsurance.isInsuranceReq()
						&& StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)) {
					SaveList = true;
				}
			}

			if (deleteRecord) {
				getFinInsurancesDAO().delete(finInsurance, type, isWif);
				deleteList = true;
			}

			if (approveRec) {
				finInsurance.setRecordType(rcdType);
				finInsurance.setRecordStatus(recordStatus);
			}

			if (SaveList || deleteList) {

				// Delete Existing List from Tables
				if (deleteList) {
					getFinInsurancesDAO().deleteFreqBatch(insId, isWif, type);
				}

				// Insert / update(reinsert) details
				if (SaveList) {

					List<FinSchFrqInsurance> list = finInsurance.getFinSchFrqInsurances();
					for (int j = 0; j < list.size(); j++) {
						list.get(j).setInsId(insId);
					}

					// Save List of Insurance details for schedule frequency
					getFinInsurancesDAO().saveFreqBatch(list, isWif, type);

				}
			}
			auditDetails.get(i).setModelData(finInsurance);
		}

		logger.debug("Leaving");
		return auditDetails;

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
	@SuppressWarnings("unchecked")
	public List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {

			for (int i = 0; i < list.size(); i++) {
				String field = list.get(i).getAuditField();
				String fieldValues = list.get(i).getAuditValue();
				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
				Set<String> excludeSet = new HashSet<String>();
				try {
					excludeSet = (Set<String>) object.getClass().getMethod("getExcludeFields").invoke(object);
				} catch (Exception e) {
					logger.error("Error with Exclude Fields", e);
					excludeSet = new HashSet<String>();
				}
				String[] fields = PennantJavaUtil.getFieldDetails(object, excludeSet);
				try {

					rcdType = object.getClass().getMethod("getRecordType").invoke(object).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isNotEmpty(transType)) {
						// check and change below line for Complete code
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses())
								.invoke(object, object.getClass().getClasses());

						AuditDetail auditDetail = new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(),
								fields[0], fields[1], befImg, object);
						if (auditDetail.getModelData() instanceof ExtendedFieldRender) {
							auditDetail.setExtended(true);
							auditDetail.setAuditField(field);
							auditDetail.setAuditValue(fieldValues);
						}

						auditDetailsList.add(auditDetail);

					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

	/**
	 * Method For Preparing List of AuditDetails for Contributor Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	public List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails, String type,
			FinanceMain financeMain, String procEdtEvent) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();

			if (StringUtils.isBlank(documentDetails.getRecordType())) {
				continue;
			}
			if (!(DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode()))) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				boolean isTempRecord = false;
				if (StringUtils.isEmpty(type) || type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					documentDetails.setRoleCode("");
					documentDetails.setNextRoleCode("");
					documentDetails.setTaskId("");
					documentDetails.setNextTaskId("");
				}
				documentDetails.setLastMntBy(financeMain.getLastMntBy());
				documentDetails.setWorkflowId(0);

				if (DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode())) {
					approveRec = true;
				}

				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
					isTempRecord = true;
				} else if (documentDetails.isNewRecord()) {
					saveRecord = true;
					if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
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
					if (StringUtils.isEmpty(documentDetails.getReferenceId())) {
						documentDetails.setReferenceId(financeMain.getFinReference());
					}
					documentDetails.setFinEvent(procEdtEvent);
					// Save the document (documentDetails object) into DocumentManagerTable using documentManagerDAO.save(?) get the long Id.
					// This will be used in the getDocumentDetailsDAO().save, Update & delete methods
					if (documentDetails.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetails.getDocImage());
						documentDetails.setDocRefId(getDocumentManagerDAO().save(documentManager));
					}
					// Pass the docRefId here to save this in place of docImage column. Or add another column for now to save this.
					getDocumentDetailsDAO().save(documentDetails, type);
				}

				if (updateRecord) {
					// When a document is updated, insert another file into the DocumentManager table's.
					// Get the new DocumentManager.id & set to documentDetails.getDocRefId()
					if (documentDetails.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetails.getDocImage());
						documentDetails.setDocRefId(getDocumentManagerDAO().save(documentManager));
					}
					getDocumentDetailsDAO().update(documentDetails, type);
				}

				if (deleteRecord && ((StringUtils.isEmpty(type) && !isTempRecord) || (StringUtils.isNotEmpty(type)))) {
					if (!type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
						getDocumentDetailsDAO().delete(documentDetails, type);
					}
				}

				if (approveRec) {
					documentDetails.setFinEvent("");
					documentDetails.setRecordType(rcdType);
					documentDetails.setRecordStatus(recordStatus);
				}
				auditDetails.get(i).setModelData(documentDetails);
			} else {
				CustomerDocument custdoc = getCustomerDocument(documentDetails, financeMain);
				if (custdoc.isNewRecord()) {
					getCustomerDocumentDAO().save(custdoc, "");
				} else {
					getCustomerDocumentDAO().update(custdoc, "");
				}

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
	public List<AuditDetail> processingCollateralAssignmentList(List<AuditDetail> auditDetails, String type,
			FinanceMain financeMain) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			CollateralAssignment collateralAssignment = (CollateralAssignment) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				collateralAssignment.setRoleCode("");
				collateralAssignment.setNextRoleCode("");
				collateralAssignment.setTaskId("");
				collateralAssignment.setNextTaskId("");
			}

			collateralAssignment.setReference(financeMain.getFinReference());
			collateralAssignment.setModule(FinanceConstants.MODULE_NAME);
			collateralAssignment.setWorkflowId(0);

			if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (collateralAssignment.isNewRecord()) {
				saveRecord = true;
				if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (collateralAssignment.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = collateralAssignment.getRecordType();
				recordStatus = collateralAssignment.getRecordStatus();
				collateralAssignment.setRecordType("");
				collateralAssignment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			// Insert Collateral Movement record
			if (StringUtils.isEmpty(type)) {
				CollateralMovement movement = new CollateralMovement();
				movement.setModule(FinanceConstants.MODULE_NAME);
				movement.setCollateralRef(collateralAssignment.getCollateralRef());
				movement.setReference(collateralAssignment.getReference());
				movement.setAssignPerc(collateralAssignment.getAssignPerc());
				movement.setValueDate(DateUtility.getAppDate());
				movement.setProcess(CollateralConstants.PROCESS_MANUAL);
				if (deleteRecord) {
					movement.setAssignPerc(BigDecimal.ZERO);
				}

				getCollateralAssignmentDAO().save(movement);
			}

			if (saveRecord) {
				getCollateralAssignmentDAO().save(collateralAssignment, type);
			}

			if (updateRecord) {
				getCollateralAssignmentDAO().update(collateralAssignment, type);
			}

			if (deleteRecord) {
				getCollateralAssignmentDAO().delete(collateralAssignment, type);
			}

			if (approveRec) {
				collateralAssignment.setRecordType(rcdType);
				collateralAssignment.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(collateralAssignment);
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
	public List<AuditDetail> processingFinAssetTypesList(List<AuditDetail> auditDetails, String type,
			FinanceMain financeMain) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinAssetTypes finAssetTypes = (FinAssetTypes) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finAssetTypes.setRoleCode("");
				finAssetTypes.setNextRoleCode("");
				finAssetTypes.setTaskId("");
				finAssetTypes.setNextTaskId("");
			}

			finAssetTypes.setReference(financeMain.getFinReference());
			finAssetTypes.setWorkflowId(0);

			if (finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finAssetTypes.isNewRecord()) {
				saveRecord = true;
				if (finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finAssetTypes.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finAssetTypes.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finAssetTypes.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finAssetTypes.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finAssetTypes.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finAssetTypes.getRecordType();
				recordStatus = finAssetTypes.getRecordStatus();
				finAssetTypes.setRecordType("");
				finAssetTypes.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinAssetTypeDAO().save(finAssetTypes, type);
			}

			if (updateRecord) {
				getFinAssetTypeDAO().update(finAssetTypes, type);
			}

			if (deleteRecord) {
				getFinAssetTypeDAO().delete(finAssetTypes, type);
			}

			if (approveRec) {
				finAssetTypes.setRecordType(rcdType);
				finAssetTypes.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finAssetTypes);
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	//Document Details List Maintainance
	public void listDocDeletion(FinanceDetail financeDetail, String tableType) {
		getDocumentDetailsDAO().deleteList(new ArrayList<DocumentDetails>(financeDetail.getDocumentDetailsList()),
				tableType);
	}

	/**
	 * Get Customer Document 
	 * @param documentDetails
	 * @param financeMain
	 * @return
	 */
	private CustomerDocument getCustomerDocument(DocumentDetails documentDetails, FinanceMain financeMain) {
		CustomerDocument customerDocument = getCustomerDocumentDAO().getCustomerDocumentById(financeMain.getCustID(),
				documentDetails.getDocCategory(), "");

		if (customerDocument == null) {
			customerDocument = new CustomerDocument();
			customerDocument.setCustDocIsAcrive(documentDetails.isCustDocIsAcrive());
			customerDocument.setCustDocIsVerified(documentDetails.isCustDocIsVerified());
			customerDocument.setCustDocRcvdOn(documentDetails.getCustDocRcvdOn());
			customerDocument.setCustDocVerifiedBy(documentDetails.getCustDocVerifiedBy());
			customerDocument.setNewRecord(true);
		}

		customerDocument.setCustID(financeMain.getCustID());
		customerDocument.setLovDescCustCIF(financeMain.getLovDescCustCIF());
		customerDocument.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());

		customerDocument.setCustDocTitle(documentDetails.getCustDocTitle());
		customerDocument.setCustDocIssuedCountry(documentDetails.getCustDocIssuedCountry());
		customerDocument.setLovDescCustDocIssuedCountry(documentDetails.getLovDescCustDocIssuedCountry());
		customerDocument.setCustDocIssuedOn(documentDetails.getCustDocIssuedOn());
		customerDocument.setCustDocExpDate(documentDetails.getCustDocExpDate());
		customerDocument.setCustDocSysName(documentDetails.getCustDocSysName());
		customerDocument.setCustDocImage(documentDetails.getDocImage());
		customerDocument.setCustDocType(documentDetails.getDoctype());
		customerDocument.setCustDocCategory(documentDetails.getDocCategory());
		customerDocument.setCustDocName(documentDetails.getDocName());
		customerDocument.setPdfPassWord(documentDetails.getPdfPassWord());
		customerDocument.setLovDescCustDocCategory(documentDetails.getLovDescDocCategoryName());

		customerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerDocument.setRecordType("");
		customerDocument.setUserDetails(documentDetails.getUserDetails());
		customerDocument.setVersion(documentDetails.getVersion());
		customerDocument.setLastMntBy(documentDetails.getLastMntBy());
		customerDocument.setLastMntOn(documentDetails.getLastMntOn());
		return customerDocument;
	}

	/**
	 * Method for Processing Suspense Preparation after Finance Maintenance
	 * 
	 * @param financeMain
	 * @param processType
	 * @param dateValueDate
	 * @param alwRIA
	 * @param curFinsts
	 * @param maxODDays
	 * @throws AccountNotFoundException
	 */
	protected void suspenseCheckProcess(FinanceMain financeMain, String processType, Date dateValueDate,
			String curFinsts, int maxODDays) throws InterfaceException {

		boolean chkSuspProcess = false;

		//Checking Conditions for Suspense Calculations
		if (processType.equals(FinanceConstants.FINSER_EVENT_POSTPONEMENT)) {

			//Get Current Maximum Overdue Days after Deletion Past Due Terms
			int curMaxODDays = getFinODDetailsDAO().getMaxODDaysOnDeferSchd(financeMain.getFinReference(), null);
			if (curMaxODDays < maxODDays) {
				chkSuspProcess = true;
			}

		} else {
			chkSuspProcess = true;
		}

		if (chkSuspProcess) {

			// Deletion of Suspense Details Depends on Releases
			getFinanceSuspHeadDAO().updateSuspFlag(financeMain.getFinReference());

			//Get Maximum Days Overdue Details Object with Overdue Amount
			FinODDetails odDetail = getFinODDetailsDAO().getMaxDaysFinODDetails(financeMain.getFinReference());

			// Recreation of Suspense Details , if Finance is in Suspense with Current Max Overdue Days
			if (odDetail != null) {
				FinRepayQueue repayQueue = new FinRepayQueue();
				repayQueue.setFinReference(financeMain.getFinReference());
				repayQueue.setCustomerID(financeMain.getCustID());
				repayQueue.setBranch(financeMain.getFinBranch());
				repayQueue.setFinType(financeMain.getFinType());
				repayQueue.setRpyDate(odDetail.getFinODSchdDate());
				repayQueue.setFinRpyFor(odDetail.getFinODFor());

				try {
					getSuspensePostingUtil().suspensePreparation(financeMain, repayQueue, dateValueDate, true);
				} catch (IllegalAccessException e) {
					logger.error("Exception: ", e);
				} catch (InvocationTargetException e) {
					logger.error("Exception: ", e);
				}
			}
		}

		// Customer Status Change Date & Status Update after Suspense Details updations
		String custSts = getCustomerDAO().getCustWorstStsbyCurFinSts(financeMain.getCustID(),
				financeMain.getFinReference(), curFinsts);
		List<Long> custIdList = new ArrayList<Long>(1);
		custIdList.add(financeMain.getCustID());
		List<FinStatusDetail> suspDateSts = getFinanceSuspHeadDAO().getCustSuspDate(custIdList);

		Date suspFromdate = null;
		if (suspDateSts != null && !suspDateSts.isEmpty()) {
			suspFromdate = suspDateSts.get(0).getValueDate();
		}

		FinStatusDetail statusDetail = new FinStatusDetail();
		List<FinStatusDetail> custStatuses = new ArrayList<FinStatusDetail>(1);
		statusDetail.setCustId(financeMain.getCustID());
		statusDetail.setFinStatus(custSts);
		statusDetail.setValueDate(suspFromdate);
		custStatuses.add(statusDetail);

		getFinStatusDetailDAO().updateCustStatuses(custStatuses);

	}


	/**
	 * Preparing Accounting Data 
	 * @param financeDetail
	 * @param executingMap
	 * @return
	 * @throws InterruptedException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private AEEvent prepareAccountingData(FinanceDetail financeDetail, AEEvent aeEvent, FinanceProfitDetail profitDetail, Date valueDate) {

		Date curBDay = DateUtility.getAppDate();
		if(valueDate == null){
			valueDate = curBDay;
		}
		String eventCode = financeDetail.getAccountingEventCode();

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		if (StringUtils.isBlank(eventCode)) {
			eventCode = PennantApplicationUtil.getEventCode(finMain.getFinStartDate());
		}
		
		BigDecimal totalPftSchdOld = BigDecimal.ZERO;
		BigDecimal totalPftCpzOld = BigDecimal.ZERO;
		BigDecimal totalPriSchdOld = BigDecimal.ZERO;
		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();
		if (profitDetail != null) {//FIXME
			BeanUtils.copyProperties(profitDetail, newProfitDetail);
			totalPftSchdOld = profitDetail.getTotalPftSchd();
			totalPftCpzOld = profitDetail.getTotalPftCpz();
			totalPriSchdOld = profitDetail.getTotalpriSchd();
		}
		
		aeEvent = AEAmounts.procAEAmounts(finMain, finSchdDetails, profitDetail, eventCode, valueDate, curBDay);
		if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(), eventCode, FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), eventCode, FinanceConstants.MODULEID_FINTYPE));
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		accrualService.calProfitDetails(finMain, finSchdDetails, newProfitDetail, curBDay);
		if (!FinanceConstants.BPI_NO.equals(finMain.getBpiTreatment())) {
			amountCodes.setBpi(finMain.getBpiAmount());
		}
		
		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();

		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		aeEvent.setModuleDefiner(StringUtils.isEmpty(financeDetail.getModuleDefiner()) ?  FinanceConstants.FINSER_EVENT_ORG : financeDetail.getModuleDefiner());
		if(financeDetail.getModuleDefiner().equals(FinanceConstants.FINSER_EVENT_ORG)){
			amountCodes.setDisburse(finMain.getFinCurrAssetValue().add(finMain.getDownPayment()));
		}else{
			amountCodes.setDisburse(newProfitDetail.getTotalpriSchd().subtract(totalPriSchdOld));
		}

		if (finMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(finMain.getRecordType())) {
			aeEvent.setNewRecord(true);
		}
		return aeEvent;
	}
	
	/**
	 * Method for Preparing List of Entries based on recordings for VAS
	 * @param aeEvent
	 * @param vasRecordingList
	 * @return
	 */
	protected List<ReturnDataSet> processVasAccounting(AEEvent aeEvent, List<VASRecording> vasRecordingList, boolean doPostings)  throws InterfaceException{

		List<ReturnDataSet> datasetList = new ArrayList<>();
		if (vasRecordingList != null && !vasRecordingList.isEmpty()) {

			aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_VAS_FEE);
			for (VASRecording recording : vasRecordingList) {
				recording.getDeclaredFieldValues(aeEvent.getDataMap());
				aeEvent.getAcSetIDList().clear();
				aeEvent.getAcSetIDList().add(recording.getFeeAccounting());
				aeEvent.setFinReference(recording.getVasReference());

				aeEvent.setLinkedTranId(0);
				if(doPostings){
					aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
				}else{
					aeEvent = engineExecution.getAccEngineExecResults(aeEvent);
				}
				datasetList.addAll(aeEvent.getReturnDataSet());
			}
		}
		return datasetList;
	}

	protected HashMap<String, Object> prepareFeeRulesMap(AEAmountCodes amountCodes, HashMap<String, Object> dataMap, FinanceDetail financeDetail) {
		logger.debug("Entering");

		List<FinFeeDetail> finFeeDetailList = financeDetail.getFinScheduleData().getFinFeeDetailList();

		if (finFeeDetailList != null) {
			FeeRule feeRule;

			BigDecimal deductFeeDisb = BigDecimal.ZERO;
			BigDecimal addFeeToFinance = BigDecimal.ZERO;
			BigDecimal paidFee = BigDecimal.ZERO;
			BigDecimal feeWaived = BigDecimal.ZERO;
			
			//VAS
			BigDecimal deductVasDisb = BigDecimal.ZERO;
			BigDecimal addVasToFinance = BigDecimal.ZERO;
			BigDecimal paidVasFee = BigDecimal.ZERO;
			BigDecimal vasFeeWaived = BigDecimal.ZERO;

			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				feeRule = new FeeRule();

				feeRule.setFeeCode(finFeeDetail.getFeeTypeCode());
				feeRule.setFeeAmount(finFeeDetail.getActualAmount());
				feeRule.setWaiverAmount(finFeeDetail.getWaivedAmount());
				feeRule.setPaidAmount(finFeeDetail.getPaidAmount());
				feeRule.setFeeToFinance(finFeeDetail.getFeeScheduleMethod());
				feeRule.setFeeMethod(finFeeDetail.getFeeScheduleMethod());

				dataMap.put(finFeeDetail.getFeeTypeCode() + "_C", finFeeDetail.getActualAmountOriginal());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_W", finFeeDetail.getWaivedAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_P", finFeeDetail.getPaidAmountOriginal());
				
				//GST Added
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_N", finFeeDetail.getNetAmount());
				//Calculated Amount 
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_C", finFeeDetail.getFinTaxDetails().getActualCGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_C", finFeeDetail.getFinTaxDetails().getActualSGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_C", finFeeDetail.getFinTaxDetails().getActualIGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_C", finFeeDetail.getFinTaxDetails().getActualUGST());
				
				//Paid Amount 
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_P", finFeeDetail.getFinTaxDetails().getPaidCGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_P", finFeeDetail.getFinTaxDetails().getPaidSGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_P", finFeeDetail.getFinTaxDetails().getPaidIGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_P", finFeeDetail.getFinTaxDetails().getPaidUGST());
	
				//Net Amount 
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_N", finFeeDetail.getFinTaxDetails().getNetCGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_N", finFeeDetail.getFinTaxDetails().getNetSGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_N", finFeeDetail.getFinTaxDetails().getNetIGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_N", finFeeDetail.getFinTaxDetails().getNetUGST());

				if (feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)
						|| feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
						|| feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SCH", finFeeDetail.getRemainingFee());
					//GST Added
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_SCH", finFeeDetail.getFinTaxDetails().getRemFeeCGST());
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_SCH", finFeeDetail.getFinTaxDetails().getRemFeeSGST());
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_SCH", finFeeDetail.getFinTaxDetails().getRemFeeIGST());
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_SCH", finFeeDetail.getFinTaxDetails().getRemFeeUGST());
				} else {
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SCH", 0);
					//GST Added
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_SCH", 0);
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_SCH", 0);
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_SCH", 0);
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_SCH", 0);
				}

				if (StringUtils.equals(feeRule.getFeeToFinance(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_AF", finFeeDetail.getRemainingFee());
					//GST Added
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_AF", finFeeDetail.getFinTaxDetails().getRemFeeCGST());
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_AF", finFeeDetail.getFinTaxDetails().getRemFeeSGST());
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_AF", finFeeDetail.getFinTaxDetails().getRemFeeIGST());
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_AF", finFeeDetail.getFinTaxDetails().getRemFeeUGST());

				} else {
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_AF", 0);
					//GST Added
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_AF", 0);
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_AF", 0);
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_AF", 0);
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_AF", 0);
				}

				if (finFeeDetail.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
					deductFeeDisb = deductFeeDisb.add(finFeeDetail.getRemainingFee());
					if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent())) {
						deductVasDisb = deductVasDisb.add(finFeeDetail.getActualAmount());
					}
				} else if (finFeeDetail.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					addFeeToFinance = addFeeToFinance.add(finFeeDetail.getRemainingFee());
					if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent())) {
						addVasToFinance = addVasToFinance.add(finFeeDetail.getActualAmount());
					}
				}

				paidFee = paidFee.add(finFeeDetail.getPaidAmount());
				feeWaived = feeWaived.add(finFeeDetail.getWaivedAmount());
				
				if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent())) {
					paidVasFee = paidVasFee.add(finFeeDetail.getPaidAmount());
					vasFeeWaived = vasFeeWaived.add(finFeeDetail.getWaivedAmount());
				}
			}

			amountCodes.setDeductFeeDisb(deductFeeDisb);
			amountCodes.setAddFeeToFinance(addFeeToFinance);
			amountCodes.setFeeWaived(feeWaived);
			amountCodes.setPaidFee(paidFee);
			
			//VAS
			amountCodes.setDeductVasDisb(deductVasDisb);
			amountCodes.setAddVasToFinance(addVasToFinance);
			amountCodes.setVasFeeWaived(vasFeeWaived);
			amountCodes.setPaidVasFee(paidVasFee);
		}

		logger.debug("Leaving");
		return dataMap;
	}

	/**
	 * Method for Execute posting Details on Core Banking Side
	 * 
	 * @param auditHeader
	 * @param curBDay
	 * @return
	 */
	public AuditHeader executeAccountingProcess(AuditHeader auditHeader, Date curBDay) throws InterfaceException{
		logger.debug("Entering");

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		BeanUtils.copyProperties((FinanceDetail) auditHeader.getAuditDetail().getModelData(), financeDetail);
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String eventCode = financeDetail.getAccountingEventCode();
		FinanceProfitDetail pftDetail = new FinanceProfitDetail();
		AEEvent aeEvent = new AEEvent();
		Date valueDate = null;
		
		boolean isNew = false;
		if(StringUtils.equals(FinanceConstants.FINSER_EVENT_ORG, financeDetail.getModuleDefiner())){
			pftDetail = new FinanceProfitDetail();
			isNew = true;
			
			// Added Value date as Finance Start Date in case of origination Disbursement
			valueDate = financeMain.getFinStartDate();
		}else{
			pftDetail = getProfitDetailsDAO().getFinProfitDetailsById(financeMain.getFinReference());
		}

		aeEvent = prepareAccountingData(financeDetail, aeEvent, pftDetail, valueDate);
		
		// Fee Details Validation
		/*boolean isFeeConfgMatched = validateAccSetFees(financeDetail.getFinScheduleData().getFinFeeDetailList(), 
				aeEvent.getAcSetIDList(), financeDetail.getAccountingEventCode());
		if(!isFeeConfgMatched){
			auditHeader.setErrorDetails(new ErrorDetails("60212", null));
			return auditHeader;
		}*/
						
		aeEvent.setPostingUserBranch(auditHeader.getAuditBranchCode());
		aeEvent.setEntityCode(financeMain.getLovDescEntityCode());	
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		HashMap<String, Object>	dataMap = aeEvent.getDataMap();
		dataMap = prepareFeeRulesMap(amountCodes, dataMap, financeDetail);
		
		HashMap<String, Object> gstExecutionMap = getFinFeeDetailService().prepareGstMappingDetails(financeDetail, financeMain.getFinBranch());
		if (gstExecutionMap != null) {
			for (String key : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					dataMap.put (key, gstExecutionMap.get(key));
				}
			}
		}

		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);

		// Prepared Postings execution 
		getPostingsPreparationUtil().postAccounting(aeEvent);
		
		//GST Invoice Preparation
		if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ORG) && gstInvoiceTxnService !=null) {
			gstInvoicePreparation(aeEvent, financeDetail);
		}

		//Disbursement Instruction Posting
		if (StringUtils.equals(eventCode, AccountEventConstants.ACCEVENT_ADDDBS)
				|| StringUtils.equals(eventCode, AccountEventConstants.ACCEVENT_ADDDBSF)
				|| StringUtils.equals(eventCode, AccountEventConstants.ACCEVENT_ADDDBSN)
				|| StringUtils.equals(eventCode, AccountEventConstants.ACCEVENT_ADDDBSP)) {

			Map<Long, Long> finAdvanceMap = disbursementPostings.prepareDisbPostingApproval(financeDetail.getAdvancePaymentsList(), 
					financeDetail.getFinScheduleData().getFinanceMain(), auditHeader.getAuditBranchCode());

			List<FinAdvancePayments> advPayList = financeDetail.getAdvancePaymentsList();

			//loop through the disbursements.
			if (advPayList != null && !advPayList.isEmpty()) {

				for (int i = 0; i < advPayList.size(); i++) {
					FinAdvancePayments advPayment = advPayList.get(i);
					if(finAdvanceMap.containsKey(advPayment.getPaymentId())){
						advPayment.setLinkedTranId(finAdvanceMap.get(advPayment.getPaymentId()));
					}
				}
			}
		}
		
		// VAS Recording Accounting Entries
		if(isNew){
			if(financeDetail.getFinScheduleData().getVasRecordingList() != null && 
					!financeDetail.getFinScheduleData().getVasRecordingList().isEmpty()){
				processVasAccounting(aeEvent, financeDetail.getFinScheduleData().getVasRecordingList(), true);
			}
			getInstallmentDueService().processbackDateInstallmentDues(financeDetail, pftDetail, DateUtility.getAppDate(),true, auditHeader.getAuditBranchCode());
		}

		doSave_PftDetails(pftDetail, isNew);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * preparing GST Invoice Details and insert data into GST Invoice related tables
	 * @param aeEvent
	 * @param financeDetail
	 */
	public void gstInvoicePreparation(AEEvent aeEvent, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		
		GSTInvoiceTxn gstInvoiceTxn = new GSTInvoiceTxn();

		List<FinFeeDetail> finFeeDetailsList = financeDetail.getFinScheduleData().getFinFeeDetailList();

		if (CollectionUtils.isEmpty(finFeeDetailsList)) {
			return;
		} else {
			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
			gstInvoiceTxn.setTransactionID(aeEvent.getLinkedTranId());
			List<GSTInvoiceTxnDetails> gstInvoiceTxnDetails = new ArrayList<GSTInvoiceTxnDetails>();

			gstInvoiceTxn.setInvoice_Status("I");
			gstInvoiceTxn.setInvoiceDate(DateUtility.getAppDate()); //Need to confirm either it is system date or application date
			Entity entity = null;
			if (StringUtils.isNotBlank(financeMain.getLovDescEntityCode())) {
				entity = gstInvoiceTxnService.getEntity(financeMain.getLovDescEntityCode());
			} else {
				entity = gstInvoiceTxnService.getEntityByFinDivision(financeType.getFinDivision(), "_Aview");
			}
			gstInvoiceTxn.setCompanyCode(entity.getEntityCode());
			gstInvoiceTxn.setCompanyName(entity.getEntityDesc());
			gstInvoiceTxn.setPanNumber(entity.getPANNumber());
			gstInvoiceTxn.setLoanAccountNo(financeMain.getFinReference());

			Province companyProvince = gstInvoiceTxnService.getApprovedProvince(entity.getCountry(), entity.getStateCode());

			if (companyProvince != null) {
				
				gstInvoiceTxn.setCompany_State_Code(companyProvince.getCPProvince());
				gstInvoiceTxn.setCompany_State_Name(companyProvince.getCPProvinceName());

				if (CollectionUtils.isNotEmpty(companyProvince.getTaxDetailList())) {
					TaxDetail taxDetail = companyProvince.getTaxDetailList().get(0);
					
					if (StringUtils.isBlank(taxDetail.getHsnNumber()) || StringUtils.isBlank(taxDetail.getNatureService())) {
						return;		//FIXME write this case as a error message
					}
					
					gstInvoiceTxn.setCompany_GSTIN(taxDetail.getTaxCode());
					gstInvoiceTxn.setCompany_Address1(taxDetail.getAddressLine1());
					gstInvoiceTxn.setCompany_Address2(taxDetail.getAddressLine2());
					gstInvoiceTxn.setCompany_Address3(taxDetail.getAddressLine3());
					gstInvoiceTxn.setCompany_PINCode(taxDetail.getPinCode());
					gstInvoiceTxn.setHsnNumber(taxDetail.getHsnNumber());
					gstInvoiceTxn.setNatureService(taxDetail.getNatureService());
				} else {
					return;		//FIXME write this case as a error message
				}
			}

			FinanceTaxDetail finTaxDetail = financeDetail.getFinanceTaxDetails();
			Province customerProvince =  null;
			String country = "";
			String province = "";

			if (finTaxDetail != null && StringUtils.isNotBlank(finTaxDetail.getApplicableFor()) && !PennantConstants.List_Select.equals(finTaxDetail.getApplicableFor())) {
				country = finTaxDetail.getCountry();
				province = finTaxDetail.getProvince();
				gstInvoiceTxn.setCustomerID(finTaxDetail.getCustCIF());
				gstInvoiceTxn.setCustomerName(finTaxDetail.getCustShrtName());
				gstInvoiceTxn.setCustomerGSTIN(finTaxDetail.getTaxNumber());	
				gstInvoiceTxn.setCustomerAddress(finTaxDetail.getAddrLine1());
			} else {
				List<CustomerAddres> addressList = financeDetail.getCustomerDetails().getAddressList();
				if (CollectionUtils.isNotEmpty(addressList)) {
					for (CustomerAddres customerAddres : addressList) {
						if (customerAddres.getCustAddrPriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
							country = customerAddres.getCustAddrCountry();
							
							Customer cust =  customerDAO.getCustomerByID(customerAddres.getCustID());
							province = customerAddres.getCustAddrProvince();
							gstInvoiceTxn.setCustomerID(cust.getCustCIF()); 
							gstInvoiceTxn.setCustomerName(cust.getCustShrtName());
							
							String custAddress = "";
							
							if (StringUtils.isNotBlank(customerAddres.getCustAddrHNbr())) {
								custAddress = customerAddres.getCustAddrHNbr();
							}
							
							if (StringUtils.isNotBlank(customerAddres.getCustFlatNbr())) {
								if (StringUtils.isBlank(custAddress)) {
									custAddress = custAddress.concat(customerAddres.getCustFlatNbr());
								} else {
									custAddress = custAddress + ", " + customerAddres.getCustFlatNbr();
								}
							}
							
							if (StringUtils.isNotBlank(customerAddres.getCustAddrStreet())) {
								if (StringUtils.isBlank(custAddress)) {
									custAddress = custAddress.concat(customerAddres.getCustAddrStreet());
								} else {
									custAddress = custAddress + ", " + customerAddres.getCustAddrStreet();
								}
							}
							
							if (StringUtils.isNotBlank(customerAddres.getLovDescCustAddrCityName())) {
								if (StringUtils.isBlank(custAddress)) {
									custAddress = custAddress.concat(customerAddres.getLovDescCustAddrCityName());
								} else {
									custAddress = custAddress + ", " + customerAddres.getLovDescCustAddrCityName();
								}
							}
							
							if (StringUtils.isNotBlank(customerAddres.getLovDescCustAddrProvinceName())) {
								if (StringUtils.isBlank(custAddress)) {
									custAddress = custAddress.concat(customerAddres.getLovDescCustAddrProvinceName());
								} else {
									custAddress = custAddress + ", " + customerAddres.getLovDescCustAddrProvinceName();
								}
							}
							
							if (StringUtils.isNotBlank(customerAddres.getLovDescCustAddrCountryName())) {
								if (StringUtils.isBlank(custAddress)) {
									custAddress = custAddress.concat(customerAddres.getLovDescCustAddrCountryName());
								} else {
									custAddress = custAddress + ", " + customerAddres.getLovDescCustAddrCountryName();
								}
							}
							
							if (StringUtils.isNotBlank(customerAddres.getCustPOBox())) {
								if (StringUtils.isBlank(custAddress)) {
									custAddress = custAddress.concat(customerAddres.getCustPOBox());
								} else {
									custAddress = custAddress + ", " + customerAddres.getCustPOBox();
								}
							}
							
							gstInvoiceTxn.setCustomerAddress(custAddress);
							//gstInvoiceTxn.setCustomerGSTIN(finTaxDetail.getTaxNumber());
							break;
						}
					}
				}
			}
			
			customerProvince = gstInvoiceTxnService.getApprovedProvince(country, province);
			gstInvoiceTxn.setCustomerStateCode(customerProvince.getTaxStateCode());	
			gstInvoiceTxn.setCustomerStateName(customerProvince.getCPProvinceName());
			
			BigDecimal invoiceAmout = BigDecimal.ZERO;
			for (FinFeeDetail feeDetail : finFeeDetailsList) {
				FinTaxDetails finTaxDetails = feeDetail.getFinTaxDetails();
				if (finTaxDetails != null) {
					GSTInvoiceTxnDetails details = new GSTInvoiceTxnDetails();
					details.setFeeCode(feeDetail.getFeeTypeCode());
					details.setFeeDescription(feeDetail.getFeeTypeDesc());
					details.setFeeAmount(feeDetail.getActualAmountOriginal());	//Fee Amount with out GST
					details.setCGST_RATE(feeDetail.getCgst());
					details.setIGST_RATE(feeDetail.getIgst());
					details.setSGST_RATE(feeDetail.getSgst());
					details.setUGST_RATE(feeDetail.getUgst());
					details.setCGST_AMT(finTaxDetails.getNetCGST()); //confirm which GST amount
					details.setIGST_AMT(finTaxDetails.getNetIGST());
					details.setSGST_AMT(finTaxDetails.getNetSGST());
					details.setUGST_AMT(finTaxDetails.getNetUGST());
					invoiceAmout = invoiceAmout.add(finTaxDetails.getNetCGST()).add(finTaxDetails.getNetIGST()).add(finTaxDetails.getNetSGST()).add(finTaxDetails.getNetUGST());
					gstInvoiceTxnDetails.add(details);
				}
			}
			
			gstInvoiceTxn.setInvoice_Amt(invoiceAmout);
			gstInvoiceTxn.setGstInvoiceTxnDetailsList(gstInvoiceTxnDetails);
			
			this.gstInvoiceTxnService.save(gstInvoiceTxn);
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Processing each stage Accounting Entry details for particular Finance
	 * 
	 * @param auditHeader
	 * @param list
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException
	 */
	protected AuditHeader executeStageAccounting(AuditHeader auditHeader){
		logger.debug("Entering");

		FinanceDetail financeDetail = null;
		Date valueDate = DateUtility.getAppDate();
		String receiptNumber = null;
		if (auditHeader.getAuditDetail().getModelData() instanceof FinanceDetail) {
			financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		} else if (auditHeader.getAuditDetail().getModelData() instanceof LiabilityRequest) {
			financeDetail = ((LiabilityRequest) auditHeader.getAuditDetail().getModelData()).getFinanceDetail();
		} else if (auditHeader.getAuditDetail().getModelData() instanceof RepayData) {
			financeDetail = ((RepayData) auditHeader.getAuditDetail().getModelData()).getFinanceDetail();
		} else if (auditHeader.getAuditDetail().getModelData() instanceof FinReceiptData) {
			financeDetail = ((FinReceiptData) auditHeader.getAuditDetail().getModelData()).getFinanceDetail();

			// Value Date identification
			FinReceiptData rceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
			FinReceiptHeader receiptHeader = rceiptData.getReceiptHeader();
			if(receiptHeader.getReceiptDetails() != null && !receiptHeader.getReceiptDetails().isEmpty()){
				for (FinReceiptDetail detail : receiptHeader.getReceiptDetails()) {
					if(StringUtils.equals(detail.getPaymentType(), receiptHeader.getReceiptMode()) &&
							!StringUtils.equals(receiptHeader.getReceiptMode(), RepayConstants.RECEIPTMODE_EXCESS)){
						valueDate = detail.getReceivedDate();
						receiptNumber = detail.getPaymentRef();
					}
				}
			}
		} else {
			logger.debug("Leaving");
			return auditHeader;
		}
	
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		
		// If Record action is Save then, no need to do any accounting
		if(StringUtils.equals(finMain.getRoleCode(), finMain.getNextRoleCode()) ||
				StringUtils.equals(finMain.getRecordStatus(), PennantConstants.RCD_STATUS_RESUBMITTED)){
			logger.debug("Leaving");
			return auditHeader;
		}
		
		FinanceProfitDetail pftDetail;
		if(StringUtils.equals(FinanceConstants.FINSER_EVENT_ORG, financeDetail.getModuleDefiner())){
			pftDetail = new FinanceProfitDetail();
		}else{
			pftDetail = getProfitDetailsDAO().getFinProfitDetailsById(finMain.getFinReference());
		}

		List<Long> acSetIdList = null;
		if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
			acSetIdList = getFinanceReferenceDetailDAO().getRefIdListByRefType(finMain.getPromotionCode(), 
					finMain.getRcdMaintainSts(), finMain.getRoleCode(), FinanceConstants.PROCEDT_STAGEACC);
		} else {
			acSetIdList = getFinanceReferenceDetailDAO().getRefIdListByRefType(finMain.getFinType(), 
					finMain.getRcdMaintainSts(), finMain.getRoleCode(), FinanceConstants.PROCEDT_STAGEACC);
		}
		
		// If NO Accounting Sets added against stage, no action to be done
		if(acSetIdList == null || acSetIdList.isEmpty()){
			logger.debug("Leaving");
			return auditHeader;
		}
		
		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		aeEvent = AEAmounts.procAEAmounts(finMain, financeDetail.getFinScheduleData().getFinanceScheduleDetails(),
				pftDetail, AccountEventConstants.ACCEVENT_STAGE, valueDate,valueDate);
		
		aeEvent.getAcSetIDList().addAll(acSetIdList);
		amountCodes = aeEvent.getAeAmountCodes();
		aeEvent.setModuleDefiner(financeDetail.getModuleDefiner());
		aeEvent.setPostingUserBranch(auditHeader.getAuditBranchCode());
		aeEvent.setPostDate(DateUtility.getAppDate());
		
		HashMap<String, Object>	dataMap = aeEvent.getDataMap();
		dataMap = prepareFeeRulesMap(amountCodes, dataMap, financeDetail);
		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);

		// Check Previously done Postings on same Stage with Reference
		//=======================================
		List<ReturnDataSet> newStageAcEntries = null;
		try {
			aeEvent.setDataMap(dataMap);
			aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
			newStageAcEntries = aeEvent.getReturnDataSet();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			ArrayList<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
			errorDetails.add(new ErrorDetail("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
					"Accounting Engine Failed to Create Postings:" + e.getMessage(), new String[] {}, new String[] {}));
			auditHeader.setErrorList(errorDetails);
			logger.debug("Leaving");
			return auditHeader;
		}

		// Check If there is no difference in Postings, if already executed on process of workflow
		if (!prvStageAccountingCheck(newStageAcEntries, finMain.getFinReference(),
				finMain.getRcdMaintainSts(), finMain.getRoleCode())) {
			logger.debug(Labels.getLabel("label_Finance_Recal_StageAccountings"));
			logger.debug("Leaving");
			return auditHeader;
		}

		// Prepared Postings execution 
		aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);

		// Stage Accounting Entry Details Saving
		//=======================================
		if (auditHeader.getErrorMessage() == null || auditHeader.getErrorMessage().isEmpty()) {

			FinStageAccountingLog stageAccountingLog = new FinStageAccountingLog();
			stageAccountingLog.setFinReference(finMain.getFinReference());
			stageAccountingLog.setFinEvent(finMain.getRcdMaintainSts());
			stageAccountingLog.setRoleCode(finMain.getRoleCode());
			stageAccountingLog.setLinkedTranId(aeEvent.getLinkedTranId());
			stageAccountingLog.setReceiptNo(receiptNumber);
			getFinStageAccountingLogDAO().saveStageAccountingLog(stageAccountingLog);
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Checking Process of Stage accounting Entry Details with Previous Execution if any. </br>
	 * 
	 * If already Stage Accounting Done in same same Role </br> a) Get Transaction ID of Previously executed List using
	 * Reference & RoleID </br> b) Get transactions/Postings with Transaction ID </br> c) Cross check with Existing List
	 * & Previously executed List </br> d) Any Mismatch, Reverse Earlier Transaction and Do New Transaction on next step
	 * </br> e) Update Movements List if Reversal happens on next step </br> </br>
	 * 
	 * If There are no Previous Stage Accounting Entries on Same Role with Same Finance Reference </br>
	 * 
	 * a) Nothing check on Previous Posting Entries </br> b) Do Normal Stage accounting Postings on next step </br> c)
	 * Create a Movement of Stage Accounting after successful postings on next step </br>
	 * 
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException
	 */
	protected boolean prvStageAccountingCheck(List<ReturnDataSet> curStageAccEntries, String finReference,
			String finEvent, String roleCode) {
		logger.debug("Entering");

		// Check Previously Executed Stage Accounting Entries
		long linkedTranId = getFinStageAccountingLogDAO().getLinkedTranId(finReference, finEvent, roleCode);
		if (linkedTranId == 0) {
			logger.debug("Leaving");
			return true;
		}

		// If Stage Accounting already completed on same Stage
		List<ReturnDataSet> excdStageAccList = getPostingsDAO().getPostingsByLinkTransId(linkedTranId);

		for (ReturnDataSet returnDataSet : curStageAccEntries) {

			returnDataSet.setLinkedTranId(linkedTranId);

			if (returnDataSet.getPostAmount().compareTo(BigDecimal.ZERO) < 0) {

				String tranCode = returnDataSet.getTranCode();
				String revTranCode = returnDataSet.getRevTranCode();
				String debitOrCredit = returnDataSet.getDrOrCr();

				returnDataSet.setTranCode(revTranCode);
				returnDataSet.setRevTranCode(tranCode);

				returnDataSet.setPostAmount(returnDataSet.getPostAmount().negate());

				if (debitOrCredit.equals(AccountConstants.TRANTYPE_CREDIT)) {
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
				} else {
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
				}
			}
		}

		// List Conversion into Map Details for Easy Cross Check
		Map<String, ReturnDataSet> excdMapDataSet = new HashMap<String, ReturnDataSet>();
		for (ReturnDataSet returnDataSet : excdStageAccList) {
			if (excdMapDataSet.containsKey(returnDataSet.getTranOrderId())) {
				excdMapDataSet.remove(returnDataSet.getTranOrderId());
			}
			excdMapDataSet.put(returnDataSet.getTranOrderId(), returnDataSet);
		}

		// Checking with Currently Execution List of Stage Accounting Entries
		boolean reExecuteStgAc = false;
		for (ReturnDataSet set : curStageAccEntries) {

			if (!excdMapDataSet.containsKey(set.getTranOrderId())) {
				reExecuteStgAc = true;
				break;
			}

			ReturnDataSet excdSet = excdMapDataSet.get(set.getTranOrderId());

			if (!StringUtils.equals(excdSet.getAccount(), set.getAccount())
					|| !StringUtils.equals(excdSet.getTranCode(), set.getTranCode())
					|| !StringUtils.equals(excdSet.getRevTranCode(), set.getRevTranCode())
					|| !StringUtils.equals(excdSet.getAcCcy(), set.getAcCcy())
					|| !StringUtils.equals(excdSet.getDrOrCr(), set.getDrOrCr())
					|| excdSet.getPostAmount().compareTo(set.getPostAmount()) != 0) {
				reExecuteStgAc = true;
				break;
			}
		}

		//Reverse previously executed stage accounting entries in Core Banking System
		if (reExecuteStgAc) {
			getPostingsPreparationUtil().postReversalsByLinkedTranID(linkedTranId);
		}

		//Delete Entry log after Finance Stage Accounting Reversal on particular Stage/Role
		if (reExecuteStgAc) {
			getFinStageAccountingLogDAO().deleteByRefandRole(finReference, finEvent, roleCode);
		}

		excdMapDataSet = null;
		logger.debug("Leaving");
		return reExecuteStgAc;

	}

	/**
	 * Method for Processing Reversal/Cancel of All Transaction Entries in Case of record Rejection
	 * 
	 * @param finReference
	 * @throws InterfaceException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	protected void cancelStageAccounting(String finReference, String finEvent) {

		List<Long> excdTranIdList = getFinStageAccountingLogDAO().getLinkedTranIdList(finReference, finEvent);
		if (excdTranIdList != null && !excdTranIdList.isEmpty()) {
			for (Long linkedTranId : excdTranIdList) {
				getPostingsPreparationUtil().postReversalsByLinkedTranID(linkedTranId);
				logger.debug("Reverse Transaction Success for Transaction ID : " + linkedTranId);
			}
		}
	}

	/**
	 * Method for Saving Next payment details For Salaried Account
	 * 
	 * @param finScheduleData
	 * @param oldSchd
	 */
	protected void saveFinSalPayment(FinScheduleData finScheduleData, FinanceScheduleDetail oldSchd, boolean dftZeoPay) {
		logger.debug("Entering");

		if (oldSchd == null
				|| !StringUtils.equals(finScheduleData.getFinanceMain().getFinRepayMethod(),
						FinanceConstants.REPAYMTH_AUTO)) {
			logger.debug("Leaving");
			return;
		}

		boolean saveFinSalPay = false;
		Date curBDay = DateUtility.getAppDate();

		//Finding ut Next payment Schedule Date
		FinanceScheduleDetail newSchd = null;
		if (!dftZeoPay) {
			for (FinanceScheduleDetail schd : finScheduleData.getFinanceScheduleDetails()) {
				if (schd.getSchDate().compareTo(curBDay) >= 0 && schd.isRepayOnSchDate()) {
					newSchd = schd;
					break;
				}
			}

			//Failed to Fetch Next Installment Date or No data exists after Next Business Date.
			if (newSchd == null) {
				logger.debug("Leaving");
				return;
			}

			if (oldSchd.getSchDate().compareTo(newSchd.getSchDate()) == 0) {
				if (oldSchd.getProfitSchd().compareTo(newSchd.getProfitSchd()) != 0
						|| oldSchd.getPrincipalSchd().compareTo(newSchd.getPrincipalSchd()) != 0) {
					saveFinSalPay = true;
				}
			} else {
				saveFinSalPay = true;
			}

			if (!saveFinSalPay) {
				logger.debug("Leaving");
				return;
			}
		}

		// Save Salaried Installment Details
		FinSalariedPayment salariedPayment = new FinSalariedPayment();
		salariedPayment.setFinReference(finScheduleData.getFinanceMain().getFinReference());
		salariedPayment.setPriAccount(finScheduleData.getFinanceMain().getRepayAccountId());

		List<SecondaryAccount> accList = finScheduleData.getFinanceMain().getSecondaryAccount();
		String secAccount = "";
		if (accList != null && !accList.isEmpty()) {
			for (int i = 0; i < accList.size(); i++) {
				secAccount = secAccount.concat(accList.get(i).getAccountNumber() + ",");
			}
			if (secAccount.endsWith(",")) {
				secAccount = secAccount.substring(0, secAccount.length() - 2);
			}
		}
		salariedPayment.setSecAccount(secAccount);
		if (!dftZeoPay) {
			salariedPayment.setNextPayDate(newSchd.getSchDate());
			salariedPayment.setNextPayment(newSchd.getProfitSchd().add(newSchd.getPrincipalSchd())
					.subtract(newSchd.getSchdPftPaid().subtract(newSchd.getSchdPriPaid())));
		} else {
			salariedPayment.setNextPayDate(oldSchd.getSchDate());
			salariedPayment.setNextPayment(BigDecimal.ZERO);
		}
		salariedPayment.setValueDate(DateUtility.getSysDate());

		//Save Next Payment Detail
		getExtTablesDAO().saveFinSalariedPayment(salariedPayment);

		logger.debug("Leaving");
	}

	/**
	 * Method for Add a Movement Entry for Commitment Disbursement Event
	 * 
	 * @param commitment
	 * @param financeMain
	 * @param postAmount
	 * @param linkedtranId
	 * @return
	 */
	public CommitmentMovement prepareCommitMovement(Commitment commitment, FinanceMain financeMain,
			BigDecimal postAmount, long linkedtranId) {
		CommitmentMovement movement = new CommitmentMovement();
		Date curBussDate = DateUtility.getAppDate();

		movement.setCmtReference(commitment.getCmtReference());
		movement.setFinReference(financeMain.getFinReference());
		movement.setFinBranch(financeMain.getFinBranch());
		movement.setFinType(financeMain.getFinType());
		movement.setMovementDate(curBussDate);
		movement.setMovementOrder(1);
		movement.setMovementType("DA");
		movement.setMovementAmount(postAmount);
		movement.setCmtAmount(commitment.getCmtAmount());
		movement.setCmtUtilizedAmount(commitment.getCmtUtilizedAmount().add(postAmount));
		if (commitment.getCmtExpDate().compareTo(curBussDate) < 0) {
			movement.setCmtAvailable(BigDecimal.ZERO);
		} else {
			movement.setCmtAvailable(commitment.getCmtAvailable().subtract(postAmount));
		}
		movement.setCmtCharges(BigDecimal.ZERO);
		movement.setLinkedTranId(linkedtranId);
		movement.setVersion(1);
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
	 * Method to save profit details
	 * 
	 * @param finScheduleData
	 * */
	protected FinanceProfitDetail doSave_PftDetails(FinanceProfitDetail profitDetail, boolean isNew) {
		logger.debug("Entering");

		// Save/Update Finance Profit Details
		FinanceProfitDetail finProfitDetails = profitDetail;
		if (isNew) {
			getProfitDetailsDAO().save(finProfitDetails);
		} else {
			getProfitDetailsDAO().update(finProfitDetails, false);
		}

		logger.debug("Leaving");
		return finProfitDetails;
	}

	/**
	 * Method to delete schedule, disbursement, deferementheader, repayinstruction, ratechanges lists.
	 * 
	 * @param scheduleData
	 * @param tableType
	 * @param isWIF
	 */
	public void listDeletion(FinScheduleData scheduleData, String finEvent, String tableType, boolean isWIF) {
		logger.debug("Entering ");

		getFinanceScheduleDetailDAO().deleteByFinReference(scheduleData.getFinReference(), tableType, isWIF, 0);
		getFinanceDisbursementDAO().deleteByFinReference(scheduleData.getFinReference(), tableType, isWIF, 0);
		getRepayInstructionDAO().deleteByFinReference(scheduleData.getFinReference(), tableType, isWIF, 0);

		//Fee Charge Details & Finance Overdue PenaltyRate Details
		if (StringUtils.isNotBlank(tableType) || isWIF) {
			getFinFeeChargesDAO().deleteChargesBatch(scheduleData.getFinReference(), finEvent, isWIF, tableType);
		}

		if (!isWIF) {
			getFinODPenaltyRateDAO().delete(scheduleData.getFinReference(), tableType);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method to save what if inquiry lists
	 */
	public void listSave(FinScheduleData finDetail, String tableType, boolean isWIF, long logKey) {
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
			finDetail.getFinanceScheduleDetails().get(i).setLogKey(logKey);
		}

		getFinanceScheduleDetailDAO().saveList(finDetail.getFinanceScheduleDetails(), tableType, isWIF);

		// Finance Disbursement Details
		mapDateSeq = new HashMap<Date, Integer>();
		Date curBDay = DateUtility.getAppDate();
		for (int i = 0; i < finDetail.getDisbursementDetails().size(); i++) {
			finDetail.getDisbursementDetails().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
			finDetail.getDisbursementDetails().get(i).setDisbIsActive(true);
			finDetail.getDisbursementDetails().get(i).setDisbDisbursed(true);
			finDetail.getDisbursementDetails().get(i).setLogKey(logKey);
		}
		getFinanceDisbursementDAO().saveList(finDetail.getDisbursementDetails(), tableType, isWIF);

		//Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getRepayInstructions().get(i).setLogKey(logKey);
		}
		getRepayInstructionDAO().saveList(finDetail.getRepayInstructions(), tableType, isWIF);

		//Finance Overdue Penalty Rates
		if (!isWIF && logKey == 0) {
			FinODPenaltyRate penaltyRate = finDetail.getFinODPenaltyRate();
			if (penaltyRate == null) {
				penaltyRate = new FinODPenaltyRate();
				penaltyRate.setApplyODPenalty(false);
				penaltyRate.setODIncGrcDays(false);
				penaltyRate.setODChargeType("");
				penaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
				penaltyRate.setODChargeCalOn("");
				penaltyRate.setODGraceDays(0);
				penaltyRate.setODAllowWaiver(false);
				penaltyRate.setODMaxWaiverPerc(BigDecimal.ZERO);
			}
			penaltyRate.setFinReference(finDetail.getFinReference());
			penaltyRate.setFinEffectDate(DateUtility.getSysDate());
			getFinODPenaltyRateDAO().save(penaltyRate, tableType);
		}

		if (finDetail.getFinServiceInstructions() != null && finDetail.getFinServiceInstructions().size() > 0) {
			getFinServiceInstructionDAO().saveList(finDetail.getFinServiceInstructions(), tableType);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method for Inserting IRR values against Finance
	 * @param baseIRRFeeTypes
	 * @param finReference 
	 * @param tableType
	 */
	public void saveFinIRR(List<FinIRRDetails> finIrrDetailsList, String finReference, TableType tableType){
		logger.debug("Entering");
		
		if(finIrrDetailsList != null && !finIrrDetailsList.isEmpty()){
			for (FinIRRDetails finIrrDetails : finIrrDetailsList) {
				finIrrDetails.setFinReference(finReference);
			}
			getFinIRRDetailsDAO().saveList(finIrrDetailsList, tableType);
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * Method for Inserting IRR values against Finance
	 * @param baseIRRFeeTypes
	 * @param tableType
	 */
	public void deleteFinIRR(String finReference, TableType tableType){
		logger.debug("Entering");
		getFinIRRDetailsDAO().deleteList(finReference, tableType);
		logger.debug("Leaving ");
	}
	
	/**
	 * Method for saving List of Fee Charge details
	 * 
	 * @param finDetail
	 * @param tableType
	 */
	public void saveFeeChargeList(FinScheduleData finScheduleData, String finEvent, boolean isWIF, String tableType) {
		logger.debug("Entering");

		//Finance Fee Charge Details
		if (finScheduleData.getFeeRules() != null && finScheduleData.getFeeRules().size() > 0) {
			for (int i = 0; i < finScheduleData.getFeeRules().size(); i++) {
				finScheduleData.getFeeRules().get(i).setFinReference(finScheduleData.getFinReference());
				finScheduleData.getFeeRules().get(i).setFinEvent(finEvent);
			}
			getFinFeeChargesDAO().saveChargesBatch(finScheduleData.getFeeRules(), isWIF, tableType);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for saving List of Secondary Account Details
	 * 
	 * @param finDetail
	 * @param tableType
	 */

	public void saveSecondaryAccountList(FinScheduleData finScheduleData, String moduleDefiner, boolean isWIF,
			String tableType) {
		logger.debug("Entering");
		if (finScheduleData.getFinanceMain().getSecondaryAccount() != null
				&& finScheduleData.getFinanceMain().getSecondaryAccount().size() > 0) {

			for (int i = 0; i < finScheduleData.getFinanceMain().getSecondaryAccount().size(); i++) {
				finScheduleData.getFinanceMain().getSecondaryAccount().get(i)
				.setFinReference(finScheduleData.getFinReference());
				finScheduleData.getFinanceMain().getSecondaryAccount().get(i).setFinEvent(moduleDefiner);
			}

			if (getSecondaryAccountDAO().getSecondaryAccountsByFinRef(
					finScheduleData.getFinanceMain().getFinReference(), "_View") != null) {

				getSecondaryAccountDAO().delete(finScheduleData.getFinanceMain().getFinReference(), "_temp");
				getSecondaryAccountDAO().save(finScheduleData.getFinanceMain().getSecondaryAccount(), moduleDefiner,
						tableType);

			} else {
				getSecondaryAccountDAO().save(finScheduleData.getFinanceMain().getSecondaryAccount(), moduleDefiner,
						tableType);

			}

		} else {
			getSecondaryAccountDAO().delete(finScheduleData.getFinanceMain().getFinReference(), "_temp");
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for saving List of Fee Charge details
	 * 
	 * @param finDetail
	 * @param tableType
	 */
	public void saveStepDetailList(FinScheduleData finScheduleData, boolean isWIF, String tableType) {
		logger.debug("Entering");

		if (finScheduleData.getStepPolicyDetails() != null && finScheduleData.getStepPolicyDetails().size() > 0) {
			//Finance Fee Charge Details
			for (int i = 0; i < finScheduleData.getStepPolicyDetails().size(); i++) {
				finScheduleData.getStepPolicyDetails().get(i).setFinReference(finScheduleData.getFinReference());
			}
			getFinanceStepDetailDAO().saveList(finScheduleData.getStepPolicyDetails(), isWIF, tableType);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public FinScheduleData getFinSchDataByFinRef(String finReference, String type, long logKey) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		if (logKey == 0) {
			finSchData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));
		}

		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type,
				false));

		if (logKey != 0) {
			finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference,
					type, false));
		}

		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

		if (logKey == 0) {
			String finType = finSchData.getFinanceMain().getFinType();
			finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(finType, "_AView"));
			finSchData.setFeeRules(getFinFeeChargesDAO().getFeeChargesByFinRef(finReference, "", false, ""));
			// Finance Fee Details
			finSchData.setFinFeeDetailList(getFinFeeDetailService().getFinFeeDetailById(finReference, false, ""));

			finSchData.setRepayDetails(getFinanceRepaymentsByFinRef(finReference, false));
			finSchData.setPenaltyDetails(getFinancePenaltysByFinRef(finReference));
			finSchData.setAccrueValue(getProfitDetailsDAO().getAccrueAmount(finReference));
		}
		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Method to get FinanceRepayments By FinReference
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<FinanceRepayments> getFinanceRepaymentsByFinRef(final String id, boolean isRpyCancelProc) {
		return getFinanceRepaymentsDAO().getFinRepayListByFinRef(id, isRpyCancelProc, "");
	}

	/**
	 * Method to get FinanceRepayments By FinReference
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<OverdueChargeRecovery> getFinancePenaltysByFinRef(final String id) {
		return getRecoveryDAO().getFinancePenaltysByFinRef(id, "");
	}
	
	/**
	 * Method for Validating Fee Details processed in the workflow Event & linking with Accounting Set
	 */
	public boolean validateAccSetFees(List<FinFeeDetail> feeList, List<Long> acSetIDList, String event){
		
		// Linked Fee Codes against Accounting Set Transactions
		boolean isFeeMatched = true;
		List<String> feeCodeList = getTransactionEntryDAO().getFeeCodeList(acSetIDList);
		for (FinFeeDetail feeDetail : feeList) {
			
			if(!StringUtils.equals(event, feeDetail.getFinEvent())){
				continue;
			}
			if(!feeCodeList.contains(feeDetail.getFeeTypeCode())){
				isFeeMatched = false;
				break;
			}
		}
		
		return isFeeMatched;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
		return customerStatusCodeDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
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

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public FinODPenaltyRateDAO getFinODPenaltyRateDAO() {
		return finODPenaltyRateDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public CustomerDocumentDAO getCustomerDocumentDAO() {
		return customerDocumentDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public EligibilityDetailService getEligibilityDetailService() {
		return eligibilityDetailService;
	}

	public void setEligibilityDetailService(EligibilityDetailService eligibilityDetailService) {
		this.eligibilityDetailService = eligibilityDetailService;
	}

	public GuarantorDetailService getGuarantorDetailService() {
		return guarantorDetailService;
	}

	public void setGuarantorDetailService(GuarantorDetailService guarantorDetailService) {
		this.guarantorDetailService = guarantorDetailService;
	}

	public JointAccountDetailService getJointAccountDetailService() {
		return jointAccountDetailService;
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public AgreementDetailService getAgreementDetailService() {
		return agreementDetailService;
	}

	public void setAgreementDetailService(AgreementDetailService agreementDetailService) {
		this.agreementDetailService = agreementDetailService;
	}

	public ScoringDetailService getScoringDetailService() {
		return scoringDetailService;
	}

	public void setScoringDetailService(ScoringDetailService scoringDetailService) {
		this.scoringDetailService = scoringDetailService;
	}

	public CheckListDetailService getCheckListDetailService() {
		return checkListDetailService;
	}

	public void setCheckListDetailService(CheckListDetailService checkListDetailService) {
		this.checkListDetailService = checkListDetailService;
	}

	public ContractorAssetDetailService getContractorAssetDetailService() {
		return contractorAssetDetailService;
	}

	public void setContractorAssetDetailService(ContractorAssetDetailService contractorAssetDetailService) {
		this.contractorAssetDetailService = contractorAssetDetailService;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public CommitmentDAO getCommitmentDAO() {
		return commitmentDAO;
	}

	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
		this.commitmentDAO = commitmentDAO;
	}

	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
		return finLogEntryDetailDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return recoveryDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return profitDetailsDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setCommitmentMovementDAO(CommitmentMovementDAO commitmentMovementDAO) {
		this.commitmentMovementDAO = commitmentMovementDAO;
	}

	public CommitmentMovementDAO getCommitmentMovementDAO() {
		return commitmentMovementDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public AccountProcessUtil getAccountProcessUtil() {
		return accountProcessUtil;
	}

	public void setAccountProcessUtil(AccountProcessUtil accountProcessUtil) {
		this.accountProcessUtil = accountProcessUtil;
	}

	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}

	public FinFeeChargesDAO getFinFeeChargesDAO() {
		return finFeeChargesDAO;
	}

	public void setFinFeeChargesDAO(FinFeeChargesDAO finFeeChargesDAO) {
		this.finFeeChargesDAO = finFeeChargesDAO;
	}

	public FinFeeScheduleDetailDAO getFinFeeScheduleDetailDAO() {
		return finFeeScheduleDetailDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinStatusDetailDAO getFinStatusDetailDAO() {
		return finStatusDetailDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	public SuspensePostingUtil getSuspensePostingUtil() {
		return suspensePostingUtil;
	}

	public void setSuspensePostingUtil(SuspensePostingUtil suspensePostingUtil) {
		this.suspensePostingUtil = suspensePostingUtil;
	}

	public OverDueRecoveryPostingsUtil getRecoveryPostingsUtil() {
		return recoveryPostingsUtil;
	}

	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
		this.recoveryPostingsUtil = recoveryPostingsUtil;
	}

	public FinanceDeviationsService getDeviationDetailsService() {
		return deviationDetailsService;
	}

	public void setDeviationDetailsService(FinanceDeviationsService deviationDetailsService) {
		this.deviationDetailsService = deviationDetailsService;
	}

	public FinMandateService getFinMandateService() {
		return finMandateService;
	}

	public void setFinMandateService(FinMandateService finMandateService) {
		this.finMandateService = finMandateService;
	}

	public FinanceStepDetailDAO getFinanceStepDetailDAO() {
		return financeStepDetailDAO;
	}

	public void setFinanceStepDetailDAO(FinanceStepDetailDAO financeStepDetailDAO) {
		this.financeStepDetailDAO = financeStepDetailDAO;
	}

	public BlackListCustomerDAO getBlacklistCustomerDAO() {
		return blacklistCustomerDAO;
	}

	public void setBlacklistCustomerDAO(BlackListCustomerDAO blacklistCustomerDAO) {
		this.blacklistCustomerDAO = blacklistCustomerDAO;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public FinanceDedupeDAO getFinanceDedupeDAO() {
		return financeDedupeDAO;
	}

	public void setFinanceDedupeDAO(FinanceDedupeDAO financeDedupeDAO) {
		this.financeDedupeDAO = financeDedupeDAO;
	}

	public PoliceCaseDAO getPoliceCaseDAO() {
		return policeCaseDAO;
	}

	public void setPoliceCaseDAO(PoliceCaseDAO policeCaseDAO) {
		this.policeCaseDAO = policeCaseDAO;
	}

	public CustomerDedupDAO getCustomerDedupDAO() {
		return customerDedupDAO;
	}

	public void setCustomerDedupDAO(CustomerDedupDAO customerDedupDAO) {
		this.customerDedupDAO = customerDedupDAO;
	}

	public FinStageAccountingLogDAO getFinStageAccountingLogDAO() {
		return finStageAccountingLogDAO;
	}

	public void setFinStageAccountingLogDAO(FinStageAccountingLogDAO finStageAccountingLogDAO) {
		this.finStageAccountingLogDAO = finStageAccountingLogDAO;
	}

	public FinCollateralsDAO getFinCollateralsDAO() {
		return finCollateralsDAO;
	}

	public void setFinCollateralsDAO(FinCollateralsDAO finCollateralsDAO) {
		this.finCollateralsDAO = finCollateralsDAO;
	}

	public FinCollateralService getFinCollateralService() {
		return finCollateralService;
	}

	public void setFinCollateralService(FinCollateralService finCollateralService) {
		this.finCollateralService = finCollateralService;
	}

	public EtihadCreditBureauDetailService getEtihadCreditBureauDetailService() {
		return etihadCreditBureauDetailService;
	}

	public void setEtihadCreditBureauDetailService(EtihadCreditBureauDetailService etihadCreditBureauDetailService) {
		this.etihadCreditBureauDetailService = etihadCreditBureauDetailService;
	}

	public BundledProductsDetailService getBundledProductsDetailService() {
		return bundledProductsDetailService;
	}

	public void setBundledProductsDetailService(BundledProductsDetailService bundledProductsDetailService) {
		this.bundledProductsDetailService = bundledProductsDetailService;
	}

	public FinAssetEvaluationService getFinAssetEvaluationService() {
		return finAssetEvaluationService;
	}

	public void setFinAssetEvaluationService(FinAssetEvaluationService finAssetEvaluationService) {
		this.finAssetEvaluationService = finAssetEvaluationService;
	}

	public FinAdvancePaymentsService getFinAdvancePaymentsService() {
		return finAdvancePaymentsService;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public FinCovenantTypeService getFinCovenantTypeService() {
		return finCovenantTypeService;
	}

	public void setFinCovenantTypeService(FinCovenantTypeService finCovenantTypeService) {
		this.finCovenantTypeService = finCovenantTypeService;
	}

	public RepaymentPostingsUtil getRepayPostingUtil() {
		return repayPostingUtil;
	}

	public void setRepayPostingUtil(RepaymentPostingsUtil repayPostingUtil) {
		this.repayPostingUtil = repayPostingUtil;
	}

	public SecondaryAccountDAO getSecondaryAccountDAO() {
		return secondaryAccountDAO;
	}

	public void setSecondaryAccountDAO(SecondaryAccountDAO secondaryAccountDAO) {
		this.secondaryAccountDAO = secondaryAccountDAO;
	}

	public AgreementFieldsDetailService getAgreementFieldsDetailService() {
		return agreementFieldsDetailService;
	}

	public void setAgreementFieldsDetailService(AgreementFieldsDetailService agreementFieldsDetailService) {
		this.agreementFieldsDetailService = agreementFieldsDetailService;
	}

	// Setters and Geeters for DOcumentManagerDAO proeprty
	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public ExtTablesDAO getExtTablesDAO() {
		return extTablesDAO;
	}

	public void setExtTablesDAO(ExtTablesDAO extTablesDAO) {
		this.extTablesDAO = extTablesDAO;
	}

	public FinFlagDetailsDAO getFinFlagDetailsDAO() {
		return finFlagDetailsDAO;
	}

	public void setFinFlagDetailsDAO(FinFlagDetailsDAO finFlagDetailsDAO) {
		this.finFlagDetailsDAO = finFlagDetailsDAO;
	}

	public FinServiceInstrutionDAO getFinServiceInstructionDAO() {
		return finServiceInstructionDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	public FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}

	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

	public CollateralAssignmentValidation getCollateralAssignmentValidation() {
		if (collateralAssignmentValidation == null) {
			this.collateralAssignmentValidation = new CollateralAssignmentValidation(collateralAssignmentDAO);
		}
		return collateralAssignmentValidation;
	}

	public CollateralAssignmentDAO getCollateralAssignmentDAO() {
		return collateralAssignmentDAO;
	}

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public FinInsurancesDAO getFinInsurancesDAO() {
		return finInsurancesDAO;
	}

	public void setFinInsurancesDAO(FinInsurancesDAO finInsurancesDAO) {
		this.finInsurancesDAO = finInsurancesDAO;
	}

	public FinInsuranceValidation getFinInsuranceValidation() {
		if (finInsuranceValidation == null) {
			this.finInsuranceValidation = new FinInsuranceValidation(finInsurancesDAO);
		}
		return finInsuranceValidation;
	}

	public FinAssetTypesValidation getFinAssetTypesValidation() {
		if (finAssetTypesValidation == null) {
			this.finAssetTypesValidation = new FinAssetTypesValidation(finAssetTypeDAO);
		}
		return finAssetTypesValidation;
	}

	public FinAssetTypeDAO getFinAssetTypeDAO() {
		return finAssetTypeDAO;
	}

	public void setFinAssetTypeDAO(FinAssetTypeDAO finAssetTypeDAO) {
		this.finAssetTypeDAO = finAssetTypeDAO;
	}

	public FinPlanEmiHolidayDAO getFinPlanEmiHolidayDAO() {
		return finPlanEmiHolidayDAO;
	}

	public void setFinPlanEmiHolidayDAO(FinPlanEmiHolidayDAO finPlanEmiHolidayDAO) {
		this.finPlanEmiHolidayDAO = finPlanEmiHolidayDAO;
	}

	public CustomerQueuingDAO getCustomerQueuingDAO() {
		return customerQueuingDAO;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	public AccrualService getAccrualService() {
		return accrualService;
	}

	
	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	public DisbursementPostings getDisbursementPostings() {
		return disbursementPostings;
	}

	public void setDisbursementPostings(DisbursementPostings disbursementPostings) {
		this.disbursementPostings = disbursementPostings;
	}

	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}
	
	public InstallmentDueService getInstallmentDueService() {
		return installmentDueService;
	}

	public void setInstallmentDueService(InstallmentDueService installmentDueService) {
		this.installmentDueService = installmentDueService;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public FinIRRDetailsDAO getFinIRRDetailsDAO() {
		return finIRRDetailsDAO;
	}

	public void setFinIRRDetailsDAO(FinIRRDetailsDAO finIRRDetailsDAO) {
		this.finIRRDetailsDAO = finIRRDetailsDAO;
	}

	public QueryDetailService getQueryDetailService() {
		return queryDetailService;
	}

	public void setQueryDetailService(QueryDetailService queryDetailService) {
		this.queryDetailService = queryDetailService;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}
}
