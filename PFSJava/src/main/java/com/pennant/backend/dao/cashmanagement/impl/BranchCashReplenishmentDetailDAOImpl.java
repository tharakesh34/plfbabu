package com.pennant.backend.dao.cashmanagement.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.cashmanagement.BranchCashReplenishmentDetailDAO;
import com.pennant.backend.model.cashmanagement.BranchCashReplenishmentDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class BranchCashReplenishmentDetailDAOImpl extends BasicDao<BranchCashReplenishmentDetail>
		implements BranchCashReplenishmentDetailDAO {
	private static Logger logger = LogManager.getLogger(BranchCashReplenishmentDetailDAOImpl.class);

	@Override
	public boolean addReplenishmentDetail(BranchCashReplenishmentDetail branchCashReplenishmentDetail) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder(" INSERT INTO BranchCashRepDetails");
		insertSql.append("( ProcessId, BranchCode, BranchDescription, CashLimit, ReOrderLimit, BranchCash,");
		insertSql.append(
				" AutoTransitAmount, AdhocTransitAmount, AdhocReplenishment, AutoReplenishment, TotalReplenishment)");
		insertSql.append(" values ");
		insertSql.append("( :ProcessId, :BranchCode, :BranchDescription, :CashLimit, :ReOrderLimit, :BranchCash,");
		insertSql.append(
				" :AutoTransitAmount, :AdhocTransitAmount, :AdhocReplenishment, :AutoReplenishment, :TotalReplenishment)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashReplenishmentDetail);
		try {
			jdbcTemplate.update(insertSql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);

		return true;
	}

	@Override
	public boolean updateReplenishmentDetail(BranchCashReplenishmentDetail branchCashReplenishmentDetail) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update BranchCashRepDetails Set ");
		updateSql.append(
				" Approved = :Approved, AmountProcessed = :AmountProcessed, TransactionDate = :TransactionDate ");
		updateSql.append(" Where ProcessId = :ProcessId AND BranchCode = :BranchCode");
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + updateSql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashReplenishmentDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.ENTERING);
		return true;
	}

	@Override
	public List<BranchCashReplenishmentDetail> getReplenishmentDetailsByProcessId(long processId) {
		logger.debug(Literal.ENTERING);

		List<BranchCashReplenishmentDetail> list;
		StringBuilder selectSql = new StringBuilder(" Select ");
		selectSql.append(" ProcessId, BranchCode, BranchDescription, CashLimit, ReOrderLimit, BranchCash,");
		selectSql.append(
				" AutoTransitAmount , AdhocTransitAmount , AdhocReplenishment, AutoReplenishment, TotalReplenishment, ");
		selectSql.append(" Approved, AmountProcessed, TransactionDate ");
		selectSql.append(" From BranchCashRepDetails ");
		selectSql.append(" WHERE ProcessId = :ProcessId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + selectSql.toString());
		BranchCashReplenishmentDetail detail = new BranchCashReplenishmentDetail();
		detail.setProcessId(processId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(detail);
		RowMapper<BranchCashReplenishmentDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BranchCashReplenishmentDetail.class);
		list = jdbcTemplate.query(selectSql.toString(), paramSource, rowMapper);

		logger.debug(Literal.LEAVING);
		return list;
	}

	@Override
	public List<BranchCashReplenishmentDetail> getResponseDetailsByBatchId(long batchId) {
		logger.debug(Literal.ENTERING);

		List<BranchCashReplenishmentDetail> list;
		StringBuilder selectSql = new StringBuilder(" Select ");
		selectSql.append(" ProcessId,BranchCode, BranchDescription, CashLimit, ReOrderLimit, BranchCash,");
		selectSql.append(
				" AutoTransitAmount , AdhocTransitAmount , AdhocReplenishment, AutoReplenishment, TotalReplenishment, ");
		selectSql.append(" Approved, AmountProcessed, TransactionDate, BatchId ");
		selectSql.append(" From BranchCashRepDetails_RESP ");
		selectSql.append(" WHERE BatchId = :BatchId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + selectSql.toString());
		BranchCashReplenishmentDetail detail = new BranchCashReplenishmentDetail();
		detail.setBatchId(batchId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(detail);
		RowMapper<BranchCashReplenishmentDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BranchCashReplenishmentDetail.class);
		list = jdbcTemplate.query(selectSql.toString(), paramSource, rowMapper);

		logger.debug(Literal.LEAVING);
		return list;
	}

	@Override
	public Map<String, Object> getReplenishmentDetailDifference(long batchId) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder(
				" select count(*) COUNT, H.DOWNLOADFILE from DE_BRANCHCASHREPDETAILS_VIEW REQ ");
		selectSql.append(" INNER JOIN BRANCHCASHREPHEADER H ON REQ.PROCESSID = H.PROCESSID ");
		selectSql.append(" INNER JOIN BRANCHCASHREPDETAILS_RESP RESP ON  RESP.BRANCHCODE = REQ.BRANCHCODE ");
		selectSql.append(" AND RESP.PROCESSID = REQ.PROCESSID ");
		selectSql.append(" AND RESP.BRANCHDESCRIPTION = REQ.BRANCHDESCRIPTION  AND RESP.CASHLIMIT = REQ.CASHLIMIT ");
		selectSql.append(" AND RESP.REORDERLIMIT = REQ.REORDERLIMIT AND RESP.BRANCHCASH = REQ.BRANCHCASH ");
		selectSql.append(
				" AND RESP.AUTOTRANSITAMOUNT = REQ.AUTOTRANSITAMOUNT AND RESP.ADHOCTRANSITAMOUNT = REQ.ADHOCTRANSITAMOUNT ");
		selectSql.append(
				" AND RESP.ADHOCREPLENISHMENT = REQ.ADHOCREPLENISHMENT AND RESP.AUTOREPLENISHMENT = REQ.AUTOREPLENISHMENT");
		selectSql.append(" AND RESP.TOTALREPLENISHMENT = REQ.TOTALREPLENISHMENT ");
		selectSql.append(" AND RESP.BATCHID = :BATCHID ");
		selectSql.append(" GROUP BY DOWNLOADFILE ");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("BATCHID", batchId);

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + selectSql.toString());
		Map<String, Object> respose;

		try {
			respose = jdbcTemplate.queryForMap(selectSql.toString(), map);
		} catch (EmptyResultDataAccessException e) {
			respose = new HashMap<String, Object>();
		}

		logger.debug(Literal.LEAVING);

		return respose;
	}
}
