package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.backend.model.applicationmaster.ReasonCodeResponse;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.deviation.ManualDeviationAuthReq;
import com.pennanttech.ws.model.deviation.ManualDeviationAuthorities;
import com.pennanttech.ws.model.deviation.ManualDeviationList;

@WebService
public interface ApplicationMasterSoapService {

	ReasonCodeResponse getReasonCodeDetails(@WebParam(name = "reasonTypeCode") String reasonTypeCode)
			throws ServiceException;

	@WebResult(name = "manualDeviation")
	ManualDeviationList getManualDeviationList(@WebParam(name = "categorizationCode") String categorizationCode)
			throws ServiceException;

	@WebResult(name = "manDevAuthorities")
	ManualDeviationAuthorities getManualDeviationAuthorities(ManualDeviationAuthReq request) throws ServiceException;

}
