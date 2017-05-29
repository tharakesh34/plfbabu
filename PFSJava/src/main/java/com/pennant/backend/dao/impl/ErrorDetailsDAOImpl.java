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
 * FileName    		:  ErrorDEtailsDAOImpl.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
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
package com.pennant.backend.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.ErrorDetailsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.util.PennantConstants;


public class ErrorDetailsDAOImpl implements ErrorDetailsDAO{
	private static Logger logger = Logger.getLogger(ErrorDetailsDAOImpl.class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ErrorDetailsDAOImpl() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public ErrorDetails getErrorDetail(String errorId,String errorLanguage,String[] parameters) {
		return getErrorDetail("",errorId, errorLanguage, parameters);
	}

	public ErrorDetails getErrorDetail(String errorField,String errorId,String errorLanguage,String[] parameters) {
		logger.debug("Entering + getErrorDetail("+errorId+","+errorLanguage);
		
		String selectListSql = 	"select ErrorCode, ErrorLanguage, ErrorSeverity, ErrorMessage, ErrorExtendedMessage from ErrorDetails where ErrorCode=:ErrorCode AND" +
				" ErrorLanguage IN (:DftLanguage,:ErrorLanguage)"; 

		logger.debug("selectListSql: " + selectListSql);
		Map<String,Object> namedParameters = new HashMap<String,Object>();
		
		namedParameters.put("ErrorCode", errorId);
		namedParameters.put("DftLanguage", PennantConstants.default_Language);
		namedParameters.put("ErrorLanguage", errorLanguage);
		
		RowMapper<ErrorDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ErrorDetails.class);
		
		List<ErrorDetails> errorList= this.namedParameterJdbcTemplate.query(selectListSql, namedParameters,typeRowMapper);
		ErrorDetails errorDetails=null;
		
		if (errorList!=null && !errorList.isEmpty()){
			
			for (int i = 0; i < errorList.size(); i++) {
				errorDetails = errorList.get(i);
				if (StringUtils.trimToEmpty(errorDetails.getErrorLanguage()).equalsIgnoreCase(errorLanguage)){
					break;
				}
			}
		}

		if(parameters==null || parameters.length==0){
			parameters = new String[1];
		}

		if (errorDetails==null){
			parameters[0]=errorId;
			errorDetails = new ErrorDetails(errorId,  "Invalid Error Code {0} Configuration", null);
		}	
		errorDetails.setErrorParameters(parameters);
		errorDetails.setErrorField(errorField);
		return errorDetails;
	}

	
	public List<ErrorDetails> getErrorDetail(String errorLanguage,String errorCodeList) {
		logger.debug("Entering + getErrorDetail("+errorLanguage+","+errorCodeList);
		
		String selectListSql = 	"select ErrorCode, ErrorLanguage, ErrorSeverity, ErrorMessage, ErrorExtendedMessage from ErrorDetails where ErrorCode IN (" +errorCodeList +
				") AND" +
				" ErrorLanguage IN (:ErrorLanguage)"; 

		logger.debug("selectListSql: " + selectListSql);
		Map<String,Object> namedParameters = new HashMap<String,Object>();
		namedParameters.put("ErrorCode", errorCodeList);
		namedParameters.put("ErrorLanguage", errorLanguage);
		
		RowMapper<ErrorDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ErrorDetails.class);
		List<ErrorDetails> errorList= this.namedParameterJdbcTemplate.query(selectListSql, namedParameters,typeRowMapper);
		
		if(errorList==null || errorList.size()==0){
			namedParameters.remove("ErrorLanguage");
			namedParameters.put("ErrorLanguage", PennantConstants.default_Language);
			typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ErrorDetails.class);
			errorList= this.namedParameterJdbcTemplate.query(selectListSql, namedParameters,typeRowMapper);
			
		}
		
		return errorList;
	}

	@Override
	public ErrorDetails getErrorDetail(String errorCode) {
		logger.debug("Entering + getErrorDetail"+errorCode);
		
		String selectListSql = 	"select ErrorCode, ErrorLanguage, ErrorSeverity, ErrorMessage, ErrorExtendedMessage from ErrorDetails where ErrorCode =:ErrorCode"; 
		logger.debug("selectListSql: " + selectListSql);
		Map<String,Object> namedParameters = new HashMap<String,Object>();
		namedParameters.put("ErrorCode", errorCode);
			
		RowMapper<ErrorDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ErrorDetails.class);
		List<ErrorDetails> errorList = namedParameterJdbcTemplate.query(selectListSql, namedParameters,typeRowMapper);
		if(errorList==null || errorList.isEmpty()){
			return null;
		}
		
		return errorList.get(0);
	}
	
	
}
