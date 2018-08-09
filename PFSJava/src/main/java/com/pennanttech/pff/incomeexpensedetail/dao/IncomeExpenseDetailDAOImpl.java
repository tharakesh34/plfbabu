package com.pennanttech.pff.incomeexpensedetail.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.organization.school.model.IncomeExpenseDetail;

public class IncomeExpenseDetailDAOImpl extends SequenceDao<IncomeExpenseDetail>  implements IncomeExpenseDetailDAO {
	private static final Logger logger = Logger.getLogger(IncomeExpenseDetailDAOImpl.class);

	@Override
	public long save(IncomeExpenseDetail incomeExpenseDetail, String type) {

		if (incomeExpenseDetail.getId() == 0) {
			incomeExpenseDetail.setId(getNextValue("SeqOrgIncomeExpenses"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("insert into org_income_expenses");
		sql.append(type);
		sql.append(" (id, headerid, incomeexpense, type, category, units, unitprice, frequency, consider, createdby, createdon,");
		sql.append(" version, lastmntby, lastmnton, recordstatus,");
		sql.append(" rolecode, nextrolecode, taskid, nexttaskid, recordtype, workflowid)");
		sql.append(" values(:id, :headerId, :incomeExpense, :type, :category, :units, :unitPrice,");
		sql.append(" :frequency, :consider, :createdBy, :createdOn,");
		sql.append(" :version, :lastMntBy, :lastMntOn, :recordStatus,");
		sql.append(" :roleCode, :nextRoleCode, :taskId, :nextTaskId, :recordType, :workflowId)");
		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource(incomeExpenseDetail));

		logger.debug(Literal.LEAVING);
		return incomeExpenseDetail.getId();
	}

	@Override
	public void update(IncomeExpenseDetail incomeExpenseDetail, String tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder query = new StringBuilder();
		query.append(" update org_income_expenses");
		query.append(StringUtils.trimToEmpty(tableType));
		query.append(" set units = :units, unitprice = :unitPrice,");
		query.append(" frequency =:frequency,");
		query.append(" version = :version, lastmntby = :lastMntBy, lastmnton = :lastMntOn, recordstatus = :recordStatus, rolecode = :roleCode,");
		query.append(" nextrolecode = :nextRoleCode, taskid = :taskId, nexttaskid = :nextTaskId, recordtype = :recordType, workflowid = :WorkflowId");
		query.append(" where id = :id ");

		logger.trace(Literal.SQL + query.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(incomeExpenseDetail);
		recordCount = this.jdbcTemplate.update(query.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(Long id, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder query = new StringBuilder();
		query.append(" delete from org_income_expenses");
		query.append(StringUtils.trimToEmpty(type));
		query.append(" where Id = :id ");
		logger.trace(Literal.SQL + query.toString());

		int recordCount = 0;
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("id", id);

		try {
			recordCount = this.jdbcTemplate.update(query.toString(), parameterSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<IncomeExpenseDetail> getCoreIncomeList(Long id, String incomeType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select * from org_income_expenses_view");
		sql.append(" where headerid = :id and ");
		sql.append(" type = :type ");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("type", incomeType);

		RowMapper<IncomeExpenseDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IncomeExpenseDetail.class);

		try {
			return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	@Override
	public List<IncomeExpenseDetail> getNonCoreIncomeList(Long id, String incomeType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select * from org_income_expenses_view");
		sql.append(" where orgid = :orgid and ");
		sql.append(" type = :type ");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("orgid", id);
		paramSource.addValue("type", incomeType);

		RowMapper<IncomeExpenseDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IncomeExpenseDetail.class);

		try {
			return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	@Override
	public List<IncomeExpenseDetail> getExpenseList(Long id, String incomeType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select * from org_income_expenses_view");
		sql.append(" where orgid = :orgid and ");
		sql.append(" type = :type ");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("orgid", id);
		paramSource.addValue("type", incomeType);

		RowMapper<IncomeExpenseDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IncomeExpenseDetail.class);

		try {
			return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

}
