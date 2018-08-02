package com.pennanttech.pff.organization.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.organization.model.Organization;

public class OrganizationDAOImpl extends SequenceDao<Organization> implements OrganizationDAO {
	private static Logger logger = Logger.getLogger(OrganizationDAOImpl.class);

	@Override
	public Organization getOrganization(long id, String type) {
		logger.debug(Literal.ENTERING);
		Organization organization = null;

		StringBuilder sql = new StringBuilder();
		sql.append("select * from organizations").append(type).append(" where id=:id");

		RowMapper<Organization> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Organization.class);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		try {
			organization = this.jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return organization;
	}

	public long save(Organization organization, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (organization.getId() == 0) {
			organization.setId(getNextValue("seqorganizations"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("insert into organizations");
		sql.append(tableType.getSuffix());
		sql.append("(id, type, custid, code,");
		sql.append(" name, date_incorporation, createdby, createdon,");
		sql.append(" version, lastmntby, lastmnton, recordstatus,");
		sql.append(" rolecode, nextrolecode, taskid, nexttaskid, recordtype, workflowid)");
		sql.append(" values(");
		sql.append(" :id, :type, :custId, :code,");
		sql.append(" :name, :date_Incorporation, :createdBy, :createdOn,");
		sql.append(" :version, :lastMntBy, :lastMntOn, :recordStatus,");
		sql.append(" :roleCode, :nextRoleCode, :taskId, :nextTaskId, :recordType, :workflowId)");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(organization);

		logger.trace(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return organization.getId();
	}

	public void update(Organization organization, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder query = new StringBuilder();
		query.append(" update organizations");
		query.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		query.append(" set date_incorporation =:date_Incorporation,");
		query.append(
				" version = :version, lastmntby = :lastMntBy, lastmnton = :lastMntOn, recordstatus = :recordStatus, rolecode = :roleCode,");
		query.append(
				" nextrolecode = :nextRoleCode, taskid = :taskId, nexttaskid = :nextTaskId, recordtype = :recordType, workflowid = :WorkflowId");
		query.append(" where id = :Id ");

		logger.trace(Literal.SQL + query.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(organization);
		recordCount = this.jdbcTemplate.update(query.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);

	}

	public void delete(Organization organization, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append(" delete from organizations");
		query.append(tableType.getSuffix());
		query.append(" where id = :id ");

		logger.trace(Literal.SQL + query.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(organization);
		int recordCount = this.jdbcTemplate.update(query.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(Long custId, String code, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "custid = :custid and code = :code";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("organizations", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("organizations_temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "organizations_temp", "organizations" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("custid", custId);
		paramSource.addValue("code", code);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}
