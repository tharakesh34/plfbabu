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
 * * FileName : DeviationDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-06-2015 * *
 * Modified Date : 22-06-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-06-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.solutionfactory.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.solutionfactory.DeviationDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.solutionfactory.DeviationDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>DeviationDetail model</b> class.<br>
 * 
 */

public class DeviationDetailDAOImpl extends BasicDao<DeviationDetail> implements DeviationDetailDAO {
	private static Logger logger = LogManager.getLogger(DeviationDetailDAOImpl.class);

	public DeviationDetailDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new DeviationDetail
	 * 
	 * @return DeviationDetail
	 */

	@Override
	public DeviationDetail getDeviationDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("DeviationDetail");
		DeviationDetail deviationDetail = new DeviationDetail();
		if (workFlowDetails != null) {
			deviationDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return deviationDetail;
	}

	/**
	 * This method get the module from method getDeviationDetail() and set the new record flag as true and return
	 * DeviationDetail()
	 * 
	 * @return DeviationDetail
	 */

	@Override
	public DeviationDetail getNewDeviationDetail() {
		logger.debug("Entering");
		DeviationDetail deviationDetail = getDeviationDetail();
		deviationDetail.setNewRecord(true);
		logger.debug("Leaving");
		return deviationDetail;
	}

	/**
	 * Fetch the Record Deviation Details details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return DeviationDetail
	 */
	@Override
	public DeviationDetail getDeviationDetailById(final long id, String userRole, String type) {
		logger.debug("Entering");
		DeviationDetail deviationDetail = getDeviationDetail();

		deviationDetail.setId(id);
		deviationDetail.setUserRole(userRole);

		StringBuilder selectSql = new StringBuilder("Select DeviationID, UserRole, DeviatedValue");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" From DeviationDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DeviationID =:DeviationID and UserRole=:UserRole ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deviationDetail);
		RowMapper<DeviationDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(DeviationDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the Record Deviation Details details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return DeviationDetail
	 */
	@Override
	public List<DeviationDetail> getDeviationDetailsByDeviationId(final long id, String type) {
		logger.debug("Entering");
		DeviationDetail deviationDetail = getDeviationDetail();
		deviationDetail.setId(id);

		StringBuilder selectSql = new StringBuilder("Select DeviationID, UserRole, DeviatedValue");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" From DeviationDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DeviationID =:DeviationID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deviationDetail);
		RowMapper<DeviationDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(DeviationDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Fetch the Record Deviation Details details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return DeviationDetail
	 */
	@Override
	public List<DeviationDetail> getDeviationDetailsByModuleFinType(String finType, String module, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DeviationID, UserRole, DeviatedValue, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append("");
		}

		sql.append(" from DeviationDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DeviationID in (");
		sql.append(" Select DeviationID from DeviationHeader");
		sql.append(" where Module = ? and FinType = ?)");
		sql.append(" order by DelegatorGrade");

		logger.trace(Literal.SQL + sql.toString());
		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, module);
				ps.setString(index, finType);
			}
		}, new RowMapper<DeviationDetail>() {
			@Override
			public DeviationDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
				DeviationDetail dd = new DeviationDetail();

				dd.setDeviationID(rs.getLong("DeviationID"));
				dd.setUserRole(rs.getString("UserRole"));
				dd.setDeviatedValue(rs.getString("DeviatedValue"));
				dd.setVersion(rs.getInt("Version"));
				dd.setLastMntBy(rs.getLong("LastMntBy"));
				dd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				dd.setRecordStatus(rs.getString("RecordStatus"));
				dd.setRoleCode(rs.getString("RoleCode"));
				dd.setNextRoleCode(rs.getString("NextRoleCode"));
				dd.setTaskId(rs.getString("TaskId"));
				dd.setNextTaskId(rs.getString("NextTaskId"));
				dd.setRecordType(rs.getString("RecordType"));
				dd.setWorkflowId(rs.getLong("WorkflowId"));

				return dd;
			}
		});
	}

	/**
	 * This method Deletes the Record from the DeviationDetails or DeviationDetails_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Deviation Details by key DeviationID
	 * 
	 * @param Deviation Details (deviationDetail)
	 * @param type      (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(DeviationDetail deviationDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From DeviationDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DeviationID =:DeviationID and UserRole=:UserRole ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deviationDetail);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into DeviationDetails or DeviationDetails_Temp. it fetches the available Sequence
	 * form SeqDeviationDetails by using getNextidviewDAO().getNextId() method.
	 *
	 * save Deviation Details
	 * 
	 * @param Deviation Details (deviationDetail)
	 * @param type      (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(DeviationDetail deviationDetail, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder("Insert Into DeviationDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (DeviationID, UserRole, DeviatedValue");
		insertSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		insertSql.append(", DelegatorGrade)");
		insertSql.append(" Values(:DeviationID, :UserRole, :DeviatedValue");
		insertSql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId");
		insertSql.append(", :DelegatorGrade)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deviationDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return deviationDetail.getId();
	}

	/**
	 * This method updates the Record DeviationDetails or DeviationDetails_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Deviation Details by key DeviationID and Version
	 * 
	 * @param Deviation Details (deviationDetail)
	 * @param type      (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(DeviationDetail deviationDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update DeviationDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DeviatedValue = :DeviatedValue, DelegatorGrade = :DelegatorGrade");
		updateSql.append(
				", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where DeviationID =:DeviationID and UserRole=:UserRole ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deviationDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}