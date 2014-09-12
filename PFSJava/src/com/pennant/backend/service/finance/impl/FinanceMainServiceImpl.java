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
 * FileName    		:  FinanceMainServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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



import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>FinanceMain</b>.<br>
 * 
 */
public class FinanceMainServiceImpl extends GenericService<FinanceMain> implements FinanceMainService {
	private final static Logger logger = Logger.getLogger(FinanceMainServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private FinanceMainDAO financeMainDAO;

	private String excludeFields = "calculateRepay,equalRepay,eventFromDate,eventToDate,increaseTerms," +
			"allowedDefRpyChange,availedDefRpyChange,allowedDefFrqChange,availedDefFrqChange,"+
			"financeScheduleDetails,disbDate, disbursementDetails,repayInstructions,defermentHeaders,"+
			"defermentDetails,scheduleMap,reqTerms,errorDetails";
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
	 * @return the financeMainDAO
	 */
	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	/**
	 * @param financeMainDAO the financeMainDAO to set
	 */
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	/**
	 * @return the financeMain
	 */
	@Override
	public FinanceMain getFinanceMain(boolean isWIF) {
		return getFinanceMainDAO().getFinanceMain(isWIF);
	}
	/**
	 * @return the financeMain for New Record
	 */
	@Override
	public FinanceMain getNewFinanceMain(boolean isWIF) {
		return getFinanceMainDAO().getNewFinanceMain(isWIF);
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinanceMain/FinanceMain_Temp 
	 * 			by using FinanceMainDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using FinanceMainDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean isWIF) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate", isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		FinanceMain financeMain = (FinanceMain) auditHeader.getAuditDetail().getModelData();

		if (financeMain.isWorkflow()) {
			tableType="_TEMP";
		}
		if(isWIF){
			if (financeMain.isNew()) {
				getFinanceMainDAO().save(financeMain,tableType, isWIF);
			}else{
				getFinanceMainDAO().update(financeMain,tableType, isWIF);
			}
		}else if(!isWIF){	
			if (financeMain.isNew()) {
				getFinanceMainDAO().save(financeMain,tableType, isWIF);
			}else{
				getFinanceMainDAO().update(financeMain,tableType, isWIF);
			}
		}
		if(!isWIF){
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], financeMain.getBefImage(), financeMain));
			getAuditHeaderDAO().addAudit(auditHeader);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinanceMain by using FinanceMainDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader)    
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

		FinanceMain financeMain = (FinanceMain) auditHeader.getAuditDetail().getModelData();
		if(!isWIF){
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], financeMain.getBefImage(), financeMain));
			getAuditHeaderDAO().addAudit(auditHeader);
		}
		getFinanceMainDAO().delete(financeMain,"",isWIF);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFinanceMainById fetch the details by using FinanceMainDAO's getFinanceMainById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceMain
	 */

	@Override
	public FinanceMain getFinanceMainById(String id,boolean isWIF) {
		return getFinanceMainDAO().getFinanceMainById(id,"_View",isWIF);
	}
	/**
	 * getApprovedFinanceMainById fetch the details by using FinanceMainDAO's getFinanceMainById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinanceMain.
	 * @param id (String)
	 * @return FinanceMain
	 */

	public FinanceMain getApprovedFinanceMainById(String id,boolean isWIF) {
		return getFinanceMainDAO().getFinanceMainById(id,"_AView",isWIF);
	}

	/**
	 * This method refresh the Record.
	 * @param FinanceMain (financeMain)
	 * @return financeMain
	 */
	@Override
	public FinanceMain refresh(FinanceMain financeMain) {
		logger.debug("Entering");
		getFinanceMainDAO().refresh(financeMain);
		getFinanceMainDAO().initialize(financeMain);
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getFinanceMainDAO().delete with parameters financeMain,""
	 * 		b)  NEW		Add new record in to main table by using getFinanceMainDAO().save with parameters financeMain,""
	 * 		c)  EDIT	Update record in the main table by using getFinanceMainDAO().update with parameters financeMain,""
	 * 3)	Delete the record from the workFlow table by using getFinanceMainDAO().delete with parameters financeMain,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		FinanceMain financeMain = new FinanceMain();
		BeanUtils.copyProperties((FinanceMain) auditHeader.getAuditDetail().getModelData(), financeMain);

		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getFinanceMainDAO().delete(financeMain,"",isWIF);

		} else {
			financeMain.setRoleCode("");
			financeMain.setNextRoleCode("");
			financeMain.setTaskId("");
			financeMain.setNextTaskId("");
			financeMain.setWorkflowId(0);

			if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				financeMain.setRecordType("");
				getFinanceMainDAO().save(financeMain,"",isWIF);
			} else {
				tranType=PennantConstants.TRAN_UPD;
				financeMain.setRecordType("");
				getFinanceMainDAO().update(financeMain,"",isWIF);
			}
			
			getFinanceMainDAO().delete(financeMain,"_TEMP",isWIF);
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(financeMain);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");		
		
			return auditHeader;
		}

		getFinanceMainDAO().delete(financeMain,"_TEMP",isWIF);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getFinanceMainDAO().delete with parameters financeMain,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		FinanceMain financeMain = (FinanceMain) auditHeader.getAuditDetail().getModelData();
			
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		financeMainDAO.saveRejectFinanceDetails(financeMain);
		getFinanceMainDAO().delete(financeMain,"_TEMP",isWIF);
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
	 * 5)	for any mismatch conditions Fetch the error details from getFinanceMainDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */


	private AuditHeader businessValidation(AuditHeader auditHeader, String method,boolean isWIF){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, isWIF);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		if(!isWIF){
			auditHeader = getAuditDetails(auditHeader, method);
		}
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * Validation
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @param isWIF
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean isWIF){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		FinanceMain financeMain= (FinanceMain) auditDetail.getModelData();

		FinanceMain tempFinanceMain= null;
		if (financeMain.isWorkflow()){
			tempFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "_Temp",isWIF);
		}
		FinanceMain befFinanceMain= getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "",isWIF);

		FinanceMain old_FinanceMain= financeMain.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=financeMain.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (financeMain.isNew()){ // for New record or new record into work flow

			if (!financeMain.isWorkflow()){// With out Work flow only new records  
				if (befFinanceMain !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinanceMain !=null || tempFinanceMain!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinanceMain ==null || tempFinanceMain!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeMain.isWorkflow()){	// With out Work flow for update and delete

				if (befFinanceMain ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_FinanceMain!=null && !old_FinanceMain.getLastMntOn().equals(befFinanceMain.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempFinanceMain==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (old_FinanceMain!=null && !old_FinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !financeMain.isWorkflow()){
			financeMain.setBefImage(befFinanceMain);	
		}

		return auditDetail;
	}
	
	/**
	 * Method to get Finance  related data.
	 * 
	 * @param financeReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public List<FinanceEnquiry> getFinanceDetailsByCustId(long custId) {
		return getFinanceMainDAO().getFinanceDetailsByCustId(custId);
	}
	
	/**
	 * Common Method for Retrieving AuditDetails List
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader,String method ){
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain  finMain = (FinanceMain) auditHeader.getAuditDetail().getModelData();

		//String auditTranType="";

		if(method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject") ){
			if (finMain.isWorkflow()) {
				//auditTranType= PennantConstants.TRAN_WF;
			}
		}
		auditHeader.getAuditDetail().setModelData(finMain);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");
		return auditHeader;
	}
}