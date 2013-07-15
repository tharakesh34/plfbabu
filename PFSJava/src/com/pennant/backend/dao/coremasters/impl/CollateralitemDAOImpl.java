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
 * FileName    		:  CollateralitemDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-02-2013    														*
 *                                                                  						*
 * Modified Date    :  20-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-02-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.coremasters.impl;


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

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.app.util.ErrorUtil;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.coremasters.CollateralitemDAO;
import com.pennant.backend.model.coremasters.Collateralitem;

/**
 * DAO methods implementation for the <b>Collateralitem model</b> class.<br>
 * 
 */

public class CollateralitemDAOImpl extends BasisCodeDAO<Collateralitem> implements CollateralitemDAO {

	private static Logger logger = Logger.getLogger(CollateralitemDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new Collateralitem 
	 * @return Collateralitem
	 */

	@Override
	public Collateralitem getCollateralitem() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Collateralitem");
		Collateralitem collateralitem= new Collateralitem();
		if (workFlowDetails!=null){
			collateralitem.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return collateralitem;
	}


	/**
	 * This method get the module from method getCollateralitem() and set the new record flag as true and return Collateralitem()   
	 * @return Collateralitem
	 */


	@Override
	public Collateralitem getNewCollateralitem() {
		logger.debug("Entering");
		Collateralitem collateralitem = getCollateralitem();
		collateralitem.setNewRecord(true);
		logger.debug("Leaving");
		return collateralitem;
	}

	/**
	 * Fetch the Record  Collateral Items details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Collateralitem
	 */
	@Override
	public Collateralitem getCollateralitemById(final String id, String type) {
		logger.debug("Entering");
		Collateralitem collateralitem = getCollateralitem();
		
		collateralitem.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select HYCUS, HYCLC, HYDLP, HYDLR, HYDBNM, HYAB, HYAN, HYAS, HYCLP, HYCLR, HYCCM, HYCNA, HYCLO, HYDPC, HYCPI, HYCXD, HYLRD, HYFRQ, HYNRD, HYNOU, HYUNP, HYCCY, HYCLV, HYSVM, HYMCV, HYBKV, HYTOTA, HYISV, HYIXD, HYNR1, HYNR2, HYNR3, HYNR4, HYDLM");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescHYCNAName,lovDescHYCLOName,lovDescHYDPCName,lovDescHYCPIName,lovDescHYFRQName,lovDescHYCCYName, lovDescHYCLPName");
		}
		selectSql.append(" From HYPF");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where HYCUS =:HYCUS");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralitem);
		RowMapper<Collateralitem> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Collateralitem.class);
		
		try{
			collateralitem = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			collateralitem = null;
		}
		logger.debug("Leaving");
		return collateralitem;
	}
	
	/**
	 * This method initialise the Record.
	 * @param Collateralitem (collateralitem)
 	 * @return Collateralitem
	 */
	@Override
	public void initialize(Collateralitem collateralitem) {
		super.initialize(collateralitem);
	}
	/**
	 * This method refresh the Record.
	 * @param Collateralitem (collateralitem)
 	 * @return void
	 */
	@Override
	public void refresh(Collateralitem collateralitem) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the HYPF or HYPF_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Collateral Items by key HYCUS
	 * 
	 * @param Collateral Items (collateralitem)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Collateralitem collateralitem,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From HYPF");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where HYCUS =:HYCUS");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralitem);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",collateralitem.getId() ,collateralitem.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",collateralitem.getId() ,collateralitem.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into HYPF or HYPF_Temp.
	 *
	 * save Collateral Items 
	 * 
	 * @param Collateral Items (collateralitem)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(Collateralitem collateralitem,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into HYPF");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (HYCUS, HYCLC, HYDLP, HYDLR, HYDBNM, HYAB, HYAN, HYAS, HYCLP, HYCLR, HYCCM, HYCNA, HYCLO, HYDPC, HYCPI, HYCXD, HYLRD, HYFRQ, HYNRD, HYNOU, HYUNP, HYCCY, HYCLV, HYSVM, HYMCV, HYBKV, HYTOTA, HYISV, HYIXD, HYNR1, HYNR2, HYNR3, HYNR4, HYDLM");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:HYCUS, :HYCLC, :HYDLP, :HYDLR, :HYDBNM, :HYAB, :HYAN, :HYAS, :HYCLP, :HYCLR, :HYCCM, :HYCNA, :HYCLO, :HYDPC, :HYCPI, :HYCXD, :HYLRD, :HYFRQ, :HYNRD, :HYNOU, :HYUNP, :HYCCY, :HYCLV, :HYSVM, :HYMCV, :HYBKV, :HYTOTA, :HYISV, :HYIXD, :HYNR1, :HYNR2, :HYNR3, :HYNR4, :HYDLM");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralitem);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return collateralitem.getId();
	}
	
	/**
	 * This method updates the Record HYPF or HYPF_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Collateral Items by key HYCUS and Version
	 * 
	 * @param Collateral Items (collateralitem)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(Collateralitem collateralitem,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update HYPF");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set HYCUS = :HYCUS, HYCLC = :HYCLC, HYDLP = :HYDLP, HYDLR = :HYDLR, HYDBNM = :HYDBNM, HYAB = :HYAB, HYAN = :HYAN, HYAS = :HYAS, HYCLP = :HYCLP, HYCLR = :HYCLR, HYCCM = :HYCCM, HYCNA = :HYCNA, HYCLO = :HYCLO, HYDPC = :HYDPC, HYCPI = :HYCPI, HYCXD = :HYCXD, HYLRD = :HYLRD, HYFRQ = :HYFRQ, HYNRD = :HYNRD, HYNOU = :HYNOU, HYUNP = :HYUNP, HYCCY = :HYCCY, HYCLV = :HYCLV, HYSVM = :HYSVM, HYMCV = :HYMCV, HYBKV = :HYBKV, HYTOTA = :HYTOTA, HYISV = :HYISV, HYIXD = :HYIXD, HYNR1 = :HYNR1, HYNR2 = :HYNR2, HYNR3 = :HYNR3, HYNR4 = :HYNR4, HYDLM = :HYDLM");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where HYCUS =:HYCUS");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralitem);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",collateralitem.getId() ,collateralitem.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String HYCUS, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = HYCUS;
		parms[0][0] = PennantJavaUtil.getLabel("label_HYCUS")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}