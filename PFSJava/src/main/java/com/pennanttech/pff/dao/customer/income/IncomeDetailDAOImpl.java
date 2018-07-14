package com.pennanttech.pff.dao.customer.income;

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
		query.append(" set income = :Income, margin = :Margin ,");
		query.append(" version=:version, lastmntby=:lastMntBy, lastmnton=:lastMntOn, recordStatus=:recordStatus, ");
		query.append(" rolecode=:roleCode, nextrolecode=:nextRoleCode, taskid=:taskId, nexttaskid=:nextTaskId,");
		query.append(" recordtype=:recordType, workflowid=:workflowId");
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

		/*if (recordCount <= 0) {
			throw new ConcurrencyException();
		}*/

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deletebyLinkId(long linkId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append(" delete from income_details");
		query.append(StringUtils.trimToEmpty(type));
		query.append(" where linkId = :linkId ");
		logger.trace(Literal.SQL + query.toString());

		int recordCount = 0;
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("linkId", linkId);

		try {
			recordCount = this.jdbcTemplate.update(query.toString(), parameterSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
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
	public List<CustomerIncome> getTotalIncomeByLinkId(long linkId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select income, margin, incomeexpense  from income_details where linkid = :linkid");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("linkid", linkId);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);
		
		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, typeRowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}
	
	@Override
	public List<CustomerIncome> getTotalIncomeBySamplingId(long samplingId) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder sql = new StringBuilder();
		sql.append("select income, margin, incomeexpense from income_details");
		sql.append(" where linkid in  (select linkid from link_sampling_incomes where samplingid = :id)");
		logger.debug(Literal.SQL + sql.toString());
		
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("id", samplingId);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);
		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, typeRowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<CustomerIncome> getTotalIncomeByFinReference(String keyReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select income, margin, incomeexpense from income_details id");
		sql.append(" inner join link_cust_incomes ci on ci.linkid = id.linkid");
		sql.append(" where custid in (select custid from (");
		sql.append(" select custid, finreference from financemain_view");
		sql.append(" union all");
		sql.append(" select custid, finreference from finjointaccountdetails_view");
		sql.append(") t where t.finreference = :keyReference)");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("keyReference", keyReference);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);
		
		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, typeRowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
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
