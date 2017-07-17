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
 * FileName    		:  ProvisionServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ProvisionCalculationUtil;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.InterfaceException;

/**
 * Service implementation for methods that depends on <b>Provision</b>.<br>
 * 
 */
public class ProvisionServiceImpl extends  GenericFinanceDetailService implements ProvisionService {
	private static final Logger logger = Logger.getLogger(ProvisionServiceImpl.class);

	private ProvisionDAO provisionDAO;
	private ProvisionMovementDAO provisionMovementDAO;
	private FinanceTypeDAO financeTypeDAO;
	private ProvisionCalculationUtil provisionCalculationUtil;

	public ProvisionServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ProvisionDAO getProvisionDAO() {
		return provisionDAO;
	}
	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public ProvisionMovementDAO getProvisionMovementDAO() {
		return provisionMovementDAO;
	}
	public void setProvisionMovementDAO(ProvisionMovementDAO provisionMovementDAO) {
		this.provisionMovementDAO = provisionMovementDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public ProvisionCalculationUtil getProvisionCalculationUtil() {
		return provisionCalculationUtil;
	}
	public void setProvisionCalculationUtil(ProvisionCalculationUtil provisionCalculationUtil) {
		this.provisionCalculationUtil = provisionCalculationUtil;
	}

	@Override
	public Provision getProvision() {
		return getProvisionDAO().getProvision();
	}

	@Override
	public Provision getNewProvision() {
		return getProvisionDAO().getNewProvision();
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinProvisions/FinProvisions_Temp 
	 * 			by using ProvisionDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ProvisionDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinProvisions by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		String tableType="";
		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();

		if (provision.isWorkflow()) {
			tableType="_Temp";
		}
		FinanceDetail financeDetail = provision.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();


		if(!provision.isWorkflow()){

			//Check Finance is RIA Finance Type or Not
			Date dateValueDate = DateUtility.getAppValueDate();
			getProvisionCalculationUtil().processProvCalculations(provision, dateValueDate, false, true, false);
		}else{

			if (provision.isNew()) {
				getProvisionDAO().save(provision,tableType);
			}else{
				getProvisionDAO().update(provision,tableType);
			}

		}
		// Save Fee Charges List
		//=======================================
		if (StringUtils.isNotBlank(tableType)) {
			getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(),FinanceConstants.FINSER_EVENT_PROVISION, false, tableType);
		}
		saveFeeChargeList(financeDetail.getFinScheduleData(),FinanceConstants.FINSER_EVENT_PROVISION, false,tableType);
		
		// Save Document Details
		if (financeDetail.getDocumentDetailsList() != null
				&& financeDetail.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType, financeMain,FinanceConstants.FINSER_EVENT_PROVISION);
			auditDetails.addAll(details);
		}

		// set Finance Check List audit details to auditDetails
		//=======================================
		if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(getCheckListDetailService().saveOrUpdate(financeDetail, tableType));
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinProvisions by using ProvisionDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinProvisions by using auditHeaderDAO.addAudit(auditHeader)    
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

		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();
		getProvisionDAO().delete(provision,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getProvisionById fetch the details by using ProvisionDAO's getProvisionById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Provision
	 */
	@Override
	public Provision getProvisionById(String id,boolean isEnquiry) {
		Provision provision = getProvisionDAO().getProvisionById(id,"_View");
		if(provision != null && isEnquiry){
			provision.setProvisionMovementList(getProvisionMovementDAO().getProvisionMovementListById(id, "_AView"));
		}
		return provision;
	}

	/**
	 * getApprovedProvisionById fetch the details by using ProvisionDAO's getProvisionById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinProvisions.
	 * @param id (String)
	 * @return Provision
	 */
	public Provision getApprovedProvisionById(String id) {
		return getProvisionDAO().getProvisionById(id,"_AView");
	}

	@Override
	public FinanceProfitDetail getProfitDetailById(String finReference){
		return getProfitDetailsDAO().getFinProfitDetailsById(finReference);
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getProvisionDAO().delete with parameters provision,""
	 * 		b)  NEW		Add new record in to main table by using getProvisionDAO().save with parameters provision,""
	 * 		c)  EDIT	Update record in the main table by using getProvisionDAO().update with parameters provision,""
	 * 3)	Delete the record from the workFlow table by using getProvisionDAO().delete with parameters provision,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFinProvisions by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFinProvisions by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Provision provision = new Provision();
		BeanUtils.copyProperties((Provision) auditHeader.getAuditDetail().getModelData(), provision);
		
		FinanceDetail financeDetail = provision.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		//Provision Postings Process
		Date dateValueDate = DateUtility.getAppValueDate();
		getProvisionCalculationUtil().processProvCalculations(provision, dateValueDate, false, true, false);
		
		//Fee Charge Details
		//=======================================
		saveFeeChargeList(financeDetail.getFinScheduleData(), FinanceConstants.FINSER_EVENT_PROVISION, false, "");

		// set Check list details Audit
		//=======================================
		if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {					
			auditHeader.getAuditDetails().addAll(getCheckListDetailService().doApprove(financeDetail, ""));
		}

		// Save Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "", financeMain,FinanceConstants.FINSER_EVENT_PROVISION);
			auditHeader.setAuditDetails(details);
		}

	
		getProvisionDAO().delete(provision,"_Temp");
		
		// Document Details delete
		//=======================================
		listDocDeletion(provision.getFinanceDetail(), "_Temp");

		// Checklist Details delete
		//=======================================
		getCheckListDetailService().delete(provision.getFinanceDetail(), "_Temp", tranType);

		// Fee charges deletion
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(), provision.getFinanceDetail().getModuleDefiner() ,false, "_Temp");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(provision);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}
	
	 //Document Details List Maintenance
  	public void listDocDeletion(FinanceDetail custDetails, String tableType) {
  		getDocumentDetailsDAO().deleteList(
  				new ArrayList<DocumentDetails>(custDetails.getDocumentDetailsList()), tableType);
  	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getProvisionDAO().delete with parameters provision,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinProvisions by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 * @throws InterfaceException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = provision.getFinanceDetail().getFinScheduleData().getFinanceMain();
		String tranType = PennantConstants.TRAN_DEL;

		// Cancel All Transactions done by Finance Reference
		//=======================================
		cancelStageAccounting(financeMain.getFinReference(), FinanceConstants.FINSER_EVENT_PROVISION);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getProvisionDAO().delete(provision,"_Temp");

		// Save Document Details
		if (provision.getFinanceDetail().getDocumentDetailsList() != null && provision.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			for(DocumentDetails docDetails : provision.getFinanceDetail().getDocumentDetailsList()){
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = provision.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details,  "_Temp",
					provision.getFinanceDetail().getFinScheduleData().getFinanceMain(),provision.getFinanceDetail().getModuleDefiner());
			auditHeader.setAuditDetails(details);
			listDocDeletion(provision.getFinanceDetail(), "_Temp");
		}

		// Fee charges deletion
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(), provision.getFinanceDetail().getModuleDefiner() ,false, "_Temp");

		// Checklist Details delete
		//=======================================
		getCheckListDetailService().delete(provision.getFinanceDetail(), "_Temp", tranType);

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
	 * 5)	for any mismatch conditions Fetch the error details from getProvisionDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */


	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		Provision provision= (Provision) auditDetail.getModelData();

		Provision tempProvision= null;
		if (provision.isWorkflow()){
			tempProvision = getProvisionDAO().getProvisionById(provision.getId(), "_Temp");
		}
		Provision befProvision= getProvisionDAO().getProvisionById(provision.getId(), "");
		Provision oldProvision= provision.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=provision.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (provision.isNew()){ // for New record or new record into work flow

			if (!provision.isWorkflow()){// With out Work flow only new records  
				if (befProvision !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (provision.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befProvision !=null || tempProvision!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befProvision ==null || tempProvision!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!provision.isWorkflow()){	// With out Work flow for update and delete

				if (befProvision ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldProvision!=null && !oldProvision.getLastMntOn().equals(befProvision.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempProvision==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldProvision!=null && !oldProvision.getLastMntOn().equals(tempProvision.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !provision.isWorkflow()){
			provision.setBefImage(befProvision);	
		}

		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = provision.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();


		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		//Finance Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}
		
		//Finance Check List Details 
		//=======================================
		List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail, auditTranType, method));
			}
		} else {
			String tableType = "_Temp";
			if (financeDetail.getFinScheduleData().getFinanceMain().getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tableType = "";
			}

			String finReference = financeDetail.getFinScheduleData().getFinReference();
			financeCheckList = getCheckListDetailService().getCheckListByFinRef(finReference, tableType);				
			financeDetail.setFinanceCheckList(financeCheckList);

			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail, auditTranType, method));
			}
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(provision);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	@Override
	public List<Provision> getProcessedProvisions() {
		return getProvisionDAO().getProcessedProvisions();
	}
}