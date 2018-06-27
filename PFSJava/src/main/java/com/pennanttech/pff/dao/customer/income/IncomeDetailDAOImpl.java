package com.pennanttech.pff.dao.customer.income;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;

public class IncomeDetailDAOImpl extends SequenceDao<Sampling> implements IncomeDetailDAO {
	private static Logger logger = LogManager.getLogger(IncomeDetailDAOImpl.class);
	public static final String SEQUENCE = "SeqIncomeDetails";
	public static final String SEQUENCE_LINK = "SeqIncomeLink";

	@Override
	public long save(CustomerIncome customerIncome, String type) {

		if (customerIncome.getId() == 0) {
			customerIncome.setId(getNextValue(SEQUENCE));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("insert into income_details");
		sql.append(type);
		sql.append(" (id, linkid, incometype, incomeexpense, category, income, margin,");
		sql.append(" version, lastmntby, lastmnton, recordstatus,");
		sql.append(" rolecode, nextrolecode, taskid, nexttaskid, recordtype, workflowid)");
		sql.append(" values(:id, :linkId, :incomeType, :incomeExpense, :category, :income, :margin,");
		sql.append(" :version, :lastMntBy, :lastMntOn, :recordStatus,");
		sql.append(" :roleCode, :nextRoleCode, :taskId, :nextTaskId, :recordType, :workflowId)");
		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource(customerIncome));

		logger.debug(Literal.LEAVING);
		return customerIncome.getId();
	}

	@Override
	public void update(CustomerIncome customerIncome, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder query = new StringBuilder();
		query.append(" update income_details");
		query.append(StringUtils.trimToEmpty(type));
		query.append(" set income = :Income, margin = :Margin");
		query.append(" where id = :id ");
		logger.trace(Literal.SQL + query.toString());

		recordCount = this.jdbcTemplate.update(query.toString(), new BeanPropertySqlParameterSource(customerIncome));

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append(" delete from income_details");
		query.append(StringUtils.trimToEmpty(type));
		query.append(" where linkid = :id ");
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

	/**
	 * Fetch the Record Customer Incomes details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerIncome
	 */
	public CustomerIncome getCustomerIncomeById(CustomerIncome customerIncome, String type, String source) {
		logger.debug(Literal.ENTERING);

		String tableName = null;
		if (source.equals("sampling")) {
			tableName = "sampling_income_details";
		} else {
			tableName = "customer_income_details";
		}

		type = StringUtils.trimToEmpty(type).toLowerCase();

		StringBuilder query = new StringBuilder();
		if (type.contains("view")) {
			query.append(" select custid, income, incometype, incomeExpense, category, margin,");
			query.append(" incometypedesc, categorydesc, ");
			query.append(" custcif, custshrtname, toccy, ");
			query.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
			query.append(" TaskId, NextTaskId, RecordType, WorkflowId");
			query.append(" from ");
			query.append(tableName);
			query.append(type);
			query.append(" Where custid = :custId and incometype = :incomeType ");
			query.append(" and incomeExpense = :incomeExpense and category=:category");
		} else {
			query.append(
					" select cu.custid, incd.income, incd.incometype, incd.incomeExpense, incd.category, incd.margin,");
			query.append(" it.incometypedesc, ic.categorydesc, ");
			query.append(" cu.custcif, cu.custshrtname, cu.custbaseccy toccy, ");
			query.append(
					" cin.Version, cin.LastMntOn, cin.LastMntBy, cin.RecordStatus, cin.RoleCode, cin.NextRoleCode,");
			query.append(" cin.TaskId, cin.NextTaskId, cin.RecordType, cin.WorkflowId");
			query.append(" from ");
			query.append(tableName);
			query.append(type).append(" incd");
			query.append(" inner join ").append(tableName).append(type).append(" cin");
			query.append(" on cin.linkid = incd.linkid");
			query.append(" inner join bmtincometypes it on it.incometypecode=incd.incometype");
			query.append(" and it.incomeexpense=incd.incomeexpense and it.category=incd.category");
			query.append(" inner join customers cu on cin.custid = cu.custid");
			query.append(" inner join bmtincomecategory ic on ic.incomecategory = incd.category");
			query.append(" where cu.custid = :custId and incd.incometype = :incomeType ");
			query.append(" and incd.incomeExpense = :incomeExpense and incd.category=:category");
		}
		logger.trace(Literal.SQL + query.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);

		try {
			customerIncome = this.jdbcTemplate.queryForObject(query.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			customerIncome = null;
		}
		logger.debug(Literal.LEAVING);
		return customerIncome;
	}

	@Override
	public List<CustomerIncome> getIncomesByCustomer(final long custId, String type) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.equalsIgnoreCase(type, "_temp")) {
			type = "_view";
		} else if (StringUtils.equalsIgnoreCase(type, "")) {
			type = "_aview";
		}

		StringBuilder query = new StringBuilder();
		query.append(" select *  from customer_income_details");
		query.append(StringUtils.trimToEmpty(type));
		query.append(" where custid =:custid ");
		logger.trace(Literal.SQL + query.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("custid", custId);

		RowMapper<CustomerIncome> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);
		try {
			return this.jdbcTemplate.query(query.toString(), parameterSource, rowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	public List<CustomerIncome> getCustomerIncomesByCustId(long custId, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from income_details").append(type);
		sql.append(" where custid = :custid");

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("custid", custId);

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);

		logger.debug(Literal.LEAVING);
		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
		}

		return new ArrayList<>();
	}

	@Override
	public BigDecimal getTotalIncomeByLinkId(long linkId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(sum(income), 0) from income_details where linkid = :linkid");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("linkid", linkId);
		BigDecimal totalIncome = BigDecimal.ZERO;
		try {
			totalIncome = this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, BigDecimal.class);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
			totalIncome = BigDecimal.ZERO;
		}

		logger.debug(Literal.LEAVING);
		return totalIncome;
	}

	@Override
	public BigDecimal getTotalIncomeByFinReference(String keyReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select coalesce(sum(income), 0) from income_details id");
		sql.append(" inner join link_cust_incomes ci on ci.linkid = id.linkid");
		sql.append(" where custid in (select custid from (");
		sql.append(" select custid, finreference from financemain_view");
		sql.append(" union all");
		sql.append(" select jointaccountid custid,finreference from finjointaccountdetails_view");
		sql.append(") t where t.finreference = :keyReference)");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("keyReference", keyReference);
		BigDecimal totalIncome = BigDecimal.ZERO;
		try {
			totalIncome = this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, BigDecimal.class);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
			totalIncome = BigDecimal.ZERO;
		}

		logger.debug(Literal.LEAVING);
		return totalIncome;
	}

	@Override
	public List<CustomerIncome> getIncomes(long linkId) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append("select * from income_details_view where linkid = :linkid");
		logger.trace(Literal.SQL + query.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("linkid", linkId);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);

		logger.debug(Literal.LEAVING);

		try {
			return this.jdbcTemplate.query(query.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}
}
