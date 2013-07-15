package com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.impl;

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
import com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.CreditApplicationReviewDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevType;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

public class CreditApplicationReviewDAOImpl extends BasisNextidDaoImpl<FinCreditReviewDetails> implements CreditApplicationReviewDAO {
	private static Logger logger = Logger.getLogger(CreditApplicationReviewDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * Method for get the CreditRevCategory Details
	 */
	public List<FinCreditRevCategory> getCreditRevCategoryByCreditRevCode(String creditRevCode) {
		logger.debug("Entering");
		FinCreditRevCategory finCreditRevCategory= new FinCreditRevCategory();
		finCreditRevCategory.setCreditRevCode(creditRevCode);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CategoryId,CategorySeque,CreditRevCode,CategoryDesc,Remarks,NoOfyears,changedsply,");
		selectSql.append(" brkdowndsply,Version,LastMntBy,");
		selectSql.append(" LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId");
		selectSql.append(" FROM FinCreditRevCategory Where CreditRevCode= :CreditRevCode ");


		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRevCategory);
		RowMapper<FinCreditRevCategory> typeRowMapper = ParameterizedBeanPropertyRowMapper
		.newInstance(FinCreditRevCategory.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
	}
	/**
	 * This method initialise the Record.
	 * 
	 * @param FinCreditReviewDetails
	 *            (creditReviewDetails)
	 * @return FinCreditReviewDetails
	 */
	@Override
	public void initialize(FinCreditReviewDetails creditReviewDetails) {
		super.initialize(creditReviewDetails);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinCreditReviewDetails
	 *            (creditReviewDetails)
	 * @return void
	 */
	@Override
	public void refresh(FinCreditReviewDetails creditReviewDetails) {

	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId) {
		logger.debug("Entering");
		FinCreditRevSubCategory finCreditRevSubCategory= new FinCreditRevSubCategory();
		finCreditRevSubCategory.setCategoryId(categoryId);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT SubCategoryCode,SubCategorySeque,CategoryId,SubCategoryDesc,SubCategoryItemType,");
		selectSql.append(" MainSubCategoryCode,ItemsToCal,ItemRule,isCreditCCY,Version,LastMntBy,LastMntOn,RecordStatus,format,grand,");
		selectSql.append(" RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId");
		selectSql.append(" FROM FinCreditRevSubCategory Where CategoryId= :CategoryId order by SubCategorySeque");


		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRevSubCategory);
		RowMapper<FinCreditRevSubCategory> typeRowMapper = ParameterizedBeanPropertyRowMapper
		.newInstance(FinCreditRevSubCategory.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
	}
	
	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(long categoryId) {
		logger.debug("Entering");
		FinCreditRevSubCategory finCreditRevSubCategory= new FinCreditRevSubCategory();
		finCreditRevSubCategory.setCategoryId(categoryId);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT SubCategoryCode,SubCategorySeque,CategoryId,SubCategoryDesc,SubCategoryItemType,");
		selectSql.append(" MainSubCategoryCode,ItemsToCal,ItemRule,isCreditCCY,Version,LastMntBy,LastMntOn,RecordStatus,format,grand,");
		selectSql.append(" RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId");
		selectSql.append(" FROM FinCreditRevSubCategory Where CategoryId= :CategoryId order by CalcSeque");


		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRevSubCategory);
		RowMapper<FinCreditRevSubCategory> typeRowMapper = ParameterizedBeanPropertyRowMapper
		.newInstance(FinCreditRevSubCategory.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
	}
	
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinCreditReviewDetails 
	 * @return FinCreditReviewDetails
	 */
	@Override
	public FinCreditReviewDetails getCreditReviewDetails() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinCreditReviewDetails");
		FinCreditReviewDetails creditReviewDetails= new FinCreditReviewDetails();
		if (workFlowDetails!=null){
			creditReviewDetails.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return creditReviewDetails;
	}

	/**
	 * This method get the module from method getCreditReviewDetails() and 
	 * set the new record flag as true and return FinCreditReviewDetails()   
	 * @return FinCreditReviewDetails
	 */
	@Override
	public FinCreditReviewDetails getNewCreditReviewDetails() {
		logger.debug("Entering");
		FinCreditReviewDetails creditReviewDetails = getCreditReviewDetails();
		creditReviewDetails.setNewRecord(true);
		logger.debug("Leaving");
		return creditReviewDetails;
	}

	/**
	 * Fetch the Record  FinCreditReviewDetails  by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinCreditReviewDetails
	 */
	@Override
	public FinCreditReviewDetails getCreditReviewDetailsById(final long id, String type) {
		logger.debug("Entering");
		FinCreditReviewDetails creditReviewDetails = getCreditReviewDetails();

		creditReviewDetails.setDetailId(id);

		StringBuilder selectSql = new StringBuilder("SELECT DetailId,CreditRevCode,CustomerId,AuditYear,BankName,");
		selectSql.append(" Auditors,ConsolOrUnConsol,Location,ConversionRate,AuditedDate,NoOfShares,MarketPrice,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescCustCIF,lovDescCustCtgCode,lovDescCustShrtName");
		}
		selectSql.append(" From FinCreditReviewDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DetailId =:DetailId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		RowMapper<FinCreditReviewDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinCreditReviewDetails.class);

		try{
			creditReviewDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			creditReviewDetails = null;
		}
		logger.debug("Leaving");
		return creditReviewDetails;
	}


	/**
	 * This method Deletes the Record from the CreditReviewDetails or CreditReviewDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete FinCreditReviewDetails by key detailId
	 * 
	 * @param FinCreditReviewDetails (creditReviewDetails)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FinCreditReviewDetails creditReviewDetails,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinCreditReviewDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DetailId =:DetailId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",creditReviewDetails.getDetailId() , 
						creditReviewDetails.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",creditReviewDetails.getDetailId() ,
					creditReviewDetails.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CreditReviewDetails or CreditReviewDetails_Temp.
	 * it fetches the available Sequence form SeqCreditReviewDetails by using getNextidviewDAO().getNextId() method.  
	 *
	 * save FinCreditReviewDetails
	 * 
	 * @param FinCreditReviewDetails (creditReviewDetails)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(FinCreditReviewDetails creditReviewDetails,String type) {
		logger.debug("Entering");
		if (creditReviewDetails.getDetailId()==Long.MIN_VALUE){
			creditReviewDetails.setDetailId(getNextidviewDAO().getNextId("SeqFinCreditReviewDetails"));
		}	
		StringBuilder insertSql =new StringBuilder("Insert Into FinCreditReviewDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (DetailId,CreditRevCode,CustomerId,AuditYear,BankName,Auditors,ConsolOrUnConsol,Location,");
		insertSql.append("  ConversionRate,AuditedDate,NoOfShares,MarketPrice,Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,");
		insertSql.append(" TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(" Values ( :DetailId, :CreditRevCode, :CustomerId, :AuditYear, :BankName, :Auditors, :ConsolOrUnConsol, ");
		insertSql.append(" :Location, :ConversionRate, :AuditedDate, :NoOfShares, :MarketPrice, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return creditReviewDetails.getDetailId();
	}

	/**
	 * This method updates the Record CreditReviewDetails or CreditReviewDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update FinCreditReviewDetails by key detailId and Version
	 * 
	 * @param FinCreditReviewDetails(creditReviewDetails)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(FinCreditReviewDetails creditReviewDetails,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinCreditReviewDetails");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set DetailId = :DetailId,CreditRevCode = :CreditRevCode,CustomerId = :CustomerId,");
		updateSql.append(" AuditYear = :AuditYear,BankName = :BankName,Auditors = :Auditors,ConsolOrUnConsol = :ConsolOrUnConsol,");
		updateSql.append(" Location = :Location,ConversionRate = :ConversionRate,AuditedDate = :AuditedDate," );
		updateSql.append(" NoOfShares = :NoOfShares,MarketPrice = :MarketPrice," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where DetailId =:DetailId");

		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",creditReviewDetails.getDetailId() ,
					creditReviewDetails.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}


	private ErrorDetails  getError(String errorId, long detailId, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] =String.valueOf(detailId);
		parms[0][0] = PennantJavaUtil.getLabel("label_DetailId")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	/**
	 * This method for getting the creditreviewdetails by creditrevcode
	 * @param creditRevCode
	 */
	public FinCreditRevType getFinCreditRevByRevCode(String creditRevCode) {
		logger.debug("Entering");
		FinCreditRevType finCreditRev= new FinCreditRevType();
		finCreditRev.setCreditRevCode(creditRevCode);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CreditRevCode,CreditRevDesc,CreditCCY,EntryCCY,Version,LastMntBy,LastMntOn,");		
		selectSql.append(" RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId");
		selectSql.append("  FROM FinCreditRevType Where CreditRevCode= :CreditRevCode ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRev);
		RowMapper<FinCreditRevType> typeRowMapper = ParameterizedBeanPropertyRowMapper
		.newInstance(FinCreditRevType.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
	}

	
	/**
	 * This method for checking whether record is already existed with the customer id and audited year.<br.
	 * @param custID
	 * @param auditYear
	 * @return int
	 */
	public int isCreditSummaryExists(long custID,String auditYear){
		logger.debug("Entering");
		
		FinCreditReviewDetails creditReviewDetails = getCreditReviewDetails();
		creditReviewDetails.setCustomerId(custID);
		creditReviewDetails.setAuditYear(auditYear);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT count(*) ");		
		selectSql.append("  FROM FinCreditReviewDetails_View Where CustomerId= :CustomerId  and AuditYear  = :AuditYear");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		logger.debug("selectSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);	
		
	}
	

}
