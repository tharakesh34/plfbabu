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
 * * FileName : CollateralStructureDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-11-2016 * *
 * Modified Date : 29-11-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-11-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.collateral.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.collateral.CollateralStructureDAO;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>CollateralStructure model</b> class.<br>
 * 
 */

public class CollateralStructureDAOImpl extends BasicDao<CollateralStructure> implements CollateralStructureDAO {
	private static Logger logger = LogManager.getLogger(CollateralStructureDAOImpl.class);

	public CollateralStructureDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record CollateralStructure details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CollateralStructure
	 */
	@Override
	public CollateralStructure getCollateralStructureByType(String colType, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CollateralType, CollateralDesc, LtvType, LtvPercentage, MarketableSecurities");
		sql.append(", PreValidationReq, PostValidationReq, CollateralLocReq, CollateralValuatorReq");
		sql.append(", Remarks, AllowLtvWaiver, MaxLtvWaiver, PostValidation, PreValidation, Active");
		sql.append(", Fields, ActualBlock, SQLRule");

		if (type.contains("View")) {
			sql.append(", QueryCode, QuerySubCode");
		}

		sql.append(", Version, LastMntOn, LastMntBy");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", ValuationFrequency, NextValuationDate, ValuationPending, QueryId, ThresholdLtvPercentage");
		sql.append(", CommodityId");
		sql.append(" From CollateralStructure");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralType = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CollateralStructure ct = new CollateralStructure();

				ct.setCollateralType(rs.getString("CollateralType"));
				ct.setCollateralDesc(rs.getString("CollateralDesc"));
				ct.setLtvType(rs.getString("LtvType"));
				ct.setLtvPercentage(rs.getBigDecimal("LtvPercentage"));
				ct.setMarketableSecurities(rs.getBoolean("MarketableSecurities"));
				ct.setPreValidationReq(rs.getBoolean("PreValidationReq"));
				ct.setPostValidationReq(rs.getBoolean("PostValidationReq"));
				ct.setCollateralLocReq(rs.getBoolean("CollateralLocReq"));
				ct.setCollateralValuatorReq(rs.getBoolean("CollateralValuatorReq"));
				ct.setRemarks(rs.getString("Remarks"));
				ct.setAllowLtvWaiver(rs.getBoolean("AllowLtvWaiver"));
				ct.setMaxLtvWaiver(rs.getBigDecimal("MaxLtvWaiver"));
				ct.setPostValidation(rs.getString("PostValidation"));
				ct.setPreValidation(rs.getString("PreValidation"));
				ct.setActive(rs.getBoolean("Active"));
				ct.setFields(rs.getString("Fields"));
				ct.setActualBlock(rs.getString("ActualBlock"));
				ct.setSQLRule(rs.getString("SQLRule"));

				if (type.contains("View")) {
					ct.setQueryCode(rs.getString("QueryCode"));
					ct.setQuerySubCode(rs.getString("QuerySubCode"));
				}

				ct.setVersion(rs.getInt("Version"));
				ct.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ct.setLastMntBy(rs.getLong("LastMntBy"));
				ct.setRecordStatus(rs.getString("RecordStatus"));
				ct.setRoleCode(rs.getString("RoleCode"));
				ct.setNextRoleCode(rs.getString("NextRoleCode"));
				ct.setTaskId(rs.getString("TaskId"));
				ct.setNextTaskId(rs.getString("NextTaskId"));
				ct.setRecordType(rs.getString("RecordType"));
				ct.setWorkflowId(rs.getLong("WorkflowId"));
				ct.setValuationFrequency(rs.getString("ValuationFrequency"));
				ct.setNextValuationDate(rs.getTimestamp("NextValuationDate"));
				ct.setValuationPending(rs.getBoolean("ValuationPending"));
				ct.setQueryId(rs.getLong("QueryId"));
				ct.setThresholdLtvPercentage(rs.getBigDecimal("ThresholdLtvPercentage"));
				ct.setCommodityId(JdbcUtil.getLong(rs.getObject("CommodityId")));

				return ct;
			}, colType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records not found in CollateralStructure{} for the collateral reference {} ", type, colType);
		}
		return null;
	}

	/**
	 * This method Deletes the Record from the CollateralStructure or CollateralStructure_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete CollateralStructure by key CollateralType
	 * 
	 * @param CollateralStructure (collateralStructure)
	 * @param type                (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CollateralStructure collateralStructure, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("Delete From CollateralStructure");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralType = :CollateralType");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralStructure);
		try {
			if (this.jdbcTemplate.update(sql.toString(), beanParameters) <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CollateralStructure or CollateralStructure_Temp.
	 * 
	 * save CollateralStructure
	 * 
	 * @param CollateralStructure (collateralStructure)
	 * @param type                (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(CollateralStructure collateralStructure, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into CollateralStructure");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (CollateralType, CollateralDesc, LtvType, LtvPercentage, MarketableSecurities, PreValidationReq");
		sql.append(", PostValidationReq, CollateralLocReq, CollateralValuatorReq, Remarks, AllowLtvWaiver");
		sql.append(", MaxLtvWaiver, PostValidation, PreValidation, Active, Fields, ActualBlock, SQLRule, Version");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId, ValuationFrequency, NextValuationDate, ValuationPending, QueryId");
		sql.append(", ThresholdLtvPercentage, CommodityId)");
		sql.append(" Values(");
		sql.append(" :CollateralType, :CollateralDesc, :LtvType, :LtvPercentage, :MarketableSecurities");
		sql.append(", :PreValidationReq, :PostValidationReq, :CollateralLocReq, :CollateralValuatorReq, :Remarks");
		sql.append(", :AllowLtvWaiver, :MaxLtvWaiver, :PostValidation, :PreValidation, :Active, :Fields, :ActualBlock");
		sql.append(", :SQLRule,  :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId");
		sql.append(", :NextTaskId, :RecordType, :WorkflowId, :ValuationFrequency, :NextValuationDate");
		sql.append(", :ValuationPending, :QueryId, :thresholdLtvPercentage, :commodityId)");

		logger.debug("InsertSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralStructure);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return collateralStructure.getCollateralType();
	}

	/**
	 * This method updates the Record CollateralStructure or CollateralStructure_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update CollateralStructure by key CollateralType and Version
	 * 
	 * @param CollateralStructure (collateralStructure)
	 * @param type                (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CollateralStructure collateralStructure, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("Update CollateralStructure");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set CollateralDesc = :collateralDesc, LtvType = :ltvType, LtvPercentage = :ltvPercentage");
		sql.append(", MarketableSecurities = :marketableSecurities, PreValidationReq = :preValidationReq");
		sql.append(", PostValidationReq = :postValidationReq, CollateralLocReq = :collateralLocReq");
		sql.append(", CollateralValuatorReq = :collateralValuatorReq, Remarks = :remarks");
		sql.append(", AllowLtvWaiver = :allowLtvWaiver, MaxLtvWaiver = :maxLtvWaiver");
		sql.append(", PostValidation = :PostValidation, PreValidation = :PreValidation, Active = :Active");
		sql.append(", Fields = :Fields, ActualBlock = :ActualBlock, SQLRule = :SQLRule, Version = :Version");
		sql.append(", LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus");
		sql.append(", RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId, ValuationFrequency = :ValuationFrequency");
		sql.append(", NextValuationDate = :NextValuationDate, ValuationPending = :ValuationPending");
		sql.append(", QueryId = :QueryId, ThresholdLtvPercentage = :thresholdLtvPercentage");
		sql.append(", CommodityId = :commodityId ");
		sql.append(" Where CollateralType = :CollateralType");

		logger.debug("Sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralStructure);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<String> getCollateralValuatorRequiredCodes() {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("collateralValuatorReq", 1);

		return jdbcTemplate.queryForList(
				"select CollateralType from CollateralStructure where collateralValuatorReq=:collateralValuatorReq",
				parameterSource, String.class);
	}

	@Override
	public boolean isMarketablesecuritiesExists() {
		String sql = "select count(CollateralType) from CollateralStructure where marketablesecurities = 1";
		return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Integer.class) > 0;
	}
}