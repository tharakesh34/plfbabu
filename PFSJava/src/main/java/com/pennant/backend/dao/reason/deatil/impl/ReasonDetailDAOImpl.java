package com.pennant.backend.dao.reason.deatil.impl;

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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.reason.details.ReasonDetails;
import com.pennant.backend.model.reason.details.ReasonDetailsLog;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ReasonDetailDAOImpl extends SequenceDao<ReasonHeader> implements ReasonDetailDAO {
	private static Logger logger = LogManager.getLogger(ReasonDetailDAOImpl.class);

	public ReasonDetailDAOImpl() {
		super();
	}

	@Override
	public long save(ReasonHeader reasonHeader) {
		logger.debug(Literal.ENTERING);
		try {
			long id = saveHeader(reasonHeader);
			List<ReasonDetails> details = reasonHeader.getDetailsList();

			if (details != null && !details.isEmpty()) {
				for (ReasonDetails reasonDetails : details) {
					reasonDetails.setHeaderId(id);
				}
				saveDetails(details);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

		return reasonHeader.getId();
	}

	private long saveHeader(ReasonHeader reasonHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into  ReasonHeader ");
		sql.append(" (Id, Module, Reference, Remarks, Rolecode, Activity, ToUser, LogTime)");
		sql.append(" Values(:Id, :Module, :Reference, :Remarks, :RoleCode, :Activity, :ToUser, :LogTime)");
		logger.debug("insertSql: " + sql.toString());

		if (reasonHeader.getId() == Long.MIN_VALUE) {
			reasonHeader.setId(getNextValue("SeqReasonHeader"));
		}

		try {
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reasonHeader);
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
		return reasonHeader.getId();
	}

	private void saveDetails(List<ReasonDetails> detailsList) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into  ReasonDetails");
		sql.append("(HeaderId, ReasonId)");
		sql.append(" Values( :HeaderId, :ReasonId)");
		logger.trace(Literal.SQL + sql.toString());

		try {
			SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(detailsList.toArray());
			this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<ReasonDetailsLog> getReasonDetailsLog(String reference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select RH.Module, RH.Reference, RH.Remarks, RH.Rolecode, SR.RoleDesc");
		sql.append(", RH.Activity, RC.Code, RC.Description, RH.Touser, SU.UsrLogin, SU.Usrfname");
		sql.append(", SU.Usrmname ,SU.Usrlname , RH.Logtime, RS.Description as rejectReasonDesc");
		sql.append(" from ReasonHeader RH");
		sql.append(" inner join ReasonDetails RD ON RH.ID = RD.HeaderId");
		sql.append(" inner join Reasons RS ON RS.ID = RD.ReasonID");
		sql.append(" inner join ReasonCategory RC ON RC.ID = RS.ReasonCategoryId");
		sql.append(" inner join ReasonTypes RT ON RT.ID = RS.ReasonTypeID ");
		sql.append(" inner join SecUsers SU ON SU.UsrId = RH.Touser");
		sql.append(" inner join SecRoles SR ON SR.RoleCd = RH.Rolecode");
		sql.append(" Where RH.Reference = :Reference");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);

		try {
			RowMapper<ReasonDetailsLog> mapper = ParameterizedBeanPropertyRowMapper.newInstance(ReasonDetailsLog.class);
			return this.jdbcTemplate.query(sql.toString(), source, mapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<ReasonHeader> getCancelReasonDetails(String reference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select RD.reasonid, RH.remarks");
		sql.append(" From ReasonHeader RH  ");
		sql.append(" left join ReasonDetails RD ON RH.ID = RD.HeaderId ");
		sql.append(" Where RH.Reference=:Reference");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);

		RowMapper<ReasonHeader> mapper = ParameterizedBeanPropertyRowMapper.newInstance(ReasonHeader.class);

		try {
			return jdbcTemplate.query(sql.toString(), source, mapper);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean isreasonCodeExists(long reasonCode) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reasonid", reasonCode);

		StringBuilder sql = new StringBuilder("SELECT COUNT(reasonid)");
		sql.append(" From ReasonDetails ");
		sql.append(" Where reasonid=:reasonid");

		logger.trace(Literal.SQL + sql.toString());
		int rcdCount = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);

		logger.debug(Literal.LEAVING);
		return rcdCount > 0 ? true : false;
	}

	public void deleteReasonDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		ReasonHeader reference = new ReasonHeader();
		reference.setReference(finReference);

		StringBuilder sql = new StringBuilder("delete from ReasonDetails");
		sql.append(" where headerId In(select Id from ReasonHeader where Reference = :Reference)");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Reference", finReference);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteCancelReasonDetails(String finReference) {
		deleteReasonDetails(finReference);

		ReasonHeader reference = new ReasonHeader();
		reference.setReference(finReference);

		StringBuilder sql = new StringBuilder("delete from ReasonHeader");
		sql.append(" where reference=:reference");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("reference", finReference);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public ReasonCode getCancelReasonByCode(String code, String type) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Select Id, ReasonTypeId, ReasonCategoryId, Code");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", ReasonCategoryCode, ReasonTypeCode");
		}

		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Reasons");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Code = :Code");
		sql.append(" and ReasonCategoryCode = :ReasonCategoryCode and ReasonTypeCode = :ReasonTypeCode");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Code", code);
		paramSource.addValue("ReasonCategoryCode", PennantConstants.LOAN_CANCEL);
		paramSource.addValue("ReasonTypeCode", PennantConstants.LOAN_CANCEL);
		RowMapper<ReasonCode> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReasonCode.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

}
