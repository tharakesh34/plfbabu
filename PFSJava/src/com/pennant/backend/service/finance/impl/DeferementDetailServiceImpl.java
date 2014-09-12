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
 * FileName    		:  DeferementDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2011    														*
 *                                                                  						*
 * Modified Date    :  02-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance.impl;



import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.DeferementDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.DeferementDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.DeferementDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>DeferementDetail</b>.<br>
 * 
 */
public class DeferementDetailServiceImpl extends GenericService<DeferementDetail> implements DeferementDetailService {
	private final static Logger logger = Logger.getLogger(DeferementDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private DeferementDetailDAO deferementDetailDAO;

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
	 * @return the deferementDetailDAO
	 */
	public DeferementDetailDAO getDeferementDetailDAO() {
		return deferementDetailDAO;
	}
	/**
	 * @param deferementDetailDAO the deferementDetailDAO to set
	 */
	public void setDeferementDetailDAO(DeferementDetailDAO deferementDetailDAO) {
		this.deferementDetailDAO = deferementDetailDAO;
	}

	/**
	 * @return the deferementDetail
	 */
	@Override
	public DeferementDetail getDeferementDetail() {
		return getDeferementDetailDAO().getDeferementDetail();
	}
	/**
	 * @return the deferementDetail for New Record
	 */
	@Override
	public DeferementDetail getNewDeferementDetail() {
		return getDeferementDetailDAO().getNewDeferementDetail();
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinDefermentDetail/FinDefermentDetail_Temp 
	 * 			by using DeferementDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using DeferementDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinDefermentDetail by using auditHeaderDAO.addAudit(auditHeader)
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
		DeferementDetail deferementDetail = (DeferementDetail) auditHeader.getAuditDetail().getModelData();
		
		if (deferementDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (deferementDetail.isNew()) {
			getDeferementDetailDAO().save(deferementDetail,tableType);
		}else{
			getDeferementDetailDAO().update(deferementDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinDefermentDetail by using DeferementDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinDefermentDetail by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		DeferementDetail deferementDetail = (DeferementDetail) auditHeader.getAuditDetail().getModelData();
		getDeferementDetailDAO().delete(deferementDetail,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDeferementDetailById fetch the details by using DeferementDetailDAO's getDeferementDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DeferementDetail
	 */
	
	@Override
	public DeferementDetail getDeferementDetailById(String id) {
		return getDeferementDetailDAO().getDeferementDetailById(id,"_View");
	}
	/**
	 * getApprovedDeferementDetailById fetch the details by using DeferementDetailDAO's getDeferementDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinDefermentDetail.
	 * @param id (String)
	 * @return DeferementDetail
	 */
	
	public DeferementDetail getApprovedDeferementDetailById(String id) {
		return getDeferementDetailDAO().getDeferementDetailById(id,"_AView");
	}
		
	/**
	 * This method refresh the Record.
	 * @param DeferementDetail (deferementDetail)
 	 * @return deferementDetail
	 */
	@Override
	public DeferementDetail refresh(DeferementDetail deferementDetail) {
		logger.debug("Entering");
		getDeferementDetailDAO().refresh(deferementDetail);
		getDeferementDetailDAO().initialize(deferementDetail);
		logger.debug("Leaving");
		return deferementDetail;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getDeferementDetailDAO().delete with parameters deferementDetail,""
	 * 		b)  NEW		Add new record in to main table by using getDeferementDetailDAO().save with parameters deferementDetail,""
	 * 		c)  EDIT	Update record in the main table by using getDeferementDetailDAO().update with parameters deferementDetail,""
	 * 3)	Delete the record from the workFlow table by using getDeferementDetailDAO().delete with parameters deferementDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFinDefermentDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFinDefermentDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		DeferementDetail deferementDetail = new DeferementDetail();
		BeanUtils.copyProperties((DeferementDetail) auditHeader.getAuditDetail().getModelData(), deferementDetail);

		if (deferementDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getDeferementDetailDAO().delete(deferementDetail,"");
				
			} else {
				deferementDetail.setRoleCode("");
				deferementDetail.setNextRoleCode("");
				deferementDetail.setTaskId("");
				deferementDetail.setNextTaskId("");
				deferementDetail.setWorkflowId(0);
				
				if (deferementDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					deferementDetail.setRecordType("");
					getDeferementDetailDAO().save(deferementDetail,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					deferementDetail.setRecordType("");
					getDeferementDetailDAO().update(deferementDetail,"");
				}
			}
			
			getDeferementDetailDAO().delete(deferementDetail,"_TEMP");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(deferementDetail);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getDeferementDetailDAO().delete with parameters deferementDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinDefermentDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			DeferementDetail deferementDetail = (DeferementDetail) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getDeferementDetailDAO().delete(deferementDetail,"_TEMP");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getDeferementDetailDAO().getErrorDetail with Error ID and language as parameters.
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
			DeferementDetail deferementDetail= (DeferementDetail) auditDetail.getModelData();
			
			DeferementDetail tempDeferementDetail= null;
			if (deferementDetail.isWorkflow()){
				tempDeferementDetail = getDeferementDetailDAO().getDeferementDetailById(deferementDetail.getId(), "_Temp");
			}
			DeferementDetail befDeferementDetail= getDeferementDetailDAO().getDeferementDetailById(deferementDetail.getId(), "");
			
			DeferementDetail oldDeferementDetail= deferementDetail.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=deferementDetail.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
			
			if (deferementDetail.isNew()){ // for New record or new record into work flow
				
				if (!deferementDetail.isWorkflow()){// With out Work flow only new records  
					if (befDeferementDetail !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (deferementDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befDeferementDetail !=null || tempDeferementDetail!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befDeferementDetail ==null || tempDeferementDetail!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!deferementDetail.isWorkflow()){	// With out Work flow for update and delete
				
					if (befDeferementDetail ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldDeferementDetail!=null && !oldDeferementDetail.getLastMntOn().equals(befDeferementDetail.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempDeferementDetail==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (oldDeferementDetail!=null && !oldDeferementDetail.getLastMntOn().equals(tempDeferementDetail.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !deferementDetail.isWorkflow()){
				deferementDetail.setBefImage(befDeferementDetail);	
			}

			return auditDetail;
		}

}