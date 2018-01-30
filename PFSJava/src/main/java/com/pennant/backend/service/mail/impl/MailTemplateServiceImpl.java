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
 * FileName    		:  MailTemplateServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2012    														*
 *                                                                  						*
 * Modified Date    :  04-10-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.mail.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.mail.MailTemplateService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.MailService;

/**
 * Service implementation for methods that depends on <b>MailTemplate</b>.<br>
 * 
 */
public class MailTemplateServiceImpl extends GenericService<MailTemplate> implements MailTemplateService {
	private static final Logger logger = Logger.getLogger(MailTemplateServiceImpl.class);

	private AuditHeaderDAO		auditHeaderDAO;
	private MailTemplateDAO		mailTemplateDAO;
	private SecurityUserDAO		securityUserDAO;
	
	@Autowired(required = false)
	private MailService			mailService;

	public MailTemplateServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public MailTemplateDAO getMailTemplateDAO() {
		return mailTemplateDAO;
	}
	public void setMailTemplateDAO(MailTemplateDAO mailTemplateDAO) {
		this.mailTemplateDAO = mailTemplateDAO;
	}

	public SecurityUserDAO getSecurityUserDAO() {
		return securityUserDAO;
	}
	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table Templates/Templates_Temp 
	 * 			by using MailTemplateDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using MailTemplateDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtTemplates by using auditHeaderDAO.addAudit(auditHeader)
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
		MailTemplate mailTemplate = (MailTemplate) auditHeader.getAuditDetail().getModelData();

		if (mailTemplate.isWorkflow()) {
			tableType="_Temp";
		}

		if (mailTemplate.isNew()) {
			getMailTemplateDAO().save(mailTemplate,tableType);
		}else{
			getMailTemplateDAO().update(mailTemplate,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table Templates by using MailTemplateDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtTemplates by using auditHeaderDAO.addAudit(auditHeader)    
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

		MailTemplate mailTemplate = (MailTemplate) auditHeader.getAuditDetail().getModelData();
		getMailTemplateDAO().delete(mailTemplate,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getMailTemplateById fetch the details by using MailTemplateDAO's getMailTemplateById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return MailTemplate
	 */
	@Override
	public MailTemplate getMailTemplateById(long id) {
		return getMailTemplateDAO().getMailTemplateById(id, "_View");
	}

	/**
	 * getApprovedMailTemplateById fetch the details by using MailTemplateDAO's getMailTemplateById method .
	 * with parameter id and type as blank. it fetches the approved records from the Templates.
	 * @param id (String)
	 * @return MailTemplate
	 */
	public MailTemplate getApprovedMailTemplateById(long id) {
		return getMailTemplateDAO().getMailTemplateById(id, "_AView");
	}

	/**
	 * getMailTemplates fetch the details by using MailTemplateDAO's getMailTemplates method .
	 *  it fetches the approved records from the Templates.
	 *
	 * @return MailTemplateList
	 */
	public List<MailTemplate> getMailTemplates() {
		return getMailTemplateDAO().getMailTemplates();
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getMailTemplateDAO().delete with parameters mailTemplate,""
	 * 		b)  NEW		Add new record in to main table by using getMailTemplateDAO().save with parameters mailTemplate,""
	 * 		c)  EDIT	Update record in the main table by using getMailTemplateDAO().update with parameters mailTemplate,""
	 * 3)	Delete the record from the workFlow table by using getMailTemplateDAO().delete with parameters mailTemplate,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtTemplates by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtTemplates by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		MailTemplate mailTemplate = new MailTemplate();
		BeanUtils.copyProperties((MailTemplate) auditHeader.getAuditDetail().getModelData(), mailTemplate);

		if (mailTemplate.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getMailTemplateDAO().delete(mailTemplate,"");
		} else {
			mailTemplate.setRoleCode("");
			mailTemplate.setNextRoleCode("");
			mailTemplate.setTaskId("");
			mailTemplate.setNextTaskId("");
			mailTemplate.setWorkflowId(0);

			if (mailTemplate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				mailTemplate.setRecordType("");
				getMailTemplateDAO().save(mailTemplate,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				mailTemplate.setRecordType("");
				getMailTemplateDAO().update(mailTemplate,"");
			}
		}

		getMailTemplateDAO().delete(mailTemplate,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(mailTemplate);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");		
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getMailTemplateDAO().delete 
	 * 		with parameters mailTemplate,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtTemplates by using auditHeaderDAO.addAudit(auditHeader) 
	 * 		for Work flow
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

		MailTemplate mailTemplate = (MailTemplate) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getMailTemplateDAO().delete(mailTemplate,"_Temp");
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
	 * 5)	for any mismatch conditions Fetch the error details from getMailTemplateDAO().getErrorDetail 
	 * 		with Error ID and language as parameters.
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
		MailTemplate mailTemplate= (MailTemplate) auditDetail.getModelData();

		MailTemplate tempMailTemplate= null;
		if (mailTemplate.isWorkflow()){
			tempMailTemplate = getMailTemplateDAO().getMailTemplateById(mailTemplate.getId(), "_Temp");
		}
		MailTemplate befMailTemplate= getMailTemplateDAO().getMailTemplateById(mailTemplate.getId(), "");
		MailTemplate oldMailTemplate= mailTemplate.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=mailTemplate.getTemplateCode();
		errParm[0]=PennantJavaUtil.getLabel("label_TemplateCode")+":"+valueParm[0];

		if (mailTemplate.isNew()){ // for New record or new record into work flow

			if (!mailTemplate.isWorkflow()){// With out Work flow only new records  
				if (befMailTemplate !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (mailTemplate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befMailTemplate !=null || tempMailTemplate!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befMailTemplate ==null || tempMailTemplate!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!mailTemplate.isWorkflow()){	// With out Work flow for update and delete

				if (befMailTemplate ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldMailTemplate!=null && !oldMailTemplate.getLastMntOn().equals(befMailTemplate.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempMailTemplate==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempMailTemplate!=null && oldMailTemplate!=null && !oldMailTemplate.getLastMntOn().equals(tempMailTemplate.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}
		
		if (getMailTemplateDAO().getMailTemplateByCode(mailTemplate.getTemplateCode(),mailTemplate.getId(), "") != 0) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014",
					errParm, valueParm), usrLanguage));
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !mailTemplate.isWorkflow()){
			auditDetail.setBefImage(befMailTemplate);	
		}
		logger.debug("Leaving");
		return auditDetail;
	}

	
	/**
	 * Method for call the ExternalServiceTask to send Mail.
	 * 
	 * @param custMailIdList
	 * @param templates
	 * @return
	 */
	@Override
	public void sendMail(List<MailTemplate> templates, String finReference) {
		logger.debug(Literal.ENTERING);
		
		mailService.sendEmail(templates, finReference);
		
		logger.debug(Literal.LEAVING);
	}

}