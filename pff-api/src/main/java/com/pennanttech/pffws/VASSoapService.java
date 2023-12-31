package com.pennanttech.pffws;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.vas.VASRecordingDetail;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface VASSoapService {
	@WebResult(name = "vasConfiguration")
	public VASConfiguration getVASProduct(@WebParam(name = "product") String product) throws ServiceException;

	@WebResult(name = "vasDetail")
	public VASRecording recordVAS(VASRecording vasRecording) throws ServiceException;

	@WebResult(name = "vasRecording")
	public WSReturnStatus cancelVAS(VASRecording vasRecording) throws ServiceException;

	@WebResult(name = "vasRecording")
	public VASRecording getRecordVAS(VASRecording vasRecording) throws ServiceException;

	@WebResult(name = "vasRecording")
	public VASRecordingDetail getVASRecordings(VASRecording vasRecording) throws ServiceException;

	@WebResult(name = "vasRecording")
	public VASRecording pendingRecordVAS(VASRecording vasRecording) throws ServiceException;
}
