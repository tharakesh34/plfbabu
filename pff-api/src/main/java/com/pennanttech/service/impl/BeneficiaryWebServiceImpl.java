package com.pennanttech.service.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.beneficiary.BeneficiaryService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.UpdateValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennant.ws.exception.ServiceExceptionDetails;
import com.pennanttech.controller.BeneficiaryController;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pffws.BeneficiaryRestService;
import com.pennanttech.pffws.BeneficiarySoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.beneficiary.BeneficiaryDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class BeneficiaryWebServiceImpl extends ExtendedTestClass
		implements BeneficiarySoapService, BeneficiaryRestService {
	private final Logger logger = LogManager.getLogger(getClass());

	private BeneficiaryController beneficiaryController;
	private ValidationUtility validationUtility;
	private CustomerDetailsService customerDetailsService;
	private BankBranchService bankBranchService;
	private BeneficiaryService beneficiaryService;

	/**
	 * Method for create Beneficiary in PLF system.
	 * 
	 * @param beneficiary
	 * @throws ServiceException
	 */
	@Override
	public Beneficiary createBeneficiary(Beneficiary beneficiary) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(beneficiary, SaveValidationGroup.class);
		doBasicMandatoryValidations(beneficiary);

		WSReturnStatus returnStatus = doBeneficiaryValidation(beneficiary);

		Beneficiary response = null;
		if (StringUtils.isBlank(returnStatus.getReturnCode())) {
			response = beneficiaryController.createBeneficiary(beneficiary);
		} else {
			response = new Beneficiary();
			response.setReturnStatus(returnStatus);
		}
		// for Logging Purpose
		String[] logFields = new String[1];
		logFields[0] = String.valueOf(beneficiary.getBeneficiaryId());
		APIErrorHandlerService.logKeyFields(logFields);
		APIErrorHandlerService.logReference(beneficiary.getCustCIF());
		logger.debug("Leaving");
		return response;
	}

	private void doBasicMandatoryValidations(Beneficiary beneficiary) {
		if (StringUtils.isNotEmpty(beneficiary.getiFSC())) {
			//
		} else if (StringUtils.isNotEmpty(beneficiary.getBankCode())
				&& StringUtils.isNotEmpty(beneficiary.getBranchCode())) {
			//
		} else {
			ServiceExceptionDetails exception = new ServiceExceptionDetails();

			exception.setFaultCode("9009");
			exception.setFaultMessage("specify any one of BankCode and BranchName Or ifsc code");

			ServiceExceptionDetails exceptions[] = new ServiceExceptionDetails[1];
			exceptions[0] = exception;
			throw new ServiceException(exceptions);
		}
	}

	/**
	 * get the Beneficiary Details by the given beneficiaryId.
	 * 
	 * @param beneficiaryId
	 * @return Beneficiary
	 * @throws ServiceException
	 */
	@Override
	public Beneficiary getBeneficiary(long beneficiaryId) throws ServiceException {
		logger.debug("Entering");
		// Mandatory validation
		if (beneficiaryId < 0) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(String.valueOf(beneficiaryId));
		Beneficiary response = beneficiaryController.getBeneficiary(beneficiaryId);
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for update Beneficiary in PLF system.
	 * 
	 * @param beneficiary
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateBeneficiary(Beneficiary beneficiary) throws ServiceException {
		logger.debug("Entering");
		// for logging purpose
		String[] logFields = new String[1];
		logFields[0] = beneficiary.getCustCIF();
		APIErrorHandlerService.logKeyFields(logFields);
		APIErrorHandlerService.logReference(String.valueOf(beneficiary.getBeneficiaryId()));
		// beanValidation
		validationUtility.validate(beneficiary, UpdateValidationGroup.class);
		doBasicMandatoryValidations(beneficiary);

		Beneficiary beneficiaryDetails = beneficiaryService.getApprovedBeneficiaryById(beneficiary.getBeneficiaryId());

		WSReturnStatus returnStatus = null;
		if (beneficiaryDetails != null
				&& StringUtils.equals(beneficiary.getCustCIF(), beneficiaryDetails.getCustCIF())) {
			returnStatus = doBeneficiaryValidation(beneficiary);
			if (StringUtils.isBlank(returnStatus.getReturnCode())) {
				returnStatus = beneficiaryController.updateBeneficiary(beneficiary);
			} else {
				return returnStatus;
			}
		} else {
			returnStatus = new WSReturnStatus();
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(beneficiary.getBeneficiaryId());
			returnStatus = APIErrorHandlerService.getFailedStatus("90601", valueParm);
		}

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * delete the Beneficiary by the given beneficiaryId.
	 * 
	 * @param beneficiaryId
	 * @return WSReturnStatus
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus deleteBeneficiary(long beneficiaryId) throws ServiceException {
		logger.debug("Entering");
		// Mandatory validation
		if (beneficiaryId < 0) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(String.valueOf(beneficiaryId));
		// Mandate Id is Available or not in PLF
		WSReturnStatus response = new WSReturnStatus();
		Beneficiary beneficiary = beneficiaryService.getApprovedBeneficiaryById(beneficiaryId);
		if (beneficiary != null) {
			response = beneficiaryController.deleteBeneficiary(beneficiaryId);
		} else {

			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(beneficiaryId);
			response = APIErrorHandlerService.getFailedStatus("90601", valueParm);
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * get the Beneficiary Details by the given cif.
	 * 
	 * @param cif
	 * @return BeneficiaryDetails
	 * @throws ServiceException
	 */
	@Override
	public BeneficiaryDetail getBeneficiaries(String cif) throws ServiceException {
		logger.debug("Entering");
		// Mandatory validation
		if (StringUtils.isBlank(cif)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		BeneficiaryDetail response = new BeneficiaryDetail();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = cif;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
		} else {
			response = beneficiaryController.getBeneficiaries(cif);
		}
		logger.debug("Leaving");

		return response;
	}

	/**
	 * Validate the mandatory fields in the request object
	 * 
	 * @param beneficiary
	 * @return returnStatus
	 */
	private WSReturnStatus doBeneficiaryValidation(Beneficiary beneficiary) {
		logger.debug("Entering");

		WSReturnStatus returnStatus = new WSReturnStatus();
		// validate customer
		String custCIF = beneficiary.getCustCIF();
		if (StringUtils.isNotBlank(custCIF)) {
			Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custCIF;
				return getErrorDetails("90101", valueParm);
			}
		}

		String ifsc = beneficiary.getiFSC();
		String micr = beneficiary.getMicr();
		String bankCode = beneficiary.getBankCode();
		String branchCode = beneficiary.getBranchCode();

		BankBranch bankBranch = bankBranchService.getBankBranch(ifsc, micr, bankCode, branchCode);

		if (bankBranch.getError() != null) {
			WSReturnStatus status = new WSReturnStatus();
			status.setReturnCode(bankBranch.getError().getCode());
			status.setReturnText("");

			ErrorDetail ed = ErrorUtil.getErrorDetailById(bankBranch.getError().getCode());

			if (ed != null) {
				status.setReturnText(ErrorUtil.getErrorMessage(ed.getMessage(), bankBranch.getError().getParameters()));
			}

			return status;
		}

		beneficiary.setBankCode(bankBranch.getBankCode());
		beneficiary.setBankBranchID(bankBranch.getBankBranchID());

		// validate Phone number
		String mobileNumber = beneficiary.getPhoneNumber();
		if (StringUtils.isNotBlank(mobileNumber)) {
			if (!(mobileNumber.matches("\\d{10}"))) {
				return getErrorDetails("90278", null);
			}
		}
		// validate AccNumber length
		/*
		 * if(StringUtils.isNotBlank(beneficiary.getBankCode())){ int accNoLength =
		 * bankDetailService.getAccNoLengthByCode(beneficiary.getBankCode());
		 * if(beneficiary.getAccNumber().length()!=accNoLength){ String[] valueParm = new String[2]; valueParm[0] =
		 * "AccountNumber"; valueParm[1] = String.valueOf(accNoLength)+" characters"; return getErrorDetails("30570",
		 * valueParm); } }
		 */

		if (StringUtils.isBlank(beneficiary.getAccHolderName())) {
			String[] valueParm = new String[1];
			valueParm[0] = beneficiary.getAccHolderName();
			return getErrorDetails("90502", valueParm);
		}

		Pattern pattern = Pattern
				.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ACCOUNT_HOLDER_NAME));

		Matcher matcher = pattern.matcher(beneficiary.getAccHolderName());

		if (!matcher.matches()) {
			String[] valueParm = new String[1];
			valueParm[0] = beneficiary.getAccHolderName();
			return getErrorDetails("90237", valueParm);
		}

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Method for prepare response object with errorDetails.
	 * 
	 * @param errorCode
	 * @param valueParm
	 * @return
	 */
	public WSReturnStatus getErrorDetails(String errorCode, String[] valueParm) {
		logger.debug("Entering");

		WSReturnStatus response = new WSReturnStatus();
		response = APIErrorHandlerService.getFailedStatus(errorCode, valueParm);

		// set default error code and description in case of Error code does not exists.
		if (StringUtils.isBlank(response.getReturnCode())) {
			response = APIErrorHandlerService.getFailedStatus(APIConstants.RES_FAILED_CODE,
					APIConstants.RES_FAILED_DESC);
		}

		logger.debug("Leaving");
		return response;
	}

	@Autowired
	public void setBeneficiaryController(BeneficiaryController beneficiaryController) {
		this.beneficiaryController = beneficiaryController;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	@Autowired
	public void setBeneficiaryService(BeneficiaryService beneficiaryService) {
		this.beneficiaryService = beneficiaryService;
	}

}
