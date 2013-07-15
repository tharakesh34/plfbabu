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
 * FileName    		:  AddressTypeDAOImpl.java                                                   * 	  
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

package com.pennant.backend.dao.systemmasters.impl;

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
import com.pennant.backend.dao.systemmasters.AddressTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>AddressType model</b> class.<br>
 * 
 */
public class AddressTypeDAOImpl extends BasisCodeDAO<AddressType> implements AddressTypeDAO {

	private static Logger logger = Logger.getLogger(AddressTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new AddressType
	 * 
	 * @return AddressType
	 */
	@Override
	public AddressType getAddressType() {
		logger.debug("Entering ");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("AddressType");
		AddressType addressType = new AddressType();
		if (workFlowDetails != null) {
			addressType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving ");
		return addressType;
	}

	/**
	 * This method get the module from method getAddressType() and set the new
	 * record flag as true and return AddressType()
	 * 
	 * @return AddressType
	 */
	@Override
	public AddressType getNewAddressType() {
		logger.debug("Entering ");
		AddressType addressType = getAddressType();
		addressType.setNewRecord(true);
		logger.debug("Leaving");
		return addressType;
	}

	/**
	 * Fetch the Record Address Type details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AddressType
	 */
	@Override
	public AddressType getAddressTypeById(final String id, String type) {
		logger.debug("Entering");
		AddressType addressType = getAddressType();
		addressType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT AddrTypeCode, AddrTypeDesc, AddrTypePriority, AddrTypeIsActive," );
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTAddressTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AddrTypeCode =:AddrTypeCode") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(addressType);
		RowMapper<AddressType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AddressType.class);

		try {
			addressType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			addressType = null;
		}
		logger.debug("Leaving");
		return addressType;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param AddressType
	 *            (addressType)
	 * @return AddressType
	 */
	@Override
	public void initialize(AddressType addressType) {
		super.initialize(addressType);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param AddressType
	 *            (addressType)
	 * @return void
	 */
	@Override
	public void refresh(AddressType addressType) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTAddressTypes or
	 * BMTAddressTypes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Address Type by key
	 * AddrTypeCode
	 * 
	 * @param Address
	 *            Type (addressType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(AddressType addressType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BMTAddressTypes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where AddrTypeCode =:AddrTypeCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(addressType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", addressType.getAddrTypeCode(), addressType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", addressType.getAddrTypeCode(), addressType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTAddressTypes or
	 * BMTAddressTypes_Temp.
	 * 
	 * save Address Type
	 * 
	 * @param Address
	 *            Type (addressType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(AddressType addressType, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTAddressTypes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AddrTypeCode, AddrTypeDesc, AddrTypePriority, AddrTypeIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:AddrTypeCode, :AddrTypeDesc, :AddrTypePriority, :AddrTypeIsActive, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(addressType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return addressType.getId();
	}

	/**
	 * This method updates the Record BMTAddressTypes or BMTAddressTypes_Temp.
	 * if Record not updated then throws DataAccessException with error 41004.
	 * update Address Type by key AddrTypeCode and Version
	 * 
	 * @param Address
	 *            Type (addressType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(AddressType addressType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTAddressTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set AddrTypeCode = :AddrTypeCode, AddrTypeDesc = :AddrTypeDesc," );
		updateSql.append(" AddrTypePriority = :AddrTypePriority, AddrTypeIsActive = :AddrTypeIsActive ," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where AddrTypeCode =:AddrTypeCode ");
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(addressType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),	beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails= getError("41003", addressType.getAddrTypeCode(), addressType.getUserDetails().getUsrLanguage());	
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String addrTypeCode,String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = addrTypeCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_AddrTypeCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}