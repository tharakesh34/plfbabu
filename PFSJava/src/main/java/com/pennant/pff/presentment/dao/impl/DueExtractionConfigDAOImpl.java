package com.pennant.pff.presentment.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
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
		sql.append("( ID, ExtractionMonth, Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
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
	public void updateHeader(List<DueExtractionHeader> list, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Due_Extraction_Header");
		sql.append(tableType.getSuffix());
		sql.append(" set Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", Active = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkFlowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DueExtractionHeader header = list.get(i);

				int index = 1;

				ps.setInt(index++, header.getVersion());
				ps.setLong(index++, header.getLastMntBy());
				ps.setTimestamp(index++, header.getLastMntOn());
				ps.setBoolean(index++, header.getActive());
				ps.setString(index++, header.getRecordStatus());
				ps.setString(index++, header.getRoleCode());
				ps.setString(index++, header.getNextRoleCode());
				ps.setString(index++, header.getTaskId());
				ps.setString(index++, header.getNextTaskId());
				ps.setString(index++, header.getRecordType());
				ps.setLong(index++, header.getWorkflowId());

				ps.setLong(index, header.getID());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public void save(List<DueExtractionConfig> list, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into Due_Extraction_Config");
		sql.append(tableType.getSuffix());
		sql.append("(Id, MonthID, InstrumentID, DueDate, ExtractionDate, Modified");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn, Active, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					DueExtractionConfig dec = list.get(i);

					int index = 1;
					ps.setLong(index++, dec.getID());
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
					ps.setLong(index, dec.getWorkflowId());
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
	public void update(List<DueExtractionConfig> list, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Due_Extraction_Config");
		sql.append(tableType.getSuffix());
		sql.append(" set DueDate = ?, ExtractionDate = ?, Modified = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, Active = ?");
		sql.append(", RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkFlowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DueExtractionConfig dec = list.get(i);

				int index = 1;

				ps.setDate(index++, JdbcUtil.getDate(dec.getDueDate()));
				ps.setDate(index++, JdbcUtil.getDate(dec.getExtractionDate()));
				ps.setBoolean(index++, dec.isModified());
				ps.setInt(index++, dec.getVersion());
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

				ps.setLong(index, dec.getID());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
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
	public void deleteConfig(Date extractionDate) {
		String sql = "Select Distinct MonthID From Due_Extraction_Config Where ExtractionDate <= ?";

		logger.debug(Literal.SQL.concat(sql));

		List<Long> list = jdbcOperations.query(sql, (rs, rowNum) -> rs.getLong(1), extractionDate);

		if (CollectionUtils.isEmpty(list)) {
			return;
		}

		sql = "Delete from Due_Extraction_Config Where MonthID in (" + JdbcUtil.getInCondition(list) + ")";

		Object[] obj = new Object[list.size()];

		int i = 0;
		for (Long monthID : list) {
			obj[i++] = monthID;
		}

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql, obj);

		sql = "Delete from Due_Extraction_Header Where ID in (" + JdbcUtil.getInCondition(list) + ")";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql, obj);
	}

	@Override
	public void delete(DueExtractionHeader header, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from Due_Extraction_Config");
		sql.append(tableType.getSuffix());
		sql.append(" Where MonthID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), header.getID());

		sql = new StringBuilder("Delete from Due_Extraction_Header");
		sql.append(tableType.getSuffix());
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), header.getID());
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

	@Override
	public long getNextValue() {
		return getNextValue("Seq_Due_Extraction_Config");
	}

	@Override
	public List<DueExtractionHeader> getDueExtractionHeaders() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" deh.ID, deh.ExtractionMonth, deh.Version, deh.CreatedBy, deh.CreatedOn, deh.ApprovedBy");
		sql.append(", deh.ApprovedOn, deh.LastMntBy, deh.LastMntOn, deh.Active, deh.RecordStatus, deh.RoleCode");
		sql.append(", deh.NextRoleCode, deh.TaskId, deh.NextTaskId, deh.RecordType, deh.WorkFlowId, su.UsrLogin");
		sql.append(" From Due_Extraction_Header_Temp deh");
		sql.append(" Left Join SecUsers su on su.UsrID = deh.LastMntBy");
		sql.append(" Union All");
		sql.append(" Select");
		sql.append(" deh.ID, deh.ExtractionMonth, deh.Version, deh.CreatedBy, deh.CreatedOn, deh.ApprovedBy");
		sql.append(", deh.ApprovedOn, deh.LastMntBy, deh.LastMntOn, deh.Active, deh.RecordStatus, deh.RoleCode");
		sql.append(", deh.NextRoleCode, deh.TaskId, deh.NextTaskId, deh.RecordType, deh.WorkFlowId, su.UsrLogin");
		sql.append(" From Due_Extraction_Header deh");
		sql.append(" Left Join SecUsers su on su.UsrID = deh.LastMntBy");
		sql.append(" Where not exists (Select 1 From Due_Extraction_Header_Temp");
		sql.append(" Where ID = deh.ID)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			DueExtractionHeader header = new DueExtractionHeader();

			header.setID(rs.getLong("ID"));
			header.setExtractionMonth(rs.getString("ExtractionMonth"));
			header.setVersion(rs.getInt("Version"));
			header.setCreatedBy(rs.getLong("CreatedBy"));
			header.setCreatedOn(rs.getTimestamp("CreatedOn"));
			header.setApprovedBy(rs.getLong("ApprovedBy"));
			header.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			header.setLastMntBy(rs.getLong("LastMntBy"));
			header.setLastMntOn(rs.getTimestamp("LastMntOn"));
			header.setActive(rs.getBoolean("Active"));
			header.setRecordStatus(rs.getString("RecordStatus"));
			header.setRoleCode(rs.getString("RoleCode"));
			header.setNextRoleCode(rs.getString("NextRoleCode"));
			header.setTaskId(rs.getString("TaskId"));
			header.setNextTaskId(rs.getString("NextTaskId"));
			header.setRecordType(rs.getString("RecordType"));
			header.setWorkflowId(rs.getLong("WorkflowId"));
			header.setUsrName(rs.getString("UsrLogin"));

			return header;
		});
	}

	@Override
	public List<DueExtractionConfig> getDueExtractionConfig(long monthID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" c.ID, c.MonthID, c.InstrumentID, c.DueDate, c.ExtractionDate, c.Modified, c.Version");
		sql.append(", c.CreatedBy, c.CreatedOn, c.ApprovedBy, c.ApprovedOn, c.LastMntBy, c.LastMntOn");
		sql.append(", c.Active, c.RecordStatus, c.RoleCode, c.NextRoleCode, c.TaskId, c.NextTaskId");
		sql.append(", c.RecordType, c.WorkFlowId, it.Code, it.ExtractionDays");
		sql.append(" From Due_Extraction_Config_Temp c");
		sql.append(" Inner Join Instrument_Types it on it.ID = c.InstrumentID");
		sql.append(" Where c.MonthID = ?");
		sql.append(" Union All Select");
		sql.append(" c.ID, c.MonthID, c.InstrumentID, c.DueDate, c.ExtractionDate, c.Modified, c.Version");
		sql.append(", c.CreatedBy, c.CreatedOn, c.ApprovedBy, c.ApprovedOn, c.LastMntBy, c.LastMntOn");
		sql.append(", c.Active, c.RecordStatus, c.RoleCode, c.NextRoleCode, c.TaskId, c.NextTaskId");
		sql.append(", c.RecordType, c.WorkFlowId, it.Code, it.ExtractionDays");
		sql.append(" From Due_Extraction_Config c");
		sql.append(" Inner Join Instrument_Types it on it.ID = c.InstrumentID");
		sql.append(" Where c.MonthID = ? and not exists (");
		sql.append(" Select 1 From Due_Extraction_Config_Temp Where ID = c.ID)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<DueExtractionConfig> list = jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			DueExtractionConfig config = new DueExtractionConfig();

			config.setID(rs.getLong("Id"));
			config.setMonthID(rs.getLong("MonthID"));
			config.setInstrumentID(rs.getLong("InstrumentID"));
			config.setDueDate(rs.getDate("DueDate"));
			config.setExtractionDate(rs.getDate("ExtractionDate"));
			config.setModified(rs.getBoolean("Modified"));
			config.setVersion(rs.getInt("Version"));
			config.setCreatedBy(rs.getLong("CreatedBy"));
			config.setCreatedOn(rs.getTimestamp("CreatedOn"));
			config.setApprovedBy(rs.getLong("ApprovedBy"));
			config.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			config.setLastMntBy(rs.getLong("LastMntBy"));
			config.setLastMntOn(rs.getTimestamp("LastMntOn"));
			config.setActive(rs.getBoolean("Active"));
			config.setRecordStatus(rs.getString("RecordStatus"));
			config.setRoleCode(rs.getString("RoleCode"));
			config.setNextRoleCode(rs.getString("NextRoleCode"));
			config.setTaskId(rs.getString("TaskId"));
			config.setNextTaskId(rs.getString("NextTaskId"));
			config.setRecordType(rs.getString("RecordType"));
			config.setWorkflowId(rs.getLong("WorkflowId"));
			config.setInstrumentCode(rs.getString("Code"));
			config.setConfigureDays(rs.getInt("ExtractionDays"));

			return config;
		}, monthID, monthID);

		return list.stream().sorted((l1, l2) -> l1.getDueDate().compareTo(l2.getDueDate()))
				.collect(Collectors.toList());
	}

	@Override
	public Map<Long, InstrumentTypes> getInstrumentTypesMap() {
		String sql = "Select ID, Code, ExtractionDays From Instrument_Types Where AutoExtraction = ?";

		logger.debug(Literal.SQL.concat(sql));

		Map<Long, InstrumentTypes> map = new HashMap<>();

		return this.jdbcOperations.query(sql, (ResultSet rs) -> {
			while (rs.next()) {
				InstrumentTypes it = new InstrumentTypes();
				it.setID(rs.getLong("ID"));
				it.setCode(rs.getString("Code"));
				it.setExtractionDays(rs.getInt("ExtractionDays"));

				map.put(it.getID(), it);

			}
			return map;
		}, 1);
	}

}
