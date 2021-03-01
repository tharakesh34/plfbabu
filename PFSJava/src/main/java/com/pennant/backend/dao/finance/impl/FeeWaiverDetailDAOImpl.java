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
 * FileName    		:  FeeWaiverDetailDAOImpl.java                                          * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-11-2017    														*
 *                                                                  						*
 * Modified Date    :  			    														*
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
package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FeeWaiverDetailDAO;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>FeeWaiverDetail</code> with set of CRUD operations.
 */

public class FeeWaiverDetailDAOImpl extends SequenceDao<FeeWaiverDetail> implements FeeWaiverDetailDAO {
	private static Logger logger = LogManager.getLogger(FeeWaiverDetailDAOImpl.class);

	public FeeWaiverDetailDAOImpl() {
		super();
	}

	@Override
	public String save(FeeWaiverDetail feeWaiverDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Get the identity sequence number.
		if (feeWaiverDetail.getWaiverDetailId() <= 0) {
			feeWaiverDetail.setWaiverDetailId(getNextValue("SeqFeeWaiverDetail"));
		}

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into FeeWaiverDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (WaiverDetailId, WaiverId, AdviseId, FinODSchdDate, ReceivableAmount,");
		sql.append(" ReceivedAmount, WaivedAmount, BalanceAmount, CurrWaiverAmount, FeeTypeCode, FeeTypeDesc,");
		sql.append(
				" WaiverType, TaxHeaderId, ActualReceivable, ReceivableGST, CurrActualWaiver, CurrWaiverGST, TaxApplicable, TaxComponent,");
		sql.append(" Version, lastMntBy, lastMntOn)");
		sql.append(" Values( :WaiverDetailId, :WaiverId, :AdviseId, :FinODSchdDate, :ReceivableAmount,");
		sql.append(" :ReceivedAmount, :WaivedAmount, :BalanceAmount, :CurrWaiverAmount, :FeeTypeCode, :FeeTypeDesc,");
		sql.append(
				" :WaiverType, :TaxHeaderId, :ActualReceivable, :ReceivableGST, :CurrActualWaiver, :CurrWaiverGST, :TaxApplicable, :TaxComponent,");
		sql.append(" :Version, :lastMntBy, :lastMntOn)");
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(feeWaiverDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(feeWaiverDetail.getWaiverId());
	}

	@Override
	public void update(FeeWaiverDetail feeWaiverDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update FeeWaiverDetails");
		sql.append(tableType.getSuffix());
		sql.append(" set WaiverId = :WaiverId,");
		sql.append(" AdviseId = :AdviseId, FinODSchdDate = :FinODSchdDate, ReceivableAmount = :ReceivableAmount,");
		sql.append(" ReceivedAmount = :ReceivedAmount, WaivedAmount= :WaivedAmount, BalanceAmount = :BalanceAmount,");
		sql.append(" CurrWaiverAmount = :CurrWaiverAmount, FeeTypeCode = :FeeTypeCode, FeeTypeDesc = :FeeTypeDesc,");
		sql.append(
				" WaiverType = :WaiverType, TaxHeaderId = :TaxHeaderId, ActualReceivable = :ActualReceivable, ReceivableGST = :ReceivableGST,");
		sql.append(
				" CurrActualWaiver = :CurrActualWaiver, CurrWaiverGST = :CurrWaiverGST, TaxApplicable = :TaxApplicable, TaxComponent = :TaxComponent");
		sql.append(" Where WaiverDetailId =:WaiverDetailId");
		// sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(feeWaiverDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(FeeWaiverDetail feeWaiverDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from FeeWaiverDetails");
		sql.append(tableType.getSuffix());
		sql.append(" where WaiverDetailId = :WaiverDetailId");
		// sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(feeWaiverDetail);
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
	public List<FeeWaiverDetail> getFeeWaiverByWaiverId(long waiverId, String type) {
		logger.debug(Literal.ENTERING);

		FeeWaiverDetail feeWaiverDetail = new FeeWaiverDetail();
		feeWaiverDetail.setWaiverId(waiverId);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select WaiverDetailId, WaiverId, AdviseId, FinODSchdDate, ReceivableAmount,");
		selectSql.append(" ReceivedAmount, WaivedAmount, BalanceAmount, CurrWaiverAmount, FeeTypeCode, FeeTypeDesc,");
		selectSql.append(
				" WaiverType, TaxHeaderId, ActualReceivable, ReceivableGST, CurrActualWaiver, CurrWaiverGST, TaxApplicable, TaxComponent,");
		selectSql.append(" Version, lastMntBy, lastMntOn");
		selectSql.append(" From  FeeWaiverDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where WaiverId =:WaiverId");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeWaiverDetail);
		RowMapper<FeeWaiverDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FeeWaiverDetail.class);

		try {
			return jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			feeWaiverDetail = null;
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<FeeWaiverDetail> getFeeWaiverEnqDetailList(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select fd.WaiverDetailId, fd.WaiverId, fd.AdviseId, fd.FinODSchdDate, fd.ReceivableAmount");
		sql.append(", fd.FeeTypeCode, fd.WaiverType, fd.ReceivedAmount, fd.WaivedAmount, fd.BalanceAmount");
		sql.append(", fd.CurrWaiverAmount, fd.FeeTypeDesc, fd.actualreceivable");
		sql.append(", fd.receivablegst, fd.curractualwaiver, fd.currwaivergst, fh.valueDate, su.usrFName waivedBy");
		sql.append(" from FeeWaiverDetails fd");
		sql.append(" inner join FeeWaiverHeader fh on fh.waiverId = fd.waiverId");
		sql.append(" left join SecUsers su on fh.lastMntBy = su.usrid");
		sql.append(" where fh.waiverId in ");
		sql.append(" (select waiverId from FeeWaiverHeader where FinReference = :FinReference)");
		sql.append(" order by WaiverId");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);

		RowMapper<FeeWaiverDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FeeWaiverDetail.class);

		try {
			return jdbcTemplate.query(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			//
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public BigDecimal getFeeWaiverDetailList(String finReference, long adviseId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select sum(currwaivergst)");
		sql.append(" from feewaiverheader fh");
		sql.append(" LEFT JOIN feewaiverdetails fwd on fwd.waiverid = fh.waiverid");
		sql.append(" where adviseId = :adviseId and FinReference = :FinReference and currwaiveramount > 0 ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);
		parameterSource.addValue("adviseId", adviseId);

		BigDecimal currwaivergst = BigDecimal.ZERO;

		try {
			currwaivergst = this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			currwaivergst = BigDecimal.ZERO;
		}
		logger.debug(Literal.LEAVING);
		return currwaivergst;
	}
}