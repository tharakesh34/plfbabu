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
 * FileName    		:  BundledProductsDetailDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.finance.BundledProductsDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.BundledProductsDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>BundledProductsDetail model</b> class.<br>
 */
public class BundledProductsDetailDAOImpl extends BasisCodeDAO<BundledProductsDetail> implements BundledProductsDetailDAO {

	private static Logger logger = Logger.getLogger(BundledProductsDetailDAOImpl.class);
	
	public BundledProductsDetailDAOImpl() {
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
	 * @return BundledProductsDetail
	 */
	@Override
	public BundledProductsDetail getBundledProductsDetailByID(String finReference, String type) {
		logger.debug("Entering");
		BundledProductsDetail bundledProductsDetail = new BundledProductsDetail();
		bundledProductsDetail.setId(finReference);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select FinReference, CardProduct, SalesStaff, EmbossingName, StatusOfCust, MinRepay, BillingAcc, StmtAddress, ");
		selectSql.append(" StmtEmail, PhysicalAddress, ContactNumber, Ref1Name, Ref1PhoneNum, Ref1Email, Ref2Name, Ref2PhoneNum, Ref2Email, ");
		selectSql.append(" BankName, ChequeNo, ChequeAmt, CardType, ClassType, LimitRecommended, LimitApproved, ProfitRate, CrossSellCard, UrgentIssuance, ");
		
		if(type.contains("View")){
			selectSql.append(" ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BundledProductsDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bundledProductsDetail);
		RowMapper<BundledProductsDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BundledProductsDetail.class);
		
		try{
			bundledProductsDetail = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			bundledProductsDetail = null;
		}
		logger.debug("Leaving");
		return bundledProductsDetail;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the BundledProductsDetail or
	 * BundledProductsDetail_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Equipment Loan Details by key
	 * EquipmentLoanId
	 * 
	 * @param Equipment
	 *            Loan Details (bundledProductsDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(BundledProductsDetail bundledProductsDetail,String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From BundledProductsDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bundledProductsDetail);
		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into BundledProductsDetail or
	 * BundledProductsDetail_Temp. it fetches the available Sequence form
	 * SeqBundledProductsDetail by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Equipment Loan Details
	 * 
	 * @param Equipment
	 *            Loan Details (bundledProductsDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public String save(BundledProductsDetail bundledProductsDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder();
		insertSql.append(" Insert Into BundledProductsDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" ( FinReference, CardProduct, SalesStaff, EmbossingName, StatusOfCust, MinRepay, BillingAcc, StmtAddress, StmtEmail, " );
		insertSql.append(" PhysicalAddress, ContactNumber, Ref1Name, Ref1PhoneNum, Ref1Email, Ref2Name, Ref2PhoneNum, Ref2Email, BankName, ");
		insertSql.append(" ChequeNo, ChequeAmt, CardType, ClassType, LimitRecommended, LimitApproved, ProfitRate, CrossSellCard, UrgentIssuance,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values( :FinReference, :CardProduct, :SalesStaff, :EmbossingName, :StatusOfCust, :MinRepay, :BillingAcc, :StmtAddress, :StmtEmail, ");
		insertSql.append(" :PhysicalAddress, :ContactNumber, :Ref1Name, :Ref1PhoneNum, :Ref1Email, :Ref2Name, :Ref2PhoneNum, :Ref2Email, :BankName, ");
		insertSql.append(" :ChequeNo, :ChequeAmt, :CardType, :ClassType, :LimitRecommended, :LimitApproved, :ProfitRate, :CrossSellCard, :UrgentIssuance,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bundledProductsDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return bundledProductsDetail.getId();
	}
	
	/**
	 * This method updates the Record BundledProductsDetail or
	 * BundledProductsDetail_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Equipment Loan Details by key
	 * EquipmentLoanId and Version
	 * 
	 * @param Equipment
	 *            Loan Details (bundledProductsDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(BundledProductsDetail bundledProductsDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder();
		updateSql.append(" Update BundledProductsDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set CardProduct = :CardProduct, SalesStaff = :SalesStaff, EmbossingName = :EmbossingName," );
		updateSql.append(" StatusOfCust = :StatusOfCust, MinRepay = :MinRepay, BillingAcc = :BillingAcc, StmtAddress = :StmtAddress, StmtEmail = :StmtEmail, " );
		updateSql.append(" PhysicalAddress = :PhysicalAddress, ContactNumber = :ContactNumber, Ref1Name = :Ref1Name, Ref1PhoneNum = :Ref1PhoneNum, " );
		updateSql.append(" Ref1Email = :Ref1Email,  Ref2Name = :Ref2Name, Ref2PhoneNum = :Ref2PhoneNum, Ref2Email = :Ref2Email, BankName = :BankName," );
		updateSql.append(" ChequeNo = :ChequeNo,  ChequeAmt = :ChequeAmt, CardType = :CardType, ClassType = :ClassType, LimitRecommended = :LimitRecommended," );
		updateSql.append(" LimitApproved = :LimitApproved,  ProfitRate = :ProfitRate, CrossSellCard = :CrossSellCard, UrgentIssuance = :UrgentIssuance, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bundledProductsDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}