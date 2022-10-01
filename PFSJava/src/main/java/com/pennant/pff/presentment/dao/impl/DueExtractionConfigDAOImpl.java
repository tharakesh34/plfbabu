package com.pennant.pff.presentment.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.pff.presentment.dao.DueExtractionConfigDAO;
import com.pennant.pff.presentment.model.DueExtractionConfig;
import com.pennant.pff.presentment.model.DueExtractionHeader;
import com.pennant.pff.presentment.model.InstrumentTypes;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class DueExtractionConfigDAOImpl extends SequenceDao<InstrumentTypes> implements DueExtractionConfigDAO {

	public DueExtractionConfigDAOImpl() {
		super();
	}

	@Override
	public long save(InstrumentTypes instrType, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into Instrument_Types");
		sql.append(tableType.getSuffix());
		sql.append(" (ID, Code, Description, Internal, Enabled, AutoExtraction, ExtractionDays");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", Active, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkFlowId)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, instrType.getID());
				ps.setString(index++, instrType.getCode());
				ps.setString(index++, instrType.getDescription());
				ps.setBoolean(index++, instrType.isInternal());
				ps.setBoolean(index++, instrType.isEnabled());
				ps.setBoolean(index++, instrType.isAutoExtraction());
				ps.setInt(index++, instrType.getExtractionDays());
				ps.setInt(index++, instrType.getVersion());
				ps.setLong(index++, instrType.getCreatedBy());
				ps.setTimestamp(index++, instrType.getCreatedOn());
				ps.setLong(index++, instrType.getApprovedBy());
				ps.setTimestamp(index++, instrType.getApprovedOn());
				ps.setLong(index++, instrType.getLastMntBy());
				ps.setTimestamp(index++, instrType.getLastMntOn());
				ps.setBoolean(index++, instrType.getActive());
				ps.setString(index++, instrType.getRecordStatus());
				ps.setString(index++, instrType.getRoleCode());
				ps.setString(index++, instrType.getNextRoleCode());
				ps.setString(index++, instrType.getTaskId());
				ps.setString(index++, instrType.getNextTaskId());
				ps.setString(index++, instrType.getRecordType());
				ps.setLong(index++, instrType.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return instrType.getID();
	}

	@Override
	public boolean isConfigExists() {
		String sql = "Select Count(ID) From Due_Extraction_Header";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> rs.getInt(1)) > 0;
	}

	@Override
	public long getHeaderID() {
		return getNextValue("Seq_Due_Extraction_Header");
	}

	@Override
	public void saveHeader(List<DueExtractionHeader> list, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into Due_Extraction_Header");
		sql.append(tableType.getSuffix());
		sql.append("( ID, ExtractinMonth, Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn, Active, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					DueExtractionHeader deh = list.get(i);

					int index = 1;

					ps.setLong(index++, deh.getID());
					ps.setString(index++, deh.getExtractionMonth());
					ps.setInt(index++, deh.getVersion());
					ps.setLong(index++, deh.getCreatedBy());
					ps.setTimestamp(index++, deh.getCreatedOn());
					ps.setLong(index++, deh.getApprovedBy());
					ps.setTimestamp(index++, deh.getApprovedOn());
					ps.setLong(index++, deh.getLastMntBy());
					ps.setTimestamp(index++, deh.getLastMntOn());
					ps.setBoolean(index++, deh.getActive());
					ps.setString(index++, deh.getRecordStatus());
					ps.setString(index++, deh.getRoleCode());
					ps.setString(index++, deh.getNextRoleCode());
					ps.setString(index++, deh.getTaskId());
					ps.setString(index++, deh.getNextTaskId());
					ps.setString(index++, deh.getRecordType());
					ps.setLong(index++, deh.getWorkflowId());
				}

				@Override
				public int getBatchSize() {
					return list.size();
				}
			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void save(List<DueExtractionConfig> list, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into Due_Extraction_Config");
		sql.append(tableType.getSuffix());
		sql.append("( MonthID, InstrumentID, DueDate, ExtractionDate, Modified");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn, Active, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					DueExtractionConfig dec = list.get(i);

					int index = 1;

					ps.setLong(index++, dec.getMonthID());
					ps.setLong(index++, dec.getInstrumentID());
					ps.setDate(index++, JdbcUtil.getDate(dec.getDueDate()));
					ps.setDate(index++, JdbcUtil.getDate(dec.getExtractionDate()));
					ps.setBoolean(index++, dec.isModified());
					ps.setInt(index++, dec.getVersion());
					ps.setLong(index++, dec.getCreatedBy());
					ps.setTimestamp(index++, dec.getCreatedOn());
					ps.setLong(index++, dec.getApprovedBy());
					ps.setTimestamp(index++, dec.getApprovedOn());
					ps.setLong(index++, dec.getLastMntBy());
					ps.setTimestamp(index++, dec.getLastMntOn());
					ps.setBoolean(index++, dec.getActive());
					ps.setString(index++, dec.getRecordStatus());
					ps.setString(index++, dec.getRoleCode());
					ps.setString(index++, dec.getNextRoleCode());
					ps.setString(index++, dec.getTaskId());
					ps.setString(index++, dec.getNextTaskId());
					ps.setString(index++, dec.getRecordType());
					ps.setLong(index++, dec.getWorkflowId());
				}

				@Override
				public int getBatchSize() {
					return list.size();
				}
			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void delete(InstrumentTypes instrType, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from Instrument_Types");
		sql.append(tableType.getSuffix());
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, instrType.getID());
		});
	}

	@Override
	public void deleteConfig() {
		StringBuilder sql = new StringBuilder("Delete from Due_Extraction_Config");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString());
	}

	@Override
	public List<InstrumentTypes> getInstrumentTypes() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, Code, Description, Internal, Enabled, AutoExtraction, ExtractionDays");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", Active, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" From Instrument_Types");
		sql.append(" Where AutoExtraction = ? and Enabled = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			InstrumentTypes it = new InstrumentTypes();

			it.setID(rs.getLong("Id"));
			it.setCode(rs.getString("Code"));
			it.setDescription(rs.getString("Description"));
			it.setInternal(rs.getBoolean("Internal"));
			it.setEnabled(rs.getBoolean("Enabled"));
			it.setAutoExtraction(rs.getBoolean("AutoExtraction"));
			it.setExtractionDays(rs.getInt("ExtractionDays"));
			it.setVersion(rs.getInt("Version"));
			it.setCreatedBy(rs.getLong("CreatedBy"));
			it.setCreatedOn(rs.getTimestamp("CreatedOn"));
			it.setApprovedBy(rs.getLong("ApprovedBy"));
			it.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			it.setLastMntBy(rs.getLong("LastMntBy"));
			it.setLastMntOn(rs.getTimestamp("LastMntOn"));
			it.setActive(rs.getBoolean("Active"));
			it.setRecordStatus(rs.getString("RecordStatus"));
			it.setRoleCode(rs.getString("RoleCode"));
			it.setNextRoleCode(rs.getString("NextRoleCode"));
			it.setTaskId(rs.getString("TaskId"));
			it.setNextTaskId(rs.getString("NextTaskId"));
			it.setRecordType(rs.getString("RecordType"));
			it.setWorkflowId(rs.getLong("WorkflowId"));

			return it;
		}, 1, 1);
	}

	@Override
	public Map<String, Date> getDueDates(Date extractionDate) {
		StringBuilder sql = new StringBuilder("Select it.Code, dec.DueDate");
		sql.append(" From Due_Extraction_Config dec");
		sql.append(" Inner Join Instrument_Types it on it.ID = dec.InstrumentID");
		sql.append(" Where dec.ExtractionDate = ? and it.AutoExtraction = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		Map<String, Date> map = new HashMap<>();

		return this.jdbcOperations.query(sql.toString(), (ResultSet rs) -> {
			while (rs.next()) {
				map.put(rs.getString(1), JdbcUtil.getDate(rs.getDate(2)));

			}
			return map;
		}, JdbcUtil.getDate(extractionDate), 1);
	}

	@Override
	public List<DueExtractionConfig> getConfig(long instrumentID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, MonthID, InstrumentID, DueDate, ExtractionDate, Modified");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn, Active, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" From Due_Extraction_Config");
		sql.append(" Where InstrumentID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			DueExtractionConfig pec = new DueExtractionConfig();

			pec.setID(rs.getLong("Id"));
			pec.setMonthID(rs.getLong("MonthID"));
			pec.setInstrumentID(rs.getLong("InstrumentID"));
			pec.setDueDate(rs.getDate("DueDate"));
			pec.setExtractionDate(rs.getDate("ExtractionDate"));
			pec.setModified(rs.getBoolean("Modified"));
			pec.setVersion(rs.getInt("Version"));
			pec.setCreatedBy(rs.getLong("CreatedBy"));
			pec.setCreatedOn(rs.getTimestamp("CreatedOn"));
			pec.setApprovedBy(rs.getLong("ApprovedBy"));
			pec.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			pec.setLastMntBy(rs.getLong("LastMntBy"));
			pec.setLastMntOn(rs.getTimestamp("LastMntOn"));
			pec.setActive(rs.getBoolean("Active"));
			pec.setRecordStatus(rs.getString("RecordStatus"));
			pec.setRoleCode(rs.getString("RoleCode"));
			pec.setNextRoleCode(rs.getString("NextRoleCode"));
			pec.setTaskId(rs.getString("TaskId"));
			pec.setNextTaskId(rs.getString("NextTaskId"));
			pec.setRecordType(rs.getString("RecordType"));
			pec.setWorkflowId(rs.getLong("WorkflowId"));

			return pec;
		}, instrumentID);
	}

	@Override
	public List<InstrumentTypes> getInstrumentHeader() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, Code, Description, Internal, Enabled, AutoExtraction, ExtractionDays");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", Active, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" From Instrument_Types_Temp");
		sql.append(" Union all");
		sql.append(" Select");
		sql.append(" ID, Code, Description, Internal, Enabled, AutoExtraction, ExtractionDays");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", Active, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" From Instrument_Types");
		sql.append(" Where not exists (Select 1 From Instrument_Types_Temp Where ID = Instrument_Types.ID)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			InstrumentTypes it = new InstrumentTypes();

			it.setID(rs.getLong("Id"));
			it.setCode(rs.getString("Code"));
			it.setDescription(rs.getString("Description"));
			it.setInternal(rs.getBoolean("Internal"));
			it.setEnabled(rs.getBoolean("Enabled"));
			it.setAutoExtraction(rs.getBoolean("AutoExtraction"));
			it.setExtractionDays(rs.getInt("ExtractionDays"));
			it.setVersion(rs.getInt("Version"));
			it.setCreatedBy(rs.getLong("CreatedBy"));
			it.setCreatedOn(rs.getTimestamp("CreatedOn"));
			it.setApprovedBy(rs.getLong("ApprovedBy"));
			it.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			it.setLastMntBy(rs.getLong("LastMntBy"));
			it.setLastMntOn(rs.getTimestamp("LastMntOn"));
			it.setActive(rs.getBoolean("Active"));
			it.setRecordStatus(rs.getString("RecordStatus"));
			it.setRoleCode(rs.getString("RoleCode"));
			it.setNextRoleCode(rs.getString("NextRoleCode"));
			it.setTaskId(rs.getString("TaskId"));
			it.setNextTaskId(rs.getString("NextTaskId"));
			it.setRecordType(rs.getString("RecordType"));
			it.setWorkflowId(rs.getLong("WorkflowId"));

			return it;
		});
	}

	public boolean getExtractionDays(int extDys) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(ExtractionDays) From Instrument_Types");
		sql.append(" Where ExtractionDays > ? ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1)) > 0;
	}

}
