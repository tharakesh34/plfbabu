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
 * FileName    		:  FinCreditRevSubCategoryServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-11-2013    														*
 *                                                                  						*
 * Modified Date    :  13-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-11-2013       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.customermasters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.FinCreditRevSubCategoryDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.FinCreditRevSubCategoryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>FinCreditRevSubCategory</b>.<br>
 * 
 */
public class FinCreditRevSubCategoryServiceImpl extends GenericService<FinCreditRevSubCategory> implements FinCreditRevSubCategoryService {
	private static final Logger logger = Logger.getLogger(FinCreditRevSubCategoryServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private FinCreditRevSubCategoryDAO finCreditRevSubCategoryDAO;

	public FinCreditRevSubCategoryServiceImpl() {
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
	 * @return the finCreditRevSubCategoryDAO
	 */
	public FinCreditRevSubCategoryDAO getFinCreditRevSubCategoryDAO() {
		return finCreditRevSubCategoryDAO;
	}
	/**
	 * @param finCreditRevSubCategoryDAO the finCreditRevSubCategoryDAO to set
	 */
	public void setFinCreditRevSubCategoryDAO(FinCreditRevSubCategoryDAO finCreditRevSubCategoryDAO) {
		this.finCreditRevSubCategoryDAO = finCreditRevSubCategoryDAO;
	}

	/**
	 * @return the finCreditRevSubCategory
	 */
	@Override
	public FinCreditRevSubCategory getFinCreditRevSubCategory() {
		return getFinCreditRevSubCategoryDAO().getFinCreditRevSubCategory();
	}
	/**
	 * @return the finCreditRevSubCategory for New Record
	 */
	@Override
	public FinCreditRevSubCategory getNewFinCreditRevSubCategory() {
		return getFinCreditRevSubCategoryDAO().getNewFinCreditRevSubCategory();
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinCreditRevSubCategory/FinCreditRevSubCategory_Temp 
	 * 			by using FinCreditRevSubCategoryDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using FinCreditRevSubCategoryDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinCreditRevSubCategory by using auditHeaderDAO.addAudit(auditHeader)
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
		FinCreditRevSubCategory finCreditRevSubCategory = (FinCreditRevSubCategory) auditHeader.getAuditDetail().getModelData();
		
		if (finCreditRevSubCategory.isWorkflow()) {
			tableType="_Temp";
		}

		if (finCreditRevSubCategory.isNew()) {
			getFinCreditRevSubCategoryDAO().save(finCreditRevSubCategory,tableType);
		}else{
			getFinCreditRevSubCategoryDAO().update(finCreditRevSubCategory,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinCreditRevSubCategory by using FinCreditRevSubCategoryDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinCreditRevSubCategory by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		FinCreditRevSubCategory finCreditRevSubCategory = (FinCreditRevSubCategory) auditHeader.getAuditDetail().getModelData();
		getFinCreditRevSubCategoryDAO().delete(finCreditRevSubCategory,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFinCreditRevSubCategoryById fetch the details by using FinCreditRevSubCategoryDAO's getFinCreditRevSubCategoryById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinCreditRevSubCategory
	 */
	
	@Override
	public FinCreditRevSubCategory getFinCreditRevSubCategoryById(String id) {
		return getFinCreditRevSubCategoryDAO().getFinCreditRevSubCategoryById(id,"_View");
	}
	/**
	 * getApprovedFinCreditRevSubCategoryById fetch the details by using FinCreditRevSubCategoryDAO's getFinCreditRevSubCategoryById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinCreditRevSubCategory.
	 * @param id (String)
	 * @return FinCreditRevSubCategory
	 */
	
	public FinCreditRevSubCategory getApprovedFinCreditRevSubCategoryById(String id) {
		return getFinCreditRevSubCategoryDAO().getFinCreditRevSubCategoryById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinCreditRevSubCategoryDAO().delete
	 * with parameters finCreditRevSubCategory,"" b) NEW Add new record in to main table by using
	 * getFinCreditRevSubCategoryDAO().save with parameters finCreditRevSubCategory,"" c) EDIT Update record in the main
	 * table by using getFinCreditRevSubCategoryDAO().update with parameters finCreditRevSubCategory,"" 3) Delete the
	 * record from the workFlow table by using getFinCreditRevSubCategoryDAO().delete with parameters
	 * finCreditRevSubCategory,"_Temp" 4) Audit the record in to AuditHeader and AdtFinCreditRevSubCategory by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and
	 * AdtFinCreditRevSubCategory by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		FinCreditRevSubCategory finCreditRevSubCategory = new FinCreditRevSubCategory();
		BeanUtils.copyProperties((FinCreditRevSubCategory) auditHeader.getAuditDetail().getModelData(),
				finCreditRevSubCategory);

		if (finCreditRevSubCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getFinCreditRevSubCategoryDAO().delete(finCreditRevSubCategory, "");

		} else {
			finCreditRevSubCategory.setRoleCode("");
			finCreditRevSubCategory.setNextRoleCode("");
			finCreditRevSubCategory.setTaskId("");
			finCreditRevSubCategory.setNextTaskId("");
			finCreditRevSubCategory.setWorkflowId(0);

			if (finCreditRevSubCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				finCreditRevSubCategory.setRecordType("");
				getFinCreditRevSubCategoryDAO().save(finCreditRevSubCategory, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				finCreditRevSubCategory.setRecordType("");
				getFinCreditRevSubCategoryDAO().update(finCreditRevSubCategory, "");
			}
		}

		getFinCreditRevSubCategoryDAO().delete(finCreditRevSubCategory, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finCreditRevSubCategory);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getFinCreditRevSubCategoryDAO().delete with parameters finCreditRevSubCategory,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinCreditRevSubCategory by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			FinCreditRevSubCategory finCreditRevSubCategory = (FinCreditRevSubCategory) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getFinCreditRevSubCategoryDAO().delete(finCreditRevSubCategory,"_Temp");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getFinCreditRevSubCategoryDAO().getErrorDetail with Error ID and language as parameters.
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

		public List<AuditDetail> finCreditRevSubCategoryListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
			
			if(auditDetails!=null && auditDetails.size()>0){
				List<AuditDetail> details = new ArrayList<AuditDetail>();
				for (int i = 0; i < auditDetails.size(); i++) {
					AuditDetail auditDetail =   validation(auditDetails.get(i), method, usrLanguage);
					details.add(auditDetail); 		
				}
				return details;
			}
			return new ArrayList<AuditDetail>();
		}
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			FinCreditRevSubCategory finCreditRevSubCategory= (FinCreditRevSubCategory) auditDetail.getModelData();
			
			FinCreditRevSubCategory tempFinCreditRevSubCategory= null;
			if (finCreditRevSubCategory.isWorkflow()){
				tempFinCreditRevSubCategory = getFinCreditRevSubCategoryDAO().getFinCreditRevSubCategoryById(finCreditRevSubCategory.getId(), "_Temp");
			}
			FinCreditRevSubCategory befFinCreditRevSubCategory= getFinCreditRevSubCategoryDAO().getFinCreditRevSubCategoryById(finCreditRevSubCategory.getId(), "");
			
			FinCreditRevSubCategory oldFinCreditRevSubCategory= finCreditRevSubCategory.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=finCreditRevSubCategory.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_SubCategoryCode")+":"+valueParm[0];
			finCreditRevSubCategory.setWorkflowId(0);
			if (finCreditRevSubCategory.isNew()){ // for New record or new record into work flow
				
				if (!finCreditRevSubCategory.isWorkflow()){// With out Work flow only new records  
					if (befFinCreditRevSubCategory !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (finCreditRevSubCategory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befFinCreditRevSubCategory !=null || tempFinCreditRevSubCategory!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befFinCreditRevSubCategory ==null || tempFinCreditRevSubCategory!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!finCreditRevSubCategory.isWorkflow()){	// With out Work flow for update and delete
				
					if (befFinCreditRevSubCategory ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldFinCreditRevSubCategory!=null && !oldFinCreditRevSubCategory.getLastMntOn().equals(befFinCreditRevSubCategory.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempFinCreditRevSubCategory==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempFinCreditRevSubCategory!=null && oldFinCreditRevSubCategory!=null && !oldFinCreditRevSubCategory.getLastMntOn().equals(tempFinCreditRevSubCategory.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !finCreditRevSubCategory.isWorkflow()){
				finCreditRevSubCategory.setBefImage(befFinCreditRevSubCategory);	
			}

			return auditDetail;
		}

}