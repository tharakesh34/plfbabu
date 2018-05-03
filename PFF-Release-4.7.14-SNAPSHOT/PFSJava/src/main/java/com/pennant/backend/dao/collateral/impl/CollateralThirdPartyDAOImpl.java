package com.pennant.backend.dao.collateral.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.collateral.CollateralThirdPartyDAO;
import com.pennant.backend.model.collateral.CollateralThirdParty;
import com.pennanttech.pennapps.core.ConcurrencyException;

public class CollateralThirdPartyDAOImpl implements CollateralThirdPartyDAO {
	private static Logger	logger	= Logger.getLogger(CollateralThirdPartyDAOImpl.class);

	public CollateralThirdPartyDAOImpl() {
		super();
	}

	private NamedParameterJdbcTemplate	jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public CollateralThirdParty getCollThirdPartyDetails(String collateralRef, long customerId, String type) {
		logger.debug("Entering");
		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" Select CollateralRef, CustomerId, Version, LastMntBy, LastMntOn, ");
		sql.append(" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, ");
		sql.append(" WorkflowId From CollateralThirdParty");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef AND CustomerId = :CustomerId");
		logger.debug("selectSql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);
		source.addValue("CustomerId", customerId);

		RowMapper<CollateralThirdParty> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CollateralThirdParty.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception :", e);
		} finally {
			source = null;
		}
		logger.debug("Leaving");
		return null;
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
		sql.append("  Values (:CollateralRef, :CustomerId, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
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
	 * @param CollateralThirdParty
	 *            Details (collateralThirdParty)
	 * @param type
	 *            (String) ""/_Temp/_View
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
		sql.append(" RecordStatus = :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
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
	 * This method Deletes the Record  from the CollateralThirdParty or CollateralThirdParty_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete CollateralThirdParty Details by key reference and CustomerId
	 * 
	 * @param CollateralThirdParty
	 *            Details (collateralThirdParty)
	 * @param type
	 *            (String) ""/_Temp/_View
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
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select CollateralRef, CustomerId, Version, LastMntBy, LastMntOn, ");
		sql.append(" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		if (type.contains("View")) {
			sql.append(", custCIF, custShrtName, custCRCPR, custPassportNo, custNationality, custCtgCode ");
		}
		sql.append(" From CollateralThirdParty");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef");
		logger.debug("selectSql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		RowMapper<CollateralThirdParty> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CollateralThirdParty.class);
		
		List<CollateralThirdParty> list = null;
		try {
			list = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception :", e);
			list = new ArrayList<>();
		} finally {
			source = null;
			sql = null;
		}
		return list;
	}

	/**
	 * This method Deletes the Records from the CollateralThirdParty or CollateralThirdParty_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete CollateralThirdParty Details by key reference
	 * 
	 * @param CollateralThirdParty
	 *            Details (collateralThirdParty)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteList(CollateralThirdParty collateralThirdParty, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From CollateralThirdParty");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  CollateralRef = :CollateralRef");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralThirdParty);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
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

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("Select count(*) from (Select T1.CollateralRef from CollateralAssignment_Temp t1");
		selectSql.append(" inner Join Financemain_Temp T3 on T3.Finreference = T1.Reference ");
		selectSql.append(" where T1.CollateralRef='" + collateralRef + "' and T3.custid=' " + custId + "'");
		selectSql.append(" union ");
		selectSql.append(" Select T1.CollateralRef");
		selectSql.append(" from CollateralAssignment t1 ");
		selectSql.append(" inner Join Financemain T3 on T3.Finreference = T1.Reference ");
		selectSql.append(" where T1.CollateralRef='" + collateralRef + "' and T3.custid=' " + custId + "')T");

		logger.debug("selectSql: " + selectSql.toString());

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.info(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		
		return recordCount > 0 ? true : false;
	}
}
