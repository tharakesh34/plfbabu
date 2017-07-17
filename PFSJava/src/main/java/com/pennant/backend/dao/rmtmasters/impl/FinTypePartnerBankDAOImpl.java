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
 * FileName    		:  FinTypePartnerBankDAOImpl.java                                       * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-04-2017    														*
 *                                                                  						*
 * Modified Date    :  24-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.rmtmasters.impl;

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
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>FinTypePartnerBank</code> with set of CRUD operations.
 */
public class FinTypePartnerBankDAOImpl extends BasisNextidDaoImpl<FinTypePartnerBank> implements FinTypePartnerBankDAO {
	private static Logger				logger	= Logger.getLogger(FinTypePartnerBankDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinTypePartnerBankDAOImpl() {
		super();
	}
	
	@Override
	public FinTypePartnerBank getFinTypePartnerBank(String finType, long iD,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" iD, finType, purpose, paymentMode, partnerBankID, ");
		if(type.contains("View")){
			sql.append("PartnerBankName, PartnerBankCode,");
		}	
		
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From FinTypePartnerBanks");
		sql.append(type);
		sql.append(" Where iD = :iD and FinType = :FinType");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		FinTypePartnerBank finTypePartnerBank = new FinTypePartnerBank();
		finTypePartnerBank.setID(iD);
		finTypePartnerBank.setFinType(finType);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finTypePartnerBank);
		RowMapper<FinTypePartnerBank> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypePartnerBank.class);

		try {
			finTypePartnerBank = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finTypePartnerBank = null;
		}

		logger.debug(Literal.LEAVING);
		return finTypePartnerBank;
	}		
	
	@Override
	public List<FinTypePartnerBank> getFinTypePartnerBank(String finType, String type) {
		logger.debug(Literal.ENTERING);

		FinTypePartnerBank finTypePartnerBank = new FinTypePartnerBank();
		finTypePartnerBank.setFinType(finType);

		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" iD, finType, purpose, paymentMode, partnerBankID, ");
		if (type.contains("View")) {
			sql.append("PartnerBankName, PartnerBankCode,");
		}

		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinTypePartnerBanks");
		sql.append(type);
		sql.append(" Where FinType = :FinType");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypePartnerBank);
		RowMapper<FinTypePartnerBank> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinTypePartnerBank.class);

		logger.debug(Literal.LEAVING);
		return this.namedParameterJdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	
	@Override
	public String save(FinTypePartnerBank finTypePartnerBank,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		if (finTypePartnerBank.getId() == Long.MIN_VALUE) {
			finTypePartnerBank.setId(getNextidviewDAO().getNextId("SeqFinTypePartnerBanks"));
			logger.debug("get NextID:" + finTypePartnerBank.getId());
		}
		StringBuilder sql =new StringBuilder(" insert into FinTypePartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append(" (iD, finType, purpose, paymentMode, partnerBankID, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :iD, :finType, :purpose, :paymentMode, :partnerBankID, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finTypePartnerBank);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(finTypePartnerBank.getID());
	}	

	@Override
	public void update(FinTypePartnerBank finTypePartnerBank,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update FinTypePartnerBanks" );
		sql.append(tableType.getSuffix());
		sql.append("  set finType = :finType, purpose = :purpose, paymentMode = :paymentMode, ");
		sql.append(" partnerBankID = :partnerBankID, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where iD = :iD ");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finTypePartnerBank);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(FinTypePartnerBank finTypePartnerBank, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from FinTypePartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append(" where iD = :iD ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finTypePartnerBank);
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
	
	@Override
	public void deleteByFinType(String finType, String tableType) {
		logger.debug(Literal.ENTERING);

		FinTypePartnerBank finTypePartnerBank = new FinTypePartnerBank();
		finTypePartnerBank.setFinType(finType);

		// Prepare the SQL.
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypePartnerBanks");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where FinType =:FinType");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypePartnerBank);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
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
	
	/**
	 * Method for fetch number of records from FinTypePartnerBanks
	 * 
	 * @param Fintype
	 * @param PaymentMode
	 * @param PartnerBankID
	 * 
	 * @return Integer
	 */
	@Override
	public int getPartnerBankCount(String finType, String paymentType, String purpose, long partnerBankID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Fintype", finType);
		source.addValue("PaymentMode", paymentType);
		source.addValue("Purpose", purpose);
		source.addValue("PartnerBankID", partnerBankID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From FinTypePartnerBanks");
		selectSql.append(" Where Fintype = :Fintype AND PaymentMode = :PaymentMode AND Purpose = :Purpose AND PartnerBankID = :PartnerBankID");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			return 0;
		}
	}
}	
