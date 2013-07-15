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
 * FileName    		:  DiaryNotesDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2011    														*
 *                                                                  						*
 * Modified Date    :  20-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.diarynotes.impl;

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
import com.pennant.backend.dao.diarynotes.DiaryNotesDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.diarynotes.DiaryNotes;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>DiaryNotes model</b> class.<br>
 * 
 */

public class DiaryNotesDAOImpl extends BasisNextidDaoImpl<DiaryNotes> implements DiaryNotesDAO {

	private static Logger logger = Logger.getLogger(DiaryNotesDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new DiaryNotes 
	 * @return DiaryNotes
	 */

	@Override
	public DiaryNotes getDiaryNotes() {
		logger.debug("Entering  getDiaryNotes()");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("DiaryNotes");
		DiaryNotes diaryNotes= new DiaryNotes();
		if (workFlowDetails!=null){
			diaryNotes.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving  getDiaryNotes()");
		return diaryNotes;
	}


	/**
	 * This method get the module from method getDiaryNotes() and set the new record flag as true and return DiaryNotes()   
	 * @return DiaryNotes
	 */


	@Override
	public DiaryNotes getNewDiaryNotes() {
		logger.debug("Entering  getNewDiaryNotes()");
		DiaryNotes diaryNotes = getDiaryNotes();
		diaryNotes.setNewRecord(true);
		logger.debug("Leaving getNewDiaryNotes()");
		return diaryNotes;
	}

	/**
	 * Fetch the Record  Diary Notes details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DiaryNotes
	 */
	@Override
	public DiaryNotes getDiaryNotesById(final long id, String type) {
		logger.debug("Entering");
		DiaryNotes diaryNotes = getDiaryNotes();
		diaryNotes.setId(id);
		
		StringBuilder   selectSql = new StringBuilder  ("Select SeqNo, DnType, DnCreatedNo, DnCreatedName, FrqCode, FirstActionDate, NextActionDate, ");
		selectSql.append("LastActionDate, FinalActionDate, Suspend, SuspendStartDate, SuspendEndDate, RecordDeleted, Narration");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}

		selectSql.append(" From DiaryNotes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SeqNo =:SeqNo  ");  //Added the RecordDeleted Condition 
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(diaryNotes);
		RowMapper<DiaryNotes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DiaryNotes.class);
		
		try{
			diaryNotes = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			diaryNotes = null;
		}
		logger.debug("Leaving getDiaryNotesByID()");
		return diaryNotes;
	}
	
	/**
	 * This method initialise the Record.
	 * @param DiaryNotes (diaryNotes)
 	 * @return DiaryNotes
	 */
	@Override
	public void initialize(DiaryNotes diaryNotes) {
		super.initialize(diaryNotes);
	}
	/**
	 * This method refresh the Record.
	 * @param DiaryNotes (diaryNotes)
 	 * @return void
	 */
	@Override
	public void refresh(DiaryNotes diaryNotes) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the DiaryNotes or DiaryNotes_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Diary Notes by key SeqNo
	 * 
	 * @param Diary Notes (diaryNotes)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(DiaryNotes diaryNotes,String type) {
		logger.debug("Entering delete Method");
		int recordCount = 0;
		StringBuilder deleteSql= new StringBuilder();
		
		if(!StringUtils.trimToEmpty(type).equalsIgnoreCase("")){
			 deleteSql.append("Delete From DiaryNotes");
			 deleteSql.append(StringUtils.trimToEmpty(type));
			 deleteSql.append(" Where SeqNo =:SeqNo");
		}else {
			deleteSql.append("Update DiaryNotes set RecordDeleted = '1' Where SeqNo =:SeqNo");
		}	
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(diaryNotes);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetail= getError("41003", diaryNotes.getId(), diaryNotes.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetail.getError()) {};
			}
		}catch(DataAccessException e){
			logger.debug("Error delete Method");
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving delete Method");
	}
	
	/**
	 * This method insert new Records into DiaryNotes or DiaryNotes_Temp.
	 * it fetches the available Sequence form SeqDiaryNotes by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Diary Notes 
	 * 
	 * @param Diary Notes (diaryNotes)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("unused")
	@Override
	public long save(DiaryNotes diaryNotes,String type) {
		logger.debug("Entering Save Method");
		if (diaryNotes.getId()==Long.MIN_VALUE){
			diaryNotes.setId(getNextidviewDAO().getNextId("SeqDiaryNotes"));
			logger.debug("get NextID:"+diaryNotes.getId());
		}

		DiaryNotes  existingDN = null;
		
		/*//Fecth the Data from the Main table
		if(diaryNotes.isRecordExist()){
			existingDN  = getDiaryNotesByID(diaryNotes.getId(),"");
		}
		*/
		
	
		StringBuilder   insertSql = new StringBuilder("Insert Into DiaryNotes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SeqNo, DnType, DnCreatedNo, DnCreatedName, FrqCode, FirstActionDate, NextActionDate, LastActionDate, FinalActionDate, Suspend, ");
		insertSql.append("SuspendStartDate, SuspendEndDate, RecordDeleted, Narration, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		insertSql.append("TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:SeqNo, :DnType, :DnCreatedNo, :DnCreatedName, :FrqCode, :FirstActionDate, :NextActionDate, :LastActionDate, :FinalActionDate, ");
		insertSql.append(":Suspend, :SuspendStartDate, :SuspendEndDate, :RecordDeleted, :Narration, :Version , :LastMntBy, :LastMntOn, :RecordStatus, ");
		insertSql.append(":RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());
		

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(diaryNotes);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving save Method");
		return diaryNotes.getId();
	}
	
	/**
	 * This method updates the Record DiaryNotes or DiaryNotes_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Diary Notes by key SeqNo and Version
	 * 
	 * @param Diary Notes (diaryNotes)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(DiaryNotes diaryNotes,String type) {
		int recordCount = 0;
		logger.debug("Entering Update Method");

		StringBuilder updateSql = new StringBuilder("Update DiaryNotes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SeqNo = :SeqNo, DnType = :DnType, DnCreatedNo = :DnCreatedNo, DnCreatedName = :DnCreatedName, FrqCode = :FrqCode, ");
		updateSql.append("FirstActionDate = :FirstActionDate, NextActionDate = :NextActionDate, LastActionDate = :LastActionDate, FinalActionDate = :FinalActionDate,");
		updateSql.append(" Suspend = :Suspend, SuspendStartDate = :SuspendStartDate, SuspendEndDate = :SuspendEndDate, RecordDeleted = :RecordDeleted, " );
		updateSql.append("Narration = :Narration, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType,");
		updateSql.append(" WorkflowId = :WorkflowId Where SeqNo =:SeqNo");

		
		//Commeneted the Above to break the Update for Next Action Date as this will be calculated by Automate Process
/*		String updateSql = 	"Update DiaryNotes" + StringUtils.trimToEmpty(type) + 
		" Set SeqNo = :SeqNo, DnType = :DnType, DnCreatedNo = :DnCreatedNo, DnCreatedName = :DnCreatedName, FrqCode = :FrqCode, FirstActionDate = :FirstActionDate, LastActionDate = :LastActionDate, FinalActionDate = :FinalActionDate, Suspend = :Suspend, SuspendStartDate = :SuspendStartDate, SuspendEndDate = :SuspendEndDate, RecordDeleted = :RecordDeleted, Narration = :Narration" +
		", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" +
		" Where SeqNo =:SeqNo";*/
		
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("insertSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(diaryNotes);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetail= getError("41004", diaryNotes.getId(), diaryNotes.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetail.getError()) {};
		}
		logger.debug("Leaving Update Method");
	}

	
	
	@SuppressWarnings("serial")
	public void updateForScheduled(DiaryNotes diaryNotes) {
		int recordCount = 0;
		logger.debug("Entering Update Method");
		
		String updateSql = 	"Update DiaryNotes_Temp"+ 
							" Set NextActionDate = :NextActionDate, LastActionDate = :LastActionDate" +
							" Where SeqNo =:SeqNo";
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(diaryNotes);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql, beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			String[] errParm ={PennantJavaUtil.getLabel("label_SeqNo"),String.valueOf(diaryNotes.getId())};
			//ErrorDetails errorDetails= getErrorDetailsDAO().getErrorDetail("41004", diaryNotes.getUserDetails().getUsrLanguage(),errParm);
			//throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving Update Method");
	}
	
	
	public void updateForSuspend() {
		logger.debug("Entering updateForSuspend Method");
			DiaryNotes diaryNotes = new DiaryNotes();
			String updateSql = 	"update diarynotes_temp set SUSPEND = 'N',suspendstartdate = NULL,suspendenddate=null  "+ 
								" where SUSPEND='1' AND	SUSPENDENDDATE = CURRENT_TIMESTAMP AND RECORDDELETED <> 'Y' ";
			
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(diaryNotes);
			this.namedParameterJdbcTemplate.update(updateSql, beanParameters);
		logger.debug("Leaving updateForSuspend Method");
	}
	
	public void updateForDelete() {
		logger.debug("Entering updateForDelete Method");
			DiaryNotes diaryNotes = new DiaryNotes();
			
			String updateSql = 	"update diarynotes_temp set RECORDDELETED = 1 where FinalActionDate < CURRENT_TIMESTAMP ";
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(diaryNotes);
			this.namedParameterJdbcTemplate.update(updateSql, beanParameters);
		logger.debug("Leaving updateForDelete Method");
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unused" })
	public List getDiaryNoteRecord() {		
		logger.debug("Entering getDiaryNoteRecord Method");
			DiaryNotes diaryNotes = new DiaryNotes();
			List rowTypes = null;
			String fetchListSql = 	"Select SeqNo, DnType, DnCreatedNo, DnCreatedName, FrqCode, FirstActionDate, NextActionDate, LastActionDate, FinalActionDate " +		
			" From DiaryNotes_temp Where  RecordDeleted!=1 AND firstactiondate <= CURRENT_TIMESTAMP  AND finalActionDate > CURRENT_TIMESTAMP "+
			" AND nextactiondate is null OR nextactiondate <= CURRENT_TIMESTAMP AND suspend != 1 OR suspendstartdate <=CURRENT_TIMESTAMP and "+
			" suspendenddate <= finalactiondate ";
		
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(diaryNotes);
			RowMapper<DiaryNotes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DiaryNotes.class);		
			boolean firstResult = false;
			try {
					Map<String,Object> namedParameters = new HashMap<String,Object>();		
					rowTypes = this.namedParameterJdbcTemplate.query(fetchListSql, beanParameters, typeRowMapper);
			    }catch(Exception e){
			    	e.printStackTrace();
			    }
			logger.debug("Leaving getDiaryNoteRecord Method");
	   return rowTypes;
	}


	@Override
	public ErrorDetails getErrorDetail(String errorId, String errorLanguage,
			String[] parameters) {
		// TODO Auto-generated method stub
		return null;
	}
	

	
	private ErrorDetails  getError(String errorId, long diaryNotesId, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = String.valueOf(diaryNotesId);
		parms[0][0] = PennantJavaUtil.getLabel("label_SeqNo")+":" +parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}