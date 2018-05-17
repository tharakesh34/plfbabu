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
 * FileName    		:  QueryDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-05-2018    														*
 *                                                                  						*
 * Modified Date    :  09-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-05-2018       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.loanquery.QueryDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>QueryDetail</b>.<br>
 */
public class QueryDetailServiceImpl extends GenericService<QueryDetail> implements QueryDetailService {
	private static final  Logger logger = Logger.getLogger(QueryDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private QueryDetailDAO queryDetailDAO;


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
	 * @return the queryDetailDAO
	 */
	public QueryDetailDAO getQueryDetailDAO() {
		return queryDetailDAO;
	}
	/**
	 * @param queryDetailDAO the queryDetailDAO to set
	 */
	public void setQueryDetailDAO(QueryDetailDAO queryDetailDAO) {
		this.queryDetailDAO = queryDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * QUERYDETAIL/QUERYDETAIL_Temp by using QUERYDETAILDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using QUERYDETAILDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtQUERYDETAIL by using
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

		QueryDetail queryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (queryDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (queryDetail.isNew()) {
			queryDetail.setId(Long.parseLong(getQueryDetailDAO().save(queryDetail,tableType)));
			auditHeader.getAuditDetail().setModelData(queryDetail);
			auditHeader.setAuditReference(String.valueOf(queryDetail.getId()));
		}else{
			getQueryDetailDAO().update(queryDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table QUERYDETAIL by using QUERYDETAILDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtQUERYDETAIL by using
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
		
		QueryDetail queryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();
		getQueryDetailDAO().delete(queryDetail,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getQUERYDETAIL fetch the details by using QUERYDETAILDAO's getQUERYDETAILById
	 * method.
	 * 
	 * @param id
	 *            id of the QueryDetail.
	 * @return QUERYDETAIL
	 */
	@Override
	public QueryDetail getQueryDetail(long id) {
		return getQueryDetailDAO().getQueryDetail(id,"_View");
	}

	/**
	 * getApprovedQUERYDETAILById fetch the details by using QUERYDETAILDAO's
	 * getQUERYDETAILById method . with parameter id and type as blank. it fetches
	 * the approved records from the QUERYDETAIL.
	 * 
	 * @param id
	 *            id of the QueryDetail.
	 *            (String)
	 * @return QUERYDETAIL
	 */
	public QueryDetail getApprovedQueryDetail(long id) {
		return getQueryDetailDAO().getQueryDetail(id,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getQueryDetailDAO().delete with parameters queryDetail,"" b) NEW Add new
	 * record in to main table by using getQueryDetailDAO().save with parameters
	 * queryDetail,"" c) EDIT Update record in the main table by using
	 * getQueryDetailDAO().update with parameters queryDetail,"" 3) Delete the record
	 * from the workFlow table by using getQueryDetailDAO().delete with parameters
	 * queryDetail,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtQUERYDETAIL by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtQUERYDETAIL by using
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

		QueryDetail queryDetail = new QueryDetail();
		BeanUtils.copyProperties((QueryDetail) auditHeader.getAuditDetail().getModelData(), queryDetail);

		getQueryDetailDAO().delete(queryDetail, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(queryDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(queryDetailDAO.getQueryDetail(queryDetail.getId(), ""));
		}

		if (queryDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getQueryDetailDAO().delete(queryDetail, TableType.MAIN_TAB);
		} else {
			queryDetail.setRoleCode("");
			queryDetail.setNextRoleCode("");
			queryDetail.setTaskId("");
			queryDetail.setNextTaskId("");
			queryDetail.setWorkflowId(0);

			if (queryDetail.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				queryDetail.setRecordType("");
				getQueryDetailDAO().save(queryDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				queryDetail.setRecordType("");
				getQueryDetailDAO().update(queryDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(queryDetail);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getQueryDetailDAO().delete with parameters
		 * queryDetail,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtQUERYDETAIL by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			QueryDetail queryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getQueryDetailDAO().delete(queryDetail,TableType.TEMP_TAB);
			
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
		 * from getQueryDetailDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Write the required validation over hear.
			
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

}