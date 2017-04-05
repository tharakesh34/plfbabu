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
 * FileName    		:  DivisionDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-08-2013    														*
 *                                                                  						*
 * Modified Date    :  02-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.systemmasters.impl;


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
import com.pennant.backend.dao.systemmasters.DivisionDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>DivisionDetail model</b> class.<br>
 * 
 */

public class DivisionDetailDAOImpl extends BasisCodeDAO<DivisionDetail> implements DivisionDetailDAO {

	private static Logger logger = Logger.getLogger(DivisionDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public DivisionDetailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  Division Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DivisionDetail
	 */
	@Override
	public DivisionDetail getDivisionDetailById(final String id, String type) {
		logger.debug("Entering");
		DivisionDetail divisionDetail = new DivisionDetail();
		
		divisionDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select DivisionCode, DivisionCodeDesc, Active, DivSuspTrigger, DivSuspRemarks ");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,AlwPromotion");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From SMTDivisionDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DivisionCode =:DivisionCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(divisionDetail);
		RowMapper<DivisionDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DivisionDetail.class);
		
		try{
			divisionDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			divisionDetail = null;
		}
		logger.debug("Leaving");
		return divisionDetail;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the SMTDivisionDetail or SMTDivisionDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Division Detail by key DivisionCode
	 * 
	 * @param Division Detail (divisionDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(DivisionDetail divisionDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From SMTDivisionDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DivisionCode =:DivisionCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(divisionDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",divisionDetail.getId() ,divisionDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006",divisionDetail.getId() ,divisionDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into SMTDivisionDetail or SMTDivisionDetail_Temp.
	 *
	 * save Division Detail 
	 * 
	 * @param Division Detail (divisionDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(DivisionDetail divisionDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into SMTDivisionDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (DivisionCode, DivisionCodeDesc, Active, DivSuspTrigger, DivSuspRemarks ");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,AlwPromotion)");
		insertSql.append(" Values(:DivisionCode, :DivisionCodeDesc, :Active, :DivSuspTrigger, :DivSuspRemarks ");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId,:AlwPromotion)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(divisionDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return divisionDetail.getId();
	}
	
	/**
	 * This method updates the Record SMTDivisionDetail or SMTDivisionDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Division Detail by key DivisionCode and Version
	 * 
	 * @param Division Detail (divisionDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(DivisionDetail divisionDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update SMTDivisionDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set DivisionCodeDesc = :DivisionCodeDesc, Active = :Active, DivSuspTrigger=:DivSuspTrigger, DivSuspRemarks=:DivSuspRemarks");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, AlwPromotion = :AlwPromotion");
		updateSql.append(" Where DivisionCode =:DivisionCode");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(divisionDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",divisionDetail.getId() ,divisionDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String divisionCode, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = divisionCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_DivisionCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}