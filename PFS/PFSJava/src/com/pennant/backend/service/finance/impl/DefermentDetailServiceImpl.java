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
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.DefermentDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>DeferementDetail</b>.<br>
 * 
 */
public class DefermentDetailServiceImpl extends GenericService<DefermentDetail> implements DefermentDetailService {
	private final static Logger logger = Logger.getLogger(DefermentDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private DefermentDetailDAO defermentDetailDAO;

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
	public DefermentDetailDAO getDefermentDetailDAO() {
		return defermentDetailDAO;
	}
	/**
	 * @param defermentDetailDAO the deferementDetailDAO to set
	 */
	public void setDefermentDetailDAO(DefermentDetailDAO defermentDetailDAO) {
		this.defermentDetailDAO = defermentDetailDAO;
	}

	/**
	 * @return the deferementDetail
	 */
	@Override
	public DefermentDetail getDefermentDetail(boolean isWIF) {
		return getDefermentDetailDAO().getDefermentDetail(isWIF);
	}
	/**
	 * @return the deferementDetail for New Record
	 */
	@Override
	public DefermentDetail getNewDefermentDetail(boolean isWIF) {
		return getDefermentDetailDAO().getNewDefermentDetail(isWIF);
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
	public AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean isWIF) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		DefermentDetail defermentDetail = (DefermentDetail) auditHeader.getAuditDetail().getModelData();
		
		if (defermentDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (defermentDetail.isNew()) {
			getDefermentDetailDAO().save(defermentDetail,tableType,isWIF);
		}else{
			getDefermentDetailDAO().update(defermentDetail,tableType,isWIF);
		}

		//getAuditHeaderDAO().addAudit(auditHeader);
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
	public AuditHeader delete(AuditHeader auditHeader,boolean isWIF) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete",isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		DefermentDetail defermentDetail = (DefermentDetail) auditHeader.getAuditDetail().getModelData();
		getDefermentDetailDAO().delete(defermentDetail,"",isWIF);
		
		//getAuditHeaderDAO().addAudit(auditHeader);
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
	public DefermentDetail getDefermentDetailById(String id,Date schdDate,boolean isWIF) {
		return getDefermentDetailDAO().getDefermentDetailById(id,schdDate, "_View",isWIF);
	}
	/**
	 * getApprovedDeferementDetailById fetch the details by using DeferementDetailDAO's getDeferementDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinDefermentDetail.
	 * @param id (String)
	 * @return DeferementDetail
	 */
	
	public DefermentDetail getApprovedDefermentDetailById(String id,Date schdDate,boolean isWIF) {
		return getDefermentDetailDAO().getDefermentDetailById(id, schdDate, "_AView",isWIF);
	}
		
	/**
	 * This method refresh the Record.
	 * @param DeferementDetail (deferementDetail)
 	 * @return deferementDetail
	 */
	@Override
	public DefermentDetail refresh(DefermentDetail defermentDetail) {
		logger.debug("Entering");
		getDefermentDetailDAO().refresh(defermentDetail);
		getDefermentDetailDAO().initialize(defermentDetail);
		logger.debug("Leaving");
		return defermentDetail;
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

	public AuditHeader doApprove(AuditHeader auditHeader,boolean isWIF) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove",isWIF);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		DefermentDetail defermentDetail = new DefermentDetail();
		BeanUtils.copyProperties((DefermentDetail) auditHeader.getAuditDetail().getModelData(), defermentDetail);

		if (defermentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getDefermentDetailDAO().delete(defermentDetail,"",isWIF);
				
			} else {
				defermentDetail.setRoleCode("");
				defermentDetail.setNextRoleCode("");
				defermentDetail.setTaskId("");
				defermentDetail.setNextTaskId("");
				defermentDetail.setWorkflowId(0);
				
				if (defermentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					defermentDetail.setRecordType("");
					getDefermentDetailDAO().save(defermentDetail,"",isWIF);
				} else {
					tranType=PennantConstants.TRAN_UPD;
					defermentDetail.setRecordType("");
					getDefermentDetailDAO().update(defermentDetail,"",isWIF);
				}
			}
			
			getDefermentDetailDAO().delete(defermentDetail,"_TEMP",isWIF);
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(defermentDetail);
			
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
	
		public AuditHeader  doReject(AuditHeader auditHeader,boolean isWIF) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doReject",isWIF);
			if (!auditHeader.isNextProcess()) {
				logger.debug("Leaving");
				return auditHeader;
			}

			DefermentDetail defermentDetail = (DefermentDetail) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getDefermentDetailDAO().delete(defermentDetail,"_TEMP",isWIF);
			
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

		
		private AuditHeader businessValidation(AuditHeader auditHeader, String method,boolean isWIF){
			logger.debug("Entering");
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,isWIF);
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);
			logger.debug("Leaving");
			return auditHeader;
		}

		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean isWIF){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
			DefermentDetail defermentDetail= (DefermentDetail) auditDetail.getModelData();
			
			DefermentDetail tempDeferementDetail= null;
			if (defermentDetail.isWorkflow()){
				tempDeferementDetail = getDefermentDetailDAO().getDefermentDetailById(defermentDetail.getId(),defermentDetail.getDeferedSchdDate(), "_Temp",isWIF);
			}
			DefermentDetail befDefermentDetail= getDefermentDetailDAO().getDefermentDetailById(defermentDetail.getId(),defermentDetail.getDeferedSchdDate(), "",isWIF);
			
			DefermentDetail old_DefermentDetail= defermentDetail.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=defermentDetail.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
			
			if (defermentDetail.isNew()){ // for New record or new record into work flow
				
				if (!defermentDetail.isWorkflow()){// With out Work flow only new records  
					if (befDefermentDetail !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (defermentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befDefermentDetail !=null || tempDeferementDetail!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befDefermentDetail ==null || tempDeferementDetail!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!defermentDetail.isWorkflow()){	// With out Work flow for update and delete
				
					if (befDefermentDetail ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (old_DefermentDetail!=null && !old_DefermentDetail.getLastMntOn().equals(befDefermentDetail.getLastMntOn())){
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
					
					if (old_DefermentDetail!=null && !old_DefermentDetail.getLastMntOn().equals(tempDeferementDetail.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !defermentDetail.isWorkflow()){
				defermentDetail.setBefImage(befDefermentDetail);	
			}

			return auditDetail;
		}

}