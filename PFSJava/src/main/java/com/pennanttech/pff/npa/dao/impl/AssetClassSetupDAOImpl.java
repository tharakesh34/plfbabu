package com.pennanttech.pff.npa.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
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
import com.pennanttech.pff.npa.dao.AssetClassSetupDAO;
import com.pennanttech.pff.npa.model.AssetClassSetupDetail;
import com.pennanttech.pff.npa.model.AssetClassSetupHeader;

public class AssetClassSetupDAOImpl extends SequenceDao<AssetClassSetupHeader> implements AssetClassSetupDAO {
	private static Logger logger = LogManager.getLogger(AssetClassSetupDAOImpl.class);

	public AssetClassSetupDAOImpl() {
		super();
	}

	@Override
	public AssetClassSetupHeader getAssetClassSetupHeader(long id, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select Id, EntityCode, Active, Code, Description");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Asset_Class_Setup_Header");
		sql.append(type);
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				AssetClassSetupHeader acsh = new AssetClassSetupHeader();

				acsh.setId(rs.getLong("Id"));
				acsh.setEntityCode(rs.getString("EntityCode"));
				acsh.setActive(rs.getBoolean("Active"));
				acsh.setCode(rs.getString("Code"));
				acsh.setDescription(rs.getString("Description"));
				acsh.setVersion(rs.getInt("Version"));
				acsh.setCreatedBy(rs.getLong("CreatedBy"));
				acsh.setCreatedOn(rs.getTimestamp("CreatedOn"));
				acsh.setApprovedBy(rs.getLong("ApprovedBy"));
				acsh.setApprovedOn(rs.getTimestamp("ApprovedOn"));
				acsh.setLastMntBy(rs.getLong("LastMntBy"));
				acsh.setLastMntOn(rs.getTimestamp("LastMntOn"));
				acsh.setRecordStatus(rs.getString("RecordStatus"));
				acsh.setRoleCode(rs.getString("RoleCode"));
				acsh.setNextRoleCode(rs.getString("NextRoleCode"));
				acsh.setTaskId(rs.getString("TaskId"));
				acsh.setNextTaskId(rs.getString("NextTaskId"));
				acsh.setRecordType(rs.getString("RecordType"));
				acsh.setWorkflowId(rs.getLong("WorkflowId"));

				return acsh;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return null;
	}

	@Override
	public boolean isDuplicateKey(long id, String entityCode, TableType tableType) {
		String sql;
		String whereClause = "EntityCode = ? And id != ?";
		Object obj = new Object[] { entityCode, id };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Asset_Class_Setup_Header", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Asset_Class_Setup_Header", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Asset_Class_Setup_Header_Temp", "Asset_Class_Setup_Header" },
					whereClause);
			obj = new Object[] { entityCode, id, entityCode, id };

			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public String save(AssetClassSetupHeader acsh, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into Asset_Class_Setup_Header");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, EntityCode, Code, Description, Version");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (acsh.getId() == Long.MIN_VALUE) {
			acsh.setId(getNextValue("SEQ_ASSET_CLASS_SETUP_HEADER"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, acsh.getId());
				ps.setString(index++, acsh.getEntityCode());
				ps.setString(index++, acsh.getCode());
				ps.setString(index++, acsh.getDescription());
				ps.setInt(index++, acsh.getVersion());
				ps.setLong(index++, acsh.getCreatedBy());
				ps.setTimestamp(index++, acsh.getCreatedOn());
				ps.setLong(index++, JdbcUtil.getLong(acsh.getApprovedBy()));
				ps.setTimestamp(index++, acsh.getApprovedOn());
				ps.setLong(index++, acsh.getLastMntBy());
				ps.setTimestamp(index++, acsh.getLastMntOn());
				ps.setString(index++, acsh.getRecordStatus());
				ps.setString(index++, acsh.getRoleCode());
				ps.setString(index++, acsh.getNextRoleCode());
				ps.setString(index++, acsh.getTaskId());
				ps.setString(index++, acsh.getNextTaskId());
				ps.setString(index++, acsh.getRecordType());
				ps.setLong(index, acsh.getWorkflowId());
			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return Long.toString(acsh.getId());
	}

	@Override
	public void softDelete(long id, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Asset_Class_Setup_Header");
		sql.append(tableType.getSuffix());
		sql.append(" Set Active = ? where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setBoolean(1, false);
			ps.setLong(2, id);
		});
	}

	@Override
	public void update(AssetClassSetupHeader asch, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Asset_Class_Setup_Header");
		sql.append(tableType.getSuffix());
		sql.append(" Set EntityCode = ?, Active = ?, Code = ?, Description = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?,  RecordStatus  = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, asch.getEntityCode());
			ps.setBoolean(index++, asch.isActive());
			ps.setString(index++, asch.getCode());
			ps.setString(index++, asch.getDescription());
			ps.setInt(index++, asch.getVersion());
			ps.setLong(index++, asch.getLastMntBy());
			ps.setTimestamp(index++, asch.getLastMntOn());
			ps.setString(index++, asch.getRecordStatus());
			ps.setString(index++, asch.getRoleCode());
			ps.setString(index++, asch.getNextRoleCode());
			ps.setString(index++, asch.getTaskId());
			ps.setString(index++, asch.getNextTaskId());
			ps.setString(index++, asch.getRecordType());
			ps.setLong(index++, asch.getWorkflowId());

			ps.setLong(index, asch.getId());
		});
	}

	@Override
	public void delete(AssetClassSetupHeader AssetClassSetupHeader, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From ASSET_CLASS_SETUP_HEADER");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ? ");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, AssetClassSetupHeader.getId()));
	}

	@Override
	public boolean isAssetEntityCodeExists(String entityCode, String code, TableType type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(entityCode) From ASSET_CLASS_SETUP_HEADER");
		sql.append(type.getSuffix());
		sql.append(" Where EntityCode = ? and Code = ? and Active = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), entityCode, code,
				1) > 0;
	}

	private List<AssetClassSetupDetail> getDetails(long setupID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" acsd.Id, DpdMin, DpdMax, ClassID, SubClassID, NpaStage, NpaAge");
		sql.append(", acc.Code ClassCode, ascc.Code SubClassCode");
		sql.append(" From Asset_Class_Setup_Details acsd");
		sql.append(" Inner Join Asset_Class_Codes acc On acc.Id = acsd.ClassID");
		sql.append(" Inner Join Asset_Sub_Class_Codes ascc On ascc.Id = acsd.SubClassID");
		sql.append(" Where SetupId = ?");
		sql.append(" Order by DpdMin desc");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			AssetClassSetupDetail acsd = new AssetClassSetupDetail();

			acsd.setId(rs.getLong("Id"));
			acsd.setDpdMin(rs.getInt("DpdMin"));
			acsd.setDpdMax(rs.getInt("DpdMax"));
			acsd.setClassID(rs.getLong("ClassID"));
			acsd.setSubClassID(rs.getLong("SubClassID"));
			acsd.setNpaStage(rs.getBoolean("NpaStage"));
			acsd.setNpaAge(rs.getInt("NpaAge"));
			acsd.setClassCode(rs.getString("ClassCode"));
			acsd.setSubClassCode(rs.getString("SubClassCode"));

			return acsd;
		}, setupID);
	}

	@Override
	public long saveDetail(AssetClassSetupDetail acsd, String type) {
		if (acsd.getId() == Long.MIN_VALUE) {
			acsd.setId(getNextValue("Seq_Asset_Class_Setup_Details"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Asset_Class_Setup_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Id, SetupID, DPDMin, DPDMax, ClassID, SubClassID, NPAStage, NPAAge");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(")");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, acsd.getId());
				ps.setLong(index++, acsd.getSetupID());
				ps.setInt(index++, acsd.getDpdMin());
				ps.setInt(index++, acsd.getDpdMax());
				ps.setLong(index++, acsd.getClassID());
				ps.setLong(index++, acsd.getSubClassID());
				ps.setBoolean(index++, acsd.isNpaStage());
				ps.setLong(index++, acsd.getNpaAge());
				ps.setInt(index++, acsd.getVersion());
				ps.setLong(index++, acsd.getCreatedBy());
				ps.setTimestamp(index++, acsd.getCreatedOn());
				ps.setLong(index++, JdbcUtil.getLong(acsd.getApprovedBy()));
				ps.setTimestamp(index++, acsd.getApprovedOn());
				ps.setLong(index++, acsd.getLastMntBy());
				ps.setTimestamp(index++, acsd.getLastMntOn());
				ps.setString(index++, acsd.getRecordStatus());
				ps.setString(index++, acsd.getRoleCode());
				ps.setString(index++, acsd.getNextRoleCode());
				ps.setString(index++, acsd.getTaskId());
				ps.setString(index++, acsd.getNextTaskId());
				ps.setString(index++, acsd.getRecordType());
				ps.setLong(index, acsd.getWorkflowId());
			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return acsd.getId();
	}

	@Override
	public void updateDetail(AssetClassSetupDetail acsd, String type) {
		StringBuilder sql = new StringBuilder("Update Asset_Class_Setup_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set SetupID = ?, DPDMin = ?, DPDMax = ?");
		sql.append(", ClassID = ?,  SubClassID = ?, NPAStage = ?");
		sql.append(", NPAAge = ?,  Version = ?, CreatedBy = ?");
		sql.append(", CreatedOn = ?,  ApprovedBy = ?, ApprovedOn = ?");
		sql.append(", LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, acsd.getSetupID());
			ps.setInt(index++, acsd.getDpdMin());
			ps.setInt(index++, acsd.getDpdMax());
			ps.setLong(index++, acsd.getClassID());
			ps.setLong(index++, acsd.getSubClassID());
			ps.setBoolean(index++, acsd.isNpaStage());
			ps.setLong(index++, acsd.getNpaAge());
			ps.setInt(index++, acsd.getVersion());
			ps.setLong(index++, acsd.getCreatedBy());
			ps.setTimestamp(index++, acsd.getCreatedOn());
			ps.setLong(index++, JdbcUtil.getLong(acsd.getApprovedBy()));
			ps.setTimestamp(index++, acsd.getApprovedOn());
			ps.setLong(index++, acsd.getLastMntBy());
			ps.setTimestamp(index++, acsd.getLastMntOn());
			ps.setString(index++, acsd.getRecordStatus());
			ps.setString(index++, acsd.getRoleCode());
			ps.setString(index++, acsd.getNextRoleCode());
			ps.setString(index++, acsd.getTaskId());
			ps.setString(index++, acsd.getNextTaskId());
			ps.setString(index++, acsd.getRecordType());
			ps.setLong(index++, acsd.getWorkflowId());

			ps.setLong(index, acsd.getId());
		});
	}

	@Override
	public void deleteDetail(AssetClassSetupDetail acsd, String type) {
		StringBuilder sql = new StringBuilder("Delete From Asset_Class_Setup_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, acsd.getId()));
	}

	@Override
	public AssetClassSetupDetail getAssetClassSetupDetailByID(AssetClassSetupDetail acsd, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, SetupID, DPDMin, DPDMax, ClassID, SubClassID, NPAStage, NPAAge");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" FROM Asset_Class_Setup_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				AssetClassSetupDetail item = new AssetClassSetupDetail();

				item.setId(rs.getLong("Id"));
				item.setSetupID(rs.getLong("SetupId"));
				item.setDpdMin(rs.getInt("DpdMin"));
				item.setDpdMax(rs.getInt("DpdMax"));
				item.setClassID(rs.getLong("ClassID"));
				item.setSubClassID(rs.getLong("SubClassID"));
				item.setNpaStage(rs.getBoolean("NpaStage"));
				item.setNpaAge(rs.getInt("NpaAge"));
				item.setVersion(rs.getInt("Version"));
				item.setCreatedBy(rs.getLong("CreatedBy"));
				item.setCreatedOn(rs.getTimestamp("CreatedOn"));
				item.setApprovedBy(rs.getLong("ApprovedBy"));
				item.setApprovedOn(rs.getTimestamp("ApprovedOn"));
				item.setLastMntBy(rs.getLong("LastMntBy"));
				item.setLastMntOn(rs.getTimestamp("LastMntOn"));
				item.setRecordStatus(rs.getString("RecordStatus"));
				item.setRoleCode(rs.getString("RoleCode"));
				item.setNextRoleCode(rs.getString("NextRoleCode"));
				item.setTaskId(rs.getString("TaskId"));
				item.setNextTaskId(rs.getString("NextTaskId"));
				item.setRecordType(rs.getString("RecordType"));
				item.setWorkflowId(rs.getLong("WorkflowId"));

				return item;
			}, acsd.getId());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public List<AssetClassSetupDetail> getAssetClassSetupDetailBySetupID(long id, String suffix) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, SetupId, DpdMin, DpdMax, ClassID, SubClassID, NpaStage, NpaAge");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Asset_Class_Setup_Details");
		sql.append(StringUtils.trimToEmpty(suffix));
		sql.append(" Where SetupId = ?");

		logger.debug(Literal.SQL + sql);

		List<AssetClassSetupDetail> list = jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			AssetClassSetupDetail detail = new AssetClassSetupDetail();

			detail.setId(rs.getLong("Id"));
			detail.setSetupID(rs.getLong("SetupId"));
			detail.setDpdMin(rs.getInt("DpdMin"));
			detail.setDpdMax(rs.getInt("DpdMax"));
			detail.setClassID(rs.getLong("ClassID"));
			detail.setSubClassID(rs.getLong("SubClassID"));
			detail.setNpaStage(rs.getBoolean("NpaStage"));
			detail.setNpaAge(rs.getInt("NpaAge"));
			detail.setVersion(rs.getInt("Version"));
			detail.setCreatedBy(rs.getLong("CreatedBy"));
			detail.setCreatedOn(rs.getTimestamp("CreatedOn"));
			detail.setApprovedBy(rs.getLong("ApprovedBy"));
			detail.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			detail.setLastMntBy(rs.getLong("LastMntBy"));
			detail.setLastMntOn(rs.getTimestamp("LastMntOn"));
			detail.setRecordStatus(rs.getString("RecordStatus"));
			detail.setRoleCode(rs.getString("RoleCode"));
			detail.setNextRoleCode(rs.getString("NextRoleCode"));
			detail.setTaskId(rs.getString("TaskId"));
			detail.setNextTaskId(rs.getString("NextTaskId"));
			detail.setRecordType(rs.getString("RecordType"));
			detail.setWorkflowId(rs.getLong("WorkflowId"));

			return detail;
		}, id);

		return list.stream().sorted((l1, l2) -> Long.compare(l1.getId(), l2.getId())).collect(Collectors.toList());
	}

	@Override
	public void deleteDetailBySetupID(long setupID, String type) {
		StringBuilder sql = new StringBuilder("Delete From Asset_Class_Setup_Details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where setupID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, setupID));
	}

	@Override
	public List<AssetClassSetupHeader> getAssetClassSetups() {
		StringBuilder sql = new StringBuilder();

		sql.append("Select acsh.Id, ft.FinType");
		sql.append(" From Asset_Class_Setup_Header acsh");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.AssetClassSetup = acsh.Id");
		sql.append(" Where Active = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			AssetClassSetupHeader header = new AssetClassSetupHeader();

			header.setId(rs.getLong("Id"));
			header.setFinType(rs.getString("FinType"));

			header.setDetails(getDetails(header.getId()));

			return header;
		}, 1);
	}

	@Override
	public boolean checkDependency(long assetClassSetupId) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(ID) From RMTfinanceTypes");
		sql.append(" Where AssetClassSetup = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), assetClassSetupId) > 0;
	}

	@Override
	public List<String> getAssetClassSetCodes(long finID) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select acc.Code ClassCode, ascc.Code SubClassCode From Asset_Class_Setup_Details acsd ");
		sql.append(" Inner Join Asset_Class_Codes acc On acc.Id = acsd.ClassID");
		sql.append(" Inner Join Asset_Sub_Class_Codes ascc On ascc.Id = acsd.SubClassID");
		sql.append(" Inner Join NPA_Loan_Info na on na.PastDueDays <= acsd.DPDMAX and na.FinID =?");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType = na.FinType and acsd.SetupId = ft.AssetClassSetup");
		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID), (rs, rowNum) -> rs.getString(1));
	}
}