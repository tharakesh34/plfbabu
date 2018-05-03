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
 * FileName    		:  OverdueChargeRecoveryServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-05-2012    														*
 *                                                                  						*
 * Modified Date    :  11-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.financemanagement.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>OverdueChargeRecovery</b>.<br>
 * 
 */
public class OverdueChargeRecoveryServiceImpl extends GenericService<OverdueChargeRecovery> implements OverdueChargeRecoveryService {
	private static final Logger logger = Logger.getLogger(OverdueChargeRecoveryServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private OverdueChargeRecoveryDAO overdueChargeRecoveryDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

	public OverdueChargeRecoveryServiceImpl() {
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
	
	public OverdueChargeRecoveryDAO getOverdueChargeRecoveryDAO() {
		return overdueChargeRecoveryDAO;
	}
	public void setOverdueChargeRecoveryDAO(OverdueChargeRecoveryDAO overdueChargeRecoveryDAO) {
		this.overdueChargeRecoveryDAO = overdueChargeRecoveryDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	/**
	 * @return the overdueChargeRecovery
	 */
	@Override
	public OverdueChargeRecovery getOverdueChargeRecovery() {
		return getOverdueChargeRecoveryDAO().getOverdueChargeRecovery();
	}
	
	/**
	 * @return the overdueChargeRecovery for New Record
	 */
	@Override
	public OverdueChargeRecovery getNewOverdueChargeRecovery() {
		return getOverdueChargeRecoveryDAO().getNewOverdueChargeRecovery();
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinODCRecovery/FinODCRecovery_Temp 
	 * 			by using OverdueChargeRecoveryDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using OverdueChargeRecoveryDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinODCRecovery by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		/*auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}*/
		String tableType="";
		OverdueChargeRecovery overdueChargeRecovery = (OverdueChargeRecovery) auditHeader.getAuditDetail().getModelData();

		if (overdueChargeRecovery.isWorkflow()) {
			tableType="_Temp";
		}

		if (overdueChargeRecovery.isNew()) {
			getOverdueChargeRecoveryDAO().save(overdueChargeRecovery,tableType);
		}else{
			getOverdueChargeRecoveryDAO().update(overdueChargeRecovery,tableType);
		}

		//getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinODCRecovery by using OverdueChargeRecoveryDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinODCRecovery by using auditHeaderDAO.addAudit(auditHeader)    
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

		OverdueChargeRecovery overdueChargeRecovery = (OverdueChargeRecovery) auditHeader.getAuditDetail().getModelData();
		getOverdueChargeRecoveryDAO().delete(overdueChargeRecovery,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getOverdueChargeRecoveryById fetch the details by using OverdueChargeRecoveryDAO's getOverdueChargeRecoveryById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return OverdueChargeRecovery
	 */

	@Override
	public OverdueChargeRecovery getOverdueChargeRecoveryById(String id, Date finSchDate, String finOdFor) {
		OverdueChargeRecovery ocr = getNewOverdueChargeRecovery();
		ocr = getOverdueChargeRecoveryDAO().getOverdueChargeRecoveryById(id, finSchDate, finOdFor,"_View");
		ocr = getOverdueChargeRecoveryDAO().getODCRecoveryDetails(ocr);
		return ocr;
	}
	/**
	 * getApprovedOverdueChargeRecoveryById fetch the details by using OverdueChargeRecoveryDAO's getOverdueChargeRecoveryById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinODCRecovery.
	 * @param id (String)
	 * @return OverdueChargeRecovery
	 */
	public OverdueChargeRecovery getApprovedOverdueChargeRecoveryById(String id, Date finSchDate, String finOdFor) {
		return getOverdueChargeRecoveryDAO().getOverdueChargeRecoveryById(id, finSchDate, finOdFor,"_AView");
	}
	
	/**
	 * Method for Getting Pending OverDueAmount
	 */
	@Override
	public BigDecimal getPendingODCAmount(String finReference) {
		return getOverdueChargeRecoveryDAO().getPendingODCAmount(finReference);
	}
	

	@Override
	public OverdueChargeRecovery getOverdueChargeRecovery(String finReference) {
		OverdueChargeRecovery overdueCherage = new OverdueChargeRecovery();
		overdueCherage.setFinReference(finReference);

		return getOverdueChargeRecoveryDAO().getODCRecoveryDetails(overdueCherage);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getOverdueChargeRecoveryDAO().delete
	 * with parameters overdueChargeRecovery,"" b) NEW Add new record in to main table by using
	 * getOverdueChargeRecoveryDAO().save with parameters overdueChargeRecovery,"" c) EDIT Update record in the main
	 * table by using getOverdueChargeRecoveryDAO().update with parameters overdueChargeRecovery,"" 3) Delete the record
	 * from the workFlow table by using getOverdueChargeRecoveryDAO().delete with parameters
	 * overdueChargeRecovery,"_Temp" 4) Audit the record in to AuditHeader and AdtFinODCRecovery by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinODCRecovery by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		OverdueChargeRecovery overdueChargeRecovery = new OverdueChargeRecovery();
		BeanUtils.copyProperties((OverdueChargeRecovery) auditHeader.getAuditDetail().getModelData(), overdueChargeRecovery);

		if (overdueChargeRecovery.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getOverdueChargeRecoveryDAO().delete(overdueChargeRecovery,"");

		} else {
			overdueChargeRecovery.setRoleCode("");
			overdueChargeRecovery.setNextRoleCode("");
			overdueChargeRecovery.setTaskId("");
			overdueChargeRecovery.setNextTaskId("");
			overdueChargeRecovery.setWorkflowId(0);

			if (overdueChargeRecovery.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				overdueChargeRecovery.setRecordType("");
				getOverdueChargeRecoveryDAO().save(overdueChargeRecovery,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				overdueChargeRecovery.setRecordType("");
				getOverdueChargeRecoveryDAO().update(overdueChargeRecovery, "");
			}
		}

		getOverdueChargeRecoveryDAO().delete(overdueChargeRecovery,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(overdueChargeRecovery);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getOverdueChargeRecoveryDAO().delete with parameters overdueChargeRecovery,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinODCRecovery by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		OverdueChargeRecovery overdueChargeRecovery = (OverdueChargeRecovery) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getOverdueChargeRecoveryDAO().delete(overdueChargeRecovery,"_Temp");

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
	 * 5)	for any mismatch conditions Fetch the error details from getOverdueChargeRecoveryDAO().getErrorDetail with Error ID and language as parameters.
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
		OverdueChargeRecovery overdueChargeRecovery= (OverdueChargeRecovery) auditDetail.getModelData();

		OverdueChargeRecovery tempOverdueChargeRecovery= null;
		if (overdueChargeRecovery.isWorkflow()){
			tempOverdueChargeRecovery = getOverdueChargeRecoveryDAO().
			getOverdueChargeRecoveryById(overdueChargeRecovery.getId(),
					overdueChargeRecovery.getFinODSchdDate(),
					overdueChargeRecovery.getFinODFor(), "_Temp");
		}
		OverdueChargeRecovery befOverdueChargeRecovery= getOverdueChargeRecoveryDAO().
		getOverdueChargeRecoveryById(overdueChargeRecovery.getId(),
				overdueChargeRecovery.getFinODSchdDate(),
				overdueChargeRecovery.getFinODFor(), "");
		OverdueChargeRecovery oldOverdueChargeRecovery= overdueChargeRecovery.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=overdueChargeRecovery.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (overdueChargeRecovery.isNew()){ // for New record or new record into work flow

			if (!overdueChargeRecovery.isWorkflow()){// With out Work flow only new records  
				if (befOverdueChargeRecovery !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (overdueChargeRecovery.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befOverdueChargeRecovery !=null || tempOverdueChargeRecovery!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befOverdueChargeRecovery ==null || tempOverdueChargeRecovery!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!overdueChargeRecovery.isWorkflow()){	// With out Work flow for update and delete

				if (befOverdueChargeRecovery ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldOverdueChargeRecovery!=null && !oldOverdueChargeRecovery.getLastMntOn().equals(befOverdueChargeRecovery.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempOverdueChargeRecovery==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempOverdueChargeRecovery!=null && oldOverdueChargeRecovery!=null && !oldOverdueChargeRecovery.getLastMntOn().equals(tempOverdueChargeRecovery.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !overdueChargeRecovery.isWorkflow()){
			overdueChargeRecovery.setBefImage(befOverdueChargeRecovery);	
		}

		return auditDetail;
	}

}