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
 * FileName    		:  ReasonCodeServiceImpl.java                                                   * 	  
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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.ReasonCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.ReasonCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;




/**
 * Service implementation for methods that depends on <b>ReasonCode</b>.<br>
 */
public class ReasonCodeServiceImpl extends GenericService<ReasonCode> implements ReasonCodeService {
	private static final  Logger logger = Logger.getLogger(ReasonCodeServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
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
	 * @return the reasonCodeDAO
	 */
	public ReasonCodeDAO getReasonCodeDAO() {
		return reasonCodeDAO;
	}
	/**
	 * @param reasonCodeDAO the reasonCodeDAO to set
	 */
	public void setReasonCodeDAO(ReasonCodeDAO reasonCodeDAO) {
		this.reasonCodeDAO = reasonCodeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * Reasons/Reasons_Temp by using ReasonsDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using ReasonsDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtReasons by using
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

		ReasonCode reasonCode = (ReasonCode) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (reasonCode.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (reasonCode.isNew()) {
			reasonCode.setId(Long.parseLong(getReasonCodeDAO().save(reasonCode,tableType)));
			auditHeader.getAuditDetail().setModelData(reasonCode);
			auditHeader.setAuditReference(String.valueOf(reasonCode.getId()));
		}else{
			getReasonCodeDAO().update(reasonCode,tableType);
		}

		//getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table Reasons by using ReasonsDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtReasons by using
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
		
		ReasonCode reasonCode = (ReasonCode) auditHeader.getAuditDetail().getModelData();
		getReasonCodeDAO().delete(reasonCode,TableType.MAIN_TAB);
		
		//getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getReasons fetch the details by using ReasonsDAO's getReasonsById
	 * method.
	 * 
	 * @param id
	 *            id of the ReasonCode.
	 * @return Reasons
	 */
	@Override
	public ReasonCode getReasonCode(long id) {
		return getReasonCodeDAO().getReasonCode(id,"_View");
	}

	/**
	 * getApprovedReasonsById fetch the details by using ReasonsDAO's
	 * getReasonsById method . with parameter id and type as blank. it fetches
	 * the approved records from the Reasons.
	 * 
	 * @param id
	 *            id of the ReasonCode.
	 *            (String)
	 * @return Reasons
	 */
	public ReasonCode getApprovedReasonCode(long id) {
		return getReasonCodeDAO().getReasonCode(id,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getReasonCodeDAO().delete with parameters reasonCode,"" b) NEW Add new
	 * record in to main table by using getReasonCodeDAO().save with parameters
	 * reasonCode,"" c) EDIT Update record in the main table by using
	 * getReasonCodeDAO().update with parameters reasonCode,"" 3) Delete the record
	 * from the workFlow table by using getReasonCodeDAO().delete with parameters
	 * reasonCode,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtReasons by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtReasons by using
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

		ReasonCode reasonCode = new ReasonCode();
		BeanUtils.copyProperties((ReasonCode) auditHeader.getAuditDetail().getModelData(), reasonCode);

		getReasonCodeDAO().delete(reasonCode, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(reasonCode.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(reasonCodeDAO.getReasonCode(reasonCode.getId(), ""));
		}

		if (reasonCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getReasonCodeDAO().delete(reasonCode, TableType.MAIN_TAB);
		} else {
			reasonCode.setRoleCode("");
			reasonCode.setNextRoleCode("");
			reasonCode.setTaskId("");
			reasonCode.setNextTaskId("");
			reasonCode.setWorkflowId(0);

			if (reasonCode.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				reasonCode.setRecordType("");
				getReasonCodeDAO().save(reasonCode, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				reasonCode.setRecordType("");
				getReasonCodeDAO().update(reasonCode, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		//getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(reasonCode);
		//getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getReasonCodeDAO().delete with parameters
		 * reasonCode,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtReasons by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			ReasonCode reasonCode = (ReasonCode) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getReasonCodeDAO().delete(reasonCode,TableType.TEMP_TAB);
			
			//getAuditHeaderDAO().addAudit(auditHeader);
			
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
		 * from getReasonCodeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Get the model object.
			ReasonCode reasonCode = (ReasonCode) auditDetail.getModelData();

			// Check the unique keys.
			if (reasonCode.isNew() && reasonCodeDAO.isDuplicateKey(reasonCode.getReasonTypeCode(),reasonCode.getReasonCategoryCode(),reasonCode.getCode(),
					reasonCode.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];
				
				parameters[0] = PennantJavaUtil.getLabel("label_ReasonTypeID") + ": " + reasonCode.getReasonTypeCode();
				parameters[1] = PennantJavaUtil.getLabel("label_ReasonCategoryID") + ": " + reasonCode.getReasonCategoryCode();
				parameters[2] = PennantJavaUtil.getLabel("label_Code") + ": " + reasonCode.getCode();

				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

}