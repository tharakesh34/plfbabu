/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  ErrorUtil.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.service.errordetail.ErrorDetailService;
import com.pennant.backend.util.PennantConstants;

public class ErrorUtil implements Serializable {
	private static final long serialVersionUID = 6700340086746473118L;
	
	private List<ErrorDetails> errorDetails = null;
	private static ErrorDetailService errorDetailService;
	
	private ErrorUtil() {
		super();
	}

	private ErrorUtil(List<ErrorDetails> errorDetails, String errorLanguage) {
		if (errorDetails != null && errorDetails.size() != 0) {
			HashMap<String, ErrorDetails> hashMap = getErrorsByErrorCodes(errorLanguage, errorDetails);

			this.errorDetails = new ArrayList<>();
			for (ErrorDetails errorDetail : errorDetails) {
				this.errorDetails.add(copyErrorDetails(errorDetail, hashMap.get(errorDetail.getErrorCode())));
			}
		}
	}

	public static ErrorDetails getErrorDetail(ErrorDetails errorDetail) {
		List<ErrorDetails> errorDetails = new ArrayList<>();
		errorDetails.add(errorDetail);
		errorDetails = new ErrorUtil(errorDetails, PennantConstants.default_Language).errorDetails;
		return errorDetails.get(0);
	}

	public static ErrorDetails getErrorDetail(ErrorDetails errorDetail, String errorLanguage) {
		List<ErrorDetails> errorDetails = new ArrayList<>();
		errorDetails.add(errorDetail);
		errorDetails = new ErrorUtil(errorDetails, errorLanguage).errorDetails;
		return errorDetails.get(0);
	}

	public static List<ErrorDetails> getErrorDetails(List<ErrorDetails> errorDetails, String errorLanguage) {
		return new ErrorUtil(errorDetails, errorLanguage).errorDetails;
	}

	private static ErrorDetails getErrorDetail(String errorCode) {
		return errorDetailService.getErrorDetail(errorCode);
	}

	
	private HashMap<String, ErrorDetails> getErrorsByErrorCodes(String errorLanguage, List<ErrorDetails> errorDetails) {
		HashMap<String, ErrorDetails> hashMap = new HashMap<String, ErrorDetails>();

		for (ErrorDetails errorDetail : errorDetails) {
			//errorDetail = getError(errorDetail.getErrorCode());
			errorDetail = getErrorDetail(errorDetail.getErrorCode());
			hashMap.put(StringUtils.trimToEmpty(errorDetail.getErrorCode()), errorDetail);
		}
		return hashMap;
	}

	private ErrorDetails copyErrorDetails(ErrorDetails oldDetail, ErrorDetails newDetail) {

		if (newDetail == null) {
			String[] parameters = new String[] { oldDetail.getErrorCode() };
			oldDetail.setErrorSeverity("E");
			oldDetail.setErrorMessage("Invalid Error Code {0} Configuration");
			oldDetail.setErrorParameters(parameters);
		} else {
			oldDetail.setErrorSeverity(newDetail.getErrorSeverity());
			oldDetail.setErrorMessage(newDetail.getErrorMessage());
			oldDetail.setErrorExtendedMessage(newDetail.getErrorExtendedMessage());
		}
		return oldDetail;
	}
	
	public void setErrorDetailService(ErrorDetailService errorDetailService) {
		ErrorUtil.errorDetailService = errorDetailService;
	}

}
