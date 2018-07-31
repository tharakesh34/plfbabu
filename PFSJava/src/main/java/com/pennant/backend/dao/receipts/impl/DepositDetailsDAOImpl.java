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
 * FileName    		:  DepositDetailsDAOImpl.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-07-2018    														*
 *                                                                  						*
 * Modified Date    :  10-07-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-07-2018       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.receipts.impl;

import java.math.BigDecimal;

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

import com.pennant.backend.dao.receipts.DepositDetailsDAO;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.model.finance.DepositMovements;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class DepositDetailsDAOImpl extends SequenceDao<DepositDetails> implements DepositDetailsDAO {
	private static Logger logger = Logger.getLogger(DepositDetailsDAOImpl.class);

	
	public DepositDetailsDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Academic
	 */
	@Override
	public DepositDetails getDepositDetailsById(long id, String type) {
		logger.debug(Literal.ENTERING);

		DepositDetails depositDetails = new DepositDetails();
		depositDetails.setDepositId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select DepositId, DepositType, BranchCode, ActualAmount, TransactionAmount, ReservedAmount,");
		if (type.contains("View")) {
			selectSql.append(" BranchDesc,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM DepositDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DepositId = :DepositId");

		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(depositDetails);
		RowMapper<DepositDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DepositDetails.class);

		try {
			depositDetails = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			depositDetails = null;
		}

		logger.debug(Literal.LEAVING);
		return depositDetails;
	}
	
	@Override
	public DepositDetails getDepositDetails(String depositType, String branchCode, String type) {
		logger.debug(Literal.ENTERING);

		DepositDetails depositDetails = new DepositDetails();
		depositDetails.setDepositType(depositType);
		depositDetails.setBranchCode(branchCode);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select DepositId, DepositType, BranchCode, ActualAmount, TransactionAmount, ReservedAmount,");
		if (type.contains("View")) {
			selectSql.append(" BranchDesc,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM DepositDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DepositType = :DepositType And BranchCode = :BranchCode");

		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(depositDetails);
		RowMapper<DepositDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DepositDetails.class);

		try {
			depositDetails = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			depositDetails = null;
		}

		logger.debug(Literal.LEAVING);
		return depositDetails;
	}
	
	@Override
	public boolean isDuplicateKey(long depositId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "DepositId = :DepositId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("DepositDetails", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("DepositDetails_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "DepositDetails_Temp", "DepositDetails" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("DepositId", depositId);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public long save(DepositDetails depositDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Insert into DepositDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (DepositId, DepositType, BranchCode, ActualAmount, TransactionAmount, ReservedAmount,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values (:DepositId, :DepositType, :BranchCode, :ActualAmount, :TransactionAmount, :ReservedAmount,");
		sql.append("  :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		// Get the identity sequence number.
		if (depositDetails.getDepositId() <= 0) {
			depositDetails.setDepositId(getNextValue("SeqDepositDetails"));
		}
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(depositDetails);
		
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		
		logger.debug(Literal.LEAVING);
		return depositDetails.getDepositId();
	}

	@Override
	public void update(DepositDetails depositDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update DepositDetails");
		sql.append(tableType.getSuffix());
		
		sql.append(" set DepositType = :DepositType, BranchCode = :BranchCode,");
		if ("_Temp".equalsIgnoreCase(tableType.getSuffix())) {
			sql.append(" ActualAmount = :ActualAmount, TransactionAmount = :TransactionAmount, ReservedAmount = :ReservedAmount,");
		} else {
			sql.append(" TransactionAmount = TransactionAmount + :ReservedAmount, ReservedAmount = 0,");
		}
		sql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where DepositId = :DepositId");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(depositDetails);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public void updateTransactionAmount(long depositId, BigDecimal transactionAmount, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("TransactionAmount", transactionAmount);
		source.addValue("DepositId", depositId);

		StringBuilder updateSql = new StringBuilder("Update DepositDetails");
		updateSql.append(type);
		updateSql.append(" Set TransactionAmount = TransactionAmount + :TransactionAmount");
		updateSql.append(" Where DepositId = :DepositId");
		logger.trace(Literal.SQL + updateSql.toString());

		this.jdbcTemplate.update(updateSql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateActualAmount(long depositId, BigDecimal actualAmount, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ActualAmount", actualAmount);
		source.addValue("DepositId", depositId);

		StringBuilder updateSql = new StringBuilder("Update DepositDetails");
		updateSql.append(type);
		updateSql.append(" Set ActualAmount = ActualAmount + :ActualAmount");
		updateSql.append(" Where DepositId = :DepositId");
		logger.trace(Literal.SQL + updateSql.toString());

		this.jdbcTemplate.update(updateSql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(DepositDetails depositDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from DepositDetails");
		sql.append(tableType.getSuffix());
		sql.append(" where DepositId = :DepositId");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(depositDetails);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
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
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Academic
	 */
	@Override
	public DepositMovements getDepositMovementsById(long movementId, String type) {
		logger.debug(Literal.ENTERING);
		
		DepositMovements depositMovements = new DepositMovements();
		depositMovements.setMovementId(movementId);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select MovementId, DepositId, TransactionType, ReservedAmount, PartnerBankId, DepositSlipNumber, TransactionDate, ReceiptId, LinkedTranId,");
		if (type.contains("View")) {
			selectSql.append(" PartnerBankCode, PartnerBankName, BranchCode, BranchDesc,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM DepositMovements");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where MovementId = :MovementId");
		
		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(depositMovements);
		RowMapper<DepositMovements> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DepositMovements.class);
		
		try {
			depositMovements = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.info("Information: ", e);
			depositMovements = null;
		}
		
		logger.debug(Literal.LEAVING);
		return depositMovements;
	}
	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Academic
	 */
	@Override
	public DepositMovements getDepositMovementsByReceiptId(long receiptId, String type) {
		logger.debug(Literal.ENTERING);
		
		DepositMovements depositMovements = new DepositMovements();
		depositMovements.setReceiptId(receiptId);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select MovementId, DepositId, TransactionType, ReservedAmount, PartnerBankId, DepositSlipNumber, TransactionDate, ReceiptId, LinkedTranId,");
		if (type.contains("View")) {
			selectSql.append(" PartnerBankCode, PartnerBankName, BranchCode, BranchDesc,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM DepositMovements");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ReceiptId = :ReceiptId");
		
		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(depositMovements);
		RowMapper<DepositMovements> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DepositMovements.class);
		
		try {
			depositMovements = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.info("Information: ", e);
			depositMovements = null;
		}
		
		logger.debug(Literal.LEAVING);
		return depositMovements;
	}

	@Override
	public long saveDepositMovements(DepositMovements depositMovements, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Insert into DepositMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (MovementId, DepositId, TransactionType, ReservedAmount, PartnerBankId, DepositSlipNumber, TransactionDate, ReceiptId, LinkedTranId,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values (:MovementId, :DepositId, :TransactionType, :ReservedAmount, :PartnerBankId, :DepositSlipNumber, :TransactionDate, :ReceiptId, :LinkedTranId,");
		sql.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (depositMovements.getMovementId() <= 0) {
			depositMovements.setMovementId(getNextValue("SeqDepositMovements"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(depositMovements);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return depositMovements.getMovementId();
	}
	
	@Override
	public void updateDepositMovements(DepositMovements depositMovements, String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update DepositMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" set MovementId = :MovementId, TransactionType = :TransactionType, ReservedAmount = :ReservedAmount, PartnerBankId = :PartnerBankId,");
		sql.append(" DepositSlipNumber = DepositSlipNumber, TransactionDate = :TransactionDate, LinkedTranId = :LinkedTranId,");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where DepositId = :DepositId");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(depositMovements);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public void updateLinkedTranIdByMovementId(long movementId, long likedTranId, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("MovementId", movementId);
		source.addValue("LinkedTranId", likedTranId);

		StringBuilder updateSql = new StringBuilder("Update DepositMovements");
		updateSql.append(type);
		updateSql.append(" Set LinkedTranId = :LinkedTranId");
		updateSql.append(" Where MovementId = :MovementId");
		logger.trace(Literal.SQL + updateSql.toString());

		this.jdbcTemplate.update(updateSql.toString(), source);

		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public void deleteDepositMovements(DepositMovements depositMovements, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from DepositMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where MovementId = :MovementId");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(depositMovements);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
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
	public void deleteMovementsByDepositId(long depositId, String type) {
		logger.debug(Literal.ENTERING);
		DepositMovements depositMovements= new DepositMovements();
		depositMovements.setDepositId(depositId);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from DepositMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where DepositId = :DepositId");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(depositMovements);

		try {
			this.jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public DepositMovements getDepositMovementsByDepositId(long depositId, String type) {
		logger.debug(Literal.ENTERING);
		
		DepositMovements depositMovements = new DepositMovements();
		depositMovements.setDepositId(depositId);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select MovementId, DepositId, TransactionType, ReservedAmount, PartnerBankId, DepositSlipNumber, TransactionDate, ReceiptId, LinkedTranId,");
		if (type.contains("View")) {
			selectSql.append(" PartnerBankCode, PartnerBankName, BranchCode, BranchDesc,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM DepositMovements");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DepositId = :DepositId And MovementId = (Select Max(MovementId) from DepositMovements");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DepositId = :DepositId)");
		
		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(depositMovements);
		RowMapper<DepositMovements> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DepositMovements.class);
		
		try {
			depositMovements = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			depositMovements = null;
		}
		
		logger.debug(Literal.LEAVING);
		return depositMovements;
	}
	
	@Override
	public boolean isDuplicateKey(String depositSlipNumber, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "DepositSlipNumber = :DepositSlipNumber";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("DepositMovements", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("DepositMovements_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "DepositMovements_Temp", "DepositMovements" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("DepositSlipNumber", depositSlipNumber);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
}
