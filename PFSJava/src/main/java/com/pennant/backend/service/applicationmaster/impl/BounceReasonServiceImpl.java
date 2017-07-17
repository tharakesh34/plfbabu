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
 * FileName    		:  BounceReasonServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BounceReasonService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>BounceReason</b>.<br>
 */
public class BounceReasonServiceImpl extends GenericService<BounceReason> implements BounceReasonService {
	private static final Logger logger = Logger.getLogger(BounceReasonServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private BounceReasonDAO bounceReasonDAO;


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
	 * @return the bounceReasonDAO
	 */
	public BounceReasonDAO getBounceReasonDAO() {
		return bounceReasonDAO;
	}
	/**
	 * @param bounceReasonDAO the bounceReasonDAO to set
	 */
	public void setBounceReasonDAO(BounceReasonDAO bounceReasonDAO) {
		this.bounceReasonDAO = bounceReasonDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BounceReasons/BounceReasons_Temp by using BounceReasonsDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using BounceReasonsDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBounceReasons by using
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

		BounceReason bounceReason = (BounceReason) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (bounceReason.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (bounceReason.isNew()) {
			bounceReason.setId(Long.parseLong(getBounceReasonDAO().save(bounceReason,tableType)));
			auditHeader.getAuditDetail().setModelData(bounceReason);
			auditHeader.setAuditReference(String.valueOf(bounceReason.getBounceID()));
		}else{
			getBounceReasonDAO().update(bounceReason,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BounceReasons by using BounceReasonsDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBounceReasons by using
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
		
		BounceReason bounceReason = (BounceReason) auditHeader.getAuditDetail().getModelData();
		getBounceReasonDAO().delete(bounceReason,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getBounceReasons fetch the details by using BounceReasonsDAO's getBounceReasonsById
	 * method.
	 * 
	 * @param bounceID
	 *            bounceID of the BounceReason.
	 * @return BounceReasons
	 */
	@Override
	public BounceReason getBounceReason(long bounceID) {
		return getBounceReasonDAO().getBounceReason(bounceID,"_View");
	}

	/**
	 * getApprovedBounceReasonsById fetch the details by using BounceReasonsDAO's
	 * getBounceReasonsById method . with parameter id and type as blank. it fetches
	 * the approved records from the BounceReasons.
	 * 
	 * @param bounceID
	 *            bounceID of the BounceReason.
	 *            (String)
	 * @return BounceReasons
	 */
	public BounceReason getApprovedBounceReason(long bounceID) {
		return getBounceReasonDAO().getBounceReason(bounceID,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getBounceReasonDAO().delete with parameters bounceReason,"" b) NEW Add new
	 * record in to main table by using getBounceReasonDAO().save with parameters
	 * bounceReason,"" c) EDIT Update record in the main table by using
	 * getBounceReasonDAO().update with parameters bounceReason,"" 3) Delete the record
	 * from the workFlow table by using getBounceReasonDAO().delete with parameters
	 * bounceReason,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBounceReasons by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBounceReasons by using
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

		BounceReason bounceReason = new BounceReason();
		BeanUtils.copyProperties((BounceReason) auditHeader.getAuditDetail().getModelData(), bounceReason);

		getBounceReasonDAO().delete(bounceReason, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(bounceReason.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(bounceReasonDAO.getBounceReason(bounceReason.getBounceID(), ""));
		}

		if (bounceReason.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getBounceReasonDAO().delete(bounceReason, TableType.MAIN_TAB);
		} else {
			bounceReason.setRoleCode("");
			bounceReason.setNextRoleCode("");
			bounceReason.setTaskId("");
			bounceReason.setNextTaskId("");
			bounceReason.setWorkflowId(0);

			if (bounceReason.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				bounceReason.setRecordType("");
				getBounceReasonDAO().save(bounceReason, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				bounceReason.setRecordType("");
				getBounceReasonDAO().update(bounceReason, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(bounceReason);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getBounceReasonDAO().delete with parameters
		 * bounceReason,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtBounceReasons by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			BounceReason bounceReason = (BounceReason) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getBounceReasonDAO().delete(bounceReason,TableType.TEMP_TAB);
			
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
		 * from getBounceReasonDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Get the model object.
			BounceReason bounceReason = (BounceReason) auditDetail.getModelData();

			// Check the unique keys.
			if (bounceReason.isNew() && bounceReasonDAO.isDuplicateKey(bounceReason.getBounceID(),bounceReason.getBounceCode(),
					bounceReason.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];
				
				parameters[0] = PennantJavaUtil.getLabel("label_BounceCode") + ": " + bounceReason.getBounceCode();

				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

}