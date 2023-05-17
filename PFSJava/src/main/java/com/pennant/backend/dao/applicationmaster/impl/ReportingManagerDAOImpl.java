package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.ReportingManagerDAO;
import com.pennant.backend.model.administration.ReportingManager;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class ReportingManagerDAOImpl extends SequenceDao<ReportingManager> implements ReportingManagerDAO {

	private static Logger logger = LogManager.getLogger(ReportingManagerDAOImpl.class);

	public ReportingManagerDAOImpl() {
		super();
	}

	@Override
	public ReportingManager getReportingManager(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getReportingManagerQuery(type);
		sql.append(" Where id = :id");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		RowMapper<ReportingManager> rowMapper = BeanPropertyRowMapper.newInstance(ReportingManager.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<ReportingManager> getReportingManagers(long usrid, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getReportingManagerQuery(type);
		sql.append(" Where usrid = :usrid");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("usrid", usrid);

		RowMapper<ReportingManager> rowMapper = BeanPropertyRowMapper.newInstance(ReportingManager.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

	private StringBuilder getReportingManagerQuery(String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("select id, usrid, businessvertical, product, fintype, branch, reportingto");
		if ("_view".equalsIgnoreCase(type)) {
			sql.append(
					", businessVerticalCode, businessVerticalDesc, productDesc, finTypeDesc,branchDesc, reportingToUserName");
		}
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From secusr_reporting_managers");
		sql.append(type);
		return sql;
	}

	@Override
	public String save(ReportingManager reportingManager, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("insert into secusr_reporting_managers");
		sql.append(tableType);
		sql.append("(id, usrid, businessvertical, product, finType, branch, reportingto");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append("(:id, :userId, :businessVertical, :product, :finType, :branch, :reportingTo");
		sql.append(", :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (reportingManager.getId() == Long.MIN_VALUE) {
			reportingManager.setId(getNextValue("SeqReportingManager"));
			logger.debug("get NextID:" + reportingManager.getId());
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(reportingManager);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(reportingManager.getId());
	}

	@Override
	public void update(ReportingManager reportingManager, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("update secusr_reporting_managers");
		sql.append(tableType.getSuffix());
		sql.append("  set businessvertical = :businessVertical, product = :product, fintype = :finType");
		sql.append(", branch = :branch, reportingto = :reportingTo");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(reportingManager);

		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(ReportingManager reportingManager, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "usrid = :userId and businessvertical = :businessVertical and productcode = :productcode and fintype= :finType and branchcode= :branchcode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("secusr_reporting_managers", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("secusr_reporting_managers_temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(
					new String[] { "secusr_reporting_managers_temp", "secusr_reporting_managers" }, whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("usrId", reportingManager.getUserId());
		paramSource.addValue("businessVertical", reportingManager.getBusinessVertical());
		paramSource.addValue("product", reportingManager.getProduct());
		paramSource.addValue("fintype", reportingManager.getFinType());
		paramSource.addValue("branch", reportingManager.getBranch());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public void deleteByUserId(long usrID, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from secusr_reporting_managers");
		sql.append(tableType.getSuffix());
		sql.append(" where usrid = :usrId ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("usrId", usrID);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (Exception e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteById(long ID, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from secusr_reporting_managers");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", ID);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (Exception e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

}
