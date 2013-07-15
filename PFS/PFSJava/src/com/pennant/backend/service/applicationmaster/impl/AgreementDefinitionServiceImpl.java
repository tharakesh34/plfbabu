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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.AgreementDefinitionDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>AgreementDefinition</b>.<br>
 * 
 */
public class AgreementDefinitionServiceImpl extends GenericService<AgreementDefinition> implements AgreementDefinitionService {
	private final static Logger logger = Logger.getLogger(AgreementDefinitionServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private AgreementDefinitionDAO agreementDefinitionDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	@Override
	public AgreementDefinition getAgreementDefinition() {
		return getAgreementDefinitionDAO().getAgreementDefinition();
	}
	@Override
	public AgreementDefinition getNewAgreementDefinition() {
		return getAgreementDefinitionDAO().getNewAgreementDefinition();
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
		String tableType="";
		AgreementDefinition agreementDefinition = (AgreementDefinition) auditHeader.getAuditDetail().getModelData();

		if (agreementDefinition.isWorkflow()) {
			tableType="_TEMP";
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
		getAgreementDefinitionDAO().delete(agreementDefinition,"");

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
	 * This method refresh the Record.
	 * @param AgreementDefinition (agreementDefinition)
	 * @return agreementDefinition
	 */
	@Override
	public AgreementDefinition refresh(AgreementDefinition agreementDefinition) {
		logger.debug("Entering");
		getAgreementDefinitionDAO().refresh(agreementDefinition);
		getAgreementDefinitionDAO().initialize(agreementDefinition);
		logger.debug("Leaving");
		return agreementDefinition;
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

		if (agreementDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getAgreementDefinitionDAO().delete(agreementDefinition,"");

		} else {
			agreementDefinition.setRoleCode("");
			agreementDefinition.setNextRoleCode("");
			agreementDefinition.setTaskId("");
			agreementDefinition.setNextTaskId("");
			agreementDefinition.setWorkflowId(0);

			if (agreementDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				agreementDefinition.setRecordType("");
				getAgreementDefinitionDAO().save(agreementDefinition,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				agreementDefinition.setRecordType("");
				getAgreementDefinitionDAO().update(agreementDefinition,"");
			}
		}

		getAgreementDefinitionDAO().delete(agreementDefinition,"_TEMP");
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
		getAgreementDefinitionDAO().delete(agreementDefinition,"_TEMP");

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
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
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
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		AgreementDefinition agreementDefinition= (AgreementDefinition) auditDetail.getModelData();

		AgreementDefinition tempAgreementDefinition= null;
		if (agreementDefinition.isWorkflow()){
			tempAgreementDefinition = getAgreementDefinitionDAO().getAgreementDefinitionByCode(agreementDefinition.getAggCode(), "_Temp");
		}
		AgreementDefinition befAgreementDefinition= getAgreementDefinitionDAO().getAgreementDefinitionByCode(agreementDefinition.getAggCode(), "");
		AgreementDefinition old_AgreementDefinition= agreementDefinition.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=agreementDefinition.getAggCode();
		errParm[0]=PennantJavaUtil.getLabel("label_AggCode")+":"+valueParm[0];

		if (agreementDefinition.isNew()){ // for New record or new record into work flow

			if (!agreementDefinition.isWorkflow()){// With out Work flow only new records  
				if (befAgreementDefinition !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (agreementDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befAgreementDefinition !=null || tempAgreementDefinition!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befAgreementDefinition ==null || tempAgreementDefinition!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!agreementDefinition.isWorkflow()){	// With out Work flow for update and delete

				if (befAgreementDefinition ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_AgreementDefinition!=null && !old_AgreementDefinition.getLastMntOn().equals(befAgreementDefinition.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempAgreementDefinition==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (old_AgreementDefinition!=null && !old_AgreementDefinition.getLastMntOn().equals(tempAgreementDefinition.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !agreementDefinition.isWorkflow()){
			agreementDefinition.setBefImage(befAgreementDefinition);	
		}

		return auditDetail;
	}

}