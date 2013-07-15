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
 * FileName    		:  DefermentHeaderServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.finance.DefermentHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.DefermentHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.DefermentHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>defermentHeader</b>.<br>
 * 
 */
public class DefermentHeaderServiceImpl extends GenericService<DefermentHeader> implements DefermentHeaderService {
	private final static Logger logger = Logger.getLogger(DefermentHeaderServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private DefermentHeaderDAO defermentHeaderDAO;

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
	 * @return the defermentHeaderDAO
	 */
	public DefermentHeaderDAO getDefermentHeaderDAO() {
		return defermentHeaderDAO;
	}
	/**
	 * @param defermentHeaderDAO the defermentHeaderDAO to set
	 */
	public void setdefermentHeaderDAO(DefermentHeaderDAO defermentHeaderDAO) {
		this.defermentHeaderDAO = defermentHeaderDAO;
	}

	/**
	 * @return the defermentHeader
	 */
	@Override
	public DefermentHeader getDefermentHeader(boolean isWIF) {
		return getDefermentHeaderDAO().getDefermentHeader(isWIF);
	}
	/**
	 * @return the defermentHeader for New Record
	 */
	@Override
	public DefermentHeader getNewDefermentHeader(boolean isWIF) {
		return getDefermentHeaderDAO().getNewDefermentHeader(isWIF);
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinDefermentHeader/FinDefermentHeader_Temp 
	 * 			by using defermentHeaderDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using defermentHeaderDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinDefermentHeader by using auditHeaderDAO.addAudit(auditHeader)
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
		DefermentHeader defermentHeader = (DefermentHeader) auditHeader.getAuditDetail().getModelData();
		
		if (defermentHeader.isWorkflow()) {
			tableType="_TEMP";
		}

		if (defermentHeader.isNew()) {
			getDefermentHeaderDAO().save(defermentHeader,tableType,isWIF);
		}else{
			getDefermentHeaderDAO().update(defermentHeader,tableType,isWIF);
		}

		//getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinDefermentHeader by using defermentHeaderDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinDefermentHeader by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		DefermentHeader defermentHeader = (DefermentHeader) auditHeader.getAuditDetail().getModelData();
		getDefermentHeaderDAO().delete(defermentHeader,"",isWIF);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getdefermentHeaderById fetch the details by using defermentHeaderDAO's getdefermentHeaderById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return defermentHeader
	 */
	
	@Override
	public DefermentHeader getDefermentHeaderById(String id,boolean isWIF) {
		return getDefermentHeaderDAO().getDefermentHeaderById(id,"_View",isWIF);
	}
	/**
	 * getApproveddefermentHeaderById fetch the details by using defermentHeaderDAO's getdefermentHeaderById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinDefermentHeader.
	 * @param id (String)
	 * @return defermentHeader
	 */
	
	public DefermentHeader getApprovedDefermentHeaderById(String id,boolean isWIF) {
		return getDefermentHeaderDAO().getDefermentHeaderById(id,"_AView",isWIF);
	}
		
	/**
	 * This method refresh the Record.
	 * @param defermentHeader (defermentHeader)
 	 * @return defermentHeader
	 */
	@Override
	public DefermentHeader refresh(DefermentHeader defermentHeader) {
		logger.debug("Entering");
		getDefermentHeaderDAO().refresh(defermentHeader);
		getDefermentHeaderDAO().initialize(defermentHeader);
		logger.debug("Leaving");
		return defermentHeader;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getdefermentHeaderDAO().delete with parameters defermentHeader,""
	 * 		b)  NEW		Add new record in to main table by using getdefermentHeaderDAO().save with parameters defermentHeader,""
	 * 		c)  EDIT	Update record in the main table by using getdefermentHeaderDAO().update with parameters defermentHeader,""
	 * 3)	Delete the record from the workFlow table by using getdefermentHeaderDAO().delete with parameters defermentHeader,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFinDefermentHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFinDefermentHeader by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		DefermentHeader defermentHeader = new DefermentHeader();
		BeanUtils.copyProperties((DefermentHeader) auditHeader.getAuditDetail().getModelData(), defermentHeader);

		if (defermentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getDefermentHeaderDAO().delete(defermentHeader,"",isWIF);
				
			} else {
				defermentHeader.setRoleCode("");
				defermentHeader.setNextRoleCode("");
				defermentHeader.setTaskId("");
				defermentHeader.setNextTaskId("");
				defermentHeader.setWorkflowId(0);
				
				if (defermentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					defermentHeader.setRecordType("");
					getDefermentHeaderDAO().save(defermentHeader,"",isWIF);
				} else {
					tranType=PennantConstants.TRAN_UPD;
					defermentHeader.setRecordType("");
					getDefermentHeaderDAO().update(defermentHeader,"",isWIF);
				}
			}
			
			getDefermentHeaderDAO().delete(defermentHeader,"_TEMP",isWIF);
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(defermentHeader);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getdefermentHeaderDAO().delete with parameters defermentHeader,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinDefermentHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			DefermentHeader defermentHeader = (DefermentHeader) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getDefermentHeaderDAO().delete(defermentHeader,"_TEMP",isWIF);
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getdefermentHeaderDAO().getErrorDetail with Error ID and language as parameters.
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
			DefermentHeader defermentHeader= (DefermentHeader) auditDetail.getModelData();
			
			DefermentHeader tempDefermentHeader= null;
			if (defermentHeader.isWorkflow()){
				tempDefermentHeader = getDefermentHeaderDAO().getDefermentHeaderById(defermentHeader.getId(), "_Temp",isWIF);
			}
			DefermentHeader befDefermentHeader= getDefermentHeaderDAO().getDefermentHeaderById(defermentHeader.getId(), "",isWIF);
			
			DefermentHeader old_DefermentHeader= defermentHeader.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=defermentHeader.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
			
			if (defermentHeader.isNew()){ // for New record or new record into work flow
				
				if (!defermentHeader.isWorkflow()){// With out Work flow only new records  
					if (befDefermentHeader !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (defermentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befDefermentHeader !=null || tempDefermentHeader!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befDefermentHeader ==null || tempDefermentHeader!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!defermentHeader.isWorkflow()){	// With out Work flow for update and delete
				
					if (befDefermentHeader ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (old_DefermentHeader!=null && !old_DefermentHeader.getLastMntOn().equals(befDefermentHeader.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempDefermentHeader==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (old_DefermentHeader!=null && !old_DefermentHeader.getLastMntOn().equals(tempDefermentHeader.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !defermentHeader.isWorkflow()){
				defermentHeader.setBefImage(befDefermentHeader);	
			}

			return auditDetail;
		}

}