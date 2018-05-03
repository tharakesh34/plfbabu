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
 * FileName    		:  MMAgreementDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster.impl;

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

import com.pennant.backend.dao.applicationmaster.MMAgreementDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.MMAgreement.MMAgreement;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>MMAgreement model</b> class.<br>
 */
public class MMAgreementDAOImpl extends BasisNextidDaoImpl<MMAgreement> implements MMAgreementDAO {
	private static Logger logger = Logger.getLogger(MMAgreementDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public MMAgreementDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new MMAgreement
	 * 
	 * @return MMAgreement
	 */
	@Override
	public MMAgreement getMMAgreement() {
		logger.debug("Entering ");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("MMAgreement");
		MMAgreement aMMAgreement= new MMAgreement();
		aMMAgreement.setNewRecord(true);
		if (workFlowDetails!=null){
			aMMAgreement.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving ");
		return aMMAgreement;
	}

	/**
	 * This method get the module from method getMMAgreement() and set the new record
	 * flag as true and return MMAgreement()
	 * 
	 * @return MMAgreement
	 */
	@Override
	public MMAgreement getNewMMAgreement() {
		logger.debug("Entering ");
		MMAgreement aMMAgreement = getMMAgreement();
		aMMAgreement.setNewRecord(true);
		logger.debug("Leaving ");
		return aMMAgreement;
	}

	/**
	 * Fetch the Record  MMAgreement details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return MMAgreement
	 */
	@Override
	public MMAgreement getMMAgreementById(final long id, String type) {
		logger.debug("Entering");
		MMAgreement aMMAgreement = new MMAgreement();
		aMMAgreement.setId(id);
		StringBuilder selectSql = new StringBuilder("SELECT MMAId,MMAReference,CustCIF ,PurchRegOffice ,");
		selectSql.append(" ContractAmt ,ContractDate,Rate ,Fax,Purchaddress,Attention,TitleNo,MMAgreeType,Product,AgreeName," );
		selectSql.append(" FOLIssueDate,MaturityDate,FacilityLimit,ProfitRate,BaseRateCode,minRate,Margin,ProfitPeriod," );
		selectSql.append(" MinAmount,NumberOfTerms,LatePayRate,FOlReference,AvlPerDays,MaxCapProfitRate,PmaryRelOfficer," );
		selectSql.append(" FacOfferLetterDate,MinCapRate,CustAccount," );
		selectSql.append(" AssetValue,Dealer,AssetDesc,SharePerc," );
		if(type.contains("_View")){
			selectSql.append(" CustShrtName,lovDescPurchRegOffice,lovDescBaseRateName,DealerName," );
			selectSql.append(" DealerPOBox,DealerCountry,CustAddrCountry,CustPOBox,CustAddrCity," );
			selectSql.append(" CustAddrStreet,CustAddrLine1,CustAddrLine2," );
			selectSql.append(" lovDescCustAddrCityName,lovDescCustAddrProvinceName,lovDescCustAddrCountryName,CustShrtNameLclLng," );
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode,  NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From MMAgreements");
		selectSql.append(StringUtils.trimToEmpty(type)); 
		selectSql.append(" Where MMAId = :MMAId" );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aMMAgreement);
		RowMapper<MMAgreement> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(MMAgreement.class);
		
		try{
			aMMAgreement = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			aMMAgreement = null;
		}
		logger.debug("Leaving ");
		return aMMAgreement;
	}
	
	/**
	 * Fetch the Record  MMAgreement details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return MMAgreement
	 */
	@Override
	public MMAgreement getMMAgreementByMMARef(String mMAReference, String type) {
		logger.debug("Entering");
		MMAgreement aMMAgreement = new MMAgreement();
		aMMAgreement.setMMAReference(mMAReference);
				StringBuilder selectSql = new StringBuilder("SELECT MMAId,MMAReference,CustCIF ,PurchRegOffice ,");
		selectSql.append(" ContractAmt ,ContractDate,Rate ,Fax,Purchaddress,Attention,TitleNo,MMAgreeType,Product,AgreeName," );
		selectSql.append(" FOLIssueDate,MaturityDate,FacilityLimit,ProfitRate,BaseRateCode,minRate,Margin,ProfitPeriod," );
		selectSql.append(" MinAmount,NumberOfTerms,LatePayRate,FOlReference,AvlPerDays,MaxCapProfitRate,PmaryRelOfficer," );
		selectSql.append(" FacOfferLetterDate,MinCapRate,CustAccount," );
		selectSql.append(" AssetValue,Dealer,AssetDesc,SharePerc," );
		if(type.contains("_View")){
			selectSql.append(" CustShrtName,lovDescPurchRegOffice,lovDescBaseRateName,DealerName," );
			selectSql.append(" DealerPOBox,DealerCountry,CustAddrCountry,CustPOBox,CustAddrCity," );
			selectSql.append(" CustAddrStreet,CustAddrLine1,CustAddrLine2," );
			selectSql.append(" lovDescCustAddrCityName,lovDescCustAddrProvinceName,lovDescCustAddrCountryName," );
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode,  NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From MMAgreements");
		selectSql.append(StringUtils.trimToEmpty(type)); 
		selectSql.append(" Where  MMAReference = :MMAReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aMMAgreement);
		RowMapper<MMAgreement> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(MMAgreement.class);
		
		try{
			aMMAgreement = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			aMMAgreement = null;
		}
		logger.debug("Leaving ");
		return aMMAgreement;
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the MMAgreement or
	 * MMAgreement_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete MMAgreement by key MMAgreement Id
	 * 
	 * @param MMAgreement
	 *            (aMMAgreement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(MMAgreement aMMAgreement,String type) {
		logger.debug("Entering ");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From MMAgreements" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where MMAId =:MMAId");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aMMAgreement);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(
					deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	

	/**
	 * This method insert new Records into MMAgreement or
	 * MMAgreement_Temp.
	 * 
	 * save MMAgreement
	 * 
	 * @param MMAgreement
	 *            (aMMAgreement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(MMAgreement aMMAgreement,String type) {
		logger.debug("Entering ");
		if (aMMAgreement.getId()==Long.MIN_VALUE){
			aMMAgreement.setId(getNextidviewDAO().getNextId("SeqMMAgreements"));
			logger.debug("get NextID:"+aMMAgreement.getId());
		}
		StringBuilder insertSql = new StringBuilder("Insert Into MMAgreements" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (MMAId,MMAReference,CustCIF ,PurchRegOffice ,ContractAmt ," );
		insertSql.append(" ContractDate ,Rate ,Fax,Purchaddress,Attention,TitleNo," );
		insertSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId,MMAgreeType,Product,AgreeName," );
		insertSql.append(" FOLIssueDate,MaturityDate,FacilityLimit,ProfitRate,BaseRateCode,minRate,Margin,ProfitPeriod," );
		insertSql.append(" MinAmount,NumberOfTerms,LatePayRate," );
		insertSql.append(" FOlReference,AvlPerDays,MaxCapProfitRate,PmaryRelOfficer," );
		insertSql.append(" FacOfferLetterDate,MinCapRate,CustAccount," );
		insertSql.append(" Dealer,AssetDesc,SharePerc,AssetValue)" );
		insertSql.append(" Values(:MMAId, :MMAReference, :CustCIF, :PurchRegOffice,:ContractAmt ,:ContractDate, " );
		insertSql.append(" :Rate , :Fax, :Purchaddress, :Attention,:TitleNo," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :MMAgreeType,:Product,:AgreeName,");
		insertSql.append(" :FOLIssueDate, :MaturityDate, :FacilityLimit, :ProfitRate, :BaseRateCode, :minRate, :Margin, :ProfitPeriod,");
		insertSql.append(" :MinAmount, :NumberOfTerms, :LatePayRate ,");
		insertSql.append(" :FOlReference, :AvlPerDays,:MaxCapProfitRate,:PmaryRelOfficer," );
		insertSql.append(" :FacOfferLetterDate,:MinCapRate,:CustAccount," );
		insertSql.append(" :Dealer,:AssetDesc,:SharePerc,:AssetValue)" );
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aMMAgreement);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return aMMAgreement.getId();
		
	}
	
	/**
	 * This method updates the Record MMAgreement or MMAgreement_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update MMAgreement by key PCCountry and Version
	 * 
	 * @param Ciry (aMMAgreement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(MMAgreement aMMAgreement,String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		StringBuilder updateSql = new StringBuilder("Update MMAgreements" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set MMAReference = :MMAReference, CustCIF = :CustCIF,");
		updateSql.append(" ContractAmt = :ContractAmt ,ContractDate = :ContractDate,PurchRegOffice = :PurchRegOffice,Fax = :Fax," );
		updateSql.append(" Rate = :Rate , Purchaddress = :Purchaddress, Attention = :Attention,TitleNo = :TitleNo," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, MMAgreeType = :MMAgreeType, Product = :Product, AgreeName= :AgreeName," );
		updateSql.append(" FOLIssueDate =  :FOLIssueDate, MaturityDate =  :MaturityDate,FacilityLimit  =  :FacilityLimit, ProfitRate =  :ProfitRate," );
		updateSql.append(" BaseRateCode =  :BaseRateCode, minRate =  :minRate,Margin  =  :Margin,ProfitPeriod =  :ProfitPeriod," );
		updateSql.append(" MinAmount = :MinAmount,NumberOfTerms  =  :NumberOfTerms, LatePayRate =  :LatePayRate, " );
		updateSql.append(" FOlReference = :FOlReference,AvlPerDays= :AvlPerDays,MaxCapProfitRate= :MaxCapProfitRate,PmaryRelOfficer= :PmaryRelOfficer," );
		updateSql.append(" FacOfferLetterDate= :FacOfferLetterDate,MinCapRate= :MinCapRate,CustAccount= :CustAccount," );
		updateSql.append(" Dealer = :Dealer,AssetDesc = :AssetDesc,SharePerc = :SharePerc,AssetValue = :AssetValue" );
		updateSql.append(" Where MMAId =:MMAId ");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aMMAgreement);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}