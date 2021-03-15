package com.pennanttech.pffws;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.ws.model.presentment.PresentmentResponse;

public interface PresentmentSoapService {

	public PresentmentResponse extractPresentmentDetails(PresentmentHeader presentmentHeader) throws ServiceException;

	public PresentmentResponse approvePresentmentDetails(PresentmentHeader presentmentHeader) throws ServiceException;

	public PresentmentResponse getApprovedPresentment(PresentmentDetail presentmentDetail) throws ServiceException;

	public WSReturnStatus uploadPresentment(Presentment presentment) throws ServiceException;
}
