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
 * FileName    		:  CarLoanDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.lmtmasters.CarLoanDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CarLoanDetail model</b> class.<br>
 * 
 */
public class CarLoanDetailDAOImpl extends BasisCodeDAO<CarLoanDetail>
		implements CarLoanDetailDAO {

	private static Logger logger = Logger.getLogger(CarLoanDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new CarLoanDetail
	 * 
	 * @return CarLoanDetail
	 */
	@Override
	public CarLoanDetail getCarLoanDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CarLoanDetail");
		CarLoanDetail carLoanDetail= new CarLoanDetail();
		if (workFlowDetails!=null){
			carLoanDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return carLoanDetail;
	}

	/**
	 * This method get the module from method getCarLoanDetail() and set the new
	 * record flag as true and return CarLoanDetail()
	 * 
	 * @return CarLoanDetail
	 */
	@Override
	public CarLoanDetail getNewCarLoanDetail() {
		logger.debug("Entering");
		CarLoanDetail carLoanDetail = getCarLoanDetail();
		carLoanDetail.setNewRecord(true);
		logger.debug("Leaving");
		return carLoanDetail;
	}

	/**
	 * Fetch the Record  Car Loan Details details by key field
	 * 
	 * @param loanRefNumber (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CarLoanDetail
	 */
	@Override
	public CarLoanDetail getCarLoanDetailByID(final String loanRefNumber,int itemNumber, String type) {
		logger.debug("Entering");
		CarLoanDetail carLoanDetail = new CarLoanDetail();
		carLoanDetail.setLoanRefNumber(loanRefNumber);
		carLoanDetail.setItemNumber(itemNumber);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT LoanRefNumber, ItemNumber, LoanRefType, CarLoanFor, CarUsage, CarVersion,");
		selectSql.append(" CarMakeYear, CarCapacity, CarDealer,CarCc,CarChasisNo,CarRegNo,CarColor,");
		selectSql.append(" EngineNumber,PaymentMode, ManufacturerId, ");
		selectSql.append(" PurchageOdrNumber,QuoationNbr,QuoationDate,DealerPhone,PurchaseDate,  VehicleModelId, ");
		
		if(type.contains("View")){
			selectSql.append(" LovDescLoanForCodeName,LovDescLoanForCode,LovDescCarUsageCodeName,");
			selectSql.append(" LovDescLoanForValue,LovDescCarUsageCode,LovDescCarUsageValue,");
			selectSql.append(" LovDescManufacturerName, LovDescModelDesc, lovDescVehicleVersionCode, ");
			selectSql.append(" LovDescCarDealerName,lovDescCarDealerPhone,lovDescCarDealerFax,LovDescThirdPartyNatName,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" ,ThirdPartyReg,ThirdPartyName,PassportNum,ThirdPartyNat,EmiratesRegNum,");
		selectSql.append(" SellerType,DealerOrSellerAcc,VehicleValue,PrivateDealerName,SalesPersonName");
		selectSql.append(" FROM  LMTCarLoanDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LoanRefNumber = :LoanRefNumber and ItemNumber = :ItemNumber");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(carLoanDetail);
		RowMapper<CarLoanDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CarLoanDetail.class);

		try{
			carLoanDetail = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			carLoanDetail = null;
		}
		logger.debug("Leaving");
		return carLoanDetail;
	}


    @Override
	public List<CarLoanDetail> getVehicleLoanDetailByFinRef(final String id, String type) {
		logger.debug("Entering");
		CarLoanDetail vehicleLoanDetail = new CarLoanDetail();
		vehicleLoanDetail.setLoanRefNumber(id);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT LoanRefNumber, ItemNumber, LoanRefType, CarLoanFor, CarUsage, CarVersion,");
		selectSql.append(" CarMakeYear, CarCapacity, CarDealer,CarCc,CarChasisNo,CarRegNo,CarColor,");
		selectSql.append(" EngineNumber,PaymentMode, ManufacturerId, ");
		selectSql.append(" PurchageOdrNumber,QuoationNbr,QuoationDate,DealerPhone,PurchaseDate,  VehicleModelId, ");
		
		if(type.contains("View")){
			selectSql.append(" LovDescLoanForCodeName,LovDescLoanForCode,LovDescCarUsageCodeName,");
			selectSql.append(" LovDescLoanForValue,LovDescCarUsageCode,LovDescCarUsageValue,");
			selectSql.append(" LovDescManufacturerName, LovDescModelDesc, lovDescVehicleVersionCode, ");
			selectSql.append(" LovDescCarDealerName,lovDescCarDealerPhone,lovDescCarDealerFax,LovDescThirdPartyNatName,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" ,ThirdPartyReg,ThirdPartyName,PassportNum,ThirdPartyNat,EmiratesRegNum,");
		selectSql.append(" SellerType,DealerOrSellerAcc,VehicleValue,PrivateDealerName,SalesPersonName ");
		selectSql.append(" FROM  LMTCarLoanDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LoanRefNumber =:LoanRefNumber ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleLoanDetail);
		RowMapper<CarLoanDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CarLoanDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	
	
	/**
	 * This method initialize the Record.
	 * @param CarLoanDetail (carLoanDetail)
	 * @return CarLoanDetail
	 */
	@Override
	public void initialize(CarLoanDetail carLoanDetail) {
		super.initialize(carLoanDetail);
	}
	
	/**
	 * This method refresh the Record.
	 * @param CarLoanDetail (carLoanDetail)
	 * @return void
	 */
	@Override
	public void refresh(CarLoanDetail carLoanDetail) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the LMTCarLoanDetail or
	 * LMTCarLoanDetail_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Car Loan Details by key
	 * CarLoanId
	 * 
	 * @param Car
	 *            Loan Details (carLoanDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(CarLoanDetail carLoanDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From LMTCarLoanDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LoanRefNumber =:LoanRefNumber And ItemNumber = :ItemNumber");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(carLoanDetail);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003",
						carLoanDetail.getLoanRefNumber(), carLoanDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",
					carLoanDetail.getLoanRefNumber(), carLoanDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into LMTCarLoanDetail or
	 * LMTCarLoanDetail_Temp. it fetches the available Sequence form
	 * SeqLMTCarLoanDetail by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Car Loan Details
	 * 
	 * @param Car
	 *            Loan Details (carLoanDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(CarLoanDetail carLoanDetail,String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder("Insert Into LMTCarLoanDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(LoanRefNumber, ItemNumber, LoanRefType, CarLoanFor, ");
		insertSql.append(" CarUsage, CarVersion, CarMakeYear, CarCapacity, CarDealer, " );
		insertSql.append(" CarCc,CarChasisNo,CarRegNo,CarColor, ");
		insertSql.append("	EngineNumber,PaymentMode,");
		insertSql.append("	PurchageOdrNumber,QuoationNbr,QuoationDate,DealerPhone,PurchaseDate,");
		insertSql.append("	ManufacturerId, VehicleModelId, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		insertSql.append(" ,ThirdPartyReg,ThirdPartyName,PassportNum,ThirdPartyNat,EmiratesRegNum,");
		insertSql.append(" SellerType,DealerOrSellerAcc,VehicleValue,PrivateDealerName,SalesPersonName)");
		insertSql.append(" Values(:LoanRefNumber, :ItemNumber, :LoanRefType, :CarLoanFor, ");
		insertSql.append(" :CarUsage, :CarVersion, :CarMakeYear, :CarCapacity, :CarDealer,");
		insertSql.append(" :CarCc, :CarChasisNo,:CarRegNo, :CarColor, ");
		insertSql.append(" :EngineNumber,:PaymentMode,");
		insertSql.append(" :PurchageOdrNumber,:QuoationNbr,:QuoationDate,:DealerPhone,:PurchaseDate,");
		insertSql.append("	:ManufacturerId, :VehicleModelId, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId,");
		insertSql.append(" :ThirdPartyReg,:ThirdPartyName,:PassportNum,:ThirdPartyNat,:EmiratesRegNum,");
		insertSql.append(" :SellerType,:DealerOrSellerAcc,:VehicleValue,:PrivateDealerName,:SalesPersonName)");
		
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(carLoanDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return carLoanDetail.getLoanRefNumber();
	}

	/**
	 * This method updates the Record LMTCarLoanDetail or LMTCarLoanDetail_Temp.
	 * if Record not updated then throws DataAccessException with error 41004.
	 * update Car Loan Details by key CarLoanId and Version
	 * 
	 * @param Car
	 *            Loan Details (carLoanDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(CarLoanDetail carLoanDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update LMTCarLoanDetail");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set LoanRefNumber = :LoanRefNumber, ItemNumber = :ItemNumber, LoanRefType = :LoanRefType," );
		updateSql.append(" CarLoanFor = :CarLoanFor, CarUsage = :CarUsage, CarVersion = :CarVersion," );
		updateSql.append(" CarMakeYear = :CarMakeYear, CarCapacity = :CarCapacity, CarDealer = :CarDealer,");
		updateSql.append(" CarCc= :CarCc, CarChasisNo= :CarChasisNo,");
		updateSql.append(" CarRegNo= :CarRegNo, CarColor= :CarColor, ");
		updateSql.append(" EngineNumber=:EngineNumber,PaymentMode=:PaymentMode,");
		updateSql.append(" PurchageOdrNumber=:PurchageOdrNumber,QuoationNbr=:QuoationNbr,QuoationDate=:QuoationDate,DealerPhone=:DealerPhone,PurchaseDate=:PurchaseDate,");
		updateSql.append(" ManufacturerId = :ManufacturerId, VehicleModelId = :VehicleModelId, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, ");
		updateSql.append(" NextRoleCode = :NextRoleCode,TaskId = :TaskId, ");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, ");
		updateSql.append(" ThirdPartyReg = :ThirdPartyReg,ThirdPartyName = :ThirdPartyName, ");
		updateSql.append(" PassportNum = :PassportNum, ThirdPartyNat = :ThirdPartyNat, EmiratesRegNum = :EmiratesRegNum, ");
		updateSql.append(" SellerType = :SellerType,DealerOrSellerAcc = :DealerOrSellerAcc, ");
		updateSql.append(" VehicleValue = :VehicleValue, PrivateDealerName = :PrivateDealerName,SalesPersonName = :SalesPersonName");
		updateSql.append(" Where LoanRefNumber =:LoanRefNumber And ItemNumber = :ItemNumber");

		if (!type.endsWith("_TEMP")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(carLoanDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails = getError("41004",
					carLoanDetail.getLoanRefNumber(), carLoanDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails  getError(String errorId, String loanRef, String userLanguage){

		String[][] parms= new String[2][2]; 
		parms[1][0] = loanRef;
		parms[0][0] = PennantJavaUtil.getLabel("label_CarLoanRefNumber")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0]), userLanguage);
	}
}