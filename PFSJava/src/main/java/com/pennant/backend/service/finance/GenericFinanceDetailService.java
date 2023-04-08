/**
 * Copyright 2011 - Pennant Technologies
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
 *
 * FileName : GenericFinanceDetailService.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 *
 * 
 * 13-06-2018 Siva 0.2 Stage Accounting Modifications * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.InstallmentDueService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.OverDueRecoveryPostingsUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.SuspensePostingUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
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
import com.pennant.backend.dao.feetype.FeeTypeDAO;
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
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.finance.covenant.CovenantsDAO;
import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.dao.findedup.FinanceDedupeDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.FinFeeChargesDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.financeservice.RestructureService;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.AdvancePaymentDetail;
import com.pennant.backend.model.finance.AdviseDueTaxDetail;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinIRRDetails;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.service.collateral.impl.CollateralAssignmentValidation;
import com.pennant.backend.service.collateral.impl.FinAssetTypesValidation;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.covenant.CovenantsService;
import com.pennant.backend.service.finance.putcall.FinOptionService;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.backend.service.mandate.FinMandateService;
import com.pennant.backend.service.tandetails.TanAssignmentService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.subvention.service.SubventionService;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.advancepayment.service.AdvancePaymentService;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.rits.cloning.Cloner;

public abstract class GenericFinanceDetailService extends GenericService<FinanceDetail> {
	private static final Logger logger = LogManager.getLogger(GenericFinanceDetailService.class);

	protected AuditHeaderDAO auditHeaderDAO;
	protected CustomerStatusCodeDAO customerStatusCodeDAO;
	protected FinanceScheduleDetailDAO financeScheduleDetailDAO;
	protected FinanceDisbursementDAO financeDisbursementDAO;
	protected RepayInstructionDAO repayInstructionDAO;
	protected FinODPenaltyRateDAO finODPenaltyRateDAO;
	protected FinanceTypeDAO financeTypeDAO;
	protected DocumentDetailsDAO documentDetailsDAO;
	protected CustomerDocumentDAO customerDocumentDAO;
	protected CommitmentDAO commitmentDAO;
	protected PostingsDAO postingsDAO;
	protected FinanceProfitDetailDAO profitDetailsDAO;
	protected CommitmentMovementDAO commitmentMovementDAO;
	protected FinLogEntryDetailDAO finLogEntryDetailDAO;
	protected FinODDetailsDAO finODDetailsDAO;
	protected FinanceMainDAO financeMainDAO;
	protected FinanceRepaymentsDAO financeRepaymentsDAO;
	protected OverdueChargeRecoveryDAO recoveryDAO;
	protected FinanceSuspHeadDAO financeSuspHeadDAO;
	protected TransactionEntryDAO transactionEntryDAO;
	protected FinFeeChargesDAO finFeeChargesDAO;
	protected FinFeeScheduleDetailDAO finFeeScheduleDetailDAO;
	protected FinanceStepDetailDAO financeStepDetailDAO;
	protected BlackListCustomerDAO blacklistCustomerDAO;
	protected FinanceDedupeDAO financeDedupeDAO;
	protected CustomerDedupDAO customerDedupDAO;
	protected FinStageAccountingLogDAO finStageAccountingLogDAO;
	protected FinCollateralsDAO finCollateralsDAO;
	protected FinTypeAccountingDAO finTypeAccountingDAO;
	protected FinPlanEmiHolidayDAO finPlanEmiHolidayDAO;
	protected FinFeeDetailDAO finFeeDetailDAO;
	protected FinanceReferenceDetailDAO financeReferenceDetailDAO;
	protected FinanceTaxDetailDAO financeTaxDetailDAO;
	protected AccountEngineExecution engineExecution;
	protected CustomerDAO customerDAO;
	protected PostingsPreparationUtil postingsPreparationUtil;
	protected FinStatusDetailDAO finStatusDetailDAO;
	protected SuspensePostingUtil suspensePostingUtil;
	protected OverDueRecoveryPostingsUtil recoveryPostingsUtil;
	protected FinanceDeviationsService deviationDetailsService;
	protected FinMandateService finMandateService;
	protected AccrualService accrualService;
	protected EligibilityDetailService eligibilityDetailService;
	protected GuarantorDetailService guarantorDetailService;
	protected JointAccountDetailService jointAccountDetailService;
	protected ScoringDetailService scoringDetailService;
	protected CheckListDetailService checkListDetailService;
	protected CustomerDetailsService customerDetailsService;
	protected FinCollateralService finCollateralService;
	protected FinAdvancePaymentsService finAdvancePaymentsService;
	protected FinFeeDetailService finFeeDetailService;
	protected FinCovenantTypeService finCovenantTypeService;
	protected RepaymentPostingsUtil repaymentPostingsUtil;
	protected FinFlagDetailsDAO finFlagDetailsDAO;
	protected FinServiceInstrutionDAO finServiceInstructionDAO;
	protected CollateralAssignmentValidation collateralAssignmentValidation;
	protected CollateralAssignmentDAO collateralAssignmentDAO;
	protected FinAssetTypesValidation finAssetTypesValidation;
	protected FinAssetTypeDAO finAssetTypeDAO;
	protected InstallmentDueService installmentDueService;
	protected AdvancePaymentService advancePaymentService;
	protected FinIRRDetailsDAO finIRRDetailsDAO;
	protected VehicleDealerService vehicleDealerService;
	protected CustomerQueuingDAO customerQueuingDAO;
	protected GSTInvoiceTxnService gstInvoiceTxnService;
	protected QueryDetailService queryDetailService;
	protected FeeTypeDAO feeTypeDAO;
	protected ReceiptCalculator receiptCalculator;
	protected RuleDAO ruleDAO;
	protected CovenantsService covenantsService;
	protected FinOptionService finOptionService;
	protected CovenantsDAO covenantsDAO;
	protected ManualAdviseDAO manualAdviseDAO;
	protected AccountingSetDAO accountingSetDAO;
	protected SubventionService subventionService;
	protected RestructureService restructureService;
	protected FeeTypeService feeTypeService;
	protected TanAssignmentService tanAssignmentService;
	protected FeeDetailService feeDetailService;

	public GenericFinanceDetailService() {
		super();
	}

	/**
	 * Method to delete Co-borrower & Guarantors Details
	 * 
	 * @param finDetail
	 * @param tableType
	 * @return auditList
	 */
	public List<AuditDetail> jointGuarantorDeletion(FinanceDetail financeDetail, String tableType,
			String auditTranType) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		List<JointAccountDetail> jointAccountDetails = financeDetail.getJointAccountDetailList();
		List<GuarantorDetail> guarantorDetails = financeDetail.getGurantorsDetailList();

		if (jointAccountDetails != null && !jointAccountDetails.isEmpty()) {
			auditDetails.addAll(jointAccountDetailService.delete(jointAccountDetails, tableType, auditTranType));
		}

		if (guarantorDetails != null) {
			guarantorDetailService.delete(guarantorDetails, tableType, auditTranType);
		}

		logger.debug("Leaving ");
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
				// #PSD:168724-900 error for document
				// isRcdType = true;
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
	public List<AuditDetail> setCollateralAssignmentAuditData(FinanceDetail detail, String auditTranType,
			String method) {
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
				// Bug fix 16 july 2018 at collaterlal list item in delete
				// isRcdType = true;
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

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					collateralAssignment.getBefImage(), collateralAssignment));
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
				Object object = list.get(i).getModelData();
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

						AuditDetail auditDetail = new AuditDetail(transType, list.get(i).getAuditSeq(), fields[0],
								fields[1], befImg, object);
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
			FinanceMain financeMain, String procEdtEvent, long instructionUID) {
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
				documentDetails.setInstructionUID(instructionUID);

				if (DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode())) {
					approveRec = true;
				}
				if (DocumentCategories.FINANCE.getKey().equalsIgnoreCase(documentDetails.getCategoryCode())) {
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
					} else if (documentDetails.isNewRecord()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}

				if (approveRec
						&& !DocumentCategories.FINANCE.getKey().equalsIgnoreCase(documentDetails.getCategoryCode())) {
					rcdType = documentDetails.getRecordType();
					recordStatus = documentDetails.getRecordStatus();
					documentDetails.setRecordType("");
					documentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (approveRec
						&& StringUtils.equals(documentDetails.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)
						&& StringUtils.isBlank(financeMain.getNextRoleCode())) {
					documentDetails.setRecordType("");
				}

				documentDetails.setCustId(financeMain.getCustID());
				documentDetails.setFinReference(financeMain.getFinReference());

				if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(documentDetails.getRecordType()) && approveRec) {
					updateRecord = false;
				}

				if (saveRecord) {
					if (StringUtils.isEmpty(documentDetails.getReferenceId())) {
						documentDetails.setReferenceId(financeMain.getFinReference());
					}
					documentDetails.setFinEvent(procEdtEvent);
					// Save the document (documentDetails object) into DocumentManagerTable using
					// documentManagerDAO.save(?) get the long Id.
					// This will be used in the documentDetailsDAO.save, Update & delete methodss

					saveDocument(DMSModule.FINANCE, DMSModule.FINANCE, documentDetails);

					// Pass the docRefId here to save this in place of docImage column. Or add another column for now to
					// save this.
					/*
					 * if(!StringUtils.equals(rcdType, PennantConstants.RECORD_TYPE_UPD) && documentDetails.getDocId() >
					 * 0){ documentDetails.setDocId(0); }
					 */
					// Pass the docRefId here to save this in place of docImage column. Or add another column for now to
					// save this.
					if (!StringUtils.equals(rcdType, PennantConstants.RECORD_TYPE_UPD)
							&& documentDetails.getDocId() > 0) {
					}
					documentDetailsDAO.save(documentDetails, type);
				}

				if (updateRecord) {
					// When a document is updated, insert another file into the DocumentManager table's.
					// Get the new DocumentManager.id & set to documentDetails.getDocRefId()

					saveDocument(DMSModule.FINANCE, DMSModule.FINANCE, documentDetails);

					documentDetailsDAO.update(documentDetails, type);
				}

				if (deleteRecord && ((StringUtils.isEmpty(type) && !isTempRecord) || (StringUtils.isNotEmpty(type)))) {
					if (!type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
						documentDetailsDAO.delete(documentDetails, type);
						if (covenantsService != null) {
							covenantsService.deleteDocumentByDocumentId(documentDetails.getDocId(), type);
						} else {
							documentDetailsDAO.deleteDocumentByDocumentId(documentDetails, type);
						}
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
					customerDocumentDAO.save(custdoc, "");
				} else {
					customerDocumentDAO.update(custdoc, "");
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
		Date appDate = SysParamUtil.getAppDate();

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
				} else if (collateralAssignment.isNewRecord()) {
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
				movement.setValueDate(appDate);
				movement.setProcess(CollateralConstants.PROCESS_MANUAL);
				if (deleteRecord) {
					movement.setAssignPerc(BigDecimal.ZERO);
				}

				collateralAssignmentDAO.save(movement);
			}

			if (saveRecord) {
				collateralAssignmentDAO.save(collateralAssignment, type);
			}

			if (updateRecord) {
				collateralAssignmentDAO.update(collateralAssignment, type);
			}

			if (deleteRecord) {
				collateralAssignmentDAO.delete(collateralAssignment, type);
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
				} else if (finAssetTypes.isNewRecord()) {
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
				finAssetTypeDAO.save(finAssetTypes, type);
			}

			if (updateRecord) {
				finAssetTypeDAO.update(finAssetTypes, type);
			}

			if (deleteRecord) {
				finAssetTypeDAO.delete(finAssetTypes, type);
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

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setFinStepDetailAuditData(FinScheduleData finScheduleData, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		FinanceStepPolicyDetail fspdObj = new FinanceStepPolicyDetail();
		FinanceMain fm = finScheduleData.getFinanceMain();
		int i = 0;

		String[] fields = PennantJavaUtil.getFieldDetails(fspdObj, fspdObj.getExcludeFields());

		for (FinanceStepPolicyDetail fspd : finScheduleData.getStepPolicyDetails()) {
			if (StringUtils.isEmpty(fspd.getRecordType())) {
				i++;
				continue;
			}

			fspd.setWorkflowId(fm.getWorkflowId());
			boolean isRcdType = false;

			if (fspd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				fspd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (fspd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				fspd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (fspd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				fspd.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				fspd.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (fspd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (fspd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| fspd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			fspd.setRecordStatus(fm.getRecordStatus());
			fspd.setUserDetails(fm.getUserDetails());
			fspd.setLastMntOn(fm.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], fspd.getBefImage(), fspd));
			i++;
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	// Document Details List Maintainance
	public void listDocDeletion(FinanceDetail financeDetail, String tableType) {
		documentDetailsDAO.deleteList(new ArrayList<DocumentDetails>(financeDetail.getDocumentDetailsList()),
				tableType);
	}

	/**
	 * Get Customer Document
	 * 
	 * @param documentDetails
	 * @param financeMain
	 * @return
	 */
	private CustomerDocument getCustomerDocument(DocumentDetails documentDetails, FinanceMain financeMain) {
		CustomerDocument customerDocument = customerDocumentDAO.getCustomerDocumentById(financeMain.getCustID(),
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
	 * @param fm
	 * @param processType
	 * @param dateValueDate
	 * @param alwRIA
	 * @param curFinsts
	 * @param maxODDays
	 * @throws AccountNotFoundException
	 */
	protected void suspenseCheckProcess(FinanceMain fm, String processType, Date dateValueDate, String curFinsts,
			int maxODDays) throws InterfaceException {

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		boolean chkSuspProcess = false;

		// Checking Conditions for Suspense Calculations
		if (processType.equals(FinServiceEvent.POSTPONEMENT)) {

			// Get Current Maximum Overdue Days after Deletion Past Due Terms
			int curMaxODDays = finODDetailsDAO.getMaxODDaysOnDeferSchd(finID, null);
			if (curMaxODDays < maxODDays) {
				chkSuspProcess = true;
			}

		} else {
			chkSuspProcess = true;
		}

		if (chkSuspProcess) {

			// Deletion of Suspense Details Depends on Releases
			financeSuspHeadDAO.updateSuspFlag(finID);

			// Get Maximum Days Overdue Details Object with Overdue Amount
			FinODDetails odDetail = finODDetailsDAO.getMaxDaysFinODDetails(finID);

			// Recreation of Suspense Details , if Finance is in Suspense with Current Max Overdue Days
			if (odDetail != null) {
				FinRepayQueue repayQueue = new FinRepayQueue();

				repayQueue.setFinID(finID);
				repayQueue.setFinReference(finReference);
				repayQueue.setCustomerID(fm.getCustID());
				repayQueue.setBranch(fm.getFinBranch());
				repayQueue.setFinType(fm.getFinType());
				repayQueue.setRpyDate(odDetail.getFinODSchdDate());
				repayQueue.setFinRpyFor(odDetail.getFinODFor());

				try {
					suspensePostingUtil.suspensePreparation(fm, repayQueue, dateValueDate, true);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}

			// Customer Status Change Date & Status Update after Suspense Details updations
			String custSts = customerDAO.getCustWorstStsbyCurFinSts(fm.getCustID(), finReference, curFinsts);
			List<Long> custIdList = new ArrayList<Long>(1);
			custIdList.add(fm.getCustID());
			List<FinStatusDetail> suspDateSts = financeSuspHeadDAO.getCustSuspDate(custIdList);

			Date suspFromdate = null;
			if (suspDateSts != null && !suspDateSts.isEmpty()) {
				suspFromdate = suspDateSts.get(0).getValueDate();
			}

			FinStatusDetail statusDetail = new FinStatusDetail();
			List<FinStatusDetail> custStatuses = new ArrayList<FinStatusDetail>(1);
			statusDetail.setCustId(fm.getCustID());
			statusDetail.setFinStatus(custSts);
			statusDetail.setValueDate(suspFromdate);
			custStatuses.add(statusDetail);

			finStatusDetailDAO.updateCustStatuses(custStatuses);
		}
	}

	/**
	 * Preparing Accounting Data
	 * 
	 * @param financeDetail
	 * @param executingMap
	 * @return
	 * @throws InterruptedException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private AEEvent prepareAccountingData(FinanceDetail financeDetail, AEEvent aeEvent,
			FinanceProfitDetail profitDetail, Date valueDate) {

		Date curBDay = SysParamUtil.getAppDate();
		if (valueDate == null) {
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
		if (profitDetail != null) {// FIXME
			BeanUtils.copyProperties(profitDetail, newProfitDetail);
			totalPftSchdOld = profitDetail.getTotalPftSchd();
			totalPftCpzOld = profitDetail.getTotalPftCpz();
			totalPriSchdOld = profitDetail.getTotalpriSchd();
		}

		aeEvent = AEAmounts.procAEAmounts(finMain, finSchdDetails, profitDetail, eventCode, valueDate, curBDay);

		// TODO: PV: 28AUG19. No Separate Accounting COnfiuration Required for Promotions
		/*
		 * if (StringUtils.isBlank(finMain.getPromotionCode()) || finMain.getPromotionSeqId() == 0) {
		 * aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), eventCode,
		 * FinanceConstants.MODULEID_FINTYPE)); } else {
		 * aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(), eventCode,
		 * FinanceConstants.MODULEID_PROMOTION)); }
		 */

		aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), eventCode,
				FinanceConstants.MODULEID_FINTYPE));

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		accrualService.calProfitDetails(finMain, finSchdDetails, newProfitDetail, curBDay);
		if (StringUtils.equals(FinanceConstants.BPI_DISBURSMENT, finMain.getBpiTreatment())) {
			amountCodes.setBpi(finMain.getBpiAmount());
			if (TDSCalculator.isTDSApplicable(finMain,
					SysParamUtil.isAllowed(SMTParameterConstants.BPI_TDS_DEDUCT_ON_ORG))) {
				for (int i = 0; i < finSchdDetails.size(); i++) {
					FinanceScheduleDetail curSchd = finSchdDetails.get(i);
					if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
						amountCodes.setBpiTds(curSchd.getTDSAmount());
						break;
					}
				}
			}

			// BPI Payment on Installment Due Date
			if (SysParamUtil.isAllowed(SMTParameterConstants.BPI_PAID_ON_INSTDATE)) {
				amountCodes.setBpiToAdvInt(true);
			}
		}

		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();

		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		if (eventCode.equals(AccountingEvent.ADDDBSP)) {
			amountCodes.setQuickDisb(finMain.isQuickDisb());
			Promotion promotion = financeDetail.getPromotion();
			if (promotion != null && promotion.isDbd()) {
				amountCodes.setDbdAmount(finMain.getFinAmount().multiply(promotion.getDbdPerc())
						.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
			}
		}

		aeEvent.setModuleDefiner(StringUtils.isEmpty(financeDetail.getModuleDefiner()) ? FinServiceEvent.ORG
				: financeDetail.getModuleDefiner());
		if (financeDetail.getModuleDefiner().equals(FinServiceEvent.ORG)) {
			// FIXME: PV. 18AUG19. Some confusion. As downpayment was not deducted from current asset value earlier
			// addiing now gives double impact.
			if (ImplementationConstants.ALW_DOWNPAY_IN_LOANENQ_AND_SOA) {
				amountCodes.setDisburse(finMain.getFinCurrAssetValue().add(finMain.getDownPayment()));
			} else {
				amountCodes.setDisburse(finMain.getFinCurrAssetValue());
			}
		} else {
			amountCodes.setDisburse(newProfitDetail.getTotalpriSchd().subtract(totalPriSchdOld));
		}

		if (finMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(finMain.getRecordType())) {
			aeEvent.setNewRecord(true);
		}
		return aeEvent;
	}

	public List<ReturnDataSet> procesSubVenAccounting(AEEvent aeEvent, FinanceDetail fd, boolean doPostings) {
		String event = AccountingEvent.MANSUB;
		int moduleId = FinanceConstants.MODULEID_FINTYPE;
		String finType = fd.getFinScheduleData().getFinanceMain().getFinType();

		aeEvent.setAccountingEvent(event);
		aeEvent.getAcSetIDList().clear();
		aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finType, event, moduleId));
		aeEvent.setLinkedTranId(0);

		if (doPostings) {
			postingsPreparationUtil.postAccounting(aeEvent);
		} else {
			engineExecution.getAccEngineExecResults(aeEvent);
		}

		return aeEvent.getReturnDataSet();
	}

	/**
	 * Method for Execute posting Details on Core Banking Side
	 * 
	 * @param auditHeader
	 * @param curBDay
	 * @return
	 */
	public AuditHeader executeAccountingProcess(AuditHeader auditHeader, Date curBDay) throws InterfaceException {
		logger.debug("Entering");

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), fd);
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String eventCode = fd.getAccountingEventCode();
		FinanceProfitDetail pftDetail = new FinanceProfitDetail();
		AEEvent aeEvent = new AEEvent();
		Date valueDate = null;

		boolean isNew = false;
		long finID = fm.getFinID();

		if (FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
			pftDetail = new FinanceProfitDetail();
			isNew = true;

			// Added Value date as Finance Start Date in case of origination Disbursement
			valueDate = fm.getFinStartDate();
		} else {
			pftDetail = profitDetailsDAO.getFinProfitDetailsById(finID);
		}

		aeEvent = prepareAccountingData(fd, aeEvent, pftDetail, valueDate);

		// Fee Details Validation
		/*
		 * boolean isFeeConfgMatched = validateAccSetFees(financeDetail.getFinScheduleData().getFinFeeDetailList(),
		 * aeEvent.getAcSetIDList(), financeDetail.getAccountingEventCode()); if(!isFeeConfgMatched){
		 * auditHeader.setErrorDetails(new ErrorDetails("60212", null)); return auditHeader; }
		 */

		String branchCode = auditHeader.getAuditBranchCode();
		aeEvent.setPostingUserBranch(branchCode);
		aeEvent.setEntityCode(fm.getLovDescEntityCode());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		if (StringUtils.equals(eventCode, AccountingEvent.ADDDBSP)) {
			amountCodes.setQuickDisb(fm.isQuickDisb());
		}

		Map<String, Object> dataMap = aeEvent.getDataMap();
		Map<String, Object> gstExecutionMap = null;

		setFeesesForAccounting(aeEvent, fd);

		if (PennantConstants.FINSOURCE_ID_API.equals(fm.getFinSourceID()) && auditHeader.getApiHeader() != null
				&& FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
			String custDftBranch = null;
			String custResdSts = null;
			String highPriorityState = null;
			String highPriorityCountry = null;
			if (fd.getCustomerDetails() != null) {
				custDftBranch = fd.getCustomerDetails().getCustomer().getCustDftBranch();
				custResdSts = fd.getCustomerDetails().getCustomer().getResidentialStatus();

				List<CustomerAddres> addressList = fd.getCustomerDetails().getAddressList();
				if (CollectionUtils.isNotEmpty(addressList)) {
					for (CustomerAddres customerAddres : addressList) {
						if (customerAddres.getCustAddrPriority() == Integer
								.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
							highPriorityState = customerAddres.getCustAddrProvince();
							highPriorityCountry = customerAddres.getCustAddrCountry();
							break;
						}
					}
				}
			}

			gstExecutionMap = GSTCalculator.getGSTDataMap(fm.getFinBranch(), custDftBranch, highPriorityState,
					custResdSts, highPriorityCountry, fd.getFinanceTaxDetail(),
					fd.getCustomerDetails().getGstDetailsList());
		} else {
			gstExecutionMap = GSTCalculator.getGSTDataMap(fm.getFinID());
		}

		// Based on Each service instruction on every Servicing action postings should be done(Multiple times)
		// On Origination processing based on Service instructions is not required
		boolean feesExecuted = false;
		boolean subventionExists = false;
		if (FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
			subventionExists = setSubventionFeeToDataMap(branchCode, fd, fm, dataMap);
			dataMap = feeDetailService.prepareFeeRulesMap(amountCodes, dataMap, fd);

			// Advance payment Details Resetting
			AdvancePaymentDetail curAdvpay = AdvancePaymentUtil.getDiffOnAdvIntAndAdvEMI(schdData, null,
					FinServiceEvent.ORG);
			if (curAdvpay != null) {
				amountCodes.setIntAdjusted(curAdvpay.getAdvInt());
				amountCodes.setEmiAdjusted(curAdvpay.getAdvEMI());
				boolean advTDSInczUpf = SysParamUtil.isAllowed(SMTParameterConstants.ADVANCE_TDS_INCZ_UPF);
				if (!advTDSInczUpf) {
					amountCodes.setIntTdsAdjusted(BigDecimal.ZERO);
					amountCodes.setEmiTdsAdjusted(BigDecimal.ZERO);
				} else {
					amountCodes.setIntTdsAdjusted(curAdvpay.getAdvIntTds());
					amountCodes.setEmiTdsAdjusted(curAdvpay.getAdvEMITds());
				}

				fd.setAdvancePaymentDetail(curAdvpay);
			}

			dataMap = amountCodes.getDeclaredFieldValues(dataMap);

			if (gstExecutionMap != null) {
				for (String key : gstExecutionMap.keySet()) {
					if (StringUtils.isNotBlank(key)) {
						dataMap.put(key, gstExecutionMap.get(key));
					}
				}
			}

			setVASAcctCodes(fd, dataMap);

			aeEvent.setDataMap(dataMap);

			// Prepared Postings execution
			aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

			// Update Linked Transaction ID
			List<FinServiceInstruction> serviceInsts = schdData.getFinServiceInstructions();
			if (serviceInsts != null && !serviceInsts.isEmpty() && aeEvent != null && aeEvent.getLinkedTranId() > 0) {
				for (FinServiceInstruction inst : serviceInsts) {
					inst.setLinkedTranID(aeEvent.getLinkedTranId());
				}
			}

		} else {

			List<FinServiceInstruction> serviceInsts = schdData.getFinServiceInstructions();

			Cloner cloner = new Cloner();
			int instruction = 0;
			for (FinServiceInstruction inst : serviceInsts) {

				AEAmountCodes tempAmountCodes = cloner.deepClone(amountCodes);
				aeEvent.setDataMap(new HashMap<>());

				if (!feesExecuted) {// No segregation of fees based on instruction
					dataMap = feeDetailService.prepareFeeRulesMap(tempAmountCodes, dataMap, fd);
					setFeesesForAccounting(aeEvent, fd);
				}

				if (FinServiceEvent.ADDDISB.equals(fd.getModuleDefiner())) {
					tempAmountCodes.setDisburse(inst.getAmount());
					tempAmountCodes.setIntAdjusted(fm.getIntTdsAdjusted());
				} else if (FinServiceEvent.PART_CANCELLATION.equals(fd.getModuleDefiner())) {
					tempAmountCodes.setRefund(inst.getRefund());
				}
				tempAmountCodes.setPftChg(inst.getPftChg());

				dataMap = tempAmountCodes.getDeclaredFieldValues(dataMap);
				if (inst.getFromDate() != null) {
					aeEvent.setValueDate(inst.getFromDate());
				}
				aeEvent.setAeAmountCodes(tempAmountCodes);

				if (gstExecutionMap != null) {
					for (String key : gstExecutionMap.keySet()) {
						if (StringUtils.isNotBlank(key)) {
							dataMap.put(key, gstExecutionMap.get(key));
						}
					}
				}

				instruction++;
				// Advance payment Details Resetting
				if (instruction == serviceInsts.size() - 1) {
					if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())) {
						advancePaymentService.setAdvancePaymentDetails(fd, tempAmountCodes);
					}
				}

				dataMap = tempAmountCodes.getDeclaredFieldValues(dataMap);

				setVASAcctCodes(fd, dataMap);

				aeEvent.setDataMap(dataMap);

				if (FinServiceEvent.RESTRUCTURE.equals(fd.getModuleDefiner()) && restructureService != null) {
					restructureService.processRestructureAccounting(aeEvent, fd);
				}

				// Prepared Postings execution
				aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

				// Update Linked Transaction ID
				if (aeEvent != null && aeEvent.getLinkedTranId() > 0) {
					inst.setLinkedTranID(aeEvent.getLinkedTranId());
				}
			}
		}

		// BPI Updation Checking for Deduct from Disbursement case only
		if (FinServiceEvent.ORG.equals(fd.getModuleDefiner())
				&& FinanceConstants.BPI_DISBURSMENT.equals(fm.getBpiTreatment()) && aeEvent.isBpiIncomized()
				&& SysParamUtil.isAllowed(SMTParameterConstants.BPI_INCOMIZED_ON_ORG)
				&& !SysParamUtil.isAllowed(SMTParameterConstants.BPI_PAID_ON_INSTDATE)) {
			pftDetail.setAmzTillLBD(pftDetail.getAmzTillLBD().add(amountCodes.getBpi()));
		}

		// GST Invoice Preparation
		if (gstInvoiceTxnService != null && aeEvent.getLinkedTranId() > 0
				&& CollectionUtils.isNotEmpty(schdData.getFinFeeDetailList())) {
			boolean orgination = false;
			if (FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
				orgination = true;
			}

			// Normal Fees invoice preparation

			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
			invoiceDetail.setFinanceDetail(fd);
			invoiceDetail.setFinFeeDetailsList(schdData.getFinFeeDetailList());
			invoiceDetail.setOrigination(orgination);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setDbInvSetReq(false);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);
			invoiceDetail.setSubventionFeeInv(false);

			// Normal Fees invoice preparation
			// In Case of Loan Approval GST Invoice is happen only for remaining fee after IMD.
			if (CollectionUtils.isNotEmpty(schdData.getFinFeeDetailList())) {
				for (FinFeeDetail fee : schdData.getFinFeeDetailList()) {
					fee.setPaidFromLoanApproval(true);
				}
			}

			Long dueInvoiceID = this.gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);

			Long subvnDueInvoiceID = null;
			String subventionFeeCode = "";
			if (subventionExists) {
				invoiceDetail.setSubventionFeeInv(true);
				subvnDueInvoiceID = this.gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);
				subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;
			}

			for (FinFeeDetail finFeeDetail : schdData.getFinFeeDetailList()) {
				if (finFeeDetail.getTaxHeader() != null && finFeeDetail.getNetAmount().compareTo(BigDecimal.ZERO) > 0) {
					if (subventionExists && subventionFeeCode.equals(finFeeDetail.getFeeTypeCode())) {
						finFeeDetail.getTaxHeader().setInvoiceID(subvnDueInvoiceID);
					} else {
						if (dueInvoiceID == null) {
							dueInvoiceID = finFeeDetail.getTaxHeader().getInvoiceID();
						}
						finFeeDetail.getTaxHeader().setInvoiceID(dueInvoiceID);
					}
				}
			}

			// Waiver Fees Invoice Preparation
			if (ImplementationConstants.TAX_DFT_CR_INV_REQ) {
				List<FinFeeDetail> waiverFees = new ArrayList<>();
				for (FinFeeDetail fee : schdData.getFinFeeDetailList()) {
					if (fee.isTaxApplicable() && fee.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
						waiverFees.add(fee);
					}
				}
				if (CollectionUtils.isNotEmpty(waiverFees)) {
					invoiceDetail = new InvoiceDetail();
					invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
					invoiceDetail.setFinanceDetail(fd);
					invoiceDetail.setOrigination(orgination);
					invoiceDetail.setWaiver(true);
					invoiceDetail.setDbInvSetReq(false);
					invoiceDetail.setFinFeeDetailsList(waiverFees);
					invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);

					dueInvoiceID = gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);

					for (int i = 0; i < schdData.getFinFeeDetailList().size(); i++) {
						FinFeeDetail finFeeDetail = schdData.getFinFeeDetailList().get(i);
						if (finFeeDetail.getTaxHeader() != null
								&& finFeeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
							finFeeDetail.getTaxHeader().setInvoiceID(dueInvoiceID);
						}
					}
				}
			}
		}

		PostingDTO postingDTO = new PostingDTO();
		postingDTO.setFinanceDetail(fd);
		postingDTO.setUserBranch(branchCode);

		// Disbursement Instruction Posting
		if (AccountingEvent.isDisbursementEvent(eventCode) && !ImplementationConstants.HOLD_DISB_INST_POST) {
			AccountingEngine.post(AccountingEvent.DISBINS, postingDTO);
		}

		if (FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
			AccountingEngine.post(AccountingEvent.VAS_FEE, postingDTO);
		}

		if (isNew) {
			installmentDueService.processbackDateInstallmentDues(fd, pftDetail, SysParamUtil.getAppDate(), true,
					branchCode);
		}

		if (fd.getFinScheduleData().getFinanceType().isSubventionReq()) {
			procesSubVenAccounting(aeEvent, fd, true);
		}

		doSave_PftDetails(pftDetail, isNew);
		logger.debug("Leaving");
		return auditHeader;
	}

	private void setFeesesForAccounting(AEEvent aeEvent, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		List<FeeType> feeTypesList = new ArrayList<>();
		List<Long> feeTypeIds = new ArrayList<>();

		List<FinFeeDetail> finFeeDetailList = financeDetail.getFinScheduleData().getFinFeeDetailList();
		if (finFeeDetailList != null && !finFeeDetailList.isEmpty()) {
			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				feeTypeIds.add(finFeeDetail.getFeeTypeID());
			}
			if (!feeTypeIds.isEmpty()) {
				feeTypesList = feeTypeService.getFeeTypeListByIds(feeTypeIds, "");
				aeEvent.setFeesList(feeTypesList);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private boolean setSubventionFeeToDataMap(String branchCode, FinanceDetail fd, FinanceMain financeMain,
			Map<String, Object> dataMap) {
		List<FinFeeDetail> fees = fd.getFinScheduleData().getFinFeeDetailList();
		if (CollectionUtils.isEmpty(fees)) {
			return false;
		}

		boolean subventionExists = false;
		String subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;

		for (FinFeeDetail fee : fees) {
			if (subventionFeeCode.equals(fee.getFeeTypeCode())) {
				subventionExists = true;
				break;
			}
		}

		if (!subventionExists) {
			return false;
		}

		String finBranch = financeMain.getFinBranch();
		Long dealerId = financeMain.getManufacturerDealerId();

		Map<String, Object> gstDataMapForDealer = GSTCalculator.getDealerGSTDataMap(dealerId, branchCode, finBranch);

		for (String key : gstDataMapForDealer.keySet()) {
			if (StringUtils.isNotBlank(key)) {
				dataMap.put("sv_" + key, gstDataMapForDealer.get(key));
			}
		}

		return subventionExists;
	}

	private void setVASAcctCodes(FinanceDetail fd, Map<String, Object> dataMap) {
		List<VASRecording> vasRecordingList = fd.getFinScheduleData().getVasRecordingList();
		String finReference = fd.getFinScheduleData().getFinanceMain().getFinReference();

		if (CollectionUtils.isNotEmpty(vasRecordingList)) {
			VASRecording vasRecording = vasRecordingList.get(0);
			if (vasRecording != null) {
				// For GL Code
				VehicleDealer vehicleDealer = vehicleDealerService.getDealerShortCodes(vasRecording.getProductCode());
				String productCode = financeMainDAO.getFinCategory(finReference);
				dataMap.put("ae_vasProductShrtCode", vehicleDealer.getProductShortCode());
				dataMap.put("ae_productCode", productCode);
				dataMap.put("ae_dealerCode", vehicleDealer.getDealerShortCode());
				dataMap.put("ae_vasProdCategory", vasRecording.getProductCode());
			}
		}
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
		Date curBussDate = SysParamUtil.getAppDate();

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
	 */
	protected FinanceProfitDetail doSave_PftDetails(FinanceProfitDetail profitDetail, boolean isNew) {
		logger.debug("Entering");

		// Save/Update Finance Profit Details
		FinanceProfitDetail finProfitDetails = profitDetail;
		if (isNew) {
			profitDetailsDAO.save(finProfitDetails);
		} else {
			profitDetailsDAO.update(finProfitDetails, false);
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

		FinanceMain fm = scheduleData.getFinanceMain();
		long finID = fm.getFinID();

		financeScheduleDetailDAO.deleteByFinReference(finID, tableType, isWIF, 0);
		financeDisbursementDAO.deleteByFinReference(finID, tableType, isWIF, 0);
		repayInstructionDAO.deleteByFinReference(finID, tableType, isWIF, 0);

		if (StringUtils.isBlank(tableType) || "_Temp".equalsIgnoreCase(tableType)) {
			if (subventionService != null) {
				subventionService.deleteByFinReference(finID, tableType);
			}
		}

		// Fee Charge Details & Finance Overdue PenaltyRate Details
		if (StringUtils.isNotBlank(tableType) || isWIF) {
			finFeeChargesDAO.deleteChargesBatch(finID, finEvent, isWIF, tableType);
		}

		if (!isWIF) {
			finODPenaltyRateDAO.delete(finID, scheduleData.getFinODPenaltyRate().getFinEffectDate(), tableType);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method to save what if inquiry lists
	 */
	public void listSave(FinScheduleData schdData, String tableType, boolean isWIF, long logKey, long instructionUID) {
		logger.debug(Literal.ENTERING);
		Map<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		List<FinanceDisbursement> disbursements = schdData.getDisbursementDetails();
		List<RepayInstruction> repayInstructions = schdData.getRepayInstructions();

		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		for (FinanceScheduleDetail schedule : schedules) {
			schedule.setLastMntBy(fm.getLastMntBy());
			schedule.setFinID(finID);
			schedule.setFinReference(finReference);
			int seqNo = 0;

			if (mapDateSeq.containsKey(schedule.getSchDate())) {
				seqNo = mapDateSeq.get(schedule.getSchDate());
				mapDateSeq.remove(schedule.getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(schedule.getSchDate(), seqNo);
			schedule.setSchSeq(seqNo);
			schedule.setLogKey(logKey);
		}

		financeScheduleDetailDAO.saveList(schedules, tableType, isWIF);

		// Schedule Version Updating
		if (StringUtils.isBlank(tableType) && !isWIF) {
			financeMainDAO.updateSchdVersion(fm, false);
		}
		if (subventionService != null) {
			subventionService.savSubvnetion(schdData, tableType);
		}

		// Finance Disbursement Details
		mapDateSeq = new HashMap<Date, Integer>();
		Date curBDay = SysParamUtil.getAppDate();

		for (FinanceDisbursement disbursement : disbursements) {
			disbursement.setFinID(finID);
			disbursement.setFinReference(finReference);
			disbursement.setDisbReqDate(curBDay);
			disbursement.setDisbIsActive(true);
			disbursement.setLogKey(logKey);
			disbursement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			disbursement.setLastMntBy(fm.getLastMntBy());

			if (disbursement.getInstructionUID() == Long.MIN_VALUE) {
				disbursement.setInstructionUID(instructionUID);
			}
		}
		financeDisbursementDAO.saveList(disbursements, tableType, isWIF);

		for (RepayInstruction rpayInst : repayInstructions) {
			rpayInst.setFinID(finID);
			rpayInst.setFinReference(finReference);
			rpayInst.setLogKey(logKey);
		}
		repayInstructionDAO.saveList(repayInstructions, tableType, isWIF);

		// Finance Overdue Penalty Rates
		if (!isWIF && logKey == 0) {
			FinODPenaltyRate penaltyRate = schdData.getFinODPenaltyRate();
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
				penaltyRate.setODRuleCode("");
				penaltyRate.setOdMinAmount(BigDecimal.ZERO);
			}

			penaltyRate.setFinID(finID);
			penaltyRate.setFinReference(finReference);
			if (FinServiceEvent.ORG.equals(fm.getModuleDefiner()) || StringUtils.isEmpty(fm.getModuleDefiner())) {
				penaltyRate.setFinEffectDate(fm.getFinStartDate());
			} else {
				penaltyRate.setFinEffectDate(curBDay);
			}

			finODPenaltyRateDAO.save(penaltyRate, tableType);
		}

		FinLogEntryDetail logDtls = finLogEntryDetailDAO.getFinLogEntryDetail(fm.getFinID());
		List<FinServiceInstruction> finServiceInstructions = schdData.getFinServiceInstructions();
		if (CollectionUtils.isNotEmpty(finServiceInstructions) && !isWIF) {
			if (logDtls != null) {
				finServiceInstructions.forEach(fsi -> fsi.setLogKey(logDtls.getLogKey()));
			}
			finServiceInstructionDAO.saveList(finServiceInstructions, tableType);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Inserting IRR values against Finance
	 * 
	 * @param baseIRRFeeTypes
	 * @param finReference
	 * @param tableType
	 */
	public void saveFinIRR(List<FinIRRDetails> finIrrDetailsList, long finID, String finReference,
			TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (!finIrrDetailsList.isEmpty()) {
			for (FinIRRDetails finIrrDetails : finIrrDetailsList) {
				finIrrDetails.setFinID(finID);
				finIrrDetails.setFinReference(finReference);
			}
			finIRRDetailsDAO.saveList(finIrrDetailsList, tableType);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Inserting IRR values against Finance
	 * 
	 * @param baseIRRFeeTypes
	 * @param tableType
	 */
	public void deleteFinIRR(long finID, TableType tableType) {
		logger.debug("Entering");
		finIRRDetailsDAO.deleteList(finID, tableType);
		logger.debug("Leaving ");
	}

	/**
	 * Method for saving List of Fee Charge details
	 * 
	 * @param finDetail
	 * @param tableType
	 */
	public void saveFeeChargeList(FinScheduleData schdData, String finEvent, boolean isWIF, String tableType) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(schdData.getFeeRules())) {
			logger.debug(Literal.LEAVING);
			return;
		}

		for (FeeRule feeRule : schdData.getFeeRules()) {
			feeRule.setFinReference(schdData.getFinReference());
			feeRule.setFinEvent(finEvent);
		}

		finFeeChargesDAO.saveCharges(schdData.getFeeRules(), isWIF, tableType);

		logger.debug(Literal.LEAVING);
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
			// Finance Fee Charge Details
			for (int i = 0; i < finScheduleData.getStepPolicyDetails().size(); i++) {
				finScheduleData.getStepPolicyDetails().get(i).setFinID(finScheduleData.getFinID());
				finScheduleData.getStepPolicyDetails().get(i).setFinReference(finScheduleData.getFinReference());
			}
			financeStepDetailDAO.saveList(finScheduleData.getStepPolicyDetails(), isWIF, tableType);
		}

		logger.debug("Leaving");
	}

	public FinScheduleData getFinSchDataByFinRef(long finID, String type, long logKey) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		if (logKey == 0) {
			finSchData.setFinanceMain(financeMainDAO.getFinanceMainById(finID, type, false));
		}

		finSchData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));

		if (logKey != 0) {
			finSchData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));
		}

		finSchData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, false));

		if (logKey == 0) {
			String finType = finSchData.getFinanceMain().getFinType();
			finSchData.setFinanceType(financeTypeDAO.getFinanceTypeByID(finType, "_AView"));
			finSchData.setFeeRules(finFeeChargesDAO.getFeeChargesByFinRef(finID, "", false, ""));
			// Finance Fee Details
			finSchData.setFinFeeDetailList(finFeeDetailService.getFinFeeDetailById(finID, false, ""));

			finSchData.setRepayDetails(getFinRepayList(finID));
			finSchData.setPenaltyDetails(getFinancePenaltysByFinRef(finID));
			finSchData.setAccrueValue(profitDetailsDAO.getAccrueAmount(finID));
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
	public List<FinanceRepayments> getFinRepayList(long finID) {
		return financeRepaymentsDAO.getFinRepayList(finID);
	}

	/**
	 * Method to get FinanceRepayments By FinReference
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<OverdueChargeRecovery> getFinancePenaltysByFinRef(long finID) {
		return recoveryDAO.getFinancePenaltysByFinRef(finID, "");
	}

	/**
	 * Method for Validating Fee Details processed in the workflow Event & linking with Accounting Set
	 */
	public boolean validateAccSetFees(List<FinFeeDetail> feeList, List<Long> acSetIDList, String event) {

		// Linked Fee Codes against Accounting Set Transactions
		boolean isFeeMatched = true;
		List<String> feeCodeList = transactionEntryDAO.getFeeCodeList(acSetIDList);
		for (FinFeeDetail feeDetail : feeList) {

			if (!StringUtils.equals(event, feeDetail.getFinEvent())) {
				continue;
			}
			if (!feeCodeList.contains(feeDetail.getFeeTypeCode())) {
				isFeeMatched = false;
				break;
			}
		}

		return isFeeMatched;
	}

	protected AEEvent executeBounceDueAccounting(FinanceMain fm, Date valueDate, ManualAdvise advise, String postBranch,
			String accFor) {
		logger.debug("Entering");

		List<Long> acSetIdList = new ArrayList<>();
		BigDecimal dueAmount = advise.getAdviseAmount();

		FeeType feeType = feeTypeDAO.getTaxDetailByCode(Allocation.BOUNCE);
		if (feeType == null || feeType.getAccountSetId() == null || feeType.getAccountSetId() <= 0) {
			return null;
		}

		if (!feeType.isAmortzReq()) {
			return null;
		}

		acSetIdList.add(feeType.getAccountSetId());
		String taxType = feeType.getTaxComponent();

		if (acSetIdList.isEmpty()) {
			return null;
		}

		AEEvent aeEvent = new AEEvent();
		aeEvent.setAeAmountCodes(new AEAmountCodes());
		aeEvent.setFinID(fm.getFinID());
		aeEvent.setFinReference(fm.getFinReference());
		aeEvent.setCustID(fm.getCustID());
		aeEvent.setFinType(fm.getFinType());
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setPostingUserBranch(postBranch);
		aeEvent.setValueDate(valueDate);
		aeEvent.setPostDate(SysParamUtil.getAppDate());
		aeEvent.setEntityCode(fm.getLovDescEntityCode());
		aeEvent.setEOD(false);

		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		if (!phase.equals(PennantConstants.APP_PHASE_DAY)) {
			aeEvent.setEOD(true);
		}

		aeEvent.setAccountingEvent(AccountingEvent.MANFEE);
		aeEvent.getAcSetIDList().addAll(acSetIdList);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setFinType(fm.getFinType());

		Map<String, Object> dataMap = aeEvent.getDataMap();
		dataMap = amountCodes.getDeclaredFieldValues(dataMap);

		dataMap.put("bounceCharge", dueAmount);
		dataMap.put("bounceCharge_CGST", BigDecimal.ZERO);
		dataMap.put("bounceCharge_SGST", BigDecimal.ZERO);
		dataMap.put("bounceCharge_UGST", BigDecimal.ZERO);
		dataMap.put("bounceCharge_IGST", BigDecimal.ZERO);
		dataMap.put("bounceCharge_CESS", BigDecimal.ZERO);

		TaxHeader taxHeader = null;
		if (feeType.isTaxApplicable()) {
			Map<String, BigDecimal> taxes = GSTCalculator.getTaxPercentages(fm);

			taxHeader = new TaxHeader();
			taxHeader.setNewRecord(true);
			taxHeader.setRecordType(PennantConstants.RCD_ADD);
			taxHeader.setVersion(taxHeader.getVersion() + 1);
			taxHeader.setTaxDetails(new ArrayList<>());

			Taxes cgstTax = getTaxDetail(RuleConstants.CODE_CGST, taxes.get(RuleConstants.CODE_CGST));
			taxHeader.getTaxDetails().add(cgstTax);

			Taxes sgstTax = getTaxDetail(RuleConstants.CODE_SGST, taxes.get(RuleConstants.CODE_SGST));
			taxHeader.getTaxDetails().add(sgstTax);

			Taxes igstTax = getTaxDetail(RuleConstants.CODE_IGST, taxes.get(RuleConstants.CODE_IGST));
			taxHeader.getTaxDetails().add(igstTax);

			Taxes ugstTax = getTaxDetail(RuleConstants.CODE_UGST, taxes.get(RuleConstants.CODE_UGST));
			taxHeader.getTaxDetails().add(ugstTax);

			Taxes cessTax = getTaxDetail(RuleConstants.CODE_CESS, taxes.get(RuleConstants.CODE_CESS));
			taxHeader.getTaxDetails().add(cessTax);

			TaxAmountSplit taxSplit = null;
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
				taxSplit = GSTCalculator.getExclusiveGST(dueAmount, taxes);
			} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
				taxSplit = GSTCalculator.getInclusiveGST(dueAmount, taxes);
			}

			cgstTax.setPaidTax(taxSplit.getcGST());
			sgstTax.setPaidTax(taxSplit.getsGST());
			igstTax.setPaidTax(taxSplit.getiGST());
			ugstTax.setPaidTax(taxSplit.getuGST());
			cessTax.setPaidTax(taxSplit.getCess());

			dataMap.put("bounceCharge_CGST", taxSplit.getcGST());
			dataMap.put("bounceCharge_SGST", taxSplit.getsGST());
			dataMap.put("bounceCharge_IGST", taxSplit.getiGST());
			dataMap.put("bounceCharge_UGST", taxSplit.getuGST());
			dataMap.put("bounceCharge_CESS", taxSplit.getCess());
		}

		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(fm.getFinID());
		if (gstExecutionMap != null) {
			for (String key : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					dataMap.put(key, gstExecutionMap.get(key));
				}
			}
		}
		aeEvent.setDataMap(dataMap);

		try {
			aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
			aeEvent.setErrorMessage(ErrorUtil.getErrorDetail(new ErrorDetail("Accounting Engine",
					PennantConstants.ERR_UNDEF, "E", "Accounting Engine Failed to Create Postings:" + e.getMessage(),
					new String[] {}, new String[] {})).getMessage());
			return aeEvent;
		}

		if (!aeEvent.isPostingSucess()) {
			throw new InterfaceException("9998", "Bounce charge due accounting postings failed.");
		}

		createGSTInvoiceForBounce(fm, advise, aeEvent, feeType, taxHeader);

		logger.debug(Literal.LEAVING);
		return aeEvent;
	}

	private Taxes getTaxDetail(String taxType, BigDecimal taxPerc) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPerc);
		return taxes;
	}

	private void createGSTInvoiceForBounce(FinanceMain fm, ManualAdvise ma, AEEvent aeEvent, FeeType feeType,
			TaxHeader taxHeader) {

		long linkedTranID = aeEvent.getLinkedTranId();

		if (linkedTranID <= 0) {
			return;
		}

		if (taxHeader == null || CollectionUtils.isEmpty(taxHeader.getTaxDetails())) {
			return;
		}

		if (!SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE)) {
			return;
		}

		ma.setDueCreation(true);

		ManualAdviseMovements advMovement = new ManualAdviseMovements();
		advMovement.setFeeTypeCode(ma.getFeeTypeCode());
		advMovement.setFeeTypeDesc(ma.getFeeTypeDesc());
		advMovement.setMovementAmount(ma.getAdviseAmount());

		advMovement.setFeeTypeCode(feeType.getFeeTypeCode());
		advMovement.setFeeTypeDesc(feeType.getFeeTypeDesc());
		advMovement.setTaxApplicable(feeType.isTaxApplicable());
		advMovement.setTaxComponent(feeType.getTaxComponent());
		advMovement.setStatus("D");

		Map<String, Object> dataMap = aeEvent.getDataMap();
		advMovement.setPaidAmount(ma.getAdviseAmount());
		advMovement.setTaxHeader(taxHeader);

		List<Taxes> taxDetails = taxHeader.getTaxDetails();
		BigDecimal gstAmount = BigDecimal.ZERO;
		for (Taxes taxes : taxDetails) {
			gstAmount = gstAmount.add(taxes.getPaidTax());
		}

		// GST Invoice data resetting based on Accounting Process
		if (gstAmount.compareTo(BigDecimal.ZERO) > 0) {
			List<ManualAdviseMovements> advMovements = new ArrayList<>();
			advMovements.add(advMovement);

			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(fm);

			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
			invoiceDetail.setFinanceDetail(financeDetail);
			invoiceDetail.setMovements(advMovements);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setDbInvSetReq(false);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);

			Long invoiceID = this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

			// saving Due tax advice details
			saveDueTaxDetail(dataMap, feeType.getTaxComponent(), ma.getAdviseID(), invoiceID);
		}

	}

	private void saveDueTaxDetail(Map<String, Object> dataMap, String taxType, long adviseId, Long invoiceID) {
		AdviseDueTaxDetail detail = new AdviseDueTaxDetail();

		detail.setAdviseID(adviseId);
		detail.setTaxType(taxType);
		detail.setTaxType(taxType);
		detail.setInvoiceID(invoiceID);

		detail.setAmount(BigDecimal.ZERO);
		detail.setCGST(BigDecimal.ZERO);
		detail.setSGST(BigDecimal.ZERO);
		detail.setIGST(BigDecimal.ZERO);
		detail.setUGST(BigDecimal.ZERO);
		detail.setCESS(BigDecimal.ZERO);

		if (dataMap.containsKey("bounceCharge")) {
			detail.setAmount(new BigDecimal(dataMap.get("bounceCharge").toString()));
		}

		if (dataMap.containsKey("bounceCharge_CGST")) {
			detail.setCGST(new BigDecimal(dataMap.get("bounceCharge_CGST").toString()));
		}

		if (dataMap.containsKey("bounceCharge_SGST")) {
			detail.setSGST(new BigDecimal(dataMap.get("bounceCharge_SGST").toString()));
		}

		if (dataMap.containsKey("bounceCharge_IGST")) {
			detail.setIGST(new BigDecimal(dataMap.get("bounceCharge_IGST").toString()));
		}

		if (dataMap.containsKey("bounceCharge_UGST")) {
			detail.setUGST(new BigDecimal(dataMap.get("bounceCharge_UGST").toString()));
		}

		if (dataMap.containsKey("bounceCharge_CESS")) {
			detail.setCESS(new BigDecimal(dataMap.get("bounceCharge_CESS").toString()));
		}

		detail.setTotalGST(detail.getCGST().add(detail.getSGST()).add(detail.getIGST()).add(detail.getUGST())
				.add(detail.getCESS()));

		this.manualAdviseDAO.saveDueTaxDetail(detail);
	}

	protected FinanceDetail getFinanceDetail(long finID, String type) {
		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, type, false);
		String finReference = fm.getFinReference();
		String finType = fm.getFinType();

		FinanceType ft = financeTypeDAO.getFinanceTypeByID(finType, "_AView");
		schdData.setFinanceMain(fm);
		schdData.setFinanceType(ft);

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));
		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));
		schdData.setFinFeeDetailList(finFeeDetailDAO.getFinFeeDetailByFinRef(finID, false, "_View"));

		return fd;
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public void setEligibilityDetailService(EligibilityDetailService eligibilityDetailService) {
		this.eligibilityDetailService = eligibilityDetailService;
	}

	@Autowired
	public void setGuarantorDetailService(GuarantorDetailService guarantorDetailService) {
		this.guarantorDetailService = guarantorDetailService;
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public void setScoringDetailService(ScoringDetailService scoringDetailService) {
		this.scoringDetailService = scoringDetailService;
	}

	public void setCheckListDetailService(CheckListDetailService checkListDetailService) {
		this.checkListDetailService = checkListDetailService;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
		this.commitmentDAO = commitmentDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setCommitmentMovementDAO(CommitmentMovementDAO commitmentMovementDAO) {
		this.commitmentMovementDAO = commitmentMovementDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public void setFinFeeChargesDAO(FinFeeChargesDAO finFeeChargesDAO) {
		this.finFeeChargesDAO = finFeeChargesDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	public void setSuspensePostingUtil(SuspensePostingUtil suspensePostingUtil) {
		this.suspensePostingUtil = suspensePostingUtil;
	}

	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
		this.recoveryPostingsUtil = recoveryPostingsUtil;
	}

	public void setDeviationDetailsService(FinanceDeviationsService deviationDetailsService) {
		this.deviationDetailsService = deviationDetailsService;
	}

	public void setFinMandateService(FinMandateService finMandateService) {
		this.finMandateService = finMandateService;
	}

	public void setFinanceStepDetailDAO(FinanceStepDetailDAO financeStepDetailDAO) {
		this.financeStepDetailDAO = financeStepDetailDAO;
	}

	public void setBlacklistCustomerDAO(BlackListCustomerDAO blacklistCustomerDAO) {
		this.blacklistCustomerDAO = blacklistCustomerDAO;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setFinanceDedupeDAO(FinanceDedupeDAO financeDedupeDAO) {
		this.financeDedupeDAO = financeDedupeDAO;
	}

	public void setCustomerDedupDAO(CustomerDedupDAO customerDedupDAO) {
		this.customerDedupDAO = customerDedupDAO;
	}

	public void setFinStageAccountingLogDAO(FinStageAccountingLogDAO finStageAccountingLogDAO) {
		this.finStageAccountingLogDAO = finStageAccountingLogDAO;
	}

	public void setFinCollateralsDAO(FinCollateralsDAO finCollateralsDAO) {
		this.finCollateralsDAO = finCollateralsDAO;
	}

	public void setFinCollateralService(FinCollateralService finCollateralService) {
		this.finCollateralService = finCollateralService;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public void setFinCovenantTypeService(FinCovenantTypeService finCovenantTypeService) {
		this.finCovenantTypeService = finCovenantTypeService;
	}

	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	public void setFinFlagDetailsDAO(FinFlagDetailsDAO finFlagDetailsDAO) {
		this.finFlagDetailsDAO = finFlagDetailsDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
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

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public FinAssetTypesValidation getFinAssetTypesValidation() {
		if (finAssetTypesValidation == null) {
			this.finAssetTypesValidation = new FinAssetTypesValidation(finAssetTypeDAO);
		}
		return finAssetTypesValidation;
	}

	public void setFinAssetTypeDAO(FinAssetTypeDAO finAssetTypeDAO) {
		this.finAssetTypeDAO = finAssetTypeDAO;
	}

	public void setFinPlanEmiHolidayDAO(FinPlanEmiHolidayDAO finPlanEmiHolidayDAO) {
		this.finPlanEmiHolidayDAO = finPlanEmiHolidayDAO;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public void setInstallmentDueService(InstallmentDueService installmentDueService) {
		this.installmentDueService = installmentDueService;
	}

	@Autowired
	public void setAdvancePaymentService(AdvancePaymentService advancePaymentService) {
		this.advancePaymentService = advancePaymentService;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public void setFinIRRDetailsDAO(FinIRRDetailsDAO finIRRDetailsDAO) {
		this.finIRRDetailsDAO = finIRRDetailsDAO;
	}

	public void setQueryDetailService(QueryDetailService queryDetailService) {
		this.queryDetailService = queryDetailService;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public void setCovenantsService(CovenantsService covenantsService) {
		this.covenantsService = covenantsService;
	}

	public void setFinOptionService(FinOptionService finOptionService) {
		this.finOptionService = finOptionService;
	}

	public void setCovenantsDAO(CovenantsDAO covenantsDAO) {
		this.covenantsDAO = covenantsDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setCollateralAssignmentValidation(CollateralAssignmentValidation collateralAssignmentValidation) {
		this.collateralAssignmentValidation = collateralAssignmentValidation;
	}

	public void setFinAssetTypesValidation(FinAssetTypesValidation finAssetTypesValidation) {
		this.finAssetTypesValidation = finAssetTypesValidation;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public void setSubventionService(SubventionService subventionService) {
		this.subventionService = subventionService;
	}

	public void setRestructureService(RestructureService restructureService) {
		this.restructureService = restructureService;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	@Autowired
	public void setTanAssignmentService(TanAssignmentService tanAssignmentService) {
		this.tanAssignmentService = tanAssignmentService;
	}

	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
	}

}
