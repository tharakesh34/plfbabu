package com.pennattech.pff.mmfl.cd.dao;

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

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.mmfl.cd.model.ConsumerProduct;

public class ConsumerProductDAOImpl extends SequenceDao<ConsumerProduct> implements ConsumerProductDAO {
	private static Logger logger = LogManager.getLogger(ConsumerProductDAOImpl.class);

	public ConsumerProductDAOImpl() {
		super();
	}

	@Override
	public ConsumerProduct getConsumerProduct(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select ProductId, ModelId, ModelDescription, ManufacturerId, AssetDescription, MinAmount");
		if (type.contains("View")) {
			sql.append(" ");
		}
		sql.append(", MaxAmount, ModelStatus, Channel, Active, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CD_PRODUCTS");
		sql.append(type);
		sql.append(" Where ProductId = :productId");

		ConsumerProduct consumerProduct = new ConsumerProduct();
		consumerProduct.setProductId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(consumerProduct);
		RowMapper<ConsumerProduct> rowMapper = BeanPropertyRowMapper.newInstance(ConsumerProduct.class);

		try {
			consumerProduct = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return consumerProduct;
	}

	@Override
	public String save(ConsumerProduct consumerProduct, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("insert into CD_PRODUCTS");
		sql.append(tableType.getSuffix());
		sql.append("(ProductId, ModelId, ModelDescription, ManufacturerId, AssetDescription, MinAmount, MaxAmount");
		sql.append(", ModelStatus, Channel, Active, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append("(:productId, :modelId, :modelDescription, :ManufacturerId, :AssetDescription, :MinAmount");
		sql.append(", :MaxAmount, :modelStatus, :channel, :active, :Version , :LastMntBy, :LastMntOn, :RecordStatus");
		sql.append(", :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (consumerProduct.getProductId() == Long.MIN_VALUE) {
			consumerProduct.setProductId(getNextValue("SEQCD_PRODUCTS"));
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(consumerProduct);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(consumerProduct.getProductId());
	}

	@Override
	public void update(ConsumerProduct consumerProduct, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update CD_PRODUCTS");
		sql.append(tableType.getSuffix());
		sql.append(" set ModelId = :modelId, ModelDescription = :modelDescription, ManufacturerId = :manufacturerId");
		sql.append(
				", AssetDescription = :assetDescription, MinAmount = :MinAmount, MaxAmount = :MaxAmount, ModelStatus = :modelStatus");
		sql.append(", Channel = :channel, Active = :active, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus");
		sql.append(", RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where ProductId = :productId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(consumerProduct);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ConsumerProduct ConsumerProduct, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from CD_PRODUCTS");
		sql.append(tableType.getSuffix());
		sql.append(" where ProductId = :productId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(ConsumerProduct);
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
	public boolean isDuplicateKey(ConsumerProduct consumerProduct, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "ModelId = :modelId ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CD_PRODUCTS", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CD_PRODUCTS_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CD_PRODUCTS_Temp", "CD_PRODUCTS" }, whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("modelId", consumerProduct.getModelId());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
}
