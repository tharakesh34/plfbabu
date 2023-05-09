package com.pennanttech.pff.rs.financeenquiry;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.customerdata.CustomerData;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.loandetail.LoanDetail;
import com.pennant.ws.exception.ServiceException;

import jakarta.jws.WebParam;

@Produces("application/json")
public interface FinanceEnquiryRestService {

	@GET
	@Path("/financeEnquiry/getLoanBasicDetails/{finReference}")
	CustomerData getLoanBasicDetails(@PathParam("finReference") String finReference) throws ServiceException;

	@GET
	@Path("/financeEnquiry/getRepaymetDetails/{finReference}")
	LoanDetail getRepaymentDetails(@PathParam("finReference") String finReference);

	@GET
	@Path("/financeEnquiry/getCustomerData/{finReference}")
	CustomerData getCustomerData(@PathParam("finReference") String finReference);

	@GET
	@Path("/financeEnquiry/getLoansByPhoneNumber/{phoneNumber}")
	List<CustomerData> getByPhoneNumber(@PathParam("phoneNumber") String phoneNumber);

	@GET
	@Path("/financeEnquiry/getLoansByAccountNumber/{accNumber}")
	List<CustomerData> getByAccNumber(@PathParam("accNumber") String accNumber);

	@GET
	@Path("/financeEnquiry/getLoansByCustomerName/{shrtName}")
	List<CustomerData> getByShrtName(@PathParam("shrtName") String shrtName);

	@POST
	@Path("/financeEnquiry/getLoansByNameAndMobileNumber")
	List<CustomerData> getByShrtNameAndMobileNumber(@WebParam(name = "customer") Customer customer);

	@POST
	@Path("/financeEnquiry/getLoansByNameAndDateOfBirth")
	List<CustomerData> getByShrtNameAndDateOfBirth(@WebParam(name = "customer") Customer customer);

	@GET
	@Path("/financeEnquiry/getLoansByPANNumber/{custCRCPR}")
	List<CustomerData> getByPanNumber(@PathParam("custCRCPR") String custCRCPR);

	@POST
	@Path("/financeEnquiry/getLoansByNameAndPanNumber")
	List<CustomerData> getByShrtNameAndPanNumber(@WebParam(name = "customer") Customer customer);

	@POST
	@Path("/financeEnquiry/getLoansByProductShrtNameAndDateOfBirth")
	List<CustomerData> getByProductShrtNameAndDateOfBirth(@WebParam(name = "customer") Customer customer);

	@GET
	@Path("/financeEnquiry/getCustomerDataByCIF/{custCIF}")
	CustomerData getCustomerDataByCIF(@PathParam("custCIF") String cif);

	@POST
	@Path("/financeEnquiry/getLoansByNameAndEMIAmount")
	List<CustomerData> getByNameAndEMIAmount(@WebParam(name = "financeDetail") FinanceMain financeMain);
}
