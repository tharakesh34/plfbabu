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
 * FileName    		:  FlagServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-07-2015    														*
 *                                                                  						*
 * Modified Date    :  14-07-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-07-2015       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.FlagDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmasters.Flag;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.FlagService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Flag</b>.<br>
 * 
 */
public class FlagServiceImpl extends GenericService<Flag> implements FlagService {
	private final static Logger logger = Logger.getLogger(FlagServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private FlagDAO flagDAO;

	public FlagServiceImpl() {
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
	 * @return the flagDAO
	 */
	public FlagDAO getFlagDAO() {
		return flagDAO;
	}
	/**
	 * @param flagDAO the flagDAO to set
	 */
	public void setFlagDAO(FlagDAO flagDAO) {
		this.flagDAO = flagDAO;
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table Flags/Flags_Temp 
	 * 			by using FlagDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using FlagDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFlags by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table Flags/Flags_Temp 
	 * 			by using FlagDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using FlagDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFlags by using auditHeaderDAO.addAudit(auditHeader)
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
		Flag flag = (Flag) auditHeader.getAuditDetail().getModelData();
		
		if (flag.isWorkflow()) {
			tableType="_Temp";
		}

		if (flag.isNew()) {
			getFlagDAO().save(flag,tableType);
		}else{
			getFlagDAO().update(flag,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table Flags by using FlagDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFlags by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		Flag flag = (Flag) auditHeader.getAuditDetail().getModelData();
		getFlagDAO().delete(flag,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFlagById fetch the details by using FlagDAO's getFlagById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Flag
	 */
	
	@Override
	public Flag getFlagById(String id) {
		return getFlagDAO().getFlagById(id,"_View");
	}
	/**
	 * getApprovedFlagById fetch the details by using FlagDAO's getFlagById method .
	 * with parameter id and type as blank. it fetches the approved records from the Flags.
	 * @param id (String)
	 * @return Flag
	 */
	
	public Flag getApprovedFlagById(String id) {
		return getFlagDAO().getFlagById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFlagDAO().delete with parameters
	 * flag,"" b) NEW Add new record in to main table by using getFlagDAO().save with parameters flag,"" c) EDIT Update
	 * record in the main table by using getFlagDAO().update with parameters flag,"" 3) Delete the record from the
	 * workFlow table by using getFlagDAO().delete with parameters flag,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtFlags by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader
	 * and AdtFlags by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		Flag flag = new Flag();
		BeanUtils.copyProperties((Flag) auditHeader.getAuditDetail().getModelData(), flag);

		if (flag.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getFlagDAO().delete(flag, "");

		} else {
			flag.setRoleCode("");
			flag.setNextRoleCode("");
			flag.setTaskId("");
			flag.setNextTaskId("");
			flag.setWorkflowId(0);

			if (flag.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				flag.setRecordType("");
				getFlagDAO().save(flag, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				flag.setRecordType("");
				getFlagDAO().update(flag, "");
			}
		}

		getFlagDAO().delete(flag, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(flag);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getFlagDAO().delete with parameters flag,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFlags by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doApprove",false);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			Flag flag = (Flag) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getFlagDAO().delete(flag,"_Temp");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getFlagDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
			Flag flag= (Flag) auditDetail.getModelData();
			
			Flag tempFlag= null;
			if (flag.isWorkflow()){
				tempFlag = getFlagDAO().getFlagById(flag.getId(), "_Temp");
			}
			Flag befFlag= getFlagDAO().getFlagById(flag.getId(), "");
			
			Flag oldFlag= flag.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=flag.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FlagCode")+":"+valueParm[0];
			
			if (flag.isNew()){ // for New record or new record into work flow
				
				if (!flag.isWorkflow()){// With out Work flow only new records  
					if (befFlag !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (flag.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befFlag !=null || tempFlag!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befFlag ==null || tempFlag!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!flag.isWorkflow()){	// With out Work flow for update and delete
				
					if (befFlag ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldFlag!=null && !oldFlag.getLastMntOn().equals(befFlag.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempFlag==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempFlag!=null && oldFlag!=null && !oldFlag.getLastMntOn().equals(tempFlag.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !flag.isWorkflow()){
				auditDetail.setBefImage(befFlag);	
			}

			return auditDetail;
		}

}