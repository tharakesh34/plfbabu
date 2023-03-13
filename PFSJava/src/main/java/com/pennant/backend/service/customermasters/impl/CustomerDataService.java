package com.pennant.backend.service.customermasters.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.customermasters.CustEmployeeDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.customermasters.CustomerBankInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerCardSalesInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.customermasters.CustomerGstDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.dao.customermasters.CustomerRatingDAO;
import com.pennant.backend.dao.customermasters.DirectorDetailDAO;
import com.pennant.backend.dao.customermasters.GSTDetailDAO;
import com.pennant.backend.model.PrimaryAccount;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.dao.customer.income.IncomeDetailDAO;
import com.pennanttech.pff.dao.customer.liability.ExternalLiabilityDAO;
import com.pennanttech.pff.external.pan.dao.PrimaryAccountDAO;

public class CustomerDataService {
	private static final Logger logger = LogManager.getLogger(CustomerDataService.class);

	private CustomerDAO customerDAO;
	private PrimaryAccountDAO primaryAccountDAO;
	private CustomerEmploymentDetailDAO customerEmploymentDetailDAO;
	private CustEmployeeDetailDAO custEmployeeDetailDAO;
	private IncomeDetailDAO incomeDetailDAO;
	private DirectorDetailDAO directorDetailDAO;
	private CustomerRatingDAO customerRatingDAO;
	private CustomerPhoneNumberDAO customerPhoneNumberDAO;
	private CustomerEMailDAO customerEMailDAO;
	private CustomerBankInfoDAO customerBankInfoDAO;
	private CustomerGstDetailDAO customerGstDetailDAO;
	private CustomerChequeInfoDAO customerChequeInfoDAO;
	private ExternalLiabilityDAO externalLiabilityDAO;
	private CustomerCardSalesInfoDAO customerCardSalesInfoDAO;
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	private CustomerAddresDAO customerAddresDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private GSTDetailDAO gstDetailDAO;

	public CustomerDetails getCustomerChildDetails(long id, String type) {
		logger.debug(Literal.ENTERING);

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(customerDAO.getCustomerByID(id, type));
		customerDetails.setCustID(id);

		customerDetails.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, type));
		customerDetails.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomer(id, type));
		customerDetails.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(id, type));

		logger.debug(Literal.LEAVING);
		return customerDetails;
	}

	public CustomerDetails getCustomerDetailsbyID(long id, boolean reqChildDetails, String type) {
		logger.debug(Literal.ENTERING);

		CustomerDetails cd = new CustomerDetails();
		cd.setCustomer(customerDAO.getCustomerByID(id, type));
		cd.setCustID(id);

		Customer customer = cd.getCustomer();
		PrimaryAccount primaryAccount = primaryAccountDAO.getPrimaryAccountDetails(customer.getCustCRCPR());

		if (primaryAccount != null) {
			customer.setPrimaryIdName(primaryAccount.getDocumentName());
		}

		if (!reqChildDetails) {
			return cd;
		}

		cd.setCustomerDocumentsList(customerDocumentDAO.getCustomerDocumentByCustomer(id, type));
		cd.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, type));
		cd.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomer(id, type));
		cd.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(id, type));
		cd.setCustomerBankInfoList(customerBankInfoDAO.getBankInfoByCustomer(id, type));
		cd.setCustomerGstList(customerGstDetailDAO.getCustomerGSTById(id, type));
		cd.setCustomerChequeInfoList(customerChequeInfoDAO.getChequeInfoByCustomer(id, type));
		cd.setGstDetailsList(gstDetailDAO.getGSTDetailById(id, type));
		cd.setCustomerExtLiabilityList(externalLiabilityDAO.getLiabilities(id, type));
		cd.setCustCardSales(customerCardSalesInfoDAO.getCardSalesInfoByCustomer(id, type));
		cd.setCustFinanceExposureList(customerDAO.getCustomerFinanceDetailById(cd.getCustomer()));

		loadEmployementDetails(id, type, cd);
		loadCustomerBankingInfo(type, cd);
		loadCustomerGSTDetails(type, cd);
		loadCustomerExtLibility(type, cd);
		loadCardSales(type, cd);

		if (ImplementationConstants.ALLOW_CUSTOMER_INCOMES) {
			cd.setCustomerIncomeList(incomeDetailDAO.getIncomesByCustomer(id, type));
		}

		String custCtgCode = customer.getCustCtgCode();
		if (StringUtils.isNotEmpty(custCtgCode) && !PennantConstants.PFF_CUSTCTG_INDIV.equals(custCtgCode)) {
			if (ImplementationConstants.ALLOW_CUSTOMER_SHAREHOLDERS) {
				cd.setCustomerDirectorList(directorDetailDAO.getCustomerDirectorByCustomer(id, type));
			}

			if (ImplementationConstants.ALLOW_CUSTOMER_RATINGS) {
				cd.setRatingsList(customerRatingDAO.getCustomerRatingByCustomer(id, type));
			}
		}
		logger.debug(Literal.LEAVING);
		return cd;
	}

	private void loadCardSales(String type, CustomerDetails cd) {
		List<CustCardSales> custCardSales = cd.getCustCardSales();

		if (CollectionUtils.isEmpty(custCardSales)) {
			return;
		}

		custCardSales.forEach(ccs -> ccs
				.setCustCardMonthSales(customerCardSalesInfoDAO.getCardSalesInfoSubDetailById(ccs.getId(), type)));
	}

	private void loadCustomerExtLibility(String type, CustomerDetails cd) {
		List<CustomerExtLiability> liabilities = cd.getCustomerExtLiabilityList();

		if (CollectionUtils.isEmpty(liabilities)) {
			return;
		}

		liabilities.forEach(lbty -> lbty
				.setExtLiabilitiesPayments(customerExtLiabilityDAO.getExtLiabilitySubDetailById(lbty.getId(), type)));
	}

	private void loadCustomerGSTDetails(String type, CustomerDetails cd) {
		List<CustomerGST> gstList = cd.getCustomerGstList();

		if (CollectionUtils.isEmpty(gstList)) {
			return;
		}

		gstList.forEach(gst -> gst
				.setCustomerGSTDetailslist(customerGstDetailDAO.getCustomerGSTDetailsByCustomer(gst.getId(), type)));
	}

	private void loadCustomerBankingInfo(String type, CustomerDetails cd) {
		List<CustomerBankInfo> bankInfoList = cd.getCustomerBankInfoList();

		if (CollectionUtils.isNotEmpty(bankInfoList)) {
			return;
		}

		for (CustomerBankInfo cbi : bankInfoList) {
			cbi.setBankInfoDetails(customerBankInfoDAO.getBankInfoDetailById(cbi.getBankId(), type));

			List<BankInfoDetail> bankInfoDetails = cbi.getBankInfoDetails();
			if (CollectionUtils.isNotEmpty(bankInfoDetails)) {
				bankInfoDetails.forEach(bid -> bid.setBankInfoSubDetails(
						customerBankInfoDAO.getBankInfoSubDetailById(bid.getBankId(), bid.getMonthYear(), type)));
			}
		}
	}

	private void loadEmployementDetails(long id, String type, CustomerDetails cd) {
		if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
			cd.setEmploymentDetailsList(customerEmploymentDetailDAO.getCustomerEmploymentDetailsByID(id, type));
		} else {
			cd.setCustEmployeeDetail(custEmployeeDetailDAO.getCustEmployeeDetailById(id, type));
		}
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setPrimaryAccountDAO(PrimaryAccountDAO primaryAccountDAO) {
		this.primaryAccountDAO = primaryAccountDAO;
	}

	@Autowired
	public void setCustomerEmploymentDetailDAO(CustomerEmploymentDetailDAO customerEmploymentDetailDAO) {
		this.customerEmploymentDetailDAO = customerEmploymentDetailDAO;
	}

	@Autowired
	public void setCustEmployeeDetailDAO(CustEmployeeDetailDAO custEmployeeDetailDAO) {
		this.custEmployeeDetailDAO = custEmployeeDetailDAO;
	}

	@Autowired
	public void setIncomeDetailDAO(IncomeDetailDAO incomeDetailDAO) {
		this.incomeDetailDAO = incomeDetailDAO;
	}

	@Autowired
	public void setDirectorDetailDAO(DirectorDetailDAO directorDetailDAO) {
		this.directorDetailDAO = directorDetailDAO;
	}

	@Autowired
	public void setCustomerRatingDAO(CustomerRatingDAO customerRatingDAO) {
		this.customerRatingDAO = customerRatingDAO;
	}

	@Autowired
	public void setCustomerPhoneNumberDAO(CustomerPhoneNumberDAO customerPhoneNumberDAO) {
		this.customerPhoneNumberDAO = customerPhoneNumberDAO;
	}

	@Autowired
	public void setCustomerEMailDAO(CustomerEMailDAO customerEMailDAO) {
		this.customerEMailDAO = customerEMailDAO;
	}

	@Autowired
	public void setCustomerBankInfoDAO(CustomerBankInfoDAO customerBankInfoDAO) {
		this.customerBankInfoDAO = customerBankInfoDAO;
	}

	@Autowired
	public void setCustomerGstDetailDAO(CustomerGstDetailDAO customerGstDetailDAO) {
		this.customerGstDetailDAO = customerGstDetailDAO;
	}

	@Autowired
	public void setCustomerChequeInfoDAO(CustomerChequeInfoDAO customerChequeInfoDAO) {
		this.customerChequeInfoDAO = customerChequeInfoDAO;
	}

	@Autowired
	public void setExternalLiabilityDAO(ExternalLiabilityDAO externalLiabilityDAO) {
		this.externalLiabilityDAO = externalLiabilityDAO;
	}

	@Autowired
	public void setCustomerCardSalesInfoDAO(CustomerCardSalesInfoDAO customerCardSalesInfoDAO) {
		this.customerCardSalesInfoDAO = customerCardSalesInfoDAO;
	}

	@Autowired
	public void setCustomerExtLiabilityDAO(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
	}

	@Autowired
	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	@Autowired
	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	@Autowired
	public void setGstDetailDAO(GSTDetailDAO gstDetailDAO) {
		this.gstDetailDAO = gstDetailDAO;
	}

	public Customer getCheckCustomerByCIF(String cif) {
		return customerDAO.getCustomerByCIF(cif, "_View");
	}

}
