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

import com.pennant.backend.dao.finance.BulkRateChangeProcessDetailsDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.BulkRateChangeDetails;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

public class BulkRateChangeProcessDetailsDAOImpl extends BasisCodeDAO<BulkRateChangeDetails> implements BulkRateChangeProcessDetailsDAO {


	private static Logger logger = Logger.getLogger(BulkRateChangeProcessDetailsDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public BulkRateChangeProcessDetailsDAOImpl() {
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
	 * This method set the Work Flow id based on the module name and return the
	 * new BulkRateChangeDetails
	 * 
	 * @return BulkRateChangeDetails
	 */
	@Override
	public BulkRateChangeDetails getBulkRateChangeDetails() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("BulkRateChangeDetail");
		BulkRateChangeDetails bulkRateChangeDetails = new BulkRateChangeDetails();
		if (workFlowDetails != null) {
			bulkRateChangeDetails.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return bulkRateChangeDetails;
	}

	/**
	 * This method get the module from method getBulkRateChangeDetails() and set the new
	 * record flag as true and return BulkRateChangeDetails()
	 * 
	 * @return BulkRateChangeDetails
	 */
	@Override
	public BulkRateChangeDetails getNewBulkRateChangeDetails() {
		logger.debug("Entering");
		BulkRateChangeDetails aBulkRateChangeDetails = getBulkRateChangeDetails();
		aBulkRateChangeDetails.setNewRecord(true);
		logger.debug("Leaving");
		return aBulkRateChangeDetails;
	}

	/**
	 * Fetch the Record BulkRateChangeDetails Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BulkRateChangeDetails
	 */
	@Override
	public List<BulkRateChangeDetails> getBulkRateChangeDetailsListByRef(String bulkRateChangeRef, String type) {
		logger.debug("Entering");

		BulkRateChangeDetails bulkRateChangeDetails= new BulkRateChangeDetails();
		bulkRateChangeDetails.setBulkRateChangeRef(bulkRateChangeRef);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select BulkRateChangeRef, FinReference, FinBranch, FinCCY, CustCIF, FinAmount,");
		selectSql.append(" OldProfitRate, NewProfitRate, OldProfit, NewProfit, AllowRateChange," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId," );
		selectSql.append(" Status, ErrorMsg" ); 
		if(type.contains("View")){
			selectSql.append(", lovDescFinFormatter" );
		}
		selectSql.append(" FROM  BulkRateChangeDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BulkRateChangeRef = :BulkRateChangeRef AND (Status = 'P' OR Status = 'F')" ) ;//Pending OR Failed Finances

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkRateChangeDetails);
		RowMapper<BulkRateChangeDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkRateChangeDetails.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Fetch the Record BulkRateChangeDetails Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BulkRateChangeDetails
	 */
	@Override
	public BulkRateChangeDetails getDetailsByRateChangeRefAndFinRef(String bulkRateChangeRef, String finReference, String type) {
		logger.debug("Entering");

		BulkRateChangeDetails bulkRateChangeDetails = new BulkRateChangeDetails();
		bulkRateChangeDetails.setBulkRateChangeRef(bulkRateChangeRef);
		bulkRateChangeDetails.setFinReference(finReference);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select BulkRateChangeRef, FinReference, FinBranch, FinCCY, CustCIF, FinAmount," );
		selectSql.append(" OldProfitRate, NewProfitRate, OldProfit, NewProfit, AllowRateChange," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId," );
		selectSql.append(" Status, ErrorMsg" ); 
		if(type.contains("View")){
			selectSql.append(", lovDescFinFormatter" );
		}
		selectSql.append(" FROM  BulkRateChangeDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BulkRateChangeRef = :BulkRateChangeRef and FinReference = :FinReference") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkRateChangeDetails);
		RowMapper<BulkRateChangeDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkRateChangeDetails.class);

		try {
			bulkRateChangeDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			bulkRateChangeDetails = null;
		}
		logger.debug("Leaving");
		return bulkRateChangeDetails;
	}

	/**
	 * This method insert new Records into BulkRateChangeDetails or BulkRateChangeDetails_Temp.
	 * 
	 * save BulkRateChangeDetails Details
	 * 
	 * @param BulkRateChangeDetails
	 *            Details (bulkRateChangeDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(BulkRateChangeDetails bulkRateChangeDetails, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BulkRateChangeDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BulkRateChangeRef, FinReference, FinBranch, FinCCY, CustCIF, FinAmount, " );
		insertSql.append(" OldProfitRate, NewProfitRate, OldProfit, NewProfit, AllowRateChange," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId, Status, ErrorMsg)" );
		insertSql.append(" Values(:BulkRateChangeRef, :FinReference, :FinBranch, :FinCCY, :CustCIF, :FinAmount, ");
		insertSql.append(" :OldProfitRate, :NewProfitRate, :OldProfit, :NewProfit, :AllowRateChange, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId, :Status, :ErrorMsg)" );

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkRateChangeDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return bulkRateChangeDetails.getBulkRateChangeRef();
	}

	/**
	 * This method updates the Record BulkRateChangeDetails or BulkRateChangeDetails_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update BulkRateChangeDetails Details by key BulkRateChangeDetailsLevel and Version
	 * 
	 * @param BulkRateChangeDetails
	 *            Details (bulkRateChangeDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(BulkRateChangeDetails BulkRateChangeDetails, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BulkRateChangeDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinBranch = :FinBranch, FinCCY = :FinCCY,  CustCIF = :CustCIF," );
		updateSql.append(" FinAmount = :FinAmount, OldProfitRate = :OldProfitRate, NewProfitRate =  :NewProfitRate, OldProfit = :OldProfit," );
		updateSql.append(" NewProfit = :NewProfit, AllowRateChange = :AllowRateChange," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, Status = :Status, ErrorMsg = :ErrorMsg" );
		updateSql.append(" Where BulkRateChangeRef = :BulkRateChangeRef AND FinReference = :FinReference " );

		/*if (!type.endsWith("_TEMP")) {	//TODO
			updateSql.append(" AND Version = :Version-1");
		}*/

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(BulkRateChangeDetails);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BulkRateChangeDetails or BulkRateChangeDetails_Temp.
	 * 
	 * save BulkRateChangeDetails Details
	 * 
	 * @param List<BulkRateChangeDetails>
	 *            Details (bulkRateChangeDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void saveList(List<BulkRateChangeDetails> bulkRateChangeDetails, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		if(bulkRateChangeDetails!= null && type.equalsIgnoreCase("_Temp")){
			deleteBulkRateChangeDetailsByRef(bulkRateChangeDetails.get(0).getBulkRateChangeRef(), type);
		}

		insertSql.append("Insert Into BulkRateChangeDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BulkRateChangeRef, FinReference, FinBranch, FinCCY, CustCIF, FinAmount, " );
		insertSql.append(" OldProfitRate, NewProfitRate, OldProfit, NewProfit, AllowRateChange," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId, Status, ErrorMsg)");
		insertSql.append(" Values(:BulkRateChangeRef, :FinReference, :FinBranch, :FinCCY, :CustCIF, :FinAmount, ");
		insertSql.append(" :OldProfitRate, :NewProfitRate, :OldProfit, :NewProfit, :AllowRateChange, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId, :Status, :ErrorMsg)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(bulkRateChangeDetails.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record BulkRateChangeDetails or BulkRateChangeDetails_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update BulkRateChangeDetails Details by key BulkRateChangeDetailsLevel and Version
	 * 
	 * @param List<BulkRateChangeDetails>
	 *            Details (bulkRateChangeDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void updateList(List<BulkRateChangeDetails> bulkRateChangeDetails, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BulkRateChangeDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinBranch = :FinBranch, FinCCY = :FinCCY,  CustCIF = :CustCIF," );
		updateSql.append(" FinAmount = :FinAmount, OldProfitRate = :OldProfitRate, NewProfitRate =  :NewProfitRate, OldProfit = :OldProfit," );
		updateSql.append(" NewProfit = :NewProfit, AllowRateChange = :AllowRateChange," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, Status = :Status, ErrorMsg = :ErrorMsg" );
		updateSql.append(" Where BulkRateChangeRef = :BulkRateChangeRef AND FinReference = :FinReference" );

		/*if (!type.endsWith("_TEMP")) {
			updateSql.append(" AND Version= :Version-1");
		}*/

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(bulkRateChangeDetails.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the BulkRateChangeDetails or
	 * BulkRateChangeDetails_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete BulkRateChangeDetails Details by key BulkRateChangeDetailsLevel
	 * 
	 * @param BulkRateChangeDetails
	 *            Details (bulkRateChangeDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(BulkRateChangeDetails bulkRateChangeDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql =new StringBuilder();
		deleteSql.append("Delete From BulkRateChangeDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  BulkRateChangeRef = :BulkRateChangeRef AND FinReference = :FinReference");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkRateChangeDetail);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),	beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**	
	 * This method is used for Delete the Details Based in Bulk Rate Change Ref 
	 */
	@Override
	public void deleteBulkRateChangeDetailsByRef(String bulkRateChangeRef, String type){
		logger.debug("Entering");
		BulkRateChangeDetails bulkRateChangeDetails = new BulkRateChangeDetails();
		bulkRateChangeDetails.setBulkRateChangeRef(bulkRateChangeRef);;
		StringBuilder deleteSql = new StringBuilder("Delete From BulkRateChangeDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BulkRateChangeRef = :BulkRateChangeRef");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkRateChangeDetails);
		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for fetching List Of Finances between Two dates for Bulk Rate Change
	 */
	@Override
	public List<BulkRateChangeDetails> getBulkRateChangeFinList(String finType, Date schFromDate, String whereClause) {
		logger.debug("Entering");

		BulkRateChangeDetails bulkRateChangeDetails = new BulkRateChangeDetails();
		bulkRateChangeDetails.setLovDescEventFinType(finType);
		bulkRateChangeDetails.setLovDescEventFromDate(schFromDate);

		StringBuilder selectSql = new StringBuilder(" SELECT  distinct FinType, FinReference, CustCIF, FinBranch, FinCcy, LovDescFinDivision," );
		selectSql.append(" ProductCode, OldProfitRate, OldProfit, FinAmount, lovDescFinFormatter" );
		selectSql.append(" FROM BulkRateChange_View" );
		selectSql.append(" WHERE " + whereClause );
		selectSql.append(" ORDER BY FinReference" );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkRateChangeDetails);
		RowMapper<BulkRateChangeDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkRateChangeDetails.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}

	/**
	 * Method for fetching List Of Finances between Two dates for Bulk Rate Change
	 */
	@Override
	public List<BulkRateChangeDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate) { //TODO
		logger.debug("Entering");

		BulkRateChangeDetails bulkRateChangeDetails = new BulkRateChangeDetails();
		bulkRateChangeDetails.setLovDescEventFromDate(fromDate);
		bulkRateChangeDetails.setLovDescEventToDate(toDate);

		StringBuilder selectSql = new StringBuilder(" SELECT FinReference, FinType, " );
		selectSql.append(" FinCcy, ScheduleMethod, ProfitDaysBasis, CustCIF, FinBranch, " );
		selectSql.append(" ProductCode, MIN(SchDate) EventFromDate, MAX(SchDate) EventToDate " );
		selectSql.append(" FROM IjarahFinance_View WHERE SchDate BETWEEN :LovDescEventFromDate AND :LovDescEventToDate ");
		selectSql.append(" GROUP BY FinReference ,FinType ,FinCcy ,ScheduleMethod , ProfitDaysBasis ,CustCIF ,FinBranch ,ProductCode ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bulkRateChangeDetails);
		RowMapper<BulkRateChangeDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkRateChangeDetails.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
	}
}
