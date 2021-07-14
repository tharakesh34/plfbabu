package com.pennanttech.pffws;

import com.pennant.backend.model.refundupload.RefundUpload;
import com.pennant.ws.exception.ServiceException;

import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService
public interface RefundUploadSoapService {

	public RefundUpload createRefundUpload(@WebParam(name = "refundUpload") RefundUpload refundUpload)
			throws ServiceException;
}
