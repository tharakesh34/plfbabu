/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ProvinceDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified Date
 * : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.systemmasters.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.systemmasters.Province;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>Province model</b> class.<br>
 * 
 */
public class ProvinceDAOImpl extends BasicDao<Province> implements ProvinceDAO {
	private static Logger logger = LogManager.getLogger(ProvinceDAOImpl.class);

	public ProvinceDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Province details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Province
	 */
	public Province getProvinceById(final String cPCountry, String cPProvince, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where CPCountry = ? and CPProvince = ?");

		logger.trace(Literal.SQL + sql.toString());

		ProvinceRowMapper rowMapper = new ProvinceRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, cPCountry, cPProvince);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CPCountry, CPProvince, CPProvinceName, SystemDefault, BankRefNo, CPIsActive");
		sql.append(", TaxExempted, UnionTerritory, TaxStateCode, TaxAvailable, BusinessArea, Version");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCPCountryName");
		}

		sql.append(" from RMTCountryVsProvince");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	/**
	 * This method Deletes the Record from the RMTCountryVsProvince or RMTCountryVsProvince_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete Province by key CPCountry
	 * 
	 * @param Province (province)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(Province province, TableType tableType) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTCountryVsProvince");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where CPCountry =:CPCountry and CPProvince = :CPProvince");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(province);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
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
	 * This method insert new Records into RMTCountryVsProvince or RMTCountryVsProvince_Temp.
	 * 
	 * save Province
	 * 
	 * @param Province (province)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Province province, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder("Insert Into RMTCountryVsProvince");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (CPCountry, CPProvince, CPProvinceName,SystemDefault,BankRefNo,CPIsActive,");
		insertSql.append(" TaxExempted, UnionTerritory, TaxStateCode, TaxAvailable, BusinessArea,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CPCountry, :CPProvince, :CPProvinceName,:SystemDefault,:BankRefNo, :CPIsActive,");
		insertSql.append(" :TaxExempted, :UnionTerritory, :TaxStateCode, :TaxAvailable, :BusinessArea,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(province);
		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * This method updates the Record RMTCountryVsProvince or RMTCountryVsProvince_Temp. if Record not updated then
	 * throws DataAccessException with error 41004. update Province by key CPCountry and Version
	 * 
	 * @param Province (province)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(Province province, TableType tableType) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update RMTCountryVsProvince");
		updateSql.append(tableType.getSuffix());
		updateSql.append(
				" Set CPProvinceName = :CPProvinceName, SystemDefault=:SystemDefault,BankRefNo=:BankRefNo,CPIsActive=:CPIsActive,");
		updateSql.append(
				" TaxExempted = :TaxExempted, UnionTerritory = :UnionTerritory, TaxStateCode = :TaxStateCode, TaxAvailable = :TaxAvailable, BusinessArea = :BusinessArea,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CPCountry =:CPCountry  and  CPProvince = :CPProvince");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(province);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Fetch the count of system default values by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Gender
	 */
	@Override
	public String getSystemDefaultCount(String cpprovince) {
		logger.debug(Literal.ENTERING);
		Province province = new Province();
		province.setCPProvince(cpprovince);
		province.setSystemDefault(true);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT CPProvince FROM  RMTCountryVsProvince_View ");
		selectSql.append(" Where CPProvince != :CPProvince and SystemDefault = :SystemDefault");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(province);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return "";
		}
	}

	@Override
	public boolean isDuplicateKey(String cPCountry, String cPProvince, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "CPCountry = :cPCountry AND CPProvince =:cPProvince";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTCountryVsProvince", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTCountryVsProvince_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTCountryVsProvince_Temp", "RMTCountryVsProvince" },
					whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("cPCountry", cPCountry);
		paramSource.addValue("cPProvince", cPProvince);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public boolean count(String taxStateCode, String cPProvince, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "taxStateCode = :taxStateCode and cPProvince <> :cPProvince";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTCountryVsProvince", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTCountryVsProvince_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTCountryVsProvince_Temp", "RMTCountryVsProvince" },
					whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("taxStateCode", taxStateCode);
		paramSource.addValue("cPProvince", cPProvince);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public int getBusinessAreaCount(String businessAreaValue, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select Count(*) From RMTCountryVsProvince");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BusinessArea = :BusinessArea");
		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BusinessArea", businessAreaValue);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public int geStateCodeCount(String taxStateCode, String cpProvince, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select Count(TaxStateCode) From RMTCountryVsProvince");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where TaxStateCode = :TaxStateCode And CPPROVINCE <> :CPPROVINCE");
		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("TaxStateCode", taxStateCode);
		source.addValue("CPPROVINCE", cpProvince);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public Province getProvinceById(String cPProvince, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where CPProvince = ?");

		logger.trace(Literal.SQL + sql.toString());

		ProvinceRowMapper rowMapper = new ProvinceRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, cPProvince);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private class ProvinceRowMapper implements RowMapper<Province> {
		private String type;

		private ProvinceRowMapper(String type) {
			this.type = type;
		}

		@Override
		public Province mapRow(ResultSet rs, int rowNum) throws SQLException {
			Province cvp = new Province();

			cvp.setCPCountry(rs.getString("CPCountry"));
			cvp.setCPProvince(rs.getString("CPProvince"));
			cvp.setCPProvinceName(rs.getString("CPProvinceName"));
			cvp.setSystemDefault(rs.getBoolean("SystemDefault"));
			cvp.setBankRefNo(rs.getString("BankRefNo"));
			cvp.setcPIsActive(rs.getBoolean("CPIsActive"));
			cvp.setTaxExempted(rs.getBoolean("TaxExempted"));
			cvp.setUnionTerritory(rs.getBoolean("UnionTerritory"));
			cvp.setTaxStateCode(rs.getString("TaxStateCode"));
			cvp.setTaxAvailable(rs.getBoolean("TaxAvailable"));
			cvp.setBusinessArea(rs.getString("BusinessArea"));
			cvp.setVersion(rs.getInt("Version"));
			cvp.setLastMntBy(rs.getLong("LastMntBy"));
			cvp.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cvp.setRecordStatus(rs.getString("RecordStatus"));
			cvp.setRoleCode(rs.getString("RoleCode"));
			cvp.setNextRoleCode(rs.getString("NextRoleCode"));
			cvp.setTaskId(rs.getString("TaskId"));
			cvp.setNextTaskId(rs.getString("NextTaskId"));
			cvp.setRecordType(rs.getString("RecordType"));
			cvp.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				cvp.setLovDescCPCountryName(rs.getString("LovDescCPCountryName"));
			}

			return cvp;
		}

	}
}