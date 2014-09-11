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
 * FileName    		:  EducationalLoanServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  18-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.lmtmasters.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.lmtmasters.EducationalExpenseDAO;
import com.pennant.backend.dao.lmtmasters.EducationalLoanDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.EducationalExpense;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.validation.EduExpenseDetailValidation;
import com.pennant.backend.service.finance.validation.EducationalLoanDetailValidation;
import com.pennant.backend.service.lmtmasters.EducationalLoanService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>EducationalLoan</b>.<br>
 * 
 */
public class EducationalLoanServiceImpl extends GenericService<EducationalLoan> implements EducationalLoanService {
	private final static Logger logger = Logger.getLogger(EducationalLoanServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private EducationalLoanDAO educationalLoanDAO;
	private EducationalExpenseDAO educationalExpenseDAO;

	private EducationalLoanDetailValidation educationalLoanDetailValidation;
	private EduExpenseDetailValidation eduExpenseDetailValidation;	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public EducationalLoanDAO getEducationalLoanDAO() {
		return educationalLoanDAO;
	}
	public void setEducationalLoanDAO(EducationalLoanDAO educationalLoanDAO) {
		this.educationalLoanDAO = educationalLoanDAO;
	}

	@Override
	public EducationalLoan getEducationalLoan() {
		return getEducationalLoanDAO().getEducationalLoan();
	}

	@Override
	public EducationalLoan getNewEducationalLoan() {
		return getEducationalLoanDAO().getNewEducationalLoan();
	}

	public EducationalExpenseDAO getEducationalExpenseDAO() {
		return educationalExpenseDAO;
	}
	public void setEducationalExpenseDAO(EducationalExpenseDAO educationalExpenseDAO) {
		this.educationalExpenseDAO = educationalExpenseDAO;
	}


	/**
	 * @return the educationalLoanDetailValidation
	 */
	public EducationalLoanDetailValidation getEducationalLoanDetailValidation() {
		if(educationalLoanDetailValidation==null){
			this.educationalLoanDetailValidation = new EducationalLoanDetailValidation(educationalLoanDAO);
		}
		return this.educationalLoanDetailValidation;
	}

	/**
	 * @return the eduExpenseDetailValidation
	 */
	public EduExpenseDetailValidation getEduExpenseDetailValidation() {
		if(eduExpenseDetailValidation==null){
			this.eduExpenseDetailValidation = new EduExpenseDetailValidation(educationalExpenseDAO);
		}
		return this.eduExpenseDetailValidation;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table
					LMTEducationLoanDetail/LMTEducationLoanDetail_Temp 
	 * 			by using EducationalLoanDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using EducationalLoanDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLMTEducationLoanDetail by using 
	 * 			auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		EducationalLoan educationalLoan = (EducationalLoan) auditHeader.getAuditDetail().getModelData();

		if (educationalLoan.isWorkflow()) {
			tableType="_TEMP";
		}

		if (educationalLoan.isNew()) {
			educationalLoan.setLoanRefNumber(getEducationalLoanDAO().save(educationalLoan,tableType));
			auditHeader.getAuditDetail().setModelData(educationalLoan);
			auditHeader.setAuditReference(educationalLoan.getLoanRefNumber());
		}else{
			getEducationalLoanDAO().update(educationalLoan,tableType);
		}
		//Retrieving List of Audit Details For educational expense  related modules
		if(educationalLoan.getEduExpenseList()!=null &&educationalLoan.getEduExpenseList().size()>0){
			List<AuditDetail> details = educationalLoan.getLovDescAuditDetailMap().get("EduExpense");
			details = saveOrUpdateEduExpenseList(details,tableType,educationalLoan.getLoanRefNumber() );
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table LMTEducationLoanDetail by using EducationalLoanDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtLMTEducationLoanDetail by using auditHeaderDAO.addAudit(auditHeader)    
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

		EducationalLoan educationalLoan = (EducationalLoan) auditHeader.getAuditDetail().getModelData();
		getEducationalLoanDAO().delete(educationalLoan,"");
		getAuditHeaderDAO().addAudit(auditHeader);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(educationalLoan,"",auditHeader.getAuditTranType())));
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getEducationalLoanById fetch the details by using EducationalLoanDAO's
	 * getEducationalLoanById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return EducationalLoan
	 */
	@Override
	public EducationalLoan getEducationalLoanById(String id) {
		EducationalLoan educationalLoan=getEducationalLoanDAO().getEducationalLoanByID(id,"_View");
		educationalLoan.setEduExpenseList(getEducationalExpenseDAO().getEducationalExpenseByEduLoanId(id, "_View"));
		return educationalLoan;
	}
	/**
	 * getApprovedEducationalLoanById fetch the details by using EducationalLoanDAO's getEducationalLoanById method .
	 * with parameter id and type as blank. it fetches the approved records from the LMTEducationLoanDetail.
	 * @param id (String)
	 * @return EducationalLoan
	 */

	public EducationalLoan getApprovedEducationalLoanById(String id) {
		return getEducationalLoanDAO().getEducationalLoanByID(id,"_AView");
	}	

	/**
	 * This method refresh the Record.
	 * @param EducationalLoan (educationalLoan)
	 * @return educationalLoan
	 */
	@Override
	public EducationalLoan refresh(EducationalLoan educationalLoan) {
		logger.debug("Entering");
		getEducationalLoanDAO().refresh(educationalLoan);
		getEducationalLoanDAO().initialize(educationalLoan);
		logger.debug("Leaving");
		return educationalLoan;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using 
	 * 				getEducationalLoanDAO().delete with parameters educationalLoan,""
	 * 		b)  NEW		Add new record in to main table by using 
	 * 				getEducationalLoanDAO().save with parameters educationalLoan,""
	 * 		c)  EDIT	Update record in the main table by using 
	 * 				getEducationalLoanDAO().update with parameters educationalLoan,""
	 * 3)	Delete the record from the workFlow table by using 
	 * 				getEducationalLoanDAO().delete with parameters educationalLoan,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtLMTEducationLoanDetail by using 
	 * 				auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtLMTEducationLoanDetail by using 
	 * 				auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		EducationalLoan educationalLoan = new EducationalLoan();
		BeanUtils.copyProperties((EducationalLoan) auditHeader.getAuditDetail().getModelData(),
				educationalLoan);

		if (educationalLoan.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getEducationalLoanDAO().delete(educationalLoan,"");
			auditDetails.addAll(listDeletion(educationalLoan, "",auditHeader.getAuditTranType()));
		} else {
			educationalLoan.setRoleCode("");
			educationalLoan.setNextRoleCode("");
			educationalLoan.setTaskId("");
			educationalLoan.setNextTaskId("");
			educationalLoan.setWorkflowId(0);

			if (educationalLoan.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				educationalLoan.setRecordType("");
				getEducationalLoanDAO().save(educationalLoan,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				educationalLoan.setRecordType("");
				getEducationalLoanDAO().update(educationalLoan,"");
			}

			if(educationalLoan.getEduExpenseList()!=null &&educationalLoan.getEduExpenseList().size()>0){
				List<AuditDetail> details = educationalLoan.getLovDescAuditDetailMap().get("EduExpense");
				details = saveOrUpdateEduExpenseList(details,"",educationalLoan.getLoanRefNumber());
				auditDetails.addAll(details);
			}
		}

		//Retrieving List of Audit Details For Customer related modules
		getEducationalLoanDAO().delete(educationalLoan,"_TEMP");
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(educationalLoan, "_TEMP",auditHeader.getAuditTranType())));
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 
				1, educationalLoan.getBefImage(), educationalLoan));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 
				1, educationalLoan.getBefImage(), educationalLoan));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using 
	 * 			getEducationalLoanDAO().delete with parameters educationalLoan,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtLMTEducationLoanDetail by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		EducationalLoan educationalLoan = (EducationalLoan) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getEducationalLoanDAO().delete(educationalLoan,"_TEMP");
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(educationalLoan, "_TEMP",auditHeader.getAuditTranType())));

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
	 * 5)	for any mismatch conditions Fetch the error details from 
	 * 			getEducationalLoanDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = validate(auditHeader, method);
		auditHeader = getAuditDetails(auditHeader, method);		

		EducationalLoan educationalLoan = (EducationalLoan) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = educationalLoan.getUserDetails().getUsrLanguage();

		// EmploymentDetail Validation
		if(educationalLoan.getEduExpenseList() != null){
			List<AuditDetail> details = educationalLoan.getLovDescAuditDetailMap().get("EduExpense");
			details = validateEduExpense(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());	
		}

		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	private AuditHeader validate(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> validateEduExpense(List<AuditDetail> auditDetails, String method,String  usrLanguage){

		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validateEduExpense(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
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
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		EducationalLoan educationalLoan = (EducationalLoan) auditHeader.getAuditDetail().getModelData();

		String auditTranType="";

		if(method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject") ){
			if (educationalLoan.isWorkflow()) {
				auditTranType= PennantConstants.TRAN_WF;
			}
		}

		if(educationalLoan.getEduExpenseList()!=null && educationalLoan.getEduExpenseList().size()>0){
			auditDetailMap.put("EduExpense", setEduExpenseAuditData(educationalLoan,auditTranType,method));
			auditDetails.addAll(auditDetailMap.get("EduExpense"));
		}

		educationalLoan.setLovDescAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(educationalLoan);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * @param educationalLoan
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setEduExpenseAuditData(EducationalLoan educationalLoan,String auditTranType,String method) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new EducationalExpense());


		for (int i = 0; i < educationalLoan.getEduExpenseList().size(); i++) {

			EducationalExpense educationalExpense  = educationalLoan.getEduExpenseList().get(i);
			educationalExpense.setWorkflowId(educationalLoan.getWorkflowId());
			educationalExpense.setLoanRefNumber((educationalLoan.getLoanRefNumber()));

			boolean isRcdType= false;

			if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType=true;
			}else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType=true;
			}else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType=true;
			}

			if(method.equals("saveOrUpdate") && (isRcdType==true)){
				educationalExpense.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}

			educationalExpense.setRecordStatus(educationalLoan.getRecordStatus());
			educationalExpense.setUserDetails(educationalLoan.getUserDetails());
			educationalExpense.setLastMntOn(educationalLoan.getLastMntOn());
			educationalExpense.setLastMntBy(educationalLoan.getLastMntBy());
			if(!educationalExpense.getRecordType().equals("")){
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], educationalExpense.getBefImage(), educationalExpense));
			}
		}
		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Educational expenses
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> saveOrUpdateEduExpenseList(List<AuditDetail> auditDetails, String type,String eduCationLoanID) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;

		for (int i = 0; i < auditDetails.size(); i++) {

			EducationalExpense educationalExpense = (EducationalExpense) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;                                                                                                      
			approveRec=false;
			String rcdType ="";	
			String recordStatus ="";
			educationalExpense.setLoanRefNumber(eduCationLoanID);
			if (type.equals("")) {
				approveRec=true;
				educationalExpense.setVersion(educationalExpense.getVersion()+1);
				educationalExpense.setRoleCode("");
				educationalExpense.setNextRoleCode("");
				educationalExpense.setTaskId("");
				educationalExpense.setNextTaskId("");
			}

			educationalExpense.setWorkflowId(0);

			if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(educationalExpense.isNewRecord()){
				saveRecord=true;
				if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			}else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if(approveRec){
					deleteRecord=true;
				}else if(educationalExpense.isNew()){
					saveRecord=true;
				}else {
					updateRecord=true;
				}
			}

			if(approveRec){
				rcdType= educationalExpense.getRecordType();
				recordStatus = educationalExpense.getRecordStatus();
				educationalExpense.setRecordType("");
				educationalExpense.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {
				getEducationalExpenseDAO().save(educationalExpense, type);
			}
			if (updateRecord) {
				getEducationalExpenseDAO().update(educationalExpense, type);
			}
			if (deleteRecord) {
				getEducationalExpenseDAO().delete(educationalExpense, type);
			}

			if(approveRec){
				educationalExpense.setRecordType(rcdType);
				educationalExpense.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(educationalExpense);
		}
		logger.debug("Leaving ");
		return auditDetails;	
	}

	/**
	 * This method deletes Educational expenses records related to  EducationalLoan 
	 *  by calling EducationalExpenseDAO's delete method
	 * @param educationalLoan
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(EducationalLoan educationalLoan, String tableType, String auditTranType) {

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		EducationalExpense educationalExpense=new EducationalExpense();
		if(educationalLoan.getEduExpenseList()!=null && educationalLoan.getEduExpenseList().size()>0){
			String[] fields = PennantJavaUtil.getFieldDetails(new EducationalExpense());
			for (int i = 0; i <educationalLoan.getEduExpenseList().size(); i++) {
				EducationalExpense expense = educationalLoan.getEduExpenseList().get(i);
				if(!expense.getRecordType().equals("") || tableType.equals("")){
					auditList.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], 
							expense.getBefImage(), expense));
				}
			}
			educationalExpense.setLoanRefNumber(educationalLoan.getLoanRefNumber());
			getEducationalExpenseDAO().delete(educationalExpense.getLoanRefNumber(), tableType);
		}
		return auditList;
	}

	/** 
	 * Common Method for Customers list validation
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list){
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList =new ArrayList<AuditDetail>();

		if(list!=null & list.size()>0){

			for (int i = 0; i < list.size(); i++) {

				String transType="";
				String rcdType = "";
				EducationalExpense expense = (EducationalExpense) ((AuditDetail)list.get(i)).getModelData();			

				rcdType = expense.getRecordType();

				if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					transType= PennantConstants.TRAN_ADD;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) || 
						rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					transType= PennantConstants.TRAN_DEL;
				}else{
					transType= PennantConstants.TRAN_UPD;
				}

				if(!(transType.equals(""))){
					//check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail)list.get(i)).getAuditSeq(),expense.getBefImage(), expense));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

	@Override
	public EducationalLoan getEducationalLoanById(String finReference, String tableType) {
		return getEducationalLoanDAO().getEducationalLoanByID(finReference, tableType);
	}


	@Override
	public void setEducationalLoanDetails(FinanceDetail financeDetail, String tableType) {
		String finReference = financeDetail.getFinScheduleData().getFinReference();
		EducationalLoan educationalLoan = null;
		List<EducationalExpense> eduExpenseList = null;

		educationalLoan = getEducationalLoanDAO().getEducationalLoanByID(finReference, tableType);
		financeDetail.setEducationalLoan(educationalLoan);

		if(educationalLoan != null) {
			eduExpenseList = getEducationalExpenseDAO().getEducationalExpenseByEduLoanId(finReference, tableType);
			educationalLoan.setEduExpenseList(eduExpenseList);
		}


	}
	
	@Override
	public List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, String tableType, String auditTranType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new EducationalLoan());
		List<AuditDetail> auditDetails = null;
		EducationalLoan educationalLoan = financeDetail.getEducationalLoan(); 


		auditDetails = new ArrayList<AuditDetail>();

		if(tableType.equals("")) {
			educationalLoan.setRecordType("");
		}
		educationalLoan.setWorkflowId(0);

		if (educationalLoan.isNewRecord()) {
			getEducationalLoanDAO().save(educationalLoan, tableType);
		} else {
			getEducationalLoanDAO().update(educationalLoan, tableType);
		}

		auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], educationalLoan.getBefImage(), educationalLoan));

		if (educationalLoan.getEduExpenseList() != null && !educationalLoan.getEduExpenseList().isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("EduExpense");
			details = saveOrUpdateEduExpenseList(details, tableType, educationalLoan.getLoanRefNumber());
			auditDetails.addAll(details);
		}


		return auditDetails;
	}
	
	
	@Override
	public List<AuditDetail> doApprove(FinanceDetail financeDetail, String tableType, String auditTranType) {
			
			EducationalLoan educationalLoan = financeDetail.getEducationalLoan();
			List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

			String[] fields = PennantJavaUtil.getFieldDetails(educationalLoan, educationalLoan.getExcludeFields());
			EducationalLoan header = new EducationalLoan();
			BeanUtils.copyProperties(educationalLoan, header);
			
			
			if (tableType.equals("")) {
				educationalLoan.setRoleCode("");
				educationalLoan.setNextRoleCode("");
				educationalLoan.setTaskId("");
				educationalLoan.setNextTaskId("");
			}
			
			educationalLoan.setWorkflowId(0);

			getEducationalLoanDAO().save(educationalLoan, tableType);
			
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], header.getBefImage(), header));
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], educationalLoan.getBefImage(), educationalLoan));

			List<EducationalExpense> educationalExpenses = educationalLoan.getEduExpenseList();
			
			
			if(educationalExpenses !=null && !educationalExpenses.isEmpty()) {
				boolean saveRecord = false;
				boolean updateRecord = false;
				boolean deleteRecord = false;
				boolean approveRec = false;
				
				for (EducationalExpense educationalExpense : educationalExpenses) {
					saveRecord = false;
					updateRecord = false;
					deleteRecord = false;
					approveRec = true;
					String rcdType = "";
					String recordStatus = "";

					EducationalExpense detail = new EducationalExpense();

					BeanUtils.copyProperties(educationalExpense, detail);
					
					if (tableType.equals("")) {
						approveRec = true;
						educationalExpense.setRoleCode("");
						educationalExpense.setNextRoleCode("");
						educationalExpense.setTaskId("");
						educationalExpense.setNextTaskId("");
					}
					
					educationalExpense.setWorkflowId(0);		
					if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						deleteRecord = true;
					} else if (educationalExpense.isNewRecord()) {
						saveRecord = true;
						if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
							educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
							educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
							educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						}

					} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						if (approveRec) {
							saveRecord = true;
						} else {
							updateRecord = true;
						}
					} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
						updateRecord = true;
					} else if (educationalExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
						if (approveRec) {
							deleteRecord = true;
						} else if (educationalExpense.isNew()) {
							saveRecord = true;
						} else {
							updateRecord = true;
						}
					}
					if (approveRec) {
						rcdType = educationalExpense.getRecordType();
						recordStatus = educationalExpense.getRecordStatus();
						educationalExpense.setRecordType("");
						educationalExpense.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					}
					if (saveRecord) {
						getEducationalExpenseDAO().save(educationalExpense, tableType);
					}

					if (updateRecord) {
						getEducationalExpenseDAO().update(educationalExpense, tableType);
					}

					if (deleteRecord) {
						getEducationalExpenseDAO().delete(educationalExpense, tableType);
					}

					if (approveRec) {
						educationalExpense.setRecordType(rcdType);
						educationalExpense.setRecordStatus(recordStatus);
					}
				}
			}
			
			return auditDetails;
		}


	@Override
	public List<AuditDetail> delete(FinanceDetail financeDetail, String tableType, String auditTranType) {
		List<AuditDetail> auditDetails  = new ArrayList<AuditDetail>();
		EducationalLoan object = new EducationalLoan();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());
		
		EducationalLoan educationalLoan = financeDetail.getEducationalLoan();
		getEducationalLoanDAO().delete(educationalLoan, tableType);
		
		auditTranType = getAuditTranType(educationalLoan.getRecordType());

		if ("".equals(auditTranType)) {
			auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], educationalLoan.getBefImage(), educationalLoan));
		}
		
		
		EducationalExpense eduExpense;
		if (educationalLoan.getEduExpenseList() != null && !educationalLoan.getEduExpenseList().isEmpty()) {
			EducationalExpense subObject = new EducationalExpense();
			fields = PennantJavaUtil.getFieldDetails(subObject, subObject.getExcludeFields());
			for (int i = 0; i < educationalLoan.getEduExpenseList().size(); i++) {
				eduExpense = educationalLoan.getEduExpenseList().get(i);
				
				auditTranType = getAuditTranType(eduExpense.getRecordType());				
				if (!eduExpense.getRecordType().equals("")) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], eduExpense.getBefImage(), eduExpense));
				}
			}
			getEducationalExpenseDAO().delete(financeDetail.getFinScheduleData().getFinReference(), tableType);
		}

		return auditDetails;
	}

	@Override
	public List<AuditDetail> validate(FinanceDetail financeDetail, String method, String usrLanguage) {
		List<AuditDetail> auditDetails = financeDetail.getAuditDetailMap().get("EducationalLoan");		
		List<AuditDetail> details = null;
		if(auditDetails!=null && !auditDetails.isEmpty()){
			details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
		}

		if (financeDetail.getEducationalLoan() != null) {
			if (financeDetail.getEducationalLoan().getEduExpenseList() != null) {
				details.addAll(validateEduExpense(financeDetail, method, usrLanguage));
			}
		}	


		if(details == null) {
			details = new ArrayList<AuditDetail>();
		}

		return details;

	}

	private AuditDetail validate(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		EducationalLoan educationalLoan= (EducationalLoan) auditDetail.getModelData();

		EducationalLoan tempEducationalLoan= null;
		if (educationalLoan.isWorkflow()){
			tempEducationalLoan = getEducationalLoanDAO().getEducationalLoanByID(
					educationalLoan.getLoanRefNumber(), "_Temp");
		}
		EducationalLoan befEducationalLoan= getEducationalLoanDAO().getEducationalLoanByID(
				educationalLoan.getLoanRefNumber(), "");

		EducationalLoan old_EducationalLoan= educationalLoan.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(educationalLoan.getLoanRefNumber());
		errParm[0]=PennantJavaUtil.getLabel("label_LoanRefNumber")+":"+valueParm[0];

		if (educationalLoan.isNew()){ // for New record or new record into work flow

			if (!educationalLoan.isWorkflow()){// With out Work flow only new records  
				if (befEducationalLoan !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (educationalLoan.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befEducationalLoan !=null || tempEducationalLoan!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befEducationalLoan ==null || tempEducationalLoan!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!educationalLoan.isWorkflow()){	// With out Work flow for update and delete

				if (befEducationalLoan ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_EducationalLoan!=null && !old_EducationalLoan.getLastMntOn().equals(
							befEducationalLoan.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempEducationalLoan==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempEducationalLoan!=null && old_EducationalLoan!=null && !old_EducationalLoan.getLastMntOn().equals(
						tempEducationalLoan.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !educationalLoan.isWorkflow()){
			educationalLoan.setBefImage(befEducationalLoan);	
		}
		logger.debug("Leaving ");
		return auditDetail;
	}



	private List<AuditDetail> validateEduExpense(FinanceDetail financeDetail, String method,String  usrLanguage){
		List<AuditDetail> auditDetails = financeDetail.getAuditDetailMap().get("EduExpense");

		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validateEduExpense(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	private AuditDetail validateEduExpense(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		EducationalExpense educationalExpense= (EducationalExpense) auditDetail.getModelData();

		EducationalExpense tempEducationalExpense= null;
		if (educationalExpense.isWorkflow()){
			tempEducationalExpense = getEducationalExpenseDAO().getEducationalExpenseByID(
					educationalExpense.getLoanRefNumber(),educationalExpense.getId(), "_Temp");
		}
		EducationalExpense befEducationalExpense= getEducationalExpenseDAO().getEducationalExpenseByID(
				educationalExpense.getLoanRefNumber(),educationalExpense.getId(), "");

		EducationalExpense old_EducationalExpense= educationalExpense.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(educationalExpense.getLoanRefNumber());
		errParm[0]=PennantJavaUtil.getLabel("label_loanReferenceNumber")+":"+valueParm[0];

		if (educationalExpense.isNew()){ // for New record or new record into work flow

			if (!educationalExpense.isWorkflow()){// With out Work flow only new records  
				if (befEducationalExpense !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (educationalExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befEducationalExpense !=null || tempEducationalExpense!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befEducationalExpense ==null || tempEducationalExpense!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!educationalExpense.isWorkflow()){	// With out Work flow for update and delete

				if (befEducationalExpense ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_EducationalExpense!=null && !old_EducationalExpense.getLastMntOn().equals(befEducationalExpense.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempEducationalExpense==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempEducationalExpense!=null && old_EducationalExpense!=null && !old_EducationalExpense.getLastMntOn().equals(tempEducationalExpense.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !educationalExpense.isWorkflow()){
			educationalExpense.setBefImage(befEducationalExpense);	
		}

		return auditDetail;
	}


	/**
	 * set the financeMain Workflow details to EducationalLoan module and create auditDetails
	 * 
	 * and put the auditDetails into auditDetailMap
	 * 
	 * @param Map
	 *            <String, List<AuditDetail>> (auditDetailMap)
	 * @param FinanceDetail
	 *            (financeDetail)
	 * @param String
	 *            (auditTranType)
	 * @return List<AuditDetail> auditDetails
	 */
	@Override
	public List<AuditDetail> getAuditDetail(Map<String, List<AuditDetail>> auditDetailMap, FinanceDetail financeDetail, String auditTranType, String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<AuditDetail> subAuditDetails;
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain(); 

		EducationalLoan educationalLoan = financeDetail.getEducationalLoan();
		educationalLoan.setWorkflowId(financeMain.getWorkflowId());

		EducationalLoan object = new EducationalLoan();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());
		auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], educationalLoan.getBefImage(), educationalLoan));

		auditDetailMap.put("EducationalLoan", auditDetails);

		if (educationalLoan.getEduExpenseList() != null && !educationalLoan.getEduExpenseList().isEmpty()) {
			subAuditDetails = setEduExpenseAuditData(educationalLoan, auditTranType, method);
			auditDetailMap.put("EduExpense", subAuditDetails);
		}

		return auditDetails;

	}

}