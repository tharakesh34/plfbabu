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
 * FileName    		:  ScoringTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-11-2011    														*
 *                                                                  						*
 * Modified Date    :  08-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.bmtmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.ScoringTypeDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.ScoringType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.bmtmasters.ScoringTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>ScoringType</b>.<br>
 * 
 */
public class ScoringTypeServiceImpl extends GenericService<ScoringType> implements ScoringTypeService {
	private static final Logger logger = Logger.getLogger(ScoringTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ScoringTypeDAO scoringTypeDAO;

	public ScoringTypeServiceImpl() {
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

	public ScoringTypeDAO getScoringTypeDAO() {
		return scoringTypeDAO;
	}
	public void setScoringTypeDAO(ScoringTypeDAO scoringTypeDAO) {
		this.scoringTypeDAO = scoringTypeDAO;
	}

	@Override
	public ScoringType getScoringType() {
		return getScoringTypeDAO().getScoringType();
	}
	@Override
	public ScoringType getNewScoringType() {
		return getScoringTypeDAO().getNewScoringType();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table BMTScoringType/BMTScoringType_Temp 
	 * 			by using ScoringTypeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ScoringTypeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtBMTScoringType by using auditHeaderDAO.addAudit(auditHeader)
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
		ScoringType scoringType = (ScoringType) auditHeader.getAuditDetail().getModelData();

		if (scoringType.isWorkflow()) {
			tableType="_Temp";
		}

		if (scoringType.isNew()) {
			getScoringTypeDAO().save(scoringType,tableType);
		}else{
			getScoringTypeDAO().update(scoringType,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table BMTScoringType by using ScoringTypeDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtBMTScoringType by using auditHeaderDAO.addAudit(auditHeader)    
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

		ScoringType scoringType = (ScoringType) auditHeader.getAuditDetail().getModelData();
		getScoringTypeDAO().delete(scoringType,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getScoringTypeById fetch the details by using ScoringTypeDAO's getScoringTypeById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ScoringType
	 */
	@Override
	public ScoringType getScoringTypeById(String id) {
		return getScoringTypeDAO().getScoringTypeById(id,"_View");
	}

	/**
	 * getApprovedScoringTypeById fetch the details by using ScoringTypeDAO's getScoringTypeById method .
	 * with parameter id and type as blank. it fetches the approved records from the BMTScoringType.
	 * @param id (String)
	 * @return ScoringType
	 */

	public ScoringType getApprovedScoringTypeById(String id) {
		return getScoringTypeDAO().getScoringTypeById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getScoringTypeDAO().delete with parameters scoringType,""
	 * 		b)  NEW		Add new record in to main table by using getScoringTypeDAO().save with parameters scoringType,""
	 * 		c)  EDIT	Update record in the main table by using getScoringTypeDAO().update with parameters scoringType,""
	 * 3)	Delete the record from the workFlow table by using getScoringTypeDAO().delete with parameters scoringType,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtBMTScoringType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtBMTScoringType by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		ScoringType scoringType = new ScoringType();
		BeanUtils.copyProperties((ScoringType) auditHeader.getAuditDetail().getModelData(), scoringType);

		if (scoringType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getScoringTypeDAO().delete(scoringType,"");

		} else {
			scoringType.setRoleCode("");
			scoringType.setNextRoleCode("");
			scoringType.setTaskId("");
			scoringType.setNextTaskId("");
			scoringType.setWorkflowId(0);

			if (scoringType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				scoringType.setRecordType("");
				getScoringTypeDAO().save(scoringType,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				scoringType.setRecordType("");
				getScoringTypeDAO().update(scoringType,"");
			}
		}

		getScoringTypeDAO().delete(scoringType,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(scoringType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getScoringTypeDAO().delete with parameters scoringType,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtBMTScoringType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		ScoringType scoringType = (ScoringType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getScoringTypeDAO().delete(scoringType,"_Temp");

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
	 * 5)	for any mismatch conditions Fetch the error details from getScoringTypeDAO().getErrorDetail with Error ID and language as parameters.
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
	 * getScoringTypeDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		ScoringType scoringType= (ScoringType) auditDetail.getModelData();

		ScoringType tempScoringType= null;
		if (scoringType.isWorkflow()){
			tempScoringType = getScoringTypeDAO().getScoringTypeById(scoringType.getId(), "_Temp");
		}
		ScoringType befScoringType= getScoringTypeDAO().getScoringTypeById(scoringType.getId(), "");

		ScoringType oldScoringType= scoringType.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=scoringType.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_ScoType")+":"+valueParm[0];

		if (scoringType.isNew()){ // for New record or new record into work flow

			if (!scoringType.isWorkflow()){// With out Work flow only new records  
				if (befScoringType !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (scoringType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befScoringType !=null || tempScoringType!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befScoringType ==null || tempScoringType!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!scoringType.isWorkflow()){	// With out Work flow for update and delete

				if (befScoringType ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldScoringType!=null && !oldScoringType.getLastMntOn().equals(befScoringType.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempScoringType==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempScoringType!=null  && oldScoringType!=null && !oldScoringType.getLastMntOn().equals(tempScoringType.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method) ) || !scoringType.isWorkflow()){
			scoringType.setBefImage(befScoringType);	
		}

		return auditDetail;
	}

}