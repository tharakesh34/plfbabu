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
 * FileName    		:  CustomerPRelationDAOImpl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
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
package com.pennant.backend.dao.customermasters.impl;

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

import com.pennant.backend.dao.customermasters.CustomerPRelationDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.customermasters.CustomerPRelation;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustomerPRelation model</b> class.<br>
 * 
 */
public class CustomerPRelationDAOImpl extends BasisNextidDaoImpl<CustomerPRelation> implements CustomerPRelationDAO {

	private static Logger logger = Logger.getLogger(CustomerPRelationDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerPRelationDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  Customer P Relation details by key field
	 * 
	 * @param id (long)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerPRelation
	 */
	@Override
	public CustomerPRelation getCustomerPRelationByID(final long pRCustID,int pRCustPRSNo, String type) {
		logger.debug("Entering");
		CustomerPRelation customerPRelation = new CustomerPRelation();
		customerPRelation.setId(pRCustID);
		customerPRelation.setPRCustPRSNo(pRCustPRSNo);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select PRCustID, PRCustPRSNo, PRRelationCode," );
		selectSql.append(" PRRelationCustID, PRisGuardian, PRFName, PRMName, PRLName,");
		selectSql.append(" PRSName, PRFNameLclLng, PRMNameLclLng, PRLNameLclLng, PRDOB, PRAddrHNbr," );
		selectSql.append(" PRAddrFNbr, PRAddrStreet, PRAddrLine1," );
		selectSql.append(" PRAddrLine2, PRAddrPOBox, PRAddrCity, PRAddrProvince, PRAddrCountry," );
		selectSql.append(" PRAddrZIP, PRPhone, PRMail," );
		if(type.contains("View")){
			selectSql.append(" lovDescPRRelationCodeName, lovDescPRAddrCityName, lovDescPRAddrProvinceName," );
			selectSql.append(" lovDescPRAddrCountryName, lovDescCustCIF, lovDescCustShrtName, lovDescCustRecordType, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  CustomersPRelations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PRCustID =:PRCustID AND PRCustPRSNo=:PRCustPRSNo ") ;
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPRelation);
		RowMapper<CustomerPRelation> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerPRelation.class);

		try{
			customerPRelation = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			customerPRelation = null;
		}

		logger.debug("Leaving");
		return customerPRelation;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the CustomersPRelations or
	 * CustomersPRelations_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Customer P Relation by key
	 * PRCustID
	 * 
	 * @param Customer
	 *            P Relation (customerPRelation)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerPRelation customerPRelation,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomersPRelations");
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where PRCustID =:PRCustID AND PRCustPRSNo=:PRCustPRSNo");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPRelation);
		
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
	 * Method for Deletion of Customer Related List of CustomerRatings
	 */
	@Override
	public void deleteByCustomer(final long id,String type) {
		logger.debug("Entering delete Method");
		int recordCount = 0;
		CustomerPRelation customerPRelation = new CustomerPRelation();
		customerPRelation.setId(id);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomersPRelations" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where PRCustID =:PRCustID ");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPRelation);

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
	 * This method insert new Records into CustomersPRelations or
	 * CustomersPRelations_Temp. it fetches the available Sequence form
	 * SeqCustomersPRelations by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Customer P Relation
	 * 
	 * @param Customer
	 *            P Relation (customerPRelation)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerPRelation customerPRelation,String type) {
		logger.debug("Entering");
		
		if(StringUtils.isNotEmpty(type) && customerPRelation.getPRCustPRSNo()==0){
			int sNo= getMaxSeqNo(customerPRelation.getPRCustID());
			customerPRelation.setPRCustPRSNo(sNo+1);
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomersPRelations" ); 
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (PRCustID, PRCustPRSNo, PRRelationCode, PRRelationCustID, PRisGuardian," );
		insertSql.append(" PRFName, PRMName, PRLName, PRSName, PRFNameLclLng, PRMNameLclLng, PRLNameLclLng," );
		insertSql.append(" PRDOB, PRAddrHNbr, PRAddrFNbr, PRAddrStreet, PRAddrLine1, PRAddrLine2, PRAddrPOBox,");
		insertSql.append(" PRAddrCity, PRAddrProvince, PRAddrCountry, PRAddrZIP, PRPhone, PRMail," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId," );
		insertSql.append(" NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:PRCustID, :PRCustPRSNo, :PRRelationCode, :PRRelationCustID, :PRisGuardian,");
		insertSql.append(" :PRFName, :PRMName, :PRLName, :PRSName, :PRFNameLclLng, :PRMNameLclLng,");
		insertSql.append(" :PRLNameLclLng, :PRDOB, :PRAddrHNbr, :PRAddrFNbr, :PRAddrStreet, :PRAddrLine1," );
		insertSql.append(" :PRAddrLine2, :PRAddrPOBox, :PRAddrCity, :PRAddrProvince,:PRAddrCountry," );
		insertSql.append(" :PRAddrZIP, :PRPhone, :PRMail," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus," );
		insertSql.append(" :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId )");
		
		 logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPRelation);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerPRelation.getPRCustPRSNo();
	}

	/**
	 * This method updates the Record CustomersPRelations or CustomersPRelations_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Customer P Relation by key PRCustID and Version
	 * 
	 * @param Customer P Relation (customerPRelation)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerPRelation customerPRelation,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomersPRelations" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set PRRelationCode = :PRRelationCode, PRRelationCustID = :PRRelationCustID," );
		updateSql.append(" PRisGuardian = :PRisGuardian, PRFName = :PRFName, PRMName = :PRMName," );
		updateSql.append(" PRLName = :PRLName, PRSName = :PRSName, PRFNameLclLng = :PRFNameLclLng," );
		updateSql.append(" PRMNameLclLng = :PRMNameLclLng, PRLNameLclLng = :PRLNameLclLng, PRDOB = :PRDOB," );
		updateSql.append(" PRAddrHNbr = :PRAddrHNbr, PRAddrFNbr = :PRAddrFNbr, PRAddrStreet = :PRAddrStreet," );
		updateSql.append(" PRAddrLine1 = :PRAddrLine1, PRAddrLine2 = :PRAddrLine2, PRAddrPOBox = :PRAddrPOBox," );
		updateSql.append(" PRAddrCity = :PRAddrCity, PRAddrProvince = :PRAddrProvince," );
		updateSql.append(" PRAddrCountry = :PRAddrCountry, PRAddrZIP = :PRAddrZIP, PRPhone = :PRPhone," );
		updateSql.append(" PRMail = :PRMail, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where PRCustID =:PRCustID AND PRCustPRSNo=:PRCustPRSNo");

		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPRelation);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method For Generating PRelation SeqNo dynamically by getting max number
	 * @param pRCustID
	 * @return
	 */
	public int getMaxSeqNo(long pRCustID) {
		logger.debug("Entering");
		int count =0; 

		try {
			StringBuilder selectSql = new StringBuilder("select max(PRCustPRSNo)" );
			selectSql.append(" FROM CustomersPRelations_VIEW WHERE PRCustID =");
			selectSql.append(pRCustID);
			
			logger.debug("selectSql: " + selectSql.toString());
			count = this.namedParameterJdbcTemplate.getJdbcOperations().queryForObject(selectSql.toString(), Integer.class);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return count;
	}

	/** 
	 * Method For getting List of CustomerPRelation objects for Customer
	 */
	@Override
	public List<CustomerPRelation> getCustomerPRelationByCustomer(long id,String type) {
		logger.debug("Entering");
		
		CustomerPRelation customerPRelation = new CustomerPRelation();
		customerPRelation.setId(id);

		StringBuilder selectSql = new StringBuilder("Select PRCustID, PRCustPRSNo, PRRelationCode," );
		selectSql.append(" PRRelationCustID, PRisGuardian, PRFName, PRMName, PRLName, PRSName, PRFNameLclLng,");
		selectSql.append(" PRMNameLclLng, PRLNameLclLng, PRDOB, PRAddrHNbr, PRAddrFNbr, PRAddrStreet," );
		selectSql.append(" PRAddrLine1, PRAddrLine2, PRAddrPOBox, PRAddrCity, PRAddrProvince," );
		selectSql.append(" PRAddrCountry, PRAddrZIP, PRPhone, PRMail," );
		if(type.contains("View")){
			selectSql.append(" lovDescPRRelationCodeName, lovDescPRAddrCityName, lovDescPRAddrProvinceName," );
			selectSql.append(" lovDescPRAddrCountryName, lovDescCustCIF, lovDescCustShrtName," );
			selectSql.append(" lovDescCustRecordType, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  CustomersPRelations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PRCustID =:PRCustID ") ;
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPRelation);
		RowMapper<CustomerPRelation> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerPRelation.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	
}