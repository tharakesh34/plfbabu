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
 * FileName    		:  LovFieldDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2011    														*
 *                                                                  						*
 * Modified Date    :  04-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.systemmasters.LovFieldDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>LovFieldDetail model</b> class.<br>
 * 
 */
public class LovFieldDetailDAOImpl extends BasisNextidDaoImpl<LovFieldDetail>
		implements LovFieldDetailDAO {

	private static Logger logger = Logger.getLogger(LovFieldDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new LovFieldDetail 
	 * @return LovFieldDetail
	 */
	@Override
	public LovFieldDetail getLovFieldDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("LovFieldDetail");
		LovFieldDetail lovFieldDetail= new LovFieldDetail();
		if (workFlowDetails!=null){
			lovFieldDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return lovFieldDetail;
	}

	/**
	 * This method get the module from method getLovFieldDetail() and set the
	 * new record flag as true and return LovFieldDetail()
	 * 
	 * @return LovFieldDetail
	 */
	@Override
	public LovFieldDetail getNewLovFieldDetail() {
		logger.debug("Entering");
		LovFieldDetail lovFieldDetail = getLovFieldDetail();
		lovFieldDetail.setNewRecord(true);
		logger.debug("Leaving");
		return lovFieldDetail;
	}

	/**
	 * Fetch the Record  LOV Field Details details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LovFieldDetail
	 */
	@Override
	public LovFieldDetail getLovFieldDetailById(final long id, String type) {
		logger.debug("Entering");
		LovFieldDetail lovFieldDetail = getLovFieldDetail();
		lovFieldDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FieldCodeId, FieldCode," );
		selectSql.append(" FieldCodeValue, valueDesc, isActive," );
		if(type.contains("View")){
			selectSql.append(" lovDescFieldCodeName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTLovFieldDetail"+ StringUtils.trimToEmpty(type) );
		selectSql.append(" Where FieldCodeId =:FieldCodeId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldDetail);
		RowMapper<LovFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LovFieldDetail.class);
		
		try{
			lovFieldDetail = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			lovFieldDetail = null;
		}
		logger.debug("Leaving");
		return lovFieldDetail;
	}
	
	/**
	 * This method initialize the Record.
	 * @param LovFieldDetail (lovFieldDetail)
 	 * @return LovFieldDetail
	 */
	@Override
	public void initialize(LovFieldDetail lovFieldDetail) {
		super.initialize(lovFieldDetail);
	}
	
	/**
	 * This method refresh the Record.
	 * @param LovFieldDetail (lovFieldDetail)
 	 * @return void
	 */
	@Override
	public void refresh(LovFieldDetail lovFieldDetail) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the RMTLovFieldDetail or RMTLovFieldDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete LOV Field Details by key FieldCodeId
	 * 
	 * @param LOV Field Details (lovFieldDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(LovFieldDetail lovFieldDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From RMTLovFieldDetail");
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where FieldCodeId =:FieldCodeId");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldDetail);
		          
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003",
						lovFieldDetail.getId(), lovFieldDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails=  getError("41006",lovFieldDetail.getId(),
					lovFieldDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into RMTLovFieldDetail or
	 * RMTLovFieldDetail_Temp. it fetches the available Sequence form
	 * SeqRMTLovFieldDetail by using getNextidviewDAO().getNextId() method.
	 * 
	 * save LOV Field Details
	 * 
	 * @param LOV
	 *            Field Details (lovFieldDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(LovFieldDetail lovFieldDetail,String type) {
		logger.debug("Entering");
		
		if (lovFieldDetail.getId()==Long.MIN_VALUE){
			lovFieldDetail.setId(getNextidviewDAO().getNextId("SeqRMTLovFieldDetail"));
			logger.debug("get NextID:"+lovFieldDetail.getId());
		}
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTLovFieldDetail" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (FieldCodeId, FieldCode, FieldCodeValue,valueDesc, isActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:FieldCodeId, :FieldCode, :FieldCodeValue, :valueDesc, :isActive," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return lovFieldDetail.getId();
	}
	
	/**
	 * This method updates the Record RMTLovFieldDetail or RMTLovFieldDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update LOV Field Details by key FieldCodeId and Version
	 * 
	 * @param LOV Field Details (lovFieldDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(LovFieldDetail lovFieldDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update RMTLovFieldDetail");
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set FieldCodeId = :FieldCodeId, FieldCode = :FieldCode," );
		updateSql.append(" FieldCodeValue =:FieldCodeValue, valueDesc =:valueDesc,isActive =:isActive ," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where FieldCodeId =:FieldCodeId ");

		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",lovFieldDetail.getId(),
					lovFieldDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId,long fieldCodeId, String userLanguage){
		String[][] parms= new String[2][1]; 
		
		parms[1][0] = String.valueOf(fieldCodeId);
		parms[0][0] = PennantJavaUtil.getLabel("label_FieldCodeId")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
				errorId, parms[0],parms[1]), userLanguage);
	}
	
}