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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.DivisionDetailDAO;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

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
		
		StringBuilder selectSql = new StringBuilder("Select DivisionCode, DivisionCodeDesc, Active, DivSuspTrigger, DivSuspRemarks, EntityCode ");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,AlwPromotion");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(", EntityDesc");
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
	
	@Override
	public boolean isDuplicateKey(String divisionCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "DivisionCode = :divisionCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("SMTDivisionDetail", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("SMTDivisionDetail_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "SMTDivisionDetail_Temp", "SMTDivisionDetail" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("divisionCode", divisionCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(DivisionDetail divisionDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder("insert into SMTDivisionDetail");
		sql.append(tableType.getSuffix());
		sql.append(" (DivisionCode, DivisionCodeDesc, Active, DivSuspTrigger, DivSuspRemarks, EntityCode ");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,AlwPromotion)");
		sql.append(" values(:DivisionCode, :DivisionCodeDesc, :Active, :DivSuspTrigger, :DivSuspRemarks, :EntityCode ");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId,:AlwPromotion)");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL +sql.toString());		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(divisionDetail);
		try{
		namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		}catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return divisionDetail.getId();
	}
	
	@Override
	public void update(DivisionDetail divisionDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql =new StringBuilder("update SMTDivisionDetail");
		sql.append(tableType.getSuffix()); 
		sql.append(" set DivisionCodeDesc = :DivisionCodeDesc, Active = :Active, DivSuspTrigger=:DivSuspTrigger, DivSuspRemarks=:DivSuspRemarks, EntityCode = :EntityCode");
		sql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, AlwPromotion = :AlwPromotion");
		sql.append(" where DivisionCode =:DivisionCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(divisionDetail);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(DivisionDetail divisionDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from SMTDivisionDetail");
		sql.append(tableType.getSuffix());
		sql.append(" where DivisionCode =:DivisionCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL +  sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(divisionDetail);
		int recordCount = 0;
		
		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(),paramSource);
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
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}