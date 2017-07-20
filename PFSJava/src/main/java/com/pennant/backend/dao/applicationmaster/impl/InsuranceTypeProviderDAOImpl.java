package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;

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

import com.pennant.backend.dao.applicationmaster.InsuranceTypeProviderDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.InsuranceTypeProvider;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

public class InsuranceTypeProviderDAOImpl extends BasisCodeDAO<InsuranceTypeProvider> implements
		InsuranceTypeProviderDAO {
	private static Logger				logger	= Logger.getLogger(InsuranceTypeProviderDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public InsuranceTypeProviderDAOImpl() {
		super();
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public List<InsuranceTypeProvider> getInsuranceTypeProviderListByID(final String id, String type) {
		logger.debug("Entering");
		InsuranceTypeProvider insuranceTypeProvider = new InsuranceTypeProvider();
		insuranceTypeProvider.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT InsuranceType,ProviderCode ,");
		if (type.contains("View")) {
			selectSql.append(" ProviderName,InsuranceRate");
		}
		selectSql.append("Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" FROM FinTypeInsurances");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where InsuranceType = :InsuranceType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insuranceTypeProvider);
		RowMapper<InsuranceTypeProvider> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InsuranceTypeProvider.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public InsuranceTypeProvider getInsuranceTypeProviderByID(InsuranceTypeProvider insuranceTypeProvider, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT InsuranceType,ProviderCode, ");
		if (type.contains("View")) {
			selectSql.append("ProviderName,InsuranceRate,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" FROM InsuranceTypeProvider");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where InsuranceType = :InsuranceType AND ProviderCode = :ProviderCode");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insuranceTypeProvider);
		RowMapper<InsuranceTypeProvider> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InsuranceTypeProvider.class);

		try {
			insuranceTypeProvider = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			insuranceTypeProvider = null;
		}
		logger.debug("Leaving");
		return insuranceTypeProvider;
	}

	/**
	 * This method insert new Records into RMTFinanceTypes or RMTFinanceTypes_Temp.
	 * 
	 * save Finance Types
	 * 
	 * @param Finance
	 *            Types (financeType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(InsuranceTypeProvider insuranceTypeProvider, String type) {
		logger.debug("Entering ");

		StringBuilder insertSql = new StringBuilder("Insert Into  Insurancetypeprovider");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (InsuranceType, ProviderCode,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:InsuranceType, :ProviderCode,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insuranceTypeProvider);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return insuranceTypeProvider.getId();
	}

	@Override
	public void update(InsuranceTypeProvider insuranceTypeProvider, String type) {
		int recordCount = 0;
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder("Update InsuranceTypeProvider");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where InsuranceType =:InsuranceType and ProviderCode =:ProviderCode");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insuranceTypeProvider);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}

	@Override
	public void delete(InsuranceTypeProvider insuranceTypeProvider, String type) {

		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From InsuranceTypeProvider");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where InsuranceType =:InsuranceType AND ProviderCode= :ProviderCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insuranceTypeProvider);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount < 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");

	}
}
