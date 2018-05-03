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
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.beneficiary.BeneficiaryService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.beneficiary.BeneficiaryDetail;
import com.pennanttech.ws.model.mandate.MandateDetial;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class BeneficiaryController {
	private final Logger logger = Logger.getLogger(getClass());
	private BeneficiaryService beneficiaryService;
	private BankBranchService bankBranchService;
	private CustomerDetailsService customerDetailsService;

	/**
	 * Method for create Beneficiary in PLF system.
	 * 
	 * @param beneficiary
	 * @return Beneficiary
	 */
	public Beneficiary createBeneficiary(Beneficiary beneficiary){
		logger.debug("Entering");
		Beneficiary response = null;
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		try {
			// setting required values which are not received from API
			prepareRequiredData(beneficiary);
			Customer customer = customerDetailsService.getCustomerByCIF(beneficiary.getCustCIF());
			beneficiary.setCustID(customer.getCustID());
			beneficiary.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			beneficiary.setNewRecord(true);
			beneficiary.setVersion(1);
			AuditHeader auditHeader = getAuditHeader(beneficiary, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = beneficiaryService.doApprove(auditHeader);
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new Beneficiary();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
							errorDetail.getError()));
					reqHeaderDetails.setReturnCode(response.getReturnStatus().getReturnCode());
					reqHeaderDetails.setReturnDesc(response.getReturnStatus().getReturnText());
				}
			} else {
				response = (Beneficiary) auditHeader.getAuditDetail().getModelData();
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				reqHeaderDetails.setReturnCode(response.getReturnStatus().getReturnCode());
				reqHeaderDetails.setReturnDesc(response.getReturnStatus().getReturnText());
				doEmptyResponseObject(response);
			}
		} catch (Exception e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new Beneficiary();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		PhaseInterceptorChain.getCurrentMessage().getExchange().put(APIHeader.API_HEADER_KEY,reqHeaderDetails);
		return response;
	}

	/**
	 * get the Beneficiary Details by the given beneficiaryId.
	 * 
	 * @param beneficiaryId
	 * @return Beneficiary
	 */
	public Beneficiary getBeneficiary(long beneficiaryId) {
		logger.debug("Entering");
		Beneficiary response = null;		
		try {
			response = beneficiaryService.getApprovedBeneficiaryById(beneficiaryId);
			if (response != null) {
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new Beneficiary();
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(beneficiaryId);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90601", valueParm));
			}
		} catch (Exception e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new Beneficiary();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for update Beneficiary.
	 * 
	 * @param beneficiary
	 * @throws ServiceException
	 */
	public WSReturnStatus updateBeneficiary(Beneficiary beneficiary) {
		logger.debug("Entering");
		WSReturnStatus response = new WSReturnStatus();
		try {
			// set the default values for mandate
			prepareRequiredData(beneficiary);
			
			Beneficiary prevBeneficiary = beneficiaryService.getApprovedBeneficiaryById(beneficiary.getBeneficiaryId());
			beneficiary.setCustID(prevBeneficiary.getCustID());
			beneficiary.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			beneficiary.setNewRecord(false);
			beneficiary.setVersion(prevBeneficiary.getVersion() + 1);
			
			// copy properties
			BeanUtils.copyProperties(beneficiary, prevBeneficiary);
			
			// call update service method
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader= getAuditHeader(prevBeneficiary, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			 auditHeader = beneficiaryService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
							errorDetail.getError()));
				}
			} else {
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete the Beneficiary by the given beneficiaryId.
	 * 
	 * @param beneficiaryId
	 * @return WSReturnStatus
	 * @throws ServiceException
	 */

	public WSReturnStatus deleteBeneficiary(long beneficiaryId) {
		logger.debug("Entering");
		WSReturnStatus response = new WSReturnStatus();
		try {
			// get the mandate by the mandateId
			Beneficiary beneficiary = beneficiaryService.getApprovedBeneficiaryById(beneficiaryId);

			prepareRequiredData(beneficiary);
			beneficiary.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			beneficiary.setNewRecord(false);
			beneficiary.setVersion(beneficiary.getVersion() + 1);
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(beneficiary, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = beneficiaryService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
							errorDetail.getError()));
				}
			} else {
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * get the Beneficiary Details by the given cif.
	 * 
	 * @param cif
	 * @return BeneficiaryDetail
	 */
	public BeneficiaryDetail getBeneficiaries(String cif) {
		logger.debug("Entering");
		BeneficiaryDetail response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<Beneficiary> beneficiaryList = beneficiaryService.getApprovedBeneficiaryByCustomerId(customer
					.getCustID());
			if (!beneficiaryList.isEmpty()) {
				response = new BeneficiaryDetail();
				response.setBeneficiaryList(beneficiaryList);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new BeneficiaryDetail();
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			}
		} catch (Exception e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			MandateDetial mandates = new MandateDetial();
			mandates.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aBeneficiary
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Beneficiary aBeneficiary, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBeneficiary.getBefImage(), aBeneficiary);
		return new AuditHeader(String.valueOf(aBeneficiary.getBeneficiaryId()), String.valueOf(aBeneficiary
				.getBeneficiaryId()), null, null, auditDetail, aBeneficiary.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetail>>());
	}

	/**
	 * Setting default values from Beneficiary object
	 * 
	 * @param mandate
	 * 
	 */
	private void prepareRequiredData(Beneficiary beneficiary) {
		logger.debug("Entering");

		BankBranch bankBranch = new BankBranch();
		
		if (StringUtils.isNotBlank(beneficiary.getiFSC())) {
			bankBranch = bankBranchService.getBankBrachByIFSC(beneficiary.getiFSC());
		} else if (StringUtils.isNotBlank(beneficiary.getBankCode())
				&& StringUtils.isNotBlank(beneficiary.getBranchCode())) {
			bankBranch = bankBranchService.getBankBrachByCode(beneficiary.getBankCode(), beneficiary.getBranchCode());
		}
		
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		beneficiary.setUserDetails(userDetails);
		beneficiary.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		beneficiary.setiFSC(bankBranch.getIFSC());
		beneficiary.setBankBranchID(bankBranch.getBankBranchID());
		beneficiary.setLastMntBy(userDetails.getUserId());
		beneficiary.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		beneficiary.setSourceId(APIConstants.FINSOURCE_ID_API);
		logger.debug("Leaving");

	}

	/**
	 * Nullify the un-necessary objects to prepare response in a structured format specified in API.
	 * 
	 * @param response
	 */
	private void doEmptyResponseObject(Beneficiary response) {
		response.setCustCIF(null);
		response.setBankCode(null);
		response.setBranchCode(null);
		response.setiFSC(null);
		response.setAccNumber(null);
		response.setAccHolderName(null);
		response.setPhoneAreaCode(null);
		response.setPhoneCountryCode(null);
		response.setPhoneNumber(null);
	}

	public BeneficiaryService getBeneficiaryService() {
		return beneficiaryService;
	}

	public void setBeneficiaryService(BeneficiaryService beneficiaryService) {
		this.beneficiaryService = beneficiaryService;
	}

	@Autowired
	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
}
