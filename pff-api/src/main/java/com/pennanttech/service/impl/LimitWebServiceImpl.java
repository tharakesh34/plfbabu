package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.dao.limit.LimitTransactionDetailsDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.model.limit.LimitTransactionDetail;
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
import com.pennant.validation.LimitSetupGroup;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.UpdateValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennanttech.controller.LimitServiceController;
import com.pennanttech.pffws.LimitRestService;
import com.pennanttech.pffws.LimitSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class LimitWebServiceImpl implements LimitRestService, LimitSoapService {
	private static final Logger logger = Logger.getLogger(LimitWebServiceImpl.class);

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
		LimitHeader response = null;

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

		logger.debug("Leaving");
		return response;
	}

	@Override
	public LimitHeader createLimitSetup(LimitHeader limitHeader) {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(limitHeader, SaveValidationGroup.class);

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
		WSReturnStatus returnStatus = null;
		try {
			limitTransactionDetailDAO.deleteReservedLogs(limitTransDetail.getReferenceNumber());
			
			// validate reserve limit request
			returnStatus = doLimitReserveValidations(limitTransDetail);
			
			if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
				return returnStatus;
			}
			
			// call do Reserve limit method
			returnStatus = limitServiceController.doReserveLimit(limitTransDetail);
			
			logger.debug("Leaving");
			return returnStatus;
		} catch(Exception e) {
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
		
		// validate limit transaction details
		returnStatus = doLimitReserveValidations(limitTransDetail);

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		}
		
		// validate limit reserve amount for cancellation
		String finReference = limitTransDetail.getReferenceNumber();
		String transType = limitTransDetail.getTransactionType();
		long limitId = limitTransDetail.getHeaderId();
		List<LimitTransactionDetail> lmtTransDetails = limitTransactionDetailDAO.getPreviousReservedAmt(finReference,
				transType, limitId);
		BigDecimal prvReserv = LimitManagement.getPreviousReservedAmt(lmtTransDetails);
		if(prvReserv.compareTo(BigDecimal.ZERO) <= 0) {
			return APIErrorHandlerService.getFailedStatus("90340");
		}
		

		// call cancel Reserve limit method
		returnStatus = limitServiceController.cancelReserveLimit(limitTransDetail);

		logger.debug("Leaving");
		return APIErrorHandlerService.getSuccessStatus();
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
		} /*else {
			if(limitTransDetail.getLimitAmount().compareTo(BigDecimal.ZERO)>0){
				String[] valueParm = new String[2];
				valueParm[0] = "Amount";
				valueParm[1] = "0";
				return APIErrorHandlerService.getFailedStatus("91121", valueParm);
			}
		}*/
		LimitHeader limitheader=null;
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
		if(customer.getCustID()!=limitheader.getCustomerId()){
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
			FinanceMain financeMain = financeMainService.getFinanceMainById(finReference, true);
			if (financeMain == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				return APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}
			
			if(!financeMain.isFinIsActive()) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				return APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}
			
			if(financeMain.getCustID() != 0 && financeMain.getCustID() != customer.getCustID()) {
				String[] valueParm = new String[1];
				valueParm[0] = customer.getCustCIF();
				return APIErrorHandlerService.getFailedStatus("90812", valueParm);
			}
			BigDecimal finAmount = PennantApplicationUtil.formateAmount(financeMain.getFinAmount(),
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
				null, null, auditDetail, limitHeader.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
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
}
