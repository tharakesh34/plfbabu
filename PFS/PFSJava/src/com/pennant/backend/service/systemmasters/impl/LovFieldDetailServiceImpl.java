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
 * FileName    		:  LovFieldDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2011    														*
 *                                                                  						*
 * Modified Date    :  04-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.systemmasters.LovFieldDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.LovFieldDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>LovFieldDetail</b>.<br>
 * 
 */
public class LovFieldDetailServiceImpl extends GenericService<LovFieldDetail> 
		implements LovFieldDetailService {
	
	private final static Logger logger = Logger.getLogger(LovFieldDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private LovFieldDetailDAO lovFieldDetailDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public LovFieldDetailDAO getLovFieldDetailDAO() {
		return lovFieldDetailDAO;
	}
	public void setLovFieldDetailDAO(LovFieldDetailDAO lovFieldDetailDAO) {
		this.lovFieldDetailDAO = lovFieldDetailDAO;
	}

	@Override
	public LovFieldDetail getLovFieldDetail() {
		return getLovFieldDetailDAO().getLovFieldDetail();
	}
	
	@Override
	public LovFieldDetail getNewLovFieldDetail() {
		return getLovFieldDetailDAO().getNewLovFieldDetail();
	}
	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTLovFieldDetail/RMTLovFieldDetail_Temp by using LovFieldDetailDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using LovFieldDetailDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtRMTLovFieldDetail by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
		LovFieldDetail lovFieldDetail = (LovFieldDetail) auditHeader.getAuditDetail().getModelData();
		
		if (lovFieldDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (lovFieldDetail.isNew()) {
			lovFieldDetail.setId(getLovFieldDetailDAO().save(lovFieldDetail,tableType));
			auditHeader.getAuditDetail().setModelData(lovFieldDetail);
			auditHeader.setAuditReference(String.valueOf(lovFieldDetail.getFieldCodeId()));
		}else{
			getLovFieldDetailDAO().update(lovFieldDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTLovFieldDetail by using LovFieldDetailDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtRMTLovFieldDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
		
		LovFieldDetail lovFieldDetail = (LovFieldDetail) auditHeader.getAuditDetail().getModelData();
		getLovFieldDetailDAO().delete(lovFieldDetail,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getLovFieldDetailById fetch the details by using LovFieldDetailDAO's
	 * getLovFieldDetailById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return LovFieldDetail
	 */
	@Override
	public LovFieldDetail getLovFieldDetailById(long id) {
		return getLovFieldDetailDAO().getLovFieldDetailById(id,"_View");
	}
	
	/**
	 * getApprovedLovFieldDetailById fetch the details by using
	 * LovFieldDetailDAO's getLovFieldDetailById method . with parameter id and
	 * type as blank. it fetches the approved records from the
	 * RMTLovFieldDetail.
	 * 
	 * @param id
	 *            (int)
	 * @return LovFieldDetail
	 */
	public LovFieldDetail getApprovedLovFieldDetailById(long id) {
		return getLovFieldDetailDAO().getLovFieldDetailById(id,"_AView");
	}	
		
	/**
	 * This method refresh the Record.
	 * @param LovFieldDetail (lovFieldDetail)
 	 * @return lovFieldDetail
	 */
	@Override
	public LovFieldDetail refresh(LovFieldDetail lovFieldDetail) {
		logger.debug("Entering");
		getLovFieldDetailDAO().refresh(lovFieldDetail);
		getLovFieldDetailDAO().initialize(lovFieldDetail);
		logger.debug("Leaving");
		return lovFieldDetail;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getLovFieldDetailDAO().delete with parameters lovFieldDetail,"" b)
	 * NEW Add new record in to main table by using getLovFieldDetailDAO().save
	 * with parameters lovFieldDetail,"" c) EDIT Update record in the main table
	 * by using getLovFieldDetailDAO().update with parameters lovFieldDetail,""
	 * 3) Delete the record from the workFlow table by using
	 * getLovFieldDetailDAO().delete with parameters lovFieldDetail,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtRMTLovFieldDetail by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtRMTLovFieldDetail by using
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

		LovFieldDetail lovFieldDetail = new LovFieldDetail();
		BeanUtils.copyProperties((LovFieldDetail) auditHeader.getAuditDetail().getModelData(),
				lovFieldDetail);

		if (lovFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getLovFieldDetailDAO().delete(lovFieldDetail,"");

		} else {
			lovFieldDetail.setRoleCode("");
			lovFieldDetail.setNextRoleCode("");
			lovFieldDetail.setTaskId("");
			lovFieldDetail.setNextTaskId("");
			lovFieldDetail.setWorkflowId(0);

			if (lovFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){	
				tranType=PennantConstants.TRAN_ADD;
				lovFieldDetail.setRecordType("");
				getLovFieldDetailDAO().save(lovFieldDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				lovFieldDetail.setRecordType("");
				getLovFieldDetailDAO().update(lovFieldDetail,"");
			}
		}

		getLovFieldDetailDAO().delete(lovFieldDetail,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(lovFieldDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getLovFieldDetailDAO().delete with parameters
	 * lovFieldDetail,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTLovFieldDetail by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		LovFieldDetail lovFieldDetail = (LovFieldDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getLovFieldDetailDAO().delete(lovFieldDetail,"_TEMP");

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
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), 
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getLovFieldDetailDAO().getErrorDetail with Error ID and language as parameters.
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
		LovFieldDetail lovFieldDetail= (LovFieldDetail) auditDetail.getModelData();

		LovFieldDetail tempLovFieldDetail= null;
		if (lovFieldDetail.isWorkflow()){
			tempLovFieldDetail = getLovFieldDetailDAO().getLovFieldDetailById(
					lovFieldDetail.getId(), "_Temp");
		}
		LovFieldDetail befLovFieldDetail= getLovFieldDetailDAO().getLovFieldDetailById(
				lovFieldDetail.getId(), "");

		LovFieldDetail old_LovFieldDetail= lovFieldDetail.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(lovFieldDetail.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_FieldCodeId")+":"+valueParm[0];

		if (lovFieldDetail.isNew()){ // for New record or new record into work flow

			if (!lovFieldDetail.isWorkflow()){// With out Work flow only new records  
				if (befLovFieldDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41001",
									errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (lovFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befLovFieldDetail !=null || tempLovFieldDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001",
										errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befLovFieldDetail ==null || tempLovFieldDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
										errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!lovFieldDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befLovFieldDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41002", 
									errParm,valueParm), usrLanguage));
				}else{
					if (old_LovFieldDetail!=null && !old_LovFieldDetail.getLastMntOn().equals(
							befLovFieldDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41003", 
											errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41004", 
											errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempLovFieldDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", 
									errParm,valueParm), usrLanguage));
				}

				if (old_LovFieldDetail!=null && !old_LovFieldDetail.getLastMntOn().equals(
						tempLovFieldDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", 
									errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !lovFieldDetail.isWorkflow()){
			auditDetail.setBefImage(befLovFieldDetail);	
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}