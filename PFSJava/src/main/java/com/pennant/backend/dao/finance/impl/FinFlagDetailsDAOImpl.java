package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinFlagDetailsDAOImpl extends BasicDao<FinFlagsDetail> implements FinFlagDetailsDAO {
	private static Logger logger = LogManager.getLogger(FinFlagDetailsDAOImpl.class);

	public FinFlagDetailsDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceFlag
	 * 
	 * @return FinanceFlag
	 */
	@Override
	public FinFlagsDetail getfinFlagDetails() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("");

		FinFlagsDetail finFlagsDetail = new FinFlagsDetail();
		if (workFlowDetails != null) {
			finFlagsDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return finFlagsDetail;

	}

	/**
	 * This method get the module from method getFinanceFlags() and set the new record flag as true and return
	 * FinanceFlag
	 * 
	 * @return FinanceFlag
	 */
	@Override
	public FinFlagsDetail getNewFinFlagsDetail() {
		logger.debug("Entering");
		FinFlagsDetail finFlagsDetail = getfinFlagDetails();
		finFlagsDetail.setNewRecord(true);
		logger.debug("Leaving");
		return finFlagsDetail;
	}

	/**
	 * This method insert new Records into FinanceFlags or FlagDetails_Temp.
	 * 
	 * save FinanceFlags
	 * 
	 * @param FinanceFlags (finFlagsDetail)
	 * @param type         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(FinFlagsDetail finFlagsDetail, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" FlagDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Reference,FlagCode,ModuleName,");
		insertSql.append(
				" Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(" values (:Reference,:FlagCode,:ModuleName, ");
		insertSql.append(" :Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");

		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFlagsDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public List<FinFlagsDetail> getFinFlagsByFinRef(String finReference, String moduleName, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Reference, FlagCode, ModuleName, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from FlagDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Reference = ? and ModuleName = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, finReference);
			ps.setString(index, moduleName);
		}, (rs, rowNum) -> {
			FinFlagsDetail fd = new FinFlagsDetail();

			fd.setReference(rs.getString("Reference"));
			fd.setFlagCode(rs.getString("FlagCode"));
			fd.setModuleName(rs.getString("ModuleName"));
			fd.setVersion(rs.getInt("Version"));
			fd.setLastMntBy(rs.getLong("LastMntBy"));
			fd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fd.setRecordStatus(rs.getString("RecordStatus"));
			fd.setRoleCode(rs.getString("RoleCode"));
			fd.setNextRoleCode(rs.getString("NextRoleCode"));
			fd.setTaskId(rs.getString("TaskId"));
			fd.setNextTaskId(rs.getString("NextTaskId"));
			fd.setRecordType(rs.getString("RecordType"));
			fd.setWorkflowId(rs.getLong("WorkflowId"));

			return fd;
		});
	}

	/**
	 * Fetch the Record Finance Flags details by key field
	 * 
	 * @param finRef (String)
	 * @param type   (String) ""/_Temp/_View
	 * @return finFlagsDetail
	 */
	@Override
	public FinFlagsDetail getFinFlagsByRef(final String finRef, String flagCode, String moduleName, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Reference, FlagCode, ModuleName, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" From FlagDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Reference = ? and FlagCode = ? and ModuleName = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				FinFlagsDetail ffd = new FinFlagsDetail();

				ffd.setReference(rs.getString("Reference"));
				ffd.setFlagCode(rs.getString("FlagCode"));
				ffd.setModuleName(rs.getString("ModuleName"));
				ffd.setVersion(rs.getInt("Version"));
				ffd.setLastMntBy(rs.getLong("LastMntBy"));
				ffd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ffd.setRecordStatus(rs.getString("RecordStatus"));
				ffd.setRoleCode(rs.getString("RoleCode"));
				ffd.setNextRoleCode(rs.getString("NextRoleCode"));
				ffd.setTaskId(rs.getString("TaskId"));
				ffd.setNextTaskId(rs.getString("NextTaskId"));
				ffd.setRecordType(rs.getString("RecordType"));
				ffd.setWorkflowId(rs.getLong("WorkflowId"));

				return ffd;
			}, finRef, flagCode, moduleName);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Record not found in FlagDetails{} table for the specified Reference >> {}, FlagCode >> {} and ModuleName >> {}",
					type, finRef, flagCode, moduleName);
		}

		return null;
	}

	@Override
	public void deleteList(String finRef, String module, String type) {
		logger.debug("Entering");
		FinFlagsDetail finFlagsDetail = new FinFlagsDetail();
		finFlagsDetail.setReference(finRef);
		finFlagsDetail.setModuleName(module);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FlagDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Reference =:Reference AND ModuleName = :ModuleName ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFlagsDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	public void delete(String finRef, String flagCode, String moduleName, String type) {
		logger.debug("Entering");
		FinFlagsDetail finFlagsDetail = new FinFlagsDetail();
		finFlagsDetail.setReference(finRef);
		finFlagsDetail.setFlagCode(flagCode);
		finFlagsDetail.setModuleName(moduleName);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FlagDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Reference =:Reference AND FlagCode =:FlagCode AND ModuleName =:ModuleName");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFlagsDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<String> getScheduleEffectModuleList(boolean schdChangeReq) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("schdChangeReq", schdChangeReq);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT ModuleName FROM ScheduleEffectModule WHERE SchdCanModify =:schdChangeReq ");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		return this.jdbcTemplate.queryForList(selectSql.toString(), source, String.class);
	}

	@Override
	public void savefinFlagList(List<FinFlagsDetail> finFlagsDetail, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" FlagDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Reference,FlagCode,ModuleName,");
		insertSql.append(
				" Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(" values (:Reference,:FlagCode,:ModuleName, ");
		insertSql.append(" :Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");

		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finFlagsDetail.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void updateList(List<FinFlagsDetail> finFlagsDetail, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FlagDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Version = :Version,");
		updateSql.append(" LastMntBy = :LastMntBy , LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, ");
		updateSql.append(
				" RoleCode= :RoleCode, NextRoleCode = :NextRoleCode,TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Reference =:Reference  AND FlagCode =:FlagCode AND ModuleName = :ModuleName ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finFlagsDetail.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	/**
	 * Method for Updating Finance Flag Details
	 * 
	 * @param finFlagsDetail
	 * @param type
	 */
	@Override
	public void update(FinFlagsDetail finFlagsDetail, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FlagDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Version = :Version,");
		updateSql.append(" LastMntBy = :LastMntBy , LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, ");
		updateSql.append(
				" RoleCode= :RoleCode, NextRoleCode = :NextRoleCode,TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Reference =:Reference  AND FlagCode =:FlagCode AND ModuleName=:ModuleName ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFlagsDetail);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	/**
	 * 
	 * @param finReference
	 * @param type
	 * @return Integer
	 */
	@Override
	public int getFinFlagDetailCountByRef(String finReference, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM FlagDetails WHERE Reference = :Reference ");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}
}
