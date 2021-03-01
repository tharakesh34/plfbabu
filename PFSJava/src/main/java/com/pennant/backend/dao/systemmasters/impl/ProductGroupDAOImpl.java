package com.pennant.backend.dao.systemmasters.impl;

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

import com.pennant.backend.dao.systemmasters.ProductGroupDAO;
import com.pennant.backend.model.systemmasters.ProductGroup;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class ProductGroupDAOImpl extends SequenceDao<ProductGroup> implements ProductGroupDAO {

	private static Logger logger = LogManager.getLogger(ProductGroupDAOImpl.class);

	public ProductGroupDAOImpl() {
		super();
	}

	@Override
	public String save(ProductGroup productGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into ProductGroup");
		sql.append(tableType.getSuffix());
		sql.append(" (productGroupId, modelId, productCategoryId, active, channel ");
		sql.append(" , Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(" , RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :productGroupId, :modelId, :productCategoryId, :active, :channel,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId");
		sql.append(" , :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (productGroup.getId() <= 0) {
			productGroup.setId(getNextValue("SEQPRODUCTGROUP"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(productGroup);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(productGroup.getId());
	}

	@Override
	public void update(ProductGroup productGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update ProductGroup");
		sql.append(tableType.getSuffix());
		sql.append(" set modelId = :modelId, channel = :channel, active = :active");
		sql.append(" , productCategoryId=:productCategoryId, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus");
		sql.append(" , RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId");
		sql.append(" , NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where productGroupId = :productGroupId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(productGroup);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(ProductGroup productGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ProductGroup");
		sql.append(tableType.getSuffix());
		sql.append(" where productGroupId = :productGroupId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(productGroup);
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
	public ProductGroup getProductGroup(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" productGroupId, modelId, channel, productcategoryid, active");
		sql.append(" , Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(" , RecordType, WorkflowId");
		sql.append(" From ProductGroup");
		sql.append(type);
		if (type.contains("View")) {
			sql.append(" Where productGroupId = :productGroupId ");
		} else {
			sql.append(" Where productGroupId = :productGroupId");
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ProductGroup productGroup = new ProductGroup();
		productGroup.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(productGroup);
		RowMapper<ProductGroup> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProductGroup.class);

		try {
			productGroup = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			productGroup = null;
		}

		logger.debug(Literal.LEAVING);
		return productGroup;
	}

	@Override
	public boolean isDuplicateKey(long id, String modelId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = " ProductGroupId = :ProductGroupId and PRODUCTCATEGORYID != PRODUCTCATEGORYID ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("ProductGroup", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("ProductGroup_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "ProductGroup_Temp", "ProductGroup" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("ProductGroupId", id);
		paramSource.addValue("modelId", modelId);
		// paramSource.addValue("groupId", groupId);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public boolean isIdExists(long id) {
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from ProductGroup ");
		sql.append(" Where productGroupId = :productGroupId ");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("productGroupId", id);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return false;
	}

}
