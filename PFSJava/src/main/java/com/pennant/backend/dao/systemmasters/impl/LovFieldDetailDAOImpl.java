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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.systemmasters.LovFieldDetailDAO;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>LovFieldDetail model</b> class.<br>
 * 
 */
public class LovFieldDetailDAOImpl extends SequenceDao<LovFieldDetail> implements LovFieldDetailDAO {
     private static Logger logger = Logger.getLogger(LovFieldDetailDAOImpl.class);
	
	
	public LovFieldDetailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  LOV Field Details details by key field
	 * 
	 * @param id (int)
	 * 
	 * 
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LovFieldDetail
	 */
	@Override
	public LovFieldDetail getLovFieldDetailById(String fieldCode, String fieldCodeValue,String type) {
		logger.debug("Entering");
		LovFieldDetail lovFieldDetail = new LovFieldDetail();
		lovFieldDetail.setFieldCode(fieldCode);
		lovFieldDetail.setFieldCodeValue(fieldCodeValue);
		
		StringBuilder selectSql = new StringBuilder("Select FieldCodeId, FieldCode," );
		selectSql.append(" FieldCodeValue, valueDesc, isActive,SystemDefault," );
		if(type.contains("View")){
			selectSql.append(" lovDescFieldCodeName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTLovFieldDetail"+ StringUtils.trimToEmpty(type) );
		selectSql.append(" Where FieldCode =:FieldCode AND FieldCodeValue =:FieldCodeValue ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldDetail);
		RowMapper<LovFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LovFieldDetail.class);
		
		try{
			lovFieldDetail = this.jdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			lovFieldDetail = null;
		}
		logger.debug("Leaving");
		return lovFieldDetail;
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
	public void delete(LovFieldDetail lovFieldDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From RMTLovFieldDetail");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where FieldCodeId =:FieldCodeId");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldDetail);
		          
		try{
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
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
	public String save(LovFieldDetail lovFieldDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		if (lovFieldDetail.getId()==Long.MIN_VALUE){
			lovFieldDetail.setId(getNextId("SeqRMTLovFieldDetail"));
			logger.debug("get NextID:"+lovFieldDetail.getId());
		}
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTLovFieldDetail" );
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (FieldCodeId, FieldCode, FieldCodeValue,valueDesc, isActive,SystemDefault,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:FieldCodeId, :FieldCode, :FieldCodeValue, :valueDesc, :isActive,:SystemDefault," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldDetail);
		try{
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return String.valueOf(lovFieldDetail.getId());
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
	@Override
	public void update(LovFieldDetail lovFieldDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update RMTLovFieldDetail");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set FieldCode = :FieldCode," );
		updateSql.append(" FieldCodeValue =:FieldCodeValue, valueDesc =:valueDesc,isActive =:isActive , SystemDefault=:SystemDefault," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where FieldCodeId =:FieldCodeId ");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL +  updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
	
	
	
	/**
	 * Fetch the count of system default values by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Gender
	 */
	@Override
	public int getSystemDefaultCount(String fieldCode,String fieldCodeValue) {
		logger.debug("Entering");
		LovFieldDetail lovFieldDetail = new LovFieldDetail();
		lovFieldDetail.setFieldCode(fieldCode);
		lovFieldDetail.setFieldCodeValue(fieldCodeValue);
		lovFieldDetail.setSystemDefault(true);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT Count(*) FROM  RMTLovFieldDetail_View ");
		selectSql.append(" Where FieldCode = :FieldCode  and FieldCodeValue !=:FieldCodeValue and SystemDefault = :SystemDefault");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lovFieldDetail);
		int sysDftCount = 0;
		try {
			sysDftCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
        } catch (Exception e) {
        	logger.warn("Exception: ", e);
        	sysDftCount = 0;
        }
		logger.debug("Leaving");
		return sysDftCount;

	}

	@Override
	public boolean isDuplicateKey(String fieldCode, String fieldDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "FieldCode =:fieldCode AND FieldCodeValue =:fieldDetail";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTLovFieldDetail", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTLovFieldDetail_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTLovFieldDetail_Temp", "RMTLovFieldDetail" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("fieldCode", fieldCode);
		paramSource.addValue("fieldDetail", fieldDetail);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
}