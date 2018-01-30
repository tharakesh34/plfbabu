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
 * FileName    		:  ExtendedFieldHeaderServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.staticparms.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;

import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.staticparms.ExtendedFieldHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>ExtendedFieldHeader</b>.<br>
 * 
 */
public class ExtendedFieldHeaderServiceImpl extends GenericService<ExtendedFieldHeader> implements ExtendedFieldHeaderService {
	private static final Logger logger = Logger.getLogger(ExtendedFieldHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;
	private String excludeFields = "extendedFieldDetails";

	public ExtendedFieldHeaderServiceImpl() {
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

	public ExtendedFieldHeaderDAO getExtendedFieldHeaderDAO() {
		return extendedFieldHeaderDAO;
	}
	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table ExtendedFieldHeader/ExtendedFieldHeader_Temp 
	 * 			by using ExtendedFieldHeaderDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ExtendedFieldHeaderDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtExtendedFieldHeader by using auditHeaderDAO.addAudit(auditHeader)
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
		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();

		if (extendedFieldHeader.isWorkflow()) {
			tableType="_Temp";
		}

		if (extendedFieldHeader.isNew()) {
			extendedFieldHeader.setId(getExtendedFieldHeaderDAO().save(extendedFieldHeader,tableType));
			auditHeader.getAuditDetail().setModelData(extendedFieldHeader);
			auditHeader.setAuditReference(String.valueOf(extendedFieldHeader.getModuleId()));
		}else{
			getExtendedFieldHeaderDAO().update(extendedFieldHeader,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table ExtendedFieldHeader by using ExtendedFieldHeaderDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtExtendedFieldHeader by using auditHeaderDAO.addAudit(auditHeader)    
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

		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getExtendedFieldHeaderById fetch the details by using ExtendedFieldHeaderDAO's getExtendedFieldHeaderById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ExtendedFieldHeader
	 */
	@Override
	public ExtendedFieldHeader getExtendedFieldHeaderById(long id) {
		return getExtendedFieldHeaderDAO().getExtendedFieldHeaderById(id,"_View");
	}

	/**
	 * getApprovedExtendedFieldHeaderById fetch the details by using
	 * ExtendedFieldHeaderDAO's getExtendedFieldHeaderById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * ExtendedFieldHeader.
	 * 
	 * @param id
	 *            (int)
	 * @return ExtendedFieldHeader
	 */
	public ExtendedFieldHeader getApprovedExtendedFieldHeaderById(long id) {
		return getExtendedFieldHeaderDAO().getExtendedFieldHeaderById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getExtendedFieldHeaderDAO().delete
	 * with parameters extendedFieldHeader,"" b) NEW Add new record in to main table by using
	 * getExtendedFieldHeaderDAO().save with parameters extendedFieldHeader,"" c) EDIT Update record in the main table
	 * by using getExtendedFieldHeaderDAO().update with parameters extendedFieldHeader,"" 3) Delete the record from the
	 * workFlow table by using getExtendedFieldHeaderDAO().delete with parameters extendedFieldHeader,"_Temp" 4) Audit
	 * the record in to AuditHeader and AdtExtendedFieldHeader by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtExtendedFieldHeader by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
		BeanUtils.copyProperties((ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData(), extendedFieldHeader);

		if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getExtendedFieldHeaderDAO().delete(extendedFieldHeader,"");
		} else {
			extendedFieldHeader.setRoleCode("");
			extendedFieldHeader.setNextRoleCode("");
			extendedFieldHeader.setTaskId("");
			extendedFieldHeader.setNextTaskId("");
			extendedFieldHeader.setWorkflowId(0);

			if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				extendedFieldHeader.setRecordType("");
				getExtendedFieldHeaderDAO().save(extendedFieldHeader,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				extendedFieldHeader.setRecordType("");
				getExtendedFieldHeaderDAO().update(extendedFieldHeader,"");
			}
		}

		getExtendedFieldHeaderDAO().delete(extendedFieldHeader,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(extendedFieldHeader);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getExtendedFieldHeaderDAO().delete with parameters extendedFieldHeader,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtExtendedFieldHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader,"_Temp");

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
	 * 5)	for any mismatch conditions Fetch the error details from getExtendedFieldHeaderDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");

		ExtendedFieldHeader message = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		String[] fields = PennantJavaUtil.getFieldDetails(message,
				excludeFields);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader
				.getAuditTranType(), 1, fields[0], fields[1], message.getBefImage(), message));

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		ExtendedFieldHeader extendedFieldHeader= (ExtendedFieldHeader) auditDetail.getModelData();

		ExtendedFieldHeader tempExtendedFieldHeader= null;
		if (extendedFieldHeader.isWorkflow()){
			tempExtendedFieldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(extendedFieldHeader.getModuleName(),extendedFieldHeader.getSubModuleName(), "_Temp");
		}
		ExtendedFieldHeader befExtendedFieldHeader= getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(extendedFieldHeader.getModuleName(),extendedFieldHeader.getSubModuleName(), "");

		ExtendedFieldHeader oldExtendedFieldHeader= extendedFieldHeader.getBefImage();

		String[] errParm= new String[2];
		String[] valueParm= new String[2];
		
		valueParm[0]= Labels.getLabel(extendedFieldHeader.getModuleName());
		valueParm[1]= extendedFieldHeader.getSubModuleName();
		
		errParm[0]=PennantJavaUtil.getLabel("label_ModuleName")+":"+valueParm[0];
		errParm[1]=PennantJavaUtil.getLabel("label_SubModuleName")+":"+valueParm[1];

		if (extendedFieldHeader.isNew()){ // for New record or new record into work flow

			if (!extendedFieldHeader.isWorkflow()){// With out Work flow only new records  
				if (befExtendedFieldHeader !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41015",
							errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befExtendedFieldHeader !=null || tempExtendedFieldHeader!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, 
								"41015", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befExtendedFieldHeader ==null || tempExtendedFieldHeader!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, 
								"41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!extendedFieldHeader.isWorkflow()){	// With out Work flow for update and delete

				if (befExtendedFieldHeader ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, 
							"41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldExtendedFieldHeader!=null && !oldExtendedFieldHeader.getLastMntOn().equals(befExtendedFieldHeader.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, 
									"41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, 
									"41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempExtendedFieldHeader==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm,valueParm), usrLanguage));
				}

				if (tempExtendedFieldHeader!=null && oldExtendedFieldHeader!=null && !oldExtendedFieldHeader.getLastMntOn().equals(tempExtendedFieldHeader.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !extendedFieldHeader.isWorkflow()){
			auditDetail.setBefImage(befExtendedFieldHeader);	
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}