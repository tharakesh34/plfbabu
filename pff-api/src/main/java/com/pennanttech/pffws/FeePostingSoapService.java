package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.ws.exception.ServiceException;

@WebService
public interface FeePostingSoapService {

	public WSReturnStatus doFeePostings(@WebParam(name = "feePostings") FeePostings feePostings) throws ServiceException;
}
