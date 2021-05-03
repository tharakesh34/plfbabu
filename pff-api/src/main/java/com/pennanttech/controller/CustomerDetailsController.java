package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.customermasters.CustomerBankInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerCardSalesInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.customermasters.CustomerGstDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.BankInfoSubDetail;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.CustCardSalesDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerGSTDetails;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.ExtLiabilityPaymentdetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.service.customermasters.CustomerBankInfoService;
import com.pennant.backend.service.customermasters.CustomerCardSalesInfoService;
import com.pennant.backend.service.customermasters.CustomerChequeInfoService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.service.customermasters.CustomerExtLiabilityService;
import com.pennant.backend.service.customermasters.CustomerGstService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.customermasters.CustomerPhoneNumberService;
import com.pennant.backend.service.customermasters.validation.CustomerBankInfoValidation;
import com.pennant.backend.service.customermasters.validation.CustomerChequeInfoValidation;
import com.pennant.backend.service.customermasters.validation.CustomerExtLiabilityValidation;
import com.pennant.backend.service.customermasters.validation.CustomerGstInfoValidation;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.customer.CustomerBankInfoDetail;
import com.pennanttech.ws.model.customer.CustomerCardSaleInfoDetails;
import com.pennanttech.ws.model.customer.CustomerChequeInfoDetail;
import com.pennanttech.ws.model.customer.CustomerExtLiabilityDetail;
import com.pennanttech.ws.model.customer.CustomerExtendedFieldDetails;
import com.pennanttech.ws.model.customer.CustomerGstInfoDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class CustomerDetailsController extends GenericService<Object> {
	private final Logger logger = LogManager.getLogger(CustomerDetailsController.class);

	private CustomerDetailsService customerDetailsService;
	private CustomerPhoneNumberService customerPhoneNumberService;
	private CustomerAddresService customerAddresService;
	private CustomerEMailService customerEMailService;
	private CustomerIncomeService customerIncomeService;
	private CustomerBankInfoDAO customerBankInfoDAO;
	private CustomerGstDetailDAO customerGstDetailDAO;
	private CustomerCardSalesInfoDAO customerCardSalesInfoDAO;
	private CustomerChequeInfoDAO customerChequeInfoDAO;
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	private CustomerDocumentService customerDocumentService;
	private CustomerBankInfoService customerBankInfoService;
	private CustomerGstService customerGstService;
	private CustomerCardSalesInfoService customerCardSalesInfoService;
	private CustomerChequeInfoService customerChequeInfoService;
	private CustomerExtLiabilityService customerExtLiabilityService;
	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;

	/**
	 * get the Customer PhoneNumbers By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerPhoneNumbers(String cif) {
		logger.debug("Entering");
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
		WSReturnStatus response = null;
		try {
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
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerPhoneNumber, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
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
		try {
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
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerPhoneNumber, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
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
		try {
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
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(curPhoneNumber, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
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
	 * 
	 * @param customerAddress
	 */

	public WSReturnStatus addCustomerAddress(CustomerAddres customerAddress, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = new WSReturnStatus();
		try {
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
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerAddress, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * Method for update CustomerAddress in PLF system.
	 * 
	 * @param customerAddres
	 * @throws ServiceException
	 */

	public WSReturnStatus updateCustomerAddress(CustomerAddres customerAddres, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
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
			customerAddres.setVersion(
					(customerAddresService.getVersion(customerAddres.getCustID(), customerAddres.getCustAddrType()))
							+ 1);
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerAddres, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete the CustomerAddress.
	 * 
	 * @param customerAddres
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerAddress(CustomerAddres customerAddres) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			// get the CustomerAddress by the CustID and Addrtype
			CustomerAddres curCustomerAddres = customerAddresService
					.getApprovedCustomerAddresById(customerAddres.getCustID(), customerAddres.getCustAddrType());

			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			curCustomerAddres.setUserDetails(userDetails);
			curCustomerAddres.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curCustomerAddres.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			curCustomerAddres.setNewRecord(false);
			curCustomerAddres.setSourceId(APIConstants.FINSOURCE_ID_API);
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(curCustomerAddres, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
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
	 * 
	 * @param customerEMail
	 */

	public WSReturnStatus addCustomerEmail(CustomerEMail customerEMail, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
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
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerEMail, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;

	}

	/**
	 * Method for update CustomerEmail in PLF system.
	 * 
	 * @param customerEMail
	 */

	public WSReturnStatus updateCustomerEmail(CustomerEMail customerEMail, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
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
			customerEMail.setVersion(
					(customerEMailService.getVersion(customerEMail.getCustID(), customerEMail.getCustEMailTypeCode()))
							+ 1);
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerEMail, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete the CustomerEMail.
	 * 
	 * @param customerEmail
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerEmail(CustomerEMail customerEmail) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			CustomerEMail curCustomerEMail = customerEMailService
					.getApprovedCustomerEMailById(customerEmail.getCustID(), customerEmail.getCustEMailTypeCode());

			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			curCustomerEMail.setUserDetails(userDetails);
			curCustomerEMail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curCustomerEMail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			curCustomerEMail.setNewRecord(false);
			curCustomerEMail.setSourceId(APIConstants.FINSOURCE_ID_API);
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(curCustomerEMail, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * get the Customer Incomes By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerIncomes(String cif) {
		logger.debug("Entering");
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustomerIncome> customerIncomeList = customerIncomeService.getCustomerIncomes(customer.getCustID(),
					false);
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
	 * 
	 * @param customerIncome
	 */

	public WSReturnStatus addCustomerIncome(CustomerIncome customerIncome, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			customerIncome.setUserDetails(userDetails);
			customerIncome.setCustId(prvCustomer.getCustID());
			//customerIncome.setMargin(new BigDecimal(0));
			customerIncome.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			customerIncome.setSourceId(APIConstants.FINSOURCE_ID_API);
			customerIncome.setCustCif(cif);
			customerIncome.setNewRecord(true);
			customerIncome.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			customerIncome.setLastMntBy(userDetails.getUserId());
			customerIncome.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			customerIncome.setVersion(1);
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerIncome, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for update CustomerIncome in PLF system.
	 * 
	 * @param customerIncome
	 */

	public WSReturnStatus updateCustomerIncome(CustomerIncome customerIncome, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			customerIncome.setUserDetails(userDetails);
			customerIncome.setCustId(prvCustomer.getCustID());
			customerIncome.setCustCif(cif);
			customerIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			customerIncome.setNewRecord(false);
			customerIncome.setSourceId(APIConstants.FINSOURCE_ID_API);
			customerIncome.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			customerIncome.setLastMntBy(userDetails.getUserId());
			customerIncome.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			customerIncome.setVersion(customerIncomeService.getVersion(customerIncome) + 1);
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerIncome, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete the CustomerIncome.
	 * 
	 * @param customerIncome
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerIncome(CustomerIncome customerIncome) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			CustomerIncome curCustomerIncom = customerIncomeService.getApprovedCustomerIncomeById(customerIncome);
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			curCustomerIncom.setUserDetails(userDetails);
			curCustomerIncom.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curCustomerIncom.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			curCustomerIncom.setNewRecord(false);
			curCustomerIncom.setSourceId(APIConstants.FINSOURCE_ID_API);
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(curCustomerIncom, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
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
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		List<BankInfoDetail> bankInfoDetailList = null;
		List<BankInfoSubDetail> bankInfoSubDetailList = null;
		try {
			List<CustomerBankInfo> customerBankInfoList = customerBankInfoService
					.getApprovedBankInfoByCustomerId(customer.getCustID());
			for (CustomerBankInfo customerBankInfo : customerBankInfoList) {
				bankInfoDetailList = customerBankInfoService.getBankInfoDetailById(customerBankInfo.getBankId());
				for (BankInfoDetail bankInfoDetail : bankInfoDetailList) {
					customerBankInfoService.getBankInfoSubDetailById(bankInfoDetail.getBankId(),
							bankInfoDetail.getMonthYear());
				}
			}

			if (customerBankInfoList != null && !customerBankInfoList.isEmpty() && bankInfoDetailList != null
					&& !bankInfoDetailList.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				response.setCustomerBankInfoList(customerBankInfoList);
				for (CustomerBankInfo customerBankInfo : customerBankInfoList) {
					customerBankInfo.setBankInfoDetails(bankInfoDetailList);
					for (BankInfoDetail bankInfoDetail : bankInfoDetailList) {
						bankInfoDetail.setBankInfoSubDetails(bankInfoSubDetailList);
					}
				}
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
		try {
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
			for (BankInfoDetail bankInfoDetail : customerBankInfo.getBankInfoDetails()) {
				bankInfoDetail.setUserDetails(userDetails);
				bankInfoDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				bankInfoDetail.setNewRecord(true);
				bankInfoDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				bankInfoDetail.setLastMntBy(userDetails.getUserId());
				// bankInfoDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
				bankInfoDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				bankInfoDetail.setVersion(1);
				for (BankInfoSubDetail bankInfoSubDetail : bankInfoDetail.getBankInfoSubDetails()) {
					bankInfoSubDetail.setUserDetails(userDetails);
					bankInfoSubDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					bankInfoSubDetail.setNewRecord(false);
					bankInfoSubDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					bankInfoSubDetail.setLastMntBy(userDetails.getUserId());
					// bankInfoDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
					bankInfoSubDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					bankInfoSubDetail.setVersion(1);
				}

			}
			CustomerBankInfoValidation validation = new CustomerBankInfoValidation(customerBankInfoDAO);
			AuditHeader auditHeader = validation
					.bankInfoValidation(getAuditHeader(customerBankInfo, PennantConstants.TRAN_WF), "doApprove");

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new CustomerBankInfoDetail();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				//get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				//set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				auditHeader = customerBankInfoService.doApprove(auditHeader);
				response = new CustomerBankInfoDetail();
				CustomerBankInfo custBankInfo = (CustomerBankInfo) auditHeader.getAuditDetail().getModelData();
				response.setBankId(custBankInfo.getBankId());
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (Exception e) {
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
	 * 
	 * @param customerBankInfo
	 */

	public WSReturnStatus updateCustomerBankingInformation(CustomerBankInfo customerBankInfo, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
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
			for (BankInfoDetail bankInfoDetail : customerBankInfo.getBankInfoDetails()) {
				bankInfoDetail.setUserDetails(userDetails);
				bankInfoDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				bankInfoDetail.setNewRecord(false);
				bankInfoDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				bankInfoDetail.setLastMntBy(userDetails.getUserId());
				// bankInfoDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
				bankInfoDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				bankInfoDetail.setVersion(customerBankInfo.getVersion());
				for (BankInfoSubDetail bankInfoSubDetail : bankInfoDetail.getBankInfoSubDetails()) {
					bankInfoSubDetail.setUserDetails(userDetails);
					bankInfoSubDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					bankInfoSubDetail.setNewRecord(false);
					bankInfoSubDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					bankInfoSubDetail.setLastMntBy(userDetails.getUserId());
					// bankInfoDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
					bankInfoSubDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					bankInfoSubDetail.setVersion(customerBankInfo.getVersion());
				}
			}
			CustomerBankInfoValidation validation = new CustomerBankInfoValidation(customerBankInfoDAO);
			AuditHeader auditHeader = validation
					.bankInfoValidation(getAuditHeader(customerBankInfo, PennantConstants.TRAN_WF), "doApprove");
			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				//get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				//set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				customerBankInfoService.doApprove(auditHeader);
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete the CustomerBankingInformation.
	 * 
	 * @param customerBankInfo
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerBankingInformation(CustomerBankInfo customerBankInfo) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			CustomerBankInfo curCustBankInfo = customerBankInfoService
					.getCustomerBankInfoById(customerBankInfo.getBankId());
			List<BankInfoDetail> bankInfoDetailList = customerBankInfoService
					.getBankInfoDetailById(curCustBankInfo.getBankId());
			List<BankInfoSubDetail> bnkInfoSubDetailList = new ArrayList<>();
			if (curCustBankInfo != null && !bankInfoDetailList.isEmpty() && bankInfoDetailList != null) {
				for (BankInfoDetail bankInfoDetail : bankInfoDetailList) {
					bnkInfoSubDetailList = customerBankInfoService.getBankInfoSubDetailById(bankInfoDetail.getBankId(),
							bankInfoDetail.getMonthYear());
					if (bnkInfoSubDetailList != null) {
						bankInfoDetail.setBankInfoSubDetails(bnkInfoSubDetailList);
					}
				}

				curCustBankInfo.setBankInfoDetails(bankInfoDetailList);

			}
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			curCustBankInfo.setUserDetails(userDetails);
			curCustBankInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curCustBankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			curCustBankInfo.setSourceId(APIConstants.FINSOURCE_ID_API);
			curCustBankInfo.setNewRecord(false);
			CustomerBankInfoValidation validation = new CustomerBankInfoValidation(customerBankInfoDAO);
			AuditHeader auditHeader = validation
					.bankInfoValidation(getAuditHeader(curCustBankInfo, PennantConstants.TRAN_WF), "doApprove");
			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				//get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				//set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				customerBankInfoService.doApprove(auditHeader);
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * get CustomerGstInformation By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerGstInformation(String cif) {
		logger.debug("Entering");
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustomerGST> custometGSTInfoList = customerGstService
					.getApprovedGstInfoByCustomerId(customer.getCustID());
			for (int i = 0; i < custometGSTInfoList.size(); i++) {
				List<CustomerGSTDetails> CustomerGSTDetailslist = customerGstService
						.getCustomerGstDeatailsByCustomerId(custometGSTInfoList.get(i).getId(), "");
				custometGSTInfoList.get(i).setCustomerGSTDetailslist(CustomerGSTDetailslist);
			}

			if (custometGSTInfoList != null && !custometGSTInfoList.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				response.setCustomerGstList(custometGSTInfoList);
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
	 * Method for create Customer CustomerGstInfoDetail in PLF system.
	 * 
	 * @param CustomerGstInfoDetail
	 * 
	 */

	public CustomerGstInfoDetail addCustomerGstInformation(CustomerGST customerGST, String cif) {
		CustomerGstInfoDetail response = null;
		try {
			logger.debug("Entering");
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

			Customer customer = customerDetailsService.getCustomerByCIF(cif);
			customerGST.setCustId(customer.getCustID());
			// customerGST.setLovDescCustCIF(cif);
			customerGST.setNewRecord(true);
			customerGST.setVersion(1);
			customerGST.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			customerGST.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			customerGST.setLastMntBy(userDetails.getUserId());
			customerGST.setSourceId(APIConstants.FINSOURCE_ID_API);
			customerGST.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			CustomerGstInfoValidation validation = new CustomerGstInfoValidation(customerGstDetailDAO);
			AuditHeader auditHeader = validation
					.gstInfoValidation(getAuditHeader(customerGST, PennantConstants.TRAN_WF), "doApprove");

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new CustomerGstInfoDetail();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				// get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				// set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				auditHeader = customerGstService.doApprove(auditHeader);
				response = new CustomerGstInfoDetail();
				CustomerGST customerGstInfo = (CustomerGST) auditHeader.getAuditDetail().getModelData();
				response.setId(customerGstInfo.getId());
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerGstInfoDetail();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");

		return response;

	}

	/**
	 * Method for update CustomerGSTInformation in PLF system.
	 * 
	 * @param customerBankInfo
	 */

	public WSReturnStatus updateCustomerGstInformation(CustomerGST customerGST, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			customerGST.setUserDetails(userDetails);
			customerGST.setCustId(prvCustomer.getCustID());
			// customerGST.setLovDescCustCIF(cif);
			customerGST.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			customerGST.setNewRecord(false);
			customerGST.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			customerGST.setLastMntBy(userDetails.getUserId());
			customerGST.setSourceId(APIConstants.FINSOURCE_ID_API);
			customerGST.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			customerGST.setVersion((customerGstService.getVersion(customerGST.getId())) + 1);
			/*
			 * for (CustomerGSTDetails customerGSTDetails : customerGST.getCustomerGSTDetailslist()) {
			 * customerGSTDetails.setVersion(customerGST.getVersion()); }
			 */

			List<CustomerGSTDetails> cutomerGSTDetailistdb = customerGstService
					.getCustomerGstDeatailsByCustomerId(customerGST.getId(), "");

			for (CustomerGSTDetails customerGSTDetails : customerGST.getCustomerGSTDetailslist()) {
				int i = 0;
				while (!cutomerGSTDetailistdb.isEmpty()) {
					if (StringUtils.equals(customerGSTDetails.getFrequancy(),
							cutomerGSTDetailistdb.get(i).getFrequancy())) {
						customerGSTDetails.setVersion(cutomerGSTDetailistdb.get(i).getVersion() + 1);
						break;
					}
					i++;
				}

			}
			CustomerGstInfoValidation validation = new CustomerGstInfoValidation(customerGstDetailDAO);
			/*
			 * AuditHeader auditHeader = validation .gstInfoValidation(getAuditHeader(customerGST,
			 * PennantConstants.TRAN_WF), "doApprove");
			 */
			AuditHeader auditHeader = getAuditHeader(customerGST, PennantConstants.TRAN_WF);
			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				// get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				// set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				customerGstService.doApprove(auditHeader);
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete the CustomerGSTInformation.
	 * 
	 * @param CustomerGSTInformation
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerGSTInformation(CustomerGST CustomerGSTInfo) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			CustomerGST curCustomerGST = customerGstService.getCustomerGstDeatailsByCustomerId(CustomerGSTInfo.getId());

			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			curCustomerGST.setUserDetails(userDetails);
			curCustomerGST.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curCustomerGST.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			curCustomerGST.setSourceId(APIConstants.FINSOURCE_ID_API);
			curCustomerGST.setNewRecord(false);
			CustomerGstInfoValidation validation = new CustomerGstInfoValidation(customerGstDetailDAO);
			AuditHeader auditHeader = getAuditHeader(curCustomerGST, PennantConstants.TRAN_WF);

			//AuditHeader auditHeader = validation
			//.gstInfoValidation(getAuditHeader(curCustomerGST, PennantConstants.TRAN_WF), "doApprove");
			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				// get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				// set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);

				getCustomerGstDetailDAO().delete(curCustomerGST.getId(), "");
				customerGstService.doApprove(auditHeader);
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * Method for create Customer CustomerGstInfoDetail in PLF system.
	 * 
	 * @param CustomerGstInfoDetail
	 * 
	 */

	public CustomerCardSaleInfoDetails addCardSalesInformation(CustCardSales custCardSales, String cif) {
		CustomerCardSaleInfoDetails response = null;
		try {
			logger.debug("Entering");
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

			Customer customer = customerDetailsService.getCustomerByCIF(cif);
			custCardSales.setCustID(customer.getCustID());
			// customerGST.setLovDescCustCIF(cif);
			custCardSales.setNewRecord(true);
			custCardSales.setVersion(1);
			custCardSales.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			custCardSales.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			custCardSales.setLastMntBy(userDetails.getUserId());
			custCardSales.setSourceId(APIConstants.FINSOURCE_ID_API);
			custCardSales.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			AuditHeader auditHeader = getAuditHeader(custCardSales, PennantConstants.TRAN_WF);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new CustomerCardSaleInfoDetails();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				// get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				// set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				auditHeader = customerCardSalesInfoService.doApprove(auditHeader);
				response = new CustomerCardSaleInfoDetails();
				CustCardSales custCardSalesinfo = (CustCardSales) auditHeader.getAuditDetail().getModelData();
				response.setId(custCardSalesinfo.getId());
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerCardSaleInfoDetails();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");

		return response;

	}

	/**
	 * Method for update CustomerGSTInformation in PLF system.
	 * 
	 * @param customerBankInfo
	 */

	public WSReturnStatus updateCardSalestInformation(CustCardSales custCardSales, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			custCardSales.setUserDetails(userDetails);
			custCardSales.setCustID(prvCustomer.getCustID());
			custCardSales.setLovDescCustCIF(cif);
			custCardSales.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			custCardSales.setNewRecord(false);
			custCardSales.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			custCardSales.setLastMntBy(userDetails.getUserId());
			custCardSales.setSourceId(APIConstants.FINSOURCE_ID_API);
			custCardSales.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			custCardSales.setVersion((customerCardSalesInfoService.getVersion(custCardSales.getId())) + 1);

			for (CustCardSalesDetails custCardSalesDetails : custCardSales.getCustCardMonthSales()) {
				custCardSalesDetails.setVersion(custCardSales.getVersion());
			}

			AuditHeader auditHeader = getAuditHeader(custCardSales, PennantConstants.TRAN_WF);
			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				// get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				// set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				customerCardSalesInfoService.doApprove(auditHeader);
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * get CustomerGstInformation By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCardSalesInformation(String cif) {
		logger.debug("Entering");
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustCardSales> custCardSaleslist = customerCardSalesInfoService
					.getApprovedCardSalesInfoByCustomerId(customer.getCustID());
			for (int i = 0; i < custCardSaleslist.size(); i++) {
				List<CustCardSalesDetails> custCardSalesDetailsList = customerCardSalesInfoService
						.getCardSalesInfoSubDetailById(custCardSaleslist.get(i).getId(), "");
				custCardSaleslist.get(i).setCustCardMonthSales(custCardSalesDetailsList);
			}
			if (custCardSaleslist != null && !custCardSaleslist.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				response.setCustCardSales(custCardSaleslist);
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
	 * delete the CustomerGSTInformation.
	 * 
	 * @param CustomerGSTInformation
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCardSaleInformation(CustCardSales custCardSales) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			CustCardSales curCustCardSales = customerCardSalesInfoService
					.getCustomerCardSalesInfoById(custCardSales.getId());

			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			curCustCardSales.setUserDetails(userDetails);
			curCustCardSales.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curCustCardSales.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			curCustCardSales.setSourceId(APIConstants.FINSOURCE_ID_API);
			curCustCardSales.setNewRecord(false);
			AuditHeader auditHeader = getAuditHeader(curCustCardSales, PennantConstants.TRAN_WF);

			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				// get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				// set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);

				getCustomerCardSalesInfoDAO().delete(curCustCardSales.getId(), "");
				customerCardSalesInfoService.doApprove(auditHeader);
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
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
		try {
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
			AuditHeader auditHeader = validation
					.chequeInfoValidation(getAuditHeader(customerChequeInfo, PennantConstants.TRAN_WF), "doApprove");
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new CustomerChequeInfoDetail();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				//get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				//set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				auditHeader = customerChequeInfoService.doApprove(auditHeader);
				response = new CustomerChequeInfoDetail();
				CustomerChequeInfo custCustomerChequeInfo = (CustomerChequeInfo) auditHeader.getAuditDetail()
						.getModelData();
				response.setChequeSeq(custCustomerChequeInfo.getChequeSeq());
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (Exception e) {
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
	 * 
	 * @param customerChequeInfo
	 */

	public WSReturnStatus updateCustomerAccountBehaviour(CustomerChequeInfo customerChequeInfo, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
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
			AuditHeader auditHeader = validation
					.chequeInfoValidation(getAuditHeader(customerChequeInfo, PennantConstants.TRAN_WF), "doApprove");
			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				//get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				//set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				customerChequeInfoService.doApprove(auditHeader);
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete the CustomerAccountBehaviour.
	 * 
	 * @param customerChequeInfo
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerAccountBehaviour(CustomerChequeInfo customerChequeInfo) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			CustomerChequeInfo curCustChequeInfo = customerChequeInfoDAO
					.getCustomerChequeInfoById(customerChequeInfo.getCustID(), customerChequeInfo.getChequeSeq(), "");

			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			curCustChequeInfo.setUserDetails(userDetails);
			curCustChequeInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curCustChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			curCustChequeInfo.setSourceId(APIConstants.FINSOURCE_ID_API);
			curCustChequeInfo.setNewRecord(false);
			CustomerChequeInfoValidation validation = new CustomerChequeInfoValidation(customerChequeInfoDAO);
			AuditHeader auditHeader = validation
					.chequeInfoValidation(getAuditHeader(curCustChequeInfo, PennantConstants.TRAN_WF), "doApprove");
			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				//get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				//set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				customerChequeInfoService.doApprove(auditHeader);
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;

	}

	/**
	 * Method for create Customer ExtLiability in PLF system.
	 * 
	 * @param liability
	 * 
	 */

	public CustomerExtLiabilityDetail addCustomerExternalLiability(CustomerExtLiability liability, String cif) {
		logger.debug(Literal.ENTERING);
		CustomerExtLiabilityDetail response = null;

		try {
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			Customer customer = customerDetailsService.getCustomerByCIF(cif);
			liability.setCustId(customer.getCustID());
			liability.setCustCif(cif);
			liability.setNewRecord(true);
			liability.setVersion(1);
			liability.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			liability.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			liability.setSourceId(APIConstants.FINSOURCE_ID_API);
			liability.setLastMntBy(userDetails.getUserId());
			liability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			if (liability.getExtLiabilitiesPayments() != null && !liability.getExtLiabilitiesPayments().isEmpty()) {
				for (ExtLiabilityPaymentdetails extLiabilityPaymentdetails : liability.getExtLiabilitiesPayments()) {
					liability.setNewRecord(true);
					extLiabilityPaymentdetails.setVersion(1);
					extLiabilityPaymentdetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					extLiabilityPaymentdetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					//extLiabilityPaymentdetails.setSourceId(APIConstants.FINSOURCE_ID_API);
					extLiabilityPaymentdetails.setLastMntBy(userDetails.getUserId());
					extLiabilityPaymentdetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				}
			}
			liability.setLinkId(customerExtLiabilityDAO.getLinkId(liability.getCustId()));
			if (liability.getLinkId() == 0) {
				customerExtLiabilityDAO.setLinkId(liability);
			}
			CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);

			AuditHeader auditHeader = validation
					.extLiabilityValidation(getAuditHeader(liability, PennantConstants.TRAN_WF), "doApprove");
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new CustomerExtLiabilityDetail();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				// get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				// set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				auditHeader = customerExtLiabilityService.doApprove(auditHeader);
				response = new CustomerExtLiabilityDetail();
				CustomerExtLiability tempLiability = (CustomerExtLiability) auditHeader.getAuditDetail().getModelData();
				response.setLiabilitySeq(tempLiability.getSeqNo());
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (Exception e) {
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
	 * 
	 * @param liability
	 */

	public WSReturnStatus updateCustomerExternalLiability(CustomerExtLiability liability, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);
			liability.setCustId(prvCustomer.getCustID());
			liability.setLinkId(customerExtLiabilityDAO.getLinkId(prvCustomer.getCustID()));
			CustomerExtLiability curliability = customerExtLiabilityService.getLiability(liability);
			List<CustomerExtLiability> customerExtLiabilityList = customerExtLiabilityService.getLiabilities(liability);
			if (customerExtLiabilityList != null && !customerExtLiabilityList.isEmpty()) {
				for (CustomerExtLiability customerExtLiability : customerExtLiabilityList) {
					if (liability.getId() == customerExtLiability.getId()) {
						for (ExtLiabilityPaymentdetails extLiabilityPaymentdetails : liability
								.getExtLiabilitiesPayments()) {
							extLiabilityPaymentdetails.setLiabilityId(liability.getId());
						}
						liability.setExtLiabilitiesPayments(liability.getExtLiabilitiesPayments());
					}
				}

			}
			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

			liability.setUserDetails(userDetails);
			liability.setCustId(prvCustomer.getCustID());
			liability.setCustCif(cif);
			liability.setRecordType(PennantConstants.RCD_UPD);
			liability.setNewRecord(false);
			liability.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			liability.setSourceId(APIConstants.FINSOURCE_ID_API);
			liability.setLastMntBy(userDetails.getUserId());
			liability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			liability.setBefImage(curliability);
			liability.setVersion((customerExtLiabilityDAO.getVersion(liability.getLinkId(), liability.getSeqNo())) + 1);
			if (liability.getExtLiabilitiesPayments() != null && !liability.getExtLiabilitiesPayments().isEmpty()) {
				for (ExtLiabilityPaymentdetails extLiabilityPaymentdetails : liability.getExtLiabilitiesPayments()) {
					extLiabilityPaymentdetails.setUserDetails(userDetails);
					// extLiabilityPaymentdetails.setCustId(prvCustomer.getCustID());
					// extLiabilityPaymentdetails.setCustCif(cif);
					extLiabilityPaymentdetails.setRecordType(PennantConstants.RCD_UPD);
					extLiabilityPaymentdetails.setNewRecord(false);
					extLiabilityPaymentdetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					// extLiabilityPaymentdetails.setSourceId(APIConstants.FINSOURCE_ID_API);
					extLiabilityPaymentdetails.setLastMntBy(userDetails.getUserId());
					extLiabilityPaymentdetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					extLiabilityPaymentdetails.setVersion(liability.getVersion());

				}
			}

			CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
			AuditHeader auditHeader = validation
					.extLiabilityValidation(getAuditHeader(liability, PennantConstants.TRAN_WF), "doApprove");
			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				//get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				//set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				customerExtLiabilityService.doApprove(auditHeader);
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete the CustomerExternalLiability.
	 * 
	 * @param liability
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerExternalLiability(CustomerExtLiability liability) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			CustomerExtLiability curliability = customerExtLiabilityService.getLiability(liability);

			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			curliability.setUserDetails(userDetails);
			curliability.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curliability.setSourceId(APIConstants.FINSOURCE_ID_API);
			curliability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			curliability.setNewRecord(false);
			curliability.setBefImage(curliability);
			CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
			AuditHeader auditHeader = validation
					.extLiabilityValidation(getAuditHeader(curliability, PennantConstants.TRAN_WF), "doApprove");
			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				// get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				// set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				customerExtLiabilityService.doApprove(auditHeader);
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
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
		logger.debug(Literal.ENTERING);
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			CustomerExtLiability temp = new CustomerExtLiability();
			temp.setCustId(customer.getCustID());
			List<CustomerExtLiability> customerExtLiabilityList = customerExtLiabilityService.getLiabilities(temp);
			if (customerExtLiabilityList != null && !customerExtLiabilityList.isEmpty()) {
				for (CustomerExtLiability customerExtLiability : customerExtLiabilityList) {
					List<ExtLiabilityPaymentdetails> extLiabilityPaymentdetailsList = customerExtLiabilityDAO
							.getExtLiabilitySubDetailById(customerExtLiability.getId(), "");
					if (extLiabilityPaymentdetailsList != null && !extLiabilityPaymentdetailsList.isEmpty())
						customerExtLiability.setExtLiabilitiesPayments(extLiabilityPaymentdetailsList);
				}
			}

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
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerDetails();
			response.setCustomer(null);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
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
		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustomerDocument> customerDocumentsList = customerDocumentService
					.getApprovedCustomerDocumentById(customer.getCustID());
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

	/**
	 * Method for create CustomerDocument in PLF system.
	 * 
	 * @param customerDocument
	 */

	public WSReturnStatus addCustomerDocument(CustomerDocument customerDocument, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
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

			if (StringUtils.equalsIgnoreCase(customerDocument.getCustDocType(), "JPG")
					|| StringUtils.equalsIgnoreCase(customerDocument.getCustDocType(), "PNG")
					|| StringUtils.equalsIgnoreCase(customerDocument.getCustDocType(), "JPEG")
					|| StringUtils.equalsIgnoreCase(customerDocument.getCustDocType(), "JFIF")) {
				customerDocument.setCustDocType(PennantConstants.DOC_TYPE_IMAGE);
			}
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerDocument, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for update CustomerDocument in PLF system.
	 * 
	 * @param customerDocument
	 */

	public WSReturnStatus updateCustomerDocument(CustomerDocument customerDocument, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
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
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerDocument, PennantConstants.TRAN_WF);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = customerDocumentService.doApprove(auditHeader);
			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				if (StringUtils.equals(customerDocument.getCustDocCategory(), "03")) {
					customerDetailsService.updateCustCRCPR(customerDocument.getCustDocTitle(),
							customerDocument.getCustID());
				}
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete the CustomerDocument.
	 * 
	 * @param customerDocument
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerDocument(CustomerDocument customerDocument) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			CustomerDocument curCustomerDocument = customerDocumentService.getApprovedCustomerDocumentById(
					customerDocument.getCustID(), customerDocument.getCustDocCategory());

			LoggedInUser userDetails = new LoggedInUser();
			userDetails.setUsrLanguage("EN");
			curCustomerDocument.setUserDetails(userDetails);
			curCustomerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curCustomerDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			curCustomerDocument.setNewRecord(false);
			curCustomerDocument.setSourceId(APIConstants.FINSOURCE_ID_API);
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(curCustomerDocument, PennantConstants.TRAN_WF);
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
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for create Customer Extended in PLF system.
	 * 
	 * @param ExtendedFields
	 * 
	 */

	public CustomerExtendedFieldDetails addCustomerExtendedFields(
			CustomerExtendedFieldDetails customerExtendedFieldDetails, Customer customerDetails) {
		int maxSeqNo = 0;
		CustomerExtendedFieldDetails response = new CustomerExtendedFieldDetails();
		String tableName = getTableName(ExtendedFieldConstants.MODULE_CUSTOMER, customerDetails.getCustCtgCode(), "");
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
		try {
			// process Extended field details
			// Get the ExtendedFieldHeader for given module and subModule
			ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
					ExtendedFieldConstants.MODULE_CUSTOMER, customerDetails.getCustCtgCode(), "");
			customerExtendedFieldDetails.setExtendedFieldHeader(extendedFieldHeader);
			List<ExtendedField> extendedFields = customerExtendedFieldDetails.getExtendedDetails();
			if (extendedFieldHeader != null) {
				try {
					maxSeqNo = extendedFieldRenderDAO.getMaxSeqNoByRef(customerDetails.getCustCIF(),
							tableName.toString());
				} catch (Exception e) {
					maxSeqNo = 0;
				}
				int seqNo = 0;
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(customerDetails.getCustomer().getCustCIF());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				exdFieldRender.setLastMntBy(customerDetails.getLastMntBy());
				if (maxSeqNo == 0) {
					exdFieldRender.setSeqNo(++seqNo);
					exdFieldRender
							.setTypeCode(customerExtendedFieldDetails.getExtendedFieldHeader().getSubModuleName());
					exdFieldRender.setNewRecord(true);
					exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					exdFieldRender.setVersion(1);
				} else {
					ExtendedFieldRender fieldRender = extendedFieldRenderDAO.getExtendedFieldDetails(
							customerDetails.getCustCIF(), maxSeqNo, tableName, TableType.MAIN_TAB.getSuffix());
					exdFieldRender.setSeqNo(fieldRender.getSeqNo());
					exdFieldRender
							.setTypeCode(customerExtendedFieldDetails.getExtendedFieldHeader().getSubModuleName());
					exdFieldRender.setNewRecord(false);
					exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					exdFieldRender.setVersion(fieldRender.getVersion());
				}

				if (extendedFields != null) {
					for (ExtendedField extendedField : extendedFields) {
						Map<String, Object> mapValues = new HashMap<String, Object>();
						if (extendedField.getExtendedFieldDataList() != null) {
							for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
								mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
								exdFieldRender.setMapValues(mapValues);
							}
						} else {
							Map<String, Object> map = new HashMap<String, Object>();
							exdFieldRender.setMapValues(map);
						}
					}
					if (extendedFields.isEmpty()) {
						Map<String, Object> mapValues = new HashMap<String, Object>();
						exdFieldRender.setMapValues(mapValues);
					}
				} else {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					exdFieldRender.setMapValues(mapValues);
				}

				customerExtendedFieldDetails.setExtendedFieldRender(exdFieldRender);

			}
			if (customerExtendedFieldDetails.getExtendedFieldRender() != null) {
				auditDetailMap.put("ExtendedFieldDetails",
						extendedFieldDetailsService.setExtendedFieldsAuditData(
								customerExtendedFieldDetails.getExtendedFieldRender(), PennantConstants.TRAN_WF,
								"doApprove", null));
			}
			List<AuditDetail> details = auditDetailMap.get("ExtendedFieldDetails");
			extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName,
					TableType.MAIN_TAB.getSuffix());
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			logger.debug("Leaving");
			return response;
		} catch (Exception e) {

			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

	}

	private String getTableName(String module, String subModuleName, String event) {
		StringBuilder sb = new StringBuilder();
		sb.append(module);
		sb.append("_");
		sb.append(subModuleName);
		if (StringUtils.trimToNull(event) != null) {
			sb.append("_");
			sb.append(PennantStaticListUtil.getFinEventCode(event));
		}
		sb.append("_ED");
		return sb.toString();
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerPhonenumber
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerPhoneNumber aCustomerPhonenumber, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerPhonenumber.getBefImage(),
				aCustomerPhonenumber);
		return new AuditHeader(String.valueOf(aCustomerPhonenumber.getPhoneCustID()),
				String.valueOf(aCustomerPhonenumber.getPhoneCustID()), null, null, auditDetail,
				aCustomerPhonenumber.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
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
				aCustomerBankInfo.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerGSTInfo
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustCardSales aCustCardSales, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustCardSales.getBefImage(), aCustCardSales);
		return new AuditHeader(String.valueOf(aCustCardSales.getCustID()), String.valueOf(aCustCardSales.getCustID()),
				null, null, auditDetail, aCustCardSales.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerGSTInfo
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerGST aCustomerGST, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerGST.getBefImage(), aCustomerGST);
		return new AuditHeader(String.valueOf(aCustomerGST.getCustId()), String.valueOf(aCustomerGST.getCustId()), null,
				null, auditDetail, aCustomerGST.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
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
		return new AuditHeader(String.valueOf(aCustomerAddres.getCustID()), String.valueOf(aCustomerAddres.getCustID()),
				null, null, auditDetail, aCustomerAddres.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
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
		return new AuditHeader(String.valueOf(aCustomerEMail.getCustID()), String.valueOf(aCustomerEMail.getCustID()),
				null, null, auditDetail, aCustomerEMail.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
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
		return new AuditHeader(String.valueOf(aCustomerIncome.getCustId()), String.valueOf(aCustomerIncome.getCustId()),
				null, null, auditDetail, aCustomerIncome.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
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
				aCustomerChequeInfo.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param liability
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerExtLiability liability, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, liability.getBefImage(), liability);
		return new AuditHeader(String.valueOf(liability.getCustId()), String.valueOf(liability.getCustId()), null, null,
				auditDetail, liability.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
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
				aCustomerDocument.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
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

	public void setCustomerExtLiabilityService(CustomerExtLiabilityService customerExtLiabilityService) {
		this.customerExtLiabilityService = customerExtLiabilityService;
	}

	public CustomerGstDetailDAO getCustomerGstDetailDAO() {
		return customerGstDetailDAO;
	}

	public void setCustomerGstDetailDAO(CustomerGstDetailDAO customerGstDetailDAO) {
		this.customerGstDetailDAO = customerGstDetailDAO;
	}

	public CustomerCardSalesInfoDAO getCustomerCardSalesInfoDAO() {
		return customerCardSalesInfoDAO;
	}

	public void setCustomerCardSalesInfoDAO(CustomerCardSalesInfoDAO customerCardSalesInfoDAO) {
		this.customerCardSalesInfoDAO = customerCardSalesInfoDAO;
	}

	public CustomerGstService getCustomerGstService() {
		return customerGstService;
	}

	public void setCustomerGstService(CustomerGstService customerGstService) {
		this.customerGstService = customerGstService;
	}

	public CustomerCardSalesInfoService getCustomerCardSalesInfoService() {
		return customerCardSalesInfoService;
	}

	public void setCustomerCardSalesInfoService(CustomerCardSalesInfoService customerCardSalesInfoService) {
		this.customerCardSalesInfoService = customerCardSalesInfoService;
	}

	public ExtendedFieldHeaderDAO getExtendedFieldHeaderDAO() {
		return extendedFieldHeaderDAO;
	}

	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	public ExtendedFieldRenderDAO getExtendedFieldRenderDAO() {
		return extendedFieldRenderDAO;
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public ExtendedFieldDetailsService getExtendedFieldDetailsService() {
		return extendedFieldDetailsService;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

}
