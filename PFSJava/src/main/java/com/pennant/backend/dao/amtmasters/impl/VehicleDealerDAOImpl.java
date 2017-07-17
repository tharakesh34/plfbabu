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
 * FileName    		:  VehicleDealerDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.amtmasters.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>VehicleDealer model</b> class.<br>
 */
public class VehicleDealerDAOImpl extends BasisNextidDaoImpl<VehicleDealer> implements VehicleDealerDAO {
	private static Logger logger = Logger.getLogger(VehicleDealerDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private JdbcTemplate jdbcTemplate;

	public VehicleDealerDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record  Vehicle Dealer details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VehicleDealer
	 */
	@Override
	public VehicleDealer getVehicleDealerById(final long id, String type) {
		logger.debug("Entering");
		VehicleDealer vehicleDealer = new VehicleDealer();
		vehicleDealer.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT DealerId,DealerType, DealerName,DealerTelephone,DealerFax,DealerAddress1,DealerAddress2, ");
		selectSql.append("DealerAddress3,DealerAddress4,DealerCountry,DealerCity,DealerProvince,Email,POBox,ZipCode,Active,Emirates,CommisionPaidAt,");
		selectSql.append("CalculationRule,PaymentMode,AccountNumber,AccountingSetId,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode,TaskId, NextTaskId, RecordType, WorkflowId,SellerType");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",LovDescCountry,LovDescCity,LovDescProvince,");
			selectSql.append("CalRuleDesc,AccountingSetCode,AccountingSetDesc,EmiratesDescription");
		}
		selectSql.append(" FROM  AMTVehicleDealer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DealerId =:DealerId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		RowMapper<VehicleDealer> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VehicleDealer.class);

		try{
			vehicleDealer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), 
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vehicleDealer = null;
		}
		logger.debug("Leaving");
		return vehicleDealer;
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the AMTVehicleDealer or AMTVehicleDealer_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Vehicle Dealer by key DealerId
	 * 
	 * @param Vehicle Dealer (vehicleDealer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(VehicleDealer vehicleDealer, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From AMTVehicleDealer");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DealerId =:DealerId");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
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
	 * This method insert new Records into AMTVehicleDealer or
	 * AMTVehicleDealer_Temp. it fetches the available Sequence form
	 * SeqAMTVehicleDealer by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Vehicle Dealer
	 * 
	 * @param Vehicle
	 *            Dealer (vehicleDealer)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(VehicleDealer vehicleDealer,String type) {
		logger.debug("Entering");
		if (vehicleDealer.getId()==Long.MIN_VALUE){
			vehicleDealer.setId(getNextidviewDAO().getNextId("SeqAMTVehicleDealer"));
			logger.debug("get NextID:"+vehicleDealer.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into AMTVehicleDealer");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(DealerId,DealerType, DealerName,DealerTelephone,DealerFax,DealerAddress1,DealerAddress2, " );
		insertSql.append("DealerAddress3,DealerAddress4,DealerCountry,DealerCity,DealerProvince,");
		insertSql.append("Email,POBox,ZipCode,Active,Emirates,CommisionPaidAt,CalculationRule,PaymentMode,AccountNumber,AccountingSetId,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,SellerType)");
		insertSql.append(" Values(:DealerId,:DealerType, :DealerName,:DealerTelephone,:DealerFax,:DealerAddress1,");
		insertSql.append(" :DealerAddress2,:DealerAddress3,:DealerAddress4,:DealerCountry,:DealerCity,:DealerProvince,");
		insertSql.append(" :Email,:POBox,:ZipCode,:Active,:Emirates,:CommisionPaidAt,:CalculationRule,:PaymentMode,:AccountNumber,:AccountingSetId,");
		insertSql.append(" :Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId,:SellerType)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vehicleDealer.getId();
	}

	/**
	 * This method updates the Record AMTVehicleDealer or AMTVehicleDealer_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Vehicle Dealer by key DealerId and Version
	 * 
	 * @param Vehicle Dealer (vehicleDealer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(VehicleDealer vehicleDealer, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update AMTVehicleDealer");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DealerType = :DealerType, DealerName = :DealerName, ");
		updateSql.append("DealerTelephone =:DealerTelephone,DealerFax = :DealerFax,DealerAddress1 = :DealerAddress1,");
		updateSql.append("DealerAddress2 = :DealerAddress2,DealerAddress3 = :DealerAddress3,DealerAddress4 = :DealerAddress4,");
		updateSql.append(" DealerCountry = :DealerCountry,DealerCity = :DealerCity,DealerProvince = :DealerProvince,");
		updateSql.append(" Email = :Email,POBox = :POBox,ZipCode = :ZipCode,Active = :Active,Emirates = :Emirates,CommisionPaidAt = :CommisionPaidAt,");
		updateSql.append("CalculationRule = :CalculationRule,PaymentMode = :PaymentMode,AccountNumber = :AccountNumber,AccountingSetId = :AccountingSetId,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, " );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId,SellerType = :SellerType ");
		updateSql.append(" Where DealerId =:DealerId");

		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
    public boolean SearchName(String dealerName) {
		logger.debug("Entering");
		logger.debug(" Inside searchname");
		boolean status = false;
		try{
			String searchQuery="select count(dealerName) from AMTVehicleDealer_View where dealerName=?";
			
			@SuppressWarnings("deprecation")
            int count =jdbcTemplate.queryForInt(searchQuery, new Object []{dealerName});
			logger.debug(" dealer name  : "+count);
			if(count != 0){
				status = true;
			}
		}catch(IncorrectResultSizeDataAccessException e ){
			logger.warn("Exception: ", e);				
		}
		logger.debug("Leaving");
		
        return status;
    }
	/**
	 * Fetch the Record  VehicleDealer details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return count
	 */
	@Override
	public int getVehicleDealerByType(final String dealerType,String name, long id,String type) {
		logger.debug("Entering");
		VehicleDealer vehicleDealer = new VehicleDealer();
		
		vehicleDealer.setDealerType(dealerType);;
		vehicleDealer.setDealerName(name);
		vehicleDealer.setDealerId(id);
		
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From AMTVehicleDealer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DealerType =:DealerType AND DealerName =:DealerName AND DealerId !=:DealerId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);	
	}

	@Override
	public int getVASManufactureCode(String dealerName, String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ManufacturerName", dealerName);
		int count;

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From VASStructure");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ManufacturerName =:ManufacturerName ");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			 count= this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source,Integer.class);
		} catch(EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
		logger.debug("Leaving");
		return count;
	}
}