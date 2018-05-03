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
 * FileName    		:  FinContributorDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-01-2013    														*
 *                                                                  						*
 * Modified Date    :  30-01-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinContributorDetail model</b> class.<br>
 * 
 */
public class FinContributorDetailDAOImpl extends BasisNextidDaoImpl<FinContributorDetail> implements FinContributorDetailDAO {

	private static Logger logger = Logger.getLogger(FinContributorDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FinContributorDetailDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record  Finance Contributor Details by key field
	 */
	@Override
	public FinContributorDetail getFinContributorDetailByID(final String finReference, long id,String type) {
		logger.debug("Entering");
		
		FinContributorDetail contributorDetail = new FinContributorDetail();
		contributorDetail.setFinReference(finReference);
		contributorDetail.setContributorBaseNo(id);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference , ContributorBaseNo , CustID , " );
		selectSql.append(" ContributorName , ContributorInvest , InvestAccount , " );
		selectSql.append(" InvestDate ,RecordDate, TotalInvestPerc , MudaribPerc, " );
		
		if(type.contains("View")){
			selectSql.append(" LovDescContributorCIF, LovDescFinFormatter, ");
		}
		
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  FinContributorDetail") ;
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference AND ContributorBaseNo = :ContributorBaseNo") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contributorDetail);
		RowMapper<FinContributorDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FinContributorDetail.class);

		try{
			contributorDetail = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			contributorDetail = null;
		}
		logger.debug("Leaving");
		return contributorDetail;
	}

	/** 
	 * Method For getting List of Finance Contributor Details
	 */
	public List<FinContributorDetail> getFinContributorDetailByFinRef(final String finReference,String type) {
		logger.debug("Entering");
		FinContributorDetail contributorDetail = new FinContributorDetail();
		contributorDetail.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference , ContributorBaseNo , CustID , " );
		selectSql.append(" ContributorName , ContributorInvest , InvestAccount , " );
		selectSql.append(" InvestDate , RecordDate, TotalInvestPerc , MudaribPerc , " );
		if(type.contains("View")){
			selectSql.append(" LovDescContributorCIF, ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  FinContributorDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference ") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contributorDetail);
		RowMapper<FinContributorDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinContributorDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the FinContributorDetails or FinContributorDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Contributor Details List by FinReference
	 * 
	 * @param Contributor Details (contributorDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinContributorDetail contributorDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinContributorDetail" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where FinReference =:FinReference AND ContributorBaseNo =:ContributorBaseNo");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contributorDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Deletion of Customer Related List of FinContributorDetails for the Customer
	 */
	public void deleteByFinRef(final String finReference,String type) {
		logger.debug("Entering");
		FinContributorDetail contributorDetail = new FinContributorDetail();
		contributorDetail.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinContributorDetail" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where FinReference =:FinReference ");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contributorDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinContributorDetails or FinContributorDetails_Temp.
	 *
	 * save Customer Ratings 
	 * 
	 * @param Customer Ratings (contributorDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(FinContributorDetail contributorDetail,String type) {
		logger.debug("Entering");
		
		if(contributorDetail.getContributorBaseNo() == 0 || contributorDetail.getContributorBaseNo()==Long.MIN_VALUE){
			contributorDetail.setContributorBaseNo(getNextidviewDAO().getNextId("SeqFinContributorDetail"));	
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into FinContributorDetail" );
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference , ContributorBaseNo , CustID , ContributorName , " );
		insertSql.append(" ContributorInvest , InvestAccount , InvestDate ,RecordDate, TotalInvestPerc , MudaribPerc, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" VALUES (:FinReference , :ContributorBaseNo , :CustID , :ContributorName , " );
		insertSql.append(" :ContributorInvest , :InvestAccount , :InvestDate ,:RecordDate, :TotalInvestPerc , :MudaribPerc, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contributorDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return contributorDetail.getContributorBaseNo();
	}

	/**
	 * This method updates the Record FinContributorDetails or FinContributorDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Contributor Details by key FinReference and Version
	 * 
	 * @param Customer Ratings (contributorDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinContributorDetail contributorDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update FinContributorDetail" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set CustID =:CustID , ContributorName =:ContributorName , ContributorInvest =:ContributorInvest , " );
		updateSql.append(" InvestAccount =:InvestAccount , InvestDate =:InvestDate ,RecordDate=:RecordDate, " );
		updateSql.append(" TotalInvestPerc =:TotalInvestPerc , MudaribPerc =:MudaribPerc , " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId" );
		updateSql.append(" Where FinReference =:FinReference and ContributorBaseNo = :ContributorBaseNo ");

		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1 ");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contributorDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}