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
 * FileName    		:  Additional FieldsServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-12-2011    														*
 *                                                                  						*
 * Modified Date    :  22-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.testing.impl;



import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.testing.AdditionalFieldValuesDAO;
import com.pennant.backend.dao.testing.AdditionalFieldsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.testing.AdditionalFieldValues;
import com.pennant.backend.model.testing.AdditionalFields;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.testing.AdditionalFieldsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Additional Fields</b>.<br>
 * 
 */
public class AdditionalFieldsServiceImpl extends GenericService<AdditionalFields> implements AdditionalFieldsService {
	private final static Logger logger = Logger.getLogger(AdditionalFieldsServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private AdditionalFieldsDAO additionalFieldsDAO;
	
	private transient  AdditionalFieldValuesDAO additionalFieldValuesDAO; 

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
	 * @return the additional FieldsDAO
	 */
	public AdditionalFieldsDAO getAdditionalFieldsDAO() {
		return additionalFieldsDAO;
	}
	/**
	 * @param additional FieldsDAO the additional FieldsDAO to set
	 */
	public void setAdditionalFieldsDAO(AdditionalFieldsDAO additionalFieldsDAO) {
		this.additionalFieldsDAO = additionalFieldsDAO;
	}

	/**
	 * @return the additional Fields
	 */
	@Override
	public AdditionalFields getAdditionalFields() {
		return getAdditionalFieldsDAO().getAdditionalFields();
	}
	/**
	 * @return the additional Fields for New Record
	 */
	@Override
	public AdditionalFields getNewAdditionalFields() {
		return getAdditionalFieldsDAO().getNewAdditionalFields();
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table AdditionalFields/AdditionalFields_Temp 
	 * 			by using Additional FieldsDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using Additional FieldsDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtAdditionalFields by using auditHeaderDAO.addAudit(auditHeader)
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
		AdditionalFields additionalFields = (AdditionalFields) auditHeader.getAuditDetail().getModelData();
		
		if (additionalFields.isWorkflow()) {
			tableType="_TEMP";
		}

		if (additionalFields.isNew()) {
			getAdditionalFieldsDAO().save(additionalFields,tableType);
		}else{
			getAdditionalFieldsDAO().update(additionalFields,tableType);
		}
		if (additionalFields.getLovDescAdditionalFields().size()>0) {
			List<String> temp=new ArrayList<String>(additionalFields.getLovDescAdditionalFields().keySet());
		
			for (int i = 0; i <temp.size(); i++) {				
				
				AdditionalFieldValues additionalFieldValues=new AdditionalFieldValues(); 
				additionalFieldValues.setNewRecord(additionalFields.isNewRecord());
				additionalFieldValues.setFieldName(temp.get(i));
				additionalFieldValues.setFieldValue(additionalFields.getLovDescAdditionalFields().get(temp.get(i)).toString());
				additionalFieldValues.setModuleName("Customer");
				if (additionalFieldValues.isNewRecord()) {
					getAdditionalFieldValuesDAO().save(additionalFieldValues, "");   
				}else{
					getAdditionalFieldValuesDAO().update(additionalFieldValues, "");
				}
            }
			
	
			
        }
	

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table AdditionalFields by using Additional FieldsDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtAdditionalFields by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		AdditionalFields additionalFields = (AdditionalFields) auditHeader.getAuditDetail().getModelData();
		getAdditionalFieldsDAO().delete(additionalFields,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAdditional FieldsById fetch the details by using Additional FieldsDAO's getAdditional FieldsById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Additional Fields
	 */
	
	@Override
	public AdditionalFields getAdditionalFieldsById(String id) {
		AdditionalFields AdditionalFields =getAdditionalFieldsDAO().getAdditionalFieldsById(id,"_View");
		List<AdditionalFieldValues>  additionalFieldValues=     getAdditionalFieldValuesDAO().getAddfeldList("Customer", "");
		for (int i = 0; i < additionalFieldValues.size(); i++) {			
			AdditionalFields.setLovDescAdditionalFields(additionalFieldValues.get(i).getFieldName(), additionalFieldValues.get(i).getFieldValue());
		}
	
		return AdditionalFields;
	}
	/**
	 * getApprovedAdditional FieldsById fetch the details by using Additional FieldsDAO's getAdditional FieldsById method .
	 * with parameter id and type as blank. it fetches the approved records from the AdditionalFields.
	 * @param id (String)
	 * @return Additional Fields
	 */
	
	public AdditionalFields getApprovedAdditionalFieldsById(String id) {
		return getAdditionalFieldsDAO().getAdditionalFieldsById(id,"_AView");
	}
		
	/**
	 * This method refresh the Record.
	 * @param Additional Fields (additional Fields)
 	 * @return additional Fields
	 */
	@Override
	public AdditionalFields refresh(AdditionalFields additionalFields) {
		logger.debug("Entering");
		getAdditionalFieldsDAO().refresh(additionalFields);
		getAdditionalFieldsDAO().initialize(additionalFields);
		logger.debug("Leaving");
		return additionalFields;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getAdditional FieldsDAO().delete with parameters additional Fields,""
	 * 		b)  NEW		Add new record in to main table by using getAdditional FieldsDAO().save with parameters additional Fields,""
	 * 		c)  EDIT	Update record in the main table by using getAdditional FieldsDAO().update with parameters additional Fields,""
	 * 3)	Delete the record from the workFlow table by using getAdditional FieldsDAO().delete with parameters additional Fields,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtAdditionalFields by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtAdditionalFields by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		AdditionalFields additionalFields = new AdditionalFields();
		BeanUtils.copyProperties((AdditionalFields) auditHeader.getAuditDetail().getModelData(), additionalFields);

		if (additionalFields.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getAdditionalFieldsDAO().delete(additionalFields,"");
				
			} else {
				additionalFields.setRoleCode("");
				additionalFields.setNextRoleCode("");
				additionalFields.setTaskId("");
				additionalFields.setNextTaskId("");
				additionalFields.setWorkflowId(0);
				
				if (additionalFields.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					additionalFields.setRecordType("");
					getAdditionalFieldsDAO().save(additionalFields,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					additionalFields.setRecordType("");
					getAdditionalFieldsDAO().update(additionalFields,"");
				}
			}
			
			getAdditionalFieldsDAO().delete(additionalFields,"_TEMP");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(additionalFields);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getAdditional FieldsDAO().delete with parameters additional Fields,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtAdditionalFields by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			AdditionalFields additionalFields = (AdditionalFields) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAdditionalFieldsDAO().delete(additionalFields,"_TEMP");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getAdditional FieldsDAO().getErrorDetail with Error ID and language as parameters.
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
			AdditionalFields additionalFields= (AdditionalFields) auditDetail.getModelData();
			
			AdditionalFields tempAdditionalFields= null;
			if (additionalFields.isWorkflow()){
				tempAdditionalFields = getAdditionalFieldsDAO().getAdditionalFieldsById(additionalFields.getId(), "_Temp");
			}
			AdditionalFields befAdditionalFields= getAdditionalFieldsDAO().getAdditionalFieldsById(additionalFields.getId(), "");
			
			AdditionalFields old_AdditionalFields= additionalFields.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=additionalFields.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_Code")+":"+valueParm[0];
			
			if (additionalFields.isNew()){ // for New record or new record into work flow
				
				if (!additionalFields.isWorkflow()){// With out Work flow only new records  
					if (befAdditionalFields !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (additionalFields.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befAdditionalFields !=null || tempAdditionalFields!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befAdditionalFields ==null || tempAdditionalFields!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!additionalFields.isWorkflow()){	// With out Work flow for update and delete
				
					if (befAdditionalFields ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (old_AdditionalFields!=null && !old_AdditionalFields.getLastMntOn().equals(befAdditionalFields.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempAdditionalFields==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (old_AdditionalFields!=null && !old_AdditionalFields.getLastMntOn().equals(tempAdditionalFields.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !additionalFields.isWorkflow()){
				additionalFields.setBefImage(befAdditionalFields);	
			}

			return auditDetail;
		}

		public void setAdditionalFieldValuesDAO(AdditionalFieldValuesDAO additionalFieldValuesDAO) {
	        this.additionalFieldValuesDAO = additionalFieldValuesDAO;
        }

		public AdditionalFieldValuesDAO getAdditionalFieldValuesDAO() {
	        return additionalFieldValuesDAO;
        }

}