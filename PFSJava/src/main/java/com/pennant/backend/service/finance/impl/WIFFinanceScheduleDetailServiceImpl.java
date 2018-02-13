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
 * FileName    		:  WIFFinanceScheduleDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.finance.WIFFinanceScheduleDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.WIFFinanceScheduleDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>WIFFinanceScheduleDetail</b>.<br>
 * 
 */
public class WIFFinanceScheduleDetailServiceImpl extends GenericService<FinanceScheduleDetail> implements WIFFinanceScheduleDetailService {
	private static final Logger logger = Logger.getLogger(WIFFinanceScheduleDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private WIFFinanceScheduleDetailDAO wIFFinanceScheduleDetailDAO;

	public WIFFinanceScheduleDetailServiceImpl() {
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
	 * @return the wIFFinanceScheduleDetailDAO
	 */
	public WIFFinanceScheduleDetailDAO getWIFFinanceScheduleDetailDAO() {
		return wIFFinanceScheduleDetailDAO;
	}
	/**
	 * @param wIFFinanceScheduleDetailDAO the wIFFinanceScheduleDetailDAO to set
	 */
	public void setWIFFinanceScheduleDetailDAO(WIFFinanceScheduleDetailDAO wIFFinanceScheduleDetailDAO) {
		this.wIFFinanceScheduleDetailDAO = wIFFinanceScheduleDetailDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table WIFFinScheduleDetails/WIFFinScheduleDetails_Temp 
	 * 			by using WIFFinanceScheduleDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using WIFFinanceScheduleDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtWIFFinScheduleDetails by using auditHeaderDAO.addAudit(auditHeader)
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
		FinanceScheduleDetail wIFFinanceScheduleDetail = (FinanceScheduleDetail) auditHeader.getAuditDetail().getModelData();
		
		if (wIFFinanceScheduleDetail.isWorkflow()) {
			tableType="_Temp";
		}

		if (wIFFinanceScheduleDetail.isNew()) {
			getWIFFinanceScheduleDetailDAO().save(wIFFinanceScheduleDetail,tableType);
		}else{
			getWIFFinanceScheduleDetailDAO().update(wIFFinanceScheduleDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table WIFFinScheduleDetails by using WIFFinanceScheduleDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtWIFFinScheduleDetails by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		FinanceScheduleDetail wIFFinanceScheduleDetail = (FinanceScheduleDetail) auditHeader.getAuditDetail().getModelData();
		getWIFFinanceScheduleDetailDAO().delete(wIFFinanceScheduleDetail,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getWIFFinanceScheduleDetailById fetch the details by using WIFFinanceScheduleDetailDAO's getWIFFinanceScheduleDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return WIFFinanceScheduleDetail
	 */
	
	@Override
	public FinanceScheduleDetail getWIFFinanceScheduleDetailById(String id) {
		return getWIFFinanceScheduleDetailDAO().getWIFFinanceScheduleDetailById(id,"_View");
	}
	/**
	 * getApprovedWIFFinanceScheduleDetailById fetch the details by using WIFFinanceScheduleDetailDAO's getWIFFinanceScheduleDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the WIFFinScheduleDetails.
	 * @param id (String)
	 * @return WIFFinanceScheduleDetail
	 */
	
	public FinanceScheduleDetail getApprovedWIFFinanceScheduleDetailById(String id) {
		return getWIFFinanceScheduleDetailDAO().getWIFFinanceScheduleDetailById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using
	 * getWIFFinanceScheduleDetailDAO().delete with parameters wIFFinanceScheduleDetail,"" b) NEW Add new record in to
	 * main table by using getWIFFinanceScheduleDetailDAO().save with parameters wIFFinanceScheduleDetail,"" c) EDIT
	 * Update record in the main table by using getWIFFinanceScheduleDetailDAO().update with parameters
	 * wIFFinanceScheduleDetail,"" 3) Delete the record from the workFlow table by using
	 * getWIFFinanceScheduleDetailDAO().delete with parameters wIFFinanceScheduleDetail,"_Temp" 4) Audit the record in
	 * to AuditHeader and AdtWIFFinScheduleDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit
	 * the record in to AuditHeader and AdtWIFFinScheduleDetails by using auditHeaderDAO.addAudit(auditHeader) based on
	 * the transaction Type.
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

		FinanceScheduleDetail wIFFinanceScheduleDetail = new FinanceScheduleDetail();
		BeanUtils.copyProperties((FinanceScheduleDetail) auditHeader.getAuditDetail().getModelData(),
				wIFFinanceScheduleDetail);

		if (wIFFinanceScheduleDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getWIFFinanceScheduleDetailDAO().delete(wIFFinanceScheduleDetail, "");

		} else {
			wIFFinanceScheduleDetail.setRoleCode("");
			wIFFinanceScheduleDetail.setNextRoleCode("");
			wIFFinanceScheduleDetail.setTaskId("");
			wIFFinanceScheduleDetail.setNextTaskId("");
			wIFFinanceScheduleDetail.setWorkflowId(0);

			if (wIFFinanceScheduleDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				wIFFinanceScheduleDetail.setRecordType("");
				getWIFFinanceScheduleDetailDAO().save(wIFFinanceScheduleDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				wIFFinanceScheduleDetail.setRecordType("");
				getWIFFinanceScheduleDetailDAO().update(wIFFinanceScheduleDetail, "");
			}
		}

		getWIFFinanceScheduleDetailDAO().delete(wIFFinanceScheduleDetail, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(wIFFinanceScheduleDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getWIFFinanceScheduleDetailDAO().delete with parameters wIFFinanceScheduleDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtWIFFinScheduleDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			FinanceScheduleDetail wIFFinanceScheduleDetail = (FinanceScheduleDetail) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getWIFFinanceScheduleDetailDAO().delete(wIFFinanceScheduleDetail,"_Temp");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getWIFFinanceScheduleDetailDAO().getErrorDetail with Error ID and language as parameters.
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
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			FinanceScheduleDetail wIFFinanceScheduleDetail= (FinanceScheduleDetail) auditDetail.getModelData();
			
			FinanceScheduleDetail tempWIFFinanceScheduleDetail= null;
			if (wIFFinanceScheduleDetail.isWorkflow()){
				tempWIFFinanceScheduleDetail = getWIFFinanceScheduleDetailDAO().getWIFFinanceScheduleDetailById(wIFFinanceScheduleDetail.getId(), "_Temp");
			}
			FinanceScheduleDetail befWIFFinanceScheduleDetail= getWIFFinanceScheduleDetailDAO().getWIFFinanceScheduleDetailById(wIFFinanceScheduleDetail.getId(), "");
			
			FinanceScheduleDetail oldWIFFinanceScheduleDetail= wIFFinanceScheduleDetail.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=wIFFinanceScheduleDetail.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
			
			if (wIFFinanceScheduleDetail.isNew()){ // for New record or new record into work flow
				
				if (!wIFFinanceScheduleDetail.isWorkflow()){// With out Work flow only new records  
					if (befWIFFinanceScheduleDetail !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (wIFFinanceScheduleDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befWIFFinanceScheduleDetail !=null || tempWIFFinanceScheduleDetail!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befWIFFinanceScheduleDetail ==null || tempWIFFinanceScheduleDetail!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!wIFFinanceScheduleDetail.isWorkflow()){	// With out Work flow for update and delete
				
					if (befWIFFinanceScheduleDetail ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldWIFFinanceScheduleDetail!=null && !oldWIFFinanceScheduleDetail.getLastMntOn().equals(befWIFFinanceScheduleDetail.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempWIFFinanceScheduleDetail==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempWIFFinanceScheduleDetail!=null && oldWIFFinanceScheduleDetail!=null && !oldWIFFinanceScheduleDetail.getLastMntOn().equals(tempWIFFinanceScheduleDetail.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !wIFFinanceScheduleDetail.isWorkflow()){
				wIFFinanceScheduleDetail.setBefImage(befWIFFinanceScheduleDetail);	
			}

			return auditDetail;
		}

}