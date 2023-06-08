package com.pennanttech.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

	private static final String ERR_90101 = "90101";

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
		if (CollectionUtils.isNotEmpty(phoneNumberList)) {
			CustomerPhoneNumber custPhone = phoneNumberList.stream()
					.sorted((pt1, pt2) -> Long.compare(pt1.getPhoneTypePriority(), pt2.getPhoneTypePriority())).toList()
					.get(0);
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
			response.setReturnStatus(getFailedStatus(ERR_90101, custCIF));

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
			return getFailedStatus(ERR_90101, custCIF);
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
			response.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
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
			return getFailedStatus(ERR_90101, cd.getCustCIF());
		}

		if (!StringUtils.equals(cd.getCustCtgCode(), customer.getCustCtgCode())) {
			return getFailedStatus("90599", cd.getCustCtgCode(), cd.getCustCIF());
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

	@Override
	public EmploymentDetail addCustomerEmployment(EmploymentDetail ed) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(ed, SaveValidationGroup.class);

		if (ed.getCustomerEmploymentDetail() == null) {
			EmploymentDetail response = new EmploymentDetail();
			response.setReturnStatus(getFailedStatus("90502", "employment"));
			return response;
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(ed.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(ed.getCif());
			if (customer == null) {
				EmploymentDetail response = new EmploymentDetail();
				response.setReturnStatus(getFailedStatus(ERR_90101, ed.getCif()));
				return response;
			}
		}

		logReference(ed.getCif());
		AuditHeader auditHeader = getAuditHeader(ed.getCustomerEmploymentDetail(), PennantConstants.TRAN_WF);

		AuditDetail auditDetail = customerEmploymentDetailService.doValidations(ed.getCustomerEmploymentDetail(),
				customer);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail error = auditHeader.getErrorMessage().get(0);
			EmploymentDetail response = new EmploymentDetail();
			response.setReturnStatus(getFailedStatus(error.getCode(), error.getError()));
			return response;
		}

		logger.debug(Literal.LEAVING);
		return customerController.addCustomerEmployment(ed.getCustomerEmploymentDetail(), ed.getCif());
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
			response.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
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
			return getFailedStatus(ERR_90101, ed.getCif());
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
			return getFailedStatus(ERR_90101, String.valueOf(custEmpId), ed.getCif());
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
			return getFailedStatus(ERR_90101, ed.getCif());
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
			response.setReturnStatus(getFailedStatus(ERR_90101, cif));
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
			response.setReturnStatus(getErrorDetails(ERR_90101, valueParm));
		} else {
			if (PennantConstants.PFF_CUSTCTG_INDIV.equals(customer.getCustCtgCode())) {
				response.setReturnStatus(getFailedStatus("90124", "director details",
						PennantConstants.PFF_CUSTCTG_CORP + "," + PennantConstants.PFF_CUSTCTG_SME));
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
			return getFailedStatus(ERR_90101, cdd.getCif());
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
			return getFailedStatus(ERR_90101, cdd.getCif());
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
			return getFailedStatus(ERR_90101, cpn.getCif());
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
			return getFailedStatus(ERR_90101, cpn.getCif());
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
			response.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
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
			return getFailedStatus(ERR_90101, cpn.getCif());
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
			return getFailedStatus(ERR_90101, ca.getCif());

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
			return getFailedStatus(ERR_90101, ca.getCif());
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
			response.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
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
			return getFailedStatus(ERR_90101, ca.getCif());
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
			return getFailedStatus(ERR_90101, custEMail.getCif());
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
			return getFailedStatus(ERR_90101, custEMail.getCif());
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
			response.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
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
			return getFailedStatus(ERR_90101, custCIF);
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
			return getFailedStatus(ERR_90101, custCIF);

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
			return getFailedStatus(ERR_90101, custCIF);
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
			response.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
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
			return getFailedStatus(ERR_90101, custCIF);
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
			cbd.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
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
			return getFailedStatus(ERR_90101, custCIF);
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
			response.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
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
			return getFailedStatus(ERR_90101, custCIF);
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
			custGST.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
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
			return getFailedStatus(ERR_90101, custCIF);
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
			response.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
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
			return getFailedStatus(ERR_90101, custCIF);
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
			ccsid.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
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

	@Override
	public CustomerDetails getCardSalesInformation(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
		if (customer == null) {
			CustomerDetails response = new CustomerDetails();

			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails(ERR_90101, valueParm));
			response.setCustomer(null);

			return response;
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.getCardSalesInformation(custCIF);
	}

	@Override
	public WSReturnStatus updateCardSaleInformation(CustomerCardSaleInfoDetails cardSalesInfo) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cardSalesInfo, UpdateValidationGroup.class);

		CustCardSales custCardSales = cardSalesInfo.getCustCardSales();

		if (custCardSales == null) {
			return getFailedStatus("90502", "customerCardSalesInfo");
		}

		String custCIF = cardSalesInfo.getCif();

		if (StringUtils.isNotBlank(custCIF) && customerDetailsService.getCustomerByCIF(custCIF) == null) {
			return getFailedStatus(ERR_90101, custCIF);
		}

		logReference(custCIF);
		AuditHeader ah = getAuditHeader(custCardSales, PennantConstants.TRAN_WF);
		AuditDetail ad = customerCardSalesInfoService.doValidations(custCardSales, PennantConstants.RECORD_TYPE_UPD);

		ah.setAuditDetail(ad);
		ah.setErrorList(ad.getErrorDetails());

		if (CollectionUtils.isNotEmpty(ah.getErrorMessage())) {
			ErrorDetail ed = ah.getErrorMessage().get(0);
			return getFailedStatus(ed.getCode(), ed.getError());
		}

		long custID = custCardSales.getCustID();
		long cardSalesID = custCardSales.getId();

		CustCardSales custCardSalesInfo = customerCardSalesInfoService.getCustomerCardSalesInfoById(cardSalesID);

		if (custCardSalesInfo != null) {
			return customerDetailsController.updateCardSalestInformation(custCardSales, custCIF);
		}

		return getFailedStatus("90116", String.valueOf(custID), custCIF);
	}

	@Override
	public WSReturnStatus deleteCardSaleInformation(CustomerCardSaleInfoDetails cardSalesInfo) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cardSalesInfo, DeleteValidationGroup.class);

		long cardSalesID = cardSalesInfo.getId();
		String custCIF = cardSalesInfo.getCif();

		if (StringUtils.isNotBlank(custCIF)) {
			Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
			if (customer == null) {
				return getFailedStatus(ERR_90101, custCIF);
			}

			CustCardSales custCardSales = new CustCardSales();
			custCardSales.setCustID(customer.getCustID());
			custCardSales.setId(cardSalesID);
			logReference(custCIF);
		}

		CustCardSales custCardSalesInfo = customerCardSalesInfoService.getCustomerCardSalesInfoById(cardSalesID);

		if (custCardSalesInfo != null) {
			return customerDetailsController.deleteCardSaleInformation(custCardSalesInfo);
		}

		logger.debug(Literal.LEAVING);
		return getFailedStatus("90116", String.valueOf(cardSalesID), custCIF);
	}

	@Override
	public CustomerChequeInfoDetail addCustomerAccountBehaviour(CustomerChequeInfoDetail chequeInfo)
			throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(chequeInfo, SaveValidationGroup.class);

		CustomerChequeInfo custChequeInfo = chequeInfo.getCustomerChequeInfo();
		if (custChequeInfo == null) {
			CustomerChequeInfoDetail response = new CustomerChequeInfoDetail();
			response.setReturnStatus(getFailedStatus("90502", "accountBehaviour"));
			return response;
		}

		String custCIF = chequeInfo.getCif();
		if (StringUtils.isNotBlank(custCIF)) {
			Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
			if (customer == null) {
				CustomerChequeInfoDetail response = new CustomerChequeInfoDetail();
				response.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
				return response;
			}
		}

		logReference(custCIF);
		logger.debug(Literal.LEAVING);
		return customerDetailsController.addCustomerAccountBehaviour(custChequeInfo, custCIF);
	}

	@Override
	public WSReturnStatus updateCustomerAccountBehaviour(CustomerChequeInfoDetail chequeInfo) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(chequeInfo, UpdateValidationGroup.class);

		CustomerChequeInfo customerChequeInfo = chequeInfo.getCustomerChequeInfo();
		if (customerChequeInfo == null) {
			return getFailedStatus("90502", "accountBehaviour");
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(chequeInfo.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(chequeInfo.getCif());
		}

		if (customer == null) {
			return getFailedStatus(ERR_90101, chequeInfo.getCif());
		}

		logReference(chequeInfo.getCif());

		int chequeSeq = customerChequeInfo.getChequeSeq();
		CustomerChequeInfo custCheqInfo = customerChequeInfoDAO.getCustomerChequeInfoById(customer.getCustID(),
				chequeSeq, "");

		if (custCheqInfo == null) {
			return getFailedStatus("90117", String.valueOf(chequeSeq), chequeInfo.getCif());
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.updateCustomerAccountBehaviour(customerChequeInfo, chequeInfo.getCif());
	}

	@Override
	public CustomerDetails getCustomerAccountBehaviour(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);

		if (customer == null) {
			CustomerDetails response = new CustomerDetails();
			response.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
			response.setCustomer(null);
			return response;
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.getCustomerAccountBehaviour(custCIF);
	}

	@Override
	public WSReturnStatus deleteCustomerAccountBehaviour(CustomerChequeInfoDetail ccd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(ccd, DeleteValidationGroup.class);

		Customer customer = null;
		String custCIF = ccd.getCif();

		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			return getFailedStatus(ERR_90101, custCIF);
		}

		long custID = customer.getCustID();

		CustomerChequeInfo custCheque = new CustomerChequeInfo();
		custCheque.setCustID(custID);
		custCheque.setChequeSeq(ccd.getChequeSeq());

		logReference(custCIF);

		CustomerChequeInfo custChequeInfo = customerChequeInfoDAO.getCustomerChequeInfoById(custID,
				custCheque.getChequeSeq(), "");

		if (custChequeInfo == null) {
			return getFailedStatus("90117", String.valueOf(ccd.getChequeSeq()), custCIF);
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.deleteCustomerAccountBehaviour(custCheque);
	}

	@Override
	public CustomerExtLiabilityDetail addCustomerExternalLiability(CustomerExtLiabilityDetail liabilityDetail)
			throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(liabilityDetail, SaveValidationGroup.class);

		CustomerExtLiability liability = liabilityDetail.getExternalLiability();
		if (liability == null) {
			CustomerExtLiabilityDetail response = new CustomerExtLiabilityDetail();
			response.setReturnStatus(getFailedStatus("90502", "customerExtLiability"));
			return response;
		}

		String custCIF = liabilityDetail.getCif();

		if (StringUtils.isNotBlank(custCIF)) {
			Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
			if (customer == null) {
				CustomerExtLiabilityDetail response = new CustomerExtLiabilityDetail();
				response.setReturnStatus(getFailedStatus(ERR_90101, custCIF));
				return response;
			}
		}

		logReference(custCIF);

		AuditHeader ah = getAuditHeader(liability, PennantConstants.TRAN_WF);
		CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		AuditDetail ad = validation.doValidations(liability);

		ah.setAuditDetail(ad);
		ah.setErrorList(ad.getErrorDetails());

		if (CollectionUtils.isNotEmpty(ah.getErrorMessage())) {
			CustomerExtLiabilityDetail response = new CustomerExtLiabilityDetail();
			ErrorDetail ed = ah.getErrorMessage().get(0);
			response.setReturnStatus(getFailedStatus(ed.getCode(), ed.getError()));
			return response;
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.addCustomerExternalLiability(liability, custCIF);
	}

	@Override
	public WSReturnStatus updateCustomerExternalLiability(CustomerExtLiabilityDetail liabilityDetail)
			throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(liabilityDetail, UpdateValidationGroup.class);

		CustomerExtLiability externalLiability = liabilityDetail.getExternalLiability();
		if (externalLiability == null) {
			return getFailedStatus("90502", "customerExtLiability");
		}

		Customer customer = null;
		if (StringUtils.isNotBlank(liabilityDetail.getCif())) {
			customer = customerDetailsService.getCustomerByCIF(liabilityDetail.getCif());
		}

		if (customer == null) {
			return getFailedStatus(ERR_90101, liabilityDetail.getCif());
		}

		logReference(liabilityDetail.getCif());
		AuditHeader ah = getAuditHeader(externalLiability, PennantConstants.TRAN_WF);

		CustomerExtLiabilityValidation validation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		AuditDetail ad = validation.doValidations(externalLiability);

		ah.setAuditDetail(ad);
		ah.setErrorList(ad.getErrorDetails());

		if (CollectionUtils.isNotEmpty(ah.getErrorMessage())) {
			ErrorDetail ed = ah.getErrorMessage().get(0);
			return getFailedStatus(ed.getCode(), ed.getError());
		}

		CustomerExtLiability liability = new CustomerExtLiability();
		liability.setCustId(customer.getCustID());
		liability.setSeqNo(externalLiability.getSeqNo());
		liability.setLinkId(customerExtLiabilityDAO.getLinkId(customer.getCustID()));
		liability = customerExtLiabilityService.getLiability(liability);

		if (liability == null) {
			return getFailedStatus("90118", String.valueOf(externalLiability.getSeqNo()), liabilityDetail.getCif());
		}

		externalLiability.setId(liability.getId());

		logger.debug(Literal.LEAVING);
		return customerDetailsController.updateCustomerExternalLiability(externalLiability, liabilityDetail.getCif());
	}

	@Override
	public CustomerDetails getCustomerExternalLiabilities(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		if (customerDetailsService.getCustomerByCIF(custCIF) == null) {
			String[] valueParm = new String[1];
			valueParm[0] = custCIF;

			CustomerDetails response = new CustomerDetails();
			response.setReturnStatus(getErrorDetails(ERR_90101, valueParm));
			response.setCustomer(null);

			return response;
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.getCustomerExternalLiabilities(custCIF);
	}

	@Override
	public WSReturnStatus deleteCustomerExternalLiability(CustomerExtLiabilityDetail liabilityDetail)
			throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(liabilityDetail, DeleteValidationGroup.class);

		String custCIF = liabilityDetail.getCif();

		Customer customer = null;
		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			return getFailedStatus(ERR_90101, custCIF);
		}

		CustomerExtLiability extLiability = new CustomerExtLiability();
		extLiability.setCustId(customer.getCustID());
		extLiability.setSeqNo(liabilityDetail.getLiabilitySeq());
		logReference(custCIF);

		CustomerExtLiability liability = new CustomerExtLiability();
		liability.setCustId(extLiability.getCustId());
		liability.setSeqNo(liabilityDetail.getLiabilitySeq());
		liability.setLinkId(customerExtLiabilityDAO.getLinkId(extLiability.getCustId()));

		CustomerExtLiability custExtLiability = customerExtLiabilityService.getLiability(liability);

		if (custExtLiability == null) {
			return getFailedStatus("90118", String.valueOf(liabilityDetail.getLiabilitySeq()), custCIF);
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.deleteCustomerExternalLiability(liability);
	}

	@Override
	public WSReturnStatus addCustomerDocument(CustomerDocumentDetail cdd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cdd, SaveValidationGroup.class);

		if (cdd.getCustomerDocument() == null) {
			return getFailedStatus("90502", "document");
		}

		Customer customer = null;
		String custCIF = cdd.getCif();
		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			return getFailedStatus(ERR_90101, custCIF);
		}

		logReference(custCIF);

		AuditHeader ah = getAuditHeader(cdd.getCustomerDocument(), PennantConstants.TRAN_WF);
		AuditDetail ad = customerDocumentService.validateCustomerDocuments(cdd.getCustomerDocument(), customer);

		ah.setAuditDetail(ad);
		ah.setErrorList(ad.getErrorDetails());

		if (CollectionUtils.isNotEmpty(ah.getErrorMessage())) {
			ErrorDetail ed = ah.getErrorMessage().get(0);
			return getFailedStatus(ed.getCode(), ed.getError());
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.addCustomerDocument(cdd.getCustomerDocument(), custCIF);
	}

	@Override
	public WSReturnStatus updateCustomerDocument(CustomerDocumentDetail cdd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cdd, UpdateValidationGroup.class);

		CustomerDocument document = cdd.getCustomerDocument();
		if (document == null) {
			return getFailedStatus("90502", "document");
		}

		String custCIF = cdd.getCif();

		Customer customer = null;

		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			return getFailedStatus(ERR_90101, custCIF);
		}

		long custID = customer.getCustID();
		logReference(custCIF);

		AuditHeader ah = getAuditHeader(document, PennantConstants.TRAN_WF);
		AuditDetail ad = customerDocumentService.validateCustomerDocuments(document, customer);

		ah.setAuditDetail(ad);
		ah.setErrorList(ad.getErrorDetails());

		if (CollectionUtils.isNotEmpty(ah.getErrorMessage())) {
			ErrorDetail ed = ah.getErrorMessage().get(0);
			return getFailedStatus(ed.getCode(), ed.getError());
		}

		String custDocCategory = document.getCustDocCategory();
		CustomerDocument custDoc = customerDocumentService.getApprovedCustomerDocumentById(custID, custDocCategory);

		if (custDoc == null) {
			return getFailedStatus("90119", custDocCategory, custCIF);
		}

		logger.debug(Literal.LEAVING);
		document.setID(custDoc.getID());
		return customerDetailsController.updateCustomerDocument(document, custCIF);
	}

	@Override
	public CustomerDetails getCustomerDocuments(String custCIF) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(custCIF)) {
			validationUtility.fieldLevelException();
		}

		logReference(custCIF);

		if (customerDetailsService.getCustomerByCIF(custCIF) == null) {
			CustomerDetails response = new CustomerDetails();

			String[] valueParm = new String[1];
			valueParm[0] = custCIF;
			response.setReturnStatus(getErrorDetails(ERR_90101, valueParm));
			response.setCustomer(null);

			return response;
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.getCustomerDocuments(custCIF);
	}

	@Override
	public WSReturnStatus deleteCustomerDocument(CustomerDocumentDetail cdd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(cdd, DeleteValidationGroup.class);

		Customer customer = null;
		String custCIF = cdd.getCif();

		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
		}

		if (customer == null) {
			return getFailedStatus(ERR_90101, custCIF);
		}

		CustomerDocument cd = new CustomerDocument();
		cd.setCustID(customer.getCustID());
		String docCategory = cdd.getCustDocCategory();
		cd.setCustDocCategory(docCategory);
		logReference(custCIF);

		CustomerDocument prvCD = customerDocumentService.getApprovedCustomerDocumentById(cd.getCustID(),
				cd.getCustDocCategory());
		if (prvCD == null) {
			return getFailedStatus("90119", docCategory, custCIF);
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.deleteCustomerDocument(cd);
	}

	private CustomerDedup doSetCustomerDedup(CustomerDetails cd) {
		logger.debug(Literal.ENTERING);

		String mobileNumber = "";
		String mailId = "";
		Customer customer = cd.getCustomer();
		List<CustomerPhoneNumber> phones = cd.getCustomerPhoneNumList();
		List<CustomerEMail> mails = cd.getCustomerEMailList();

		if (CollectionUtils.isNotEmpty(phones)) {
			CustomerPhoneNumber phone = phones.stream()
					.sorted((p1, p2) -> Integer.compare(p1.getPhoneTypePriority(), p2.getPhoneTypePriority())).toList()
					.get(0);

			mobileNumber = PennantApplicationUtil.formatPhoneNumber(phone.getPhoneCountryCode(),
					phone.getPhoneAreaCode(), phone.getPhoneNumber());
		}

		if (CollectionUtils.isNotEmpty(mails)) {
			CustomerEMail custMail = mails.stream()
					.sorted((m1, m2) -> Integer.compare(m1.getCustEMailPriority(), m2.getCustEMailPriority())).toList()
					.get(0);

			mailId = custMail.getCustEMail();
		}

		String panNumber = PennantApplicationUtil.getPanNumber(cd.getCustomerDocumentsList());

		if (StringUtils.isNotBlank(panNumber)) {
			cd.getCustomer().setCustCRCPR(panNumber);
		}

		CustomerDedup dedup = new CustomerDedup();
		dedup.setCustFName(customer.getCustFName());
		dedup.setCustLName(customer.getCustLName());
		dedup.setCustShrtName(customer.getCustShrtName());
		dedup.setCustDOB(customer.getCustDOB());
		dedup.setCustCRCPR(customer.getCustCRCPR());
		dedup.setCustCtgCode(customer.getCustCtgCode());
		dedup.setCustDftBranch(customer.getCustDftBranch());
		dedup.setCustSector(customer.getCustSector());
		dedup.setCustSubSector(customer.getCustSubSector());
		dedup.setCustNationality(customer.getCustNationality());
		dedup.setCustPassportNo(customer.getCustPassportNo());
		dedup.setCustTradeLicenceNum(customer.getCustTradeLicenceNum());
		dedup.setCustVisaNum(customer.getCustVisaNum());
		dedup.setCustPOB(customer.getCustPOB());
		dedup.setCustResdCountry(customer.getCustResdCountry());
		dedup.setMobileNumber(mobileNumber);
		dedup.setCustEMail(mailId);

		if (ImplementationConstants.CUSTOMER_PAN_VALIDATION_STOP) {
			setUCIC(cd, dedup);
		}

		logger.debug(Literal.LEAVING);
		return dedup;
	}

	@Override
	public AgreementData getCustomerAgreement(AgreementRequest agrRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);

		try {
			if (StringUtils.isBlank(agrRequest.getCif())) {
				AgreementData agrData = new AgreementData();
				agrData.setReturnStatus(getFailedStatus("90502", "CIF"));

				return agrData;
			}

			if (StringUtils.isBlank(agrRequest.getAgreementType())) {
				AgreementData agrData = new AgreementData();
				agrData.setReturnStatus(getFailedStatus("90502", "AgreementType"));

				return agrData;
			}

			logReference(agrRequest.getCif());

			if (!APIConstants.CUST_AGR_NAME.equals(agrRequest.getAgreementType())) {
				AgreementData agrData = new AgreementData();
				agrData.setReturnStatus(getFailedStatus("90298", APIConstants.CUST_AGR_NAME, "AgreementType"));

				return agrData;
			}

			Customer customer = customerDetailsService.getCustomerByCIF(agrRequest.getCif());
			if (customer == null) {
				AgreementData agrData = new AgreementData();
				agrData.setReturnStatus(getFailedStatus(ERR_90101, agrRequest.getCif()));

				return agrData;
			}

			return customerController.getCustomerAgreement(customer.getCustID());
		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			AgreementData agrData = new AgreementData();
			agrData.setReturnStatus(getFailedStatus());

			return agrData;
		}
	}

	@Override
	public ProspectCustomerDetails getDedupCustomer(ProspectCustomerDetails customer) {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(customer, ProspectCustDetailsGroup.class);

		String custCtgCode = customer.getCustCtgCode();

		if (!customerCategoryDAO.isCustCtgExist(custCtgCode, "")) {
			ProspectCustomerDetails response = new ProspectCustomerDetails();
			String[] valueParm = new String[2];
			valueParm[0] = "CustCtg";
			valueParm[1] = custCtgCode;
			response.setReturnStatus(getErrorDetails("90224", valueParm));
			return response;
		}

		logger.debug(Literal.LEAVING);
		return customerController.getDedupCustomer(customer);
	}

	@Override
	public WSReturnStatus addCreditReviewDetails(FinCreditReviewDetailsData fcrd) {
		logger.debug(Literal.ENTERING);

		Customer customer = null;

		if (StringUtils.isBlank(fcrd.getCif())) {
			return getFailedStatus("90502", "Cif");
		}

		customer = customerDetailsService.getCustomerByCIF(fcrd.getCif());

		if (customer == null) {
			return getFailedStatus(ERR_90101, fcrd.getCif());
		}

		for (FinCreditReviewDetails detail : fcrd.getFinCreditReviewDetails()) {
			detail.setCreditRevCode(customer.getCustCtgCode());
			detail.setCustomerId(customer.getCustID());

			if (StringUtils.isBlank(detail.getCurrency())) {
				detail.setCurrency("INR");
			}

			WSReturnStatus wsReturnStatus = validateCRDetails(detail);

			if (wsReturnStatus != null) {
				return wsReturnStatus;
			}

			if (!isValidAuditType(detail.getAuditType())) {
				String errorDesc = FacilityConstants.CREDITREVIEW_AUDITED + ","
						+ FacilityConstants.CREDITREVIEW_UNAUDITED + "," + FacilityConstants.CREDITREVIEW_MNGRACNTS;
				return getFailedStatus("90281", "AuditType", errorDesc);
			}

			wsReturnStatus = validateCRSummaryEntries(detail);

			if (wsReturnStatus != null) {
				return wsReturnStatus;
			}
		}

		logger.debug(Literal.LEAVING);
		return customerController.doAddCreditReviewDetails(fcrd);
	}

	@Override
	public CustDedupResponse getCustDedup(CustDedupDetails custDedupDetails) throws ServiceException {
		logger.debug(Literal.ENTERING);

		CustDedupResponse response = new CustDedupResponse();
		CustomerDedup dedup = new CustomerDedup();
		List<CustDedupRequest> dedupList = custDedupDetails.getDedupList();

		if (CollectionUtils.isEmpty(dedupList) || dedupList.size() < 2) {
			response.setReturnStatus(getFailedStatus("30507", "Request", " two fields"));
			return response;
		}

		String custCtgCode = "";
		for (CustDedupRequest detail : dedupList) {
			if (StringUtils.equalsIgnoreCase(detail.getName(), "CustCtgCode")) {
				custCtgCode = String.valueOf(detail.getValue());
				break;
			}
		}

		if (StringUtils.isBlank(custCtgCode)) {
			response.setReturnStatus(getFailedStatus("90502", "CategoryCode"));
			return response;
		}

		if (!customerCategoryDAO.isCustCtgExist(custCtgCode, "")) {
			String[] valueParm = new String[2];
			valueParm[0] = "CustCtg";
			valueParm[1] = custCtgCode;
			response.setReturnStatus(getErrorDetails("90224", valueParm));

			return response;
		}

		List<BuilderTable> fieldList = dedupFieldsDAO.getFieldList(custCtgCode.concat("Customer"));
		List<String> fieldNamesList = fieldList.stream().map(d -> d.getFieldName()).toList();

		for (CustDedupRequest feild : dedupList) {
			if (StringUtils.isBlank(feild.getName())) {
				response.setReturnStatus(getFailedStatus("90502", "name"));
				return response;
			}

			if (StringUtils.isBlank(String.valueOf(feild.getValue()))) {
				response.setReturnStatus(getFailedStatus("90502", "value"));
				return response;
			}

			validateFieldNames(response, dedup, fieldNamesList, feild);

			if (response.getReturnStatus() != null) {
				return response;
			}
		}

		List<CustomerDedup> resDedupList = getCustomerDedupList(dedup, custCtgCode);

		if (CollectionUtils.isNotEmpty(resDedupList)) {
			response.setDedupList(resDedupList);
			response.setReturnStatus(getSuccessStatus());
		} else {
			response.setReturnStatus(getSuccessStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	private List<CustomerDedup> getCustomerDedupList(CustomerDedup dedup, String custCtgCode) {
		List<CustomerDedup> duplicateList = new ArrayList<>();

		List<CustomerDedup> resDedupList = new ArrayList<>();
		List<DedupParm> dedupParmList = dedupParmDAO.getDedupParmByModule(FinanceConstants.DEDUP_CUSTOMER, custCtgCode,
				"");

		for (DedupParm dedupParm : dedupParmList) {
			List<CustomerDedup> list = customerDedupDAO.fetchCustomerDedupDetails(dedup, dedupParm.getSQLQuery());
			duplicateList.addAll(list);

			for (CustomerDedup customerDedup : resDedupList) {
				for (CustomerDedup dupCustDedup : duplicateList) {
					if (StringUtils.equalsIgnoreCase(customerDedup.getCustCIF(), dupCustDedup.getCustCIF())) {
						list.remove(dupCustDedup);
					}
				}
			}

			resDedupList.addAll(list);
			duplicateList.clear();
		}
		return resDedupList;
	}

	@Override
	public CustDedupResponse getNegativeListCustomer(CustDedupDetails cdd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		CustDedupResponse response = new CustDedupResponse();
		List<BlackListCustomers> duplicateList = new ArrayList<>();
		BlackListCustomers blackListCustomers = new BlackListCustomers();

		List<CustDedupRequest> dedupList = cdd.getDedupList();

		if (CollectionUtils.isEmpty(dedupList) || dedupList.size() < 2) {
			response.setReturnStatus(getFailedStatus("30507", "Request", " two fields"));
			return response;
		}

		String custCtgCode = null;
		for (CustDedupRequest detail : dedupList) {
			if (StringUtils.equalsIgnoreCase(detail.getName(), "CustCtgCode")) {
				custCtgCode = String.valueOf(detail.getValue());
				break;
			}
		}

		if (StringUtils.isBlank(custCtgCode)) {
			response.setReturnStatus(getFailedStatus("90502", "CategoryCode"));
			return response;
		}

		if (!customerCategoryDAO.isCustCtgExist(custCtgCode, "")) {
			String[] valueParm = new String[2];
			valueParm[0] = "CustCtg";
			valueParm[1] = custCtgCode;
			response.setReturnStatus(getErrorDetails("90224", valueParm));
			return response;
		}

		List<BuilderTable> fieldList = dedupFieldsDAO.getFieldList(custCtgCode.concat("BlackList"));
		List<String> fieldNamesList = fieldList.stream().map(d -> d.getFieldName()).toList();

		for (CustDedupRequest feild : dedupList) {
			if (StringUtils.isBlank(feild.getName())) {
				response.setReturnStatus(getFailedStatus("90502", "name"));
				return response;
			}

			if (StringUtils.isBlank(String.valueOf(feild.getValue()))) {
				response.setReturnStatus(getFailedStatus("90502", "value"));
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
							response.setReturnStatus(getFailedStatus("41002", feild.getName(), "Date"));
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
				response.setReturnStatus(getFailedStatus("41002", "field name"));
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
	public List<CustomerDetails> getSRMCustDetails(SRMCustRequest srmCust) throws ServiceException {
		logger.debug(Literal.ENTERING);

		List<CustomerDetails> cdDetails = new ArrayList<>();
		CustomerDetails response = new CustomerDetails();
		response.setCustomer(null);

		String srmSource = srmCust.getSource();
		if (StringUtils.isBlank(srmSource)) {
			response.setReturnStatus(getFailedStatus("90502", "Source"));
			cdDetails.add(response);

			return cdDetails;
		}

		if (!APIConstants.SRM_SOURCE.equals(srmSource) && !APIConstants.COB_SOURCE.equalsIgnoreCase(srmSource)) {
			response.setReturnStatus(getFailedStatus("90337", "SOURCE", "SRM or COB"));
			cdDetails.add(response);

			return cdDetails;
		}

		if (APIConstants.SRM_SOURCE.equals(srmSource) && fieldValueNotProvided(srmCust)) {
			response.setReturnStatus(getFailedStatus("90502", "Any one field value"));
			cdDetails.add(response);

			return cdDetails;
		}

		if (APIConstants.COB_SOURCE.equals(srmSource) && StringUtils.isBlank(srmCust.getCustCRCPR())) {
			response.setReturnStatus(getFailedStatus("90502", "panNumber"));
			cdDetails.add(response);

			return cdDetails;
		}

		List<Long> custIdList = customerDAO.getCustomerDetailsBySRM(srmCust);
		if (CollectionUtils.isEmpty(custIdList)) {
			response.setReturnStatus(getFailedStatus("90266", "given request"));
			cdDetails.add(response);

			return cdDetails;
		}

		for (Long custId : custIdList) {
			response = customerController.getCustomerDetails(custId);

			List<CustomerFinanceDetail> custFD = approvalStatusEnquiryDAO.getListOfCustomerFinanceDetailById(custId,
					"_AView", false);

			List<CustomerFinanceDetail> finances = response.getCustomerFinanceDetailList();
			if (finances != null) {
				finances.addAll(custFD);
			}

			if (CollectionUtils.isNotEmpty(finances)) {
				finances.forEach(cfd -> cfd.setJointAccountDetails(
						jointAccountDetailDAO.getJointAccountDetailByFinRef(cfd.getFinID(), "_View")));

				for (CustomerFinanceDetail cfd : finances) {
					cfd.setStage(cfd.getNextRoleCode());
					cfd.setCurOddays(financeProfitDetailDAO.getCurOddays(cfd.getFinID()));
				}
			}

			cdDetails.add(response);
		}

		logger.debug(Literal.LEAVING);
		return cdDetails;
	}

	private boolean fieldValueNotProvided(SRMCustRequest srmCust) {
		return StringUtils.isBlank(srmCust.getCustCif()) && StringUtils.isBlank(srmCust.getPhoneNumber())
				&& StringUtils.isBlank(srmCust.getFinReference()) && StringUtils.isBlank(srmCust.getCustCRCPR())
				&& StringUtils.isBlank(srmCust.getCustShrtName()) && srmCust.getCustDOB() == null;
	}

	@Override
	public CustValidationResponse doCustomerValidation(String coreBankId) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(coreBankId)) {
			validationUtility.fieldLevelException();
		}

		if (!customerDetailsService.getCustomerByCoreBankId(coreBankId)) {
			CustValidationResponse response = new CustValidationResponse();
			response.setReturnStatus(getFailedStatus("90266", "coreBank"));
			return response;
		}

		CustValidationResponse response = new CustValidationResponse();
		Customer cust = customerDAO.getCustomerByCoreBankId(coreBankId, "");

		if (cust == null) {
			return new CustValidationResponse();
		}

		response.setCustomerPhoneNumber(
				customerPhoneNumberService.getApprovedCustomerPhoneNumberById(cust.getCustID()));
		response.setCustomerName(cust.getCustShrtName());
		response.setCif(cust.getCustCIF());

		LimitHeader headerDetail = limitDetailService.getLimitHeaderByCustomer(cust.getCustID());

		if (headerDetail == null) {
			return response;
		}

		List<LimitDetails> limits = headerDetail.getCustomerLimitDetailsList();

		for (LimitDetails detail : limits) {
			if (LimitConstants.LIMIT_ITEM_TOTAL.equals(detail.getGroupCode())) {
				response.setActualLimit(PennantApplicationUtil.formateAmount(
						detail.getLimitSanctioned().subtract(detail.getUtilisedLimit()),
						CurrencyUtil.getFormat(headerDetail.getLimitCcy())));
				response.setExpiryDate(detail.getExpiryDate());
			}

			response.setBlocklimit(headerDetail.isBlocklimit());
		}

		response.setReturnStatus(getSuccessStatus());
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public CustomerExtendedFieldDetails addCustomerExtendedFieldDetails(
			CustomerExtendedFieldDetails custExtendedDetails) throws ServiceException {
		logger.debug(Literal.ENTERING);
		CustomerExtendedFieldDetails response = new CustomerExtendedFieldDetails();

		validationUtility.validate(custExtendedDetails, SaveValidationGroup.class);

		if (CollectionUtils.isEmpty(custExtendedDetails.getExtendedDetails())) {
			response.setReturnStatus(getFailedStatus("90502", "extendedDetails"));
			return response;
		}

		if (StringUtils.isBlank(custExtendedDetails.getCif())) {
			response.setReturnStatus(getFailedStatus("90502", "cif"));
			return response;
		}

		Customer customerDetails = customerDetailsService.getCustomerByCIF(custExtendedDetails.getCif());
		if (customerDetails == null) {
			response.setReturnStatus(getFailedStatus(ERR_90101, custExtendedDetails.getCif()));
			return response;
		}

		List<ErrorDetail> errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(
				custExtendedDetails.getExtendedDetails(), ExtendedFieldConstants.MODULE_CUSTOMER,
				customerDetails.getCustCtgCode(), "");

		if (!errorDetails.isEmpty()) {
			response.setErrorDetails(errorDetails);
			return getErrorMessage(response);
		}

		logger.debug(Literal.LEAVING);
		return customerDetailsController.addCustomerExtendedFields(custExtendedDetails, customerDetails);
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

	private AuditHeader getAuditHeader(CustomerEmploymentDetail aCustomerEmploymentDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerEmploymentDetail.getBefImage(),
				aCustomerEmploymentDetail);
		return new AuditHeader(String.valueOf(aCustomerEmploymentDetail.getCustID()),
				String.valueOf(aCustomerEmploymentDetail.getCustID()), null, null, auditDetail,
				aCustomerEmploymentDetail.getUserDetails(), new HashMap<>());
	}

	private AuditHeader getAuditHeader(CustomerAddres aCustomerAddres, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerAddres.getBefImage(), aCustomerAddres);
		return new AuditHeader(String.valueOf(aCustomerAddres.getCustID()), String.valueOf(aCustomerAddres.getCustID()),
				null, null, auditDetail, aCustomerAddres.getUserDetails(), new HashMap<>());
	}

	private AuditHeader getAuditHeader(CustomerEMail aCustomerEMail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerEMail.getBefImage(), aCustomerEMail);
		return new AuditHeader(String.valueOf(aCustomerEMail.getCustID()), String.valueOf(aCustomerEMail.getCustID()),
				null, null, auditDetail, aCustomerEMail.getUserDetails(), new HashMap<>());
	}

	private AuditHeader getAuditHeader(CustomerIncome aCustomerIncome, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerIncome.getBefImage(), aCustomerIncome);
		return new AuditHeader(String.valueOf(aCustomerIncome.getCustId()), String.valueOf(aCustomerIncome.getCustId()),
				null, null, auditDetail, aCustomerIncome.getUserDetails(), new HashMap<>());
	}

	private AuditHeader getAuditHeader(CustomerBankInfo aCustomerBankInfo, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerBankInfo.getBefImage(), aCustomerBankInfo);
		return new AuditHeader(String.valueOf(aCustomerBankInfo.getCustID()),
				String.valueOf(aCustomerBankInfo.getCustID()), null, null, auditDetail,
				aCustomerBankInfo.getUserDetails(), new HashMap<>());
	}

	private AuditHeader getAuditHeader(CustCardSales aCustCardSales, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustCardSales.getBefImage(), aCustCardSales);
		return new AuditHeader(String.valueOf(aCustCardSales.getCustID()), String.valueOf(aCustCardSales.getCustID()),
				null, null, auditDetail, aCustCardSales.getUserDetails(), new HashMap<>());
	}

	private AuditHeader getAuditHeader(CustomerGST aCustomerGST, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerGST.getBefImage(), aCustomerGST);
		return new AuditHeader(String.valueOf(aCustomerGST.getCustId()), String.valueOf(aCustomerGST.getCustId()), null,
				null, auditDetail, aCustomerGST.getUserDetails(), new HashMap<>());
	}

	private AuditHeader getAuditHeader(CustomerExtLiability externalLiability, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, externalLiability.getBefImage(), externalLiability);
		return new AuditHeader(String.valueOf(externalLiability.getCustId()),
				String.valueOf(externalLiability.getCustId()), null, null, auditDetail,
				externalLiability.getUserDetails(), new HashMap<>());
	}

	private AuditHeader getAuditHeader(CustomerDocument aCustomerDocument, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerDocument.getBefImage(), aCustomerDocument);
		return new AuditHeader(String.valueOf(aCustomerDocument.getCustID()),
				String.valueOf(aCustomerDocument.getCustID()), null, null, auditDetail,
				aCustomerDocument.getUserDetails(), new HashMap<>());
	}

	private AuditHeader getAuditHeader(DirectorDetail directorDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, directorDetail.getBefImage(), directorDetail);
		return new AuditHeader(String.valueOf(directorDetail.getCustID()), String.valueOf(directorDetail.getCustID()),
				null, null, auditDetail, directorDetail.getUserDetails(), new HashMap<>());
	}

	public WSReturnStatus getErrorDetails(String errorCode, String[] valueParm) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = getFailedStatus(errorCode, valueParm);

		if (StringUtils.isBlank(response.getReturnCode())) {
			return getFailedStatus(APIConstants.RES_FAILED_CODE, APIConstants.RES_FAILED_DESC);
		}

		return response;
	}

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

	private String[] getCustomerLogDetails(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);

		String[] logFields = null;
		if (customerDetails != null) {
			logFields = new String[3];
			logFields[0] = customerDetails.getCustCtgCode();
			logFields[1] = customerDetails.getCustDftBranch();

			List<CustomerPhoneNumber> customerPhoneNumbers = customerDetails.getCustomerPhoneNumList();
			if (CollectionUtils.isNotEmpty(customerPhoneNumbers)) {
				logFields[2] = customerPhoneNumbers.get(0).getPhoneNumber();
			}
		}

		logger.debug(Literal.LEAVING);
		return logFields;
	}

	private WSReturnStatus validateCustomerCIF(String custCIF) {
		if (StringUtils.isNotBlank(custCIF)) {
			if (customerDetailsService.getCustomerCountByCIF(custCIF, "") == 0) {
				return getFailedStatus(ERR_90101, custCIF);
			}

			if (customerDetailsService.getCustomerCountByCIF(custCIF, "_Temp") > 0) {
				return getFailedStatus("90248", custCIF);
			}
		}

		return null;
	}

	private WSReturnStatus validateCustomerCatageory(CustomerDetails customerDetails) {
		WSReturnStatus returnStatus = null;

		Customer customer = customerDetailsService.getCustomerByCIF(customerDetails.getCustCIF());

		if (customer != null) {
			if (StringUtils.isNotEmpty(customerDetails.getCustCtgCode())) {
				return getFailedStatus("90329", "categoryCode", "update Customer");
			}

			customerDetails.setCustCtgCode(customer.getCustCtgCode());
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

	private WSReturnStatus validateCRDetails(FinCreditReviewDetails detail) {
		if (StringUtils.isBlank(detail.getAuditYear())) {
			return getFailedStatus("90502", "Audit Year");
		}

		if (StringUtils.isBlank(detail.getBankName())) {
			return getFailedStatus("90502", "Bank Name");
		}

		if (StringUtils.isBlank(detail.getAuditors())) {
			return getFailedStatus("90502", "Auditors");
		}

		if (StringUtils.isBlank(detail.getLocation())) {
			return getFailedStatus("90502", "Location");
		}

		if (StringUtils.isBlank(String.valueOf(detail.getAuditedDate()))) {
			return getFailedStatus("90502", "Audited Date");
		}

		if (StringUtils.isBlank(String.valueOf(detail.isQualified()))) {
			return getFailedStatus("90502", "Qualified");
		}

		if (StringUtils.isBlank(detail.getAuditType())) {
			return getFailedStatus("90502", "AuditType");
		}

		return null;
	}

	private boolean isValidAuditType(String auditType) {
		return FacilityConstants.CREDITREVIEW_AUDITED.equals(auditType)
				|| FacilityConstants.CREDITREVIEW_UNAUDITED.equals(auditType)
				|| FacilityConstants.CREDITREVIEW_MNGRACNTS.equals(auditType);
	}

	private WSReturnStatus validateCRSummaryEntries(FinCreditReviewDetails detail) {
		for (FinCreditReviewSummary fcrs : detail.getCreditReviewSummaryEntries()) {
			String subCategoryCode = fcrs.getSubCategoryCode();

			if (StringUtils.isBlank(String.valueOf(subCategoryCode))) {
				return getFailedStatus("90502", "SubCategory Code");
			}

			FinCreditRevSubCategory category = finCreditRevSubCategoryDAO
					.getFinCreditRevSubCategoryById(subCategoryCode, "");

			if (category == null) {
				return getFailedStatus("90501", "SubCategory Code " + subCategoryCode);
			}

			if (StringUtils.endsWithIgnoreCase(category.getSubCategoryItemType(),
					FacilityConstants.CREDITREVIEW_CALCULATED_FIELD)) {
				return getFailedStatus("90501", "SubCategory Code " + subCategoryCode);
			}

			if (StringUtils.isBlank(String.valueOf(fcrs.getItemValue()))) {
				return getFailedStatus("90502", "Item Value");
			}
		}

		return null;
	}

	private void validateFieldNames(CustDedupResponse response, CustomerDedup dedup, List<String> fieldNamesList,
			CustDedupRequest feild) {
		boolean fieldFound = false;

		for (String dbField : fieldNamesList) {
			if (StringUtils.equalsIgnoreCase(dbField, feild.getName())) {
				fieldFound = true;
				prepareDedupByRBFields(response, dedup, feild);
			}
		}

		if (!fieldFound) {
			response.setReturnStatus(getFailedStatus("41002", "field name"));
		}
	}

	private void prepareDedupByRBFields(CustDedupResponse response, CustomerDedup dedup, CustDedupRequest feild) {
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
				response.setReturnStatus(getFailedStatus("41002", feild.getName(), "Date"));
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