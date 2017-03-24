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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.backend.dao.ErrorDetailsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.util.PennantConstants;

public class ErrorUtil implements Serializable {

    private static final long serialVersionUID = 6700340086746473118L;

	private List<ErrorDetails> errorDetails=null;
	private static ErrorDetailsDAO errorDetailsDAO;
	private final static Logger logger = Logger.getLogger(ErrorUtil.class);
	public ErrorUtil() {
		super();
	}

	public static ErrorDetails getErrorDetail(ErrorDetails errorDetail){
		List<ErrorDetails> errorDetails=new ArrayList<ErrorDetails>();
		errorDetails.add(errorDetail);
		errorDetails = new ErrorUtil(errorDetails, PennantConstants.default_Language).getErrorDetails();
		return errorDetails.get(0);
	} 
	
	public static ErrorDetails getErrorDetail(ErrorDetails errorDetail,String errorLanguage){
		List<ErrorDetails> errorDetails=new ArrayList<ErrorDetails>();
		errorDetails.add(errorDetail);
		errorDetails = new ErrorUtil(errorDetails, errorLanguage).getErrorDetails();
		return errorDetails.get(0);
	} 
	
	
	public static List<ErrorDetails> getErrorDetails(List<ErrorDetails> errorDetails,String errorLanguage){
		return new ErrorUtil(errorDetails, errorLanguage).getErrorDetails();
	} 
	
	private static LoadingCache<String, ErrorDetails> errorCache = CacheBuilder.newBuilder().
			expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<String, ErrorDetails>() {
				@Override
				public ErrorDetails load(String errorCode) throws Exception {
					return getErrorDetail(errorCode);
				}
			});
	
	
	private static ErrorDetails getErrorDetail(String errorCode){
			return getErrorDetailsDAO().getErrorDetail(errorCode);
	} 

	
	private static ErrorDetails getError(String errorCode){
		
		ErrorDetails errorDetail = null;
		try {
			errorDetail = errorCache.get(errorCode);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from cache: ", e);
			errorDetail = getErrorDetail(errorCode);
		}
		
		return errorDetail;
	} 
	
	
	// Clear errorCache data.
		public static void clearErrorCache(String errorCode) {
			try {
				errorCache.invalidate(errorCode);
			} catch (Exception ex) {
				logger.warn("Error clearing data from errorCache cache: ", ex);
			}
		}

	
	private ErrorUtil(List<ErrorDetails> errorDetails,String errorLanguage){
		if(errorDetails!=null && errorDetails.size()!=0){
			//String errorCodeList = getErrorCodeList(errorDetails);
			HashMap<String, ErrorDetails> hashMap = getErrorsByErrorCodes(errorLanguage,errorDetails);
			
			this.errorDetails= new ArrayList<ErrorDetails>();
			for (ErrorDetails errorDetail : errorDetails) {
				this.errorDetails.add(copyErrorDetails(errorDetail, hashMap.get(errorDetail.getErrorCode())));
			}
		}
	}
	
	/*private String getErrorCodeList(List<ErrorDetails> errorDetails){
		String errorCodeList = "";
		
		if(errorDetails!=null){
			for (int i = 0; i < errorDetails.size(); i++) {
				ErrorDetails errorDetail = errorDetails.get(i); 
				
				if(!StringUtils.contains(errorCodeList, errorDetail.getErrorCode())){
					
					errorCodeList = errorCodeList+"'"+errorDetail.getErrorCode()+"'";
					
					if(i<(errorDetails.size()-1)){
						errorCodeList = errorCodeList+",";	
					}
				}
			}	
		}
		if(errorCodeList.endsWith(",")){
			errorCodeList= errorCodeList.substring(0, errorCodeList.length()-1);
		}
		return errorCodeList;
	}
	
	private HashMap<String, ErrorDetails> getErrorsByErrorCodes(String errorLanguage,String errorCodeList){
		HashMap<String, ErrorDetails> hashMap= new HashMap<String, ErrorDetails>();
		
		List<ErrorDetails> errorDetails =getErrorDetailsDAO().getErrorDetail(errorLanguage, errorCodeList);
		
		if(errorDetails != null){
			hashMap = new HashMap<String, ErrorDetails>(errorDetails.size());	
			for (int i = 0; i < errorDetails.size(); i++) {
				hashMap.put(StringUtils.trim(errorDetails.get(i).getErrorCode()), errorDetails.get(i));
			}
			
		}else{
			hashMap = new HashMap<String, ErrorDetails>(1);
		}
		return hashMap;
	}*/

	private HashMap<String, ErrorDetails> getErrorsByErrorCodes(String errorLanguage,List<ErrorDetails> errorDetails){
		HashMap<String, ErrorDetails> hashMap= new HashMap<String, ErrorDetails>();
		
		for (ErrorDetails errorDetail: errorDetails) {
			errorDetail= getError(errorDetail.getErrorCode());
			hashMap.put(StringUtils.trimToEmpty(errorDetail.getErrorCode()), errorDetail);
		}
		return hashMap;
	}
	
	
	
	private ErrorDetails copyErrorDetails(ErrorDetails oldDetail,ErrorDetails newDetail){
		
		if(newDetail==null){
			String[] parameters= new String[]{oldDetail.getErrorCode()};
			oldDetail.setErrorSeverity("E");
			oldDetail.setErrorMessage("Invalid Error Code {0} Configuration");
			oldDetail.setErrorParameters(parameters);
		}else{
			oldDetail.setErrorSeverity(newDetail.getErrorSeverity());
			oldDetail.setErrorMessage(newDetail.getErrorMessage());
			oldDetail.setErrorExtendedMessage(newDetail.getErrorExtendedMessage());
		}
		return oldDetail;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public void setErrorDetailsDAO(ErrorDetailsDAO errorDetailsDAO) {
		ErrorUtil.errorDetailsDAO = errorDetailsDAO;
	}
	public static ErrorDetailsDAO getErrorDetailsDAO() {
		return errorDetailsDAO;
	}
	
	public List<ErrorDetails> getErrorDetails() {
		return errorDetails;
	}
	public void setErrorDetails(ArrayList<ErrorDetails> errorDetails) {
		this.errorDetails = errorDetails;
	}  
	
}
