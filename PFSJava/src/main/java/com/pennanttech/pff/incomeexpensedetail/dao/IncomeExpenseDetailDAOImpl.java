package com.pennanttech.pff.incomeexpensedetail.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.organization.model.IncomeExpenseDetail;

public class IncomeExpenseDetailDAOImpl extends SequenceDao<IncomeExpenseDetail> implements IncomeExpenseDetailDAO {
	private static final Logger logger = LogManager.getLogger(IncomeExpenseDetailDAOImpl.class);

	@Override
	public long save(IncomeExpenseDetail incomeExpenseDetail, String type) {

		if (incomeExpenseDetail.getId() == 0) {
			incomeExpenseDetail.setId(getNextValue("SeqOrgIncomeExpenses"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("insert into org_income_expenses");
		sql.append(type);
		sql.append(
				" (id, headerid, incomeexpense, incomeexpensecode, incomeexpensetype, category, units, unitprice, frequency, loockupid, total, consider, createdby, createdon,");
		sql.append(" version, lastmntby, lastmnton, recordstatus,");
		sql.append(" rolecode, nextrolecode, taskid, nexttaskid, recordtype, workflowid)");
		sql.append(
				" values(:id, :headerId, :incomeExpense, :incomeExpenseCode, :incomeExpenseType, :category, :units, :unitPrice,");
		sql.append(" :frequency, :loockUpId, :total, :consider, :createdBy, :createdOn,");
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
		query.append(
				" set incomeexpensecode = :incomeExpenseCode, category = :category, units = :units, unitprice = :unitPrice,");
		query.append(" frequency = :frequency, total = :total, consider = :consider,");
		query.append(
				" version = :version, lastmntby = :lastMntBy, lastmnton = :lastMntOn, recordstatus = :recordStatus, rolecode = :roleCode,");
		query.append(
				" nextrolecode = :nextRoleCode, taskid = :taskId, nexttaskid = :nextTaskId, recordtype = :recordType, workflowid = :WorkflowId");
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
		query.append("delete from org_income_expenses");
		query.append(StringUtils.trimToEmpty(type));
		query.append(" where Id = :id ");
		logger.trace(Literal.SQL + query.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("id", id);

		try {
			this.jdbcTemplate.update(query.toString(), parameterSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<IncomeExpenseDetail> getIncomeExpenseList(Long id, String incomeType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select * from org_income_expenses");
		sql.append(type);
		sql.append(" where headerid = :id and ");
		sql.append(" incomeexpensetype = :type ");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("type", incomeType);

		RowMapper<IncomeExpenseDetail> rowMapper = BeanPropertyRowMapper.newInstance(IncomeExpenseDetail.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

	@Override
	public List<Map<String, Object>> getTotal(Long custId, Integer financialYear) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select incomeexpensetype, sum(total) from (");
		sql.append(" select total,incomeexpensetype,custid,financialyear from organizations  org");
		sql.append(" inner join org_income_expense_header h on h.orgid =  org.id");
		sql.append(" INNER join org_income_expenses ie on ie.headerid = h.id ");
		sql.append(" where custid = :custId and financialyear = :financialYear  and consider=1)t");
		sql.append(" group by incomeexpensetype");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custId", custId);
		source.addValue("financialYear", financialYear);

		return jdbcTemplate.queryForList(sql.toString(), source);
	}
}
