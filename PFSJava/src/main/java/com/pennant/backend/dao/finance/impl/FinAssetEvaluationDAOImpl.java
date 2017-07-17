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
 * FileName    		:  FinAssetEvaluationDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.finance.FinAssetEvaluationDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.FinAssetEvaluation;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinAssetEvaluation model</b> class.<br>
 */
public class FinAssetEvaluationDAOImpl extends BasisCodeDAO<FinAssetEvaluation> implements FinAssetEvaluationDAO {

	private static Logger logger = Logger.getLogger(FinAssetEvaluationDAOImpl.class);
	
	public FinAssetEvaluationDAOImpl() {
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
	 * @return FinAssetEvaluation
	 */
	@Override
	public FinAssetEvaluation getFinAssetEvaluationByID(String finReference, String type) {
		logger.debug("Entering");
		FinAssetEvaluation finAssetEvaluation = new FinAssetEvaluation();
		finAssetEvaluation.setId(finReference);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select FinReference, TypeofValuation, CustAwareVisit, CustRepreName, Leased, TotalRevenue, TenantContactNum,");
		selectSql.append(" TenantAwareVisit, Remarks, PanelFirm, ReuReference, PropertyDesc, VendorInstructedDate, ReportDeliveredDate,");
		selectSql.append(" InspectionDate, FinalReportDate, MarketValueAED, ValuationDate, Status, VendorValuer, ValuerFee, CustomerFee, ");
		selectSql.append(" ValuationComments, ExpRentalIncome, PropIsRented, PropertyStatus, PercWorkCompletion, IllegalDivAlteration,");
		selectSql.append(" NocReqDevMunicipality, ReuDecision, UnitVillaSize,");
		if(type.contains("View")){
			selectSql.append(" VendorValuerDesc, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinAssetEvaluation");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetEvaluation);
		RowMapper<FinAssetEvaluation> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinAssetEvaluation.class);
		
		try{
			finAssetEvaluation = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finAssetEvaluation = null;
		}
		logger.debug("Leaving");
		return finAssetEvaluation;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinAssetEvaluation or
	 * FinAssetEvaluation_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Equipment Loan Details by key
	 * EquipmentLoanId
	 * 
	 * @param Equipment
	 *            Loan Details (finAssetEvaluation)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinAssetEvaluation finAssetEvaluation,String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From FinAssetEvaluation");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetEvaluation);
		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);

		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinAssetEvaluation or
	 * FinAssetEvaluation_Temp. it fetches the available Sequence form
	 * SeqFinAssetEvaluation by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Equipment Loan Details
	 * 
	 * @param Equipment
	 *            Loan Details (finAssetEvaluation)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public String save(FinAssetEvaluation finAssetEvaluation,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder();
		insertSql.append(" Insert Into FinAssetEvaluation");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" ( FinReference, TypeofValuation, CustAwareVisit, CustRepreName, Leased, TotalRevenue, TenantContactNum, " );
		insertSql.append(" TenantAwareVisit, Remarks, PanelFirm, ReuReference, PropertyDesc, VendorInstructedDate, ReportDeliveredDate, ");
		insertSql.append(" InspectionDate, FinalReportDate, MarketValueAED, ValuationDate, Status, VendorValuer, ValuerFee, CustomerFee,");
		insertSql.append(" ValuationComments, ExpRentalIncome, PropIsRented, PropertyStatus, PercWorkCompletion, IllegalDivAlteration, NocReqDevMunicipality, ReuDecision, UnitVillaSize,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values( :FinReference, :TypeofValuation, :CustAwareVisit, :CustRepreName, :Leased, :TotalRevenue, :TenantContactNum, ");
		insertSql.append(" :TenantAwareVisit, :Remarks, :PanelFirm, :ReuReference, :PropertyDesc, :VendorInstructedDate, :ReportDeliveredDate, ");
		insertSql.append(" :InspectionDate, :FinalReportDate, :MarketValueAED, :ValuationDate, :Status, :VendorValuer, :ValuerFee, :CustomerFee,");
		insertSql.append(" :ValuationComments, :ExpRentalIncome, :PropIsRented, :PropertyStatus, :PercWorkCompletion, :IllegalDivAlteration, :NocReqDevMunicipality, :ReuDecision, :UnitVillaSize,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetEvaluation);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return finAssetEvaluation.getId();
	}
	
	/**
	 * This method updates the Record FinAssetEvaluation or
	 * FinAssetEvaluation_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Equipment Loan Details by key
	 * EquipmentLoanId and Version
	 * 
	 * @param Equipment
	 *            Loan Details (finAssetEvaluation)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinAssetEvaluation finAssetEvaluation,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder();
		updateSql.append(" Update FinAssetEvaluation");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set TypeofValuation = :TypeofValuation, CustAwareVisit = :CustAwareVisit, CustRepreName = :CustRepreName," );
		updateSql.append(" Leased = :Leased, TotalRevenue = :TotalRevenue, TenantContactNum = :TenantContactNum, TenantAwareVisit = :TenantAwareVisit, Remarks = :Remarks, " );
		updateSql.append(" PanelFirm = :PanelFirm, ReuReference = :ReuReference, PropertyDesc = :PropertyDesc, VendorInstructedDate = :VendorInstructedDate, " );
		updateSql.append(" ReportDeliveredDate = :ReportDeliveredDate,  InspectionDate = :InspectionDate, FinalReportDate = :FinalReportDate, MarketValueAED = :MarketValueAED, " );
		updateSql.append(" ValuationDate = :ValuationDate, Status = :Status,  VendorValuer = :VendorValuer, ValuerFee = :ValuerFee, CustomerFee = :CustomerFee, ValuationComments = :ValuationComments," );
		updateSql.append(" ExpRentalIncome = :ExpRentalIncome,  PropIsRented = :PropIsRented, PropertyStatus = :PropertyStatus, PercWorkCompletion = :PercWorkCompletion, " );
		updateSql.append(" IllegalDivAlteration = :IllegalDivAlteration,  NocReqDevMunicipality = :NocReqDevMunicipality, ReuDecision = :ReuDecision, UnitVillaSize = :UnitVillaSize," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetEvaluation);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
}