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
 * FileName    		:  VASProductCategoryServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.vasproduct.impl;


import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.vasproduct.VASProductCategoryDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.vasproduct.VASProductCategory;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.vasproduct.VASProductCategoryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>VASProductCategory</b>.<br>
 * 
 */
public class VASProductCategoryServiceImpl extends GenericService<VASProductCategory> implements VASProductCategoryService {
	private static final Logger logger = Logger.getLogger(VASProductCategoryServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private VASProductCategoryDAO vASProductCategoryDAO;

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public VASProductCategoryDAO getVASProductCategoryDAO() {
		return vASProductCategoryDAO;
	}
	public void setVASProductCategoryDAO(VASProductCategoryDAO vASProductCategoryDAO) {
		this.vASProductCategoryDAO = vASProductCategoryDAO;
	}

	/**
	 * @return the vAS Product Category
	 */
	@Override
	public VASProductCategory getVASProductCategory() {
		return getVASProductCategoryDAO().getVASProductCategory();
	}

	/**
	 * @return the vASProductCategory for New Record
	 */
	@Override
	public VASProductCategory getNewVASProductCategory() {
		return getVASProductCategoryDAO().getNewVASProductCategory();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table VasProductCategory/VasProductCategory_Temp 
	 * 			by using VASProductCategoryDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using VASProductCategoryDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtVasProductCategory by using auditHeaderDAO.addAudit(auditHeader)
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
		VASProductCategory vASProductCategory = (VASProductCategory) auditHeader.getAuditDetail().getModelData();

		if (vASProductCategory.isWorkflow()) {
			tableType="_Temp";
		}

		if (vASProductCategory.isNew()) {
			getVASProductCategoryDAO().save(vASProductCategory,tableType);
		}else{
			getVASProductCategoryDAO().update(vASProductCategory,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table VasProductCategory by using VASProductCategoryDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtVasProductCategory by using auditHeaderDAO.addAudit(auditHeader)    
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

		VASProductCategory vASProductCategory = (VASProductCategory) auditHeader.getAuditDetail().getModelData();
		getVASProductCategoryDAO().delete(vASProductCategory,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getVASProductCategoryById fetch the details by using VASProductCategoryDAO's getVASProductCategoryById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VASProductCategory
	 */
	@Override
	public VASProductCategory getVASProductCategoryById(String id) {
		return getVASProductCategoryDAO().getVASProductCategoryById(id, "_View");
	}

	/**
	 * getVASProductCategoryById fetch the details by using VASProductCategoryDAO's getVASProductCategoryByActive
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return int
	 */
	@Override
	public int getVASProductCategoryByACtive(String id) {
		return getVASProductCategoryDAO().getVASProductCategoryByActive(id, "");
	}

	
	/**
	 * getApprovedVASProductCategoryById fetch the details by using VASProductCategoryDAO's getVASProductCategoryById method .
	 * with parameter id and type as blank. it fetches the approved records from the VasProductCategory.
	 * @param id (String)
	 * @return VASProductCategory
	 */
	public VASProductCategory getApprovedVASProductCategoryById(String id) {
		return getVASProductCategoryDAO().getVASProductCategoryById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getVASProductCategoryDAO().delete with parameters vASProductCategory,""
	 * 		b)  NEW		Add new record in to main table by using getVASProductCategoryDAO().save with parameters vASProductCategory,""
	 * 		c)  EDIT	Update record in the main table by using getVASProductCategoryDAO().update with parameters vASProductCategory,""
	 * 3)	Delete the record from the workFlow table by using getVASProductCategoryDAO().delete with parameters vASProductCategory,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtVasProductCategory by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtVasProductCategory by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		VASProductCategory vASProductCategory = new VASProductCategory("");
		BeanUtils.copyProperties((VASProductCategory) auditHeader.getAuditDetail().getModelData(), vASProductCategory);

		if (vASProductCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getVASProductCategoryDAO().delete(vASProductCategory,"");

		} else {
			vASProductCategory.setRoleCode("");
			vASProductCategory.setNextRoleCode("");
			vASProductCategory.setTaskId("");
			vASProductCategory.setNextTaskId("");
			vASProductCategory.setWorkflowId(0);

			if (vASProductCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				vASProductCategory.setRecordType("");
				getVASProductCategoryDAO().save(vASProductCategory,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				vASProductCategory.setRecordType("");
				getVASProductCategoryDAO().update(vASProductCategory,"");
			}
		}

		getVASProductCategoryDAO().delete(vASProductCategory,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(vASProductCategory);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getVASProductCategoryDAO().delete with parameters vASProductCategory,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtVasProductCategory by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		VASProductCategory vASProductCategory = (VASProductCategory) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getVASProductCategoryDAO().delete(vASProductCategory,"_Temp");

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
	 * 5)	for any mismatch conditions Fetch the error details from getVASProductCategoryDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		VASProductCategory vASProductCategory= (VASProductCategory) auditDetail.getModelData();

		VASProductCategory tempVASProductCategory= null;
		if (vASProductCategory.isWorkflow()){
			tempVASProductCategory = getVASProductCategoryDAO().getVASProductCategoryById(vASProductCategory.getId(), "_Temp");
		}
		VASProductCategory befVASProductCategory= getVASProductCategoryDAO().getVASProductCategoryById(vASProductCategory.getId(), "");

		VASProductCategory old_VASProductCategory= vASProductCategory.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=vASProductCategory.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_ProductCtg")+":"+valueParm[0];

		if (vASProductCategory.isNew()){ // for New record or new record into work flow

			if (!vASProductCategory.isWorkflow()){// With out Work flow only new records  
				if (befVASProductCategory !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
				}	
			}else{ // with work flow
				if (vASProductCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befVASProductCategory !=null || tempVASProductCategory!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
					}
				}else{ // if records not exists in the Main flow table
					if (befVASProductCategory ==null || tempVASProductCategory!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!vASProductCategory.isWorkflow()){	// With out Work flow for update and delete

				if (befVASProductCategory ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm));
				}else{
					if (old_VASProductCategory!=null && !old_VASProductCategory.getLastMntOn().equals(befVASProductCategory.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm));
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm));
						}
					}
				}
			}else{

				if (tempVASProductCategory==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}

				if (tempVASProductCategory != null  && old_VASProductCategory!=null && !old_VASProductCategory.getLastMntOn().equals(tempVASProductCategory.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !vASProductCategory.isWorkflow()){
			auditDetail.setBefImage(befVASProductCategory);	
		}

		return auditDetail;
	}
	
	

}