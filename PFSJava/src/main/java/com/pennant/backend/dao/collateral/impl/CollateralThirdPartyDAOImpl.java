package com.pennant.backend.dao.collateral.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.collateral.CollateralThirdPartyDAO;
import com.pennant.backend.model.collateral.CollateralThirdParty;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class CollateralThirdPartyDAOImpl extends BasicDao<CollateralThirdParty> implements CollateralThirdPartyDAO {
	private static Logger logger = LogManager.getLogger(CollateralThirdPartyDAOImpl.class);

	public CollateralThirdPartyDAOImpl() {
		super();
	}

	@Override
	public CollateralThirdParty getCollThirdPartyDetails(String collateralRef, long customerId, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" Select CollateralRef, CustomerId, Version, LastMntBy, LastMntOn, ");
		sql.append(" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, ");
		sql.append(" WorkflowId From CollateralThirdParty");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef AND CustomerId = :CustomerId");
		logger.debug("selectSql: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);
		source.addValue("CustomerId", customerId);

		RowMapper<CollateralThirdParty> typeRowMapper = BeanPropertyRowMapper.newInstance(CollateralThirdParty.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void save(CollateralThirdParty collateralThirdParty, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into CollateralThirdParty");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (CollateralRef, CustomerId,");
		sql.append("  Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		sql.append("  NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId) ");
		sql.append(
				"  Values (:CollateralRef, :CustomerId, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId) ");
		logger.debug("insertSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralThirdParty);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record CollateralThirdParty or CollateralThirdParty_Temp. if Record not updated then
	 * throws DataAccessException with error 41004. update CollateralThirdParty Details by key reference and CustomerId
	 * 
	 * @param CollateralThirdParty Details (collateralThirdParty)
	 * @param type                 (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CollateralThirdParty collateralThirdParty, String tableType) {
		int recordCount = 0;

		StringBuilder sql = new StringBuilder();

		sql.append("Update CollateralThirdParty");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Set Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(
				" RecordStatus = :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where CollateralRef = :CollateralRef AND CustomerId = :CustomerId");
		logger.debug("updateSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralThirdParty);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the CollateralThirdParty or CollateralThirdParty_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete CollateralThirdParty Details by key reference and
	 * CustomerId
	 * 
	 * @param CollateralThirdParty Details (collateralThirdParty)
	 * @param type                 (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CollateralThirdParty collateralThirdParty, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From CollateralThirdParty");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  CollateralRef = :CollateralRef AND CustomerId = :CustomerId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralThirdParty);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<CollateralThirdParty> getCollThirdPartyDetails(String collateralRef, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CollateralRef, CustomerId, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (type.contains("View")) {
			sql.append(", CustCIF, CustShrtName, CustCRCPR, CustPassportNo, CustNationality, CustCtgCode");
		}
		sql.append(" from CollateralThirdParty");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, collateralRef);
		}, (rs, rowNum) -> {
			CollateralThirdParty ctp = new CollateralThirdParty();

			ctp.setCollateralRef(rs.getString("CollateralRef"));
			ctp.setCustomerId(rs.getLong("CustomerId"));
			ctp.setVersion(rs.getInt("Version"));
			ctp.setLastMntBy(rs.getLong("LastMntBy"));
			ctp.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ctp.setRecordStatus(rs.getString("RecordStatus"));
			ctp.setRoleCode(rs.getString("RoleCode"));
			ctp.setNextRoleCode(rs.getString("NextRoleCode"));
			ctp.setTaskId(rs.getString("TaskId"));
			ctp.setNextTaskId(rs.getString("NextTaskId"));
			ctp.setRecordType(rs.getString("RecordType"));
			ctp.setWorkflowId(rs.getLong("WorkflowId"));

			if (type.contains("View")) {
				ctp.setCustCIF(rs.getString("CustCIF"));
				ctp.setCustShrtName(rs.getString("CustShrtName"));
				ctp.setCustCRCPR(rs.getString("CustCRCPR"));
				ctp.setCustPassportNo(rs.getString("CustPassportNo"));
				ctp.setCustNationality(rs.getString("CustNationality"));
				ctp.setCustCtgCode(rs.getString("CustCtgCode"));
			}

			return ctp;
		});

	}

	/**
	 * This method Deletes the Records from the CollateralThirdParty or CollateralThirdParty_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete CollateralThirdParty Details by key reference
	 * 
	 * @param CollateralThirdParty Details (collateralThirdParty)
	 * @param type                 (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteList(String collateralRef, String tableType) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From CollateralThirdParty");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where  CollateralRef = :CollateralRef");
		logger.debug("deleteSql: " + deleteSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		this.jdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}

	/**
	 * Get version of co-Owner details
	 * 
	 * @param collateralRef
	 * @param tableType
	 * @return Integer
	 */
	@Override
	public boolean isThirdPartyUsed(String collateralRef, long custId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select count(*) from (Select T1.CollateralRef from CollateralAssignment_Temp t1");
		selectSql.append(" inner Join Financemain_Temp T3 on T3.Finreference = T1.Reference ");
		selectSql.append(" where T1.CollateralRef='" + collateralRef + "' and T3.custid=' " + custId + "'");
		selectSql.append(" union ");
		selectSql.append(" Select T1.CollateralRef");
		selectSql.append(" from CollateralAssignment t1 ");
		selectSql.append(" inner Join Financemain T3 on T3.Finreference = T1.Reference ");
		selectSql.append(" where T1.CollateralRef='" + collateralRef + "' and T3.custid=' " + custId + "')T");

		logger.debug(Literal.SQL + selectSql.toString());
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class) > 0;
	}
}
