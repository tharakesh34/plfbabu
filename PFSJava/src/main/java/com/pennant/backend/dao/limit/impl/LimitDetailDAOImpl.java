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
 * * FileName : LimitDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-03-2016 * * Modified
 * Date : 31-03-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-03-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.limit.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>LimitDetail model</b> class.<br>
 * 
 */

public class LimitDetailDAOImpl extends SequenceDao<LimitDetails> implements LimitDetailDAO {
	private static Logger logger = LogManager.getLogger(LimitDetailDAOImpl.class);

	public LimitDetailDAOImpl() {
		super();

	}

	/**
	 * Fetch the Record Limit Details details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return LimitDetail
	 */
	@Override
	public List<LimitDetails> getLimitDetailsByHeaderId(final long id, String type) {
		logger.debug(Literal.ENTERING);

		LimitDetails limitDetail = new LimitDetails();
		limitDetail.setLimitHeaderId(id);

		StringBuilder sql = new StringBuilder();
		sql.append("Select DetailId, LimitHeaderId, LimitStructureDetailsID, ExpiryDate, Revolving, LimitSanctioned");
		sql.append(", ReservedLimit, UtilisedLimit, LimitCheck,LimitChkMethod");
		sql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LimitLineDesc, GroupName, GroupCode, LimitLine, ItemSeq, ItemPriority");
			sql.append(", Editable, DisplayStyle, ItemLevel, BankingArrangement, LimitCondition");
			sql.append(", ExternalRef, ExternalRef1, Tenor, osPriBal");
		}
		sql.append(" From LimitDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LimitHeaderId =:LimitHeaderId");
		sql.append(" order by ItemPriority, ItemSeq");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		RowMapper<LimitDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitDetails.class);

		List<LimitDetails> detailsList = this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);

		logger.debug(Literal.LEAVING);
		return detailsList;
	}

	/**
	 * Fetch the Record Limit Details details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return LimitDetail
	 */
	@Override
	public List<LimitDetails> getLatestLimitExposures(final long id, String type) {
		logger.debug(Literal.ENTERING);

		LimitDetails limitDetail = new LimitDetails();
		limitDetail.setLimitHeaderId(id);

		StringBuilder sql = new StringBuilder();
		sql.append("Select DetailId, LimitHeaderId, LimitStructureDetailsID, LimitSanctioned, ReservedLimit");
		sql.append(", UtilisedLimit, NonRvlUtilised, BankingArrangement, LimitCondition");
		sql.append(", ExternalRef, ExternalRef1, Tenor, osPriBal");
		sql.append(" From LimitDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LimitHeaderId = :LimitHeaderId");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		RowMapper<LimitDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitDetails.class);

		List<LimitDetails> detailsList = this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		logger.debug(Literal.LEAVING);
		return detailsList;
	}

	/**
	 * This method Deletes the Record from the LimitDetails or LimitDetails_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Limit Details by key DetailId
	 * 
	 * @param Limit Details (limitDetail)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void deletebyHeaderId(long headerId, String type) {
		logger.debug(Literal.ENTERING);
		LimitDetails limitDetail = new LimitDetails();
		limitDetail.setLimitHeaderId(headerId);
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder("Delete From LimitDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LimitHeaderId =:LimitHeaderId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
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
	 * This method insert new Records into LimitDetails or LimitDetails_Temp. it fetches the available Sequence form
	 * SeqLimitDetails by using getNextidviewDAO().getNextId() method.
	 *
	 * save Limit Details
	 * 
	 * @param Limit Details (limitDetail)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(LimitDetails ld, String type) {
		logger.debug(Literal.ENTERING);
		if (ld.getId() == Long.MIN_VALUE) {
			ld.setId(getNextValue("SeqLimitDetails"));
		}

		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" LimitDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(DetailId, LimitHeaderId, LimitStructureDetailsID, ExpiryDate, Revolving, LimitSanctioned");
		sql.append(", ReservedLimit, UtilisedLimit, NonRvlUtilised, LimitCheck");
		sql.append(", LimitChkMethod, Version, CreatedBy, CreatedOn");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, bankingArrangement, limitCondition, externalRef, externalRef1");
		sql.append(", tenor, osPriBal");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setLong(index++, ld.getDetailId());
				ps.setLong(index++, ld.getLimitHeaderId());
				ps.setLong(index++, ld.getLimitStructureDetailsID());
				ps.setDate(index++, JdbcUtil.getDate(ld.getExpiryDate()));
				ps.setBoolean(index++, ld.isRevolving());
				ps.setBigDecimal(index++, ld.getLimitSanctioned());
				ps.setBigDecimal(index++, ld.getReservedLimit());
				ps.setBigDecimal(index++, ld.getUtilisedLimit());
				ps.setBigDecimal(index++, ld.getNonRvlUtilised());
				ps.setBoolean(index++, ld.isLimitCheck());
				ps.setString(index++, ld.getLimitChkMethod());
				ps.setInt(index++, ld.getVersion());
				ps.setLong(index++, ld.getCreatedBy());
				ps.setTimestamp(index++, ld.getCreatedOn());
				ps.setLong(index++, ld.getLastMntBy());
				ps.setTimestamp(index++, ld.getLastMntOn());
				ps.setString(index++, ld.getRecordStatus());
				ps.setString(index++, ld.getRoleCode());
				ps.setString(index++, ld.getNextRoleCode());
				ps.setString(index++, ld.getTaskId());
				ps.setString(index++, ld.getNextTaskId());
				ps.setString(index++, ld.getRecordType());
				ps.setLong(index++, ld.getWorkflowId());
				ps.setString(index++, ld.getBankingArrangement());
				ps.setString(index++, ld.getLimitCondition());
				ps.setString(index++, ld.getExternalRef());
				ps.setString(index++, ld.getExternalRef1());
				ps.setInt(index++, ld.getTenor());
				ps.setBigDecimal(index, ld.getOsPriBal());
			}
		});

		return ld.getId();
	}

	/**
	 * This method updates the Record LimitDetails or LimitDetails_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Limit Details by key DetailId and Version
	 * 
	 * @param Limit Details (limitDetail)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(LimitDetails ld, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("Update LimitDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set LimitHeaderId = ?, LimitStructureDetailsID = ?, ExpiryDate = ?, Revolving = ?");
		sql.append(
				", LimitSanctioned= ?, LimitCheck = ?, LimitChkMethod = ?, Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?, bankingArrangement = ?, limitCondition = ?");
		sql.append(" Where DetailId = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ?-1");
		}

		logger.trace(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setLong(index++, ld.getLimitHeaderId());
				ps.setLong(index++, ld.getLimitStructureDetailsID());
				ps.setDate(index++, JdbcUtil.getDate(ld.getExpiryDate()));
				ps.setBoolean(index++, ld.isRevolving());
				ps.setBigDecimal(index++, ld.getLimitSanctioned());
				ps.setBoolean(index++, ld.isLimitCheck());
				ps.setString(index++, ld.getLimitChkMethod());
				ps.setInt(index++, ld.getVersion());
				ps.setLong(index++, ld.getLastMntBy());
				ps.setTimestamp(index++, ld.getLastMntOn());
				ps.setString(index++, ld.getRecordStatus());
				ps.setString(index++, ld.getRoleCode());
				ps.setString(index++, ld.getNextRoleCode());
				ps.setString(index++, ld.getTaskId());
				ps.setString(index++, ld.getNextTaskId());
				ps.setString(index++, ld.getRecordType());
				ps.setLong(index++, ld.getWorkflowId());
				ps.setString(index++, ld.getBankingArrangement());
				ps.setString(index++, ld.getLimitCondition());
				ps.setLong(index++, ld.getDetailId());
				if (!type.endsWith("_Temp")) {
					ps.setInt(index, ld.getVersion());
				}
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateReserveUtilise(LimitDetails limitDetail, String type) {
		StringBuilder sql = new StringBuilder("Update LimitDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set ReservedLimit = ?, UtilisedLimit = ?");
		sql.append(", NonRvlUtilised = ?, OsPriBal = ?");
		sql.append(" Where DetailId = ?");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, limitDetail.getReservedLimit());
			ps.setBigDecimal(index++, limitDetail.getUtilisedLimit());
			ps.setBigDecimal(index++, limitDetail.getNonRvlUtilised());
			ps.setBigDecimal(index++, limitDetail.getOsPriBal());
			ps.setLong(index, limitDetail.getDetailId());
		});
	}

	@Override
	public int validationCheck(String limitGroup, String type) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder("Select Count(*) From LimitDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GroupCode = :GroupCode");
		source.addValue("GroupCode", limitGroup);

		logger.debug("selectSql: " + selectSql.toString());
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public int limitItemCheck(String limitItem, String limitcategory, String type) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder("Select Count(*) From LimitDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  LimitLine = :LimitLine ");
		source.addValue("LimitLine", limitItem);
		source.addValue("LimitCategory", limitcategory);

		logger.debug("selectSql: " + selectSql.toString());
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public int limitStructureCheck(String structureCode, String type) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder("Select Count(*) From LimitHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitStructureCode = :LimitStructureCode");
		source.addValue("LimitStructureCode", structureCode);

		logger.debug("selectSql: " + selectSql.toString());
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public List<LimitDetails> getLimitDetailsByCustID(long headerId) {
		String sql = "Select LimitLine, LimitLineDesc, SqlRule From LimitLines_View Where LimitHeaderId = ? and SqlRule is not null";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, ps -> {
			ps.setLong(1, headerId);
		}, (rs, i) -> {
			LimitDetails ld = new LimitDetails();

			ld.setLimitLine(rs.getString(1));
			ld.setLimitLineDesc(rs.getString(2));
			ld.setSqlRule(rs.getString(3));

			return ld;
		});
	}

	@Override
	public List<LimitDetails> getLimitByLineAndgroup(long headerId, String limitItem, List<String> groupcode) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select DetailId, LimitHeaderId, LimitLine, GroupCode, LimitStructureDetailsID, LimitChkMethod");
		sql.append(", ExpiryDate, LimitSanctioned,  ReservedLimit, UtilisedLimit, NonRvlUtilised, OsPriBal");
		sql.append(", LimitCheck, Revolving, Currency, ValidateMaturityDate");
		sql.append(", lv.Version, lv.CreatedBy, lv.CreatedOn, lv.LastMntBy, lv.LastMntOn, lv.RecordStatus");
		sql.append(", lv.RoleCode, lv.NextRoleCode, lv.TaskId");
		sql.append(", lv.NextTaskId, lv.RecordType, lv.WorkflowId, lv.OsPriBal");
		sql.append(" From LimitLines_View lv");
		sql.append(" Inner Join LimitHeader lh on lh.HeaderId = lv.LimitHeaderId");
		sql.append(" Where (LimitLine=:LimitLine OR GroupCode in (:GroupCodes))");
		sql.append(" and lv.LimitHeaderId = :LimitHeaderId ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitLine", limitItem);
		source.addValue("GroupCodes", groupcode);
		source.addValue("LimitHeaderId", headerId);

		RowMapper<LimitDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitDetails.class);

		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public List<LimitDetails> getLimitDetailsByLimitLine(long headeId, String type) {

		StringBuilder sql = new StringBuilder();
		sql.append("Select R.RuleCode  LimitLine, R.RuleCodeDesc LimitLineDesc, R.SqlRule");
		sql.append(" from Rules R where RuleCode in ( select LimitLine from LimitDetails_view");
		sql.append(" where LimitLine is not null and LimitHeaderId= ?)");
		sql.append(StringUtils.trimToEmpty(type));
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, headeId);
		}, (rs, rowNum) -> {
			LimitDetails ld = new LimitDetails();
			ld.setLimitLine(rs.getString("RuleCode"));
			ld.setLimitLineDesc(rs.getString("LimitLineDesc"));
			ld.setSqlRule(rs.getString("SqlRule"));
			return ld;
		});

	}

	/**
	 * Method for fetch record count from Limit details.
	 * 
	 * @param structureId
	 * @return integer
	 */
	@Override
	public int getLimitDetailByStructureId(long structureId, String type) {
		logger.debug(Literal.ENTERING);

		LimitDetails limitDetails = new LimitDetails();
		limitDetails.setLimitStructureDetailsID(structureId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) FROM LimitStructureDetails ");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE LimitStructureDetailsID = :LimitStructureDetailsID");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(limitDetails);
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParams, Integer.class);
	}

	/**
	 * Fetch the Record Limit Details details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return LimitDetail
	 */
	@Override
	public LimitDetails getLimitLineByDetailId(final long id, String type) {
		logger.debug(Literal.ENTERING);

		LimitDetails limitDetail = new LimitDetails();
		limitDetail.setDetailId(id);

		StringBuilder sql = new StringBuilder();
		sql.append("select DetailId, LimitLine, LimitHeaderId, LimitStructureDetailsID, ExpiryDate, Revolving");
		sql.append(", LimitSanctioned, ReservedLimit, UtilisedLimit, LimitCheck, LimitChkMethod");
		sql.append(", BankingArrangement, LimitCondition, ExternalRef, ExternalRef1, Tenor, OSPriBal");
		sql.append(", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LimitLineDesc, GroupName, GroupCode, LimitLine, ItemSeq, ItemPriority");
			sql.append(", Editable, DisplayStyle, ItemLevel");
		}
		sql.append(" From LimitDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DetailId = :DetailId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		RowMapper<LimitDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitDetails.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the LimitDetails or LimitDetails_Temp. Using LimitStructureDetailid
	 */
	@Override
	public void deletebyLimitStructureDetailId(long strId, String type) {
		logger.debug(Literal.ENTERING);
		LimitDetails limitDetail = new LimitDetails();
		limitDetail.setLimitStructureDetailsID(strId);
		StringBuilder deleteSql = new StringBuilder("Delete From LimitDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LimitStructureDetailsID =:LimitStructureDetailsID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");

	}

	/**
	 * Method for fetching LimitDetails for Institution Limits
	 */
	@Override
	public List<LimitDetails> getLimitDetails(long headerId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select DetailId, LimitHeaderId, LimitStructureDetailsID, ExpiryDate, Revolving");
		sql.append(", LimitLine, LimitLineDesc, SqlRule");
		sql.append(", LimitSanctioned, ReservedLimit, UtilisedLimit, LimitCheck, LimitChkMethod");
		sql.append(" From LimitLines_View");
		sql.append(" Where LimitHeaderId = :HeaderId");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);

		RowMapper<LimitDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitDetails.class);
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	/**
	 * 
	 * @param limitDetailsList
	 * @param type
	 */
	@Override
	public void updateReserveUtiliseList(List<LimitDetails> limitDetailsList, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Update LimitDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" set ReservedLimit = ?, UtilisedLimit = ?, OsPriBal = ?, NonRvlUtilised = ?, LimitSanctioned = ?");
		sql.append(" Where DetailId = ?");

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				LimitDetails ld = limitDetailsList.get(i);

				int index = 1;

				ps.setBigDecimal(index++, ld.getReservedLimit());
				ps.setBigDecimal(index++, ld.getUtilisedLimit());
				ps.setBigDecimal(index++, ld.getOsPriBal());
				ps.setBigDecimal(index++, ld.getNonRvlUtilised());
				ps.setBigDecimal(index++, ld.getLimitSanctioned());
				ps.setLong(index, ld.getDetailId());
			}

			@Override
			public int getBatchSize() {
				return limitDetailsList.size();
			}
		});
		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param limitDetailsLists
	 * @param type
	 * @return
	 */
	@Override
	public void saveList(List<LimitDetails> limitDetails, String type) {
		logger.debug(Literal.ENTERING);

		for (LimitDetails limitDetail : limitDetails) {
			if (limitDetail.getId() == Long.MIN_VALUE) {
				limitDetail.setId(getNextValue("SeqLimitDetails"));
			}
		}

		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" LimitDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(DetailId, LimitHeaderId, LimitStructureDetailsID, ExpiryDate, Revolving, LimitSanctioned");
		sql.append(", ReservedLimit, UtilisedLimit, NonRvlUtilised, LimitCheck, LimitChkMethod, Version");
		sql.append(", CreatedBy, CreatedOn, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, bankingArrangement, limitCondition");
		sql.append(", externalRef, externalRef1, tenor, osPriBal");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				LimitDetails ld = limitDetails.get(i);

				int index = 1;

				ps.setLong(index++, ld.getDetailId());
				ps.setLong(index++, ld.getLimitHeaderId());
				ps.setLong(index++, ld.getLimitStructureDetailsID());
				ps.setDate(index++, JdbcUtil.getDate(ld.getExpiryDate()));
				ps.setBoolean(index++, ld.isRevolving());
				ps.setBigDecimal(index++, ld.getLimitSanctioned());
				ps.setBigDecimal(index++, ld.getReservedLimit());
				ps.setBigDecimal(index++, ld.getUtilisedLimit());
				ps.setBigDecimal(index++, ld.getNonRvlUtilised());
				ps.setBoolean(index++, ld.isLimitCheck());
				ps.setString(index++, ld.getLimitChkMethod());
				ps.setInt(index++, ld.getVersion());
				ps.setLong(index++, ld.getCreatedBy());
				ps.setTimestamp(index++, ld.getCreatedOn());
				ps.setLong(index++, ld.getLastMntBy());
				ps.setTimestamp(index++, ld.getLastMntOn());
				ps.setString(index++, ld.getRecordStatus());
				ps.setString(index++, ld.getRoleCode());
				ps.setString(index++, ld.getNextRoleCode());
				ps.setString(index++, ld.getTaskId());
				ps.setString(index++, ld.getNextTaskId());
				ps.setString(index++, ld.getRecordType());
				ps.setLong(index++, ld.getWorkflowId());
				ps.setString(index++, ld.getBankingArrangement());
				ps.setString(index++, ld.getLimitCondition());
				ps.setString(index++, ld.getExternalRef());
				ps.setString(index++, ld.getExternalRef1());
				ps.setInt(index++, ld.getTenor());
				ps.setBigDecimal(index, ld.getOsPriBal());
			}

			@Override
			public int getBatchSize() {
				return limitDetails.size();
			}
		});

		logger.debug(Literal.LEAVING);
	}

	@Override
	public Map<String, BigDecimal> getOsPriBal(long id) {
		Map<String, BigDecimal> hashMap = new HashMap<>();

		String sql = "Select FinReference, TotalPriBal From FinPFTDetails Where CustID = ? and FinIsActive = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			this.jdbcOperations.query(sql, new ResultSetExtractor<Map<String, BigDecimal>>() {
				@Override
				public Map<String, BigDecimal> extractData(ResultSet rs) throws SQLException, DataAccessException {

					while (rs.next()) {
						hashMap.put(rs.getString("FinReference"), rs.getBigDecimal("TotalPriBal"));
					}
					return hashMap;
				}
			}, id, 1);

		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return hashMap;
	}

	@Override
	public BigDecimal getOsPriBal(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select TotalPriBal");
		sql.append(" From FinPFTDetails");
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public int getLimitHeaderIDByCustId(long customerId) {
		logger.debug(Literal.ENTERING);

		LimitHeader limitHeader = new LimitHeader();
		limitHeader.setCustomerId(customerId);
		StringBuilder sql = new StringBuilder();
		sql.append("select HeaderId");
		sql.append(" From LimitHeader");
		sql.append(" Where CustomerId = :CustomerId");

		logger.debug("selectSql: " + sql.toString());

		try {
			SqlParameterSource beanParams = new BeanPropertySqlParameterSource(limitHeader);
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParams, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public BigDecimal getSanctionedAmtByCustId(long customerId, String type) {
		StringBuilder sql = new StringBuilder("Select LimitSanctioned From LimitDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where limitline like ? and LimitHeaderId in (Select HeaderId from LimitHeader");
		sql.append(" Where CustomerId = ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, "%OD%", customerId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}
}
