package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.UserDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.dedup.DedupFieldsDAO;
import com.pennant.backend.dao.dedup.DedupParmDAO;
import com.pennant.backend.dao.finance.FinanceDeviationsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.impl.FinanceDeviationsDAOImpl;
import com.pennant.backend.dao.findedup.FinanceDedupeDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.perfios.PerfiosTransactionDAO;
import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.model.BuilderTable;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinCustomerDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.LoanStage;
import com.pennant.backend.model.finance.OverDraftMaintenance;
import com.pennant.backend.model.finance.UserActions;
import com.pennant.backend.model.finance.UserPendingCases;
import com.pennant.backend.model.finance.UserPendingCasesResponse;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.perfios.PerfiosTransaction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.impl.FinanceDataDefaulting;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.validation.CreateFinanceGroup;
import com.pennant.validation.CreateFinanceWithCollateral;
import com.pennant.validation.CreateFinancewithWIFGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.activity.log.Activity;
import com.pennanttech.activity.log.ActivityLogService;
import com.pennanttech.controller.CreateFinanceController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.CreateFinanceRestService;
import com.pennanttech.pffws.CreateFinanceSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.activity.ActivityLogDetails;
import com.pennanttech.ws.model.customer.AgreementRequest;
import com.pennanttech.ws.model.deviation.DeviationList;
import com.pennanttech.ws.model.eligibility.AgreementData;
import com.pennanttech.ws.model.eligibility.AgreementDetails;
import com.pennanttech.ws.model.finance.FinanceDedupDetails;
import com.pennanttech.ws.model.finance.FinanceDedupRequest;
import com.pennanttech.ws.model.finance.FinanceDedupResponse;
import com.pennanttech.ws.model.finance.LoanStatus;
import com.pennanttech.ws.model.finance.LoanStatusDetails;
import com.pennanttech.ws.model.finance.MoveLoanStageRequest;
import com.pennanttech.ws.model.financetype.FinanceInquiry;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class CreateFinanceWebServiceImpl implements CreateFinanceSoapService, CreateFinanceRestService {

	private static final Logger logger = LogManager.getLogger(CreateFinanceWebServiceImpl.class);

	private CreateFinanceController createFinanceController;
	private CustomerDetailsService customerDetailsService;
	private FinanceDetailService financeDetailService;
	private ValidationUtility validationUtility;
	private FinanceMainDAO financeMainDAO;
	private FinanceDataDefaulting financeDataDefaulting;
	private FinanceDataValidation financeDataValidation;
	private CollateralSetupService collateralSetupService;
	private ActivityLogService activityLogService;
	private UserDAO userDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private FinanceDeviationsDAO financeDeviationsDAO;
	private DeviationHelper deviationHelper;
	private SecurityUserDAO securityUserDAO;
	private PerfiosTransactionDAO perfiosTransactionDAO;
	private DedupFieldsDAO dedupFieldsDAO;
	private DedupParmDAO dedupParmDAO;
	private FinanceDedupeDAO financeDedupeDAO;

	/**
	 * validate and create finance by receiving request object from interface
	 * 
	 * @param financeDetail
	 */
	@Override
	public FinanceDetail createFinance(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		FinScheduleData fsData = financeDetail.getFinScheduleData();
		FinanceMain finMain = fsData.getFinanceMain();
		finMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);

		// do Basic mandatory validations using hibernate validator
		validationUtility.validate(financeDetail, CreateFinanceGroup.class);

		if (!CollectionUtils.isEmpty(financeDetail.getCollaterals())) {
			for (CollateralSetup setup : financeDetail.getCollaterals()) {
				validationUtility.validate(setup, CreateFinanceWithCollateral.class);
			}
		}

		if (finMain == null) {
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			String[] valueParm = new String[1];
			valueParm[0] = "financeDetail";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		if (StringUtils.isBlank(finMain.getCustCIF()) && StringUtils.isBlank(finMain.getCoreBankId())) {
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			String[] valueParm = new String[1];
			valueParm[0] = "Cif/CoreBankId";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		// for logging purpose
		String[] logFields = getLogFields(financeDetail);
		APIErrorHandlerService.logKeyFields(logFields);

		// logging customer CIF as reference for create loan failure cases
		String custCif = logFields[0];
		APIErrorHandlerService.logReference(custCif);

		try {
			// validate and Data defaulting
			financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_LOAN, financeDetail);

			if (!fsData.getErrorDetails().isEmpty()) {
				return getErrorMessage(fsData);
			}

			if (finMain.getProductCategory().equals(FinanceConstants.PRODUCT_ODFACILITY)) {
				FinanceDetail finResponse = createOverDraftLoanValidation(financeDetail);
				WSReturnStatus status = finResponse.getReturnStatus();
				if (status != null) {
					doEmptyResponseObject(finResponse);
					return finResponse;
				}
			}

			// validate Finance schedule details Validations
			financeDataValidation.financeDataValidation(PennantConstants.VLD_CRT_LOAN,
					financeDetail.getFinScheduleData(), true, financeDetail, false);
			if (!financeDetail.getFinScheduleData().getErrorDetails().isEmpty()) {
				return getErrorMessage(financeDetail.getFinScheduleData());
			}

			// validate FinanceDetail Validations
			financeDataValidation.financeDetailValidation(PennantConstants.VLD_CRT_LOAN, financeDetail, true);
			if (!financeDetail.getFinScheduleData().getErrorDetails().isEmpty()) {
				return getErrorMessage(financeDetail.getFinScheduleData());
			}

			// call doCreateFinance method after successful validations
			FinanceDetail financeDetailRes = null;
			financeDetailRes = createFinanceController.doCreateFinance(financeDetail, false);

			if (financeDetailRes != null) {
				if (financeDetailRes.getFinScheduleData() != null) {
					for (ErrorDetail errorDetails : financeDetailRes.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getCode(),
								errorDetails.getError()));
						return response;
					}
				}
			}
			// for logging purpose
			if (financeDetailRes != null) {
				APIErrorHandlerService.logReference(financeDetailRes.getFinReference());
			}
			logger.debug(Literal.LEAVING);
			return financeDetailRes;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
	}

	public FinanceDetail createOverDraftLoanValidation(FinanceDetail financeDetail) {
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		FinanceDetail response = new FinanceDetail();
		if (!financeType.isFinIsGenRef()) {
			FinanceDetail checkDuplicateRecord = createFinanceController
					.getFinanceDetails(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
			WSReturnStatus checkExistingRecordStatus = checkDuplicateRecord.getReturnStatus();
			if (!checkExistingRecordStatus.getReturnCode().equals("API006")) {
				doEmptyResponseObject(response);
				response.setStp(financeDetail.isStp());
				String[] valueParm = new String[1];
				valueParm[0] = "Finance Reference ";
				WSReturnStatus status = APIErrorHandlerService.getFailedStatus("PR002", valueParm);
				status.setReturnText(
						valueParm[0] + financeDetail.getFinScheduleData().getFinanceMain().getFinReference() + " "
								+ status.getReturnText());
				response.setReturnStatus(status);
				return response;
			}
		}

		if (financeDetail.getFinScheduleData().getFinanceMain().getFinAssetValue()
				.compareTo(new BigDecimal("0")) == 0) {
			doEmptyResponseObject(response);
			response.setStp(financeDetail.isStp());
			String[] valueParm = new String[1];
			valueParm[0] = "Finance Asset Value ";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90501", valueParm);
			status.setReturnText(status.getReturnText().replace("Invalid", "Required"));
			response.setReturnStatus(status);
			return response;
		}

		if (financeDetail.getFinScheduleData().getFinanceMain().getFinAssetValue()
				.compareTo(new BigDecimal("0")) == -1) {
			doEmptyResponseObject(response);
			response.setStp(financeDetail.isStp());
			String[] valueParm = new String[1];
			valueParm[0] = "Finance Asset Value ";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90259", valueParm);
			status.setReturnText(status.getReturnText().replace(" feeCode:", " "));
			response.setReturnStatus(status);
			return response;
		}
		if ((new BigDecimal("999999999999999999")
				.compareTo(financeDetail.getFinScheduleData().getFinanceMain().getFinAssetValue()) == -1)) {
			doEmptyResponseObject(response);
			response.setStp(financeDetail.isStp());
			String[] valueParm = new String[2];
			valueParm[0] = "Finance Asset Value is less than 18 digits or ";
			valueParm[1] = " 18";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90300", valueParm);
			status.setReturnText(status.getReturnText().replace("maximum", "Maximum").replaceAll("\n\t\t\t", " "));
			response.setReturnStatus(status);
			return response;
		}

		// schedule method PFT only allowed in Over Draft

		if (StringUtils.isBlank(financeDetail.getFinScheduleData().getFinanceMain().getScheduleMethod())) {
			doEmptyResponseObject(response);
			response.setStp(financeDetail.isStp());
			String[] valueParm = new String[1];
			valueParm[0] = "Schedule Method ";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90501", valueParm);
			status.setReturnText(status.getReturnText().replace("Invalid", "Required"));
			response.setReturnStatus(status);
			return response;
		}

		if (!financeDetail.getFinScheduleData().getFinanceMain().getScheduleMethod()
				.equals(CalculationConstants.SCHMTHD_POS_INT)) {
			doEmptyResponseObject(response);
			response.setStp(financeDetail.isStp());
			String[] valueParm = new String[2];
			valueParm[0] = "Schedule Method ";
			valueParm[1] = CalculationConstants.SCHMTHD_POS_INT;
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90337", valueParm);
			status.setReturnText(status.getReturnText());
			response.setReturnStatus(status);
			return response;
		}

		// disbursement is not required in Over Draft loan

		List<FinAdvancePayments> advancePaymentsList = financeDetail.getAdvancePaymentsList();

		if (!CollectionUtils.isEmpty(advancePaymentsList)) {
			doEmptyResponseObject(response);
			response.setStp(financeDetail.isStp());
			String[] valueParm = new String[2];
			valueParm[0] = "Disbursement ";
			valueParm[1] = " Overdraft Loan";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90204", valueParm);
			status.setReturnText(status.getReturnText());
			response.setReturnStatus(status);
			return response;
		}

		// Compare first drop line date greater than start date
		/*
		 * if(financeDetail.getFinScheduleData().getFinanceMain(). getFirstDroplineDate()==null){ String[] valueParm =
		 * new String[1]; valueParm[0] = "FirstDroplineDate"; WSReturnStatus status =
		 * APIErrorHandlerService.getFailedStatus("90502", valueParm); response.setReturnStatus(status); return
		 * response; }
		 */

		if (financeDetail.getFinScheduleData().getFinanceMain().getFirstDroplineDate() != null) {
			if (financeDetail.getFinScheduleData().getFinanceMain().getFirstDroplineDate()
					.compareTo(financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate()) <= 0) {
				doEmptyResponseObject(response);
				response.setStp(financeDetail.isStp());
				String[] valueParm = new String[2];
				valueParm[0] = "First Drop line Date";
				valueParm[1] = "Finance Start Date";
				WSReturnStatus status = APIErrorHandlerService.getFailedStatus("91121", valueParm);
				status.setReturnText(status.getReturnText());
				response.setReturnStatus(status);
				return response;
			}

		}
		if (financeDetail.getFinScheduleData().getFinanceMain().getFinAmount().compareTo(new BigDecimal("0")) != 0) {
			doEmptyResponseObject(response);
			response.setStp(financeDetail.isStp());
			String[] valueParm = new String[2];
			valueParm[0] = "Finance Amount ";
			valueParm[1] = "Zero";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90281", valueParm);
			String alterMessage = status.getReturnText();
			System.out.println("Text is  :" + status.getReturnText());
			status.setReturnText(alterMessage);
			response.setReturnStatus(status);
			return response;
		}
		financeDetail.getFinScheduleData().getFinanceMain().setRecalType("");
		if (StringUtils.isBlank(financeDetail.getFinScheduleData().getFinanceMain().getRepayBaseRate())) {
			financeDetail.getFinScheduleData().getFinanceMain().setRepayBaseRate(null);
		}

		return financeDetail;
	}

	public FinanceDetail getOverDraftMaintenance(OverDraftMaintenance overDraftMaintenance) throws ServiceException {

		APIErrorHandlerService.logReference(overDraftMaintenance.getFinReference());
		// service level validations
		WSReturnStatus returnStatus = validateFinReference(overDraftMaintenance.getFinReference());

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			FinanceDetail financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}

		FinanceDetail financeDetail = createFinanceController
				.getFinInquiryDetails(overDraftMaintenance.getFinReference());
		financeDetail.getFinScheduleData().setFinanceType(null);
		financeDetail.setCustomerDetails(null);
		financeDetail.setMandate(null);

		return financeDetail;
	}

	/**
	 * validate and create finance with WIF reference by receiving request object from interface.
	 * 
	 * @param financeDetail
	 */
	@Override
	public FinanceDetail createFinanceWithWIF(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		// do Basic mandatory validations using hibernate validator
		validationUtility.validate(financeDetail, CreateFinancewithWIFGroup.class);
		// for logging purpose
		String[] logFields = getLogFields(financeDetail);
		APIErrorHandlerService.logKeyFields(logFields);

		try {
			// call WIF finance related validations
			WSReturnStatus returnStatus = doValidations(financeDetail);
			if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(returnStatus);
				return response;
			}

			FinanceDetail financeDetailRes = null;
			String finReference = financeDetail.getFinReference();
			String procEdtEvent = FinanceConstants.FINSER_EVENT_ORG;

			FinanceDetail wifFinanceDetail = null;
			int countInWIF = financeMainDAO.getFinanceCountById(finReference, "", true);
			if (countInWIF > 0) {
				// fetch WIF finance details
				wifFinanceDetail = financeDetailService.getWIFFinance(finReference, true, procEdtEvent);
				if (wifFinanceDetail != null) {
					String custCIF = financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
					String finRepayMethod = financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod();
					Date finContractDate = financeDetail.getFinScheduleData().getFinanceMain().getFinContractDate();
					String finPurpose = financeDetail.getFinScheduleData().getFinanceMain().getFinPurpose();
					String finLimitRef = financeDetail.getFinScheduleData().getFinanceMain().getFinLimitRef();
					String finCommitmentRef = financeDetail.getFinScheduleData().getFinanceMain().getFinCommitmentRef();
					String repayAccountId = financeDetail.getFinScheduleData().getFinanceMain().getRepayAccountId();
					String depreciationFrq = financeDetail.getFinScheduleData().getFinanceMain().getDepreciationFrq();
					String dsaCode = financeDetail.getFinScheduleData().getFinanceMain().getDsaCode();
					String salesDepartment = financeDetail.getFinScheduleData().getFinanceMain().getSalesDepartment();
					String dmaCode = financeDetail.getFinScheduleData().getFinanceMain().getDmaCode();
					long accountsOfficer = financeDetail.getFinScheduleData().getFinanceMain().getAccountsOfficer();
					String referralId = financeDetail.getFinScheduleData().getFinanceMain().getReferralId();
					boolean quickDisb = financeDetail.getFinScheduleData().getFinanceMain().isQuickDisb();
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setLovDescCustCIF(custCIF);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setFinRepayMethod(finRepayMethod);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setFinContractDate(finContractDate);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setFinPurpose(finPurpose);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setFinLimitRef(finLimitRef);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setFinCommitmentRef(finCommitmentRef);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setRepayAccountId(repayAccountId);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setDepreciationFrq(depreciationFrq);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setDsaCode(dsaCode);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setSalesDepartment(salesDepartment);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setDmaCode(dmaCode);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setAccountsOfficer(accountsOfficer);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setReferralId(referralId);
					wifFinanceDetail.getFinScheduleData().getFinanceMain().setQuickDisb(quickDisb);
					financeDetail.setFinScheduleData(wifFinanceDetail.getFinScheduleData());
				}

				// check origination with same WIF Reference
				int countInOrg = financeMainDAO.getFinanceCountById(finReference, "", false);
				if (countInOrg > 0) {
					String[] valueParm = new String[1];
					valueParm[0] = finReference;
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("91122"));
					return response;
				}
			}
			if (financeDetail.getFinScheduleData().getFinanceMain() == null) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				String[] valueParm = new String[1];
				valueParm[0] = "financeDetail";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return response;
			}
			// validate and Data defaulting
			financeDetail = financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_LOAN, financeDetail);

			if (!financeDetail.getFinScheduleData().getErrorDetails().isEmpty()) {
				return getErrorMessage(financeDetail.getFinScheduleData());
			}
			// validate FinanceDetail Validations
			// validate finance data
			if (StringUtils.isNotBlank(financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
				CustomerDetails customerDetails = new CustomerDetails();
				customerDetails.setCustomer(null);
				financeDetail.setCustomerDetails(customerDetails);
			}
			financeDataValidation.financeDetailValidation(PennantConstants.VLD_CRT_LOAN, financeDetail, true);

			if (!financeDetail.getFinScheduleData().getErrorDetails().isEmpty()) {
				return getErrorMessage(financeDetail.getFinScheduleData());
			}

			// call doCreate method to create finance with WIF Reference
			financeDetailRes = createFinanceController.doCreateFinance(financeDetail, true);

			if (financeDetailRes != null) {
				if (financeDetailRes.getFinScheduleData() != null) {
					for (ErrorDetail errorDetails : financeDetailRes.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetails.getCode(),
								errorDetails.getError()));
						return response;
					}
				}
			}
			if (financeDetailRes != null) {
				APIErrorHandlerService.logReference(financeDetailRes.getFinReference());
			}
			logger.debug(Literal.LEAVING);
			return financeDetailRes;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
	}

	/**
	 * Validate the mandatory fields in the request object
	 * 
	 * @param financeDetail
	 * @return
	 */
	private WSReturnStatus doValidations(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String custCIF = financeMain.getLovDescCustCIF();

		// validate Customer
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			returnStatus = APIErrorHandlerService.getFailedStatus("90101", valueParm);
		}

		String finReference = financeDetail.getFinReference();
		int rcdCountInWIF = financeMainDAO.getFinanceCountById(finReference, "", true);

		if (rcdCountInWIF <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		if (StringUtils.isBlank(financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod())) {
			String[] valueParm = new String[1];
			valueParm[0] = "finRepayMethod";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		logger.debug(Literal.LEAVING);

		return returnStatus;
	}

	/**
	 * get the Finance Details by the given finReference.
	 * 
	 * @param finReference
	 * @return FinanceDetail
	 * @throws ServiceException
	 */
	@Override
	public FinanceDetail getFinInquiryDetails(String finReference) throws ServiceException {
		logger.debug(Literal.ENTERING);
		// for logging purpose
		APIErrorHandlerService.logReference(finReference);
		// service level validations
		WSReturnStatus returnStatus = validateFinReference(finReference);

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			FinanceDetail financeDetail = new FinanceDetail();
			doEmptyResponseObject(financeDetail);
			financeDetail.setReturnStatus(returnStatus);
			return financeDetail;
		}
		FinanceDetail financeDetail = createFinanceController.getFinInquiryDetails(finReference);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * get the Finance Details by the given CustCif.
	 * 
	 * @param custCif
	 * @return FinanceResponse
	 * @throws ServiceException
	 */
	@Override
	public FinanceInquiry getFinanceWithCustomer(String custCif) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// Mandatory validation
		if (StringUtils.isBlank(custCif)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(custCif);
		FinanceInquiry response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(custCif);
		if (customer == null) {
			response = new FinanceInquiry();
			String[] valueParm = new String[1];
			valueParm[0] = custCif;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
		} else {
			response = createFinanceController.getFinanceDetailsById(custCif, APIConstants.FINANCE_INQUIRY_CUSTOMER,
					false);
		}
		logger.debug(Literal.LEAVING);

		return response;
	}

	/**
	 * get the Finance Details by the given collateralRef.
	 * 
	 * @param collateralRef
	 * @return FinanceResponse
	 * @throws ServiceException
	 */
	@Override
	public FinanceInquiry getFinanceWithCollateral(String collateralRef) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(collateralRef)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(collateralRef);

		FinanceInquiry response = null;
		int count = collateralSetupService.getCountByCollateralRef(collateralRef);
		if (count <= 0) {
			response = new FinanceInquiry();
			String[] valueParm = new String[1];
			valueParm[0] = collateralRef;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90906", valueParm));
		} else {
			response = createFinanceController.getFinanceDetailsById(collateralRef, "Collateral", false);
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public FinanceDetail getFinanceDetails(String finReference) {
		logger.debug(Literal.ENTERING);
		// for logging purpose
		if (StringUtils.isNotBlank(finReference)) {
			APIErrorHandlerService.logReference(finReference);
		}
		FinanceDetail financeDetail = createFinanceController.getFinanceDetails(finReference);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for update finance details(Disbursement, Mandate and Extended fields)
	 * 
	 * @param financeDetail
	 * @return WSReturnStatus
	 */
	@Override
	public WSReturnStatus updateFinance(FinanceDetail financeDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (financeDetail != null) {
			FinanceMain finMain = financeMainDAO.getFinanceDetailsForService(financeDetail.getFinReference(), "_Temp",
					false);
			if (finMain == null) {
				String valueParam[] = new String[1];
				valueParam[0] = financeDetail.getFinReference();
				return APIErrorHandlerService.getFailedStatus("90201", valueParam);
			}

		}

		// set default values
		financeDataDefaulting.doFinanceDetailDefaulting(financeDetail);
		// for logging purpose
		String[] logFields = getLogFields(financeDetail);
		APIErrorHandlerService.logKeyFields(logFields);
		APIErrorHandlerService.logReference(financeDetail.getFinReference());

		// validate FinanceDetail Validations
		FinScheduleData finSchData = financeDataValidation.financeDetailValidation(PennantConstants.VLD_UPD_LOAN,
				financeDetail, true);
		if (!finSchData.getErrorDetails().isEmpty()) {
			FinanceDetail finDetail = getErrorMessage(finSchData);
			return finDetail.getReturnStatus();
		}

		WSReturnStatus response = createFinanceController.updateFinance(financeDetail);
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * Method for approve temp_finance details
	 * 
	 * @param financeDetail
	 * @return WSReturnStatus
	 */

	@Override
	public WSReturnStatus approveLoan(FinanceDetail financeDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);
		FinanceDetail finDetail = null;
		WSReturnStatus returnStatus = null;
		String finReference = financeDetail.getFinReference();

		// for logging purpose
		if (StringUtils.isNotBlank(finReference)) {
			APIErrorHandlerService.logReference(finReference);
		}
		// check reference is in temp table or not
		FinanceMain finMain = financeMainDAO.getFinanceDetailsForService(finReference, "_Temp", false);

		if (finMain == null) {

			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);

		}

		finDetail = createFinanceController.getFinanceDetails(finReference);

		if (finDetail != null) {
			returnStatus = createFinanceController.doApproveLoan(finDetail);
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	private FinanceDetail getErrorMessage(FinScheduleData financeSchdData) {
		for (ErrorDetail erroDetail : financeSchdData.getErrorDetails()) {
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(
					APIErrorHandlerService.getFailedStatus(erroDetail.getCode(), erroDetail.getError()));
			return response;
		}
		return new FinanceDetail();
	}

	/**
	 * Nullify the un-necessary objects to prepare response in a structured format specified in API.
	 * 
	 * @param response
	 */
	private void doEmptyResponseObject(FinanceDetail response) {
		response.setFinScheduleData(null);
		response.setAdvancePaymentsList(null);
		response.setMandate(null);
		response.setJountAccountDetailList(null);
		response.setGurantorsDetailList(null);
		response.setDocumentDetailsList(null);
		response.setCovenantTypeList(null);
		response.setCollateralAssignmentList(null);
		response.setFinFlagsDetails(null);
		response.setCustomerDetails(null);
	}

	/**
	 * Method for validate finance reference and check existence in origination
	 * 
	 * @param finReference
	 * @return
	 */
	private WSReturnStatus validateFinReference(String finReference) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();

		// check records in origination
		FinanceMain finMain = financeMainDAO.getFinanceMainParms(finReference);
		if (finMain == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}
		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	/**
	 * Method to reject loan based on data provided by customer
	 * 
	 * @param financeDetail
	 *            {@link FinanceDetail}
	 * @return {@link WSReturnStatus}
	 */
	@Override
	public WSReturnStatus rejectFinance(FinanceDetail financeDetail) throws ServiceException {

		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = null;
		try {
			if (StringUtils.isEmpty(financeDetail.getFinScheduleData().getFinReference())) {
				List<ErrorDetail> validationErrors = createFinanceController.rejectFinanceValidations(financeDetail);
				if (CollectionUtils.isEmpty(validationErrors)) {
					financeDetail = financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_LOAN, financeDetail);
					List<ErrorDetail> financeDetailErrors = null;
					if (!CollectionUtils.isEmpty(financeDetail.getFinScheduleData().getErrorDetails())) {
						financeDetailErrors = financeDetail.getFinScheduleData().getErrorDetails();
						for (ErrorDetail errorDetail : financeDetailErrors) {
							doEmptyResponseObject(financeDetail);
							returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
									errorDetail.getError());
						}
					} else {
						returnStatus = createFinanceController.processRejectFinance(financeDetail, false);
					}
				} else {
					for (ErrorDetail errorDetail : validationErrors) {
						returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getParameters());
					}
				}
			} else {
				FinanceMain finMain = new FinanceMain();
				financeDetail.getFinScheduleData().setFinanceMain(finMain);
				returnStatus = createFinanceController.processRejectFinance(financeDetail, true);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);

		return returnStatus;
	}

	/**
	 * Method to cancel loan based on data provided by customer
	 * 
	 * @param financeDetail
	 *            {@link FinanceDetail}
	 * @return {@link WSReturnStatus}
	 */
	@Override
	public FinanceDetail cancelFinance(FinanceDetail financeDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// for logging purpose
		APIErrorHandlerService.logReference(financeDetail.getFinReference());
		FinanceDetail response = null;
		if (StringUtils.isNotBlank(financeDetail.getFinScheduleData().getFinReference())
				&& StringUtils.isNotBlank(financeDetail.getFinScheduleData().getExternalReference())) {
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			String[] valueParm = new String[2];
			valueParm[0] = "finReference";
			valueParm[1] = "External Reference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30511", valueParm));
			return response;

		}
		if (StringUtils.isBlank(financeDetail.getFinScheduleData().getFinReference())
				&& StringUtils.isBlank(financeDetail.getFinScheduleData().getExternalReference())) {
			response = new FinanceDetail();
			doEmptyResponseObject(response);
			String[] valueParm = new String[2];
			valueParm[0] = "finReference";
			valueParm[1] = "External Reference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90123", valueParm));
			return response;
		}
		// service level validations
		if (StringUtils.isNotBlank(financeDetail.getFinScheduleData().getFinReference())) {
			int count = financeMainDAO.getCountByFinReference(financeDetail.getFinScheduleData().getFinReference(),
					true);
			if (count <= 0) {
				response = new FinanceDetail();
				doEmptyResponseObject(response);
				String[] valueParm = new String[1];
				valueParm[0] = financeDetail.getFinReference();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return response;
			} else {
				financeDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
			}
		} else {
			WSReturnStatus returnStatus = validateOldFinReference(financeDetail, true);
			if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
				response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(returnStatus);
				return response;
			}
		}
		response = createFinanceController.processCancelFinance(financeDetail);
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public FinanceDetail reInitiateFinance(FinanceDetail financeDetail) throws ServiceException {

		logger.debug(Literal.ENTERING);

		// for logging purpose
		String finReference = financeDetail.getFinScheduleData().getFinReference();
		APIErrorHandlerService.logReference(finReference);
		FinanceDetail findetail = null;
		WSReturnStatus returnStatus = new WSReturnStatus();
		try {
			if (StringUtils.isNotBlank(finReference)
					&& StringUtils.isNotBlank(financeDetail.getFinScheduleData().getExternalReference())) {
				findetail = new FinanceDetail();
				String[] valueParm = new String[2];
				valueParm[0] = "finReference";
				valueParm[1] = "ExternalReference";
				returnStatus = APIErrorHandlerService.getFailedStatus("30511", valueParm);
				findetail.setReturnStatus(returnStatus);
				return findetail;

			}
			financeDetail.setFinReference(finReference);
			// service level validations
			if (StringUtils.isNotBlank(finReference)) {
				int count = financeMainDAO.getCountByFinReference(finReference, false);
				if (count <= 0) {
					findetail = new FinanceDetail();
					String[] valueParm = new String[1];
					valueParm[0] = financeDetail.getFinReference();
					returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
					findetail.setReturnStatus(returnStatus);
					return findetail;

				}
			} else {
				returnStatus = validateOldFinReference(financeDetail, false);
				if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
					findetail = new FinanceDetail();
					findetail.setReturnStatus(returnStatus);
					return findetail;
				}
			}
			if (StringUtils.isNotBlank(financeDetail.getFinScheduleData().getOldFinReference())) {
				int count = financeMainDAO
						.getCountByOldFinReference(financeDetail.getFinScheduleData().getOldFinReference());
				if (count > 0) {
					findetail = new FinanceDetail();
					String[] valueParm = new String[2];
					valueParm[0] = "Host Reference";
					valueParm[1] = financeDetail.getFinScheduleData().getOldFinReference();
					returnStatus = APIErrorHandlerService.getFailedStatus("30506", valueParm);
					findetail.setReturnStatus(returnStatus);
					return findetail;
				}
			}
			if (StringUtils.isNotBlank(financeDetail.getFinScheduleData().getExternalReference())) {
				int count = financeMainDAO
						.getCountByExternalReference(financeDetail.getFinScheduleData().getExternalReference());
				if (count > 0) {
					findetail = new FinanceDetail();
					String[] valueParm = new String[2];
					valueParm[0] = "ReInitiate";
					returnStatus = APIErrorHandlerService.getFailedStatus("90273", valueParm);
					findetail.setReturnStatus(returnStatus);
					return findetail;
				}
			}
			financeDetail.setFinReference(finReference);
			findetail = createFinanceController.doReInitiateFinance(financeDetail);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		logger.debug(Literal.LEAVING);
		return findetail;

	}

	@Override
	public WSReturnStatus moveLoanStage(MoveLoanStageRequest moveLoanStageRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);
		// for logging purpose
		APIErrorHandlerService.logReference(moveLoanStageRequest.getFinReference());
		WSReturnStatus returnStatus = new WSReturnStatus();
		if (StringUtils.isBlank(moveLoanStageRequest.getFinReference())) {

			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		if (StringUtils.isBlank(moveLoanStageRequest.getCurrentStage())) {

			String[] valueParm = new String[1];
			valueParm[0] = "CurrentStage";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		if (StringUtils.isBlank(moveLoanStageRequest.getAction())) {

			String[] valueParm = new String[1];
			valueParm[0] = "Action";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		FinanceMain finMain = financeMainDAO.getFinanceMainById(moveLoanStageRequest.getFinReference(), "_Temp", false);
		if (finMain == null) {
			String[] valueParm = new String[1];
			valueParm[0] = moveLoanStageRequest.getFinReference();
			return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		} else {
			if (!StringUtils.equals(moveLoanStageRequest.getCurrentStage(), finMain.getNextRoleCode())) {
				String[] valueParm = new String[2];
				valueParm[0] = "CurrentStage: " + moveLoanStageRequest.getCurrentStage();
				valueParm[1] = finMain.getNextRoleCode();
				return returnStatus = APIErrorHandlerService.getFailedStatus("90337", valueParm);
			}
		}
		Map<String, String> userActions = createFinanceController.getUserActions(finMain);
		if (userActions != null) {
			if (!userActions.containsKey(moveLoanStageRequest.getAction())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Action: " + moveLoanStageRequest.getAction();
				valueParm[1] = userActions.keySet().toString();
				return returnStatus = APIErrorHandlerService.getFailedStatus("90337", valueParm);
			} else {
				FinanceDetail finDetail = createFinanceController
						.getFinanceDetails(moveLoanStageRequest.getFinReference());
				FinanceMain financeMain = finDetail.getFinScheduleData().getFinanceMain();

				FinanceMain beffinanceMain = new FinanceMain();
				BeanUtils.copyProperties(financeMain, beffinanceMain);
				financeMain.setBefImage(beffinanceMain);

				financeMain.setRecordStatus(userActions.get(moveLoanStageRequest.getAction()));
				// financeMain.setVersion(financeMain.getVersion()+1);
				// financeMain.setNewRecord(false);
				WSReturnStatus response = createFinanceController.doMoveLoanStage(finDetail, moveLoanStageRequest);
				logger.debug(Literal.LEAVING);
				return response;

			}
		} else {

		}
		return returnStatus;

	}

	private WSReturnStatus validateOldFinReference(FinanceDetail financeDetail, boolean active) {
		WSReturnStatus returnStatus = new WSReturnStatus();

		// check records in origination
		FinanceMain finMain = financeMainDAO
				.getFinanceMainByHostReference(financeDetail.getFinScheduleData().getExternalReference(), active);
		if (finMain == null) {
			String[] valueParm = new String[1];
			valueParm[0] = financeDetail.getFinScheduleData().getExternalReference();
			return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		} else {
			financeDetail.setFinReference(finMain.getFinReference());
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	/**
	 * Method to prepare the log fields.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private String[] getLogFields(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		String[] logfields = new String[3];
		FinScheduleData finscheduleData = financeDetail.getFinScheduleData();
		if (finscheduleData != null) {
			FinanceMain finMain = finscheduleData.getFinanceMain();
			if (finMain != null) {
				logfields[0] = finMain.getCustCIF();
				logfields[1] = finMain.getFinType();
				logfields[2] = String.valueOf(finMain.getFinAmount());
			}
		}
		logger.debug(Literal.LEAVING);
		return logfields;
	}

	@Override
	public FinanceInquiry getPendingFinanceWithCustomer(String custCif) throws ServiceException {

		logger.debug(Literal.ENTERING);

		// Mandatory validation
		if (StringUtils.isBlank(custCif)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(custCif);
		FinanceInquiry response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(custCif);
		if (customer == null) {
			response = new FinanceInquiry();
			String[] valueParm = new String[1];
			valueParm[0] = custCif;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
		} else {
			response = createFinanceController.getFinanceDetailsById(custCif, APIConstants.FINANCE_INQUIRY_CUSTOMER,
					true);
		}
		logger.debug(Literal.LEAVING);

		return response;

	}

	@Override
	public List<LoanStatus> getLoansStatus(LoanStatusDetails loanStatusDetails) throws ServiceException {
		logger.debug(Literal.ENTERING);
		// for logging purpose
		List<LoanStatus> listResponse = new ArrayList<LoanStatus>();
		for (LoanStatus loanStatus : loanStatusDetails.getLoanSatusDetails()) {
			APIErrorHandlerService.logReference(loanStatus.getFinReference());
			LoanStatus response = null;
			if (StringUtils.isBlank(loanStatus.getFinReference())) {
				response = new LoanStatus();
				// /doEmptyResponseObject(response);
				String[] valueParm = new String[1];
				valueParm[0] = "finreference";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				listResponse.add(response);
				return listResponse;

			}
			FinanceMain finMain = financeMainDAO.getFinanceMainStutusById(loanStatus.getFinReference(), "_View");
			if (finMain == null) {
				response = new LoanStatus();
				// /doEmptyResponseObject(response);
				String[] valueParm = new String[1];
				valueParm[0] = loanStatus.getFinReference();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90501", valueParm));
				listResponse.add(response);
				return listResponse;
			} else {
				response = new LoanStatus();
				response.setRecordStatus(finMain.getRecordStatus());
				response.setRoleCode(finMain.getRoleCode());
				response.setNextRoleCode(finMain.getNextRoleCode());
				response.setFinReference(finMain.getFinReference());
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				listResponse.add(response);

			}
		}
		logger.debug(Literal.LEAVING);
		return listResponse;
	}

	@Override
	public AgreementDetails getAgreements(AgreementRequest aggReq) throws ServiceException {
		logger.debug(Literal.ENTERING);

		AgreementDetails details = new AgreementDetails();
		FinanceMain financeMain = null;
		AgreementRequest agreementReq = null;

		// Mandatory validation
		if (StringUtils.isBlank(aggReq.getFinReference())) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			details.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return details;
		}

		try {
			financeMain = financeMainDAO.getFinanceMainById(aggReq.getFinReference(), "_View", false);
			if (financeMain == null) {
				String[] valueParm = new String[1];
				valueParm[0] = aggReq.getFinReference();
				details.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return details;
			}

			// for logging purpose
			APIErrorHandlerService.logReference(aggReq.getFinReference());

			// validate Customer with given CustCIF
			WSReturnStatus returnStatus = validateFinReference(aggReq.getFinReference());

			if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
				details.setReturnStatus(returnStatus);
				return details;
			}

			FinanceDetail finDetail = createFinanceController.getFinanceDetails(aggReq.getFinReference());

			if (StringUtils.isNotBlank(aggReq.getAgreementType())) {
				List<AgreementData> aggList = createFinanceController.getAgreements(finDetail, aggReq);
				details.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				details.setAgreementsList(aggList);
				return details;
			}

			List<AgreementData> agreements = new ArrayList<>();
			List<FinanceReferenceDetail> agreemantsList = financeReferenceDetailDAO
					.getAgreemantsListByFinType(financeMain.getFinType());

			for (FinanceReferenceDetail financeDetail : agreemantsList) {
				agreementReq = new AgreementRequest();
				agreementReq.setAgreementType(financeDetail.getLovDescCodelov());
				List<AgreementData> agglist = createFinanceController.getAgreements(finDetail, agreementReq);
				agreements.addAll(agglist);
			}

			if (CollectionUtils.isEmpty(agreements)) {
				String[] valueParm = new String[1];
				valueParm[0] = "No Agreements found with " + aggReq.getFinReference();
				details.setReturnStatus(APIErrorHandlerService.getFailedStatus("21005", valueParm));
				return details;
			} else {
				details.setAgreementsList(agreements);
			}

		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			details.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug(Literal.LEAVING);

		return details;

	}

	/**
	 * Method to get userActions based on finReference
	 * 
	 * @param finReference
	 * 
	 */
	@Override
	public UserActions getUserActions(String finReference) throws ServiceException {
		logger.debug(Literal.ENTERING);

		UserActions response = new UserActions();
		List<ValueLabel> actionsList = new ArrayList<>();
		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		} else {
			FinanceMain finMain = financeMainDAO.getFinanceMainById(finReference, "_Temp", false);
			if (finMain == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return response;
			} else {

				Map<String, String> userActions = createFinanceController.getUserActions(finMain);
				if (userActions != null) {
					for (Map.Entry<String, String> entry : userActions.entrySet()) {
						ValueLabel valueLabel = new ValueLabel();
						valueLabel.setLabel(entry.getKey());
						valueLabel.setValue(entry.getValue());
						actionsList.add(valueLabel);
					}
					response.setValueLabel(actionsList);
					response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				} else {
					String[] valueParm = new String[1];
					valueParm[0] = finReference;
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90355", valueParm));
					return response;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public ActivityLogDetails getActivityLogs(String finReference) throws ServiceException {
		logger.debug(Literal.ENTERING);

		ActivityLogDetails response = new ActivityLogDetails();

		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		} else {
			int count = financeMainDAO.getFinanceCountById(finReference, "_View", false);
			if (count <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return response;
			}
		}

		List<Activity> activityLogList = activityLogService.getActivities(PennantConstants.NOTES_MODULE_FINANCEMAIN,
				finReference);

		if (CollectionUtils.isNotEmpty(activityLogList)) {

			response.setActivityLogList(activityLogList);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			return response;
		} else {
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;

	}

	public UserPendingCasesResponse getLoansByStage(LoanStage loanStage) throws ServiceException {
		logger.debug(Literal.ENTERING);

		UserPendingCasesResponse response = new UserPendingCasesResponse();
		List<UserPendingCases> responseList = new ArrayList<>();
		List<String> list = new ArrayList<>();
		long userID = 0;

		if (StringUtils.isBlank(loanStage.getUserLogin())) {
			String[] valueParm = new String[1];
			valueParm[0] = "userLogin";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		} else {
			userID = securityUserDAO.getUserByName(loanStage.getUserLogin());
			if (userID <= 0) {
				String[] param = new String[2];
				param[0] = "User Name";
				param[1] = String.valueOf(loanStage.getUserLogin());
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90224", param));
				return response;
			}
		}
		if (StringUtils.isBlank(loanStage.getRoleCode())) {
			String[] valueParm = new String[1];
			valueParm[0] = "roleCode";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		} else {
			if (StringUtils.endsWith(loanStage.getRoleCode().trim(), ",")) {
				String[] valueParm = new String[1];
				valueParm[0] = "rolecode";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
				return response;
			}
		}
		String roleCode = loanStage.getRoleCode();
		if (roleCode.contains(",")) {
			String[] arrRoleCode = roleCode.split(",");
			list = Arrays.asList(arrRoleCode);
			if (CollectionUtils.isEmpty(list)) {
				String[] valueParm = new String[1];
				valueParm[0] = "rolecode :" + loanStage.getRoleCode();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
				return response;
			} else {
				List<UserPendingCases> userPendingList = new ArrayList<>();
				for (String role : list) {
					userPendingList = financeMainDAO.getUserPendingCasesDetails(userID, role);
					responseList.addAll(userPendingList);
				}
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				response.setUserPendingList(responseList);
			}
		} else {
			responseList = financeMainDAO.getUserPendingCasesDetails(userID, roleCode);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			response.setUserPendingList(responseList);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public DeviationList getDeviations(String finReference) throws ServiceException {
		logger.debug(Literal.ENTERING);

		DeviationList response = new DeviationList();

		// Mandatory validation
		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "_View", false);
		if (financeMain == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}

		List<FinanceDeviations> deviations = financeDeviationsDAO.getFinanceDeviations(finReference, "_View");

		if (CollectionUtils.isEmpty(deviations)) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			return response;
		} else {

			response.setDevitionList(deviations);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public FinanceDedupResponse loanDedup(FinanceDedupDetails financeDedupDetails) throws ServiceException {

		FinanceDedupResponse response = new FinanceDedupResponse();
		FinanceDedup dedup = new FinanceDedup();

		List<FinanceDedupRequest> dedupList = financeDedupDetails.getDedupList();

		if (CollectionUtils.isEmpty(dedupList)) {
			String[] valueParm = new String[2];
			valueParm[0] = "Request";
			valueParm[1] = " two fields";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30507", valueParm));
			return response;
		} else {
			if (dedupList.size() < 2) {
				String[] valueParm = new String[2];
				valueParm[0] = "Request";
				valueParm[1] = " two fields";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30507", valueParm));
				return response;
			}
		}

		List<BuilderTable> fieldList = dedupFieldsDAO.getFieldList(FinanceConstants.MODULE_NAME);
		List<String> fieldNamesList = fieldList.stream().map(d -> d.getFieldName()).collect(Collectors.toList());

		for (FinanceDedupRequest feild : dedupList) {
			// mandatory validation
			if (StringUtils.isBlank(feild.getName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "name";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return response;
			}
			if (StringUtils.isBlank(String.valueOf(feild.getValue()))) {
				String[] valueParm = new String[1];
				valueParm[0] = "value";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return response;
			}

			boolean fieldFound = false;
			for (String dbField : fieldNamesList) {
				if (StringUtils.equalsIgnoreCase(dbField, feild.getName())) {
					fieldFound = true;
					if (feild.getName().equalsIgnoreCase("CustCIF")) {
						dedup.setCustCIF(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("FinanceType")) {
						dedup.setFinanceType(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustCRCPR")) {
						dedup.setCustCRCPR(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("TradeLicenceNo")) {
						dedup.setTradeLicenceNo(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("ChassisNumber")) {
						dedup.setChassisNumber(String.valueOf(feild.getValue()));
					}
				}
			}
			if (!fieldFound) {
				String[] valueParm = new String[1];
				valueParm[0] = "field name";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("41002", valueParm));
				return response;
			}

		}

		List<FinanceDedup> resDedupList = new ArrayList<FinanceDedup>();
		List<DedupParm> dedupParmList = dedupParmDAO.getDedupParmByModule(FinanceConstants.DEDUP_FINANCE, "L", "");

		// TO Check duplicate customer in Local database
		for (DedupParm dedupParm : dedupParmList) {
			List<FinanceDedup> list = financeDedupeDAO.fetchFinanceDedup(dedup, dedupParm.getSQLQuery());
			if (list != null && !list.isEmpty()) {
				resDedupList.addAll(list);
			}
		}

		if (CollectionUtils.isNotEmpty(resDedupList)) {
			response.setFinanceDedup(resDedupList);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		} else {
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	public WSReturnStatus getErrorDetails(String errorCode, String[] valueParm) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		response = APIErrorHandlerService.getFailedStatus(errorCode, valueParm);

		// set default error code and description in case of Error code does not
		// exists.
		if (StringUtils.isBlank(response.getReturnCode())) {
			response = APIErrorHandlerService.getFailedStatus(APIConstants.RES_FAILED_CODE,
					APIConstants.RES_FAILED_DESC);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public DeviationList getLoanDeviations(FinanceDeviations financeDeviations) throws ServiceException {

		logger.debug(Literal.ENTERING);

		DeviationList response = new DeviationList();
		List<FinanceDeviations> deviations = new ArrayList<>();
		// Mandatory validation
		if (StringUtils.isBlank(financeDeviations.getFinReference())) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		String status = financeDeviations.getApprovalStatus();
		if (StringUtils.isNotBlank(status) && !StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_PENDING)
				&& !StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_APPROVED)
				&& !StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_REJECT)) {
			String[] valueParm = new String[2];
			valueParm[0] = "approvalStatus";
			valueParm[1] = DeviationConstants.DEVIATION_STATUS_PENDING + ", "
					+ DeviationConstants.DEVIATION_STATUS_APPROVED + ", " + DeviationConstants.DEVIATION_STATUS_REJECT;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90281", valueParm));
			return response;
		}
		FinanceMain financeMain = financeMainDAO.getFinanceMainById(financeDeviations.getFinReference(), "_View",
				false);
		if (financeMain == null) {
			String[] valueParm = new String[1];
			valueParm[0] = financeDeviations.getFinReference();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}

		if (StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_APPROVED)
				|| StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_REJECT)) {
			deviations = financeDeviationsDAO.getFinanceDeviationsByStatus(financeDeviations.getFinReference(), status,
					"");
		} else if (StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_PENDING)) {
			deviations = financeDeviationsDAO.getFinanceDeviations(financeDeviations.getFinReference(), "_Temp");
		} else {
			deviations = financeDeviationsDAO.getFinanceDeviations(financeDeviations.getFinReference(), "_View");
		}

		if (CollectionUtils.isEmpty(deviations)) {
			String[] valueParm = new String[1];
			valueParm[0] = financeDeviations.getFinReference();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			return response;
		} else {
			response.setDevitionList(deviations);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus updateLoanDeviation(FinanceDeviations financeDeviations) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		response = validateUpdateDeviationRequest(financeDeviations);

		if (response.getReturnCode() != null) {
			return response;
		}

		response = createFinanceController.updateDeviationStatus(financeDeviations);

		logger.debug(Literal.LEAVING);
		return response;
	}

	public WSReturnStatus validateUpdateDeviationRequest(FinanceDeviations financeDeviations) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		if (StringUtils.isBlank(financeDeviations.getFinReference())) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			response = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return response;
		}
		FinanceMain financeMain = financeMainDAO.getFinanceMainById(financeDeviations.getFinReference(), "_View",
				false);
		if (financeMain == null) {
			String[] valueParm = new String[1];
			valueParm[0] = financeDeviations.getFinReference();
			response = APIErrorHandlerService.getFailedStatus("90201", valueParm);
			return response;
		}

		if (financeDeviations.getDeviationId() == Long.MIN_VALUE) {
			String[] valueParm = new String[1];
			valueParm[0] = "deviationId";
			response = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return response;
		}
		String status = financeDeviations.getApprovalStatus();
		if (StringUtils.isBlank(status) && StringUtils.isBlank(financeDeviations.getDelegationRole())) {
			String[] valueParm = new String[2];
			valueParm[0] = "approvalStatus";
			valueParm[1] = "delegationRole";
			response = APIErrorHandlerService.getFailedStatus("90123", valueParm);
			return response;

		}
		if (StringUtils.isNotBlank(status) && !StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_PENDING)
				&& !StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_APPROVED)
				&& !StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_REJECT)) {
			String[] valueParm = new String[2];
			valueParm[0] = "approvalStatus";
			valueParm[1] = DeviationConstants.DEVIATION_STATUS_PENDING + ", "
					+ DeviationConstants.DEVIATION_STATUS_APPROVED + ", " + DeviationConstants.DEVIATION_STATUS_REJECT;
			response = APIErrorHandlerService.getFailedStatus("90281", valueParm);
			return response;
		}
		if (StringUtils.isNotBlank(financeDeviations.getDelegationRole())) {
			StringBuilder roles = new StringBuilder();
			boolean roleFound = false;
			List<ValueLabel> delegators = deviationHelper.getRoleAndDesc(financeMain.getWorkflowId());
			if (CollectionUtils.isNotEmpty(delegators)) {
				for (ValueLabel details : delegators) {
					roles.append(details.getValue() + ",");
				}
				for (ValueLabel valueLabel : delegators) {
					if (StringUtils.equals(financeDeviations.getDelegationRole(), valueLabel.getValue())) {
						roleFound = true;
						break;
					}
				}
			} else {
				String[] valueParm = new String[2];
				valueParm[0] = "delegationRole not found";
				response = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				return response;
			}
			if (!roleFound) {
				String[] valueParm = new String[2];
				valueParm[0] = "delegationRole";
				valueParm[1] = roles.toString();
				response = APIErrorHandlerService.getFailedStatus("90281", valueParm);
				return response;
			}
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus updatePerfiosStatus(PerfiosTransaction perfiosTransaction) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();
		String response = customerDetailsService.processPerfiosReport(perfiosTransaction);
		returnStatus.setReturnText(response);

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	@Override
	public FinCustomerDetails getDetailsByOfferID(String offerID) throws ServiceException {
		logger.debug(Literal.ENTERING);
		String offerId = null;
		try {
			JSONObject jsonObject = new JSONObject(offerID);
			offerId = jsonObject.getString("offerID");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + "OfferID is not passed in the request");
		}
		FinCustomerDetails response = new FinCustomerDetails();
		if (StringUtils.isBlank(offerId)) {
			response.setCif(null);
			String[] valueParm = new String[1];
			valueParm[0] = "offerID";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		response = financeDetailService.getDetailsByOfferID(offerId);
		if (response != null && response.getFinReference() == null) {
			//if no data found for offerID
			response.setCif(null);
			String[] valueParm = new String[2];
			valueParm[0] = "data is";
			valueParm[1] = offerId;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90295", valueParm));
			return response;
		} else {
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Autowired
	public void setCreateFinanceController(CreateFinanceController createFinanceController) {
		this.createFinanceController = createFinanceController;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setFinanceDataDefaulting(FinanceDataDefaulting financeDataDefaulting) {
		this.financeDataDefaulting = financeDataDefaulting;
	}

	@Autowired
	public void setFinanceDataValidation(FinanceDataValidation financeDataValidation) {
		this.financeDataValidation = financeDataValidation;
	}

	@Autowired
	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	@Autowired
	public void setActivityLogService(ActivityLogService activityLogService) {
		this.activityLogService = activityLogService;
	}

	@Autowired
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Autowired
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	@Autowired
	public void setFinanceDeviationsDAO(FinanceDeviationsDAOImpl financeDeviationsDAO) {
		this.financeDeviationsDAO = financeDeviationsDAO;
	}

	@Autowired
	public void setDeviationHelper(DeviationHelper deviationHelper) {
		this.deviationHelper = deviationHelper;
	}

	@Autowired
	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	@Autowired
	public void setPerfiosTransactionDAO(PerfiosTransactionDAO perfiosTransactionDAO) {
		this.perfiosTransactionDAO = perfiosTransactionDAO;
	}

	@Autowired
	public void setDedupFieldsDAO(DedupFieldsDAO dedupFieldsDAO) {
		this.dedupFieldsDAO = dedupFieldsDAO;
	}

	public DedupParmDAO getDedupParmDAO() {
		return dedupParmDAO;
	}

	@Autowired
	public void setDedupParmDAO(DedupParmDAO dedupParmDAO) {
		this.dedupParmDAO = dedupParmDAO;
	}

	public FinanceDedupeDAO getFinanceDedupeDAO() {
		return financeDedupeDAO;
	}

	@Autowired
	public void setFinanceDedupeDAO(FinanceDedupeDAO financeDedupeDAO) {
		this.financeDedupeDAO = financeDedupeDAO;
	}

}
