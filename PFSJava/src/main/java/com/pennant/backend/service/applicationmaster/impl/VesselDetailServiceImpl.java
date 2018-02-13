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
 * FileName    		:  VesselDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-05-2015    														*
 *                                                                  						*
 * Modified Date    :  12-05-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-05-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.VesselDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.VesselDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.VesselDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>VesselDetail</b>.<br>
 * 
 */
public class VesselDetailServiceImpl extends GenericService<VesselDetail> implements VesselDetailService {
	private static final Logger logger = Logger.getLogger(VesselDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private VesselDetailDAO vesselDetailDAO;

	public VesselDetailServiceImpl() {
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
	 * @return the vesselDetailDAO
	 */
	public VesselDetailDAO getVesselDetailDAO() {
		return vesselDetailDAO;
	}
	/**
	 * @param vesselDetailDAO the vesselDetailDAO to set
	 */
	public void setVesselDetailDAO(VesselDetailDAO vesselDetailDAO) {
		this.vesselDetailDAO = vesselDetailDAO;
	}
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table VesselDetails/VesselDetails_Temp 
	 * 			by using VesselDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using VesselDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtVesselDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table VesselDetails/VesselDetails_Temp 
	 * 			by using VesselDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using VesselDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtVesselDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	 
		
	private AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean online) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		VesselDetail vesselDetail = (VesselDetail) auditHeader.getAuditDetail().getModelData();
		
		if (vesselDetail.isWorkflow()) {
			tableType="_Temp";
		}

		if (vesselDetail.isNew()) {
			getVesselDetailDAO().save(vesselDetail,tableType);
			auditHeader.getAuditDetail().setModelData(vesselDetail);
			auditHeader.setAuditReference(String.valueOf(vesselDetail.getVesselTypeID()));
		}else{
			getVesselDetailDAO().update(vesselDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table VesselDetails by using VesselDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtVesselDetails by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete",false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		VesselDetail vesselDetail = (VesselDetail) auditHeader.getAuditDetail().getModelData();
		getVesselDetailDAO().delete(vesselDetail,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getVesselDetailById fetch the details by using VesselDetailDAO's getVesselDetailById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VesselDetail
	 */
	
	@Override
	public VesselDetail getVesselDetailById(String id) {
		return getVesselDetailDAO().getVesselDetailById(id,"_View");
	}
	/**
	 * getApprovedVesselDetailById fetch the details by using VesselDetailDAO's getVesselDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the VesselDetails.
	 * @param id (int)
	 * @return VesselDetail
	 */
	
	public VesselDetail getApprovedVesselDetailById(String id) {
		return getVesselDetailDAO().getVesselDetailById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getVesselDetailDAO().delete with
	 * parameters vesselDetail,"" b) NEW Add new record in to main table by using getVesselDetailDAO().save with
	 * parameters vesselDetail,"" c) EDIT Update record in the main table by using getVesselDetailDAO().update with
	 * parameters vesselDetail,"" 3) Delete the record from the workFlow table by using getVesselDetailDAO().delete with
	 * parameters vesselDetail,"_Temp" 4) Audit the record in to AuditHeader and AdtVesselDetails by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtVesselDetails by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		VesselDetail vesselDetail = new VesselDetail();
		BeanUtils.copyProperties((VesselDetail) auditHeader.getAuditDetail().getModelData(), vesselDetail);

		if (vesselDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getVesselDetailDAO().delete(vesselDetail, "");

		} else {
			vesselDetail.setRoleCode("");
			vesselDetail.setNextRoleCode("");
			vesselDetail.setTaskId("");
			vesselDetail.setNextTaskId("");
			vesselDetail.setWorkflowId(0);

			if (vesselDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				vesselDetail.setRecordType("");
				getVesselDetailDAO().save(vesselDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				vesselDetail.setRecordType("");
				getVesselDetailDAO().update(vesselDetail, "");
			}
		}

		getVesselDetailDAO().delete(vesselDetail, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(vesselDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getVesselDetailDAO().delete with parameters vesselDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtVesselDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doApprove",false);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			VesselDetail vesselDetail = (VesselDetail) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getVesselDetailDAO().delete(vesselDetail,"_Temp");
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");
			
			return auditHeader;
		}

		/**
		 * businessValidation method do the following steps.
		 * 1)	validate the audit detail 
		 * 2)	if any error/Warnings  then assign the to auditHeader
		 * 3)   identify the nextprocess
		 *  
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */

		
		private AuditHeader businessValidation(AuditHeader auditHeader, String method,boolean onlineRequest){
			logger.debug("Entering");
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,onlineRequest);
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);
			logger.debug("Leaving");
			return auditHeader;
		}

		/**
		 * Validation method do the following steps.
		 * 1)	get the details from the auditHeader. 
		 * 2)	fetch the details from the tables
		 * 3)	Validate the Record based on the record details. 
		 * 4) 	Validate for any business validation.
		 * 5)	for any mismatch conditions Fetch the error details from getVesselDetailDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			VesselDetail vesselDetail= (VesselDetail) auditDetail.getModelData();
			
			VesselDetail tempVesselDetail= null;
			if (vesselDetail.isWorkflow()){
				tempVesselDetail = getVesselDetailDAO().getVesselDetailById(vesselDetail.getId(), "_Temp");
			}
			VesselDetail befVesselDetail= getVesselDetailDAO().getVesselDetailById(vesselDetail.getId(), "");
			
			VesselDetail oldVesselDetail= vesselDetail.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=String.valueOf(vesselDetail.getId());
			errParm[0]=PennantJavaUtil.getLabel("label_VesselTypeID")+":"+valueParm[0];
			
			if (vesselDetail.isNew()){ // for New record or new record into work flow
				
				if (!vesselDetail.isWorkflow()){// With out Work flow only new records  
					if (befVesselDetail !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (vesselDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befVesselDetail !=null || tempVesselDetail!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befVesselDetail ==null || tempVesselDetail!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!vesselDetail.isWorkflow()){	// With out Work flow for update and delete
				
					if (befVesselDetail ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldVesselDetail!=null && !oldVesselDetail.getLastMntOn().equals(befVesselDetail.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempVesselDetail==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempVesselDetail!=null && oldVesselDetail!=null && !oldVesselDetail.getLastMntOn().equals(tempVesselDetail.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			VesselDetail  vesselType = getVesselDetailDAO().getVesselDetailByType(vesselDetail, "_View");
			if(vesselType != null){
				String[] errParm1= new String[1];
				String[] valueParm1= new String[2];
				valueParm1[0] = vesselDetail.getVesselTypeName();
				valueParm1[1] = vesselDetail.getVesselSubType();
				errParm1[0]=PennantJavaUtil.getLabel("label_VesselType")+":"+valueParm1[0]+ " "+PennantJavaUtil.getLabel("label_VesselSubType")+":"+ valueParm1[1];		
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm1,null));
			}
		
			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !vesselDetail.isWorkflow()){
				auditDetail.setBefImage(befVesselDetail);	
			}

			return auditDetail;
		}

}