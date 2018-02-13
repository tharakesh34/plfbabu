package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.mandate.MandateDetial;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class MandateController {
	private final Logger logger = Logger.getLogger(getClass());

	private MandateService mandateService;
	private BankBranchService bankBranchService;
	private CustomerDetailsService customerDetailsService;
	private FinanceMainService  financeMainService;

	/**
	 * Method for create Mandate in PLF system.
	 * 
	 * @param mandate
	 * @return Mandate
	 */
	public Mandate createMandate(Mandate mandate) {
		logger.debug("Entering");

		//for logging purpose
		APIErrorHandlerService.logReference(mandate.getCustCIF());
		Mandate response = null;
		try{
		// setting required values which are not received from API
		prepareRequiredData(mandate);
		Customer customer = customerDetailsService.getCustomerByCIF(mandate.getCustCIF());
		mandate.setCustID(customer.getCustID());
		mandate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		mandate.setNewRecord(true);
		mandate.setActive(true);
		mandate.setVersion(1);
		mandate.setMandateCcy(SysParamUtil.getAppCurrency());
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(mandate, PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		
		auditHeader = mandateService.doApprove(auditHeader);
		
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new Mandate();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getError()));
			}
		} else {
			response = (Mandate) auditHeader.getAuditDetail().getModelData();
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			doEmptyResponseObject(response);
		}
		}catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new Mandate();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");

		return response;
	}

	/**
	 * get the mandate Details by the given mandate Id.
	 * 
	 * @param mandateID
	 * @return Mandate
	 */
	public Mandate getMandate(long mandateId) {
		logger.debug("Entering");

		//for logging purpose
		APIErrorHandlerService.logReference(String.valueOf(mandateId));

		Mandate response = new Mandate();
		try {
			response = mandateService.getApprovedMandateById(mandateId);
			if (response != null) {
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new Mandate();
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(mandateId);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90303", valueParm));
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new Mandate();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for update Mandate.
	 * 
	 * @param mandate
	 * @throws ServiceException
	 */
	public WSReturnStatus updateMandate(Mandate mandate) {
		logger.debug("Entering");
		//for logging purpose
		APIErrorHandlerService.logReference(mandate.getCustCIF());

		WSReturnStatus response = new WSReturnStatus();
		try{
		//set the default values for mandate 
		prepareRequiredData(mandate);
		
		Mandate prvMandate = mandateService.getApprovedMandateById(mandate.getMandateID());
		mandate.setCustID(prvMandate.getCustID());
		mandate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		mandate.setNewRecord(false);
		mandate.setVersion(prvMandate.getVersion() + 1);
		mandate.setActive(true);
		mandate.setMandateCcy(SysParamUtil.getAppCurrency());
		// copy properties
		BeanUtils.copyProperties(mandate,prvMandate);
		
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(prvMandate, PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		
		// call update service method
		auditHeader = mandateService.doApprove(auditHeader);
		
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {

			response = APIErrorHandlerService.getSuccessStatus();
		}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		
		logger.debug("Leaving");
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
		logger.debug("Entering");
		WSReturnStatus response = new WSReturnStatus();
		try{
		//get the mandate by the mandateId
		Mandate mandate = mandateService.getApprovedMandateById(mandateID);
		
		prepareRequiredData(mandate);
		mandate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		mandate.setNewRecord(false);
		mandate.setVersion(mandate.getVersion() + 1);
		
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(mandate, PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		
		auditHeader = mandateService.doApprove(auditHeader);
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {

			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * get the mandate Details by the given cif.
	 * 
	 * @param cif
	 * @return MandateDetial
	 */
	public MandateDetial getMandates(String cif) {
		logger.debug("Entering");

		MandateDetial response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<Mandate> mandatesList = mandateService.getApprovedMandatesByCustomerId(customer.getCustID());
			if (!mandatesList.isEmpty()) {
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
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			MandateDetial mandates = new MandateDetial();
			mandates.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * loanMandateSwapping to oldmandateId to newMandateId With repect to  FinanceReference.
	 * 
	 * @param mandateID
	 * @return Mandates
	 */
	public WSReturnStatus loanMandateSwapping(MandateDetial mandateDetail) {
		logger.debug("Entering");
		WSReturnStatus response=null; 
		//for logging purpose
		APIErrorHandlerService.logReference(mandateDetail.getFinReference());
		try{
		int count =financeMainService.loanMandateSwapping(mandateDetail.getFinReference(),mandateDetail.getNewMandateId());
		if(count>0)
		{
			response = APIErrorHandlerService.getSuccessStatus();
		}else{
			response = APIErrorHandlerService.getFailedStatus();
		}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			MandateDetial mandates = new MandateDetial();
			mandates.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");
		return response;
	}

	
	
	
	/**
	 * Setting default values from Mandate object
	 * 
	 * @param mandate
	 * 
	 */
	private void prepareRequiredData(Mandate mandate) {
		logger.debug("Entering");
		BankBranch bankBranch = new BankBranch();
		if (StringUtils.isNotBlank(mandate.getIFSC())) {
			bankBranch = bankBranchService.getBankBrachByIFSC(mandate.getIFSC());
		} else if (StringUtils.isNotBlank(mandate.getBankCode()) && StringUtils.isNotBlank(mandate.getBranchCode())) {
			bankBranch = bankBranchService.getBankBrachByCode(mandate.getBankCode(), mandate.getBranchCode());
		}
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		mandate.setUserDetails(userDetails);
		if (StringUtils.isBlank(mandate.getPeriodicity())) {
			mandate.setPeriodicity(MandateConstants.MANDATE_DEFAULT_FRQ);
		}
		mandate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		mandate.setIFSC(bankBranch.getIFSC());
		mandate.setBankBranchID(bankBranch.getBankBranchID());
		mandate.setInputDate(DateUtility.getAppDate());
		mandate.setLastMntBy(userDetails.getUserId());
		mandate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		mandate.setSourceId(APIConstants.FINSOURCE_ID_API);
		logger.debug("Leaving");

	}

	/**
	 * Nullify the un-necessary objects to prepare response in a structured format specified in API.
	 * 
	 * @param response
	 */
	private void doEmptyResponseObject(Mandate response) {
		response.setCustCIF(null);
		response.setMandateRef(null);
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
				null, auditDetail, aMandate.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
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

}
