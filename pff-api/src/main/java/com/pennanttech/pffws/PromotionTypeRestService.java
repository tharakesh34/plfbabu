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

@Produces("application/json")
public interface PromotionTypeRestService {

	@POST
	@Path("/promotionType/getPromotion")
	public FinanceTypeResponse getPromotion(FinanceTypeRequest finTypeReq) throws ServiceException;
	
	@GET
	@Path("/stepPolicy/getStepPolicy/{policyCode}")
	public StepPolicyHeader getStepPolicy(@PathParam("policyCode") String policyCode) throws ServiceException;
	
	@GET
	@Path("/promotionType/getPromotions/{productCode}")
	public FinanceTypeResponse getPromotions(@PathParam("productCode") String productCode) throws ServiceException;
}
