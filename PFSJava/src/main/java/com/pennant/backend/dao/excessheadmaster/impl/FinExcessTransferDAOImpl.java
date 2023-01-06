package com.pennant.backend.dao.excessheadmaster.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.excessheadmaster.FinExcessTransferDAO;
import com.pennant.backend.model.excessheadmaster.FinExcessTransfer;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class FinExcessTransferDAOImpl extends SequenceDao<FinExcessTransfer> implements FinExcessTransferDAO {
	private static Logger logger = LogManager.getLogger(FinExcessTransferDAOImpl.class);

	public FinExcessTransferDAOImpl() {
		super();
	}

	@Override
	public FinExcessTransfer getExcessTransferByFinId(long finId, long trnasferId, String type) {
		logger.debug("Entering");
		FinExcessTransfer finExcessTransfer = new FinExcessTransfer();
		finExcessTransfer.setFinId(finId);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinId, FinReference, TransferDate, TransferToType, TransferToId, TransferFromType");
		sql.append(", TransferFromId, LinkedTranId, Status");
		sql.append(", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, ApprovedBy, ApprovedOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", TransferAmount");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CustCIF");
		}

		sql.append(" From Excess_Transfer_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinId =:FinId and Id =:Id");

		logger.debug("sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finExcessTransfer);
		RowMapper<FinExcessTransfer> typeRowMapper = BeanPropertyRowMapper.newInstance(FinExcessTransfer.class);

		try {
			finExcessTransfer = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finExcessTransfer = null;
		}
		logger.debug("Leaving");
		return finExcessTransfer;
	}

	@Override
	public void delete(FinExcessTransfer finExcessTransfer, TableType type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append(" Delete From Excess_Transfer_Details");
		deleteSql.append(type.getSuffix());
		deleteSql.append(" Where Id =:Id");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finExcessTransfer);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	@Override
	public String save(FinExcessTransfer finExcessTransfer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (finExcessTransfer.getId() <= 0) {
			finExcessTransfer.setId(getNextValue("SeqExcess_Transfer_Details"));
		}

		StringBuilder sql = new StringBuilder(" Insert into Excess_Transfer_Details");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, FinId, FinReference, TransferDate, TransferToType, TransferToId, TransferFromType");
		sql.append(", TransferFromId, LinkedTranId, Status");
		sql.append(", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, ApprovedBy, ApprovedOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(", TransferAmount)");
		sql.append(" Values(:Id, :FinId, :FinReference, :TransferDate, :TransferToType");
		sql.append(", :TransferToId, :TransferFromType, :TransferFromId, :LinkedTranId, :Status");
		sql.append(", :Version, :CreatedBy, :CreatedOn, :LastMntBy, :LastMntOn, :ApprovedBy, :ApprovedOn");
		sql.append(", :RecordStatus, :RoleCode, :NextRoleCode, :TaskId");
		sql.append(", :NextTaskId, :RecordType, :WorkflowId");
		sql.append(", :TransferAmount)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finExcessTransfer);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(finExcessTransfer.getId());
	}

	@Override
	public void update(FinExcessTransfer finExcessTransfer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update Excess_Transfer_Details");
		sql.append(tableType.getSuffix());

		sql.append(" set FINID =:FinId, FINREFERENCE =:FinReference, TRANSFERDATE =:TransferDate,");
		sql.append(
				" TRANSFERTOTYPE =:TransferToType, TRANSFERTOID =:TransferToId, TRANSFERFROMTYPE =:TransferFromType,");
		sql.append(" TRANSFERFROMID =:TransferFromId, LINKEDTRANID =:LinkedTranId, STATUS =:Status,");
		sql.append(" Version = :Version, CreatedOn = :CreatedOn,");
		sql.append(" LastMntOn = :LastMntOn, ApprovedBy = :ApprovedBy, ApprovedOn = :ApprovedOn,");
		sql.append(" RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId,");
		sql.append(" TransferAmount =:TransferAmount");
		sql.append(" where ID = :Id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finExcessTransfer);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public FinExcessTransfer getExcessTransferByTransferId(long transferId, String type) {
		logger.debug("Entering");
		FinExcessTransfer finExcessTransfer = new FinExcessTransfer();
		finExcessTransfer.setId(transferId);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinId, FinReference, TransferDate, TransferToType, TransferToId, TransferFromType");
		sql.append(", TransferFromId, LinkedTranId, Status");
		sql.append(
				", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, ApprovedBy, ApprovedOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", TransferAmount");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CustCIF");
		}

		sql.append(" From Excess_Transfer_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id =:Id");

		logger.debug("sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finExcessTransfer);
		RowMapper<FinExcessTransfer> typeRowMapper = BeanPropertyRowMapper.newInstance(FinExcessTransfer.class);

		try {
			finExcessTransfer = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finExcessTransfer = null;
		}
		logger.debug("Leaving");
		return finExcessTransfer;
	}

	@Override
	public boolean isIdExists(long transferId) {
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from Excess_Transfer_Details");
		sql.append(" Where Id = :Id");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("Id", transferId);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return false;
	}

	@Override
	public boolean isFinReceferenceExist(String finReference, String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from Excess_Transfer_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :finReference");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("finReference", finReference);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return false;
	}

}
