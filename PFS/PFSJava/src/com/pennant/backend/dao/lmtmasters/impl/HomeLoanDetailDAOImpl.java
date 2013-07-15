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
 * FileName    		:  HomeLoanDetailDAOImpl.java                                                   * 	  
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

package com.pennant.backend.dao.lmtmasters.impl;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.lmtmasters.HomeLoanDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>HomeLoanDetail model</b> class.<br>
 */
public class HomeLoanDetailDAOImpl extends BasisCodeDAO<HomeLoanDetail> implements HomeLoanDetailDAO {

	private static Logger logger = Logger.getLogger(HomeLoanDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new HomeLoanDetail
	 * 
	 * @return HomeLoanDetail
	 */
	@Override
	public HomeLoanDetail getHomeLoanDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("HomeLoanDetail");
		HomeLoanDetail homeLoanDetail= new HomeLoanDetail();
		if (workFlowDetails!=null){
			homeLoanDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return homeLoanDetail;
	}

	/**
	 * This method get the module from method getHomeLoanDetail() and set the
	 * new record flag as true and return HomeLoanDetail()
	 * 
	 * @return HomeLoanDetail
	 */
	@Override
	public HomeLoanDetail getNewHomeLoanDetail() {
		logger.debug("Entering");
		HomeLoanDetail homeLoanDetail = getHomeLoanDetail();
		homeLoanDetail.setNewRecord(true);
		logger.debug("Leaving");
		return homeLoanDetail;
	}
	
	/**
	 * Fetch the Record Home Loan Details details by key field
	 * 
	 * @param id (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return HomeLoanDetail
	 */
	@Override
	public HomeLoanDetail getHomeLoanDetailByID(String loanRefNumber, String type) {
		logger.debug("Entering");
		HomeLoanDetail homeLoanDetail = getHomeLoanDetail();
		homeLoanDetail.setId(loanRefNumber);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select LoanRefNumber, LoanRefType, HomeDetails,");
		selectSql.append(" HomeBuilderName, HomeCostPerFlat, HomeCostOfLand,HomeCostOfConstruction,");
		selectSql.append(" HomeConstructionStage, HomeDateOfPocession, HomeAreaOfLand, HomeAreaOfFlat," );
		selectSql.append(" HomePropertyType, HomeOwnerShipType, HomeAddrFlatNbr, HomeAddrStreet,");
		selectSql.append(" HomeAddrLane1, HomeAddrLane2, HomeAddrPOBox,HomeAddrCountry, HomeAddrProvince,");
		selectSql.append(" HomeAddrCity, HomeAddrZIP,HomeAddrPhone,");
		if(type.contains("View")){
			selectSql.append(" LovDescHomeDetailsName,LovDescHomePropertyTypeName, LovDescHomeAddrCityName,");
			selectSql.append(" LovDescHomeOwnerShipTypeName,LovDescHomeAddrCountryName,LovDescHomeAddrProvinceName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From LMTHomeLoanDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LoanRefNumber = :LoanRefNumber ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(homeLoanDetail);
		RowMapper<HomeLoanDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(HomeLoanDetail.class);
		
		try{
			homeLoanDetail = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			homeLoanDetail = null;
		}
		logger.debug("Leaving");
		return homeLoanDetail;
	}
	
	/**
	 * This method initialize the Record.
	 * @param HomeLoanDetail (homeLoanDetail)
 	 * @return HomeLoanDetail
	 */
	@Override
	public void initialize(HomeLoanDetail homeLoanDetail) {
		super.initialize(homeLoanDetail);
	}
	
	/**
	 * This method refresh the Record.
	 * @param HomeLoanDetail (homeLoanDetail)
 	 * @return void
	 */
	@Override
	public void refresh(HomeLoanDetail homeLoanDetail) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the LMTHomeLoanDetail or
	 * LMTHomeLoanDetail_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Home Loan Details by key
	 * HomeLoanId
	 * 
	 * @param Home
	 *            Loan Details (homeLoanDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(HomeLoanDetail homeLoanDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From LMTHomeLoanDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LoanRefNumber =:LoanRefNumber");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(homeLoanDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", homeLoanDetail.getLoanRefNumber(),
					homeLoanDetail.getLovDescHomeDetailsName(), homeLoanDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",homeLoanDetail.getLoanRefNumber(),
				homeLoanDetail.getLovDescHomeDetailsName(),homeLoanDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into LMTHomeLoanDetail or
	 * LMTHomeLoanDetail_Temp. it fetches the available Sequence form
	 * SeqLMTHomeLoanDetail by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Home Loan Details
	 * 
	 * @param Home
	 *            Loan Details (homeLoanDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public String save(HomeLoanDetail homeLoanDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder();
		insertSql.append(" Insert Into LMTHomeLoanDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LoanRefNumber, LoanRefType, HomeDetails, HomeBuilderName, HomeCostPerFlat," );
		insertSql.append(" HomeCostOfLand, HomeCostOfConstruction, HomeConstructionStage,");
		insertSql.append(" HomeDateOfPocession, HomeAreaOfLand, HomeAreaOfFlat, HomePropertyType,");
		insertSql.append(" HomeOwnerShipType, HomeAddrFlatNbr, HomeAddrStreet, HomeAddrLane1, HomeAddrLane2,");
		insertSql.append(" HomeAddrPOBox, HomeAddrCountry, HomeAddrProvince, HomeAddrCity, HomeAddrZIP, HomeAddrPhone,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:LoanRefNumber, :LoanRefType, :HomeDetails, :HomeBuilderName,");
		insertSql.append(" :HomeCostPerFlat, :HomeCostOfLand, :HomeCostOfConstruction, :HomeConstructionStage,");
		insertSql.append(" :HomeDateOfPocession, :HomeAreaOfLand, :HomeAreaOfFlat, :HomePropertyType,");
		insertSql.append(" :HomeOwnerShipType, :HomeAddrFlatNbr, :HomeAddrStreet, :HomeAddrLane1, :HomeAddrLane2,");
		insertSql.append(" :HomeAddrPOBox, :HomeAddrCountry, :HomeAddrProvince, :HomeAddrCity, :HomeAddrZIP, :HomeAddrPhone,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(homeLoanDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return homeLoanDetail.getId();
	}
	
	/**
	 * This method updates the Record LMTHomeLoanDetail or
	 * LMTHomeLoanDetail_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Home Loan Details by key
	 * HomeLoanId and Version
	 * 
	 * @param Home
	 *            Loan Details (homeLoanDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(HomeLoanDetail homeLoanDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder();
		updateSql.append(" Update LMTHomeLoanDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set LoanRefNumber = :LoanRefNumber, LoanRefType = :LoanRefType," );
		updateSql.append(" HomeDetails = :HomeDetails, HomeBuilderName = :HomeBuilderName," );
		updateSql.append(" HomeCostPerFlat = :HomeCostPerFlat, HomeCostOfLand = :HomeCostOfLand," );
		updateSql.append(" HomeCostOfConstruction = :HomeCostOfConstruction,");
		updateSql.append(" HomeConstructionStage = :HomeConstructionStage,");
		updateSql.append(" HomeDateOfPocession = :HomeDateOfPocession, HomeAreaOfLand = :HomeAreaOfLand,");
		updateSql.append(" HomeAreaOfFlat = :HomeAreaOfFlat, HomePropertyType = :HomePropertyType,");
		updateSql.append(" HomeOwnerShipType = :HomeOwnerShipType, HomeAddrFlatNbr = :HomeAddrFlatNbr,");
		updateSql.append(" HomeAddrStreet = :HomeAddrStreet, HomeAddrLane1 = :HomeAddrLane1,");
		updateSql.append(" HomeAddrLane2 = :HomeAddrLane2, HomeAddrPOBox = :HomeAddrPOBox,");
		updateSql.append(" HomeAddrCountry = :HomeAddrCountry, HomeAddrProvince = :HomeAddrProvince,");
		updateSql.append(" HomeAddrCity = :HomeAddrCity, HomeAddrZIP = :HomeAddrZIP, HomeAddrPhone = :HomeAddrPhone,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where LoanRefNumber =:LoanRefNumber");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(homeLoanDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails = getError("41004",homeLoanDetail.getLoanRefNumber(),
					homeLoanDetail.getLovDescHomeDetailsName(), 
					homeLoanDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Fetch the Construction Stage in HomeLoanDetail details
	 * @return Construction Stage
	 */
	@Override
	public List<LovFieldDetail> getHomeConstructionStage() {
		logger.debug("Entering");

		StringBuilder selectSql =new StringBuilder();
		selectSql.append(" SELECT DISTINCT FieldCodeValue from RMTLovFieldDetail");
		selectSql.append(" Where FieldCode='conststage'");
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<LovFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LovFieldDetail.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.getJdbcOperations().query(
				selectSql.toString(), typeRowMapper);
	}
	
	private ErrorDetails getError(String errorId, String loanRefNumber,
			String homeDetailsName, String userLanguage) {
		
		String[][] parms = new String[2][2];
		parms[1][0] = loanRefNumber;
		parms[1][1] = homeDetailsName;
		parms[0][0] = PennantJavaUtil.getLabel("label_LoanRefNumber") + ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_HomeDetails") + ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(
				PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]), userLanguage);
	}
}