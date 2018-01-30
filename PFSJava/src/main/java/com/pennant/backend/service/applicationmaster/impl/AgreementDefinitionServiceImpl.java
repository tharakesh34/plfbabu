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
 * FileName    		:  AgreementDefinitionServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-11-2011    														*
 *                                                                  						*
 * Modified Date    :  23-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-11-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.AgreementDefinitionDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>AgreementDefinition</b>.<br>
 * 
 */
public class AgreementDefinitionServiceImpl extends GenericService<AgreementDefinition> implements AgreementDefinitionService {
	private static final Logger logger = Logger.getLogger(AgreementDefinitionServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private AgreementDefinitionDAO agreementDefinitionDAO;

	public AgreementDefinitionServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public AgreementDefinitionDAO getAgreementDefinitionDAO() {
		return agreementDefinitionDAO;
	}
	public void setAgreementDefinitionDAO(AgreementDefinitionDAO agreementDefinitionDAO) {
		this.agreementDefinitionDAO = agreementDefinitionDAO;
	}
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table BMTAggrementDef/BMTAggrementDef_Temp 
	 * 			by using AgreementDefinitionDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using AgreementDefinitionDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtBMTAggrementDef by using auditHeaderDAO.addAudit(auditHeader)
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
		AgreementDefinition agreementDefinition = (AgreementDefinition) auditHeader.getAuditDetail().getModelData();

		if (agreementDefinition.isWorkflow()) {
			tableType=TableType.TEMP_TAB;
		}

		if (agreementDefinition.isNew()) {
			getAgreementDefinitionDAO().save(agreementDefinition,tableType);
		}else{
			getAgreementDefinitionDAO().update(agreementDefinition,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table BMTAggrementDef by using AgreementDefinitionDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtBMTAggrementDef by using auditHeaderDAO.addAudit(auditHeader)    
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

		AgreementDefinition agreementDefinition = (AgreementDefinition) auditHeader.getAuditDetail().getModelData();
		getAgreementDefinitionDAO().delete(agreementDefinition, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAgreementDefinitionById fetch the details by using AgreementDefinitionDAO's getAgreementDefinitionById method.
	 * @param id (long)
	 * @param  type (long)
	 * 			""/_Temp/_View          
	 * @return AgreementDefinition
	 */
	@Override
	public AgreementDefinition getAgreementDefinitionById(long id) {
		return getAgreementDefinitionDAO().getAgreementDefinitionById(id,"_View");
	}

	/**
	 * getApprovedAgreementDefinitionById fetch the details by using AgreementDefinitionDAO's getAgreementDefinitionById method .
	 * with parameter id and type as blank. it fetches the approved records from the BMTAggrementDef.
	 * @param id (String)
	 * @return AgreementDefinition
	 */
	public AgreementDefinition getApprovedAgreementDefinitionById(long id) {
		return getAgreementDefinitionDAO().getAgreementDefinitionById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getAgreementDefinitionDAO().delete with parameters agreementDefinition,""
	 * 		b)  NEW		Add new record in to main table by using getAgreementDefinitionDAO().save with parameters agreementDefinition,""
	 * 		c)  EDIT	Update record in the main table by using getAgreementDefinitionDAO().update with parameters agreementDefinition,""
	 * 3)	Delete the record from the workFlow table by using getAgreementDefinitionDAO().delete with parameters agreementDefinition,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtBMTAggrementDef by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtBMTAggrementDef by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		AgreementDefinition agreementDefinition = new AgreementDefinition();
		BeanUtils.copyProperties((AgreementDefinition) auditHeader.getAuditDetail().getModelData(), agreementDefinition);
		
		getAgreementDefinitionDAO().delete(agreementDefinition, TableType.TEMP_TAB);
		if (!PennantConstants.RECORD_TYPE_NEW.equals(agreementDefinition.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(agreementDefinitionDAO.getAgreementDefinitionByCode(
							agreementDefinition.getAggCode(), ""));
		}

		if (agreementDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getAgreementDefinitionDAO().delete(agreementDefinition, TableType.MAIN_TAB);

		} else {
			agreementDefinition.setRoleCode("");
			agreementDefinition.setNextRoleCode("");
			agreementDefinition.setTaskId("");
			agreementDefinition.setNextTaskId("");
			agreementDefinition.setWorkflowId(0);

			if (agreementDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				agreementDefinition.setRecordType("");
				getAgreementDefinitionDAO().save(agreementDefinition, TableType.MAIN_TAB);
			} else {
				tranType=PennantConstants.TRAN_UPD;
				agreementDefinition.setRecordType("");
				getAgreementDefinitionDAO().update(agreementDefinition, TableType.MAIN_TAB);
			}
		}

		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(agreementDefinition);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getAgreementDefinitionDAO().delete with parameters agreementDefinition,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtBMTAggrementDef by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		AgreementDefinition agreementDefinition = (AgreementDefinition) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAgreementDefinitionDAO().delete(agreementDefinition, TableType.TEMP_TAB);

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
	 * 5)	for any mismatch conditions Fetch the error details from getAgreementDefinitionDAO().getErrorDetail with Error ID and 
	 * 		language as parameters.
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

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getAddressTypeDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		AgreementDefinition agreementDefinition= (AgreementDefinition) auditDetail.getModelData();
		// Check the unique keys.
		if (agreementDefinition.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(agreementDefinition.getRecordType())
				&& agreementDefinitionDAO.isDuplicateKey(agreementDefinition.getAggCode(),
						agreementDefinition.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_AggCode")+":"+ agreementDefinition.getAggCode();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001", parameters, null));
		}
	
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
}