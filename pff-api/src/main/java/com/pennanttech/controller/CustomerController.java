package com.pennanttech.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.customermasters.DirectorDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.CustCardSalesDetails;
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
import com.pennant.backend.model.customermasters.CustomerGSTDetails;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.customermasters.ExtLiabilityPaymentdetails;
import com.pennant.backend.model.customermasters.GSTDetail;
import com.pennant.backend.model.customermasters.ProspectCustomerDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.CustomerAgreementDetail;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.approvalstatusenquiry.ApprovalStatusEnquiryService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerEmploymentDetailService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.util.AgreementGeneration;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.customer.CustomerDirectorDetail;
import com.pennanttech.ws.model.customer.EmploymentDetail;
import com.pennanttech.ws.model.customer.FinCreditReviewDetailsData;
import com.pennanttech.ws.model.eligibility.AgreementData;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class CustomerController extends GenericService<Object> {
	private final Logger logger = LogManager.getLogger(CustomerController.class);

	private CustomerService customerService;
	private CustomerDetailsService customerDetailsService;
	private CustomerEmploymentDetailService customerEmploymentDetailService;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private AgreementGeneration agreementGeneration;
	private CreditApplicationReviewService creditApplicationReviewService;
	private DirectorDetailDAO directorDetailDAO;
	private DirectorDetailService directorDetailService;
	private DMSService dMSService;
	private CustomerIncomeDAO customerIncomeDAO;
	private ApprovalStatusEnquiryService approvalStatusEnquiryService;

	private final String PROCESS_TYPE_SAVE = "Save";
	private final String PROCESS_TYPE_UPDATE = "Update";

	/**
	 * 
	 * 
	 * @param customerDetails
	 * @return
	 */
	public CustomerDetails createCustomer(CustomerDetails customerDetails) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// data preparation
		doSetRequiredDetails(customerDetails, PROCESS_TYPE_SAVE);

		CustomerDetails response = null;
		try {
			Customer customer = customerDetails.getCustomer();
			/*
			 * customer.setCustNationality("IN"); customer.setCustCOB("IN");
			 */
			customer.setCustSourceID(APIConstants.FINSOURCE_ID_API);
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerDetails, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = customerDetailsService.doApprove(auditHeader);
			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
				for (ErrorDetail errorDetail : auditHeader.getOverideMessage()) {
					response = new CustomerDetails();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new CustomerDetails();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerDetails();
			response.setCustomer(null);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		// prepare create customer response object
		response = getCreateCustomerResponse(customerDetails.getCustomer());
		logger.debug(Literal.LEAVING);

		return response;
	}

	/**
	 * 
	 * @param customerDetails
	 * @return
	 */
	public WSReturnStatus updateCustomer(CustomerDetails customerDetails) throws ServiceException {
		logger.debug(Literal.ENTERING);
		try {
			doSetRequiredDetails(customerDetails, PROCESS_TYPE_UPDATE);

			String AppCountry = (String) SysParamUtil.getValue(PennantConstants.DEFAULT_COUNTRY);
			customerDetails.getCustomer().setCustNationality(AppCountry);

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerDetails, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = customerDetailsService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);

		return APIErrorHandlerService.getSuccessStatus();
	}

	/**
	 * 
	 * 
	 * @param custCIF
	 * @return
	 */
	private CustomerDetails getCreateCustomerResponse(Customer cust) {
		logger.debug(Literal.ENTERING);

		CustomerDetails customerDetails = new CustomerDetails();

		customerDetails.setCustCIF(cust.getCustCIF());
		customerDetails.setCustCoreBank(cust.getCustCoreBank());
		doEmptyResponseObject(customerDetails);
		customerDetails.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		logger.debug(Literal.LEAVING);
		return customerDetails;
	}

	private void doEmptyResponseObject(CustomerDetails customerDetails) {
		customerDetails.setCustomer(null);
	}

	/**
	 * prepare customer detail object with required data to process customer creation.<br>
	 * 
	 * @param customerDetails
	 */
	private void doSetRequiredDetails(CustomerDetails customerDetails, String processType) {
		logger.debug(Literal.ENTERING);

		Customer curCustomer = customerDetails.getCustomer();
		CustomerDetails prvCustomerDetails = null;

		if (StringUtils.equals(processType, PROCESS_TYPE_UPDATE)) {
			// fetch customer object
			Customer customer = customerDetailsService.getCustomerByCIF(customerDetails.getCustCIF());
			// fetch customer details
			prvCustomerDetails = customerDetailsService.getCustomerDetailsById(customer.getCustID(), true, "");
		}

		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		customerDetails.setUserDetails(userDetails);
		customerDetails.setSourceId(APIConstants.FINSOURCE_ID_API);
		curCustomer.setUserDetails(userDetails);

		// set values from customerDetails to customer
		if (StringUtils.isNotBlank(customerDetails.getCustCtgCode())) {
			curCustomer.setCustCtgCode(customerDetails.getCustCtgCode());
		}
		if (StringUtils.isNotBlank(customerDetails.getCustDftBranch())) {
			curCustomer.setCustDftBranch(customerDetails.getCustDftBranch());
		}
		if (StringUtils.isNotBlank(customerDetails.getCustCoreBank())) {
			curCustomer.setCustCoreBank(customerDetails.getCustCoreBank());
		}
		if (StringUtils.isNotBlank(customerDetails.getCustBaseCcy())) {
			curCustomer.setCustBaseCcy(customerDetails.getCustBaseCcy());
		}
		if (StringUtils.isNotBlank(Long.toString(customerDetails.getPrimaryRelationOfficer()))) {
			curCustomer.setCustRO1(customerDetails.getPrimaryRelationOfficer());
		}
		if (StringUtils.isBlank(customerDetails.getCustomer().getCustLng())) {
			curCustomer.setCustLng(PennantConstants.default_Language);
		}
		if (StringUtils.isBlank(customerDetails.getCustomer().getCustCOB())) {
			String AppCountry = (String) SysParamUtil.getValue(PennantConstants.DEFAULT_COUNTRY);
			curCustomer.setCustCOB(AppCountry);
		}
		if (StringUtils.equals(curCustomer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			curCustomer.setCustShrtName(PennantApplicationUtil.getFullName(curCustomer.getCustFName(),
					curCustomer.getCustMName(), curCustomer.getCustLName()));
		}
		CustomerStatusCode customerStatusCode = getCustomerDetailsService().getCustStatusByMinDueDays();
		if (customerStatusCode != null) {
			curCustomer.setCustSts(customerStatusCode.getCustStsCode());
		}
		if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
			// generate new customer CIF
			String custCIF = customerDetailsService.getNewProspectCustomerCIF();

			curCustomer.setNewRecord(true);
			curCustomer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			curCustomer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curCustomer.setCustCIF(custCIF);
			curCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			curCustomer.setVersion(1);
			curCustomer.setLastMntBy(userDetails.getUserId());
		} else {
			Customer prvCustomer = prvCustomerDetails.getCustomer();
			if (StringUtils.isBlank(curCustomer.getCustDftBranch())) {
				curCustomer.setCustDftBranch(prvCustomer.getCustDftBranch());
			}
			customerDetails.setCustID(prvCustomer.getCustID());
			curCustomer.setCustCIF(customerDetails.getCustCIF());
			curCustomer.setCustCoreBank(customerDetails.getCustCoreBank());
			curCustomer.setCustID(prvCustomer.getCustID());
			curCustomer.setCustCRCPR(prvCustomer.getCustCRCPR());
			curCustomer.setNewRecord(false);
			curCustomer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			curCustomer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curCustomer.setVersion((prvCustomer.getVersion()) + 1);
			curCustomer.setLastMntBy(userDetails.getUserId());
			curCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			curCustomer.setCustCoreBank(prvCustomer.getCustCoreBank());
			// copy properties
			BeanUtils.copyProperties(curCustomer, prvCustomer);
		}

		// customer employment details
		List<CustomerEmploymentDetail> curEmpDetails = customerDetails.getEmploymentDetailsList();
		if (curEmpDetails != null) {
			for (CustomerEmploymentDetail curEmpDetail : curEmpDetails) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curEmpDetail.setNewRecord(true);
					curEmpDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curEmpDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					curEmpDetail.setVersion(1);
				} else {
					List<CustomerEmploymentDetail> prvEmpDetails = prvCustomerDetails.getEmploymentDetailsList();
					if (prvEmpDetails != null) {
						for (CustomerEmploymentDetail prvEmpDetail : prvEmpDetails) {
							if (curEmpDetail.getCustEmpId() == prvEmpDetail.getCustEmpId()) {
								curEmpDetail.setNewRecord(false);
								curEmpDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curEmpDetail.setVersion(prvEmpDetail.getVersion() + 1);
								curEmpDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curEmpDetail, prvEmpDetails);
							}
						}
					}
				}
			}
		}

		// customer Address details
		List<CustomerAddres> curAddressList = customerDetails.getAddressList();
		if (curAddressList != null) {
			for (CustomerAddres curAddres : curAddressList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curAddres.setNewRecord(true);
					curAddres.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curAddres.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					curAddres.setVersion(1);
				} else {
					List<CustomerAddres> prvAddressList = prvCustomerDetails.getAddressList();
					if (curAddressList != null && prvAddressList != null) {
						for (CustomerAddres prvAddres : prvAddressList) {
							if (StringUtils.equals(curAddres.getCustAddrType(), prvAddres.getCustAddrType())) {
								curAddres.setNewRecord(false);
								curAddres.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curAddres.setVersion(prvAddres.getVersion() + 1);
								curAddres.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curAddres, prvAddres);
							}
						}
					}
				}
			}
		}

		// customer Phone details
		List<CustomerPhoneNumber> customerPhoneNumList = customerDetails.getCustomerPhoneNumList();
		if (customerPhoneNumList != null) {
			for (CustomerPhoneNumber curCustPhoneNum : customerPhoneNumList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustPhoneNum.setNewRecord(true);
					curCustPhoneNum.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustPhoneNum.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					curCustPhoneNum.setVersion(1);
				} else {
					List<CustomerPhoneNumber> prvCustomerPhoneNumberList = prvCustomerDetails.getCustomerPhoneNumList();
					if (prvCustomerPhoneNumberList != null) {
						for (CustomerPhoneNumber prvCustomerPhoneNum : prvCustomerPhoneNumberList) {
							if (StringUtils.equals(curCustPhoneNum.getPhoneTypeCode(),
									prvCustomerPhoneNum.getPhoneTypeCode())) {
								curCustPhoneNum.setNewRecord(false);
								curCustPhoneNum.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustPhoneNum.setVersion(prvCustomerPhoneNum.getVersion() + 1);
								curCustPhoneNum.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curCustPhoneNum, prvCustomerPhoneNum);
							}
						}
					}
				}
			}
		}

		// customer Email details
		List<CustomerEMail> customerEMailList = customerDetails.getCustomerEMailList();
		if (customerEMailList != null) {
			for (CustomerEMail curCustEmail : customerEMailList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustEmail.setNewRecord(true);
					curCustEmail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustEmail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					curCustEmail.setVersion(1);
				} else {
					List<CustomerEMail> prvCustomerEMailList = prvCustomerDetails.getCustomerEMailList();
					if (prvCustomerEMailList != null) {
						for (CustomerEMail prvCustomerEMail : prvCustomerEMailList) {
							if (StringUtils.equals(curCustEmail.getCustEMailTypeCode(),
									prvCustomerEMail.getCustEMailTypeCode())) {
								curCustEmail.setNewRecord(false);
								curCustEmail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustEmail.setVersion(prvCustomerEMail.getVersion() + 1);
								curCustEmail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curCustEmail, prvCustomerEMail);
							}
						}
					}
				}
			}
		}
		BigDecimal custTotIncomeExp = BigDecimal.ZERO;
		BigDecimal custTotExpense = BigDecimal.ZERO;
		// customer income details
		List<CustomerIncome> customerIncomes = customerDetails.getCustomerIncomeList();
		long custID = customerDetails.getCustomer().getCustID();

		if (customerIncomes != null) {
			for (CustomerIncome curCustomerIncome : customerIncomes) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustomerIncome.setNewRecord(true);
					curCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					// curCustomerIncome.setMargin(BigDecimal.ZERO);
					curCustomerIncome.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					curCustomerIncome.setVersion(1);

				} else {
					List<CustomerIncome> prvCustomerIncomeList = prvCustomerDetails.getCustomerIncomeList();

					String cIncomeExpense = curCustomerIncome.getIncomeExpense();

					if (prvCustomerIncomeList != null) {
						for (CustomerIncome prvCustomerIncome : prvCustomerIncomeList) {
							String pIncomeExpense = prvCustomerIncome.getIncomeExpense();

							if (StringUtils.equals(cIncomeExpense, pIncomeExpense)) {
								curCustomerIncome.setNewRecord(false);
								curCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustomerIncome.setVersion(prvCustomerIncome.getVersion() + 1);
								curCustomerIncome.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								curCustomerIncome.setCustId(custID);

								long linkedId = customerIncomeDAO.getLinkId(custID);

								curCustomerIncome.setLinkId(linkedId);

								CustomerIncome tempCustIncome = null;
								tempCustIncome = customerIncomeDAO.getCustomerIncomeById(curCustomerIncome, "_View",
										"customer");

								if (tempCustIncome != null) {
									curCustomerIncome.setId(tempCustIncome.getId());
								}
								// copy properties
								BeanUtils.copyProperties(curCustomerIncome, prvCustomerIncome);
							}
						}
					}

					if (StringUtils.equals(PennantConstants.INCOME, cIncomeExpense)) {
						custTotIncomeExp = custTotIncomeExp.add(curCustomerIncome.getIncome());
					} else if (StringUtils.equals(PennantConstants.EXPENSE, cIncomeExpense)) {
						custTotIncomeExp = custTotIncomeExp.add(curCustomerIncome.getIncome());
					}

				}
			}
		}
		String docCategory = null;
		if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			docCategory = (String) SysParamUtil.getValue("CUST_PRIMARY_ID_RETL_DOC_TYPE");
		} else {
			docCategory = (String) SysParamUtil.getValue("CUST_PRIMARY_ID_CORP_DOC_TYPE");
		}
		// customer document details
		List<CustomerDocument> customerDocumentsList = customerDetails.getCustomerDocumentsList();
		if (customerDocumentsList != null) {
			for (CustomerDocument curCustDocument : customerDocumentsList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustDocument.setNewRecord(true);
					curCustDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustDocument.setVersion(1);
					// curCustDocument.setCustDocImage(PennantApplicationUtil.decode(curCustDocument.getCustDocImage()));
					curCustDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));

					if (curCustDocument.getDocRefId() == null) {
						curCustDocument.setDocRefId(Long.MIN_VALUE);
					}

					if (StringUtils.equals(curCustDocument.getCustDocCategory(), docCategory)) {
						customerDetails.getCustomer().setCustCRCPR(curCustDocument.getCustDocTitle());
					}
					/*
					 * if(StringUtils.equals(curCustDocument.getCustDocCategory( ), "15") &&
					 * StringUtils.equals(customerDetails.getCustomer(). getCustCtgCode(),PennantConstants.
					 * PFF_CUSTCTG_CORP)){ customerDetails.getCustomer().setCustCRCPR(
					 * curCustDocument.getCustDocTitle()); }
					 */
				} else {
					List<CustomerDocument> prvCustomerDocumentsList = prvCustomerDetails.getCustomerDocumentsList();
					if (prvCustomerDocumentsList != null) {
						for (CustomerDocument prvCustomerDocuments : prvCustomerDocumentsList) {
							if (StringUtils.equals(curCustDocument.getCustDocCategory(),
									prvCustomerDocuments.getCustDocCategory())) {
								curCustDocument.setID(prvCustomerDocuments.getID());
								curCustDocument.setNewRecord(false);
								curCustDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustDocument.setVersion(prvCustomerDocuments.getVersion() + 1);
								curCustDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								if (StringUtils.equals(curCustDocument.getCustDocCategory(), "03")) {
									customerDetails.getCustomer().setCustCRCPR(curCustDocument.getCustDocTitle());
								}
								if (prvCustomerDocuments.getDocRefId() == null) {
									prvCustomerDocuments.setDocRefId(Long.MIN_VALUE);
								}
								// copy properties
								BeanUtils.copyProperties(curCustDocument, prvCustomerDocuments);

							}
						}
					}
				}
			}
		}

		// customer Banking information
		List<CustomerBankInfo> customerBankInfoList = customerDetails.getCustomerBankInfoList();
		if (customerBankInfoList != null) {
			for (CustomerBankInfo curCustBankInfo : customerBankInfoList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustBankInfo.setNewRecord(true);
					curCustBankInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustBankInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					curCustBankInfo.setVersion(1);
					if (CollectionUtils.isNotEmpty(curCustBankInfo.getBankInfoDetails())) {
						for (BankInfoDetail bankInfoDetail : curCustBankInfo.getBankInfoDetails()) {
							bankInfoDetail.setNewRecord(true);
							bankInfoDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
							bankInfoDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
							bankInfoDetail.setVersion(1);
						}
					}
				} else {
					List<CustomerBankInfo> prvCustomerBankInfoList = prvCustomerDetails.getCustomerBankInfoList();
					if (prvCustomerBankInfoList != null) {
						for (CustomerBankInfo prvCustomerBankInfo : prvCustomerBankInfoList) {
							if (curCustBankInfo.getBankId() == prvCustomerBankInfo.getBankId()) {
								curCustBankInfo.setNewRecord(false);
								curCustBankInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustBankInfo.setVersion(prvCustomerBankInfo.getVersion() + 1);
								curCustBankInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curCustBankInfo, prvCustomerBankInfo);
							}
						}
					}
				}
			}
		}

		// customer Gst information
		List<CustomerGST> CustomerGSTInfoList = customerDetails.getCustomerGstList();
		if (CustomerGSTInfoList != null) {
			for (CustomerGST cuCustomerGSTInfo : CustomerGSTInfoList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					cuCustomerGSTInfo.setNewRecord(true);
					cuCustomerGSTInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					cuCustomerGSTInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					cuCustomerGSTInfo.setVersion(1);
					if (CollectionUtils.isNotEmpty(cuCustomerGSTInfo.getCustomerGSTDetailslist())) {
						for (CustomerGSTDetails customerGSTDetails : cuCustomerGSTInfo.getCustomerGSTDetailslist()) {
							customerGSTDetails.setNewRecord(true);
							customerGSTDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
							customerGSTDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
							customerGSTDetails.setVersion(1);
						}
					}
				} else {
					List<CustomerGST> prvCustomerGSTInfoList = prvCustomerDetails.getCustomerGstList();
					List<CustomerGSTDetails> cuCustomerGSTDetailsList = cuCustomerGSTInfo.getCustomerGSTDetailslist();
					if (prvCustomerGSTInfoList != null) {
						for (CustomerGST prvCustomerGSTInfo : prvCustomerGSTInfoList) {
							if (cuCustomerGSTInfo.getId() == prvCustomerGSTInfo.getId()) {
								cuCustomerGSTInfo.setNewRecord(false);
								cuCustomerGSTInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								cuCustomerGSTInfo.setVersion(prvCustomerGSTInfo.getVersion() + 1);
								cuCustomerGSTInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								List<CustomerGSTDetails> customerGSTDetailsList = cuCustomerGSTInfo
										.getCustomerGSTDetailslist();
								for (CustomerGSTDetails customerGSTDetails : customerGSTDetailsList) {
									for (CustomerGSTDetails cuCustomerGSTDetails : cuCustomerGSTDetailsList) {
										if (customerGSTDetails.getId() == cuCustomerGSTDetails.getId()) {
											cuCustomerGSTDetails.setNewRecord(false);
											cuCustomerGSTDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
											cuCustomerGSTDetails.setVersion(prvCustomerGSTInfo.getVersion() + 1);
											cuCustomerGSTDetails
													.setLastMntOn(new Timestamp(System.currentTimeMillis()));
										}
									}

								}
								BeanUtils.copyProperties(cuCustomerGSTInfo, prvCustomerGSTInfo);
							}
						}
					}
				}
			}
		}

		// customer Account behavior
		List<CustomerChequeInfo> customerChequeInfoList = customerDetails.getCustomerChequeInfoList();
		if (customerChequeInfoList != null) {
			for (CustomerChequeInfo curCustChequeInfo : customerChequeInfoList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustChequeInfo.setNewRecord(true);
					curCustChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustChequeInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					curCustChequeInfo.setVersion(1);
				} else {
					List<CustomerChequeInfo> prvCustomerChequeInfoList = prvCustomerDetails.getCustomerChequeInfoList();
					if (prvCustomerChequeInfoList != null) {
						for (CustomerChequeInfo prvCustomerChequeInfo : prvCustomerChequeInfoList) {
							if (curCustChequeInfo.getChequeSeq() == prvCustomerChequeInfo.getChequeSeq()) {
								curCustChequeInfo.setNewRecord(false);
								curCustChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustChequeInfo.setVersion(prvCustomerChequeInfo.getVersion() + 1);
								curCustChequeInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curCustChequeInfo, prvCustomerChequeInfo);
							}
						}
					}
				}
			}
		}

		// customerExtLiability
		List<CustomerExtLiability> customerExtLiabilityList = customerDetails.getCustomerExtLiabilityList();
		if (customerExtLiabilityList != null) {
			for (CustomerExtLiability curCustomerExtLiability : customerExtLiabilityList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustomerExtLiability.setNewRecord(true);
					curCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustomerExtLiability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					curCustomerExtLiability.setVersion(1);
					if (CollectionUtils.isNotEmpty(curCustomerExtLiability.getExtLiabilitiesPayments())) {
						for (ExtLiabilityPaymentdetails detail : curCustomerExtLiability.getExtLiabilitiesPayments()) {
							detail.setNewRecord(true);
							detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
							detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
							detail.setVersion(1);
						}
					}
				} else {
					List<CustomerExtLiability> prvCustomerExtLiabilityList = prvCustomerDetails
							.getCustomerExtLiabilityList();
					if (prvCustomerExtLiabilityList != null) {
						for (CustomerExtLiability prvCustomerExtLiability : prvCustomerExtLiabilityList) {
							if (curCustomerExtLiability.getSeqNo() == prvCustomerExtLiability.getSeqNo()) {
								curCustomerExtLiability.setNewRecord(false);
								curCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustomerExtLiability.setVersion(prvCustomerExtLiability.getVersion() + 1);
								curCustomerExtLiability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								curCustomerExtLiability.setBefImage(prvCustomerExtLiability);
								if (CollectionUtils.isNotEmpty(curCustomerExtLiability.getExtLiabilitiesPayments())) {
									for (ExtLiabilityPaymentdetails detail : curCustomerExtLiability
											.getExtLiabilitiesPayments()) {
										detail.setNewRecord(false);
										detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
										detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
										// detail.setVersion(prvCustomerExtLiability.getVersion()
										// + 1);
									}
								}
								// copy properties
								BeanUtils.copyProperties(curCustomerExtLiability, prvCustomerExtLiability);
							}
						}
					}
				}
				custTotExpense = custTotExpense.add(curCustomerExtLiability.getInstalmentAmount());
			}

		}
		// Cust card details
		List<CustCardSales> customerCardSalesInfo = customerDetails.getCustCardSales();
		if (customerCardSalesInfo != null) {
			for (CustCardSales curCustCardSalesInfo : customerCardSalesInfo) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustCardSalesInfo.setNewRecord(true);
					curCustCardSalesInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustCardSalesInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					curCustCardSalesInfo.setVersion(1);
					if (CollectionUtils.isNotEmpty(curCustCardSalesInfo.getCustCardMonthSales())) {
						for (CustCardSalesDetails cardsalesInfoDetail : curCustCardSalesInfo.getCustCardMonthSales()) {
							cardsalesInfoDetail.setNewRecord(true);
							cardsalesInfoDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
							cardsalesInfoDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
							cardsalesInfoDetail.setVersion(1);
						}
					}
				} else {
					List<CustCardSales> prvCustomerCardSalesInfoList = prvCustomerDetails.getCustCardSales();
					if (prvCustomerCardSalesInfoList != null) {
						for (CustCardSales prvCustomerBankInfo : prvCustomerCardSalesInfoList) {
							if (curCustCardSalesInfo.getMerchantId().equals(prvCustomerBankInfo.getMerchantId())) {
								curCustCardSalesInfo.setNewRecord(false);
								curCustCardSalesInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustCardSalesInfo.setVersion(prvCustomerBankInfo.getVersion() + 1);
								curCustCardSalesInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								if (CollectionUtils.isNotEmpty(curCustCardSalesInfo.getCustCardMonthSales())) {
									for (CustCardSalesDetails cardsalesInfoDetail : curCustCardSalesInfo
											.getCustCardMonthSales()) {
										if (curCustCardSalesInfo.getId() == prvCustomerBankInfo.getId()) {
											cardsalesInfoDetail.setNewRecord(false);
											cardsalesInfoDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
											cardsalesInfoDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
											cardsalesInfoDetail.setVersion(1);
										}
									}
								}

							}
							// copy properties
							BeanUtils.copyProperties(curCustCardSalesInfo, prvCustomerBankInfo);
						}

					}
				}
			}
		}

		// process Extended field details
		// Get the ExtendedFieldHeader for given module and subModule
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
				ExtendedFieldConstants.MODULE_CUSTOMER, customerDetails.getCustCtgCode(), "");
		customerDetails.setExtendedFieldHeader(extendedFieldHeader);
		Map<String, Object> prvExtFieldMap = new HashMap<>();

		List<ExtendedField> extendedFields = customerDetails.getExtendedDetails();
		if (extendedFieldHeader != null) {
			int seqNo = 0;
			ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
			exdFieldRender.setReference(customerDetails.getCustomer().getCustCIF());
			exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			exdFieldRender.setLastMntBy(userDetails.getUserId());
			exdFieldRender.setSeqNo(++seqNo);
			exdFieldRender.setTypeCode(customerDetails.getExtendedFieldHeader().getSubModuleName());
			if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
				exdFieldRender.setNewRecord(true);
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				exdFieldRender.setVersion(1);
			} else {

				List<ExtendedFieldData> prvExtendedFields = new ArrayList<>(1);
				ExtendedFieldHeader curExtendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
						ExtendedFieldConstants.MODULE_CUSTOMER, customerDetails.getCustCtgCode(), "");
				if (curExtendedFieldHeader != null) {
					ExtendedFieldRender extendedFieldRender = extendedFieldDetailsService.getExtendedFieldRender(
							ExtendedFieldConstants.MODULE_CUSTOMER, customerDetails.getCustCtgCode(),
							customerDetails.getCustomer().getCustCIF());
					if (extendedFieldRender != null && extendedFieldRender.getMapValues() != null) {
						prvExtFieldMap = extendedFieldRender.getMapValues();
						for (Map.Entry<String, Object> entry : prvExtFieldMap.entrySet()) {
							ExtendedFieldData data = new ExtendedFieldData();
							data.setFieldName(entry.getKey());
							data.setFieldValue(entry.getValue());
							prvExtendedFields.add(data);
						}
					}
					exdFieldRender.setNewRecord(false);
					exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					exdFieldRender.setVersion(extendedFieldRender.getVersion() + 1);
				}

				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
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

			if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
				customerDetails.setExtendedFieldRender(exdFieldRender);
			} else if (StringUtils.equals(processType, PROCESS_TYPE_UPDATE)) {
				Map<String, Object> curextFieldMap = exdFieldRender.getMapValues();
				prvExtFieldMap.putAll(curextFieldMap);
				exdFieldRender.setMapValues(prvExtFieldMap);
				customerDetails.setExtendedFieldRender(exdFieldRender);
			}
		}
		// customer director details
		setDirectorDetails(customerDetails, processType, prvCustomerDetails);
		setGSTDetails(customerDetails, processType, prvCustomerDetails);

		if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)
				&& !CollectionUtils.isEmpty(customerDetails.getCustomerDirectorList())) {

		}
		curCustomer.setCustTotalIncome(custTotIncomeExp);
		curCustomer.setCustTotalExpense(custTotExpense);

		logger.debug(Literal.LEAVING);
	}

	private void setGSTDetails(CustomerDetails cd, String processType, CustomerDetails prvCd) {
		List<GSTDetail> gstDetails = cd.getGstDetailsList();
		if (CollectionUtils.isEmpty(gstDetails)) {
			return;
		}

		for (GSTDetail gst : gstDetails) {
			if (PROCESS_TYPE_SAVE.equals(processType)) {
				gst.setNewRecord(true);
				gst.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				gst.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				gst.setVersion(1);
			} else {
				List<GSTDetail> prvGSTDetails = prvCd.getGstDetailsList();

				if (CollectionUtils.isEmpty(prvGSTDetails)) {
					continue;
				}

				for (GSTDetail prvGST : prvGSTDetails) {
					if (gst.getId() == prvGST.getId()) {
						gst.setNewRecord(false);
						gst.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						gst.setVersion(prvGST.getVersion() + 1);
						gst.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param customerDetails
	 * @param processType
	 * @param prvCustomerDetails
	 */
	private void setDirectorDetails(CustomerDetails customerDetails, String processType,
			CustomerDetails prvCustomerDetails) {
		List<DirectorDetail> customerDirectorList = customerDetails.getCustomerDirectorList();
		if (customerDirectorList == null) {
			return;
		}

		for (DirectorDetail directorList : customerDirectorList) {
			if (PROCESS_TYPE_SAVE.equals(processType)) {
				directorList.setNewRecord(true);
				directorList.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				directorList.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				directorList.setVersion(1);
			} else {
				List<DirectorDetail> prvCustomerDirectorList = prvCustomerDetails.getCustomerDirectorList();
				if (prvCustomerDirectorList != null) {
					for (DirectorDetail prvDirectorList : prvCustomerDirectorList) {
						if (directorList.getDirectorId() == prvDirectorList.getDirectorId()) {
							directorList.setNewRecord(false);
							directorList.setRecordType(PennantConstants.RECORD_TYPE_UPD);
							directorList.setVersion(prvDirectorList.getVersion() + 1);
							directorList.setLastMntOn(new Timestamp(System.currentTimeMillis()));

							BeanUtils.copyProperties(directorList, prvDirectorList);
						}
					}
				}
			}
		}

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
				aCustomerDetails.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	private AuditHeader getAuditHeader(DirectorDetail directorDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, directorDetail.getBefImage(), directorDetail);
		return new AuditHeader(String.valueOf(directorDetail.getCustID()), String.valueOf(directorDetail.getCustID()),
				null, null, auditDetail, directorDetail.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerDetails
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerEmploymentDetail aCustomerDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerDetails.getBefImage(), aCustomerDetails);
		return new AuditHeader(String.valueOf(aCustomerDetails.getCustID()),
				String.valueOf(aCustomerDetails.getCustID()), null, null, auditDetail,
				aCustomerDetails.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * get the Customer Details By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerDetails(long customerId) {
		logger.debug(Literal.ENTERING);

		CustomerDetails response = new CustomerDetails();

		try {
			response = customerDetailsService.getCustById(customerId);
			if (response != null) {
				response.setCustCIF(response.getCustomer().getCustCIF());
				response.setCustCoreBank(response.getCustomer().getCustCoreBank());
				response.setCustCtgCode(response.getCustomer().getCustCtgCode());
				response.setCustDftBranch(response.getCustomer().getCustDftBranch());
				response.setCustBaseCcy(response.getCustomer().getCustBaseCcy());
				response.setPrimaryRelationOfficer(response.getCustomer().getCustRO1());
				if (response.getCustomerDocumentsList() != null) {
					for (CustomerDocument documents : response.getCustomerDocumentsList()) {
						byte[] custDocImage = getDocumentImage(documents.getDocRefId());
						documents.setCustDocImage(custDocImage);
					}
				}
				response.setExtendedDetails(
						extendedFieldDetailsService.getExtndedFieldDetails(ExtendedFieldConstants.MODULE_CUSTOMER,
								response.getCustomer().getCustCtgCode(), null, response.getCustomer().getCustCIF()));

				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

				if (approvalStatusEnquiryService != null) {
					response.setCustomerFinanceDetailList(
							approvalStatusEnquiryService.getListOfCustomerFinanceById(customerId, null));
				}

			} else {
				response = new CustomerDetails();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerDetails();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	public byte[] getDocumentImage(long docID) {

		byte[] docImage = dMSService.getById(docID);
		if (docImage != null) {
			return docImage;
		}
		return null;
	}

	/**
	 * delete the Customer by given CustomerId
	 * 
	 * @param customerId
	 * @return
	 */
	public WSReturnStatus deleteCustomerById(long customerId) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = null;
		CustomerDetails customerDetails = customerDetailsService.getCustomerDetailsById(customerId, true, "");
		if (customerDetails != null) {
			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

			customerDetails.setUserDetails(userDetails);
			Customer customer = customerDetails.getCustomer();
			customer.setUserDetails(userDetails);
			customer.setRecordType(PennantConstants.RECORD_TYPE_DEL);

			try {
				doSetCustomerDeleteData(customerDetails);
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				AuditHeader auditHeader = getAuditHeader(customerDetails, PennantConstants.TRAN_WF);
				auditHeader.setApiHeader(reqHeaderDetails);
				// do customer delete
				auditHeader = customerDetailsService.delete(auditHeader);

				if (auditHeader.getErrorMessage() != null) {
					for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
						response = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getError());
					}
				} else {
					response = APIErrorHandlerService.getSuccessStatus();
				}
			} catch (DataAccessException dae) {
				response = APIErrorHandlerService.getFailedStatus("90999", dae.getMessage());
				return response;
			} catch (Exception e) {
				logger.error("Exception", e);
				APIErrorHandlerService.logUnhandledException(e);
				response = APIErrorHandlerService.getFailedStatus();
				return response;
			}
		}

		logger.debug(Literal.LEAVING);

		return response;
	}

	// TODO: DDP- Need to change the below method
	/**
	 * set Record type while delete customer
	 * 
	 * @param customerDetails
	 */
	private void doSetCustomerDeleteData(CustomerDetails customerDetails) {

		// customer employment details
		List<CustomerEmploymentDetail> employmentDetails = customerDetails.getEmploymentDetailsList();
		if (employmentDetails != null) {
			for (CustomerEmploymentDetail employmentDetail : employmentDetails) {
				employmentDetail.setNewRecord(false);
				employmentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer Address details
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		if (addressList != null) {
			for (CustomerAddres custAddres : addressList) {
				custAddres.setNewRecord(false);
				custAddres.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer Phone details
		List<CustomerPhoneNumber> customerPhoneNumList = customerDetails.getCustomerPhoneNumList();
		if (customerPhoneNumList != null) {
			for (CustomerPhoneNumber custPhoneNum : customerPhoneNumList) {
				custPhoneNum.setNewRecord(false);
				custPhoneNum.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer Email details
		List<CustomerEMail> customerEMailList = customerDetails.getCustomerEMailList();
		if (customerEMailList != null) {
			for (CustomerEMail custEmail : customerEMailList) {
				custEmail.setNewRecord(false);
				custEmail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

			}
		}

		// customer income details
		List<CustomerIncome> customerIncomes = customerDetails.getCustomerIncomeList();
		if (customerIncomes != null) {
			for (CustomerIncome customerIncome : customerIncomes) {
				customerIncome.setNewRecord(false);
				customerIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer document details
		List<CustomerDocument> customerDocumentsList = customerDetails.getCustomerDocumentsList();
		if (customerDocumentsList != null) {
			for (CustomerDocument custDocument : customerDocumentsList) {
				custDocument.setNewRecord(false);
				custDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer Banking information
		List<CustomerBankInfo> customerBankInfoList = customerDetails.getCustomerBankInfoList();
		if (customerBankInfoList != null) {
			for (CustomerBankInfo custBankInfo : customerBankInfoList) {
				custBankInfo.setNewRecord(false);
				custBankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer GST information
		List<CustomerGST> customerGSTInfoList = customerDetails.getCustomerGstList();
		if (customerGSTInfoList != null) {
			for (CustomerGST customerGSTInfo : customerGSTInfoList) {
				customerGSTInfo.setNewRecord(false);
				customerGSTInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer Account behavior
		List<CustomerChequeInfo> customerChequeInfoList = customerDetails.getCustomerChequeInfoList();
		if (customerChequeInfoList != null) {
			for (CustomerChequeInfo custChequeInfo : customerChequeInfoList) {
				custChequeInfo.setNewRecord(false);
				custChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}
		// customerExtLiability
		List<CustomerExtLiability> customerExtLiabilityList = customerDetails.getCustomerExtLiabilityList();
		if (customerExtLiabilityList != null) {
			for (CustomerExtLiability customerExtLiability : customerExtLiabilityList) {
				customerExtLiability.setNewRecord(false);
				customerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				customerExtLiability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			}
		}

		// customer Card Sales information
		List<CustCardSales> customerCardSalesInfoList = customerDetails.getCustCardSales();
		if (customerCardSalesInfoList != null) {
			for (CustCardSales custCardSalesInfo : customerCardSalesInfoList) {
				custCardSalesInfo.setNewRecord(false);
				custCardSalesInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}
	}

	/**
	 * get the Customer Personal Information By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerPersonalInfo(long customerId) {
		logger.debug(Literal.ENTERING);
		CustomerDetails response = new CustomerDetails();
		response.setReturnStatus(APIErrorHandlerService.getFailedStatus());

		try {
			Customer customer = getCustomerService().getApprovedCustomerById(customerId);
			if (customer != null) {
				response.setCustCIF(customer.getCustCIF());
				response.setCustCoreBank(customer.getCustCoreBank());
				response.setCustCtgCode(customer.getCustCtgCode());
				response.setCustDftBranch(customer.getCustDftBranch());
				response.setCustBaseCcy(customer.getCustBaseCcy());
				response.setPrimaryRelationOfficer(customer.getCustRO1());
				response.setCustomer(customer);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
		}

		logger.debug(Literal.EXCEPTION);
		return response;

	}

	/**
	 * 
	 * @param customerDetails
	 * @return WSReturnStatus
	 */
	public WSReturnStatus updateCustomerPersionalInfo(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		// user details from session
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		Customer prvCustomer = customerDetailsService.getCustomerByCIF(customerDetails.getCustCIF());
		Customer curCustomer = customerDetails.getCustomer();
		curCustomer.setCustCIF(customerDetails.getCustCIF());
		if (StringUtils.isNotBlank(customerDetails.getCustCoreBank())) {
			curCustomer.setCustCoreBank(customerDetails.getCustCoreBank());
		} else {
			curCustomer.setCustCoreBank(prvCustomer.getCustCoreBank());
		}
		if (StringUtils.isNotBlank(customerDetails.getCustDftBranch())) {
			curCustomer.setCustDftBranch(customerDetails.getCustDftBranch());
		} else {
			curCustomer.setCustDftBranch(prvCustomer.getCustDftBranch());
		}
		if (StringUtils.isNotBlank(customerDetails.getCustBaseCcy())) {
			curCustomer.setCustBaseCcy(customerDetails.getCustBaseCcy());
		} else {
			curCustomer.setCustBaseCcy(prvCustomer.getCustBaseCcy());
		}
		if (customerDetails.getPrimaryRelationOfficer() != 0) {
			curCustomer.setCustRO1(customerDetails.getPrimaryRelationOfficer());
		} else {
			curCustomer.setCustRO1(prvCustomer.getCustRO1());
		}
		if (StringUtils.equals(curCustomer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			curCustomer.setCustShrtName(PennantApplicationUtil.getFullName(curCustomer.getCustFName(),
					curCustomer.getCustMName(), curCustomer.getCustLName()));
		}
		curCustomer.setCustCtgCode(prvCustomer.getCustCtgCode());
		curCustomer.setCustID(prvCustomer.getCustID());
		curCustomer.setCustCRCPR(prvCustomer.getCustCRCPR());
		curCustomer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		curCustomer.setNewRecord(false);
		curCustomer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		curCustomer.setLastMntBy(userDetails.getUserId());
		curCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		curCustomer.setVersion(prvCustomer.getVersion() + 1);
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(curCustomer, PennantConstants.TRAN_WF);
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerService.doApprove(auditHeader);
		WSReturnStatus response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}

		logger.debug(Literal.LEAVING);
		return response;

	}

	/**
	 * get the Customer Employment By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerEmployment(String cif) {
		logger.debug(Literal.ENTERING);

		CustomerDetails response = null;
		try {
			Customer customer = customerDetailsService.getCustomerByCIF(cif);
			List<CustomerEmploymentDetail> customerEmploymentDetailList = getCustomerEmploymentDetailService()
					.getApprovedCustomerEmploymentDetailById(customer.getCustID());
			if (customerEmploymentDetailList != null && !customerEmploymentDetailList.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				for (CustomerEmploymentDetail detail : customerEmploymentDetailList) {
					detail.setLovDescCustCIF(null);
				}
				response.setEmploymentDetailsList(customerEmploymentDetailList);
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
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerDetails();
			response.setCustomer(null);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;

	}

	/**
	 * Method for create CustomerEmployment in PLF system.
	 * 
	 * @param customerEmploymentDetail
	 * 
	 */
	public EmploymentDetail addCustomerEmployment(CustomerEmploymentDetail customerEmploymentDetail, String cif) {

		EmploymentDetail response = null;
		logger.debug(Literal.ENTERING);
		try {
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			Customer customer = customerDetailsService.getCustomerByCIF(cif);
			customerEmploymentDetail.setCustID(customer.getCustID());
			customerEmploymentDetail.setLovDescCustCIF(cif);
			customerEmploymentDetail.setLovDesccustEmpName(String.valueOf(customerEmploymentDetail.getCustEmpName()));
			customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			customerEmploymentDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
			customerEmploymentDetail.setNewRecord(true);
			customerEmploymentDetail.setVersion(1);
			customerEmploymentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			customerEmploymentDetail.setLastMntBy(userDetails.getUserId());
			customerEmploymentDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerEmploymentDetail, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = customerEmploymentDetailService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new EmploymentDetail();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				CustomerEmploymentDetail customerEmpDetail = (CustomerEmploymentDetail) auditHeader.getAuditDetail()
						.getModelData();
				response = new EmploymentDetail();
				response.setEmployementId(customerEmpDetail.getCustEmpId());
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new EmploymentDetail();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug(Literal.LEAVING);

		return response;

	}

	public CustomerDirectorDetail addCustomerDirectorDetails(DirectorDetail directorDetail, String cif) {
		logger.debug(Literal.ENTERING);

		CustomerDirectorDetail response = null;

		try {
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			Customer customer = customerDetailsService.getCustomerByCIF(cif);
			directorDetail.setCustID(customer.getCustID());
			directorDetail.setLovDescCustCIF(cif);
			directorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			directorDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
			directorDetail.setNewRecord(true);
			directorDetail.setVersion(1);
			directorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			directorDetail.setLastMntBy(userDetails.getUserId());
			directorDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(directorDetail, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = directorDetailService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new CustomerDirectorDetail();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				DirectorDetail directorDetails = (DirectorDetail) auditHeader.getAuditDetail().getModelData();
				response = new CustomerDirectorDetail();
				response.setDirectorId(directorDetails.getDirectorId());
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CustomerDirectorDetail();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug(Literal.LEAVING);

		return response;

	}

	/**
	 * Method for update CustomerEmploymentDetail in PLF system.
	 * 
	 * @param customerEmploymentDetail
	 */
	public WSReturnStatus updateCustomerEmployment(CustomerEmploymentDetail customerEmploymentDetail, String cif) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus response = null;
		try {
			Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);

			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			customerEmploymentDetail.setUserDetails(userDetails);
			customerEmploymentDetail.setCustID(prvCustomer.getCustID());
			customerEmploymentDetail.setLovDescCustCIF(cif);
			customerEmploymentDetail.setCustEmpName(customerEmploymentDetail.getCustEmpName());
			customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			customerEmploymentDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
			customerEmploymentDetail.setNewRecord(false);
			customerEmploymentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			customerEmploymentDetail.setLastMntBy(userDetails.getUserId());
			customerEmploymentDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			customerEmploymentDetail.setVersion((customerEmploymentDetailService
					.getVersion(customerEmploymentDetail.getCustID(), customerEmploymentDetail.getCustEmpId())) + 1);

			// call service method to update customer Employment details
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerEmploymentDetail, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = customerEmploymentDetailService.doApprove(auditHeader);

			response = new WSReturnStatus();
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
			response = new WSReturnStatus();
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	public WSReturnStatus updateCustomerDirectorDetail(DirectorDetail directorDetail, String cif) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus response = null;
		try {
			Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);

			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			directorDetail.setUserDetails(userDetails);
			directorDetail.setCustID(prvCustomer.getCustID());
			directorDetail.setLovDescCustCIF(cif);
			directorDetail.setFirstName(directorDetail.getFirstName());
			directorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			directorDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
			directorDetail.setNewRecord(false);
			directorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			directorDetail.setLastMntBy(userDetails.getUserId());
			directorDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			directorDetail.setVersion(
					(directorDetailService.getVersion(directorDetail.getCustID(), directorDetail.getDirectorId())) + 1);

			// call service method to update customer Employment details
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(directorDetail, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = directorDetailService.doApprove(auditHeader);

			response = new WSReturnStatus();
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
			response = new WSReturnStatus();
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	public CustomerDetails getCustomerDirectorDetails(String custCIF, long custID) {
		logger.debug(Literal.ENTERING);

		CustomerDetails response = new CustomerDetails();
		response.setCustomer(null);
		response.setReturnStatus(APIErrorHandlerService.getFailedStatus());

		try {
			List<DirectorDetail> customerDirectorDetailDetailList = directorDetailDAO
					.getCustomerDirectorByCustomer(custID, "");

			if (!CollectionUtils.isEmpty(customerDirectorDetailDetailList)) {
				response.setCustCIF(custCIF);

				for (DirectorDetail detail : customerDirectorDetailDetailList) {
					detail.setLovDescCustCIF(null);
				}

				response.setCustomerDirectorList(customerDirectorDetailDetailList);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = custCIF;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			}
		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
		}

		logger.debug(Literal.LEAVING);
		return response;

	}

	/**
	 * delete the customerEmploymentDetail.
	 * 
	 * @param customerEmploymentDetail
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerEmployment(CustomerEmploymentDetail customerEmploymentDetail) {
		// get the CustomerEmploymentDetail by the CustID and empName
		WSReturnStatus response = null;
		try {
			CustomerEmploymentDetail customerEmpDetail = customerEmploymentDetailService
					.getApprovedCustomerEmploymentDetailByCustEmpId(customerEmploymentDetail.getCustEmpId());
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			customerEmpDetail.setUserDetails(userDetails);
			customerEmpDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			customerEmpDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			customerEmpDetail.setNewRecord(false);
			customerEmpDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerEmpDetail, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = customerEmploymentDetailService.doApprove(auditHeader);
			response = new WSReturnStatus();
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
			response = new WSReturnStatus();
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	public WSReturnStatus deleteCustomerDirectorDetail(DirectorDetail directorDetail) {
		WSReturnStatus response = null;
		try {
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			directorDetail.setUserDetails(userDetails);
			directorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			directorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			directorDetail.setNewRecord(false);
			directorDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(directorDetail, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = directorDetailService.doApprove(auditHeader);
			response = new WSReturnStatus();
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
			response = new WSReturnStatus();
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	public AgreementData getCustomerAgreement(long custId) {
		logger.debug(" Entering ");

		CustomerAgreementDetail custAgreementDetail = new CustomerAgreementDetail();
		CustomerDetails customerDetails = customerDetailsService.getApprovedCustomerById(custId);
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
				ExtendedFieldConstants.MODULE_CUSTOMER, customerDetails.getCustomer().getCustCtgCode(), null, "");
		if (extendedFieldHeader != null) {
			StringBuilder tableName = new StringBuilder();
			tableName.append(extendedFieldHeader.getModuleName());
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			tableName.append("_ED");

			List<Map<String, Object>> renderMapList = extendedFieldRenderDAO
					.getExtendedFieldMap(customerDetails.getCustomer().getCustCIF(), tableName.toString(), "");

			if (renderMapList != null) {
				for (Map<String, Object> mapValues : renderMapList) {
					custAgreementDetail.setExtendedFields(mapValues);
				}
			}
		}

		Map<String, Object> result = new HashMap<>();

		for (Entry<String, Object> entry : custAgreementDetail.getExtendedFields().entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			result.put("CUST_EF_" + key.toString(), value);
		}

		custAgreementDetail.setExtendedFields(result);
		custAgreementDetail.setCustCIF(customerDetails.getCustomer().getCustCIF());
		custAgreementDetail.setCustShrtName(customerDetails.getCustomer().getCustShrtName());
		custAgreementDetail.setCustCRCPR(customerDetails.getCustomer().getCustCRCPR());
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		custAgreementDetail.setCustCurrentAddres(new CustomerAddres());
		custAgreementDetail.setCustomerEMail(new CustomerEMail());
		custAgreementDetail.setAppDate(SysParamUtil.getAppDate());
		custAgreementDetail.setStrAppDate(DateUtility.formatToLongDate(SysParamUtil.getAppDate()));
		custAgreementDetail.setDob(DateUtility.formatToLongDate(customerDetails.getCustomer().getCustDOB()));

		setCustomerAddress(custAgreementDetail, addressList);

		int highPriorty = 0;
		for (CustomerEMail email : customerDetails.getCustomerEMailList()) {
			if (highPriorty < email.getCustEMailPriority()) {
				custAgreementDetail.getCustomerEMail().setCustEMail(StringUtils.trimToEmpty(email.getCustEMail()));
				highPriorty = email.getCustEMailPriority();
			}
			CustomerEMail emailDetails = custAgreementDetail.getCustomerEMail();
			emailDetails.setCustEMailTypeCode(StringUtils.trimToEmpty(email.getLovDescCustEMailTypeCode()));
			emailDetails.setCustEMail(StringUtils.trimToEmpty(email.getCustEMail()));
			custAgreementDetail.setCustomerEMail(emailDetails);
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
			custAgreementDetail.setCustomerPhoneNumber(mobileNumber);
		}
		String path = PathUtil.getPath(PathUtil.FINANCE_AGREEMENTS);
		byte[] doc = agreementGeneration.getCustomerAgreementGeneration(custAgreementDetail, path,
				"Limit Sanction Letter.docx");
		AgreementData agrdata = new AgreementData();
		agrdata.setDocContent(doc);
		if (doc != null) {
			agrdata.setCif(customerDetails.getCustomer().getCustCIF());
			agrdata.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} else {
			agrdata.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug(" Leaving ");
		return agrdata;
	}

	public ProspectCustomerDetails getDedupCustomer(ProspectCustomerDetails prospectCustomerDetails) {
		ProspectCustomerDetails response = new ProspectCustomerDetails();
		List<CustomerDedup> customerDedups = new ArrayList<CustomerDedup>();
		List<Customer> customerlist = null;

		customerlist = customerService.getCustomerDetailsByCRCPR(prospectCustomerDetails.getReference(),
				prospectCustomerDetails.getCustCtgCode(), "");

		for (Customer detail : customerlist) {
			CustomerDedup customerDedup = new CustomerDedup();
			customerDedup.setCustCIF(detail.getCustCIF());
			customerDedup.setCustCtgCode(detail.getCustCtgCode());
			customerDedup.setCustDftBranch(detail.getCustDftBranch());
			customerDedup.setCustLName(detail.getCustLName());
			customerDedup.setCustFName(detail.getCustLName());
			customerDedup.setCustShrtName(detail.getCustShrtName());
			customerDedup.setCustDOB(detail.getCustDOB());
			customerDedup.setCustCRCPR(detail.getCustCRCPR());
			customerDedup.setCustSector(detail.getCustSector());

			customerDedups.add(customerDedup);
		}

		if (CollectionUtils.isNotEmpty(customerDedups)) {
			response.setCustomerDedupList(customerDedups);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			return response;
		} else {
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		return response;

	}

	private void setCustomerAddress(CustomerAgreementDetail agreement, List<CustomerAddres> addressList) {
		if (addressList != null && !addressList.isEmpty()) {
			if (addressList.size() == 1) {
				setAddressDetails(agreement, addressList.get(0));
			} else {
				// sort the address based on priority and consider the top
				// priority
				sortCustomerAdress(addressList);
				for (CustomerAddres customerAddres : addressList) {
					setAddressDetails(agreement, customerAddres);
					break;
				}

			}
		}
	}

	private void setAddressDetails(CustomerAgreementDetail agreement, CustomerAddres customerAddres) {
		agreement.getCustCurrentAddres().setCustAddrHNbr(customerAddres.getCustAddrHNbr());
		agreement.getCustCurrentAddres().setCustFlatNbr(StringUtils.trimToEmpty(customerAddres.getCustFlatNbr()));
		agreement.getCustCurrentAddres().setCustPOBox(StringUtils.trimToEmpty(customerAddres.getCustPOBox()));
		agreement.getCustCurrentAddres().setCustAddrStreet(StringUtils.trimToEmpty(customerAddres.getCustAddrStreet()));
		agreement.getCustCurrentAddres()
				.setCustAddrCountry(StringUtils.trimToEmpty(customerAddres.getLovDescCustAddrCountryName()));
		agreement.getCustCurrentAddres()
				.setCustAddrProvince(StringUtils.trimToEmpty(customerAddres.getLovDescCustAddrProvinceName()));
		agreement.getCustCurrentAddres()
				.setCustAddrCity(StringUtils.trimToEmpty(customerAddres.getLovDescCustAddrCityName()));
		if (!PennantConstants.CITY_FREETEXT) {
			agreement.getCustCurrentAddres()
					.setCustAddrCity(StringUtils.trimToEmpty(customerAddres.getLovDescCustAddrCityName()));
		}
		agreement.getCustCurrentAddres().setCustDistrict(StringUtils.trimToEmpty(customerAddres.getCustDistrict()));
		agreement.getCustCurrentAddres().setCustAddrLine4(StringUtils.trimToEmpty(customerAddres.getCustAddrLine4()));
		agreement.getCustCurrentAddres().setCustAddrLine1(StringUtils.trimToEmpty(customerAddres.getCustAddrLine1()));
		agreement.getCustCurrentAddres().setCustAddrLine2(StringUtils.trimToEmpty(customerAddres.getCustAddrLine2()));
		agreement.getCustCurrentAddres().setCustAddrZIP(StringUtils.trimToEmpty(customerAddres.getCustAddrZIP()));
	}

	public static void sortCustomerAdress(List<CustomerAddres> list) {

		if (list != null && !list.isEmpty()) {
			Collections.sort(list, new Comparator<CustomerAddres>() {
				@Override
				public int compare(CustomerAddres detail1, CustomerAddres detail2) {
					return detail2.getCustAddrPriority() - detail1.getCustAddrPriority();
				}
			});
		}
	}

	public WSReturnStatus doAddCreditReviewDetails(FinCreditReviewDetailsData finCreditReviewDetailsData) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus response = null;
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		Map<String, List<FinCreditReviewSummary>> creditReviewSummaryMap;
		Map<String, BigDecimal> prv1YearValuesMap = null;
		List<FinCreditReviewSummary> creditReviewSummaryList = new ArrayList<FinCreditReviewSummary>();
		Map<String, BigDecimal> extValuesMap = new HashMap<String, BigDecimal>();
		extValuesMap = setExtValuesMap(extValuesMap);
		Map<String, BigDecimal> curYearValuesMap = new HashMap<String, BigDecimal>();
		Map<String, FinCreditReviewSummary> summaryMap = new HashMap<String, FinCreditReviewSummary>();
		Map<String, BigDecimal> prvYearValuesMap = new HashMap<String, BigDecimal>();
		for (FinCreditReviewDetails finCreditReviewDetails : finCreditReviewDetailsData.getFinCreditReviewDetails()) {
			try {
				finCreditReviewDetails.setUserDetails(userDetails);
				// finCreditReviewDetails.setSourceId(APIConstants.FINSOURCE_ID_API);
				finCreditReviewDetails.setUserDetails(userDetails);
				finCreditReviewDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				finCreditReviewDetails.setLastMntBy(userDetails.getUserId());
				finCreditReviewDetails.setAuditPeriod(12);
				finCreditReviewDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				// finCreditReviewDetails.setCustID(customer.getCustID());
				finCreditReviewDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				finCreditReviewDetails.setNewRecord(true);
				finCreditReviewDetails.setVersion(1);

				// creditReviewSummaryList=
				// finCreditReviewDetails.getLovDescCreditReviewSummaryEntries();
				String category = "";
				String type = "";
				if (finCreditReviewDetails.getCreditRevCode().equalsIgnoreCase(PennantConstants.PFF_CUSTCTG_SME)) {
					category = PennantConstants.PFF_CUSTCTG_SME;
				} else if (finCreditReviewDetails.getCreditRevCode()
						.equalsIgnoreCase(PennantConstants.PFF_CUSTCTG_CORP)) {
					category = PennantConstants.PFF_CUSTCTG_CORP;
				} else {
					category = PennantConstants.PFF_CUSTCTG_INDIV;
				}
				int audityear = Integer.parseInt(finCreditReviewDetails.getAuditYear());
				List<FinCreditReviewSummary> listOfCreditReviewSummary = new ArrayList<FinCreditReviewSummary>();
				List<FinCreditRevCategory> listFinCreditRevCategory = creditApplicationReviewService
						.getCreditRevCategoryByCreditRevCode(finCreditReviewDetails.getCreditRevCode());
				List<FinCreditRevSubCategory> listOfFinCreditRevSubCategory = creditApplicationReviewService
						.getFinCreditRevSubCategoryByMainCategory(finCreditReviewDetails.getCreditRevCode());
				creditReviewSummaryMap = creditApplicationReviewService.getListCreditReviewSummaryByCustId2(
						finCreditReviewDetails.getCustomerId(), 2, audityear - 1, category,
						finCreditReviewDetails.getAuditPeriod(), false, "_VIEW");
				List<FinCreditRevSubCategory> listFinCreditRevSubCategory = null;
				boolean isCurrentDataMapAvil = false;
				if (creditReviewSummaryMap == null || creditReviewSummaryMap.isEmpty()) {
					creditReviewSummaryMap = creditApplicationReviewService.getListCreditReviewSummaryByCustId2(
							finCreditReviewDetails.getCustomerId(), 0, audityear, category,
							finCreditReviewDetails.getAuditPeriod(), true, type);
					isCurrentDataMapAvil = true;
					if (creditReviewSummaryMap != null && creditReviewSummaryMap.size() > 0) {
						creditReviewSummaryMap.put(String.valueOf(audityear - 1),
								creditReviewSummaryMap.get(String.valueOf(audityear)));
						creditReviewSummaryMap.put(String.valueOf(audityear - 2),
								creditReviewSummaryMap.get(String.valueOf(audityear)));
					}
				}
				prv1YearValuesMap = new HashMap<String, BigDecimal>();
				prv1YearValuesMap.put("auditYear", BigDecimal.valueOf(audityear - 2));
				creditReviewSummaryList = creditReviewSummaryMap.get(String.valueOf(audityear - 2));

				Map<String, Object> engine = new HashMap<>();

				if (creditReviewSummaryList != null && creditReviewSummaryList.size() > 0) {
					prv1YearValuesMap.putAll(extValuesMap);
					for (int k = 0; k < creditReviewSummaryList.size(); k++) {
						// to set map and engine default values for which is not
						// available previous years
						if (isCurrentDataMapAvil) {
							prv1YearValuesMap.put(creditReviewSummaryList.get(k).getSubCategoryCode(), BigDecimal.ZERO);
							engine.put("Y" + (audityear - 2) + creditReviewSummaryList.get(k).getSubCategoryCode(),
									BigDecimal.ZERO);
						} else {
							// if data available to set data for it
							prv1YearValuesMap
									.put(creditReviewSummaryList.get(k).getSubCategoryCode(),
											PennantApplicationUtil
													.formateAmount(creditReviewSummaryList.get(k).getItemValue(),
															SysParamUtil
																	.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT))
													.setScale(
															SysParamUtil
																	.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT),
															RoundingMode.HALF_DOWN));
							engine.put("Y" + (audityear - 2) + creditReviewSummaryList.get(k).getSubCategoryCode(),
									PennantApplicationUtil.formateAmount(creditReviewSummaryList.get(k).getItemValue(),
											SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
						}
					}
					// External fields to put default data for map and engine
					if (extValuesMap != null && extValuesMap.size() > 0) {
						for (Entry<String, BigDecimal> entry : extValuesMap.entrySet()) {
							if (entry.getKey().startsWith("EXT_")) {
								engine.put(entry.getKey(), extValuesMap.get(entry.getKey()));
								prv1YearValuesMap.put(entry.getKey(), extValuesMap.get(entry.getKey()));
							}
						}
					}
				}
				if (prv1YearValuesMap != null && prv1YearValuesMap.size() > 1) {
					setData(prv1YearValuesMap, finCreditReviewDetails, listOfFinCreditRevSubCategory, engine);
				}

				boolean isPrevYearSummayNull = false;
				creditReviewSummaryList = creditReviewSummaryMap.get(String.valueOf(audityear - 1));
				if (creditReviewSummaryList == null) {
					isPrevYearSummayNull = true;
				}
				prvYearValuesMap.put("auditYear", BigDecimal.valueOf(audityear - 1));
				if (!isPrevYearSummayNull && creditReviewSummaryList.size() > 0) {
					prvYearValuesMap.putAll(extValuesMap);
					for (int k = 0; k < creditReviewSummaryList.size(); k++) {
						prvYearValuesMap.put(creditReviewSummaryList.get(k).getSubCategoryCode(),
								PennantApplicationUtil
										.formateAmount(creditReviewSummaryList.get(k).getItemValue(),
												SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT))
										.setScale(SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT),
												RoundingMode.HALF_DOWN));
						// summaryMap.put(this.creditReviewSummaryList.get(k).getSubCategoryCode(),this.creditReviewSummaryList.get(k));
						engine.put("Y" + (audityear - 1) + creditReviewSummaryList.get(k).getSubCategoryCode(),
								PennantApplicationUtil.formateAmount(creditReviewSummaryList.get(k).getItemValue(),
										SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
					}
					// External fields to put default data for map and engine
					if (extValuesMap != null && extValuesMap.size() > 0) {
						for (Entry<String, BigDecimal> entry : extValuesMap.entrySet()) {
							if (entry.getKey().startsWith("EXT_")) {
								engine.put(entry.getKey(), extValuesMap.get(entry.getKey()));
								prvYearValuesMap.put(entry.getKey(), extValuesMap.get(entry.getKey()));
							}
						}
					}
				}

				if (prvYearValuesMap.size() > 28) {
					setData(prvYearValuesMap, finCreditReviewDetails, listOfFinCreditRevSubCategory, engine);
				}
				curYearValuesMap.putAll(extValuesMap);
				// below flag is previous years data not available.It is true
				if (!isCurrentDataMapAvil) {
					creditReviewSummaryMap = creditApplicationReviewService.getListCreditReviewSummaryByCustId2(
							finCreditReviewDetails.getCustomerId(), 0, audityear, category,
							finCreditReviewDetails.getAuditPeriod(), true, type);
				}

				if (creditReviewSummaryMap.get(finCreditReviewDetails.getAuditYear()) != null
						&& creditReviewSummaryMap.size() > 0) {
					creditReviewSummaryList = creditReviewSummaryMap.get(finCreditReviewDetails.getAuditYear());
					curYearValuesMap.put("auditYear", BigDecimal.valueOf(audityear));
					for (int k = 0; k < creditReviewSummaryList.size(); k++) {
						curYearValuesMap.put(creditReviewSummaryList.get(k).getSubCategoryCode(),
								PennantApplicationUtil.formateAmount(creditReviewSummaryList.get(k).getItemValue(),
										SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
						summaryMap.put(creditReviewSummaryList.get(k).getSubCategoryCode(),
								creditReviewSummaryList.get(k));
						engine.put("Y" + audityear + creditReviewSummaryList.get(k).getSubCategoryCode(),
								PennantApplicationUtil.formateAmount(creditReviewSummaryList.get(k).getItemValue(),
										SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
						if (isPrevYearSummayNull) {
							prvYearValuesMap.put(creditReviewSummaryList.get(k).getSubCategoryCode(),
									PennantApplicationUtil.formateAmount(BigDecimal.ZERO,
											SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
							engine.put("Y" + (audityear - 1) + creditReviewSummaryList.get(k).getSubCategoryCode(),
									PennantApplicationUtil.formateAmount(BigDecimal.ZERO,
											SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
							if (prvYearValuesMap != null && prvYearValuesMap.size() > 0) {
								setData(prvYearValuesMap, finCreditReviewDetails, listOfFinCreditRevSubCategory,
										engine);
							}
						}
					}
				} else {
					for (int k = 0; k < listOfFinCreditRevSubCategory.size(); k++) {
						curYearValuesMap.put(listOfFinCreditRevSubCategory.get(k).getSubCategoryCode(),
								PennantApplicationUtil.formateAmount(BigDecimal.ZERO,
										SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
						engine.put("Y" + audityear + listOfFinCreditRevSubCategory.get(k).getSubCategoryCode(),
								PennantApplicationUtil.formateAmount(BigDecimal.ZERO,
										SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
						if (isPrevYearSummayNull) {
							prvYearValuesMap.put(listOfFinCreditRevSubCategory.get(k).getSubCategoryCode(),
									PennantApplicationUtil.formateAmount(BigDecimal.ZERO,
											SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
							engine.put(
									"Y" + (audityear - 1) + listOfFinCreditRevSubCategory.get(k).getSubCategoryCode(),
									PennantApplicationUtil.formateAmount(BigDecimal.ZERO,
											SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
							if (prvYearValuesMap != null && prvYearValuesMap.size() > 0
									&& !finCreditReviewDetails.isNewRecord()) {
								setData(prvYearValuesMap, finCreditReviewDetails, listOfFinCreditRevSubCategory,
										engine);
							}
						}
					}
				}
				// If current year data is available and not new than set
				if (curYearValuesMap != null && !finCreditReviewDetails.isNewRecord()) {
					setData(curYearValuesMap, finCreditReviewDetails, listOfFinCreditRevSubCategory, engine);
				}

				for (FinCreditReviewSummary finCreditReviewSummary : finCreditReviewDetails
						.getLovDescCreditReviewSummaryEntries()) {
					String subCategory = finCreditReviewSummary.getSubCategoryCode();
					curYearValuesMap.put(subCategory, finCreditReviewSummary.getItemValue() == null ? BigDecimal.ZERO
							: finCreditReviewSummary.getItemValue());
					curYearValuesMap.put("auditYear",
							new BigDecimal(Integer.parseInt(finCreditReviewDetails.getAuditYear())));
					engine.put("Y" + finCreditReviewDetails.getAuditYear() + subCategory,
							finCreditReviewSummary.getItemValue() == null ? BigDecimal.ZERO
									: finCreditReviewSummary.getItemValue());
					engine.put(
							"Y" + finCreditReviewDetails.getAuditYear() + FacilityConstants.CREDITREVIEW_REMARKS
									+ subCategory,
							finCreditReviewSummary.getItemValue() == null ? BigDecimal.ZERO
									: finCreditReviewSummary.getItemValue());
					setData(curYearValuesMap, finCreditReviewDetails, listOfFinCreditRevSubCategory, engine);
				}

				for (int i = 0; i < listFinCreditRevCategory.size(); i++) {
					listFinCreditRevSubCategory = creditApplicationReviewService
							.getFinCreditRevSubCategoryByCategoryId(listFinCreditRevCategory.get(i).getCategoryId());
					for (int j = 0; j < listFinCreditRevSubCategory.size(); j++) {
						FinCreditReviewSummary creditReviewSummary = new FinCreditReviewSummary();
						FinCreditRevSubCategory finCreditRevSubCategory = listFinCreditRevSubCategory.get(j);
						creditReviewSummary.setNewRecord(true);
						creditReviewSummary.setSubCategoryCode(finCreditRevSubCategory.getSubCategoryCode());
						creditReviewSummary.setVersion(1);
						finCreditRevSubCategory.setNewRecord(true);
						finCreditRevSubCategory.setLastMntBy(userDetails.getUserId());
						finCreditRevSubCategory.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						creditReviewSummary.setLastMntBy(userDetails.getUserId());
						creditReviewSummary.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						creditReviewSummary.setWorkflowId(0);
						creditReviewSummary.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						creditReviewSummary.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						creditReviewSummary.setSubCategoryCode(finCreditRevSubCategory.getSubCategoryCode());
						creditReviewSummary
								.setItemValue(curYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode()) == null
										? BigDecimal.ZERO
										: PennantApplicationUtil.unFormateAmount(
												curYearValuesMap.get(finCreditRevSubCategory.getSubCategoryCode()),
												SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
						listOfCreditReviewSummary.add(creditReviewSummary);
						creditReviewSummary = null;
					}
					if (listOfFinCreditRevSubCategory != null && !listOfFinCreditRevSubCategory.isEmpty()) {
						finCreditReviewDetails.setLovDescFinCreditRevSubCategory(listOfFinCreditRevSubCategory);
					}
					finCreditReviewDetails.setCreditReviewSummaryEntries(listOfCreditReviewSummary);
				}
				finCreditReviewDetails.getCreditReviewSummaryEntries();
				// get the header details from the request
				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				AuditHeader auditHeader = getAuditHeader(finCreditReviewDetails, PennantConstants.TRAN_WF);
				// set the headerDetails to AuditHeader
				auditHeader.setApiHeader(reqHeaderDetails);
				auditHeader = creditApplicationReviewService.doApprove(auditHeader);
				response = new WSReturnStatus();
				response = APIErrorHandlerService.getSuccessStatus();
				return response;
			} catch (Exception e) {
				logger.error("Exception", e);
				response = APIErrorHandlerService.getFailedStatus();
				return response;
			}
		}
		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	/**
	 * This method for set the data according to the formulae.<br>
	 * 
	 * @param listItem
	 */
	public void setData(Map<String, BigDecimal> dataMap, FinCreditReviewDetails finCreditReviewDetails,
			List<FinCreditRevSubCategory> listOfFinCreditRevSubCategory, Map<String, Object> engine) {
		logger.debug(Literal.ENTERING);

		engine.put("EXCHANGE", finCreditReviewDetails.getConversionRate());
		engine.put("NoOfShares", finCreditReviewDetails.getNoOfShares());
		engine.put("MARKETPRICE", finCreditReviewDetails.getMarketPrice());

		String year = "";

		if (dataMap.get("auditYear") != null) {
			year = String.valueOf(dataMap.get("auditYear"));
		}
		if (finCreditReviewDetails.getAuditYear().equals(year)) {
			engine.put("Y" + finCreditReviewDetails.getAuditType() + "DIVCOUNT", BigDecimal.ONE);
		} else {
			engine.put("Y" + year + "DIVCOUNT", new BigDecimal(2));
		}

		// total calculation
		for (int i = 0; i < listOfFinCreditRevSubCategory.size(); i++) {
			FinCreditRevSubCategory finCreditRevSubCategory = listOfFinCreditRevSubCategory.get(i);

			if (finCreditRevSubCategory.getSubCategoryItemType().equals(FacilityConstants.CREDITREVIEW_CALCULATED_FIELD)
					&& StringUtils.isNotEmpty(finCreditRevSubCategory.getItemsToCal())) {
				BigDecimal value = BigDecimal.ZERO;

				value = (BigDecimal) RuleExecutionUtil.executeRule(
						replaceYear(finCreditRevSubCategory.getItemRule(), year), engine, RuleReturnType.DECIMAL);

				dataMap.put(finCreditRevSubCategory.getSubCategoryCode(), value == null ? BigDecimal.ZERO : value);
				engine.put("Y" + year + finCreditRevSubCategory.getSubCategoryCode(),
						value == null ? BigDecimal.ZERO : value);
			}
		}

		// ratio calculation
		for (int i = 0; i < listOfFinCreditRevSubCategory.size(); i++) {
			FinCreditRevSubCategory finCreditRevSubCategory = listOfFinCreditRevSubCategory.get(i);
			if (StringUtils.isNotEmpty(finCreditRevSubCategory.getItemRule())) {
				BigDecimal value = BigDecimal.ZERO;

				value = (BigDecimal) RuleExecutionUtil.executeRule(
						replaceYear(finCreditRevSubCategory.getItemRule(), year), engine, RuleReturnType.DECIMAL);

				if (finCreditRevSubCategory.getCategoryId() == 4 || finCreditRevSubCategory.getCategoryId() == 7) {
					dataMap.put(finCreditRevSubCategory.getSubCategoryCode(), value);
					engine.put("Y" + year + finCreditRevSubCategory.getSubCategoryCode(),
							value == null ? BigDecimal.ZERO : value);
				} else {
					dataMap.put(FacilityConstants.CREDITREVIEW_REMARKS + finCreditRevSubCategory.getSubCategoryCode(),
							value == null ? BigDecimal.ZERO : value);
					dataMap.put(FacilityConstants.CREDITREVIEW_REMARKS + finCreditRevSubCategory.getSubCategoryCode(),
							value == null ? BigDecimal.ZERO : value);
				}
			}
		}
	}

	public String replaceYear(String formula, String year) {
		String formatedFormula = formula;
		if (StringUtils.isNotEmpty(year)) {
			formatedFormula = formatedFormula.replace("YN-1" + ".", "Y" + (Integer.parseInt(year) - 1));
			formatedFormula = formatedFormula.replace("YN" + ".", "Y" + year);
		}
		return formatedFormula;
	}

	/**
	 * set default values zero for Extended Fields to remove exceptions in console and work performance
	 * 
	 * @return
	 */

	public Map<String, BigDecimal> setExtValuesMap(Map<String, BigDecimal> extValuesMap) {
		extValuesMap.put("EXT_CREDITTRANNO", BigDecimal.ZERO);
		extValuesMap.put("EXT_CREDITTRANAMT", BigDecimal.ZERO);
		extValuesMap.put("EXT_CREDITTRANAVG", BigDecimal.ZERO);
		extValuesMap.put("EXT_DEBITTRANNO", BigDecimal.ZERO);
		extValuesMap.put("EXT_DEBITTRANAMT", BigDecimal.ZERO);
		extValuesMap.put("EXT_CASHDEPOSITNO", BigDecimal.ZERO);
		extValuesMap.put("EXT_CASHDEPOSITAMT", BigDecimal.ZERO);
		extValuesMap.put("EXT_CASHWITHDRAWALNO", BigDecimal.ZERO);
		extValuesMap.put("EXT_CASHWITHDRAWALAMT", BigDecimal.ZERO);
		extValuesMap.put("EXT_CHQDEPOSITNO", BigDecimal.ZERO);
		extValuesMap.put("EXT_CHQDEPOSITAMT", BigDecimal.ZERO);
		extValuesMap.put("EXT_CHQISSUEN", BigDecimal.ZERO);
		extValuesMap.put("EXT_CHQISSUEAMT", BigDecimal.ZERO);
		extValuesMap.put("EXT_INWARDCHQBOUNCENO", BigDecimal.ZERO);
		extValuesMap.put("EXT_OUTWARDCHQBOUNCENO", BigDecimal.ZERO);
		extValuesMap.put("EXT_EODBALAVG", BigDecimal.ZERO);
		extValuesMap.put("EXT_EODBALMAX", BigDecimal.ZERO);
		extValuesMap.put("EXT_EODBALMIN", BigDecimal.ZERO);
		extValuesMap.put("EXT_SUMOFEMI", BigDecimal.ZERO);
		extValuesMap.put("EXT_NUMBEROFTERMS", BigDecimal.ZERO);
		extValuesMap.put("EXT_CHQISSUENO", BigDecimal.ZERO);
		extValuesMap.put("EXT_OBLIGATION", BigDecimal.ZERO);
		extValuesMap.put("EXT_REPAYPROFITRATE", BigDecimal.ZERO);
		extValuesMap.put("EXT_ROUNDINGTARGET", BigDecimal.ZERO);
		extValuesMap.put("EXT_FINASSETVALUE", BigDecimal.ZERO);
		extValuesMap.put("EXT_FINAMOUNT", BigDecimal.ZERO);
		extValuesMap.put("EXT_FIRSTREPAY", BigDecimal.ZERO);
		return extValuesMap;
	}

	private AuditHeader getAuditHeader(FinCreditReviewDetails finCreditReviewDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, finCreditReviewDetails.getBefImage(),
				finCreditReviewDetails);
		return new AuditHeader(String.valueOf(finCreditReviewDetails.getCustomerId()),
				String.valueOf(finCreditReviewDetails.getCustomerId()), null, null, auditDetail,
				finCreditReviewDetails.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomer
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Customer aCustomer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomer.getBefImage(), aCustomer);
		return new AuditHeader(String.valueOf(aCustomer.getCustID()), String.valueOf(aCustomer.getCustID()), null, null,
				auditDetail, aCustomer.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public CustomerEmploymentDetailService getCustomerEmploymentDetailService() {
		return customerEmploymentDetailService;
	}

	public void setCustomerEmploymentDetailService(CustomerEmploymentDetailService customerEmploymentDetailService) {
		this.customerEmploymentDetailService = customerEmploymentDetailService;
	}

	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public ExtendedFieldRenderDAO getExtendedFieldRenderDAO() {
		return extendedFieldRenderDAO;
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public void setAgreementGeneration(AgreementGeneration agreementGeneration) {
		this.agreementGeneration = agreementGeneration;
	}

	public void setCreditApplicationReviewService(CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

	public DirectorDetailDAO getDirectorDetailDAO() {
		return directorDetailDAO;
	}

	public void setDirectorDetailDAO(DirectorDetailDAO directorDetailDAO) {
		this.directorDetailDAO = directorDetailDAO;
	}

	public DirectorDetailService getDirectorDetailService() {
		return directorDetailService;
	}

	public void setDirectorDetailService(DirectorDetailService directorDetailService) {
		this.directorDetailService = directorDetailService;
	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

	public void setCustomerIncomeDAO(CustomerIncomeDAO customerIncomeDAO) {
		this.customerIncomeDAO = customerIncomeDAO;
	}

	public void setApprovalStatusEnquiryService(ApprovalStatusEnquiryService approvalStatusEnquiryService) {
		this.approvalStatusEnquiryService = approvalStatusEnquiryService;
	}

}