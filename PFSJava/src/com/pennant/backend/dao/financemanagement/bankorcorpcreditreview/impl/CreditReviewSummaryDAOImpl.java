package com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.CreditReviewSummaryDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

public class CreditReviewSummaryDAOImpl extends BasisNextidDaoImpl<FinCreditReviewSummary> implements CreditReviewSummaryDAO {
	private static Logger logger = Logger.getLogger(CreditReviewSummaryDAOImpl.class);
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	/**
	 * This method set the Work Flow id based on the module name and return the new FinCreditReviewSummary 
	 * @return FinCreditReviewSummary
	 */
	@Override
	public FinCreditReviewSummary getCreditReviewSummary() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinCreditReviewSummary");
		FinCreditReviewSummary creditReviewSummary= new FinCreditReviewSummary();
		if (workFlowDetails!=null){
			creditReviewSummary.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return creditReviewSummary;
	}

	/**
	 * This method get the module from method getFinCreditReviewSummary() and set the
	 * new record flag as true and return FinCreditReviewSummary()
	 * 
	 * @return FinCreditReviewSummary
	 */
	@Override
	public FinCreditReviewSummary getNewCreditReviewSummary() {
		logger.debug("Entering");
		FinCreditReviewSummary creditReviewSummary = getCreditReviewSummary();
		creditReviewSummary.setNewRecord(true);
		logger.debug("Leaving");
		return creditReviewSummary;
	}

	/**
	 * Fetch the Record  CreditReviewSummary by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinCreditReviewSummary
	 */
	@Override
	public FinCreditReviewSummary getCreditReviewSummaryById(final long summaryId,long detailId,String type) {
		logger.debug("Entering");
		FinCreditReviewSummary creditReviewSummary = getCreditReviewSummary();		
		creditReviewSummary.setDetailId(detailId);
		creditReviewSummary.setSummaryId(summaryId);

		StringBuilder selectSql = new StringBuilder("Select SummaryId,DetailId,SubCategoryCode,ItemValue," );
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From FinCreditReviewSummary");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DetailId =:DetailId and SummaryId = :SummaryId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewSummary);
		RowMapper<FinCreditReviewSummary> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinCreditReviewSummary.class);

		try{
			creditReviewSummary = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			creditReviewSummary = null;
		}
		logger.debug("Leaving");
		return creditReviewSummary;
	}

	/**
	 * This method initialise the Record.
	 * 
	 * @param FinCreditReviewSummary
	 *            (creditReviewSummary)
	 * @return FinCreditReviewSummary
	 */
	@Override
	public void initialize(FinCreditReviewSummary creditReviewSummary) {
		super.initialize(creditReviewSummary);
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
	 * This method refresh the Record.
	 * 
	 * @param FinCreditReviewSummary
	 *            (creditReviewSummary)
	 * @return void
	 */
	@Override
	public void refresh(FinCreditReviewSummary creditReviewSummary) {

	}
	/**
	 * Fetch the Record  FinCreditReviewSummary details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinCreditReviewSummary
	 */
	@Override
	public List<FinCreditReviewSummary> getListCreditReviewSummaryById(final long id, String type, boolean postingsProcess) {
		logger.debug("Entering");

		FinCreditReviewSummary creditReviewSummary = getCreditReviewSummary();
		creditReviewSummary.setDetailId(id);

		StringBuilder selectSql = new StringBuilder(" Select SummaryId,DetailId,SubCategoryCode,ItemValue," );
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		if(!postingsProcess){
			selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
			selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		}
		selectSql.append(" From FinCreditReviewSummary");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DetailId =:DetailId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewSummary);
		RowMapper<FinCreditReviewSummary> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FinCreditReviewSummary.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}

	/**
	 * This method Deletes the Record from the FinCreditReviewSummary or FinCreditReviewSummary_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete FinCreditReviewSummary by key detailid and summary id
	 * 
	 * @param FinCreditReviewSummary (creditReviewSummary)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FinCreditReviewSummary creditReviewSummary,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinCreditReviewSummary");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DetailId =:DetailId and SummaryId = :SummaryId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewSummary);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",creditReviewSummary.getId() ,
						creditReviewSummary.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",creditReviewSummary.getId() ,
					creditReviewSummary.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method for deleting the summary details of the perticular detail item id
	 */
	public void deleteByDetailId(long detailId,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		FinCreditReviewSummary creditReviewSummary = new FinCreditReviewSummary();
		creditReviewSummary.setDetailId(detailId);
		StringBuilder deleteSql = new StringBuilder("Delete From FinCreditReviewSummary");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DetailId =:DetailId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewSummary);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",creditReviewSummary.getDetailId() ,
						creditReviewSummary.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",creditReviewSummary.getDetailId() ,
					creditReviewSummary.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinCreditReviewSummary or
	 * TransactionEntry_Temp. it fetches the available Sequence form
	 * SeqFinCreditReviewSummary by using getNextidviewDAO().getNextId() method.
	 * 
	 * save FinCreditReviewSummary Entry
	 * 
	 * @param FinCreditReviewSummary
	 *            Entry (creditReviewSummary)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(FinCreditReviewSummary creditReviewSummary,String type) {
		logger.debug("Entering");
		if (creditReviewSummary.getSummaryId() == Long.MIN_VALUE){
			creditReviewSummary.setSummaryId(getNextidviewDAO().getNextId("SeqFinCreditReviewSummary"));
		}
		System.out.println(creditReviewSummary.getSubCategoryCode());
		System.out.println(creditReviewSummary.getItemValue());
		logger.debug("get NextID:"+creditReviewSummary.getSummaryId());
		StringBuilder insertSql =new StringBuilder("Insert Into FinCreditReviewSummary");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SummaryId,DetailId,SubCategoryCode,ItemValue," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId) ");
		insertSql.append(" Values(:SummaryId, :DetailId, :SubCategoryCode, :ItemValue," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewSummary);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return creditReviewSummary.getId();
	}

	/**
	 * This method updates the Record FinCreditReviewSummary or FinCreditReviewSummary.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update FinCreditReviewSummary Entry by key DetailId and Version
	 * 
	 * @param FinCreditReviewSummary (creditReviewSummary)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(FinCreditReviewSummary creditReviewSummary,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinCreditReviewSummary");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set SummaryId = :SummaryId,DetailId = :DetailId,SubCategoryCode = :SubCategoryCode,ItemValue = :ItemValue," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where DetailId =:DetailId and SummaryId = :SummaryId");

		/*if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}*/

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewSummary);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",creditReviewSummary.getId() ,creditReviewSummary.getUserDetails().getUsrLanguage());
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
	 * This method for getting the list of summary details based on the customer id and year.<br>
	 * 
	 */
	@Override
	public List<FinCreditReviewSummary> getListCreditReviewSummaryByYearAndCustId(long id, String year) {
		logger.debug("Entering");
		Map<String,Object> namedParameterMap = new HashMap<String,Object>();
		namedParameterMap.put("Customerid", id);
		namedParameterMap.put("Audityear", year);
		List<FinCreditReviewSummary> listOfCreditReviewSummary = null;
		StringBuilder selectSql = new StringBuilder(" select T1.SummaryId,T1.DetailId,T1.SubCategoryCode,T1.ItemValue,");
		selectSql.append(" T2.ConversionRate as LovDescConversionRate,T2.bankName as LovDescBankName ,T2.noOfShares as lovDescNoOfShares,");
		selectSql.append(" T2.marketPrice as lovDescMarketPrice from FinCreditReviewSummary as T1  ");
		selectSql.append(" inner join finCreditReviewDetails as T2 on T1.detailId = T2.detailId ");
		selectSql.append(" and T2.Customerid = :Customerid and T2.Audityear = :Audityear");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinCreditReviewSummary> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FinCreditReviewSummary.class);
		logger.debug("Leaving");
		try{
			listOfCreditReviewSummary = this.namedParameterJdbcTemplate.query(selectSql.toString(), namedParameterMap, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			listOfCreditReviewSummary = null;
		}
		return listOfCreditReviewSummary ;	
	}

}
