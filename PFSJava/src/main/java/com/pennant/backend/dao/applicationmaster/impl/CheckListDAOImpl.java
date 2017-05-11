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
 * FileName    		:  CheckListDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.CheckListDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>CheckList model</b> class.<br>
 * 
 */
public class CheckListDAOImpl extends BasisNextidDaoImpl<CheckList> implements CheckListDAO {
	private static Logger logger = Logger.getLogger(CheckListDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public CheckListDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  Check List details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CheckList
	 */
	@Override
	public CheckList getCheckListById(final long id, String type) {
		logger.debug("Entering");
		
		CheckList checkList = new CheckList();
		checkList.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select CheckListId, CheckListDesc, CheckMinCount, CheckMaxCount, CheckRule, Active,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleName");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(", LovDescCheckRuleName ");
		}
		selectSql.append(" From BMTCheckList");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CheckListId =:CheckListId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkList);
		RowMapper<CheckList> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CheckList.class);
		
		try{
			checkList = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			checkList = null;
		}
		logger.debug("Leaving");
		return checkList;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the BMTCheckList or BMTCheckList_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Check List by key CheckListId
	 * 
	 * @param Check List (checkList)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(CheckList checkList, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From BMTCheckList");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CheckListId =:CheckListId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkList);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",checkList.getCheckListDesc() ,checkList.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006",checkList.getCheckListDesc() ,checkList.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into BMTCheckList or BMTCheckList_Temp.
	 * it fetches the available Sequence form SeqBMTCheckList by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Check List 
	 * 
	 * @param Check List (checkList)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(CheckList checkList,String type) {
		logger.debug("Entering");
		if (checkList.getId()==Long.MIN_VALUE){
			checkList.setId(getNextidviewDAO().getNextId("SeqBMTCheckList"));
			logger.debug("get NextID:"+checkList.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into BMTCheckList");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CheckListId, CheckListDesc, CheckMinCount, CheckMaxCount,CheckRule, Active, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, ModuleName)");
		insertSql.append(" Values(:CheckListId, :CheckListDesc, :CheckMinCount, :CheckMaxCount,:CheckRule, :Active, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :ModuleName)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkList);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return checkList.getId();
	}
	
	/**
	 * This method updates the Record BMTCheckList or BMTCheckList_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Check List by key CheckListId and Version
	 * 
	 * @param Check List (checkList)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(CheckList checkList, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update BMTCheckList");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set CheckListDesc = :CheckListDesc,");
		updateSql.append(" CheckMinCount = :CheckMinCount, CheckMaxCount = :CheckMaxCount,CheckRule=:CheckRule, Active = :Active, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, ModuleName = :ModuleName");
		updateSql.append(" Where CheckListId =:CheckListId");
		
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkList);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",checkList.getCheckListDesc() ,checkList.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	@Override
	public int getCheckListByRuleCode(String ruleCode, String type) {
		logger.debug("Entering");
		CheckList checkList = new CheckList();
		checkList.setCheckRule(ruleCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From BMTCheckList");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CheckRule =:CheckRule");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkList);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
	
	private ErrorDetails  getError(String errorId, String checkListDesc, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = checkListDesc;
		parms[0][0] = PennantJavaUtil.getLabel("label_CheckListDesc")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}