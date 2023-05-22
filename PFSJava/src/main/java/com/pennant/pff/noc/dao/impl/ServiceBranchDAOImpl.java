package com.pennant.pff.noc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.noc.dao.ServiceBranchDAO;
import com.pennant.pff.noc.model.ServiceBranch;
import com.pennant.pff.noc.model.ServiceBranchesLoanType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class ServiceBranchDAOImpl extends SequenceDao<ServiceBranch> implements ServiceBranchDAO {

	public ServiceBranchDAOImpl() {
		super();
	}

	@Override
	public ServiceBranch getServiceBranch(long id) {
		StringBuilder sql = getSqlQuery(TableType.TEMP_TAB);
		sql.append(" Where csb.Id = ?");
		sql.append(" Union all ");
		sql.append(getSqlQuery(TableType.MAIN_TAB));
		sql.append(" Where csb.Id = ?");
		sql.append(" and not exists (Select 1 From Service_Branches_Temp Where Id = csb.Id)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			ServiceBranch csb = this.jdbcOperations.queryForObject(sql.toString(), new ServiceBranchRM(), id, id);
			csb.setServiceBranchLoanTypeList(getLoanTypeList(csb.getId()));
			return csb;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<ServiceBranch> getServiceBranches(List<String> roleCodes) {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" Id, Code, Description, OfcOrHouseNum, FlatNum, Street");
		sql.append(", AddrLine1, AddrLine2,  PoBox, Country, City, CpProvince, PinCodeId, PinCode, FolderPath, Active");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From (");
		sql.append(getSqlQuery(TableType.TEMP_TAB));
		sql.append(" Union All ");
		sql.append(getSqlQuery(TableType.MAIN_TAB));
		sql.append(" Where not exists (Select 1 From Service_Branches_Temp Where Id = csb.Id)) p");
		sql.append(" Where NextRoleCode is null or NextRoleCode = ? or NextRoleCode in (");
		sql.append(JdbcUtil.getInCondition(roleCodes));
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<ServiceBranch> list = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, "");
			for (String roleCode : roleCodes) {
				ps.setString(++index, roleCode);
			}
		}, new ServiceBranchRM());

		return list.stream().sorted((l1, l2) -> l1.getCode().compareTo(l2.getCode())).collect(Collectors.toList());
	}

	@Override
	public List<ReportListDetail> getPrintServiceBranches(List<String> roleCodes) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Code, Description, PinCode, City");
		sql.append(" From (Select Code, Description, PinCode, City");
		sql.append(" From Service_Branches_Temp csb");
		sql.append(" Union All ");
		sql.append(" Select Code, Description, PinCode, City");
		sql.append(" From Service_Branches csb");
		sql.append(" Where csb.NextRoleCode is null or csb.NextRoleCode = ? or csb.NextRoleCode in (");
		sql.append(JdbcUtil.getInCondition(roleCodes));
		sql.append(") Order By Code)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, "");
			for (String roleCode : roleCodes) {
				ps.setString(++index, roleCode);
			}
		}, new ReportListRM());
	}

	@Override
	public List<ServiceBranch> getResult(ISearch search) {
		List<Object> value = new ArrayList<>();

		StringBuilder sql = new StringBuilder("select");
		sql.append(" Id, Code, Description, OfcOrHouseNum, FlatNum, Street");
		sql.append(", AddrLine1, AddrLine2,  PoBox, Country, City, CpProvince, PinCodeId, PinCode, FolderPath, Active");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn, Active, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From (");
		sql.append(getSqlQuery(TableType.TEMP_TAB));
		sql.append(" Union All ");
		sql.append(getSqlQuery(TableType.MAIN_TAB));
		sql.append(" Where not exists (Select 1 From Service_Branches_Temp Where Id = csb.Id)) csb");
		sql.append(QueryUtil.buildWhereClause(search, value));

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<ServiceBranch> list = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			for (Object object : value) {
				ps.setObject(++index, object);
			}
		}, new ServiceBranchRM());

		return list.stream().sorted((l1, l2) -> l1.getCode().compareTo(l2.getCode())).collect(Collectors.toList());
	}

	@Override
	public void delete(ServiceBranch sb, TableType type) {
		StringBuilder sql = new StringBuilder("Delete From Service_Branches");
		sql.append(type.getSuffix());
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			if (this.jdbcOperations.update(sql.toString(), sb.getId()) == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(ServiceBranch sb, TableType type) {
		if (sb.getId() == 0 || sb.getId() == Long.MIN_VALUE) {
			sb.setId(getNextValue("SEQ_Service_Branches"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Service_Branches");
		sql.append(type.getSuffix());
		sql.append("(Id, Code, Description, OfcOrHouseNum, FlatNum, Street");
		sql.append(", AddrLine1, AddrLine2,  PoBox, Country, City, CpProvince, PinCodeId, PinCode, FolderPath, Active");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setLong(++index, sb.getId());
				ps.setString(++index, sb.getCode());
				ps.setString(++index, sb.getDescription());
				ps.setString(++index, sb.getOfcOrHouseNum());
				ps.setString(++index, sb.getFlatNum());
				ps.setString(++index, sb.getStreet());
				ps.setString(++index, sb.getAddrLine1());
				ps.setString(++index, sb.getAddrLine2());
				ps.setString(++index, sb.getPoBox());
				ps.setString(++index, sb.getCountry());
				ps.setString(++index, sb.getCity());
				ps.setString(++index, sb.getCpProvince());
				ps.setLong(++index, sb.getPinCodeId());
				ps.setString(++index, sb.getPinCode());
				ps.setString(++index, sb.getFolderPath());
				ps.setBoolean(++index, sb.isActive());
				ps.setInt(++index, sb.getVersion());
				ps.setLong(++index, sb.getCreatedBy());
				ps.setTimestamp(++index, sb.getCreatedOn());
				ps.setObject(++index, sb.getApprovedBy());
				ps.setTimestamp(++index, sb.getApprovedOn());
				ps.setLong(++index, sb.getLastMntBy());
				ps.setTimestamp(++index, sb.getLastMntOn());
				ps.setString(++index, sb.getRecordStatus());
				ps.setString(++index, sb.getRoleCode());
				ps.setString(++index, sb.getNextRoleCode());
				ps.setString(++index, sb.getTaskId());
				ps.setString(++index, sb.getNextTaskId());
				ps.setString(++index, sb.getRecordType());
				ps.setLong(++index, sb.getWorkflowId());

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return sb.getId();
	}

	@Override
	public void update(ServiceBranch sb, TableType type) {
		StringBuilder sql = new StringBuilder("Update Service_Branches");
		sql.append(type.getSuffix());
		sql.append(" Set Description = ?, OfcOrHouseNum = ?, FlatNum = ?, Street = ?");
		sql.append(", AddrLine1 = ?, AddrLine2 = ?, POBox = ?, Country = ?, City = ?");
		sql.append(", CpProvince= ?,  PinCodeId = ?, PinCode = ?, FolderPath = ?");
		sql.append(", Active = ?, Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setString(++index, sb.getDescription());
				ps.setString(++index, sb.getOfcOrHouseNum());
				ps.setString(++index, sb.getFlatNum());
				ps.setString(++index, sb.getStreet());
				ps.setString(++index, sb.getAddrLine1());
				ps.setString(++index, sb.getAddrLine2());
				ps.setString(++index, sb.getPoBox());
				ps.setString(++index, sb.getCountry());
				ps.setString(++index, sb.getCity());
				ps.setString(++index, sb.getCpProvince());
				ps.setLong(++index, sb.getPinCodeId());
				ps.setString(++index, sb.getPinCode());
				ps.setString(++index, sb.getFolderPath());
				ps.setBoolean(++index, sb.isActive());
				ps.setInt(++index, sb.getVersion());
				ps.setLong(++index, sb.getLastMntBy());
				ps.setTimestamp(++index, sb.getLastMntOn());
				ps.setString(++index, sb.getRecordStatus());
				ps.setString(++index, sb.getRoleCode());
				ps.setString(++index, sb.getNextRoleCode());
				ps.setString(++index, sb.getTaskId());
				ps.setString(++index, sb.getNextTaskId());
				ps.setString(++index, sb.getRecordType());
				ps.setLong(++index, sb.getWorkflowId());

				ps.setLong(++index, sb.getId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public boolean isDuplicateKey(String code, TableType type) {
		Object[] parameters = new Object[] { code };

		String sql;
		String whereClause = "Code = ?";

		switch (type) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Service_Branches", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Service_Branches_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Service_Branches", "Service_Branches_Temp" }, whereClause);
			parameters = new Object[] { code, code };
			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, parameters) > 0;
	}

	@Override
	public void saveLoanType(ServiceBranchesLoanType sb, TableType type) {
		if (sb.getId() == 0 || sb.getId() == Long.MIN_VALUE) {
			sb.setId(getNextValue("SEQ_Service_Branches_LoanType"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Service_Branches_LoanType");
		sql.append(type.getSuffix());
		sql.append(" (Id, HeaderId, FinType, Branch");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId )");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setLong(++index, sb.getId());
				ps.setLong(++index, sb.getHeaderId());
				ps.setString(++index, sb.getFinType());
				ps.setString(++index, sb.getBranch());
				ps.setInt(++index, sb.getVersion());
				ps.setLong(++index, sb.getCreatedBy());
				ps.setTimestamp(++index, sb.getCreatedOn());
				ps.setObject(++index, sb.getApprovedBy());
				ps.setTimestamp(++index, sb.getApprovedOn());
				ps.setLong(++index, sb.getLastMntBy());
				ps.setTimestamp(++index, sb.getLastMntOn());
				ps.setString(++index, sb.getRecordStatus());
				ps.setString(++index, sb.getRoleCode());
				ps.setString(++index, sb.getNextRoleCode());
				ps.setString(++index, sb.getTaskId());
				ps.setString(++index, sb.getNextTaskId());
				ps.setString(++index, sb.getRecordType());
				ps.setLong(++index, sb.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

	}

	@Override
	public void delete(ServiceBranchesLoanType sb, TableType type) {
		StringBuilder sql = new StringBuilder("Delete From Service_Branches_LoanType");
		sql.append(type.getSuffix());
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), sb.getHeaderId());
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void deleteBranchLoanTypeById(long id, TableType type) {
		StringBuilder sql = new StringBuilder("Delete From Service_Branches_LoanType");
		sql.append(type.getSuffix());
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), id);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void updateLoanType(ServiceBranchesLoanType sb, TableType type) {
		StringBuilder sql = new StringBuilder("Update Service_Branches_LoanType");
		sql.append(type.getSuffix());
		sql.append(" Set FinType = ?, Branch = ?");
		sql.append(", Version = ?, ApprovedBy = ?, ApprovedOn = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ? and HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setString(++index, sb.getFinType());
				ps.setString(++index, sb.getBranch());
				ps.setInt(++index, sb.getVersion());
				ps.setObject(++index, sb.getApprovedBy());
				ps.setTimestamp(++index, sb.getApprovedOn());
				ps.setLong(++index, sb.getLastMntBy());
				ps.setTimestamp(++index, sb.getLastMntOn());
				ps.setString(++index, sb.getRecordStatus());
				ps.setString(++index, sb.getRoleCode());
				ps.setString(++index, sb.getNextRoleCode());
				ps.setString(++index, sb.getTaskId());
				ps.setString(++index, sb.getNextTaskId());
				ps.setString(++index, sb.getRecordType());
				ps.setLong(++index, sb.getWorkflowId());

				ps.setLong(++index, sb.getId());
				ps.setLong(++index, sb.getHeaderId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	private List<ServiceBranchesLoanType> getLoanTypeList(long headerId) {
		StringBuilder sql = new StringBuilder(getLoanTypeSqlQuery(TableType.TEMP_TAB));
		sql.append(" Where csb.HeaderId = ?");
		sql.append(" Union all ");
		sql.append(getLoanTypeSqlQuery(TableType.MAIN_TAB));
		sql.append(" Where csb.HeaderId = ?");
		sql.append(" and not exists (Select 1 From Service_Branches_LoanType_Temp Where Id = csb.Id)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, headerId);
			ps.setLong(2, headerId);
		}, new LoanTypeRM());
	}

	private StringBuilder getLoanTypeSqlQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" csb.Id, csb.HeaderId, csb.FinType, csb.Branch");
		sql.append(", csb.Version, csb.CreatedBy, csb.CreatedOn, csb.ApprovedBy, csb.ApprovedOn");
		sql.append(", csb.LastMntBy, csb.LastMntOn, csb.RecordStatus, csb.RoleCode");
		sql.append(", csb.NextRoleCode, csb.TaskId, csb.NextTaskId, csb.RecordType, csb.WorkflowId");
		sql.append(" From Service_Branches_LoanType").append(tableType.getSuffix()).append(" csb");

		return sql;
	}

	private StringBuilder getSqlQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" csb.Id, csb.Code, csb.Description, csb.OfcOrHouseNum, csb.FlatNum");
		sql.append(", csb.Street, csb.AddrLine1, csb.AddrLine2");
		sql.append(", csb.PoBox, csb.Country, csb.City, csb.CpProvince, csb.PinCodeId, csb.PinCode, csb.FolderPath");
		sql.append(", csb.Active, csb.Version, csb.CreatedBy, csb.CreatedOn, csb.ApprovedBy, csb.ApprovedOn");
		sql.append(", csb.LastMntBy, csb.LastMntOn, csb.RecordStatus, csb.RoleCode");
		sql.append(", csb.NextRoleCode, csb.TaskId, csb.NextTaskId, csb.RecordType, csb.WorkflowId");
		sql.append(" From Service_Branches").append(tableType.getSuffix()).append(" csb");

		return sql;
	}

	private class ServiceBranchRM implements RowMapper<ServiceBranch> {

		private ServiceBranchRM() {
			super();
		}

		@Override
		public ServiceBranch mapRow(ResultSet rs, int rowNum) throws SQLException {
			ServiceBranch sb = new ServiceBranch();

			sb.setId(rs.getLong("Id"));
			sb.setCode(rs.getString("Code"));
			sb.setDescription(rs.getString("Description"));
			sb.setOfcOrHouseNum(rs.getString("OfcOrHouseNum"));
			sb.setFlatNum(rs.getString("FlatNum"));
			sb.setStreet(rs.getString("Street"));
			sb.setAddrLine1(rs.getString("AddrLine1"));
			sb.setAddrLine2(rs.getString("AddrLine2"));
			sb.setPoBox(rs.getString("PoBox"));
			sb.setCountry(rs.getString("Country"));
			sb.setCity(rs.getString("City"));
			sb.setCpProvince(rs.getString("CpProvince"));
			sb.setPinCodeId(rs.getLong("PinCodeId"));
			sb.setPinCode(rs.getString("PinCode"));
			sb.setFolderPath(rs.getString("FolderPath"));
			sb.setActive(rs.getBoolean("Active"));
			sb.setVersion(rs.getInt("Version"));
			sb.setCreatedBy(rs.getLong("CreatedBy"));
			sb.setCreatedOn(rs.getTimestamp("CreatedOn"));
			sb.setApprovedBy(rs.getLong("ApprovedBy"));
			sb.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			sb.setLastMntBy(rs.getLong("LastMntBy"));
			sb.setLastMntOn(rs.getTimestamp("LastMntOn"));
			sb.setRecordStatus(rs.getString("RecordStatus"));
			sb.setRoleCode(rs.getString("RoleCode"));
			sb.setNextRoleCode(rs.getString("NextRoleCode"));
			sb.setTaskId(rs.getString("TaskId"));
			sb.setNextTaskId(rs.getString("NextTaskId"));
			sb.setRecordType(rs.getString("RecordType"));
			sb.setWorkflowId(rs.getLong("WorkflowId"));

			return sb;
		}
	}

	private class ReportListRM implements RowMapper<ReportListDetail> {

		private ReportListRM() {
			super();
		}

		@Override
		public ReportListDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReportListDetail bc = new ReportListDetail();

			bc.setfieldString01(rs.getString("Code"));
			bc.setfieldString02(rs.getString("Description"));
			bc.setfieldString03(rs.getString("PinCode"));
			bc.setfieldString04(rs.getString("City"));

			return bc;
		}
	}

	private class LoanTypeRM implements RowMapper<ServiceBranchesLoanType> {

		private LoanTypeRM() {
			super();
		}

		@Override
		public ServiceBranchesLoanType mapRow(ResultSet rs, int rowNum) throws SQLException {
			ServiceBranchesLoanType sb = new ServiceBranchesLoanType();

			sb.setId(rs.getLong("Id"));
			sb.setHeaderId(rs.getLong("HeaderId"));
			sb.setFinType(rs.getString("FinType"));
			sb.setBranch(rs.getString("Branch"));
			sb.setVersion(rs.getInt("Version"));
			sb.setCreatedBy(rs.getLong("CreatedBy"));
			sb.setCreatedOn(rs.getTimestamp("CreatedOn"));
			sb.setApprovedBy(rs.getLong("ApprovedBy"));
			sb.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			sb.setLastMntBy(rs.getLong("LastMntBy"));
			sb.setLastMntOn(rs.getTimestamp("LastMntOn"));
			sb.setRecordStatus(rs.getString("RecordStatus"));
			sb.setRoleCode(rs.getString("RoleCode"));
			sb.setNextRoleCode(rs.getString("NextRoleCode"));
			sb.setTaskId(rs.getString("TaskId"));
			sb.setNextTaskId(rs.getString("NextTaskId"));
			sb.setRecordType(rs.getString("RecordType"));
			sb.setWorkflowId(rs.getLong("WorkflowId"));

			return sb;
		}
	}
}
