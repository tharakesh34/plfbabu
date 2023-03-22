package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.refundupload.RefundUpload;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface RefundUploadRestService {

	@POST
	@Path("/createRefund")
	public RefundUpload createRefundUpload(RefundUpload refundUpload) throws ServiceException;

}
