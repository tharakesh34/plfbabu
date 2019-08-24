package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.CreditReviewDetailDAO;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class CreditReviewDetailDAOImpl extends SequenceDao<CreditReviewDetails> implements CreditReviewDetailDAO {

	private static Logger logger = LogManager.getLogger(CreditReviewDetailDAOImpl.class);

	public CreditReviewDetailDAOImpl() {
		super();
	}

	@Override
	public CreditReviewDetails getCreditReviewDetails(CreditReviewDetails creditReviewDetail) {

		logger.debug(Literal.ENTERING);
		StringBuilder selectSql = new StringBuilder();
		StringBuilder whereCondition = new StringBuilder();
		whereCondition.append(" eligibilityMethod = :eligibilityMethod");

		selectSql.append(
				" Select ID,FINCATEGORY,EMPLOYMENTTYPE,ELIGIBILITYMETHOD,SECTION,TEMPLATENAME,TEMPLATEVERSION, FIELDS, PROTECTEDCELLS ");
		selectSql.append(" FROM  CREDITREVIEWCONFIG ");
		if (StringUtils.isNotBlank(whereCondition.toString())) {
			selectSql.append(" Where ").append(whereCondition);
		} else {
			return creditReviewDetail = null;
		}

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetail);
		RowMapper<CreditReviewDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CreditReviewDetails.class);

		try {
			creditReviewDetail = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			creditReviewDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return creditReviewDetail;
	}

	@Override
	public CreditReviewData getCreditReviewData(String finReference, String templateName, int templateVersion) {
		logger.debug(Literal.ENTERING);
		CreditReviewData creditReviewData = null;
		StringBuilder selectSql = new StringBuilder();

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("TemplateName", templateName);
		source.addValue("TemplateVersion", templateVersion);

		selectSql.append(" Select FinReference, TemplateData, TemplateName, TemplateVersion");
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  CreditReviewData");
		selectSql.append(
				" Where FinReference=:FinReference AND TemplateName =:TemplateName AND TemplateVersion=:TemplateVersion");

		logger.trace(Literal.SQL + selectSql.toString());
		RowMapper<CreditReviewData> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CreditReviewData.class);

		try {
			creditReviewData = jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			creditReviewData = null;
		}

		logger.debug(Literal.LEAVING);
		return creditReviewData;

	}

	@Override
	public void save(CreditReviewData creditReviewData) {

		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into CreditReviewData");
		sql.append(" (FinReference, TemplateData, TemplateName, TemplateVersion");
		sql.append(" ,Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(" ,RecordType, WorkflowId)");
		sql.append(" values (:FinReference, :TemplateData, :TemplateName, :TemplateVersion");
		sql.append(" ,:Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId");
		sql.append(" ,:RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(creditReviewData);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void update(CreditReviewData creditReviewData) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Update CreditReviewData");
		sql.append(" set TemplateData = :TemplateData");
		sql.append(" , Version = :Version, LastMntBy = :LastMntBy");
		sql.append(" ,LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode");
		sql.append(" ,NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(" ,RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(
				" where FinReference = :FinReference AND TemplateVersion=:TemplateVersion AND TemplateName=:TemplateName");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(creditReviewData);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(String finReference, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from CREDITREVIEWDATA");
		sql.append(tableType.getSuffix());
		sql.append(" where finReference = :finReference ");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("finReference", finReference);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}
}
