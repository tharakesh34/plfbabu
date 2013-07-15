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
 * FileName    		:  CustomerGroupDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.customermasters.CustomerGroupDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CustomerGroup model</b> class.<br>
 * 
 */
public class CustomerGroupDAOImpl extends BasisNextidDaoImpl<CustomerGroup> implements CustomerGroupDAO {

	private static Logger logger = Logger.getLogger(CustomerGroupDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new CustomerGroup 
	 * @return CustomerGroup
	 */
	@Override
	public CustomerGroup getCustomerGroup() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CustomerGroup");
		CustomerGroup customerGroup= new CustomerGroup();
		if (workFlowDetails!=null){
			customerGroup.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return customerGroup;
	}


	/**
	 * This method get the module from method getCustomerGroup() and set the new record flag as true and return CustomerGroup()   
	 * @return CustomerGroup
	 */
	@Override
	public CustomerGroup getNewCustomerGroup() {
		logger.debug("Entering");
		CustomerGroup customerGroup = getCustomerGroup();
		customerGroup.setNewRecord(true);
		logger.debug("Leaving");
		return customerGroup;
	}

	/**
	 * Fetch the Record  Customer Groups details by key field
	 * 
	 * @param id (integer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerGroup
	 */
	@Override
	public CustomerGroup getCustomerGroupByID(final long id, String type) {
		logger.debug("Entering");
		CustomerGroup customerGroup = getCustomerGroup();
		customerGroup.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT CustGrpID, CustGrpCode, CustGrpDesc, CustGrpRO1, CustGrpLimit, CustGrpIsActive,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustGrpRO1Name,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  CustomerGroups");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustGrpID =:CustGrpID") ;
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGroup);
		RowMapper<CustomerGroup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerGroup.class);
		
		try{
			customerGroup = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			customerGroup = null;
		}
		logger.debug("Leaving");
		return customerGroup;
	}
	
	/**
	 * This method initialize the Record.
	 * @param CustomerGroup (customerGroup)
 	 * @return CustomerGroup
	 */
	@Override
	public void initialize(CustomerGroup customerGroup) {
		super.initialize(customerGroup);
	}
	
	/**
	 * This method refresh the Record.
	 * @param CustomerGroup (customerGroup)
 	 * @return void
	 */
	@Override
	public void refresh(CustomerGroup customerGroup) {
		
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the CustomerGroups or CustomerGroups_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Customer Groups by key CustGrpID
	 * 
	 * @param Customer Groups (customerGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(CustomerGroup customerGroup,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append(" Delete From CustomerGroups");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustGrpID =:CustGrpID");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGroup);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003", customerGroup.getCustGrpID(), customerGroup.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", customerGroup.getCustGrpID(), customerGroup.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into CustomerGroups or CustomerGroups_Temp.
	 * it fetches the available Sequence form SeqCustomerGroups by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Customer Groups 
	 * 
	 * @param Customer Groups (customerGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerGroup customerGroup,String type) {
		logger.debug("Entering");
		
		if(customerGroup.getCustGrpID() ==0 || customerGroup.getCustGrpID() == Long.MIN_VALUE){
			customerGroup.setCustGrpID(getNextidviewDAO().getNextId("Seq"+PennantJavaUtil.getTabelMap("CustomerGroup")));	
		}
		
		StringBuilder insertSql = new StringBuilder(" Insert Into CustomerGroups");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustGrpID, CustGrpCode, CustGrpDesc, CustGrpRO1, CustGrpLimit, CustGrpIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values( :CustGrpID, :CustGrpCode, :CustGrpDesc, :CustGrpRO1, :CustGrpLimit, :CustGrpIsActive, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGroup);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return customerGroup.getId();
	}
	
	/**
	 * This method updates the Record CustomerGroups or CustomerGroups_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Customer Groups by key CustGrpID and Version
	 * 
	 * @param Customer Groups (customerGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(CustomerGroup customerGroup,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update CustomerGroups");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set CustGrpID = :CustGrpID, CustGrpCode = :CustGrpCode, CustGrpDesc = :CustGrpDesc, CustGrpRO1 = :CustGrpRO1, " );
		updateSql.append(" CustGrpLimit = :CustGrpLimit, CustGrpIsActive = :CustGrpIsActive ," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where CustGrpID =:CustGrpID ");
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGroup);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004", customerGroup.getCustGrpID(), customerGroup.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, long custGrpID, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = String.valueOf(custGrpID);
		parms[0][0] = PennantJavaUtil.getLabel("label_CustGrpID")+":" +parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}