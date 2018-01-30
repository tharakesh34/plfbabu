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
 * FileName    		:  OtherBankFinanceTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-04-2015    														*
 *                                                                  						*
 * Modified Date    :  03-04-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-04-2015       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.OtherBankFinanceTypeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.OtherBankFinanceType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.OtherBankFinanceTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>OtherBankFinanceType</b>.<br>
 * 
 */
public class OtherBankFinanceTypeServiceImpl extends GenericService<OtherBankFinanceType> implements
		OtherBankFinanceTypeService {
	private static final Logger logger = Logger.getLogger(OtherBankFinanceTypeServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private OtherBankFinanceTypeDAO otherBankFinanceTypeDAO;

	public OtherBankFinanceTypeServiceImpl() {
		super();
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
	 * @return the otherBankFinanceTypeDAO
	 */
	public OtherBankFinanceTypeDAO getOtherBankFinanceTypeDAO() {
		return otherBankFinanceTypeDAO;
	}
	/**
	 * @param otherBankFinanceTypeDAO the otherBankFinanceTypeDAO to set
	 */
	public void setOtherBankFinanceTypeDAO(OtherBankFinanceTypeDAO otherBankFinanceTypeDAO) {
		this.otherBankFinanceTypeDAO = otherBankFinanceTypeDAO;
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table OtherBankFinanceTypes/OtherBankFinanceTypes_Temp 
	 * 			by using OtherBankFinanceTypeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using OtherBankFinanceTypeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtOtherBankFinanceTypes by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		TableType tableType = TableType.MAIN_TAB;
		OtherBankFinanceType otherBankFinanceType = (OtherBankFinanceType) auditHeader.getAuditDetail().getModelData();
		
		if (otherBankFinanceType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (otherBankFinanceType.isNew()) {
			getOtherBankFinanceTypeDAO().save(otherBankFinanceType,tableType);
		}else{
			getOtherBankFinanceTypeDAO().update(otherBankFinanceType,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table OtherBankFinanceTypes by using OtherBankFinanceTypeDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtOtherBankFinanceTypes by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		OtherBankFinanceType otherBankFinanceType = (OtherBankFinanceType) auditHeader.getAuditDetail().getModelData();
		getOtherBankFinanceTypeDAO().delete(otherBankFinanceType, TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getOtherBankFinanceTypeById fetch the details by using OtherBankFinanceTypeDAO's getOtherBankFinanceTypeById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return OtherBankFinanceType
	 */
	
	@Override
	public OtherBankFinanceType getOtherBankFinanceTypeById(String id) {
		return getOtherBankFinanceTypeDAO().getOtherBankFinanceTypeById(id,"_View");
	}
	/**
	 * getApprovedOtherBankFinanceTypeById fetch the details by using OtherBankFinanceTypeDAO's getOtherBankFinanceTypeById method .
	 * with parameter id and type as blank. it fetches the approved records from the OtherBankFinanceTypes.
	 * @param id (String)
	 * @return OtherBankFinanceType
	 */
	
	public OtherBankFinanceType getApprovedOtherBankFinanceTypeById(String id) {
		return getOtherBankFinanceTypeDAO().getOtherBankFinanceTypeById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getOtherBankFinanceTypeDAO().delete
	 * with parameters otherBankFinanceType,"" b) NEW Add new record in to main table by using
	 * getOtherBankFinanceTypeDAO().save with parameters otherBankFinanceType,"" c) EDIT Update record in the main table
	 * by using getOtherBankFinanceTypeDAO().update with parameters otherBankFinanceType,"" 3) Delete the record from
	 * the workFlow table by using getOtherBankFinanceTypeDAO().delete with parameters otherBankFinanceType,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtOtherBankFinanceTypes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtOtherBankFinanceTypes by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		OtherBankFinanceType otherBankFinanceType = new OtherBankFinanceType();
		BeanUtils.copyProperties((OtherBankFinanceType) auditHeader.getAuditDetail().getModelData(),
				otherBankFinanceType);
		getOtherBankFinanceTypeDAO().delete(otherBankFinanceType, TableType.TEMP_TAB);
		if (!PennantConstants.RECORD_TYPE_NEW.equals(otherBankFinanceType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(otherBankFinanceTypeDAO.getOtherBankFinanceTypeById(otherBankFinanceType.getId(), ""));
		}
		if (otherBankFinanceType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getOtherBankFinanceTypeDAO().delete(otherBankFinanceType, TableType.MAIN_TAB);

		} else {
			otherBankFinanceType.setRoleCode("");
			otherBankFinanceType.setNextRoleCode("");
			otherBankFinanceType.setTaskId("");
			otherBankFinanceType.setNextTaskId("");
			otherBankFinanceType.setWorkflowId(0);

			if (otherBankFinanceType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				otherBankFinanceType.setRecordType("");
				getOtherBankFinanceTypeDAO().save(otherBankFinanceType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				otherBankFinanceType.setRecordType("");
				getOtherBankFinanceTypeDAO().update(otherBankFinanceType, TableType.MAIN_TAB);
			}
		}

		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(otherBankFinanceType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getOtherBankFinanceTypeDAO().delete with parameters otherBankFinanceType,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtOtherBankFinanceTypes by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doReject");
			if (!auditHeader.isNextProcess()) {
				logger.debug("Leaving");
				return auditHeader;
			}

			OtherBankFinanceType otherBankFinanceType = (OtherBankFinanceType) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getOtherBankFinanceTypeDAO().delete(otherBankFinanceType, TableType.TEMP_TAB);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");
			
			return auditHeader;
		}

		/**
		 * businessValidation method do the following steps.
		 * 1)	get the details from the auditHeader. 
		 * 2)	fetch the details from the tables
		 * 3)	Validate the Record based on the record details. 
		 * 4) 	Validate for any business validation.
		 * 5)	for any mismatch conditions Fetch the error details from getOtherBankFinanceTypeDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)    
		 * @return auditHeader
		 */

		
		private AuditHeader businessValidation(AuditHeader auditHeader, String method){
			logger.debug("Entering");
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);
			logger.debug("Leaving");
			return auditHeader;
		}

		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug("Entering");

			// Get the model object.
			OtherBankFinanceType otherBankFinanceType = (OtherBankFinanceType) auditDetail.getModelData();
			// Check the unique keys.
			if (otherBankFinanceType.isNew()
					&& PennantConstants.RECORD_TYPE_NEW.equals(otherBankFinanceType.getRecordType())
					&& otherBankFinanceTypeDAO.isDuplicateKey(otherBankFinanceType.getId(),
							otherBankFinanceType.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[1];
				parameters[0] = PennantJavaUtil.getLabel("label_FinType")+":"+ otherBankFinanceType.getId();
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
		
			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

			logger.debug("Leaving");
			return auditDetail;
		}

}