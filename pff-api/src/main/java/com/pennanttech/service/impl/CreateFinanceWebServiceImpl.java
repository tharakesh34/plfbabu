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
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.dedup.DedupFieldsDAO;
import com.pennant.backend.dao.dedup.DedupParmDAO;
import com.pennant.backend.dao.finance.FinanceDeviationsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.impl.FinanceDeviationsDAOImpl;
import com.pennant.backend.dao.findedup.FinanceDedupeDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
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
import com.pennant.backend.model.finance.FinReqParams;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceData;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceStatusEnquiry;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.ForeClosureLetter;
import com.pennant.backend.model.finance.ForeClosureResponse;
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
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.impl.FinanceDataDefaulting;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.api.controller.AbstractController;
import com.pennant.validation.CreateFinanceGroup;
import com.pennant.validation.CreateFinanceWithCollateral;
import com.pennant.validation.CreateFinancewithWIFGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennant.ws.exception.ServiceExceptionDetails;
import com.pennanttech.activity.log.Activity;
import com.pennanttech.activity.log.ActivityLogService;
import com.pennanttech.controller.CreateFinanceController;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.foreclosure.service.ForeClosureService;
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
import com.pennanttech.ws.model.finance.FinanceStatusEnquiryDetail;
import com.pennanttech.ws.model.finance.LoanStatus;
import com.pennanttech.ws.model.finance.LoanStatusDetails;
import com.pennanttech.ws.model.finance.MoveLoanStageRequest;
import com.pennanttech.ws.model.financetype.FinInquiryDetail;
import com.pennanttech.ws.model.financetype.FinanceInquiry;
import com.pennanttech.ws.model.statement.FinStatementRequest;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class CreateFinanceWebServiceImpl extends AbstractController
		implements CreateFinanceSoapService, CreateFinanceRestService {

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
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private FinanceDeviationsDAO financeDeviationsDAO;
	private DeviationHelper deviationHelper;
	private SecurityUserDAO securityUserDAO;
	private DedupFieldsDAO dedupFieldsDAO;
	private DedupParmDAO dedupParmDAO;
	private FinanceDedupeDAO financeDedupeDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private ForeClosureService foreClosureService;
	private CustomerDocumentService customerDocumentService;

	/**
	 * validate and create finance by receiving request object from interface
	 * 
	 * @param fd
	 */
	@Override
	public FinanceDetail createFinance(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		if (fm == null) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			String[] valueParm = new String[1];
			valueParm[0] = "financeDetail";
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return fd;
		}

		String custCIF = fm.getCustCIF();
		String coreBankId = fm.getCoreBankId();
		String productCategory = fm.getProductCategory();

		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);

		// do Basic mandatory validations using hibernate validator
		validationUtility.validate(fd, CreateFinanceGroup.class);

		financeDataValidation.doBasicMandatoryValidations(fd);

		if (!CollectionUtils.isEmpty(fd.getCollaterals())) {
			for (CollateralSetup setup : fd.getCollaterals()) {
				validationUtility.validate(setup, CreateFinanceWithCollateral.class);
			}
		}

		if (StringUtils.isBlank(custCIF) && StringUtils.isBlank(coreBankId)) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			String[] valueParm = new String[1];
			valueParm[0] = "Cif/CoreBankId";
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return fd;
		}

		String[] logFields = getLogFields(fd);
		APIErrorHandlerService.logKeyFields(logFields);

		String custCif = logFields[0];
		APIErrorHandlerService.logReference(custCif);

		try {
			financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_LOAN, fd);

			if (!schdData.getErrorDetails().isEmpty()) {
				return getErrorMessage(schdData);
			}

			if (FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory)) {
				fd = createOverDraftLoanValidation(fd);
				WSReturnStatus status = fd.getReturnStatus();
				if (status != null) {
					doEmptyResponseObject(fd);
					return fd;
				}
			}

			financeDataValidation.financeDataValidation(PennantConstants.VLD_CRT_LOAN, schdData, true, fd, false);

			if (!schdData.getErrorDetails().isEmpty()) {
				return getErrorMessage(schdData);
			}

			financeDataValidation.financeDetailValidation(PennantConstants.VLD_CRT_LOAN, fd, true);
			if (!schdData.getErrorDetails().isEmpty()) {
				return getErrorMessage(schdData);
			}

			fd = createFinanceController.doCreateFinance(fd, false);

			if (fd != null) {
				if (fd.getFinScheduleData() != null) {
					for (ErrorDetail ed : schdData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
						return response;
					}
				}
			}
			// for logging purpose
			if (fd != null) {
				APIErrorHandlerService.logReference(fd.getFinReference());
			}

			logger.debug(Literal.LEAVING);
			return fd;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
	}

	public FinanceDetail createOverDraftLoanValidation(FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceType financeType = schdData.getFinanceType();

		FinanceDetail response = new FinanceDetail();
		FinanceMain fm = schdData.getFinanceMain();

		if (!financeType.isFinIsGenRef()) {
			if (financeMainDAO.getCountByFinReference(fm.getFinID(), true) > 0) {
				doEmptyResponseObject(response);
				response.setStp(fd.isStp());
				String[] valueParm = new String[1];
				valueParm[0] = "Finance Reference ";
				WSReturnStatus status = APIErrorHandlerService.getFailedStatus("PR002", valueParm);
				status.setReturnText(valueParm[0] + fm.getFinReference() + " " + status.getReturnText());
				response.setReturnStatus(status);
				return response;
			}
		}

		if (fm.getFinAssetValue().compareTo(new BigDecimal("0")) == 0) {
			doEmptyResponseObject(response);
			response.setStp(fd.isStp());
			String[] valueParm = new String[1];
			valueParm[0] = "Finance Asset Value ";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90501", valueParm);
			status.setReturnText(status.getReturnText().replace("Invalid", "Required"));
			fd.setReturnStatus(status);
			return fd;
		}

		if (fm.getFinAssetValue().compareTo(new BigDecimal("0")) == -1) {
			doEmptyResponseObject(response);
			response.setStp(fd.isStp());
			String[] valueParm = new String[1];
			valueParm[0] = "Finance Asset Value ";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90259", valueParm);
			status.setReturnText(status.getReturnText().replace(" feeCode:", " "));
			fd.setReturnStatus(status);
			return fd;
		}

		if ((new BigDecimal("999999999999999999").compareTo(fm.getFinAssetValue()) == -1)) {
			doEmptyResponseObject(response);
			response.setStp(fd.isStp());
			String[] valueParm = new String[2];
			valueParm[0] = "Finance Asset Value is less than 18 digits or ";
			valueParm[1] = " 18";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90300", valueParm);
			status.setReturnText(status.getReturnText().replace("maximum", "Maximum").replaceAll("\n\t\t\t", " "));
			fd.setReturnStatus(status);
			return fd;
		}

		// schedule method PFT only allowed in Over Draft

		if (StringUtils.isBlank(fm.getScheduleMethod())) {
			doEmptyResponseObject(response);
			response.setStp(fd.isStp());
			String[] valueParm = new String[1];
			valueParm[0] = "Schedule Method ";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90501", valueParm);
			status.setReturnText(status.getReturnText().replace("Invalid", "Required"));
			fd.setReturnStatus(status);
			return fd;
		}

		if (!fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_POS_INT)) {
			doEmptyResponseObject(response);
			response.setStp(fd.isStp());
			String[] valueParm = new String[2];
			valueParm[0] = "Schedule Method ";
			valueParm[1] = CalculationConstants.SCHMTHD_POS_INT;
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90337", valueParm);
			status.setReturnText(status.getReturnText());
			fd.setReturnStatus(status);
			return fd;
		}

		// disbursement is not required in Over Draft loan

		List<FinAdvancePayments> advancePaymentsList = fd.getAdvancePaymentsList();

		if (!CollectionUtils.isEmpty(advancePaymentsList)) {
			doEmptyResponseObject(response);
			response.setStp(fd.isStp());
			String[] valueParm = new String[2];
			valueParm[0] = "Disbursement ";
			valueParm[1] = " Overdraft Loan";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90204", valueParm);
			status.setReturnText(status.getReturnText());
			fd.setReturnStatus(status);
			return fd;
		}

		if (fm.getFirstDroplineDate() != null) {
			if (fm.getFirstDroplineDate().compareTo(fm.getFinStartDate()) <= 0) {
				doEmptyResponseObject(response);
				response.setStp(fd.isStp());
				String[] valueParm = new String[2];
				valueParm[0] = "First Drop line Date";
				valueParm[1] = "Finance Start Date";
				WSReturnStatus status = APIErrorHandlerService.getFailedStatus("91121", valueParm);
				status.setReturnText(status.getReturnText());
				fd.setReturnStatus(status);
				return fd;
			}
		}

		if (fm.getFinAmount().compareTo(new BigDecimal("0")) != 0) {
			doEmptyResponseObject(response);
			response.setStp(fd.isStp());
			String[] valueParm = new String[2];
			valueParm[0] = "Finance Amount ";
			valueParm[1] = "Zero";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90281", valueParm);
			String alterMessage = status.getReturnText();
			status.setReturnText(alterMessage);
			fd.setReturnStatus(status);
			return fd;
		}

		if (fm.getOverdraftChrgAmtOrPerc().compareTo(BigDecimal.ZERO) < 0) {
			doEmptyResponseObject(response);
			response.setStp(fd.isStp());
			String[] valueParm = new String[1];
			valueParm[0] = "OverDraft Charge Amt or Percentage ";
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("STP008", valueParm);
			status.setReturnText(valueParm[0] + fm.getOverdraftChrgAmtOrPerc() + " " + status.getReturnText());
			response.setReturnStatus(status);
			return response;
		}

		fm.setRecalType("");
		if (StringUtils.isBlank(fm.getRepayBaseRate())) {
			fm.setRepayBaseRate(null);
		}

		return fd;
	}

	public FinanceDetail getOverDraftMaintenance(OverDraftMaintenance odFm) throws ServiceException {
		String finReference = odFm.getFinReference();
		APIErrorHandlerService.logReference(finReference);

		WSReturnStatus returnStatus = new WSReturnStatus();

		Long finID = financeMainDAO.getFinID(finReference);
		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		FinanceDetail fd = null;
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			fd = getFaultResponse(returnStatus);
			return fd;
		}

		fd = createFinanceController.getFinInquiryDetails(finID);
		fd.getFinScheduleData().setFinanceType(null);
		fd.setCustomerDetails(null);
		fd.setMandate(null);

		return fd;
	}

	/**
	 * validate and create finance with WIF reference by receiving request object from interface.
	 * 
	 * @param fd
	 */
	@Override
	public FinanceDetail createFinanceWithWIF(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(fd, CreateFinancewithWIFGroup.class);

		String[] logFields = getLogFields(fd);
		APIErrorHandlerService.logKeyFields(logFields);

		try {
			WSReturnStatus returnStatus = new WSReturnStatus();

			FinScheduleData schdData = fd.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();

			if (fm == null) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				String[] valueParm = new String[1];
				valueParm[0] = "financeDetail";
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return fd;
			}

			if (StringUtils.isEmpty(fm.getFinType())) {
				ErrorDetail error = ErrorUtil.getErrorDetail(new ErrorDetail("90126"));

				ServiceExceptionDetails exceptions[] = new ServiceExceptionDetails[1];
				ServiceExceptionDetails exception = new ServiceExceptionDetails();

				exception.setFaultCode(error.getCode());
				exception.setFaultMessage(error.getError());

				exceptions[0] = exception;

				throw new ServiceException(exceptions);
			}

			String custCIF = fm.getLovDescCustCIF();
			String finReference = fd.getFinReference();

			// validate Customer
			Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custCIF;
				returnStatus = APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}

			if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
				return getFaultResponse(returnStatus);
			}

			Long wifFinID = financeMainDAO.getFinIDByFinReference(finReference, "", true);

			if (wifFinID == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}

			if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
				return getFaultResponse(returnStatus);
			}

			if (StringUtils.isBlank(fm.getFinRepayMethod())) {
				String[] valueParm = new String[1];
				valueParm[0] = "finRepayMethod";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
				return getFaultResponse(returnStatus);
			}

			String procEdtEvent = FinServiceEvent.ORG;

			FinanceDetail wfd = null;
			Long wIfFinID = financeMainDAO.getActiveWIFFinID(finReference, TableType.MAIN_TAB);

			if (wIfFinID != null) {
				// fetch WIF finance details
				wfd = financeDetailService.getWIFFinance(wIfFinID, true, procEdtEvent);
				if (wfd != null) {
					custCIF = fm.getLovDescCustCIF();
					String finRepayMethod = fm.getFinRepayMethod();
					Date finContractDate = fm.getFinContractDate();
					String finPurpose = fm.getFinPurpose();
					String finLimitRef = fm.getFinLimitRef();
					String finCommitmentRef = fm.getFinCommitmentRef();
					String dsaCode = fm.getDsaCode();
					String salesDepartment = fm.getSalesDepartment();
					String dmaCode = fm.getDmaCode();
					long accountsOfficer = fm.getAccountsOfficer();
					String referralId = fm.getReferralId();
					boolean quickDisb = fm.isQuickDisb();

					FinScheduleData wSchdData = wfd.getFinScheduleData();
					FinanceMain wfm = wSchdData.getFinanceMain();
					wfm.setLovDescCustCIF(custCIF);
					wfm.setFinRepayMethod(finRepayMethod);
					wfm.setFinContractDate(finContractDate);
					wfm.setFinPurpose(finPurpose);
					wfm.setFinLimitRef(finLimitRef);
					wfm.setFinCommitmentRef(finCommitmentRef);
					wfm.setDsaCode(dsaCode);
					wfm.setSalesDepartment(salesDepartment);
					wfm.setDmaCode(dmaCode);
					wfm.setAccountsOfficer(accountsOfficer);
					wfm.setReferralId(referralId);
					wfm.setQuickDisb(quickDisb);

					fd.setFinScheduleData(wSchdData);
				}

				Long finID = financeMainDAO.getActiveFinID(finReference, TableType.MAIN_TAB);
				if (finID != null) {
					String[] valueParm = new String[1];
					valueParm[0] = finReference;
					fd = new FinanceDetail();
					doEmptyResponseObject(fd);
					fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("91122"));
					return fd;
				}
			}

			// validate and Data defaulting
			fd = financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_LOAN, fd);

			if (!schdData.getErrorDetails().isEmpty()) {
				return getErrorMessage(schdData);
			}
			// validate FinanceDetail Validations
			// validate finance data
			if (StringUtils.isNotBlank(fm.getLovDescCustCIF())) {
				CustomerDetails customerDetails = new CustomerDetails();
				customerDetails.setCustomer(null);
				fd.setCustomerDetails(customerDetails);
			}

			financeDataValidation.financeDetailValidation(PennantConstants.VLD_CRT_LOAN, fd, true);

			if (!schdData.getErrorDetails().isEmpty()) {
				return getErrorMessage(schdData);
			}

			// call doCreate method to create finance with WIF Reference
			fd = createFinanceController.doCreateFinance(fd, true);

			if (fd != null) {
				schdData = fd.getFinScheduleData();
				if (schdData != null) {
					for (ErrorDetail ed : schdData.getErrorDetails()) {
						fd = getFaultResponse(ed);
						return fd;
					}
				}
			}

			if (fd != null) {
				APIErrorHandlerService.logReference(fd.getFinReference());
			}

			logger.debug(Literal.LEAVING);

			return fd;
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			fd = getFaultResponse();
			return fd;
		}
	}

	private FinanceDetail getFaultResponse(WSReturnStatus returnStatus) {
		FinanceDetail fd = new FinanceDetail();
		doEmptyResponseObject(fd);
		fd.setReturnStatus(returnStatus);
		return fd;
	}

	private FinanceDetail getFaultResponse(ErrorDetail ed) {
		FinanceDetail fd = new FinanceDetail();
		doEmptyResponseObject(fd);
		fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
		return fd;
	}

	private FinanceDetail getFaultResponse() {
		FinanceDetail fd = new FinanceDetail();
		doEmptyResponseObject(fd);
		fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		return fd;
	}

	@Override
	public FinanceDetail getFinInquiryDetails(String finReference) throws ServiceException {
		logger.debug(Literal.ENTERING);

		APIErrorHandlerService.logReference(finReference);

		WSReturnStatus returnStatus = new WSReturnStatus();

		Long finID = financeMainDAO.getFinID(finReference);
		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			FinanceDetail fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(returnStatus);
			return fd;
		}

		FinanceDetail financeDetail = createFinanceController.getFinInquiryDetails(finID);

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

		FinanceDetail response = new FinanceDetail();

		if (StringUtils.isNotBlank(finReference)) {
			APIErrorHandlerService.logReference(finReference);
		}

		Long finID = financeMainDAO.getFinID(finReference);

		if (finID == null) {
			String valueParam[] = new String[1];
			valueParam[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParam));
			return response;
		}

		response = createFinanceController.getFinanceDetails(finID);

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus updateFinance(FinanceDetail fd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		String finReference = fd.getFinReference();

		FinanceMain fm = null;
		if (fd != null) {
			fm = financeMainDAO.getFinanceDetailsForService1(finReference, "_Temp", false);
			if (fm == null) {
				String valueParam[] = new String[1];
				valueParam[0] = finReference;
				return APIErrorHandlerService.getFailedStatus("90201", valueParam);
			}

		}

		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinanceMain(fm);

		financeDataDefaulting.doFinanceDetailDefaulting(fd);

		String[] logFields = getLogFields(fd);
		APIErrorHandlerService.logKeyFields(logFields);
		APIErrorHandlerService.logReference(finReference);

		FinScheduleData finSchData = financeDataValidation.financeDetailValidation(PennantConstants.VLD_UPD_LOAN, fd,
				true);
		if (!finSchData.getErrorDetails().isEmpty()) {
			FinanceDetail finDetail = getErrorMessage(finSchData);
			return finDetail.getReturnStatus();
		}

		WSReturnStatus response = createFinanceController.updateFinance(fd);

		logger.debug(Literal.LEAVING);

		return response;
	}

	@Override
	public WSReturnStatus approveLoan(FinanceDetail fd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = null;
		String finReference = fd.getFinReference();

		if (StringUtils.isNotBlank(finReference)) {
			APIErrorHandlerService.logReference(finReference);
		}

		FinanceMain fm = financeMainDAO.getFinanceDetailsForService1(finReference, "_Temp", false);

		if (fm == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);

		}

		long finID = fm.getFinID();

		fd = createFinanceController.getFinanceDetails(finID);

		if (fd != null) {
			returnStatus = createFinanceController.doApproveLoan(fd);
		}

		logger.debug(Literal.LEAVING);

		return returnStatus;
	}

	private FinanceDetail getErrorMessage(FinScheduleData schdData) {

		for (ErrorDetail ed : schdData.getErrorDetails()) {
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));

			return response;
		}

		return new FinanceDetail();
	}

	/**
	 * Nullify the un-necessary objects to prepare response in a structured format specified in API.
	 * 
	 * @param fd
	 */
	private void doEmptyResponseObject(FinanceDetail fd) {
		fd.setFinScheduleData(null);
		fd.setAdvancePaymentsList(null);
		fd.setMandate(null);
		fd.setJointAccountDetailList(null);
		fd.setGurantorsDetailList(null);
		fd.setDocumentDetailsList(null);
		fd.setCovenantTypeList(null);
		fd.setCollateralAssignmentList(null);
		fd.setFinFlagsDetails(null);
		fd.setCustomerDetails(null);
		fd.setInterfaceDetailList(null);
		fd.setLegalDetailsList(null);
		fd.setReturnDataSetList(null);
		fd.setStp(null);
		fd.setReceiptId(null);
		fd.setDisbStp(null);
		fd.setOrigination(null);
	}

	/**
	 * Method to reject loan based on data provided by customer
	 * 
	 * @param fd {@link FinanceDetail}
	 * @return {@link WSReturnStatus}
	 */
	@Override
	public WSReturnStatus rejectFinance(FinanceDetail fd) throws ServiceException {

		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = null;
		try {
			if (StringUtils.isEmpty(fd.getFinScheduleData().getFinReference())) {
				List<ErrorDetail> validationErrors = createFinanceController.rejectFinanceValidations(fd);
				if (CollectionUtils.isEmpty(validationErrors)) {
					fd = financeDataDefaulting.defaultFinance(PennantConstants.VLD_CRT_LOAN, fd);
					List<ErrorDetail> financeDetailErrors = null;
					if (!CollectionUtils.isEmpty(fd.getFinScheduleData().getErrorDetails())) {
						financeDetailErrors = fd.getFinScheduleData().getErrorDetails();
						for (ErrorDetail errorDetail : financeDetailErrors) {
							doEmptyResponseObject(fd);
							returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
									errorDetail.getError());
						}
					} else {
						returnStatus = createFinanceController.processRejectFinance(fd, false);
					}
				} else {
					for (ErrorDetail errorDetail : validationErrors) {
						returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getParameters());
					}
				}
			} else {
				FinanceMain fm = new FinanceMain();
				Long finID = financeMainDAO.getActiveFinID(fd.getFinScheduleData().getFinReference(),
						TableType.TEMP_TAB);

				// throw validation error
				if (finID == null) {
					String[] valueParam = new String[1];
					valueParam[0] = "finreference: " + fd.getFinScheduleData().getFinReference();

					return APIErrorHandlerService.getFailedStatus("90266", valueParam);
				}
				fm.setFinID(finID);
				fd.getFinScheduleData().setFinID(finID);
				fd.getFinScheduleData().setFinanceMain(fm);
				returnStatus = createFinanceController.processRejectFinance(fd, true);
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
	 * @param fd {@link FinanceDetail}
	 * @return {@link WSReturnStatus}
	 */
	@Override
	public FinanceDetail cancelFinance(FinanceDetail fd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		APIErrorHandlerService.logReference(fd.getFinReference());

		FinScheduleData schdData = fd.getFinScheduleData();
		String finReference = schdData.getFinReference();
		String externalReference = schdData.getExternalReference();

		if (StringUtils.isNotBlank(finReference) && StringUtils.isNotBlank(externalReference)) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			String[] valueParm = new String[2];
			valueParm[0] = "finReference";
			valueParm[1] = "External Reference";
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("30511", valueParm));
			return fd;
		}

		if (StringUtils.isBlank(finReference) && StringUtils.isBlank(externalReference)) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			String[] valueParm = new String[2];
			valueParm[0] = "finReference";
			valueParm[1] = "External Reference";
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90123", valueParm));
			return fd;
		}

		if (StringUtils.isNotBlank(finReference)) {
			Long finID = financeMainDAO.getActiveFinID(finReference, TableType.MAIN_TAB);
			if (finID == null) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				String[] valueParm = new String[1];
				valueParm[0] = fd.getFinReference();
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return fd;
			} else {
				fd.setFinID(finID);
				fd.setFinReference(finReference);
			}
		} else {
			WSReturnStatus returnStatus = new WSReturnStatus();
			FinanceMain fm = financeMainDAO.getFinanceMainByHostReference(externalReference, true);

			if (fm == null) {
				String[] valueParm = new String[1];
				valueParm[0] = externalReference;
				returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
			} else {
				fd.setFinID(fm.getFinID());
				fd.setFinReference(fm.getFinReference());
			}

			if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
				fd = getFaultResponse(returnStatus);
				return fd;
			}
		}

		fd = createFinanceController.processCancelFinance(fd);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	@Override
	public FinanceDetail reInitiateFinance(FinanceDetail fd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		String finReference = schdData.getFinReference();
		String externalReference = schdData.getExternalReference();

		APIErrorHandlerService.logReference(finReference);

		WSReturnStatus returnStatus = new WSReturnStatus();

		if (StringUtils.isNotBlank(finReference) && StringUtils.isNotBlank(schdData.getExternalReference())) {
			fd = new FinanceDetail();
			String[] valueParm = new String[2];
			valueParm[0] = "finReference";
			valueParm[1] = "ExternalReference";
			returnStatus = APIErrorHandlerService.getFailedStatus("30511", valueParm);
			fd.setReturnStatus(returnStatus);

			return fd;
		}

		try {
			fd.setFinReference(finReference);
			if (StringUtils.isNotBlank(finReference)) {
				FinanceMain fm = financeMainDAO.getRejectFinanceMainByRef(finReference);
				if (fm == null) {
					fd = new FinanceDetail();
					String[] valueParm = new String[1];
					valueParm[0] = finReference;
					returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
					fd.setReturnStatus(returnStatus);
					return fd;
				}

				fd.setFinID(fm.getFinID());
			} else {
				FinanceMain fm = financeMainDAO.getFinanceMainByHostReference(externalReference, false);

				if (fm == null) {
					String[] valueParm = new String[1];
					valueParm[0] = externalReference;
					returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);

					fd = new FinanceDetail();
					fd.setReturnStatus(returnStatus);
					return fd;
				}

				fd.setFinReference(fm.getFinReference());
				fd.setFinID(fm.getFinID());
			}

			if (StringUtils.isNotBlank(schdData.getOldFinReference())) {
				int count = financeMainDAO.getCountByOldFinReference(schdData.getOldFinReference());
				if (count > 0) {
					fd = new FinanceDetail();
					String[] valueParm = new String[2];
					valueParm[0] = "Host Reference";
					valueParm[1] = schdData.getOldFinReference();
					returnStatus = APIErrorHandlerService.getFailedStatus("30506", valueParm);
					fd.setReturnStatus(returnStatus);
					return fd;
				}
			}
			if (StringUtils.isNotBlank(schdData.getExternalReference())) {
				int count = financeMainDAO.getCountByExternalReference(schdData.getExternalReference());
				if (count > 0) {
					fd = new FinanceDetail();
					String[] valueParm = new String[2];
					valueParm[0] = "ReInitiate";
					returnStatus = APIErrorHandlerService.getFailedStatus("90273", valueParm);
					fd.setReturnStatus(returnStatus);
					return fd;
				}
			}
			fd.setFinReference(finReference);
			fd = createFinanceController.doReInitiateFinance(fd);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return fd;
		}
		logger.debug(Literal.LEAVING);
		return fd;

	}

	@Override
	public WSReturnStatus moveLoanStage(MoveLoanStageRequest mlsr) throws ServiceException {
		logger.debug(Literal.ENTERING);

		String finReference = mlsr.getFinReference();
		APIErrorHandlerService.logReference(finReference);

		WSReturnStatus returnStatus = new WSReturnStatus();
		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		if (StringUtils.isBlank(mlsr.getCurrentStage())) {
			String[] valueParm = new String[1];
			valueParm[0] = "CurrentStage";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		if (StringUtils.isBlank(mlsr.getAction())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Action";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		Long finID = financeMainDAO.getFinID(finReference, TableType.TEMP_TAB);

		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, TableType.TEMP_TAB.getSuffix(), false);

		if (!StringUtils.equals(mlsr.getCurrentStage(), fm.getNextRoleCode())) {
			String[] valueParm = new String[2];
			valueParm[0] = "CurrentStage: " + mlsr.getCurrentStage();
			valueParm[1] = fm.getNextRoleCode();
			return returnStatus = APIErrorHandlerService.getFailedStatus("90337", valueParm);
		}

		Map<String, String> userActions = createFinanceController.getUserActions(fm);

		if (userActions != null) {
			if (!userActions.containsKey(mlsr.getAction())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Action: " + mlsr.getAction();
				valueParm[1] = userActions.keySet().toString();
				return returnStatus = APIErrorHandlerService.getFailedStatus("90337", valueParm);
			} else {
				FinanceDetail fd = createFinanceController.getFinanceDetails(finID);
				fm = fd.getFinScheduleData().getFinanceMain();

				FinanceMain beffinanceMain = new FinanceMain();
				BeanUtils.copyProperties(fm, beffinanceMain);
				fm.setBefImage(beffinanceMain);

				fm.setRecordStatus(userActions.get(mlsr.getAction()));
				// financeMain.setVersion(financeMain.getVersion()+1);
				// financeMain.setNewRecord(false);
				WSReturnStatus response = createFinanceController.doMoveLoanStage(fd, mlsr);
				logger.debug(Literal.LEAVING);
				return response;

			}
		} else {

		}
		return returnStatus;

	}

	private String[] getLogFields(FinanceDetail fd) {
		String[] logfields = new String[3];
		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = schdData.getFinanceMain();
		if (fm != null) {
			logfields[0] = fm.getCustCIF();
			logfields[1] = fm.getFinType();
			logfields[2] = String.valueOf(fm.getFinAmount());
		}

		return logfields;
	}

	@Override
	public FinanceInquiry getPendingFinanceWithCustomer(String custCif) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCif)) {
			validationUtility.fieldLevelException();
		}

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

		List<LoanStatus> listResponse = new ArrayList<LoanStatus>();
		for (LoanStatus loanStatus : loanStatusDetails.getLoanSatusDetails()) {
			String finReference = loanStatus.getFinReference();

			APIErrorHandlerService.logReference(finReference);
			LoanStatus response = null;
			if (StringUtils.isBlank(finReference)) {
				response = new LoanStatus();
				// /doEmptyResponseObject(response);
				String[] valueParm = new String[1];
				valueParm[0] = "finreference";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				listResponse.add(response);
				return listResponse;

			}

			FinanceMain fm = financeMainDAO.getFinanceMain(finReference);
			if (fm == null) {
				response = new LoanStatus();

				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90501", valueParm));
				listResponse.add(response);

				return listResponse;
			}

			response = new LoanStatus();
			response.setRecordStatus(fm.getRecordStatus());
			response.setRoleCode(fm.getRoleCode());
			response.setNextRoleCode(fm.getNextRoleCode());
			response.setFinReference(fm.getFinReference());
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

			listResponse.add(response);

		}
		logger.debug(Literal.LEAVING);
		return listResponse;
	}

	@Override
	public AgreementDetails getAgreements(AgreementRequest aggReq) throws ServiceException {
		logger.debug(Literal.ENTERING);

		String finReference = aggReq.getFinReference();

		AgreementDetails ad = new AgreementDetails();

		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			ad.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return ad;
		}

		try {
			Long finID = financeMainDAO.getFinID(finReference, TableType.BOTH_TAB);

			if (finID == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				ad.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return ad;
			}

			// for logging purpose
			APIErrorHandlerService.logReference(finReference);

			FinanceDetail fd = createFinanceController.getFinanceDetails(finID);
			FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

			if (StringUtils.isNotBlank(aggReq.getAgreementType())) {
				List<AgreementData> aggList = createFinanceController.getAgreements(fd, aggReq);
				for (AgreementData agreementData : aggList) {
					WSReturnStatus wrs = agreementData.getReturnStatus();
					if (wrs != null && StringUtils.isBlank(wrs.getReturnCode())) {
						ad.setReturnStatus(agreementData.getReturnStatus());
						return ad;
					}

				}

				ad.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				ad.setAgreementsList(aggList);
				return ad;
			}

			List<AgreementData> agreements = new ArrayList<>();
			List<FinanceReferenceDetail> agreemantsList = financeReferenceDetailDAO
					.getAgreemantsListByFinType(fm.getFinType());

			for (FinanceReferenceDetail financeDetail : agreemantsList) {
				AgreementRequest ar = new AgreementRequest();
				ar.setAgreementType(financeDetail.getLovDescCodelov());
				List<AgreementData> agglist = createFinanceController.getAgreements(fd, ar);
				agreements.addAll(agglist);
			}

			if (CollectionUtils.isEmpty(agreements)) {
				String[] valueParm = new String[1];
				valueParm[0] = "No Agreements found with " + finReference;
				ad.setReturnStatus(APIErrorHandlerService.getFailedStatus("21005", valueParm));
				return ad;
			} else {
				ad.setAgreementsList(agreements);
			}

		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			ad.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug(Literal.LEAVING);

		return ad;

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
		}

		FinanceMain fm = financeMainDAO.getUserActions(finReference);

		if (fm == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}

		Map<String, String> userActions = createFinanceController.getUserActions(fm);

		if (userActions.isEmpty()) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90355", valueParm));
			return response;
		}

		for (Map.Entry<String, String> entry : userActions.entrySet()) {
			ValueLabel valueLabel = new ValueLabel();
			valueLabel.setLabel(entry.getKey());
			valueLabel.setValue(entry.getValue());
			actionsList.add(valueLabel);
		}

		response.setValueLabel(actionsList);
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

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
		}

		Long finID = financeMainDAO.getFinID(finReference, TableType.BOTH_TAB);

		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
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

		Long finID = financeMainDAO.getFinID(finReference, TableType.BOTH_TAB);
		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}

		List<FinanceDeviations> deviations = financeDeviationsDAO.getFinanceDeviations(finID, "_View");

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
	public DeviationList getLoanDeviations(FinanceDeviations deviation) throws ServiceException {
		logger.debug(Literal.ENTERING);

		DeviationList response = new DeviationList();
		List<FinanceDeviations> deviations = new ArrayList<>();

		String finReference = deviation.getFinReference();
		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		String status = deviation.getApprovalStatus();
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

		Long finID = financeMainDAO.getFinID(finReference, TableType.BOTH_TAB);
		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}

		if (DeviationConstants.DEVIATION_STATUS_APPROVED.equals(status)
				|| DeviationConstants.DEVIATION_STATUS_REJECT.equals(status)) {
			deviations = financeDeviationsDAO.getFinanceDeviationsByStatus(finID, status, "");
		} else if (StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_PENDING)) {
			deviations = financeDeviationsDAO.getFinanceDeviations(finID, "_Temp");
		} else {
			deviations = financeDeviationsDAO.getFinanceDeviations(finID, "_View");
		}

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
	public WSReturnStatus updateLoanDeviation(FinanceDeviations deviation) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();

		String finReference = deviation.getFinReference();

		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			response = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return response;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(finReference);
		if (fm == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response = APIErrorHandlerService.getFailedStatus("90201", valueParm);
			return response;
		}

		long finID = fm.getFinID();
		long workflowId = fm.getWorkflowId();
		deviation.setFinID(finID);
		deviation.setWorkflowId(workflowId);

		response = validateUpdateDeviationRequest(deviation);

		if (response.getReturnCode() != null) {
			return response;
		}

		response = createFinanceController.updateDeviationStatus(deviation);

		logger.debug(Literal.LEAVING);
		return response;
	}

	public WSReturnStatus validateUpdateDeviationRequest(FinanceDeviations deviation) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus wsrs = new WSReturnStatus();

		if (deviation.getDeviationId() == Long.MIN_VALUE) {
			String[] valueParm = new String[1];
			valueParm[0] = "deviationId";
			wsrs = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return wsrs;
		}

		String status = deviation.getApprovalStatus();
		if (StringUtils.isBlank(status) && StringUtils.isBlank(deviation.getDelegationRole())) {
			String[] valueParm = new String[2];
			valueParm[0] = "approvalStatus";
			valueParm[1] = "delegationRole";
			wsrs = APIErrorHandlerService.getFailedStatus("90123", valueParm);
			return wsrs;

		}
		if (StringUtils.isNotBlank(status) && !StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_PENDING)
				&& !StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_APPROVED)
				&& !StringUtils.equals(status, DeviationConstants.DEVIATION_STATUS_REJECT)) {
			String[] valueParm = new String[2];
			valueParm[0] = "approvalStatus";
			valueParm[1] = DeviationConstants.DEVIATION_STATUS_PENDING + ", "
					+ DeviationConstants.DEVIATION_STATUS_APPROVED + ", " + DeviationConstants.DEVIATION_STATUS_REJECT;
			wsrs = APIErrorHandlerService.getFailedStatus("90281", valueParm);
			return wsrs;
		}

		if (StringUtils.isNotBlank(deviation.getDelegationRole())) {
			StringBuilder roles = new StringBuilder();
			boolean roleFound = false;
			List<ValueLabel> delegators = deviationHelper.getRoleAndDesc(deviation.getWorkflowId());
			if (CollectionUtils.isNotEmpty(delegators)) {
				for (ValueLabel details : delegators) {
					roles.append(details.getValue() + ",");
				}
				for (ValueLabel valueLabel : delegators) {
					if (StringUtils.equals(deviation.getDelegationRole(), valueLabel.getValue())) {
						roleFound = true;
						break;
					}
				}
			} else {
				String[] valueParm = new String[2];
				valueParm[0] = "delegationRole not found";
				wsrs = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				return wsrs;
			}
			if (!roleFound) {
				String[] valueParm = new String[2];
				valueParm[0] = "delegationRole";
				valueParm[1] = roles.toString();
				wsrs = APIErrorHandlerService.getFailedStatus("90281", valueParm);
				return wsrs;
			}
		}

		logger.debug(Literal.LEAVING);
		return wsrs;
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
			// if no data found for offerID
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

	@Override
	public FinanceStatusEnquiryDetail getLoansStatusEnquiry(FinanceStatusEnquiryDetail fsed) throws ServiceException {
		logger.debug(Literal.ENTERING);

		FinanceStatusEnquiryDetail lsd = new FinanceStatusEnquiryDetail();

		List<FinanceStatusEnquiry> enquiryDetails = fsed.getFinanceStatusEnquiryList();

		if (CollectionUtils.isEmpty(enquiryDetails)) {
			String[] valueParam = new String[1];
			valueParam[0] = "LoanSatusDetails";
			lsd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParam));
			return lsd;
		}

		if (enquiryDetails.size() > 20) {
			String[] valueParam = new String[2];
			valueParam[0] = "FinReferences";
			valueParam[1] = "20";
			lsd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90220", valueParam));
			return lsd;
		}

		List<FinanceStatusEnquiry> responseList = new ArrayList<>();

		for (FinanceStatusEnquiry detail : enquiryDetails) {
			String finReference = detail.getFinReference();
			if (StringUtils.isBlank(finReference)) {
				lsd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", new String[] { "Finreference" }));
				return lsd;
			} else {
				Long finID = financeMainDAO.getActiveFinID(finReference, TableType.MAIN_TAB);

				if (finID == null) {
					String[] valueParam = new String[1];
					valueParam[0] = finReference;
					lsd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParam));
					return lsd;
				}

				FinanceStatusEnquiry fse = financeMainDAO.getLoanStatusDetailsByFinReference(finID);
				if (fse == null) {
					fse = new FinanceStatusEnquiry();
					fse.setStatus("No Loan Are Available for the Reference");
				}
				fse.setFinID(finID);
				// set Finance closing status
				if (StringUtils.isBlank(fse.getClosingStatus()) && StringUtils.isBlank(fse.getStatus())) {
					fse.setClosingStatus(APIConstants.CLOSE_STATUS_ACTIVE);
				}
				// Temporary FIX at API Level, it should be FIX in entire Application level.
				if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fse.getClosingStatus())
						|| FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(fse.getClosingStatus())) {
					fse.setOutStandPrincipal(BigDecimal.ZERO);
				}

				FinStatementRequest statement = new FinStatementRequest();
				ForeClosureResponse response = new ForeClosureResponse();

				statement.setFinID(finID);
				statement.setFinReference(detail.getFinReference());
				statement.setDays(1);
				statement.setFromDate(SysParamUtil.getAppDate());

				ForeClosureLetter fcl = foreClosureService.getForeClosureAmt(statement, response);
				fse.setForeclosureAmt(fcl.getForeCloseAmount());
				fse.setExcessAmt(fcl.getExcessAmount());
				fse.setWriteOffAmt(getWriteOffAmount(fse, financeScheduleDetailDAO.getWriteoffTotals(finID)));

				if (statement.getFromDate().compareTo(fse.getMaturityDate()) > 0) {
					fse.setForeclosureAmt(BigDecimal.ZERO);
				}

				responseList.add(fse);
			}
		}

		lsd.setFinanceStatusEnquiryList(responseList);
		lsd.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug(Literal.LEAVING);

		return lsd;
	}

	@Override
	public FinanceInquiry getFinanceDetailsByParams(FinReqParams reqParams) throws ServiceException {
		logger.debug(Literal.ENTERING);
		String reqType = reqParams.getReqType();
		String reqParam = reqParams.getReqParam();

		FinanceData financeData = null;

		if (StringUtils.isNotEmpty(reqParams.getLoggedInUser()) && StringUtils.isNotEmpty(reqParams.getStageCodes())) {
			financeData = new FinanceData();
			financeData.setLoginId(reqParams.getLoggedInUser());
			financeData.setStageCodes(reqParams.getStageCodes());
		}

		if (StringUtils.isBlank(reqType)) {
			FinanceInquiry response = new FinanceInquiry();
			response.setReturnStatus(getError("90502", "reqType"));
			return response;
		}

		if (StringUtils.isBlank(reqParam)) {
			FinanceInquiry response = new FinanceInquiry();
			response.setReturnStatus(getError("90502", "reqParam"));
			return response;
		}

		switch (reqParams.getReqType()) {
		case "LAN":
			return prepareResponseForLAN(reqParams);
		case "CIF":
			return prepareResponseForCIF(reqParams, financeData);
		case PennantConstants.PHONETYPE_MOBILE:
			return prepareResponseForMobile(reqParam, financeData);
		case "PAN":
			return prepareResponseForPAN(reqParam, financeData);
		case "LOANTYPE":
			return createFinanceController.getFinDetailsByFinType(reqParam);
		default:
			FinanceInquiry response = new FinanceInquiry();
			response.setReturnStatus(getError("30550", "reqType Not Valid"));
			return response;
		}
	}

	@Override
	public FinanceDetail getFinDetailsByFinReference(String finReference) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotBlank(finReference)) {
			APIErrorHandlerService.logReference(finReference);
		}

		logger.debug(Literal.LEAVING);
		return createFinanceController.getFinDetailsByFinReference(finReference);
	}

	private BigDecimal getWriteOffAmount(FinanceStatusEnquiry fse, FinanceWriteoff writeOff) {
		BigDecimal writeOffAmount = BigDecimal.ZERO;

		writeOffAmount = writeOffAmount.add(writeOff.getUnPaidSchdPri());
		writeOffAmount = writeOffAmount.add(writeOff.getUnPaidSchdPft());
		writeOffAmount = writeOffAmount.add(writeOff.getUnpaidSchFee());
		writeOffAmount = writeOffAmount.add(fse.getPenaltyDue());

		return writeOffAmount;
	}

	private FinanceInquiry prepareResponseForLAN(FinReqParams reqParams) {
		FinanceInquiry response = new FinanceInquiry();

		FinanceMain fm = financeMainDAO.getFinanceDetailsByFinRefence(reqParams.getReqParam());
		if (fm == null) {
			response.setReturnStatus(getError("30550", "LAN Details Not Found"));
			return response;
		}

		Customer customer = customerDetailsService.getCustomer(fm.getCustID());

		List<FinInquiryDetail> finInqList = new ArrayList<>();

		finInqList.add(createFinanceController.getFinInquiryByFinance(fm, customer));

		response.setFinance(finInqList);

		return response;
	}

	private FinanceInquiry prepareResponseForCIF(FinReqParams reqParams, FinanceData financeData) {
		Customer customer = customerDetailsService.getCustomerByCIF(reqParams.getReqParam());

		if (customer == null) {
			FinanceInquiry response = new FinanceInquiry();
			response.setReturnStatus(getError("30550", "No LAN's found for given details"));
			return response;
		}

		return createFinanceController.getFinanceDetailsByCIF(customer, financeData);
	}

	private FinanceInquiry prepareResponseForMobile(String reqParam, FinanceData financeData) {
		FinanceInquiry response = new FinanceInquiry();
		List<Customer> custList = customerDetailsService.getCustomersByPhoneNum(reqParam);

		if (CollectionUtils.isEmpty(custList)) {
			response.setReturnStatus(getError("30550", "No LAN's found for given details"));
			return response;
		}

		response = createFinanceController.getFinanceDetailsByParams(custList, financeData);

		if (response.getFinance() == null || response.getFinance().isEmpty()) {
			response = new FinanceInquiry();
			response.setReturnStatus(getError("30550", "No LAN's found for given details"));
		}

		return response;
	}

	private FinanceInquiry prepareResponseForPAN(String reqParam, FinanceData financeData) {
		List<Customer> custList = customerDocumentService.getCustIdByDocTitle(reqParam);

		if (CollectionUtils.isEmpty(custList)) {
			FinanceInquiry response = new FinanceInquiry();
			response.setReturnStatus(getError("30550", "No LAN's found for given details"));
			return response;
		}

		return createFinanceController.getFinanceDetailsByParams(custList, financeData);
	}

	private static WSReturnStatus getError(String code, String... parms) {
		return APIErrorHandlerService.getFailedStatus(code, parms);
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

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setForeClosureService(ForeClosureService foreClosureService) {
		this.foreClosureService = foreClosureService;
	}

	@Autowired
	public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}

}