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
 * * FileName : LimitGroupDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-03-2016 * * Modified
 * Date : 31-03-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-03-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.limit.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.limit.LimitGroupDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.limit.LimitGroup;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>LimitGroup model</b> class.<br>
 * 
 */
public class LimitGroupDAOImpl extends BasicDao<LimitGroup> implements LimitGroupDAO {
	private static Logger logger = LogManager.getLogger(LimitGroupDAOImpl.class);

	/**
	 * This method set the Work Flow id based on the module name and return the new LimitGroup
	 * 
	 * @return LimitGroup
	 */
	@Override
	public LimitGroup getLimitGroup() {
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LimitGroup");
		LimitGroup limitGroup = new LimitGroup();
		if (workFlowDetails != null) {
			limitGroup.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		return limitGroup;
	}

	/**
	 * This method get the module from method getLimitGroup() and set the new record flag as true and return
	 * LimitGroup()
	 * 
	 * @return LimitGroup
	 */
	@Override
	public LimitGroup getNewLimitGroup() {
		LimitGroup limitGroup = getLimitGroup();
		limitGroup.setNewRecord(true);
		return limitGroup;
	}

	/**
	 * Fetch the Record Limit Group details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return LimitGroup
	 */
	@Override
	public LimitGroup getLimitGroupById(final String id, String type) {
		logger.debug(Literal.ENTERING);
		LimitGroup limitGroup = getLimitGroup();
		limitGroup.setId(id);

		StringBuilder sql = new StringBuilder();
		sql.append("Select LimitCategory, GroupCode, GroupName,Active, GroupOf");
		sql.append(", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append("");
		}
		sql.append(" From LimitGroup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where GroupCode =:GroupCode");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroup);
		RowMapper<LimitGroup> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitGroup.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Limit Group not avilable for the specified Group {}", id);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * This method Deletes the Record from the LimitGroup or LimitGroup_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Limit Group by key GroupCode
	 * 
	 * @param Limit Group (limitGroup)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(LimitGroup limitGroup, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Delete From LimitGroup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where GroupCode =:GroupCode");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroup);
		try {
			recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into LimitGroup or LimitGroup_Temp.
	 *
	 * save Limit Group
	 * 
	 * @param Limit Group (limitGroup)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(LimitGroup limitGroup, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into LimitGroup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (LimitCategory,GroupCode, GroupName,Active,GroupOf");
		sql.append(", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:LimitCategory,:GroupCode, :GroupName,:Active,:GroupOf");
		sql.append(", :Version, :CreatedBy, :CreatedOn, :LastMntBy, :LastMntOn, :RecordStatus");
		sql.append(", :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroup);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return limitGroup.getId();
	}

	/**
	 * This method updates the Record LimitGroup or LimitGroup_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Limit Group by key GroupCode and Version
	 * 
	 * @param Limit Group (limitGroup)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(LimitGroup limitGroup, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update LimitGroup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set LimitCategory =:LimitCategory, GroupName = :GroupName");
		sql.append(", Active =:Active, GroupOf =:GroupOf");
		sql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where GroupCode =:GroupCode");

		if (!type.endsWith("_Temp")) {
			sql.append("  and Version= :Version-1");
		}

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroup);
		int recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

}