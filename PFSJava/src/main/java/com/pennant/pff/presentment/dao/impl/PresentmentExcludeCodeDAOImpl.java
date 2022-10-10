package com.pennant.pff.presentment.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.presentment.dao.PresentmentExcludeCodeDAO;
import com.pennant.pff.presentment.model.PresentmentExcludeCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class PresentmentExcludeCodeDAOImpl extends BasicDao<PresentmentExcludeCode>
		implements PresentmentExcludeCodeDAO {

	public PresentmentExcludeCodeDAOImpl() {
		super();
	}

	@Override
	public PresentmentExcludeCode getExcludeCode(String code) {
		StringBuilder sql = getSqlQuery(TableType.TEMP_TAB);
		sql.append(" Where pec.Code = ?");
		sql.append(" Union all ");
		sql.append(getSqlQuery(TableType.MAIN_TAB));
		sql.append(" Where pec.Code = ?");
		sql.append(" and not exists (Select 1 From Presentment_Exclude_Codes_Temp Where Id = pec.Id)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new PresentmentExcludeCodesRM(), code, code);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<PresentmentExcludeCode> getPresentmentExcludeCodes(List<String> roleCodes) {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" Id, Code, Description, ExcludeId, BounceId, CreateBounceOnDueDate");
		sql.append(", BounceCode, ReturnCode, Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn, Active, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From (");
		sql.append(getSqlQuery(TableType.TEMP_TAB));
		sql.append(" Union All ");
		sql.append(getSqlQuery(TableType.MAIN_TAB));
		sql.append(" Where not exists (Select 1 From Presentment_Exclude_Codes_Temp Where Id = pec.Id)) p");
		sql.append(" Where NextRoleCode is null or NextRoleCode = ? or NextRoleCode in (");
		sql.append(JdbcUtil.getInCondition(roleCodes));
		sql.append(") Order By Code");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, "");
			for (String roleCode : roleCodes) {
				ps.setString(index++, roleCode);
			}
		}, new PresentmentExcludeCodesRM());
	}

	@Override
	public List<PresentmentExcludeCode> getResult(ISearch search) {
		List<Object> value = new ArrayList<>();

		StringBuilder sql = new StringBuilder("select");
		sql.append(" Id, Code, Description, ExcludeId, BounceId, CreateBounceOnDueDate");
		sql.append(", BounceCode, ReturnCode, Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn, Active, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From (");
		sql.append(getSqlQuery(TableType.TEMP_TAB));
		sql.append(" Union All ");
		sql.append(getSqlQuery(TableType.MAIN_TAB));
		sql.append(" Where not exists (Select 1 From Presentment_Exclude_Codes_Temp Where Id = pec.Id)) pec");
		sql.append(QueryUtil.buildWhereClause(search, value));
		sql.append(" Order By Code");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			for (Object object : value) {
				ps.setObject(index++, object);
			}

		}, new PresentmentExcludeCodesRM());
	}

	@Override
	public String save(PresentmentExcludeCode bc, TableType type) {
		StringBuilder sql = new StringBuilder("Insert Into Presentment_Exclude_Codes");
		sql.append(type.getSuffix());
		sql.append(" (Id, Code, Description, ExcludeId, BounceId, CreateBounceOnDueDate");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy");
		sql.append(", LastMntOn, Active, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, bc.getId());
				ps.setString(index++, bc.getCode());
				ps.setString(index++, bc.getDescription());
				ps.setInt(index++, bc.getExcludeId());
				ps.setObject(index++, bc.getBounceId());
				ps.setBoolean(index++, bc.isCreateBounceOnDueDate());
				ps.setInt(index++, bc.getVersion());
				ps.setLong(index++, bc.getCreatedBy());
				ps.setTimestamp(index++, bc.getCreatedOn());
				ps.setObject(index++, bc.getApprovedBy());
				ps.setTimestamp(index++, bc.getApprovedOn());
				ps.setLong(index++, bc.getLastMntBy());
				ps.setTimestamp(index++, bc.getLastMntOn());
				ps.setBoolean(index++, bc.isActive());
				ps.setString(index++, bc.getRecordStatus());
				ps.setString(index++, bc.getRoleCode());
				ps.setString(index++, bc.getNextRoleCode());
				ps.setString(index++, bc.getTaskId());
				ps.setString(index++, bc.getNextTaskId());
				ps.setString(index++, bc.getRecordType());
				ps.setLong(index, bc.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(bc.getCode());
	}

	@Override
	public void update(PresentmentExcludeCode bc, TableType type) {
		StringBuilder sql = new StringBuilder("Update Presentment_Exclude_Codes");
		sql.append(type.getSuffix());
		sql.append(" Set Code = ?, Description = ?, ExcludeId = ?, BounceId = ?, CreateBounceOnDueDate = ? ");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, bc.getCode());
				ps.setString(index++, bc.getDescription());
				ps.setInt(index++, bc.getExcludeId());
				ps.setObject(index++, bc.getBounceId());
				ps.setBoolean(index++, bc.isCreateBounceOnDueDate());
				ps.setInt(index++, bc.getVersion());
				ps.setLong(index++, bc.getLastMntBy());
				ps.setTimestamp(index++, bc.getLastMntOn());
				ps.setString(index++, bc.getRecordStatus());
				ps.setString(index++, bc.getRoleCode());
				ps.setString(index++, bc.getNextRoleCode());
				ps.setString(index++, bc.getTaskId());
				ps.setString(index++, bc.getNextTaskId());
				ps.setString(index++, bc.getRecordType());
				ps.setLong(index++, bc.getWorkflowId());

				ps.setLong(index, bc.getId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void delete(PresentmentExcludeCode bc, TableType type) {
		StringBuilder sql = new StringBuilder("Delete From Presentment_Exclude_Codes");
		sql.append(type.getSuffix());
		sql.append(" Where Code = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			if (jdbcOperations.update(sql.toString(), bc.getCode()) == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public boolean isDuplicateKey(long id, TableType type) {
		Object[] parameters = new Object[] { id };

		String sql;
		String whereClause = "Id = ?";
		String[] tables = new String[] { "Presentment_Exclude_Codes_Temp", "Presentment_Exclude_Codes" };

		switch (type) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Presentment_Exclude_Codes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Presentment_Exclude_Codes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(tables, whereClause);
			parameters = new Object[] { id, id };
			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, parameters) > 0;
	}

	@Override
	public Map<Integer, String> getBounceForPD() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" pec.ExcludeID, br.ReturnCode");
		sql.append(" From Presentment_Exclude_Codes pec");
		sql.append(" Inner Join BounceReasons br on br.BounceID = pec.BounceID");
		sql.append(" Where CreateBounceOnDueDate = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		Map<Integer, String> map = new HashMap<>();

		return jdbcOperations.query(sql.toString(), rs -> {
			while (rs.next()) {
				map.put(rs.getInt(1), rs.getString(2));
			}

			return map;
		}, 1);
	}

	private StringBuilder getSqlQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" pec.Id, pec.Code, pec.Description, pec.ExcludeId, pec.BounceId, pec.CreateBounceOnDueDate");
		sql.append(", br.BounceCode, br.ReturnCode, pec.Version");
		sql.append(", pec.CreatedBy, pec.CreatedOn, pec.ApprovedBy, pec.ApprovedOn");
		sql.append(", pec.LastMntBy, pec.LastMntOn, pec.Active, pec.RecordStatus, pec.RoleCode");
		sql.append(", pec.NextRoleCode, pec.TaskId, pec.NextTaskId, pec.RecordType, pec.WorkflowId");
		sql.append(" From Presentment_Exclude_Codes").append(tableType.getSuffix()).append(" pec");
		sql.append(" Left Join BounceReasons br on br.BounceID = pec.BounceID");

		return sql;
	}

	public List<ReportListDetail> getPrintCodes(List<String> roleCodes) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Code, Description, BounceCode, CreateBounceOnDueDate");
		sql.append(" From (Select Code, Description, BounceCode, CreateBounceOnDueDate");
		sql.append(" From Presentment_Exclude_Codes_temp pec");
		sql.append(" Left Join BounceReasons br on br.BounceID = pec.BounceID");
		sql.append(" Union All ");
		sql.append(" Select Code, Description, BounceCode, CreateBounceOnDueDate");
		sql.append(" From Presentment_Exclude_Codes pec");
		sql.append(" Left Join BounceReasons br on br.BounceID = pec.BounceID");
		sql.append(" Where pec.NextRoleCode is null or pec.NextRoleCode = ? or pec.NextRoleCode in (");
		sql.append(JdbcUtil.getInCondition(roleCodes));
		sql.append(") Order By Code)");

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, "");
			for (String roleCode : roleCodes) {
				ps.setString(index++, roleCode);
			}
		}, new ReportListRM());
	}

	private class PresentmentExcludeCodesRM implements RowMapper<PresentmentExcludeCode> {

		@Override
		public PresentmentExcludeCode mapRow(ResultSet rs, int rowNum) throws SQLException {
			PresentmentExcludeCode bc = new PresentmentExcludeCode();

			bc.setId(rs.getLong("Id"));
			bc.setCode(rs.getString("Code"));
			bc.setDescription(rs.getString("Description"));
			bc.setExcludeId(rs.getInt("ExcludeId"));
			bc.setBounceId(JdbcUtil.getLong(rs.getObject("BounceId")));
			bc.setCreateBounceOnDueDate(rs.getBoolean("CreateBounceOnDueDate"));
			bc.setBounceCode(rs.getString("BounceCode"));
			bc.setReturnCode(rs.getString("ReturnCode"));
			bc.setVersion(rs.getInt("Version"));
			bc.setCreatedBy(rs.getLong("CreatedBy"));
			bc.setCreatedOn(rs.getTimestamp("CreatedOn"));
			bc.setApprovedBy(rs.getLong("ApprovedBy"));
			bc.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			bc.setLastMntBy(rs.getLong("LastMntBy"));
			bc.setLastMntOn(rs.getTimestamp("LastMntOn"));
			bc.setActive(rs.getBoolean("Active"));
			bc.setRecordStatus(rs.getString("RecordStatus"));
			bc.setRoleCode(rs.getString("RoleCode"));
			bc.setNextRoleCode(rs.getString("NextRoleCode"));
			bc.setTaskId(rs.getString("TaskId"));
			bc.setNextTaskId(rs.getString("NextTaskId"));
			bc.setRecordType(rs.getString("RecordType"));
			bc.setWorkflowId(rs.getLong("WorkflowId"));

			return bc;
		}

	}

	private class ReportListRM implements RowMapper<ReportListDetail> {

		@Override
		public ReportListDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReportListDetail bc = new ReportListDetail();

			bc.setfieldString01(rs.getString("Code"));
			bc.setfieldString02(rs.getString("Description"));
			bc.setFieldBoolean04(rs.getInt("CreateBounceOnDueDate"));
			bc.setfieldString03(rs.getString("BounceCode"));

			return bc;
		}

	}
}
