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
 * FileName    		:  WIFFinanceMainServiceImpl.java                                                   * 	  
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.WIFFinanceMainDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.WIFFinanceMainService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>WIFFinanceMain</b>.<br>
 * 
 */
public class WIFFinanceMainServiceImpl extends GenericService<FinanceMain> implements WIFFinanceMainService {
	private static final Logger logger = Logger.getLogger(WIFFinanceMainServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private WIFFinanceMainDAO wIFFinanceMainDAO;
	
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	
	private FinanceDisbursementDAO financeDisbursementDAO;

	public WIFFinanceMainServiceImpl() {
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
	 * @return the wIFFinanceMainDAO
	 */
	public WIFFinanceMainDAO getWIFFinanceMainDAO() {
		return wIFFinanceMainDAO;
	}
	/**
	 * @param wIFFinanceMainDAO the wIFFinanceMainDAO to set
	 */
	public void setWIFFinanceMainDAO(WIFFinanceMainDAO wIFFinanceMainDAO) {
		this.wIFFinanceMainDAO = wIFFinanceMainDAO;
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
	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
	
	/**
	 * @return the FinanceDisbursementDAO
	 */
	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	/**
	 * @param wIFFinanceDisbursementDAO the wIFFinanceDisbursementDAO to set
	 */
	public void setFinanceDisbursementDAO(
			FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table WIFFinanceMain/WIFFinanceMain_Temp 
	 * 			by using WIFFinanceMainDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using WIFFinanceMainDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtWIFFinanceMain by using auditHeaderDAO.addAudit(auditHeader)
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
		FinanceMain wIFFinanceMain = (FinanceMain) auditHeader.getAuditDetail().getModelData();
		
		
		if (wIFFinanceMain.isWorkflow()) {
			tableType="_Temp";
		}

		if (wIFFinanceMain.isNew()) {
			getWIFFinanceMainDAO().save(wIFFinanceMain,tableType);
			
		}else{
			getWIFFinanceMainDAO().update(wIFFinanceMain,tableType);
		}

		//getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table WIFFinanceMain by using WIFFinanceMainDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtWIFFinanceMain by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		FinanceMain wIFFinanceMain = (FinanceMain) auditHeader.getAuditDetail().getModelData();
		getWIFFinanceMainDAO().delete(wIFFinanceMain,"");
		
		//getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getWIFFinanceMainById fetch the details by using WIFFinanceMainDAO's getWIFFinanceMainById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceMain
	 */
	
	@Override
	public FinanceMain getWIFFinanceMainById(String id) {
		return getWIFFinanceMainDAO().getWIFFinanceMainById(id,"_View"); 
	}
	/**
	 * getApprovedWIFFinanceMainById fetch the details by using WIFFinanceMainDAO's getWIFFinanceMainById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinanceMain.
	 * @param id (String)
	 * @return FinanceMain
	 */
	
	public FinanceMain getApprovedWIFFinanceMainById(String id) {
		return getWIFFinanceMainDAO().getWIFFinanceMainById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getWIFFinanceMainDAO().delete with
	 * parameters wIFFinanceMain,"" b) NEW Add new record in to main table by using getWIFFinanceMainDAO().save with
	 * parameters wIFFinanceMain,"" c) EDIT Update record in the main table by using getWIFFinanceMainDAO().update with
	 * parameters wIFFinanceMain,"" 3) Delete the record from the workFlow table by using getWIFFinanceMainDAO().delete
	 * with parameters wIFFinanceMain,"_Temp" 4) Audit the record in to AuditHeader and AdtWIFFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtWIFFinanceMain by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinanceMain wIFFinanceMain = new FinanceMain();
		try {
			BeanUtils.copyProperties((FinanceMain) auditHeader.getAuditDetail().getModelData(), wIFFinanceMain);
		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
		}

		if (wIFFinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getWIFFinanceMainDAO().delete(wIFFinanceMain, "");

		} else {
			wIFFinanceMain.setRoleCode("");
			wIFFinanceMain.setNextRoleCode("");
			wIFFinanceMain.setTaskId("");
			wIFFinanceMain.setNextTaskId("");
			wIFFinanceMain.setWorkflowId(0);

			if (wIFFinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				wIFFinanceMain.setRecordType("");
				getWIFFinanceMainDAO().save(wIFFinanceMain, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				wIFFinanceMain.setRecordType("");
				getWIFFinanceMainDAO().update(wIFFinanceMain, "");
			}
		}

		getWIFFinanceMainDAO().delete(wIFFinanceMain, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(wIFFinanceMain);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getWIFFinanceMainDAO().delete with parameters wIFFinanceMain,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtWIFFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

			FinanceMain wIFFinanceMain = (FinanceMain) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getWIFFinanceMainDAO().delete(wIFFinanceMain,"_Temp");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getWIFFinanceMainDAO().getErrorDetail with Error ID and language as parameters.
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
			FinanceMain wIFFinanceMain= (FinanceMain) auditDetail.getModelData();
			
			FinanceMain tempWIFFinanceMain= null;
			if (wIFFinanceMain.isWorkflow()){
				tempWIFFinanceMain = getWIFFinanceMainDAO().getWIFFinanceMainById(wIFFinanceMain.getId(), "_Temp");
			}
			
			FinanceMain befWIFFinanceMain= getWIFFinanceMainDAO().getWIFFinanceMainById(wIFFinanceMain.getId(), "");
			
			FinanceMain oldWIFFinanceMain= wIFFinanceMain.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=wIFFinanceMain.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
			
			if (wIFFinanceMain.isNew()){ // for New record or new record into work flow
				
				if (!wIFFinanceMain.isWorkflow()){// With out Work flow only new records  
					if (befWIFFinanceMain !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (wIFFinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befWIFFinanceMain !=null || tempWIFFinanceMain!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befWIFFinanceMain ==null || tempWIFFinanceMain!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!wIFFinanceMain.isWorkflow()){	// With out Work flow for update and delete
				
					if (befWIFFinanceMain ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldWIFFinanceMain!=null && !oldWIFFinanceMain.getLastMntOn().equals(befWIFFinanceMain.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempWIFFinanceMain==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempWIFFinanceMain!=null && oldWIFFinanceMain!=null && !oldWIFFinanceMain.getLastMntOn().equals(tempWIFFinanceMain.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !wIFFinanceMain.isWorkflow()){
				wIFFinanceMain.setBefImage(befWIFFinanceMain);	
			}

			return auditDetail;
		}
}