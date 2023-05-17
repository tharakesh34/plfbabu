package com.pennant.backend.dao.collateral.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.collateral.ExtendedFieldExtensionDAO;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

public class ExtendedFieldExtensionDAOImpl extends SequenceDao<ExtendedFieldExtension>
		implements ExtendedFieldExtensionDAO {
	private static Logger logger = LogManager.getLogger(ExtendedFieldExtensionDAOImpl.class);

	@Override
	public long getExtFieldExtensionId() {
		return getNextValue("seqExtendedFieldExtension");
	}

	@Override
	public void save(ExtendedFieldExtension extendedFieldExtension, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into Extended_Field_Ext");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, ExtenrnalRef, Purpose, ModeStatus, InstructionUID, Sequence, Event");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" Values( :Id, :ExtenrnalRef, :Purpose, :ModeStatus, :InstructionUID, :Sequence, :Event");
		sql.append(", :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId");
		sql.append(", :RecordType, :WorkflowId)");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldExtension);

		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void update(ExtendedFieldExtension extendedFieldExtension, TableType tableType) {

		StringBuilder sql = new StringBuilder("update Extended_Field_Ext");
		sql.append(tableType.getSuffix());
		sql.append(" SET ExtenrnalRef = :ExtenrnalRef, Purpose = :Purpose");
		sql.append(", ModeStatus = :ModeStatus, InstructionUID = :InstructionUID");
		sql.append(", Sequence = :Sequence, Event = :Event, Version = :Version, LastMntBy = :LastMntBy");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where Id = :Id");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(extendedFieldExtension);

		jdbcTemplate.update(sql.toString(), paramSource);
	}

	@Override
	public void delete(ExtendedFieldExtension extendedFieldExtension, TableType tableType) {

		StringBuilder sql = new StringBuilder("delete from Extended_Field_Ext");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = :Id");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(extendedFieldExtension);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public ExtendedFieldExtension getExtendedFieldExtension(String externalRef, String modeStatus, String finEvent,
			TableType tableType) {
		ExtendedFieldExtension extendedFieldExt = new ExtendedFieldExtension();
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, ExtenrnalRef, Purpose, ModeStatus, InstructionUid, Sequence, Event");
		sql.append(", VERSION, LASTMNTBY, LASTMNTON, RECORDSTATUS, ROLECODE, NEXTROLECODE, TASKID, NEXTTASKID");
		sql.append(", RecordType, WORKFLOWID From Extended_Field_Ext");
		sql.append(tableType.getSuffix());
		sql.append(" Where ExtenrnalRef = ? and ModeStatus = ? And Event = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<ExtendedFieldExtension>() {

				@Override
				public ExtendedFieldExtension mapRow(ResultSet rs, int rowNum) throws SQLException {
					ExtendedFieldExtension extendedFieldExt = new ExtendedFieldExtension();
					extendedFieldExt.setId(rs.getLong("Id"));
					extendedFieldExt.setExtenrnalRef(rs.getString("ExtenrnalRef"));
					extendedFieldExt.setPurpose(rs.getString("Purpose"));
					extendedFieldExt.setModeStatus(rs.getString("ModeStatus"));
					extendedFieldExt.setInstructionUID(rs.getLong("InstructionUid"));
					extendedFieldExt.setSequence(rs.getInt("Sequence"));
					extendedFieldExt.setEvent(rs.getString("Event"));
					extendedFieldExt.setVersion(rs.getInt("Version"));
					extendedFieldExt.setLastMntOn(rs.getTimestamp("LastMntOn"));
					extendedFieldExt.setLastMntBy(rs.getLong("LastMntBy"));
					extendedFieldExt.setRecordStatus(rs.getString("RecordStatus"));
					extendedFieldExt.setRoleCode(rs.getString("RoleCode"));
					extendedFieldExt.setNextRoleCode(rs.getString("NextRoleCode"));
					extendedFieldExt.setTaskId(rs.getString("TaskId"));
					extendedFieldExt.setNextTaskId(rs.getString("NextTaskId"));
					extendedFieldExt.setRecordType(rs.getString("RecordType"));
					extendedFieldExt.setWorkflowId(rs.getLong("WorkflowId"));

					return extendedFieldExt;
				}
			}, externalRef, modeStatus, finEvent);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return extendedFieldExt;
		}
	}

	@Override
	public boolean isDuplicateKey(ExtendedFieldExtension extendedFieldExtension, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select count(*) From Extended_Field_Ext");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id != ? and ExtenrnalRef = ? and ModeStatus = ? and InstructionUID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, extendedFieldExtension.getId(),
				extendedFieldExtension.getExtenrnalRef(), extendedFieldExtension.getModeStatus(),
				extendedFieldExtension.getInstructionUID()) > 0;
	}

	@Override
	public boolean isExtenstionExist(ExtendedFieldExtension extendedFieldExtension, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select count(*) From Extended_Field_Ext");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ? and ExtenrnalRef = ? and ModeStatus = ? and InstructionUID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, extendedFieldExtension.getId(),
				extendedFieldExtension.getExtenrnalRef(), extendedFieldExtension.getModeStatus(),
				extendedFieldExtension.getInstructionUID()) > 0;
	}
}
