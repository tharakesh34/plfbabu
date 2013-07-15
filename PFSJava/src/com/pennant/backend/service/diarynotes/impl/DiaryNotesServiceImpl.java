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
 * FileName    		:  DiaryNotesServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2011    														*
 *                                                                  						*
 * Modified Date    :  20-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.diarynotes.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.diarynotes.DiaryNotesDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.diarynotes.DiaryNotes;
import com.pennant.backend.service.diarynotes.DiaryNotesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>DiaryNotes</b>.<br>
 * 
 */
public class DiaryNotesServiceImpl implements DiaryNotesService {
	private final static Logger logger = Logger.getLogger(DiaryNotesServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private DiaryNotesDAO diaryNotesDAO;

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
	 * @return the diaryNotesDAO
	 */
	public DiaryNotesDAO getDiaryNotesDAO() {
		return diaryNotesDAO;
	}
	/**
	 * @param diaryNotesDAO the diaryNotesDAO to set
	 */
	public void setDiaryNotesDAO(DiaryNotesDAO diaryNotesDAO) {
		this.diaryNotesDAO = diaryNotesDAO;
	}

	/**
	 * @return the diaryNotes
	 */
	@Override
	public DiaryNotes getDiaryNotes() {
		return getDiaryNotesDAO().getDiaryNotes();
	}
	/**
	 * @return the diaryNotes for New Record
	 */
	@Override
	public DiaryNotes getNewDiaryNotes() {
		return getDiaryNotesDAO().getNewDiaryNotes();
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table DiaryNotes/DiaryNotes_Temp 
	 * 			by using DiaryNotesDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using DiaryNotesDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtDiaryNotes by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!nextProcess(auditHeader)){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		DiaryNotes diaryNotes = (DiaryNotes) auditHeader.getAuditDetail().getModelData();
		
		if (diaryNotes.isWorkflow()) {
			tableType="_TEMP";
		}

		if (diaryNotes.isNew()) {
			diaryNotes.setId(getDiaryNotesDAO().save(diaryNotes,tableType));
			auditHeader.getAuditDetail().setModelData(diaryNotes);
			auditHeader.setAuditReference(String.valueOf(diaryNotes.getSeqNo()));
		}else{
			getDiaryNotesDAO().update(diaryNotes,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table DiaryNotes by using DiaryNotesDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtDiaryNotes by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"delete");
		if (!nextProcess(auditHeader)){
			logger.debug("Leaving");
			return auditHeader;
		}
		
		DiaryNotes diaryNotes = (DiaryNotes) auditHeader.getAuditDetail().getModelData();
		getDiaryNotesDAO().delete(diaryNotes,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDiaryNotesById fetch the details by using DiaryNotesDAO's getDiaryNotesById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DiaryNotes
	 */
	
	@Override
	public DiaryNotes getDiaryNotesById(long id) {
		return getDiaryNotesDAO().getDiaryNotesById(id,"_View");
	}
	/**
	 * getApprovedDiaryNotesById fetch the details by using DiaryNotesDAO's getDiaryNotesById method .
	 * with parameter id and type as blank. it fetches the approved records from the DiaryNotes.
	 * @param id (int)
	 * @return DiaryNotes
	 */
	
	public DiaryNotes getApprovedDiaryNotesById(long id) {
		return getDiaryNotesDAO().getDiaryNotesById(id,"_AView");
	}	
		
	/**
	 * This method refresh the Record.
	 * @param DiaryNotes (diaryNotes)
 	 * @return diaryNotes
	 */
	@Override
	public DiaryNotes refresh(DiaryNotes diaryNotes) {
		logger.debug("Entering");
		getDiaryNotesDAO().refresh(diaryNotes);
		getDiaryNotesDAO().initialize(diaryNotes);
		logger.debug("Leaving");
		return diaryNotes;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getDiaryNotesDAO().delete with parameters diaryNotes,""
	 * 		b)  NEW		Add new record in to main table by using getDiaryNotesDAO().save with parameters diaryNotes,""
	 * 		c)  EDIT	Update record in the main table by using getDiaryNotesDAO().update with parameters diaryNotes,""
	 * 3)	Delete the record from the workFlow table by using getDiaryNotesDAO().delete with parameters diaryNotes,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtDiaryNotes by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtDiaryNotes by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!nextProcess(auditHeader)){
			return auditHeader;
		}

		DiaryNotes diaryNotes = new DiaryNotes();
		BeanUtils.copyProperties((DiaryNotes) auditHeader.getAuditDetail().getModelData(), diaryNotes);

		if (diaryNotes.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getDiaryNotesDAO().delete(diaryNotes,"");
				
			} else {
				diaryNotes.setRoleCode("");
				diaryNotes.setNextRoleCode("");
				diaryNotes.setTaskId("");
				diaryNotes.setNextTaskId("");
				diaryNotes.setWorkflowId(0);
				
				if (diaryNotes.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					diaryNotes.setRecordType("");
					getDiaryNotesDAO().save(diaryNotes,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					diaryNotes.setRecordType("");
					getDiaryNotesDAO().update(diaryNotes,"");
				}
			}
			
			getDiaryNotesDAO().delete(diaryNotes,"_TEMP");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(diaryNotes);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getDiaryNotesDAO().delete with parameters diaryNotes,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtDiaryNotes by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			
			auditHeader = businessValidation(auditHeader,"doReject");
			if (!nextProcess(auditHeader)){
				logger.debug("Leaving");
				return auditHeader;
			}

			DiaryNotes diaryNotes = (DiaryNotes) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getDiaryNotesDAO().delete(diaryNotes,"_TEMP");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getDiaryNotesDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)    
		 * @return auditHeader
		 */

		
		private AuditHeader businessValidation(AuditHeader auditHeader, String method){
			logger.debug("Entering");
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			logger.debug("Leaving");
			return auditHeader;
		}

		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
			logger.debug("Entering");
			
			DiaryNotes diaryNotes= (DiaryNotes) auditDetail.getModelData();
			
			DiaryNotes tempDiaryNotes= null;
			if (diaryNotes.isWorkflow()){
				tempDiaryNotes = getDiaryNotesDAO().getDiaryNotesById(diaryNotes.getId(), "_Temp");
			}
			DiaryNotes befDiaryNotes= getDiaryNotesDAO().getDiaryNotesById(diaryNotes.getId(), "");
			
			DiaryNotes old_DiaryNotes= diaryNotes.getBefImage();
			
			
			String[] errParm= new String[4];
			errParm[0]=PennantJavaUtil.getLabel("label_SeqNo");
			errParm[1]=String.valueOf(diaryNotes.getId());
			
			
			if (diaryNotes.isNew()){ // for New record or new record into work flow
				
				if (!diaryNotes.isWorkflow()){// With out Work flow only new records  
					if (befDiaryNotes !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}	
				}else{ // with work flow
					if (diaryNotes.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befDiaryNotes !=null){ // if records already exists in the main table
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
						}
					}else{ // if records not exists in the Main flow table
						if (befDiaryNotes ==null){
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
						}
					}
					if (tempDiaryNotes!=null ){ // if records already exists in the Work flow table 
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!diaryNotes.isWorkflow()){	// With out Work flow for update and delete
				
					if (befDiaryNotes ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
					}else{
						if (old_DiaryNotes!=null && !old_DiaryNotes.getLastMntOn().equals(befDiaryNotes.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
							}else{
								auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
							}
						}
					}
				}else{
					if (tempDiaryNotes==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
					
					if (old_DiaryNotes!=null && !old_DiaryNotes.getLastMntOn().equals(tempDiaryNotes.getLastMntOn())){ 
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}

			
			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !diaryNotes.isWorkflow()){
				diaryNotes.setBefImage(befDiaryNotes);	
			}

			return auditDetail;
		}


		/**
		 * nextProcess method do the following steps.
		 * if errorMessage List or OverideMessage size is more than 0 then return False else return true.  	
		 * @param AuditHeader (auditHeader)    
		 * @return boolean
		 */

		private boolean nextProcess(AuditHeader auditHeader){
			
			if (auditHeader.getErrorMessage()!=null  && auditHeader.getErrorMessage().size()>0){
				return false;
			}
			
			if (auditHeader.getOverideMessage()!=null && auditHeader.getOverideMessage().size()>0 && !auditHeader.isOveride()){
				return false;
			}
			return true; 
		}


		public ErrorDetails getErrorDetails(String errorCode,String language,String[] parm) {
			return getDiaryNotesDAO().getErrorDetail(errorCode, language,parm);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public List getDiaryNoteRecord() {
			return getDiaryNotesDAO().getDiaryNoteRecord();
		}
		
		public void updateForScheduled(DiaryNotes diaryNotes) {
			getDiaryNotesDAO().updateForScheduled(diaryNotes);
		}

}