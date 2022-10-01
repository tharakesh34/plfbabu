package com.pennant.backend.dao.finance.putcall.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.putcall.FinOptionDAO;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

public class FinOptionDAOImpl extends SequenceDao<FinOption> implements FinOptionDAO {
	private static Logger logger = LogManager.getLogger(FinOptionDAOImpl.class);

	@Override
	public String save(FinOption fo, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into Fin_Options");
		sql.append(tableType.getSuffix());
		sql.append("(Id, FinID, FinReference, OptionType, CurrentOptionDate, Frequency, NoticePeriodDays");
		sql.append(", AlertDays, OptionExercise, NextOptionDate, AlertType, AlertToRoles");
		sql.append(", UserTemplate, CustomerTemplate, Remarks");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values");
		sql.append("( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (fo.getId() == Long.MIN_VALUE) {
			fo.setId(getNextValue("SEQ_FIN_OPTIONS"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, fo.getId());
				ps.setLong(index++, fo.getFinID());
				ps.setString(index++, fo.getFinReference());
				ps.setString(index++, fo.getOptionType());
				ps.setDate(index++, JdbcUtil.getDate(fo.getCurrentOptionDate()));
				ps.setString(index++, fo.getFrequency());
				ps.setInt(index++, fo.getNoticePeriodDays());
				ps.setInt(index++, fo.getAlertDays());
				ps.setBoolean(index++, fo.isOptionExercise());
				ps.setDate(index++, JdbcUtil.getDate(fo.getNextOptionDate()));
				ps.setString(index++, fo.getAlertType());
				ps.setString(index++, fo.getAlertToRoles());
				ps.setObject(index++, fo.getUserTemplate());
				ps.setObject(index++, fo.getCustomerTemplate());
				ps.setString(index++, fo.getRemarks());
				ps.setInt(index++, fo.getVersion());
				ps.setLong(index++, fo.getLastMntBy());
				ps.setTimestamp(index++, fo.getLastMntOn());
				ps.setString(index++, fo.getRecordStatus());
				ps.setString(index++, fo.getRoleCode());
				ps.setString(index++, fo.getNextRoleCode());
				ps.setString(index++, fo.getTaskId());
				ps.setString(index++, fo.getNextTaskId());
				ps.setString(index++, fo.getRecordType());
				ps.setLong(index, fo.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(fo.getId());
	}

	@Override
	public void update(FinOption fo, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Fin_options");
		sql.append(tableType.getSuffix());
		sql.append(" Set CurrentOptionDate = ?, Frequency = ?, NoticePeriodDays = ?, AlertDays = ?");
		sql.append(", OptionExercise = ?, NextOptionDate = ?, AlertType = ?, AlertToRoles = ?, UserTemplate = ?");
		sql.append(", CustomerTemplate = ?, Remarks = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(fo.getCurrentOptionDate()));
			ps.setString(index++, fo.getFrequency());
			ps.setInt(index++, fo.getNoticePeriodDays());
			ps.setInt(index++, fo.getAlertDays());
			ps.setBoolean(index++, fo.isOptionExercise());
			ps.setDate(index++, JdbcUtil.getDate(fo.getNextOptionDate()));
			ps.setString(index++, fo.getAlertType());
			ps.setString(index++, fo.getAlertToRoles());
			ps.setObject(index++, fo.getUserTemplate());
			ps.setObject(index++, fo.getCustomerTemplate());
			ps.setString(index++, fo.getRemarks());
			ps.setTimestamp(index++, fo.getLastMntOn());
			ps.setString(index++, fo.getRecordStatus());
			ps.setString(index++, fo.getRoleCode());
			ps.setString(index++, fo.getNextRoleCode());
			ps.setString(index++, fo.getTaskId());
			ps.setString(index++, fo.getNextTaskId());
			ps.setString(index++, fo.getRecordType());
			ps.setLong(index++, fo.getWorkflowId());

			ps.setLong(index, fo.getId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(FinOption fo, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From Fin_Options");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index, fo.getId());
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public FinOption getFinOption(long id, TableType tableType) {
		StringBuilder sql = getSelectQuery(tableType);
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinOptionRowMapper rowMapper = new FinOptionRowMapper(tableType);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinOption> getPutCallAlertList() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Distinct fo.Id, fo.FinID, fo.FinReference, fo.CurrentOptionDate, fo.AlertDays");
		sql.append(", fo.NoticePeriodDays, foa.AlertSentOn, ut.TemplateCode UserTemplateCode");
		sql.append(", cust.TemplateCode CustomerTemplateCode, fo.AlertToRoles, fo.OptionType");
		sql.append(", fo.Frequency, fo.NextOptionDate, fo.AlertType, fpd.TotalPriBal, fpd.PenaltyPaid");
		sql.append(", fpd.PftAmz, fpd.TdSchdPftBal, fpd.PftAccrued, fpd.PenaltyDue, fpd.PenaltyWaived");
		sql.append(" From Fin_Options fo");
		sql.append(" Inner Join FinPftDetails fpd on fpd.FinID = fo.FinID");
		sql.append(" Left Join (Select FinOptiontId, max(AlertSentOn) AlertSentOn From Fin_Option_Alerts");
		sql.append(" group by FinOptiontId) foa on foa.FinOptiontId = fo.Id");
		sql.append(" Left Join Templates ut on ut.TemplateId = fo.UserTemplate");
		sql.append(" Left Join Templates Cust on Cust.TemplateId = fo.CustomerTemplate");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, num) -> {
			FinOption fp = new FinOption();

			fp.setId(rs.getLong("Id"));
			fp.setFinID(rs.getLong("FinID"));
			fp.setFinReference(rs.getString("FinReference"));
			fp.setCurrentOptionDate(rs.getDate("CurrentOptionDate"));
			fp.setAlertDays(rs.getInt("AlertDays"));
			fp.setNoticePeriodDays(rs.getInt("NoticePeriodDays"));
			fp.setAlertsentOn(rs.getDate("AlertSentOn"));
			fp.setUserTemplateCode(rs.getString("UserTemplateCode"));
			fp.setCustomerTemplateCode(rs.getString("CustomerTemplateCode"));
			fp.setAlertToRoles(rs.getString("AlertToRoles"));
			fp.setOptionType(rs.getString("OptionType"));
			fp.setFrequency(rs.getString("Frequency"));
			fp.setNextOptionDate(rs.getDate("NextOptionDate"));
			fp.setAlertType(rs.getString("AlertType"));
			fp.setTotalPriBal(rs.getBigDecimal("TotalPriBal"));
			fp.setPenaltyPaid(rs.getBigDecimal("PenaltyPaid"));
			fp.setPftAmz(rs.getBigDecimal("PftAmz"));
			fp.setTdSchdPftBal(rs.getBigDecimal("TdSchdPftBal"));
			fp.setPftAccrued(rs.getBigDecimal("PftAccrued"));
			fp.setPenaltyDue(rs.getBigDecimal("PenaltyDue"));
			fp.setPenaltyWaived(rs.getBigDecimal("PenaltyWaived"));

			return fp;
		});
	}

	@Override
	public List<FinOption> getFinOptions(long finID, TableType tableType) {
		StringBuilder sql = getSelectQuery(tableType);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinOptionRowMapper rowMapper = new FinOptionRowMapper(tableType);

		return jdbcOperations.query(sql.toString(), rowMapper, finID);
	}

	@Override
	public void deleteByFinRef(long finID, String tableType) {
		StringBuilder sql = new StringBuilder("Delete From FIN_OPTIONS");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, finID);
		});
	}

	private StringBuilder getSelectQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, FinReference, OptionType, CurrentOptionDate, Frequency, NoticePeriodDays");
		sql.append(", AlertDays, OptionExercise, NextOptionDate, AlertType");
		sql.append(", AlertToRoles, UserTemplate, CustomerTemplate, Remarks");

		if (tableType.getSuffix().contains("View")) {
			sql.append(", CustomerTemplateCode, UserTemplateCode");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Fin_Options");
		sql.append(tableType.getSuffix());

		return sql;
	}

	private class FinOptionRowMapper implements RowMapper<FinOption> {
		private TableType tableType;

		private FinOptionRowMapper(TableType tableType) {
			this.tableType = tableType;
		}

		@Override
		public FinOption mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinOption fo = new FinOption();

			fo.setId(rs.getLong("Id"));
			fo.setFinID(rs.getLong("FinID"));
			fo.setFinReference(rs.getString("FinReference"));
			fo.setOptionType(rs.getString("OptionType"));
			fo.setCurrentOptionDate(rs.getDate("CurrentOptionDate"));
			fo.setFrequency(rs.getString("Frequency"));
			fo.setNoticePeriodDays(rs.getInt("NoticePeriodDays"));
			fo.setAlertDays(rs.getInt("AlertDays"));
			fo.setOptionExercise(rs.getBoolean("OptionExercise"));
			fo.setNextOptionDate(rs.getDate("NextOptionDate"));
			fo.setAlertType(rs.getString("AlertType"));
			fo.setAlertToRoles(rs.getString("AlertToRoles"));
			fo.setUserTemplate(JdbcUtil.getLong(rs.getObject("UserTemplate")));
			fo.setCustomerTemplate(JdbcUtil.getLong(rs.getObject("CustomerTemplate")));
			fo.setRemarks(rs.getString("Remarks"));

			if (tableType.getSuffix().contains("View")) {
				fo.setCustomerTemplateCode(rs.getString("CustomerTemplateCode"));
				fo.setUserTemplateCode(rs.getString("UserTemplateCode"));
			}

			fo.setVersion(rs.getInt("Version"));
			fo.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fo.setLastMntBy(rs.getLong("LastMntBy"));
			fo.setRecordStatus(rs.getString("RecordStatus"));
			fo.setRoleCode(rs.getString("RoleCode"));
			fo.setNextRoleCode(rs.getString("NextRoleCode"));
			fo.setTaskId(rs.getString("TaskId"));
			fo.setNextTaskId(rs.getString("NextTaskId"));
			fo.setRecordType(rs.getString("RecordType"));
			fo.setWorkflowId(rs.getLong("WorkflowId"));

			return fo;
		}
	}
}
