package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.OverDraftMaintenance;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.impl.FinanceDataDefaulting;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.validation.CreateFinanceGroup;
import com.pennant.validation.CreateFinancewithWIFGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.CreateFinanceController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.CreateFinanceRestService;
import com.pennanttech.pffws.CreateFinanceSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.financetype.FinanceInquiry;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class CreateFinanceWebServiceImpl implements CreateFinanceSoapService, CreateFinanceRestService {

	private static final Logger logger = Logger.getLogger(CreateFinanceWebServiceImpl.class);

	private CreateFinanceController createFinanceController;
	private CustomerDetailsService customerDetailsService;
	private FinanceDetailService financeDetailService;
	private ValidationUtility validationUtility;
	private FinanceMainDAO financeMainDAO;
	private FinanceDataDefaulting financeDataDefaulting;
	private FinanceDataValidation financeDataValidation;
	private CollateralSetupService collateralSetupService;

	/**
	 * validate and create finance by receiving request object from interface
	 * 
	 * @param financeDetail
	 */
	@Override
	public FinanceDetail createFinance(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		// do Basic mandatory validations using hibernate validator
		validationUtility.validate(financeDetail, CreateFinanceGroup.class);
		//for logging purpose
		String[] logFields = getLogFields(financeDetail);
		APIErrorHandlerService.logKeyFields(logFields);
		// logging customer CIF as reference for create loan failure cases
		String custCif = logFields[0];
		APIErrorHandlerService.logReference(custCif);
		try {
			if (financeDetail.getFinScheduleData().getFinanceMain() == null) {
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				String[] valueParm = new String[1];
				valueParm[0] = "financeDetail";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return response;
			}

			// validate and Data defaulting
			financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_LOAN, financeDetail.getFinScheduleData());

			if (!financeDetail.getFinScheduleData().getErrorDetails().isEmpty()) {
				return getErrorMessage(financeDetail.getFinScheduleData());
			}

			if (financeDetail.getFinScheduleData().getFinanceMain().getProductCategory()
					.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
				FinanceDetail finResponse = createOverDraftLoanValidation(financeDetail);
				WSReturnStatus status = finResponse.getReturnStatus();
				if (status != null) {
					return finResponse;
				}
			}

			// validate finance data
			if (!StringUtils.isBlank(financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
				CustomerDetails customerDetails = new CustomerDetails();
				customerDetails.setCustomer(null);
				financeDetail.setCustomerDetails(customerDetails);
				financeDataValidation.setFinanceDetail(financeDetail);
			}

			// validate Finance schedule details Validations
			financeDataValidation.financeDataValidation(PennantConstants.VLD_CRT_LOAN,
					financeDetail.getFinScheduleData(), true);
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
			logger.error("Exception", e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		} finally {
			financeDataValidation.setFinanceDetail(null);
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

		if (!financeDetail.getFinScheduleData().getFinanceMain().getScheduleMethod().equals("PFT")) {
			doEmptyResponseObject(response);
			response.setStp(financeDetail.isStp());
			String[] valueParm = new String[2];
			valueParm[0] = "Schedule Method ";
			valueParm[1] = " PFT";
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

		if (financeDetail.getFinScheduleData().getFinanceMain().getFirstDroplineDate()
				.compareTo(financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate()) == -1) {
			doEmptyResponseObject(response);
			response.setStp(financeDetail.isStp());
			String[] valueParm = new String[2];
			valueParm[0] = "First Drop line Date";
			valueParm[1] = "Finance Start Date";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("65012", valueParm);
			status.setReturnText(status.getReturnText());
			response.setReturnStatus(status);
			return response;
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
		//for logging purpose
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
			financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_LOAN, financeDetail.getFinScheduleData());

			if (!financeDetail.getFinScheduleData().getErrorDetails().isEmpty()) {
				return getErrorMessage(financeDetail.getFinScheduleData());
			}
			//validate FinanceDetail Validations
			// validate finance data
			if (StringUtils.isNotBlank(financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
				CustomerDetails customerDetails = new CustomerDetails();
				customerDetails.setCustomer(null);
				financeDetail.setCustomerDetails(customerDetails);
				financeDataValidation.setFinanceDetail(financeDetail);
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
			logger.error("Exception", e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		} finally {
			financeDataValidation.setFinanceDetail(null);
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
		logger.debug("Enetring");
		//for logging purpose
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
		logger.debug("Enetring");

		// Mandatory validation
		if (StringUtils.isBlank(custCif)) {
			validationUtility.fieldLevelException();
		}
		//for logging purpose
		APIErrorHandlerService.logReference(custCif);
		FinanceInquiry response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(custCif);
		if (customer == null) {
			response = new FinanceInquiry();
			String[] valueParm = new String[1];
			valueParm[0] = custCif;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
		} else {
			response = createFinanceController.getFinanceDetailsById(custCif, APIConstants.FINANCE_INQUIRY_CUSTOMER);
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
		logger.debug("Enetring");

		if (StringUtils.isBlank(collateralRef)) {
			validationUtility.fieldLevelException();
		}
		//for logging purpose
		APIErrorHandlerService.logReference(collateralRef);

		FinanceInquiry response = null;
		int count = collateralSetupService.getCountByCollateralRef(collateralRef);
		if (count <= 0) {
			response = new FinanceInquiry();
			String[] valueParm = new String[1];
			valueParm[0] = collateralRef;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90906", valueParm));
		} else {
			response = createFinanceController.getFinanceDetailsById(collateralRef, "Collateral");
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public FinanceDetail getFinanceDetails(String finReference) {
		logger.debug("Enetring");
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

		// set default values
		financeDataDefaulting.doFinanceDetailDefaulting(financeDetail);
		//for logging purpose
		String[] logFields = getLogFields(financeDetail);
		APIErrorHandlerService.logKeyFields(logFields);
		APIErrorHandlerService.logReference(financeDetail.getFinReference());

		//validate FinanceDetail Validations
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
		logger.debug("Enetring");
		FinanceDetail finDetail = null;
		WSReturnStatus returnStatus = null;
		String finReference = financeDetail.getFinReference();

		// for logging purpose
		if (StringUtils.isNotBlank(finReference)) {
			APIErrorHandlerService.logReference(finReference);
		}
		//check reference is in temp table or not
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

	/**
	 * @param createFinanceController
	 *            the createFinanceController to set
	 */
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
}
