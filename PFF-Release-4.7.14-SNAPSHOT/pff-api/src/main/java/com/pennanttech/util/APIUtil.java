/*package com.pennanttech.util;

import com.pennant.app.util.WSReturnStatus;

public class APIUtil {

	*//**
	 * Method for prepare and return default Success return status with below details.<br>
	 * 	Return Code:0000<br>
	 * 	Return Text:Success.
	 * 
	 * @return WSReturnStatus
	 *//*
	public static WSReturnStatus getSuccessStatus() {
		WSReturnStatus status = new WSReturnStatus();
		status.setReturnCode(APIConstants.RES_SUCCESS_CODE);
		status.setReturnText(APIConstants.RES_SUCCESS_DESC);
		return status;
	}
	
	*//**
	 * Method for prepare and return default Failed status with below details.<br>
	 * 	Return Code:9999<br>
	 * 	Return Text:"Failed to process the request".
	 * 
	 * @return WSReturnStatus 
	 *//*
	public static WSReturnStatus getFailedStatus() {
		WSReturnStatus status = new WSReturnStatus();
		status.setReturnCode(APIConstants.RES_FAILED_CODE);
		status.setReturnText(APIConstants.RES_FAILED_DESC);
		return status;
	}
	
	*//**
	 * Method for prepare Failed status object with dynamic parameters.<br>
	 * 
	 * @param errorCode
	 * @param errorDesc
	 * 
	 * @return WSReturnStatus
	 *//*
	public static WSReturnStatus getFailedStatus(String errorCode, String errorDesc) {
		WSReturnStatus status = new WSReturnStatus();
		status.setReturnCode(errorCode);
		status.setReturnText(errorDesc);
		return status;
	}
	
	public static WSReturnStatus getFailedStatus(String errorCode) {
		WSReturnStatus status = new WSReturnStatus();
		status.setReturnCode(errorCode);
		status.setReturnText("");
		return status;
	}
}
*/