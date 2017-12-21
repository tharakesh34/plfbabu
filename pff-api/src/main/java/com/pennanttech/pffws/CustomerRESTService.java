package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerDetails;
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

@Produces(MediaType.APPLICATION_JSON)
public interface CustomerRESTService {

	@POST
	@Path("/customerService/createCustomer")
	public CustomerDetails createCustomer(@WebParam(name = "customer") CustomerDetails customerDetails)
			throws ServiceException;

	@POST
	@Path("/customerService/updateCustomer")
	public WSReturnStatus updateCustomer(@WebParam(name = "customer") CustomerDetails customerDetails)
			throws ServiceException;

	@GET
	@Path("/customerService/getCustomerDetails/{cif}")
	public CustomerDetails getCustomerDetails(@PathParam("cif") String custCIF) throws ServiceException;

	@DELETE
	@Path("/customerService/deleteCustomer/{cif}")
	public WSReturnStatus deleteCustomer(@PathParam("cif") String custCIF) throws ServiceException;

	@GET
	@Path("/customerService/getCustomerPersonalInfo/{cif}")
	public CustomerDetails getCustomerPersonalInfo(@PathParam("cif") String custCIF) throws ServiceException;

	@POST
	@Path("/customerService/updateCustomerPersonalInfo")
	public WSReturnStatus updateCustomerPersonalInfo(@WebParam(name = "customer") CustomerDetails customer)
			throws ServiceException;

	@POST
	@Path("/customerService/addCustomerEmployment")
	public EmploymentDetail addCustomerEmployment(@WebParam(name = "customer") EmploymentDetail employmentDetail)
			throws ServiceException;

	@POST
	@Path("/customerService/updateCustomerEmployment")
	public WSReturnStatus updateCustomerEmployment(@WebParam(name = "customer") EmploymentDetail employmentDetail)
			throws ServiceException;

	@GET
	@Path("/customerService/getCustomerEmployment/{cif}")
	public CustomerDetails getCustomerEmployment(@PathParam("cif") String custCIF) throws ServiceException;

	@DELETE
	@Path("/customerService/deleteCustomerEmployment")
	public WSReturnStatus deleteCustomerEmployment(@WebParam(name = "customer") EmploymentDetail employmentDetail)
			throws ServiceException;

	@POST
	@Path("/customerService/addCustomerPhoneNumber")
	public WSReturnStatus addCustomerPhoneNumber(@WebParam(name = "customer") CustPhoneNumber customerPhoneNumber)
			throws ServiceException;

	@POST
	@Path("/customerService/updateCustomerPhoneNumber")
	public WSReturnStatus updateCustomerPhoneNumber(@WebParam(name = "customer") CustPhoneNumber customerPhoneNumber)
			throws ServiceException;

	@GET
	@Path("/customerService/getCustomerPhoneNumbers/{cif}")
	public CustomerDetails getCustomerPhoneNumbers(@PathParam("cif") String custCIF) throws ServiceException;

	@DELETE
	@Path("/customerService/deleteCustomerPhoneNumber")
	public WSReturnStatus deleteCustomerPhoneNumber(@WebParam(name = "customer") CustPhoneNumber customerPhoneNumber)
			throws ServiceException;

	@POST
	@Path("/customerService/addCustomerAddress")
	public WSReturnStatus addCustomerAddress(@WebParam(name = "customer") CustAddress custAddress)
			throws ServiceException;

	@POST
	@Path("/customerService/updateCustomerAddress")
	public WSReturnStatus updateCustomerAddress(@WebParam(name = "customer") CustAddress custAddress)
			throws ServiceException;

	@GET
	@Path("/customerService/getCustomerAddresses/{cif}")
	public CustomerDetails getCustomerAddresses(@PathParam("cif") String custCIF) throws ServiceException;

	@DELETE
	@Path("/customerService/deleteCustomerAddress")
	public WSReturnStatus deleteCustomerAddress(@WebParam(name = "customer") CustAddress custAddress)
			throws ServiceException;

	@POST
	@Path("/customerService/addCustomerEmail")
	public WSReturnStatus addCustomerEmail(@WebParam(name = "customer") CustEMail custEMail) throws ServiceException;

	@POST
	@Path("/customerService/updateCustomerEmail")
	public WSReturnStatus updateCustomerEmail(@WebParam(name = "customer") CustEMail custEMail) throws ServiceException;

	@GET
	@Path("/customerService/getCustomerEmails/{cif}")
	public CustomerDetails getCustomerEmails(@PathParam("cif") String custCIF) throws ServiceException;

	@DELETE
	@Path("/customerService/deleteCustomerEmail")
	public WSReturnStatus deleteCustomerEmail(@WebParam(name = "customer") CustEMail custEMail) throws ServiceException;

	@POST
	@Path("/customerService/addCustomerIncome")
	public WSReturnStatus addCustomerIncome(@WebParam(name = "customer") CustomerIncomeDetail customerIncomeDetail)
			throws ServiceException;

	@POST
	@Path("/customerService/updateCustomerIncome")
	public WSReturnStatus updateCustomerIncome(@WebParam(name = "customer") CustomerIncomeDetail customerIncomeDetail)
			throws ServiceException;

	@GET
	@Path("/customerService/getCustomerIncomes/{cif}")
	public CustomerDetails getCustomerIncomes(@PathParam("cif") String custCIF) throws ServiceException;

	@DELETE
	@Path("/customerService/deleteCustomerIncome")
	public WSReturnStatus deleteCustomerIncome(@WebParam(name = "customer") CustomerIncomeDetail customerIncomeDetail)
			throws ServiceException;

	@POST
	@Path("/customerService/addCustomerBankingInformation")
	public CustomerBankInfoDetail addCustomerBankingInformation(
			@WebParam(name = "customer") CustomerBankInfoDetail customerBankInfoDetail) throws ServiceException;

	@POST
	@Path("/customerService/updateCustomerBankingInformation")
	public WSReturnStatus updateCustomerBankingInformation(
			@WebParam(name = "customer") CustomerBankInfoDetail customerBankInfoDetail) throws ServiceException;

	@GET
	@Path("/customerService/getCustomerBankingInformation/{cif}")
	public CustomerDetails getCustomerBankingInformation(@PathParam("cif") String custCIF) throws ServiceException;

	@DELETE
	@Path("/customerService/deleteCustomerBankingInformation")
	public WSReturnStatus deleteCustomerBankingInformation(
			@WebParam(name = "customer") CustomerBankInfoDetail customerBankInfoDetail) throws ServiceException;

	@POST
	@Path("/customerService/addCustomerAccountBehaviour")
	public CustomerChequeInfoDetail addCustomerAccountBehaviour(
			@WebParam(name = "customer") CustomerChequeInfoDetail customerChequeInfoDetail) throws ServiceException;

	@POST
	@Path("/customerService/updateCustomerAccountBehaviour")
	public WSReturnStatus updateCustomerAccountBehaviour(
			@WebParam(name = "customer") CustomerChequeInfoDetail customerChequeInfoDetail) throws ServiceException;

	@GET
	@Path("/customerService/getCustomerAccountBehaviour/{cif}")
	public CustomerDetails getCustomerAccountBehaviour(@PathParam("cif") String custCIF) throws ServiceException;

	@DELETE
	@Path("/customerService/deleteCustomerAccountBehaviour")
	public WSReturnStatus deleteCustomerAccountBehaviour(
			@WebParam(name = "customer") CustomerChequeInfoDetail customerChequeInfoDetail) throws ServiceException;

	@POST
	@Path("/customerService/addCustomerExternalLiability")
	public CustomerExtLiabilityDetail addCustomerExternalLiability(
			@WebParam(name = "customer") CustomerExtLiabilityDetail customerExtLiabilityDetail) throws ServiceException;

	@POST
	@Path("/customerService/updateCustomerExternalLiability")
	public WSReturnStatus updateCustomerExternalLiability(
			@WebParam(name = "customer") CustomerExtLiabilityDetail customerExtLiabilityDetail) throws ServiceException;

	@GET
	@Path("/customerService/getCustomerExternalLiabilities/{cif}")
	public CustomerDetails getCustomerExternalLiabilities(@PathParam("cif") String custCIF) throws ServiceException;

	@DELETE
	@Path("/customerService/deleteCustomerExternalLiability")
	public WSReturnStatus deleteCustomerExternalLiability(
			@WebParam(name = "customer") CustomerExtLiabilityDetail customerExtLiabilityDetail) throws ServiceException;

	@POST
	@Path("/customerService/addCustomerDocument")
	public WSReturnStatus addCustomerDocument(@WebParam(name = "customer") CustomerDocumentDetail customerDocumentDetail)
			throws ServiceException;

	@POST
	@Path("/customerService/updateCustomerDocument")
	public WSReturnStatus updateCustomerDocument(
			@WebParam(name = "customer") CustomerDocumentDetail customerDocumentDetail) throws ServiceException;

	@GET
	@Path("/customerService/getCustomerDocuments/{cif}")
	public CustomerDetails getCustomerDocuments(@PathParam("cif") String custCIF) throws ServiceException;

	@DELETE
	@Path("/customerService/deleteCustomerDocument")
	public WSReturnStatus deleteCustomerDocument(
			@WebParam(name = "customer") CustomerDocumentDetail customerDocumentDetail) throws ServiceException;
}
