package com.pennanttech.pff.npa.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.npa.dao.AssetClassCodeDAO;
import com.pennanttech.pff.npa.model.AssetClassCode;

public class AssetClassCodeDAOImpl extends SequenceDao<AssetClassCode> implements AssetClassCodeDAO {
	private static Logger logger = LogManager.getLogger(AssetClassCodeDAOImpl.class);

	public AssetClassCodeDAOImpl() {
		super();
	}

	@Override
	public AssetClassCode getAssetClassCode(long id, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select Id, Code, Description, Active");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Asset_Class_Codes");
		sql.append(type);
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				AssetClassCode accde = new AssetClassCode();

				accde.setId(rs.getLong("Id"));
				accde.setCode(rs.getString("Code"));
				accde.setDescription(rs.getString("Description"));
				accde.setActive(rs.getBoolean("Active"));
				accde.setVersion(rs.getInt("Version"));
				accde.setCreatedBy(rs.getLong("CreatedBy"));
				accde.setCreatedOn(rs.getTimestamp("CreatedOn"));
				accde.setApprovedBy(rs.getLong("ApprovedBy"));
				accde.setApprovedOn(rs.getTimestamp("ApprovedOn"));
				accde.setLastMntBy(rs.getLong("LastMntBy"));
				accde.setLastMntOn(rs.getTimestamp("LastMntOn"));
				accde.setRecordStatus(rs.getString("RecordStatus"));
				accde.setRoleCode(rs.getString("RoleCode"));
				accde.setNextRoleCode(rs.getString("NextRoleCode"));
				accde.setTaskId(rs.getString("TaskId"));
				accde.setNextTaskId(rs.getString("NextTaskId"));
				accde.setRecordType(rs.getString("RecordType"));
				accde.setWorkflowId(rs.getLong("WorkflowId"));

				return accde;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			//
		}
		return null;
	}

	@Override
	public boolean isDuplicateKey(long id, String code, TableType tableType) {
		String sql;
		String whereClause = "Code = ? AND And id != ?";
		Object obj = new Object[] { code, id };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Asset_Class_Codes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Asset_Class_Codes", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Asset_Class_Codes_Temp", "Asset_Class_Codes" }, whereClause);
			obj = new Object[] { code, id, code, id };

			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public String save(AssetClassCode assetClassCode, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into Asset_Class_Codes");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, Code, Description,Active, Version");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (assetClassCode.getId() == Long.MIN_VALUE) {
			assetClassCode.setId(getNextValue("Seq_Asset_Class_Codes"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, assetClassCode.getId());
				ps.setString(index++, assetClassCode.getCode());
				ps.setString(index++, assetClassCode.getDescription());
				ps.setBoolean(index++, assetClassCode.isActive());
				ps.setInt(index++, assetClassCode.getVersion());
				ps.setLong(index++, assetClassCode.getCreatedBy());
				ps.setTimestamp(index++, assetClassCode.getCreatedOn());
				ps.setLong(index++, JdbcUtil.getLong(assetClassCode.getApprovedBy()));
				ps.setTimestamp(index++, assetClassCode.getApprovedOn());
				ps.setLong(index++, assetClassCode.getLastMntBy());
				ps.setTimestamp(index++, assetClassCode.getLastMntOn());
				ps.setString(index++, assetClassCode.getRecordStatus());
				ps.setString(index++, assetClassCode.getRoleCode());
				ps.setString(index++, assetClassCode.getNextRoleCode());
				ps.setString(index++, assetClassCode.getTaskId());
				ps.setString(index++, assetClassCode.getNextTaskId());
				ps.setString(index++, assetClassCode.getRecordType());
				ps.setLong(index, assetClassCode.getWorkflowId());
			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return Long.toString(assetClassCode.getId());
	}

	@Override
	public void update(AssetClassCode assetClassCode, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Asset_Class_Codes");
		sql.append(tableType.getSuffix());
		sql.append(" Set Code = ?, Description = ?, Active = ? ");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?,  RecordStatus  = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, assetClassCode.getCode());
			ps.setString(index++, assetClassCode.getDescription());
			ps.setBoolean(index++, assetClassCode.isActive());
			ps.setInt(index++, assetClassCode.getVersion());
			ps.setLong(index++, assetClassCode.getLastMntBy());
			ps.setTimestamp(index++, assetClassCode.getLastMntOn());
			ps.setString(index++, assetClassCode.getRecordStatus());
			ps.setString(index++, assetClassCode.getRoleCode());
			ps.setString(index++, assetClassCode.getNextRoleCode());
			ps.setString(index++, assetClassCode.getTaskId());
			ps.setString(index++, assetClassCode.getNextTaskId());
			ps.setString(index++, assetClassCode.getRecordType());
			ps.setLong(index++, assetClassCode.getWorkflowId());

			ps.setLong(index, assetClassCode.getId());
		});
	}

	@Override
	public void delete(AssetClassCode assetClassCode, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From Asset_Class_Codes");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ? ");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, assetClassCode.getId());

		});

	}

	@Override
	public boolean isAssetCodeExists(String code, TableType type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(Code) From Asset_Class_Codes");
		sql.append(type.getSuffix());
		sql.append(" Where Code = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), code) > 0;
	}

	@Override
	public boolean checkDependency(String code) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(Acc.Id) From (");
		sql.append(" Select Id, AssetClassId From Asset_Sub_Class_Codes_Temp");
		sql.append(" Union All");
		sql.append(" Select Id, AssetClassId From Asset_Sub_Class_Codes");
		sql.append(" ) Ascc");
		sql.append(" Inner Join Asset_Class_Codes Acc on Acc.Id = Ascc.AssetClassId");
		sql.append(" Where Acc.Code = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), code) > 0;
	}

}
