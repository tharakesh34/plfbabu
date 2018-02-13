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
 * FileName    		:  PinCodeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-06-2017    														*
 *                                                                  						*
 * Modified Date    :  01-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-06-2017       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.PinCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>PinCode</b>.<br>
 */
public class PinCodeServiceImpl extends GenericService<PinCode> implements PinCodeService {
	private static final Logger logger = Logger.getLogger(PinCodeServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private PinCodeDAO pinCodeDAO;
	private BranchDAO branchDAO;


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
	 * @return the pinCodeDAO
	 */
	public PinCodeDAO getPinCodeDAO() {
		return pinCodeDAO;
	}
	/**
	 * @param pinCodeDAO the pinCodeDAO to set
	 */
	public void setPinCodeDAO(PinCodeDAO pinCodeDAO) {
		this.pinCodeDAO = pinCodeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * PinCodes/PinCodes_Temp by using PinCodesDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using PinCodesDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtPinCodes by using
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

		PinCode pinCode = (PinCode) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (pinCode.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (pinCode.isNew()) {
			pinCode.setId(Long.parseLong(getPinCodeDAO().save(pinCode,tableType)));
			auditHeader.getAuditDetail().setModelData(pinCode);
			auditHeader.setAuditReference(String.valueOf(pinCode.getPinCodeId()));
		}else{
			getPinCodeDAO().update(pinCode,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table PinCodes by using PinCodesDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtPinCodes by using
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
		
		PinCode pinCode = (PinCode) auditHeader.getAuditDetail().getModelData();
		getPinCodeDAO().delete(pinCode,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getPinCodes fetch the details by using PinCodesDAO's getPinCodesById
	 * method.
	 * 
	 * @param pinCodeId
	 *            pinCodeId of the PinCode.
	 * @return PinCodes
	 */
	@Override
	public PinCode getPinCode(long pinCodeId) {
		return getPinCodeDAO().getPinCode(pinCodeId,"_View");
	}

	/**
	 * getApprovedPinCodesById fetch the details by using PinCodesDAO's
	 * getPinCodesById method . with parameter id and type as blank. it fetches
	 * the approved records from the PinCodes.
	 * 
	 * @param pinCodeId
	 *            pinCodeId of the PinCode.
	 *            (String)
	 * @return PinCodes
	 */
	public PinCode getApprovedPinCode(long pinCodeId) {
		return getPinCodeDAO().getPinCode(pinCodeId,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getPinCodeDAO().delete with parameters pinCode,"" b) NEW Add new
	 * record in to main table by using getPinCodeDAO().save with parameters
	 * pinCode,"" c) EDIT Update record in the main table by using
	 * getPinCodeDAO().update with parameters pinCode,"" 3) Delete the record
	 * from the workFlow table by using getPinCodeDAO().delete with parameters
	 * pinCode,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtPinCodes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtPinCodes by using
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

		PinCode pinCode = new PinCode();
		BeanUtils.copyProperties((PinCode) auditHeader.getAuditDetail().getModelData(), pinCode);

		getPinCodeDAO().delete(pinCode, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(pinCode.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(pinCodeDAO.getPinCode(pinCode.getPinCodeId(), ""));
		}

		if (pinCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPinCodeDAO().delete(pinCode, TableType.MAIN_TAB);
		} else {
			pinCode.setRoleCode("");
			pinCode.setNextRoleCode("");
			pinCode.setTaskId("");
			pinCode.setNextTaskId("");
			pinCode.setWorkflowId(0);

			if (pinCode.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				pinCode.setRecordType("");
				getPinCodeDAO().save(pinCode, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				pinCode.setRecordType("");
				getPinCodeDAO().update(pinCode, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(pinCode);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getPinCodeDAO().delete with parameters
		 * pinCode,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtPinCodes by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			PinCode pinCode = (PinCode) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getPinCodeDAO().delete(pinCode,TableType.TEMP_TAB);
			
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
		 * from getPinCodeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Get the model object.
			PinCode pinCode = (PinCode) auditDetail.getModelData();

			// Check the unique keys.
			if (pinCode.isNew() && pinCodeDAO.isDuplicateKey(pinCode.getPinCodeId(),pinCode.getPinCode(),
					pinCode.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];
				
				parameters[0] = PennantJavaUtil.getLabel("label_PinCode") + ": " + pinCode.getPinCode();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
			
		// If PIN Code is already utilized in Branches 
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, pinCode.getRecordType())) {
			boolean workflowExists = getBranchDAO().isPinCodeExists(pinCode.getPinCode());
			if (workflowExists) {

				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_PinCode") + ": " + pinCode.getPinCode();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));
			}
		}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

		public BranchDAO getBranchDAO() {
			return branchDAO;
		}

		public void setBranchDAO(BranchDAO branchDAO) {
			this.branchDAO = branchDAO;
		}

}