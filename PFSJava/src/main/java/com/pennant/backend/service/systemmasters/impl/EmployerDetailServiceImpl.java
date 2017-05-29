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
 * FileName    		:  EmployerDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-07-2013    														*
 *                                                                  						*
 * Modified Date    :  31-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-07-2013       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.systemmasters.EmployerDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.EmployerDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>EmployerDetail</b>.<br>
 * 
 */
public class EmployerDetailServiceImpl extends GenericService<EmployerDetail> implements EmployerDetailService {
	private final static Logger logger = Logger.getLogger(EmployerDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private EmployerDetailDAO employerDetailDAO;

	public EmployerDetailServiceImpl() {
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
	 * @return the employerDetailDAO
	 */
	public EmployerDetailDAO getEmployerDetailDAO() {
		return employerDetailDAO;
	}
	/**
	 * @param employerDetailDAO the employerDetailDAO to set
	 */
	public void setEmployerDetailDAO(EmployerDetailDAO employerDetailDAO) {
		this.employerDetailDAO = employerDetailDAO;
	}
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table EmployerDetail/EmployerDetail_Temp 
	 * 			by using EmployerDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using EmployerDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtEmployerDetail by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table EmployerDetail/EmployerDetail_Temp 
	 * 			by using EmployerDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using EmployerDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtEmployerDetail by using auditHeaderDAO.addAudit(auditHeader)
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
		TableType tableType = TableType.MAIN_TAB;
		EmployerDetail employerDetail = (EmployerDetail) auditHeader.getAuditDetail().getModelData();
		
		if (employerDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (employerDetail.isNew()) {
			employerDetail.setId(Long.parseLong(getEmployerDetailDAO().save(employerDetail,tableType)));
			auditHeader.getAuditDetail().setModelData(employerDetail);
			auditHeader.setAuditReference(String.valueOf(employerDetail.getEmployerId()));
		}else{
			getEmployerDetailDAO().update(employerDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table EmployerDetail by using EmployerDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtEmployerDetail by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		EmployerDetail employerDetail = (EmployerDetail) auditHeader.getAuditDetail().getModelData();
		getEmployerDetailDAO().delete(employerDetail, TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getEmployerDetailById fetch the details by using EmployerDetailDAO's getEmployerDetailById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return EmployerDetail
	 */
	
	@Override
	public EmployerDetail getEmployerDetailById(long id) {
		return getEmployerDetailDAO().getEmployerDetailById(id,"_View");
	}
	/**
	 * getApprovedEmployerDetailById fetch the details by using EmployerDetailDAO's getEmployerDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the EmployerDetail.
	 * @param id (int)
	 * @return EmployerDetail
	 */
	
	public EmployerDetail getApprovedEmployerDetailById(long id) {
		return getEmployerDetailDAO().getEmployerDetailById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getEmployerDetailDAO().delete with
	 * parameters employerDetail,"" b) NEW Add new record in to main table by using getEmployerDetailDAO().save with
	 * parameters employerDetail,"" c) EDIT Update record in the main table by using getEmployerDetailDAO().update with
	 * parameters employerDetail,"" 3) Delete the record from the workFlow table by using getEmployerDetailDAO().delete
	 * with parameters employerDetail,"_Temp" 4) Audit the record in to AuditHeader and AdtEmployerDetail by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtEmployerDetail by
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

		EmployerDetail employerDetail = new EmployerDetail();
		BeanUtils.copyProperties((EmployerDetail) auditHeader.getAuditDetail().getModelData(), employerDetail);

		if (employerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getEmployerDetailDAO().delete(employerDetail, TableType.MAIN_TAB);

		} else {
			employerDetail.setRoleCode("");
			employerDetail.setNextRoleCode("");
			employerDetail.setTaskId("");
			employerDetail.setNextTaskId("");
			employerDetail.setWorkflowId(0);

			if (employerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				employerDetail.setRecordType("");
				getEmployerDetailDAO().save(employerDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				employerDetail.setRecordType("");
				getEmployerDetailDAO().update(employerDetail, TableType.MAIN_TAB);
			}
		}

		getEmployerDetailDAO().delete(employerDetail, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(employerDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getEmployerDetailDAO().delete with parameters employerDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtEmployerDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doApprove",false);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			EmployerDetail employerDetail = (EmployerDetail) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getEmployerDetailDAO().delete(employerDetail, TableType.TEMP_TAB);
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getEmployerDetailDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		
		/*private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug("Entering");

			// Get the model object.
			EmployerDetail employerDetail = (EmployerDetail) auditDetail.getModelData();

			// Check the unique keys.
		if (employerDetail.isNew()
				&& employerDetailDAO.isDuplicateKey(employerDetail.getId(), employerDetail.getEmpIndustry(),
						employerDetail.getEmpName(), employerDetail.isWorkflow() ? TableType.BOTH_TAB
								: TableType.MAIN_TAB)) {
				String[] parameters = new String[2];

				parameters[0] = PennantJavaUtil.getLabel("label_AcademicLevel") + ": " + employerDetail.getAcademicLevel();
				parameters[1] = PennantJavaUtil.getLabel("label_AcademicDecipline") + ": "
						+ employerDetail.getAcademicDecipline();

				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

			logger.debug("Leaving");
			return auditDetail;
		}	*/
		
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
			EmployerDetail employerDetail= (EmployerDetail) auditDetail.getModelData();
			
			EmployerDetail tempEmployerDetail= null;
			if (employerDetail.isWorkflow()){
				tempEmployerDetail = getEmployerDetailDAO().getEmployerDetailById(employerDetail.getId(), "_Temp");
			}
			EmployerDetail befEmployerDetail= getEmployerDetailDAO().getEmployerDetailById(employerDetail.getId(), "");
			
			EmployerDetail oldEmployerDetail= employerDetail.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=String.valueOf(employerDetail.getId());
			errParm[0]=PennantJavaUtil.getLabel("label_EmployerId")+":"+valueParm[0];
			
			if (employerDetail.isNew()){ // for New record or new record into work flow
				
				if (!employerDetail.isWorkflow()){// With out Work flow only new records  
					if (befEmployerDetail !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (employerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befEmployerDetail !=null || tempEmployerDetail!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befEmployerDetail ==null || tempEmployerDetail!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!employerDetail.isWorkflow()){	// With out Work flow for update and delete
				
					if (befEmployerDetail ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldEmployerDetail!=null && !oldEmployerDetail.getLastMntOn().equals(befEmployerDetail.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempEmployerDetail==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempEmployerDetail!=null && oldEmployerDetail!=null && !oldEmployerDetail.getLastMntOn().equals(tempEmployerDetail.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !employerDetail.isWorkflow()){
				auditDetail.setBefImage(befEmployerDetail);	
			}

			return auditDetail;
		}

}