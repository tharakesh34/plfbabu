package com.pennanttech.pffws;

import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import com.pennant.backend.model.refundupload.RefundUpload;
import com.pennant.ws.exception.ServiceException;

@WebService
public interface RefundUploadSoapService {

	public RefundUpload createRefundUpload(@WebParam(name = "refundUpload") RefundUpload refundUpload)
			throws ServiceException;
}
