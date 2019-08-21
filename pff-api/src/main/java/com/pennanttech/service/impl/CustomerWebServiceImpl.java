package com.pennanttech.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.dao.applicationmaster.CustomerCategoryDAO;
import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.dao.customermasters.CustomerCardSalesInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.dedup.DedupParmDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;
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
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.ProspectCustomerDetails;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.service.customermasters.CustomerBankInfoService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.service.customermasters.CustomerEmploymentDetailService;
import com.pennant.backend.service.customermasters.CustomerExtLiabilityService;
import com.pennant.backend.service.customermasters.CustomerGstService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.customermasters.CustomerPhoneNumberService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.customermasters.validation.CustomerExtLiabilityValidation;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.validation.DeleteValidationGroup;
import com.pennant.validation.PersionalInfoGroup;
import com.pennant.validation.ProspectCustDetailsGroup;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.UpdateValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.CustomerController;
import com.pennanttech.controller.CustomerDetailsController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.CustomerRESTService;
import com.pennanttech.pffws.CustomerSOAPService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.customer.AgreementRequest;
import com.pennanttech.ws.model.customer.CustAddress;
import com.pennanttech.ws.model.customer.CustEMail;
import com.pennanttech.ws.model.customer.CustPhoneNumber;
import com.pennanttech.ws.model.customer.CustomerBankInfoDetail;
import com.pennanttech.ws.model.customer.CustomerChequeInfoDetail;
import com.pennanttech.ws.model.customer.CustomerDocumentDetail;
import com.pennanttech.ws.model.customer.CustomerExtLiabilityDetail;
import com.pennanttech.ws.model.customer.CustomerGstInfoDetail;
import com.pennanttech.ws.model.customer.CustomerIncomeDetail;
import com.pennanttech.ws.model.customer.EmploymentDetail;
import com.pennanttech.ws.model.customer.FinCreditReviewDetailsData;
import com.pennanttech.ws.model.eligibility.AgreementData;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class CustomerWebServiceImpl implements CustomerRESTService, CustomerSOAPService {
	private static final Logger logger = Logger.getLogger(CustomerWebServiceImpl.class);

	private CustomerController customerController;
	private CustomerDetailsController customerDetailsController;
	private ValidationUtility validationUtility;
	private CustomerDetailsService customerDetailsService;
	private CustomerService customerService;
	private CustomerEmploymentDetailService customerEmploymentDetailService;
	private CustomerPhoneNumberService customerPhoneNumberService;
	private CustomerAddresService customerAddresService;
	private CustomerEMailService customerEMailService;
	private CustomerIncomeService customerIncomeService;
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	private CustomerDocumentService customerDocumentService;
	private CustomerBankInfoService customerBankInfoService;
	private CustomerGstService customerGstService;
	private CustomerExtLiabilityService customerExtLiabilityService;
	private CustomerChequeInfoDAO customerChequeInfoDAO;
	private DedupParmDAO dedupParmDAO;
	private CustomerDedupDAO customerDedupDAO;
	private BlackListCustomerDAO blacklistCustomerDAO;
	private CustomerCategoryDAO customerCategoryDAO;
	private CustomerCardSalesInfoDAO customerCardSalesInfoDAO;

	private CreditApplicationReviewService creditApplicationReviewService;
	/**
	 * Method for create customer in PLF system.
	 * 
	 * @param customerDetails
	 */
	@Override
	public CustomerDetails createCustomer(CustomerDetails customerDetails) throws ServiceException {
		logger.debug("Entering");
		// for logging purpose
		String[] logFields = getCustomerLogDetails(customerDetails);
		APIErrorHandlerService.logKeyFields(logFields);
		// bean validations
		validationUtility.validate(customerDetails, SaveValidationGroup.class);
		AuditHeader auditHeader = getAuditHeader(customerDetails, PennantConstants.TRAN_WF);
		// set empty to null
		setDefaults(customerDetails);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerDetailsService.doCustomerValidations(auditHeader);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		CustomerDetails response = null;
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerDetails();
				doEmptyResponseObject(response);
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
		}

		// call dedup service for customer duplication
		if (customerDetails.isDedupReq()) {
			List<CustomerDedup> dedupList = new ArrayList<CustomerDedup>(1);
			CustomerDedup customerDedup = doSetCustomerDedup(customerDetails);
			List<DedupParm> dedupParmList = dedupParmDAO.getDedupParmByModule(FinanceConstants.DEDUP_CUSTOMER,
					customerDedup.getCustCtgCode(), "");
			// TO Check duplicate customer in Local database
			for (DedupParm dedupParm : dedupParmList) {
				List<CustomerDedup> list = customerDedupDAO.fetchCustomerDedupDetails(customerDedup,
						dedupParm.getSQLQuery());
				if (list != null && !list.isEmpty()) {
					dedupList.addAll(list);
				}
			}
			if (!dedupList.isEmpty()) {
				response = new CustomerDetails();
				String[] valueParm = new String[1];
				valueParm[0] = "dedup";
				doEmptyResponseObject(response);
				response.setDedupReq(customerDetails.isDedupReq());
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90343", valueParm));
				response.setCustomerDedupList(dedupList);
				return response;
			}
		}

		// call dedup service for balck list customer 
		if (customerDetails.isBlackListReq()) {
			List<BlackListCustomers> blackList = new ArrayList<BlackListCustomers>(1);
			BlackListCustomers balckListData = doSetBlackListCustomerData(customerDetails);
			List<DedupParm> dedupParmList = dedupParmDAO.getDedupParmByModule(FinanceConstants.DEDUP_BLACKLIST,
					balckListData.getCustCtgCode(), "");
			// TO Check black List customer in Local database
			for (DedupParm dedupParm : dedupParmList) {
				List<BlackListCustomers> list = blacklistCustomerDAO.fetchBlackListedCustomers(balckListData,
						dedupParm.getSQLQuery());
				if (list != null && !list.isEmpty()) {
					blackList.addAll(list);
				}
			}
			if (!blackList.isEmpty()) {
				response = new CustomerDetails();
				String[] valueParm = new String[1];
				valueParm[0] = "blackList";
				doEmptyResponseObject(response);
				response.setBlackListReq(customerDetails.isBlackListReq());
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90343", valueParm));
				response.setBalckListCustomers(blackList);
				return response;
			}
		}
		// call create customer method in case of no errors
		response = customerController.createCustomer(customerDetails);
		// for logging purpose
		APIErrorHandlerService.logReference(response.getCustCIF());
		logger.debug("Leaving");
		return response;
	}

	private BlackListCustomers doSetBlackListCustomerData(CustomerDetails customerDetails) {
		Customer customer = customerDetails.getCustomer();
		if (customer != null) {
			BlackListCustomers blackListCustomer = new BlackListCustomers();
			String mobileNumber = "";

			List<CustomerPhoneNumber> phoneNumberList = customerDetails.getCustomerPhoneNumList();
			if (phoneNumberList != null && !phoneNumberList.isEmpty()) {
				if (phoneNumberList.size() > 1) {
					Collections.sort(phoneNumberList, new Comparator<CustomerPhoneNumber>() {
						@Override
						public int compare(CustomerPhoneNumber detail1, CustomerPhoneNumber detail2) {
							return detail2.getPhoneTypePriority() - detail1.getPhoneTypePriority();
						}
					});
				}
				CustomerPhoneNumber custPhone = phoneNumberList.get(0);
				mobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(),
						custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
			}
			blackListCustomer.setCustCIF(customer.getCustCIF());
			blackListCustomer.setCustShrtName(customer.getCustShrtName());
			blackListCustomer.setCustFName(customer.getCustFName());
			blackListCustomer.setCustLName(customer.getCustLName());
			blackListCustomer.setCustCRCPR(customer.getCustCRCPR());
			blackListCustomer.setCustPassportNo(customer.getCustPassportNo());
			blackListCustomer.setMobileNumber(mobileNumber);
			blackListCustomer.setCustNationality(customer.getCustNationality());
			blackListCustomer.setCustDOB(customer.getCustDOB());
			blackListCustomer.setCustCtgCode(customer.getCustCtgCode());

			blackListCustomer.setLikeCustFName(
					blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
			blackListCustomer.setLikeCustLName(
					blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");

			logger.debug("Leaving");

			return blackListCustomer;
		} else {
			return null;
		}
	}

	/**
	 * Method for verifying empty objects and set "null" value.
	 * 
	 * This method mainly written to handle API requests to resolve foreign key issues.
	 * 
	 * @param customerDetails
	 */
	private void setDefaults(CustomerDetails customerDetails) {
		if (customerDetails.getCustomer() != null) {
			Customer customer = customerDetails.getCustomer();
			customer.setCustSegment(StringUtils.trimToNull(customer.getCustSegment()));
			customer.setCustEmpSts(StringUtils.trimToNull(customer.getCustEmpSts()));
			customer.setCustCOB(StringUtils.trimToNull(customer.getCustCOB()));
			customer.setCustParentCountry(StringUtils.trimToNull(customer.getCustParentCountry()));
			customer.setCustResdCountry(StringUtils.trimToNull(customer.getCustResdCountry()));
			customer.setCustRiskCountry(StringUtils.trimToNull(customer.getCustRiskCountry()));
			customer.setCustCtgCode(StringUtils.trimToNull(customer.getCustCtgCode()));
			customer.setCustDSADept(StringUtils.trimToNull(customer.getCustDSADept()));
			customer.setCustEmpSts(StringUtils.trimToNull(customer.getCustEmpSts()));
			customer.setCustGenderCode(StringUtils.trimToNull(customer.getCustGenderCode()));
			customer.setCustGroupSts(StringUtils.trimToNull(customer.getCustGroupSts()));
			customer.setCustLng(StringUtils.trimToNull(customer.getCustLng()));
			customer.setCustSalutationCode(StringUtils.trimToNull(customer.getCustSalutationCode()));
			customer.setCustSector(StringUtils.trimToNull(customer.getCustSector()));
			customer.setCustSegment(StringUtils.trimToNull(customer.getCustSegment()));
			customer.setCustDftBranch(StringUtils.trimToNull(customer.getCustDftBranch()));
			customer.setCustBaseCcy(StringUtils.trimToNull(customer.getCustBaseCcy()));
			customer.setCustTypeCode(StringUtils.trimToNull(customer.getCustTypeCode()));
			customer.setCustAddlVar82(StringUtils.trimToNull(customer.getCustAddlVar82()));
			customer.setCustMaritalSts(StringUtils.trimToNull(customer.getCustMaritalSts()));
			customer.setCustNationality(StringUtils.trimToNull(customer.getCustNationality()));
		}
	}

	/**
	 * Method for update customer in PLF system.
	 * 
	 * @param customerDetails
	 */
	@Override
	public WSReturnStatus updateCustomer(CustomerDetails customerDetails) throws ServiceException {
		logger.debug("Entering");
		// for logging purpose
		String[] logFields = getCustomerLogDetails(customerDetails);
		APIErrorHandlerService.logKeyFields(logFields);
		// bean validations
		validationUtility.validate(customerDetails, UpdateValidationGroup.class);
		// set empty to null
		setDefaults(customerDetails);
		WSReturnStatus status = null;
		// customer validations
		status = validateCustomerCIF(customerDetails.getCustCIF());
		if (status != null) {
			return status;
		}
		// customer catageory validations
		status = validateCustomerCatageory(customerDetails);
		if (status != null) {
			return status;
		}

		// for logging purpose
		APIErrorHandlerService.logReference(customerDetails.getCustCIF());
		AuditHeader auditHeader = getAuditHeader(customerDetails, PennantConstants.TRAN_WF);

		AuditDetail auditDetail = customerDetailsService.doCustomerValidations(auditHeader);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
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
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
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
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
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
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
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
		// for logging purpose
		String[] logFields = getCustomerLogDetails(customerDetails);
		APIErrorHandlerService.logKeyFields(logFields);
		// bean validations
		validationUtility.validate(customerDetails, PersionalInfoGroup.class);
		Customer customer = null;
		// set empty to null
		setDefaults(customerDetails);
		// customer validations
		if (StringUtils.isNotBlank(customerDetails.getCustCIF())) {
			customer = customerDetailsService.getCustomerByCIF(customerDetails.getCustCIF());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerDetails.getCustCIF();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}
		// for logging purpose
		APIErrorHandlerService.logReference(customerDetails.getCustCIF());
		customerDetails.getCustomer().setCustID(customer.getCustID());
		customerDetails.getCustomer().setCustCtgCode(customer.getCustCtgCode());
		AuditHeader auditHeader = getAuditHeader(customerDetails, PennantConstants.TRAN_WF);

		AuditDetail auditDetail = customerService.doCustomerValidations(auditHeader);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
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
		// for logging purpose
		APIErrorHandlerService.logReference(employmentDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(employmentDetail.getCustomerEmploymentDetail(),
				PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerEmploymentDetailService
				.doValidations(employmentDetail.getCustomerEmploymentDetail(), customerDetails);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new EmploymentDetail();
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
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
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
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
		// for logging purpose
		APIErrorHandlerService.logReference(employmentDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(employmentDetail.getCustomerEmploymentDetail(),
				PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		AuditDetail auditDetail = customerEmploymentDetailService
				.doValidations(employmentDetail.getCustomerEmploymentDetail(), customer);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerEmploymentDetail customerEmpDetail = customerEmploymentDetailService
				.getApprovedCustomerEmploymentDetailByCustEmpId(
						employmentDetail.getCustomerEmploymentDetail().getCustEmpId());
		if (customerEmpDetail != null) {
			if (customerEmpDetail.getCustID() == (customer.getCustID())) {
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
				// for logging purpose
				APIErrorHandlerService.logReference(employmentDetail.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerEmploymentDetail customerEmpDetail = customerEmploymentDetailService
				.getApprovedCustomerEmploymentDetailByCustEmpId(employmentDetail.getEmployementId());

		if (customerEmpDetail != null) {
			customerEmploymentDetail.setCustEmpName(customerEmpDetail.getCustEmpName());
			// call delete customer service
			if (customerEmpDetail.getCustID() == (customer.getCustID())) {
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
		// for logging purpose
		APIErrorHandlerService.logReference(custPhoneNumber.getCif());
		custPhoneNumber.getCustomerPhoneNumber().setPhoneCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custPhoneNumber.getCustomerPhoneNumber(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerPhoneNumberService.doValidations(custPhoneNumber.getCustomerPhoneNumber(),
				APIConstants.SERVICE_TYPE_CREATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());

			}
		}

		// call add Customer Employment method in case of no errors
		WSReturnStatus returnStatus = customerDetailsController
				.addCustomerPhoneNumber(custPhoneNumber.getCustomerPhoneNumber(), custPhoneNumber.getCif());

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
		// for logging purpose
		APIErrorHandlerService.logReference(customerPhoneNumber.getCif());
		AuditHeader auditHeader = getAuditHeader(customerPhoneNumber.getCustomerPhoneNumber(),
				PennantConstants.TRAN_WF);
		customerPhoneNumber.getCustomerPhoneNumber().setPhoneCustID(customer.getCustID());
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerPhoneNumberService.doValidations(customerPhoneNumber.getCustomerPhoneNumber(),
				APIConstants.SERVICE_TYPE_UPDATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
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
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
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
				// for logging purpose
				APIErrorHandlerService.logReference(custPhoneNumber.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerPhoneNumber prvCustomerPhoneNumber = customerPhoneNumberService.getApprovedCustomerPhoneNumberById(
				customerPhoneNumber.getPhoneCustID(), customerPhoneNumber.getPhoneTypeCode());
		if (prvCustomerPhoneNumber != null) {
			if (prvCustomerPhoneNumber.getPhoneTypePriority() == Integer
					.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
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
		// for logging purpose
		APIErrorHandlerService.logReference(custAddress.getCif());
		custAddress.getCustomerAddres().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custAddress.getCustomerAddres(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerAddresService.doValidations(custAddress.getCustomerAddres(),
				APIConstants.SERVICE_TYPE_CREATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());

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
		// for logging purpose
		APIErrorHandlerService.logReference(custAddress.getCif());
		custAddress.getCustomerAddres().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custAddress.getCustomerAddres(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerAddresService.doValidations(custAddress.getCustomerAddres(),
				APIConstants.SERVICE_TYPE_UPDATE);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
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
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
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
				// for logging purpose
				APIErrorHandlerService.logReference(custAddress.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerAddres prvCustomerAddres = customerAddresService
				.getApprovedCustomerAddresById(customerAddres.getCustID(), customerAddres.getCustAddrType());
		if (prvCustomerAddres != null) {
			if (prvCustomerAddres.getCustAddrPriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
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
		// for logging purpose
		APIErrorHandlerService.logReference(custEMail.getCif());
		custEMail.getCustomerEMail().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custEMail.getCustomerEMail(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerEMailService.doValidations(custEMail.getCustomerEMail(),
				APIConstants.SERVICE_TYPE_CREATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
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
		// for logging purpose
		APIErrorHandlerService.logReference(custEMail.getCif());
		custEMail.getCustomerEMail().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custEMail.getCustomerEMail(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerEMailService.doValidations(custEMail.getCustomerEMail(),
				APIConstants.SERVICE_TYPE_UPDATE);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		// validate Customer with given CustCIF
		CustomerEMail customerEmail = customerEMailService.getApprovedCustomerEMailById(customer.getCustID(),
				custEMail.getCustomerEMail().getCustEMailTypeCode());
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
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
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
				// for logging purpose
				APIErrorHandlerService.logReference(custEMail.getCif());
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
		// for logging purpose
		APIErrorHandlerService.logReference(customerIncomeDetail.getCif());
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
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}
		WSReturnStatus returnStatus = customerDetailsController
				.addCustomerIncome(customerIncomeDetail.getCustomerIncome(), customerIncomeDetail.getCif());

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
		// for logging purpose
		APIErrorHandlerService.logReference(customerIncomeDetail.getCif());
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
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}
		// validate Customer with given CustCIF
		CustomerIncome curCustomerIncome = customerIncomeDetail.getCustomerIncome();
		curCustomerIncome.setCustId(customer.getCustID());
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
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
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
				customerIncome.setCustId(customer.getCustID());
				customerIncome.setIncomeType(customerIncomeDetail.getCustIncomeType());
				customerIncome.setCategory(customerIncomeDetail.getCategory());
				customerIncome.setIncomeExpense(customerIncomeDetail.getIncomeExpense());
				// for logging purpose
				APIErrorHandlerService.logReference(customerIncomeDetail.getCif());
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
		// for logging purpose
		APIErrorHandlerService.logReference(customerBankInfoDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerBankInfoDetail.getCustomerBankInfo(),
				PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerBankInfoService.doValidations(customerBankInfoDetail.getCustomerBankInfo(),PennantConstants.RECORD_TYPE_NEW);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		CustomerBankInfoDetail response = null;
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerBankInfoDetail();
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
		}

		// call add Customer Employment method in case of no errors
		response = customerDetailsController.addCustomerBankingInformation(customerBankInfoDetail.getCustomerBankInfo(),
				customerBankInfoDetail.getCif());

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
		// for logging purpose
		APIErrorHandlerService.logReference(customerBankInfoDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerBankInfoDetail.getCustomerBankInfo(),
				PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		AuditDetail auditDetail = customerBankInfoService.doValidations(customerBankInfoDetail.getCustomerBankInfo(),PennantConstants.RECORD_TYPE_UPD);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerBankInfo customerBankInfo = customerBankInfoService
				.getCustomerBankInfoById(customerBankInfoDetail.getCustomerBankInfo().getBankId());
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
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
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
				// for logging purpose
				APIErrorHandlerService.logReference(customerBankInfoDetail.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerBankInfo custBankInfo = customerBankInfoService
				.getCustomerBankInfoById(customerBankInfoDetail.getBankId());
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
	 * Method for create CustomerGstInfoDetail in PLF system.
	 * 
	 * @param CustomerGstInfoDetail
	 * @throws ServiceException
	 */
	@Override
	public CustomerGstInfoDetail addCustomerGstInformation(CustomerGstInfoDetail customerGstInfoDetail)
			throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(customerGstInfoDetail, SaveValidationGroup.class);
		if (customerGstInfoDetail.getCustomerGST() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerGstInfo";
			CustomerGstInfoDetail aCustomerGstInfoDetail = new CustomerGstInfoDetail();
			aCustomerGstInfoDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return aCustomerGstInfoDetail;
		}
		if (StringUtils.isNotBlank(customerGstInfoDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerGstInfoDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerGstInfoDetail.getCif();
				CustomerGstInfoDetail customerGstInfoDetails = new CustomerGstInfoDetail();
				customerGstInfoDetails.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
				return customerGstInfoDetail;
			}
		}
		// for logging purpose
		APIErrorHandlerService.logReference(customerGstInfoDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerGstInfoDetail.getCustomerGST(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerGstService.doValidations(customerGstInfoDetail.getCustomerGST(),
				PennantConstants.RECORD_TYPE_NEW);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		CustomerGstInfoDetail response = null;
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerGstInfoDetail();
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
		}

		// call add Customer gst method in case of no errors
		response = customerDetailsController.addCustomerGstInformation(customerGstInfoDetail.getCustomerGST(),
				customerGstInfoDetail.getCif());

		logger.debug("Leaving");
		return response;

	}
	/**
	 * Method for update CustomerGstInfoDetail in PLF system.
	 * 
	 * @param CustomerGstInfoDetail
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateCustomerGstInformation(CustomerGstInfoDetail customerGstInfoDetail)
			throws ServiceException {
		logger.debug("Entering");
		// bean validations
		validationUtility.validate(customerGstInfoDetail, UpdateValidationGroup.class);
		if (customerGstInfoDetail.getCustomerGST() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerBankInfo";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerGstInfoDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerGstInfoDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerGstInfoDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}
		}
		// for logging purpose
		APIErrorHandlerService.logReference(customerGstInfoDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerGstInfoDetail.getCustomerGST(), PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		AuditDetail auditDetail = customerGstService.doValidations(customerGstInfoDetail.getCustomerGST(),
				PennantConstants.RECORD_TYPE_UPD);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerGST CustomerGSTInfo = customerGstService
				.getCustomerGstDeatailsByCustomerId(customerGstInfoDetail.getCustomerGST().getId());

		if (CustomerGSTInfo != null) {
			// call update customer if there is no errors
			response = customerDetailsController.updateCustomerGstInformation(customerGstInfoDetail.getCustomerGST(),
					customerGstInfoDetail.getCif());
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerGstInfoDetail.getCustomerGST().getCustId());
			valueParm[1] = customerGstInfoDetail.getCif();
			return APIErrorHandlerService.getFailedStatus("90116", valueParm);
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * get CustomerGstInfoDetail by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerGstnformation(String custCIF) throws ServiceException {
		logger.debug("Entering");
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
		CustomerDetails response = new CustomerDetails();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
			response.setCustomer(null);
		} else {
			response = customerDetailsController.getCustomerGstInformation(custCIF);
		}
		logger.debug("Leaving");

		return response;
	}
	/**
	 * delete CustomerGstInfoDetail.
	 * 
	 * @param CustomerGstInfoDetail
	 */
	@Override
	public WSReturnStatus deleteCustomerGstInformation(CustomerGstInfoDetail customerGstInfoDetail)
			throws ServiceException {

		logger.debug("Enetring");
		// bean validations
		validationUtility.validate(customerGstInfoDetail, DeleteValidationGroup.class);

		// customer validations
		CustomerGST customerGST = null;
		if (StringUtils.isNotBlank(customerGstInfoDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerGstInfoDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerGstInfoDetail.getCif();
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			} else {
				customerGST = new CustomerGST();
				customerGST.setCustId(customerDetails.getCustID());
				customerGST.setId(customerGstInfoDetail.getId());
				// for logging purpose
				APIErrorHandlerService.logReference(customerGstInfoDetail.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerGST customeGST = customerGstService.getCustomerGstDeatailsByCustomerId(customerGstInfoDetail.getId());

		if (customeGST != null) {
			// call delete customer service
			response = customerDetailsController.deleteCustomerGSTInformation(customeGST);
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerGstInfoDetail.getId());
			valueParm[1] = customerGstInfoDetail.getCif();
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
		// for logging purpose
		APIErrorHandlerService.logReference(customerChequeInfoDetail.getCif());
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
		// for logging purpose
		APIErrorHandlerService.logReference(customerChequeInfoDetail.getCif());
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerChequeInfo customerChequeInfo = customerChequeInfoDAO.getCustomerChequeInfoById(customer.getCustID(),
				customerChequeInfoDetail.getCustomerChequeInfo().getChequeSeq(), "");
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
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
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
				// for logging purpose
				APIErrorHandlerService.logReference(customerChequeInfoDetail.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerChequeInfo custChequeInfo = customerChequeInfoDAO
				.getCustomerChequeInfoById(customerChequeInfo.getCustID(), customerChequeInfo.getChequeSeq(), "");
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
	 * @param liability
	 * @throws ServiceException
	 */
	@Override
	public CustomerExtLiabilityDetail addCustomerExternalLiability(CustomerExtLiabilityDetail customerLiability)
			throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(customerLiability, SaveValidationGroup.class);
		if (customerLiability.getExternalLiability() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerExtLiability";
			CustomerExtLiabilityDetail aCustomerExtLiabilityDetail = new CustomerExtLiabilityDetail();
			aCustomerExtLiabilityDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return aCustomerExtLiabilityDetail;
		}
		if (StringUtils.isNotBlank(customerLiability.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerLiability.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerLiability.getCif();
				CustomerExtLiabilityDetail custExtLiabilityDetail = new CustomerExtLiabilityDetail();
				custExtLiabilityDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
				return custExtLiabilityDetail;
			}
		}
		// for logging purpose
		APIErrorHandlerService.logReference(customerLiability.getCif());
		AuditHeader auditHeader = getAuditHeader(customerLiability.getExternalLiability(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		AuditDetail auditDetail = validation.doValidations(customerLiability.getExternalLiability());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		CustomerExtLiabilityDetail response = null;
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerExtLiabilityDetail();
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
		}

		// call add Customer Employment method in case of no errors
		response = customerDetailsController.addCustomerExternalLiability(customerLiability.getExternalLiability(),
				customerLiability.getCif());

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
		if (customerExtLiabilityDetail.getExternalLiability() == null) {
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
		// for logging purpose
		APIErrorHandlerService.logReference(customerExtLiabilityDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerExtLiabilityDetail.getExternalLiability(),
				PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		AuditDetail auditDetail = validation.doValidations(customerExtLiabilityDetail.getExternalLiability());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		WSReturnStatus response = null;
		// validate Customer with given CustCIF

		CustomerExtLiability liability = new CustomerExtLiability();
		liability.setCustId(customer.getCustID());
		liability.setSeqNo(customerExtLiabilityDetail.getLiabilitySeq());

		liability = customerExtLiabilityService.getLiability(liability);
		if (liability != null) {
			// call update customer if there is no errors
			response = customerDetailsController.updateCustomerExternalLiability(
					customerExtLiabilityDetail.getExternalLiability(), customerExtLiabilityDetail.getCif());
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerExtLiabilityDetail.getExternalLiability().getSeqNo());
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
		logger.debug(Literal.ENTERING);
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
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
		logger.debug(Literal.LEAVING);

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
				customerExtLiability.setCustId(customerDetails.getCustID());
				customerExtLiability.setSeqNo(customerExtLiabilityDetail.getLiabilitySeq());
				// for logging purpose
				APIErrorHandlerService.logReference(customerExtLiabilityDetail.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF

		CustomerExtLiability liability = new CustomerExtLiability();
		liability.setCustId(customerExtLiability.getCustId());
		liability.setSeqNo(customerExtLiabilityDetail.getLiabilitySeq());

		liability = customerExtLiabilityService.getLiability(liability);

		CustomerExtLiability custExtLiability = customerExtLiabilityService.getLiability(customerExtLiability);
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
		// for logging purpose
		APIErrorHandlerService.logReference(customerDocumentDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerDocumentDetail.getCustomerDocument(),
				PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerDocumentService
				.validateCustomerDocuments(customerDocumentDetail.getCustomerDocument(), customer);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}
		// call add Customer Employment method in case of no errors
		WSReturnStatus returnStatus = customerDetailsController
				.addCustomerDocument(customerDocumentDetail.getCustomerDocument(), customerDocumentDetail.getCif());

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
	public WSReturnStatus updateCustomerDocument(CustomerDocumentDetail customerDocumentDetail)
			throws ServiceException {
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
		// for logging purpose
		APIErrorHandlerService.logReference(customerDocumentDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerDocumentDetail.getCustomerDocument(),
				PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerDocumentService
				.validateCustomerDocuments(customerDocumentDetail.getCustomerDocument(), customer);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		// validate Customer with given CustCIF
		CustomerDocument customerDocument = customerDocumentService.getApprovedCustomerDocumentById(
				customer.getCustID(), customerDocumentDetail.getCustomerDocument().getCustDocCategory());
		WSReturnStatus returnStatus = null;
		if (customerDocument != null) {
			// call update customer if there is no errors
			returnStatus = customerDetailsController.updateCustomerDocument(
					customerDocumentDetail.getCustomerDocument(), customerDocumentDetail.getCif());
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
		// for logging purpose
		APIErrorHandlerService.logReference(custCIF);
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
	public WSReturnStatus deleteCustomerDocument(CustomerDocumentDetail customerDocumentDetail)
			throws ServiceException {
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
				// for logging purpose
				APIErrorHandlerService.logReference(customerDocumentDetail.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerDocument prvCustomerDocument = customerDocumentService
				.getApprovedCustomerDocumentById(customerDocument.getCustID(), customerDocument.getCustDocCategory());
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
		String mailId = "";
		Customer customer = customerDetails.getCustomer();
		List<CustomerPhoneNumber> phoneNumberList = customerDetails.getCustomerPhoneNumList();
		List<CustomerEMail> mailIdList = customerDetails.getCustomerEMailList();

		if (phoneNumberList != null && !phoneNumberList.isEmpty()) {
			if (phoneNumberList.size() > 1) {
				Collections.sort(phoneNumberList, new Comparator<CustomerPhoneNumber>() {
					@Override
					public int compare(CustomerPhoneNumber detail1, CustomerPhoneNumber detail2) {
						return detail2.getPhoneTypePriority() - detail1.getPhoneTypePriority();
					}
				});
			}
			CustomerPhoneNumber custPhone = phoneNumberList.get(0);
			mobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(),
					custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
		}

		if (mailIdList != null && !mailIdList.isEmpty()) {
			if (mailIdList.size() > 1) {
				// mailIdList.sort((m1, m2) -> m2.getCustEMailPriority() -
				// m1.getCustEMailPriority());
				Collections.sort(mailIdList, new Comparator<CustomerEMail>() {
					@Override
					public int compare(CustomerEMail detail1, CustomerEMail detail2) {
						return detail2.getCustEMailPriority() - detail1.getCustEMailPriority();
					}
				});
			}
			CustomerEMail custMail = mailIdList.get(0);
			mailId = custMail.getCustEMail();
		}

		List<CustomerDocument> customerDocumentsList = customerDetails.getCustomerDocumentsList();
		String panNumber = PennantApplicationUtil.getPanNumber(customerDocumentsList);
		if (StringUtils.isNotBlank(panNumber)) {
			customerDetails.getCustomer().setCustCRCPR(panNumber);
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
		customerDedup.setCustPOB(customer.getCustPOB());
		customerDedup.setCustResdCountry(customer.getCustResdCountry());
		customerDedup.setMobileNumber(mobileNumber);
		customerDedup.setCustEMail(mailId);

		logger.debug("Leaving");
		return customerDedup;

	}

	@Override
	public AgreementData getCustomerAgreement(AgreementRequest agrRequest) throws ServiceException {

		logger.debug("Enetring");
		AgreementData agrData = null;
		try {
			// Mandatory validation
			if (StringUtils.isBlank(agrRequest.getCif())) {
				agrData = new AgreementData();
				String[] valueParm = new String[1];
				valueParm[0] = "CIF";
				agrData.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return agrData;
			}

			if (StringUtils.isBlank(agrRequest.getAgreementType())) {
				agrData = new AgreementData();
				String[] valueParm = new String[1];
				valueParm[0] = "AgreementType";
				agrData.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return agrData;
			}
			// for logging purpose
			APIErrorHandlerService.logReference(agrRequest.getCif());
			if (!StringUtils.equals(agrRequest.getAgreementType(), APIConstants.CUST_AGR_NAME)) {
				agrData = new AgreementData();
				String[] valueParm = new String[2];
				valueParm[0] = APIConstants.CUST_AGR_NAME;
				valueParm[1] = "AgreementType";
				agrData.setReturnStatus(APIErrorHandlerService.getFailedStatus("90298", valueParm));
				return agrData;
			}
			// validate Customer with given CustCIF
			Customer customer = customerDetailsService.getCustomerByCIF(agrRequest.getCif());
			if (customer != null) {
				agrData = customerController.getCustomerAgreement(customer.getCustID());
			} else {
				agrData = new AgreementData();
				String[] valueParm = new String[1];
				valueParm[0] = agrRequest.getCif();
				agrData.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
			}
		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			agrData = new AgreementData();
			agrData.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");

		return agrData;
	}

	@Override
	public ProspectCustomerDetails getDedupCustomer(ProspectCustomerDetails customer) {
		logger.debug("Entering");

		String reference = null;
		ProspectCustomerDetails response = null;
		// bean validations
		validationUtility.validate(customer, ProspectCustDetailsGroup.class);

		// validate Customer category code
		boolean isExist = customerCategoryDAO.isCustCtgExist(customer.getCustCtgCode(), "");
		if (!isExist) {
			response = new ProspectCustomerDetails();
			String[] valueParm = new String[2];
			valueParm[0] = "CustCtg";
			valueParm[1] = customer.getCustCtgCode();
			response.setReturnStatus(getErrorDetails("90224", valueParm));
			return response;
		}

		response = customerController.getDedupCustomer(customer);

		logger.debug(Literal.LEAVING);
		return response;
	}
	/**
	 * add CreditReviewDetails.
	 * 
	 * @param finCreditReviewDetailsData
	 */
	@Override
	public WSReturnStatus addCreditReviewDetails(FinCreditReviewDetailsData finCreditReviewDetailsData) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus response = null;
		Customer customer=null;
		if(StringUtils.isBlank(finCreditReviewDetailsData.getCif())){
			String[] valueParm = new String[1];
			valueParm[0] = "Cif";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);	
		} else {
			 customer = customerDetailsService.getCustomerByCIF(finCreditReviewDetailsData.getCif());
			
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finCreditReviewDetailsData.getCif();
				APIErrorHandlerService.getFailedStatus("90101", valueParm);			
			}
		}
		for(FinCreditReviewDetails detail:finCreditReviewDetailsData.getFinCreditReviewDetails()){
			if(customer!=null){
			detail.setCreditRevCode(customer.getCustCtgCode());
			detail.setCustomerId(customer.getCustID());
			}
			if(StringUtils.isBlank(detail.getCurrency())){
				detail.setCurrency("INR");
			}
			if(StringUtils.isBlank(detail.getAuditYear())){
				String[] valueParm = new String[1];
				valueParm[0] = "Audit Year";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);	
			}
			if(StringUtils.isBlank(detail.getBankName())){
				String[] valueParm = new String[1];
				valueParm[0] = "Bank Name";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);	
			}
			if(StringUtils.isBlank(detail.getAuditors())){
				String[] valueParm = new String[1];
				valueParm[0] = "Auditors";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);	
			}
			if(StringUtils.isBlank(detail.getLocation())){
				String[] valueParm = new String[1];
				valueParm[0] = "Location";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);	
			}
			if(StringUtils.isBlank(String.valueOf(detail.getAuditedDate()))){
				String[] valueParm = new String[1];
				valueParm[0] = "Audited Date";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);	
			}
			if(StringUtils.isBlank(String.valueOf(detail.isQualified()))){
				String[] valueParm = new String[1];
				valueParm[0] = "Qualified";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);	
			}
			if(StringUtils.isBlank(detail.getAuditType())){
				String[] valueParm = new String[1];
				valueParm[0] = "AuditType";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);	
			} else if (!StringUtils.equals(detail.getAuditType(), FacilityConstants.CREDITREVIEW_AUDITED)
					&& !StringUtils.equals(detail.getAuditType(), FacilityConstants.CREDITREVIEW_UNAUDITED)
					&& !StringUtils.equals(detail.getAuditType(), FacilityConstants.CREDITREVIEW_MNGRACNTS)) {
				String[] valueParm = new String[2];
				valueParm[0] = "AuditType";
				valueParm[1] = FacilityConstants.CREDITREVIEW_AUDITED + "," + FacilityConstants.CREDITREVIEW_UNAUDITED
						+ "," + FacilityConstants.CREDITREVIEW_MNGRACNTS ;
				return APIErrorHandlerService.getFailedStatus("90281", valueParm);

			}
			for(FinCreditReviewSummary summaryDetail:detail.getCreditReviewSummaryEntries()){
				if(StringUtils.isBlank(String.valueOf(summaryDetail.getSubCategoryCode()))){
					String[] valueParm = new String[1];
					valueParm[0] = "SubCategory Code";
					return APIErrorHandlerService.getFailedStatus("90502", valueParm);	
				}
				if(StringUtils.isBlank(String.valueOf(summaryDetail.getItemValue()))){
					String[] valueParm = new String[1];
					valueParm[0] = "Item Value";
					return APIErrorHandlerService.getFailedStatus("90502", valueParm);	
				}
			}
			
		}
		customerController.doAddCreditReviewDetails(finCreditReviewDetailsData);
		
		logger.debug(Literal.LEAVING);

		return null;
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
		return new AuditHeader(String.valueOf(aCustomerDetails.getCustID()),
				String.valueOf(aCustomerDetails.getCustID()), null, null, auditDetail,
				aCustomerDetails.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerPhoneNumber
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerPhoneNumber aCustomerPhoneNumber, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerPhoneNumber.getBefImage(),
				aCustomerPhoneNumber);
		return new AuditHeader(String.valueOf(aCustomerPhoneNumber.getPhoneCustID()),
				String.valueOf(aCustomerPhoneNumber.getPhoneCustID()), null, null, auditDetail,
				aCustomerPhoneNumber.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
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
				aCustomerEmploymentDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
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
				null, null, auditDetail, aCustomerAddres.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetail>>());
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
				new HashMap<String, ArrayList<ErrorDetail>>());
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
				null, null, auditDetail, aCustomerIncome.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetail>>());
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
	 * @param aCustomerBankInfo
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerGST aCustomerGST, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerGST.getBefImage(), aCustomerGST);
		return new AuditHeader(String.valueOf(aCustomerGST.getCustId()),
				String.valueOf(aCustomerGST.getCustId()), null, null, auditDetail,
				aCustomerGST.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerExtLiability
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerExtLiability externalLiability, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, externalLiability.getBefImage(), externalLiability);
		return new AuditHeader(String.valueOf(externalLiability.getCustId()),
				String.valueOf(externalLiability.getCustId()), null, null, auditDetail,
				externalLiability.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
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

		// set default error code and description in case of Error code does not
		// exists.
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

	/**
	 * Method for fetch the basic log fields from the given request.
	 * 
	 * @param customerDetails
	 * @return
	 */
	private String[] getCustomerLogDetails(CustomerDetails customerDetails) {
		// for logging purpose
		String[] logFields = null;
		if (customerDetails != null) {
			logFields = new String[3];
			logFields[0] = customerDetails.getCustCtgCode();
			logFields[1] = customerDetails.getCustDftBranch();

			List<CustomerPhoneNumber> customerPhoneNumbers = customerDetails.getCustomerPhoneNumList();
			if (customerPhoneNumbers != null && !customerPhoneNumbers.isEmpty()) {
				CustomerPhoneNumber custPhoneNumber = customerPhoneNumbers.get(0);
				logFields[2] = custPhoneNumber.getPhoneNumber();
			}
		}
		return logFields;
	}

	/**
	 * Method for validate customer CIF
	 * 
	 * @param custCIF
	 * @return
	 */
	private WSReturnStatus validateCustomerCIF(String custCIF) {
		WSReturnStatus returnStatus = null;
		if (StringUtils.isNotBlank(custCIF)) {
			int mainCount = customerDetailsService.getCustomerCountByCIF(custCIF, "");
			if (mainCount == 0) {
				String[] valueParm = new String[1];
				valueParm[0] = custCIF;
				return APIErrorHandlerService.getFailedStatus("90101", valueParm);
			}

			int tempCount = customerDetailsService.getCustomerCountByCIF(custCIF, "_Temp");
			if (tempCount > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = custCIF;
				return APIErrorHandlerService.getFailedStatus("90248", valueParm);
			}
		}
		return returnStatus;
	}

	/**
	 * Method for validate customer Catageory
	 * 
	 * @param customerDetails
	 * @return
	 */
	private WSReturnStatus validateCustomerCatageory(CustomerDetails customerDetails) {
		WSReturnStatus returnStatus = null;
		Customer customer = customerDetailsService.getCustomerByCIF(customerDetails.getCustCIF());
		if (customer != null) {
			if (StringUtils.isBlank(customerDetails.getCustCtgCode())) {
				customerDetails.setCustCtgCode(customer.getCustCtgCode());
			} else {
				String[] valueParm = new String[2];
				valueParm[0] = "categoryCode";
				valueParm[1] = "update Customer";
				return APIErrorHandlerService.getFailedStatus("90329", valueParm);
			}
		}
		return returnStatus;
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
	public void setExternalLiabilityDAO(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
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

	@Autowired
	public void setBlackListCustomerDAO(BlackListCustomerDAO blacklistCustomerDAO) {
		this.blacklistCustomerDAO = blacklistCustomerDAO;
	}

	@Autowired
	public void setCustomerCategoryDAO(CustomerCategoryDAO customerCategoryDAO) {
		this.customerCategoryDAO = customerCategoryDAO;
	}
	@Autowired
	public void setCreditApplicationReviewService(CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}
	@Autowired
	public void setCustomerGstService(CustomerGstService customerGstService) {
		this.customerGstService = customerGstService;
	}

	public CustomerCardSalesInfoDAO getCustomerCardSalesInfoDAO() {
		return customerCardSalesInfoDAO;
	}

	public void setCustomerCardSalesInfoDAO(CustomerCardSalesInfoDAO customerCardSalesInfoDAO) {
		this.customerCardSalesInfoDAO = customerCardSalesInfoDAO;
	}

}
