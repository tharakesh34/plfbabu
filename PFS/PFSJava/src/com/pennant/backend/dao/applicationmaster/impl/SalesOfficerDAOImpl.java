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
 * FileName    		:  SalesOfficerDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.applicationmaster.impl;

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
import com.pennant.backend.dao.applicationmaster.SalesOfficerDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.SalesOfficer;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>SalesOfficer model</b> class.<br>
 * 
 */
public class SalesOfficerDAOImpl extends BasisCodeDAO<SalesOfficer> implements SalesOfficerDAO {

	private static Logger logger = Logger.getLogger(SalesOfficerDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new SalesOfficer 
	 * @return SalesOfficer
	 */
	@Override
	public SalesOfficer getSalesOfficer() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SalesOfficer");
		SalesOfficer salesOfficer= new SalesOfficer();
		if (workFlowDetails!=null){
			salesOfficer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return salesOfficer;
	}

	/**
	 * This method get the module from method getSalesOfficer() and set the new record flag as true and return SalesOfficer()   
	 * @return SalesOfficer
	 */
	@Override
	public SalesOfficer getNewSalesOfficer() {
		logger.debug("Entering");
		SalesOfficer salesOfficer = getSalesOfficer();
		salesOfficer.setNewRecord(true);
		logger.debug("Leaving");
		return salesOfficer;
	}

	/**
	 * Fetch the Record  Sales Officers details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SalesOfficer
	 */
	@Override
	public SalesOfficer getSalesOfficerById(final String id, String type) {
		logger.debug("Entering");
		SalesOfficer salesOfficer = getSalesOfficer();
		salesOfficer.setId(id);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT T1.SalesOffCode, T1.SalesOffFName, T1.SalesOffMName, T1.SalesOffLName, T1.SalesOffShrtName, T1.SalesOffDept, T1.SalesOffIsActive,T2.DeptDesc AS lovDescSalesOffDeptName," );
		if(type.contains("View")){
			selectSql.append("");
		}
		selectSql.append(" T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId" );
		selectSql.append(" FROM  SalesOfficers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" AS T1 INNER JOIN BMTDepartments AS T2 ON T1.SalesOffDept = T2.DeptCode");
		selectSql.append(" Where SalesOffCode =:SalesOffCode");
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salesOfficer);
		RowMapper<SalesOfficer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SalesOfficer.class);
		
		try{
			salesOfficer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			salesOfficer = null;
		}
		logger.debug("Leaving");
		return salesOfficer;
	}
	
	/**
	 * This method initialize the Record.
	 * @param SalesOfficer (salesOfficer)
 	 * @return SalesOfficer
	 */
	@Override
	public void initialize(SalesOfficer salesOfficer) {
		super.initialize(salesOfficer);
	}
	
	/**
	 * This method refresh the Record.
	 * @param SalesOfficer (salesOfficer)
 	 * @return void
	 */
	@Override
	public void refresh(SalesOfficer salesOfficer) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the SalesOfficers or SalesOfficers_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Sales Officers by key SalesOffCode
	 * 
	 * @param Sales Officers (salesOfficer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(SalesOfficer salesOfficer,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From SalesOfficers");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SalesOffCode =:SalesOffCode");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salesOfficer);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", salesOfficer.getSalesOffCode(), salesOfficer.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", salesOfficer.getSalesOffCode(), salesOfficer.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into SalesOfficers or SalesOfficers_Temp.
	 *
	 * save Sales Officers 
	 * 
	 * @param Sales Officers (salesOfficer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(SalesOfficer salesOfficer,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into SalesOfficers");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SalesOffCode, SalesOffFName, SalesOffMName, SalesOffLName, SalesOffShrtName, SalesOffDept, SalesOffIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:SalesOffCode, :SalesOffFName, :SalesOffMName, :SalesOffLName, :SalesOffShrtName, :SalesOffDept, :SalesOffIsActive, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salesOfficer);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return salesOfficer.getId();
	}
	
	/**
	 * This method updates the Record SalesOfficers or SalesOfficers_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Sales Officers by key SalesOffCode and Version
	 * 
	 * @param Sales Officers (salesOfficer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(SalesOfficer salesOfficer,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update SalesOfficers");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SalesOffCode = :SalesOffCode, SalesOffFName = :SalesOffFName, SalesOffMName = :SalesOffMName, SalesOffLName = :SalesOffLName, ");
		updateSql.append(" SalesOffShrtName = :SalesOffShrtName, SalesOffDept = :SalesOffDept, SalesOffIsActive = :SalesOffIsActive, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where SalesOffCode =:SalesOffCode ");

		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salesOfficer);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41003", salesOfficer.getSalesOffCode(), salesOfficer.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String salesOffCode,String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = salesOffCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_SalesOffCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}