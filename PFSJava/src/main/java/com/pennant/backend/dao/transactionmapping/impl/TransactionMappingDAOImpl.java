package com.pennant.backend.dao.transactionmapping.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.transactionmapping.TransactionMappingDAO;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.cd.model.TransactionMapping;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class TransactionMappingDAOImpl extends SequenceDao<TransactionMapping> implements TransactionMappingDAO {

	private static final Logger logger = LogManager.getLogger(TransactionMappingDAOImpl.class);

	@Override
	public String save(TransactionMapping mapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String mapId = null;

		try {
			StringBuilder sql = new StringBuilder("insert into TransactionMapping");
			sql.append(tableType.getSuffix());
			sql.append("(Id, POSId, DealerCode, DealerName, MID, TID, MobileNumber1, MobileNumber2, MobileNumber3");
			sql.append(", Active, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
			sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
			sql.append(" values");
			sql.append(
					"(:Id, :posId, :DealerCode, :DealerName, :mid, :tid, :MobileNumber1, :MobileNumber2, :MobileNumber3");
			sql.append(", :Active, :Version , :LastMntBy, :LastMntOn, :RecordStatus");
			sql.append(", :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

			if (mapping.getId() == Long.MIN_VALUE) {
				mapping.setId(getNextValue("SEQTRANSACTIONMAPPING"));
			}

			mapId = String.valueOf(mapping.getId());

			logger.trace(Literal.SQL + sql.toString());
			SqlParameterSource paramSource = new BeanPropertySqlParameterSource(mapping);

			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return mapId;
	}

	@Override
	public void update(TransactionMapping mapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update TransactionMapping");
		sql.append(tableType.getSuffix());
		sql.append(" Set POSId= :posId, DealerCode= :DealerCode, DealerName= :DealerName,");
		sql.append(" MID= :mid, TID= :tid, MobileNumber1 = :MobileNumber1, MobileNumber2 = :MobileNumber2");
		sql.append(", MobileNumber3 = :MobileNumber3, Active = :Active, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus = :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where Id = :Id ");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(mapping);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(TransactionMapping mapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from TransactionMapping");
		sql.append(tableType.getSuffix());
		sql.append(" where Id = :Id ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(mapping);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public TransactionMapping getTransactionMappingById(long id, String type) {
		logger.debug(Literal.ENTERING);

		TransactionMapping mapping = new TransactionMapping();
		mapping.setId(id);

		try {
			StringBuilder sql = new StringBuilder("Select Id");
			sql.append(
					", POSId, DealerCode, DealerName, MID, TID, MobileNumber1, MobileNumber2, MobileNumber3, Active");
			sql.append(
					", Version, LastMntBy, LastMntOn,RecordStatus,RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
			sql.append(" from  TransactionMapping");
			sql.append(type);
			sql.append(" where Id=:id");

			logger.trace(Literal.SQL + sql.toString());
			SqlParameterSource paramSource = new BeanPropertySqlParameterSource(mapping);

			RowMapper<TransactionMapping> rowMapper = BeanPropertyRowMapper.newInstance(TransactionMapping.class);

			mapping = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION + e);
			mapping = null;
		}

		logger.debug(Literal.ENTERING);
		return mapping;
	}

	@Override
	public boolean isDuplicateKey(TransactionMapping mapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "tid = :tid  and posId= :posId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("TransactionMapping", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("TransactionMapping_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "TransactionMapping_Temp", "TransactionMapping" },
					whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("tid", mapping.getTid());
		paramSource.addValue("posId", mapping.getPosId());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public int getcountByMID(long mid, String tid) {
		logger.debug("Entering");
		TransactionMapping transactionMapping = new TransactionMapping();
		transactionMapping.setMid(mid == Long.MIN_VALUE ? 0 : mid);
		transactionMapping.setTid(tid);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM TransactionMapping");
		selectSql.append(" WHERE mid = :mid AND tid = :tid");

		logger.debug("SelectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionMapping);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public int getCountByPhoneAndStroeId(String mobileNumber, int storeId, int posId) {

		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("MID", storeId);
		mapSqlParameterSource.addValue("PosId", posId);
		mapSqlParameterSource.addValue("MOBILENUMBER1", mobileNumber);
		mapSqlParameterSource.addValue("MOBILENUMBER2", mobileNumber);
		mapSqlParameterSource.addValue("MOBILENUMBER3", mobileNumber);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT COUNT(*) FROM TransactionMapping");
		selectSql.append(" WHERE( (MID = :MID AND PosId = :PosId) AND");
		selectSql.append(
				"( MOBILENUMBER1 = :MOBILENUMBER1 OR MOBILENUMBER2 = :MOBILENUMBER2 OR MOBILENUMBER3 = :MOBILENUMBER3))");

		logger.debug("SelectSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class);
	}

	@Override
	public int getCountByPhoneNumber(String mobileNumber) {

		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("MOBILENUMBER1", mobileNumber);
		mapSqlParameterSource.addValue("MOBILENUMBER2", mobileNumber);
		mapSqlParameterSource.addValue("MOBILENUMBER3", mobileNumber);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM TransactionMapping");
		selectSql.append(
				" WHERE ( MOBILENUMBER1 = :MOBILENUMBER1 OR MOBILENUMBER2 = :MOBILENUMBER2 OR MOBILENUMBER3 = :MOBILENUMBER3)");

		logger.debug("SelectSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class);
	}

	@Override
	public TransactionMapping getDealerDetails(long mId, String tId) {
		String sql = "Select DealerCode, DealerName From TransactionMapping Where Mid = ? and Tid = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				TransactionMapping tm = new TransactionMapping();

				tm.setDealerCode(rs.getLong("DealerCode"));
				tm.setDealerName(rs.getString("DealerName"));

				return tm;
			}, mId, tId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

}
