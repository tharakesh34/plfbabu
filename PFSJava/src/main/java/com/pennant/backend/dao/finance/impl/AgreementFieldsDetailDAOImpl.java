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
 * FileName    		:  AgreementFieldsDetailDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.finance.AgreementFieldsDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.AgreementFieldDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>AgreementFieldDetails model</b> class.<br>
 */
public class AgreementFieldsDetailDAOImpl extends BasisCodeDAO<AgreementFieldDetails> implements AgreementFieldsDetailDAO {

	private static Logger logger = Logger.getLogger(AgreementFieldsDetailDAOImpl.class);
	
	public AgreementFieldsDetailDAOImpl() {
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
	 * @return AgreementfieldDetails
	 */
	@Override
	public AgreementFieldDetails getAgreementFieldsDetailByID(String finReference, String type) {
		logger.debug("Entering");
		AgreementFieldDetails agreementFieldDetails = new AgreementFieldDetails();
		agreementFieldDetails.setId(finReference);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select FinReference, CustCity, CustPoBox,CustCntAmt,SellerName, CustNationality, PlotOrUnitNo, OtherbankName,");
		selectSql.append("PropertyType, sectorOrCommunity, FinAmount, propertyLocation,proprtyDesc,JointApplicant,SellerNationality,SellerPobox,PropertyUse,Plotareainsqft,BuiltupAreainSqft,");
		selectSql.append(" AhbBranch,Fininstitution,FacilityName,SellerCntbAmt,OtherBankAmt,PropertyOwner,CollateralAuthority,Collateral1,SellerInternal,Area,");
		
		if(type.contains("View")){
			selectSql.append(" ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From AgreementFieldDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementFieldDetails);
		RowMapper<AgreementFieldDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AgreementFieldDetails.class);
		
		try{
			agreementFieldDetails = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			agreementFieldDetails = null;
		}
		logger.debug("Leaving");
		return agreementFieldDetails;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the AgreementfieldDetails or
	 * AgreementfieldDetails_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Equipment Loan Details by key
	 * EquipmentLoanId
	 * 
	 * @param Equipment
	 *            Loan Details (AgreementfieldDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(AgreementFieldDetails agreementFieldDetails,String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From AgreementFieldDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementFieldDetails);
		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into AgreementfieldDetails or
	 * AgreementfieldDetails_Temp. it fetches the available Sequence form
	 * SeqAgreementfieldDetails by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Equipment Loan Details
	 * 
	 * @param Equipment
	 *            Loan Details (AgreementfieldDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public String save(AgreementFieldDetails agreementFieldDetails,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder();
		insertSql.append(" Insert Into AgreementFieldDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, CustCity,CustPoBox,CustCntAmt,SellerName, CustNationality,PlotOrUnitNo,OtherbankName,PropertyType,sectorOrCommunity,FinAmount," );
		insertSql.append(" ProprtyDesc, PropertyLocation,JointApplicant,SellerNationality,SellerPobox,PropertyUse,Plotareainsqft,BuiltupAreainSqft,");
		insertSql.append(" AhbBranch,Fininstitution,FacilityName,SellerCntbAmt,OtherBankAmt,PropertyOwner,CollateralAuthority,Collateral1,SellerInternal,Area,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		
		insertSql.append(" Values( :FinReference,:CustCity,:CustPoBox,:CustCntAmt, :SellerName, :CustNationality, :PlotOrUnitNo, :OtherbankName, :PropertyType, :sectorOrCommunity, :FinAmount, ");
		insertSql.append(" :ProprtyDesc, :PropertyLocation,:JointApplicant,:SellerNationality,:SellerPobox,:PropertyUse,:Plotareainsqft,:BuiltupAreainSqft,");
		insertSql.append(" :AhbBranch,:Fininstitution,:FacilityName,:SellerCntbAmt,:OtherBankAmt,:PropertyOwner,:CollateralAuthority,:Collateral1,:SellerInternal,:Area,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementFieldDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return agreementFieldDetails.getId();
	}
	
	/**
	 * This method updates the Record AgreementfieldDetails or
	 * AgreementfieldDetails_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Equipment Loan Details by key
	 * EquipmentLoanId and Version
	 * 
	 * @param Equipment
	 *            Loan Details (AgreementfieldDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(AgreementFieldDetails agreementFieldDetails,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder();
		updateSql.append(" Update AgreementFieldDetails");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set CustCity = :CustCity,SellerName = :SellerName, CustPoBox = :CustPoBox, CustCntAmt = :CustCntAmt,  CustNationality = :CustNationality, PlotOrUnitNo = :PlotOrUnitNo," );
		updateSql.append(" OtherbankName = :OtherbankName, PropertyType = :PropertyType, SectorOrCommunity = :SectorOrCommunity, FinAmount = :FinAmount, ProprtyDesc = :ProprtyDesc, " );
		updateSql.append(" PropertyLocation = :PropertyLocation,JointApplicant = :JointApplicant,SellerNationality = :SellerNationality,Area = :Area,");
		updateSql.append(" SellerPobox = :SellerPobox,PropertyUse = :PropertyUse,Plotareainsqft = :Plotareainsqft,BuiltupAreainSqft = :BuiltupAreainSqft,");
		updateSql.append(" AhbBranch = :AhbBranch,Fininstitution = :Fininstitution,FacilityName = :FacilityName,SellerCntbAmt = :SellerCntbAmt,");
		updateSql.append(" OtherBankAmt = :OtherBankAmt,PropertyOwner = :PropertyOwner,CollateralAuthority = :CollateralAuthority,Collateral1 = :Collateral1,SellerInternal = :SellerInternal,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementFieldDetails);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}