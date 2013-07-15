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
 * FileName    		:  PropertyRelationTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.amtmasters.impl;



import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.amtmasters.PropertyRelationTypeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.amtmasters.PropertyRelationType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.PropertyRelationTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>PropertyRelationType</b>.<br>
 * 
 */
public class PropertyRelationTypeServiceImpl extends GenericService<PropertyRelationType> implements PropertyRelationTypeService {

	private final static Logger logger = Logger.getLogger(PropertyRelationTypeServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private PropertyRelationTypeDAO propertyRelationTypeDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public PropertyRelationTypeDAO getPropertyRelationTypeDAO() {
		return propertyRelationTypeDAO;
	}
	public void setPropertyRelationTypeDAO(PropertyRelationTypeDAO propertyRelationTypeDAO) {
		this.propertyRelationTypeDAO = propertyRelationTypeDAO;
	}

	public PropertyRelationType getPropertyRelationType() {
		return getPropertyRelationTypeDAO().getPropertyRelationType();
	}
	public PropertyRelationType getNewPropertyRelationType() {
		return getPropertyRelationTypeDAO().getNewPropertyRelationType();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table AMTPropertyRelationType/AMTPropertyRelationType_Temp 
	 * 			by using PropertyRelationTypeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using PropertyRelationTypeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtAMTPropertyRelationType by using auditHeaderDAO.addAudit(auditHeader)
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
		PropertyRelationType propertyRelationType = (PropertyRelationType) auditHeader.getAuditDetail().getModelData();

		if (propertyRelationType.isWorkflow()) {
			tableType="_TEMP";
		}

		if (propertyRelationType.isNew()) {
			propertyRelationType.setId(getPropertyRelationTypeDAO().save(propertyRelationType,tableType));
			auditHeader.getAuditDetail().setModelData(propertyRelationType);
			auditHeader.setAuditReference(String.valueOf(propertyRelationType.getPropertyRelationTypeId()));
		}else{
			getPropertyRelationTypeDAO().update(propertyRelationType,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table AMTPropertyRelationType by using PropertyRelationTypeDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtAMTPropertyRelationType by using auditHeaderDAO.addAudit(auditHeader)    
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

		PropertyRelationType propertyRelationType = (PropertyRelationType) auditHeader.getAuditDetail().getModelData();
		getPropertyRelationTypeDAO().delete(propertyRelationType,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getPropertyRelationTypeById fetch the details by using PropertyRelationTypeDAO's getPropertyRelationTypeById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return PropertyRelationType
	 */

	@Override
	public PropertyRelationType getPropertyRelationTypeById(long id) {
		return getPropertyRelationTypeDAO().getPropertyRelationTypeById(id,"_View");
	}
	/**
	 * getApprovedPropertyRelationTypeById fetch the details by using PropertyRelationTypeDAO's getPropertyRelationTypeById method .
	 * with parameter id and type as blank. it fetches the approved records from the AMTPropertyRelationType.
	 * @param id (int)
	 * @return PropertyRelationType
	 */

	public PropertyRelationType getApprovedPropertyRelationTypeById(long id) {
		return getPropertyRelationTypeDAO().getPropertyRelationTypeById(id,"_AView");
	}	

	/**
	 * This method refresh the Record.
	 * @param PropertyRelationType (propertyRelationType)
	 * @return propertyRelationType
	 */
	@Override
	public PropertyRelationType refresh(PropertyRelationType propertyRelationType) {
		logger.debug("Entering");
		getPropertyRelationTypeDAO().refresh(propertyRelationType);
		getPropertyRelationTypeDAO().initialize(propertyRelationType);
		logger.debug("Leaving");
		return propertyRelationType;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getPropertyRelationTypeDAO().delete with parameters propertyRelationType,""
	 * 		b)  NEW		Add new record in to main table by using getPropertyRelationTypeDAO().save with parameters propertyRelationType,""
	 * 		c)  EDIT	Update record in the main table by using getPropertyRelationTypeDAO().update with parameters propertyRelationType,""
	 * 3)	Delete the record from the workFlow table by using getPropertyRelationTypeDAO().delete with parameters propertyRelationType,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtAMTPropertyRelationType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtAMTPropertyRelationType by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		PropertyRelationType propertyRelationType = new PropertyRelationType();
		BeanUtils.copyProperties((PropertyRelationType) auditHeader.getAuditDetail().getModelData(), propertyRelationType);

		if (propertyRelationType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getPropertyRelationTypeDAO().delete(propertyRelationType,"");

		} else {
			propertyRelationType.setRoleCode("");
			propertyRelationType.setNextRoleCode("");
			propertyRelationType.setTaskId("");
			propertyRelationType.setNextTaskId("");
			propertyRelationType.setWorkflowId(0);

			if (propertyRelationType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				propertyRelationType.setRecordType("");
				getPropertyRelationTypeDAO().save(propertyRelationType,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				propertyRelationType.setRecordType("");
				getPropertyRelationTypeDAO().update(propertyRelationType,"");
			}
		}

		getPropertyRelationTypeDAO().delete(propertyRelationType,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(propertyRelationType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getPropertyRelationTypeDAO().delete with parameters propertyRelationType,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtAMTPropertyRelationType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		PropertyRelationType propertyRelationType = (PropertyRelationType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPropertyRelationTypeDAO().delete(propertyRelationType,"_TEMP");

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
	 * 
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
	 * getPropertyRelationTypeDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		PropertyRelationType propertyRelationType= (PropertyRelationType) auditDetail.getModelData();

		PropertyRelationType tempPropertyRelationType= null;
		if (propertyRelationType.isWorkflow()){
			tempPropertyRelationType = getPropertyRelationTypeDAO().getPropertyRelationTypeById(propertyRelationType.getId(), "_Temp");
		}
		PropertyRelationType befPropertyRelationType= getPropertyRelationTypeDAO().getPropertyRelationTypeById(propertyRelationType.getId(), "");

		PropertyRelationType old_PropertyRelationType= propertyRelationType.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(propertyRelationType.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_PropertyRelationTypeId")+":"+valueParm[0];

		if (propertyRelationType.isNew()){ // for New record or new record into work flow

			if (!propertyRelationType.isWorkflow()){// With out Work flow only new records  
				if (befPropertyRelationType !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (propertyRelationType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befPropertyRelationType !=null || tempPropertyRelationType!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befPropertyRelationType ==null || tempPropertyRelationType!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!propertyRelationType.isWorkflow()){	// With out Work flow for update and delete

				if (befPropertyRelationType ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_PropertyRelationType!=null && !old_PropertyRelationType.getLastMntOn().equals(befPropertyRelationType.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempPropertyRelationType==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (old_PropertyRelationType!=null && !old_PropertyRelationType.getLastMntOn().equals(tempPropertyRelationType.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !propertyRelationType.isWorkflow()){
			propertyRelationType.setBefImage(befPropertyRelationType);	
		}

		return auditDetail;
	}

}