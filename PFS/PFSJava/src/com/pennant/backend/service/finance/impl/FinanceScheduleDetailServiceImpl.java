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
 * FileName    		:  WIFFinanceScheduleDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance.impl;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceScheduleDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>WIFFinanceScheduleDetail</b>.<br>
 * 
 */
public class FinanceScheduleDetailServiceImpl extends GenericService<FinanceScheduleDetail> implements FinanceScheduleDetailService {
	private final static Logger logger = Logger.getLogger(FinanceScheduleDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

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
	 * @return the wIFFinanceScheduleDetailDAO
	 */
	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	/**
	 * @param wIFFinanceScheduleDetailDAO the wIFFinanceScheduleDetailDAO to set
	 */
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	/**
	 * @return the wIFFinanceScheduleDetail
	 */
	@Override
	public FinanceScheduleDetail getFinanceScheduleDetail(boolean isWIF) {
		return getFinanceScheduleDetailDAO().getFinanceScheduleDetail(isWIF);
	}
	/**
	 * @return the wIFFinanceScheduleDetail for New Record
	 */
	@Override
	public FinanceScheduleDetail getNewFinanceScheduleDetail(boolean isWIF) {
		return getFinanceScheduleDetailDAO().getNewFinanceScheduleDetail(isWIF);
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table WIFFinScheduleDetails/WIFFinScheduleDetails_Temp 
	 * 			by using WIFFinanceScheduleDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using WIFFinanceScheduleDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtWIFFinScheduleDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean isWIF) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) auditHeader.getAuditDetail().getModelData();

		if (financeScheduleDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (financeScheduleDetail.isNew()) {
			getFinanceScheduleDetailDAO().save(financeScheduleDetail,tableType,isWIF);
		}else{
			getFinanceScheduleDetailDAO().update(financeScheduleDetail,tableType,isWIF);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table WIFFinScheduleDetails by using WIFFinanceScheduleDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtWIFFinScheduleDetails by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader,boolean isWIF) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete",isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) auditHeader.getAuditDetail().getModelData();
		getFinanceScheduleDetailDAO().delete(financeScheduleDetail,"",isWIF);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getWIFFinanceScheduleDetailById fetch the details by using WIFFinanceScheduleDetailDAO's getWIFFinanceScheduleDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return WIFFinanceScheduleDetail
	 */

	@Override
	public FinanceScheduleDetail getFinanceScheduleDetailById(String id,boolean isWIF) {
		return getFinanceScheduleDetailDAO().getFinanceScheduleDetailById(id,new Date(),"_View",isWIF);
	}
	/**
	 * getApprovedWIFFinanceScheduleDetailById fetch the details by using WIFFinanceScheduleDetailDAO's getWIFFinanceScheduleDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the WIFFinScheduleDetails.
	 * @param id (String)
	 * @return WIFFinanceScheduleDetail
	 */

	public FinanceScheduleDetail getApprovedFinanceScheduleDetailById(String id,boolean isWIF) {
		return getFinanceScheduleDetailDAO().getFinanceScheduleDetailById(id,new Date(),"_AView",isWIF);
	}

	/**
	 * This method refresh the Record.
	 * @param FinanceScheduleDetail (financeScheduleDetail)
	 * @return financeScheduleDetail
	 */
	@Override
	public FinanceScheduleDetail refresh(FinanceScheduleDetail financeScheduleDetail) {
		logger.debug("Entering");
		getFinanceScheduleDetailDAO().refresh(financeScheduleDetail);
		getFinanceScheduleDetailDAO().initialize(financeScheduleDetail);
		logger.debug("Leaving");
		return financeScheduleDetail;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getFinanceScheduleDetailDAO().delete with parameters wIFFinanceScheduleDetail,""
	 * 		b)  NEW		Add new record in to main table by using getFinanceScheduleDetailDAO().save with parameters wIFFinanceScheduleDetail,""
	 * 		c)  EDIT	Update record in the main table by using getFinanceScheduleDetailDAO().update with parameters wIFFinanceScheduleDetail,""
	 * 3)	Delete the record from the workFlow table by using getFinanceScheduleDetailDAO().delete with parameters wIFFinanceScheduleDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtWIFFinScheduleDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtWIFFinScheduleDetails by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader,boolean isWIF) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove",isWIF);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinanceScheduleDetail financeScheduleDetail = new FinanceScheduleDetail();
		BeanUtils.copyProperties((FinanceScheduleDetail) auditHeader.getAuditDetail().getModelData(), financeScheduleDetail);

		if (financeScheduleDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getFinanceScheduleDetailDAO().delete(financeScheduleDetail,"",isWIF);

		} else {
			financeScheduleDetail.setRoleCode("");
			financeScheduleDetail.setNextRoleCode("");
			financeScheduleDetail.setTaskId("");
			financeScheduleDetail.setNextTaskId("");
			financeScheduleDetail.setWorkflowId(0);

			if (financeScheduleDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				financeScheduleDetail.setRecordType("");
				getFinanceScheduleDetailDAO().save(financeScheduleDetail,"",isWIF);
			} else {
				tranType=PennantConstants.TRAN_UPD;
				financeScheduleDetail.setRecordType("");
				getFinanceScheduleDetailDAO().update(financeScheduleDetail,"",isWIF);
			}
		}

		getFinanceScheduleDetailDAO().delete(financeScheduleDetail,"_TEMP",isWIF);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeScheduleDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getFinanceScheduleDetailDAO().delete with parameters wIFFinanceScheduleDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtWIFFinScheduleDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader,boolean isWIF) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doReject",isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinanceScheduleDetailDAO().delete(financeScheduleDetail,"_TEMP",isWIF);

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
	 * 5)	for any mismatch conditions Fetch the error details from getFinanceScheduleDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */


	private AuditHeader businessValidation(AuditHeader auditHeader, String method,boolean isWIF){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,isWIF);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean isWIF){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		FinanceScheduleDetail financeScheduleDetail= (FinanceScheduleDetail) auditDetail.getModelData();

		FinanceScheduleDetail tempWIFFinanceScheduleDetail= null;
		if (financeScheduleDetail.isWorkflow()){
			tempWIFFinanceScheduleDetail = getFinanceScheduleDetailDAO().getFinanceScheduleDetailById(financeScheduleDetail.getId(),new Date(), "_Temp",isWIF);
		}
		FinanceScheduleDetail befWIFFinanceScheduleDetail= getFinanceScheduleDetailDAO().getFinanceScheduleDetailById(financeScheduleDetail.getId(), new Date(),"",isWIF);

		FinanceScheduleDetail old_WIFFinanceScheduleDetail= financeScheduleDetail.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=financeScheduleDetail.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (financeScheduleDetail.isNew()){ // for New record or new record into work flow

			if (!financeScheduleDetail.isWorkflow()){// With out Work flow only new records  
				if (befWIFFinanceScheduleDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (financeScheduleDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befWIFFinanceScheduleDetail !=null || tempWIFFinanceScheduleDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befWIFFinanceScheduleDetail ==null || tempWIFFinanceScheduleDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeScheduleDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befWIFFinanceScheduleDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_WIFFinanceScheduleDetail!=null && !old_WIFFinanceScheduleDetail.getLastMntOn().equals(befWIFFinanceScheduleDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempWIFFinanceScheduleDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (old_WIFFinanceScheduleDetail!=null && !old_WIFFinanceScheduleDetail.getLastMntOn().equals(tempWIFFinanceScheduleDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !financeScheduleDetail.isWorkflow()){
			financeScheduleDetail.setBefImage(befWIFFinanceScheduleDetail);	
		}

		return auditDetail;
	}

	@Override
	public List<FinanceScheduleDetail> getFinanceScheduleDetailById(String financeReference,String type) {
		return getFinanceScheduleDetailDAO().getFinScheduleDetails(financeReference, type, false);
	}

	@Override
    public BigDecimal getTotalRepayAmount(String finReference) {
	    return getFinanceScheduleDetailDAO().getTotalRepayAmount(finReference);
    }

	@Override
    public BigDecimal getTotalUnpaidPriAmount(String finReference) {
		return getFinanceScheduleDetailDAO().getTotalUnpaidPriAmount(finReference);
    }

	@Override
    public BigDecimal getTotalUnpaidPftAmount(String finReference) {
		return getFinanceScheduleDetailDAO().getTotalUnpaidPftAmount(finReference);
    }

}