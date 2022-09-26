package com.pennanttech.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.pennydrop.BankAccountValidation;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.service.pennydrop.PennyDropService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.mandate.MandateDetial;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class MandateController extends ExtendedTestClass {
	private final Logger logger = LogManager.getLogger(getClass());

	private MandateService mandateService;
	private BankBranchService bankBranchService;
	private CustomerDetailsService customerDetailsService;
	private FinanceMainService financeMainService;
	private FinanceMainDAO financeMainDAO;
	private PennyDropService pennyDropService;
	private MandateStatusDAO mandateStatusDAO;
	private AuditHeaderDAO auditHeaderDAO;

	/**
	 * Method for create Mandate in PLF system.
	 * 
	 * @param mandate
	 * @return Mandate
	 */
	public Mandate createMandate(Mandate mandate) {
		logger.debug(Literal.ENTERING);
		Mandate response = null;
		BankAccountValidation bankAccountValidation = new BankAccountValidation();
		try {
			// setting required values which are not received from API
			ErrorDetail aErrorDetail = prepareRequiredData(mandate);

			// preparing PennyDropResult and storing into Database.
			if (mandate.getPennyDropStatus() != null) {
				bankAccountValidation.setiFSC(mandate.getiFSC());
				bankAccountValidation.setInitiateType("M");
				bankAccountValidation.setAcctNum(mandate.getAccNumber());
				bankAccountValidation.setStatus(mandate.getPennyDropStatus());

				pennyDropService.savePennyDropSts(bankAccountValidation);
			}

			Customer customer = customerDetailsService.getCustomerByCIF(mandate.getCustCIF());
			mandate.setCustID(customer.getCustID());
			mandate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			mandate.setNewRecord(true);
			mandate.setActive(true);
			mandate.setVersion(1);
			mandate.setMandateCcy(SysParamUtil.getAppCurrency());
			mandate.setStatus(com.pennant.pff.mandate.MandateStatus.NEW);
			// get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(mandate, PennantConstants.TRAN_WF);
			// set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = mandateService.doApprove(auditHeader);

			if (aErrorDetail != null) {
				auditHeader.getAuditDetail().getErrorDetails().add(aErrorDetail);
			}

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new Mandate();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				response = (Mandate) auditHeader.getAuditDetail().getModelData();
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				doEmptyResponseObject(response);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new Mandate();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug(Literal.LEAVING);

		return response;
	}

	/**
	 * get the mandate Details by the given mandate Id.
	 * 
	 * @param mandateID
	 * @return Mandate
	 */
	public Mandate getMandate(long mandateId) {
		logger.debug(Literal.ENTERING);
		Mandate response = new Mandate();
		try {
			response = mandateService.getApprovedMandateById(mandateId);
			if (response != null) {

				BigDecimal maxlimt = PennantApplicationUtil.formateAmount(response.getMaxLimit(),
						CurrencyUtil.getFormat(response.getMandateCcy()));
				response.setAmountInWords(NumberToEnglishWords.getNumberToWords(maxlimt.toBigInteger()));
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new Mandate();
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(mandateId);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90303", valueParm));
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new Mandate();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * Method for update Mandate.
	 * 
	 * @param mandate
	 * @throws ServiceException
	 */
	public WSReturnStatus updateMandate(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		try {
			// set the default values for mandate
			ErrorDetail aErrorDetail = prepareRequiredData(mandate);

			Mandate prvMandate = mandateService.getApprovedMandateById(mandate.getMandateID());
			mandate.setCustID(prvMandate.getCustID());
			mandate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			mandate.setNewRecord(false);
			mandate.setVersion(prvMandate.getVersion() + 1);
			mandate.setActive(true);
			mandate.setMandateCcy(SysParamUtil.getAppCurrency());
			mandate.setStatus(com.pennant.pff.mandate.MandateStatus.NEW);
			// copy properties
			BeanUtils.copyProperties(mandate, prvMandate);

			// get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(prvMandate, PennantConstants.TRAN_WF);
			// set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);

			// call update service method
			auditHeader = mandateService.doApprove(auditHeader);

			if (aErrorDetail != null) {
				auditHeader.getAuditDetail().getErrorDetails().add(aErrorDetail);
			}

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {

				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * delete the mandate Details by the given mandate Id.
	 * 
	 * @param mandateID
	 * @return WSReturnStatus
	 * @throws ServiceException
	 */

	public WSReturnStatus deleteMandate(long mandateID) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus response = new WSReturnStatus();
		try {
			// get the mandate by the mandateId
			Mandate mandate = mandateService.getApprovedMandateById(mandateID);

			ErrorDetail aErrorDetail = prepareRequiredData(mandate);
			mandate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			mandate.setNewRecord(false);
			mandate.setVersion(mandate.getVersion() + 1);

			// get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(mandate, PennantConstants.TRAN_WF);
			// set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = mandateService.doApprove(auditHeader);

			if (aErrorDetail != null) {
				auditHeader.getAuditDetail().getErrorDetails().add(aErrorDetail);
			}

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {

				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug(Literal.LEAVING);
		return response;

	}

	/**
	 * get the mandate Details by the given cif.
	 * 
	 * @param cif
	 * @return MandateDetial
	 */
	public MandateDetial getMandates(String cif) {
		logger.debug(Literal.ENTERING);

		MandateDetial response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<Mandate> mandatesList = mandateService.getApprovedMandatesByCustomerId(customer.getCustID());
			if (!mandatesList.isEmpty()) {
				// set the amount in words for response
				for (Mandate mandate : mandatesList) {
					BigDecimal maxlimt = PennantApplicationUtil.formateAmount(mandate.getMaxLimit(),
							CurrencyUtil.getFormat(mandate.getMandateCcy()));
					mandate.setAmountInWords(NumberToEnglishWords.getNumberToWords(maxlimt.toBigInteger()));
				}
				response = new MandateDetial();
				response.setMandateList(mandatesList);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new MandateDetial();
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			MandateDetial mandates = new MandateDetial();
			mandates.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * loanMandateSwapping to oldmandateId to newMandateId With repect to FinanceReference.
	 * 
	 * @param mandateID
	 * @return Mandates
	 */
	public WSReturnStatus loanMandateSwapping(MandateDetial md) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus response = null;
		try {
			String finReference = md.getFinReference();
			Long newMandateId = md.getNewMandateId();
			String mandateType = md.getMandateType();

			Long finID = financeMainDAO.getFinID(finReference, TableType.MAIN_TAB);

			int count = financeMainService.loanMandateSwapping(finID, newMandateId, mandateType, "");

			if (count > 0) {
				response = APIErrorHandlerService.getSuccessStatus();
			} else {
				response = APIErrorHandlerService.getFailedStatus();
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			MandateDetial mandates = new MandateDetial();
			mandates.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * Method for create Mandate in PLF system.
	 * 
	 * @param mandate
	 * @return Mandate
	 */
	public Mandate doApproveMandate(Mandate mandate) {
		logger.debug(Literal.ENTERING);
		Mandate response = null;
		AuditHeader auditHeader = null;
		// set status
		mandate.setApproveMandate(true);
		mandate.setStatus(MandateStatus.APPROVED);

		// set mandate detail and get audit header detail
		auditHeader = doSetMandateDefault(mandate);

		try {
			auditHeader = mandateService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new Mandate();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				response = (Mandate) auditHeader.getAuditDetail().getModelData();
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

				if (mandate.isSwapIsActive()) {
					String type = "";
					Long finID = financeMainDAO.getFinID(mandate.getOrgReference(), TableType.MAIN_TAB);
					if (finID != null) {
						type = "";
					} else if (ImplementationConstants.ALW_APPROVED_MANDATE_IN_ORG) {
						finID = financeMainDAO.getFinID(mandate.getOrgReference(), TableType.MAIN_TAB);
						if (finID != null) {
							type = "_Temp";
						}
					}

					financeMainService.loanMandateSwapping(finID, response.getMandateID(), mandate.getMandateType(),
							type);
				}
				doEmptyResponseObject(response);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new Mandate();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug(Literal.LEAVING);

		return response;
	}

	/**
	 * do mandate doSetMandateDefault(for create and approve mandate)
	 * 
	 * @param mandate
	 * 
	 * @return AuditHeader
	 */
	private AuditHeader doSetMandateDefault(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		// setting required values which are not received from API
		ErrorDetail aErrorDetail = prepareRequiredData(mandate);

		Customer customer = customerDetailsService.getCustomerByCIF(mandate.getCustCIF());

		mandate.setCustID(customer.getCustID());
		mandate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		mandate.setNewRecord(true);
		mandate.setActive(true);
		mandate.setVersion(1);
		mandate.setMandateCcy(SysParamUtil.getAppCurrency());

		// get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(mandate, PennantConstants.TRAN_WF);

		if (aErrorDetail != null) {
			auditHeader.getAuditDetail().getErrorDetails().add(aErrorDetail);
		}

		// set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Setting default values from Mandate object
	 * 
	 * @param mandate
	 * 
	 */
	private ErrorDetail prepareRequiredData(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		mandate.setUserDetails(userDetails);

		mandate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		mandate.setInputDate(SysParamUtil.getAppDate());
		mandate.setLastMntBy(userDetails.getUserId());
		mandate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		mandate.setSourceId(APIConstants.FINSOURCE_ID_API);

		String ifsc = mandate.getIFSC();
		String micr = mandate.getMICR();
		String bankCode = mandate.getBankCode();
		String branchCode = mandate.getBranchCode();

		BankBranch bankBranch = bankBranchService.getBankBranch(ifsc, micr, bankCode, branchCode);

		if (bankBranch.getError() != null) {
			return bankBranch.getError();
		}

		if (StringUtils.isBlank(mandate.getPeriodicity())) {
			mandate.setPeriodicity(MandateConstants.MANDATE_DEFAULT_FRQ);
		}
		mandate.setIFSC(bankBranch.getIFSC());
		mandate.setBankBranchID(bankBranch.getBankBranchID());

		logger.debug(Literal.LEAVING);

		return null;
	}

	/**
	 * Nullify the un-necessary objects to prepare response in a structured format specified in API.
	 * 
	 * @param response
	 */
	private void doEmptyResponseObject(Mandate response) {
		response.setCustCIF(null);
		response.setMandateType(null);
		response.setBankCode(null);
		response.setBranchCode(null);
		response.setIFSC(null);
		response.setMICR(null);
		response.setAccType(null);
		response.setAccNumber(null);
		response.setAccHolderName(null);
		response.setJointAccHolderName(null);
		response.setStartDate(null);
		response.setExpiryDate(null);
		response.setMaxLimit(null);
		response.setPeriodicity(null);
		response.setPhoneAreaCode(null);
		response.setPhoneCountryCode(null);
		response.setPhoneNumber(null);
		response.setBarCodeNumber(null);
		response.setOrgReference(null);
		response.setAmountInWords(null);
		response.setEntityCode(null);
		response.setDocImage(null);
		response.setDocumentName(null);
		response.setExternalRef(null);
		response.seteMandateReferenceNo(null);
		response.seteMandateSource(null);
	}

	/**
	 * Method for update Mandate status in PLF system.
	 * 
	 * @param mandate
	 * @throws ServiceException
	 */
	public WSReturnStatus updateMandateStatus(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		try {
			int count = mandateService.updateMandateStatus(mandate);

			if (count == 0) {
				logger.error(Literal.LEAVING);
				return APIErrorHandlerService.getFailedStatus();
			}

			com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();
			long mandateID = mandate.getMandateID();
			mandateStatus.setMandateID(mandateID);
			mandateStatus.setStatus(mandate.getStatus());
			mandateStatus.setReason(mandate.getReason());
			mandateStatus.setChangeDate(SysParamUtil.getAppDate());
			mandateStatusDAO.save(mandateStatus, "");

			if ((MandateStatus.isApproved(mandate.getStatus()) || MandateStatus.isAccepted(mandate.getStatus()))
					&& mandate.isSwapIsActive()) {
				String type = "";
				Long finID = financeMainDAO.getFinID(mandate.getOrgReference(), TableType.MAIN_TAB);

				if (finID != null) {
					type = "";
				} else if (ImplementationConstants.ALW_APPROVED_MANDATE_IN_ORG) {
					finID = financeMainDAO.getFinID(mandate.getOrgReference(), TableType.TEMP_TAB);
					if (finID != null) {
						type = "_Temp";
					}
				}

				String mandateType = mandate.getMandateType();
				financeMainService.loanMandateSwapping(finID, mandateID, mandateType, type);
			}

			logger.debug(Literal.LEAVING);
			return APIErrorHandlerService.getSuccessStatus();

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

	}

	public WSReturnStatus updateApprovedMandate(Mandate mandate) {
		int count = 0;

		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		AuditHeader ah = getAuditHeader(mandate, PennantConstants.TRAN_UPD);

		ah.setApiHeader(reqHeaderDetails);

		try {
			if (mandateService.updateMandateStatus(mandate) <= 0) {
				return APIErrorHandlerService.getFailedStatus();
			}

			auditHeaderDAO.addAudit(ah);

			com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();

			mandateStatus.setMandateID(mandate.getMandateID());
			mandateStatus.setStatus(mandate.getStatus());
			mandateStatus.setChangeDate(SysParamUtil.getAppDate());
			mandateStatusDAO.save(mandateStatus, "");

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return APIErrorHandlerService.getFailedStatus();
		}
		return APIErrorHandlerService.getSuccessStatus();
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aMandate
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Mandate aMandate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMandate.getBefImage(), aMandate);
		return new AuditHeader(String.valueOf(aMandate.getMandateID()), String.valueOf(aMandate.getMandateID()), null,
				null, auditDetail, aMandate.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	public MandateService getMandateService() {
		return mandateService;
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	@Autowired
	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setPennyDropService(PennyDropService pennyDropService) {
		this.pennyDropService = pennyDropService;
	}

	@Autowired
	public void setMandateStatusDAO(MandateStatusDAO mandateStatusDAO) {
		this.mandateStatusDAO = mandateStatusDAO;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
}
