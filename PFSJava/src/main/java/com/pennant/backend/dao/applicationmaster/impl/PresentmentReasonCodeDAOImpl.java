package com.pennant.backend.dao.applicationmaster.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.PresentmentReasonCodeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.PresentmentReasonCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

public class PresentmentReasonCodeDAOImpl extends BasisCodeDAO<PresentmentReasonCode> implements PresentmentReasonCodeDAO{
	private static Logger logger = Logger.getLogger(PresentmentReasonCodeDAOImpl.class);
	
	// Spring Named JDBC Template
		private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public PresentmentReasonCodeDAOImpl() {
		super();
	}
	

	@Override
    public PresentmentReasonCode getPresentmentReasonCodeById(String id, String type) {
		logger.debug("Entering");
		PresentmentReasonCode presentmentReasonCode = new PresentmentReasonCode();
		presentmentReasonCode.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select Code, Description, Active,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From PresentmentReasonCode");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Code =:Code");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(presentmentReasonCode);
		RowMapper<PresentmentReasonCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PresentmentReasonCode.class);

		try {
			presentmentReasonCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			presentmentReasonCode = null;
		}
		logger.debug("Leaving");
		return presentmentReasonCode;
    }

	@Override
    public void update(PresentmentReasonCode presentmentReasonCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update PresentmentReasonCode");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Description = :Description, Active = :Active,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Code =:Code");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(presentmentReasonCode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	    
    }

	@Override
    public void delete(PresentmentReasonCode presentmentReasonCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From PresentmentReasonCode");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Code =:Code");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(presentmentReasonCode);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
    }

	@Override
    public String save(PresentmentReasonCode presentmentReasonCode, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into PresentmentReasonCode");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Code, Description, Active,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values (:Code, :Description, :Active,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(presentmentReasonCode);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return presentmentReasonCode.getId();
    }

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}
