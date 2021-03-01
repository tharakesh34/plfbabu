package com.pennant.backend.dao.dealermapping.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.dealermapping.DealerMappingDAO;
import com.pennant.backend.model.dealermapping.DealerMapping;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class DealerMappingDAOImpl extends SequenceDao<DealerMapping> implements DealerMappingDAO {

	private final static Logger logger = LogManager.getLogger(DealerMappingDAOImpl.class);

	@Override
	public String save(DealerMapping dealerMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String dealerMapId = null;
		StringBuilder sql = new StringBuilder("insert into CD_DealerMapping");
		sql.append(tableType.getSuffix());
		sql.append("(DealerMapId, MerchantId, StoreId, DealerCode, PosId, Active, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append(
				"(:DealerMapId, :MerchantId, :StoreId, :DealerCode, :PosId, :Active, :Version , :LastMntBy, :LastMntOn, :RecordStatus");
		sql.append(", :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (dealerMapping.getDealerMapId() == Long.MIN_VALUE) {
			dealerMapping.setDealerMapId(getNextValue("SEQCD_DEALERMAPPING"));
		}

		dealerMapId = String.valueOf(dealerMapping.getDealerMapId());

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dealerMapping);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return dealerMapId;
	}

	@Override
	public void update(DealerMapping dealerMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update CD_DealerMapping");
		sql.append(tableType.getSuffix());

		sql.append(
				" set MerchantId=:MerchantId, StoreId=:StoreId, DealerCode=:DealerCode, PosId= :PosId, Active = :Active");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where MerchantId = :MerchantId ");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dealerMapping);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(DealerMapping dealerMapping, TableType tableType) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from CD_DealerMapping");
		sql.append(tableType.getSuffix());
		sql.append(" where DealerMapId = :DealerMapId ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dealerMapping);
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
	public DealerMapping getDealerMappingById(long id, String type) {
		logger.debug(Literal.ENTERING);

		DealerMapping dealerMapping = new DealerMapping();
		dealerMapping.setDealerMapId(id);

		try {
			StringBuilder sql = new StringBuilder("Select DealerMapId,");
			sql.append(" MerchantId, StoreId, DealerCode, posId, Active, Version , LastMntBy, LastMntOn,");
			sql.append("RecordStatus,RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

			if (type.contains("_View")) {
				sql.append(",merchantName,storeName,storeCity,storeAddress");
			}

			sql.append(" from  CD_DealerMapping");
			sql.append(type);
			sql.append(" where DealerMapId=:DealerMapId");

			logger.trace(Literal.SQL + sql.toString());
			SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dealerMapping);

			RowMapper<DealerMapping> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DealerMapping.class);

			dealerMapping = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION + e);
			dealerMapping = null;
		}

		logger.debug(Literal.ENTERING);
		return dealerMapping;
	}

	@Override
	public boolean isDuplicateKey(DealerMapping dealerMapping, TableType tableType) {

		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "DealerMapId = :DealerMapId ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CD_DealerMapping", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CD_DealerMapping_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CD_DealerMapping_Temp", "CD_DealerMapping" }, whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("DealerMapId", dealerMapping.getDealerMapId());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;

	}

	@Override
	public long getDealerCode() {
		return getNextValue("SEQCD_DEALERMAPPING_Code");
	}

}
