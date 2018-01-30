package com.pennanttech.ws.service;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.util.APIHeader;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.service.errordetail.ErrorDetailService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.log.model.APILogDetail;

@Service("apiErrorHandlerService")
public class APIErrorHandlerService {

	private static ErrorDetailService errorDetailService;
	

	/**
	 * Method for prepare and return default Success return status with below details.<br>
	 * 	Return Code:0000<br>
	 * 	Return Text:Success.
	 * 
	 * @return WSReturnStatus
	 */
	public static WSReturnStatus getSuccessStatus() {
		WSReturnStatus status = new WSReturnStatus();
		status.setReturnCode(APIConstants.RES_SUCCESS_CODE);
		status.setReturnText(APIConstants.RES_SUCCESS_DESC);
		return status;
	}
	
	/**
	 * Method for prepare and return default Failed status with below details.<br>
	 * 	Return Code:9999<br>
	 * 	Return Text:"Failed to process the request".
	 * 
	 * @return WSReturnStatus 
	 */
	public static WSReturnStatus getFailedStatus() {
		WSReturnStatus status = new WSReturnStatus();
		status.setReturnCode(APIConstants.RES_FAILED_CODE);
		status.setReturnText(APIConstants.RES_FAILED_DESC);
		return status;
	}
	
	/**
	 * Method for prepare Failed status object with dynamic parameters.<br>
	 * 
	 * @param errorCode
	 * @param errorDesc
	 * 
	 * @return WSReturnStatus
	 */
	public static WSReturnStatus getFailedStatus(String errorCode, String errorDesc) {
		WSReturnStatus status = new WSReturnStatus();
		status.setReturnCode(errorCode);
		status.setReturnText(errorDesc);
		return status;
	}
	
	/**
	 * fetch error description from ErrorDetails table and prepare response object.<br>
	 * 	Response object includes ErrorCode and description.
	 * 
	 * @param errorCode
	 * @return
	 */
	public static WSReturnStatus getFailedStatus(String errorCode) {
		WSReturnStatus status = new WSReturnStatus();
		ErrorDetail errorDetail = errorDetailService.getErrorDetailById(errorCode);
		if(errorDetail != null) {
			status.setReturnCode(errorCode);
			status.setReturnText(errorDetail.getMessage());
		}
		return status;
	}
	
	/**
	 * Method for prepare response object with specified errorCode and description.
	 * 
	 * @param errorCode
	 * @param parameter[]
	 * @return
	 */
	public static WSReturnStatus getFailedStatus(String errorCode, String parameter[]) {
		WSReturnStatus status = new WSReturnStatus();
		ErrorDetail errorDetail = errorDetailService.getErrorDetailById(errorCode);
		status.setReturnCode(errorCode);
		if(errorDetail != null) {
			String errorMessage = getErrorMessage(errorDetail.getMessage(), parameter);
			status.setReturnText(errorMessage);
		} else {
			String errorMessage = "";
			status.setReturnText(errorMessage);
		}
		return status;
	}
	
	private static String getErrorMessage(String errorMessage, String[] errorParameters) {
		String error = StringUtils.trimToEmpty(errorMessage);

		if (errorParameters != null) {
			for (int i = 0; i < errorParameters.length; i++) {
				String parameter = StringUtils.trimToEmpty(errorParameters[i]);
				error = error.replace("{" + (i) + "}", parameter);
			}
		}

		for (int i = 0; i < 5; i++) {
			error = error.replace("{" + (i) + "}", "");
		}

		return error;
	}

	/**
	 * Method for logging un-handled exceptions
	 * 
	 * @param e
	 */
	public static void logUnhandledException(Exception exception) {
		StringWriter writer = new StringWriter();
		exception.printStackTrace(new PrintWriter(writer));
		String errorMessage = writer.toString();
		APILogDetail logDetail = (APILogDetail) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_LOG_KEY);
		if (errorMessage.length() > 1990) {
			errorMessage = errorMessage.substring(0, 1990);
		}
		logDetail.setError(errorMessage);
	}

	/**
	 * Method for log the reference.
	 * 
	 * @param reference
	 */
	//TODO: Need to move Method
	public static void logReference(String reference) {
		APILogDetail apiLogDetail = (APILogDetail) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_LOG_KEY);
		if (apiLogDetail != null) {
			apiLogDetail.setReference(reference);
		}
	}
	
	@Autowired
	public void setErrorDetailService(ErrorDetailService errorDetailService) {
		APIErrorHandlerService.errorDetailService = errorDetailService;
	}
}
