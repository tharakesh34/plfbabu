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
 * FileName    		:  DocumentDetailsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2012    														*
 *                                                                  						*
 * Modified Date    :  21-06-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.documentdetails.impl;

import java.util.ArrayList;
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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>documentDetails model</b> class.<br>
 */
public class DocumentDetailsDAOImpl extends BasisNextidDaoImpl<DocumentDetails> implements DocumentDetailsDAO {

	private static Logger				logger	= Logger.getLogger(DocumentDetailsDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	/**
	 * Fetch the Record Channel Detail details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return documentDetails
	 */
	@Override
	public DocumentDetails getDocumentDetailsById(final long id, String type) {
		logger.debug("Entering");
		DocumentDetails documentDetails = new DocumentDetails();

		documentDetails.setId(id);

		StringBuilder selectSql = new StringBuilder("Select DocId,DocModule, DocCategory, " );
		selectSql.append(" Doctype,DocName,DocImage,ReferenceId , ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From DocumentDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DocId =:DocId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DocumentDetails.class);

		try {
			documentDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			documentDetails = null;
		}
		logger.debug("Leaving");
		return documentDetails;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the documentDetailss or
	 * documentDetailss_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Channel Detail by key
	 * ChannelId
	 * 
	 * @param Channel
	 *            Detail (documentDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(DocumentDetails documentDetails, String type) {
		logger.debug("Entering");

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder("Delete From DocumentDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DocId =:DocId ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",documentDetails.getDocModule() ,documentDetails.getDocName(),
						documentDetails.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",documentDetails.getDocModule() ,documentDetails.getDocName(),
					documentDetails.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Deletion List of Document Details in temp table 
	 * @param documentDetailList
	 * @param type
	 */
	public void deleteList(List<DocumentDetails> documentDetailList, String type) {
		logger.debug("Entering");
		
		StringBuilder deleteSql = new StringBuilder("Delete From DocumentDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DocId =:DocId ");
		logger.debug("deleteSql: " + deleteSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(documentDetailList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into documentDetailss or
	 * documentDetailss_Temp. it fetches the available Sequence form
	 * SeqdocumentDetailss by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Channel Detail
	 * 
	 * @param Channel
	 *            Detail (documentDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(DocumentDetails documentDetails, String type) {
		logger.debug("Entering");
		
		if(documentDetails.getDocId() == Long.MIN_VALUE){
			documentDetails.setDocId(getNextidviewDAO().getNextId("SeqDocumentDetails"));
		}

		StringBuilder insertSql = new StringBuilder("Insert Into DocumentDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" ( DocId, DocModule, DocCategory, Doctype,DocName,DocImage,ReferenceId");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:DocId,:DocModule, :DocCategory, :Doctype, :DocName, :DocImage,:ReferenceId");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return documentDetails.getId();
	}
	
	/**
	 * Method for Generation of Sequence ID
	 */
	public long generateDocSeq() {
		logger.debug("Entering");
		long docId = getNextidviewDAO().getNextId("SeqDocumentDetails");
		logger.debug("get NextID:" + docId);
		logger.debug("Leaving");
		return docId;
	}
	
	/**
	 * Method for Saving List Of Document Details
	 */
	public void saveList(ArrayList<DocumentDetails> docList, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into DocumentDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" ( DocId, DocModule, DocCategory, Doctype,DocName,DocImage,ReferenceId");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:DocId,:DocModule, :DocCategory, :Doctype, :DocName, :DocImage,:ReferenceId");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(docList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record documentDetailss or documentDetailss_Temp.
	 * if Record not updated then throws DataAccessException with error 41004.
	 * update Channel Detail by key ChannelId and Version
	 * 
	 * @param Channel
	 *            Detail (documentDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(DocumentDetails documentDetails, String type) {
		logger.debug("Entering");
		
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update DocumentDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DocModule=:DocModule, DocCategory=:DocCategory, Doctype=:Doctype,DocName=:DocName, " );
		updateSql.append(" DocImage=:DocImage,ReferenceId=:ReferenceId");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where DocId =:DocId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",documentDetails.getDocModule() ,documentDetails.getDocName(),
					documentDetails.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails getError(String errorId, String docModule,String docCategory, String userLanguage) {
		String[][] parms = new String[2][2];
		parms[1][0] = docModule;
		parms[1][1] = docCategory;
		parms[0][0] = PennantJavaUtil.getLabel("label_docModule") + ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_docName") + ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]), userLanguage);
	}

	@Override
    public List<DocumentDetails> getDocumentDetailsByRef(String ref, String type) {
		logger.debug("Entering");
		DocumentDetails documentDetails = new DocumentDetails();
		documentDetails.setReferenceId(ref);
		StringBuilder selectSql = new StringBuilder("Select DocId,DocModule, DocCategory, " );
		selectSql.append(" Doctype,DocName,DocImage,ReferenceId , ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From DocumentDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ReferenceId =:ReferenceId");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		RowMapper<DocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DocumentDetails.class);
	
		logger.debug("Leaving");
		return  this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }
 


}