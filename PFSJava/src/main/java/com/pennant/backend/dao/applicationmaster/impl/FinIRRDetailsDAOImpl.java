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
 * FileName    		:  IRRFinanceTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2017    														*
 *                                                                  						*
 * Modified Date    :  21-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2017       PENNANT	                 0.1                                            * 
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.FinIRRDetailsDAO;
import com.pennant.backend.model.finance.FinIRRDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>IRRFinanceType</code> with set of CRUD operations.
 */
public class FinIRRDetailsDAOImpl extends BasicDao<FinIRRDetails> implements FinIRRDetailsDAO {
	private static Logger	logger	= Logger.getLogger(FinIRRDetailsDAOImpl.class);

	

	@Override
	public List<FinIRRDetails> getFinIRRList(String finReference, String type) {
		logger.debug(Literal.ENTERING);
		FinIRRDetails finIRRDetails = new FinIRRDetails();
		finIRRDetails.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder("SELECT IRRID, FinReference, IRR, IRRCode, IrrCodeDesc, ");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode,  NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinIRRDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference" );

		List<FinIRRDetails> details = new ArrayList<FinIRRDetails>();
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finIRRDetails);
		RowMapper<FinIRRDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinIRRDetails.class);
		details = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		
		logger.debug(Literal.LEAVING);
		return details;
	}
	
	@Override
	public String save(FinIRRDetails entity, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into FinIRRDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (iRRID, finReference, iRR, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :iRRID, :finReference, :iRR,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(entity);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(entity.getiRRID());
	}

	@Override
	public void update(FinIRRDetails entity, TableType tableType) {
		logger.debug(Literal.ENTERING);
		StringBuilder	updatesql =new StringBuilder("update FinIRRDetails" );
		updatesql.append(tableType.getSuffix());
		updatesql.append(" set iRRID = :iRRID, ");
		updatesql.append(" finReference = :finReference, iRR = :iRR,");
		updatesql.append(" RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		updatesql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updatesql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updatesql.append(" where iRRID = :iRRID And finReference = :finReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + updatesql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(entity);
		int recordCount = jdbcTemplate.update(updatesql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(FinIRRDetails entity, TableType tableType) {
		StringBuilder deleteSql = new StringBuilder("Delete From FinIRRDetails");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where iRRID = :iRRID And finReference = :finReference");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(entity);
		this.jdbcTemplate.update(deleteSql.toString(),  beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void deleteList(String finReference, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		FinIRRDetails irrFeeType = new FinIRRDetails();
		irrFeeType.setFinReference(finReference);

		StringBuilder sql = new StringBuilder("delete from FinIRRDetails");
		sql.append(tableType.getSuffix());
		sql.append(" where FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(irrFeeType);
		jdbcTemplate.update(sql.toString(), paramSource);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveList(List<FinIRRDetails> finIrrDetails, TableType tableType) {
		StringBuilder sql =new StringBuilder(" insert into FinIRRDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (iRRID, finReference, iRR, ");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :iRRID, :finReference, :iRR,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finIrrDetails.toArray());

		try {
			jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);

	}

	
}	
