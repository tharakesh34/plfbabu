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
 * FileName    		:  EtihadCreditBureauDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.finance.EtihadCreditBureauDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.EtihadCreditBureauDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>EtihadCreditBureauDetail model</b> class.<br>
 */
public class EtihadCreditBureauDetailDAOImpl extends BasisCodeDAO<EtihadCreditBureauDetail> implements EtihadCreditBureauDetailDAO {

	private static Logger logger = Logger.getLogger(EtihadCreditBureauDetailDAOImpl.class);
	
	public EtihadCreditBureauDetailDAOImpl() {
		super();
	}
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * Fetch the Record Equipment Loan Details details by key field
	 * 
	 * @param id (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return EtihadCreditBureauDetail
	 */
	@Override
	public EtihadCreditBureauDetail getEtihadCreditBureauDetailByID(String finReference, String type) {
		logger.debug("Entering");
		EtihadCreditBureauDetail etihadCreditBureauDetail = new EtihadCreditBureauDetail();
		etihadCreditBureauDetail.setId(finReference);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select FinReference,BureauScore ,TotOutstandingAmt,TotOverdueAmt,DefaultContracts,TotMonthlyInst, ");
		selectSql.append(" WorstCurrPayDelay,WorstPayDelay,WorstStatus,OldConStartDate,NewConStartDate,OtherBankFinType, ");
		selectSql.append(" NoOfInquiry,NoOfContractsInst,NoOfContractsNonInst,NoOfContractsCredit, ");
		
		if(type.contains("View")){
			selectSql.append(" ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From EtihadCreditBureauDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(etihadCreditBureauDetail);
		RowMapper<EtihadCreditBureauDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(EtihadCreditBureauDetail.class);
		
		try{
			etihadCreditBureauDetail = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			etihadCreditBureauDetail = null;
		}
		logger.debug("Leaving");
		return etihadCreditBureauDetail;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the EtihadCreditBureauDetail or
	 * EtihadCreditBureauDetail_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Equipment Loan Details by key
	 * EquipmentLoanId
	 * 
	 * @param Equipment
	 *            Loan Details (etihadCreditBureauDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(EtihadCreditBureauDetail etihadCreditBureauDetail,String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From EtihadCreditBureauDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(etihadCreditBureauDetail);
		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into EtihadCreditBureauDetail or
	 * EtihadCreditBureauDetail_Temp. it fetches the available Sequence form
	 * SeqEtihadCreditBureauDetail by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Equipment Loan Details
	 * 
	 * @param Equipment
	 *            Loan Details (etihadCreditBureauDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public String save(EtihadCreditBureauDetail etihadCreditBureauDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder();
		insertSql.append(" Insert Into EtihadCreditBureauDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference,BureauScore ,TotOutstandingAmt,TotOverdueAmt,DefaultContracts,TotMonthlyInst," );
		insertSql.append(" WorstCurrPayDelay,WorstPayDelay,WorstStatus,OldConStartDate,NewConStartDate,OtherBankFinType,");
		insertSql.append(" NoOfInquiry,NoOfContractsInst,NoOfContractsNonInst,NoOfContractsCredit,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values( :FinReference, :BureauScore , :TotOutstandingAmt, :TotOverdueAmt, :DefaultContracts, :TotMonthlyInst,");
		insertSql.append(" :WorstCurrPayDelay, :WorstPayDelay, :WorstStatus, :OldConStartDate, :NewConStartDate, :OtherBankFinType,");
		insertSql.append(" :NoOfInquiry, :NoOfContractsInst, :NoOfContractsNonInst, :NoOfContractsCredit,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(etihadCreditBureauDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return etihadCreditBureauDetail.getId();
	}
	
	/**
	 * This method updates the Record EtihadCreditBureauDetail or
	 * EtihadCreditBureauDetail_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Equipment Loan Details by key
	 * EquipmentLoanId and Version
	 * 
	 * @param Equipment
	 *            Loan Details (etihadCreditBureauDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(EtihadCreditBureauDetail etihadCreditBureauDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder();
		updateSql.append(" Update EtihadCreditBureauDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set BureauScore = :BureauScore," );
		updateSql.append(" TotOutstandingAmt = :TotOutstandingAmt, TotOverdueAmt = :TotOverdueAmt, DefaultContracts = :DefaultContracts, " );
		updateSql.append(" TotMonthlyInst = :TotMonthlyInst, WorstCurrPayDelay = :WorstCurrPayDelay, WorstPayDelay = :WorstPayDelay, " );
		updateSql.append(" WorstStatus = :WorstStatus,  OldConStartDate = :OldConStartDate, NewConStartDate = :NewConStartDate, " );
		updateSql.append(" OtherBankFinType = :OtherBankFinType,  NoOfInquiry = :NoOfInquiry, NoOfContractsInst = :NoOfContractsInst, " );
		updateSql.append(" NoOfContractsNonInst = :NoOfContractsNonInst,  NoOfContractsCredit = :NoOfContractsCredit,  " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(etihadCreditBureauDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
}