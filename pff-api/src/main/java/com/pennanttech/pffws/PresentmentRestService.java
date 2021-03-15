package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.ws.model.presentment.PresentmentResponse;

@Produces(MediaType.APPLICATION_JSON)
public interface PresentmentRestService {

	@POST
	@Path("/presentmentService/extractPresentmentDetails")
	public PresentmentResponse extractPresentmentDetails(PresentmentHeader presentmentHeader) throws ServiceException;

	@POST
	@Path("/presentmentService/approvePresentmentDetails")
	public PresentmentResponse approvePresentmentDetails(PresentmentHeader presentmentHeader) throws ServiceException;

	@POST
	@Path("/presentmentService/getApprovedPresentment")
	public PresentmentResponse getApprovedPresentment(PresentmentDetail presentmentDetail) throws ServiceException;

	@POST
	@Path("/presentmentService/uploadPresentment")
	public WSReturnStatus uploadPresentment(Presentment presentment) throws ServiceException;

}
