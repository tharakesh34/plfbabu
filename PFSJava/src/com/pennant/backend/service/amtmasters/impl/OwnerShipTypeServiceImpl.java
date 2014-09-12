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
 * FileName    		:  OwnerShipTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.amtmasters.impl;



import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.amtmasters.OwnerShipTypeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.amtmasters.OwnerShipType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.OwnerShipTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>OwnerShipType</b>.<br>
 * 
 */
public class OwnerShipTypeServiceImpl extends GenericService<OwnerShipType> implements OwnerShipTypeService {

	private final static Logger logger = Logger.getLogger(OwnerShipTypeServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private OwnerShipTypeDAO ownerShipTypeDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public OwnerShipTypeDAO getOwnerShipTypeDAO() {
		return ownerShipTypeDAO;
	}
	public void setOwnerShipTypeDAO(OwnerShipTypeDAO ownerShipTypeDAO) {
		this.ownerShipTypeDAO = ownerShipTypeDAO;
	}

	public OwnerShipType getOwnerShipType() {
		return getOwnerShipTypeDAO().getOwnerShipType();
	}
	public OwnerShipType getNewOwnerShipType() {
		return getOwnerShipTypeDAO().getNewOwnerShipType();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table AMTOwnerShipType/AMTOwnerShipType_Temp 
	 * 			by using OwnerShipTypeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using OwnerShipTypeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtAMTOwnerShipType by using auditHeaderDAO.addAudit(auditHeader)
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
		OwnerShipType ownerShipType = (OwnerShipType) auditHeader.getAuditDetail().getModelData();

		if (ownerShipType.isWorkflow()) {
			tableType="_TEMP";
		}

		if (ownerShipType.isNew()) {
			ownerShipType.setId(getOwnerShipTypeDAO().save(ownerShipType,tableType));
			auditHeader.getAuditDetail().setModelData(ownerShipType);
			auditHeader.setAuditReference(String.valueOf(ownerShipType.getOwnerShipTypeId()));
		}else{
			getOwnerShipTypeDAO().update(ownerShipType,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table AMTOwnerShipType by using OwnerShipTypeDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtAMTOwnerShipType by using auditHeaderDAO.addAudit(auditHeader)    
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

		OwnerShipType ownerShipType = (OwnerShipType) auditHeader.getAuditDetail().getModelData();
		getOwnerShipTypeDAO().delete(ownerShipType,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getOwnerShipTypeById fetch the details by using OwnerShipTypeDAO's getOwnerShipTypeById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return OwnerShipType
	 */

	@Override
	public OwnerShipType getOwnerShipTypeById(long id) {
		return getOwnerShipTypeDAO().getOwnerShipTypeById(id,"_View");
	}
	/**
	 * getApprovedOwnerShipTypeById fetch the details by using OwnerShipTypeDAO's getOwnerShipTypeById method .
	 * with parameter id and type as blank. it fetches the approved records from the AMTOwnerShipType.
	 * @param id (int)
	 * @return OwnerShipType
	 */

	public OwnerShipType getApprovedOwnerShipTypeById(long id) {
		return getOwnerShipTypeDAO().getOwnerShipTypeById(id,"_AView");
	}	

	/**
	 * This method refresh the Record.
	 * @param OwnerShipType (ownerShipType)
	 * @return ownerShipType
	 */
	@Override
	public OwnerShipType refresh(OwnerShipType ownerShipType) {
		logger.debug("Entering");
		getOwnerShipTypeDAO().refresh(ownerShipType);
		getOwnerShipTypeDAO().initialize(ownerShipType);
		logger.debug("Leaving");
		return ownerShipType;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getOwnerShipTypeDAO().delete with parameters ownerShipType,""
	 * 		b)  NEW		Add new record in to main table by using getOwnerShipTypeDAO().save with parameters ownerShipType,""
	 * 		c)  EDIT	Update record in the main table by using getOwnerShipTypeDAO().update with parameters ownerShipType,""
	 * 3)	Delete the record from the workFlow table by using getOwnerShipTypeDAO().delete with parameters ownerShipType,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtAMTOwnerShipType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtAMTOwnerShipType by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		OwnerShipType ownerShipType = new OwnerShipType();
		BeanUtils.copyProperties((OwnerShipType) auditHeader.getAuditDetail().getModelData(), ownerShipType);

		if (ownerShipType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getOwnerShipTypeDAO().delete(ownerShipType,"");

		} else {
			ownerShipType.setRoleCode("");
			ownerShipType.setNextRoleCode("");
			ownerShipType.setTaskId("");
			ownerShipType.setNextTaskId("");
			ownerShipType.setWorkflowId(0);

			if (ownerShipType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				ownerShipType.setRecordType("");
				getOwnerShipTypeDAO().save(ownerShipType,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				ownerShipType.setRecordType("");
				getOwnerShipTypeDAO().update(ownerShipType,"");
			}
		}

		getOwnerShipTypeDAO().delete(ownerShipType,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(ownerShipType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getOwnerShipTypeDAO().delete with parameters ownerShipType,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtAMTOwnerShipType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		OwnerShipType ownerShipType = (OwnerShipType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getOwnerShipTypeDAO().delete(ownerShipType,"_TEMP");

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
	 * 
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

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getOwnerShipTypeDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		OwnerShipType ownerShipType= (OwnerShipType) auditDetail.getModelData();

		OwnerShipType tempOwnerShipType= null;
		if (ownerShipType.isWorkflow()){
			tempOwnerShipType = getOwnerShipTypeDAO().getOwnerShipTypeById(ownerShipType.getId(), "_Temp");
		}
		OwnerShipType befOwnerShipType= getOwnerShipTypeDAO().getOwnerShipTypeById(ownerShipType.getId(), "");

		OwnerShipType oldOwnerShipType= ownerShipType.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(ownerShipType.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_OwnerShipTypeId")+":"+valueParm[0];

		if (ownerShipType.isNew()){ // for New record or new record into work flow

			if (!ownerShipType.isWorkflow()){// With out Work flow only new records  
				if (befOwnerShipType !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (ownerShipType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befOwnerShipType !=null || tempOwnerShipType!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befOwnerShipType ==null || tempOwnerShipType!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!ownerShipType.isWorkflow()){	// With out Work flow for update and delete

				if (befOwnerShipType ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldOwnerShipType!=null && !oldOwnerShipType.getLastMntOn().equals(befOwnerShipType.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempOwnerShipType==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldOwnerShipType!=null && !oldOwnerShipType.getLastMntOn().equals(tempOwnerShipType.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !ownerShipType.isWorkflow()){
			ownerShipType.setBefImage(befOwnerShipType);	
		}

		return auditDetail;
	}

}