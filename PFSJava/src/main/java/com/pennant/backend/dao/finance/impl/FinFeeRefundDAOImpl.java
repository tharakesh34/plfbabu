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
 * FileName    		:  FinFeeReceiptDAOImpl.java                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-12-2019    														*
 *                                                                  						*
 * Modified Date    :  22-12-2019    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-12-2019       Ganesh.P	                 0.1                                        * 
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

import com.pennant.backend.dao.finance.FinFeeRefundDAO;
import com.pennant.backend.model.finance.FinFeeRefundDetails;
import com.pennant.backend.model.finance.FinFeeRefundHeader;
import com.pennant.backend.model.finance.PrvsFinFeeRefund;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>FinFeeReceipt model</b> class.<br>
 * 
 */

public class FinFeeRefundDAOImpl extends SequenceDao<FinFeeRefundHeader> implements FinFeeRefundDAO {
	private static Logger logger = Logger.getLogger(FinFeeRefundDAOImpl.class);

	public FinFeeRefundDAOImpl() {
		super();
	}

	@Override
	public long save(FinFeeRefundHeader finFeeRefundHeader, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into FinFeeRefundHeader");
		sql.append(type);

		sql.append("( HeaderId, FinReference, LinkedTranId, ");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId )");
		sql.append(" Values ");
		sql.append("( :HeaderId, :FinReference, :LinkedTranId, ");
		sql.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (finFeeRefundHeader.getId() <= 0) {
			finFeeRefundHeader.setId(getNextValue(("SeqFinFeeRefundHeader")));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finFeeRefundHeader);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return finFeeRefundHeader.getId();
	}

	@Override
	public void update(FinFeeRefundHeader finFeeRefundHeader, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update FinFeeRefundHeader");
		sql.append(type);
		sql.append(" Set HeaderId = :HeaderId, FinReference = :FinReference, LinkedTranId = :LinkedTranId, ");
		sql.append(" Version = :Version,LastMntBy = :LastMntBy,LastMntOn = :LastMntOn,RecordStatus = :RecordStatus,");
		sql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where HeaderId = :HeaderId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finFeeRefundHeader);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteFinFeeRefundHeader(FinFeeRefundHeader refundHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Delete from FinFeeRefundHeader");
		sql.append(tableType.getSuffix());
		sql.append(" where HeaderId = :HeaderId");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(refundHeader);
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
	 * Fetch the Record FinFeeRefundDetails details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinFeeRefund
	 */
	@Override
	public FinFeeRefundHeader getFinFeeRefundHeaderById(long headerId, String type) {
		logger.debug(Literal.ENTERING);

		FinFeeRefundHeader finFeeRefundHeader = new FinFeeRefundHeader();
		finFeeRefundHeader.setId(headerId);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT  HeaderId, finReference, LinkedTranId, ");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId ");
		if (type.contains("View")) {
			sql.append(
					" ,Fintype, FinBranch, FinCcy, lovDescCustCIF, LovDescCustShrtName, fintypedesc, branchdesc, custId ");
		}
		sql.append(" FROM  FinFeeRefundHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderId =:HeaderId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeRefundHeader);
		RowMapper<FinFeeRefundHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinFeeRefundHeader.class);
		try {
			finFeeRefundHeader = jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finFeeRefundHeader = null;
		}
		logger.debug(Literal.LEAVING);
		return finFeeRefundHeader;
	}

	/**
	 * Fetch the Record FinFeeRefundDetails details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinFeeRefund
	 */
	@Override
	public FinFeeRefundDetails getFinFeeRefundDetailsById(long id, String type) {
		logger.debug(Literal.ENTERING);

		FinFeeRefundDetails FinFeeRefund = new FinFeeRefundDetails();
		FinFeeRefund.setId(id);
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" Id, HeaderId, FeeId, RefundAmount, RefundAmtGST, RefundAmtOriginal,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId ");
		sql.append(" FROM  FinFeeRefundDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id =:Id");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(FinFeeRefund);
		RowMapper<FinFeeRefundDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinFeeRefundDetails.class);
		try {
			FinFeeRefund = jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			//logger.error("Exception: ", e);
			FinFeeRefund = null;
		}
		logger.debug(Literal.LEAVING);
		return FinFeeRefund;
	}

	/**
	 * Fetch the Record FinFeeRefundDetails details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinFeeRefund
	 */
	@Override
	public List<FinFeeRefundDetails> getFinFeeRefundDetailsByHeaderId(long headerId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" Id, HeaderId, FeeId, RefundAmount, RefundAmtGST, RefundAmtOriginal, RefundAmtTds,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
		}
		sql.append(" FROM  FinFeeRefundDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderId =:HeaderId ");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("HeaderId", headerId);
		RowMapper<FinFeeRefundDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinFeeRefundDetails.class);

		return this.jdbcTemplate.query(sql.toString(), mapSqlParameterSource, typeRowMapper);
	}

	@Override
	public String save(FinFeeRefundDetails finFeeRefundDetails, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into FinFeeRefundDetails");
		sql.append(type);

		sql.append("(Id, HeaderId, FeeId, RefundAmount, RefundAmtGST, RefundAmtOriginal, RefundAmtTDS,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId )");
		sql.append(" Values ");
		sql.append("(:Id, :HeaderId, :FeeId, :RefundAmount, :RefundAmtGST, :RefundAmtOriginal, :RefundAmtTDS,");
		sql.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (finFeeRefundDetails.getId() <= 0) {
			finFeeRefundDetails.setId(getNextValue(("SeqFinFeeRefundDetails")));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finFeeRefundDetails);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(finFeeRefundDetails.getId());
	}

	@Override
	public void update(FinFeeRefundDetails FinFeeRefund, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update FinFeeRefundDetails");
		sql.append(type);
		sql.append(" Set Id = :Id, HeaderId = :HeaderId, FeeId = :FeeId, RefundAmount = :RefundAmount, ");
		sql.append(" RefundAmtGST = :RefundAmtGST, RefundAmtOriginal = :RefundAmtOriginal,");
		sql.append(" Version = :Version,LastMntBy = :LastMntBy,LastMntOn = :LastMntOn,RecordStatus = :RecordStatus,");
		sql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where Id = :Id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(FinFeeRefund);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteFinFeeRefundDetailsByID(FinFeeRefundDetails refundDetails, String type) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Delete from FinFeeRefundDetails");
		sql.append(type);
		sql.append(" where Id = :Id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(refundDetails);
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
	public void deleteFinFeeRefundDetailsByHeaderID(FinFeeRefundHeader refundHeader, String type) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Delete from FinFeeRefundDetails");
		sql.append(type);
		sql.append(" where HeaderId = :HeaderId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(refundHeader);
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
	public PrvsFinFeeRefund getPrvsRefundsByFeeId(long feeID) {
		logger.debug(Literal.ENTERING);

		PrvsFinFeeRefund prvsFinFeeRefund = new PrvsFinFeeRefund();
		prvsFinFeeRefund.setFeeId(feeID);
		StringBuilder sql = new StringBuilder();
		sql.append(" Select Sum(refundAmount) as TotRefundAmount, ");
		sql.append(" Sum(refundAmtGST) as TotRefundAmtGST, Sum(refundAmtOriginal) as TotRefundAmtOriginal ,");
		sql.append(" Sum(refundAmtTDS) as TotRefundAmtTDS");
		sql.append(" FROM  FinFeeRefundDetails");
		sql.append(" Where FeeId =:FeeId");
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(prvsFinFeeRefund);
		RowMapper<PrvsFinFeeRefund> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PrvsFinFeeRefund.class);
		try {
			prvsFinFeeRefund = jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			prvsFinFeeRefund = null;
		}
		logger.debug(Literal.LEAVING);
		return prvsFinFeeRefund;
	}

}