package com.pennanttech.pff.incomeexpensedetail.dao;

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
import com.pennanttech.pff.organization.school.model.IncomeExpenseHeader;

public class IncomeExpenseHeaderDAOImpl extends SequenceDao<IncomeExpenseHeader> implements IncomeExpenseHeaderDAO {
	private static Logger logger = Logger.getLogger(IncomeExpenseHeaderDAOImpl.class);

	@Override
	public IncomeExpenseHeader getIncomeExpenseHeader(long id, String type) {
		logger.debug(Literal.ENTERING);
		IncomeExpenseHeader incomeExpenseHeader = null;

		StringBuilder sql = new StringBuilder();
		sql.append("select * from org_income_expense_header").append(type).append(" where id=:id");

		RowMapper<IncomeExpenseHeader> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IncomeExpenseHeader.class);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		try {
			incomeExpenseHeader = this.jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return incomeExpenseHeader;
	}

	@Override
	public long save(IncomeExpenseHeader incomeExpenseHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (incomeExpenseHeader.getId() == 0) {
			incomeExpenseHeader.setId(getNextValue("SeqOrgIncomeExpenseHeader"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("insert into org_income_expense_header");
		sql.append(tableType.getSuffix());
		sql.append("(id, orgid, financialyear, createdby, createdon,");
		sql.append(" version, lastmntby, lastmnton, recordstatus,");
		sql.append(" rolecode, nextrolecode, taskid, nexttaskid, recordtype, workflowid)");
		sql.append(" values(");
		sql.append(" :id, :orgId, :financialYear,");
		sql.append(" :createdBy, :createdOn,");
		sql.append(" :version, :lastMntBy, :lastMntOn, :recordStatus,");
		sql.append(" :roleCode, :nextRoleCode, :taskId, :nextTaskId, :recordType, :workflowId)");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(incomeExpenseHeader);

		logger.trace(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return incomeExpenseHeader.getId();
	
	}

	@Override
	public void update(IncomeExpenseHeader incomeExpenseHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder query = new StringBuilder();
		query.append(" update org_income_expense_header");
		query.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		query.append(" set createdby =:createdBy, createdon = :createdOn,");
		query.append(" version = :version, lastmntby = :lastMntBy, lastmnton = :lastMntOn, recordstatus = :recordStatus, rolecode = :roleCode,");
		query.append(" nextrolecode = :nextRoleCode, taskid = :taskId, nexttaskid = :nextTaskId, recordtype = :recordType, workflowid = :WorkflowId");
		query.append(" where id = :Id ");

		logger.trace(Literal.SQL + query.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(incomeExpenseHeader);
		recordCount = this.jdbcTemplate.update(query.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(IncomeExpenseHeader incomeExpenseHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append(" delete from org_income_expense_header");
		query.append(tableType.getSuffix());
		query.append(" where id = :id ");

		logger.trace(Literal.SQL + query.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(incomeExpenseHeader);
		int recordCount = this.jdbcTemplate.update(query.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isExist(String custCif, int financialYear, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from org_income_expense_header");
		sql.append(type);
		sql.append(" where custcif = :custCif and financialyear = :financialYear");
		
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("custCif", custCif);
		paramSource.addValue("financialYear", financialYear);

		Integer count = jdbcTemplate.queryForObject(sql.toString(), paramSource, Integer.class);
		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}
