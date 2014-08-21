package com.pennant.backend.dao.financemanagement.impl;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinanceStepDetailDAOImpl extends BasisCodeDAO<StepPolicyDetail> implements FinanceStepDetailDAO {


	private static Logger logger = Logger.getLogger(FinanceStepDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

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
	public List<FinanceStepPolicyDetail> getFinStepDetailListByFinRef(final String finReference, String type) {
		logger.debug("Entering");
		
		FinanceStepPolicyDetail finStepPolicy = new FinanceStepPolicyDetail();
		finStepPolicy.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder("SELECT FinReference, StepNo, TenorSplitPerc, Installments, RateMargin, EmiSplitPerc, SteppedEMI, ");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM FinanceStepPolicyDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :finReference");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStepPolicy);
		RowMapper<FinanceStepPolicyDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceStepPolicyDetail.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	/**
	 * Method for Fetching Step Details based on Step Number & Reference
	 */
	public FinanceStepPolicyDetail getFinStepPolicy(String finReference, int stepNumber, String type){

		logger.debug("Entering");
		FinanceStepPolicyDetail finStepPolicy = new FinanceStepPolicyDetail();
		finStepPolicy.setFinReference(finReference);
		finStepPolicy.setStepNo(stepNumber);
		StringBuilder selectSql = new StringBuilder("SELECT FinReference, StepNo, TenorSplitPerc, Installments, RateMargin, EmiSplitPerc, SteppedEMI, ");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM FinanceStepPolicyDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference AND  StepNo = :StepNo");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStepPolicy);
		RowMapper<FinanceStepPolicyDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceStepPolicyDetail.class);

		try {
			finStepPolicy = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finStepPolicy = null;
		}
		logger.debug("Leaving");
		return finStepPolicy;
	}
	
	@Override
	public void initialize(FinanceStepPolicyDetail FinStepDetail) {
		super.initialize(FinStepDetail);
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
	public String save(FinanceStepPolicyDetail finStepDetail, String type) {
		logger.debug("Entering ");
		
		StringBuilder insertSql = new StringBuilder("Insert Into FinanceStepPolicyDetail" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (FinReference, StepNo, TenorSplitPerc, Installments,  RateMargin, EmiSplitPerc, SteppedEMI, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:FinReference, :StepNo, :TenorSplitPerc, :Installments, :RateMargin, :EmiSplitPerc, :SteppedEMI, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStepDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return String.valueOf(finStepDetail.getStepNo());
	}

	/**
	 * This method updates the Record StepPolicyDetails or StepPolicyDetails_Temp. if Record not updated
	 * then throws DataAccessException with error 41004. update Finance Types by key FinType and
	 * Version
	 * 
	 * @param FinanceStepPolicyDetail (StepPolicyDetail)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(FinanceStepPolicyDetail finStepDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		
		StringBuilder updateSql = new StringBuilder("Update FinanceStepPolicyDetail" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set FinReference = :FinReference, StepNo = :StepNo, TenorSplitPerc = :TenorSplitPerc, Installments = :Installments, ");
		updateSql.append(" RateMargin = :RateMargin, EmiSplitPerc = :EmiSplitPerc, SteppedEMI = :SteppedEMI, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where FinReference = :FinReference AND StepNumber=:StepNumber ");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStepDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004", String.valueOf(finStepDetail.getStepNo()),
					String.valueOf(finStepDetail.getStepNo()),  finStepDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
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
	@SuppressWarnings("serial")
	public void delete(FinanceStepPolicyDetail finStepPolicy,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinanceStepPolicyDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where  FinReference = :FinReference AND StepNo =:StepNo ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStepPolicy);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",finStepPolicy.getFinReference(),String.valueOf(finStepPolicy.getStepNo()),finStepPolicy.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",finStepPolicy.getFinReference(),String.valueOf(finStepPolicy.getStepNo()),finStepPolicy.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
		
	private ErrorDetails  getError(String errorId, String policyCode,String stepNumber, String userLanguage){
		String[][] parms= new String[2][2]; 

		parms[1][0] = policyCode;
		parms[1][1] = stepNumber;

		parms[0][0] = PennantJavaUtil.getLabel("label_PolicyCode")+ ":" + parms[1][0]
		                +" "+ PennantJavaUtil.getLabel("label_StepNumber")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

}
