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
 * FileName    		:  ManualAdviseServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.finance.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>ManualAdvise</b>.<br>
 */
public class ManualAdviseServiceImpl extends GenericService<ManualAdvise> implements ManualAdviseService {
	private static final Logger logger = Logger.getLogger(ManualAdviseServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private ManualAdviseDAO manualAdviseDAO;


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
	 * @return the manualAdviseDAO
	 */
	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}
	/**
	 * @param manualAdviseDAO the manualAdviseDAO to set
	 */
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * ManualAdvise/ManualAdvise_Temp by using ManualAdviseDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using ManualAdviseDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtManualAdvise by using
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

		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (manualAdvise.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (manualAdvise.isNew()) {
			manualAdvise.setId(Long.parseLong(getManualAdviseDAO().save(manualAdvise,tableType)));
			auditHeader.getAuditDetail().setModelData(manualAdvise);
			auditHeader.setAuditReference(String.valueOf(manualAdvise.getAdviseID()));
		}else{
			getManualAdviseDAO().update(manualAdvise,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table ManualAdvise by using ManualAdviseDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtManualAdvise by using
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
		
		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
		getManualAdviseDAO().delete(manualAdvise,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getManualAdvise fetch the details by using ManualAdviseDAO's getManualAdviseById
	 * method.
	 * 
	 * @param adviseID
	 *            adviseID of the ManualAdvise.
	 * @return ManualAdvise
	 */
	@Override
	public ManualAdvise getManualAdviseById(long adviseID) {
		return getManualAdviseDAO().getManualAdviseById(adviseID,"_View");
	}

	/**
	 * getApprovedManualAdviseById fetch the details by using ManualAdviseDAO's
	 * getManualAdviseById method . with parameter id and type as blank. it fetches
	 * the approved records from the ManualAdvise.
	 * 
	 * @param adviseID
	 *            adviseID of the ManualAdvise.
	 *            (String)
	 * @return ManualAdvise
	 */
	public ManualAdvise getApprovedManualAdvise(long adviseID) {
		return getManualAdviseDAO().getManualAdviseById(adviseID,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getManualAdviseDAO().delete with parameters manualAdvise,"" b) NEW Add new
	 * record in to main table by using getManualAdviseDAO().save with parameters
	 * manualAdvise,"" c) EDIT Update record in the main table by using
	 * getManualAdviseDAO().update with parameters manualAdvise,"" 3) Delete the record
	 * from the workFlow table by using getManualAdviseDAO().delete with parameters
	 * manualAdvise,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtManualAdvise by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtManualAdvise by using
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

		ManualAdvise manualAdvise = new ManualAdvise();
		BeanUtils.copyProperties((ManualAdvise) auditHeader.getAuditDetail().getModelData(), manualAdvise);

		getManualAdviseDAO().delete(manualAdvise, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(manualAdvise.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(manualAdviseDAO.getManualAdviseById(manualAdvise.getAdviseID(), ""));
		}

		if (manualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getManualAdviseDAO().delete(manualAdvise, TableType.MAIN_TAB);
		} else {
			manualAdvise.setRoleCode("");
			manualAdvise.setNextRoleCode("");
			manualAdvise.setTaskId("");
			manualAdvise.setNextTaskId("");
			manualAdvise.setWorkflowId(0);

			if (manualAdvise.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				manualAdvise.setRecordType("");
				getManualAdviseDAO().save(manualAdvise, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				manualAdvise.setRecordType("");
				getManualAdviseDAO().update(manualAdvise, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(manualAdvise);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getManualAdviseDAO().delete with parameters
		 * manualAdvise,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtManualAdvise by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getManualAdviseDAO().delete(manualAdvise,TableType.TEMP_TAB);
			
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
		 * from getManualAdviseDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
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

		@Override
		public List<ManualAdviseMovements> getAdivseMovements(long id) {
			return getManualAdviseDAO().getAdviseMovements(id);
		}

}