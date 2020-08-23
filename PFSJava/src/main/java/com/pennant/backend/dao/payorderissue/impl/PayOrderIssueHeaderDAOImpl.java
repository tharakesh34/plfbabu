package com.pennant.backend.dao.payorderissue.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class PayOrderIssueHeaderDAOImpl extends BasicDao<PayOrderIssueHeader> implements PayOrderIssueHeaderDAO {
	private static Logger logger = LogManager.getLogger(PayOrderIssueHeaderDAOImpl.class);

	public PayOrderIssueHeaderDAOImpl() {
		super();
	}

	/**
	 * This method insert new Records into PayOrderIssueHeader or PayOrderIssueHeader_Temp.
	 *
	 * save PayOrderIssueHeader
	 * 
	 * @param paymentOrderIssue
	 *            (paymentOrderIssue)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(PayOrderIssueHeader payOrderIssueHeader, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" PayOrderIssueHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (FinReference,TotalPOAmount,TotalPOCount,IssuedPOAmount,IssuedPOCount,PODueAmount,PODueCount,");
		insertSql.append(
				" Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(
				" values (:FinReference,:TotalPOAmount,:TotalPOCount,:IssuedPOAmount,:IssuedPOCount,:PODueAmount,:PODueCount,");
		insertSql.append(" :Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(payOrderIssueHeader);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	/**
	 * This method updates the Record PayOrderIssueHeader or PayOrderIssueHeader_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update PayOrderIssueHeader by key FinReference and Version
	 * 
	 * @param PayOrderIssueHeader
	 *            (PayOrderIssueHeader)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(PayOrderIssueHeader paymentOrderIssue, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update PayOrderIssueHeader");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set TotalPOAmount = :TotalPOAmount,TotalPOCount = :TotalPOCount,");
		updateSql.append(
				" IssuedPOAmount = :IssuedPOAmount,IssuedPOCount =:IssuedPOCount,PODueAmount = :PODueAmount,PODueCount = :PODueCount,");
		updateSql.append(" Version= :Version , LastMntBy=:LastMntBy,");
		updateSql.append(" LastMntOn= :LastMntOn, RecordStatus=:RecordStatus, RoleCode=:RoleCode,");
		updateSql.append(" NextRoleCode= :NextRoleCode, TaskId= :TaskId,");
		updateSql.append(" NextTaskId= :NextTaskId, RecordType= :RecordType, WorkflowId= :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference ");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(paymentOrderIssue);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");

	}

	@Override
	public PayOrderIssueHeader getPayOrderIssueByHeaderRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, TotalPOAmount, TotalPOCount, IssuedPOAmount, IssuedPOCount, PODueAmount");
		sql.append(", PODueCount, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinType, CustCIF, CustID, CustShrtName");
			sql.append(", FinTypeDesc, FinCcy, AlwMultiPartyDisb, FinIsActive"); //FinIsActive not availble in AView
		}

		sql.append(" from PayOrderIssueHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<PayOrderIssueHeader>() {
						@Override
						public PayOrderIssueHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
							PayOrderIssueHeader poi = new PayOrderIssueHeader();

							poi.setFinReference(rs.getString("FinReference"));
							poi.setTotalPOAmount(rs.getBigDecimal("TotalPOAmount"));
							poi.setTotalPOCount(rs.getInt("TotalPOCount"));
							poi.setIssuedPOAmount(rs.getBigDecimal("IssuedPOAmount"));
							poi.setIssuedPOCount(rs.getInt("IssuedPOCount"));
							poi.setpODueAmount(rs.getBigDecimal("PODueAmount"));
							poi.setpODueCount(rs.getInt("PODueCount"));
							poi.setVersion(rs.getInt("Version"));
							poi.setLastMntBy(rs.getLong("LastMntBy"));
							poi.setLastMntOn(rs.getTimestamp("LastMntOn"));
							poi.setRecordStatus(rs.getString("RecordStatus"));
							poi.setRoleCode(rs.getString("RoleCode"));
							poi.setNextRoleCode(rs.getString("NextRoleCode"));
							poi.setTaskId(rs.getString("TaskId"));
							poi.setNextTaskId(rs.getString("NextTaskId"));
							poi.setRecordType(rs.getString("RecordType"));
							poi.setWorkflowId(rs.getLong("WorkflowId"));

							if (StringUtils.trimToEmpty(type).contains("View")) {
								poi.setFinType(rs.getString("FinType"));
								poi.setCustCIF(rs.getString("CustCIF"));
								//	poi.setCustID(rs.getString("CustID"));		(not availble in bean)
								poi.setCustShrtName(rs.getString("CustShrtName"));
								poi.setFinTypeDesc(rs.getString("FinTypeDesc"));
								poi.setFinCcy(rs.getString("FinCcy"));
								poi.setAlwMultiPartyDisb(rs.getBoolean("AlwMultiPartyDisb"));
								poi.setFinIsActive(rs.getBoolean("FinIsActive"));
							}

							return poi;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * This method Deletes the Record from the PayOrderIssueHeader or PayOrderIssueHeader_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete PayOrderIssueHeader by key FinReference
	 * 
	 * @param payment
	 *            OrderIssue (paymentOrderIssue)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(PayOrderIssueHeader paymentOrderIssue, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" PayOrderIssueHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(paymentOrderIssue);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}
