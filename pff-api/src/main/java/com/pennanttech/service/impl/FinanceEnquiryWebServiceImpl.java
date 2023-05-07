package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customerdata.CustomerData;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.loandetail.LoanDetail;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.pff.response.AbstractResponse;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.FinanceEnquiryController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.SchdUtil;
import com.pennanttech.pff.rs.financeenquiry.FinanceEnquiryRestService;

public class FinanceEnquiryWebServiceImpl extends AbstractResponse implements FinanceEnquiryRestService {

	private FinanceMainDAO financeMainDAO;
	private FinanceEnquiryController financeEnquiryController;
	private CustomerDAO customerDAO;
	private FinanceTypeDAO financeTypeDAO;

	private static final String ERROR_92021 = "92021";
	private static final String ERROR_DESC_92021 = "There is no approved/active loan with the requested details";

	@Override
	public CustomerData getLoanBasicDetails(String finReference) throws ServiceException {
		logger.debug(Literal.ENTERING);

		CustomerData response = new CustomerData();

		WSReturnStatus wsrs = validateFinReference(finReference);

		if (wsrs != null) {
			logger.debug(Literal.LEAVING);
			return getCustomerData(wsrs);
		}

		logReference(finReference);

		logger.debug("FinReference {}", finReference);

		Long finID = getActiveFinID(finReference);

		if (finID == null) {
			logger.debug(Literal.LEAVING);
			return getResponse(ERROR_92021, ERROR_DESC_92021);
		}

		FinanceDetail fd = financeEnquiryController.getLoanBasicDetails(finID);

		response = prepareCustomerData(fd.getCustomerDetails());

		response.setLoanDetail(prepareLoanDetail(fd));

		logger.debug(Literal.LEAVING);

		return response;

	}

	@Override
	public LoanDetail getRepaymentDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus wsrs = validateFinReference(finReference);

		if (wsrs != null) {
			logger.debug(Literal.LEAVING);
			return getLoanDetail(wsrs);
		}

		logReference(finReference);

		logger.debug("FinReference {}", finReference);

		Long finID = getActiveFinID(finReference);

		if (finID == null) {
			logger.debug(Literal.LEAVING);
			WSReturnStatus failedStatus = getFailedStatus(ERROR_92021, ERROR_DESC_92021);
			return getLoanDetail(failedStatus);
		}

		logger.debug(Literal.LEAVING);

		return prepareLoanDetail(financeEnquiryController.getLoanDetails(finID));
	}

	@Override
	public CustomerData getCustomerData(String finReference) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus wsrs = validateFinReference(finReference);

		if (wsrs != null) {
			logger.debug(Literal.LEAVING);
			return getCustomerData(wsrs);
		}

		logReference(finReference);

		logger.debug("FinReference {}", finReference);

		FinanceMain fm = financeMainDAO.getBasicDetails(finReference, TableType.MAIN_TAB);

		if (fm == null) {
			logger.debug(Literal.LEAVING);
			return getResponse(ERROR_92021, ERROR_DESC_92021);
		}

		logger.debug(Literal.LEAVING);

		return prepareCustomerData(financeEnquiryController.getCustomerDetails(fm.getCustID()));
	}

	@Override
	public List<CustomerData> getByPhoneNumber(String phoneNumber) {
		logger.debug(Literal.ENTERING);

		List<CustomerData> cdList = new ArrayList<>();

		WSReturnStatus wsrs = validateMobileNumber(phoneNumber);

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		logReference(phoneNumber);

		logger.debug("Phone Number {}", phoneNumber);

		List<Long> list = customerDAO.getByPhoneNumber(phoneNumber, TableType.MAIN_TAB);

		if (CollectionUtils.isEmpty(list)) {
			cdList.add(getResponse(ERROR_92021, ERROR_DESC_92021));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		List<CustomerData> customerData = getCustomerData(cdList, list);

		if (CollectionUtils.isEmpty(customerData)) {
			customerData.add(getResponse(ERROR_92021, ERROR_DESC_92021));
		}

		return customerData;
	}

	@Override
	public List<CustomerData> getByAccNumber(String accNumber) {
		logger.debug(Literal.ENTERING);

		List<CustomerData> cdList = new ArrayList<>();

		WSReturnStatus wsrs = validateBankAccountNumber(accNumber);

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		logReference(accNumber);

		logger.debug("Account Number {}", accNumber);

		List<Long> list = customerDAO.getByAccNumber(accNumber, TableType.MAIN_TAB);

		list = list.stream().distinct().collect(Collectors.toList());

		if (CollectionUtils.isEmpty(list)) {
			cdList.add(getResponse(ERROR_92021, ERROR_DESC_92021));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		List<CustomerData> customerData = getCustomerData(cdList, list);

		if (CollectionUtils.isEmpty(customerData)) {
			customerData.add(getResponse(ERROR_92021, ERROR_DESC_92021));
		}

		return customerData;
	}

	@Override
	public List<CustomerData> getByShrtName(String shrtName) {
		logger.debug(Literal.ENTERING);

		List<CustomerData> cdList = new ArrayList<>();

		WSReturnStatus wsrs = validateCustShrtName(shrtName);

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		logReference(shrtName);

		logger.debug("Customer Name {}", shrtName);

		List<Long> list = customerDAO.getByCustShrtName(shrtName, TableType.MAIN_TAB);

		if (CollectionUtils.isEmpty(list)) {
			cdList.add(getResponse(ERROR_92021, ERROR_DESC_92021));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		List<CustomerData> customerData = getCustomerData(cdList, list);

		if (CollectionUtils.isEmpty(customerData)) {
			customerData.add(getResponse(ERROR_92021, ERROR_DESC_92021));
		}

		return customerData;
	}

	@Override
	public List<CustomerData> getByShrtNameAndMobileNumber(Customer customer) {
		logger.debug(Literal.ENTERING);

		List<CustomerData> cdList = new ArrayList<>();

		String phoneNumber = customer.getPhoneNumber();
		String custShrtName = customer.getCustShrtName();

		WSReturnStatus wsrs = validateCustShrtName(custShrtName);

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		wsrs = validateMobileNumber(phoneNumber);

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		logger.debug("Phone Number {} and Customer Short Name {}", phoneNumber, custShrtName);

		logKeyFields(phoneNumber, custShrtName);

		List<Long> list = customerDAO.getByCustShrtNameAndPhoneNumber(custShrtName, phoneNumber, TableType.MAIN_TAB);

		if (CollectionUtils.isEmpty(list)) {
			cdList.add(getResponse(ERROR_92021, ERROR_DESC_92021));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		List<CustomerData> customerData = getCustomerData(cdList, list);

		if (CollectionUtils.isEmpty(customerData)) {
			customerData.add(getResponse(ERROR_92021, ERROR_DESC_92021));
		}

		return customerData;
	}

	@Override
	public List<CustomerData> getByShrtNameAndDateOfBirth(Customer customer) {
		logger.debug(Literal.ENTERING);

		List<CustomerData> cdList = new ArrayList<>();

		Date dateOfBirth = customer.getCustDOB();
		String shrtName = customer.getCustShrtName();

		WSReturnStatus wsrs = validateDob(dateOfBirth, SysParamUtil.getAppDate());

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		wsrs = validateCustShrtName(shrtName);

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		logKeyFields(shrtName, dateOfBirth);

		String strDOB = DateUtil.formatToLongDate(dateOfBirth);
		logger.debug("Customer Short Name {} and Date of Birth {}", shrtName, strDOB);

		List<Long> list = customerDAO.getByCustShrtNameAndDOB(shrtName, dateOfBirth, TableType.MAIN_TAB);

		if (CollectionUtils.isEmpty(list)) {
			cdList.add(getResponse(ERROR_92021, ERROR_DESC_92021));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		List<CustomerData> customerData = getCustomerData(cdList, list);

		if (CollectionUtils.isEmpty(customerData)) {
			customerData.add(getResponse(ERROR_92021, ERROR_DESC_92021));
		}

		return customerData;
	}

	@Override
	public List<CustomerData> getByPanNumber(String panNumber) {
		logger.debug(Literal.ENTERING);

		List<CustomerData> cdList = new ArrayList<>();

		WSReturnStatus wsrs = validatePANNumner(panNumber);

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		logReference(panNumber);

		logger.debug("Pan Number {}", panNumber);

		List<Long> list = customerDAO.getByCustCRCPR(panNumber, TableType.MAIN_TAB);

		if (CollectionUtils.isEmpty(list)) {
			cdList.add(getResponse(ERROR_92021, ERROR_DESC_92021));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		List<CustomerData> customerData = getCustomerData(cdList, list);

		if (CollectionUtils.isEmpty(customerData)) {
			customerData.add(getResponse(ERROR_92021, ERROR_DESC_92021));
		}

		return customerData;
	}

	@Override
	public List<CustomerData> getByShrtNameAndPanNumber(Customer customer) {
		logger.debug(Literal.ENTERING);

		List<CustomerData> cdList = new ArrayList<>();

		String panNumber = customer.getCustCRCPR();
		String shrtName = customer.getCustShrtName();

		WSReturnStatus wsrs = validatePANNumner(panNumber);

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		wsrs = validateCustShrtName(shrtName);

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		logKeyFields(shrtName, panNumber);

		logger.debug("Customer Short Name {} and Pan Number {}", shrtName, panNumber);

		List<Long> list = customerDAO.getByCustShrtNameAndPANNumber(shrtName, panNumber, TableType.MAIN_TAB);

		if (CollectionUtils.isEmpty(list)) {
			cdList.add(getResponse(ERROR_92021, ERROR_DESC_92021));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		List<CustomerData> customerData = getCustomerData(cdList, list);

		if (CollectionUtils.isEmpty(customerData)) {
			customerData.add(getResponse(ERROR_92021, ERROR_DESC_92021));
		}

		return customerData;
	}

	@Override
	public List<CustomerData> getByNameAndEMIAmount(FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		List<CustomerData> cdList = new ArrayList<>();

		String shrtName = fm.getCustShrtName();
		BigDecimal nextRepayAmt = fm.getRepayAmount();
		Date appDate = SysParamUtil.getAppDate();

		WSReturnStatus wsrs = validateCustShrtName(shrtName);

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		logKeyFields(shrtName, nextRepayAmt);

		logger.debug("Customer Short Name {} and Next Instalment Amount {}", shrtName, nextRepayAmt);

		List<Long> list = customerDAO.getByCustName(shrtName, TableType.MAIN_TAB);

		if (CollectionUtils.isEmpty(list)) {
			cdList.add(getResponse(ERROR_92021, ERROR_DESC_92021));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		Date businessDate = appDate;
		for (Long custID : list) {
			List<FinanceDetail> fdList = new ArrayList<>();
			List<LoanDetail> ldList = new ArrayList<>();
			List<Long> finIDList = financeMainDAO.getFinIDsByCustID(custID, TableType.MAIN_TAB);

			if (CollectionUtils.isEmpty(finIDList)) {
				continue;
			}

			for (Long finID : finIDList) {
				FinanceDetail fd = financeEnquiryController.getLoanDetails(finID);
				Date maturityDate = fd.getFinScheduleData().getFinanceMain().getMaturityDate();
				if (appDate.compareTo(maturityDate) >= 0) {
					businessDate = DateUtil.addDays(maturityDate, -1);
				}
				BigDecimal nextInstalment = SchdUtil.getNextEMI(businessDate,
						fd.getFinScheduleData().getFinanceScheduleDetails());
				if (nextRepayAmt.compareTo(nextInstalment) != 0) {
					continue;
				}
				fdList.add(fd);
			}

			if (CollectionUtils.isEmpty(fdList)) {
				continue;
			}

			CustomerDetails cd = financeEnquiryController.getCustomerDetails(custID);
			CustomerData custData = prepareCustomerData(cd);

			for (FinanceDetail finDetail : fdList) {
				LoanDetail ld = prepareLoanDetail(finDetail);
				ldList.add(ld);
			}

			if (CollectionUtils.isEmpty(fdList)) {
				continue;
			}

			custData.setLoanDetails(ldList);
			cdList.add(custData);
		}

		logger.debug(Literal.LEAVING);

		if (CollectionUtils.isEmpty(cdList)) {
			cdList.add(getResponse(ERROR_92021, ERROR_DESC_92021));
		}

		return cdList;
	}

	@Override
	public List<CustomerData> getByProductShrtNameAndDateOfBirth(Customer customer) {
		logger.debug(Literal.ENTERING);

		List<CustomerData> cdList = new ArrayList<>();

		Date dateOfBirth = customer.getCustDOB();
		String shrtName = customer.getCustShrtName();
		String product = customer.getProduct();

		WSReturnStatus wsrs = validateDob(dateOfBirth, SysParamUtil.getAppDate());

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		wsrs = validateCustShrtName(shrtName);

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		wsrs = validateFinTye(product);

		if (wsrs != null) {
			cdList.add(getCustomerData(wsrs));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		logKeyFields(shrtName, dateOfBirth, product);

		String strDOB = DateUtil.formatToLongDate(dateOfBirth);

		logger.debug("Customer Short Name {} , Date Of Birth {} and Product {}", shrtName, strDOB, product);

		List<Long> list = customerDAO.getByCustShrtNameDOBAndFinType(shrtName, dateOfBirth, product,
				TableType.MAIN_TAB);

		if (CollectionUtils.isEmpty(list)) {
			cdList.add(getResponse(ERROR_92021, ERROR_DESC_92021));
			logger.debug(Literal.LEAVING);
			return cdList;
		}

		return getCustomerData(cdList, list.stream().distinct().collect(Collectors.toList()));
	}

	@Override
	public CustomerData getCustomerDataByCIF(String cif) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus wsrs = validateCustomerIF(cif);

		if (wsrs != null) {
			logger.debug(Literal.LEAVING);
			return getCustomerData(wsrs);
		}

		logReference(cif);

		logger.debug("Customer CIF {}", cif);

		Long custID = customerDAO.getCustIDByCIF(cif);

		if (custID == 0) {
			logger.debug(Literal.LEAVING);
			return getResponse(ERROR_92021, ERROR_DESC_92021);
		}

		logger.debug(Literal.LEAVING);

		return prepareCustomerData(financeEnquiryController.getCustomerDetails(custID));
	}

	private Long getActiveFinID(String finReference) {
		return financeMainDAO.getActiveFinID(finReference, TableType.MAIN_TAB);
	}

	private List<CustomerData> getCustomerData(List<CustomerData> cdList, List<Long> list) {

		for (Long custID : list) {
			List<FinanceDetail> fdList = new ArrayList<>();
			List<LoanDetail> ldList = new ArrayList<>();
			List<Long> finIDList = financeMainDAO.getFinIDsByCustID(custID, TableType.MAIN_TAB);

			if (CollectionUtils.isEmpty(finIDList)) {
				continue;
			}

			for (Long finID : finIDList) {
				fdList.add(financeEnquiryController.getLoanDetails(finID));
			}

			CustomerData custData = prepareCustomerData(financeEnquiryController.getCustomerDetails(custID));

			for (FinanceDetail fd : fdList) {
				ldList.add(prepareLoanDetail(fd));
			}

			custData.setLoanDetails(ldList);
			cdList.add(custData);
		}

		logger.debug(Literal.LEAVING);

		return cdList;
	}

	private WSReturnStatus validateFinTye(String product) {
		if (StringUtils.isEmpty(product)) {
			return getFailedStatus("90126");
		}

		return financeTypeDAO.isFinTypeExists(product) ? null : getFailedStatus("90701", "Product", product);
	}

	private WSReturnStatus validatePANNumner(String custCRCPR) {
		if (StringUtils.isEmpty(custCRCPR)) {
			return getFailedStatus("90502", "PAN Number");
		}

		String panRegex = PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_PANNUMBER);
		if (!Pattern.compile(panRegex).matcher(custCRCPR).matches()) {
			return getFailedStatus("90251");
		}
		return null;
	}

	private WSReturnStatus validateDob(Date dob, Date businessDate) {
		if (dob == null) {
			return getFailedStatus("90502", "Date Of Birth");
		}

		dob = DateUtil.getDatePart(dob);

		if (dob.compareTo(businessDate) >= 0) {
			return getFailedStatus("90319", "Date Of Birth");
		}

		return null;
	}

	private WSReturnStatus validateCustShrtName(String custShrtName) {
		if (StringUtils.isEmpty(custShrtName)) {
			return getFailedStatus("90502", "Customer Short Name");
		}

		if (custShrtName.length() < 4) {
			return getFailedStatus("30569", "Customer Short Name", "4 characters");
		}

		return null;
	}

	private WSReturnStatus validateBankAccountNumber(String accNumber) {
		return StringUtils.trimToNull(accNumber) == null ? getFailedStatus("90502", "Bank Account Number") : null;
	}

	private WSReturnStatus validateMobileNumber(String phoneNumber) {
		if (StringUtils.isEmpty(phoneNumber)) {
			return getFailedStatus("90502", "Phone Number");
		}

		String mobileRegex = PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_MOBILE);
		if (!Pattern.compile(mobileRegex).matcher(phoneNumber).matches()) {
			return getFailedStatus("90278");
		}
		return null;
	}

	private LoanDetail getLoanDetail(WSReturnStatus wsrs) {
		LoanDetail ld = new LoanDetail();
		ld.setReturnStatus(wsrs);

		return ld;
	}

	private WSReturnStatus validateFinReference(String finReference) {
		return StringUtils.isEmpty(finReference) ? getFailedStatus("90502", "FinReference") : null;
	}

	private WSReturnStatus validateCustomerIF(String cif) {
		return StringUtils.isEmpty(cif) ? getFailedStatus("90502", "Customer CIF") : null;
	}

	private CustomerData getCustomerData(WSReturnStatus wsrs) {
		CustomerData cd = new CustomerData();
		cd.setReturnStatus(wsrs);

		return cd;
	}

	private CustomerData getResponse(String errorCode, String... param) {
		CustomerData cd = new CustomerData();
		cd.setReturnStatus(getFailedStatus(errorCode, param));

		return cd;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceEnquiryController(FinanceEnquiryController financeEnquiryController) {
		this.financeEnquiryController = financeEnquiryController;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}
}
