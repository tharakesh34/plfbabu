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
 * FileName    		:  PresentmentHeaderServiceImpl.java                                                   * 	  
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
package com.pennant.backend.service.financemanagement.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.financemanagement.PresentmentHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.PresentmentHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>PresentmentHeader</b>.<br>
 */
public class PresentmentHeaderServiceImpl extends GenericService<PresentmentHeader> implements PresentmentHeaderService {
	private final static Logger logger = Logger.getLogger(PresentmentHeaderServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private PresentmentHeaderDAO presentmentHeaderDAO;


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
	 * @return the presentmentHeaderDAO
	 */
	public PresentmentHeaderDAO getPresentmentHeaderDAO() {
		return presentmentHeaderDAO;
	}
	/**
	 * @param presentmentHeaderDAO the presentmentHeaderDAO to set
	 */
	public void setPresentmentHeaderDAO(PresentmentHeaderDAO presentmentHeaderDAO) {
		this.presentmentHeaderDAO = presentmentHeaderDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * PresentmentHeader/PresentmentHeader_Temp by using PresentmentHeaderDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using PresentmentHeaderDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtPresentmentHeader by using
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

		PresentmentHeader presentmentHeader = (PresentmentHeader) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (presentmentHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (presentmentHeader.isNew()) {
			presentmentHeader.setId(Long.parseLong(getPresentmentHeaderDAO().save(presentmentHeader,tableType)));
			auditHeader.getAuditDetail().setModelData(presentmentHeader);
			auditHeader.setAuditReference(String.valueOf(presentmentHeader.getPresentmentID()));
		}else{
			getPresentmentHeaderDAO().update(presentmentHeader,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table PresentmentHeader by using PresentmentHeaderDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtPresentmentHeader by using
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
		
		PresentmentHeader presentmentHeader = (PresentmentHeader) auditHeader.getAuditDetail().getModelData();
		getPresentmentHeaderDAO().delete(presentmentHeader,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getPresentmentHeader fetch the details by using PresentmentHeaderDAO's getPresentmentHeaderById
	 * method.
	 * 
	 * @param presentmentID
	 *            presentmentID of the PresentmentHeader.
	 * @return PresentmentHeader
	 */
	@Override
	public PresentmentHeader getPresentmentHeader(long presentmentID) {
		return getPresentmentHeaderDAO().getPresentmentHeader(presentmentID,"_View");
	}

	/**
	 * getApprovedPresentmentHeaderById fetch the details by using PresentmentHeaderDAO's
	 * getPresentmentHeaderById method . with parameter id and type as blank. it fetches
	 * the approved records from the PresentmentHeader.
	 * 
	 * @param presentmentID
	 *            presentmentID of the PresentmentHeader.
	 *            (String)
	 * @return PresentmentHeader
	 */
	public PresentmentHeader getApprovedPresentmentHeader(long presentmentID) {
		return getPresentmentHeaderDAO().getPresentmentHeader(presentmentID,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getPresentmentHeaderDAO().delete with parameters presentmentHeader,"" b) NEW Add new
	 * record in to main table by using getPresentmentHeaderDAO().save with parameters
	 * presentmentHeader,"" c) EDIT Update record in the main table by using
	 * getPresentmentHeaderDAO().update with parameters presentmentHeader,"" 3) Delete the record
	 * from the workFlow table by using getPresentmentHeaderDAO().delete with parameters
	 * presentmentHeader,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtPresentmentHeader by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtPresentmentHeader by using
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

		PresentmentHeader presentmentHeader = new PresentmentHeader();
		BeanUtils.copyProperties((PresentmentHeader) auditHeader.getAuditDetail().getModelData(), presentmentHeader);

		getPresentmentHeaderDAO().delete(presentmentHeader, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(presentmentHeader.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(presentmentHeaderDAO.getPresentmentHeader(presentmentHeader.getPresentmentID(), ""));
		}

		if (presentmentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPresentmentHeaderDAO().delete(presentmentHeader, TableType.MAIN_TAB);
		} else {
			presentmentHeader.setRoleCode("");
			presentmentHeader.setNextRoleCode("");
			presentmentHeader.setTaskId("");
			presentmentHeader.setNextTaskId("");
			presentmentHeader.setWorkflowId(0);

			if (presentmentHeader.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				presentmentHeader.setRecordType("");
				getPresentmentHeaderDAO().save(presentmentHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				presentmentHeader.setRecordType("");
				getPresentmentHeaderDAO().update(presentmentHeader, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(presentmentHeader);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getPresentmentHeaderDAO().delete with parameters
		 * presentmentHeader,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtPresentmentHeader by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			PresentmentHeader presentmentHeader = (PresentmentHeader) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getPresentmentHeaderDAO().delete(presentmentHeader,TableType.TEMP_TAB);
			
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
		 * from getPresentmentHeaderDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
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