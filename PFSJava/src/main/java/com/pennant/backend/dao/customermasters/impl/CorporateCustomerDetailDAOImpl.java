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
 * FileName    		:  CorporateCustomerDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.customermasters.impl;

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

import com.pennant.backend.dao.customermasters.CorporateCustomerDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CorporateCustomerDetail model</b> class.<br>
 * 
 */
public class CorporateCustomerDetailDAOImpl extends BasisNextidDaoImpl<CorporateCustomerDetail> 
				implements CorporateCustomerDetailDAO {

	private static Logger logger = Logger.getLogger(CorporateCustomerDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public CorporateCustomerDetailDAOImpl() {
		super();
	}


	/**
	 * Fetch the Record  Corporate Detail details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CorporateCustomerDetail
	 */
	@Override
	public CorporateCustomerDetail getCorporateCustomerDetailById(final long id, String type) {
		logger.debug("Entering");
		
		CorporateCustomerDetail corporateCustomerDetail = new CorporateCustomerDetail();
		corporateCustomerDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select CustId, Name, PhoneNumber, EmailId," );
		selectSql.append(" BussCommenceDate, ServCommenceDate, BankRelationshipDate, PaidUpCapital," );
		selectSql.append(" AuthorizedCapital, ReservesAndSurPlus, IntangibleAssets, TangibleNetWorth," );
		selectSql.append(" LongTermLiabilities, CapitalEmployed, Investments, NonCurrentAssets," );
		selectSql.append(" NetWorkingCapital, NetSales, OtherIncome, NetProfitAfterTax, Depreciation," );
		selectSql.append(" CashAccurals, AnnualTurnover, ReturnOnCapitalEmp, CurrentAssets," );
		selectSql.append(" CurrentLiabilities, CurrentBookValue, CurrentMarketValue, PromotersShare," );
		selectSql.append(" AssociatesShare, PublicShare, FinInstShare, Others, ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescCustCIF,lovDescCustShrtName,lovDescCustRecordType, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From CustomerCorporateDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustId =:CustId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(corporateCustomerDetail);
		RowMapper<CorporateCustomerDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CorporateCustomerDetail.class);
		
		try{
			corporateCustomerDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			corporateCustomerDetail = null;
		}
		logger.debug("Leaving");
		return corporateCustomerDetail;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the CustomerCorporateDetail or CustomerCorporateDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Corporate Detail by key CustId
	 * 
	 * @param Corporate Detail (corporateCustomerDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CorporateCustomerDetail corporateCustomerDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From CustomerCorporateDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustId =:CustId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(corporateCustomerDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into CustomerCorporateDetail or
	 * CustomerCorporateDetail_Temp. it fetches the available Sequence form
	 * SeqCustomerCorporateDetail by using getNextidviewDAO().getNextId()
	 * method.
	 * 
	 * save Corporate Detail
	 * 
	 * @param Corporate
	 *            Detail (corporateCustomerDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CorporateCustomerDetail corporateCustomerDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into CustomerCorporateDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustId, Name, PhoneNumber, EmailId, BussCommenceDate, ServCommenceDate," );
		insertSql.append(" BankRelationshipDate, PaidUpCapital, AuthorizedCapital, ReservesAndSurPlus," );
		insertSql.append(" IntangibleAssets, TangibleNetWorth, LongTermLiabilities, CapitalEmployed," );
		insertSql.append(" Investments, NonCurrentAssets, NetWorkingCapital, NetSales, OtherIncome," );
		insertSql.append(" NetProfitAfterTax, Depreciation, CashAccurals, AnnualTurnover, ReturnOnCapitalEmp,");
		insertSql.append(" CurrentAssets, CurrentLiabilities, CurrentBookValue, CurrentMarketValue," );
		insertSql.append(" PromotersShare, AssociatesShare, PublicShare, FinInstShare, Others, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId," );
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustId, :Name, :PhoneNumber, :EmailId, :BussCommenceDate," );
		insertSql.append(" :ServCommenceDate, :BankRelationshipDate, :PaidUpCapital, :AuthorizedCapital," );
		insertSql.append(" :ReservesAndSurPlus, :IntangibleAssets, :TangibleNetWorth, :LongTermLiabilities,");
		insertSql.append(" :CapitalEmployed, :Investments, :NonCurrentAssets, :NetWorkingCapital, :NetSales,");
		insertSql.append(" :OtherIncome, :NetProfitAfterTax, :Depreciation, :CashAccurals, :AnnualTurnover,");
		insertSql.append(" :ReturnOnCapitalEmp, :CurrentAssets, :CurrentLiabilities, :CurrentBookValue," );
		insertSql.append(" :CurrentMarketValue, :PromotersShare, :AssociatesShare, :PublicShare," );
		insertSql.append(" :FinInstShare, :Others, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(corporateCustomerDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return corporateCustomerDetail.getId();
	}
	
	/**
	 * This method updates the Record CustomerCorporateDetail or CustomerCorporateDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Corporate Detail by key CustId and Version
	 * 
	 * @param Corporate Detail (corporateCustomerDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CorporateCustomerDetail corporateCustomerDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update CustomerCorporateDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set Name = :Name, PhoneNumber = :PhoneNumber,");
		updateSql.append(" EmailId = :EmailId, BussCommenceDate = :BussCommenceDate," );
		updateSql.append(" ServCommenceDate = :ServCommenceDate, BankRelationshipDate = :BankRelationshipDate,");
		updateSql.append(" PaidUpCapital = :PaidUpCapital, AuthorizedCapital = :AuthorizedCapital," );
		updateSql.append(" ReservesAndSurPlus = :ReservesAndSurPlus, IntangibleAssets = :IntangibleAssets,");
		updateSql.append(" TangibleNetWorth = :TangibleNetWorth, LongTermLiabilities = :LongTermLiabilities," );
		updateSql.append(" CapitalEmployed = :CapitalEmployed, Investments = :Investments," );
		updateSql.append(" NonCurrentAssets = :NonCurrentAssets, NetWorkingCapital = :NetWorkingCapital," );
		updateSql.append(" NetSales = :NetSales, OtherIncome = :OtherIncome, NetProfitAfterTax = :NetProfitAfterTax,");
		updateSql.append(" Depreciation = :Depreciation, CashAccurals = :CashAccurals," );
		updateSql.append(" AnnualTurnover = :AnnualTurnover, ReturnOnCapitalEmp = :ReturnOnCapitalEmp," );
		updateSql.append(" CurrentAssets = :CurrentAssets, CurrentLiabilities = :CurrentLiabilities," );
		updateSql.append(" CurrentBookValue = :CurrentBookValue, CurrentMarketValue = :CurrentMarketValue," );
		updateSql.append(" PromotersShare = :PromotersShare, AssociatesShare = :AssociatesShare," );
		updateSql.append(" PublicShare = :PublicShare, FinInstShare = :FinInstShare, Others = :Others,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId");
		updateSql.append(" Where CustId =:CustId");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(corporateCustomerDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}