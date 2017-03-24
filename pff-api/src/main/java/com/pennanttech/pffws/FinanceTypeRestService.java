package com.pennanttech.pffws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.financetype.FinanceTypeRequest;
import com.pennanttech.ws.model.financetype.FinanceTypeResponse;
import com.pennanttech.ws.model.financetype.ProductType;

@Produces("application/json")
public interface FinanceTypeRestService {

	@POST
	@Path("/financeType/getFinanceType")
	public FinanceTypeResponse getFinanceTypeDetails(FinanceTypeRequest finTypeReq) throws ServiceException;
	
	@GET
	@Path("/stepPolicy/getStepPolicy/{policyCode}")
	public StepPolicyHeader getStepPolicyDetails(@PathParam("policyCode") String policyCode) throws ServiceException;
	
	@GET
	@Path("/productType/getLoanTypes/{productCode}")
	public ProductType getLoanTypes(@PathParam("productCode") String productCode) throws ServiceException;
}
