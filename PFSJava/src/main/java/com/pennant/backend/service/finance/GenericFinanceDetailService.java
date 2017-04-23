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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.Interface.service.PostingsInterfaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.AccountEngineExecutionRIA;
import com.pennant.app.util.AccountProcessUtil;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.OverDueRecoveryPostingsUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.SuspensePostingUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
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
import com.pennant.backend.dao.policecase.PoliceCaseDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.FinFeeChargesDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinStageAccountingLog;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.SecondaryAccount;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.backend.model.finance.salary.FinSalariedPayment;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.CollateralAssignmentValidation;
import com.pennant.backend.service.collateral.impl.FinAssetTypesValidation;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.contractor.ContractorAssetDetailService;
import com.pennant.backend.service.finance.impl.FinInsuranceValidation;
import com.pennant.backend.service.mandate.FinMandateService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennant.exception.PFFInterfaceException;

public abstract class GenericFinanceDetailService extends GenericService<FinanceDetail> {
	private final static Logger				logger	= Logger.getLogger(GenericFinanceDetailService.class);

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

	// DocumentManagerDAO
	private DocumentManagerDAO				documentManagerDAO;

	// External Database
	private ExtTablesDAO					extTablesDAO;

	private AccountEngineExecution			engineExecution;
	private AccountEngineExecutionRIA		engineExecutionRIA;
	private PostingsInterfaceService		postingsInterfaceService;
	private CustomerDAO						customerDAO;
	private AccountProcessUtil				accountProcessUtil;
	private PostingsPreparationUtil			postingsPreparationUtil;
	private FinStatusDetailDAO				finStatusDetailDAO;
	private SuspensePostingUtil				suspensePostingUtil;
	private OverDueRecoveryPostingsUtil		recoveryPostingsUtil;
	private FinanceDeviationsService		deviationDetailsService;
	private FinMandateService				finMandateService;

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

	// EOD Process Checking
	private CustomerQueuingDAO				customerQueuingDAO;

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
		String[] fields = PennantJavaUtil.getFieldDetails(assignment, "");
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
						auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(),
								fields[0], fields[1], befImg, object));
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
			if (!documentDetails.isDocIsCustDoc()) {
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

				if (documentDetails.isDocIsCustDoc()) {
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
	 * Method for Execute posting Details on Core Banking Side
	 * 
	 * @param auditHeader
	 * @param curBDay
	 * @return
	 * @throws AccountNotFoundException
	 */
	public AuditHeader executeAccountingProcess(AuditHeader auditHeader, Date curBDay) throws PFFInterfaceException {
		logger.debug("Entering");

		long linkedTranId = Long.MIN_VALUE;
		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
		AEAmountCodes amountCodes = null;
		// Used for Commitment Purpose , Commented for Limit Usage
		BigDecimal cmtPostAmt = BigDecimal.ZERO;
		Commitment commitment = null;

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		BeanUtils.copyProperties((FinanceDetail) auditHeader.getAuditDetail().getModelData(), financeDetail);
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String productType = financeMain.getLovDescProductCodeName();

		Date dateValueDate = financeMain.getFinStartDate();
		if (!financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			dateValueDate = curBDay;
		}

		DataSet dataSet = AEAmounts.createDataSet(financeMain, financeDetail.getAccountingEventCode(), dateValueDate,
				dateValueDate);
		dataSet.setModuledefiner(financeDetail.getModuleDefiner());

		//Profit Details Data Fetching for Maintenance Record, otherwise Profit Detail object not Exists
		FinanceProfitDetail pftDetail = null;
		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			pftDetail = new FinanceProfitDetail();
		} else {
			pftDetail = getProfitDetailsDAO().getFinProfitDetailsById(financeMain.getFinReference());
		}

		pftDetail = AEAmounts.calProfitDetails(financeMain, financeDetail.getFinScheduleData()
				.getFinanceScheduleDetails(), pftDetail, curBDay);
		amountCodes = AEAmounts.procCalAEAmounts(financeMain, pftDetail, curBDay);

		try {

			Map<String, FeeRule> feeRuleDetailsMap = null;
			/*
			 * if (financeDetail.getFinScheduleData().getFeeRules() != null &&
			 * financeDetail.getFinScheduleData().getFeeRules().size() > 0) {
			 * 
			 * feeRuleDetailsMap = new HashMap<String, FeeRule>(); for (FeeRule feeRule :
			 * financeDetail.getFinScheduleData().getFeeRules()) { if
			 * (!feeRuleDetailsMap.containsKey(feeRule.getFeeCode())) { feeRuleDetailsMap.put(feeRule.getFeeCode(),
			 * feeRule); } } }
			 */

			//Commented for Excluding Fee Details Checking with only Final Stage Accounting. 
			//Fees may occur at Stage level accounting also.
			Long accountSetID;

			if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
				accountSetID = getFinTypeAccountingDAO().getAccountSetID(financeMain.getPromotionCode(),
						dataSet.getFinEvent(), FinanceConstants.MODULEID_PROMOTION);
			} else {
				accountSetID = getFinTypeAccountingDAO().getAccountSetID(
						financeDetail.getFinScheduleData().getFinanceType().getFinType(), dataSet.getFinEvent(),
						FinanceConstants.MODULEID_FINTYPE);
			}

			List<String> feeCodeList = getTransactionEntryDAO().getListFeeCodes(accountSetID);

			Set<String> transactionEntryFees = new HashSet<>();
			for (String feeCode : feeCodeList) {
				String[] feelist = null;
				if (feeCode.contains(",")) {
					feelist = feeCode.split(",");
					for (String string : feelist) {
						transactionEntryFees.add(string);
					}
				} else {
					transactionEntryFees.add(feeCode);
				}
			}

			List<FinFeeDetail> finFeeDetailList = financeDetail.getFinScheduleData().getFinFeeDetailList();
			if (finFeeDetailList != null) {
				feeRuleDetailsMap = new HashMap<>();
				FeeRule feeRule;
				for (FinFeeDetail finFeeDetail : finFeeDetailList) {
					if (!transactionEntryFees.contains(finFeeDetail.getFeeTypeCode())) {
						ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
						errorDetails.add(new ErrorDetails("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
								PennantJavaUtil.getLabel("label_mismatchFeeswithAccounting"), new String[] {},
								new String[] {}));
						auditHeader.setErrorList(errorDetails);
						logger.debug("Leaving");
						return auditHeader;
					}
					feeRule = new FeeRule();
					feeRule.setFeeCode(finFeeDetail.getFeeTypeCode());
					feeRule.setFeeAmount(finFeeDetail.getActualAmount());
					feeRule.setWaiverAmount(finFeeDetail.getWaivedAmount());
					feeRule.setPaidAmount(finFeeDetail.getPaidAmount());
					feeRule.setFeeToFinance(finFeeDetail.getFeeScheduleMethod());
					feeRule.setFeeMethod(finFeeDetail.getFeeScheduleMethod());
					feeRuleDetailsMap.put(feeRule.getFeeCode(), feeRule);
				}
			}
			/*
			 * //Fee Rules Checking with Existing Fees with in Process & Latest Accounting Entry Details boolean
			 * reExecuteFees = false;
			 * 
			 * for (int i = 0; i < feeCodeList.size(); i++) {
			 * 
			 * String[] feelist = null; if(feeCodeList.get(i).contains(",")){ feelist = feeCodeList.get(i).split(","); }
			 * 
			 * if(feelist != null && feelist.length > 0){ for (int j = 0; j < feelist.length; j++) {
			 * if(feeRuleDetailsMap == null || !feeRuleDetailsMap.containsKey(feelist[j].trim())){ reExecuteFees = true;
			 * break; } } }else{ if(feeRuleDetailsMap != null &&
			 * !feeRuleDetailsMap.containsKey(feeCodeList.get(i).trim())){ reExecuteFees = true; break; } } }
			 * 
			 * //If Fees Mismatch with Existing Executed Fees to the Accounting Entry Fee Details if(reExecuteFees){
			 * ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>(); errorDetails.add(new
			 * ErrorDetails("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
			 * PennantJavaUtil.getLabel("label_mismatchFeeswithAccounting"), new String[] {}, new String[] {}));
			 * auditHeader.setErrorList(errorDetails); logger.debug("Leaving"); return auditHeader; }
			 */

			// TODO : NED TO DO CLEANUP ON BELOW EXECUTION PROCESS (Execution process calling multiple times(Repeated code- added time being)

			if (!financeDetail.getFinScheduleData().getFinanceType().isAllowRIAInvestment()) {

				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)
						&& !StringUtils
								.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ROLLOVER)) {

					List<FinanceDisbursement> disbursementDetails = financeDetail.getFinScheduleData()
							.getDisbursementDetails();

					// Loop Repetition for Multiple Disbursement
					if (disbursementDetails != null && disbursementDetails.size() > 0) {

						Map<Long, BigDecimal> advPendingDueMap = new HashMap<Long, BigDecimal>();

						for (FinanceDisbursement disbursement : disbursementDetails) {
							if (disbursement.getDisbDate().after(DateUtility.getAppDate())) {
								if ("B".equals(disbursement.getDisbType())) {
									continue;
								}
								if (AccountEventConstants.ACCEVENT_ADDDBSF_REQ) {
									dataSet.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSF);
								} else {
									dataSet.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSP);
								}
							} else {
								dataSet.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSP);
							}

							if (StringUtils.isNotBlank(disbursement.getDisbAccountId())) {
								dataSet.setDisburseAccount(disbursement.getDisbAccountId());
							}
							dataSet.setDisburseAmount(disbursement.getDisbAmount());
							dataSet.setCurDisbRet(disbursement.getDisbRetAmount());
							dataSet.setNetRetDue(disbursement.getNetRetDue());
							dataSet.setClaimAmt(disbursement.getDisbClaim());
							dataSet.setGrcPftTillNow(calculateTillGrcProfit(financeDetail.getFinScheduleData(),
									disbursement.getDisbDate()));

							//Net Customer Advance Amount Calculation
							BigDecimal netAdvDue = BigDecimal.ZERO;
							if ("B".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))) {
								dataSet.setFinEvent("ISTBILL");//FIXME--- Hard code

								if (advPendingDueMap.containsKey(disbursement.getContractorId())) {
									netAdvDue = advPendingDueMap.get(disbursement.getContractorId());
								}

								BigDecimal balAdv = netAdvDue.subtract(disbursement.getNetAdvDue());
								if (balAdv.compareTo(BigDecimal.ZERO) > 0) {
									dataSet.setAdvDue(balAdv);
									balAdv = BigDecimal.ZERO;
								} else {
									dataSet.setAdvDue(BigDecimal.ZERO);
								}

								advPendingDueMap.put(disbursement.getContractorId(), balAdv);

							} else if ("A".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))) {

								if (advPendingDueMap.containsKey(disbursement.getContractorId())) {
									netAdvDue = advPendingDueMap.get(disbursement.getContractorId());
								}
								netAdvDue = netAdvDue.add(disbursement.getNetAdvDue());
								advPendingDueMap.put(disbursement.getContractorId(), netAdvDue);
							}

							List<ReturnDataSet> returnSetEntries = getEngineExecution().getAccEngineExecResults(
									dataSet, amountCodes, "Y", feeRuleDetailsMap, false,
									financeDetail.getFinScheduleData().getFinanceType(),
									financeDetail.getPremiumDetail());

							if (returnSetEntries != null && !returnSetEntries.isEmpty()) {

								// Method for validating Postings with interface program and
								// return results
								if (returnSetEntries.get(0).getLinkedTranId() == Long.MIN_VALUE) {
									linkedTranId = getPostingsDAO().getLinkedTransId();
								} else {
									linkedTranId = returnSetEntries.get(0).getLinkedTranId();
								}

								disbursement.setLinkedTranId(linkedTranId);

								//Method for Checking for Reverse Calculations Based upon Negative Amounts
								for (ReturnDataSet returnDataSet : returnSetEntries) {

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

								//Core Banking Posting Process call
								returnSetEntries = getPostingsInterfaceService().doFillPostingDetails(returnSetEntries,
										financeMain.getFinBranch(), linkedTranId, "Y");

								if (returnSetEntries != null && !returnSetEntries.isEmpty()) {
									ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
									boolean isFetchFinAc = false;
									boolean isFetchCistIntAc = false;
									for (int i = 0; i < returnSetEntries.size(); i++) {
										ReturnDataSet set = returnSetEntries.get(i);
										set.setLinkedTranId(linkedTranId);
										set.setPostDate(curBDay);
										if (!("0000".equals(StringUtils.trimToEmpty(set.getErrorId())) || StringUtils
												.isEmpty(StringUtils.trimToEmpty(set.getErrorId())))) {
											errorDetails.add(new ErrorDetails(set.getAccountType(), set.getErrorId(),
													"E", set.getErrorMsg()
															+ " "
															+ PennantApplicationUtil.formatAccountNumber(set
																	.getAccount()), new String[] {}, new String[] {}));
										} else {
											set.setPostStatus("S");
										}

										if (!isFetchFinAc
												&& set.getAccountType().equals(
														financeDetail.getFinScheduleData().getFinanceType()
																.getFinAcType())) {
											isFetchFinAc = true;
											financeMain.setFinAccount(set.getAccount());
										}
										if (!isFetchCistIntAc
												&& set.getAccountType().equals(
														financeDetail.getFinScheduleData().getFinanceType()
																.getPftPayAcType())) {
											isFetchCistIntAc = true;
											financeMain.setFinCustPftAccount(set.getAccount());
										}
									}
									auditHeader.setErrorList(errorDetails);
								}
							}

							list.addAll(returnSetEntries);
						}
					}
					// Finance GraceEnd Posting Details
					if (StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
						if (StringUtils.equals(productType, FinanceConstants.PRODUCT_IJARAH)
								|| StringUtils.equals(productType, FinanceConstants.PRODUCT_FWIJARAH)) {
							if (financeMain.getGrcPeriodEndDate() != null
									&& financeMain.getGrcPeriodEndDate().compareTo(curBDay) <= 0) {
								List<ReturnDataSet> returnSetEntries = null;
								dataSet.setFinEvent(AccountEventConstants.ACCEVENT_GRACEEND);
								dataSet.setFeeAmount(financeMain.getFeeChargeAmt() == null ? BigDecimal.ZERO
										: financeMain.getFeeChargeAmt());
								dataSet.setInsAmount(financeMain.getInsuranceAmt() == null ? BigDecimal.ZERO
										: financeMain.getInsuranceAmt());
								returnSetEntries = getEngineExecution().getAccEngineExecResults(dataSet, amountCodes,
										"Y", null, false, financeDetail.getFinScheduleData().getFinanceType());

								if (returnSetEntries != null && !returnSetEntries.isEmpty()) {

									// Method for validating Postings with interface program and
									// return results
									if (returnSetEntries.get(0).getLinkedTranId() == Long.MIN_VALUE) {
										linkedTranId = getPostingsDAO().getLinkedTransId();
									} else {
										linkedTranId = returnSetEntries.get(0).getLinkedTranId();
									}

									//Method for Checking for Reverse Calculations Based upon Negative Amounts
									for (ReturnDataSet returnDataSet : returnSetEntries) {

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

									//Core Banking Posting Process call
									returnSetEntries = getPostingsInterfaceService().doFillPostingDetails(
											returnSetEntries, financeMain.getFinBranch(), linkedTranId, "Y");

									if (returnSetEntries != null && !returnSetEntries.isEmpty()) {
										ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
										for (int i = 0; i < returnSetEntries.size(); i++) {
											ReturnDataSet set = returnSetEntries.get(i);
											set.setLinkedTranId(linkedTranId);
											set.setPostDate(curBDay);
											if (!("0000".equals(StringUtils.trimToEmpty(set.getErrorId())) || StringUtils
													.isEmpty(StringUtils.trimToEmpty(set.getErrorId())))) {
												errorDetails.add(new ErrorDetails(set.getAccountType(), set
														.getErrorId(), "E", set.getErrorMsg() + " "
														+ PennantApplicationUtil.formatAccountNumber(set.getAccount()),
														new String[] {}, new String[] {}));
											} else {
												set.setPostStatus("S");
											}
										}
										auditHeader.setErrorList(errorDetails);
									}
								}
								list.addAll(returnSetEntries);
							}
						}
					}

				} else {

					List<ReturnDataSet> returnSetEntries = null;
					if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ADDDISB)) {

						// Get Approved Disbursement Records 
						List<FinanceDisbursement> oldDisbList = getFinanceDisbursementDAO()
								.getFinanceDisbursementDetails(financeMain.getFinReference(), "", false);
						List<FinanceDisbursement> curDisbList = financeDetail.getFinScheduleData()
								.getDisbursementDetails();

						// Loop Repetition for Multiple Disbursement
						if (curDisbList != null && !curDisbList.isEmpty()) {
							for (int i = 0; i < curDisbList.size(); i++) {
								FinanceDisbursement curDisb = curDisbList.get(i);
								if (StringUtils.equals(curDisb.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL)) {
									continue;
								}
								boolean isDisbFound = false;
								for (int j = 0; j < oldDisbList.size(); j++) {
									FinanceDisbursement oldDisb = oldDisbList.get(j);
									if (curDisb.getDisbDate().compareTo(oldDisb.getDisbDate()) == 0
											&& (curDisb.getDisbSeq() == oldDisb.getDisbSeq())) {
										isDisbFound = true;
										break;
									}
								}

								// If Disbursement not found in Existing List
								if (isDisbFound) {
									continue;
								}

								if (curDisb.getDisbDate().after(DateUtility.getAppDate())) {
									if (AccountEventConstants.ACCEVENT_ADDDBSF_REQ) {
										dataSet.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSF);
									} else {
										dataSet.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSP);
									}
								} else {
									dataSet.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSP);
								}

								if (StringUtils.isNotBlank(curDisb.getDisbAccountId())) {
									dataSet.setDisburseAccount(curDisb.getDisbAccountId());
								}
								dataSet.setDisburseAmount(curDisb.getDisbAmount());
								dataSet.setCurDisbRet(curDisb.getDisbRetAmount());
								dataSet.setNetRetDue(curDisb.getNetRetDue());
								dataSet.setClaimAmt(curDisb.getDisbClaim());
								dataSet.setGrcPftTillNow(calculateTillGrcProfit(financeDetail.getFinScheduleData(),
										curDisb.getDisbDate()));

								returnSetEntries = getEngineExecution().getAccEngineExecResults(dataSet, amountCodes,
										"Y", feeRuleDetailsMap, false,
										financeDetail.getFinScheduleData().getFinanceType(),
										financeDetail.getPremiumDetail());

								if (returnSetEntries != null && !returnSetEntries.isEmpty()) {

									// Method for validating Postings with interface program and
									// return results
									if (returnSetEntries.get(0).getLinkedTranId() == Long.MIN_VALUE) {
										linkedTranId = getPostingsDAO().getLinkedTransId();
									} else {
										linkedTranId = returnSetEntries.get(0).getLinkedTranId();
									}

									curDisb.setLinkedTranId(linkedTranId);

									//Method for Checking for Reverse Calculations Based upon Negative Amounts
									for (ReturnDataSet returnDataSet : returnSetEntries) {

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

									//Core Banking Posting Process call
									returnSetEntries = getPostingsInterfaceService().doFillPostingDetails(
											returnSetEntries, financeMain.getFinBranch(), linkedTranId, "Y");

									if (returnSetEntries != null && !returnSetEntries.isEmpty()) {
										ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
										for (int k = 0; k < returnSetEntries.size(); k++) {
											ReturnDataSet set = returnSetEntries.get(k);
											set.setLinkedTranId(linkedTranId);
											set.setPostDate(curBDay);
											if (!("0000".equals(StringUtils.trimToEmpty(set.getErrorId())) || StringUtils
													.isEmpty(StringUtils.trimToEmpty(set.getErrorId())))) {
												errorDetails.add(new ErrorDetails(set.getAccountType(), set
														.getErrorId(), "E", set.getErrorMsg() + " "
														+ PennantApplicationUtil.formatAccountNumber(set.getAccount()),
														new String[] {}, new String[] {}));
											} else {
												set.setPostStatus("S");
											}
										}
										auditHeader.setErrorList(errorDetails);
									}
								}

								list.addAll(returnSetEntries);
							}
						}

						oldDisbList = null;
					} else if (StringUtils.equals(financeDetail.getModuleDefiner(),
							FinanceConstants.FINSER_EVENT_CANCELDISB)) {

						// Get Approved Disbursement Records 
						List<FinanceDisbursement> oldDisbList = getFinanceDisbursementDAO()
								.getFinanceDisbursementDetails(financeMain.getFinReference(), "", false);
						List<FinanceDisbursement> curDisbList = financeDetail.getFinScheduleData()
								.getDisbursementDetails();

						// Loop Repetition for Multiple Disbursement
						if (curDisbList != null && !curDisbList.isEmpty()) {
							for (int i = 0; i < curDisbList.size(); i++) {
								FinanceDisbursement curDisb = curDisbList.get(i);
								if (!StringUtils.equals(curDisb.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL)) {
									continue;
								}
								boolean isCancelDisbExists = false;
								for (int j = 0; j < oldDisbList.size(); j++) {
									FinanceDisbursement oldDisb = oldDisbList.get(j);
									if (curDisb.getDisbDate().compareTo(oldDisb.getDisbDate()) == 0
											&& (curDisb.getDisbSeq() == oldDisb.getDisbSeq())
											&& StringUtils.equals(oldDisb.getDisbStatus(),
													FinanceConstants.DISB_STATUS_CANCEL)) {
										isCancelDisbExists = true;
										break;
									}
								}
								linkedTranId = curDisb.getLinkedTranId();

								// If Disbursement not found in Existing List
								if (isCancelDisbExists) {
									continue;
								}

								List<Object> returnList = getPostingsPreparationUtil().processFinCanclPostings("",
										String.valueOf(linkedTranId));
								logger.debug("Reverse Transaction Success for Transaction ID : " + linkedTranId);
								if (!(Boolean) returnList.get(0)) {
									logger.debug("Reverse Transaction failed for Transaction ID : " + linkedTranId);
									throw new PFFInterfaceException("9999", returnList.get(1).toString());
								}
								returnList = null;
							}
						}
						oldDisbList = null;
					} else {

						//Accounting Execution for Maintenance
						returnSetEntries = getEngineExecution().getAccEngineExecResults(dataSet, amountCodes, "Y",
								feeRuleDetailsMap, false, financeDetail.getFinScheduleData().getFinanceType(),
								financeDetail.getPremiumDetail());

						if (returnSetEntries != null && !returnSetEntries.isEmpty()) {

							// Method for validating Postings with interface program and
							// return results
							if (returnSetEntries.get(0).getLinkedTranId() == Long.MIN_VALUE) {
								linkedTranId = getPostingsDAO().getLinkedTransId();
							} else {
								linkedTranId = returnSetEntries.get(0).getLinkedTranId();
							}

							//Method for Checking for Reverse Calculations Based upon Negative Amounts
							for (ReturnDataSet returnDataSet : returnSetEntries) {

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

							//Core Banking Posting Process call
							returnSetEntries = getPostingsInterfaceService().doFillPostingDetails(returnSetEntries,
									financeMain.getFinBranch(), linkedTranId, "Y");

							if (returnSetEntries != null && !returnSetEntries.isEmpty()) {
								ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
								for (int i = 0; i < returnSetEntries.size(); i++) {
									ReturnDataSet set = returnSetEntries.get(i);
									set.setLinkedTranId(linkedTranId);
									set.setPostDate(curBDay);
									if (!("0000".equals(StringUtils.trimToEmpty(set.getErrorId())) || StringUtils
											.isEmpty(StringUtils.trimToEmpty(set.getErrorId())))) {
										errorDetails.add(new ErrorDetails(set.getAccountType(), set.getErrorId(), "E",
												set.getErrorMsg() + " "
														+ PennantApplicationUtil.formatAccountNumber(set.getAccount()),
												new String[] {}, new String[] {}));
									} else {
										set.setPostStatus("S");
									}
								}
								auditHeader.setErrorList(errorDetails);
							}
						}

						list.addAll(returnSetEntries);
					}
				}

				//Stage Accounting Process Execution
				if (financeDetail.getStageAccountingList() != null && financeDetail.getStageAccountingList().size() > 0) {
					dataSet.setFinEvent(AccountEventConstants.ACCEVENT_STAGE);
					List<ReturnDataSet> returnSetEntries = getEngineExecution().getStageExecResults(dataSet,
							amountCodes, "Y", financeMain.getRoleCode(), feeRuleDetailsMap,
							financeDetail.getFinScheduleData().getFinanceType(), financeDetail.getPremiumDetail());

					if (returnSetEntries != null && !returnSetEntries.isEmpty()) {

						// Method for validating Postings with interface program and
						// return results
						if (returnSetEntries.get(0).getLinkedTranId() == Long.MIN_VALUE) {
							linkedTranId = getPostingsDAO().getLinkedTransId();
						} else {
							linkedTranId = returnSetEntries.get(0).getLinkedTranId();
						}

						//Method for Checking for Reverse Calculations Based upon Negative Amounts
						for (ReturnDataSet returnDataSet : returnSetEntries) {

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

						//Core Banking Posting Process call
						returnSetEntries = getPostingsInterfaceService().doFillPostingDetails(returnSetEntries,
								financeMain.getFinBranch(), linkedTranId, "Y");

						if (returnSetEntries != null && !returnSetEntries.isEmpty()) {
							ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
							for (int i = 0; i < returnSetEntries.size(); i++) {
								ReturnDataSet set = returnSetEntries.get(i);
								set.setLinkedTranId(linkedTranId);
								set.setPostDate(curBDay);
								if (!("0000".equals(StringUtils.trimToEmpty(set.getErrorId())) || StringUtils
										.isEmpty(StringUtils.trimToEmpty(set.getErrorId())))) {
									errorDetails.add(new ErrorDetails(set.getAccountType(), set.getErrorId(), "E", set
											.getErrorMsg()
											+ " "
											+ PennantApplicationUtil.formatAccountNumber(set.getAccount()),
											new String[] {}, new String[] {}));
								} else {
									set.setPostStatus("S");
								}
							}
							auditHeader.setErrorList(errorDetails);
						}
					}

					list.addAll(returnSetEntries);
				}

			} else {
				List<AEAmountCodesRIA> riaDetailList = new ArrayList<AEAmountCodesRIA>();
				List<FinContributorDetail> contributorDetailList = null;

				financeDetail.getFinContributorHeader().getContributorDetailList();

				if (financeDetail.getFinContributorHeader() != null) {
					contributorDetailList = financeDetail.getFinContributorHeader().getContributorDetailList();
					if (contributorDetailList != null && !contributorDetailList.isEmpty()) {
						riaDetailList = getEngineExecutionRIA().prepareRIADetails(contributorDetailList,
								dataSet.getFinReference());
					}
				}

				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)
						&& !StringUtils
								.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ROLLOVER)) {

					// Loop Repetition for Multiple Disbursement
					List<FinanceDisbursement> disbursementDetails = financeDetail.getFinScheduleData()
							.getDisbursementDetails();
					List<ReturnDataSet> returnSetEntries = new ArrayList<>();

					if (disbursementDetails != null && !disbursementDetails.isEmpty()) {
						for (FinanceDisbursement disbursement : disbursementDetails) {
							if (disbursement.getDisbAmount().compareTo(BigDecimal.ZERO) > 0) {
								if (disbursement.getDisbDate().after(DateUtility.getAppDate())) {
									dataSet.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSF);
								} else {
									dataSet.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSP);
								}

								dataSet.setDisburseAccount(disbursement.getDisbAccountId());
								dataSet.setDisburseAmount(disbursement.getDisbAmount());
								dataSet.setCurDisbRet(disbursement.getDisbRetAmount());
								dataSet.setNetRetDue(disbursement.getNetRetDue());
								returnSetEntries.addAll(getEngineExecutionRIA().getAccEngineExecResults(dataSet,
										amountCodes, "Y", riaDetailList, feeRuleDetailsMap));
							}
						}

						if (returnSetEntries != null && !returnSetEntries.isEmpty()) {

							// Method for validating Postings with interface program and
							// return results
							if (returnSetEntries.get(0).getLinkedTranId() == Long.MIN_VALUE) {
								linkedTranId = getPostingsDAO().getLinkedTransId();
							} else {
								linkedTranId = returnSetEntries.get(0).getLinkedTranId();
							}

							//Method for Checking for Reverse Calculations Based upon Negative Amounts
							for (ReturnDataSet returnDataSet : returnSetEntries) {

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

							//Core Banking Posting Process call
							returnSetEntries = getPostingsInterfaceService().doFillPostingDetails(returnSetEntries,
									financeMain.getFinBranch(), linkedTranId, "Y");

							if (returnSetEntries != null && !returnSetEntries.isEmpty()) {
								ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
								for (int i = 0; i < returnSetEntries.size(); i++) {
									ReturnDataSet set = returnSetEntries.get(i);
									set.setLinkedTranId(linkedTranId);
									set.setPostDate(curBDay);
									if (!("0000".equals(StringUtils.trimToEmpty(set.getErrorId())) || StringUtils
											.isEmpty(StringUtils.trimToEmpty(set.getErrorId())))) {
										errorDetails.add(new ErrorDetails(set.getAccountType(), set.getErrorId(), "E",
												set.getErrorMsg() + " "
														+ PennantApplicationUtil.formatAccountNumber(set.getAccount()),
												new String[] {}, new String[] {}));
									} else {
										set.setPostStatus("S");
									}
								}
								auditHeader.setErrorList(errorDetails);
							}
						}

						list.addAll(returnSetEntries);
					}

					// Finance GraceEnd Posting Details
					if (StringUtils.trimToEmpty(productType).equalsIgnoreCase(FinanceConstants.PRODUCT_IJARAH)
							|| StringUtils.trimToEmpty(productType).equalsIgnoreCase(FinanceConstants.PRODUCT_FWIJARAH)) {
						if (StringUtils.trimToEmpty(financeMain.getRecordType()).equals(
								PennantConstants.RECORD_TYPE_NEW)
								&& !StringUtils.equals(financeDetail.getModuleDefiner(),
										FinanceConstants.FINSER_EVENT_ROLLOVER)) {
							if (financeMain.getGrcPeriodEndDate() != null
									&& financeMain.getGrcPeriodEndDate().compareTo(curBDay) <= 0) {
								DataSet dataset = AEAmounts.createDataSet(financeMain,
										financeDetail.getAccountingEventCode(), financeMain.getFinStartDate(),
										financeMain.getFinStartDate());
								dataset.setFinEvent(AccountEventConstants.ACCEVENT_GRACEEND);
								returnSetEntries = getEngineExecutionRIA().getAccEngineExecResults(dataset,
										amountCodes, "Y", riaDetailList, feeRuleDetailsMap);

								if (returnSetEntries != null && !returnSetEntries.isEmpty()) {

									// Method for validating Postings with interface program and
									// return results
									if (returnSetEntries.get(0).getLinkedTranId() == Long.MIN_VALUE) {
										linkedTranId = getPostingsDAO().getLinkedTransId();
									} else {
										linkedTranId = returnSetEntries.get(0).getLinkedTranId();
									}

									//Method for Checking for Reverse Calculations Based upon Negative Amounts
									for (ReturnDataSet returnDataSet : returnSetEntries) {

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

									//Core Banking Posting Process call
									returnSetEntries = getPostingsInterfaceService().doFillPostingDetails(
											returnSetEntries, financeMain.getFinBranch(), linkedTranId, "Y");

									if (returnSetEntries != null && !returnSetEntries.isEmpty()) {
										ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
										for (int i = 0; i < returnSetEntries.size(); i++) {
											ReturnDataSet set = returnSetEntries.get(i);
											set.setLinkedTranId(linkedTranId);
											set.setPostDate(curBDay);
											if (!("0000".equals(StringUtils.trimToEmpty(set.getErrorId())) || StringUtils
													.isEmpty(StringUtils.trimToEmpty(set.getErrorId())))) {
												errorDetails.add(new ErrorDetails(set.getAccountType(), set
														.getErrorId(), "E", set.getErrorMsg() + " "
														+ PennantApplicationUtil.formatAccountNumber(set.getAccount()),
														new String[] {}, new String[] {}));
											} else {
												set.setPostStatus("S");
											}
										}
										auditHeader.setErrorList(errorDetails);
									}
								}

								list.addAll(returnSetEntries);
							}
						}
					}
				} else {

					//Accounting Execution for Maintenance
					List<ReturnDataSet> returnSetEntries = getEngineExecutionRIA().getAccEngineExecResults(dataSet,
							amountCodes, "Y", riaDetailList, feeRuleDetailsMap);
					if (returnSetEntries != null && !returnSetEntries.isEmpty()) {

						// Method for validating Postings with interface program and
						// return results
						if (returnSetEntries.get(0).getLinkedTranId() == Long.MIN_VALUE) {
							linkedTranId = getPostingsDAO().getLinkedTransId();
						} else {
							linkedTranId = returnSetEntries.get(0).getLinkedTranId();
						}

						//Method for Checking for Reverse Calculations Based upon Negative Amounts
						for (ReturnDataSet returnDataSet : returnSetEntries) {

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

						//Core Banking Posting Process call
						returnSetEntries = getPostingsInterfaceService().doFillPostingDetails(returnSetEntries,
								financeMain.getFinBranch(), linkedTranId, "Y");

						if (returnSetEntries != null && !returnSetEntries.isEmpty()) {
							ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
							for (int i = 0; i < returnSetEntries.size(); i++) {
								ReturnDataSet set = returnSetEntries.get(i);
								set.setLinkedTranId(linkedTranId);
								set.setPostDate(curBDay);
								if (!("0000".equals(StringUtils.trimToEmpty(set.getErrorId())) || StringUtils
										.isEmpty(StringUtils.trimToEmpty(set.getErrorId())))) {
									errorDetails.add(new ErrorDetails(set.getAccountType(), set.getErrorId(), "E", set
											.getErrorMsg()
											+ " "
											+ PennantApplicationUtil.formatAccountNumber(set.getAccount()),
											new String[] {}, new String[] {}));
								} else {
									set.setPostStatus("S");
								}
							}
							auditHeader.setErrorList(errorDetails);
						}
					}

					list.addAll(returnSetEntries);
				}
			}

			// Used for Commitment Purpose , Commented for Limit Usage
			// Finance Commitment Reference Posting Details
			if (StringUtils.isNotBlank(financeMain.getFinCommitmentRef())
					&& financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)
					&& !StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ROLLOVER)) {
				commitment = getCommitmentDAO().getCommitmentById(financeMain.getFinCommitmentRef(), "");

				AECommitment aeCommitment = new AECommitment();
				aeCommitment.setCMTAMT(commitment.getCmtAmount());
				aeCommitment.setCHGAMT(commitment.getCmtCharges());
				aeCommitment
						.setDISBURSE(CalculationUtil.getConvertedAmount(
								financeMain.getFinCcy(),
								commitment.getCmtCcy(),
								financeMain.getFinAmount().subtract(
										financeMain.getDownPayment() == null ? BigDecimal.ZERO : financeMain
												.getDownPayment())));
				aeCommitment.setRPPRI(BigDecimal.ZERO);

				List<ReturnDataSet> returnSetEntries = getEngineExecution().getCommitmentExecResults(aeCommitment,
						commitment, AccountEventConstants.ACCEVENT_CMTDISB, "Y", null);
				if (returnSetEntries != null && !returnSetEntries.isEmpty()) {

					// Method for validating Postings with interface program and
					// return results
					if (returnSetEntries.get(0).getLinkedTranId() == Long.MIN_VALUE) {
						linkedTranId = getPostingsDAO().getLinkedTransId();
					} else {
						linkedTranId = returnSetEntries.get(0).getLinkedTranId();
					}

					//Method for Checking for Reverse Calculations Based upon Negative Amounts
					for (ReturnDataSet returnDataSet : returnSetEntries) {

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

					//Core Banking Posting Process call
					returnSetEntries = getPostingsInterfaceService().doFillPostingDetails(returnSetEntries,
							financeMain.getFinBranch(), linkedTranId, "Y");

					if (returnSetEntries != null && !returnSetEntries.isEmpty()) {
						ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
						for (int i = 0; i < returnSetEntries.size(); i++) {
							ReturnDataSet set = returnSetEntries.get(i);
							set.setLinkedTranId(linkedTranId);
							set.setPostDate(curBDay);
							if (!("0000".equals(StringUtils.trimToEmpty(set.getErrorId())) || StringUtils
									.isEmpty(StringUtils.trimToEmpty(set.getErrorId())))) {
								errorDetails.add(new ErrorDetails(set.getAccountType(), set.getErrorId(), "E", set
										.getErrorMsg()
										+ " "
										+ PennantApplicationUtil.formatAccountNumber(set.getAccount()),
										new String[] {}, new String[] {}));
							} else {
								set.setPostStatus("S");
								// Used for Commitment Purpose , Commented for Limit Usage
								if (AccountEventConstants.ACCEVENT_CMTDISB.equals(set.getFinEvent())) {
									cmtPostAmt = set.getPostAmount();
								}
							}
						}
						auditHeader.setErrorList(errorDetails);
					}
				}
				list.addAll(returnSetEntries);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
			errorDetails.add(new ErrorDetails("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
					"Accounting Engine Failed to Create Postings:" + e.getMessage(), new String[] {}, new String[] {}));
			auditHeader.setErrorList(errorDetails);
			list = null;
		}

		if (auditHeader.getErrorMessage() == null || auditHeader.getErrorMessage().size() == 0) {

			// Past due Deferment Details Process
			if (!financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)
					&& (StringUtils.trimToEmpty(financeMain.getRcdMaintainSts()).equals(
							FinanceConstants.FINSER_EVENT_POSTPONEMENT) || StringUtils.trimToEmpty(
							financeMain.getRcdMaintainSts()).equals(FinanceConstants.FINSER_EVENT_CHGFRQ))) {

				List<Date> pastdueDefDateList = new ArrayList<Date>();

				if (StringUtils.trimToEmpty(financeMain.getRcdMaintainSts()).equals(
						FinanceConstants.FINSER_EVENT_CHGFRQ)) {

					List<FinanceScheduleDetail> schDetailList = financeDetail.getFinScheduleData()
							.getFinanceScheduleDetails();
					List<Date> schDateList = new ArrayList<Date>();
					for (int i = 0; i < schDetailList.size(); i++) {
						FinanceScheduleDetail curSchd = schDetailList.get(i);
						if (!(curSchd.isRepayOnSchDate() || (curSchd.isPftOnSchDate() && curSchd.getRepayAmount()
								.compareTo(BigDecimal.ZERO) > 0))) {
							continue;
						}
						if (curSchd.getSchDate().compareTo(curBDay) > 0) {
							break;
						}
						schDateList.add(curSchd.getSchDate());
					}

					//Fetch Past Due Modified Schedule Details List
					if (!schDateList.isEmpty()) {
						pastdueDefDateList = getFinODDetailsDAO().getMismatchODDates(financeMain.getFinReference(),
								schDateList);
					}
				}

				// Process on Overdue Details with Past due Deferment Schedules
				if (!pastdueDefDateList.isEmpty()) {

					//Fetching Overdue Penalty Recovery Details, If any Paid either Partially or Fully
					BigDecimal totPenaltyPaid = getRecoveryDAO().getPaidPenaltiesbySchDates(
							financeMain.getFinReference(), pastdueDefDateList);

					// DataSet Creation
					dataSet.setFinEvent(AccountEventConstants.ACCEVENT_LATEPAY);
					dataSet.setNewRecord(false);

					// AmountCodes Preparation-- Overdue Paid Amount Reversal for 
					amountCodes.setPENALTY(totPenaltyPaid.negate());
					amountCodes.setWAIVER(BigDecimal.ZERO);

					// Accounting Set Execution to get Posting Details List
					Date dateAppDate = DateUtility.getAppDate();
					List<Object> resultList = null;
					try {
						resultList = getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, false,
								false, "Y", dateAppDate, false, Long.MIN_VALUE);
					} catch (IllegalAccessException e) {
						logger.error("Exception: ", e);
					} catch (InvocationTargetException e) {
						logger.error("Exception: ", e);
					}

					//If Reversal Posting for Overdue Recover Details are Successful 
					if (resultList != null && (Boolean) resultList.get(0)) {
						//Save History of Overdue Details and Overdue Recovery Details
						getRecoveryDAO().saveODDeferHistory(financeMain.getFinReference(), pastdueDefDateList);
						getFinODDetailsDAO().saveODDeferHistory(financeMain.getFinReference(), pastdueDefDateList);

						//Get Maximum Overdue Days with in Deletion Past Due Terms
						int maxODDays = getFinODDetailsDAO().getMaxODDaysOnDeferSchd(financeMain.getFinReference(),
								pastdueDefDateList);

						//Delete Overdue Recovery Details & Overdue Details
						getRecoveryDAO().deleteODDeferHistory(financeMain.getFinReference(), pastdueDefDateList);
						getFinODDetailsDAO().deleteODDeferHistory(financeMain.getFinReference(), pastdueDefDateList);

						//Finance Status Insertion newly with today's Value date on this Finance
						String curFinStatus = getCustomerStatusCodeDAO().getFinanceStatus(
								financeMain.getFinReference(), true);
						String finStsReason = FinanceConstants.FINSTSRSN_MANUAL;
						boolean isStsChanged = false;

						if (!financeMain.getFinStatus().equals(curFinStatus)) {
							isStsChanged = true;
						}

						//Finance Status Details insertion, if status modified then change to High Risk Level
						if (isStsChanged) {
							FinStatusDetail statusDetail = new FinStatusDetail();
							statusDetail.setFinReference(financeMain.getFinReference());
							statusDetail.setValueDate(dateValueDate);
							statusDetail.setCustId(financeMain.getCustID());
							statusDetail.setFinStatus(curFinStatus);
//							statusDetail.setFinStatusReason(finStsReason);

							getFinStatusDetailDAO().saveOrUpdateFinStatus(statusDetail);
						}

						financeMain.setFinStatus(curFinStatus);

						//First OD Date and Last OD Date checking (Setting NULL in case past due Deferment Schedule dates and OD Dates are equal )
						//& Rebuild AmountCodes preparation
						for (int i = 0; i < pastdueDefDateList.size(); i++) {
							if (pftDetail.getFirstODDate() != null
									&& pftDetail.getFirstODDate().compareTo(pastdueDefDateList.get(i)) == 0) {
								pftDetail.setFirstODDate(null);
							}
							if (pftDetail.getPrvODDate() != null
									&& pftDetail.getPrvODDate().compareTo(pastdueDefDateList.get(i)) == 0) {
								pftDetail.setPrvODDate(null);
							}
						}

						if (StringUtils.trimToEmpty(financeMain.getRcdMaintainSts()).equals(
								FinanceConstants.FINSER_EVENT_POSTPONEMENT)) {
							suspenseCheckProcess(financeMain, FinanceConstants.FINSER_EVENT_POSTPONEMENT,
									dateValueDate, financeDetail.getFinScheduleData().getFinanceType()
											.isAllowRIAInvestment(), curFinStatus, maxODDays);
						}

						//AEAmounts Recalculation after Removing Overdue Details 
						amountCodes = AEAmounts.procAEAmounts(financeMain, financeDetail.getFinScheduleData()
								.getFinanceScheduleDetails(), pftDetail, curBDay);
					} else {

						String error = (String) resultList.get(2);

						ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
						errorDetails.add(new ErrorDetails("", error.substring(0, error.indexOf('-')), "E", error,
								new String[] {}, new String[] {}));
						auditHeader.setErrorList(errorDetails);
					}
				}
			}

			if (auditHeader.getErrorMessage() == null || auditHeader.getErrorMessage().size() == 0) {

				// save Postings
				if (list != null && !list.isEmpty()) {
					getPostingsDAO().saveBatch(list, "", false);
				}

				// Save/Update Finance Profit Details
				boolean isNew = false;

				if (StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
					isNew = true;
				}

				FinanceProfitDetail profitDetail = doSave_PftDetails(pftDetail, isNew);

				//Account Details Update
				if (list != null && !list.isEmpty()) {
					getAccountProcessUtil().procAccountUpdate(list, profitDetail.getPftAccrued());
				}

				// Used for Commitment Purpose , Commented for Limit Usage
				if (commitment != null && cmtPostAmt.compareTo(BigDecimal.ZERO) > 0) {
					getCommitmentDAO().updateCommitmentAmounts(commitment.getCmtReference(), cmtPostAmt,
							commitment.getCmtExpDate());
					CommitmentMovement movement = prepareCommitMovement(commitment, financeMain, cmtPostAmt,
							linkedTranId);
					if (movement != null) {
						getCommitmentMovementDAO().save(movement, "");
					}
				}
			}
		}

		logger.debug("Leaving");
		return auditHeader;
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
			boolean alwRIA, String curFinsts, int maxODDays) throws PFFInterfaceException {

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
					getSuspensePostingUtil().suspensePreparation(financeMain, repayQueue, dateValueDate, alwRIA, true);
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
	 * Method for Processing each stage Accounting Entry details for particular Finance
	 * 
	 * @param auditHeader
	 * @param list
	 * @return
	 * @throws AccountNotFoundException
	 */
	protected AuditHeader executeStageAccounting(AuditHeader auditHeader, List<ReturnDataSet> list)
			throws PFFInterfaceException {
		logger.debug("Entering");

		FinanceDetail financeDetail = null;

		if (auditHeader.getAuditDetail().getModelData() instanceof FinanceDetail) {
			financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		} else if (auditHeader.getAuditDetail().getModelData() instanceof LiabilityRequest) {
			financeDetail = ((LiabilityRequest) auditHeader.getAuditDetail().getModelData()).getFinanceDetail();
		} else if (auditHeader.getAuditDetail().getModelData() instanceof RepayData) {
			financeDetail = ((RepayData) auditHeader.getAuditDetail().getModelData()).getFinanceDetail();
		} else {
			logger.debug("Leaving");
			return auditHeader;
		}
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		long linkedTranId = Long.MIN_VALUE;
		AEAmountCodes amountCodes = null;
		Date curBDay = DateUtility.getAppDate();

		DataSet dataSet = AEAmounts.createDataSet(financeMain, AccountEventConstants.ACCEVENT_STAGE,
				financeMain.getFinStartDate(), financeMain.getFinStartDate());
		dataSet.setModuledefiner(financeDetail.getModuleDefiner());

		amountCodes = AEAmounts.procAEAmounts(financeMain, financeDetail.getFinScheduleData()
				.getFinanceScheduleDetails(), new FinanceProfitDetail(), financeMain.getFinStartDate());

		Map<String, FeeRule> feeRuleDetailsMap = null;
		/*
		 * if (financeDetail.getFinScheduleData().getFeeRules() != null &&
		 * financeDetail.getFinScheduleData().getFeeRules().size() > 0) {
		 * 
		 * feeRuleDetailsMap = new HashMap<String, FeeRule>(); for (FeeRule feeRule :
		 * financeDetail.getFinScheduleData().getFeeRules()) { if (!feeRuleDetailsMap.containsKey(feeRule.getFeeCode()))
		 * { feeRuleDetailsMap.put(feeRule.getFeeCode(), feeRule); } } }
		 */

		List<FinFeeDetail> finFeeDetailList = financeDetail.getFinScheduleData().getFinFeeDetailList();
		if (finFeeDetailList != null) {
			feeRuleDetailsMap = new HashMap<>();
			FeeRule feeRule;
			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				feeRule = new FeeRule();
				feeRule.setFeeCode(finFeeDetail.getFeeTypeCode());
				feeRule.setFeeAmount(finFeeDetail.getActualAmount());
				feeRule.setWaiverAmount(finFeeDetail.getWaivedAmount());
				feeRule.setPaidAmount(finFeeDetail.getPaidAmount());
				feeRule.setFeeToFinance(finFeeDetail.getFeeScheduleMethod());
				feeRule.setFeeMethod(finFeeDetail.getFeeScheduleMethod());
				feeRuleDetailsMap.put(feeRule.getFeeCode(), feeRule);
			}
		}

		// Check Previously done Postings on same Stage with Reference
		//=======================================
		List<ReturnDataSet> newStageAcEntries = null;
		try {
			newStageAcEntries = getEngineExecution().getStageExecResults(dataSet, amountCodes, "N",
					financeMain.getRoleCode(), feeRuleDetailsMap, financeDetail.getFinScheduleData().getFinanceType(),
					financeDetail.getPremiumDetail());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
			errorDetails.add(new ErrorDetails("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
					"Accounting Engine Failed to Create Postings:" + e.getMessage(), new String[] {}, new String[] {}));
			auditHeader.setErrorList(errorDetails);
			logger.debug("Leaving");
			return auditHeader;
		}

		if (!prvStageAccountingCheck(newStageAcEntries, financeMain.getFinReference(),
				financeDetail.getModuleDefiner(), financeMain.getRoleCode())) {
			logger.debug(Labels.getLabel("label_Finance_Recal_StageAccountings"));
			logger.debug("Leaving");
			return auditHeader;
		}

		try {
			list.addAll(getEngineExecution().getStageExecResults(dataSet, amountCodes, "Y", financeMain.getRoleCode(),
					feeRuleDetailsMap, financeDetail.getFinScheduleData().getFinanceType(),
					financeDetail.getPremiumDetail()));
		} catch (Exception e) {
			logger.error("Exception: ", e);
			ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
			errorDetails.add(new ErrorDetails("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
					"Accounting Engine Failed to Create Postings:" + e.getMessage(), new String[] {}, new String[] {}));
			auditHeader.setErrorList(errorDetails);
		}

		// Method for validating Postings with interface program and return results
		if (list.get(0).getLinkedTranId() == Long.MIN_VALUE) {
			linkedTranId = getPostingsDAO().getLinkedTransId();
		} else {
			linkedTranId = list.get(0).getLinkedTranId();
		}

		//Method for Checking for Reverse Calculations Based upon Negative Amounts
		for (ReturnDataSet returnDataSet : list) {

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

		if (!ImplementationConstants.INDIAN_IMPLEMENTATION) {
			list = getPostingsInterfaceService().doFillPostingDetails(list, financeMain.getFinBranch(), linkedTranId,
					"Y");
		}

		/*
		 * list = getPostingsInterfaceService().doFillPostingDetails(list, financeMain.getFinBranch(), linkedTranId,
		 * "Y");
		 */
		if (list != null && list.size() > 0) {
			ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
			for (int i = 0; i < list.size(); i++) {
				ReturnDataSet set = list.get(i);
				set.setLinkedTranId(linkedTranId);
				set.setPostDate(curBDay);
				set.getPostStatus();
				if (!("0000".equals(set.getErrorId()) || StringUtils.isEmpty(set.getErrorId()))) {
					set.setErrorMsg("Stage Accounting is Failed");
					errorDetails.add(new ErrorDetails(set.getAccountType(), set.getErrorId(), "E", set.getErrorMsg(),
							new String[] {}, new String[] {}));
				}
			}
			auditHeader.setErrorList(errorDetails);
			// save Postings
			getPostingsDAO().saveBatch(list, "", false);
		}

		// Stage Accounting Entry Details Saving
		//=======================================
		if (auditHeader.getErrorMessage() == null || auditHeader.getErrorMessage().isEmpty()) {

			FinStageAccountingLog stageAccountingLog = new FinStageAccountingLog();
			stageAccountingLog.setFinReference(financeMain.getFinReference());
			stageAccountingLog.setFinEvent(financeDetail.getModuleDefiner());
			stageAccountingLog.setRoleCode(financeMain.getRoleCode());
			stageAccountingLog.setLinkedTranId(linkedTranId);
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
	 * @throws AccountNotFoundException
	 */
	protected boolean prvStageAccountingCheck(List<ReturnDataSet> curStageAccEntries, String finReference,
			String finEvent, String roleCode) throws PFFInterfaceException {
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

			List<Object> returnList = getPostingsPreparationUtil().processFinCanclPostings(finReference,
					String.valueOf(linkedTranId));
			if (!(Boolean) returnList.get(0)) {
				throw new PFFInterfaceException("9999", returnList.get(1).toString());
			}
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
	 * @throws PFFInterfaceException
	 */
	protected void cancelStageAccounting(String finReference, String finEvent) throws PFFInterfaceException {

		List<Long> excdTranIdList = getFinStageAccountingLogDAO().getLinkedTranIdList(finReference, finEvent);
		if (excdTranIdList != null && !excdTranIdList.isEmpty()) {
			for (Long linkedTranId : excdTranIdList) {
				List<Object> returnList = getPostingsPreparationUtil().processFinCanclPostings(finReference,
						String.valueOf(linkedTranId));
				if (!(Boolean) returnList.get(0)) {
					logger.debug("Reverse Transaction failed for Transaction ID : " + linkedTranId);
					//Since Error Message can be null;
					String ermsg = returnList.get(1) != null ? returnList.get(1).toString()
							: "Reverse Transaction failed for Transaction ID : " + linkedTranId;
					throw new PFFInterfaceException("9999", ermsg);
				}
				logger.debug("Reverse Transaction Success for Transaction ID : " + linkedTranId);
			}
		}
	}

	/**
	 * Method for Calculate Grace Profit Till
	 * 
	 * @return
	 */
	private BigDecimal calculateTillGrcProfit(FinScheduleData scheduleData, Date disbDate) {
		logger.debug("Entering");

		BigDecimal totGrcPftTillNow = BigDecimal.ZERO;
		List<FinanceScheduleDetail> list = scheduleData.getFinanceScheduleDetails();
		for (FinanceScheduleDetail curSchdl : list) {
			if (curSchdl.getSchDate().compareTo(disbDate) <= 0) {
				totGrcPftTillNow = totGrcPftTillNow.add(curSchdl.getProfitCalc());
			} else {
				break;
			}
		}
		logger.debug("Leaving");
		return totGrcPftTillNow;
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

	public AccountEngineExecutionRIA getEngineExecutionRIA() {
		return engineExecutionRIA;
	}

	public void setEngineExecutionRIA(AccountEngineExecutionRIA engineExecutionRIA) {
		this.engineExecutionRIA = engineExecutionRIA;
	}

	public PostingsInterfaceService getPostingsInterfaceService() {
		return postingsInterfaceService;
	}

	public void setPostingsInterfaceService(PostingsInterfaceService postingsInterfaceService) {
		this.postingsInterfaceService = postingsInterfaceService;
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

}
