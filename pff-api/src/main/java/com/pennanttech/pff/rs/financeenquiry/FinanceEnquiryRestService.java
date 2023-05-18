package com.pennanttech.pff.rs.financeenquiry;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.loanbalance.LoanBalance;
import com.pennant.backend.model.loanenquiryresponse.LoanEnquiryResponse;
import com.pennant.backend.model.sourcingdetails.SourcingDetails;
import com.pennant.ws.exception.ServiceException;

import jakarta.jws.WebParam;

@Produces("application/json")
public interface FinanceEnquiryRestService {

	@GET
	@Path("/financeEnquiry/getLoanBasicDetails/{finReference}")
	LoanEnquiryResponse getLoanBasicDetails(@PathParam("finReference") String finReference) throws ServiceException;

	@GET
	@Path("/financeEnquiry/getRepaymetDetails/{finReference}")
	LoanEnquiryResponse getRepaymentDetails(@PathParam("finReference") String finReference);

	@GET
	@Path("/financeEnquiry/getCustomerData/{finReference}")
	LoanEnquiryResponse getCustomerData(@PathParam("finReference") String finReference);

	@GET
	@Path("/financeEnquiry/getLoansByPhoneNumber/{phoneNumber}")
	LoanEnquiryResponse getByPhoneNumber(@PathParam("phoneNumber") String phoneNumber);

	@GET
	@Path("/financeEnquiry/getLoansByAccountNumber/{accNumber}")
	LoanEnquiryResponse getByAccNumber(@PathParam("accNumber") String accNumber);

	@GET
	@Path("/financeEnquiry/getLoansByCustomerName/{shrtName}")
	LoanEnquiryResponse getByShrtName(@PathParam("shrtName") String shrtName);

	@POST
	@Path("/financeEnquiry/getLoansByNameAndMobileNumber")
	LoanEnquiryResponse getByShrtNameAndMobileNumber(@WebParam(name = "customer") Customer customer);

	@POST
	@Path("/financeEnquiry/getLoansByNameAndDateOfBirth")
	LoanEnquiryResponse getByShrtNameAndDateOfBirth(@WebParam(name = "customer") Customer customer);

	@GET
	@Path("/financeEnquiry/getLoansByPANNumber/{custCRCPR}")
	LoanEnquiryResponse getByPanNumber(@PathParam("custCRCPR") String custCRCPR);

	@POST
	@Path("/financeEnquiry/getLoansByNameAndPanNumber")
	LoanEnquiryResponse getByShrtNameAndPanNumber(@WebParam(name = "customer") Customer customer);

	@POST
	@Path("/financeEnquiry/getLoansByProductShrtNameAndDateOfBirth")
	LoanEnquiryResponse getByProductShrtNameAndDateOfBirth(@WebParam(name = "customer") Customer customer);

	@GET
	@Path("/financeEnquiry/getCustomerDataByCIF/{custCIF}")
	LoanEnquiryResponse getCustomerDataByCIF(@PathParam("custCIF") String cif);

	@POST
	@Path("/financeEnquiry/getLoansByNameAndEMIAmount")
	LoanEnquiryResponse getByNameAndEMIAmount(@WebParam(name = "financeDetail") FinanceMain financeMain);

	@GET
	@Path("/financeEnquiry/getPDCEnquiry/{finReference}")
	LoanEnquiryResponse getPDCEnquiry(@PathParam("finReference") String finReference);

	@GET
	@Path("/financeEnquiry/getPDCDetails/{finReference}")
	LoanEnquiryResponse getPDCDetails(@PathParam("finReference") String finReference);

	@GET
	@Path("/financeEnquiry/getSourcingDetails/{finReference}")
	SourcingDetails getSourcingDetails(@PathParam("finReference") String finReference);

	@GET
	@Path("/financeEnquiry/getLoanBalanceDetails/{finReference}")
	LoanBalance getLoanBalanceDetails(@PathParam("finReference") String finReference);

	@GET
	@Path("/financeEnquiry/getGuarantorDetails/{finReference}")
	LoanEnquiryResponse getApplicantsDetails(@PathParam("finReference") String finReference);

	@GET
	@Path("/financeEnquiry/getRateChangeDetails/{finReference}")
	LoanEnquiryResponse getRateChangeDetails(@PathParam("finReference") String finReference);
}
