package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinanceDeviationsDAO;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.util.DeviationConstants;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinanceDeviationsDAOImpl extends SequenceDao<FinanceDeviations>  implements FinanceDeviationsDAO{
   private static Logger logger = Logger.getLogger(FinanceDeviationsDAOImpl.class);

	
	public FinanceDeviationsDAOImpl(){
		super();
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
		selectSql.append(" DeviationCode, DeviationType, DeviationValue, UserRole, DeviationCategory,");
		selectSql.append(" DelegationRole, ApprovalStatus, DeviationDate, DeviationUserId, MarkDeleted,");
		selectSql.append(" DelegatedUserId, DeviationDesc From FinanceDeviations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDeviations);
		RowMapper<FinanceDeviations> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceDeviations.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}

	@Override
	public List<FinanceDeviations> getFinanceDeviations(String finReference, boolean deviProcessed, String type) {
		logger.debug("Entering");
		FinanceDeviations financeDeviations = new FinanceDeviations();
		financeDeviations.setFinReference(finReference);
		financeDeviations.setDeviProcessed(deviProcessed);

		StringBuilder selectSql = new StringBuilder("Select DeviationId, FinReference, Module, Remarks ,");
		selectSql.append(" DeviationCode ,DeviationType, DeviationValue, UserRole, DeviationCategory,");
		selectSql.append(" DelegationRole,ApprovalStatus ,DeviationDate, DeviationUserId,DeviProcessed,MarkDeleted,");
		selectSql.append(" DelegatedUserId, DeviationDesc From FinanceDeviations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference and DeviProcessed =:DeviProcessed");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDeviations);
		RowMapper<FinanceDeviations> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceDeviations.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinanceDeviations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("  Set FinReference = :FinReference, Module = :Module, DeviationCode =:DeviationCode, ");
		sql.append(" DeviationType = :DeviationType , DeviationValue = :DeviationValue, UserRole = :UserRole,");
		sql.append(" DelegationRole = :DelegationRole, ApprovalStatus = :ApprovalStatus ,DeviationDate = :DeviationDate,");
		sql.append(" DeviationCategory = :DeviationCategory, Remarks =:Remarks, ");
		sql.append(" DeviationUserId=:DeviationUserId, DelegatedUserId = :DelegatedUserId, ");
		sql.append(" MarkDeleted = :MarkDeleted");
		sql.append(" where DeviationId = :DeviationId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDeviations);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}
		
		/**
		 * save Deviation details
		 */
	@Override
	public long save(FinanceDeviations financeDeviations, String type) {
		logger.debug("Entering");

		if (financeDeviations.getDeviationId() == Long.MIN_VALUE) {
			financeDeviations.setDeviationId(getNextId("SeqDeviations"));
		}
		StringBuilder insertSql = new StringBuilder("Insert Into FinanceDeviations");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" ( DeviationId, FinReference, Module, DeviationCode, DeviationType, ");
		insertSql.append(" DeviationValue, UserRole, DelegationRole,ApprovalStatus,");
		insertSql.append(
				" DeviationDate, DeviationUserId,DelegatedUserId,DeviationCategory,Remarks,DeviProcessed, DeviationDesc, MarkDeleted)");

		insertSql.append(" Values( :DeviationId, :FinReference, :Module, :DeviationCode, :DeviationType,");
		insertSql.append(" :DeviationValue, :UserRole, :DelegationRole, :ApprovalStatus,");
		insertSql.append(
				" :DeviationDate, :DeviationUserId, :DelegatedUserId, :DeviationCategory, :Remarks, :DeviProcessed, :DeviationDesc, :MarkDeleted)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDeviations);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
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
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

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
			this.jdbcTemplate.update(deleteSql.toString(), mapSqlParameterSource);

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
		this.jdbcTemplate.update(updateSql.toString(), source);

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
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (Exception e) {
			logger.debug(e);

		}
		logger.debug("Leaving");
	}
	//### 05-05-2018- Start- story #361(tuleap server) Manual Deviations
	@Override
	public void updateMarkDeleted(long deviationId, String finReference) {
		logger.debug("Entering");
		FinanceDeviations financeDeviations = new FinanceDeviations();
		financeDeviations.setDeviationId(deviationId);
		financeDeviations.setMarkDeleted(true);
		StringBuilder updateSql = new StringBuilder("Update FinanceDeviations");
		updateSql.append("  Set MarkDeleted = :MarkDeleted ");
		updateSql.append("where DeviationId=:DeviationId ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDeviations);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}
	//### 05-05-2018- END- story #361(tuleap server) Manual Deviations

	@Override
	public void updateMarkDeleted(long deviationId, boolean markDeleted) {
		logger.debug(Literal.ENTERING);

		FinanceDeviations deviation = new FinanceDeviations();
		deviation.setDeviationId(deviationId);
		deviation.setMarkDeleted(markDeleted);

		StringBuilder sql = new StringBuilder("update FinanceDeviations");
		sql.append(" set MarkDeleted = :MarkDeleted");
		sql.append(" where DeviationId = :DeviationId");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deviation);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}
}
