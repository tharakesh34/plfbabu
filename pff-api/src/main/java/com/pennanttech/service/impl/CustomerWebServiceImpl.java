package com.pennanttech.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.dao.applicationmaster.CustomerCategoryDAO;
import com.pennant.backend.dao.approvalstatusenquiry.ApprovalStatusEnquiryDAO;
import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.dao.customermasters.CustomerCardSalesInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.customermasters.FinCreditRevSubCategoryDAO;
import com.pennant.backend.dao.dedup.DedupFieldsDAO;
import com.pennant.backend.dao.dedup.DedupParmDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
import com.pennant.backend.model.BuilderTable;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.CustCardSales;
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
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.customermasters.ProspectCustomerDetails;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.service.customermasters.CustomerBankInfoService;
import com.pennant.backend.service.customermasters.CustomerCardSalesInfoService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.service.customermasters.CustomerEmploymentDetailService;
import com.pennant.backend.service.customermasters.CustomerExtLiabilityService;
import com.pennant.backend.service.customermasters.CustomerGstService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.customermasters.CustomerPhoneNumberService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.service.customermasters.validation.CustomerExtLiabilityValidation;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.limit.LimitDetailService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.api.controller.AbstractController;
import com.pennant.validation.DeleteValidationGroup;
import com.pennant.validation.PersionalInfoGroup;
import com.pennant.validation.ProspectCustDetailsGroup;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.UpdateValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennant.ws.exception.ServiceExceptionDetails;
import com.pennanttech.controller.CustomerController;
import com.pennanttech.controller.CustomerDetailsController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pffws.CustomerRESTService;
import com.pennanttech.pffws.CustomerSOAPService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.customer.AgreementRequest;
import com.pennanttech.ws.model.customer.CustAddress;
import com.pennanttech.ws.model.customer.CustDedupDetails;
import com.pennanttech.ws.model.customer.CustDedupRequest;
import com.pennanttech.ws.model.customer.CustDedupResponse;
import com.pennanttech.ws.model.customer.CustEMail;
import com.pennanttech.ws.model.customer.CustPhoneNumber;
import com.pennanttech.ws.model.customer.CustValidationResponse;
import com.pennanttech.ws.model.customer.CustomerBankInfoDetail;
import com.pennanttech.ws.model.customer.CustomerCardSaleInfoDetails;
import com.pennanttech.ws.model.customer.CustomerChequeInfoDetail;
import com.pennanttech.ws.model.customer.CustomerDirectorDetail;
import com.pennanttech.ws.model.customer.CustomerDocumentDetail;
import com.pennanttech.ws.model.customer.CustomerExtLiabilityDetail;
import com.pennanttech.ws.model.customer.CustomerExtendedFieldDetails;
import com.pennanttech.ws.model.customer.CustomerGstInfoDetail;
import com.pennanttech.ws.model.customer.CustomerIncomeDetail;
import com.pennanttech.ws.model.customer.EmploymentDetail;
import com.pennanttech.ws.model.customer.FinCreditReviewDetailsData;
import com.pennanttech.ws.model.customer.SRMCustRequest;
import com.pennanttech.ws.model.eligibility.AgreementData;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class CustomerWebServiceImpl extends AbstractController implements CustomerRESTService, CustomerSOAPService {
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
	private CustomerCardSalesInfoService customerCardSalesInfoService;
	private CustomerExtLiabilityService customerExtLiabilityService;
	private CustomerChequeInfoDAO customerChequeInfoDAO;
	private DedupParmDAO dedupParmDAO;
	private CustomerDedupDAO customerDedupDAO;
	private BlackListCustomerDAO blacklistCustomerDAO;
	private CustomerCategoryDAO customerCategoryDAO;
	private CustomerCardSalesInfoDAO customerCardSalesInfoDAO;
	private CustomerDAO customerDAO;
	private DedupFieldsDAO dedupFieldsDAO;
	private DirectorDetailService directorDetailService;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private FinCreditRevSubCategoryDAO finCreditRevSubCategoryDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private ApprovalStatusEnquiryDAO approvalStatusEnquiryDAO;
	private JointAccountDetailDAO jointAccountDetailDAO;
	private LimitDetailService limitDetailService;
	private CustomerIncomeDAO customerIncomeDAO;

	/**
	 * Method for create customer in PLF system.
	 * 
	 * @param cd
	 */
	@Override
	public CustomerDetails createCustomer(CustomerDetails cd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (cd == null) {
			return null;
		}

		String[] logFields = getCustomerLogDetails(cd);
		APIErrorHandlerService.logKeyFields(logFields);

		validationUtility.validate(cd, SaveValidationGroup.class);

		doBasicMandatoryValidations(cd.getCustomer());

		AuditHeader auditHeader = getAuditHeader(cd, PennantConstants.TRAN_WF);
		// set empty to null
		setDefaults(cd);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerDetailsService.doCustomerValidations(auditHeader);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		CustomerDetails response = null;
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerDetails();
				doEmptyResponseObject(response);
				response.setReturnStatus(getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
		}

		// call dedup service for customer duplication
		if (cd.isDedupReq()) {
			List<CustomerDedup> dedupList = new ArrayList<>(1);
			CustomerDedup customerDedup = doSetCustomerDedup(cd);
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
				response.setDedupReq(cd.isDedupReq());
				response.setReturnStatus(getFailedStatus("90343", valueParm));
				response.setCustomerDedupList(dedupList);
				return response;
			}
		}

		// call dedup service for balck list customer
		if (cd.isBlackListReq()) {
			List<BlackListCustomers> blackList = new ArrayList<>(1);
			BlackListCustomers balckListData = doSetBlackListCustomerData(cd);
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
				response.setBlackListReq(cd.isBlackListReq());
				response.setReturnStatus(getFailedStatus("90343", valueParm));
				response.setBalckListCustomers(blackList);
				return response;
			}
		}
		// call create customer method in case of no errors
		response = customerController.createCustomer(cd);
		// for logging purpose
		logReference(response.getCustCIF());
		logger.debug(Literal.LEAVING);
		return response;
	}

	private BlackListCustomers doSetBlackListCustomerData(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		Customer customer = customerDetails.getCustomer();

		if (customer == null) {
			return null;
		}

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

		logger.debug(Literal.LEAVING);
		return blackListCustomer;
	}

	/**
	 * Method for verifying empty objects and set "null" value.
	 * 
	 * This method mainly written to handle API requests to resolve foreign key issues.
	 * 
	 * @param customerDetails
	 */
	private void setDefaults(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
	}

	@Override
	public WSReturnStatus updateCustomer(CustomerDetails cd) throws ServiceException {
		logger.debug(Literal.ENTERING);
		// for logging purpose
		String[] logFields = getCustomerLogDetails(cd);
		APIErrorHandlerService.logKeyFields(logFields);
		// bean validations
		validationUtility.validate(cd, UpdateValidationGroup.class);
		doBasicMandatoryValidations(cd.getCustomer());
		// set empty to null
		setDefaults(cd);
		WSReturnStatus status = null;
		// customer validations
		status = validateCustomerCIF(cd.getCustCIF());
		if (status != null) {
			return status;
		}
		// customer catageory validations
		status = validateCustomerCatageory(cd);
		if (status != null) {
			return status;
		}

		// for logging purpose
		logReference(cd.getCustCIF());
		AuditHeader auditHeader = getAuditHeader(cd, PennantConstants.TRAN_WF);

		AuditDetail auditDetail = customerDetailsService.doCustomerValidations(auditHeader);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		// call update customer if there is no errors
		WSReturnStatus returnStatus = customerController.updateCustomer(cd);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
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
			response.setReturnStatus(getFailedStatus("90101", valueParm));
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * delete customer Details by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public WSReturnStatus deleteCustomer(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer != null) {
			// call delete customer service
			response = customerController.deleteCustomerById(customer.getCustID());
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			return getFailedStatus("90101", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * get CustomerPersonalInfo by the given customer cif.
	 * 
	 * @param custCIF
	 */

	@Override
	public CustomerDetails getCustomerPersonalInfo(String custCIF) throws ServiceException {

		logger.debug(Literal.ENTERING);

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
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
			response.setReturnStatus(getFailedStatus("90101", valueParm));
		}

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// for logging purpose
		String[] logFields = getCustomerLogDetails(customerDetails);
		APIErrorHandlerService.logKeyFields(logFields);
		// bean validations
		validationUtility.validate(customerDetails, PersionalInfoGroup.class);
		doBasicMandatoryValidations(customerDetails.getCustomer());

		Customer customer = null;
		// set empty to null
		setDefaults(customerDetails);
		// customer validations
		if (StringUtils.isNotBlank(customerDetails.getCustCIF())) {
			customer = customerDetailsService.getCustomerByCIF(customerDetails.getCustCIF());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerDetails.getCustCIF();
				return getFailedStatus("90101", valueParm);
			}
			if (!StringUtils.equals(customerDetails.getCustCtgCode(), customer.getCustCtgCode())) {
				String[] valueParm = new String[2];
				valueParm[0] = customerDetails.getCustCtgCode();
				valueParm[1] = customerDetails.getCustCIF();
				return getFailedStatus("90599", valueParm);
			}
		}

		// for logging purpose
		logReference(customerDetails.getCustCIF());
		customerDetails.getCustomer().setCustID(customer.getCustID());
		customerDetails.getCustomer().setCustCtgCode(customer.getCustCtgCode());
		AuditHeader auditHeader = getAuditHeader(customerDetails, PennantConstants.TRAN_WF);

		AuditDetail auditDetail = customerService.doCustomerValidations(auditHeader);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		// call update customer if there is no errors
		WSReturnStatus returnStatus = customerController.updateCustomerPersionalInfo(customerDetails);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(employmentDetail, SaveValidationGroup.class);
		EmploymentDetail response = null;
		if (employmentDetail.getCustomerEmploymentDetail() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "employment";
			EmploymentDetail custEmploymentDetail = new EmploymentDetail();
			custEmploymentDetail.setReturnStatus(getFailedStatus("90502", valueParm));
			return custEmploymentDetail;
		}
		Customer customerDetails = null;
		if (StringUtils.isNotBlank(employmentDetail.getCif())) {
			customerDetails = customerDetailsService.getCustomerByCIF(employmentDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = employmentDetail.getCif();
				EmploymentDetail customerEmpDetail = new EmploymentDetail();
				customerEmpDetail.setReturnStatus(getFailedStatus("90101", valueParm));
				return customerEmpDetail;
			}
		}
		// for logging purpose
		logReference(employmentDetail.getCif());
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
				response.setReturnStatus(getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
		}

		// call add Customer Employment method in case of no errors
		response = customerController.addCustomerEmployment(employmentDetail.getCustomerEmploymentDetail(),
				employmentDetail.getCif());

		logger.debug(Literal.LEAVING);
		return response;

	}

	/**
	 * get CustomerEmployment by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerEmployment(String custCIF) throws ServiceException {

		logger.debug(Literal.ENTERING);
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
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
		logger.debug(Literal.LEAVING);

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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(employmentDetail, UpdateValidationGroup.class);
		if (employmentDetail.getCustomerEmploymentDetail() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "employment";
			return getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(employmentDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(employmentDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = employmentDetail.getCif();
				return getFailedStatus("90101", valueParm);
			}
		}
		// for logging purpose
		logReference(employmentDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(employmentDetail.getCustomerEmploymentDetail(),
				PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		AuditDetail auditDetail = customerEmploymentDetailService
				.doValidations(employmentDetail.getCustomerEmploymentDetail(), customer);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
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
				return getFailedStatus("90104", valueParm);
			}

		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(employmentDetail.getCustomerEmploymentDetail().getCustEmpId());
			valueParm[1] = employmentDetail.getCif();
			return getFailedStatus("90104", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return response;

	}

	/**
	 * delete CustomerEmploymentDetail.
	 * 
	 * @param customerEmploymentDetail
	 */
	@Override
	public WSReturnStatus deleteCustomerEmployment(EmploymentDetail employmentDetail) throws ServiceException {

		logger.debug(Literal.ENTERING);
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
				return getFailedStatus("90101", valueParm);
			} else {
				customerEmploymentDetail = new CustomerEmploymentDetail();
				customerEmploymentDetail.setCustID(customer.getCustID());
				customerEmploymentDetail.setCustEmpId(employmentDetail.getEmployementId());
				// for logging purpose
				logReference(employmentDetail.getCif());
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
				return getFailedStatus("90104", valueParm);
			}
		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(employmentDetail.getEmployementId());
			valueParm[1] = employmentDetail.getCif();
			return getFailedStatus("90104", valueParm);
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public CustomerDirectorDetail addCustomerDirectorDetail(CustomerDirectorDetail customerDirectorDetail)
			throws ServiceException {
		logger.debug(Literal.ENTERING);
		CustomerDirectorDetail response = new CustomerDirectorDetail();
		String cif = customerDirectorDetail.getCif();
		// bean validations
		validationUtility.validate(customerDirectorDetail, SaveValidationGroup.class);
		if (customerDirectorDetail.getDirectorDetail() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "directorDetail";
			response.setReturnStatus(getFailedStatus("90502", valueParm));
			return customerDirectorDetail;
		}
		Customer customerDetails = null;
		if (StringUtils.isNotBlank(cif)) {
			customerDetails = customerDetailsService.getCustomerByCIF(cif);
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				response.setReturnStatus(getFailedStatus("90101", valueParm));
				return response;
			}
			if (StringUtils.equals(customerDetails.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
				String[] valueParm = new String[2];
				valueParm[0] = "director details";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_CORP + "," + PennantConstants.PFF_CUSTCTG_SME;
				response.setReturnStatus(getFailedStatus("90124", valueParm));
				return response;
			}

		}
		// for logging purpose
		logReference(customerDirectorDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerDirectorDetail.getDirectorDetail(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = directorDetailService.doValidations(customerDirectorDetail.getDirectorDetail(),
				customerDetails);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {

				response.setReturnStatus(getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
		}

		// call add Customer DirectorDetails method in case of no errors
		response = customerController.addCustomerDirectorDetails(customerDirectorDetail.getDirectorDetail(),
				customerDirectorDetail.getCif());

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public CustomerDetails getCustomerDirectorDetails(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
		CustomerDetails response = new CustomerDetails();
		response.setCustomer(null);
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
		} else {
			if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
				String[] valueParm = new String[2];
				valueParm[0] = "director details";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_CORP + "," + PennantConstants.PFF_CUSTCTG_SME;
				response.setReturnStatus(getFailedStatus("90124", valueParm));
				return response;
			}
			response = customerController.getCustomerDirectorDetails(custCIF, customer.getCustID());
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus updateCustomerDirectorDetail(CustomerDirectorDetail customerDirectorDetail)
			throws ServiceException {
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerDirectorDetail, UpdateValidationGroup.class);
		if (customerDirectorDetail.getDirectorDetail() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "DirectorDetail";
			return getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerDirectorDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerDirectorDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerDirectorDetail.getCif();
				return getFailedStatus("90101", valueParm);
			}
			if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
				String[] valueParm = new String[2];
				valueParm[0] = "director details";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_CORP + "," + PennantConstants.PFF_CUSTCTG_SME;
				return getFailedStatus("90124", valueParm);
			}
		}

		// for logging purpose
		logReference(customerDirectorDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerDirectorDetail.getDirectorDetail(), PennantConstants.TRAN_WF);

		// validate customer details as per the API specification AuditDetail
		AuditDetail auditDetail = directorDetailService.doValidations(customerDirectorDetail.getDirectorDetail(),
				customer);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		DirectorDetail directorDetailByDirectorId = directorDetailService.getApprovedDirectorDetailByDirectorId(
				customerDirectorDetail.getDirectorDetail().getDirectorId(), customer.getCustID());
		if (directorDetailByDirectorId != null) {
			if (directorDetailByDirectorId.getCustID() == (customer.getCustID())) {
				// call update customer if there is no errors
				response = customerController.updateCustomerDirectorDetail(customerDirectorDetail.getDirectorDetail(),
						customerDirectorDetail.getCif());
			} else {
				response = new WSReturnStatus();
				String[] valueParm = new String[2];
				valueParm[0] = "DirectorId  "
						+ String.valueOf(customerDirectorDetail.getDirectorDetail().getDirectorId());
				return getFailedStatus("90266", valueParm);
			}

		} else {
			response = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = "DirectorId  " + String.valueOf(customerDirectorDetail.getDirectorDetail().getDirectorId());
			return getFailedStatus("90266", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus deleteCustomerDirectorDetail(CustomerDirectorDetail cdd) throws ServiceException {
		DirectorDetail dd = new DirectorDetail();

		Customer customer = null;

		validationUtility.validate(cdd, DeleteValidationGroup.class);

		// customer validations
		if (StringUtils.isBlank(cdd.getCif())) {
			String[] valueParm = new String[1];
			valueParm[0] = "cif";
			return getFailedStatus("90502", valueParm);
		}
		if (cdd.getDirectorId() <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "directorId";
			return getFailedStatus("90502", valueParm);
		}
		if (StringUtils.isNotBlank(cdd.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(cdd.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = cdd.getCif();
				return getFailedStatus("90101", valueParm);
			} else {

				if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
					String[] valueParm = new String[2];
					valueParm[0] = "director details";
					valueParm[1] = PennantConstants.PFF_CUSTCTG_CORP + "," + PennantConstants.PFF_CUSTCTG_SME;
					return getFailedStatus("90124", valueParm);

				}

				dd.setCustID(customer.getCustID());
				dd.setDirectorId((cdd.getDirectorId()));
				// for logging purpose
				logReference(cdd.getCif());
			}
		}
		WSReturnStatus response = new WSReturnStatus();
		// validate Customer with given DirectorId
		DirectorDetail directorDetailById = directorDetailService
				.getApprovedDirectorDetailByDirectorId(cdd.getDirectorId(), customer.getCustID());

		if (directorDetailById != null) {
			// call delete customer service
			if (dd.getCustID() == (customer.getCustID())) {
				response = customerController.deleteCustomerDirectorDetail(directorDetailById);
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = "DirectorId " + String.valueOf(cdd.getDirectorDetail().getDirectorId());
				return getFailedStatus("90266", valueParm);
			}
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = "DirectorId " + cdd.getDirectorId();
			return getFailedStatus("90266", valueParm);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(custPhoneNumber, SaveValidationGroup.class);
		if (custPhoneNumber.getCustomerPhoneNumber() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "phone";
			return getFailedStatus("90502", valueParm);
		}
		Customer customer = null;
		if (StringUtils.isNotBlank(custPhoneNumber.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(custPhoneNumber.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custPhoneNumber.getCif();
				return getFailedStatus("90101", valueParm);

			}
		}
		// for logging purpose
		logReference(custPhoneNumber.getCif());
		custPhoneNumber.getCustomerPhoneNumber().setPhoneCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custPhoneNumber.getCustomerPhoneNumber(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerPhoneNumberService.doValidations(custPhoneNumber.getCustomerPhoneNumber(),
				APIConstants.SERVICE_TYPE_CREATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());

			}
		}

		// call add Customer Employment method in case of no errors
		WSReturnStatus returnStatus = customerDetailsController
				.addCustomerPhoneNumber(custPhoneNumber.getCustomerPhoneNumber(), custPhoneNumber.getCif());

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerPhoneNumber, UpdateValidationGroup.class);
		if (customerPhoneNumber.getCustomerPhoneNumber() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "phone";
			return getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerPhoneNumber.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerPhoneNumber.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerPhoneNumber.getCif();
				return getFailedStatus("90101", valueParm);
			}
		}
		// for logging purpose
		logReference(customerPhoneNumber.getCif());
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
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
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
			String[] valueParm = new String[2];
			valueParm[0] = customerPhoneNumber.getCustomerPhoneNumber().getPhoneTypeCode();
			valueParm[1] = customerPhoneNumber.getCif();
			return getFailedStatus("90106", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	/**
	 * get getCustomerPhoneNumbers by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerPhoneNumbers(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
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
		logger.debug(Literal.LEAVING);

		return response;
	}

	/**
	 * delete CustomerPhoneNumber.
	 * 
	 * @param custPhoneNumber
	 */
	@Override
	public WSReturnStatus deleteCustomerPhoneNumber(CustPhoneNumber custPhoneNumber) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(custPhoneNumber, DeleteValidationGroup.class);

		// customer validations
		CustomerPhoneNumber customerPhoneNumber = null;
		if (StringUtils.isNotBlank(custPhoneNumber.getCif())) {
			Customer customer = customerDetailsService.getCustomerByCIF(custPhoneNumber.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custPhoneNumber.getCif();
				return getFailedStatus("90101", valueParm);
			} else {
				customerPhoneNumber = new CustomerPhoneNumber();
				customerPhoneNumber.setPhoneCustID(customer.getCustID());
				customerPhoneNumber.setPhoneTypeCode(custPhoneNumber.getPhoneTypeCode());
				// for logging purpose
				logReference(custPhoneNumber.getCif());
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
				return getFailedStatus("90270", valueParm);
			}
			// call delete customer service
			response = customerDetailsController.deleteCustomerPhoneNumber(customerPhoneNumber);
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = custPhoneNumber.getCif();
			valueParm[1] = custPhoneNumber.getPhoneTypeCode();
			return getFailedStatus("90106", valueParm);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(custAddress, SaveValidationGroup.class);
		if (custAddress.getCustomerAddres() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "address";
			return getFailedStatus("90502", valueParm);
		}
		Customer customer = null;
		if (StringUtils.isNotBlank(custAddress.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(custAddress.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custAddress.getCif();
				return getFailedStatus("90101", valueParm);

			}
		}
		// for logging purpose
		logReference(custAddress.getCif());
		custAddress.getCustomerAddres().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custAddress.getCustomerAddres(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerAddresService.doValidations(custAddress.getCustomerAddres(),
				APIConstants.SERVICE_TYPE_CREATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());

			}
		}

		// call add Customer Employment method in case of no errors
		WSReturnStatus returnStatus = customerDetailsController.addCustomerAddress(custAddress.getCustomerAddres(),
				custAddress.getCif());

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(custAddress, UpdateValidationGroup.class);
		if (custAddress.getCustomerAddres() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "address";
			return getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(custAddress.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(custAddress.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custAddress.getCif();
				return getFailedStatus("90101", valueParm);
			}
		}
		// for logging purpose
		logReference(custAddress.getCif());
		custAddress.getCustomerAddres().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custAddress.getCustomerAddres(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerAddresService.doValidations(custAddress.getCustomerAddres(),
				APIConstants.SERVICE_TYPE_UPDATE);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
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
			String[] valueParm = new String[2];
			valueParm[0] = custAddress.getCustomerAddres().getCustAddrType();
			valueParm[1] = custAddress.getCif();
			return getFailedStatus("90109", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	/**
	 * get CustomerAddresses by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerAddresses(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
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

		logger.debug(Literal.LEAVING);

		return response;
	}

	/**
	 * delete CustAddress.
	 * 
	 * @param custAddress
	 */
	@Override
	public WSReturnStatus deleteCustomerAddress(CustAddress custAddress) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(custAddress, DeleteValidationGroup.class);

		// customer validations
		CustomerAddres customerAddres = null;
		if (StringUtils.isNotBlank(custAddress.getCif())) {
			Customer customer = customerDetailsService.getCustomerByCIF(custAddress.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custAddress.getCif();
				return getFailedStatus("90101", valueParm);
			} else {
				customerAddres = new CustomerAddres();
				customerAddres.setCustID(customer.getCustID());
				customerAddres.setCustAddrType(custAddress.getAddrType());
				// for logging purpose
				logReference(custAddress.getCif());
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
				return getFailedStatus("90270", valueParm);
			}
			// call delete customer service
			response = customerDetailsController.deleteCustomerAddress(customerAddres);
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = custAddress.getCif();
			valueParm[1] = custAddress.getAddrType();
			return getFailedStatus("90109", valueParm);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(custEMail, SaveValidationGroup.class);
		if (custEMail.getCustomerEMail() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "email";
			return getFailedStatus("90502", valueParm);
		}
		Customer customer = null;
		if (StringUtils.isNotBlank(custEMail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(custEMail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custEMail.getCif();
				return getFailedStatus("90101", valueParm);

			}
		}
		// for logging purpose
		logReference(custEMail.getCif());
		custEMail.getCustomerEMail().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custEMail.getCustomerEMail(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerEMailService.doValidations(custEMail.getCustomerEMail(),
				APIConstants.SERVICE_TYPE_CREATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}
		// call add Customer Employment method in case of no errors
		WSReturnStatus returnStatus = customerDetailsController.addCustomerEmail(custEMail.getCustomerEMail(),
				custEMail.getCif());

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(custEMail, UpdateValidationGroup.class);
		if (custEMail.getCustomerEMail() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "email";
			return getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(custEMail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(custEMail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custEMail.getCif();
				return getFailedStatus("90101", valueParm);
			}
		}
		// for logging purpose
		logReference(custEMail.getCif());
		custEMail.getCustomerEMail().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custEMail.getCustomerEMail(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerEMailService.doValidations(custEMail.getCustomerEMail(),
				APIConstants.SERVICE_TYPE_UPDATE);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
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
			String[] valueParm = new String[2];
			valueParm[0] = custEMail.getCustomerEMail().getCustEMailTypeCode();
			valueParm[1] = custEMail.getCif();
			return getFailedStatus("90111", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	/**
	 * get CustomerEmails by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerEmails(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
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

		logger.debug(Literal.LEAVING);

		return response;
	}

	/**
	 * delete CustomerEmail.
	 * 
	 * @param custEMail
	 */
	@Override
	public WSReturnStatus deleteCustomerEmail(CustEMail custEMail) throws ServiceException {

		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(custEMail, DeleteValidationGroup.class);

		// customer validations
		CustomerEMail customerEMaial = null;
		if (StringUtils.isNotBlank(custEMail.getCif())) {
			Customer customer = customerDetailsService.getCustomerByCIF(custEMail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custEMail.getCif();
				return getFailedStatus("90101", valueParm);
			} else {
				customerEMaial = new CustomerEMail();
				customerEMaial.setCustID(customer.getCustID());
				customerEMaial.setCustEMailTypeCode(custEMail.getCustEMailTypeCode());
				// for logging purpose
				logReference(custEMail.getCif());
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
			String[] valueParm = new String[2];
			valueParm[0] = custEMail.getCif();
			valueParm[1] = custEMail.getCustEMailTypeCode();
			return getFailedStatus("90111", valueParm);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(customerIncomeDetail, SaveValidationGroup.class);
		if (customerIncomeDetail.getCustomerIncome() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerIncome";
			return getFailedStatus("90502", valueParm);
		}

		if (!ImplementationConstants.ALLOW_CUSTOMER_INCOMES) {
			String[] valueParm = new String[2];
			valueParm[0] = "Customerincome";
			valueParm[1] = customerIncomeDetail.getCif();
			return getFailedStatus("90599", valueParm);
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(customerIncomeDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerIncomeDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerIncomeDetail.getCif();
				return getFailedStatus("90101", valueParm);

			}
		}
		// for logging purpose
		logReference(customerIncomeDetail.getCif());

		boolean corpFinReq = SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_CORP_FINANCE_TAB_REQ);

		if (!corpFinReq && PennantConstants.PFF_CUSTCTG_CORP.equals(customer.getCustCtgCode())) {
			String[] valueParm = new String[2];
			valueParm[0] = "Customerincome";
			valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
			return getFailedStatus("90124", valueParm);
		}

		AuditHeader auditHeader = getAuditHeader(customerIncomeDetail.getCustomerIncome(), PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerIncomeService.doValidations(customerIncomeDetail.getCustomerIncome());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}
		WSReturnStatus returnStatus = customerDetailsController
				.addCustomerIncome(customerIncomeDetail.getCustomerIncome(), customerIncomeDetail.getCif());

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(customerIncomeDetail, UpdateValidationGroup.class);
		if (customerIncomeDetail.getCustomerIncome() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerIncome";
			return getFailedStatus("90502", valueParm);
		}

		if (!ImplementationConstants.ALLOW_CUSTOMER_INCOMES) {
			String[] valueParm = new String[2];
			valueParm[0] = "Customerincome";
			valueParm[1] = customerIncomeDetail.getCif();
			return getFailedStatus("90599", valueParm);
		}

		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerIncomeDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerIncomeDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerIncomeDetail.getCif();
				return getFailedStatus("90101", valueParm);
			} else {
				customerIncomeDetail.getCustomerIncome().setCustId(customer.getCustID());
			}
		}
		// for logging purpose
		logReference(customerIncomeDetail.getCif());

		boolean corpFinReq = SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_CORP_FINANCE_TAB_REQ);
		if (!corpFinReq && PennantConstants.PFF_CUSTCTG_CORP.equals(customer.getCustCtgCode())) {
			String[] valueParm = new String[2];
			valueParm[0] = "Customerincome";
			valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
			return getFailedStatus("90124", valueParm);
		}

		AuditHeader auditHeader = getAuditHeader(customerIncomeDetail.getCustomerIncome(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerIncomeService.doValidations(customerIncomeDetail.getCustomerIncome());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}
		// validate Customer with given CustCIF
		CustomerIncome curCustomerIncome = customerIncomeDetail.getCustomerIncome();
		curCustomerIncome.setCustId(customer.getCustID());
		CustomerIncome customerIncome = customerIncomeService.getCustomerIncomeById(curCustomerIncome);
		WSReturnStatus returnStatus = null;
		if (customerIncome != null) {
			customerIncomeDetail.getCustomerIncome().setId(customerIncome.getId());
			// call update customer if there is no errors
			returnStatus = customerDetailsController.updateCustomerIncome(customerIncomeDetail.getCustomerIncome(),
					customerIncomeDetail.getCif());
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = customerIncomeDetail.getCif();
			return getFailedStatus("90112", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	/**
	 * get CustomerIncomes by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerIncomes(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
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
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * delete CustomerIncome.
	 * 
	 * @param customerIncomeDetail
	 */
	@Override
	public WSReturnStatus deleteCustomerIncome(CustomerIncomeDetail customerIncomeDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(customerIncomeDetail, DeleteValidationGroup.class);

		// customer validations
		CustomerIncome customerIncome = null;
		if (StringUtils.isNotBlank(customerIncomeDetail.getCif())) {
			Customer customer = customerDetailsService.getCustomerByCIF(customerIncomeDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerIncomeDetail.getCif();
				return getFailedStatus("90101", valueParm);
			} else {
				customerIncome = new CustomerIncome();
				customerIncome.setCustId(customer.getCustID());
				customerIncome.setIncomeType(customerIncomeDetail.getCustIncomeType());
				customerIncome.setCategory(customerIncomeDetail.getCategory());
				customerIncome.setIncomeExpense(customerIncomeDetail.getIncomeExpense());
				customerIncome.setLinkId(customerIncomeDAO.getLinkId(customer.getCustID()));

				// for logging purpose
				logReference(customerIncomeDetail.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerIncome prvCustomerIncome = customerIncomeService.getApprovedCustomerIncomeById(customerIncome);
		if (prvCustomerIncome != null) {
			// call delete customer service
			response = customerDetailsController.deleteCustomerIncome(customerIncome);
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = customerIncomeDetail.getCif();
			return getFailedStatus("90112", valueParm);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(customerBankInfoDetail, SaveValidationGroup.class);
		if (customerBankInfoDetail.getCustomerBankInfo() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerBankInfo";
			CustomerBankInfoDetail aCustomerBankInfoDetail = new CustomerBankInfoDetail();
			aCustomerBankInfoDetail.setReturnStatus(getFailedStatus("90502", valueParm));
			return aCustomerBankInfoDetail;
		}
		if (StringUtils.isNotBlank(customerBankInfoDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerBankInfoDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerBankInfoDetail.getCif();
				CustomerBankInfoDetail custBankInfoDetail = new CustomerBankInfoDetail();
				custBankInfoDetail.setReturnStatus(getFailedStatus("90101", valueParm));
				return custBankInfoDetail;
			}
		}
		// for logging purpose
		logReference(customerBankInfoDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerBankInfoDetail.getCustomerBankInfo(),
				PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerBankInfoService.doValidations(customerBankInfoDetail.getCustomerBankInfo(),
				PennantConstants.RECORD_TYPE_NEW, new AuditDetail());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		CustomerBankInfoDetail response = null;
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerBankInfoDetail();
				response.setReturnStatus(getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
		}

		// call add Customer Employment method in case of no errors
		response = customerDetailsController.addCustomerBankingInformation(customerBankInfoDetail.getCustomerBankInfo(),
				customerBankInfoDetail.getCif());

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerBankInfoDetail, UpdateValidationGroup.class);
		if (customerBankInfoDetail.getCustomerBankInfo() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerBankInfo";
			return getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerBankInfoDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerBankInfoDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerBankInfoDetail.getCif();
				return getFailedStatus("90101", valueParm);
			}
		}
		// for logging purpose
		logReference(customerBankInfoDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerBankInfoDetail.getCustomerBankInfo(),
				PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		AuditDetail auditDetail = customerBankInfoService.doValidations(customerBankInfoDetail.getCustomerBankInfo(),
				PennantConstants.RECORD_TYPE_UPD, new AuditDetail());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
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
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerBankInfoDetail.getCustomerBankInfo().getBankId());
			valueParm[1] = customerBankInfoDetail.getCif();
			return getFailedStatus("90116", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return response;

	}

	/**
	 * get CustomerBankingInformation by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerBankingInformation(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
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
		logger.debug(Literal.LEAVING);

		return response;
	}

	/**
	 * delete CustomerBankingInformation.
	 * 
	 * @param cbid
	 */
	@Override
	public WSReturnStatus deleteCustomerBankingInformation(CustomerBankInfoDetail cbid) throws ServiceException {

		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(cbid, DeleteValidationGroup.class);

		// customer validations
		CustomerBankInfo customerBankInfo = null;
		if (StringUtils.isNotBlank(cbid.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(cbid.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = cbid.getCif();
				return getFailedStatus("90101", valueParm);
			} else {
				customerBankInfo = new CustomerBankInfo();
				customerBankInfo.setCustID(customerDetails.getCustID());
				customerBankInfo.setBankId(cbid.getBankId());
				// for logging purpose
				logReference(cbid.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerBankInfo custBankInfo = customerBankInfoService.getCustomerBankInfoById(cbid.getBankId());
		if (custBankInfo != null) {
			response = customerDetailsController.deleteCustomerBankingInformation(custBankInfo);
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(cbid.getBankId());
			valueParm[1] = cbid.getCif();
			return getFailedStatus("90116", valueParm);
		}

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerGstInfoDetail, SaveValidationGroup.class);
		if (customerGstInfoDetail.getCustomerGST() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerGstInfo";
			CustomerGstInfoDetail aCustomerGstInfoDetail = new CustomerGstInfoDetail();
			aCustomerGstInfoDetail.setReturnStatus(getFailedStatus("90502", valueParm));
			return aCustomerGstInfoDetail;
		}
		if (StringUtils.isNotBlank(customerGstInfoDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerGstInfoDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerGstInfoDetail.getCif();
				CustomerGstInfoDetail customerGstInfoDetails = new CustomerGstInfoDetail();
				customerGstInfoDetails.setReturnStatus(getFailedStatus("90101", valueParm));
				return customerGstInfoDetail;
			}
		}
		// for logging purpose
		logReference(customerGstInfoDetail.getCif());
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
				response.setReturnStatus(getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
		}

		// call add Customer gst method in case of no errors
		response = customerDetailsController.addCustomerGstInformation(customerGstInfoDetail.getCustomerGST(),
				customerGstInfoDetail.getCif());

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerGstInfoDetail, UpdateValidationGroup.class);
		if (customerGstInfoDetail.getCustomerGST() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerBankInfo";
			return getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerGstInfoDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerGstInfoDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerGstInfoDetail.getCif();
				return getFailedStatus("90101", valueParm);
			}
		}
		// for logging purpose
		logReference(customerGstInfoDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerGstInfoDetail.getCustomerGST(), PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		AuditDetail auditDetail = customerGstService.doValidations(customerGstInfoDetail.getCustomerGST(),
				PennantConstants.RECORD_TYPE_UPD);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
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
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerGstInfoDetail.getCustomerGST().getCustId());
			valueParm[1] = customerGstInfoDetail.getCif();
			return getFailedStatus("90116", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return response;

	}

	/**
	 * get CustomerGstInfoDetail by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerGstnformation(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
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
		logger.debug(Literal.LEAVING);

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

		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerGstInfoDetail, DeleteValidationGroup.class);

		// customer validations
		CustomerGST customerGST = null;
		if (StringUtils.isNotBlank(customerGstInfoDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerGstInfoDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerGstInfoDetail.getCif();
				return getFailedStatus("90101", valueParm);
			} else {
				customerGST = new CustomerGST();
				customerGST.setCustId(customerDetails.getCustID());
				customerGST.setId(customerGstInfoDetail.getId());
				// for logging purpose
				logReference(customerGstInfoDetail.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustomerGST customeGST = customerGstService.getCustomerGstDeatailsByCustomerId(customerGstInfoDetail.getId());

		if (customeGST != null) {
			// call delete customer service
			response = customerDetailsController.deleteCustomerGSTInformation(customeGST);
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerGstInfoDetail.getId());
			valueParm[1] = customerGstInfoDetail.getCif();
			return getFailedStatus("90116", valueParm);
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * Method for create CustomerGstInfoDetail in PLF system.
	 * 
	 * @param CustomerGstInfoDetail
	 * @throws ServiceException
	 */
	@Override
	public CustomerCardSaleInfoDetails addCardSalesInformation(CustomerCardSaleInfoDetails customerCardSaleInfoDetails)
			throws ServiceException {
		logger.debug(Literal.ENTERING);
		validationUtility.validate(customerCardSaleInfoDetails, SaveValidationGroup.class);
		if (customerCardSaleInfoDetails.getCustCardSales() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customer CardSalesInfo";
			CustomerCardSaleInfoDetails acustCardSalesInfoDetail = new CustomerCardSaleInfoDetails();
			acustCardSalesInfoDetail.setReturnStatus(getFailedStatus("90502", valueParm));
			return acustCardSalesInfoDetail;
		}
		if (StringUtils.isNotBlank(customerCardSaleInfoDetails.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerCardSaleInfoDetails.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerCardSaleInfoDetails.getCif();
				CustomerGstInfoDetail customerGstInfoDetails = new CustomerGstInfoDetail();
				customerGstInfoDetails.setReturnStatus(getFailedStatus("90101", valueParm));
				return customerCardSaleInfoDetails;
			}
		}
		// for logging purpose
		logReference(customerCardSaleInfoDetails.getCif());
		AuditHeader auditHeader = getAuditHeader(customerCardSaleInfoDetails.getCustCardSales(),
				PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerCardSalesInfoService
				.doValidations(customerCardSaleInfoDetails.getCustCardSales(), PennantConstants.RECORD_TYPE_NEW);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		CustomerCardSaleInfoDetails response = null;
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new CustomerCardSaleInfoDetails();
				response.setReturnStatus(getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
		}
		// call add Customer gst method in case of no errors
		response = customerDetailsController.addCardSalesInformation(customerCardSaleInfoDetails.getCustCardSales(),
				customerCardSaleInfoDetails.getCif());

		logger.debug(Literal.LEAVING);
		return response;

	}

	/**
	 * get CustomerGstInfoDetail by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCardSalesInformation(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
		CustomerDetails response = new CustomerDetails();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
			response.setCustomer(null);
		} else {
			response = customerDetailsController.getCardSalesInformation(custCIF);
		}
		logger.debug(Literal.LEAVING);

		return response;
	}

	/**
	 * Method for update CustomerGstInfoDetail in PLF system.
	 * 
	 * @param CustomerGstInfoDetail
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateCardSaleInformation(CustomerCardSaleInfoDetails customerCardSaleInfoDetails)
			throws ServiceException {
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerCardSaleInfoDetails, UpdateValidationGroup.class);
		if (customerCardSaleInfoDetails.getCustCardSales() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerCardSalesInfo";
			return getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerCardSaleInfoDetails.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerCardSaleInfoDetails.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerCardSaleInfoDetails.getCif();
				return getFailedStatus("90101", valueParm);
			}
		}
		// for logging purpose
		logReference(customerCardSaleInfoDetails.getCif());
		AuditHeader auditHeader = getAuditHeader(customerCardSaleInfoDetails.getCustCardSales(),
				PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		AuditDetail auditDetail = customerCardSalesInfoService
				.doValidations(customerCardSaleInfoDetails.getCustCardSales(), PennantConstants.RECORD_TYPE_UPD);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		WSReturnStatus response = null;
		// validate Customer with given CustCIF
		CustCardSales custCardSalesInfo = customerCardSalesInfoService
				.getCustomerCardSalesInfoById(customerCardSaleInfoDetails.getCustCardSales().getId());

		if (custCardSalesInfo != null) {
			// call update customer if there is no errors
			response = customerDetailsController.updateCardSalestInformation(
					customerCardSaleInfoDetails.getCustCardSales(), customerCardSaleInfoDetails.getCif());
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerCardSaleInfoDetails.getCustCardSales().getCustID());
			valueParm[1] = customerCardSaleInfoDetails.getCif();
			return getFailedStatus("90116", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return response;

	}

	/**
	 * delete CustomerGstInfoDetail.
	 * 
	 * @param CustomerGstInfoDetail
	 */
	@Override
	public WSReturnStatus deleteCardSaleInformation(CustomerCardSaleInfoDetails customerCardSaleInfoDetails)
			throws ServiceException {

		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerCardSaleInfoDetails, DeleteValidationGroup.class);

		// customer validations
		CustCardSales custCardSales = null;
		if (StringUtils.isNotBlank(customerCardSaleInfoDetails.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerCardSaleInfoDetails.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerCardSaleInfoDetails.getCif();
				return getFailedStatus("90101", valueParm);
			} else {
				custCardSales = new CustCardSales();
				custCardSales.setCustID(customerDetails.getCustID());
				custCardSales.setId(customerCardSaleInfoDetails.getId());
				// for logging purpose
				logReference(customerCardSaleInfoDetails.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF

		CustCardSales custCardSalesInfo = customerCardSalesInfoService
				.getCustomerCardSalesInfoById(customerCardSaleInfoDetails.getId());

		if (custCardSalesInfo != null) {
			// call delete customer service
			response = customerDetailsController.deleteCardSaleInformation(custCardSalesInfo);
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerCardSaleInfoDetails.getId());
			valueParm[1] = customerCardSaleInfoDetails.getCif();
			return getFailedStatus("90116", valueParm);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(customerChequeInfoDetail, SaveValidationGroup.class);

		if (customerChequeInfoDetail.getCustomerChequeInfo() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "accountBehaviour";
			CustomerChequeInfoDetail custChequeInfoDetail = new CustomerChequeInfoDetail();
			custChequeInfoDetail.setReturnStatus(getFailedStatus("90502", valueParm));
			return custChequeInfoDetail;
		}
		if (StringUtils.isNotBlank(customerChequeInfoDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerChequeInfoDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerChequeInfoDetail.getCif();
				CustomerChequeInfoDetail custChequeInfoDetail = new CustomerChequeInfoDetail();
				custChequeInfoDetail.setReturnStatus(getFailedStatus("90101", valueParm));
				return custChequeInfoDetail;
			}
		}
		// for logging purpose
		logReference(customerChequeInfoDetail.getCif());
		// call add Customer Employment method in case of no errors
		CustomerChequeInfoDetail response = customerDetailsController.addCustomerAccountBehaviour(
				customerChequeInfoDetail.getCustomerChequeInfo(), customerChequeInfoDetail.getCif());
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerChequeInfoDetail, UpdateValidationGroup.class);
		if (customerChequeInfoDetail.getCustomerChequeInfo() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "accountBehaviour";
			return getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerChequeInfoDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerChequeInfoDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerChequeInfoDetail.getCif();
				return getFailedStatus("90101", valueParm);
			}
		}
		// for logging purpose
		logReference(customerChequeInfoDetail.getCif());
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
			return getFailedStatus("90117", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * get CustomerAccountBehaviour by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerAccountBehaviour(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);
		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
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
		logger.debug(Literal.LEAVING);

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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerChequeInfoDetail, DeleteValidationGroup.class);

		// customer validations
		CustomerChequeInfo customerChequeInfo = null;
		if (StringUtils.isNotBlank(customerChequeInfoDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerChequeInfoDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerChequeInfoDetail.getCif();
				return getFailedStatus("90101", valueParm);
			} else {
				customerChequeInfo = new CustomerChequeInfo();
				customerChequeInfo.setCustID(customerDetails.getCustID());
				customerChequeInfo.setChequeSeq(customerChequeInfoDetail.getChequeSeq());
				// for logging purpose
				logReference(customerChequeInfoDetail.getCif());
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
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerChequeInfoDetail.getChequeSeq());
			valueParm[1] = customerChequeInfoDetail.getCif();
			return getFailedStatus("90117", valueParm);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(customerLiability, SaveValidationGroup.class);
		if (customerLiability.getExternalLiability() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerExtLiability";
			CustomerExtLiabilityDetail aCustomerExtLiabilityDetail = new CustomerExtLiabilityDetail();
			aCustomerExtLiabilityDetail.setReturnStatus(getFailedStatus("90502", valueParm));
			return aCustomerExtLiabilityDetail;
		}
		if (StringUtils.isNotBlank(customerLiability.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerLiability.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerLiability.getCif();
				CustomerExtLiabilityDetail custExtLiabilityDetail = new CustomerExtLiabilityDetail();
				custExtLiabilityDetail.setReturnStatus(getFailedStatus("90101", valueParm));
				return custExtLiabilityDetail;
			}
		}
		// for logging purpose
		logReference(customerLiability.getCif());
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
				response.setReturnStatus(getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				return response;
			}
		}

		// call add Customer Employment method in case of no errors
		response = customerDetailsController.addCustomerExternalLiability(customerLiability.getExternalLiability(),
				customerLiability.getCif());

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerExtLiabilityDetail, UpdateValidationGroup.class);
		if (customerExtLiabilityDetail.getExternalLiability() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "customerExtLiability";
			return getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerExtLiabilityDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerExtLiabilityDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerExtLiabilityDetail.getCif();
				return getFailedStatus("90101", valueParm);
			}
		}
		// for logging purpose
		logReference(customerExtLiabilityDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerExtLiabilityDetail.getExternalLiability(),
				PennantConstants.TRAN_WF);

		// validate customer details as per the API specification
		CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		AuditDetail auditDetail = validation.doValidations(customerExtLiabilityDetail.getExternalLiability());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		WSReturnStatus response = null;
		// validate Customer with given CustCIF

		CustomerExtLiability liability = new CustomerExtLiability();
		liability.setCustId(customer.getCustID());
		liability.setSeqNo(customerExtLiabilityDetail.getExternalLiability().getSeqNo());
		liability.setLinkId(customerExtLiabilityDAO.getLinkId(customer.getCustID()));
		liability = customerExtLiabilityService.getLiability(liability);

		if (liability != null) {
			customerExtLiabilityDetail.getExternalLiability().setId(liability.getId());

			// call update customer if there is no errors
			response = customerDetailsController.updateCustomerExternalLiability(
					customerExtLiabilityDetail.getExternalLiability(), customerExtLiabilityDetail.getCif());
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerExtLiabilityDetail.getExternalLiability().getSeqNo());
			valueParm[1] = customerExtLiabilityDetail.getCif();
			return getFailedStatus("90118", valueParm);
		}

		logger.debug(Literal.LEAVING);
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
		logReference(custCIF);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerExtLiabilityDetail, DeleteValidationGroup.class);

		// customer validations
		CustomerExtLiability customerExtLiability = null;
		if (StringUtils.isNotBlank(customerExtLiabilityDetail.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(customerExtLiabilityDetail.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerExtLiabilityDetail.getCif();
				return getFailedStatus("90101", valueParm);
			} else {
				customerExtLiability = new CustomerExtLiability();
				customerExtLiability.setCustId(customerDetails.getCustID());
				customerExtLiability.setSeqNo(customerExtLiabilityDetail.getLiabilitySeq());
				// for logging purpose
				logReference(customerExtLiabilityDetail.getCif());
			}
		}
		WSReturnStatus response = null;
		// validate Customer with given CustCIF

		CustomerExtLiability liability = new CustomerExtLiability();
		liability.setCustId(customerExtLiability.getCustId());
		liability.setSeqNo(customerExtLiabilityDetail.getLiabilitySeq());
		liability.setLinkId(customerExtLiabilityDAO.getLinkId(customerExtLiability.getCustId()));

		// liability = customerExtLiabilityService.getLiability(liability);

		CustomerExtLiability custExtLiability = customerExtLiabilityService.getLiability(liability);

		if (custExtLiability != null) {
			// call delete customer service
			response = customerDetailsController.deleteCustomerExternalLiability(liability);
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerExtLiabilityDetail.getLiabilitySeq());
			valueParm[1] = customerExtLiabilityDetail.getCif();
			return getFailedStatus("90118", valueParm);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerDocumentDetail, SaveValidationGroup.class);
		if (customerDocumentDetail.getCustomerDocument() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "document";
			return getFailedStatus("90502", valueParm);
		}
		Customer customer = null;
		if (StringUtils.isNotBlank(customerDocumentDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerDocumentDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerDocumentDetail.getCif();
				return getFailedStatus("90101", valueParm);

			}
		}
		// for logging purpose
		logReference(customerDocumentDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerDocumentDetail.getCustomerDocument(),
				PennantConstants.TRAN_WF);
		// validate customer details as per the API specification
		AuditDetail auditDetail = customerDocumentService
				.validateCustomerDocuments(customerDocumentDetail.getCustomerDocument(), customer);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}
		// call add Customer Employment method in case of no errors
		WSReturnStatus returnStatus = customerDetailsController
				.addCustomerDocument(customerDocumentDetail.getCustomerDocument(), customerDocumentDetail.getCif());

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		// bean validations
		validationUtility.validate(customerDocumentDetail, UpdateValidationGroup.class);
		if (customerDocumentDetail.getCustomerDocument() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "document";
			return getFailedStatus("90502", valueParm);
		}
		// customer validations
		Customer customer = null;
		if (StringUtils.isNotBlank(customerDocumentDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(customerDocumentDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerDocumentDetail.getCif();
				return getFailedStatus("90101", valueParm);
			}
		}
		// for logging purpose
		logReference(customerDocumentDetail.getCif());
		AuditHeader auditHeader = getAuditHeader(customerDocumentDetail.getCustomerDocument(),
				PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerDocumentService
				.validateCustomerDocuments(customerDocumentDetail.getCustomerDocument(), customer);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		// validate Customer with given CustCIF
		CustomerDocument customerDocument = customerDocumentService.getApprovedCustomerDocumentById(
				customer.getCustID(), customerDocumentDetail.getCustomerDocument().getCustDocCategory());
		WSReturnStatus returnStatus = null;
		if (customerDocument != null) {
			// call update customer if there is no errors
			customerDocumentDetail.getCustomerDocument().setID(customerDocument.getID());
			returnStatus = customerDetailsController.updateCustomerDocument(
					customerDocumentDetail.getCustomerDocument(), customerDocumentDetail.getCif());
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = customerDocumentDetail.getCustomerDocument().getCustDocCategory();
			valueParm[1] = customerDocumentDetail.getCif();
			return getFailedStatus("90119", valueParm);
		}
		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	/**
	 * get CustomerDocuments by the given customer cif.
	 * 
	 * @param custCIF
	 */
	@Override
	public CustomerDetails getCustomerDocuments(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// Mandatory validation
		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}
		// for logging purpose
		logReference(custCIF);
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

		logger.debug(Literal.LEAVING);

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
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(customerDocumentDetail, DeleteValidationGroup.class);

		// customer validations
		CustomerDocument customerDocument = null;
		if (StringUtils.isNotBlank(customerDocumentDetail.getCif())) {
			Customer customer = customerDetailsService.getCustomerByCIF(customerDocumentDetail.getCif());
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerDocumentDetail.getCif();
				return getFailedStatus("90101", valueParm);
			} else {
				customerDocument = new CustomerDocument();
				customerDocument.setCustID(customer.getCustID());
				customerDocument.setCustDocCategory(customerDocumentDetail.getCustDocCategory());
				// for logging purpose
				logReference(customerDocumentDetail.getCif());
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
			String[] valueParm = new String[2];
			valueParm[0] = customerDocumentDetail.getCustDocCategory();
			valueParm[1] = customerDocumentDetail.getCif();
			return getFailedStatus("90119", valueParm);
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	private CustomerDedup doSetCustomerDedup(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
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

		if (ImplementationConstants.CUSTOMER_PAN_VALIDATION_STOP) {
			setUCIC(customerDetails, customerDedup);
		}

		logger.debug(Literal.LEAVING);
		return customerDedup;

	}

	@Override
	public AgreementData getCustomerAgreement(AgreementRequest agrRequest) throws ServiceException {

		logger.debug(Literal.ENTERING);
		AgreementData agrData = null;
		try {
			// Mandatory validation
			if (StringUtils.isBlank(agrRequest.getCif())) {
				agrData = new AgreementData();
				String[] valueParm = new String[1];
				valueParm[0] = "CIF";
				agrData.setReturnStatus(getFailedStatus("90502", valueParm));
				return agrData;
			}

			if (StringUtils.isBlank(agrRequest.getAgreementType())) {
				agrData = new AgreementData();
				String[] valueParm = new String[1];
				valueParm[0] = "AgreementType";
				agrData.setReturnStatus(getFailedStatus("90502", valueParm));
				return agrData;
			}
			// for logging purpose
			logReference(agrRequest.getCif());
			if (!StringUtils.equals(agrRequest.getAgreementType(), APIConstants.CUST_AGR_NAME)) {
				agrData = new AgreementData();
				String[] valueParm = new String[2];
				valueParm[0] = APIConstants.CUST_AGR_NAME;
				valueParm[1] = "AgreementType";
				agrData.setReturnStatus(getFailedStatus("90298", valueParm));
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
				agrData.setReturnStatus(getFailedStatus("90101", valueParm));
			}
		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			agrData = new AgreementData();
			agrData.setReturnStatus(getFailedStatus());
		}
		logger.debug(Literal.LEAVING);

		return agrData;
	}

	@Override
	public ProspectCustomerDetails getDedupCustomer(ProspectCustomerDetails customer) {
		logger.debug(Literal.ENTERING);

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
		Customer customer = null;
		if (StringUtils.isBlank(finCreditReviewDetailsData.getCif())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Cif";
			return getFailedStatus("90502", valueParm);
		} else {
			customer = customerDetailsService.getCustomerByCIF(finCreditReviewDetailsData.getCif());

			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finCreditReviewDetailsData.getCif();
				getFailedStatus("90101", valueParm);
			}
		}
		for (FinCreditReviewDetails detail : finCreditReviewDetailsData.getFinCreditReviewDetails()) {
			if (customer != null) {
				detail.setCreditRevCode(customer.getCustCtgCode());
				detail.setCustomerId(customer.getCustID());
			}
			if (StringUtils.isBlank(detail.getCurrency())) {
				detail.setCurrency("INR");
			}
			if (StringUtils.isBlank(detail.getAuditYear())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Audit Year";
				return getFailedStatus("90502", valueParm);
			}
			if (StringUtils.isBlank(detail.getBankName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Bank Name";
				return getFailedStatus("90502", valueParm);
			}
			if (StringUtils.isBlank(detail.getAuditors())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Auditors";
				return getFailedStatus("90502", valueParm);
			}
			if (StringUtils.isBlank(detail.getLocation())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Location";
				return getFailedStatus("90502", valueParm);
			}
			if (StringUtils.isBlank(String.valueOf(detail.getAuditedDate()))) {
				String[] valueParm = new String[1];
				valueParm[0] = "Audited Date";
				return getFailedStatus("90502", valueParm);
			}
			if (StringUtils.isBlank(String.valueOf(detail.isQualified()))) {
				String[] valueParm = new String[1];
				valueParm[0] = "Qualified";
				return getFailedStatus("90502", valueParm);
			}
			if (StringUtils.isBlank(detail.getAuditType())) {
				String[] valueParm = new String[1];
				valueParm[0] = "AuditType";
				return getFailedStatus("90502", valueParm);
			} else if (!StringUtils.equals(detail.getAuditType(), FacilityConstants.CREDITREVIEW_AUDITED)
					&& !StringUtils.equals(detail.getAuditType(), FacilityConstants.CREDITREVIEW_UNAUDITED)
					&& !StringUtils.equals(detail.getAuditType(), FacilityConstants.CREDITREVIEW_MNGRACNTS)) {
				String[] valueParm = new String[2];
				valueParm[0] = "AuditType";
				valueParm[1] = FacilityConstants.CREDITREVIEW_AUDITED + "," + FacilityConstants.CREDITREVIEW_UNAUDITED
						+ "," + FacilityConstants.CREDITREVIEW_MNGRACNTS;
				return getFailedStatus("90281", valueParm);

			}
			for (FinCreditReviewSummary summaryDetail : detail.getCreditReviewSummaryEntries()) {
				if (StringUtils.isBlank(String.valueOf(summaryDetail.getSubCategoryCode()))) {
					String[] valueParm = new String[1];
					valueParm[0] = "SubCategory Code";
					return getFailedStatus("90502", valueParm);
				} else {
					FinCreditRevSubCategory finCreditRevSubCategory = finCreditRevSubCategoryDAO
							.getFinCreditRevSubCategoryById(summaryDetail.getSubCategoryCode(), "");
					if (finCreditRevSubCategory == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "SubCategory Code " + summaryDetail.getSubCategoryCode();
						return getFailedStatus("90501", valueParm);
					}
					if (finCreditRevSubCategory != null
							&& StringUtils.endsWithIgnoreCase(finCreditRevSubCategory.getSubCategoryItemType(),
									FacilityConstants.CREDITREVIEW_CALCULATED_FIELD)) {
						String[] valueParm = new String[1];
						valueParm[0] = "SubCategory Code " + summaryDetail.getSubCategoryCode();
						return getFailedStatus("90501", valueParm);
					}
				}
				if (StringUtils.isBlank(String.valueOf(summaryDetail.getItemValue()))) {
					String[] valueParm = new String[1];
					valueParm[0] = "Item Value";
					return getFailedStatus("90502", valueParm);
				}
			}

		}
		response = customerController.doAddCreditReviewDetails(finCreditReviewDetailsData);
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * getCustDedup
	 * 
	 * @param custDedupDetails
	 */

	@Override
	public CustDedupResponse getCustDedup(CustDedupDetails custDedupDetails) throws ServiceException {
		logger.debug(Literal.ENTERING);

		CustDedupResponse response = new CustDedupResponse();
		CustomerDedup dedup = new CustomerDedup();
		List<CustomerDedup> duplicateList = new ArrayList<CustomerDedup>();
		List<CustDedupRequest> dedupList = custDedupDetails.getDedupList();

		if (CollectionUtils.isEmpty(dedupList)) {
			String[] valueParm = new String[2];
			valueParm[0] = "Request";
			valueParm[1] = " two fields";
			response.setReturnStatus(getFailedStatus("30507", valueParm));
			return response;
		} else {
			if (dedupList.size() < 2) {
				String[] valueParm = new String[2];
				valueParm[0] = "Request";
				valueParm[1] = " two fields";
				response.setReturnStatus(getFailedStatus("30507", valueParm));
				return response;
			}
		}

		String custCtgCode = null;
		for (CustDedupRequest detail : dedupList) {
			if (StringUtils.equalsIgnoreCase(detail.getName(), "CustCtgCode")) {
				custCtgCode = String.valueOf(detail.getValue());
				break;
			}
		}
		if (StringUtils.isBlank(custCtgCode)) {
			String[] valueParm = new String[1];
			valueParm[0] = "CategoryCode";
			response.setReturnStatus(getFailedStatus("90502", valueParm));
			return response;
		} else {

			// validate Customer category code
			boolean isExist = customerCategoryDAO.isCustCtgExist(custCtgCode, "");
			if (!isExist) {
				String[] valueParm = new String[2];
				valueParm[0] = "CustCtg";
				valueParm[1] = custCtgCode;
				response.setReturnStatus(getErrorDetails("90224", valueParm));
				return response;
			}
		}
		List<BuilderTable> fieldList = dedupFieldsDAO.getFieldList(custCtgCode.concat("Customer"));
		List<String> fieldNamesList = fieldList.stream().map(d -> d.getFieldName()).collect(Collectors.toList());

		for (CustDedupRequest feild : dedupList) {
			// mandatory validation
			if (StringUtils.isBlank(feild.getName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "name";
				response.setReturnStatus(getFailedStatus("90502", valueParm));
				return response;
			}
			if (StringUtils.isBlank(String.valueOf(feild.getValue()))) {
				String[] valueParm = new String[1];
				valueParm[0] = "value";
				response.setReturnStatus(getFailedStatus("90502", valueParm));
				return response;
			}

			boolean fieldFound = false;
			for (String dbField : fieldNamesList) {

				if (StringUtils.equalsIgnoreCase(dbField, feild.getName())) {
					fieldFound = true;
					if (feild.getName().equalsIgnoreCase("CustCtgCode")) {
						dedup.setCustCtgCode(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustShrtName")) {
						dedup.setCustShrtName(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustFName")) {
						dedup.setCustFName(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustLName")) {
						dedup.setCustLName(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("MobileNumber")) {
						dedup.setMobileNumber(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustEMail")) {
						dedup.setCustEMail(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustCIF")) {
						dedup.setCustCIF(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustCRCPR")) {
						dedup.setCustCRCPR(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("AadharNumber")) {
						dedup.setAadharNumber((String.valueOf(feild.getValue())));
					}
					if (feild.getName().equalsIgnoreCase("CustDOB")) {
						try {
							String fieldValue = Objects.toString(feild.getValue(), "");
							dedup.setCustDOB(DateUtil.parse(fieldValue, PennantConstants.APIDateFormatter));
						} catch (Exception e) {
							String[] valueParm = new String[2];
							valueParm[0] = feild.getName();
							valueParm[1] = "Date";
							response.setReturnStatus(getFailedStatus("41002", valueParm));
							return response;
						}
					}
					if (feild.getName().equalsIgnoreCase("CustNationality")) {
						dedup.setCustNationality(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustPassportNo")) {
						dedup.setCustPassportNo(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("VoterID")) {
						dedup.setVoterID(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("DrivingLicence")) {
						dedup.setDrivingLicenceNo(String.valueOf(feild.getValue()));
					}
				}
			}
			if (!fieldFound) {
				String[] valueParm = new String[1];
				valueParm[0] = "field name";
				response.setReturnStatus(getFailedStatus("41002", valueParm));
				return response;
			}

		}

		List<CustomerDedup> resDedupList = new ArrayList<CustomerDedup>();
		List<DedupParm> dedupParmList = dedupParmDAO.getDedupParmByModule(FinanceConstants.DEDUP_CUSTOMER, custCtgCode,
				"");
		// TO Check duplicate customer in Local database
		for (DedupParm dedupParm : dedupParmList) {
			List<CustomerDedup> list = customerDedupDAO.fetchCustomerDedupDetails(dedup, dedupParm.getSQLQuery());
			if (list != null && !list.isEmpty()) {
				duplicateList.addAll(list);
				if (!CollectionUtils.isEmpty(resDedupList)) {
					for (CustomerDedup customerDedup : resDedupList) {
						for (CustomerDedup dupCustDedup : duplicateList) {
							if (StringUtils.equalsIgnoreCase(customerDedup.getCustCIF(), dupCustDedup.getCustCIF())) {
								list.remove(dupCustDedup);
							}
						}
					}
				}
				resDedupList.addAll(list);
				duplicateList.clear();
			}
		}
		if (CollectionUtils.isNotEmpty(resDedupList)) {
			response.setDedupList(resDedupList);
			response.setReturnStatus(getSuccessStatus());

		} else {
			response.setReturnStatus(getSuccessStatus());
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * get blacklisted customer in PLF system
	 * 
	 * @param custDedupDetails
	 */

	@Override
	public CustDedupResponse getNegativeListCustomer(CustDedupDetails custDedupDetails) throws ServiceException {

		logger.debug(Literal.ENTERING);
		CustDedupResponse response = new CustDedupResponse();
		List<BlackListCustomers> duplicateList = new ArrayList<BlackListCustomers>();
		BlackListCustomers blackListCustomers = new BlackListCustomers();

		List<CustDedupRequest> dedupList = custDedupDetails.getDedupList();

		if (CollectionUtils.isEmpty(dedupList)) {
			String[] valueParm = new String[2];
			valueParm[0] = "Request";
			valueParm[1] = " two fields";
			response.setReturnStatus(getFailedStatus("30507", valueParm));
			return response;
		} else {
			if (dedupList.size() < 2) {
				String[] valueParm = new String[2];
				valueParm[0] = "Request";
				valueParm[1] = " two fields";
				response.setReturnStatus(getFailedStatus("30507", valueParm));
				return response;
			}
		}

		String custCtgCode = null;
		for (CustDedupRequest detail : dedupList) {
			if (StringUtils.equalsIgnoreCase(detail.getName(), "CustCtgCode")) {
				custCtgCode = String.valueOf(detail.getValue());
				break;
			}
		}
		if (StringUtils.isBlank(custCtgCode)) {
			String[] valueParm = new String[1];
			valueParm[0] = "CategoryCode";
			response.setReturnStatus(getFailedStatus("90502", valueParm));
			return response;
		} else {

			// validate Customer category code
			boolean isExist = customerCategoryDAO.isCustCtgExist(custCtgCode, "");
			if (!isExist) {
				String[] valueParm = new String[2];
				valueParm[0] = "CustCtg";
				valueParm[1] = custCtgCode;
				response.setReturnStatus(getErrorDetails("90224", valueParm));
				return response;
			}
		}

		List<BuilderTable> fieldList = dedupFieldsDAO.getFieldList(custCtgCode.concat("BlackList"));
		List<String> fieldNamesList = fieldList.stream().map(d -> d.getFieldName()).collect(Collectors.toList());

		for (CustDedupRequest feild : dedupList) {
			// mandatory validation
			if (StringUtils.isBlank(feild.getName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "name";
				response.setReturnStatus(getFailedStatus("90502", valueParm));
				return response;
			}
			if (StringUtils.isBlank(String.valueOf(feild.getValue()))) {
				String[] valueParm = new String[1];
				valueParm[0] = "value";
				response.setReturnStatus(getFailedStatus("90502", valueParm));
				return response;
			}
			boolean fieldFound = false;
			for (String dbField : fieldNamesList) {

				if (StringUtils.equalsIgnoreCase(dbField, feild.getName())) {
					fieldFound = true;

					if (feild.getName().equalsIgnoreCase("CustCIF")) {
						blackListCustomers.setCustCIF(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustFName")) {
						blackListCustomers.setCustFName(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustLName")) {
						blackListCustomers.setCustLName(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustShrtName")) {
						blackListCustomers.setCustShrtName(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustDOB")) {
						try {
							String fieldValue = Objects.toString(feild.getValue(), "");
							blackListCustomers
									.setCustDOB(DateUtil.parse(fieldValue, PennantConstants.APIDateFormatter));
						} catch (Exception e) {
							String[] valueParm = new String[2];
							valueParm[0] = feild.getName();
							valueParm[1] = "Date";
							response.setReturnStatus(getFailedStatus("41002", valueParm));
							return response;

						}
					}
					if (feild.getName().equalsIgnoreCase("MobileNumber")) {
						blackListCustomers.setMobileNumber(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustNationality")) {
						blackListCustomers.setCustNationality(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustCRCPR")) {
						blackListCustomers.setCustCRCPR(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustAadhaar")) {
						blackListCustomers.setCustAadhaar(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustCtgCode")) {
						blackListCustomers.setCustCtgCode(String.valueOf(feild.getValue()));
					}
					if (feild.getName().equalsIgnoreCase("CustPassportNo")) {
						blackListCustomers.setCustPassportNo(String.valueOf(feild.getValue()));
					}

					if (feild.getName().equalsIgnoreCase("CustCompName")) {
						blackListCustomers.setCustCompName(String.valueOf(feild.getValue()));
					}
				}

			}
			if (!fieldFound) {
				String[] valueParm = new String[1];
				valueParm[0] = "field name";
				response.setReturnStatus(getFailedStatus("41002", valueParm));
				return response;
			}

		}

		List<BlackListCustomers> negativeList = new ArrayList<BlackListCustomers>();
		List<DedupParm> dedupParmList = dedupParmDAO.getDedupParmByModule(FinanceConstants.DEDUP_BLACKLIST, custCtgCode,
				"");
		// TO Check duplicate customer in Local database
		for (DedupParm dedupParm : dedupParmList) {
			List<BlackListCustomers> list = blacklistCustomerDAO.fetchBlackListedCustomers(blackListCustomers,
					dedupParm.getSQLQuery());
			if (list != null && !list.isEmpty()) {
				duplicateList.addAll(list);
				if (!CollectionUtils.isEmpty(negativeList)) {
					for (BlackListCustomers blackListCust : negativeList) {
						for (BlackListCustomers dupBlackListCust : duplicateList) {
							if (StringUtils.equalsIgnoreCase(blackListCust.getCustCIF(),
									dupBlackListCust.getCustCIF())) {
								list.remove(dupBlackListCust);
							}
						}
					}
				}
				negativeList.addAll(list);
				duplicateList.clear();
			}
			for (BlackListCustomers blc : list) {
				blc.setRuleCode(dedupParm.getQueryCode());
				blc.setResult("1");
			}
		}
		if (CollectionUtils.isNotEmpty(negativeList)) {
			response.setBlackList(negativeList);
			response.setReturnStatus(getSuccessStatus());

		} else {
			response.setReturnStatus(getSuccessStatus());
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * Method for get customer and finance from PLF system.
	 * 
	 * @param SRMCustRequest
	 * @throws ServiceException
	 */
	@Override
	public List<CustomerDetails> getSRMCustDetails(SRMCustRequest srmCustRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);

		List<CustomerDetails> customerDetailsList = new ArrayList<>();
		CustomerDetails response = new CustomerDetails();
		response.setCustomer(null);

		// Mandatory validation
		if (StringUtils.isBlank(srmCustRequest.getSource())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Source";
			response.setReturnStatus(getFailedStatus("90502", valueParm));
			customerDetailsList.add(response);
			return customerDetailsList;
		}
		if (!StringUtils.equals(srmCustRequest.getSource(), APIConstants.SRM_SOURCE)
				&& !StringUtils.equalsIgnoreCase(srmCustRequest.getSource(), APIConstants.COB_SOURCE)) {
			String[] valueParm = new String[2];
			valueParm[0] = "SOURCE";
			valueParm[1] = "SRM or COB";
			response.setReturnStatus(getFailedStatus("90337", valueParm));
			customerDetailsList.add(response);
			return customerDetailsList;
		}
		if (APIConstants.SRM_SOURCE.equals(srmCustRequest.getSource())) {
			// Mandatory validation
			if (StringUtils.isBlank(srmCustRequest.getCustCif()) && StringUtils.isBlank(srmCustRequest.getPhoneNumber())
					&& StringUtils.isBlank(srmCustRequest.getFinReference())
					&& StringUtils.isBlank(srmCustRequest.getCustCRCPR())
					&& StringUtils.isBlank(srmCustRequest.getCustShrtName()) && srmCustRequest.getCustDOB() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Any one field value";
				response.setReturnStatus(getFailedStatus("90502", valueParm));
				customerDetailsList.add(response);
				return customerDetailsList;
			}
		}
		if (APIConstants.COB_SOURCE.equals(srmCustRequest.getSource())) {
			// Mandatory validation
			if (StringUtils.isBlank(srmCustRequest.getCustCRCPR())) {
				String[] valueParm = new String[1];
				valueParm[0] = "panNumber";
				response.setReturnStatus(getFailedStatus("90502", valueParm));
				customerDetailsList.add(response);
				return customerDetailsList;
			}
		}
		List<Long> custIdList = customerDAO.getCustomerDetailsBySRM(srmCustRequest);
		if (!CollectionUtils.isEmpty(custIdList)) {
			for (Long custId : custIdList) {
				response = customerController.getCustomerDetails(custId);
				List<CustomerFinanceDetail> customerFinanceDetail = approvalStatusEnquiryDAO
						.getListOfCustomerFinanceDetailById(custId, "_AView", false);
				if (response.getCustomerFinanceDetailList() != null) {
					response.getCustomerFinanceDetailList().addAll(customerFinanceDetail);
				}
				if (CollectionUtils.isNotEmpty(response.getCustomerFinanceDetailList())) {
					response.getCustomerFinanceDetailList().forEach(cfd -> {
						List<JointAccountDetail> jointAccountDetailList = jointAccountDetailDAO
								.getJointAccountDetailByFinRef(cfd.getFinID(), "_View");
						cfd.setJointAccountDetails(jointAccountDetailList);
					});

					for (CustomerFinanceDetail cfd : response.getCustomerFinanceDetailList()) {
						cfd.setStage(cfd.getNextRoleCode());
						cfd.setCurOddays(financeProfitDetailDAO.getCurOddays(cfd.getFinID()));
					}
				}
				customerDetailsList.add(response);
			}
			logger.debug(Literal.LEAVING);
			return customerDetailsList;
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = "given request";
			response.setReturnStatus(getFailedStatus("90266", valueParm));
			customerDetailsList.add(response);
			return customerDetailsList;
		}

	}

	@Override
	public CustValidationResponse doCustomerValidation(String coreBankId) throws ServiceException {
		logger.debug(Literal.ENTERING);
		// Mandatory validation
		if (StringUtils.isBlank(coreBankId)) {
			validationUtility.fieldLevelException();
		}
		CustValidationResponse response = null;
		boolean status = customerDetailsService.getCustomerByCoreBankId(coreBankId);
		if (status) {
			response = new CustValidationResponse();
			Customer cust = customerDAO.getCustomerByCoreBankId(coreBankId, "");
			if (cust != null) {
				response.setCustomerPhoneNumber(
						customerPhoneNumberService.getApprovedCustomerPhoneNumberById(cust.getCustID()));
				response.setCustomerName(cust.getCustShrtName());
			}
			response.setCif(cust.getCustCIF());
			LimitHeader headerDetail = limitDetailService.getLimitHeaderByCustomer(cust.getCustID());
			if (headerDetail != null) {
				for (LimitDetails detail : headerDetail.getCustomerLimitDetailsList()) {
					if (LimitConstants.LIMIT_ITEM_TOTAL.equals(detail.getGroupCode())) {
						response.setActualLimit(PennantApplicationUtil.formateAmount(
								detail.getLimitSanctioned().subtract(detail.getUtilisedLimit()),
								CurrencyUtil.getFormat(headerDetail.getLimitCcy())));
						response.setExpiryDate(detail.getExpiryDate());
					}
					response.setBlocklimit(headerDetail.isBlocklimit());

				}
				response.setReturnStatus(getSuccessStatus());
			}
			logger.debug(Literal.LEAVING);
			return response;
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = "coreBank";
			response = new CustValidationResponse();
			response.setReturnStatus(getFailedStatus("90266", valueParm));
			return response;
		}

	}

	/**
	 * Method to add Extended details
	 * 
	 * @param addCustomerExtendedFieldDetails
	 */
	@Override
	public CustomerExtendedFieldDetails addCustomerExtendedFieldDetails(
			CustomerExtendedFieldDetails customerExtendedFieldDetails) throws ServiceException {
		logger.debug(Literal.ENTERING);
		Customer customerDetails = null;
		CustomerExtendedFieldDetails response = new CustomerExtendedFieldDetails();
		// bean validations
		validationUtility.validate(customerExtendedFieldDetails, SaveValidationGroup.class);
		if (CollectionUtils.isEmpty(customerExtendedFieldDetails.getExtendedDetails())) {
			String[] valueParm = new String[1];
			valueParm[0] = "extendedDetails";
			response.setReturnStatus(getFailedStatus("90502", valueParm));
			return response;
		}
		if (StringUtils.isBlank(customerExtendedFieldDetails.getCif())) {
			String[] valueParm = new String[1];
			valueParm[0] = "cif";
			response.setReturnStatus(getFailedStatus("90502", valueParm));
			return response;
		} else {
			customerDetails = customerDetailsService.getCustomerByCIF(customerExtendedFieldDetails.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = customerExtendedFieldDetails.getCif();
				response.setReturnStatus(getFailedStatus("90101", valueParm));
				return response;
			}
		}

		// validate customer details as per the API specification
		List<ErrorDetail> errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(
				customerExtendedFieldDetails.getExtendedDetails(), ExtendedFieldConstants.MODULE_CUSTOMER,
				customerDetails.getCustCtgCode(), "");
		if (errorDetails.isEmpty()) {
			// call add Customer Employment method in case of no errors
			response = customerDetailsController.addCustomerExtendedFields(customerExtendedFieldDetails,
					customerDetails);
		} else {
			response.setErrorDetails(errorDetails);
			return getErrorMessage(response);
		}

		logger.debug(Literal.LEAVING);
		return response;

	}

	private CustomerExtendedFieldDetails getErrorMessage(CustomerExtendedFieldDetails customerExtendedFieldDetails) {
		for (ErrorDetail erroDetail : customerExtendedFieldDetails.getErrorDetails()) {
			CustomerExtendedFieldDetails response = new CustomerExtendedFieldDetails();
			response.setReturnStatus(getFailedStatus(erroDetail.getCode(), erroDetail.getError()));
			return response;
		}
		return new CustomerExtendedFieldDetails();
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
				aCustomerDetails.getUserDetails(), new HashMap<>());
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
				aCustomerPhoneNumber.getUserDetails(), new HashMap<>());
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
				aCustomerEmploymentDetail.getUserDetails(), new HashMap<>());
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
				null, null, auditDetail, aCustomerAddres.getUserDetails(), new HashMap<>());
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
				null, null, auditDetail, aCustomerEMail.getUserDetails(), new HashMap<>());
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
				null, null, auditDetail, aCustomerIncome.getUserDetails(), new HashMap<>());
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
				aCustomerBankInfo.getUserDetails(), new HashMap<>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerBankInfo
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustCardSales aCustCardSales, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustCardSales.getBefImage(), aCustCardSales);
		return new AuditHeader(String.valueOf(aCustCardSales.getCustID()), String.valueOf(aCustCardSales.getCustID()),
				null, null, auditDetail, aCustCardSales.getUserDetails(), new HashMap<>());
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
		return new AuditHeader(String.valueOf(aCustomerGST.getCustId()), String.valueOf(aCustomerGST.getCustId()), null,
				null, auditDetail, aCustomerGST.getUserDetails(), new HashMap<>());
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
				externalLiability.getUserDetails(), new HashMap<>());
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
				aCustomerDocument.getUserDetails(), new HashMap<>());
	}

	/**
	 * 
	 * @param directorDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(DirectorDetail directorDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, directorDetail.getBefImage(), directorDetail);
		return new AuditHeader(String.valueOf(directorDetail.getCustID()), String.valueOf(directorDetail.getCustID()),
				null, null, auditDetail, directorDetail.getUserDetails(), new HashMap<>());
	}

	/**
	 * Method for prepare response object with errorDetails.
	 * 
	 * @param errorCode
	 * @param valueParm
	 * @return
	 */
	public WSReturnStatus getErrorDetails(String errorCode, String[] valueParm) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = getFailedStatus(errorCode, valueParm);

		if (StringUtils.isBlank(response.getReturnCode())) {
			response = getFailedStatus(APIConstants.RES_FAILED_CODE, APIConstants.RES_FAILED_DESC);
		}

		logger.debug(Literal.LEAVING);
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
		response.setCustomerGstList(null);
		response.setPrimaryRelationOfficer(null);
		response.setDedupReq(null);
		response.setBlackListReq(null);
	}

	/**
	 * Method for fetch the basic log fields from the given request.
	 * 
	 * @param customerDetails
	 * @return
	 */
	private String[] getCustomerLogDetails(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
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
				return getFailedStatus("90101", valueParm);
			}

			int tempCount = customerDetailsService.getCustomerCountByCIF(custCIF, "_Temp");
			if (tempCount > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = custCIF;
				return getFailedStatus("90248", valueParm);
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
				return getFailedStatus("90329", valueParm);
			}
		}
		return returnStatus;
	}

	private void setUCIC(CustomerDetails customer, CustomerDedup dedup) {
		if (customer.getExtendedDetails() == null) {
			return;
		}

		for (ExtendedField details : customer.getExtendedDetails()) {
			for (ExtendedFieldData extFieldData : details.getExtendedFieldDataList()) {
				if ("UCIC".equalsIgnoreCase(extFieldData.getFieldName())) {
					dedup.setUcic(extFieldData.getFieldValue().toString());
					break;
				}
			}
		}
	}

	private void doBasicMandatoryValidations(Customer c) {
		ServiceExceptionDetails error = new ServiceExceptionDetails();
		ServiceExceptionDetails[] execptions = new ServiceExceptionDetails[1];

		if (c.isCustIsStaff() && StringUtils.isEmpty(c.getCustStaffID())
				|| (!c.isCustIsStaff() && StringUtils.isNotEmpty(c.getCustStaffID()))) {
			error.setFaultCode("9009");
			error.setFaultMessage("cif is Applicable for CoOwnerBankCustomer");

			execptions[0] = error;
			throw new ServiceException(execptions);
		}
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
	public void setCustomerGstService(CustomerGstService customerGstService) {
		this.customerGstService = customerGstService;
	}

	@Autowired
	public void setCustomerCardSalesInfoService(CustomerCardSalesInfoService customerCardSalesInfoService) {
		this.customerCardSalesInfoService = customerCardSalesInfoService;
	}

	@Autowired
	public CustomerCardSalesInfoDAO getCustomerCardSalesInfoDAO() {
		return customerCardSalesInfoDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setCustomerCardSalesInfoDAO(CustomerCardSalesInfoDAO customerCardSalesInfoDAO) {
		this.customerCardSalesInfoDAO = customerCardSalesInfoDAO;
	}

	@Autowired
	public void setDedupFieldsDAO(DedupFieldsDAO dedupFieldsDAO) {
		this.dedupFieldsDAO = dedupFieldsDAO;
	}

	@Autowired
	public void setDirectorDetailService(DirectorDetailService directorDetailService) {
		this.directorDetailService = directorDetailService;
	}

	@Autowired
	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	@Autowired
	public void setCustomerExtLiabilityDAO(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
	}

	@Autowired
	public void setBlacklistCustomerDAO(BlackListCustomerDAO blacklistCustomerDAO) {
		this.blacklistCustomerDAO = blacklistCustomerDAO;
	}

	@Autowired
	public void setFinCreditRevSubCategoryDAO(FinCreditRevSubCategoryDAO finCreditRevSubCategoryDAO) {
		this.finCreditRevSubCategoryDAO = finCreditRevSubCategoryDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	@Autowired
	public void setApprovalStatusEnquiryDAO(ApprovalStatusEnquiryDAO approvalStatusEnquiryDAO) {
		this.approvalStatusEnquiryDAO = approvalStatusEnquiryDAO;
	}

	@Autowired
	public void setJointAccountDetailDAO(JointAccountDetailDAO jointAccountDetailDAO) {
		this.jointAccountDetailDAO = jointAccountDetailDAO;
	}

	@Autowired
	public void setLimitDetailService(LimitDetailService limitDetailService) {
		this.limitDetailService = limitDetailService;
	}

	@Autowired
	public void setCustomerIncomeDAO(CustomerIncomeDAO customerIncomeDAO) {
		this.customerIncomeDAO = customerIncomeDAO;
	}
}