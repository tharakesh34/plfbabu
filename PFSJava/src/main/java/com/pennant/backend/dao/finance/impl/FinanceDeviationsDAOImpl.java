package com.pennant.backend.dao.finance.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinanceDeviationsDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.util.DeviationConstants;

public class FinanceDeviationsDAOImpl extends BasisNextidDaoImpl<FinanceDeviations>  implements FinanceDeviationsDAO{

	private static Logger logger = Logger.getLogger(FinanceDeviationsDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FinanceDeviationsDAOImpl(){
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * get Deviation Details List based on finance reference
	 * 
	 */
	@Override
	public List<FinanceDeviations> getFinanceDeviations(String finReference, String type) {
		logger.debug("Entering");
		FinanceDeviations financeDeviations = new FinanceDeviations();
		financeDeviations.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("Select DeviationId, FinReference, Module, Remarks, ");
		selectSql.append(" DeviationCode ,DeviationType, DeviationValue, UserRole,ManualDeviation,");
		selectSql.append(" DelegationRole,ApprovalStatus ,DeviationDate, DeviationUserId,");
		selectSql.append(" DelegatedUserId From FinanceDeviations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDeviations);
		RowMapper<FinanceDeviations> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceDeviations.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}

	@Override
	public List<FinanceDeviations> getFinanceDeviations(String finReference, boolean deviProcessed, String type) {
		logger.debug("Entering");
		FinanceDeviations financeDeviations = new FinanceDeviations();
		financeDeviations.setFinReference(finReference);
		financeDeviations.setDeviProcessed(deviProcessed);

		StringBuilder selectSql = new StringBuilder("Select DeviationId, FinReference, Module, Remarks ,");
		selectSql.append(" DeviationCode ,DeviationType, DeviationValue, UserRole,ManualDeviation,");
		selectSql.append(" DelegationRole,ApprovalStatus ,DeviationDate, DeviationUserId,DeviProcessed,");
		selectSql.append(" DelegatedUserId From FinanceDeviations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference and DeviProcessed =:DeviProcessed");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDeviations);
		RowMapper<FinanceDeviations> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceDeviations.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * get Deviation Details List based on finance reference
	 * 
	 */
	@Override
	public FinanceDeviations getFinanceDeviationsByID(String finReference,String module,String deviationCode, String type) {
		logger.debug("Entering");
		FinanceDeviations financeDeviations = new FinanceDeviations();
		financeDeviations.setFinReference(finReference);
		financeDeviations.setModule(module);
		financeDeviations.setDeviationCode(deviationCode);
		
		StringBuilder selectSql = new StringBuilder("Select DeviationId, FinReference, Module, Remarks ,");
		selectSql.append(" DeviationCode ,DeviationType, DeviationValue, UserRole,ManualDeviation,");
		selectSql.append(" DelegationRole,ApprovalStatus ,DeviationDate, DeviationUserId,");
		selectSql.append(" DelegatedUserId From FinanceDeviations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference and Module=:Module and DeviationCode=:DeviationCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDeviations);
		RowMapper<FinanceDeviations> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceDeviations.class);
		logger.debug("Leaving");
		try {
	        return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,typeRowMapper);
        } catch (DataAccessException e) {
	       logger.debug(e);
        }
		return null;
	}
	

	/**
	 * This method updates the Record BMTAcademics or BMTAcademics_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Academic Details by key AcademicLevel and Version
	 * 
	 * @param Academic
	 *            Details (academic)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(FinanceDeviations financeDeviations, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceDeviations");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set FinReference = :FinReference, Module = :Module, DeviationCode =:DeviationCode, ");
		updateSql.append(" DeviationType = :DeviationType , DeviationValue = :DeviationValue, UserRole = :UserRole,");
		updateSql.append(" DelegationRole = :DelegationRole, ApprovalStatus = :ApprovalStatus ,DeviationDate = :DeviationDate,");
		updateSql.append(" ManualDeviation = :ManualDeviation,Remarks =:Remarks, ");
		updateSql.append("  DeviationUserId=:DeviationUserId, DelegatedUserId = :DelegatedUserId where DeviationId=:DeviationId ");

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDeviations);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);


		logger.debug("Leaving");
	}
		
		/**
		 * save Deviation details
		 */
	@Override
	public long save(FinanceDeviations financeDeviations, String type) {
		logger.debug("Entering");

		if (financeDeviations.getDeviationId() == Long.MIN_VALUE) {
			financeDeviations.setDeviationId(getNextidviewDAO().getNextId("SeqDeviations"));
		}
		StringBuilder insertSql = new StringBuilder("Insert Into FinanceDeviations");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" ( DeviationId, FinReference, Module, DeviationCode, DeviationType, ");
		insertSql.append(" DeviationValue, UserRole, DelegationRole,ApprovalStatus,");
		insertSql.append(" DeviationDate, DeviationUserId,DelegatedUserId,ManualDeviation,Remarks,DeviProcessed  )");

		insertSql.append(" Values( :DeviationId, :FinReference, :Module, :DeviationCode, :DeviationType,");
		insertSql.append(" :DeviationValue, :UserRole, :DelegationRole, :ApprovalStatus,");
		insertSql.append(" :DeviationDate, :DeviationUserId, :DelegatedUserId, :ManualDeviation, :Remarks, :DeviProcessed  )");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDeviations);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeDeviations.getId();
	}
	
	@Override
	public void delete(FinanceDeviations financeDeviations, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinanceDeviations");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  FinReference = :FinReference and Module = :Module ");
		deleteSql.append(" and DeviationCode = :DeviationCode ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDeviations);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (Exception e) {
			logger.debug(e);

		}
		logger.debug("Leaving");
	}
	
	@Override
	public void deleteCheckListRef(String finReference, String module, String refId,String type) {
		logger.debug("Entering");

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinReference", finReference);
		mapSqlParameterSource.addValue("Module", module);
		mapSqlParameterSource.addValue("CheckExpired", refId+DeviationConstants.CL_EXPIRED);
		mapSqlParameterSource.addValue("CheckPostoned", refId+DeviationConstants.CL_POSTPONED);
		mapSqlParameterSource.addValue("CheckWaived", refId+DeviationConstants.CL_WAIVED);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinanceDeviations");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  FinReference = :FinReference and Module = :Module and ");
		deleteSql.append("( DeviationCode =:CheckExpired or DeviationCode =:CheckPostoned or DeviationCode =:CheckWaived)");

		logger.debug("deleteSql: " + deleteSql.toString());

		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), mapSqlParameterSource);

		} catch (Exception e) {
			logger.debug(e);

		}
		logger.debug("Leaving");
	}

	@Override
	public void updateDeviProcessed(String finReference, String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("DeviProcessed", true);
		source.addValue("DeviNotProcessed", false);

		StringBuilder updateSql = new StringBuilder("Update FinanceDeviations");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set DeviProcessed = :DeviProcessed  ");
		updateSql.append("  where FinReference = :FinReference and  DeviProcessed =:DeviNotProcessed ");

		logger.debug("updateSql: " + updateSql.toString());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
	}

	
		@Override
	public void deleteById(FinanceDeviations financeDeviations, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinanceDeviations");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  DeviationId = :DeviationId");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDeviations);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (Exception e) {
			logger.debug(e);

		}
		logger.debug("Leaving");
	}
}
