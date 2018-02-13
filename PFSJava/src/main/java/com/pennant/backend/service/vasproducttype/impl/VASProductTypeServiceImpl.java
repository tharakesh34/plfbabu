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
 * FileName    		:  VASProductTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-01-2017    														*
 *                                                                  						*
 * Modified Date    :  09-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-01-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.service.vasproducttype.impl;


import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.vasproducttype.VASProductTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.vasproducttype.VASProductType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.vasproducttype.VASProductTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>VASProductType</b>.<br>
 * 
 */
public class VASProductTypeServiceImpl extends GenericService<VASProductType> implements VASProductTypeService {
	private static final Logger logger = Logger.getLogger(VASProductTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private VASProductTypeDAO vASProductTypeDAO;

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public VASProductTypeDAO getVASProductTypeDAO() {
		return vASProductTypeDAO;
	}
	public void setVASProductTypeDAO(VASProductTypeDAO vASProductTypeDAO) {
		this.vASProductTypeDAO = vASProductTypeDAO;
	}

	/**
	 * @return the vASProductType
	 */
	@Override
	public VASProductType getVASProductType() {
		return getVASProductTypeDAO().getVASProductType();
	}

	/**
	 * @return the vASProductType for New Record
	 */
	@Override
	public VASProductType getNewVASProductType() {
		return getVASProductTypeDAO().getNewVASProductType();
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table VasProductType/VasProductType_Temp 
	 * 			by using VASProductTypeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using VASProductTypeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtVasProductType by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
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
		VASProductType vASProductType = (VASProductType) auditHeader.getAuditDetail().getModelData();

		if (vASProductType.isWorkflow()) {
			tableType="_Temp";
		}

		if (vASProductType.isNew()) {
			getVASProductTypeDAO().save(vASProductType,tableType);
		}else{
			getVASProductTypeDAO().update(vASProductType,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table VasProductType by using VASProductTypeDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtVasProductType by using auditHeaderDAO.addAudit(auditHeader)    
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

		VASProductType vASProductType = (VASProductType) auditHeader.getAuditDetail().getModelData();
		getVASProductTypeDAO().delete(vASProductType,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getVASProductTypeById fetch the details by using VASProductTypeDAO's getVASProductTypeById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VASProductType
	 */
	@Override
	public VASProductType getVASProductTypeById(String id) {
		return getVASProductTypeDAO().getVASProductTypeById(id,"_View");
	}

	/**
	 * getApprovedVASProductTypeById fetch the details by using VASProductTypeDAO's getVASProductTypeById method .
	 * with parameter id and type as blank. it fetches the approved records from the VasProductType.
	 * @param id (String)
	 * @return VASProductType
	 */
	public VASProductType getApprovedVASProductTypeById(String id) {
		return getVASProductTypeDAO().getVASProductTypeById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getVASProductTypeDAO().delete with parameters vASProductType,""
	 * 		b)  NEW		Add new record in to main table by using getVASProductTypeDAO().save with parameters vASProductType,""
	 * 		c)  EDIT	Update record in the main table by using getVASProductTypeDAO().update with parameters vASProductType,""
	 * 3)	Delete the record from the workFlow table by using getVASProductTypeDAO().delete with parameters vASProductType,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtVasProductType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtVasProductType by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		VASProductType vASProductType = new VASProductType("");
		BeanUtils.copyProperties((VASProductType) auditHeader.getAuditDetail().getModelData(), vASProductType);

		if (vASProductType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getVASProductTypeDAO().delete(vASProductType,"");
		} else {
			vASProductType.setRoleCode("");
			vASProductType.setNextRoleCode("");
			vASProductType.setTaskId("");
			vASProductType.setNextTaskId("");
			vASProductType.setWorkflowId(0);

			if (vASProductType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				vASProductType.setRecordType("");
				getVASProductTypeDAO().save(vASProductType,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				vASProductType.setRecordType("");
				getVASProductTypeDAO().update(vASProductType,"");
			}
		}

		getVASProductTypeDAO().delete(vASProductType,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(vASProductType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getVASProductTypeDAO().delete with parameters vASProductType,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtVasProductType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		VASProductType vASProductType = (VASProductType) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getVASProductTypeDAO().delete(vASProductType,"_Temp");
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps.
	 * 1)	validate the audit detail 
	 * 2)	if any error/Warnings  then assign the to auditHeader
	 * 3)   identify the nextprocess
	 *  
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
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
	 * Validation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getVASProductTypeDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		VASProductType vASProductType= (VASProductType) auditDetail.getModelData();

		VASProductType tempVASProductType= null;
		if (vASProductType.isWorkflow()){
			tempVASProductType = getVASProductTypeDAO().getVASProductTypeById(vASProductType.getId(), "_Temp");
		}
		VASProductType befVASProductType= getVASProductTypeDAO().getVASProductTypeById(vASProductType.getId(), "");
		VASProductType old_VASProductType= vASProductType.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=vASProductType.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_ProductType")+":"+valueParm[0];

		if (vASProductType.isNew()){ // for New record or new record into work flow

			if (!vASProductType.isWorkflow()){// With out Work flow only new records  
				if (befVASProductType !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
				}	
			}else{ // with work flow
				if (vASProductType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befVASProductType !=null || tempVASProductType!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
					}
				}else{ // if records not exists in the Main flow table
					if (befVASProductType ==null || tempVASProductType!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!vASProductType.isWorkflow()){	// With out Work flow for update and delete

				if (befVASProductType ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm));
				}else{
					if (old_VASProductType!=null && !old_VASProductType.getLastMntOn().equals(befVASProductType.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm));
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm));
						}
					}
				}
			}else{

				if (tempVASProductType==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}

				if (tempVASProductType != null  && old_VASProductType!=null && !old_VASProductType.getLastMntOn().equals(tempVASProductType.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !vASProductType.isWorkflow()){
			auditDetail.setBefImage(befVASProductType);	
		}

		return auditDetail;
	}
	@Override
	public int getVASProductTypeByActive(String productType, String type) {
		return getVASProductTypeDAO().getVASProductTypeByActive(productType,"");
	}

}