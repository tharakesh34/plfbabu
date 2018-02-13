package com.pennanttech.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.customermasters.CustomerBankInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.service.customermasters.CustomerBankInfoService;
import com.pennant.backend.service.customermasters.CustomerChequeInfoService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.service.customermasters.CustomerExtLiabilityService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.customermasters.CustomerPhoneNumberService;
import com.pennant.backend.service.customermasters.validation.CustomerBankInfoValidation;
import com.pennant.backend.service.customermasters.validation.CustomerChequeInfoValidation;
import com.pennant.backend.service.customermasters.validation.CustomerExtLiabilityValidation;
import com.pennant.backend.util.PennantConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.customer.CustomerBankInfoDetail;
import com.pennanttech.ws.model.customer.CustomerChequeInfoDetail;
import com.pennanttech.ws.model.customer.CustomerExtLiabilityDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class CustomerDetailsController {
	private final Logger logger = Logger.getLogger(CustomerDetailsController.class);

	private CustomerDetailsService customerDetailsService;
	private CustomerPhoneNumberService customerPhoneNumberService;
	private CustomerAddresService customerAddresService;
	private CustomerEMailService customerEMailService;
	private CustomerIncomeService customerIncomeService;
	private CustomerBankInfoDAO customerBankInfoDAO;
	private CustomerChequeInfoDAO customerChequeInfoDAO;
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	private CustomerDocumentService customerDocumentService;
	private CustomerBankInfoService customerBankInfoService;
	private CustomerChequeInfoService customerChequeInfoService;
	private CustomerExtLiabilityService customerExtLiabilityService;
	private DocumentManagerDAO			documentManagerDAO;


	/**
	 * get the Customer PhoneNumbers By the Customer Id
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerPhoneNumbers(String cif) {
		logger.debug("Entering");
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustomerPhoneNumber> customerPhoneNumberList = customerPhoneNumberService
					.getApprovedCustomerPhoneNumberById(customer.getCustID());
			if (customerPhoneNumberList != null && !customerPhoneNumberList.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				for (CustomerPhoneNumber detail : customerPhoneNumberList) {
					detail.setLovDescCustCIF(null);
				}
				response.setCustomerPhoneNumList(customerPhoneNumberList);
				response.setCustomer(null);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new CustomerDetails();
				response.setCustomer(null);
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerDetails();
			response.setCustomer(null);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * Method for create CustomerPhoneNumber in PLF system.
	 * 
	 * @param customerPhoneNumber
	 * 
	 */

	public WSReturnStatus addCustomerPhoneNumber(CustomerPhoneNumber customerPhoneNumber, String cif) {
		logger.debug("Entering");
		WSReturnStatus response=null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerPhoneNumber.setUserDetails(userDetails);
		customerPhoneNumber.setPhoneCustID(prvCustomer.getCustID());
		customerPhoneNumber.setLovDescCustCIF(cif);
		customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		customerPhoneNumber.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerPhoneNumber.setNewRecord(true);
		customerPhoneNumber.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerPhoneNumber.setLastMntBy(userDetails.getUserId());
		customerPhoneNumber.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerPhoneNumber.setVersion(1);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(customerPhoneNumber,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerPhoneNumberService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}
		} catch (Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;

	}

	/**
	 * Method for update customerPhoneNumber in PLF system.
	 * 
	 * @param customerPhoneNumber
	 * @throws ServiceException
	 */

	public WSReturnStatus updateCustomerPhoneNumber(CustomerPhoneNumber customerPhoneNumber, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = new WSReturnStatus();
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerPhoneNumber.setUserDetails(userDetails);
		customerPhoneNumber.setPhoneCustID(prvCustomer.getCustID());
		customerPhoneNumber.setLovDescCustCIF(cif);
		customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		customerPhoneNumber.setNewRecord(false);
		customerPhoneNumber.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerPhoneNumber.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerPhoneNumber.setLastMntBy(userDetails.getUserId());
		customerPhoneNumber.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerPhoneNumber.setVersion((customerPhoneNumberService.getVersion(customerPhoneNumber.getPhoneCustID(),
				customerPhoneNumber.getPhoneTypeCode())) + 1);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(customerPhoneNumber,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerPhoneNumberService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete the CustomerPhoneNumber.
	 * 
	 * @param customerPhoneNumber
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerPhoneNumber(CustomerPhoneNumber customerPhoneNumber) {
		WSReturnStatus response = new WSReturnStatus();
		try{
		logger.debug("Entering");
		// get the CustomerCustomerPhoneNumber by the PhoneCustID and PhoneTypeCode
		CustomerPhoneNumber curPhoneNumber = customerPhoneNumberService.getApprovedCustomerPhoneNumberById(
				customerPhoneNumber.getPhoneCustID(), customerPhoneNumber.getPhoneTypeCode());

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		curPhoneNumber.setUserDetails(userDetails);
		curPhoneNumber.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		curPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		curPhoneNumber.setNewRecord(false);
		curPhoneNumber.setSourceId(APIConstants.FINSOURCE_ID_API);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(curPhoneNumber,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerPhoneNumberService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {

			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		
		logger.debug("Leaving");
		return response;

	}
	
	/**
	 * get the Customer CustomerAddresses By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerAddresses(String cif) {
		logger.debug("Entering");
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustomerAddres> customerAddresList = customerAddresService
					.getApprovedCustomerAddresById(customer.getCustID());
			if (customerAddresList != null && !customerAddresList.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				response.setAddressList(customerAddresList);
				response.setCustomer(null);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new CustomerDetails();
				response.setCustomer(null);
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerDetails();
			response.setCustomer(null);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * Method for create CustomerAddress in PLF system.
	 * @param customerAddress
	 */

	public WSReturnStatus addCustomerAddress(CustomerAddres customerAddress, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = new WSReturnStatus();
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerAddress.setUserDetails(userDetails);
		customerAddress.setCustID(prvCustomer.getCustID());
		customerAddress.setLovDescCustCIF(cif);
		customerAddress.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		customerAddress.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerAddress.setNewRecord(true);
		customerAddress.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerAddress.setLastMntBy(userDetails.getUserId());
		customerAddress.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerAddress.setVersion(1);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(customerAddress,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerAddresService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * Method for update CustomerAddress in PLF system.
	 * @param customerAddres
	 * @throws ServiceException
	 */

	public WSReturnStatus updateCustomerAddress(CustomerAddres customerAddres, String cif) {
		logger.debug("Entering");
		WSReturnStatus response =null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerAddres.setUserDetails(userDetails);
		customerAddres.setCustID(prvCustomer.getCustID());
		customerAddres.setLovDescCustCIF(cif);
		customerAddres.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		customerAddres.setNewRecord(false);
		customerAddres.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerAddres.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerAddres.setLastMntBy(userDetails.getUserId());
		customerAddres.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerAddres.setVersion((customerAddresService.getVersion(customerAddres.getCustID(),
				customerAddres.getCustAddrType())) + 1);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(customerAddres,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerAddresService.doApprove(auditHeader);
		 response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}
	
	/**
	 * delete the CustomerAddress.
	 * @param customerAddres
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerAddress(CustomerAddres customerAddres) {
		logger.debug("Entering");
		WSReturnStatus response=null;
		try{
		// get the CustomerAddress by the CustID and Addrtype
		CustomerAddres curCustomerAddres = customerAddresService.getApprovedCustomerAddresById(customerAddres.getCustID(), customerAddres.getCustAddrType());

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		curCustomerAddres.setUserDetails(userDetails);
		curCustomerAddres.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		curCustomerAddres.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		curCustomerAddres.setNewRecord(false);
		curCustomerAddres.setSourceId(APIConstants.FINSOURCE_ID_API);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(curCustomerAddres,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerAddresService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * get the Customer CustomerEmails By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerEmails(String cif) {
		logger.debug("Entering");
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustomerEMail> customerEMailList = customerEMailService
					.getApprovedCustomerEMailById(customer.getCustID());
			if (customerEMailList != null && !customerEMailList.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				response.setCustomerEMailList(customerEMailList);
				response.setCustomer(null);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new CustomerDetails();
				response.setCustomer(null);
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerDetails();
			response.setCustomer(null);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * Method for create CustomerEmail in PLF system.
	 * @param customerEMail
	 */

	public WSReturnStatus addCustomerEmail(CustomerEMail customerEMail, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerEMail.setUserDetails(userDetails);
		customerEMail.setCustID(prvCustomer.getCustID());
		customerEMail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		customerEMail.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerEMail.setLovDescCustCIF(cif);
		customerEMail.setNewRecord(true);
		customerEMail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerEMail.setLastMntBy(userDetails.getUserId());
		customerEMail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerEMail.setVersion(1);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(customerEMail,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerEMailService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;

	}
	
	/**
	 * Method for update CustomerEmail in PLF system.
	 * @param customerEMail
	 */

	public WSReturnStatus updateCustomerEmail(CustomerEMail customerEMail, String cif) {
		logger.debug("Entering");
		WSReturnStatus response =null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerEMail.setUserDetails(userDetails);
		customerEMail.setCustID(prvCustomer.getCustID());
		customerEMail.setLovDescCustCIF(cif);
		customerEMail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		customerEMail.setNewRecord(false);
		customerEMail.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerEMail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerEMail.setLastMntBy(userDetails.getUserId());
		customerEMail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerEMail.setVersion((customerEMailService.getVersion(customerEMail.getCustID(),
				customerEMail.getCustEMailTypeCode())) + 1);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(customerEMail,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
	    auditHeader = customerEMailService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}
	
	/**
	 * delete the CustomerEMail.
	 * @param customerEmail
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerEmail(CustomerEMail customerEmail) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try{
		CustomerEMail curCustomerEMail = customerEMailService.getApprovedCustomerEMailById(customerEmail.getCustID(), customerEmail.getCustEMailTypeCode());

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		curCustomerEMail.setUserDetails(userDetails);
		curCustomerEMail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		curCustomerEMail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		curCustomerEMail.setNewRecord(false);
		curCustomerEMail.setSourceId(APIConstants.FINSOURCE_ID_API);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(curCustomerEMail,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerEMailService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {

			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}
	
	/**
	 * get the Customer Incomes By the Customer Id
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerIncomes(String cif) {
		logger.debug("Entering");
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustomerIncome> customerIncomeList = customerIncomeService
					.getCustomerIncomes(customer.getCustID(),false);
			if (customerIncomeList != null && !customerIncomeList.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				response.setCustomerIncomeList(customerIncomeList);
				response.setCustomer(null);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new CustomerDetails();
				response.setCustomer(null);
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerDetails();
			response.setCustomer(null);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;

	}
	
	/**
	 * Method for create CustomerIncome in PLF system.
	 * @param customerIncome
	 */

	public WSReturnStatus addCustomerIncome(CustomerIncome customerIncome, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerIncome.setUserDetails(userDetails);
		customerIncome.setCustID(prvCustomer.getCustID());
		customerIncome.setMargin(new BigDecimal(0));
		customerIncome.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		customerIncome.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerIncome.setLovDescCustCIF(cif);
		customerIncome.setNewRecord(true);
		customerIncome.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerIncome.setLastMntBy(userDetails.getUserId());
		customerIncome.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerIncome.setVersion(1);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(customerIncome,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerIncomeService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}
	/**
	 * Method for update CustomerIncome in PLF system.
	 * @param customerIncome
	 */

	public WSReturnStatus updateCustomerIncome(CustomerIncome customerIncome, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerIncome.setUserDetails(userDetails);
		customerIncome.setCustID(prvCustomer.getCustID());
		customerIncome.setLovDescCustCIF(cif);
		customerIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		customerIncome.setNewRecord(false);
		customerIncome.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerIncome.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerIncome.setLastMntBy(userDetails.getUserId());
		customerIncome.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerIncome.setVersion(customerIncomeService.getVersion(customerIncome) + 1);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(customerIncome,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerIncomeService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}
	
	/**
	 * delete the CustomerIncome.
	 * @param customerIncome
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerIncome(CustomerIncome customerIncome) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try{
		CustomerIncome curCustomerIncom = customerIncomeService.getApprovedCustomerIncomeById(customerIncome);
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		curCustomerIncom.setUserDetails(userDetails);
		curCustomerIncom.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		curCustomerIncom.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		curCustomerIncom.setNewRecord(false);
		curCustomerIncom.setSourceId(APIConstants.FINSOURCE_ID_API);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(curCustomerIncom,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerIncomeService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}
	/**
	 * get CustomerBankingInformation By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerBankingInformation(String cif) {
		logger.debug("Entering");
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustomerBankInfo> customerBankInfoList = customerBankInfoService
					.getApprovedBankInfoByCustomerId(customer.getCustID());
			if (customerBankInfoList != null && !customerBankInfoList.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				response.setCustomerBankInfoList(customerBankInfoList);
				response.setCustomer(null);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new CustomerDetails();
				response.setCustomer(null);
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerDetails();
			response.setCustomer(null);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;

	}
	
	/**
	 * Method for create Customer BankingInformation in PLF system.
	 * 
	 * @param customerBankInfo
	 * 
	 */

	public CustomerBankInfoDetail addCustomerBankingInformation(CustomerBankInfo customerBankInfo, String cif) {
		CustomerBankInfoDetail response = null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		logger.debug("Entering");
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		customerBankInfo.setCustID(customer.getCustID());
		customerBankInfo.setLovDescCustCIF(cif);
		customerBankInfo.setNewRecord(true);
		customerBankInfo.setVersion(1);
		customerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		customerBankInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerBankInfo.setLastMntBy(userDetails.getUserId());
		customerBankInfo.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerBankInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		CustomerBankInfoValidation validation = new CustomerBankInfoValidation(customerBankInfoDAO);
		AuditHeader auditHeader = validation.bankInfoValidation(
				getAuditHeader(customerBankInfo, PennantConstants.TRAN_WF), "doApprove");

		
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerBankInfoDetail();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getError()));
			}
		} else {
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader=customerBankInfoService.doApprove(auditHeader);
			response = new CustomerBankInfoDetail();
			CustomerBankInfo custBankInfo = (CustomerBankInfo) auditHeader.getAuditDetail().getModelData();
			response.setBankId(custBankInfo.getBankId());
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		}catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerBankInfoDetail();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");

		return response;

	}
	
	
	/**
	 * Method for update CustomerBankingInformation in PLF system.
	 * @param customerBankInfo
	 */

	public WSReturnStatus updateCustomerBankingInformation(CustomerBankInfo customerBankInfo, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerBankInfo.setUserDetails(userDetails);
		customerBankInfo.setCustID(prvCustomer.getCustID());
		customerBankInfo.setLovDescCustCIF(cif);
		customerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		customerBankInfo.setNewRecord(false);
		customerBankInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerBankInfo.setLastMntBy(userDetails.getUserId());
		customerBankInfo.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerBankInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerBankInfo.setVersion((customerBankInfoService.getVersion(customerBankInfo.getBankId())) + 1);
		CustomerBankInfoValidation validation = new CustomerBankInfoValidation(customerBankInfoDAO);
		AuditHeader auditHeader = validation.bankInfoValidation(
				getAuditHeader(customerBankInfo, PennantConstants.TRAN_WF), "doApprove");
	response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			customerBankInfoService.doApprove(auditHeader);
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}
	/**
	 * delete the CustomerBankingInformation.
	 * @param customerBankInfo
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerBankingInformation(CustomerBankInfo customerBankInfo) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try{
		CustomerBankInfo curCustBankInfo = customerBankInfoService.getCustomerBankInfoById(customerBankInfo.getBankId());

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		curCustBankInfo.setUserDetails(userDetails);
		curCustBankInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		curCustBankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		curCustBankInfo.setSourceId(APIConstants.FINSOURCE_ID_API);
		curCustBankInfo.setNewRecord(false);
		CustomerBankInfoValidation validation = new CustomerBankInfoValidation(customerBankInfoDAO);
		AuditHeader auditHeader = validation.bankInfoValidation(
				getAuditHeader(curCustBankInfo, PennantConstants.TRAN_WF), "doApprove");
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			customerBankInfoService.doApprove(auditHeader);			
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;

	}
	/**
	 * get CustomerAccountBehaviour By the cif
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerAccountBehaviour(String cif) {
		logger.debug("Entering");
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustomerChequeInfo> customerChequeInfoList = customerChequeInfoService
					.getChequeInfoByCustomerId(customer.getCustID());
			if (customerChequeInfoList != null && !customerChequeInfoList.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				response.setCustomerChequeInfoList(customerChequeInfoList);
				response.setCustomer(null);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new CustomerDetails();
				response.setCustomer(null);
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			CustomerDetails customerBankInfo = new CustomerDetails();
			customerBankInfo.setCustomer(null);
			customerBankInfo.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return customerBankInfo;
		}

		logger.debug("Leaving");
		return response;
	}
	
	/**
	 * Method for create Customer AccountBehaviour in PLF system.
	 * 
	 * @param customerChequeInfo
	 * 
	 */

	public CustomerChequeInfoDetail addCustomerAccountBehaviour(CustomerChequeInfo customerChequeInfo, String cif) {

		logger.debug("Entering");
		CustomerChequeInfoDetail response = null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		customerChequeInfo.setCustID(customer.getCustID());
		customerChequeInfo.setLovDescCustCIF(cif);
		customerChequeInfo.setNewRecord(true);
		customerChequeInfo.setVersion(1);
		customerChequeInfo.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerChequeInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		customerChequeInfo.setLastMntBy(userDetails.getUserId());
		customerChequeInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		CustomerChequeInfoValidation validation = new CustomerChequeInfoValidation(customerChequeInfoDAO);
		AuditHeader auditHeader = validation.chequeInfoValidation(
				getAuditHeader(customerChequeInfo, PennantConstants.TRAN_WF), "doApprove");
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerChequeInfoDetail();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getError()));
			}
		} else {
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader=customerChequeInfoService.doApprove(auditHeader);
			response = new CustomerChequeInfoDetail();
			CustomerChequeInfo custCustomerChequeInfo = (CustomerChequeInfo) auditHeader.getAuditDetail().getModelData();
			response.setChequeSeq(custCustomerChequeInfo.getChequeSeq());
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		}catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			CustomerChequeInfoDetail customerChequeInfoDetail = new CustomerChequeInfoDetail();
			customerChequeInfoDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return customerChequeInfoDetail;
		}

		logger.debug("Leaving");
		return response;
	}
	/**
	 * Method for update CustomerAccountBehaviour in PLF system.
	 * @param customerChequeInfo
	 */

	public WSReturnStatus updateCustomerAccountBehaviour(CustomerChequeInfo customerChequeInfo, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerChequeInfo.setUserDetails(userDetails);
		customerChequeInfo.setCustID(prvCustomer.getCustID());
		customerChequeInfo.setLovDescCustCIF(cif);
		customerChequeInfo.setRecordType(PennantConstants.RCD_UPD);
		customerChequeInfo.setNewRecord(false);
		customerChequeInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerChequeInfo.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerChequeInfo.setLastMntBy(userDetails.getUserId());
		customerChequeInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerChequeInfo.setVersion((customerChequeInfoService.getVersion(customerChequeInfo.getCustID(),
				customerChequeInfo.getChequeSeq())) + 1);
		CustomerChequeInfoValidation validation = new CustomerChequeInfoValidation(customerChequeInfoDAO);
		AuditHeader auditHeader = validation.chequeInfoValidation(
				getAuditHeader(customerChequeInfo, PennantConstants.TRAN_WF), "doApprove");
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			customerChequeInfoService.doApprove(auditHeader);
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}
	/**
	 * delete the CustomerAccountBehaviour.
	 * @param customerChequeInfo
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerAccountBehaviour(CustomerChequeInfo customerChequeInfo) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try{
		CustomerChequeInfo curCustChequeInfo = customerChequeInfoDAO.getCustomerChequeInfoById(customerChequeInfo.getCustID(),
				customerChequeInfo.getChequeSeq(),"");

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		curCustChequeInfo.setUserDetails(userDetails);
		curCustChequeInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		curCustChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		curCustChequeInfo.setSourceId(APIConstants.FINSOURCE_ID_API);
		curCustChequeInfo.setNewRecord(false);
		CustomerChequeInfoValidation validation = new CustomerChequeInfoValidation(customerChequeInfoDAO);
		AuditHeader auditHeader = validation.chequeInfoValidation(
				getAuditHeader(curCustChequeInfo, PennantConstants.TRAN_WF), "doApprove");
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			customerChequeInfoService.doApprove(auditHeader);			
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;

	}
	/**
	 * Method for create Customer ExtLiability in PLF system.
	 * 
	 * @param customerExtLiability
	 * 
	 */

	public CustomerExtLiabilityDetail addCustomerExternalLiability(CustomerExtLiability customerExtLiability, String cif) {

		logger.debug("Entering");
		CustomerExtLiabilityDetail response = null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		customerExtLiability.setCustID(customer.getCustID());
		customerExtLiability.setLovDescCustCIF(cif);
		customerExtLiability.setNewRecord(true);
		customerExtLiability.setVersion(1);
		customerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		customerExtLiability.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerExtLiability.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerExtLiability.setLastMntBy(userDetails.getUserId());
		customerExtLiability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		AuditHeader auditHeader = validation.extLiabilityValidation(
				getAuditHeader(customerExtLiability, PennantConstants.TRAN_WF), "doApprove");
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerExtLiabilityDetail();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getError()));
			}
		} else {
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader=customerExtLiabilityService.doApprove(auditHeader);
			response = new CustomerExtLiabilityDetail();
			CustomerExtLiability custExtLiability = (CustomerExtLiability) auditHeader.getAuditDetail().getModelData();
			response.setLiabilitySeq(custExtLiability.getLiabilitySeq());
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		}catch(Exception e){
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerExtLiabilityDetail();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");

		return response;

	}

	/**
	 * Method for update CustomerExternalLiability in PLF system.
	 * @param customerExtLiability
	 */

	public WSReturnStatus updateCustomerExternalLiability(CustomerExtLiability customerExtLiability, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerExtLiability.setUserDetails(userDetails);
		customerExtLiability.setCustID(prvCustomer.getCustID());
		customerExtLiability.setLovDescCustCIF(cif);
		customerExtLiability.setRecordType(PennantConstants.RCD_UPD);
		customerExtLiability.setNewRecord(false);
		customerExtLiability.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerExtLiability.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerExtLiability.setLastMntBy(userDetails.getUserId());
		customerExtLiability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerExtLiability.setVersion((customerExtLiabilityService.getVersion(customerExtLiability.getCustID(),
				customerExtLiability.getLiabilitySeq())) + 1);
		CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		AuditHeader auditHeader = validation.extLiabilityValidation(
				getAuditHeader(customerExtLiability, PennantConstants.TRAN_WF), "doApprove");
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			customerExtLiabilityService.doApprove(auditHeader);
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}
	
	/**
	 * delete the CustomerExternalLiability.
	 * @param customerExtLiability
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerExternalLiability(CustomerExtLiability customerExtLiability) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try{
		CustomerExtLiability curCustomerExtLiability = customerExtLiabilityService.getCustomerExtLiabilityById(customerExtLiability.getCustID(),
				customerExtLiability.getLiabilitySeq());

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		curCustomerExtLiability.setUserDetails(userDetails);
		curCustomerExtLiability.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		curCustomerExtLiability.setSourceId(APIConstants.FINSOURCE_ID_API);
		curCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		curCustomerExtLiability.setNewRecord(false);
		CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		AuditHeader auditHeader = validation.extLiabilityValidation(
				getAuditHeader(curCustomerExtLiability, PennantConstants.TRAN_WF), "doApprove");
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			customerExtLiabilityService.doApprove(auditHeader);			
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}
	/**
	 * get CustomerExternalLiabilities By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerExternalLiabilities(String cif) {
		logger.debug("Entering");
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustomerExtLiability> customerExtLiabilityList = customerExtLiabilityService.getExtLiabilityByCustomer(customer.getCustID());
			if (customerExtLiabilityList != null && !customerExtLiabilityList.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				response.setCustomerExtLiabilityList(customerExtLiabilityList);
				response.setCustomer(null);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new CustomerDetails();
				response.setCustomer(null);
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerDetails();
			response.setCustomer(null);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;
	}
	
	/**
	 * get the Customer CustomerDocuments By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerDocuments(String cif) {
		logger.debug("Entering");
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustomerDocument> customerDocumentsList = customerDocumentService.getApprovedCustomerDocumentById(customer.getCustID());
			if (customerDocumentsList != null && !customerDocumentsList.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				for (CustomerDocument documents : customerDocumentsList) {
					byte[] custDocImage = getDocumentImage(documents.getDocRefId());
					documents.setCustDocImage(custDocImage);
				}
				response.setCustomerDocumentsList(customerDocumentsList);
				response.setCustomer(null);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new CustomerDetails();
				response.setCustomer(null);
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerDetails();
			response.setCustomer(null);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;

	}
	private byte[] getDocumentImage(long docID) {
		DocumentManager docImage=documentManagerDAO.getById(docID);
		if(docImage != null){
			return docImage.getDocImage();
		}
		return null;
	}
	/**
	 * Method for create CustomerDocument in PLF system.
	 * @param customerDocument
	 */

	public WSReturnStatus addCustomerDocument(CustomerDocument customerDocument, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerDocument.setUserDetails(userDetails);
		customerDocument.setCustID(prvCustomer.getCustID());
		customerDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		customerDocument.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerDocument.setLovDescCustCIF(cif);
		customerDocument.setNewRecord(true);
		customerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerDocument.setLastMntBy(userDetails.getUserId());
		customerDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerDocument.setVersion(1);
		
		if(StringUtils.equalsIgnoreCase(customerDocument.getCustDocType(), "JPG")
				|| StringUtils.equalsIgnoreCase(customerDocument.getCustDocType(), "PNG")
				|| StringUtils.equalsIgnoreCase(customerDocument.getCustDocType(), "JPEG")
			    || StringUtils.equalsIgnoreCase(customerDocument.getCustDocType(), "JFIF")) {
			customerDocument.setCustDocType(PennantConstants.DOC_TYPE_IMAGE);
		}
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(customerDocument,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerDocumentService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}
		} catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}
	/**
	 * Method for update CustomerDocument in PLF system.
	 * @param customerDocument
	 */

	public WSReturnStatus updateCustomerDocument(CustomerDocument customerDocument, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		// for logging purpose
		APIErrorHandlerService.logReference(cif);
		try{
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		customerDocument.setUserDetails(userDetails);
		customerDocument.setCustID(prvCustomer.getCustID());
		customerDocument.setLovDescCustCIF(cif);
		customerDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		customerDocument.setNewRecord(false);
		customerDocument.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerDocument.setLastMntBy(userDetails.getUserId());
		customerDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerDocument.setVersion((customerDocumentService.getVersion(customerDocument.getCustID(),
				customerDocument.getCustDocCategory())) + 1);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(customerDocument,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerDocumentService.doApprove(auditHeader);
		 response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			if(StringUtils.equals(customerDocument.getCustDocCategory(), "03")){
			customerDetailsService.updateCustCRCPR(customerDocument.getCustDocTitle(),customerDocument.getCustID());
			}
			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}
	
	/**
	 * delete the CustomerDocument.
	 * @param customerDocument
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerDocument(CustomerDocument customerDocument) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try{
		CustomerDocument curCustomerDocument = customerDocumentService.getApprovedCustomerDocumentById(customerDocument.getCustID(), customerDocument.getCustDocCategory());

		LoggedInUser userDetails = new LoggedInUser();
		userDetails.setUsrLanguage("EN");
		curCustomerDocument.setUserDetails(userDetails);
		curCustomerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		curCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		curCustomerDocument.setNewRecord(false);
		curCustomerDocument.setSourceId(APIConstants.FINSOURCE_ID_API);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(curCustomerDocument,PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerDocumentService.doApprove(auditHeader);
		response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {

			response = APIErrorHandlerService.getSuccessStatus();
		}
		}catch(Exception e){
			logger.error("Exception:"+e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}
	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerPhonenumber
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerPhoneNumber aCustomerPhonenumber, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerPhonenumber.getBefImage(), aCustomerPhonenumber);
		return new AuditHeader(String.valueOf(aCustomerPhonenumber.getPhoneCustID()),
				String.valueOf(aCustomerPhonenumber.getPhoneCustID()), null, null, auditDetail,
				aCustomerPhonenumber.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerBankInfo
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerBankInfo aCustomerBankInfo, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerBankInfo.getBefImage(), aCustomerBankInfo);
		return new AuditHeader(String.valueOf(aCustomerBankInfo.getCustID()),
				String.valueOf(aCustomerBankInfo.getCustID()), null, null, auditDetail,
				aCustomerBankInfo.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}
	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerAddres
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerAddres aCustomerAddres, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerAddres.getBefImage(), aCustomerAddres);
		return new AuditHeader(String.valueOf(aCustomerAddres.getCustID()),
				String.valueOf(aCustomerAddres.getCustID()), null, null, auditDetail,
				aCustomerAddres.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}
	
	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerEMail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerEMail aCustomerEMail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerEMail.getBefImage(), aCustomerEMail);
		return new AuditHeader(String.valueOf(aCustomerEMail.getCustID()),
				String.valueOf(aCustomerEMail.getCustID()), null, null, auditDetail,
				aCustomerEMail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}
	
	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerIncome
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerIncome aCustomerIncome, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerIncome.getBefImage(), aCustomerIncome);
		return new AuditHeader(String.valueOf(aCustomerIncome.getCustID()),
				String.valueOf(aCustomerIncome.getCustID()), null, null, auditDetail,
				aCustomerIncome.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}
	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerChequeInfo
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerChequeInfo aCustomerChequeInfo, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerChequeInfo.getBefImage(), aCustomerChequeInfo);
		return new AuditHeader(String.valueOf(aCustomerChequeInfo.getCustID()),
				String.valueOf(aCustomerChequeInfo.getCustID()), null, null, auditDetail,
				aCustomerChequeInfo.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}
	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerExtLiability
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerExtLiability aCustomerExtLiability, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerExtLiability.getBefImage(), aCustomerExtLiability);
		return new AuditHeader(String.valueOf(aCustomerExtLiability.getCustID()),
				String.valueOf(aCustomerExtLiability.getCustID()), null, null, auditDetail,
				aCustomerExtLiability.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}
	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerDocument
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerDocument aCustomerDocument, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerDocument.getBefImage(), aCustomerDocument);
		return new AuditHeader(String.valueOf(aCustomerDocument.getCustID()),
				String.valueOf(aCustomerDocument.getCustID()), null, null, auditDetail,
				aCustomerDocument.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}
	
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setCustomerPhoneNumberService(CustomerPhoneNumberService customerPhoneNumberService) {
		this.customerPhoneNumberService = customerPhoneNumberService;
	}


	public void setCustomerAddresService(CustomerAddresService customerAddresService) {
		this.customerAddresService = customerAddresService;
	}
	
	public void setCustomerEMailService(CustomerEMailService customerEMailService) {
		this.customerEMailService = customerEMailService;
	}

	public void setCustomerIncomeService(CustomerIncomeService customerIncomeService) {
		this.customerIncomeService = customerIncomeService;
	}
	public void setCustomerBankInfoDAO(CustomerBankInfoDAO customerBankInfoDAO) {
		this.customerBankInfoDAO = customerBankInfoDAO;
	}

	public void setCustomerChequeInfoDAO(CustomerChequeInfoDAO customerChequeInfoDAO) {
		this.customerChequeInfoDAO = customerChequeInfoDAO;
	}
	
	public void setCustomerExtLiabilityDAO(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
	}
	public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}

	public void setCustomerBankInfoService(CustomerBankInfoService customerBankInfoService) {
		this.customerBankInfoService = customerBankInfoService;
	}

	public CustomerChequeInfoService getCustomerChequeInfoService() {
		return customerChequeInfoService;
	}

	public void setCustomerChequeInfoService(CustomerChequeInfoService customerChequeInfoService) {
		this.customerChequeInfoService = customerChequeInfoService;
	}

	public CustomerExtLiabilityService getCustomerExtLiabilityService() {
		return customerExtLiabilityService;
	}

	public void setCustomerExtLiabilityService(CustomerExtLiabilityService customerExtLiabilityService) {
		this.customerExtLiabilityService = customerExtLiabilityService;
	}
	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

}
