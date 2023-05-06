package com.pennanttech.pff.npa.dao.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.npa.dao.AssetSubClassCodeDAO;
import com.pennanttech.pff.npa.model.AssetSubClassCode;

public class AssetSubClassCodeDAOImpl extends SequenceDao<AssetSubClassCode> implements AssetSubClassCodeDAO {
	private static Logger logger = LogManager.getLogger(AssetSubClassCodeDAOImpl.class);

	public AssetSubClassCodeDAOImpl() {
		super();
	}

	@Override
	public AssetSubClassCode getAssetClassCode(long id, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select Id, AssetClassId, Code, Description, Active");

		if (type.contains("View")) {
			sql.append(", ClassCode, ClassDescription");
		}

		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Asset_Sub_Class_Codes");
		sql.append(type);
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				AssetSubClassCode ascc = new AssetSubClassCode();

				ascc.setId(rs.getLong("Id"));
				ascc.setAssetClassId(rs.getLong("AssetClassId"));
				ascc.setCode(rs.getString("Code"));
				ascc.setDescription(rs.getString("Description"));
				ascc.setActive(rs.getBoolean("Active"));

				if (type.contains("View")) {
					ascc.setClassCode(rs.getString("ClassCode"));
					ascc.setClassDescription(rs.getString("ClassDescription"));
				}

				ascc.setVersion(rs.getInt("Version"));
				ascc.setCreatedBy(rs.getLong("CreatedBy"));
				ascc.setCreatedOn(rs.getTimestamp("CreatedOn"));
				ascc.setApprovedBy(rs.getLong("ApprovedBy"));
				ascc.setApprovedOn(rs.getTimestamp("ApprovedOn"));
				ascc.setLastMntBy(rs.getLong("LastMntBy"));
				ascc.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ascc.setRecordStatus(rs.getString("RecordStatus"));
				ascc.setRoleCode(rs.getString("RoleCode"));
				ascc.setNextRoleCode(rs.getString("NextRoleCode"));
				ascc.setTaskId(rs.getString("TaskId"));
				ascc.setNextTaskId(rs.getString("NextTaskId"));
				ascc.setRecordType(rs.getString("RecordType"));
				ascc.setWorkflowId(rs.getLong("WorkflowId"));

				return ascc;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return null;
	}

	@Override
	public boolean isDuplicateKey(long id, String code, TableType tableType) {
		String sql;
		String whereClause = "Code = ? AND And Id != ?";
		Object obj = new Object[] { code, id };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Asset_Sub_Class_Codes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Asset_Sub_Class_Codes", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Asset_Sub_Class_Codes_Temp", "Asset_Sub_Class_Codes" },
					whereClause);
			obj = new Object[] { code, id, code, id };

			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public String save(AssetSubClassCode ascc, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into Asset_Sub_Class_Codes");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, AssetClassId, Code, Description, Active, Version");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (ascc.getId() == Long.MIN_VALUE) {
			ascc.setId(getNextValue("Seq_Asset_Sub_Class_Codes"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, ascc.getId());
				ps.setLong(index++, ascc.getAssetClassId());
				ps.setString(index++, ascc.getCode());
				ps.setString(index++, ascc.getDescription());
				ps.setBoolean(index++, ascc.isActive());
				ps.setInt(index++, ascc.getVersion());
				ps.setLong(index++, ascc.getCreatedBy());
				ps.setTimestamp(index++, ascc.getCreatedOn());
				ps.setLong(index++, JdbcUtil.getLong(ascc.getApprovedBy()));
				ps.setTimestamp(index++, ascc.getApprovedOn());
				ps.setLong(index++, ascc.getLastMntBy());
				ps.setTimestamp(index++, ascc.getLastMntOn());
				ps.setString(index++, ascc.getRecordStatus());
				ps.setString(index++, ascc.getRoleCode());
				ps.setString(index++, ascc.getNextRoleCode());
				ps.setString(index++, ascc.getTaskId());
				ps.setString(index++, ascc.getNextTaskId());
				ps.setString(index++, ascc.getRecordType());
				ps.setLong(index, ascc.getWorkflowId());
			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return Long.toString(ascc.getId());
	}

	@Override
	public void update(AssetSubClassCode ascc, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Asset_Sub_Class_Codes");
		sql.append(tableType.getSuffix());
		sql.append(" Set AssetClassId = ?, Code = ?, Description = ?, Active = ? ");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?,  RecordStatus  = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ascc.getAssetClassId());
			ps.setString(index++, ascc.getCode());
			ps.setString(index++, ascc.getDescription());
			ps.setBoolean(index++, ascc.isActive());
			ps.setInt(index++, ascc.getVersion());
			ps.setLong(index++, ascc.getLastMntBy());
			ps.setTimestamp(index++, ascc.getLastMntOn());
			ps.setString(index++, ascc.getRecordStatus());
			ps.setString(index++, ascc.getRoleCode());
			ps.setString(index++, ascc.getNextRoleCode());
			ps.setString(index++, ascc.getTaskId());
			ps.setString(index++, ascc.getNextTaskId());
			ps.setString(index++, ascc.getRecordType());
			ps.setLong(index++, ascc.getWorkflowId());

			ps.setLong(index, ascc.getId());
		});

	}

	@Override
	public void delete(AssetSubClassCode assetClassCode, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From Asset_Sub_Class_Codes");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ? ");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, assetClassCode.getId()));
	}

	@Override
	public boolean checkUniqueKey(String code, long assetClassId, TableType type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(Code) From Asset_Sub_Class_Codes");
		sql.append(type.getSuffix());
		sql.append(" Where Code = ? and  AssetClassId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), code, assetClassId) > 0;
	}

	@Override
	public boolean checkDependency(String code) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(Acsd.Id) From (");
		sql.append(" Select Id, SubClassID From Asset_Class_Setup_Details_Temp");
		sql.append(" Union All");
		sql.append(" Select Id, SubClassID From Asset_Class_Setup_Details");
		sql.append(" ) Acsd");
		sql.append(" Inner Join Asset_Sub_Class_Codes Ascc on Ascc.Id = Acsd.SubClassID");
		sql.append(" Where Ascc.Code = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), code) > 0;
	}

	@Override
	public boolean checkUniqueKey(String code, TableType type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(Code) From Asset_Sub_Class_Codes");
		sql.append(type.getSuffix());
		sql.append(" Where Code = ? ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), code) > 0;
	}

	@Override
	public List<String> getAssetClassCodes() {
		String sql = "Select Code From Asset_Class_Codes";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> rs.getString(1));
	}

}
