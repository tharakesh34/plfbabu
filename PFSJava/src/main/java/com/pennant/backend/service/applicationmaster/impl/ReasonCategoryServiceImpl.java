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
 * FileName    		:  ReasonCategoryServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2017    														*
 *                                                                  						*
 * Modified Date    :  19-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2017       PENNANT	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.ReasonCategoryDAO;
import com.pennant.backend.dao.applicationmaster.ReasonCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.ReasonCategory;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.ReasonCategoryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;



/**
 * Service implementation for methods that depends on <b>ReasonCategory</b>.<br>
 */
public class ReasonCategoryServiceImpl extends GenericService<ReasonCategory> implements ReasonCategoryService {
	private static final  Logger logger = Logger.getLogger(ReasonCategoryServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private ReasonCategoryDAO reasonCategoryDAO;
	private ReasonCodeDAO reasonCodeDAO;


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
	 * @return the reasonCategoryDAO
	 */
	public ReasonCategoryDAO getReasonCategoryDAO() {
		return reasonCategoryDAO;
	}
	/**
	 * @param reasonCategoryDAO the reasonCategoryDAO to set
	 */
	public void setReasonCategoryDAO(ReasonCategoryDAO reasonCategoryDAO) {
		this.reasonCategoryDAO = reasonCategoryDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * ReasonCategory/ReasonCategory_Temp by using ReasonCategoryDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using ReasonCategoryDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtReasonCategory by using
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

		ReasonCategory reasonCategory = (ReasonCategory) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (reasonCategory.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (reasonCategory.isNew()) {
			reasonCategory.setId(Long.parseLong(getReasonCategoryDAO().save(reasonCategory,tableType)));
			auditHeader.getAuditDetail().setModelData(reasonCategory);
			auditHeader.setAuditReference(String.valueOf(reasonCategory.getId()));
		}else{
			getReasonCategoryDAO().update(reasonCategory,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table ReasonCategory by using ReasonCategoryDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtReasonCategory by using
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
		
		ReasonCategory reasonCategory = (ReasonCategory) auditHeader.getAuditDetail().getModelData();
		getReasonCategoryDAO().delete(reasonCategory,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getReasonCategory fetch the details by using ReasonCategoryDAO's getReasonCategoryById
	 * method.
	 * 
	 * @param id
	 *            id of the ReasonCategory.
	 * @return ReasonCategory
	 */
	@Override
	public ReasonCategory getReasonCategory(long id) {
		return getReasonCategoryDAO().getReasonCategory(id,"_View");
	}

	/**
	 * getApprovedReasonCategoryById fetch the details by using ReasonCategoryDAO's
	 * getReasonCategoryById method . with parameter id and type as blank. it fetches
	 * the approved records from the ReasonCategory.
	 * 
	 * @param id
	 *            id of the ReasonCategory.
	 *            (String)
	 * @return ReasonCategory
	 */
	public ReasonCategory getApprovedReasonCategory(long id) {
		return getReasonCategoryDAO().getReasonCategory(id,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getReasonCategoryDAO().delete with parameters reasonCategory,"" b) NEW Add new
	 * record in to main table by using getReasonCategoryDAO().save with parameters
	 * reasonCategory,"" c) EDIT Update record in the main table by using
	 * getReasonCategoryDAO().update with parameters reasonCategory,"" 3) Delete the record
	 * from the workFlow table by using getReasonCategoryDAO().delete with parameters
	 * reasonCategory,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtReasonCategory by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtReasonCategory by using
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

		ReasonCategory reasonCategory = new ReasonCategory();
		BeanUtils.copyProperties((ReasonCategory) auditHeader.getAuditDetail().getModelData(), reasonCategory);

		getReasonCategoryDAO().delete(reasonCategory, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(reasonCategory.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(reasonCategoryDAO.getReasonCategory(reasonCategory.getId(), ""));
		}

		if (reasonCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getReasonCategoryDAO().delete(reasonCategory, TableType.MAIN_TAB);
		} else {
			reasonCategory.setRoleCode("");
			reasonCategory.setNextRoleCode("");
			reasonCategory.setTaskId("");
			reasonCategory.setNextTaskId("");
			reasonCategory.setWorkflowId(0);

			if (reasonCategory.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				reasonCategory.setRecordType("");
				getReasonCategoryDAO().save(reasonCategory, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				reasonCategory.setRecordType("");
				getReasonCategoryDAO().update(reasonCategory, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(reasonCategory);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getReasonCategoryDAO().delete with parameters
		 * reasonCategory,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtReasonCategory by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			ReasonCategory reasonCategory = (ReasonCategory) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getReasonCategoryDAO().delete(reasonCategory,TableType.TEMP_TAB);
			
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
		 * from getReasonCategoryDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		ReasonCategory reasonCategory = (ReasonCategory) auditDetail.getModelData();

		// Check the unique keys.
		if (reasonCategory.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(reasonCategory.getRecordType())
				&& reasonCategoryDAO.isDuplicateKey(reasonCategory.getCode().trim(),
						reasonCategory.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_Code") + ": " + reasonCategory.getCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		// If ReasonCategory Code is already utilized in ReasonCode
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, reasonCategory.getRecordType())) {
			boolean workflowExists = getReasonCodeDAO().isreasonCategoryIDExists(reasonCategory.getId());
			if (workflowExists) {

				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_Code") + ": " + reasonCategory.getCode();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

		public ReasonCodeDAO getReasonCodeDAO() {
			return reasonCodeDAO;
		}

		public void setReasonCodeDAO(ReasonCodeDAO reasonCodeDAO) {
			this.reasonCodeDAO = reasonCodeDAO;
		}

}