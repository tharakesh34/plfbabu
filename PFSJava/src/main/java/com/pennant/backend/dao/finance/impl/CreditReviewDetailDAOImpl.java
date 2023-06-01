package com.pennant.backend.dao.finance.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

import com.pennant.backend.dao.finance.CreditReviewDetailDAO;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.ExtBreDetails;
import com.pennant.backend.model.finance.ExtCreditReviewConfig;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

public class CreditReviewDetailDAOImpl extends SequenceDao<CreditReviewDetails> implements CreditReviewDetailDAO {

	private static Logger logger = LogManager.getLogger(CreditReviewDetailDAOImpl.class);

	public CreditReviewDetailDAOImpl() {
		super();
	}

	@Override
	public CreditReviewDetails getCreditReviewDetails(CreditReviewDetails creditReviewDetail) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinCategory, EmploymentType, EligibilityMethod, Section");
		sql.append(", TemplateName, TemplateVersion, Fields, ProtectedCells, FormulaCells");
		sql.append(" From CreditReviewConfig");
		sql.append(" Where EligibilityMethod = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CreditReviewDetails crd = new CreditReviewDetails();

				crd.setId(rs.getLong("Id"));
				crd.setFinCategory(rs.getString("FinCategory"));
				crd.setEmploymentType(rs.getString("EmploymentType"));
				crd.setEligibilityMethod(rs.getString("EligibilityMethod"));
				crd.setSection(rs.getString("Section"));
				crd.setTemplateName(rs.getString("TemplateName"));
				crd.setTemplateVersion(rs.getInt("TemplateVersion"));
				crd.setFields(rs.getString("Fields"));
				crd.setProtectedCells(rs.getString("ProtectedCells"));
				crd.setFormulaCells(rs.getString("FormulaCells"));
				return crd;
			}, creditReviewDetail.getEligibilityMethod());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public CreditReviewData getCreditReviewData(long finID, String templateName) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, TemplateData, TemplateName, TemplateVersion");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CreditReviewData");
		sql.append(" Where FinID = ? and TemplateName = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CreditReviewData crd = new CreditReviewData();

				crd.setFinID(rs.getLong("FinID"));
				crd.setFinReference(rs.getString("FinReference"));
				crd.setTemplateData(rs.getString("TemplateData"));
				crd.setTemplateName(rs.getString("TemplateName"));
				crd.setTemplateVersion(rs.getInt("TemplateVersion"));
				crd.setVersion(rs.getInt("Version"));
				crd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				crd.setLastMntBy(rs.getLong("LastMntBy"));
				crd.setRecordStatus(rs.getString("RecordStatus"));
				crd.setRoleCode(rs.getString("RoleCode"));
				crd.setNextRoleCode(rs.getString("NextRoleCode"));
				crd.setTaskId(rs.getString("TaskId"));
				crd.setNextTaskId(rs.getString("NextTaskId"));
				crd.setRecordType(rs.getString("RecordType"));
				crd.setWorkflowId(rs.getLong("WorkflowId"));

				return crd;
			}, finID, templateName);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void save(CreditReviewData crd) {
		StringBuilder sql = new StringBuilder("Insert Into CreditReviewData");
		sql.append(" (FinID, FinReference, TemplateData, TemplateName, TemplateVersion, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, crd.getFinID());
				ps.setString(index++, crd.getFinReference());
				ps.setString(index++, crd.getTemplateData());
				ps.setString(index++, crd.getTemplateName());
				ps.setInt(index++, crd.getTemplateVersion());
				ps.setInt(index++, crd.getVersion());
				ps.setLong(index++, crd.getLastMntBy());
				ps.setTimestamp(index++, crd.getLastMntOn());
				ps.setString(index++, crd.getRecordStatus());
				ps.setString(index++, crd.getRoleCode());
				ps.setString(index++, crd.getNextRoleCode());
				ps.setString(index++, crd.getTaskId());
				ps.setString(index++, crd.getNextTaskId());
				ps.setString(index++, crd.getRecordType());
				ps.setLong(index, crd.getWorkflowId());

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void update(CreditReviewData crd) {
		StringBuilder sql = new StringBuilder("Update CreditReviewData");
		sql.append(" Set TemplateData = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ? and TemplateVersion = ? and TemplateName = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, crd.getTemplateData());
			ps.setInt(index++, crd.getVersion());
			ps.setLong(index++, crd.getLastMntBy());
			ps.setTimestamp(index++, crd.getLastMntOn());
			ps.setString(index++, crd.getRecordStatus());
			ps.setString(index++, crd.getRoleCode());
			ps.setString(index++, crd.getNextRoleCode());
			ps.setString(index++, crd.getTaskId());
			ps.setString(index++, crd.getNextTaskId());
			ps.setString(index++, crd.getRecordType());
			ps.setLong(index++, crd.getWorkflowId());

			ps.setLong(index++, crd.getFinID());
			ps.setInt(index++, crd.getTemplateVersion());
			ps.setString(index, crd.getTemplateName());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(long finID, TableType tableType) {
		StringBuilder sql = new StringBuilder("delete from CREDITREVIEWDATA");
		sql.append(tableType.getSuffix());
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, finID));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public CreditReviewDetails getCreditReviewDetailsbyLoanType(CreditReviewDetails crd) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinCategory, EmploymentType, EligibilityMethod, Section");
		sql.append(", TemplateName, TemplateVersion, Fields, ProtectedCells, FieldKeys, FormulaCells");
		sql.append(" From CreditReviewConfig");

		List<String> list = new ArrayList<>();

		StringBuilder whereCondition = new StringBuilder();
		if (StringUtils.isNotEmpty(crd.getProduct())) {
			whereCondition.append(" Product = ?");
			list.add(crd.getProduct());
		}

		if (StringUtils.isNotEmpty(crd.getEmploymentType())) {
			if (StringUtils.isNotEmpty(whereCondition.toString())) {
				whereCondition.append(" and ");
			}
			whereCondition.append(" EmploymentType = ?");
			list.add(crd.getEmploymentType());
		}

		if (StringUtils.isNotEmpty(crd.getEligibilityMethod())) {
			if (StringUtils.isNotEmpty(whereCondition.toString())) {
				whereCondition.append(" and ");
			}
			whereCondition.append(" EligibilityMethod = ?");
			list.add(crd.getEligibilityMethod());
		}

		if (StringUtils.isNotBlank(whereCondition.toString())) {
			sql.append(" Where ").append(whereCondition);
		} else {
			return null;
		}

		Object[] args = list.toArray();

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CreditReviewDetails crdts = new CreditReviewDetails();

				crdts.setId(rs.getLong("Id"));
				crdts.setFinCategory(rs.getString("FinCategory"));
				crdts.setEmploymentType(rs.getString("EmploymentType"));
				crdts.setEligibilityMethod(rs.getString("EligibilityMethod"));
				crdts.setSection(rs.getString("Section"));
				crdts.setTemplateName(rs.getString("TemplateName"));
				crdts.setTemplateVersion(rs.getInt("TemplateVersion"));
				crdts.setFields(rs.getString("Fields"));
				crdts.setProtectedCells(rs.getString("ProtectedCells"));
				crdts.setFieldKeys(rs.getString("FieldKeys"));
				crdts.setFormulaCells(rs.getString("FormulaCells"));

				return crdts;
			}, args);
		} catch (EmptyResultDataAccessException e) {
			logger.info(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public ExtCreditReviewConfig getExtCreditReviewConfigDetails(ExtCreditReviewConfig extCreditReviewConfig) {
		String sql = "select * from BREExtCreditReviewConfig where CreditReviewType = :CreditReviewType";

		logger.debug(Literal.SQL + sql);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extCreditReviewConfig);
		RowMapper<ExtCreditReviewConfig> typeRowMapper = BeanPropertyRowMapper.newInstance(ExtCreditReviewConfig.class);

		try {
			extCreditReviewConfig = jdbcTemplate.queryForObject(sql, beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			extCreditReviewConfig = null;
		}

		return extCreditReviewConfig;
	}

	@Override
	public ExtBreDetails getExtBreDetailsByRef(long finID) {
		String sql = "Select * From EXTBreDetails Where FinID = :FinID";

		logger.debug(Literal.SQL + sql);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinID", finID);

		RowMapper<ExtBreDetails> rowMapper = BeanPropertyRowMapper.newInstance(ExtBreDetails.class);
		try {
			return jdbcTemplate.queryForObject(sql, source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
