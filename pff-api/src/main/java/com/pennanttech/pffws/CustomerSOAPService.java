package com.pennanttech.pffws;

import java.util.List;

import javax.ws.rs.PathParam;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.ProspectCustomerDetails;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.customer.AgreementRequest;
import com.pennanttech.ws.model.customer.CustAddress;
import com.pennanttech.ws.model.customer.CustDedupDetails;
import com.pennanttech.ws.model.customer.CustDedupResponse;
import com.pennanttech.ws.model.customer.CustEMail;
import com.pennanttech.ws.model.customer.CustPhoneNumber;
import com.pennanttech.ws.model.customer.CustValidationResponse;
import com.pennanttech.ws.model.customer.CustomerBankInfoDetail;
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

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface CustomerSOAPService {

	@WebResult(name = "customer")
	public CustomerDetails createCustomer(@WebParam(name = "customer") CustomerDetails customerDetails);

	public WSReturnStatus updateCustomer(@WebParam(name = "customer") CustomerDetails customerDetails);

	@WebResult(name = "customer")
	public CustomerDetails getCustomerDetails(@WebParam(name = "cif") String custCIF) throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus deleteCustomer(@PathParam("cif") String custCIF) throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDetails getCustomerPersonalInfo(@WebParam(name = "cif") String custCIF) throws ServiceException;

	public WSReturnStatus updateCustomerPersonalInfo(@WebParam(name = "customer") CustomerDetails customer)
			throws ServiceException;

	@WebResult(name = "customer")
	public EmploymentDetail addCustomerEmployment(@WebParam(name = "customer") EmploymentDetail employmentDetail)
			throws ServiceException;

	public WSReturnStatus updateCustomerEmployment(@WebParam(name = "customer") EmploymentDetail employmentDetail)
			throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDetails getCustomerEmployment(@WebParam(name = "cif") String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomerEmployment(@WebParam(name = "customer") EmploymentDetail employmentDetail)
			throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus addCustomerPhoneNumber(@WebParam(name = "customer") CustPhoneNumber customerPhoneNumber)
			throws ServiceException;

	public WSReturnStatus updateCustomerPhoneNumber(@WebParam(name = "customer") CustPhoneNumber customerPhoneNumber)
			throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDetails getCustomerPhoneNumbers(@WebParam(name = "cif") String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomerPhoneNumber(@WebParam(name = "customer") CustPhoneNumber customerPhoneNumber)
			throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus addCustomerAddress(@WebParam(name = "customer") CustAddress custAddress)
			throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus updateCustomerAddress(@WebParam(name = "customer") CustAddress custAddress)
			throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDetails getCustomerAddresses(@WebParam(name = "cif") String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomerAddress(@WebParam(name = "customer") CustAddress custAddress)
			throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus addCustomerEmail(@WebParam(name = "customer") CustEMail custEMail) throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus updateCustomerEmail(@WebParam(name = "customer") CustEMail custEMail) throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDetails getCustomerEmails(@WebParam(name = "cif") String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomerEmail(@WebParam(name = "customer") CustEMail custEMail) throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus addCustomerIncome(@WebParam(name = "customer") CustomerIncomeDetail customerIncomeDetail)
			throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus updateCustomerIncome(@WebParam(name = "customer") CustomerIncomeDetail customerIncomeDetail)
			throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDetails getCustomerIncomes(@WebParam(name = "cif") String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomerIncome(@WebParam(name = "customer") CustomerIncomeDetail customerIncomeDetail)
			throws ServiceException;

	@WebResult(name = "customer")
	public CustomerBankInfoDetail addCustomerBankingInformation(
			@WebParam(name = "customer") CustomerBankInfoDetail customerBankInfoDetail) throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus updateCustomerBankingInformation(
			@WebParam(name = "customer") CustomerBankInfoDetail customerBankInfoDetail) throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDetails getCustomerBankingInformation(@WebParam(name = "cif") String custCIF)
			throws ServiceException;

	public WSReturnStatus deleteCustomerBankingInformation(
			@WebParam(name = "customer") CustomerBankInfoDetail customerBankInfoDetail) throws ServiceException;

	@WebResult(name = "customer")
	public CustomerChequeInfoDetail addCustomerAccountBehaviour(
			@WebParam(name = "customer") CustomerChequeInfoDetail customerChequeInfoDetail) throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus updateCustomerAccountBehaviour(
			@WebParam(name = "customer") CustomerChequeInfoDetail customerChequeInfoDetail) throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDetails getCustomerAccountBehaviour(@WebParam(name = "cif") String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomerAccountBehaviour(
			@WebParam(name = "customer") CustomerChequeInfoDetail customerChequeInfoDetail) throws ServiceException;

	@WebResult(name = "customer")
	public CustomerExtLiabilityDetail addCustomerExternalLiability(
			@WebParam(name = "customer") CustomerExtLiabilityDetail customerExtLiabilityDetail) throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus updateCustomerExternalLiability(
			@WebParam(name = "customer") CustomerExtLiabilityDetail customerExtLiabilityDetail) throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDetails getCustomerExternalLiabilities(@WebParam(name = "cif") String custCIF)
			throws ServiceException;

	public WSReturnStatus deleteCustomerExternalLiability(
			@WebParam(name = "customer") CustomerExtLiabilityDetail customerExtLiabilityDetail) throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus addCustomerDocument(
			@WebParam(name = "customer") CustomerDocumentDetail customerDocumentDetail) throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus updateCustomerDocument(
			@WebParam(name = "customer") CustomerDocumentDetail customerDocumentDetail) throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDetails getCustomerDocuments(@WebParam(name = "cif") String custCIF) throws ServiceException;

	public WSReturnStatus deleteCustomerDocument(
			@WebParam(name = "customer") CustomerDocumentDetail customerDocumentDetail) throws ServiceException;

	@WebResult(name = "customer")
	public AgreementData getCustomerAgreement(AgreementRequest agrRequest) throws ServiceException;

	@WebResult(name = "customer")
	public ProspectCustomerDetails getDedupCustomer(
			@WebParam(name = "customerDetails") ProspectCustomerDetails prospectCustomerDetails);

	@WebResult(name = "customer")
	public WSReturnStatus addCreditReviewDetails(
			@WebParam(name = "customerDetails") FinCreditReviewDetailsData finCreditReviewDetailsData);

	@WebResult(name = "customer")
	public CustomerGstInfoDetail addCustomerGstInformation(
			@WebParam(name = "customer") CustomerGstInfoDetail customerGstInfoDetail) throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus updateCustomerGstInformation(
			@WebParam(name = "customer") CustomerGstInfoDetail customerGstInfoDetail) throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDetails getCustomerGstnformation(@WebParam(name = "cif") String custCIF) throws ServiceException;

	WSReturnStatus deleteCustomerGstInformation(
			@WebParam(name = "customer") CustomerGstInfoDetail customerGstInfoDetail) throws ServiceException;

	public CustValidationResponse doCustomerValidation(@WebParam(name = "cif") String custCIF) throws ServiceException;

	@WebResult(name = "customer")
	public CustDedupResponse getCustDedup(CustDedupDetails custDedupDetails) throws ServiceException;

	@WebResult(name = "customer")
	public CustDedupResponse getNegativeListCustomer(CustDedupDetails custDedupDetails) throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDirectorDetail addCustomerDirectorDetail(
			@WebParam(name = "customer") CustomerDirectorDetail customerDirectorDetail) throws ServiceException;

	@WebResult(name = "customer")
	public CustomerDetails getCustomerDirectorDetails(@WebParam(name = "cif") String custCIF) throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus updateCustomerDirectorDetail(
			@WebParam(name = "customer") CustomerDirectorDetail customerDirectorDetail) throws ServiceException;

	@WebResult(name = "customer")
	public WSReturnStatus deleteCustomerDirectorDetail(
			@WebParam(name = "customer") CustomerDirectorDetail customerDirectorDetail) throws ServiceException;

	@WebResult(name = "customer")
	public CustomerExtendedFieldDetails addCustomerExtendedFieldDetails(
			@WebParam(name = "customer") CustomerExtendedFieldDetails customerExtendedFieldDetails)
			throws ServiceException;

	@WebResult(name = "customer")
	public List<CustomerDetails> getSRMCustDetails(@WebParam(name = "customer") SRMCustRequest srmCustRequest)
			throws ServiceException;

}
