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
 * FileName    		:  CostCenterServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.applicationmaster.CostCenterDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.CostCenter;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.CostCenterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>CostCenter</b>.<br>
 */
public class CostCenterServiceImpl extends GenericService<CostCenter> implements CostCenterService {
	private static final Logger logger = Logger.getLogger(CostCenterServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private CostCenterDAO costCenterDAO;
	private AccountTypeDAO accountTypeDAO;


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public AccountTypeDAO getAccountTypeDAO() {
		return accountTypeDAO;
	}

	public void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
		this.accountTypeDAO = accountTypeDAO;
	}

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
	 * @return the costCenterDAO
	 */
	public CostCenterDAO getCostCenterDAO() {
		return costCenterDAO;
	}
	/**
	 * @param costCenterDAO the costCenterDAO to set
	 */
	public void setCostCenterDAO(CostCenterDAO costCenterDAO) {
		this.costCenterDAO = costCenterDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * CostCenters/CostCenters_Temp by using CostCentersDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using CostCentersDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtCostCenters by using
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

		CostCenter costCenter = (CostCenter) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (costCenter.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (costCenter.isNew()) {
			costCenter.setId(Long.parseLong(getCostCenterDAO().save(costCenter,tableType)));
			auditHeader.getAuditDetail().setModelData(costCenter);
			auditHeader.setAuditReference(String.valueOf(costCenter.getCostCenterID()));
		}else{
			getCostCenterDAO().update(costCenter,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table CostCenters by using CostCentersDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtCostCenters by using
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
		
		CostCenter costCenter = (CostCenter) auditHeader.getAuditDetail().getModelData();
		getCostCenterDAO().delete(costCenter,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getCostCenters fetch the details by using CostCentersDAO's getCostCentersById
	 * method.
	 * 
	 * @param costCenterID
	 *            costCenterID of the CostCenter.
	 * @return CostCenters
	 */
	@Override
	public CostCenter getCostCenter(long costCenterID) {
		return getCostCenterDAO().getCostCenter(costCenterID,"_View");
	}

	/**
	 * getApprovedCostCentersById fetch the details by using CostCentersDAO's
	 * getCostCentersById method . with parameter id and type as blank. it fetches
	 * the approved records from the CostCenters.
	 * 
	 * @param costCenterID
	 *            costCenterID of the CostCenter.
	 *            (String)
	 * @return CostCenters
	 */
	public CostCenter getApprovedCostCenter(long costCenterID) {
		return getCostCenterDAO().getCostCenter(costCenterID,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCostCenterDAO().delete with parameters costCenter,"" b) NEW Add new
	 * record in to main table by using getCostCenterDAO().save with parameters
	 * costCenter,"" c) EDIT Update record in the main table by using
	 * getCostCenterDAO().update with parameters costCenter,"" 3) Delete the record
	 * from the workFlow table by using getCostCenterDAO().delete with parameters
	 * costCenter,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtCostCenters by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtCostCenters by using
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

		CostCenter costCenter = new CostCenter();
		BeanUtils.copyProperties((CostCenter) auditHeader.getAuditDetail().getModelData(), costCenter);

		getCostCenterDAO().delete(costCenter, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(costCenter.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(costCenterDAO.getCostCenter(costCenter.getCostCenterID(), ""));
		}

		if (costCenter.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCostCenterDAO().delete(costCenter, TableType.MAIN_TAB);
		} else {
			costCenter.setRoleCode("");
			costCenter.setNextRoleCode("");
			costCenter.setTaskId("");
			costCenter.setNextTaskId("");
			costCenter.setWorkflowId(0);

			if (costCenter.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				costCenter.setRecordType("");
				getCostCenterDAO().save(costCenter, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				costCenter.setRecordType("");
				getCostCenterDAO().update(costCenter, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(costCenter);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getCostCenterDAO().delete with parameters
		 * costCenter,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtCostCenters by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			CostCenter costCenter = (CostCenter) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getCostCenterDAO().delete(costCenter,TableType.TEMP_TAB);
			
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
		 * from getCostCenterDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Get the model object.
			CostCenter costCenter = (CostCenter) auditDetail.getModelData();

			// Check the unique keys.
			if (costCenter.isNew() && costCenterDAO.isDuplicateKey(costCenter.getCostCenterID(),costCenter.getCostCenterCode(),
					costCenter.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];
				
				parameters[0] = PennantJavaUtil.getLabel("label_CostCenterCode") + ": " + costCenter.getCostCenterCode();

				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
			
			if (StringUtils.trimToEmpty(costCenter.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
				int count = accountTypeDAO.getgetAccountTypeByCost(costCenter.getCostCenterID(), "");//FIXME for FinanceMain
				if (count != 0) {
					String[] parameters = new String[2];

					parameters[0] = PennantJavaUtil.getLabel("label_CostCenterCode") + ": " + costCenter.getCostCenterCode();

					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", parameters, null));
				}
			}
			
			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

}