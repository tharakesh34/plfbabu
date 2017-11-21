package com.pennanttech.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.dedup.DedupParmDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.service.customermasters.CustomerBankInfoService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.service.customermasters.CustomerEmploymentDetailService;
import com.pennant.backend.service.customermasters.CustomerExtLiabilityService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.customermasters.CustomerPhoneNumberService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.customermasters.validation.CustomerExtLiabilityValidation;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.validation.DeleteValidationGroup;
import com.pennant.validation.PersionalInfoGroup;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.UpdateValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.CustomerController;
import com.pennanttech.controller.CustomerDetailsController;
import com.pennanttech.pffws.CustomerRESTService;
import com.pennanttech.pffws.CustomerSOAPService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.customer.CustAddress;
import com.pennanttech.ws.model.customer.CustEMail;
import com.pennanttech.ws.model.customer.CustPhoneNumber;
import com.pennanttech.ws.model.customer.CustomerBankInfoDetail;
import com.pennanttech.ws.model.customer.CustomerChequeInfoDetail;
import com.pennanttech.ws.model.customer.CustomerDocumentDetail;
import com.pennanttech.ws.model.customer.CustomerExtLiabilityDetail;
import com.pennanttech.ws.model.customer.CustomerIncomeDetail;
import com.pennanttech.ws.model.customer.EmploymentDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class CustomerWebServiceImpl implements  CustomerRESTService,CustomerSOAPService {
	private static final Logger logger = Logger.getLogger(CustomerWebServiceImpl.class);

	private CustomerController				customerController;
	private CustomerDetailsController		customerDetailsController;
	private ValidationUtility				validationUtility;
	private CustomerDetailsService			customerDetailsService;
	private CustomerService					customerService;
	private CustomerEmploymentDetailService	customerEmploymentDetailService;
	private CustomerPhoneNumberService		customerPhoneNumberService;
	private CustomerAddresService			customerAddresService;
	private CustomerEMailService			customerEMailService;
	private CustomerIncomeService			customerIncomeService;
	private CustomerExtLiabilityDAO			customerExtLiabilityDAO;
	private CustomerDocumentService			customerDocumentService;
	private CustomerBankInfoService			customerBankInfoService;
	private CustomerExtLiabilityService		customerExtLiabilityService;
	private CustomerChequeInfoDAO			customerChequeInfoDAO;
	private DedupParmDAO					dedupParmDAO;
	private CustomerDedupDAO				customerDedupDAO;



	/**
	 * Method for create customer in PLF system.
	 * 
	 * @param customerDetails
	 */
	@Override
	public CustomerDetails createCustomer(CustomerDetails customerDetails) throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(customerDetails, SaveValidationGroup.class);
		AuditHeader auditHeader = getAuditHeader(customerDetails, PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		AuditDetail auditDetail = customerDetailsService.doCustomerValidations(auditHeader);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		CustomerDetails response = null;
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerDetails();
				doEmptyResponseObject(response);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return response;
			}
		}
		//call dedup service for customer duplication
		if (customerDetails.isDedupReq()) {
			List<CustomerDedup> dedupList = new ArrayList<CustomerDedup>(1);
			CustomerDedup customerDedup = doSetCustomerDedup(customerDetails);
			List<DedupParm> dedupParmList = dedupParmDAO.getDedupParmByModule(FinanceConstants.DEDUP_CUSTOMER,
					customerDedup.getCustCtgCode(), "");
			//TO Check duplicate customer  in Local database
			for (DedupParm dedupParm : dedupParmList) {
				List<CustomerDedup> list = customerDedupDAO.fetchCustomerDedupDetails(customerDedup, dedupParm.getSQLQuery());
				if (list != null && !list.isEmpty()) {
					dedupList.addAll(list);
				}
			}
			if (!dedupList.isEmpty()) {
				response = new CustomerDetails();
				doEmptyResponseObject(response);
				response.setDedupReq(customerDetails.isDedupReq());
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90343"));
				response.setCustomerDedupList(dedupList);
				return response;
			}
		}
		
		// call create customer method in case of no errors
		response = customerController.createCustomer(customerDetails);

		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for update customer in PLF system.
	 * 
	 * @param customerDetails
	 */
	@Override
	public WSReturnStatus updateCustomer(CustomerDetails customerDetails) throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(customerDetails, UpdateValidationGroup.class);
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerDetails.getCustCIF())) {
			customer = customerDetailsService.getCustomerByCIF(customerDetails.getCustCIF());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerDetails.getCustCIF();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}

		AuditHeader auditHeader = getAuditHeader(customerDetails, PennantConstants.TRAN_WF);

		AuditDetail auditDetail = customerDetailsService.doCustomerValidations(auditHeader);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}

		// call update customer if there is no errors
		WSReturnStatus returnStatus = customerController.updateCustomer(customerDetails);

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * get the customer Details by the given customer cif.
	 * 
	 * @param custCIF
	 * @return CustomerDetails
	 */
	@Override
	public CustomerDetails getCustomerDetails(String custCIF) throws ServiceException {
		logger.debug("Enetring");

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		CustomerDetails response = null;

		// validate Customer with given CustCIF
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer != null) {
			response = customerController.getCustomerDetails(customer.getCustID());
		} else {
			response = new CustomerDetails();
			doEmptyResponseObject(response);
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete customer Details by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public WSReturnStatus deleteCustomer(String custCIF) throws ServiceException {
		logger.debug("Enetring");

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer != null) {

			// call delete customer service
			response = customerController.deleteCustomerById(customer.getCustID());
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			return APIErrorHandlerService.getFailedStatus("90101", valueParm);
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * get CustomerPersonalInfo by the given customer cif.
	 * 
	 * @param custCIF
	 */

	@Override
	public CustomerDetails getCustomerPersonalInfo(String custCIF) throws ServiceException {

		logger.debug("Enetring");

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		CustomerDetails response = null;

		// validate Customer with given CustCIF
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer != null) {
			response = customerController.getCustomerPersonalInfo(customer.getCustID());
		} else {
			response = new CustomerDetails();
			// doEmptyResponseObject(response);
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for update customer in PLF system.
	 * 
	 * @param customer
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateCustomerPersonalInfo(CustomerDetails customerDetails) throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(customerDetails, PersionalInfoGroup.class);
		Customer customer = null;
		// customer validations
		if (StringUtils.isNotBlank(customerDetails.getCustCIF())) {
			customer = customerDetailsService.getCustomerByCIF(customerDetails.getCustCIF());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerDetails.getCustCIF();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}
		customerDetails.getCustomer().setCustID(customer.getCustID());
		customerDetails.getCustomer().setCustCtgCode(customer.getCustCtgCode());
		AuditHeader auditHeader = getAuditHeader(customerDetails, PennantConstants.TRAN_WF);

		AuditDetail auditDetail = customerService.doCustomerValidations(auditHeader);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}

		// call update customer if there is no errors
		WSReturnStatus returnStatus = customerController.updateCustomerPersionalInfo(customerDetails);

		logger.debug("Leaving");
		return returnStatus;

	}

	/**
	 * Method for create CustomerEmployment in PLF system.
	 * 
	 * @param customerEmploymentDetail
	 * @throws ServiceException
	 */

	@Override
	public EmploymentDetail addCustomerEmployment(EmploymentDetail employmentDetail) throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(employmentDetail, SaveValidationGroup.class);
		EmploymentDetail response = null;
		if (employmentDetail.getCustomerEmploymentDetail() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "employment";
			EmploymentDetail custEmploymentDetail = new EmploymentDetail();
			custEmploymentDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return custEmploymentDetail;
		}
		Customer customerDetails = null;
		if (StringUtils.isNotBlank(employmentDetail.getCif())) {
			customerDetails = customerDetailsService.getCustomerByCIF(employmentDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = employmentDetail.getCif();
				EmploymentDetail customerEmpDetail = new EmploymentDetail();
				customerEmpDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
				return customerEmpDetail;
			}
		}

		AuditHeader auditHeader = getAuditHeader(employmentDetail.getCustomerEmploymentDetail(),
				PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerEmploymentDetailService.doValidations(employmentDetail
				.getCustomerEmploymentDetail(),customerDetails);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				response = new EmploymentDetail();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return response;
			}
		}

		// call add Customer Employment method in case of no errors
		response = customerController.addCustomerEmployment(employmentDetail.getCustomerEmploymentDetail(),
				employmentDetail.getCif());

		logger.debug("Leaving");
		return response;

	}

	/**
	 * get CustomerEmployment by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerEmployment(String custCIF) throws ServiceException {

		logger.debug("Entering");
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		CustomerDetails response = new CustomerDetails();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
			response.setCustomer(null);
		} else {
			response = customerController.getCustomerEmployment(custCIF);
		}
		logger.debug("Leaving");

		return response;
	}

	/**
	 * Method for update CustomerEmploymentDetail in PLF system.
	 * 
	 * @param customerEmploymentDetail
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateCustomerEmployment(EmploymentDetail employmentDetail) throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(employmentDetail, UpdateValidationGroup.class);
		if (employmentDetail.getCustomerEmploymentDetail() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "employment";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(employmentDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(employmentDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = employmentDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}

		AuditHeader auditHeader = getAuditHeader(employmentDetail.getCustomerEmploymentDetail(),
				PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		AuditDetail auditDetail = customerEmploymentDetailService.doValidations(employmentDetail
				.getCustomerEmploymentDetail(),customer);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}

		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerEmploymentDetail customerEmpDetail = customerEmploymentDetailService
				.getApprovedCustomerEmploymentDetailByCustEmpId(employmentDetail
						.getCustomerEmploymentDetail().getCustEmpId());
		if (customerEmpDetail != null) {
			if(customerEmpDetail.getCustID()==(customer.getCustID())){
				// call update customer if there is no errors
				response = customerController.updateCustomerEmployment(employmentDetail.getCustomerEmploymentDetail(),
						employmentDetail.getCif());
			} else {
				response = new WSReturnStatus();
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(employmentDetail.getCustomerEmploymentDetail().getCustEmpId());
				valueParm[1] = employmentDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90104", valueParm);
			}
			
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(employmentDetail.getCustomerEmploymentDetail().getCustEmpId());
			valueParm[1] = employmentDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90104", valueParm);
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * delete CustomerEmploymentDetail.
	 * 
	 * @param customerEmploymentDetail
	 */
	@Override
	public WSReturnStatus deleteCustomerEmployment(EmploymentDetail employmentDetail) throws ServiceException {

		logger.debug("Enetring");
		// bean validations
		validationUtility.validate(employmentDetail, DeleteValidationGroup.class);

		// customer validations
		CustomerEmploymentDetail customerEmploymentDetail = null;
		Customer customer = null;
		if (StringUtils.isNotBlank(employmentDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(employmentDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = employmentDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			} else {
				customerEmploymentDetail = new CustomerEmploymentDetail();
				customerEmploymentDetail.setCustID(customer.getCustID());
				customerEmploymentDetail.setCustEmpId(employmentDetail.getEmployementId());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerEmploymentDetail customerEmpDetail = customerEmploymentDetailService
				.getApprovedCustomerEmploymentDetailByCustEmpId(
						employmentDetail.getEmployementId());
		
		if (customerEmpDetail != null) {
			customerEmploymentDetail.setCustEmpName(customerEmpDetail.getCustEmpName());
			// call delete customer service
			if(customerEmpDetail.getCustID()==(customer.getCustID())){
			response = customerController.deleteCustomerEmployment(customerEmploymentDetail);
			} else {
				response = new WSReturnStatus();
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(employmentDetail.getCustomerEmploymentDetail().getCustEmpId());
				valueParm[1] = employmentDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90104", valueParm);
			}
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(employmentDetail.getEmployementId());
			valueParm[1] = employmentDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90104", valueParm);
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for create CustPhoneNumber in PLF system.
	 * 
	 * @param custPhoneNumber
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus addCustomerPhoneNumber(CustPhoneNumber custPhoneNumber) throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(custPhoneNumber, SaveValidationGroup.class);
		if (custPhoneNumber.getCustomerPhoneNumber() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "phone";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		Customer customer = null;
		if (StringUtils.isNotBlank(custPhoneNumber.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(custPhoneNumber.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custPhoneNumber.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);

			}
		}
		custPhoneNumber.getCustomerPhoneNumber().setPhoneCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custPhoneNumber.getCustomerPhoneNumber(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerPhoneNumberService.doValidations(custPhoneNumber.getCustomerPhoneNumber(),APIConstants.SERVICE_TYPE_CREATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());

			}
		}

		// call add Customer Employment method in case of no errors
		WSReturnStatus returnStatus = customerDetailsController.addCustomerPhoneNumber(
				custPhoneNumber.getCustomerPhoneNumber(), custPhoneNumber.getCif());

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Method for update customerPhoneNumber in PLF system.
	 * 
	 * @param customerPhoneNumber
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateCustomerPhoneNumber(CustPhoneNumber customerPhoneNumber) throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(customerPhoneNumber, UpdateValidationGroup.class);
		if (customerPhoneNumber.getCustomerPhoneNumber() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "phone";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerPhoneNumber.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerPhoneNumber.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerPhoneNumber.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}
		AuditHeader auditHeader = getAuditHeader(customerPhoneNumber.getCustomerPhoneNumber(), PennantConstants.TRAN_WF);
		customerPhoneNumber.getCustomerPhoneNumber().setPhoneCustID(customer.getCustID());
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerPhoneNumberService
				.doValidations(customerPhoneNumber.getCustomerPhoneNumber(),APIConstants.SERVICE_TYPE_UPDATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}
		// validate Customer with given CustCIF
		CustomerPhoneNumber custPhoneNumber = customerPhoneNumberService.getApprovedCustomerPhoneNumberById(
				customer.getCustID(), customerPhoneNumber.getCustomerPhoneNumber().getPhoneTypeCode());
		WSReturnStatus returnStatus = null;
		if (custPhoneNumber != null) {

			// call update customer if there is no errors
			returnStatus = customerDetailsController.updateCustomerPhoneNumber(
					customerPhoneNumber.getCustomerPhoneNumber(), customerPhoneNumber.getCif());
		} else {
			returnStatus = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = customerPhoneNumber.getCustomerPhoneNumber().getPhoneTypeCode();
			valueParm[1] = customerPhoneNumber.getCif();
			return APIErrorHandlerService.getFailedStatus("90106", valueParm);
		}

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * get getCustomerPhoneNumbers by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerPhoneNumbers(String custCIF) throws ServiceException {
		logger.debug("Entering");
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		CustomerDetails response = new CustomerDetails();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
			response.setCustomer(null);
		} else {
			response = customerDetailsController.getCustomerPhoneNumbers(custCIF);
		}
		logger.debug("Leaving");

		return response;
	}

	/**
	 * delete CustomerPhoneNumber.
	 * 
	 * @param custPhoneNumber
	 */
	@Override
	public WSReturnStatus deleteCustomerPhoneNumber(CustPhoneNumber custPhoneNumber) throws ServiceException {
		logger.debug("Enetring");

		// bean validations
		validationUtility.validate(custPhoneNumber, DeleteValidationGroup.class);

		// customer validations
		CustomerPhoneNumber customerPhoneNumber = null;
		if (StringUtils.isNotBlank(custPhoneNumber.getCif())) {
			Customer customer = customerDetailsService.getCustomerByCIF(custPhoneNumber.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custPhoneNumber.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			} else {
				customerPhoneNumber = new CustomerPhoneNumber();
				customerPhoneNumber.setPhoneCustID(customer.getCustID());
				customerPhoneNumber.setPhoneTypeCode(custPhoneNumber.getPhoneTypeCode());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerPhoneNumber prvCustomerPhoneNumber = customerPhoneNumberService.getApprovedCustomerPhoneNumberById(
				customerPhoneNumber.getPhoneCustID(), customerPhoneNumber.getPhoneTypeCode());
		if (prvCustomerPhoneNumber != null) {
			if (prvCustomerPhoneNumber.getPhoneTypePriority() == Integer.valueOf(PennantConstants.EMAILPRIORITY_VeryHigh)) {
				response = new WSReturnStatus();
				String[] valueParm = new String[2];
				valueParm[0] = "cannot delete";
				valueParm[1] = "Phone";
				return APIErrorHandlerService.getFailedStatus("90270", valueParm);
			}
			// call delete customer service
			response = customerDetailsController.deleteCustomerPhoneNumber(customerPhoneNumber);
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = custPhoneNumber.getCif();
			valueParm[1] = custPhoneNumber.getPhoneTypeCode();
			return APIErrorHandlerService.getFailedStatus("90106", valueParm);
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for create CustomerAddress in PLF system.
	 * 
	 * @param custAddress
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus addCustomerAddress(CustAddress custAddress) throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(custAddress, SaveValidationGroup.class);
		if (custAddress.getCustomerAddres() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "address";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		Customer customer = null;
		if (StringUtils.isNotBlank(custAddress.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(custAddress.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custAddress.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);

			}
		}
		custAddress.getCustomerAddres().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custAddress.getCustomerAddres(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerAddresService.doValidations(custAddress.getCustomerAddres(),APIConstants.SERVICE_TYPE_CREATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());

			}
		}

		// call add Customer Employment method in case of no errors
		WSReturnStatus returnStatus = customerDetailsController.addCustomerAddress(custAddress.getCustomerAddres(),
				custAddress.getCif());

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Method for update CustomerAddress in PLF system.
	 * 
	 * @param custAddress
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateCustomerAddress(CustAddress custAddress) throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(custAddress, UpdateValidationGroup.class);
		if (custAddress.getCustomerAddres() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "address";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(custAddress.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(custAddress.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custAddress.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}
		custAddress.getCustomerAddres().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custAddress.getCustomerAddres(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerAddresService.doValidations(custAddress.getCustomerAddres(),APIConstants.SERVICE_TYPE_UPDATE);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}

		// validate Customer with given CustCIF
		CustomerAddres customerAddress = customerAddresService.getApprovedCustomerAddresById(customer.getCustID(),
				custAddress.getCustomerAddres().getCustAddrType());
		WSReturnStatus returnStatus = null;
		if (customerAddress != null) {
			// call update customer if there is no errors
			returnStatus = customerDetailsController.updateCustomerAddress(custAddress.getCustomerAddres(),
					custAddress.getCif());
		} else {
			returnStatus = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = custAddress.getCustomerAddres().getCustAddrType();
			valueParm[1] = custAddress.getCif();
			return APIErrorHandlerService.getFailedStatus("90109", valueParm);
		}

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * get CustomerAddresses by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerAddresses(String custCIF) throws ServiceException {
		logger.debug("Entering");

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		CustomerDetails response = new CustomerDetails();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
			response.setCustomer(null);
		} else {
			response = customerDetailsController.getCustomerAddresses(custCIF);
		}

		logger.debug("Leaving");

		return response;
	}

	/**
	 * delete CustAddress.
	 * 
	 * @param custAddress
	 */
	@Override
	public WSReturnStatus deleteCustomerAddress(CustAddress custAddress) throws ServiceException {
		logger.debug("Enetring");

		// bean validations
		validationUtility.validate(custAddress, DeleteValidationGroup.class);

		// customer validations
		CustomerAddres customerAddres = null;
		if (StringUtils.isNotBlank(custAddress.getCif())) {
			Customer customer = customerDetailsService.getCustomerByCIF(custAddress.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custAddress.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			} else {
				customerAddres = new CustomerAddres();
				customerAddres.setCustID(customer.getCustID());
				customerAddres.setCustAddrType(custAddress.getAddrType());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerAddres prvCustomerAddres = customerAddresService.getApprovedCustomerAddresById(
				customerAddres.getCustID(), customerAddres.getCustAddrType());
		if (prvCustomerAddres != null) {
			if (prvCustomerAddres.getCustAddrPriority() == Integer.valueOf(PennantConstants.EMAILPRIORITY_VeryHigh)) {
				response = new WSReturnStatus();
				String[] valueParm = new String[2];
				valueParm[0] = "cannot delete";
				valueParm[1] = "Address";
				return APIErrorHandlerService.getFailedStatus("90270", valueParm);
			}
			// call delete customer service
			response = customerDetailsController.deleteCustomerAddress(customerAddres);
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = custAddress.getCif();
			valueParm[1] = custAddress.getAddrType();
			return APIErrorHandlerService.getFailedStatus("90109", valueParm);
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for create CustomerEmail in PLF system.
	 * 
	 * @param custEMail
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus addCustomerEmail(CustEMail custEMail) throws ServiceException {
		logger.debug("Enetring");
		// bean validations
		validationUtility.validate(custEMail, SaveValidationGroup.class);
		if (custEMail.getCustomerEMail() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "email";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		Customer customer = null;
		if (StringUtils.isNotBlank(custEMail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(custEMail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custEMail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);

			}
		}
		custEMail.getCustomerEMail().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custEMail.getCustomerEMail(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerEMailService.doValidations(custEMail.getCustomerEMail(),APIConstants.SERVICE_TYPE_CREATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}
		// call add Customer Employment method in case of no errors
		WSReturnStatus returnStatus = customerDetailsController.addCustomerEmail(custEMail.getCustomerEMail(),
				custEMail.getCif());

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Method for update CustomerEmail in PLF system.
	 * 
	 * @param custEMail
	 * @throws ServiceException
	 */

	@Override
	public WSReturnStatus updateCustomerEmail(CustEMail custEMail) throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(custEMail, UpdateValidationGroup.class);
		if (custEMail.getCustomerEMail() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "email";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(custEMail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(custEMail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custEMail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}
		custEMail.getCustomerEMail().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custEMail.getCustomerEMail(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerEMailService.doValidations(custEMail.getCustomerEMail(),APIConstants.SERVICE_TYPE_UPDATE);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}

		// validate Customer with given CustCIF
		CustomerEMail customerEmail = customerEMailService.getApprovedCustomerEMailById(customer.getCustID(), custEMail
				.getCustomerEMail().getCustEMailTypeCode());
		WSReturnStatus returnStatus = null;
		if (customerEmail != null) {
			// call update customer if there is no errors
			returnStatus = customerDetailsController.updateCustomerEmail(custEMail.getCustomerEMail(),
					custEMail.getCif());
		} else {
			returnStatus = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = custEMail.getCustomerEMail().getCustEMailTypeCode();
			valueParm[1] = custEMail.getCif();
			return APIErrorHandlerService.getFailedStatus("90111", valueParm);
		}

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * get CustomerEmails by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerEmails(String custCIF) throws ServiceException {
		logger.debug("Entering");

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		CustomerDetails response = new CustomerDetails();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
			response.setCustomer(null);
		} else {
			response = customerDetailsController.getCustomerEmails(custCIF);
		}

		logger.debug("Leaving");

		return response;
	}

	/**
	 * delete CustomerEmail.
	 * 
	 * @param custEMail
	 */
	@Override
	public WSReturnStatus deleteCustomerEmail(CustEMail custEMail) throws ServiceException {

		logger.debug("Enetring");

		// bean validations
		validationUtility.validate(custEMail, DeleteValidationGroup.class);

		// customer validations
		CustomerEMail customerEMaial = null;
		if (StringUtils.isNotBlank(custEMail.getCif())) {
			Customer customer = customerDetailsService.getCustomerByCIF(custEMail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custEMail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			} else {
				customerEMaial = new CustomerEMail();
				customerEMaial.setCustID(customer.getCustID());
				customerEMaial.setCustEMailTypeCode(custEMail.getCustEMailTypeCode());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerEMail prvCustomerEMail = customerEMailService.getApprovedCustomerEMailById(customerEMaial.getCustID(),
				customerEMaial.getCustEMailTypeCode());
		if (prvCustomerEMail != null) {
			// call delete customer service
			response = customerDetailsController.deleteCustomerEmail(customerEMaial);
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = custEMail.getCif();
			valueParm[1] = custEMail.getCustEMailTypeCode();
			return APIErrorHandlerService.getFailedStatus("90111", valueParm);
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for create CustomerIncome in PLF system.
	 * 
	 * @param customerIncomeDetail
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus addCustomerIncome(CustomerIncomeDetail customerIncomeDetail) throws ServiceException {
		logger.debug("Enetring");
		// bean validations
		validationUtility.validate(customerIncomeDetail, SaveValidationGroup.class);
		if (customerIncomeDetail.getCustomerIncome() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerIncome";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		Customer customer = null;
		if (StringUtils.isNotBlank(customerIncomeDetail.getCif())) {
			 customer = customerDetailsService.getCustomerByCIF(customerIncomeDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerIncomeDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);

			}
		}
		if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_CORP)) {
			String[] valueParm = new String[2];
			valueParm[0] = "Customerincome";
			valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
			return APIErrorHandlerService.getFailedStatus("90124", valueParm);
		}


		AuditHeader auditHeader = getAuditHeader(customerIncomeDetail.getCustomerIncome(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerIncomeService.doValidations(customerIncomeDetail.getCustomerIncome());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}
		WSReturnStatus returnStatus = customerDetailsController.addCustomerIncome(
				customerIncomeDetail.getCustomerIncome(), customerIncomeDetail.getCif());

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Method for update CustomerIncome in PLF system.
	 * 
	 * @param customerIncomeDetail
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateCustomerIncome(CustomerIncomeDetail customerIncomeDetail) throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(customerIncomeDetail, UpdateValidationGroup.class);
		if (customerIncomeDetail.getCustomerIncome() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerIncome";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerIncomeDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerIncomeDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerIncomeDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}
		
		if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_CORP)) {
			String[] valueParm = new String[2];
			valueParm[0] = "Customerincome";
			valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
			return APIErrorHandlerService.getFailedStatus("90124", valueParm);
		}

		AuditHeader auditHeader = getAuditHeader(customerIncomeDetail.getCustomerIncome(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerIncomeService.doValidations(customerIncomeDetail.getCustomerIncome());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}
		// validate Customer with given CustCIF
		CustomerIncome curCustomerIncome = customerIncomeDetail.getCustomerIncome();
		curCustomerIncome.setCustID(customer.getCustID());
		CustomerIncome customerIncome = customerIncomeService.getCustomerIncomeById(curCustomerIncome);
		WSReturnStatus returnStatus = null;
		if (customerIncome != null) {
			// call update customer if there is no errors
			returnStatus = customerDetailsController.updateCustomerIncome(customerIncomeDetail.getCustomerIncome(),
					customerIncomeDetail.getCif());
		} else {
			returnStatus = new WSReturnStatus();
			String[] valueParm = new String[1];
			valueParm[0] = customerIncomeDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90112", valueParm);
		}

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * get CustomerIncomes by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerIncomes(String custCIF) throws ServiceException {
		logger.debug("Entering");

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		CustomerDetails response = new CustomerDetails();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
			response.setCustomer(null);
		} else {
			response = customerDetailsController.getCustomerIncomes(custCIF);
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete CustomerIncome.
	 * 
	 * @param customerIncomeDetail
	 */
	@Override
	public WSReturnStatus deleteCustomerIncome(CustomerIncomeDetail customerIncomeDetail) throws ServiceException {
		logger.debug("Enetring");

		// bean validations
		validationUtility.validate(customerIncomeDetail, DeleteValidationGroup.class);

		// customer validations
		CustomerIncome customerIncome = null;
		if (StringUtils.isNotBlank(customerIncomeDetail.getCif())) {
			Customer customer = customerDetailsService.getCustomerByCIF(customerIncomeDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerIncomeDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			} else {
				customerIncome = new CustomerIncome();
				customerIncome.setCustID(customer.getCustID());
				customerIncome.setCustIncomeType(customerIncomeDetail.getCustIncomeType());
				customerIncome.setCategory(customerIncomeDetail.getCategory());
				customerIncome.setIncomeExpense(customerIncomeDetail.getIncomeExpense());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerIncome prvCustomerIncome = customerIncomeService.getApprovedCustomerIncomeById(customerIncome);
		if (prvCustomerIncome != null) {
			// call delete customer service
			response = customerDetailsController.deleteCustomerIncome(customerIncome);
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[1];
			valueParm[0] = customerIncomeDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90112", valueParm);
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for create CustomerBankingInformation in PLF system.
	 * 
	 * @param customerBankInfoDetail
	 * @throws ServiceException
	 */
	@Override
	public CustomerBankInfoDetail addCustomerBankingInformation(CustomerBankInfoDetail customerBankInfoDetail)
			throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(customerBankInfoDetail, SaveValidationGroup.class);
		if (customerBankInfoDetail.getCustomerBankInfo() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerBankInfo";
			CustomerBankInfoDetail aCustomerBankInfoDetail = new CustomerBankInfoDetail();
			aCustomerBankInfoDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return aCustomerBankInfoDetail;
		}
		if (StringUtils.isNotBlank(customerBankInfoDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerBankInfoDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerBankInfoDetail.getCif();
				CustomerBankInfoDetail custBankInfoDetail = new CustomerBankInfoDetail();
				custBankInfoDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
				return custBankInfoDetail;
			}
		}

		AuditHeader auditHeader = getAuditHeader(customerBankInfoDetail.getCustomerBankInfo(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerBankInfoService.doValidations(customerBankInfoDetail.getCustomerBankInfo());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		CustomerBankInfoDetail response = null;
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerBankInfoDetail();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return response;
			}
		}

		// call add Customer Employment method in case of no errors
		response = customerDetailsController.addCustomerBankingInformation(
				customerBankInfoDetail.getCustomerBankInfo(), customerBankInfoDetail.getCif());

		logger.debug("Leaving");
		return response;

	}

	/**
	 * Method for update CustomerBankingInformation in PLF system.
	 * 
	 * @param customerBankInfoDetail
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateCustomerBankingInformation(CustomerBankInfoDetail customerBankInfoDetail)
			throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(customerBankInfoDetail, UpdateValidationGroup.class);
		if (customerBankInfoDetail.getCustomerBankInfo() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerBankInfo";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerBankInfoDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerBankInfoDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerBankInfoDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}

		AuditHeader auditHeader = getAuditHeader(customerBankInfoDetail.getCustomerBankInfo(), PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		AuditDetail auditDetail = customerBankInfoService.doValidations(customerBankInfoDetail.getCustomerBankInfo());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}

		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerBankInfo customerBankInfo = customerBankInfoService.getCustomerBankInfoById(
				customerBankInfoDetail.getCustomerBankInfo().getBankId());
		if (customerBankInfo != null) {
			// call update customer if there is no errors
			response = customerDetailsController.updateCustomerBankingInformation(
					customerBankInfoDetail.getCustomerBankInfo(), customerBankInfoDetail.getCif());
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerBankInfoDetail.getCustomerBankInfo().getBankId());
			valueParm[1] = customerBankInfoDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90116", valueParm);
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * get CustomerBankingInformation by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerBankingInformation(String custCIF) throws ServiceException {
		logger.debug("Entering");
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		CustomerDetails response = new CustomerDetails();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
			response.setCustomer(null);
		} else {
			response = customerDetailsController.getCustomerBankingInformation(custCIF);
		}
		logger.debug("Leaving");

		return response;
	}

	/**
	 * delete CustomerBankingInformation.
	 * 
	 * @param customerBankInfoDetail
	 */
	@Override
	public WSReturnStatus deleteCustomerBankingInformation(CustomerBankInfoDetail customerBankInfoDetail)
			throws ServiceException {

		logger.debug("Enetring");
		// bean validations
		validationUtility.validate(customerBankInfoDetail, DeleteValidationGroup.class);

		// customer validations
		CustomerBankInfo customerBankInfo = null;
		if (StringUtils.isNotBlank(customerBankInfoDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerBankInfoDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerBankInfoDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			} else {
				customerBankInfo = new CustomerBankInfo();
				customerBankInfo.setCustID(customerDetails.getCustID());
				customerBankInfo.setBankId(customerBankInfoDetail.getBankId());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerBankInfo custBankInfo = customerBankInfoService.getCustomerBankInfoById(
				customerBankInfoDetail.getBankId());
		if (custBankInfo != null) {
			// call delete customer service
			response = customerDetailsController.deleteCustomerBankingInformation(customerBankInfo);
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerBankInfoDetail.getBankId());
			valueParm[1] = customerBankInfoDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90116", valueParm);
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for create CustomerAccountBehaviour in PLF system.
	 * 
	 * @param customerChequeInfoDetail
	 * @throws ServiceException
	 */
	@Override
	public CustomerChequeInfoDetail addCustomerAccountBehaviour(CustomerChequeInfoDetail customerChequeInfoDetail)
			throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(customerChequeInfoDetail, SaveValidationGroup.class);

		if (customerChequeInfoDetail.getCustomerChequeInfo() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "accountBehaviour";
			CustomerChequeInfoDetail custChequeInfoDetail = new CustomerChequeInfoDetail();
			custChequeInfoDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return custChequeInfoDetail;
		}
		if (StringUtils.isNotBlank(customerChequeInfoDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerChequeInfoDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerChequeInfoDetail.getCif();
				CustomerChequeInfoDetail custChequeInfoDetail = new CustomerChequeInfoDetail();
				custChequeInfoDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
				return custChequeInfoDetail;
			}
		}
		// call add Customer Employment method in case of no errors
		CustomerChequeInfoDetail response = customerDetailsController.addCustomerAccountBehaviour(
				customerChequeInfoDetail.getCustomerChequeInfo(), customerChequeInfoDetail.getCif());
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for update CustomerAccountBehaviour in PLF system.
	 * 
	 * @param customerChequeInfoDetail
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateCustomerAccountBehaviour(CustomerChequeInfoDetail customerChequeInfoDetail)
			throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(customerChequeInfoDetail, UpdateValidationGroup.class);
		if (customerChequeInfoDetail.getCustomerChequeInfo() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "accountBehaviour";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerChequeInfoDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerChequeInfoDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerChequeInfoDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}

		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerChequeInfo customerChequeInfo = customerChequeInfoDAO.getCustomerChequeInfoById(customer.getCustID(),
				customerChequeInfoDetail.getCustomerChequeInfo().getChequeSeq(),"");
		if (customerChequeInfo != null) {
			// call update customer if there is no errors
			response = customerDetailsController.updateCustomerAccountBehaviour(
					customerChequeInfoDetail.getCustomerChequeInfo(), customerChequeInfoDetail.getCif());
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerChequeInfoDetail.getCustomerChequeInfo().getChequeSeq());
			valueParm[1] = customerChequeInfoDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90117", valueParm);
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * get CustomerAccountBehaviour by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerAccountBehaviour(String custCIF) throws ServiceException {
		logger.debug("Entering");
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		CustomerDetails response = new CustomerDetails();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
			response.setCustomer(null);
		} else {
			response = customerDetailsController.getCustomerAccountBehaviour(custCIF);
		}
		logger.debug("Leaving");

		return response;
	}

	/**
	 * delete CustomerAccountBehaviour.
	 * 
	 * @param customerChequeInfoDetail
	 */
	@Override
	public WSReturnStatus deleteCustomerAccountBehaviour(CustomerChequeInfoDetail customerChequeInfoDetail)
			throws ServiceException {
		logger.debug("Enetring");
		// bean validations
		validationUtility.validate(customerChequeInfoDetail, DeleteValidationGroup.class);

		// customer validations
		CustomerChequeInfo customerChequeInfo = null;
		if (StringUtils.isNotBlank(customerChequeInfoDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerChequeInfoDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerChequeInfoDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			} else {
				customerChequeInfo = new CustomerChequeInfo();
				customerChequeInfo.setCustID(customerDetails.getCustID());
				customerChequeInfo.setChequeSeq(customerChequeInfoDetail.getChequeSeq());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerChequeInfo custChequeInfo = customerChequeInfoDAO.getCustomerChequeInfoById(
				customerChequeInfo.getCustID(), customerChequeInfo.getChequeSeq(),"");
		if (custChequeInfo != null) {
			// call delete customer service
			response = customerDetailsController.deleteCustomerAccountBehaviour(customerChequeInfo);
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerChequeInfoDetail.getChequeSeq());
			valueParm[1] = customerChequeInfoDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90117", valueParm);
		}
		logger.debug("Leaving");
		return response;
	}
	

	/**
	 * Method for create CustomerExternalLiability in PLF system.
	 * 
	 * @param customerExtLiabilityDetail
	 * @throws ServiceException
	 */
	@Override
	public CustomerExtLiabilityDetail addCustomerExternalLiability(CustomerExtLiabilityDetail customerExtLiabilityDetail)
			throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(customerExtLiabilityDetail, SaveValidationGroup.class);
		if (customerExtLiabilityDetail.getCustomerExtLiability() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerExtLiability";
			CustomerExtLiabilityDetail aCustomerExtLiabilityDetail = new CustomerExtLiabilityDetail();
			aCustomerExtLiabilityDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return aCustomerExtLiabilityDetail;
		}
		if (StringUtils.isNotBlank(customerExtLiabilityDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerExtLiabilityDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerExtLiabilityDetail.getCif();
				CustomerExtLiabilityDetail custExtLiabilityDetail = new CustomerExtLiabilityDetail();
				custExtLiabilityDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
				return custExtLiabilityDetail;
			}
		}

		AuditHeader auditHeader = getAuditHeader(customerExtLiabilityDetail.getCustomerExtLiability(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		AuditDetail auditDetail = validation.doValidations(customerExtLiabilityDetail.getCustomerExtLiability());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		CustomerExtLiabilityDetail response = null;
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerExtLiabilityDetail();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return response;
			}
		}

		// call add Customer Employment method in case of no errors
		response = customerDetailsController.addCustomerExternalLiability(
				customerExtLiabilityDetail.getCustomerExtLiability(), customerExtLiabilityDetail.getCif());

		logger.debug("Leaving");
		return response;

	}
	/**
	 * Method for update CustomerExternalLiability in PLF system.
	 * 
	 * @param customerExtLiabilityDetail
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateCustomerExternalLiability(CustomerExtLiabilityDetail customerExtLiabilityDetail)
			throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(customerExtLiabilityDetail, UpdateValidationGroup.class);
		if (customerExtLiabilityDetail.getCustomerExtLiability() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerExtLiability";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerExtLiabilityDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerExtLiabilityDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerExtLiabilityDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}

		AuditHeader auditHeader = getAuditHeader(customerExtLiabilityDetail.getCustomerExtLiability(), PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		AuditDetail auditDetail = validation.doValidations(customerExtLiabilityDetail.getCustomerExtLiability());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}

		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerExtLiability customerExtLiability = customerExtLiabilityService.getCustomerExtLiabilityById(customer.getCustID(),
				customerExtLiabilityDetail.getCustomerExtLiability().getLiabilitySeq());
		if (customerExtLiability != null) {
			// call update customer if there is no errors
			response = customerDetailsController.updateCustomerExternalLiability(
					customerExtLiabilityDetail.getCustomerExtLiability(), customerExtLiabilityDetail.getCif());
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerExtLiabilityDetail.getCustomerExtLiability().getLiabilitySeq());
			valueParm[1] = customerExtLiabilityDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90118", valueParm);
		}

		logger.debug("Leaving");
		return response;

	}
	/**
	 * get CustomerExternalLiabilities by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerExternalLiabilities(String custCIF) throws ServiceException {
		logger.debug("Entering");
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		CustomerDetails response = new CustomerDetails();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
			response.setCustomer(null);
		} else {
			response = customerDetailsController.getCustomerExternalLiabilities(custCIF);
		}
		logger.debug("Leaving");

		return response;
	}
	/**
	 * delete CustomerExternalLiability.
	 * 
	 * @param customerExtLiabilityDetail
	 */
	@Override
	public WSReturnStatus deleteCustomerExternalLiability(CustomerExtLiabilityDetail customerExtLiabilityDetail)
			throws ServiceException {
		logger.debug("Enetring");
		// bean validations
		validationUtility.validate(customerExtLiabilityDetail, DeleteValidationGroup.class);

		// customer validations
		CustomerExtLiability customerExtLiability = null;
		if (StringUtils.isNotBlank(customerExtLiabilityDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerExtLiabilityDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerExtLiabilityDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			} else {
				customerExtLiability = new CustomerExtLiability();
				customerExtLiability.setCustID(customerDetails.getCustID());
				customerExtLiability.setLiabilitySeq(customerExtLiabilityDetail.getLiabilitySeq());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerExtLiability custExtLiability = customerExtLiabilityService.getCustomerExtLiabilityById(customerExtLiability.getCustID(),
				customerExtLiability.getLiabilitySeq());
		if (custExtLiability != null) {
			// call delete customer service
			response = customerDetailsController.deleteCustomerExternalLiability(customerExtLiability);
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerExtLiabilityDetail.getLiabilitySeq());
			valueParm[1] = customerExtLiabilityDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90118", valueParm);
		}
		logger.debug("Leaving");
		return response;
	}
	/**
	 * Method for create Customer Document in PLF system.
	 * 
	 * @param customerDocumentDetail
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus addCustomerDocument(CustomerDocumentDetail customerDocumentDetail) throws ServiceException {
		logger.debug("Enetring");
		// bean validations
		validationUtility.validate(customerDocumentDetail, SaveValidationGroup.class);
		if (customerDocumentDetail.getCustomerDocument() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "document";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		Customer customer = null;
		if (StringUtils.isNotBlank(customerDocumentDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerDocumentDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerDocumentDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);

			}
		}

		AuditHeader auditHeader = getAuditHeader(customerDocumentDetail.getCustomerDocument(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerDocumentService.validateCustomerDocuments(customerDocumentDetail.getCustomerDocument(),customer);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}
		// call add Customer Employment method in case of no errors
		WSReturnStatus returnStatus = customerDetailsController.addCustomerDocument(customerDocumentDetail.getCustomerDocument(),
				customerDocumentDetail.getCif());

		logger.debug("Leaving");
		return returnStatus;

	}
	/**
	 * Method for update CustomerDocument in PLF system.
	 * 
	 * @param customerDocumentDetail
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateCustomerDocument(CustomerDocumentDetail customerDocumentDetail) throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(customerDocumentDetail, UpdateValidationGroup.class);
		if (customerDocumentDetail.getCustomerDocument() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "document";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerDocumentDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerDocumentDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerDocumentDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}

		AuditHeader auditHeader = getAuditHeader(customerDocumentDetail.getCustomerDocument(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerDocumentService.validateCustomerDocuments(customerDocumentDetail.getCustomerDocument(),customer);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
			}
		}

		// validate Customer with given CustCIF
		CustomerDocument customerDocument = customerDocumentService.getApprovedCustomerDocumentById(customer.getCustID(), customerDocumentDetail
				.getCustomerDocument().getCustDocCategory());
		WSReturnStatus returnStatus = null;
		if (customerDocument != null) {
			// call update customer if there is no errors
			returnStatus = customerDetailsController.updateCustomerDocument(customerDocumentDetail.getCustomerDocument(),
					customerDocumentDetail.getCif());
		} else {
			returnStatus = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = customerDocumentDetail.getCustomerDocument().getCustDocCategory();
			valueParm[1] = customerDocumentDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90119", valueParm);
		}
		logger.debug("Leaving");
		return returnStatus;
	}
	/**
	 * get CustomerDocuments by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerDocuments(String custCIF) throws ServiceException {
		logger.debug("Entering");

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		CustomerDetails response = new CustomerDetails();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
			response.setCustomer(null);
		} else {
			response = customerDetailsController.getCustomerDocuments(custCIF);
		}

		logger.debug("Leaving");

		return response;
	}
	/**
	 * delete CustomerDocument.
	 * 
	 * @param customerDocumentDetail
	 */
	@Override
	public WSReturnStatus deleteCustomerDocument(CustomerDocumentDetail customerDocumentDetail) throws ServiceException {
		logger.debug("Enetring");

		// bean validations
		validationUtility.validate(customerDocumentDetail, DeleteValidationGroup.class);

		// customer validations
		CustomerDocument customerDocument = null;
		if (StringUtils.isNotBlank(customerDocumentDetail.getCif())) {
			Customer customer = customerDetailsService.getCustomerByCIF(customerDocumentDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerDocumentDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			} else {
				customerDocument = new CustomerDocument();
				customerDocument.setCustID(customer.getCustID());
				customerDocument.setCustDocCategory(customerDocumentDetail.getCustDocCategory());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerDocument prvCustomerDocument = customerDocumentService.getApprovedCustomerDocumentById(customerDocument.getCustID(),
				customerDocument.getCustDocCategory());
		if (prvCustomerDocument != null) {
			// call delete customer service
			response = customerDetailsController.deleteCustomerDocument(customerDocument);
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = customerDocumentDetail.getCustDocCategory();
			valueParm[1] = customerDocumentDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90119", valueParm);
		}
		logger.debug("Leaving");
		return response;
	}
	private CustomerDedup doSetCustomerDedup(CustomerDetails customerDetails) {
		logger.debug("Entering");
		String mobileNumber = "";
		Customer customer = customerDetails.getCustomer();
		if (customerDetails.getCustomerPhoneNumList() != null) {
			for (CustomerPhoneNumber custPhone : customerDetails.getCustomerPhoneNumList()) {
				if (custPhone.getPhoneTypeCode().equals(PennantConstants.PHONETYPE_MOBILE)) {
					mobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(), custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
					break;
				}
			}
		}
		
		List<CustomerDocument> customerDocumentsList = customerDetails.getCustomerDocumentsList();
		if (customerDocumentsList != null) {
			for (CustomerDocument curCustDocument : customerDocumentsList) {
					if(StringUtils.equals(curCustDocument.getCustDocCategory(), "03")) {
						customerDetails.getCustomer().setCustCRCPR(curCustDocument.getCustDocTitle());
					}
			}
		}		

		CustomerDedup customerDedup = new CustomerDedup();
		customerDedup.setCustFName(customer.getCustFName());
		customerDedup.setCustLName(customer.getCustLName());
		customerDedup.setCustShrtName(customer.getCustShrtName());
		customerDedup.setCustDOB(customer.getCustDOB());
		customerDedup.setCustCRCPR(customer.getCustCRCPR());
		customerDedup.setCustCtgCode(customer.getCustCtgCode());
		customerDedup.setCustDftBranch(customer.getCustDftBranch());
		customerDedup.setCustSector(customer.getCustSector());
		customerDedup.setCustSubSector(customer.getCustSubSector());
		customerDedup.setCustNationality(customer.getCustNationality());
		customerDedup.setCustPassportNo(customer.getCustPassportNo());
		customerDedup.setCustTradeLicenceNum(customer.getCustTradeLicenceNum());
		customerDedup.setCustVisaNum(customer.getCustVisaNum());
		customerDedup.setMobileNumber(mobileNumber);
		customerDedup.setCustPOB(customer.getCustPOB());
		customerDedup.setCustResdCountry(customer.getCustResdCountry());
		customerDedup.setCustEMail(customer.getEmailID());

		logger.debug("Leaving");
		return customerDedup;

	}
	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerDetails
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerDetails aCustomerDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerDetails.getBefImage(), aCustomerDetails);
		return new AuditHeader(String.valueOf(aCustomerDetails.getCustID()), String.valueOf(aCustomerDetails
				.getCustID()), null, null, auditDetail, aCustomerDetails.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetails>>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerPhoneNumber
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerPhoneNumber aCustomerPhoneNumber, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerPhoneNumber.getBefImage(), aCustomerPhoneNumber);
		return new AuditHeader(String.valueOf(aCustomerPhoneNumber.getPhoneCustID()),
				String.valueOf(aCustomerPhoneNumber.getPhoneCustID()), null, null, auditDetail,
				aCustomerPhoneNumber.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerEmploymentDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerEmploymentDetail aCustomerEmploymentDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerEmploymentDetail.getBefImage(),
				aCustomerEmploymentDetail);
		return new AuditHeader(String.valueOf(aCustomerEmploymentDetail.getCustID()),
				String.valueOf(aCustomerEmploymentDetail.getCustID()), null, null, auditDetail,
				aCustomerEmploymentDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());
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
				String.valueOf(aCustomerAddres.getCustID()), null, null, auditDetail, aCustomerAddres.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetails>>());
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
				null, null, auditDetail, aCustomerEMail.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetails>>());
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
				String.valueOf(aCustomerIncome.getCustID()), null, null, auditDetail, aCustomerIncome.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetails>>());
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
		return new AuditHeader(String.valueOf(aCustomerBankInfo.getCustID()), String.valueOf(aCustomerBankInfo
				.getCustID()), null, null, auditDetail, aCustomerBankInfo.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetails>>());
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
		return new AuditHeader(String.valueOf(aCustomerExtLiability.getCustID()), String.valueOf(aCustomerExtLiability
				.getCustID()), null, null, auditDetail, aCustomerExtLiability.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetails>>());
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
		return new AuditHeader(String.valueOf(aCustomerDocument.getCustID()), String.valueOf(aCustomerDocument
				.getCustID()), null, null, auditDetail, aCustomerDocument.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetails>>());
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

	/**
	 * Nullify the un-necessary objects to prepare response in a structured format specified in API.
	 * 
	 * @param response
	 */
	private void doEmptyResponseObject(CustomerDetails response) {
		response.setCustomer(null);
		response.setEmploymentDetailsList(null);
		response.setAddressList(null);
		response.setCustomerPhoneNumList(null);
		response.setCustomerEMailList(null);
		response.setCustomerIncomeList(null);
	}

	@Autowired
	public void setCustomerEmploymentDetailService(CustomerEmploymentDetailService customerEmploymentDetailService) {
		this.customerEmploymentDetailService = customerEmploymentDetailService;
	}

	@Autowired
	public void setCustomerController(CustomerController customerController) {
		this.customerController = customerController;
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
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	@Autowired
	public void setCustomerDetailsController(CustomerDetailsController customerDetailsController) {
		this.customerDetailsController = customerDetailsController;
	}

	@Autowired
	public void setCustomerPhoneNumberService(CustomerPhoneNumberService customerPhoneNumberService) {
		this.customerPhoneNumberService = customerPhoneNumberService;
	}

	@Autowired
	public void setCustomerAddresService(CustomerAddresService customerAddresService) {
		this.customerAddresService = customerAddresService;
	}

	@Autowired
	public void setCustomerEMailService(CustomerEMailService customerEMailService) {
		this.customerEMailService = customerEMailService;
	}

	@Autowired
	public void setCustomerIncomeService(CustomerIncomeService customerIncomeService) {
		this.customerIncomeService = customerIncomeService;
	}
	@Autowired
	public void setCustomerExtLiabilityDAO(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
	}
	@Autowired
	public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}
	@Autowired
	public void setCustomerBankInfoService(CustomerBankInfoService customerBankInfoService) {
		this.customerBankInfoService = customerBankInfoService;
	}
	@Autowired
	public void setCustomerExtLiabilityService(CustomerExtLiabilityService customerExtLiabilityService) {
		this.customerExtLiabilityService = customerExtLiabilityService;
	}
	@Autowired
	public void setCustomerChequeInfoDAO(CustomerChequeInfoDAO customerChequeInfoDAO) {
		this.customerChequeInfoDAO = customerChequeInfoDAO;
	}
	@Autowired
	public void setDedupParmDAO(DedupParmDAO dedupParmDAO) {
		this.dedupParmDAO = dedupParmDAO;
	}
	@Autowired
	public void setCustomerDedupDAO(CustomerDedupDAO customerDedupDAO) {
		this.customerDedupDAO = customerDedupDAO;
	}

}
