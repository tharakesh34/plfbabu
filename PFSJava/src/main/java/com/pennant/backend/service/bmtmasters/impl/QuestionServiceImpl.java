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
 * FileName    		:  QuestionServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-11-2011    														*
 *                                                                  						*
 * Modified Date    :  21-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.bmtmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.QuestionDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.Question;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.bmtmasters.QuestionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Question</b>.<br>
 * 
 */
public class QuestionServiceImpl extends GenericService<Question> implements QuestionService {
	private static final Logger logger = Logger.getLogger(QuestionServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private QuestionDAO questionDAO;

	public QuestionServiceImpl() {
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
	 * @return the questionDAO
	 */
	public QuestionDAO getQuestionDAO() {
		return questionDAO;
	}
	/**
	 * @param questionDAO the questionDAO to set
	 */
	public void setQuestionDAO(QuestionDAO questionDAO) {
		this.questionDAO = questionDAO;
	}

	/**
	 * @return the question
	 */
	@Override
	public Question getQuestion() {
		return getQuestionDAO().getQuestion();
	}
	/**
	 * @return the question for New Record
	 */
	@Override
	public Question getNewQuestion() {
		return getQuestionDAO().getNewQuestion();
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table BMTQuestion/BMTQuestion_Temp 
	 * 			by using QuestionDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using QuestionDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtBMTQuestion by using auditHeaderDAO.addAudit(auditHeader)
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
		Question question = (Question) auditHeader.getAuditDetail().getModelData();
		
		if (question.isWorkflow()) {
			tableType="_Temp";
		}

		if (question.isNew()) {
			question.setId(getQuestionDAO().save(question,tableType));
			auditHeader.getAuditDetail().setModelData(question);
			auditHeader.setAuditReference(String.valueOf(question.getQuestionId()));
		}else{
			getQuestionDAO().update(question,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table BMTQuestion by using QuestionDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtBMTQuestion by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		Question question = (Question) auditHeader.getAuditDetail().getModelData();
		getQuestionDAO().delete(question,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getQuestionById fetch the details by using QuestionDAO's getQuestionById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Question
	 */
	
	@Override
	public Question getQuestionById(long id) {
		return getQuestionDAO().getQuestionById(id,"_View");
	}
	/**
	 * getApprovedQuestionById fetch the details by using QuestionDAO's getQuestionById method .
	 * with parameter id and type as blank. it fetches the approved records from the BMTQuestion.
	 * @param id (int)
	 * @return Question
	 */
	
	public Question getApprovedQuestionById(long id) {
		return getQuestionDAO().getQuestionById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getQuestionDAO().delete with
	 * parameters question,"" b) NEW Add new record in to main table by using getQuestionDAO().save with parameters
	 * question,"" c) EDIT Update record in the main table by using getQuestionDAO().update with parameters question,""
	 * 3) Delete the record from the workFlow table by using getQuestionDAO().delete with parameters question,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtBMTQuestion by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5) Audit the record in to AuditHeader and AdtBMTQuestion by using auditHeaderDAO.addAudit(auditHeader) based on
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

		Question question = new Question();
		BeanUtils.copyProperties((Question) auditHeader.getAuditDetail().getModelData(), question);

		if (question.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getQuestionDAO().delete(question, "");

		} else {
			question.setRoleCode("");
			question.setNextRoleCode("");
			question.setTaskId("");
			question.setNextTaskId("");
			question.setWorkflowId(0);

			if (question.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				question.setRecordType("");
				getQuestionDAO().save(question, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				question.setRecordType("");
				getQuestionDAO().update(question, "");
			}
		}

		getQuestionDAO().delete(question, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(question);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getQuestionDAO().delete with parameters question,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtBMTQuestion by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			Question question = (Question) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getQuestionDAO().delete(question,"_Temp");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getQuestionDAO().getErrorDetail with Error ID and language as parameters.
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
			Question question= (Question) auditDetail.getModelData();
			
			Question tempQuestion= null;
			if (question.isWorkflow()){
				tempQuestion = getQuestionDAO().getQuestionById(question.getId(), "_Temp");
			}
			Question befQuestion= getQuestionDAO().getQuestionById(question.getId(), "");
			
			Question oldQuestion= question.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=String.valueOf(question.getId());
			errParm[0]=PennantJavaUtil.getLabel("label_QuestionId")+":"+valueParm[0];
			
			if (question.isNew()){ // for New record or new record into work flow
				
				if (!question.isWorkflow()){// With out Work flow only new records  
					if (befQuestion !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (question.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befQuestion !=null || tempQuestion!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befQuestion ==null || tempQuestion!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!question.isWorkflow()){	// With out Work flow for update and delete
				
					if (befQuestion ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldQuestion!=null && !oldQuestion.getLastMntOn().equals(befQuestion.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempQuestion==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempQuestion!=null  && oldQuestion!=null && !oldQuestion.getLastMntOn().equals(tempQuestion.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !question.isWorkflow()){
				question.setBefImage(befQuestion);	
			}

			return auditDetail;
		}

}