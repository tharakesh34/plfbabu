package com.pennant.backend.dao.returnedCheques.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.returnedCheques.ReturnedChequeDAO;
import com.pennant.backend.model.returnedcheques.ReturnedChequeDetails;
import com.pennant.backend.model.returnedcheques.ReturnedCheques;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ReturnedChequeDetails</code> with set of CRUD operations.
 */
public class ReturnedChequeDAOImpl extends BasisCodeDAO<ReturnedChequeDetails> implements ReturnedChequeDAO {

	private static Logger logger = Logger.getLogger(ReturnedChequeDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ReturnedChequeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record ReturnedCheque details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ReturnedCheque
	 */
	@Override
	public ReturnedChequeDetails getReturnedChequeById(String custCIF, String chequeNo, String type) {
		logger.debug("Entering");

		ReturnedChequeDetails returnCheque = new ReturnedChequeDetails();
		returnCheque.setCustCIF(custCIF);
		returnCheque.setChequeNo(chequeNo);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select CustCIF , ChequeNo , Amount ,ReturnDate,ReturnReason,Currency,");
		if (type.contains("View")) {
			selectSql.append("  CustShrtName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode,  NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From ReturnedCheques");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustCIF =:CustCIF and ChequeNo =:ChequeNo ");
		logger.debug("selectSql:" + selectSql.toString());

		SqlParameterSource beanparameters = new BeanPropertySqlParameterSource(returnCheque);
		RowMapper<ReturnedChequeDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ReturnedChequeDetails.class);

		try {
			returnCheque = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanparameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			returnCheque = null;
		}

		logger.debug("Leaving");
		return returnCheque;
	}

	@Override
	public boolean isDuplicateKey(String chequeNo, String custCIF, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "CustCIF =:CustCIF or ChequeNo =:ChequeNo";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("ReturnedCheques", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("ReturnedCheques_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "ReturnedCheques_Temp", "ReturnedCheques" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("ChequeNo", chequeNo);
		paramSource.addValue("CustCIF", custCIF);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public List<ReturnedCheques> fetchReturnedCheques(ReturnedCheques returnedCheques) {
		logger.debug("Entering");

		ReturnedCheques returnedCheque = new ReturnedCheques();
		returnedCheque.setCustCIF(returnedCheques.getCustCIF());

		StringBuilder selectSql = new StringBuilder(" Select CustCIF , ChequeNo , Amount , ");
		selectSql.append(" ReturnDate , ReturnReason ,Currency ");
		selectSql.append(" From ReturnedCheques");
		selectSql.append(" Where CustCIF =:CustCIF ");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(returnedCheque);
		RowMapper<ReturnedCheques> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ReturnedCheques.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public String save(ReturnedChequeDetails returnedChequeDetails, TableType tableType) {
		logger.debug("Entering");

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into ReturnedCheques");
		sql.append(tableType.getSuffix());
		sql.append("(CustCIF,ChequeNo,Amount,ReturnDate,ReturnReason,Currency,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:CustCIF, :ChequeNo, :Amount, :ReturnDate,:ReturnReason,:Currency,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(returnedChequeDetails);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return returnedChequeDetails.getChequeNo();
	}

	@Override
	public void update(ReturnedChequeDetails returnedChequeDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update ReturnedCheques");
		sql.append(tableType.getSuffix());
		sql.append(" set Amount = :Amount,");
		sql.append(" ReturnDate = :ReturnDate,ReturnReason = :ReturnReason,Currency = :Currency,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where CustCIF =:CustCIF and ChequeNo=:ChequeNo");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(returnedChequeDetails);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), beanParameters);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(ReturnedChequeDetails returnedChequeDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ReturnedCheques");
		sql.append(tableType.getSuffix());
		sql.append(" where CustCIF =:CustCIF and ChequeNo =:ChequeNo");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(returnedChequeDetails);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}
