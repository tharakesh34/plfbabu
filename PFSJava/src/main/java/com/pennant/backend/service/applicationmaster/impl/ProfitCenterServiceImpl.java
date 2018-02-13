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
 * FileName    		:  ProfitCenterServiceImpl.java                                         * 	  
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.ProfitCenterDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.ProfitCenterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>ProfitCenter</b>.<br>
 */
public class ProfitCenterServiceImpl extends GenericService<ProfitCenter> implements ProfitCenterService {
	private static final Logger logger = Logger.getLogger(ProfitCenterServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private ProfitCenterDAO profitCenterDAO;
	private AccountTypeDAO accountTypeDAO;


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
	 * @return the profitCenterDAO
	 */
	public ProfitCenterDAO getProfitCenterDAO() {
		return profitCenterDAO;
	}
	/**
	 * @param profitCenterDAO the profitCenterDAO to set
	 */
	public void setProfitCenterDAO(ProfitCenterDAO profitCenterDAO) {
		this.profitCenterDAO = profitCenterDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * ProfitCenters/ProfitCenters_Temp by using ProfitCentersDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using ProfitCentersDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtProfitCenters by using
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

		ProfitCenter profitCenter = (ProfitCenter) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (profitCenter.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (profitCenter.isNew()) {
			profitCenter.setId(Long.parseLong(getProfitCenterDAO().save(profitCenter,tableType)));
			auditHeader.getAuditDetail().setModelData(profitCenter);
			auditHeader.setAuditReference(String.valueOf(profitCenter.getProfitCenterID()));
		}else{
			getProfitCenterDAO().update(profitCenter,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table ProfitCenters by using ProfitCentersDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtProfitCenters by using
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
		
		ProfitCenter profitCenter = (ProfitCenter) auditHeader.getAuditDetail().getModelData();
		getProfitCenterDAO().delete(profitCenter,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getProfitCenters fetch the details by using ProfitCentersDAO's getProfitCentersById
	 * method.
	 * 
	 * @param profitCenterID
	 *            profitCenterID of the ProfitCenter.
	 * @return ProfitCenters
	 */
	@Override
	public ProfitCenter getProfitCenter(long profitCenterID) {
		return getProfitCenterDAO().getProfitCenter(profitCenterID,"_View");
	}

	/**
	 * getApprovedProfitCentersById fetch the details by using ProfitCentersDAO's
	 * getProfitCentersById method . with parameter id and type as blank. it fetches
	 * the approved records from the ProfitCenters.
	 * 
	 * @param profitCenterID
	 *            profitCenterID of the ProfitCenter.
	 *            (String)
	 * @return ProfitCenters
	 */
	public ProfitCenter getApprovedProfitCenter(long profitCenterID) {
		return getProfitCenterDAO().getProfitCenter(profitCenterID,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getProfitCenterDAO().delete with parameters profitCenter,"" b) NEW Add new
	 * record in to main table by using getProfitCenterDAO().save with parameters
	 * profitCenter,"" c) EDIT Update record in the main table by using
	 * getProfitCenterDAO().update with parameters profitCenter,"" 3) Delete the record
	 * from the workFlow table by using getProfitCenterDAO().delete with parameters
	 * profitCenter,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtProfitCenters by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtProfitCenters by using
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

		ProfitCenter profitCenter = new ProfitCenter();
		BeanUtils.copyProperties((ProfitCenter) auditHeader.getAuditDetail().getModelData(), profitCenter);

		getProfitCenterDAO().delete(profitCenter, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(profitCenter.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(profitCenterDAO.getProfitCenter(profitCenter.getProfitCenterID(), ""));
		}

		if (profitCenter.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getProfitCenterDAO().delete(profitCenter, TableType.MAIN_TAB);
		} else {
			profitCenter.setRoleCode("");
			profitCenter.setNextRoleCode("");
			profitCenter.setTaskId("");
			profitCenter.setNextTaskId("");
			profitCenter.setWorkflowId(0);

			if (profitCenter.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				profitCenter.setRecordType("");
				getProfitCenterDAO().save(profitCenter, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				profitCenter.setRecordType("");
				getProfitCenterDAO().update(profitCenter, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(profitCenter);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getProfitCenterDAO().delete with parameters
		 * profitCenter,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtProfitCenters by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			ProfitCenter profitCenter = (ProfitCenter) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getProfitCenterDAO().delete(profitCenter,TableType.TEMP_TAB);
			
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
		 * from getProfitCenterDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Get the model object.
			ProfitCenter profitCenter = (ProfitCenter) auditDetail.getModelData();

			// Check the unique keys.
			if (profitCenter.isNew() && profitCenterDAO.isDuplicateKey(profitCenter.getProfitCenterID(),profitCenter.getProfitCenterCode(),
					profitCenter.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];
				
				parameters[0] = PennantJavaUtil.getLabel("label_ProfitCenterCode") + ": " + profitCenter.getProfitCenterCode();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
			
			if (StringUtils.trimToEmpty(profitCenter.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
				int count = accountTypeDAO.getgetAccountTypeByProfit(profitCenter.getProfitCenterID(), "");//FIXME for FinanceMain
				if (count != 0) {
					String[] parameters = new String[2];

					parameters[0] = PennantJavaUtil.getLabel("label_ProfitCenterCode") + ": " + profitCenter.getProfitCenterCode();

					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));
				}
			}
			
			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

		public AccountTypeDAO getAccountTypeDAO() {
			return accountTypeDAO;
		}

		public void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
			this.accountTypeDAO = accountTypeDAO;
		}

}