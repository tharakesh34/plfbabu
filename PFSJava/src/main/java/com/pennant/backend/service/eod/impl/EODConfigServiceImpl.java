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
 * FileName    		:  EODConfigServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-05-2017    														*
 *                                                                  						*
 * Modified Date    :  24-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.eod.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.eod.EODConfigService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>EODConfig</b>.<br>
 */
public class EODConfigServiceImpl extends GenericService<EODConfig> implements EODConfigService {
	private static final Logger logger = Logger.getLogger(EODConfigServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private EODConfigDAO eODConfigDAO;


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
	 * @return the eODConfigDAO
	 */
	public EODConfigDAO getEODConfigDAO() {
		return eODConfigDAO;
	}
	/**
	 * @param eODConfigDAO the eODConfigDAO to set
	 */
	public void setEODConfigDAO(EODConfigDAO eODConfigDAO) {
		this.eODConfigDAO = eODConfigDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * EodConfig/EodConfig_Temp by using EodConfigDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using EodConfigDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtEodConfig by using
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

		EODConfig eODConfig = (EODConfig) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (eODConfig.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (eODConfig.isNew()) {
			eODConfig.setId(Long.parseLong(getEODConfigDAO().save(eODConfig,tableType)));
			auditHeader.getAuditDetail().setModelData(eODConfig);
			auditHeader.setAuditReference(String.valueOf(eODConfig.getEodConfigId()));
		}else{
			getEODConfigDAO().update(eODConfig,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table EodConfig by using EodConfigDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtEodConfig by using
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
		
		EODConfig eODConfig = (EODConfig) auditHeader.getAuditDetail().getModelData();
		getEODConfigDAO().delete(eODConfig,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getEodConfig fetch the details by using EodConfigDAO's getEodConfigById
	 * method.
	 * 
	 * @param eodConfigId
	 *            eodConfigId of the EODConfig.
	 * @return EodConfig
	 */
	@Override
	public EODConfig getEODConfig(long eodConfigId) {
		return getEODConfigDAO().getEODConfig(eodConfigId,"_View");
	}

	/**
	 * getApprovedEodConfigById fetch the details by using EodConfigDAO's
	 * getEodConfigById method . with parameter id and type as blank. it fetches
	 * the approved records from the EodConfig.
	 * 
	 * @param eodConfigId
	 *            eodConfigId of the EODConfig.
	 *            (String)
	 * @return EodConfig
	 */
	public EODConfig getApprovedEODConfig(long eodConfigId) {
		return getEODConfigDAO().getEODConfig(eodConfigId,"_AView");
	}	
	
	@Override
	public List<EODConfig> getEODConfig() {
		return getEODConfigDAO().getEODConfig();
	}	
	
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getEODConfigDAO().delete with parameters eODConfig,"" b) NEW Add new
	 * record in to main table by using getEODConfigDAO().save with parameters
	 * eODConfig,"" c) EDIT Update record in the main table by using
	 * getEODConfigDAO().update with parameters eODConfig,"" 3) Delete the record
	 * from the workFlow table by using getEODConfigDAO().delete with parameters
	 * eODConfig,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtEodConfig by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtEodConfig by using
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

		EODConfig eODConfig = new EODConfig();
		BeanUtils.copyProperties((EODConfig) auditHeader.getAuditDetail().getModelData(), eODConfig);

		getEODConfigDAO().delete(eODConfig, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(eODConfig.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(eODConfigDAO.getEODConfig(eODConfig.getEodConfigId(), ""));
		}

		if (eODConfig.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getEODConfigDAO().delete(eODConfig, TableType.MAIN_TAB);
		} else {
			eODConfig.setRoleCode("");
			eODConfig.setNextRoleCode("");
			eODConfig.setTaskId("");
			eODConfig.setNextTaskId("");
			eODConfig.setWorkflowId(0);

			if (eODConfig.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				eODConfig.setRecordType("");
				getEODConfigDAO().save(eODConfig, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				eODConfig.setRecordType("");
				getEODConfigDAO().update(eODConfig, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(eODConfig);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getEODConfigDAO().delete with parameters
		 * eODConfig,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtEodConfig by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			EODConfig eODConfig = (EODConfig) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getEODConfigDAO().delete(eODConfig,TableType.TEMP_TAB);
			
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
		 * from getEODConfigDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
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