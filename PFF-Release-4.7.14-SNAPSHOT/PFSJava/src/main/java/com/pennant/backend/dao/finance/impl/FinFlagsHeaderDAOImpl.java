package com.pennant.backend.dao.finance.impl;


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

import com.pennant.backend.dao.finance.FinFlagsHeaderDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;

public class FinFlagsHeaderDAOImpl implements FinFlagsHeaderDAO {

	private static Logger logger = Logger.getLogger(FinFlagsHeaderDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FinFlagsHeaderDAOImpl() {
		super();
	}
	
	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceFlags
	 * 
	 * @return FinanceFlags
	 */

	@Override
	public FinanceFlag getFinanceFlags() {

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("");

		FinanceFlag financeFlags = new FinanceFlag();
		if (workFlowDetails != null) {
			financeFlags.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return financeFlags;

	}

	/**
	 * This method get the module from method getFinanceFlags() and set the new record flag as true and return
	 * FinanceFlags
	 * 
	 * @return FinanceFlags
	 */

	@Override
	public FinanceFlag getNewFinanceFlags() {
		logger.debug("Entering");
		FinanceFlag financeFlags = getFinanceFlags();
		financeFlags.setNewRecord(true);
		logger.debug("Leaving");
		return financeFlags;
	}

	/**
	 * This method insert new Records into FinFlagsHeader or FinFlagsHeader_Temp.
	 *
	 * save FinFlagsHeader 
	 * 
	 * @param financeFlags (financeFlags)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(FinanceFlag financeFlags, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" FinFlagsHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference,");
		insertSql.append(" Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(" values (:FinReference,");
		insertSql.append(" :Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeFlags);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	/**
	 * This method updates the Record FinFlagsHeader or FinFlagsHeader_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update FinFlagsHeader by key FinReference and Version
	 * 
	 * @param FinanceFlag
	 *            (FinanceFlags)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinanceFlag financeFlags, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinFlagsHeader");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Version= :Version , LastMntBy=:LastMntBy,");
		updateSql.append(" LastMntOn= :LastMntOn, RecordStatus=:RecordStatus, RoleCode=:RoleCode,");
		updateSql.append(" NextRoleCode= :NextRoleCode, TaskId= :TaskId,");
		updateSql.append(" NextTaskId= :NextTaskId, RecordType= :RecordType, WorkflowId= :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference ");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeFlags);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public FinanceFlag getFinFlagsHeaderByRef(String finReference, String type) {
		logger.debug("Entering");

		FinanceFlag financeFlags = new FinanceFlag();
		financeFlags.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder(" Select FinReference, ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" FinType, FinTypeDesc,FinCategory, ");
			selectSql.append(" CustCIF,FinBranch,BranchDesc, FinStartDate,NumberOfTerms,GraceTerms, MaturityDate, ");
			selectSql.append(" FinCcy,FinAmount, FinRepaymentAmount,ScheduleMethod, ");
			selectSql.append(" FeeChargeAmt, DownPayBank, DownPaySupl, EffectiveRateOfReturn, TotalProfit,  ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinFlagsHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeFlags);
		RowMapper<FinanceFlag> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(FinanceFlag.class);

		try {
			financeFlags = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeFlags = null;
		}
		logger.debug("Leaving");
		return financeFlags;
	}

	/**
	 * This method Deletes the Record from the FinFlagsHeader or FinFlagsHeader_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete finance Flags by key FinReference
	 * 
	 * @param Finance Flag (FinanceFlag)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(FinanceFlag financeFlags, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FinFlagsHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeFlags);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	

}
