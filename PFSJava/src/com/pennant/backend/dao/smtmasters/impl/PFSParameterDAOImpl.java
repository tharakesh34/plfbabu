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
 *																							*
 * FileName    		:  PFSParameterDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-07-2011    														*
 *                                                                  						*
 * Modified Date    :  12-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-07-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.smtmasters.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.smtmasters.PFSParameterDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>PFSParameter model</b> class.<br>
 * 
 */
public class PFSParameterDAOImpl extends BasisCodeDAO<PFSParameter> implements PFSParameterDAO {

	private static Logger logger = Logger.getLogger(PFSParameterDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new PFSParameter
	 * 
	 * @return PFSParameter
	 */
	@Override
	public PFSParameter getPFSParameter() {
		logger.debug("Entering ");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("PFSParameter");
		PFSParameter pFSParameter = new PFSParameter();
		if (workFlowDetails != null) {
			pFSParameter.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving ");
		return pFSParameter;
	}

	/**
	 * This method get the module from method getPFSParameter() and set the new
	 * record flag as true and return PFSParameter()
	 * 
	 * @return PFSParameter
	 */
	@Override
	public PFSParameter getNewPFSParameter() {
		logger.debug("Entering ");
		PFSParameter pFSParameter = getPFSParameter();
		pFSParameter.setNewRecord(true);
		logger.debug("Leaving ");
		return pFSParameter;
	}

	/**
	 * Fetch the Record System Parameter details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return PFSParameter
	 */
	@Override
	public PFSParameter getPFSParameterById(final String id, String type) {
		logger.debug("Entering");
		PFSParameter pFSParameter = new PFSParameter();
		pFSParameter.setId(id);
		
		StringBuilder   selectSql = new StringBuilder  ("SELECT SysParmCode, SysParmDesc, " );
		selectSql.append(" SysParmType, SysParmMaint, SysParmValue, SysParmLength, SysParmDec, " );
		selectSql.append(" SysParmList, SysParmValdMod, SysParmDescription, ");
		selectSql.append(" Version, LastMntOn, LastMntBy ");
		selectSql.append(" FROM  SMTparameters");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SysParmCode =:SysParmCode ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		RowMapper<PFSParameter> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PFSParameter.class);

		try {
			pFSParameter = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			pFSParameter = null;
		}
		logger.debug("Leaving");
		return pFSParameter;
	}

	/**
	 * This method initialise the Record.
	 * 
	 * @param PFSParameter
	 *            (pFSParameter)
	 * @return PFSParameter
	 */
	@Override
	public void initialize(PFSParameter pFSParameter) {
		super.initialize(pFSParameter);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param PFSParameter
	 *            (pFSParameter)
	 * @return void
	 */
	@Override
	public void refresh(PFSParameter pFSParameter) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
	}

	/**
	 * This method Deletes the Record from the SMTparameters or
	 * SMTparameters_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete System Parameter by key SysParmCode
	 * 
	 * @param System
	 *            Parameter (pFSParameter)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(PFSParameter pFSParameter, String type) {
		logger.debug("Entering ");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From SMTparameters");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SysParmCode =:SysParmCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				pFSParameter);
	
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
					beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails =getError("41003",pFSParameter.getSysParmCode(), 
						pFSParameter.getUserDetails().getUsrLanguage()); 
				throw new DataAccessException(errorDetails.getError()) { };
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails =getError("41006",pFSParameter.getSysParmCode(),
					pFSParameter.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method insert new Records into SMTparameters or SMTparameters_Temp.
	 * 
	 * save System Parameter
	 * 
	 * @param System
	 *            Parameter (pFSParameter)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(PFSParameter pFSParameter, String type) {
		logger.debug("Entering ");
		
		StringBuilder insertSql = new StringBuilder("Insert Into SMTparameters");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SysParmCode, SysParmDesc, SysParmType, SysParmMaint, SysParmValue, SysParmLength, SysParmDec,");
		insertSql.append(" SysParmList, SysParmValdMod, SysParmDescription,Version , LastMntBy, LastMntOn)");
		insertSql.append(" Values(:SysParmCode, :SysParmDesc, :SysParmType, :SysParmMaint, :SysParmValue, :SysParmLength,");
		insertSql.append(" :SysParmDec, :SysParmList, :SysParmValdMod, :SysParmDescription,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn)"); 
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
		return pFSParameter.getId();
	}

	/**
	 * This method updates the Record SMTparameters or SMTparameters_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update System Parameter by key SysParmCode and Version
	 * 
	 * @param System
	 *            Parameter (pFSParameter)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(PFSParameter pFSParameter, String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		StringBuilder updateSql = new StringBuilder("Update SMTparameters");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SysParmCode = :SysParmCode, SysParmDesc = :SysParmDesc, " );
		updateSql.append(" SysParmType = :SysParmType, SysParmMaint = :SysParmMaint, " );
		updateSql.append(" SysParmValue = :SysParmValue, SysParmLength = :SysParmLength, " );
		updateSql.append(" SysParmDec = :SysParmDec, SysParmList = :SysParmList, " );
		updateSql.append(" SysParmValdMod = :SysParmValdMod, SysParmDescription = :SysParmDescription, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		updateSql.append(" Where SysParmCode =:SysParmCode");

		if (!type.endsWith("_TEMP")) {
			updateSql.append(" AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004",pFSParameter.getSysParmCode(), 
					pFSParameter.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method for Updating Parameter Value
	 */
	@SuppressWarnings("serial")
    public void updateParmValue(PFSParameter pFSParameter) {
		int recordCount = 0;
		logger.debug("Entering ");
		StringBuilder updateSql = new StringBuilder("Update SMTparameters");
		updateSql.append(" Set SysParmValue = :SysParmValue Where SysParmCode =:SysParmCode " );

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004",pFSParameter.getSysParmCode(), 
					pFSParameter.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving ");
	}

	
	
	/**
	 * Method for getting the List of Static PFSParameters list
	 */
	public List<PFSParameter> getAllPFSParameter() {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder(" Select SysParmCode, SysParmDesc, " );
		selectSql.append(" SysParmType, SysParmMaint, SysParmValue, SysParmLength, SysParmDec, " );
		selectSql.append(" SysParmList, SysParmValdMod, SysParmDescription, ");
		selectSql.append(" Version , LastMntBy, LastMntOn From SMTparameters");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new PFSParameter());
		RowMapper<PFSParameter> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PFSParameter.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),beanParameters, typeRowMapper);
	}
	
	/**
	 * Method for get the list of Global Variable records
	 */
	public List<GlobalVariable> getGlobaVariables(){
		logger.debug("Entering");
		StringBuilder selectQry = new StringBuilder("Select  varCode,varName,varValue,varType " );
		selectQry.append(" from GlobalVariable" );
		logger.debug("selectSql: " + selectQry.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new GlobalVariable());
		RowMapper<GlobalVariable> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(GlobalVariable.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectQry.toString(),beanParameters, typeRowMapper);
	}
	
	private ErrorDetails  getError(String errorId, String sysParmCode ,String userLanguage){
		
		String[][] parms= new String[2][2];
		parms[1][0] = sysParmCode;
		parms[1][1] = "";

		parms[0][0] = PennantJavaUtil.getLabel("label_PFSParameterDialog_SysParmCode")+ ":" + parms[1][0];
		parms[0][1] = "";
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}	
	
}