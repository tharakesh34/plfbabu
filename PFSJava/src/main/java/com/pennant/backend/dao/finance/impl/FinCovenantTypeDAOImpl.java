package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.FinCovenantTypeDAO;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class FinCovenantTypeDAOImpl extends BasicDao<FinCovenantType> implements FinCovenantTypeDAO {
	private static Logger logger = LogManager.getLogger(FinCovenantTypeDAOImpl.class);

	public FinCovenantTypeDAOImpl() {
		super();
	}

	@Override
	public FinCovenantType getFinCovenantTypeById(FinCovenantType fct, String type) {
		StringBuilder sql = sqlSelectQuery(type, false);
		sql.append(" Where FinReference = ? and CovenantType = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FinCovenantTypeRowMapper(type, false),
					fct.getFinReference(), fct.getCovenantType());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinCovenantType> getFinCovenantTypeByFinRef(String finReference, String type, boolean isEnquiry) {
		StringBuilder sql = sqlSelectQuery(type, isEnquiry);
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, finReference);
		}, new FinCovenantTypeRowMapper(type, isEnquiry));
	}

	@Override
	public void delete(FinCovenantType fct, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinCovenantType");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ? and CovenantType = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, fct.getFinReference());
				ps.setString(index, fct.getCovenantType());
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(FinCovenantType fct, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinCovenantType");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(CovenantType, Description, MandRole, AlwWaiver");
		sql.append(", AlwPostpone, PostponeDays, ReceivableDate, AlwOtc, InternalUse");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fct.getFinReference());
			ps.setString(index++, fct.getCovenantType());
			ps.setString(index++, fct.getDescription());
			ps.setString(index++, fct.getMandRole());
			ps.setBoolean(index++, fct.isAlwWaiver());
			ps.setBoolean(index++, fct.isAlwPostpone());
			ps.setInt(index++, fct.getPostponeDays());
			ps.setDate(index++, JdbcUtil.getDate(fct.getReceivableDate()));
			ps.setBoolean(index++, fct.isAlwOtc());
			ps.setBoolean(index++, fct.isInternalUse());
			ps.setInt(index++, fct.getVersion());
			ps.setLong(index++, fct.getLastMntBy());
			ps.setTimestamp(index++, fct.getLastMntOn());
			ps.setString(index++, fct.getRecordStatus());
			ps.setString(index++, fct.getRoleCode());
			ps.setString(index++, fct.getNextRoleCode());
			ps.setString(index++, fct.getTaskId());
			ps.setString(index++, fct.getNextTaskId());
			ps.setString(index++, fct.getRecordType());
			ps.setLong(index, fct.getWorkflowId());
		});

		return fct.getId();
	}

	@Override
	public void update(FinCovenantType fct, String type) {
		StringBuilder sql = new StringBuilder("Update FinCovenantType");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Description = ?, MandRole = ?, AlwWaiver = ?, AlwPostpone = ?, PostponeDays = ?");
		sql.append(", ReceivableDate = ?, AlwOtc = ?, InternalUse = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append("  Where FinReference = ? and CovenantType = ?");

		if (!type.endsWith("_Temp")) {
			sql.append("  and Version = ? - 1");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fct.getDescription());
			ps.setString(index++, fct.getMandRole());
			ps.setBoolean(index++, fct.isAlwWaiver());
			ps.setBoolean(index++, fct.isAlwPostpone());
			ps.setInt(index++, fct.getPostponeDays());
			ps.setDate(index++, JdbcUtil.getDate(fct.getReceivableDate()));
			ps.setBoolean(index++, fct.isAlwOtc());
			ps.setBoolean(index++, fct.isInternalUse());
			ps.setInt(index++, fct.getVersion());
			ps.setLong(index++, fct.getLastMntBy());
			ps.setTimestamp(index++, fct.getLastMntOn());
			ps.setString(index++, fct.getRecordStatus());
			ps.setString(index++, fct.getRoleCode());
			ps.setString(index++, fct.getNextRoleCode());
			ps.setString(index++, fct.getTaskId());
			ps.setString(index++, fct.getNextTaskId());
			ps.setString(index++, fct.getRecordType());
			ps.setLong(index++, fct.getWorkflowId());

			ps.setString(index++, fct.getFinReference());
			ps.setString(index++, fct.getCovenantType());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, fct.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteByFinRef(String finReference, String tableType) {
		StringBuilder sql = new StringBuilder("Delete From FinCovenantType");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, finReference);
		});
	}

	@Override
	public FinCovenantType getCovenantTypeById(String finReference, String covenantType, String type) {
		StringBuilder sql = sqlSelectQuery(type, false);
		sql.append(" Where FinReference = ? and CovenantType = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FinCovenantTypeRowMapper(type, false),
					finReference, covenantType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinCovenantType> getFinCovenantDocTypeByFinRef(String finReferece, String type, boolean isEnquiry) {
		StringBuilder sql = sqlSelectQuery(type, isEnquiry);
		sql.append(" Where FinReference = ?");
		sql.append(" and FinReference not in (Select ReferenceId From DocumentDetails");
		sql.append(" Where FinReference = ReferenceId and CovenantType = DocCategory) ");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new FinCovenantTypeRowMapper(type, isEnquiry), finReferece);
	}

	@Override
	public boolean isExists(FinCovenantType fct, String tableType) {
		StringBuilder sql = new StringBuilder("Select Count(FinID) From FinCovenantType");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where FinReference = ? and CovenantType = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, fct.getFinReference(),
				fct.getCovenantType()) > 0;
	}

	@Override
	public DocumentType isCovenantTypeExists(String covenantType) {
		String sql = "Select DocTypeCode, DocTypeDesc From BMTDocumentTypes_AView Where categorycode In (?, ?) And DocTypeCode = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, num) -> {
				DocumentType dt = new DocumentType();

				dt.setDocTypeCode(rs.getString("DocTypeCode"));
				dt.setDocTypeDesc(rs.getString("DocTypeDesc"));

				return dt;
			}, "FINANCE", "COLLATERAL", covenantType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public SecurityRole isMandRoleExists(String mandRole, String[] allowedRoles) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" RoleCd, RoleDesc");
		sql.append(" From SecRoles");
		sql.append(" Where RoleCd = ?");

		Object[] obj = new Object[] { mandRole };

		if (ArrayUtils.isNotEmpty(allowedRoles)) {
			sql.append(" and RoleCd In (");
			for (int i = 0; i < allowedRoles.length; i++) {
				sql.append(" ?,");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");

			obj = new Object[] { mandRole, Arrays.asList(allowedRoles) };
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				SecurityRole sr = new SecurityRole();

				sr.setRoleCd(rs.getString(1));
				sr.setRoleDesc(rs.getString(2));

				return sr;
			}, obj);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<DocumentType> getPddOtcList() {
		String sql = "Select DocTypeCode, Pdd, Otc From BMTdocumentTypes Where Pdd = ? or Otc = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.query(sql, ps -> {
			int index = 1;

			ps.setInt(index++, 1);
			ps.setInt(index, 1);
		}, (rs, num) -> {
			DocumentType dt = new DocumentType();

			dt.setDocTypeCode(rs.getString("DocTypeCode"));
			dt.setPdd(rs.getBoolean("Pdd"));
			dt.setOtc(rs.getBoolean("Otc"));

			return dt;
		});
	}

	private StringBuilder sqlSelectQuery(String type, boolean isEnquiry) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, CovenantType, Description, MandRole, AlwWaiver");
		sql.append(", AlwPostpone, PostponeDays, ReceivableDate, AlwOtc, InternalUse");

		if (isEnquiry) {
			sql.append(", CovenantTypeDesc, DocReceivedDate");
		} else {
			if (StringUtils.trimToEmpty(type).contains("View")) {
				sql.append(", CovenantTypeDesc, MandRoleDesc, PddFlag, OtcFlag, CategoryCode");
			}
		}
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinCovenantType");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FinCovenantTypeRowMapper implements RowMapper<FinCovenantType> {
		private String type;
		private boolean isEnquiry;

		private FinCovenantTypeRowMapper(String type, boolean isEnquiry) {
			this.type = type;
			this.isEnquiry = isEnquiry;
		}

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
		}
	}
}