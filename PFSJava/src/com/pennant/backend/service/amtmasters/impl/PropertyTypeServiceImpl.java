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
 * FileName    		:  PropertyTypeServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.amtmasters.PropertyTypeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.amtmasters.PropertyType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.PropertyTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>PropertyType</b>.<br>
 * 
 */
public class PropertyTypeServiceImpl extends GenericService<PropertyType> implements PropertyTypeService {

	private final static Logger logger = Logger.getLogger(PropertyTypeServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private PropertyTypeDAO propertyTypeDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public PropertyTypeDAO getPropertyTypeDAO() {
		return propertyTypeDAO;
	}

	public void setPropertyTypeDAO(PropertyTypeDAO propertyTypeDAO) {
		this.propertyTypeDAO = propertyTypeDAO;
	}

	public PropertyType getPropertyType() {
		return getPropertyTypeDAO().getPropertyType();
	}
	public PropertyType getNewPropertyType() {
		return getPropertyTypeDAO().getNewPropertyType();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table AMTPropertyType/AMTPropertyType_Temp 
	 * 			by using PropertyTypeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using PropertyTypeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtAMTPropertyType by using auditHeaderDAO.addAudit(auditHeader)
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
		PropertyType propertyType = (PropertyType) auditHeader.getAuditDetail().getModelData();

		if (propertyType.isWorkflow()) {
			tableType="_TEMP";
		}

		if (propertyType.isNew()) {
			propertyType.setId(getPropertyTypeDAO().save(propertyType,tableType));
			auditHeader.getAuditDetail().setModelData(propertyType);
			auditHeader.setAuditReference(String.valueOf(propertyType.getPropertyTypeId()));
		}else{
			getPropertyTypeDAO().update(propertyType,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table AMTPropertyType by using PropertyTypeDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtAMTPropertyType by using auditHeaderDAO.addAudit(auditHeader)    
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

		PropertyType propertyType = (PropertyType) auditHeader.getAuditDetail().getModelData();
		getPropertyTypeDAO().delete(propertyType,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getPropertyTypeById fetch the details by using PropertyTypeDAO's getPropertyTypeById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return PropertyType
	 */

	@Override
	public PropertyType getPropertyTypeById(long id) {
		return getPropertyTypeDAO().getPropertyTypeById(id,"_View");
	}
	/**
	 * getApprovedPropertyTypeById fetch the details by using PropertyTypeDAO's getPropertyTypeById method .
	 * with parameter id and type as blank. it fetches the approved records from the AMTPropertyType.
	 * @param id (int)
	 * @return PropertyType
	 */

	public PropertyType getApprovedPropertyTypeById(long id) {
		return getPropertyTypeDAO().getPropertyTypeById(id,"_AView");
	}	

	/**
	 * This method refresh the Record.
	 * @param PropertyType (propertyType)
	 * @return propertyType
	 */
	@Override
	public PropertyType refresh(PropertyType propertyType) {
		logger.debug("Entering");
		getPropertyTypeDAO().refresh(propertyType);
		getPropertyTypeDAO().initialize(propertyType);
		logger.debug("Leaving");
		return propertyType;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getPropertyTypeDAO().delete with parameters propertyType,""
	 * 		b)  NEW		Add new record in to main table by using getPropertyTypeDAO().save with parameters propertyType,""
	 * 		c)  EDIT	Update record in the main table by using getPropertyTypeDAO().update with parameters propertyType,""
	 * 3)	Delete the record from the workFlow table by using getPropertyTypeDAO().delete with parameters propertyType,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtAMTPropertyType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtAMTPropertyType by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		PropertyType propertyType = new PropertyType();
		BeanUtils.copyProperties((PropertyType) auditHeader.getAuditDetail().getModelData(), propertyType);

		if (propertyType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getPropertyTypeDAO().delete(propertyType,"");

		} else {
			propertyType.setRoleCode("");
			propertyType.setNextRoleCode("");
			propertyType.setTaskId("");
			propertyType.setNextTaskId("");
			propertyType.setWorkflowId(0);

			if (propertyType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				propertyType.setRecordType("");
				getPropertyTypeDAO().save(propertyType,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				propertyType.setRecordType("");
				getPropertyTypeDAO().update(propertyType,"");
			}
		}

		getPropertyTypeDAO().delete(propertyType,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(propertyType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getPropertyTypeDAO().delete with parameters propertyType,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtAMTPropertyType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		PropertyType propertyType = (PropertyType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPropertyTypeDAO().delete(propertyType,"_TEMP");

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
	 * 5)	for any mismatch conditions Fetch the error details from getPropertyTypeDAO().getErrorDetail with Error ID and language as parameters.
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

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		PropertyType propertyType= (PropertyType) auditDetail.getModelData();

		PropertyType tempPropertyType= null;
		if (propertyType.isWorkflow()){
			tempPropertyType = getPropertyTypeDAO().getPropertyTypeById(propertyType.getId(), "_Temp");
		}
		PropertyType befPropertyType= getPropertyTypeDAO().getPropertyTypeById(propertyType.getId(), "");

		PropertyType oldPropertyType= propertyType.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(propertyType.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_PropertyTypeId")+":"+valueParm[0];

		if (propertyType.isNew()){ // for New record or new record into work flow

			if (!propertyType.isWorkflow()){// With out Work flow only new records  
				if (befPropertyType !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (propertyType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befPropertyType !=null || tempPropertyType!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befPropertyType ==null || tempPropertyType!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!propertyType.isWorkflow()){	// With out Work flow for update and delete

				if (befPropertyType ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldPropertyType!=null && !oldPropertyType.getLastMntOn().equals(befPropertyType.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempPropertyType==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldPropertyType!=null && !oldPropertyType.getLastMntOn().equals(tempPropertyType.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !propertyType.isWorkflow()){
			propertyType.setBefImage(befPropertyType);	
		}

		return auditDetail;
	}

}