/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : ErrorUtil.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.service.errordetail.ErrorDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class ErrorUtil implements Serializable {
	private static final long serialVersionUID = 6700340086746473118L;

	private List<ErrorDetail> errorDetails = null;
	private static ErrorDetailService errorDetailService;

	private ErrorUtil() {
		super();
	}

	private ErrorUtil(List<ErrorDetail> errorDetails, String errorLanguage) {
		this.errorDetails = new ArrayList<>();

		if (errorDetails != null && !errorDetails.isEmpty()) {
			Map<String, ErrorDetail> hashMap = getErrorsByErrorCodes(errorLanguage, errorDetails);

			for (ErrorDetail errorDetail : errorDetails) {
				this.errorDetails.add(copyErrorDetails(errorDetail, hashMap.get(errorDetail.getCode())));
			}
		}
	}

	public static ErrorDetail getErrorDetail(ErrorDetail errorDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<>();
		errorDetails.add(errorDetail);
		errorDetails = new ErrorUtil(errorDetails, PennantConstants.default_Language).errorDetails;
		return errorDetails.get(0);
	}

	public static ErrorDetail getErrorDetail(ErrorDetail errorDetail, String errorLanguage) {
		List<ErrorDetail> errorDetails = new ArrayList<>();
		errorDetails.add(errorDetail);
		errorDetails = new ErrorUtil(errorDetails, errorLanguage).errorDetails;
		return errorDetails.get(0);
	}

	public static List<ErrorDetail> getErrorDetails(List<ErrorDetail> errorDetails, String errorLanguage) {
		return new ErrorUtil(errorDetails, errorLanguage).errorDetails;
	}

	private static ErrorDetail getErrorDetail(String errorCode) {
		return errorDetailService.getErrorDetail(errorCode);
	}

	private Map<String, ErrorDetail> getErrorsByErrorCodes(String errorLanguage, List<ErrorDetail> errorDetails) {
		Map<String, ErrorDetail> hashMap = new HashMap<String, ErrorDetail>();

		for (ErrorDetail errorDetail : errorDetails) {
			// errorDetail = getError(errorDetail.getErrorCode());
			ErrorDetail errDetail = getErrorDetail(errorDetail.getCode());
			hashMap.put(StringUtils.trimToEmpty(errorDetail.getCode()), errDetail);
		}
		return hashMap;
	}

	private ErrorDetail copyErrorDetails(ErrorDetail oldDetail, ErrorDetail newDetail) {

		if (newDetail == null) {
			String[] parameters = new String[] { oldDetail.getCode() };
			oldDetail.setSeverity("E");
			oldDetail.setMessage("Invalid Error Code {0} Configuration");
			oldDetail.setParameters(parameters);
		} else {
			oldDetail.setSeverity(newDetail.getSeverity());
			oldDetail.setMessage(newDetail.getMessage());
			oldDetail.setExtendedMessage(newDetail.getExtendedMessage());
		}
		return oldDetail;
	}

	public static ErrorDetail getErrorDetailById(String errorCode) {
		return errorDetailService.getErrorDetailById(errorCode);
	}

	public static String getErrorMessage(String errorMessage, String[] errorParameters) {
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

	public void setErrorDetailService(ErrorDetailService errorDetailService) {
		ErrorUtil.errorDetailService = errorDetailService;
	}

	public static void setError(FinScheduleData schdData, String errorCode, String... parms) {
		String[] valueParm = new String[parms.length];

		int index = 0;
		for (String parm : parms) {
			valueParm[index++] = parm;
		}

		schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm)));
	}

	public static ErrorDetail getError(String errorCode, String... parms) {
		String[] valueParm = new String[parms.length];

		int index = 0;
		for (String parm : parms) {
			valueParm[index++] = parm;
		}

		return ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm));
	}

}
