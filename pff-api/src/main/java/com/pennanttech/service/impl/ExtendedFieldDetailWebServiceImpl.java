package com.pennanttech.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.solutionfactory.ExtendedFieldDetailService;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.ExtendedFieldDetailRestService;
import com.pennanttech.pffws.ExtendedFieldDetailSoapService;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class ExtendedFieldDetailWebServiceImpl implements ExtendedFieldDetailRestService, ExtendedFieldDetailSoapService {
	private static final Logger			logger	= Logger.getLogger(ExtendedFieldDetailWebServiceImpl.class);

	private ExtendedFieldDetailService	extendedFieldDetailService;

	/**
	 * get the ExtendedFieldHeader by the given module and SubModule code.
	 * 
	 * @param extendedFieldHeader
	 * @return ExtendedFieldHeader
	 * @throws ServiceException
	 */
	@Override
	public ExtendedFieldHeader getExtendedFieldDetails(ExtendedFieldHeader extendedFieldHeader)	throws ServiceException {
		logger.debug(Literal.ENTERING);

		ExtendedFieldHeader response = null;
		try {
			//ExtendedFieldHeader Validations
			List<ErrorDetail> errorDetails = extendedFieldDetailService.doValidations(extendedFieldHeader);
			if (errorDetails.isEmpty()) {
				response = extendedFieldDetailService.getExtendedFieldHeaderByModuleName(extendedFieldHeader.getModuleName(),
						extendedFieldHeader.getSubModuleName(), "");
				if (response != null) {
					List<ExtendedFieldDetail> extendedFieldDetails = 
							extendedFieldDetailService.getExtendedFieldDetailByModuleID(response.getModuleId(), "");
					response.setExtendedFieldDetails(extendedFieldDetails);
				} else {
					response = new ExtendedFieldHeader();
					doEmptyResponseObject(response);
					String[] valueParm = new String[2];
					valueParm[0] = extendedFieldHeader.getModuleName();
					valueParm[1] = extendedFieldHeader.getSubModuleName();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90351", valueParm));
				}
			} else {
				for (ErrorDetail errorDetail : errorDetails) {
					response = new ExtendedFieldHeader();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			logger.debug(Literal.LEAVING);
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new ExtendedFieldHeader();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		return response;
	}

	/**
	 * Nullify the un-necessary objects to prepare response in a structured format specified in API.
	 * 
	 * @param response
	 */
	private void doEmptyResponseObject(ExtendedFieldHeader response) {
		response.setModuleName(null);
		response.setSubModuleName(null);
	}

	@Autowired
	public void setExtendedFieldDetailService(ExtendedFieldDetailService extendedFieldDetailService) {
		this.extendedFieldDetailService = extendedFieldDetailService;
	}
}