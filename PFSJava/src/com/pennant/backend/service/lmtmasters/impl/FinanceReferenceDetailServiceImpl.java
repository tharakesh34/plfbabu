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
 * FileName    		:  FinanceReferenceDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-11-2011    														*
 *                                                                  						*
 * Modified Date    :  26-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-11-2011       Pennant	                 0.1                                            * 
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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CheckListDAO;
import com.pennant.backend.dao.applicationmaster.CheckListDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.lmtmasters.FinanceWorkFlowDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on
 * <b>FinanceReferenceDetail</b>.<br>
 * 
 */
public class FinanceReferenceDetailServiceImpl extends GenericService<FinanceReferenceDetail> implements FinanceReferenceDetailService {
	private final static Logger logger = Logger.getLogger(FinanceReferenceDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceWorkFlowDAO financeWorkFlowDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the financeReferenceDetailDAO
	 */
	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	/**
	 * @param financeReferenceDetailDAO
	 *            the financeReferenceDetailDAO to set
	 */
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public FinanceWorkFlowDAO getFinanceWorkFlowDAO() {
		return financeWorkFlowDAO;
	}

	public void setFinanceWorkFlowDAO(FinanceWorkFlowDAO financeWorkFlowDAO) {
		this.financeWorkFlowDAO = financeWorkFlowDAO;
	}

	@Override
	public FinanceReference getFinanceReference(String finType) {
		FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowDAO().getFinanceWorkFlowById(finType, "_AView");
		FinanceReference financeReference = new FinanceReference();
		financeReference.setFinType(finType);

		if (financeWorkFlow != null) {
			financeReference.setLovDescFinTypeDescName(financeWorkFlow.getLovDescFinTypeName());
			financeReference.setWorkFlowType(financeWorkFlow.getWorkFlowType());
			financeReference.setLovDescWorkFlowTypeName(financeWorkFlow.getLovDescWorkFlowTypeName());
			financeReference.setLovDescWorkFlowRolesName(financeWorkFlow.getLovDescWorkFlowRolesName());

			financeReference.setCheckList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType,"", "_TQView"));
			financeReference.setAggrementList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType,"", "_TAView"));
			financeReference.setEligibilityRuleList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType,"","_TEView"));
			financeReference.setScoringGroupList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType, "","_TSGView"));
			financeReference.setCorpScoringGroupList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType, "","_TCSGView"));
			financeReference.setAccountingList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType, "","_TCView"));
			financeReference.setMailTemplateList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType, "","_TTView"));
			financeReference.setFinanceDedupeList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType, "","_TFDView"));
		}

		return financeReference;
	}

	//TODO NEED TO RENAME METHOD
	@Override
	public FinanceReference getFinanceReferenceList(String finType) {
		FinanceReference financeReference = new FinanceReference();
		financeReference.setFinType(finType);
		financeReference.setAggrementList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType,"", "_TAView"));
		financeReference.setEligibilityRuleList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType,"", "_TEView"));
		financeReference.setScoringGroupList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType,"", "_TSGView"));
		financeReference.setCorpScoringGroupList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType,"", "_TCSGView"));
		return financeReference;
	}

	@Override
	public FinanceReference getApprovedFinanceReference(String finType) {
		FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowDAO().getFinanceWorkFlowById(finType, "_AView");
		FinanceReference financeReference = new FinanceReference();
		financeReference.setFinType(finType);
		if (financeWorkFlow != null) {
			financeReference.setLovDescFinTypeDescName(financeWorkFlow.getLovDescFinTypeName());
			financeReference.setWorkFlowType(financeWorkFlow.getWorkFlowType());
			financeReference.setLovDescWorkFlowTypeName(financeWorkFlow.getLovDescWorkFlowTypeName());
			financeReference.setLovDescWorkFlowRolesName(financeWorkFlow.getLovDescWorkFlowRolesName());

			financeReference.setCheckList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType, "","_AQView"));
			financeReference.setAggrementList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType,"", "_AAView"));
			financeReference.setEligibilityRuleList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType,"", "_AEView"));
			financeReference.setScoringGroupList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType,"", "_ASGView"));
			financeReference.setCorpScoringGroupList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType,"", "_ACSGView"));
			financeReference.setFinanceDedupeList(getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType, "","_AFDView"));

		}

		return financeReference;
	}

	/**
	 * @return the financeReferenceDetail
	 */
	@Override
	public FinanceReferenceDetail getFinanceReferenceDetail() {
		return getFinanceReferenceDetailDAO().getFinanceReferenceDetail();
	}

	/**
	 * @return the financeReferenceDetail for New Record
	 */
	@Override
	public FinanceReferenceDetail getNewFinanceReferenceDetail() {
		return getFinanceReferenceDetailDAO().getNewFinanceReferenceDetail();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * LMTFinRefDetail/LMTFinRefDetail_Temp by using FinanceReferenceDetailDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using FinanceReferenceDetailDAO's update
	 * method 3) Audit the record in to AuditHeader and AdtLMTFinRefDetail by
	 * using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		FinanceReferenceDetail financeReferenceDetail = (FinanceReferenceDetail) auditHeader.getAuditDetail().getModelData();

		if (financeReferenceDetail.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (financeReferenceDetail.isNew()) {
			financeReferenceDetail.setId(getFinanceReferenceDetailDAO().save(financeReferenceDetail, tableType));
			auditHeader.getAuditDetail().setModelData(financeReferenceDetail);
			auditHeader.setAuditReference(String.valueOf(financeReferenceDetail.getFinRefDetailId()));
		} else {
			getFinanceReferenceDetailDAO().update(financeReferenceDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table LMTFinRefDetail by using FinanceReferenceDetailDAO's delete method
	 * with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtLMTFinRefDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceReferenceDetail financeReferenceDetail = (FinanceReferenceDetail) auditHeader.getAuditDetail().getModelData();
		getFinanceReferenceDetailDAO().delete(financeReferenceDetail, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFinanceReferenceDetailById fetch the details by using
	 * FinanceReferenceDetailDAO's getFinanceReferenceDetailById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceReferenceDetail
	 */

	@Override
	public FinanceReferenceDetail getFinanceReferenceDetailById(long id) {
		return getFinanceReferenceDetailDAO().getFinanceReferenceDetailById(id, "_View");
	}

	/**
	 * getApprovedFinanceReferenceDetailById fetch the details by using
	 * FinanceReferenceDetailDAO's getFinanceReferenceDetailById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * LMTFinRefDetail.
	 * 
	 * @param id
	 *            (int)
	 * @return FinanceReferenceDetail
	 */

	public FinanceReferenceDetail getApprovedFinanceReferenceDetailById(long id) {
		return getFinanceReferenceDetailDAO().getFinanceReferenceDetailById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinanceReferenceDetail
	 *            (financeReferenceDetail)
	 * @return financeReferenceDetail
	 */
	@Override
	public FinanceReferenceDetail refresh(FinanceReferenceDetail financeReferenceDetail) {
		logger.debug("Entering");
		getFinanceReferenceDetailDAO().refresh(financeReferenceDetail);
		getFinanceReferenceDetailDAO().initialize(financeReferenceDetail);
		logger.debug("Leaving");
		return financeReferenceDetail;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getFinanceReferenceDetailDAO().delete with parameters
	 * financeReferenceDetail,"" b) NEW Add new record in to main table by using
	 * getFinanceReferenceDetailDAO().save with parameters
	 * financeReferenceDetail,"" c) EDIT Update record in the main table by
	 * using getFinanceReferenceDetailDAO().update with parameters
	 * financeReferenceDetail,"" 3) Delete the record from the workFlow table by
	 * using getFinanceReferenceDetailDAO().delete with parameters
	 * financeReferenceDetail,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtLMTFinRefDetail by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtLMTFinRefDetail by
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

		FinanceReferenceDetail financeReferenceDetail = new FinanceReferenceDetail();
		BeanUtils.copyProperties((FinanceReferenceDetail) auditHeader.getAuditDetail().getModelData(), financeReferenceDetail);

		if (financeReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getFinanceReferenceDetailDAO().delete(financeReferenceDetail, "");

		} else {
			financeReferenceDetail.setRoleCode("");
			financeReferenceDetail.setNextRoleCode("");
			financeReferenceDetail.setTaskId("");
			financeReferenceDetail.setNextTaskId("");
			financeReferenceDetail.setWorkflowId(0);

			if (financeReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeReferenceDetail.setRecordType("");
				getFinanceReferenceDetailDAO().save(financeReferenceDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeReferenceDetail.setRecordType("");
				getFinanceReferenceDetailDAO().update(financeReferenceDetail, "");
			}
		}

		getFinanceReferenceDetailDAO().delete(financeReferenceDetail, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeReferenceDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getFinanceReferenceDetailDAO().delete with
	 * parameters financeReferenceDetail,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtLMTFinRefDetail by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceReferenceDetail financeReferenceDetail = (FinanceReferenceDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinanceReferenceDetailDAO().delete(financeReferenceDetail, "_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getFinanceReferenceDetailDAO().getErrorDetail with Error ID and language
	 * as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		FinanceReferenceDetail financeReferenceDetail = (FinanceReferenceDetail) auditDetail.getModelData();

		FinanceReferenceDetail tempFinanceReferenceDetail = null;
		if (financeReferenceDetail.isWorkflow()) {
			tempFinanceReferenceDetail = getFinanceReferenceDetailDAO().getFinanceReferenceDetailById(financeReferenceDetail.getId(), "_Temp");
		}
		FinanceReferenceDetail befFinanceReferenceDetail = getFinanceReferenceDetailDAO().getFinanceReferenceDetailById(financeReferenceDetail.getId(), "");
		FinanceReferenceDetail oldFinanceReferenceDetail = financeReferenceDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(financeReferenceDetail.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_FinRefDetailId") + ":" + valueParm[0];

		if (financeReferenceDetail.isNew()) { // for New record or new record
												// into work flow

			if (!financeReferenceDetail.isWorkflow()) {// With out Work flow
														// only new records
				if (befFinanceReferenceDetail != null) { // Record Already
															// Exists in the
															// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																										// records
																										// type
																										// is
																										// new
					if (befFinanceReferenceDetail != null || tempFinanceReferenceDetail != null) { // if
																									// records
																									// already
																									// exists
																									// in
																									// the
																									// main
																									// table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceReferenceDetail == null || tempFinanceReferenceDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!financeReferenceDetail.isWorkflow()) { // With out Work flow
														// for update and delete

				if (befFinanceReferenceDetail == null) { // if records not
															// exists in the
															// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceReferenceDetail != null && !oldFinanceReferenceDetail.getLastMntOn().equals(befFinanceReferenceDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempFinanceReferenceDetail == null) { // if records not
															// exists in the
															// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldFinanceReferenceDetail != null && !oldFinanceReferenceDetail.getLastMntOn().equals(tempFinanceReferenceDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !financeReferenceDetail.isWorkflow()) {
			financeReferenceDetail.setBefImage(befFinanceReferenceDetail);
		}

		return auditDetail;
	}
	
	//---------------------------------
	// Check List Details
	//---------------------------------
	
	private CheckListDAO checkListDAO;
	private CheckListDetailDAO checkListDetailDAO;
	private FinanceCheckListReferenceDAO FinanceCheckListReferenceDAO;
	/**
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<FinanceReferenceDetail>  getCheckListByFinRef(final String finType){
		logger.debug("Entering ");
		List<FinanceReferenceDetail> finRefDetailList =getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType,"", "_TQView");
		for(FinanceReferenceDetail   finRefDetail:finRefDetailList){
			finRefDetail.setLovDesccheckListDetail(getCheckListDetailDAO()
					.getCheckListDetailByChkList(finRefDetail.getFinRefId(), "_AView"));

		}
		logger.debug("Leaving ");
		return finRefDetailList;
	}
	/**
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<FinanceCheckListReference> getFinanceCheckListReferenceFinRef(final String id, String type){

		return getFinanceCheckListReferenceDAO().getCheckListByFinRef(id, null, type);

	}
	
	/**
	 * Method for Fetching Eligibility Rule List based upon Finance Type
	 */
	public List<FinanceReferenceDetail> getFinRefDetByRoleAndFinType(final String financeType, 
			String mandInputInStage, String type){
		return getFinanceReferenceDetailDAO().getFinRefDetByRoleAndFinType(financeType, 
				mandInputInStage,null, type);
	}

	public void setCheckListDAO(CheckListDAO checkListDAO) {
		this.checkListDAO = checkListDAO;
	}

	public CheckListDAO getCheckListDAO() {
		return checkListDAO;
	}
	public void setCheckListDetailDAO(CheckListDetailDAO checkListDetailDAO) {
		this.checkListDetailDAO = checkListDetailDAO;
	}

	public CheckListDetailDAO getCheckListDetailDAO() {
		return checkListDetailDAO;
	}

	public void setFinanceCheckListReferenceDAO(
			FinanceCheckListReferenceDAO financeCheckListReferenceDAO) {
		FinanceCheckListReferenceDAO = financeCheckListReferenceDAO;
	}

	public FinanceCheckListReferenceDAO getFinanceCheckListReferenceDAO() {
		return FinanceCheckListReferenceDAO;
	}
}