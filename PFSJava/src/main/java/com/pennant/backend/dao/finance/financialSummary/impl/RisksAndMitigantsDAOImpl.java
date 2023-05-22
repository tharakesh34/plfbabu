/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerPhoneNumberDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.financialSummary.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.financialSummary.RisksAndMitigantsDAO;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceCheckList;
import com.pennant.backend.model.finance.financialsummary.RisksAndMitigants;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>CustomerPhoneNumber model</b> class.<br>
 * 
 */
public class RisksAndMitigantsDAOImpl extends SequenceDao<RisksAndMitigants> implements RisksAndMitigantsDAO {
	private static Logger logger = LogManager.getLogger(RisksAndMitigantsDAOImpl.class);

	public RisksAndMitigantsDAOImpl() {
		super();
	}

	public List<RisksAndMitigants> getRisksAndMitigants(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.Id, t1.SeqNo, t1.Risk, t1.Mitigants, t1.FinReference, t1.Version");
		sql.append(", t1.LastMntBy, t1.LastMntOn, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" from Risks_mitigants_temp t1");
		sql.append(" left join FinanceMain t2 on t2.finreference =  t1.finreference");
		sql.append(" where t1.finReference = ?");
		sql.append(" union all");
		sql.append(" Select");
		sql.append(" t1.Id, t1.SeqNo, t1.Risk, t1.Mitigants, t1.FinReference, t1.Version");
		sql.append(", t1.LastMntBy, t1.LastMntOn, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" from  risks_mitigants t1");
		sql.append(" left join FinanceMain t2 on t2.finreference =  t1.finreference");
		sql.append(" Where not exists (Select 1 from Risks_mitigants_temp where id = t1.id)");
		sql.append(" and t1.finReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, finReference);
				ps.setString(index, finReference);
			}
		}, new RowMapper<RisksAndMitigants>() {
			@Override
			public RisksAndMitigants mapRow(ResultSet rs, int rowNum) throws SQLException {
				RisksAndMitigants rm = new RisksAndMitigants();

				rm.setId(rs.getLong("Id"));
				rm.setSeqNo(rs.getLong("SeqNo"));
				rm.setRisk(rs.getString("Risk"));
				rm.setMitigants(rs.getString("Mitigants"));
				rm.setFinReference(rs.getString("FinReference"));
				rm.setVersion(rs.getInt("Version"));
				rm.setLastMntBy(rs.getLong("LastMntBy"));
				rm.setLastMntOn(rs.getTimestamp("LastMntOn"));
				rm.setRecordStatus(rs.getString("RecordStatus"));
				rm.setRoleCode(rs.getString("RoleCode"));
				rm.setNextRoleCode(rs.getString("NextRoleCode"));
				rm.setTaskId(rs.getString("TaskId"));
				rm.setNextTaskId(rs.getString("NextTaskId"));
				rm.setRecordType(rs.getString("RecordType"));
				rm.setWorkflowId(rs.getLong("WorkflowId"));

				return rm;
			}
		});
	}

	public List<DueDiligenceCheckList> getDueDiligenceCheckListDetails() {

		DueDiligenceCheckList dueDiligenceDetail = new DueDiligenceCheckList();

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  T1.id,T1.Particulars,T1.Status");
		selectSql.append(", T1.Version, T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode");
		selectSql.append(", T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId ");
		selectSql.append(" FROM  DUE_DILIGENCE_CHECKLIST T1 order by id");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dueDiligenceDetail);
		RowMapper<DueDiligenceCheckList> typeRowMapper = BeanPropertyRowMapper.newInstance(DueDiligenceCheckList.class);

		List<DueDiligenceCheckList> dueDiligenceDetails = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving ");
		return dueDiligenceDetails;

	}

	@Override
	public void delete(RisksAndMitigants risksAndMitigants, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From RISKS_MITIGANTS");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where id =:id and finReference =:finReference");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(risksAndMitigants);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	@Override
	public long save(RisksAndMitigants risksAndMitigants, String type) {
		logger.debug("Entering");

		if (risksAndMitigants.getId() == Long.MIN_VALUE) {
			risksAndMitigants.setId(getNextValue("SEQ_RISKS_MITIGANTS"));
			logger.debug("get NextID:" + risksAndMitigants.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into RISKS_MITIGANTS");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (id, SeqNo, Risk,Mitigants,FinReference");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		insertSql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:id, :SeqNo, :Risk, :Mitigants, :FinReference");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		insertSql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(risksAndMitigants);

		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug("Leaving");
		return risksAndMitigants.getId();
	}

	@Override
	public void update(RisksAndMitigants risksAndMitigants, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update RISKS_MITIGANTS");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Risk = :Risk, Mitigants = :Mitigants");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		updateSql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		updateSql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType");
		updateSql.append(", WorkflowId = :WorkflowId");
		updateSql.append(" Where id =:id and finReference =:finReference");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(risksAndMitigants);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public int getVersion(long id, String risk) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("risk", risk);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT Version FROM RISKS_MITIGANTS");
		selectSql.append(" WHERE id = :id AND finReference = :finReference");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	public List<InterfaceLogDetail> getInterfaceLogDetails(String reference) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  T1.serviceName,T1.status,T1.errorDesc");
		selectSql.append(" FROM  INTERFACELOGDETAILS T1");
		selectSql.append(" WHERE T1.reference = :reference ");

		logger.debug("selectSql: " + selectSql.toString());
		InterfaceLogDetail interfaceLogDetail = new InterfaceLogDetail();
		interfaceLogDetail.setReference(reference);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interfaceLogDetail);
		RowMapper<InterfaceLogDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(InterfaceLogDetail.class);

		List<InterfaceLogDetail> interfaceLogDetails = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving ");
		return interfaceLogDetails;

	}

}