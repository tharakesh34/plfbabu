package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.ws.exception.ServiceException;
@WebService
public interface VASSoapService {
	@WebResult(name = "vasConfiguration")
	public VASConfiguration getVASProduct(@WebParam(name = "product") String product) throws ServiceException;

	@WebResult(name = "vasDetail")
	public VASRecording recordVAS(VASRecording vasRecording) throws ServiceException;

	@WebResult(name = "vasConfiguration")
	public WSReturnStatus cancelVAS(VASRecording vasRecording) throws ServiceException;

	@WebResult(name = "vasConfiguration")
	public VASRecording getRecordVAS(VASRecording vasRecording) throws ServiceException;
}
