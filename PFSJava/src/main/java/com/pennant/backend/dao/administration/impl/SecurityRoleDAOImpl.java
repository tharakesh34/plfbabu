/**
 * g * Copyright 2011 - Pennant Technologies
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
 * * FileName : SecurityRoleDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 2-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 2-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.administration.impl;

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

import com.pennant.backend.dao.administration.SecurityRoleDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>SecurityRole model</b> class.<br>
 * 
 */
public class SecurityRoleDAOImpl extends SequenceDao<SecurityRole> implements SecurityRoleDAO {
	private static Logger logger = LogManager.getLogger(SecurityRoleDAOImpl.class);

	public SecurityRoleDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new SecurityRole
	 * 
	 * @return SecurityRole
	 */
	@Override
	public SecurityRole getSecurityRole() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SecurityRole");
		SecurityRole securityRole = new SecurityRole();
		if (workFlowDetails != null) {
			securityRole.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return securityRole;
	}

	/**
	 * Fetch the Record SecurityRole details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return SecurityRole
	 */
	@Override
	public SecurityRole getSecurityRoleById(final long id, String type) {
		logger.debug("Entering");
		SecurityRole secRoles = new SecurityRole();
		secRoles.setId(id);

		StringBuilder selectSql = new StringBuilder("Select RoleID, RoleApp, RoleCd, ");
		selectSql.append(" RoleDesc, RoleCategory , ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" ,lovDescRoleAppName ");
		}

		selectSql.append(" From SecRoles");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RoleID =:RoleID");

		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		RowMapper<SecurityRole> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityRole.class);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the Record SecurityRole details by key field
	 * 
	 * @param roleCode (String)
	 * @param type     (String) ""/_Temp/_View
	 * @return SecurityRole
	 */
	@Override
	public SecurityRole getSecurityRoleByRoleCd(final String roleCd, String type) {
		logger.debug("Entering");
		SecurityRole secRoles = new SecurityRole();
		secRoles.setRoleCd(roleCd);

		StringBuilder selectSql = new StringBuilder("Select RoleID, RoleApp, ");
		selectSql.append(" RoleCd, RoleDesc, RoleCategory , ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" ,lovDescRoleAppName ");
		}

		selectSql.append(" From SecRoles");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RoleCd =:RoleCd");

		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		RowMapper<SecurityRole> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityRole.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the SecRoles or SecRoles_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete SecurityRole by key RoleID
	 * 
	 * @param SecurityRole (securityRole)
	 * @param type         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(SecurityRole secRoles, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		String deleteSql = "Delete From SecRoles" + StringUtils.trimToEmpty(type) + " Where RoleID =:RoleID";
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		recordCount = this.jdbcTemplate.update(deleteSql, beanParameters);
		logger.debug("deleteSql:" + deleteSql);
		try {
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into SecRoles or SecRoles_Temp. it fetches the available Sequence form SeqSecRoles
	 * by using getNextidviewDAO().getNextId() method.
	 *
	 * save SecurityRole
	 * 
	 * @param SecurityRole (securityRole)
	 * @param type         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 */
	public long save(SecurityRole secRoles, String type) {
		logger.debug("Entering");
		if (secRoles.getId() == Long.MIN_VALUE) {
			secRoles.setId(getNextValue("SeqSecRoles"));
			logger.debug("get NextValue:" + secRoles.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into SecRoles");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RoleID, RoleApp, RoleCd, RoleDesc, RoleCategory, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:RoleID, :RoleApp, :RoleCd, :RoleDesc, :RoleCategory, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql:" + insertSql);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return secRoles.getId();
	}

	/**
	 * This method updates the Record SecRoles or SecRoles_Temp. if Record not updated then throws DataAccessException
	 * with error 41004. update SecurityRole by key RoleID and Version
	 * 
	 * @param SecurityRole (securityRole)
	 * @param type         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(SecurityRole secRoles, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update SecRoles");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set RoleApp = :RoleApp, RoleCd = :RoleCd, ");
		updateSql.append(" RoleDesc = :RoleDesc, RoleCategory = :RoleCategory , ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, ");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, ");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where RoleID =:RoleID");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql:" + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<SecurityRole> getSecurityRole(String roleCode) {
		logger.debug("Entering");

		SecurityRole secRoles = new SecurityRole();
		secRoles.setRoleCd(roleCode);

		StringBuilder selectSql = new StringBuilder("Select RoleDesc  From SecRoles ");
		selectSql.append(" Where RoleCd =:RoleCd");

		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRoles);
		RowMapper<SecurityRole> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityRole.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<SecurityRole> getApprovedSecurityRoles() {
		SecurityRole secRole = new SecurityRole();

		StringBuilder sql = new StringBuilder("Select * From SecRoles");
		sql.append(" Where RoleID not in (");
		sql.append(" Select RoleID From SecRoleGroups srg");
		sql.append(" Inner Join SecGroupRights sgr on sgr.GrpID = srg.GrpID");
		sql.append(" Inner Join SecRights sr on sr.RightID = sgr.RightID and sr.RightType = 0");
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRole);
		RowMapper<SecurityRole> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityRole.class);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<SecurityRole> getApprovedSecurityRole() {
		String sql = "Select RoleID, RoleCd, RoleDesc From SecRoles";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcTemplate.query(sql, (rs, rowNum) -> {
			SecurityRole role = new SecurityRole();

			role.setRoleID(rs.getLong("RoleID"));
			role.setRoleCd(rs.getString("RoleCd"));
			role.setRoleDesc(rs.getString("RoleDesc"));

			return role;

		});
	}

	/**
	 * Fetch the Record SecurityRole details by key field
	 * 
	 * @return SecurityRole
	 */
	@Override
	public List<SecurityRole> getSecurityRolesByRoleCodes(List<String> strings) {
		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("Rolecds", strings);

		StringBuilder selectSql = new StringBuilder("SELECT * FROM SecRoles where rolecd in (:Rolecds) ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<SecurityRole> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityRole.class);

		return this.jdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
	}

	@Override
	public boolean isDuplicateKey(long roleApp, String roleCd, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "RoleCd = :roleCd and RoleApp = :roleApp";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("SecRoles", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("SecRoles_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "SecRoles_Temp", "SecRoles" }, whereClause);
			break;
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("roleApp", roleApp);
		paramSource.addValue("roleCd", roleCd);
		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);
		boolean exists = false;
		if (count > 0) {
			exists = true;
		}
		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public List<String> getSecurityRoleByUserId(long userId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select rol.roleCd");
		sql.append(" from SecUsers u");
		sql.append(" left join rmtbranches b on b.branchcode = u.UsrBranchCode");
		sql.append(" inner join SecuserOPerations uop on uop.usrId = u.usrId");
		sql.append(" inner join secOPerationRoles opr on opr.oprid = uop.oprid");
		sql.append(" inner join secRoles rol on rol.roleId = opr.roleId");
		sql.append(" where u.usrID = :usrID");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("usrID", userId);

		return jdbcTemplate.queryForList(sql.toString(), paramSource, String.class);
	}
}