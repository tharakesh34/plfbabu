package com.pennanttech.service;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.customer.CustAddress;
import com.pennanttech.ws.model.customer.CustEMail;
import com.pennanttech.ws.model.customer.CustPhoneNumber;
import com.pennanttech.ws.model.customer.CustomerBankInfoDetail;
import com.pennanttech.ws.model.customer.CustomerChequeInfoDetail;
import com.pennanttech.ws.model.customer.CustomerDocumentDetail;
import com.pennanttech.ws.model.customer.CustomerExtLiabilityDetail;
import com.pennanttech.ws.model.customer.CustomerIncomeDetail;
import com.pennanttech.ws.model.customer.EmploymentDetail;

public interface CustomerWebService {

	public CustomerDetails createCustomer(CustomerDetails customerDetails) throws PFFInterfaceException;

	public WSReturnStatus updateCustomer(CustomerDetails customerDetails) throws PFFInterfaceException;

	public CustomerDetails getCustomerDetails(String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomer(String custCIF) throws ServiceException;

	public CustomerDetails getCustomerPersonalInfo(String custCIF) throws ServiceException;

	public WSReturnStatus updateCustomerPersonlaInfo(CustomerDetails customer) throws ServiceException;

	public EmploymentDetail addCustomerEmployment(EmploymentDetail employmentDetail) throws ServiceException;

	public WSReturnStatus updateCustomerEmployment(EmploymentDetail employmentDetail) throws ServiceException;

	public CustomerDetails getCustomerEmployment(String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomerEmployment(EmploymentDetail employmentDetail) throws ServiceException;

	public WSReturnStatus addCustomerPhoneNumber(CustPhoneNumber customerPhoneNumber) throws ServiceException;

	public WSReturnStatus updateCustomerPhoneNumber(CustPhoneNumber customerPhoneNumber) throws ServiceException;

	public CustomerDetails getCustomerPhoneNumbers(String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomerPhoneNumber(CustPhoneNumber customerPhoneNumber) throws ServiceException;

	public WSReturnStatus addCustomerAddress(CustAddress custAddress) throws ServiceException;

	public WSReturnStatus updateCustomerAddress(CustAddress custAddress) throws ServiceException;

	public CustomerDetails getCustomerAddresses(String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomerAddress(CustAddress custAddress) throws ServiceException;
	
	public WSReturnStatus addCustomerEmail(CustEMail custEMail)throws ServiceException;
	
	public WSReturnStatus updateCustomerEmail(CustEMail custEMail)throws ServiceException;
	
	public CustomerDetails getCustomerEmails(String custCIF) throws ServiceException;
	
	public WSReturnStatus deleteCustomerEmail(CustEMail custEMail) throws ServiceException;

	public WSReturnStatus addCustomerIncome(CustomerIncomeDetail customerIncomeDetail)throws ServiceException;
	
	public WSReturnStatus updateCustomerIncome(CustomerIncomeDetail customerIncomeDetail)throws ServiceException;
	
	public CustomerDetails getCustomerIncomes(String custCIF) throws ServiceException;
	
	public WSReturnStatus deleteCustomerIncome(CustomerIncomeDetail customerIncomeDetail) throws ServiceException;
	
	public CustomerBankInfoDetail addCustomerBankingInformation(CustomerBankInfoDetail customerBankInfoDetail)throws ServiceException;
	
	public WSReturnStatus updateCustomerBankingInformation(CustomerBankInfoDetail customerBankInfoDetail)throws ServiceException;
	
	public CustomerDetails getCustomerBankingInformation(String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomerBankingInformation(CustomerBankInfoDetail customerBankInfoDetail) throws ServiceException;
	
	public CustomerChequeInfoDetail addCustomerAccountBehaviour(CustomerChequeInfoDetail customerChequeInfoDetail)throws ServiceException;
	
	public WSReturnStatus updateCustomerAccountBehaviour(CustomerChequeInfoDetail customerChequeInfoDetail)throws ServiceException;
	
	public CustomerDetails getCustomerAccountBehaviour(String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomerAccountBehaviour(CustomerChequeInfoDetail customerChequeInfoDetail) throws ServiceException;
	
	public CustomerExtLiabilityDetail addCustomerExternalLiability(CustomerExtLiabilityDetail customerExtLiabilityDetail)throws ServiceException;
	
	public WSReturnStatus updateCustomerExternalLiability(CustomerExtLiabilityDetail customerExtLiabilityDetail)throws ServiceException;
	
	public CustomerDetails getCustomerExternalLiabilities(String custCIF) throws ServiceException;
	
	public WSReturnStatus deleteCustomerExternalLiability(CustomerExtLiabilityDetail customerExtLiabilityDetail) throws ServiceException;
	
	public WSReturnStatus addCustomerDocument(CustomerDocumentDetail customerDocumentDetail)throws ServiceException;
	
	public WSReturnStatus updateCustomerDocument(CustomerDocumentDetail customerDocumentDetail)throws ServiceException;
	
	public CustomerDetails getCustomerDocuments(String custCIF) throws ServiceException;
	
	public WSReturnStatus deleteCustomerDocument(CustomerDocumentDetail customerDocumentDetail) throws ServiceException;
}
