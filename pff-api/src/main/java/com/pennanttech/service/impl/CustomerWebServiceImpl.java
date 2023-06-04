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

		setDefaults(cd);

		AuditDetail auditDetail = customerDetailsService.doCustomerValidations(auditHeader);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			for (ErrorDetail ed : auditHeader.getErrorMessage()) {
				cd = new CustomerDetails();
				doEmptyResponseObject(cd);
				cd.setReturnStatus(getFailedStatus(ed.getCode(), ed.getError()));
			}

			return cd;
		}

		if (Boolean.TRUE.equals(cd.isDedupReq())) {
			List<CustomerDedup> dedupList = new ArrayList<>(1);
			CustomerDedup customerDedup = doSetCustomerDedup(cd);
			List<DedupParm> dedupParmList = dedupParmDAO.getDedupParmByModule(FinanceConstants.DEDUP_CUSTOMER,
					customerDedup.getCustCtgCode(), "");
			for (DedupParm dedupParm : dedupParmList) {
				List<CustomerDedup> list = customerDedupDAO.fetchCustomerDedupDetails(customerDedup,
						dedupParm.getSQLQuery());
				if (list != null && !list.isEmpty()) {
					dedupList.addAll(list);
				}
			}

			if (!dedupList.isEmpty()) {
				cd = new CustomerDetails();
				doEmptyResponseObject(cd);
				cd.setDedupReq(cd.isDedupReq());
				cd.setReturnStatus(getFailedStatus("90343", "dedup"));
				cd.setCustomerDedupList(dedupList);
				return cd;
			}
		}

		if (Boolean.TRUE.equals(cd.isBlackListReq())) {
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
				cd = new CustomerDetails();
				doEmptyResponseObject(cd);
				cd.setBlackListReq(cd.isBlackListReq());
				cd.setReturnStatus(getFailedStatus("90343", "blackList"));
				cd.setBalckListCustomers(blackList);
				return cd;
			}
		}

		logReference(cd.getCustCIF());

		logger.debug(Literal.LEAVING);
		return customerController.createCustomer(cd);
	}

	private BlackListCustomers doSetBlackListCustomerData(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		BlackListCustomers blc = new BlackListCustomers();

		Customer customer = customerDetails.getCustomer();

		if (customer == null) {
			return blc;
		}

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

		blc.setCustCIF(customer.getCustCIF());
		blc.setCustShrtName(customer.getCustShrtName());
		blc.setCustFName(customer.getCustFName());
		blc.setCustLName(customer.getCustLName());
		blc.setCustCRCPR(customer.getCustCRCPR());
		blc.setCustPassportNo(customer.getCustPassportNo());
		blc.setMobileNumber(mobileNumber);
		blc.setCustNationality(customer.getCustNationality());
		blc.setCustDOB(customer.getCustDOB());
		blc.setCustCtgCode(customer.getCustCtgCode());

		blc.setLikeCustFName(blc.getCustFName() != null ? "%" + blc.getCustFName() + "%" : "");
		blc.setLikeCustLName(blc.getCustLName() != null ? "%" + blc.getCustLName() + "%" : "");

		logger.debug(Literal.LEAVING);
		return blc;
	}

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

		if (cd == null) {
			return getFailedStatus();
		}

		String[] logFields = getCustomerLogDetails(cd);

		APIErrorHandlerService.logKeyFields(logFields);

		validationUtility.validate(cd, UpdateValidationGroup.class);

		doBasicMandatoryValidations(cd.getCustomer());

		setDefaults(cd);

		WSReturnStatus status = validateCustomerCIF(cd.getCustCIF());

		if (status != null) {
			return status;
		}

		status = validateCustomerCatageory(cd);
		if (status != null) {
			return status;
		}

		logReference(cd.getCustCIF());
		AuditHeader auditHeader = getAuditHeader(cd, PennantConstants.TRAN_WF);

		AuditDetail auditDetail = customerDetailsService.doCustomerValidations(auditHeader);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);

			return getFailedStatus(ed.getCode(), ed.getError());
		}

		WSReturnStatus returnStatus = customerController.updateCustomer(cd);

		logger.debug(Literal.LEAVING);

		return returnStatus;
	}

	@Override
	public CustomerDetails getCustomerDetails(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		CustomerDetails response = null;

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			response = new CustomerDetails();
			doEmptyResponseObject(response);
			response.setReturnStatus(getFailedStatus("90101", custCIF));

			return response;
		}

		response = customerController.getCustomerDetails(customer.getCustID());

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus deleteCustomer(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);

		if (customer == null) {
			return getFailedStatus("90101", custCIF);
		}

		logger.debug(Literal.LEAVING);
		return customerController.deleteCustomerById(customer.getCustID());
	}

	@Override
	public CustomerDetails getCustomerPersonalInfo(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);

		if (customer == null) {
			CustomerDetails response = new CustomerDetails();
			response.setReturnStatus(getFailedStatus("90101", custCIF));
			return response;
		}

		logger.debug(Literal.LEAVING);

		return customerController.getCustomerPersonalInfo(customer.getCustID());
	}

	@Override
	public WSReturnStatus updateCustomerPersonalInfo(CustomerDetails cd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (cd == null) {
			return getFailedStatus();
		}

		String[] logFields = getCustomerLogDetails(cd);

		APIErrorHandlerService.logKeyFields(logFields);

		validationUtility.validate(cd, PersionalInfoGroup.class);

		doBasicMandatoryValidations(cd.getCustomer());

		Customer customer = null;

		setDefaults(cd);

		if (StringUtils.isNotBlank(cd.getCustCIF())) {
			customer = customerDetailsService.getCustomerByCIF(cd.getCustCIF());
		}

		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = cd.getCustCIF();
			return getFailedStatus("90101", valueParm);
		}

		if (!StringUtils.equals(cd.getCustCtgCode(), customer.getCustCtgCode())) {
			String[] valueParm = new String[2];
			valueParm[0] = cd.getCustCtgCode();
			valueParm[1] = cd.getCustCIF();
			return getFailedStatus("90599", valueParm);
		}

		logReference(cd.getCustCIF());

		cd.getCustomer().setCustID(customer.getCustID());
		cd.getCustomer().setCustCtgCode(customer.getCustCtgCode());

		AuditHeader auditHeader = getAuditHeader(cd, PennantConstants.TRAN_WF);

		AuditDetail auditDetail = customerService.doCustomerValidations(auditHeader);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);
			return getFailedStatus(ed.getCode(), ed.getError());
		}

		WSReturnStatus returnStatus = customerController.updateCustomerPersionalInfo(cd);

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

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);
			response = new EmploymentDetail();
			response.setReturnStatus(getFailedStatus(ed.getCode(), ed.getError()));
			return response;
		}

		response = customerController.addCustomerEmployment(employmentDetail.getCustomerEmploymentDetail(),
				employmentDetail.getCif());

		logger.debug(Literal.LEAVING);
		return response;

	}

	@Override
	public CustomerDetails getCustomerEmployment(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		CustomerDetails response = new CustomerDetails();

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);

		if (customer == null) {
			response.setReturnStatus(getFailedStatus("90101", custCIF));
			response.setCustomer(null);
		} else {
			response = customerController.getCustomerEmployment(custCIF);
		}

		logger.debug(Literal.LEAVING);

		return response;
	}

	@Override
	public WSReturnStatus updateCustomerEmployment(EmploymentDetail ed) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(ed, UpdateValidationGroup.class);

		CustomerEmploymentDetail ced = ed.getCustomerEmploymentDetail();

		if (ced == null) {
			return getFailedStatus("90502", "employment");
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(ed.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(ed.getCif());
		}

		if (customer == null) {
			return getFailedStatus("90101", ed.getCif());
		}

		logReference(ed.getCif());

		AuditHeader auditHeader = getAuditHeader(ced, PennantConstants.TRAN_WF);

		AuditDetail auditDetail = customerEmploymentDetailService.doValidations(ced, customer);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail error = auditHeader.getErrorMessage().get(0);

			return getFailedStatus(error.getCode(), error.getError());
		}

		long custEmpId = ced.getCustEmpId();
		ced = customerEmploymentDetailService.getApprovedCustomerEmploymentDetailByCustEmpId(custEmpId);

		if (ced == null || ced.getCustID() != (customer.getCustID())) {
			return getFailedStatus("90104", String.valueOf(custEmpId), ed.getCif());
		}

		WSReturnStatus response = customerController.updateCustomerEmployment(ced, ed.getCif());

		logger.debug(Literal.LEAVING);
		return response;

	}

	@Override
	public WSReturnStatus deleteCustomerEmployment(EmploymentDetail ed) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(ed, DeleteValidationGroup.class);

		Customer customer = null;
		if (StringUtils.isNotBlank(ed.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(ed.getCif());
		}

		if (customer == null) {
			return getFailedStatus("90101", ed.getCif());
		}

		long custID = customer.getCustID();
		long employementId = ed.getEmployementId();

		logReference(ed.getCif());

		CustomerEmploymentDetail ced = customerEmploymentDetailService
				.getApprovedCustomerEmploymentDetailByCustEmpId(employementId);

		if (ced == null) {
			return getFailedStatus("90104", String.valueOf(employementId), ed.getCif());
		}

		if (ced.getCustID() != custID) {
			return getFailedStatus("90104", String.valueOf(ed.getCustomerEmploymentDetail().getCustEmpId()),
					ed.getCif());
		}

		logger.debug(Literal.LEAVING);
		return customerController.deleteCustomerEmployment(ced);
	}

	@Override
	public CustomerDirectorDetail addCustomerDirectorDetail(CustomerDirectorDetail cdd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		CustomerDirectorDetail response = new CustomerDirectorDetail();

		String cif = cdd.getCif();
		validationUtility.validate(cdd, SaveValidationGroup.class);

		if (cdd.getDirectorDetail() == null) {
			response.setReturnStatus(getFailedStatus("90502", "directorDetail"));
			return response;
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(cif)) {
			customer = customerDetailsService.getCustomerByCIF(cif);
		}

		if (customer == null) {
			response.setReturnStatus(getFailedStatus("90101", cif));
			return response;
		}

		if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			response.setReturnStatus(getFailedStatus("90124", "director details",
					PennantConstants.PFF_CUSTCTG_CORP + "," + PennantConstants.PFF_CUSTCTG_SME));
			return response;
		}

		logReference(cdd.getCif());

		AuditHeader auditHeader = getAuditHeader(cdd.getDirectorDetail(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = directorDetailService.doValidations(cdd.getDirectorDetail(), customer);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);
			response.setReturnStatus(getFailedStatus(ed.getCode(), ed.getError()));
			return response;
		}

		response = customerController.addCustomerDirectorDetails(cdd.getDirectorDetail(), cdd.getCif());

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
	public WSReturnStatus updateCustomerDirectorDetail(CustomerDirectorDetail cdd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cdd, UpdateValidationGroup.class);

		if (cdd.getDirectorDetail() == null) {
			return getFailedStatus("90502", "DirectorDetail");
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(cdd.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(cdd.getCif());
		}

		if (customer == null) {
			return getFailedStatus("90101", cdd.getCif());
		}

		if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			return getFailedStatus("90124", "director details",
					PennantConstants.PFF_CUSTCTG_CORP + "," + PennantConstants.PFF_CUSTCTG_SME);
		}

		logReference(cdd.getCif());

		AuditHeader auditHeader = getAuditHeader(cdd.getDirectorDetail(), PennantConstants.TRAN_WF);

		AuditDetail auditDetail = directorDetailService.doValidations(cdd.getDirectorDetail(), customer);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);
			return getFailedStatus(ed.getCode(), ed.getError());
		}

		long directorId = cdd.getDirectorDetail().getDirectorId();
		long custID = customer.getCustID();
		DirectorDetail dd = directorDetailService.getApprovedDirectorDetailByDirectorId(directorId, custID);

		if (dd == null || (dd.getCustID() != custID)) {
			return getFailedStatus("90266", "DirectorId  " + directorId);
		}

		logger.debug(Literal.LEAVING);
		return customerController.updateCustomerDirectorDetail(cdd.getDirectorDetail(), cdd.getCif());
	}

	@Override
	public WSReturnStatus deleteCustomerDirectorDetail(CustomerDirectorDetail cdd) throws ServiceException {
		DirectorDetail dd = new DirectorDetail();

		Customer customer = null;

		validationUtility.validate(cdd, DeleteValidationGroup.class);

		if (StringUtils.isBlank(cdd.getCif())) {
			return getFailedStatus("90502", "cif");
		}

		long directorId = cdd.getDirectorId();

		if (directorId <= 0) {
			return getFailedStatus("90502", "directorId");
		}

		if (StringUtils.isNotBlank(cdd.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(cdd.getCif());
		}

		if (customer == null) {
			return getFailedStatus("90101", cdd.getCif());
		}

		if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			return getFailedStatus("90124", "director details",
					PennantConstants.PFF_CUSTCTG_CORP + "," + PennantConstants.PFF_CUSTCTG_SME);

		}

		long custID = customer.getCustID();
		dd.setCustID(custID);
		dd.setDirectorId(directorId);

		logReference(cdd.getCif());

		DirectorDetail item = directorDetailService.getApprovedDirectorDetailByDirectorId(directorId, custID);

		if (item == null || (dd.getCustID() != custID)) {
			return getFailedStatus("90266", "DirectorId " + directorId);
		}

		logger.debug(Literal.LEAVING);

		return customerController.deleteCustomerDirectorDetail(item);
	}

	@Override
	public WSReturnStatus addCustomerPhoneNumber(CustPhoneNumber cpn) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cpn, SaveValidationGroup.class);

		CustomerPhoneNumber phoneNumber = cpn.getCustomerPhoneNumber();
		if (phoneNumber == null) {
			return getFailedStatus("90502", "phone");
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(cpn.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(cpn.getCif());
		}

		if (customer == null) {
			return getFailedStatus("90101", cpn.getCif());
		}

		logReference(cpn.getCif());

		phoneNumber.setPhoneCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(phoneNumber, PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerPhoneNumberService.doValidations(phoneNumber,
				APIConstants.SERVICE_TYPE_CREATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);

			return getFailedStatus(ed.getCode(), ed.getError());

		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.addCustomerPhoneNumber(phoneNumber, cpn.getCif());
	}

	@Override
	public WSReturnStatus updateCustomerPhoneNumber(CustPhoneNumber cpn) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cpn, UpdateValidationGroup.class);

		CustomerPhoneNumber phoneNumber = cpn.getCustomerPhoneNumber();

		if (phoneNumber == null) {
			return getFailedStatus("90502", "phone");
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(cpn.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(cpn.getCif());
		}

		if (customer == null) {
			return getFailedStatus("90101", cpn.getCif());
		}

		logReference(cpn.getCif());
		AuditHeader auditHeader = getAuditHeader(phoneNumber, PennantConstants.TRAN_WF);
		long custID = customer.getCustID();
		phoneNumber.setPhoneCustID(custID);
		AuditDetail auditDetail = customerPhoneNumberService.doValidations(phoneNumber,
				APIConstants.SERVICE_TYPE_UPDATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);
			return getFailedStatus(ed.getCode(), ed.getError());
		}

		String phoneTypeCode = phoneNumber.getPhoneTypeCode();
		CustomerPhoneNumber item = customerPhoneNumberService.getApprovedCustomerPhoneNumberById(custID, phoneTypeCode);

		if (item == null) {
			return getFailedStatus("90106", phoneTypeCode, cpn.getCif());
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.updateCustomerPhoneNumber(phoneNumber, cpn.getCif());
	}

	@Override
	public CustomerDetails getCustomerPhoneNumbers(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		CustomerDetails response = new CustomerDetails();

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);

		if (customer == null) {
			response.setReturnStatus(getFailedStatus("90101", custCIF));
			response.setCustomer(null);
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.getCustomerPhoneNumbers(custCIF);
	}

	@Override
	public WSReturnStatus deleteCustomerPhoneNumber(CustPhoneNumber cpn) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cpn, DeleteValidationGroup.class);

		Customer customer = null;
		if (StringUtils.isNotBlank(cpn.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(cpn.getCif());
		}

		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = cpn.getCif();
			return getFailedStatus("90101", valueParm);
		}

		long custID = customer.getCustID();
		String phoneTypeCode = cpn.getPhoneTypeCode();

		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setPhoneCustID(custID);
		customerPhoneNumber.setPhoneTypeCode(phoneTypeCode);

		logReference(cpn.getCif());

		CustomerPhoneNumber item = customerPhoneNumberService.getApprovedCustomerPhoneNumberById(custID, phoneTypeCode);

		if (item == null) {
			return getFailedStatus("90106", cpn.getCif(), phoneTypeCode);
		}

		if (item.getPhoneTypePriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
			return getFailedStatus("90270", "cannot delete", "Phone");
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.deleteCustomerPhoneNumber(customerPhoneNumber);
	}

	@Override
	public WSReturnStatus addCustomerAddress(CustAddress ca) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(ca, SaveValidationGroup.class);

		if (ca.getCustomerAddres() == null) {
			return getFailedStatus("90502", "address");
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(ca.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(ca.getCif());
		}

		if (customer == null) {
			return getFailedStatus("90101", ca.getCif());

		}

		logReference(ca.getCif());

		long custID = customer.getCustID();

		ca.getCustomerAddres().setCustID(custID);
		AuditHeader auditHeader = getAuditHeader(ca.getCustomerAddres(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerAddresService.doValidations(ca.getCustomerAddres(),
				APIConstants.SERVICE_TYPE_CREATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);
			return getFailedStatus(ed.getCode(), ed.getError());
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.addCustomerAddress(ca.getCustomerAddres(), ca.getCif());
	}

	@Override
	public WSReturnStatus updateCustomerAddress(CustAddress ca) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(ca, UpdateValidationGroup.class);

		if (ca.getCustomerAddres() == null) {
			return getFailedStatus("90502", "address");
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(ca.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(ca.getCif());
		}

		if (customer == null) {
			return getFailedStatus("90101", ca.getCif());
		}

		logReference(ca.getCif());

		long custID = customer.getCustID();
		ca.getCustomerAddres().setCustID(custID);

		AuditHeader auditHeader = getAuditHeader(ca.getCustomerAddres(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerAddresService.doValidations(ca.getCustomerAddres(),
				APIConstants.SERVICE_TYPE_UPDATE);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);
			return getFailedStatus(ed.getCode(), ed.getError());
		}

		String custAddrType = ca.getCustomerAddres().getCustAddrType();
		CustomerAddres item = customerAddresService.getApprovedCustomerAddresById(custID, custAddrType);

		if (item == null) {
			return getFailedStatus("90109", custAddrType, ca.getCif());
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.updateCustomerAddress(ca.getCustomerAddres(), ca.getCif());
	}

	@Override
	public CustomerDetails getCustomerAddresses(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		CustomerDetails response = new CustomerDetails();

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);

		if (customer == null) {
			response.setReturnStatus(getFailedStatus("90101", custCIF));
			response.setCustomer(null);
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.getCustomerAddresses(custCIF);
	}

	@Override
	public WSReturnStatus deleteCustomerAddress(CustAddress ca) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(ca, DeleteValidationGroup.class);

		Customer customer = null;

		if (StringUtils.isNotBlank(ca.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(ca.getCif());
		}

		if (customer == null) {
			return getFailedStatus("90101", ca.getCif());
		}

		long custID = customer.getCustID();
		String addrType = ca.getAddrType();

		logReference(ca.getCif());

		CustomerAddres object = customerAddresService.getApprovedCustomerAddresById(custID, addrType);

		if (object == null) {
			return getFailedStatus("90109", ca.getCif(), addrType);
		}

		if (object.getCustAddrPriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
			return getFailedStatus("90270", "cannot delete", "Address");
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.deleteCustomerAddress(object);
	}

	@Override
	public WSReturnStatus addCustomerEmail(CustEMail custEMail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(custEMail, SaveValidationGroup.class);

		if (custEMail.getCustomerEMail() == null) {
			return getFailedStatus("90502", "email");
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(custEMail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(custEMail.getCif());
		}

		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custEMail.getCif();
			return getFailedStatus("90101", valueParm);

		}

		logReference(custEMail.getCif());

		custEMail.getCustomerEMail().setCustID(customer.getCustID());
		AuditHeader auditHeader = getAuditHeader(custEMail.getCustomerEMail(), PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerEMailService.doValidations(custEMail.getCustomerEMail(),
				APIConstants.SERVICE_TYPE_CREATE);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);

			return getFailedStatus(ed.getCode(), ed.getError());
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.addCustomerEmail(custEMail.getCustomerEMail(), custEMail.getCif());
	}

	@Override
	public WSReturnStatus updateCustomerEmail(CustEMail custEMail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(custEMail, UpdateValidationGroup.class);

		CustomerEMail item = custEMail.getCustomerEMail();

		if (item == null) {
			return getFailedStatus("90502", "email");
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(custEMail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(custEMail.getCif());
		}

		if (customer == null) {
			return getFailedStatus("90101", custEMail.getCif());
		}

		logReference(custEMail.getCif());

		long custID = customer.getCustID();

		item.setCustID(custID);

		AuditHeader auditHeader = getAuditHeader(item, PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerEMailService.doValidations(item, APIConstants.SERVICE_TYPE_UPDATE);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);
			return getFailedStatus(ed.getCode(), ed.getError());
		}

		String mailTypeCode = item.getCustEMailTypeCode();

		CustomerEMail customerEmail = customerEMailService.getApprovedCustomerEMailById(custID, mailTypeCode);

		if (customerEmail == null) {
			return getFailedStatus("90111", custEMail.getCif());
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.updateCustomerEmail(item, custEMail.getCif());
	}

	@Override
	public CustomerDetails getCustomerEmails(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);

		if (customer == null) {
			CustomerDetails response = new CustomerDetails();
			response.setReturnStatus(getFailedStatus("90101", custCIF));
			response.setCustomer(null);

			return response;
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.getCustomerEmails(custCIF);
	}

	@Override
	public WSReturnStatus deleteCustomerEmail(CustEMail custEMail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(custEMail, DeleteValidationGroup.class);

		String custCIF = custEMail.getCif();

		Customer customer = null;

		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			return getFailedStatus("90101", custCIF);
		}

		long custID = customer.getCustID();
		String mailTypeCode = custEMail.getCustEMailTypeCode();

		CustomerEMail customerEMaial = new CustomerEMail();
		customerEMaial.setCustID(custID);
		customerEMaial.setCustEMailTypeCode(mailTypeCode);

		logReference(custCIF);

		CustomerEMail prvCustomerEMail = customerEMailService.getApprovedCustomerEMailById(custID, mailTypeCode);

		if (prvCustomerEMail == null) {
			return getFailedStatus("90111", custCIF);
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.deleteCustomerEmail(customerEMaial);
	}

	@Override
	public WSReturnStatus addCustomerIncome(CustomerIncomeDetail cid) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cid, SaveValidationGroup.class);

		CustomerIncome custIncome = cid.getCustomerIncome();

		if (custIncome == null) {
			return getFailedStatus("90502", "customerIncome");
		}

		String custCIF = cid.getCif();

		if (!ImplementationConstants.ALLOW_CUSTOMER_INCOMES) {
			return getFailedStatus("90599", "Customerincome", custCIF);
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			return getFailedStatus("90101", custCIF);

		}

		logReference(custCIF);

		boolean corpFinReq = SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_CORP_FINANCE_TAB_REQ);

		if (!corpFinReq && PennantConstants.PFF_CUSTCTG_CORP.equals(customer.getCustCtgCode())) {
			return getFailedStatus("90124", "Customerincome", PennantConstants.PFF_CUSTCTG_INDIV);
		}

		AuditHeader auditHeader = getAuditHeader(custIncome, PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerIncomeService.doValidations(custIncome);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);
			return getFailedStatus(ed.getCode(), ed.getError());
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.addCustomerIncome(custIncome, custCIF);
	}

	@Override
	public WSReturnStatus updateCustomerIncome(CustomerIncomeDetail cid) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cid, UpdateValidationGroup.class);

		CustomerIncome custIncome = cid.getCustomerIncome();

		if (custIncome == null) {
			return getFailedStatus("90502", "customerIncome");
		}

		if (!ImplementationConstants.ALLOW_CUSTOMER_INCOMES) {
			return getFailedStatus("90599", "Customerincome");
		}

		Customer customer = null;
		String custCIF = cid.getCif();
		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			return getFailedStatus("90101", custCIF);
		}

		custIncome.setCustId(customer.getCustID());

		logReference(custCIF);

		boolean corpFinReq = SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_CORP_FINANCE_TAB_REQ);

		if (!corpFinReq && PennantConstants.PFF_CUSTCTG_CORP.equals(customer.getCustCtgCode())) {
			return getFailedStatus("90124", "Customerincome", PennantConstants.PFF_CUSTCTG_INDIV);
		}

		AuditHeader auditHeader = getAuditHeader(custIncome, PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerIncomeService.doValidations(custIncome);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);

			return getFailedStatus(ed.getCode(), ed.getError());
		}

		custIncome.setCustId(customer.getCustID());

		CustomerIncome customerIncome = customerIncomeService.getCustomerIncomeById(custIncome);

		if (customerIncome == null) {
			return getFailedStatus("90112", custCIF);
		}

		custIncome.setId(customerIncome.getId());

		logger.debug(Literal.LEAVING);

		return customerDetailsController.updateCustomerIncome(custIncome, custCIF);
	}

	@Override
	public CustomerDetails getCustomerIncomes(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		CustomerDetails response = new CustomerDetails();

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);

		if (customer == null) {
			response.setReturnStatus(getFailedStatus("90101", custCIF));
			response.setCustomer(null);

			return response;
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.getCustomerIncomes(custCIF);
	}

	@Override
	public WSReturnStatus deleteCustomerIncome(CustomerIncomeDetail cid) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cid, DeleteValidationGroup.class);

		String custCIF = cid.getCif();

		Customer customer = null;
		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			return getFailedStatus("90101", custCIF);
		}

		CustomerIncome custIncome = new CustomerIncome();
		custIncome.setCustId(customer.getCustID());
		custIncome.setIncomeType(cid.getCustIncomeType());
		custIncome.setCategory(cid.getCategory());
		custIncome.setIncomeExpense(cid.getIncomeExpense());

		custIncome.setLinkId(customerIncomeDAO.getLinkId(customer.getCustID()));

		logReference(custCIF);

		CustomerIncome item = customerIncomeService.getApprovedCustomerIncomeById(custIncome);

		if (item == null) {
			return getFailedStatus("90112", custCIF);
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.deleteCustomerIncome(custIncome);
	}

	@Override
	public CustomerBankInfoDetail addCustomerBankingInformation(CustomerBankInfoDetail cbd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cbd, SaveValidationGroup.class);

		CustomerBankInfo custBankInfo = cbd.getCustomerBankInfo();

		if (custBankInfo == null) {
			cbd = new CustomerBankInfoDetail();
			cbd.setReturnStatus(getFailedStatus("90502", "customerBankInfo"));
			return cbd;
		}

		String custCIF = cbd.getCif();

		Customer customer = null;
		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			cbd = new CustomerBankInfoDetail();
			cbd.setReturnStatus(getFailedStatus("90101", custCIF));
			return cbd;
		}

		logReference(custCIF);

		AuditHeader auditHeader = getAuditHeader(custBankInfo, PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerBankInfoService.doValidations(custBankInfo, PennantConstants.RECORD_TYPE_NEW,
				new AuditDetail());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);
			cbd = new CustomerBankInfoDetail();
			cbd.setReturnStatus(getFailedStatus(ed.getCode(), ed.getError()));
			return cbd;
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.addCustomerBankingInformation(custBankInfo, custCIF);

	}

	@Override
	public WSReturnStatus updateCustomerBankingInformation(CustomerBankInfoDetail cbd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cbd, UpdateValidationGroup.class);

		CustomerBankInfo custBankInfo = cbd.getCustomerBankInfo();

		if (custBankInfo == null) {
			return getFailedStatus("90502", "customerBankInfo");
		}

		String custCIF = cbd.getCif();

		Customer customer = null;
		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			return getFailedStatus("90101", custCIF);
		}

		logReference(custCIF);

		AuditHeader auditHeader = getAuditHeader(custBankInfo, PennantConstants.TRAN_WF);

		AuditDetail auditDetail = customerBankInfoService.doValidations(custBankInfo, PennantConstants.RECORD_TYPE_UPD,
				new AuditDetail());

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);

			return getFailedStatus(ed.getCode(), ed.getError());
		}

		CustomerBankInfo customerBankInfo = customerBankInfoService.getCustomerBankInfoById(custBankInfo.getBankId());

		if (customerBankInfo == null) {
			return getFailedStatus("90116", String.valueOf(custBankInfo.getBankId()));
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.updateCustomerBankingInformation(custBankInfo, custCIF);

	}

	@Override
	public CustomerDetails getCustomerBankingInformation(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);

		if (customer == null) {
			CustomerDetails response = new CustomerDetails();
			response.setReturnStatus(getFailedStatus("90101", custCIF));
			response.setCustomer(null);
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.getCustomerBankingInformation(custCIF);
	}

	@Override
	public WSReturnStatus deleteCustomerBankingInformation(CustomerBankInfoDetail cbid) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cbid, DeleteValidationGroup.class);

		String custCIF = cbid.getCif();

		Customer customer = null;

		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			return getFailedStatus("90101", valueParm);
		}

		CustomerBankInfo customerBankInfo = new CustomerBankInfo();
		customerBankInfo.setCustID(customer.getCustID());
		customerBankInfo.setBankId(cbid.getBankId());

		logReference(custCIF);

		CustomerBankInfo custBankInfo = customerBankInfoService.getCustomerBankInfoById(cbid.getBankId());

		if (custBankInfo == null) {
			return getFailedStatus("90116", String.valueOf(cbid.getBankId()), custCIF);
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.deleteCustomerBankingInformation(custBankInfo);
	}

	@Override
	public CustomerGstInfoDetail addCustomerGstInformation(CustomerGstInfoDetail custGST) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(custGST, SaveValidationGroup.class);

		CustomerGST customerGST = custGST.getCustomerGST();

		if (customerGST == null) {
			custGST = new CustomerGstInfoDetail();
			custGST.setReturnStatus(getFailedStatus("90502", "customerGstInfo"));
			return custGST;
		}

		String custCIF = custGST.getCif();

		Customer customer = null;

		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			custGST = new CustomerGstInfoDetail();
			custGST.setReturnStatus(getFailedStatus("90101", custCIF));
			return custGST;
		}

		logReference(custCIF);

		AuditHeader auditHeader = getAuditHeader(customerGST, PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerGstService.doValidations(customerGST, PennantConstants.RECORD_TYPE_NEW);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);

			custGST = new CustomerGstInfoDetail();
			custGST.setReturnStatus(getFailedStatus(ed.getCode(), ed.getError()));
			return custGST;
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.addCustomerGstInformation(customerGST, custCIF);

	}

	@Override
	public WSReturnStatus updateCustomerGstInformation(CustomerGstInfoDetail custGST) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(custGST, UpdateValidationGroup.class);

		CustomerGST customerGST = custGST.getCustomerGST();

		if (customerGST == null) {
			return getFailedStatus("90502", "customerBankInfo");
		}

		String custCIF = custGST.getCif();

		Customer customer = null;

		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			return getFailedStatus("90101", custCIF);
		}

		logReference(custCIF);

		AuditHeader auditHeader = getAuditHeader(customerGST, PennantConstants.TRAN_WF);

		AuditDetail auditDetail = customerGstService.doValidations(customerGST, PennantConstants.RECORD_TYPE_UPD);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);

			return getFailedStatus(ed.getCode(), ed.getError());
		}

		customerGST = customerGstService.getCustomerGstDeatailsByCustomerId(customerGST.getId());

		if (customerGST == null) {
			return getFailedStatus("90116", String.valueOf(customer.getCustID()), custCIF);
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.updateCustomerGstInformation(customerGST, custCIF);

	}

	@Override
	public CustomerDetails getCustomerGstnformation(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);

		if (customer == null) {
			CustomerDetails response = new CustomerDetails();
			response.setReturnStatus(getFailedStatus("90101", custCIF));
			response.setCustomer(null);

			return response;
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.getCustomerGstInformation(custCIF);
	}

	@Override
	public WSReturnStatus deleteCustomerGstInformation(CustomerGstInfoDetail custGST) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(custGST, DeleteValidationGroup.class);

		String custCIF = custGST.getCif();

		Customer customer = null;
		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			return getFailedStatus("90101", valueParm);
		}

		CustomerGST customerGST = new CustomerGST();
		customerGST.setCustId(customer.getCustID());
		customerGST.setId(custGST.getId());

		logReference(custCIF);

		CustomerGST customeGST = customerGstService.getCustomerGstDeatailsByCustomerId(custGST.getId());

		if (customeGST == null) {
			return getFailedStatus("90116", String.valueOf(custGST.getId()));
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.deleteCustomerGSTInformation(customeGST);
	}

	@Override
	public CustomerCardSaleInfoDetails addCardSalesInformation(CustomerCardSaleInfoDetails ccsid)
			throws ServiceException {

		logger.debug(Literal.ENTERING);

		validationUtility.validate(ccsid, SaveValidationGroup.class);

		CustCardSales custCardSales = ccsid.getCustCardSales();

		if (custCardSales == null) {
			ccsid = new CustomerCardSaleInfoDetails();
			ccsid.setReturnStatus(getFailedStatus("90502", "customer CardSalesInfo"));
			return ccsid;
		}

		String custCIF = ccsid.getCif();

		Customer customer = null;

		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			ccsid = new CustomerCardSaleInfoDetails();
			ccsid.setReturnStatus(getFailedStatus("90101", custCIF));
			return ccsid;
		}

		logReference(custCIF);

		AuditHeader auditHeader = getAuditHeader(custCardSales, PennantConstants.TRAN_WF);
		AuditDetail auditDetail = customerCardSalesInfoService.doValidations(custCardSales,
				PennantConstants.RECORD_TYPE_NEW);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);
			ccsid = new CustomerCardSaleInfoDetails();
			ccsid.setReturnStatus(getFailedStatus(ed.getCode(), ed.getError()));
			return ccsid;
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.addCardSalesInformation(custCardSales, custCIF);

	}

	// FIXME >> MURTHY

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

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail ed = auditHeader.getErrorMessage().get(0);

			return getFailedStatus(ed.getCode(), ed.getError());
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
			String[] valueParm = new String[2];
			valueParm[0] = String.valueOf(customerChequeInfoDetail.getCustomerChequeInfo().getChequeSeq());
			valueParm[1] = customerChequeInfoDetail.getCif();
			return getFailedStatus("90117", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public CustomerDetails getCustomerAccountBehaviour(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		CustomerDetails response = new CustomerDetails();

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);

		if (customer == null) {
			response.setReturnStatus(getFailedStatus("90101", custCIF));
			response.setCustomer(null);
		}

		logger.debug(Literal.LEAVING);

		return customerDetailsController.getCustomerAccountBehaviour(custCIF);
	}

	@Override
	public WSReturnStatus deleteCustomerAccountBehaviour(CustomerChequeInfoDetail ccd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// bean validations
		validationUtility.validate(ccd, DeleteValidationGroup.class);

		// customer validations
		CustomerChequeInfo customerChequeInfo = null;
		if (StringUtils.isNotBlank(ccd.getCif())) {
			Customer customerDetails = customerDetailsService.getCustomerByCIF(ccd.getCif());
			if (customerDetails == null) {
				String[] valueParm = new String[1];
				valueParm[0] = ccd.getCif();
				return getFailedStatus("90101", valueParm);
			} else {
				customerChequeInfo = new CustomerChequeInfo();
				customerChequeInfo.setCustID(customerDetails.getCustID());
				customerChequeInfo.setChequeSeq(ccd.getChequeSeq());
				// for logging purpose
				logReference(ccd.getCif());
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
			valueParm[0] = String.valueOf(ccd.getChequeSeq());
			valueParm[1] = ccd.getCif();
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

	@Override
	public CustDedupResponse getNegativeListCustomer(CustDedupDetails cdd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		CustDedupResponse response = new CustDedupResponse();
		List<BlackListCustomers> duplicateList = new ArrayList<>();
		BlackListCustomers blackListCustomers = new BlackListCustomers();

		List<CustDedupRequest> dedupList = cdd.getDedupList();

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

		List<BlackListCustomers> negativeList = new ArrayList<>();
		List<DedupParm> dedupParmList = dedupParmDAO.getDedupParmByModule(FinanceConstants.DEDUP_BLACKLIST, custCtgCode,
				"");
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

		if (StringUtils.isBlank(coreBankId)) {
			validationUtility.fieldLevelException();
		}

		CustValidationResponse response = null;
		boolean status = customerDetailsService.getCustomerByCoreBankId(coreBankId);

		if (!status) {
			String[] valueParm = new String[1];
			valueParm[0] = "coreBank";
			response = new CustValidationResponse();
			response.setReturnStatus(getFailedStatus("90266", valueParm));
			return response;
		}

		response = new CustValidationResponse();
		Customer cust = customerDAO.getCustomerByCoreBankId(coreBankId, "");

		LimitHeader headerDetail = null;
		if (cust != null) {
			response.setCustomerPhoneNumber(
					customerPhoneNumberService.getApprovedCustomerPhoneNumberById(cust.getCustID()));
			response.setCustomerName(cust.getCustShrtName());
			response.setCif(cust.getCustCIF());

			headerDetail = limitDetailService.getLimitHeaderByCustomer(cust.getCustID());
		}

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

	}

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

	private CustomerExtendedFieldDetails getErrorMessage(CustomerExtendedFieldDetails cefd) {
		CustomerExtendedFieldDetails response = new CustomerExtendedFieldDetails();

		if (CollectionUtils.isEmpty(cefd.getErrorDetails())) {
			return response;
		}

		ErrorDetail ed = cefd.getErrorDetails().get(0);

		response.setReturnStatus(getFailedStatus(ed.getCode(), ed.getError()));

		return response;
	}

	private AuditHeader getAuditHeader(CustomerDetails aCustomerDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerDetails.getBefImage(), aCustomerDetails);
		return new AuditHeader(String.valueOf(aCustomerDetails.getCustID()),
				String.valueOf(aCustomerDetails.getCustID()), null, null, auditDetail,
				aCustomerDetails.getUserDetails(), new HashMap<>());
	}

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