package com.pennant.backend.dao.applicationmaster.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.applicationmaster.BounceCodeDao;
import com.pennant.backend.model.applicationmaster.BounceCode;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class BounceCodeDaoImpl extends BasicDao<BounceCode> implements BounceCodeDao {

	public BounceCodeDaoImpl() {
		super();
	}

	@Override
	public BounceCode getCode(String code) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" pec.Id, pec.Code, pec.Description, pec.ExcludeId, pec.BounceId, pec.CreateBounceOnDueDate");
		sql.append(", BR.BounceCode, pec.Version, pec.CreatedBy, pec.CreatedOn, pec.ApprovedBy, pec.ApprovedOn");
		sql.append(", pec.LastMntBy, pec.LastMntOn, pec.Active, pec.RecordStatus, pec.RoleCode");
		sql.append(", pec.NextRoleCode, pec.TaskId, pec.NextTaskId, pec.RecordType, pec.WorkflowId");
		sql.append(" From Presentment_Exclude_Codes_Temp pec");
		sql.append(" Inner Join BounceReasons br on br.BounceID = pec.BounceID");
		sql.append(" Where pec.Code = ?");
		sql.append(" Union All");
		sql.append(" Select pec.Id, pec.Code, pec.Description, pec.ExcludeId, pec.BounceId, pec.CreateBounceOnDueDate");
		sql.append(", BR.BounceCode, pec.Version, pec.CreatedBy, pec.CreatedOn, pec.ApprovedBy, pec.ApprovedOn");
		sql.append(", pec.LastMntBy, pec.LastMntOn, pec.Active, pec.RecordStatus, pec.RoleCode");
		sql.append(", pec.NextRoleCode, pec.TaskId, pec.NextTaskId, pec.RecordType, pec.WorkflowId");
		sql.append(" From Presentment_Exclude_Codes pec");
		sql.append(" Inner Join BounceReasons br on br.BounceID = pec.BounceID");
		sql.append(" Where pec.Code = ?");
		sql.append(" and not exists (Select 1 From Presentment_Exclude_Codes_Temp Where Id = pec.Id)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				BounceCode bc = new BounceCode();

				bc.setId(rs.getLong("Id"));
				bc.setCode(rs.getString("Code"));
				bc.setDescription(rs.getString("Description"));
				bc.setExcludeId(rs.getInt("ExcludeId"));
				bc.setBounceId(rs.getLong("BounceId"));
				bc.setCreateBounceOnDueDate(rs.getBoolean("CreateBounceOnDueDate"));
				bc.setBounceCode(rs.getString("BounceCode"));
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
			}, code, code);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String save(BounceCode bc, TableType type) {
		StringBuilder sql = new StringBuilder("Insert Into Presentment_Exclude_Codes");
		sql.append(type.getSuffix());
		sql.append(" (Id, Code, Description, ExcludeId, BounceId, CreateBounceOnDueDate");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy");
		sql.append(", LastMntOn, Active, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" values (?, ?, ?, ?, ?, ?, ?,");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, bc.getId());
				ps.setString(index++, bc.getCode());
				ps.setString(index++, bc.getDescription());
				ps.setInt(index++, bc.getExcludeId());
				ps.setLong(index++, bc.getBounceId());
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
	public void update(BounceCode bc, TableType type) {
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
				ps.setLong(index++, bc.getBounceId());
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
	public void delete(BounceCode bc, TableType type) {
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
	public List<BounceCode> getBounceCodeById(Long Id) {
		StringBuilder sql = new StringBuilder("select * from (Select ");

		sql.append(" pec.Id, pec.Code, pec.Description, pec.ExcludeId, pec.BounceId, pec.CreateBounceOnDueDate");
		sql.append(", BR.BounceCode, pec.Version, pec.CreatedBy, pec.CreatedOn, pec.ApprovedBy, pec.ApprovedOn");
		sql.append(", pec.LastMntBy, pec.LastMntOn, pec.Active, pec.RecordStatus, pec.RoleCode");
		sql.append(", pec.NextRoleCode, pec.TaskId, pec.NextTaskId, pec.RecordType, pec.WorkflowId");
		sql.append(" From Presentment_Exclude_Codes_Temp pec");
		sql.append(" Inner Join BounceReasons br on br.BounceID = pec.BounceID");
		sql.append(" Union All");
		sql.append(" Select pec.Id, pec.Code, pec.Description, pec.ExcludeId, pec.BounceId, pec.CreateBounceOnDueDate");
		sql.append(", BR.BounceCode, pec.Version, pec.CreatedBy, pec.CreatedOn, pec.ApprovedBy, pec.ApprovedOn");
		sql.append(", pec.LastMntBy, pec.LastMntOn, pec.Active, pec.RecordStatus, pec.RoleCode");
		sql.append(", pec.NextRoleCode, pec.TaskId, pec.NextTaskId, pec.RecordType, pec.WorkflowId");
		sql.append(" From Presentment_Exclude_Codes pec");
		sql.append(" Inner Join BounceReasons br on br.BounceID = pec.BounceID");
		sql.append(" where not exists (Select 1 From Presentment_Exclude_Codes_Temp Where Id = pec.Id)) p");
		if (Id != null) {
			sql.append(" Where p.Code = ?");
		}
		sql.append(" WHERE ((nextRoleCode IS NULL) OR (nextRoleCode = nextRoleCode) OR ");
		sql.append("(nextRoleCode in (nextRoleCode))) ORDER BY code ASC,createbounceonduedate ASC LIMIT 19");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.query(sql.toString(), ps -> {
				int index = 1;
				if (Id != null) {
					ps.setLong(index++, Id);
				}
			}, (rs, rowNum) -> {
				BounceCode bc = new BounceCode();

				bc.setId(rs.getLong("Id"));
				bc.setCode(rs.getString("Code"));
				bc.setDescription(rs.getString("Description"));
				bc.setExcludeId(rs.getInt("ExcludeId"));
				bc.setBounceId(rs.getLong("BounceId"));
				bc.setCreateBounceOnDueDate(rs.getBoolean("CreateBounceOnDueDate"));
				bc.setBounceCode(rs.getString("BounceCode"));
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
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<BounceCode> getResult(ISearch search) {
		List<Object> value = new ArrayList<>();

		StringBuilder sql = new StringBuilder("select * from (Select ");

		sql.append(" pec.Id, pec.Code, pec.Description, pec.ExcludeId, pec.BounceId, pec.CreateBounceOnDueDate");
		sql.append(", BR.BounceCode, pec.Version, pec.CreatedBy, pec.CreatedOn, pec.ApprovedBy, pec.ApprovedOn");
		sql.append(", pec.LastMntBy, pec.LastMntOn, pec.Active, pec.RecordStatus, pec.RoleCode");
		sql.append(", pec.NextRoleCode, pec.TaskId, pec.NextTaskId, pec.RecordType, pec.WorkflowId");
		sql.append(" From Presentment_Exclude_Codes_Temp pec");
		sql.append(" Inner Join BounceReasons br on br.BounceID = pec.BounceID");
		sql.append(" Union All");
		sql.append(" Select pec.Id, pec.Code, pec.Description, pec.ExcludeId, pec.BounceId, pec.CreateBounceOnDueDate");
		sql.append(", BR.BounceCode, pec.Version, pec.CreatedBy, pec.CreatedOn, pec.ApprovedBy, pec.ApprovedOn");
		sql.append(", pec.LastMntBy, pec.LastMntOn, pec.Active, pec.RecordStatus, pec.RoleCode");
		sql.append(", pec.NextRoleCode, pec.TaskId, pec.NextTaskId, pec.RecordType, pec.WorkflowId");
		sql.append(" From Presentment_Exclude_Codes pec");
		sql.append(" Inner Join BounceReasons br on br.BounceID = pec.BounceID");
		sql.append(" where not exists (Select 1 From Presentment_Exclude_Codes_Temp Where Id = pec.Id)) pec");
		sql.append(buildWhereClause(search, value));
		{

			logger.debug(Literal.SQL + sql.toString());

			return this.jdbcOperations.query(sql.toString(), ps -> {
				int index = 1;
				for (Object object : value) {
					ps.setObject(index++, object);
				}

			}, (rs, rowNum) -> {
				BounceCode bc = new BounceCode();

				bc.setId(rs.getLong("Id"));
				bc.setCode(rs.getString("Code"));
				bc.setDescription(rs.getString("Description"));
				bc.setExcludeId(rs.getInt("ExcludeId"));
				bc.setBounceId(rs.getLong("BounceId"));
				bc.setCreateBounceOnDueDate(rs.getBoolean("CreateBounceOnDueDate"));
				bc.setBounceCode(rs.getString("BounceCode"));
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
			});
		}
	}

	public static String buildWhereClause(ISearch search, List<Object> psList) {
		StringBuilder sql = new StringBuilder();

		for (Filter filter : search.getFilters()) {
			String condition = filter.getProperty();

			if ("AND".equals(condition) || "OR".equals(condition)) {
				if (!(filter.getValue() instanceof List<?>)) {
					continue;
				}

				List<?> list = (List<?>) filter.getValue();

				for (Object object : list) {
					if (object instanceof Filter) {
						try {
							if (sql.length() > 0) {
								sql.append(condition).append(" ");
							}

							buildQueryByOperator((Filter) object, psList, sql);
						} catch (Exception e) {
							//
						}

					}
				}
			} else {
				try {
					if (sql.length() > 0) {
						sql.append(" AND ");
					}
					buildQueryByOperator(filter, psList, sql);
				} catch (Exception e) {
					//
				}
			}
		}

		if (sql.length() > 0) {
			return " Where ".concat(sql.toString());
		}

		return "";
	}

	private static void buildQueryByOperator(Filter filter, List<Object> psList, StringBuilder sql) throws Exception {

		String property = filter.getProperty();
		sql.append(property);

		switch (filter.getOperator()) {
		case 0:
			sql.append(" = ");
			sql.append("? ");

			psList.add(filter.getValue());
			break;
		case 1:
			sql.append(" <> ");
			sql.append("? ");

			psList.add(filter.getValue());
			break;
		case 2:
			String sql2 = "";
			sql.append(" like ? ");
			if (App.DATABASE == Database.POSTGRES) {
				sql2 = sql.toString().replaceAll("(?i)like", "ilike");
			}

			sql = new StringBuilder(sql2);

			psList.add("%" + filter.getValue() + "%");
			break;
		case 3:
			sql.append(" > ");
			sql.append("? ");

			psList.add(filter.getValue());
			break;
		case 4:
			sql.append(" <= ");
			sql.append("? ");

			psList.add(filter.getValue());
			break;
		case 5:
			sql.append(" >= ");
			sql.append("? ");

			psList.add(filter.getValue());
			break;
		case 6:
			sql.append(" LIKE ");
			sql.append("%?% ");

			psList.add(filter.getValue());
			break;
		case 10:
			sql.append(" IS NULL ");

			break;
		case 11:
			sql.append(" IS NOT NULL ");
			break;
		case 8:
			sql.append(" IN (");
			commaJoin(sql, filter.getValue(), psList);

			break;
		case 9:
			sql.append(" NOT IN (");
			commaJoin(sql, filter.getValue(), psList);

			break;
		default:
			break;
		}

	}

	private static void commaJoin(StringBuilder sql, Object value, List<Object> psList) throws Exception {
		List<Object> inList = Arrays.asList(value);
		for (Object object : inList) {
			String valu = String.valueOf(object);
			String[] split = new String[] {};
			if (valu.contains(",")) {
				split = valu.split(",");

			} else {
				split[0] = valu;
			}

			for (String s1 : split) {
				sql.append(" ?,");
				psList.add(s1);
			}

		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") ");
	}
}
