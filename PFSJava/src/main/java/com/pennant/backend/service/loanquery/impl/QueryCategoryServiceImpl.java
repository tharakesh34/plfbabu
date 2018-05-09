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
 * FileName    		:  QueryCategoryServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-05-2018    														*
 *                                                                  						*
 * Modified Date    :  08-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-05-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.loanquery.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.loanquery.QueryCategoryDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.loanquery.QueryCategory;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.loanquery.QueryCategoryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>QueryCategory</b>.<br>
 */
public class QueryCategoryServiceImpl extends GenericService<QueryCategory> implements QueryCategoryService {
	private static final  Logger logger = Logger.getLogger(QueryCategoryServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private QueryCategoryDAO queryCategoryDAO;


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
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
	 * @return the queryCategoryDAO
	 */
	public QueryCategoryDAO getQueryCategoryDAO() {
		return queryCategoryDAO;
	}
	/**
	 * @param queryCategoryDAO the queryCategoryDAO to set
	 */
	public void setQueryCategoryDAO(QueryCategoryDAO queryCategoryDAO) {
		this.queryCategoryDAO = queryCategoryDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTQueryCategories/BMTQueryCategories_Temp by using BMTQueryCategoriesDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using BMTQueryCategoriesDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTQueryCategories by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);	
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		QueryCategory queryCategory = (QueryCategory) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (queryCategory.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (queryCategory.isNew()) {
			queryCategory.setId(Long.parseLong(getQueryCategoryDAO().save(queryCategory,tableType)));
			auditHeader.getAuditDetail().setModelData(queryCategory);
			auditHeader.setAuditReference(String.valueOf(queryCategory.getId()));
		}else{
			getQueryCategoryDAO().update(queryCategory,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTQueryCategories by using BMTQueryCategoriesDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTQueryCategories by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		
		QueryCategory queryCategory = (QueryCategory) auditHeader.getAuditDetail().getModelData();
		getQueryCategoryDAO().delete(queryCategory,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getBMTQueryCategories fetch the details by using BMTQueryCategoriesDAO's getBMTQueryCategoriesById
	 * method.
	 * 
	 * @param id
	 *            id of the QueryCategory.
	 * @return BMTQueryCategories
	 */
	@Override
	public QueryCategory getQueryCategory(long id) {
		return getQueryCategoryDAO().getQueryCategory(id,"_View");
	}

	/**
	 * getApprovedBMTQueryCategoriesById fetch the details by using BMTQueryCategoriesDAO's
	 * getBMTQueryCategoriesById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTQueryCategories.
	 * 
	 * @param id
	 *            id of the QueryCategory.
	 *            (String)
	 * @return BMTQueryCategories
	 */
	public QueryCategory getApprovedQueryCategory(long id) {
		return getQueryCategoryDAO().getQueryCategory(id,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getQueryCategoryDAO().delete with parameters queryCategory,"" b) NEW Add new
	 * record in to main table by using getQueryCategoryDAO().save with parameters
	 * queryCategory,"" c) EDIT Update record in the main table by using
	 * getQueryCategoryDAO().update with parameters queryCategory,"" 3) Delete the record
	 * from the workFlow table by using getQueryCategoryDAO().delete with parameters
	 * queryCategory,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTQueryCategories by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTQueryCategories by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		QueryCategory queryCategory = new QueryCategory();
		BeanUtils.copyProperties((QueryCategory) auditHeader.getAuditDetail().getModelData(), queryCategory);

		getQueryCategoryDAO().delete(queryCategory, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(queryCategory.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(queryCategoryDAO.getQueryCategory(queryCategory.getId(), ""));
		}

		if (queryCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getQueryCategoryDAO().delete(queryCategory, TableType.MAIN_TAB);
		} else {
			queryCategory.setRoleCode("");
			queryCategory.setNextRoleCode("");
			queryCategory.setTaskId("");
			queryCategory.setNextTaskId("");
			queryCategory.setWorkflowId(0);

			if (queryCategory.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				queryCategory.setRecordType("");
				getQueryCategoryDAO().save(queryCategory, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				queryCategory.setRecordType("");
				getQueryCategoryDAO().update(queryCategory, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(queryCategory);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getQueryCategoryDAO().delete with parameters
		 * queryCategory,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtBMTQueryCategories by using auditHeaderDAO.addAudit(auditHeader) for Work
		 * flow
		 * 
		 * @param AuditHeader
		 *            (auditHeader)
		 * @return auditHeader
		 */
		@Override
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.info(Literal.ENTERING);
			
			auditHeader = businessValidation(auditHeader,"doApprove");
			if (!auditHeader.isNextProcess()) {
				logger.info(Literal.LEAVING);
				return auditHeader;
			}

			QueryCategory queryCategory = (QueryCategory) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getQueryCategoryDAO().delete(queryCategory,TableType.TEMP_TAB);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		/**
		 * businessValidation method do the following steps. 1) get the details from
		 * the auditHeader. 2) fetch the details from the tables 3) Validate the
		 * Record based on the record details. 4) Validate for any business
		 * validation.
		 * 
		 * @param AuditHeader
		 *            (auditHeader)
		 * @return auditHeader
		 */
		private AuditHeader businessValidation(AuditHeader auditHeader, String method){
			logger.debug(Literal.ENTERING);
			
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);

			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		/**
		 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
		 * from getQueryCategoryDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Get the model object.
			QueryCategory queryCategory = (QueryCategory) auditDetail.getModelData();

			// Check the unique keys.
			if (queryCategory.isNew() && queryCategoryDAO.isDuplicateKey(queryCategory.getId(),queryCategory.getCode(),
					queryCategory.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];
				
				parameters[0] = PennantJavaUtil.getLabel("label_Code") + ": " + queryCategory.getCode();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

}