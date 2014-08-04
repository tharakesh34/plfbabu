package com.pennant.backend.dao.finance.impl;

import java.util.Date;
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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.BulkProcessDetailsDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

public class BulkProcessDetailsDAOImpl extends BasisCodeDAO<BulkProcessDetails> implements BulkProcessDetailsDAO{


	private static Logger logger = Logger.getLogger(BulkProcessDetailsDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new BulkProcessDetails
	 * 
	 * @return BulkProcessDetails
	 */
	@Override
	public BulkProcessDetails getBulkProcessDetails() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("BulkProcessDetails");
		BulkProcessDetails bulkProcessDetails = new BulkProcessDetails();
		if (workFlowDetails != null) {
			bulkProcessDetails.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return bulkProcessDetails;
	}

	/**
	 * This method get the module from method getBulkProcessDetails() and set the new
	 * record flag as true and return BulkProcessDetails()
	 * 
	 * @return BulkProcessDetails
	 */
	@Override
	public BulkProcessDetails getNewBulkProcessDetails() {
		logger.debug("Entering");
		BulkProcessDetails aBulkProcessDetails = getBulkProcessDetails();
		aBulkProcessDetails.setNewRecord(true);
		logger.debug("Leaving");
		return aBulkProcessDetails;
	}

	/**
	 * Fetch the Record BulkProcessDetails Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BulkProcessDetails
	 */
	@Override
	public BulkProcessDetails getBulkProcessDetailsById(final long bulkProcessId, String finReference, Date deferedSchdDate, String type) {
		logger.debug("Entering");
		BulkProcessDetails bulkProcessDetails = new BulkProcessDetails();
		bulkProcessDetails.setBulkProcessId(bulkProcessId);
		bulkProcessDetails.setFinReference(finReference);
		bulkProcessDetails.setDeferedSchdDate(deferedSchdDate);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select BulkProcessID, FinReference, FinType, FinCCY, OldProfitRate, NewProfitRate, ");
		selectSql.append(" DeferedSchdDate, ScheduleMethod, ProfitDaysBasis, CustID, FinBranch, ProfitChange, AlwProcess, ReCalStartDate, ReCalEndDate, " );
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BulkProcessDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where bulkProcessId = :bulkProcessId and finReference = :finReference and deferedSchdDate = :deferedSchdDate") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessDetails);
		RowMapper<BulkProcessDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkProcessDetails.class);

		try {
			bulkProcessDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			bulkProcessDetails = null;
		}
		logger.debug("Leaving");
		return bulkProcessDetails;
	}
	@Override
	public List<BulkProcessDetails> getBulkProcessDetailsListById(long bulkProcessId, String type){
		logger.debug("Entering");
		BulkProcessDetails bulkProcessDetails= new BulkProcessDetails();
		bulkProcessDetails.setBulkProcessId(bulkProcessId);
		StringBuilder selectSql = new StringBuilder();
	 
		selectSql.append(" Select BulkProcessID, FinReference, FinType, FinCCY, OldProfitRate, NewProfitRate, ");
		selectSql.append(" DeferedSchdDate, ScheduleMethod, ProfitDaysBasis, CustID, FinBranch, ProfitChange, AlwProcess, ReCalStartDate, ReCalEndDate," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BulkProcessDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where bulkProcessId = :bulkProcessId ") ;

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessDetails);
		RowMapper<BulkProcessDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
		.newInstance(BulkProcessDetails.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
	}

	/**
	 * Fetch the Record BulkProcessDetails Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BulkProcessDetails
	 */
	@Override
	public BulkProcessDetails getBulkProcessDetails(long bulkProcessId, String finReference, String type) {
		logger.debug("Entering");
		BulkProcessDetails bulkProcessDetails = new BulkProcessDetails();
		bulkProcessDetails.setBulkProcessId(bulkProcessId);
		bulkProcessDetails.setFinReference(finReference);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select BulkProcessID, FinReference, FinType, FinCCY, OldProfitRate, NewProfitRate, ");
		selectSql.append(" DeferedSchdDate, ScheduleMethod, ProfitDaysBasis, CustID, FinBranch, ProfitChange, AlwProcess, ReCalStartDate, ReCalEndDate," );
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BulkProcessDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where bulkProcessId = :bulkProcessId AND  finReference = :finReference") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessDetails);
		RowMapper<BulkProcessDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkProcessDetails.class);

		try {
			bulkProcessDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			bulkProcessDetails = null;
		}
		logger.debug("Leaving");
		return bulkProcessDetails;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param BulkProcessDetails
	 *            (bulkProcessDetails)
	 * @return BulkProcessDetails
	 */
	@Override
	public void initialize(BulkProcessDetails bulkProcessDetails) {
		super.initialize(bulkProcessDetails);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param BulkProcessDetails
	 *            (bulkProcessDetails)
	 * @return void
	 */
	@Override
	public void refresh(BulkProcessDetails bulkProcessDetails) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BulkProcessDetails or
	 * BulkProcessDetails_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete BulkProcessDetails Details by key BulkProcessDetailsLevel
	 * 
	 * @param BulkProcessDetails
	 *            Details (bulkProcessDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(BulkProcessDetails bulkProcessDetails, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql =new StringBuilder();
		deleteSql.append("Delete From BulkProcessDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  bulkProcessId = :bulkProcessId ");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessDetails);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),	beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", String.valueOf(bulkProcessDetails.getBulkProcessId()), 
						String.valueOf(bulkProcessDetails.getFinReference()), bulkProcessDetails.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", String.valueOf(bulkProcessDetails.getBulkProcessId()), 
					String.valueOf(bulkProcessDetails.getFinReference()), bulkProcessDetails.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	
	
	public void deleteBulkProcessDetailsById(long  bulkProcessId,String type){
		logger.debug("Entering");
		int recordCount = 0;
		BulkProcessDetails bulkProcessDetails = new BulkProcessDetails();
		bulkProcessDetails.setBulkProcessId(bulkProcessId);
		StringBuilder deleteSql = new StringBuilder("Delete From BulkProcessDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BulkProcessId = :BulkProcessId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessDetails);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				/*ErrorDetails errorDetails= getError("41003",creditReviewSummary.getDetailId() ,
						creditReviewSummary.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};*/
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",bulkProcessDetails.getBulkProcessId() ,
					bulkProcessDetails.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
                private static final long serialVersionUID = 1L;};
		}
		logger.debug("Leaving");
	}

	
	private ErrorDetails  getError(String errorId, long bulkProcessID, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] =String.valueOf(bulkProcessID);
		parms[0][0] = PennantJavaUtil.getLabel("label_BulkProcessId")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
	
	
	/**
	 * This method insert new Records into BulkProcessDetails or BulkProcessDetails_Temp.
	 * 
	 * save BulkProcessDetails Details
	 * 
	 * @param BulkProcessDetails
	 *            Details (bulkProcessDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(BulkProcessDetails bulkProcessDetails, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BulkProcessDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BulkProcessID, FinReference, FinType, FinCCY, OldProfitRate, NewProfitRate, " );
		insertSql.append(" DeferedSchdDate, ScheduleMethod, ProfitDaysBasis, CustID, FinBranch, ProfitChange, AlwProcess, ReCalStartDate, ReCalEndDate," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:bulkProcessId, :finReference, :finType, :finCCY, :oldProfitRate, :newProfitRate, ");
		insertSql.append(" :DeferedSchdDate, :scheduleMethod, :profitDaysBasis, :custID, :finBranch, :profitChange, :AlwProcess, :ReCalStartDate, :ReCalEndDate, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return bulkProcessDetails.getBulkProcessId();
	}

	
	@Override
	public void saveList(List<BulkProcessDetails> bulkProcessDetails, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		
		if(bulkProcessDetails!= null && type.equalsIgnoreCase("_Temp")){
			deleteBulkProcessDetailsById(bulkProcessDetails.get(0).getBulkProcessId(), type);
		}
		
		insertSql.append("Insert Into BulkProcessDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BulkProcessID, FinReference, FinType, FinCCY, OldProfitRate, NewProfitRate, " );
		insertSql.append(" DeferedSchdDate, ScheduleMethod, ProfitDaysBasis, CustID, FinBranch, ProfitChange, AlwProcess, ReCalStartDate, ReCalEndDate," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:bulkProcessId, :finReference, :finType, :finCCY, :oldProfitRate, :newProfitRate, ");
		insertSql.append(" :DeferedSchdDate, :scheduleMethod, :profitDaysBasis, :custID, :finBranch, :profitChange, :AlwProcess, :ReCalStartDate, :ReCalEndDate, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(bulkProcessDetails.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	
	@Override
	public void updateList(List<BulkProcessDetails> bulkProcessDetails, String type) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BulkProcessDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set BulkProcessId = :bulkProcessId, FinReference = :finReference, FinType = :finType, FinCCY = :finCCY, OldProfitRate = :oldProfitRate, NewProfitRate =  :newProfitRate, ");
		updateSql.append("  DeferedSchdDate = :DeferedSchdDate, ScheduleMethod = :scheduleMethod, ProfitDaysBasis = :profitDaysBasis, CustID = :custID, FinBranch = :finBranch, ");
		updateSql.append("  ProfitChange = :profitChange, AlwProcess = :AlwProcess, ReCalStartDate = :ReCalStartDate, ReCalEndDate = :ReCalEndDate, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append("  Where BulkProcessId = :BulkProcessId AND FinReference = :finReference AND DeferedSchdDate = :deferedSchdDate ");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(bulkProcessDetails.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}
	
	
	
	/**
	 * This method updates the Record BulkProcessDetails or BulkProcessDetails_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update BulkProcessDetails Details by key BulkProcessDetailsLevel and Version
	 * 
	 * @param BulkProcessDetails
	 *            Details (bulkProcessDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(BulkProcessDetails bulkProcessDetails, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BulkProcessDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		 
		updateSql.append("  Set BulkProcessId = :bulkProcessId, FinReference = :finReference, FinType = :finType, FinCCY = :finCCY, OldProfitRate = :oldProfitRate, NewProfitRate =  :newProfitRate, ");
		updateSql.append("  DeferedSchdDate = :DeferedSchdDate, ScheduleMethod = :scheduleMethod, ProfitDaysBasis = :profitDaysBasis, CustID = :custID, FinBranch = :finBranch, ");
		updateSql.append("  ProfitChange = :profitChange, AlwProcess = :AlwProcess, ReCalStartDate = :ReCalStartDate, ReCalEndDate = :ReCalEndDate, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append("  Where BulkProcessId = :BulkProcessId AND FinReference = :finReference ");
		if(bulkProcessDetails.getDeferedSchdDate() != null ){
			updateSql.append(" AND DeferedSchdDate = :deferedSchdDate ");
		}
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessDetails);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails= getError("41003", String.valueOf(bulkProcessDetails.getBulkProcessId()), 
					String.valueOf(bulkProcessDetails.getFinReference()), bulkProcessDetails.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	
	/**
	 * Method for fetching List Of IJARAH Finance for Bulk Rate Change
	 */
	@Override
    public List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate) {
		logger.debug("Entering");
		
		BulkProcessDetails bulkProcessDetails = new BulkProcessDetails();
		bulkProcessDetails.setLovDescEventFromDate(fromDate);
		bulkProcessDetails.setLovDescEventToDate(toDate);

		StringBuilder selectSql = new StringBuilder(" SELECT FinReference, FinType, " );
		selectSql.append(" FinCcy, ScheduleMethod, ProfitDaysBasis, CustCIF, FinBranch, " );
		selectSql.append(" ProductCode, EventFromDate, EventToDate " );
		selectSql.append(" FROM IjarahFinance( :LovDescEventFromDate, :LovDescEventToDate )");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessDetails);
		RowMapper<BulkProcessDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkProcessDetails.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
    }
	
	/**
	 * Method for Fetch List of Finance for Bulk Deferment Process
	 */
	@Override
    public List<BulkProcessDetails> getBulkDefermentFinList(Date fromDate, Date toDate, String whereClause) {
		logger.debug("Entering");
		
		BulkProcessDetails bulkProcessDetails = new BulkProcessDetails();
		bulkProcessDetails.setLovDescEventFromDate(fromDate);
		bulkProcessDetails.setLovDescEventToDate(toDate);

		StringBuilder selectSql = null;
		if(whereClause == null){
			selectSql = new StringBuilder(" SELECT FinReference, FinType, " );
			selectSql.append(" FinCcy, ScheduleMethod, ProfitDaysBasis, CustCIF, FinBranch, lovDescFinDivision, " );
			selectSql.append(" ProductCode, EventFromDate " );
			selectSql.append(" FROM BulkDefermentFinance( :lovDescEventFromDate, :lovDescEventToDate )");
		} else {
			selectSql = new StringBuilder(" SELECT FinReference, FinType, " );
			selectSql.append(" FinCcy, DeferedSchdDate, ScheduleMethod, ProfitDaysBasis, CustID, FinBranch, lovDescFinDivision, " );
			selectSql.append(" ProductCode FROM BulkDeferement_View " );
			selectSql.append(" WHERE "+ whereClause);
		}
		

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkProcessDetails);
		RowMapper<BulkProcessDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkProcessDetails.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
    }
	
	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String bulkProcessDetailsLevel,String bulkProcessDetailsDecipline, String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = bulkProcessDetailsLevel;
		parms[1][1] = bulkProcessDetailsDecipline;

		parms[0][0] = PennantJavaUtil.getLabel("label_BulkRateChangeSearch_fromDate.value")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_BulkRateChangeSearch_toDate.value")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}


}
