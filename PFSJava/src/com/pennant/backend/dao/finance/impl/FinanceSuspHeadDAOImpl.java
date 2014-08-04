/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinanceSuspHeadDAOImpl.java                                          * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-02-2012    														*
 *                                                                  						*
 * Modified Date    :  04-02-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-02-2012       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.dao.finance.impl;

import java.util.Date;
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
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceSuspDetails;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinanceSuspHeadDAOImpl  extends BasisCodeDAO<FinanceSuspHead> implements FinanceSuspHeadDAO {

	private static Logger logger = Logger.getLogger(FinanceSuspHeadDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceSuspHead 
	 * @return FinanceSuspHead
	 */

	@Override
	public FinanceSuspHead getFinanceSuspHead() {
		return new FinanceSuspHead();
	}

	/**
	 * This method get the module from method getFinanceSuspHead() 
	 * and set the new record flag as true and return FinanceSuspHead()
	 * 
	 * @return FinanceSuspHead
	 */
	@Override
	public FinanceSuspHead getNewFinanceSuspHead() {
		logger.debug("Entering");
		FinanceSuspHead suspHead = getFinanceSuspHead();
		logger.debug("Leaving");
		return suspHead;
	}
	
	/**
	 * This method initialize the Record.
	 * @param FinanceSuspHead (suspHead)
 	 * @return FinanceSuspHead
	 */
	@Override
	public void initialize(FinanceSuspHead suspHead) {
		super.initialize(suspHead);
	}
	/**
	 * This method refresh the Record.
	 * @param FinanceSuspHead (suspHead)
 	 * @return void
	 */
	@Override
	public void refresh(FinanceSuspHead provision) {
		
	}
	
	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
	public FinanceSuspHead getFinanceSuspHeadById(String finReference, String type) {
		logger.debug("Entering");
		
		FinanceSuspHead financeSuspHead = new FinanceSuspHead();
		financeSuspHead.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference, FinBranch, FinType," );
		selectSql.append(" CustId, FinSuspSeq, FinIsInSusp, ManualSusp, FinSuspDate, FinSuspTrfDate, FinSuspAmt, FinCurSuspAmt, ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescFinFormatter, lovDescCustCIFName,lovDescCustShrtName, ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" From FinSuspHead");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeSuspHead);
		RowMapper<FinanceSuspHead> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceSuspHead.class);

		try {
			financeSuspHead = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			financeSuspHead = null;
		}
		logger.debug("Leaving");
		return financeSuspHead;
	}
	
	@Override
	public List<String> getSuspFinanceList() {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("SELECT FinReference From FinSuspHead ");
		selectSql.append(" WHERE FinIsInSusp = 1 ");
		
		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(),
				new BeanPropertySqlParameterSource(""), String.class);
	}
	
	/**
	 * This method insert new Records into FinanceSuspHead .
	 *
	 * save FinanceSuspHead 
	 * 
	 * @param FinanceSuspHead (financeSuspHead)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(FinanceSuspHead financeSuspHead,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinSuspHead");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, FinBranch, FinType, CustId," );
		insertSql.append(" FinSuspSeq, FinIsInSusp, ManualSusp, FinSuspDate, FinSuspTrfDate, FinSuspAmt, FinCurSuspAmt, " );
		insertSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId )");
		insertSql.append(" Values(:FinReference, :FinBranch, :FinType, :CustId, ");
		insertSql.append(" :FinSuspSeq, :FinIsInSusp, :ManualSusp, :FinSuspDate, :FinSuspTrfDate, :FinSuspAmt, :FinCurSuspAmt, " );
		insertSql.append(" :Version, :LastMntOn, :LastMntBy, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId ) ");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeSuspHead);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeSuspHead.getFinReference();
	}
	
	/**
	 * This method updates the Record FinanceSuspHead .
	 * update FinanceSuspHead
	 * 
	 * @param FinanceSuspHead (financeSuspHead)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinanceSuspHead financeSuspHead,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinSuspHead");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinReference = :FinReference, FinBranch = :FinBranch, FinType = :FinType,");
		updateSql.append(" CustId = :CustId, FinSuspSeq = :FinSuspSeq, FinIsInSusp = :FinIsInSusp, ManualSusp =:ManualSusp,");
		updateSql.append(" FinSuspDate = :FinSuspDate, FinSuspTrfDate=:FinSuspTrfDate, FinSuspAmt = :FinSuspAmt, FinCurSuspAmt = :FinCurSuspAmt, " );
		updateSql.append(" Version =:Version , LastMntOn=:LastMntOn, LastMntBy=:LastMntBy,RecordStatus=:RecordStatus, RoleCode=:RoleCode, " );
		updateSql.append(" NextRoleCode=:NextRoleCode, TaskId=:TaskId, NextTaskId=:NextTaskId, RecordType=:RecordType, WorkflowId=:WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeSuspHead);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method Deletes the Record from the BMTAcademics or
	 * BMTAcademics_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete Academic Details by key AcademicLevel
	 * 
	 * @param Academic
	 *            Details (academic)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FinanceSuspHead financeSuspHead,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql =new StringBuilder();
		deleteSql.append("Delete From FinSuspHead");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference ");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeSuspHead);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),	beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", financeSuspHead.getFinReference(), financeSuspHead.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", financeSuspHead.getFinReference(), financeSuspHead.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Save Finance Suspend Details
	 */
	@Override
	public String saveSuspenseDetails(FinanceSuspDetails financeSuspDetails,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinSuspDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, FinBranch, FinType, CustId, FinSuspSeq, FinTrfDate," );
		insertSql.append(" FinTrfMvt, FinTrfAmt, FinODDate , FinTrfFromDate, LinkedTranId )");
		insertSql.append(" Values(:FinReference, :FinBranch, :FinType, :CustId, :FinSuspSeq, ");
		insertSql.append(" :FinTrfDate, :FinTrfMvt, :FinTrfAmt, :FinODDate , :FinTrfFromDate, :LinkedTranId )");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeSuspDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeSuspDetails.getFinReference();
	}
	
	/**
	 * Method for Fetch Finance Suspend Details List
	 */
	@Override
	public List<FinanceSuspDetails> getFinanceSuspDetailsListById(String finReference) {
		logger.debug("Entering");
		
		FinanceSuspDetails suspDetails = new FinanceSuspDetails();
		suspDetails.setFinReference(finReference);
		
		StringBuilder selectSql =new StringBuilder("Select FinReference, FinBranch, " );
		selectSql.append(" FinType, CustId, FinSuspSeq, FinTrfDate, FinTrfMvt, " );
		selectSql.append(" FinTrfAmt, FinODDate , FinTrfFromDate, LinkedTranId ");
		selectSql.append(" FROM FinSuspDetail ");
		selectSql.append(" WHERE FInReference =:FinReference ");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(suspDetails);
		RowMapper<FinanceSuspDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceSuspDetails.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	
	/**
	 * Method for Fetch Finance Suspend Details List
	 */
	@Override
	public List<FinStatusDetail> getCustSuspDate(List<Long> CustIdList) {
		logger.debug("Entering");
		
		Map<String, List<Long>> beanParameters=new HashMap<String, List<Long>>();
		beanParameters.put("CustId", CustIdList);
		
		StringBuilder selectSql =new StringBuilder("Select CustId, MIN(FinSuspTrfDate) AS ValueDate " );
		selectSql.append(" FROM FinSuspHead where CustId IN(:CustId) AND FinIsInSusp = 1 GROUP BY CustId ");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<FinStatusDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinStatusDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	
	/**
	 * Method for Fetch Finance Suspend Details List
	 */
	@Override
	public Date getCustSuspDate(long custId) {
		logger.debug("Entering");
		
		FinanceSuspHead suspHead = new FinanceSuspHead();
		suspHead.setCustId(custId);
		
		StringBuilder selectSql =new StringBuilder("Select CustStsChgDate FROM Customers where CustId = :CustId ");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(suspHead);
		logger.debug("Leaving");
		
		Date suspFromDate = null;
		try {
			suspFromDate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,Date.class);
        } catch (Exception e) {
        	logger.error(e.getMessage());
        	suspFromDate = null;
        }
		return suspFromDate;
	}

	/**
	 * This method updates the Record FinanceSuspHead Flag
	 */
	@Override
	public void updateSuspFlag(String finReference ) {
		logger.debug("Entering");
		
		FinanceSuspHead suspHead = new FinanceSuspHead();
		suspHead.setFinReference(finReference);
		
		StringBuilder	updateSql =new StringBuilder(" Update FinSuspHead ");
		updateSql.append(" Set FinIsInSusp = 0  Where FinReference =:FinReference");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(suspHead);
		try {
			this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.debug("Finance Reference Record Not in Suspense.");
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String finReference ,String userLanguage){
		String[][] parms= new String[1][1]; 
		parms[1][0] = finReference;

		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}
