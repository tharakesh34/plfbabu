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
 * FileName    		:  ChequeDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-11-2017    														*
 *                                                                  						*
 * Modified Date    :  27-11-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-11-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.pdc.impl;

import java.util.List;

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

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ChequeDetail</code> with set of CRUD operations.
 */
public class ChequeDetailDAOImpl extends BasisNextidDaoImpl<Mandate> implements ChequeDetailDAO {
	private static Logger				logger	= Logger.getLogger(ChequeDetailDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public ChequeDetailDAOImpl() {
		super();
	}
	
	@Override
	public ChequeDetail getChequeDetail(long chequeDetailsID,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" chequeDetailsID, headerID, bankBranchID, accountNo, chequeSerialNo, chequeDate, ");
		sql.append(" eMIRefNo, amount, chequeCcy, status, active, ");
		
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From CHEQUEDETAIL");
		sql.append(type);
		sql.append(" Where chequeDetailsID = :chequeDetailsID");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ChequeDetail chequeDetail = new ChequeDetail();
		chequeDetail.setChequeDetailsID(chequeDetailsID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeDetail);
		RowMapper<ChequeDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ChequeDetail.class);

		try {
			chequeDetail = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			chequeDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return chequeDetail;
	}		
	
	@Override
	public List<ChequeDetail> getChequeDetailList(long headerID,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" chequeDetailsID, headerID, bankBranchID, accountNo, chequeSerialNo, chequeDate, ");
		sql.append(" eMIRefNo, amount, chequeCcy, status, active, ");
		if(type.equals("_View")){
			sql.append(" branchDesc as bankBranchIDName, ");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From CHEQUEDETAIL");
		sql.append(type);
		sql.append(" Where headerID = :headerID");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ChequeDetail chequeDetail = new ChequeDetail();
		chequeDetail.setHeaderID(headerID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeDetail);
		RowMapper<ChequeDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ChequeDetail.class);
		return namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		 
	}
	
	@Override
	public boolean isDuplicateKey(long chequeDetailsID,long bankBranchID,String accountNo,int chequeSerialNo, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "bankBranchID = :bankBranchID AND accountNo = :accountNo AND chequeSerialNo = :chequeSerialNo AND chequeDetailsID != :chequeDetailsID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CHEQUEDETAIL", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CHEQUEDETAIL_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CHEQUEDETAIL_Temp", "CHEQUEDETAIL" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("chequeDetailsID", chequeDetailsID);
		paramSource.addValue("bankBranchID", bankBranchID);
		paramSource.addValue("accountNo", accountNo);
		paramSource.addValue("chequeSerialNo", chequeSerialNo);
		
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public String save(ChequeDetail chequeDetail,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into CHEQUEDETAIL");
		sql.append(tableType.getSuffix());
		sql.append("(chequeDetailsID, headerID, bankBranchID, accountNo, chequeSerialNo, chequeDate, ");
		sql.append(" eMIRefNo, amount, chequeCcy, status, active, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :chequeDetailsID, :headerID, :bankBranchID, :accountNo, :chequeSerialNo, :chequeDate, ");
		sql.append(" :eMIRefNo, :amount, :chequeCcy, 'NEW', :active, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		if (chequeDetail.getId() == Long.MIN_VALUE || chequeDetail.getId() == 0) {
			chequeDetail.setId(getNextidviewDAO().getNextId("SeqChequeDetail"));
			logger.debug("get NextID:" + chequeDetail.getId());
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeDetail);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(chequeDetail.getChequeDetailsID());
	}	

	@Override
	public void update(ChequeDetail chequeDetail,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update CHEQUEDETAIL" );
		sql.append(tableType.getSuffix());
		sql.append("  set headerID = :headerID, bankBranchID = :bankBranchID, accountNo = :accountNo, ");
		sql.append(" chequeSerialNo = :chequeSerialNo, chequeDate = :chequeDate, eMIRefNo = :eMIRefNo, ");
		sql.append(" amount = :amount, chequeCcy = :chequeCcy, status = :status, ");
		sql.append(" active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where chequeDetailsID = :chequeDetailsID ");
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeDetail);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ChequeDetail chequeDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CHEQUEDETAIL");
		sql.append(tableType.getSuffix());
		sql.append(" where chequeDetailsID = :chequeDetailsID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeDetail);
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
	
	public void deleteById(long headerID, String tableType) {
		logger.debug(Literal.ENTERING);
		ChequeDetail chequeDetail = new ChequeDetail();
		chequeDetail.setHeaderID(headerID);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CHEQUEDETAIL");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" where headerID = :headerID ");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeDetail);
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
	 * Method for Deletion of Cheque Header Related List of chequeDetail for the Cheque Header
	 */
	public void deleteByCheqID(final long headerID,String type) {
		logger.debug("Entering");
		ChequeDetail chequeDetail = new ChequeDetail();
		chequeDetail.setHeaderID(headerID);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From CHEQUEDETAIL" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where headerID =:headerID ");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(chequeDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
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
	
}	
