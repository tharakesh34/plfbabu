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
 * FileName    		:  DivisionDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-08-2013    														*
 *                                                                  						*
 * Modified Date    :  02-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.systemmasters.impl;



import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.DivisionDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.DivisionDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>DivisionDetail</b>.<br>
 * 
 */
public class DivisionDetailServiceImpl extends GenericService<DivisionDetail> implements DivisionDetailService {
	private final static Logger logger = Logger.getLogger(DivisionDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private DivisionDetailDAO divisionDetailDAO;

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
	 * @return the divisionDetailDAO
	 */
	public DivisionDetailDAO getDivisionDetailDAO() {
		return divisionDetailDAO;
	}
	/**
	 * @param divisionDetailDAO the divisionDetailDAO to set
	 */
	public void setDivisionDetailDAO(DivisionDetailDAO divisionDetailDAO) {
		this.divisionDetailDAO = divisionDetailDAO;
	}

	/**
	 * @return the divisionDetail
	 */
	@Override
	public DivisionDetail getDivisionDetail() {
		return getDivisionDetailDAO().getDivisionDetail();
	}
	/**
	 * @return the divisionDetail for New Record
	 */
	@Override
	public DivisionDetail getNewDivisionDetail() {
		return getDivisionDetailDAO().getNewDivisionDetail();
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table SMTDivisionDetail/SMTDivisionDetail_Temp 
	 * 			by using DivisionDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using DivisionDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtSMTDivisionDetail by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table SMTDivisionDetail/SMTDivisionDetail_Temp 
	 * 			by using DivisionDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using DivisionDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtSMTDivisionDetail by using auditHeaderDAO.addAudit(auditHeader)
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
		DivisionDetail divisionDetail = (DivisionDetail) auditHeader.getAuditDetail().getModelData();
		
		if (divisionDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (divisionDetail.isNew()) {
			getDivisionDetailDAO().save(divisionDetail,tableType);
		}else{
			getDivisionDetailDAO().update(divisionDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table SMTDivisionDetail by using DivisionDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtSMTDivisionDetail by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		DivisionDetail divisionDetail = (DivisionDetail) auditHeader.getAuditDetail().getModelData();
		getDivisionDetailDAO().delete(divisionDetail,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDivisionDetailById fetch the details by using DivisionDetailDAO's getDivisionDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DivisionDetail
	 */
	
	@Override
	public DivisionDetail getDivisionDetailById(String id) {
		return getDivisionDetailDAO().getDivisionDetailById(id,"_View");
	}
	/**
	 * getApprovedDivisionDetailById fetch the details by using DivisionDetailDAO's getDivisionDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the SMTDivisionDetail.
	 * @param id (String)
	 * @return DivisionDetail
	 */
	
	public DivisionDetail getApprovedDivisionDetailById(String id) {
		return getDivisionDetailDAO().getDivisionDetailById(id,"_AView");
	}
		
	/**
	 * This method refresh the Record.
	 * @param DivisionDetail (divisionDetail)
 	 * @return divisionDetail
	 */
	@Override
	public DivisionDetail refresh(DivisionDetail divisionDetail) {
		logger.debug("Entering");
		getDivisionDetailDAO().refresh(divisionDetail);
		getDivisionDetailDAO().initialize(divisionDetail);
		logger.debug("Leaving");
		return divisionDetail;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getDivisionDetailDAO().delete with parameters divisionDetail,""
	 * 		b)  NEW		Add new record in to main table by using getDivisionDetailDAO().save with parameters divisionDetail,""
	 * 		c)  EDIT	Update record in the main table by using getDivisionDetailDAO().update with parameters divisionDetail,""
	 * 3)	Delete the record from the workFlow table by using getDivisionDetailDAO().delete with parameters divisionDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtSMTDivisionDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtSMTDivisionDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		DivisionDetail divisionDetail = new DivisionDetail();
		BeanUtils.copyProperties((DivisionDetail) auditHeader.getAuditDetail().getModelData(), divisionDetail);

		if (divisionDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getDivisionDetailDAO().delete(divisionDetail,"");
				
			} else {
				divisionDetail.setRoleCode("");
				divisionDetail.setNextRoleCode("");
				divisionDetail.setTaskId("");
				divisionDetail.setNextTaskId("");
				divisionDetail.setWorkflowId(0);
				
				if (divisionDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					divisionDetail.setRecordType("");
					getDivisionDetailDAO().save(divisionDetail,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					divisionDetail.setRecordType("");
					getDivisionDetailDAO().update(divisionDetail,"");
				}
			}
			
			getDivisionDetailDAO().delete(divisionDetail,"_TEMP");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(divisionDetail);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getDivisionDetailDAO().delete with parameters divisionDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtSMTDivisionDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doApprove",false);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			DivisionDetail divisionDetail = (DivisionDetail) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getDivisionDetailDAO().delete(divisionDetail,"_TEMP");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getDivisionDetailDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
			DivisionDetail divisionDetail= (DivisionDetail) auditDetail.getModelData();
			
			DivisionDetail tempDivisionDetail= null;
			if (divisionDetail.isWorkflow()){
				tempDivisionDetail = getDivisionDetailDAO().getDivisionDetailById(divisionDetail.getId(), "_Temp");
			}
			DivisionDetail befDivisionDetail= getDivisionDetailDAO().getDivisionDetailById(divisionDetail.getId(), "");
			
			DivisionDetail oldDivisionDetail= divisionDetail.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=divisionDetail.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_DivisionCode")+":"+valueParm[0];
			
			if (divisionDetail.isNew()){ // for New record or new record into work flow
				
				if (!divisionDetail.isWorkflow()){// With out Work flow only new records  
					if (befDivisionDetail !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (divisionDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befDivisionDetail !=null || tempDivisionDetail!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befDivisionDetail ==null || tempDivisionDetail!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!divisionDetail.isWorkflow()){	// With out Work flow for update and delete
				
					if (befDivisionDetail ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldDivisionDetail!=null && !oldDivisionDetail.getLastMntOn().equals(befDivisionDetail.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempDivisionDetail==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (oldDivisionDetail!=null && !oldDivisionDetail.getLastMntOn().equals(tempDivisionDetail.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !divisionDetail.isWorkflow()){
				auditDetail.setBefImage(befDivisionDetail);	
			}

			return auditDetail;
		}

}