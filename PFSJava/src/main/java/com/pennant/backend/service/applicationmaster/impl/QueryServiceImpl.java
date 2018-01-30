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
 * FileName    		:  QueryServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-07-2013    														*
 *                                                                  						*
 * Modified Date    :  04-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-07-2013       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.QueryDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.QueryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Query</b>.<br>
 * 
 */
public class QueryServiceImpl extends GenericService<Query> implements QueryService {
	private static final Logger logger = Logger.getLogger(QueryServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private QueryDAO queryDAO;

	public QueryServiceImpl() {
		super();
	}
	
	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	
	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	/**
	 * @return the queryDAO
	 */
	public QueryDAO getQueryDAO() {
		return queryDAO;
	}
	/**
	 * @param queryDAO the queryDAO to set
	 */
	public void setQueryDAO(QueryDAO queryDAO) {
		this.queryDAO = queryDAO;
	}
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table Queries/Queries_Temp 
	 * 			by using QueryDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using QueryDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtQueries by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table Queries/Queries_Temp 
	 * 			by using QueryDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using QueryDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtQueries by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	 
		
	private AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean online) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		Query query = (Query) auditHeader.getAuditDetail().getModelData();
		
		if (query.isWorkflow()) {
			tableType="_Temp";
		}

		if (query.isNew()) {
			getQueryDAO().save(query,tableType);
		}else{
			getQueryDAO().update(query,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table Queries by using QueryDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtQueries by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete",false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		Query query = (Query) auditHeader.getAuditDetail().getModelData();
		getQueryDAO().delete(query,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getQueryById fetch the details by using QueryDAO's getQueryById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Query
	 */
	
	@Override
	public Query getQueryById(String queryCode, String queryModule) {
		return getQueryDAO().getQueryById(queryCode, queryModule,"_View");
	}
	/**
	 * getApprovedQueryById fetch the details by using QueryDAO's getQueryById method .
	 * with parameter id and type as blank. it fetches the approved records from the Queries.
	 * @param id (String)
	 * @return Query
	 */
	
	public Query getApprovedQueryById(String queryCode, String queryModule) {
		return getQueryDAO().getQueryById(queryCode, queryModule,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getQueryDAO().delete with parameters
	 * query,"" b) NEW Add new record in to main table by using getQueryDAO().save with parameters query,"" c) EDIT
	 * Update record in the main table by using getQueryDAO().update with parameters query,"" 3) Delete the record from
	 * the workFlow table by using getQueryDAO().delete with parameters query,"_Temp" 4) Audit the record in to
	 * AuditHeader and AdtQueries by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtQueries by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Query query = new Query("");
		BeanUtils.copyProperties((Query) auditHeader.getAuditDetail().getModelData(), query);

		if (query.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getQueryDAO().delete(query, "");

		} else {
			query.setRoleCode("");
			query.setNextRoleCode("");
			query.setTaskId("");
			query.setNextTaskId("");
			query.setWorkflowId(0);

			if (query.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				query.setRecordType("");
				getQueryDAO().save(query, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				query.setRecordType("");
				getQueryDAO().update(query, "");
			}
		}

		getQueryDAO().delete(query, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(query);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getQueryDAO().delete with parameters query,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtQueries by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doApprove",false);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			Query query = (Query) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getQueryDAO().delete(query,"_Temp");
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");
			
			return auditHeader;
		}

		/**
		 * businessValidation method do the following steps.
		 * 1)	validate the audit detail 
		 * 2)	if any error/Warnings  then assign the to auditHeader
		 * 3)   identify the nextprocess
		 *  
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */

		
		private AuditHeader businessValidation(AuditHeader auditHeader, String method,boolean onlineRequest){
			logger.debug("Entering");
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,onlineRequest);
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);
			logger.debug("Leaving");
			return auditHeader;
		}

		/**
		 * Validation method do the following steps.
		 * 1)	get the details from the auditHeader. 
		 * 2)	fetch the details from the tables
		 * 3)	Validate the Record based on the record details. 
		 * 4) 	Validate for any business validation.
		 * 5)	for any mismatch conditions Fetch the error details from getQueryDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			Query query= (Query) auditDetail.getModelData();
			
			Query tempQuery= null;
			if (query.isWorkflow()){
				tempQuery = getQueryDAO().getQueryById(query.getQueryCode(),query.getQueryModule(), "_Temp");
			}
			Query befQuery= getQueryDAO().getQueryById(query.getQueryCode(),query.getQueryModule(), "");
			
			Query oldQuery= query.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=query.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_QueryCode")+":"+valueParm[0];
			
			if (query.isNew()){ // for New record or new record into work flow
				
				if (!query.isWorkflow()){// With out Work flow only new records  
					if (befQuery !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
					}	
				}else{ // with work flow
					if (query.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befQuery !=null || tempQuery!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
						}
					}else{ // if records not exists in the Main flow table
						if (befQuery ==null || tempQuery!=null ){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!query.isWorkflow()){	// With out Work flow for update and delete
				
					if (befQuery ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm));
					}else{
						if (oldQuery!=null && !oldQuery.getLastMntOn().equals(befQuery.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm));
							}else{
								auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm));
							}
						}
					}
				}else{
				
					if (tempQuery==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
					
					if (tempQuery!=null  && oldQuery!=null && !oldQuery.getLastMntOn().equals(tempQuery.getLastMntOn())){ 
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !query.isWorkflow()){
				auditDetail.setBefImage(befQuery);	
			}

			return auditDetail;
		}

}