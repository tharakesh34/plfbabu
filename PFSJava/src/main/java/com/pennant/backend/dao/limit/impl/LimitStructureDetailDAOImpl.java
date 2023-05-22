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
 * * FileName : LimitStructureDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-03-2016 * *
 * Modified Date : 31-03-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-03-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.limit.impl;

import java.sql.Timestamp;
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

import com.pennant.backend.dao.limit.LimitStructureDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>LimitStructureDetail model</b> class.<br>
 * 
 */

public class LimitStructureDetailDAOImpl extends SequenceDao<LimitDetails> implements LimitStructureDetailDAO {
	private static Logger logger = LogManager.getLogger(LimitStructureDetailDAOImpl.class);

	/**
	 * This method set the Work Flow id based on the module name and return the new LimitStructureDetail
	 * 
	 * @return LimitStructureDetail
	 */

	@Override
	public LimitStructureDetail getLimitStructureDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LimitStructureDetail");
		LimitStructureDetail limitStructureDetail = new LimitStructureDetail();
		if (workFlowDetails != null) {
			limitStructureDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return limitStructureDetail;
	}

	/**
	 * This method get the module from method getLimitStructureDetail() and set the new record flag as true and return
	 * LimitStructureDetail()
	 * 
	 * @return LimitStructureDetail
	 */

	@Override
	public LimitStructureDetail getNewLimitStructureDetail() {
		logger.debug("Entering");
		LimitStructureDetail limitStructureDetail = getLimitStructureDetail();
		limitStructureDetail.setNewRecord(true);
		logger.debug("Leaving");
		return limitStructureDetail;
	}

	/**
	 * This method Deletes the Record from the LimitStructureDetails or LimitStructureDetails_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Limit Structure Detail by key LimitStructureCode
	 * 
	 * @param Limit Structure Detail (limitStructureDetail)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(LimitStructureDetail limitStructureDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From LimitStructureDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LimitStructureCode =:LimitStructureCode");
		deleteSql.append("  AND  GroupCode = :GroupCode ");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitStructureDetail);
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

	@Override
	public void deleteByStructureCode(String code, String type) {
		logger.debug("Entering");

		LimitStructureDetail limitStructureDetail = new LimitStructureDetail();
		limitStructureDetail.setLimitStructureCode(code);
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From LimitStructureDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LimitStructureCode =:LimitStructureCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitStructureDetail);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount < 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void deleteBySrtructureId(long id, String type) {
		logger.debug("Entering");

		LimitStructureDetail limitStructureDetail = new LimitStructureDetail();
		limitStructureDetail.setLimitStructureDetailsID(id);

		StringBuilder deleteSql = new StringBuilder("Delete From LimitStructureDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LimitStructureDetailsID =:LimitStructureDetailsID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitStructureDetail);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);

		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into LimitStructureDetails or LimitStructureDetails_Temp.
	 *
	 * save Limit Structure Detail
	 * 
	 * @param Limit Structure Detail (limitStructureDetail)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(LimitStructureDetail limitStructureDetail, String type) {
		logger.debug("Entering");
		if (limitStructureDetail.getCreatedOn() == null) {
			limitStructureDetail.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		}
		if (limitStructureDetail.getId() == Long.MIN_VALUE) {
			limitStructureDetail.setId(getNextValue("SeqLimitStructureDetails"));
			logger.debug("get NextValue:" + limitStructureDetail.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into LimitStructureDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (LimitStructureDetailsID,LimitCategory, LimitStructureCode, GroupCode, LimitLine, ItemSeq, Editable, DisplayStyle, ItemPriority ,LimitCheck,Revolving,ItemLevel");
		insertSql.append(
				", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:LimitStructureDetailsID,:LimitCategory,:LimitStructureCode, :GroupCode, :LimitLine, :ItemSeq, :Editable, :DisplayStyle,:ItemPriority ,:LimitCheck,:Revolving,:ItemLevel");
		insertSql.append(
				", :Version ,:CreatedBy, :CreatedOn, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitStructureDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return limitStructureDetail.getId();
	}

	/**
	 * This method updates the Record LimitStructureDetails or LimitStructureDetails_Temp. if Record not updated then
	 * throws DataAccessException with error 41004. update Limit Structure Detail by key LimitStructureCode and Version
	 * 
	 * @param Limit Structure Detail (limitStructureDetail)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(LimitStructureDetail limitStructureDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update LimitStructureDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set LimitStructureDetailsID =:LimitStructureDetailsID,LimitCategory=:LimitCategory, GroupCode = :GroupCode, LimitLine = :LimitLine, ItemSeq = :ItemSeq, Editable = :Editable, DisplayStyle = :DisplayStyle");
		updateSql.append(
				" ,ItemPriority =:ItemPriority ,LimitCheck =:LimitCheck,Revolving =:Revolving,ItemLevel = :ItemLevel");
		updateSql.append(
				", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where LimitStructureCode =:LimitStructureCode");
		if (limitStructureDetail.getLimitLine() == null) {
			updateSql.append("  AND  GroupCode = :GroupCode ");
		} else {
			updateSql.append(" AND LimitLine = :LimitLine  ");
		}

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitStructureDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateById(LimitStructureDetail limitStructureDetail, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update LimitStructureDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set LimitCategory=:LimitCategory,  LimitStructureCode = :LimitStructureCode, GroupCode = :GroupCode, LimitLine = :LimitLine, ItemSeq = :ItemSeq, Editable = :Editable, DisplayStyle = :DisplayStyle");
		updateSql.append(
				" ,ItemPriority =:ItemPriority ,LimitCheck =:LimitCheck,Revolving =:Revolving,ItemLevel = :ItemLevel");
		updateSql.append(
				", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where LimitStructureDetailsID =:LimitStructureDetailsID");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitStructureDetail);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Record Limit Group details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return limitStructureDetails
	 */
	@Override
	public List<LimitStructureDetail> getLimitStructureDetailById(final String id, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;

		StringBuilder selectSql = new StringBuilder(
				"Select LimitStructureDetailsID,LimitStructureCode, GroupCode, LimitLine, ItemSeq, Editable, DisplayStyle,LimitCategory, ItemPriority ,LimitCheck,Revolving,ItemLevel");
		selectSql.append(
				", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",GroupName,LimitLineDesc");
		}
		selectSql.append(" From LimitStructureDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitStructureCode =:LimitStructureCode ");
		selectSql.append("  order by ItemSeq");

		logger.debug("selectSql: " + selectSql.toString());
		source = new MapSqlParameterSource();
		source.addValue("LimitStructureCode", id);

		RowMapper<LimitStructureDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitStructureDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public int validationCheck(String limitGroup, String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder("Select Count(*) From LimitStructureDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GroupCode = :GroupCode");
		source.addValue("GroupCode", limitGroup);

		logger.debug(Literal.SQL + selectSql.toString());
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public int limitItemCheck(String limitItem, String limitcategory, String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder("Select Count(*) From LimitStructureDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  LimitLine = :LimitLine AND LimitCategory = :LimitCategory ");
		source.addValue("LimitLine", limitItem);
		source.addValue("LimitCategory", limitcategory);

		logger.debug(Literal.SQL + selectSql.toString());
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	/**
	 * Fetch Limit Structure details by limit structure Id.
	 * 
	 * @param limitStructureId (long)
	 * @param type
	 * @return LimitStructureDetail
	 */
	@Override
	public LimitStructureDetail getLimitStructureDetail(long limitStructureId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" LimitStructureDetailsID, LimitStructureCode, GroupCode, LimitLine");
		sql.append(", ItemSeq, Editable, DisplayStyle, LimitCategory, ItemPriority, LimitCheck");
		sql.append(", Revolving, ItemLevel, Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", StructureName, GroupName, LimitLineDesc");
		}

		sql.append(" From LimitStructureDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LimitStructureDetailsID = ?");
		sql.append(" order by ItemSeq");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				LimitStructureDetail lsd = new LimitStructureDetail();

				lsd.setLimitStructureDetailsID(rs.getLong("LimitStructureDetailsID"));
				lsd.setLimitStructureCode(rs.getString("LimitStructureCode"));
				lsd.setGroupCode(rs.getString("GroupCode"));
				lsd.setLimitLine(rs.getString("LimitLine"));
				lsd.setItemSeq(rs.getInt("ItemSeq"));
				lsd.setEditable(rs.getBoolean("Editable"));
				lsd.setDisplayStyle(rs.getString("DisplayStyle"));
				lsd.setLimitCategory(rs.getString("LimitCategory"));
				lsd.setItemPriority(rs.getInt("ItemPriority"));
				lsd.setLimitCheck(rs.getBoolean("LimitCheck"));
				lsd.setRevolving(rs.getBoolean("Revolving"));
				lsd.setItemLevel(rs.getInt("ItemLevel"));
				lsd.setVersion(rs.getInt("Version"));
				lsd.setCreatedBy(rs.getLong("CreatedBy"));
				lsd.setCreatedOn(rs.getTimestamp("CreatedOn"));
				lsd.setLastMntBy(rs.getLong("LastMntBy"));
				lsd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				lsd.setRecordStatus(rs.getString("RecordStatus"));
				lsd.setRoleCode(rs.getString("RoleCode"));
				lsd.setNextRoleCode(rs.getString("NextRoleCode"));
				lsd.setTaskId(rs.getString("TaskId"));
				lsd.setNextTaskId(rs.getString("NextTaskId"));
				lsd.setRecordType(rs.getString("RecordType"));
				lsd.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					lsd.setStructureName(rs.getString("StructureName"));
					lsd.setGroupName(rs.getString("GroupName"));
					lsd.setLimitLineDesc(rs.getString("LimitLineDesc"));
				}

				return lsd;
			}, limitStructureId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Method for get total count of limit structure records.
	 * 
	 * @param structureCode
	 * @param tableType
	 * 
	 * @return integer
	 */
	@Override
	public int getLimitStructureCountById(String structureCode, String tableType) {
		logger.debug("Entering");

		LimitStructure limitStructure = new LimitStructure();
		limitStructure.setStructureCode(structureCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) FROM LimitStructure ");
		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" WHERE StructureCode = :StructureCode");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(limitStructure);
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParams, Integer.class);
	}

	@Override
	public List<LimitStructureDetail> getStructuredetailsByLimitGroup(String limitCategory, String code, boolean isLine,
			String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				"SELECT LimitStructureDetailsID, LimitStructureCode, GroupCode, LimitLine, ");
		selectSql.append(
				" ItemSeq, Editable, DisplayStyle,LimitCategory, ItemPriority ,LimitCheck,Revolving,ItemLevel, Version, CreatedBy ");

		selectSql.append(" From LimitStructureDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitCategory =:LimitCategory ");

		if (isLine) {
			selectSql.append(" AND LimitLine =:LimitLine ");
		} else {
			selectSql.append(" AND GroupCode =:GroupCode ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("GroupCode", code);
		source.addValue("LimitLine", code);
		source.addValue("LimitCategory", limitCategory);

		RowMapper<LimitStructureDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitStructureDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	/**
	 * Fetch Limit Structure details by limit structure Id.
	 * 
	 * @param limitStructureCode
	 * @param limitLine
	 * @return
	 */
	@Override
	public LimitStructureDetail getStructureByLine(String limitStructureCode, String limitLine, boolean group) {
		logger.debug("Entering");

		LimitStructureDetail structureDetail = new LimitStructureDetail();
		structureDetail.setLimitStructureCode(limitStructureCode);

		if (group) {
			structureDetail.setGroupCode(limitLine);
		} else {
			structureDetail.setLimitLine(limitLine);
		}

		StringBuilder selectSql = new StringBuilder(
				"SELECT LimitStructureDetailsID, LimitStructureCode, GroupCode, LimitLine, ");
		selectSql.append(
				" ItemSeq, Editable, DisplayStyle,LimitCategory, ItemPriority ,LimitCheck,Revolving,ItemLevel, Version, CreatedBy,");
		selectSql.append(
				" CreatedOn, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From LimitStructureDetails");
		selectSql.append(" Where LimitStructureCode =:LimitStructureCode and  ");
		if (group) {
			selectSql.append(" GroupCode=:GroupCode");
		} else {
			selectSql.append(" LimitLine=:LimitLine");
		}
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(structureDetail);
		RowMapper<LimitStructureDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitStructureDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}