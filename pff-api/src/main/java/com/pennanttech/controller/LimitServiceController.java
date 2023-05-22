package com.pennanttech.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.limit.LimitStructureDetailDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerGroupService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.limit.LimitDetailService;
import com.pennant.backend.service.limit.LimitStructureService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class LimitServiceController extends ExtendedTestClass {
	private static Logger logger = LogManager.getLogger(LimitServiceController.class);

	private LimitStructureService limitStructureService;
	private LimitDetailService limitDetailService;
	private LimitStructureDetailDAO limitStructureDetailDAO;
	private CustomerDetailsService customerDetailsService;
	private CustomerGroupService customerGroupService;
	private FinanceMainService financeMainService;
	private FinanceTypeService financeTypeService;
	private LimitManagement limitManagement;
	private FinanceDetailService financeDetailService;
	private LimitHeaderDAO limitHeaderDAO;
	private LimitDetailDAO limitDetailDAO;

	public LimitServiceController() {
	    super();
	}

	private final String PROCESS_TYPE_SAVE = "Save";
	private final String PROCESS_TYPE_UPDATE = "Update";

	/**
	 * Fetch customer limit structure by structure code.
	 * 
	 * @param structureCode
	 * @return LimitStructure
	 */
	public LimitStructure getCustomerLimitStructure(String structureCode) {
		logger.debug("Entering");

		LimitStructure limitStructure = null;
		try {
			limitStructure = limitStructureService.getApprovedLimitStructureById(structureCode);
			if (limitStructure != null) {
				// set return status as success
				limitStructure.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				// set return status as Failed
				limitStructure = new LimitStructure();
				limitStructure.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			limitStructure = new LimitStructure();
			limitStructure.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return limitStructure;
	}

	/**
	 * Method for fetch Limit setup details
	 * 
	 * @param limitHeader
	 * @return
	 */
	public LimitHeader getLimitSetup(LimitHeader limitHeader) {
		logger.debug("Entering");

		LimitHeader headerDetail = null;
		boolean isCustCIF = false;
		try {
			// fetch limit header details by using either custId or Customer group id.
			if (StringUtils.isNotBlank(limitHeader.getCustCIF())) {
				isCustCIF = true;
				headerDetail = limitDetailService.getLimitHeaderByCustomer(limitHeader.getCustomerId());
			} else {
				headerDetail = limitDetailService.getLimitHeaderByCustomerGroupCode(limitHeader.getCustomerGroup());
			}

			// fetch limit structure details
			if (headerDetail != null && headerDetail.getCustomerLimitDetailsList() != null) {
				for (LimitDetails detail : headerDetail.getCustomerLimitDetailsList()) {
					long limitStructureId = detail.getLimitStructureDetailsID();
					detail.setLimitReservedexposure(PennantApplicationUtil.formateAmount(detail.getReservedexposure(),
							CurrencyUtil.getFormat(headerDetail.getLimitCcy())));
					detail.setLimitActualexposure(PennantApplicationUtil.formateAmount(detail.getActualexposure(),
							CurrencyUtil.getFormat(headerDetail.getLimitCcy())));
					detail.setReservedLimit(PennantApplicationUtil.formateAmount(detail.getReservedLimit(),
							CurrencyUtil.getFormat(headerDetail.getLimitCcy())));
					detail.setActualLimit(PennantApplicationUtil.formateAmount(
							detail.getLimitSanctioned().subtract(detail.getUtilisedLimit()),
							CurrencyUtil.getFormat(headerDetail.getLimitCcy())));
					LimitStructureDetail limitStrucDetail = limitStructureDetailDAO
							.getLimitStructureDetail(limitStructureId, "_AView");
					detail.setLimitStructureDetails(limitStrucDetail);
					detail.setLimitSanctioned(PennantApplicationUtil.formateAmount(detail.getLimitSanctioned(),
							CurrencyUtil.getFormat(headerDetail.getLimitCcy())));
				}
			}

			if (headerDetail != null) {
				headerDetail.setCustFullName(PennantApplicationUtil.getFullName(headerDetail.getCustFName(),
						headerDetail.getCustMName(), headerDetail.getCustFullName()));
				if (isCustCIF) {
					headerDetail.setCustomerName(headerDetail.getCustFullName());
				} else {
					headerDetail.setCustomerName(headerDetail.getGroupName());
				}
				headerDetail.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				String[] valueParam = new String[1];
				valueParam[0] = limitHeader.getCustGrpCode();
				if (isCustCIF) {
					valueParam[0] = limitHeader.getCustCIF();
				}
				headerDetail = new LimitHeader();
				headerDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90802", valueParam));
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			headerDetail = new LimitHeader();
			headerDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return headerDetail;
	}

	public LimitHeader getInstitutionLimitSetup(String ruleCode) {
		logger.info(Literal.ENTERING);

		LimitHeader lh = limitHeaderDAO.getLimitHeaderByRule(ruleCode, LimitConstants.LIMIT_CATEGORY_BANK, "");

		if (lh == null) {
			return new LimitHeader();
		}

		int format = CurrencyUtil.getFormat(lh.getLimitCcy());
		List<LimitDetails> limitDetails = limitDetailDAO.getLimitDetailsByHeaderId(lh.getHeaderId(), "_AView");

		for (LimitDetails ld : limitDetails) {
			long sID = ld.getLimitStructureDetailsID();
			BigDecimal limitBalance = ld.getLimitSanctioned().subtract(ld.getUtilisedLimit());

			ld.setLimitReservedexposure(PennantApplicationUtil.formateAmount(ld.getReservedexposure(), format));
			ld.setLimitActualexposure(PennantApplicationUtil.formateAmount(ld.getActualexposure(), format));
			ld.setReservedLimit(PennantApplicationUtil.formateAmount(ld.getReservedLimit(), format));
			ld.setActualLimit(PennantApplicationUtil.formateAmount(limitBalance, format));
			ld.setLimitStructureDetails(limitStructureDetailDAO.getLimitStructureDetail(sID, "_AView"));
			ld.setLimitSanctioned(PennantApplicationUtil.formateAmount(ld.getLimitSanctioned(), format));
		}

		lh.setInstitutionLimitDetailsList(limitDetails);

		logger.info(Literal.LEAVING);
		return lh;
	}

	/**
	 * Method for create Limit setup
	 * 
	 * @param auditHeader
	 * @return
	 */
	public LimitHeader createLimitSetup(AuditHeader auditHeader) {
		logger.debug("Entering");

		// data preparation
		doPffDataPreparation(auditHeader, PROCESS_TYPE_SAVE);

		LimitHeader response = null;
		try {
			// get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);

			// set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);

			// call create limit setup method
			AuditHeader header = limitDetailService.doApprove(auditHeader, false);

			if (header.getErrorMessage() != null && !header.getErrorMessage().isEmpty()) {
				for (ErrorDetail errorDetail : header.getErrorMessage()) {
					response = new LimitHeader();
					String errorCode = errorDetail.getCode();
					String errorMessage = errorDetail.getMessage();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorCode, errorMessage));
				}
			} else {
				response = (LimitHeader) header.getAuditDetail().getModelData();
				LimitHeader limitHeader = new LimitHeader();
				limitHeader.setHeaderId(response.getHeaderId());
				response = limitHeader;
				response.setActive(true);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new LimitHeader();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for update limit set up details
	 * 
	 * @param auditHeader
	 * @return
	 */
	public WSReturnStatus updateLimitSetup(AuditHeader auditHeader) {
		logger.debug("Entering");

		// data preparation
		doPffDataPreparation(auditHeader, PROCESS_TYPE_UPDATE);

		WSReturnStatus returnStatus = null;
		try {
			// get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			// set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			// call create limit setup method
			AuditHeader header = limitDetailService.doApprove(auditHeader, false);

			if (header.getErrorMessage() != null && !header.getErrorMessage().isEmpty()) {
				for (ErrorDetail errorDetail : header.getErrorMessage()) {
					String errorCode = errorDetail.getCode();
					String errorMessage = errorDetail.getMessage();
					returnStatus = APIErrorHandlerService.getFailedStatus(errorCode, errorMessage);
				}
			} else {
				returnStatus = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			returnStatus = APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Method for reserve a limit for finance or commitment
	 * 
	 * @param limitHeader
	 * @return
	 */
	public WSReturnStatus doReserveLimit(LimitTransactionDetail limitTransDetail) {
		WSReturnStatus status = new WSReturnStatus();
		if (StringUtils.equals(limitTransDetail.getReferenceCode(), LimitConstants.FINANCE)) {
			status = processLimits(limitTransDetail, LimitConstants.BLOCK);
		} else {
			logger.info("Received Other than finance Reference Code:" + limitTransDetail.getReferenceCode());
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return status;
	}

	/**
	 * Process cancel reserve limit request
	 * 
	 * @param limitTransDetail
	 * @return
	 */
	public WSReturnStatus cancelReserveLimit(LimitTransactionDetail limitTransDetail) {
		return processLimits(limitTransDetail, LimitConstants.UNBLOCK);
	}

	public WSReturnStatus doBlockLimit(LimitHeader limitHeader, boolean blockLimt) {
		logger.debug("Entering");
		WSReturnStatus returnStatus = null;
		int count = 0;
		try {
			count = limitHeaderDAO.updateBlockLimit(limitHeader.getCustomerId(), limitHeader.getHeaderId(), blockLimt);
			if (count > 0) {
				returnStatus = APIErrorHandlerService.getSuccessStatus();
			} else {
				returnStatus = APIErrorHandlerService.getFailedStatus();
			}
		} catch (Exception e) {
			returnStatus = APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Method for process Reserve and cancel reserve limits
	 * 
	 * @param limitTransDetail
	 * @param lmtTransType
	 * @return WSReturnStatus
	 */
	private WSReturnStatus processLimits(LimitTransactionDetail limitTransDetail, String lmtTransType) {
		logger.debug("Entering");

		FinanceDetail financeDetail = doPrepareLimitTransData(limitTransDetail);
		if (financeDetail.getReturnStatus() != null) {
			return financeDetail.getReturnStatus();
		}
		List<ErrorDetail> errorDetails = null;
		try {
			// process limits
			errorDetails = limitManagement.processLoanLimitOrgination(financeDetail, false, lmtTransType, false);
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		if (errorDetails != null && !errorDetails.isEmpty()) {
			for (ErrorDetail errorDetail : errorDetails) {
				String errorCode = errorDetail.getCode();
				String errorMessage = errorDetail.getError();
				return APIErrorHandlerService.getFailedStatus(errorCode, errorMessage);
			}
		}
		logger.debug("Leaving");
		return APIErrorHandlerService.getSuccessStatus();
	}

	/**
	 * Method for prepare internal data which is required for PFF to process API request.
	 * 
	 * @param auditHeader
	 * @param processType
	 */
	private void doPffDataPreparation(AuditHeader auditHeader, String processType) {
		logger.debug("Entering");

		// process limit header
		LimitHeader limitHeader = (LimitHeader) auditHeader.getAuditDetail().getModelData();

		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		limitHeader.setUserDetails(userDetails);

		// fetch customer id and group id
		String custCIF = limitHeader.getCustCIF();
		if (StringUtils.isNotBlank(custCIF)) {
			Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
			if (customer != null) {
				limitHeader.setCustomerId(customer.getCustID());
			}
		}

		// validate customer group code
		String custGrpCode = limitHeader.getCustGrpCode();
		if (StringUtils.isNotBlank(custGrpCode)) {
			CustomerGroup customerGroup = customerGroupService.getCustomerGroupByCode(custGrpCode);
			if (customerGroup != null) {
				limitHeader.setCustomerGroup(customerGroup.getCustGrpID());
			}
		}

		if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
			limitHeader.setNewRecord(true);
			limitHeader.setLastMntBy(userDetails.getUserId());
			limitHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			limitHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			limitHeader.setCreatedOn(new Timestamp(new Date().getTime()));
			limitHeader.setLastMntOn(new Timestamp(new Date().getTime()));
		}

		// set customer branch
		Customer customer = customerDetailsService.getCustomerByCIF(limitHeader.getCustCIF());
		if (customer != null) {
			limitHeader.setResponsibleBranch(customer.getCustDftBranch());
		}

		// process limit details
		List<LimitDetails> limitDetails = limitHeader.getCustomerLimitDetailsList();
		for (LimitDetails detail : limitDetails) {
			if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
				detail.setLastMntBy(userDetails.getUserId());
				detail.setNewRecord(true);
				detail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				detail.setLastMntOn(new Timestamp(new Date().getTime()));
				detail.setUserDetails(userDetails);
				detail.setLimitSanctioned(PennantApplicationUtil.unFormateAmount(detail.getLimitSanctioned(),
						CurrencyUtil.getFormat(limitHeader.getLimitCcy())));
				detail.setRevolving(detail.isRevolving());
			}
		}

		// get latest limit header details
		LimitHeader prvLimitHeader = null;
		if (StringUtils.equals(processType, PROCESS_TYPE_UPDATE)) {
			prvLimitHeader = limitDetailService.getLimitHeaderById(limitHeader.getHeaderId());
			if (prvLimitHeader != null) {
				prvLimitHeader.setCustFullName(PennantApplicationUtil.getFullName(prvLimitHeader.getCustFName(),
						prvLimitHeader.getCustMName(), prvLimitHeader.getCustFullName()));
				prvLimitHeader.setVersion(prvLimitHeader.getVersion() + 1);
				prvLimitHeader.setCustCIF(limitHeader.getCustCIF());
				prvLimitHeader.setCustomerId(limitHeader.getCustomerId());
				prvLimitHeader.setCustGrpCode(limitHeader.getCustGrpCode());
				prvLimitHeader.setCustomerGroup(limitHeader.getCustomerGroup());

				if (limitHeader.getLimitExpiryDate() != null) {
					prvLimitHeader.setLimitExpiryDate(limitHeader.getLimitExpiryDate());
				}
				if (limitHeader.getLimitRvwDate() != null) {
					prvLimitHeader.setLimitRvwDate(limitHeader.getLimitRvwDate());
				}

				if (StringUtils.isNotBlank(limitHeader.getLimitSetupRemarks())) {
					prvLimitHeader.setLimitSetupRemarks(limitHeader.getLimitSetupRemarks());
				}

				prvLimitHeader.setActive(limitHeader.isActive());
				prvLimitHeader.setValidateMaturityDate(limitHeader.isValidateMaturityDate());

				prvLimitHeader.setNewRecord(false);
				prvLimitHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);

				// process limit details
				List<LimitDetails> prvLimitDetails = prvLimitHeader.getCustomerLimitDetailsList();
				List<LimitDetails> currentLimitDetails = limitHeader.getCustomerLimitDetailsList();
				for (LimitDetails prvDetail : prvLimitDetails) {
					for (LimitDetails curDetail : currentLimitDetails) {
						if (prvDetail.getLimitStructureDetailsID() == curDetail.getLimitStructureDetailsID()) {

							/*
							 * // expiry date if (curDetail.getExpiryDate() != null) {
							 * prvDetail.setExpiryDate(curDetail.getExpiryDate() ); } // limit check
							 * prvDetail.setLimitCheck(curDetail.isLimitCheck()) ;
							 * 
							 * // limit check method if (StringUtils.isNotBlank(curDetail. getLimitChkMethod())) {
							 * prvDetail.setLimitChkMethod(curDetail. getLimitChkMethod()); }
							 * 
							 * if (!curDetail.getLimitSanctioned().equals( BigDecimal.ZERO)) {
							 * prvDetail.setLimitSanctioned(curDetail. getLimitSanctioned()); }
							 */
							curDetail.setDetailId(prvDetail.getDetailId());
							curDetail.setNewRecord(false);
							curDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
							curDetail.setVersion(prvDetail.getVersion() + 1);
						}
					}
				}
				for (LimitDetails detail : limitHeader.getCustomerLimitDetailsList()) {
					for (LimitDetails prvDetail : prvLimitHeader.getCustomerLimitDetailsList()) {
						if (detail.getLimitStructureDetailsID() == prvDetail.getLimitStructureDetailsID()) {
							prvDetail.setLastMntBy(userDetails.getUserId());
							prvDetail.setNewRecord(false);
							prvDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
							prvDetail.setLastMntOn(new Timestamp(new Date().getTime()));
							prvDetail.setUserDetails(userDetails);
							prvDetail.setLimitCheck(detail.isLimitCheck());
							prvDetail.setVersion(prvDetail.getVersion() + 1);
							prvDetail.setLimitSanctioned(PennantApplicationUtil.unFormateAmount(
									detail.getLimitSanctioned(), CurrencyUtil.getFormat(limitHeader.getLimitCcy())));
							prvDetail.setRevolving(detail.isRevolving());
						}
					}
				}
			}

			limitHeader = prvLimitHeader;
			auditHeader.getAuditDetail().setModelData(prvLimitHeader);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for process limit transaction details and prepare data for below oprerations.<br>
	 * - Reserve Limit<br>
	 * - Cancel Reserve Limit.
	 * 
	 * @param limitTransDetail
	 * @return
	 */
	private FinanceDetail doPrepareLimitTransData(LimitTransactionDetail limitTransDetail) {
		FinanceMain financeMain = financeMainService.getFinanceMainById(limitTransDetail.getFinID(), false);
		Customer customer = customerDetailsService.getCustomerByCIF(limitTransDetail.getCustCIF());
		// user details
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		List<FinanceDisbursement> finDisbursements = new ArrayList<FinanceDisbursement>(1);

		if (financeMain == null) {
			String[] valueParam = new String[1];
			FinanceDetail fd = new FinanceDetail();
			valueParam[0] = limitTransDetail.getReferenceNumber();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParam));
			return fd;
		}

		FinanceType financeType = financeTypeService.getApprovedFinanceTypeById(financeMain.getFinType());
		financeMain.setUserDetails(userDetails);
		finDisbursements = financeDetailService.getFinanceDisbursements(financeMain.getFinID(), "", false);

		if (customer != null) {
			financeMain.setCustID(customer.getCustID());
		}

		// throw error if Finance type is not found
		if (financeType == null) {
			String[] valueParam = new String[1];
			valueParam[0] = financeMain.getFinType();
			FinanceDetail response = new FinanceDetail();
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90202", valueParam);
			response.setReturnStatus(status);
			return response;
		}

		// call reserve limit with finReference
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.getFinScheduleData().setFinanceMain(financeMain);
		financeDetail.getFinScheduleData().setFinanceType(financeType);
		financeDetail.getFinScheduleData().setDisbursementDetails(finDisbursements);
		financeDetail.setCustomerDetails(new CustomerDetails());
		financeDetail.getCustomerDetails().setCustomer(customer);

		return financeDetail;
	}

	public void setLimitStructureService(LimitStructureService limitStructureService) {
		this.limitStructureService = limitStructureService;
	}

	public void setLimitDetailService(LimitDetailService limitDetailService) {
		this.limitDetailService = limitDetailService;
	}

	public void setLimitStructureDetailDAO(LimitStructureDetailDAO limitStructureDetailDAO) {
		this.limitStructureDetailDAO = limitStructureDetailDAO;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setCustomerGroupService(CustomerGroupService customerGroupService) {
		this.customerGroupService = customerGroupService;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setLimitHeaderDAO(LimitHeaderDAO limitHeaderDAO) {
		this.limitHeaderDAO = limitHeaderDAO;
	}

	public void setLimitDetailDAO(LimitDetailDAO limitDetailDAO) {
		this.limitDetailDAO = limitDetailDAO;
	}
}