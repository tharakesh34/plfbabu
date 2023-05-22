package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.limit.LimitStructureDetailDAO;
import com.pennant.backend.dao.limit.LimitTransactionDetailsDAO;
import com.pennant.backend.dao.rulefactory.impl.LimitRuleDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.backend.model.rulefactory.LimitFilterQuery;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerGroupService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.limit.LimitDetailService;
import com.pennant.backend.service.limit.LimitStructureService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.validation.LimitSetupGroup;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.UpdateValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennant.ws.exception.ServiceExceptionDetails;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.controller.LimitServiceController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pffws.LimitRestService;
import com.pennanttech.pffws.LimitSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class LimitWebServiceImpl extends ExtendedTestClass implements LimitRestService, LimitSoapService {
	private static final Logger logger = LogManager.getLogger(LimitWebServiceImpl.class);

	private LimitServiceController limitServiceController;
	private LimitStructureService limitStructureService;
	private CustomerDetailsService customerDetailsService;
	private CustomerGroupService customerGroupService;
	private LimitDetailService limitDetailService;
	private FinanceMainService financeMainService;
	private CurrencyService currencyService;
	private CommitmentService commitmentService;
	private ValidationUtility validationUtility;
	private LimitTransactionDetailsDAO limitTransactionDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private LimitRuleDAO limitRuleDAO;
	private LimitStructureDetailDAO limitStructureDetailDAO;

	/**
	 * Fetch customer limit structure by structure code.
	 * 
	 * @param structureCode
	 * @return LimitStructure
	 */
	@Override
	public LimitStructure getCustomerLimitStructure(String structureCode) {
		logger.debug("Entering");

		if (StringUtils.isBlank(structureCode)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(structureCode);

		LimitStructure limitStructure = null;

		// validate limit structure code
		int recordCount = limitStructureService.getLimitStructureCountById(structureCode);
		if (recordCount > 0) {
			limitStructure = limitServiceController.getCustomerLimitStructure(structureCode);
		} else {
			limitStructure = new LimitStructure();
			String[] valueParm = new String[1];
			valueParm[0] = structureCode;
			limitStructure.setReturnStatus(getErrorDetails("90801", valueParm));

			return limitStructure;
		}

		logger.debug("Leaving");
		return limitStructure;
	}

	/**
	 * Get Limit setup details by customer id or customer group id
	 * 
	 * @param limitHeader
	 * @return LimitHeader
	 */
	@Override
	public LimitHeader getLimitSetup(LimitHeader limitHeader) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(limitHeader, LimitSetupGroup.class);
		doBasicMandatoryValidations(limitHeader);

		LimitHeader response = null;
		// for logging purpose
		String[] logFields = new String[3];
		logFields[0] = limitHeader.getCustCIF();
		logFields[1] = limitHeader.getCustGrpCode();
		logFields[2] = limitHeader.getLimitStructureCode();
		APIErrorHandlerService.logKeyFields(logFields);

		// validate customer id and customer group id
		WSReturnStatus returnStatus = doCustomerValidations(limitHeader);

		// call get limit setup method
		if (StringUtils.isBlank(returnStatus.getReturnCode())) {
			response = limitServiceController.getLimitSetup(limitHeader);
		} else {
			response = new LimitHeader();
			doEmptyResponseObject(response);
			response.setReturnStatus(returnStatus);
		}
		// for logging purpose
		if (response.getHeaderId() != Long.MIN_VALUE) {
			APIErrorHandlerService.logReference(String.valueOf(response.getHeaderId()));
		}
		logger.debug("Leaving");
		return response;
	}

	@Override
	public LimitHeader getInstitutionLimitSetup(LimitHeader limitHeader) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// for logging purpose
		String[] logFields = new String[3];
		logFields[0] = limitHeader.getRuleCode();
		logFields[1] = String.valueOf(limitHeader.getHeaderId());
		logFields[2] = limitHeader.getLimitStructureCode();
		APIErrorHandlerService.logKeyFields(logFields);

		LimitHeader response = new LimitHeader();

		// validate rule code and limit Structure Code
		WSReturnStatus returnStatus = doInstitutionValidations(limitHeader);
		response.setReturnStatus(returnStatus);

		// call get Institution limit setup method
		if (StringUtils.isBlank(returnStatus.getReturnCode())) {
			response = limitServiceController.getInstitutionLimitSetup(limitHeader.getRuleCode());
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}

		// for logging purpose
		if (response.getHeaderId() != Long.MIN_VALUE) {
			APIErrorHandlerService.logReference(String.valueOf(response.getHeaderId()));
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	private void doBasicMandatoryValidations(LimitHeader limitHeader) {
		ServiceExceptionDetails[] exceptions = new ServiceExceptionDetails[1];

		if ((StringUtils.isNotEmpty(limitHeader.getCustCIF()) && StringUtils.isNotEmpty(limitHeader.getCustGrpCode()))
				|| (StringUtils.isEmpty(limitHeader.getCustCIF())
						&& StringUtils.isEmpty(limitHeader.getCustGrpCode()))) {
			ServiceExceptionDetails error = new ServiceExceptionDetails();
			error.setFaultCode("9009");
			error.setFaultMessage("cif and customerGroup are mutually exclusive");

			exceptions[0] = error;
			throw new ServiceException(exceptions);
		}

	}

	@Override
	public LimitHeader createLimitSetup(LimitHeader limitHeader) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(limitHeader, SaveValidationGroup.class);
		doBasicMandatoryValidations(limitHeader);
		// for logging purpose
		String[] logFields = new String[3];
		logFields[0] = limitHeader.getCustCIF();
		logFields[1] = limitHeader.getCustGrpCode();
		logFields[2] = limitHeader.getLimitStructureCode();
		APIErrorHandlerService.logKeyFields(logFields);

		AuditHeader auditHeader = getAuditHeader(limitHeader, PennantConstants.TRAN_WF);

		LimitHeader response = null;
		limitHeader.setNewRecord(true);
		AuditDetail auditDetail = limitDetailService.doValidations(auditHeader);

		if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				String errorCode = errorDetail.getCode();
				String errorMessage = errorDetail.getError();
				response = new LimitHeader();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorCode, errorMessage));

				return response;
			}
		}

		// call create Limit setup
		response = limitServiceController.createLimitSetup(auditHeader);
		// for logging purpose
		if (response.getHeaderId() != Long.MIN_VALUE) {
			APIErrorHandlerService.logReference(String.valueOf(response.getHeaderId()));
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for update Limit setup details received from API.
	 * 
	 * @param limitHeader
	 * @return WSReturnStatus
	 */
	@Override
	public WSReturnStatus updateLimitSetup(LimitHeader limitHeader) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(limitHeader, UpdateValidationGroup.class);
		doBasicMandatoryValidations(limitHeader);

		// for logging purpose
		String[] logFields = new String[3];
		logFields[0] = limitHeader.getCustCIF();
		logFields[1] = limitHeader.getCustGrpCode();
		logFields[2] = limitHeader.getLimitStructureCode();
		APIErrorHandlerService.logKeyFields(logFields);

		AuditHeader auditHeader = getAuditHeader(limitHeader, PennantConstants.TRAN_WF);

		WSReturnStatus response = null;
		AuditDetail auditDetail = limitDetailService.doValidations(auditHeader);
		if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
			for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
				String errorCode = errorDetail.getCode();
				String errorMessage = errorDetail.getError();
				return APIErrorHandlerService.getFailedStatus(errorCode, errorMessage);
			}
		}

		// call create Limit setup
		response = limitServiceController.updateLimitSetup(auditHeader);
		// for logging purpose
		APIErrorHandlerService.logReference(String.valueOf(limitHeader.getHeaderId()));
		logger.debug("Leaving");
		return response;
	}

	private void doEmptyResponseObject(LimitHeader response) {

	}

	/**
	 * Method for reserve a limit to the finance or commitment
	 * 
	 * @param limitHeader
	 * @return WSReturnStatus
	 */
	@Override
	public WSReturnStatus reserveLimit(LimitTransactionDetail limitTransDetail) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(limitTransDetail, SaveValidationGroup.class);
		// for logging purpose
		String[] logFields = new String[3];
		logFields[0] = limitTransDetail.getCustCIF();
		logFields[1] = limitTransDetail.getCustGrpCode();
		logFields[2] = String.valueOf(limitTransDetail.getLimitAmount());
		APIErrorHandlerService.logKeyFields(logFields);

		WSReturnStatus returnStatus = null;
		try {
			String referenceNumber = limitTransDetail.getReferenceNumber();
			limitTransactionDetailDAO.deleteReservedLogs(referenceNumber);

			// validate reserve limit request
			returnStatus = doLimitReserveValidations(limitTransDetail);

			if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
				return returnStatus;
			}

			// call do Reserve limit method
			returnStatus = limitServiceController.doReserveLimit(limitTransDetail);
			// for logging purpose
			if (StringUtils.equals(returnStatus.getReturnCode(), APIConstants.RES_SUCCESS_CODE)) {
				APIErrorHandlerService.logReference(String.valueOf(limitTransDetail.getHeaderId()));
			}
			logger.debug("Leaving");
			return returnStatus;
		} catch (Exception e) {
			logger.error(e);
			returnStatus = APIErrorHandlerService.getFailedStatus();
		}
		return returnStatus;
	}

	/**
	 * Cancel limit reservation against reference.
	 * 
	 * @param limitTransDetail
	 * @return WSReturnStatus
	 */
	@Override
	public WSReturnStatus cancelLimitReserve(LimitTransactionDetail limitTransDetail) {
		logger.debug("Entering");
		WSReturnStatus returnStatus = null;
		validationUtility.validate(limitTransDetail, SaveValidationGroup.class);
		limitTransDetail.setReferenceCode(LimitConstants.FINANCE);
		// for logging purpose
		String[] logFields = new String[3];
		logFields[0] = limitTransDetail.getCustCIF();
		logFields[1] = limitTransDetail.getCustGrpCode();
		logFields[2] = String.valueOf(limitTransDetail.getLimitAmount());
		APIErrorHandlerService.logKeyFields(logFields);
		// validate limit transaction details
		returnStatus = doLimitReserveValidations(limitTransDetail);

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}

		// for failure case logging purpose
		APIErrorHandlerService.logReference(StringUtils.trimToEmpty(limitTransDetail.getCustGrpCode()));

		// validate limit reserve amount for cancellation
		String finReference = limitTransDetail.getReferenceNumber();
		String transType = limitTransDetail.getTransactionType();
		long limitId = limitTransDetail.getHeaderId();

		Long finID = financeMainDAO.getFinID(finReference);

		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = limitTransDetail.getReferenceNumber();
			FinanceDetail response = new FinanceDetail();
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90201", valueParam);
			response.setReturnStatus(status);
			return status;
		}

		limitTransDetail.setFinID(finID);

		List<LimitTransactionDetail> lmtTransDetails = limitTransactionDetailDAO.getPreviousReservedAmt(finReference,
				transType, limitId);
		BigDecimal prvReserv = LimitManagement.getPreviousReservedAmt(lmtTransDetails);
		if (prvReserv.compareTo(BigDecimal.ZERO) <= 0) {
			return APIErrorHandlerService.getFailedStatus("90340");
		}

		// call cancel Reserve limit method
		returnStatus = limitServiceController.cancelReserveLimit(limitTransDetail);
		// for logging purpose
		if (StringUtils.equals(returnStatus.getReturnCode(), APIConstants.RES_SUCCESS_CODE)) {
			APIErrorHandlerService.logReference(String.valueOf(limitTransDetail.getHeaderId()));
		}
		logger.debug("Leaving");
		return APIErrorHandlerService.getSuccessStatus();
	}

	@Override
	public WSReturnStatus blockLimit(LimitHeader limitHeader) throws ServiceException {
		logger.debug("Entering");
		WSReturnStatus returnStatus = null;

		// for failure case logging purpose
		APIErrorHandlerService.logReference(StringUtils.trimToEmpty(limitHeader.getCustCIF()));
		returnStatus = blockLimitValidation(limitHeader);
		if (returnStatus != null && StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}
		// call cancel Reserve limit method
		returnStatus = limitServiceController.doBlockLimit(limitHeader, true);
		logger.debug("Leaving");
		return returnStatus;
	}

	private WSReturnStatus blockLimitValidation(LimitHeader limitHeader) {
		logger.debug("Entering");
		WSReturnStatus returnStatus = null;
		if (StringUtils.isBlank(limitHeader.getCustCIF())) {
			String[] valueParm = new String[1];
			valueParm[0] = "cif";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		} else {
			// validate customer CIF
			Customer customer = null;
			String custCIF = limitHeader.getCustCIF();
			if (StringUtils.isNotBlank(custCIF)) {
				customer = customerDetailsService.getCustomerByCIF(custCIF);
				if (customer == null) {
					String[] valueParm = new String[1];
					valueParm[0] = custCIF;
					return getErrorDetails("90101", valueParm);
				} else {
					limitHeader.setCustomerId(customer.getCustID());
				}
			}
		}
		if (limitHeader.getHeaderId() <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "LimitId";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		} else {
			LimitHeader limitheader = limitDetailService.getCustomerLimitsById(limitHeader.getHeaderId());
			if (limitheader == null || !limitheader.isActive()) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(limitHeader.getHeaderId());
				return getErrorDetails("90807", valueParm);
			}

			if (limitHeader.getCustomerId() != limitheader.getCustomerId()) {
				String[] valueParm = new String[2];
				valueParm[0] = limitHeader.getCustCIF();
				valueParm[1] = String.valueOf(limitHeader.getHeaderId());
				return APIErrorHandlerService.getFailedStatus("90341", valueParm);
			}
		}
		logger.debug("Leaving");
		return returnStatus;
	}

	@Override
	public WSReturnStatus unBlockLimit(LimitHeader limitHeader) throws ServiceException {
		logger.debug("Entering");
		WSReturnStatus returnStatus = null;

		// for failure case logging purpose
		APIErrorHandlerService.logReference(StringUtils.trimToEmpty(limitHeader.getCustCIF()));
		returnStatus = blockLimitValidation(limitHeader);
		if (returnStatus != null && StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}
		// call cancel Reserve limit method
		returnStatus = limitServiceController.doBlockLimit(limitHeader, false);
		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Validate Limit header fields
	 * 
	 * @param limitHeader
	 * @return
	 */
	private WSReturnStatus doLimitReserveValidations(LimitTransactionDetail limitTransDetail) {
		logger.debug("Enrtering");

		// validate amount
		if (limitTransDetail.getLimitAmount() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "Amount";
			return APIErrorHandlerService.getFailedStatus("90242", valueParm);
		} /*
			 * else { if(limitTransDetail.getLimitAmount().compareTo(BigDecimal.ZERO)>0){ String[] valueParm = new
			 * String[2]; valueParm[0] = "Amount"; valueParm[1] = "0"; return
			 * APIErrorHandlerService.getFailedStatus("91121", valueParm); } }
			 */
		LimitHeader limitheader = null;
		// validate limitId
		if (limitTransDetail.getHeaderId() != Long.MIN_VALUE) {
			limitheader = limitDetailService.getCustomerLimitsById(limitTransDetail.getHeaderId());
			if (limitheader == null || !limitheader.isActive()) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(limitTransDetail.getHeaderId());
				return getErrorDetails("90807", valueParm);
			}
		}

		// validate customer CIF
		Customer customer = null;
		String custCIF = limitTransDetail.getCustCIF();
		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custCIF;
				return getErrorDetails("90101", valueParm);
			}
		}
		if (customer.getCustID() != limitheader.getCustomerId()) {
			String[] valueParm = new String[2];
			valueParm[0] = customer.getCustCIF();
			valueParm[1] = String.valueOf(limitTransDetail.getHeaderId());
			return APIErrorHandlerService.getFailedStatus("90341", valueParm);
		}
		// validate customer group code
		String custGrpCode = limitTransDetail.getCustGrpCode();
		if (StringUtils.isNotBlank(custGrpCode)) {
			CustomerGroup customerGroup = customerGroupService.getCustomerGroupByCode(custGrpCode);
			if (customerGroup == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custGrpCode;
				return getErrorDetails("90107", valueParm);
			}
		}
		// validate currency code
		if (StringUtils.isNotBlank(limitTransDetail.getLimitCurrency())) {
			Currency currency = currencyService.getCurrencyById(limitTransDetail.getLimitCurrency());
			if (currency == null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Limit Currency";
				valueParm[1] = limitTransDetail.getLimitCurrency();
				return APIErrorHandlerService.getFailedStatus("90701", valueParm);
			}
		}
		// validate referenceCode
		String referenceCode = limitTransDetail.getReferenceCode();
		switch (referenceCode) {
		case LimitConstants.FINANCE:
			// validate limit Reference number
			String finReference = limitTransDetail.getReferenceNumber();
			FinanceMain fm = financeMainService.getFinanceMain(finReference, TableType.MAIN_TAB);

			if (fm == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				return APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}

			limitTransDetail.setFinID(fm.getFinID());
			if (!fm.isFinIsActive()) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				return APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}

			if (fm.getCustID() != 0 && fm.getCustID() != customer.getCustID()) {
				String[] valueParm = new String[1];
				valueParm[0] = customer.getCustCIF();
				return APIErrorHandlerService.getFailedStatus("90812", valueParm);
			}
			BigDecimal finAmount = PennantApplicationUtil.formateAmount(fm.getFinAmount(),
					CurrencyUtil.getFormat(limitTransDetail.getLimitCurrency()));
			if (finAmount.compareTo(limitTransDetail.getLimitAmount()) != 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "amount";
				valueParm[1] = "FinanceAmont: " + finAmount;
				return APIErrorHandlerService.getFailedStatus("90277", valueParm);
			}
			break;
		case LimitConstants.COMMITMENT:
			String cmtReference = limitTransDetail.getReferenceNumber();
			int cmtCount = commitmentService.getCommitmentCountById(cmtReference);
			if (cmtCount <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = cmtReference;
				return APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}
			break;
		default:
			String[] valueParm = new String[1];
			valueParm[0] = referenceCode;
			return APIErrorHandlerService.getFailedStatus("90804", valueParm);
		}

		logger.debug("Enrtering");

		return new WSReturnStatus();
	}

	private WSReturnStatus doCustomerValidations(LimitHeader limitHeader) {
		logger.debug("Entering");

		String custCIF = limitHeader.getCustCIF();

		// validate customer CIF
		if (StringUtils.isNotBlank(custCIF)) {
			Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custCIF;
				return getErrorDetails("90101", valueParm);
			}

			limitHeader.setCustomerId(customer.getCustID());
		}

		String custGrpCode = limitHeader.getCustGrpCode();

		// validate customer group code
		if (StringUtils.isNotBlank(custGrpCode)) {
			CustomerGroup customerGroup = customerGroupService.getCustomerGroupByCode(custGrpCode);
			if (customerGroup == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custGrpCode;
				return getErrorDetails("90107", valueParm);
			}

			limitHeader.setCustomerGroup(customerGroup.getCustGrpID());
		}

		logger.debug("Leaving");
		return new WSReturnStatus();
	}

	private WSReturnStatus doInstitutionValidations(LimitHeader limitHeader) {
		logger.info(Literal.ENTERING);

		String code = limitHeader.getRuleCode();
		String strucCode = limitHeader.getLimitStructureCode();

		if (StringUtils.isBlank(code)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Rule Code";
			return getErrorDetails("90502", valueParm);
		}

		if (StringUtils.isBlank(strucCode)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Structure Code";
			return getErrorDetails("90502", valueParm);
		}

		LimitFilterQuery filters = limitRuleDAO.getLimitRuleByQueryCode(code, RuleConstants.MODULE_IRLFILTER, "_AView");
		if (filters == null) {
			String[] valueParm = new String[4];
			valueParm[0] = "Rule Code: " + code;
			valueParm[1] = "is";
			valueParm[2] = "Not Valid";
			valueParm[3] = "";
			return getErrorDetails("30550", valueParm);
		}

		limitHeader.setRuleCode(filters.getQueryCode());

		// validate Limit Structure code
		if (limitStructureDetailDAO.getLimitStructureCountById(strucCode, "") <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = strucCode;
			return getErrorDetails("90107", valueParm);
		}

		logger.info(Literal.LEAVING);
		return new WSReturnStatus();
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerDetails
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(LimitHeader limitHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, limitHeader.getBefImage(), limitHeader);
		return new AuditHeader(String.valueOf(limitHeader.getHeaderId()), String.valueOf(limitHeader.getHeaderId()),
				null, null, auditDetail, limitHeader.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
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
	public void setLimitServiceController(LimitServiceController limitServiceController) {
		this.limitServiceController = limitServiceController;
	}

	@Autowired
	public void setLimitStructureService(LimitStructureService limitStructureService) {
		this.limitStructureService = limitStructureService;
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
	public void setCustomerGroupService(CustomerGroupService customerGroupService) {
		this.customerGroupService = customerGroupService;
	}

	@Autowired
	public void setLimitDetailService(LimitDetailService limitDetailService) {
		this.limitDetailService = limitDetailService;
	}

	@Autowired
	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	@Autowired
	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	@Autowired
	public void setCommitmentService(CommitmentService commitmentService) {
		this.commitmentService = commitmentService;
	}

	@Autowired
	public void setLimitTransactionDetailDAO(LimitTransactionDetailsDAO limitTransactionDetailDAO) {
		this.limitTransactionDetailDAO = limitTransactionDetailDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setLimitRuleDAO(LimitRuleDAO limitRuleDAO) {
		this.limitRuleDAO = limitRuleDAO;
	}

	@Autowired
	public void setLimitStructureDetailDAO(LimitStructureDetailDAO limitStructureDetailDAO) {
		this.limitStructureDetailDAO = limitStructureDetailDAO;
	}

}
