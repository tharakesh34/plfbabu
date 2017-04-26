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
 * FileName    		:  ManualAdviseDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.finance.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennanttech.pff.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ManualAdvise</code> with set of CRUD operations.
 */
public class ManualAdviseDAOImpl extends BasisNextidDaoImpl<ManualAdvise> implements ManualAdviseDAO {
	private static Logger				logger	= Logger.getLogger(ManualAdviseDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public ManualAdviseDAOImpl() {
		super();
	}
	
	@Override
	public ManualAdvise getManualAdviseById(long adviseID,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" adviseID, adviseType, finReference, feeTypeID, sequence, adviseAmount, ");
		sql.append(" paidAmount, waivedAmount, remarks, ");
		if(type.contains("View")){
			sql.append(" FeeTypeCode, FeeTypeDesc," );
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where adviseID = :adviseID");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(adviseID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		RowMapper<ManualAdvise> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdvise.class);

		try {
			manualAdvise = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			manualAdvise = null;
		}

		logger.debug(Literal.LEAVING);
		return manualAdvise;
	}		
	
	@Override
	public String save(ManualAdvise manualAdvise,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append("(adviseID, adviseType, finReference, feeTypeID, sequence, adviseAmount, ");
		sql.append(" paidAmount, waivedAmount, remarks, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :adviseID, :adviseType, :finReference, :feeTypeID, :sequence, :adviseAmount, ");
		sql.append(" :paidAmount, :waivedAmount, :remarks, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		// Get the identity sequence number.
		if (manualAdvise.getAdviseID() <= 0) {
			manualAdvise.setAdviseID(getNextidviewDAO().getNextId("seqManualAdvise"));
		}

		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(manualAdvise.getAdviseID());
	}	

	@Override
	public void update(ManualAdvise manualAdvise,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update ManualAdvise" );
		sql.append(tableType.getSuffix());
		sql.append("  set adviseType = :adviseType, finReference = :finReference, feeTypeID = :feeTypeID, ");
		sql.append(" sequence = :sequence, adviseAmount = :adviseAmount, paidAmount = :paidAmount, ");
		sql.append(" waivedAmount = :waivedAmount, remarks = :remarks, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where adviseID = :adviseID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ManualAdvise manualAdvise, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append(" where adviseID = :adviseID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
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
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
 
	@Override
	public List<ManualAdvise> getManualAdviseByRef(String finReference , int adviseType, String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" Select AdviseID, AdviseAmount, PaidAmount, WaivedAmount " );
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(" , FeeTypeDesc ");
		}
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where FinReference = :FinReference AND AdviseType =:AdviseType ");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setFinReference(finReference);
		manualAdvise.setAdviseType(adviseType);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		RowMapper<ManualAdvise> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdvise.class);

		List<ManualAdvise> adviseList = namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		logger.debug(Literal.LEAVING);
		return adviseList;
	}

	@Override
	public List<ManualAdviseMovements> getAdviseMovements(long id) {
		logger.debug("Entering");

		ManualAdviseMovements movements = new ManualAdviseMovements();
		movements.setAdviseID(id);

		StringBuilder selectSql = new StringBuilder("Select T1.MovementID , T1.AdviseID, T1.MovementDate, T1.MovementAmount, ");
		selectSql.append(" T1.PaidAmount , T1.WaivedAmount, T1.Status, T1.PayAgainstID,T3.FeeTypeCode   From ManualAdviseMovements T1 ");
		selectSql.append(" LEFT OUTER JOIN ");
		selectSql.append(" FinReceiptHeader T2 ON T1.PayAgainstID = T2.ReceiptID");
		selectSql.append(" LEFT OUTER JOIN ");
		selectSql.append(" FeeTypes T3 ON T1.PayAgainstID = T3.FeeTypeID");
		selectSql.append(" Where AdviseID = :AdviseID  ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(movements);
		RowMapper<ManualAdviseMovements> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ManualAdviseMovements.class);

		List<ManualAdviseMovements> list = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return list;
	}		
	
}	
