package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinCovenantTypeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class FinCovenantTypeDAOImpl extends BasicDao<FinCovenantType> implements FinCovenantTypeDAO {
	private static Logger logger = LogManager.getLogger(FinCovenantTypeDAOImpl.class);

	public FinCovenantTypeDAOImpl() {
		super();
	}

	@Override
	public FinCovenantType getFinCovenantType() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinCovenantType");
		FinCovenantType finCovenantType = new FinCovenantType();
		if (workFlowDetails != null) {
			finCovenantType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return finCovenantType;
	}

	@Override
	public FinCovenantType getNewFinCovenantType() {
		logger.debug("Entering");
		FinCovenantType finCovenantType = getFinCovenantType();
		finCovenantType.setNewRecord(true);
		logger.debug("Leaving");
		return finCovenantType;
	}

	@Override
	public FinCovenantType getFinCovenantTypeById(FinCovenantType finCovenantType, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				"Select FinReference, CovenantType, Description, MandRole, AlwWaiver, AlwPostpone, PostponeDays,ReceivableDate,AlwOtc, InternalUse,");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("CovenantTypeDesc,");
		}
		selectSql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" From FinCovenantType");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference and CovenantType = :CovenantType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		RowMapper<FinCovenantType> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinCovenantType.class);

		try {
			finCovenantType = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finCovenantType = null;
		}
		logger.debug("Leaving");
		return finCovenantType;
	}

	@Override
	public List<FinCovenantType> getFinCovenantTypeByFinRef(final String id, String type, boolean isEnquiry) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, CovenantType, Description, MandRole, AlwWaiver, AlwPostpone, PostponeDays");
		sql.append(", ReceivableDate, AlwOtc, InternalUse");

		if (isEnquiry) {
			sql.append(", CovenantTypeDesc, DocReceivedDate");
		} else {

			if (StringUtils.trimToEmpty(type).contains("View")) {
				sql.append(", CovenantTypeDesc, MandRoleDesc, PddFlag, OtcFlag, CategoryCode");
			}
		}
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from FinCovenantType");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, id);
		}, (rs, rowNum) -> {
			FinCovenantType fct = new FinCovenantType();

			fct.setFinReference(rs.getString("FinReference"));
			fct.setCovenantType(rs.getString("CovenantType"));
			fct.setDescription(rs.getString("Description"));
			fct.setMandRole(rs.getString("MandRole"));
			fct.setAlwWaiver(rs.getBoolean("AlwWaiver"));
			fct.setAlwPostpone(rs.getBoolean("AlwPostpone"));
			fct.setPostponeDays(rs.getInt("PostponeDays"));
			fct.setReceivableDate(rs.getTimestamp("ReceivableDate"));
			fct.setAlwOtc(rs.getBoolean("AlwOtc"));
			fct.setInternalUse(rs.getBoolean("InternalUse"));

			if (isEnquiry) {
				fct.setCovenantTypeDesc(rs.getString("CovenantTypeDesc"));
				fct.setDocReceivedDate(rs.getTimestamp("DocReceivedDate"));
			} else {

				if (StringUtils.trimToEmpty(type).contains("View")) {
					fct.setCovenantTypeDesc(rs.getString("CovenantTypeDesc"));
					fct.setMandRoleDesc(rs.getString("MandRoleDesc"));
					fct.setPddFlag(rs.getBoolean("PddFlag"));
					fct.setOtcFlag(rs.getBoolean("OtcFlag"));
					fct.setCategoryCode(rs.getString("CategoryCode"));
				}
			}
			fct.setVersion(rs.getInt("Version"));
			fct.setLastMntBy(rs.getLong("LastMntBy"));
			fct.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fct.setRecordStatus(rs.getString("RecordStatus"));
			fct.setRoleCode(rs.getString("RoleCode"));
			fct.setNextRoleCode(rs.getString("NextRoleCode"));
			fct.setTaskId(rs.getString("TaskId"));
			fct.setNextTaskId(rs.getString("NextTaskId"));
			fct.setRecordType(rs.getString("RecordType"));
			fct.setWorkflowId(rs.getLong("WorkflowId"));

			return fct;
		});
	}

	@Override
	public void delete(FinCovenantType finCovenantType, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder deleteSql = new StringBuilder(" Delete From FinCovenantType");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference and CovenantType = :CovenantType");
		logger.debug(Literal.SQL + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public String save(FinCovenantType finCovenantType, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder insertSql = new StringBuilder();

		insertSql.append(" Insert Into FinCovenantType");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (FinReference, CovenantType , Description, MandRole, AlwWaiver, AlwPostpone, PostponeDays,ReceivableDate,AlwOtc, InternalUse,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values( :FinReference, :CovenantType , :Description, :MandRole, :AlwWaiver,:AlwPostpone, :PostponeDays, :ReceivableDate, :AlwOtc, :InternalUse,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
		return finCovenantType.getId();
	}

	@Override
	public void update(FinCovenantType finCovenantType, String type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);

		StringBuilder updateSql = new StringBuilder("Update FinCovenantType");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set Description = :Description,");
		updateSql.append(
				"  MandRole = :MandRole, AlwWaiver = :AlwWaiver, AlwPostpone = :AlwPostpone, PostponeDays = :PostponeDays, ReceivableDate =:ReceivableDate, AlwOtc =:AlwOtc, InternalUse = :InternalUse,");
		updateSql.append(
				"  Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append("  Where FinReference = :FinReference and CovenantType = :CovenantType");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug(Literal.SQL + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteByFinRef(String loanReference, String tableType) {
		logger.debug(Literal.ENTERING);
		FinCovenantType finCovenantType = new FinCovenantType();
		finCovenantType.setId(loanReference);

		StringBuilder deleteSql = new StringBuilder("Delete From FinCovenantType");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where FinReference = :FinReference ");
		logger.debug(Literal.SQL + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);

	}

	@Override
	public boolean isDuplicateKey(String finReference, String covenantType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "FinReference = :finReference and CovenantType = :covenantType";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("FinCovenantType", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("FinCovenantType_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "FinCovenantType_Temp", "FinCovenantType" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("finReference", finReference);
		paramSource.addValue("covenantType", covenantType);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public void delete(FinCovenantType finCovenantType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from FinCovenantType");
		sql.append(tableType.getSuffix());
		sql.append(" where FinReference = :FinReference AND CovenantType = :CovenantType");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finCovenantType);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public String save(FinCovenantType aFinCovenantType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.ENTERING);
		StringBuilder insertSql = new StringBuilder();

		insertSql.append(" Insert Into FinCovenantType");
		insertSql.append(tableType.getSuffix());
		insertSql.append(
				" (FinReference, CovenantType , Description, MandRole, AlwWaiver, AlwPostpone, PostponeDays,ReceivableDate,InternalUse,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values( :FinReference, :CovenantType , :Description, :MandRole, :AlwWaiver,:AlwPostpone, :PostponeDays, :ReceivableDate, :InternalUse,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aFinCovenantType);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
		return aFinCovenantType.getId();
	}

	@Override
	public void update(FinCovenantType aFinCovenantType, TableType tableType) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);

		StringBuilder updateSql = new StringBuilder("Update FinCovenantType");
		updateSql.append(tableType.getSuffix());
		updateSql.append("  Set Description = :Description,");
		updateSql.append(
				"  MandRole = :MandRole, AlwWaiver = :AlwWaiver, AlwPostpone = :AlwPostpone, PostponeDays = :PostponeDays, ReceivableDate =:ReceivableDate, InternalUse = :InternalUse,");
		updateSql.append(
				"  Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append("  Where FinReference = :FinReference and CovenantType = :CovenantType");

		logger.debug(Literal.SQL + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aFinCovenantType);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public FinCovenantType getCovenantTypeById(String finReference, String covenantType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, CovenantType, Description, MandRole, AlwWaiver, AlwPostpone, PostponeDays");
		sql.append(", ReceivableDate, InternalUse, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CovenantTypeDesc");
		}

		sql.append(" from FinCovenantType");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ? and CovenantType = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, covenantType },
					new RowMapper<FinCovenantType>() {
						@Override
						public FinCovenantType mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinCovenantType fct = new FinCovenantType();

							fct.setFinReference(rs.getString("FinReference"));
							fct.setCovenantType(rs.getString("CovenantType"));
							fct.setDescription(rs.getString("Description"));
							fct.setMandRole(rs.getString("MandRole"));
							fct.setAlwWaiver(rs.getBoolean("AlwWaiver"));
							fct.setAlwPostpone(rs.getBoolean("AlwPostpone"));
							fct.setPostponeDays(rs.getInt("PostponeDays"));
							fct.setReceivableDate(rs.getTimestamp("ReceivableDate"));
							fct.setInternalUse(rs.getBoolean("InternalUse"));
							fct.setVersion(rs.getInt("Version"));
							fct.setLastMntBy(rs.getLong("LastMntBy"));
							fct.setLastMntOn(rs.getTimestamp("LastMntOn"));
							fct.setRecordStatus(rs.getString("RecordStatus"));
							fct.setRoleCode(rs.getString("RoleCode"));
							fct.setNextRoleCode(rs.getString("NextRoleCode"));
							fct.setTaskId(rs.getString("TaskId"));
							fct.setNextTaskId(rs.getString("NextTaskId"));
							fct.setRecordType(rs.getString("RecordType"));
							fct.setWorkflowId(rs.getLong("WorkflowId"));

							if (StringUtils.trimToEmpty(type).contains("View")) {
								fct.setCovenantTypeDesc(rs.getString("CovenantTypeDesc"));
							}

							return fct;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<FinCovenantType> getFinCovenantDocTypeByFinRef(String id, String type, boolean isEnquiry) {
		FinCovenantType finCovenantType = new FinCovenantType();
		finCovenantType.setId(id);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select FinReference, CovenantType, Description, MandRole, AlwWaiver, AlwPostpone, PostponeDays");
		sql.append(" ,ReceivableDate,AlwOtc,InternalUse,");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append("CategoryCode, ");
		}
		if (isEnquiry) {
			sql.append(" CovenantTypeDesc,DocReceivedDate,");
		} else {
			if (StringUtils.trimToEmpty(type).contains("View")) {
				sql.append(" CovenantTypeDesc,MandRoleDesc,");
			}
		}
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId");
		sql.append(" From FinCovenantType");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference ");
		sql.append(" AND finreference not in (select referenceid  from documentdetails where finreference=referenceid");
		sql.append(" and covenanttype=doccategory) ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		RowMapper<FinCovenantType> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinCovenantType.class);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public boolean isExists(FinCovenantType finCovenantType, String tableType) {
		logger.debug(Literal.ENTERING);

		int count = 0;
		StringBuilder selectSql = new StringBuilder(" SELECT  COUNT(*)  FROM  FinCovenantType");
		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" Where FinReference = :FinReference and CovenantType = :CovenantType");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			count = 0;
		}
		logger.debug(Literal.LEAVING);
		return count > 0 ? true : false;
	}

	@Override
	public DocumentType isCovenantTypeExists(String covenantType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select DocTypeCode, DocTypeDesc  from  BMTDocumentTypes_AView");
		sql.append(" Where categorycode In ('FINANCE', 'COLLATERAL') And DocTypeCode = :DocTypeCode");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("DocTypeCode", covenantType);

		RowMapper<DocumentType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DocumentType.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), paramSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public SecurityRole isMandRoleExists(String mandRole, String[] allowedRoles) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select  RoleCd, RoleDesc  from  SecRoles");
		sql.append(" Where RoleCd In (:RoleCd) And RoleCd = :mandRole");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("RoleCd", Arrays.asList(allowedRoles));
		paramSource.addValue("mandRole", mandRole);

		RowMapper<SecurityRole> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRole.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), paramSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<DocumentType> getPddOtcList() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select  DocTypeCode, Pdd, Otc  from  BMTdocumentTypes");
		sql.append(" Where Pdd=1 OR Otc = 1");

		logger.debug(Literal.SQL + sql.toString());
		RowMapper<DocumentType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DocumentType.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;

	}

	@Override
	public SecurityRole isMandRoleExists(String mandRole) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select  RoleCd, RoleDesc  from  SecRoles");
		sql.append(" Where RoleCd = :mandRole");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("mandRole", mandRole);

		RowMapper<SecurityRole> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRole.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), paramSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

}