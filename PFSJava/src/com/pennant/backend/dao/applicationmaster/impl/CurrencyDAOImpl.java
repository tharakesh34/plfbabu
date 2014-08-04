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
 * FileName    		:  CurrencyDAOImpl.java                                                   * 	  
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

import java.util.HashMap;
import java.util.List;
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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Currency model</b> class.<br>
 * 
 */
public class CurrencyDAOImpl extends BasisCodeDAO<Currency> implements CurrencyDAO {

	private static Logger logger = Logger.getLogger(CurrencyDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new Currency 
	 * @return Currency
	 */
	@Override
	public Currency getCurrency() {
		logger.debug("Entering ");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Currency");
		Currency currency= new Currency();
		if (workFlowDetails!=null){
			currency.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving ");
		return currency;
	}

	/**
	 * This method get the module from method getCurrency() and set the new
	 * record flag as true and return Currency()
	 * 
	 * @return Currency
	 */
	@Override
	public Currency getNewCurrency() {
		logger.debug("Entering ");
		Currency currency = getCurrency();
		currency.setNewRecord(true);
		logger.debug("Leaving ");
		return currency;
	}

	/**
	 * Fetch the Record  Currency details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Currency
	 */
	@Override
	public Currency getCurrencyById(final String id, String type) {
		logger.debug("Entering ");
		Currency currency = new Currency();
		currency.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT CcyCode, CcyNumber, CcyDesc, CcySwiftCode," );
		selectSql.append(" CcyEditField, CcyMinorCcyUnits, CcyDrRateBasisCode," );
		selectSql.append(" CcyCrRateBasisCode, CcyIsIntRounding, CcySpotRate, CcyIsReceprocal," );
		selectSql.append(" CcyUserRateBuy, CcyUserRateSell, CcyIsMember, CcyIsGroup," );
		selectSql.append(" CcyIsAlwForLoans, CcyIsAlwForDepo, CcyIsAlwForAc, CcyIsActive,");
		selectSql.append(" CcyMinorCcyDesc, CcySymbol," );
		if(type.contains("View")){
			selectSql.append(" lovDescCcyDrRateBasisCodeName, lovDescCcyCrRateBasisCodeName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  RMTCurrencies");
		selectSql.append(StringUtils.trimToEmpty(type) );
		selectSql.append("  Where CcyCode =:CcyCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		RowMapper<Currency> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(Currency.class);

		try{
			currency = this.namedParameterJdbcTemplate.queryForObject(
					 selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			currency = null;
		}
		logger.debug("Leaving ");
		return currency;
	}
	
	/**
	 * Fetch the Record  Currency details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Currency
	 */
	@Override
	public Currency getCurrencyByCode(final String id) {
		logger.debug("Entering ");
		Currency currency = new Currency();
		currency.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT CcyCode, CcyNumber, CcyEditField" );
		selectSql.append(" FROM  RMTCurrencies");
		selectSql.append("  Where CcyCode =:CcyCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		RowMapper<Currency> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Currency.class);

		try{
			currency = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			currency = null;
		}
		logger.debug("Leaving ");
		return currency;
	}
	
	
	/**
	 * Fetch the Record  Currency details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ValueLabel
	 */
	@Override
	public String getCurrencyById(final String id) {
		logger.debug("Entering ");
		String ccyCode = null;
		Currency currency = new Currency();
		currency.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT CcyNumber FROM  RMTCurrencies");
		selectSql.append(" Where CcyCode =:CcyCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);

		try{
			ccyCode = this.namedParameterJdbcTemplate.queryForObject(
					 selectSql.toString(), beanParameters, String.class);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			ccyCode = "";
		}
		logger.debug("Leaving ");
		return ccyCode;
	}

	/**
	 * This method initialize the Record.
	 * @param Currency (currency)
	 * @return Currency
	 */
	@Override
	public void initialize(Currency currency) {
		super.initialize(currency);
	}
	
	/**
	 * This method refresh the Record.
	 * @param Currency (currency)
	 * @return void
	 */
	@Override
	public void refresh(Currency currency) {

	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTCurrencies or RMTCurrencies_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Currency by key CcyCode
	 * 
	 * @param Currency (currency)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Currency currency,String type) {
		logger.debug("Entering ");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTCurrencies");
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where CcyCode =:CcyCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003", currency.getCcyCode(),
						currency.getCcyNumber(),currency.getCcySwiftCode(),
						currency.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", currency.getCcyCode(),
					currency.getCcyNumber(),currency.getCcySwiftCode(),
					currency.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method insert new Records into RMTCurrencies or RMTCurrencies_Temp.
	 *
	 * save Currency 
	 * 
	 * @param Currency (currency)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return String
	 * 
	 */
	@Override
	public String save(Currency currency,String type) {
		logger.debug("Entering ");

		StringBuilder insertSql = new StringBuilder("Insert Into RMTCurrencies" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (CcyCode, CcyNumber, CcyDesc, CcySwiftCode, CcyEditField," );
		insertSql.append(" CcyMinorCcyUnits, CcyDrRateBasisCode, CcyCrRateBasisCode," );
		insertSql.append(" CcyIsIntRounding, CcySpotRate, CcyIsReceprocal, CcyUserRateBuy," );
		insertSql.append(" CcyUserRateSell, CcyIsMember, CcyIsGroup, CcyIsAlwForLoans, CcyIsAlwForDepo," );
		insertSql.append(" CcyIsAlwForAc, CcyIsActive, CcyMinorCcyDesc, CcySymbol," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CcyCode, :CcyNumber, :CcyDesc, :CcySwiftCode, :CcyEditField," );
		insertSql.append(" :CcyMinorCcyUnits, :CcyDrRateBasisCode, :CcyCrRateBasisCode," );
		insertSql.append(" :CcyIsIntRounding, :CcySpotRate, :CcyIsReceprocal, :CcyUserRateBuy," );
		insertSql.append(" :CcyUserRateSell, :CcyIsMember, :CcyIsGroup, :CcyIsAlwForLoans," );
		insertSql.append(" :CcyIsAlwForDepo, :CcyIsAlwForAc, :CcyIsActive, :CcyMinorCcyDesc, :CcySymbol,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
		return currency.getId();
	}

	/**
	 * This method updates the Record RMTCurrencies or RMTCurrencies_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Currency by key CcyCode and Version
	 * 
	 * @param Currency (currency)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Currency currency,String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		
		StringBuilder updateSql = new StringBuilder("Update RMTCurrencies" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set CcyCode = :CcyCode, CcyNumber = :CcyNumber, CcyDesc = :CcyDesc,");
		updateSql.append(" CcySwiftCode = :CcySwiftCode, CcyEditField = :CcyEditField," );
		updateSql.append(" CcyMinorCcyUnits =:CcyMinorCcyUnits,CcyDrRateBasisCode = :CcyDrRateBasisCode,");
		updateSql.append(" CcyCrRateBasisCode = :CcyCrRateBasisCode,CcyIsIntRounding = :CcyIsIntRounding,");
		updateSql.append(" CcySpotRate = :CcySpotRate, CcyIsReceprocal = :CcyIsReceprocal,");
		updateSql.append(" CcyUserRateBuy = :CcyUserRateBuy, CcyUserRateSell = :CcyUserRateSell," );
		updateSql.append(" CcyIsMember = :CcyIsMember, CcyIsGroup = :CcyIsGroup," );
		updateSql.append(" CcyIsAlwForLoans = :CcyIsAlwForLoans, CcyIsAlwForDepo = :CcyIsAlwForDepo," );
		updateSql.append(" CcyIsAlwForAc = :CcyIsAlwForAc, CcyIsActive = :CcyIsActive," );
		updateSql.append(" CcyMinorCcyDesc = :CcyMinorCcyDesc, CcySymbol = :CcySymbol ," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where CcyCode =:CcyCode ");

		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails=  getError("41004", currency.getCcyCode(),
					currency.getCcyNumber(),currency.getCcySwiftCode(),
					currency.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method for Checking Currency having Unique CcyCode,CcyNumber and SwiftCode or Not
	 */
	@Override
	public boolean getUniqueCurrencyByID(Currency currency,boolean ccyNum, boolean swiftCode) {
		logger.debug("Entering ");
		
		String whereCond = "";
		if(ccyNum){
			whereCond = " Where  CcyNumber =:CcyNumber";
		}else if(swiftCode){
			whereCond = " Where CcySwiftCode =:CcySwiftCode"; 
		}

		StringBuilder selectSql = new StringBuilder("SELECT CcyCode, CcyNumber, CcyDesc, CcySwiftCode ");
		selectSql.append(" FROM  RMTCurrencies_View ");
		selectSql.append(whereCond);

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		RowMapper<Currency> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(Currency.class);

		List<Currency> list = this.namedParameterJdbcTemplate.query(
				selectSql.toString(), beanParameters, typeRowMapper);	
		if(list !=null && list.size()>0){
			logger.debug("Leaving ");
			return true;
		}
		logger.debug("Leaving ");
		return false;
	}
	
	/**
	 * Fetch the Record  Currency details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ValueLabel
	 */
	@Override
	 public List<ValueLabel> getCcyCodesByFinRef() {
		logger.debug("Entering ");

		StringBuilder selectSql = new StringBuilder("SELECT CcyCode AS Label, CcyNumber AS Value FROM  RMTCurrencies");
		//selectSql.append(" where CcyCode IN (select DISTINCT FinCcy from FinanceMain WHERE FinIsActive = 1) ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ValueLabel> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ValueLabel.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);	
	}


	
	private ErrorDetails  getError(String errorId, String ccyCode,String ccyNumber,
			String ccySwiftCode, String userLanguage){
		String[][] parms= new String[2][3]; 

		parms[1][0] = ccyCode;
		parms[1][1] = ccyNumber;
		parms[1][2] = ccySwiftCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_CcyCode")+ ":" + parms[1][0] 
		                 +" "+ PennantJavaUtil.getLabel("label_CcyNumber")+ ":" + parms[1][1];
		parms[0][1]= PennantJavaUtil.getLabel("label_CcySwiftCode")+ ":" + parms[1][2];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

	@Override
    public List<Currency> getCurrencyList(List<String> asList) {
		logger.debug("Entering ");
		
		Map<String, Object> namedParameters=new HashMap<String, Object>();
		namedParameters.put("CCYList", asList);

		StringBuilder selectSql = new StringBuilder("SELECT CcyCode, CcyNumber, " );
		selectSql.append(" CcyEditField, CcyMinorCcyUnits, CcySpotRate, CcyIsReceprocal " );
		selectSql.append(" FROM  RMTCurrencies");
		selectSql.append("  Where CcyCode IN(:CCYList)");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<Currency> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Currency.class);

		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), namedParameters, typeRowMapper);	
    }

}