package com.pennant.backend.dao.financemanagement.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;

public class FinanceStepDetailDAOImpl extends BasisCodeDAO<StepPolicyDetail> implements FinanceStepDetailDAO {

	private static Logger logger = Logger.getLogger(FinanceStepDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinanceStepDetailDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new StepPolicyDetail
	 * 
	 * @return StepPolicyDetail
	 */
	@Override
	public FinanceStepPolicyDetail getFinStepPolicy() {
		logger.debug("Entering");
		FinanceStepPolicyDetail finStepDetail = new FinanceStepPolicyDetail();
		logger.debug("Leaving");
		return finStepDetail;
	}

	/**
	 * This method get the module from method getStepPolicyDetail() and set the new record flag as true and
	 * return StepPolicyDetail()
	 * 
	 * @return StepPolicyDetail
	 */
	@Override
	public FinanceStepPolicyDetail getNewFinStepPolicy() {
		logger.debug("Entering");
		FinanceStepPolicyDetail stepPolicyDetail = getFinStepPolicy();
		stepPolicyDetail.setNewRecord(true);
		logger.debug("Leaving");
		return stepPolicyDetail;
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *         (String)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return StepPolicyDetail
	 */
	@Override
	public List<FinanceStepPolicyDetail> getFinStepDetailListByFinRef(final String finReference, String type, boolean isWIF) {
		logger.debug("Entering");
		
		FinanceStepPolicyDetail finStepPolicy = new FinanceStepPolicyDetail();
		finStepPolicy.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder("SELECT FinReference, StepNo, TenorSplitPerc, Installments, RateMargin, EmiSplitPerc, SteppedEMI, ");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(isWIF){
			selectSql.append(" FROM WIFFinStepPolicyDetail");
		}else{
			selectSql.append(" FROM FinStepPolicyDetail");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :finReference");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStepPolicy);
		RowMapper<FinanceStepPolicyDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceStepPolicyDetail.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * @param dataSource
	 *         the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method insert new Records into StepPolicyDetails or StepPolicyDetails_Temp.
	 * 
	 * save StepPolicyDetails
	 * 
	 * @param FinanceStepPolicyDetail (StepPolicyDetail)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void saveList(List<FinanceStepPolicyDetail> finStepDetailList, boolean isWIF, String type) {
		logger.debug("Entering ");
		
		StringBuilder insertSql = new StringBuilder("Insert Into " );
		if(isWIF){
			insertSql.append(" WIFFinStepPolicyDetail");
		}else{
			insertSql.append(" FinStepPolicyDetail");
		}
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (FinReference, StepNo, TenorSplitPerc, Installments,  RateMargin, EmiSplitPerc, SteppedEMI, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:FinReference, :StepNo, :TenorSplitPerc, :Installments, :RateMargin, :EmiSplitPerc, :SteppedEMI, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finStepDetailList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
	}

	/**
	 * This method Deletes the Record from the StepPolicyDetails or StepPolicyDetails_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Finance Types by key FinType
	 * 
	 * @param Finance
	 *         Types (StepPolicyDetail)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteList(String finReference, boolean isWIF,String type) {
		logger.debug("Entering");
		
		FinanceStepPolicyDetail finStepPolicy = new FinanceStepPolicyDetail();
		finStepPolicy.setFinReference(finReference);
		
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		if(isWIF){
			deleteSql.append(" WIFFinStepPolicyDetail");
		}else{
			deleteSql.append(" FinStepPolicyDetail");
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where  FinReference = :FinReference ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStepPolicy);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}
