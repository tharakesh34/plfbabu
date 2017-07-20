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
 * FileName    		:  FacilityDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-11-2013    														*
 *                                                                  						*
 * Modified Date    :  25-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-11-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.facility.impl;


import java.util.HashMap;
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

import com.pennant.backend.dao.facility.FacilityDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.facility.Facility;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;



/**
 * DAO methods implementation for the <b>Facility model</b> class.<br>
 * 
 */

public class FacilityDAOImpl extends BasisCodeDAO<Facility> implements FacilityDAO {

	private static Logger logger = Logger.getLogger(FacilityDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public FacilityDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new Facility 
	 * @return Facility
	 */

	@Override
	public Facility getFacility() {
		logger.debug("Entering");
		logger.debug("Leaving");
		return new Facility();
	}


	/**
	 * This method get the module from method getFacility() and set the new record flag as true and return Facility()   
	 * @return Facility
	 */


	@Override
	public Facility getNewFacility() {
		logger.debug("Entering");
		Facility facility = getFacility();
		facility.setNewRecord(true);
		logger.debug("Leaving");
		return facility;
	}

	/**
	 * Fetch the Record  Facility Queue details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Facility
	 */
	@Override
	public Facility getFacilityById(final String id, String type) {
		logger.debug("Entering");
		Facility facility = getFacility();
		facility.setId(id);
		StringBuilder selectSql = new StringBuilder("Select CAFReference,FacilityType, CustID, StartDate, PresentingUnit, CountryOfDomicile, DeadLine, CountryOfRisk, EstablishedDate, NatureOfBusiness, SICCode, CountryManager, CustomerRiskType, RelationshipManager, CustomerGroup, NextReviewDate");
		selectSql.append(" ,CountryExposure,CountryLimit,ReviewCenter,CountryLimitAdeq,LevelOfApproval,DedupFound, SkipDedup");
		selectSql.append(" ,CustomerBackGround,Strength,Weaknesses,SourceOfRepayment,AdequacyOfCashFlows,TypesOfSecurities,GuaranteeDescription,FinancialSummary,Mitigants");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(",Purpose,Interim,AccountRelation,AntiMoneyLaunderClear,LimitAndAncillary,AntiMoneyLaunderSection,OverriddeCirculation");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",custCIF,custShrtName,CustCtgCode,CustCoreBank,CustDOB ,CountryOfDomicileName");
			selectSql.append(",CountryOfRiskName,NatureOfBusinessName,customerRiskTypeName");
			selectSql.append(",sICCodeName,CustomerGroupName,CustGrpCodeName,CountryManagerName, CustRelation,CustTypeDesc");
		}
		selectSql.append(" From FacilityHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CAFReference =:CAFReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facility);
		RowMapper<Facility> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Facility.class);
		
		try{
			facility = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			facility = null;
		}
		logger.debug("Leaving");
		return facility;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FacilityHeader or FacilityHeader_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Facility Queue by key CAFReference
	 * 
	 * @param Facility Queue (facility)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Facility facility,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FacilityHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  CAFReference =:CAFReference");
	
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facility);
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
	 * This method insert new Records into FacilityHeader or FacilityHeader_Temp.
	 *
	 * save Facility Queue 
	 * 
	 * @param Facility Queue (facility)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(Facility facility,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FacilityHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CAFReference, FacilityType,CustID, StartDate, PresentingUnit, CountryOfDomicile, DeadLine, CountryOfRisk, EstablishedDate, NatureOfBusiness, SICCode, CountryManager, CustomerRiskType, RelationshipManager, CustomerGroup, NextReviewDate");
		insertSql.append(" ,CountryExposure,CountryLimit,ReviewCenter,CountryLimitAdeq,LevelOfApproval,OverriddeCirculation,DedupFound, SkipDedup");
		insertSql.append(" ,CustomerBackGround,Strength,Weaknesses,SourceOfRepayment,AdequacyOfCashFlows,TypesOfSecurities,GuaranteeDescription,FinancialSummary,Mitigants");
		insertSql.append(" ,Purpose,Interim,AccountRelation,AntiMoneyLaunderClear,LimitAndAncillary,AntiMoneyLaunderSection");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values( :CAFReference, :FacilityType,:CustID, :StartDate, :PresentingUnit, :CountryOfDomicile, :DeadLine, :CountryOfRisk, :EstablishedDate, :NatureOfBusiness, :SICCode, :CountryManager, :CustomerRiskType, :RelationshipManager, :CustomerGroup, :NextReviewDate");
		insertSql.append(" ,:CountryExposure,:CountryLimit,:ReviewCenter,:CountryLimitAdeq,:LevelOfApproval,:OverriddeCirculation,:DedupFound, :SkipDedup");
		insertSql.append(" ,:CustomerBackGround,:Strength,:Weaknesses,:SourceOfRepayment,:AdequacyOfCashFlows,:TypesOfSecurities,:GuaranteeDescription,:FinancialSummary,:Mitigants");
		insertSql.append(" ,:Purpose,:Interim,:AccountRelation,:AntiMoneyLaunderClear,:LimitAndAncillary,:AntiMoneyLaunderSection");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facility);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return facility.getId();
	}
	
	/**
	 * This method updates the Record FacilityHeader or FacilityHeader_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Facility Queue by key CAFReference and Version
	 * 
	 * @param Facility Queue (facility)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(Facility facility,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FacilityHeader");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FacilityType=:FacilityType,CustID = :CustID, StartDate = :StartDate, PresentingUnit = :PresentingUnit, CountryOfDomicile = :CountryOfDomicile, DeadLine = :DeadLine, CountryOfRisk = :CountryOfRisk, EstablishedDate = :EstablishedDate, NatureOfBusiness = :NatureOfBusiness, SICCode = :SICCode, CountryManager = :CountryManager, CustomerRiskType = :CustomerRiskType, RelationshipManager = :RelationshipManager, CustomerGroup = :CustomerGroup, NextReviewDate = :NextReviewDate");
		updateSql.append(", Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" , CountryExposure=:CountryExposure, CountryLimit=:CountryLimit, ReviewCenter=:ReviewCenter, CountryLimitAdeq=:CountryLimitAdeq, LevelOfApproval=:LevelOfApproval,OverriddeCirculation=:OverriddeCirculation");
		updateSql.append(" ,CustomerBackGround=:CustomerBackGround,Strength=:Strength,Weaknesses=:Weaknesses,SourceOfRepayment=:SourceOfRepayment,AdequacyOfCashFlows=:AdequacyOfCashFlows,TypesOfSecurities=:TypesOfSecurities,GuaranteeDescription=:GuaranteeDescription,FinancialSummary=:FinancialSummary,Mitigants=:Mitigants");
		updateSql.append(" ,Purpose=:Purpose, Interim=:Interim, AccountRelation=:AccountRelation, AntiMoneyLaunderClear=:AntiMoneyLaunderClear, LimitAndAncillary=:LimitAndAncillary, AntiMoneyLaunderSection=:AntiMoneyLaunderSection ");
		updateSql.append(" ,DedupFound=:DedupFound, SkipDedup=:SkipDedup");
		updateSql.append(" Where CAFReference =:CAFReference");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facility);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Check Have to Creating New Finance Accessibility for User or not
	 */
	@Override
    public boolean checkFirstTaskOwnerAccess(long usrLogin) {
		logger.debug("Entering");

		int facilityCount = 0;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COALESCE(FacilityCount,0) From FacilityCreationAccess_View");
		selectSql.append(" Where UsrID =:UsrID ");

		logger.debug("selectSql: " + selectSql.toString());
		Map<String, String> parameterMap=new HashMap<String, String>();
		parameterMap.put("UsrID",String.valueOf(usrLogin));
		try {
			facilityCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), parameterMap, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			facilityCount = 0;
		}
		 
		logger.debug("Leaving");
		return facilityCount > 0 ? true : false;
    }
	
	/**
	 * Fetch the Record  Facility Queue details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Facility
	 */
	@Override
	public Facility getLatestFacilityByCustID(final long custID, String type) {
		logger.debug("Entering");
		Facility facility = getFacility();
		facility.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT CAFReference FROM (SELECT CAFReference, " );
		selectSql.append(" row_number() over (order by LastMntOn DESC) row_num FROM FacilityHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE CustID =:CustID )T where row_num <= 1 ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facility);
		
		String prvCAFReference = null;
		try{
			prvCAFReference = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			prvCAFReference = null;
		}
		
		if(StringUtils.trimToNull(prvCAFReference) == null){
			logger.debug("Leaving");
			return null;
		}
		
		facility.setCAFReference(prvCAFReference);
		
		selectSql = new StringBuilder();
		selectSql.append(" Select CAFReference,FacilityType, CustID, StartDate, PresentingUnit, CountryOfDomicile, DeadLine, CountryOfRisk, ");
		selectSql.append(" EstablishedDate, NatureOfBusiness, SICCode, CountryManager, CustomerRiskType, RelationshipManager, CustomerGroup, NextReviewDate");
		selectSql.append(" ,CountryExposure,CountryLimit,ReviewCenter,CountryLimitAdeq,LevelOfApproval,DedupFound, SkipDedup");
		selectSql.append(" ,CustomerBackGround,Strength,Weaknesses,SourceOfRepayment,AdequacyOfCashFlows,TypesOfSecurities,GuaranteeDescription,FinancialSummary,Mitigants ");
		selectSql.append(" , Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" ,Purpose,Interim,AccountRelation,AntiMoneyLaunderClear,LimitAndAncillary,AntiMoneyLaunderSection,OverriddeCirculation ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,CustCIF,CustShrtName,CustCtgCode,CustCoreBank,CustDOB ,CountryOfDomicileName ");
			selectSql.append(" ,CountryOfRiskName,NatureOfBusinessName,customerRiskTypeName ");
			selectSql.append(" ,SICCodeName,CustomerGroupName,CustGrpCodeName,CountryManagerName, CustRelation,CustTypeDesc ");
		}
		selectSql.append(" From FacilityHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CAFReference =:CAFReference ");
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<Facility> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Facility.class);
		
		try{
			facility = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			facility = null;
		}
		logger.debug("Leaving");
		return facility;
	}
	
}