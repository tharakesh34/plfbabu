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
 * FileName    		:  FacilityReferenceDetailServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.lmtmasters.FacilityReferenceDetailDAO;
import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceWorkFlowDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.FacilityReference;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.lmtmasters.FacilityReferenceDetailService;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on
 * <b>FacilityReferenceDetail</b>.<br>
 * 
 */
public class FacilityReferenceDetailServiceImpl extends GenericService<FacilityReferenceDetail> implements FacilityReferenceDetailService {
	private static final Logger logger = Logger.getLogger(FacilityReferenceDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceWorkFlowDAO financeWorkFlowDAO;
	private FacilityReferenceDetailDAO facilityReferenceDetailDAO;

	public FacilityReferenceDetailServiceImpl() {
		super();
	}
	
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
	 * @return the facilityReferenceDetailDAO
	 */
	public FacilityReferenceDetailDAO getFacilityReferenceDetailDAO() {
		return facilityReferenceDetailDAO;
	}

	/**
	 * @param facilityReferenceDetailDAO
	 *            the facilityReferenceDetailDAO to set
	 */
	public void setFacilityReferenceDetailDAO(FacilityReferenceDetailDAO facilityReferenceDetailDAO) {
		this.facilityReferenceDetailDAO = facilityReferenceDetailDAO;
	}

	public FinanceWorkFlowDAO getFinanceWorkFlowDAO() {
		return financeWorkFlowDAO;
	}

	public void setFinanceWorkFlowDAO(FinanceWorkFlowDAO financeWorkFlowDAO) {
		this.financeWorkFlowDAO = financeWorkFlowDAO;
	}

	@Override
	public FacilityReference getFacilityReference(String finType) {
		FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowDAO().getFinanceWorkFlowById(
				finType,FinanceConstants.FINSER_EVENT_ORG, FacilityConstants.MODULE_NAME, "_AView");
		FacilityReference financeReference = new FacilityReference();
		financeReference.setFinType(finType);

		if (financeWorkFlow != null) {
			financeReference.setLovDescFinTypeDescName(financeWorkFlow.getLovDescFinTypeName());
			financeReference.setWorkFlowType(financeWorkFlow.getWorkFlowType());
			financeReference.setLovDescWorkFlowTypeName(financeWorkFlow.getLovDescWorkFlowTypeName());
			financeReference.setLovDescWorkFlowRolesName(financeWorkFlow.getLovDescWorkFlowRolesName());

			List<FacilityReferenceDetail> refList = getFacilityReferenceDetailDAO().getFacilityReferenceDetailById(finType);
			financeReference.setCheckList(new ArrayList<FacilityReferenceDetail>());
			financeReference.setAggrementList(new ArrayList<FacilityReferenceDetail>());
			financeReference.setScoringGroupList(new ArrayList<FacilityReferenceDetail>());
			financeReference.setCorpScoringGroupList(new ArrayList<FacilityReferenceDetail>());
			financeReference.setMailTemplateList(new ArrayList<FacilityReferenceDetail>());
			if (refList != null && !refList.isEmpty()) {
				for (FacilityReferenceDetail facRefDetail : refList) {
					switch (facRefDetail.getFinRefType()) {
					case FinanceConstants.PROCEDT_CHECKLIST:
						financeReference.getCheckList().add(facRefDetail);
						break;
					case FinanceConstants.PROCEDT_AGREEMENT:
						financeReference.getAggrementList().add(facRefDetail);
						break;
					case FinanceConstants.PROCEDT_RTLSCORE:
						financeReference.getScoringGroupList().add(facRefDetail);
						break;
					case FinanceConstants.PROCEDT_CORPSCORE:	
						financeReference.getCorpScoringGroupList().add(facRefDetail);
						break;
					case FinanceConstants.PROCEDT_TEMPLATE:	
						financeReference.getMailTemplateList().add(facRefDetail);
						break;
					default:
						break;
					}
				}
			}
		}

		return financeReference;
	}


	/**
	 * @return the facilityReferenceDetail for New Record
	 */
	@Override
	public FacilityReferenceDetail getNewFacilityReferenceDetail() {
		return getFacilityReferenceDetailDAO().getNewFacilityReferenceDetail();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * LMTFinRefDetail/LMTFinRefDetail_Temp by using FacilityReferenceDetailDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using FacilityReferenceDetailDAO's update
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
		FacilityReferenceDetail facilityReferenceDetail = (FacilityReferenceDetail) auditHeader.getAuditDetail().getModelData();

		if (facilityReferenceDetail.isWorkflow()) {
			tableType = "_Temp";
		}

		if (facilityReferenceDetail.isNew()) {
			facilityReferenceDetail.setId(getFacilityReferenceDetailDAO().save(facilityReferenceDetail, tableType));
			auditHeader.getAuditDetail().setModelData(facilityReferenceDetail);
			auditHeader.setAuditReference(String.valueOf(facilityReferenceDetail.getFinRefDetailId()));
		} else {
			getFacilityReferenceDetailDAO().update(facilityReferenceDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table LMTFinRefDetail by using FacilityReferenceDetailDAO's delete method
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

		FacilityReferenceDetail facilityReferenceDetail = (FacilityReferenceDetail) auditHeader.getAuditDetail().getModelData();
		getFacilityReferenceDetailDAO().delete(facilityReferenceDetail, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFacilityReferenceDetailById fetch the details by using
	 * FacilityReferenceDetailDAO's getFacilityReferenceDetailById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FacilityReferenceDetail
	 */

	@Override
	public FacilityReferenceDetail getFacilityReferenceDetailById(long id) {
		return getFacilityReferenceDetailDAO().getFacilityReferenceDetailById(id, "_View");
	}

	/**
	 * getApprovedFacilityReferenceDetailById fetch the details by using
	 * FacilityReferenceDetailDAO's getFacilityReferenceDetailById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * LMTFinRefDetail.
	 * 
	 * @param id
	 *            (int)
	 * @return FacilityReferenceDetail
	 */

	public FacilityReferenceDetail getApprovedFacilityReferenceDetailById(long id) {
		return getFacilityReferenceDetailDAO().getFacilityReferenceDetailById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getFacilityReferenceDetailDAO().delete with parameters
	 * facilityReferenceDetail,"" b) NEW Add new record in to main table by using
	 * getFacilityReferenceDetailDAO().save with parameters
	 * facilityReferenceDetail,"" c) EDIT Update record in the main table by
	 * using getFacilityReferenceDetailDAO().update with parameters
	 * facilityReferenceDetail,"" 3) Delete the record from the workFlow table by
	 * using getFacilityReferenceDetailDAO().delete with parameters
	 * facilityReferenceDetail,"_Temp" 4) Audit the record in to AuditHeader and
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

		FacilityReferenceDetail facilityReferenceDetail = new FacilityReferenceDetail();
		BeanUtils.copyProperties((FacilityReferenceDetail) auditHeader.getAuditDetail().getModelData(), facilityReferenceDetail);

		if (facilityReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getFacilityReferenceDetailDAO().delete(facilityReferenceDetail, "");

		} else {
			facilityReferenceDetail.setRoleCode("");
			facilityReferenceDetail.setNextRoleCode("");
			facilityReferenceDetail.setTaskId("");
			facilityReferenceDetail.setNextTaskId("");
			facilityReferenceDetail.setWorkflowId(0);

			if (facilityReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				facilityReferenceDetail.setRecordType("");
				getFacilityReferenceDetailDAO().save(facilityReferenceDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				facilityReferenceDetail.setRecordType("");
				getFacilityReferenceDetailDAO().update(facilityReferenceDetail, "");
			}
		}

		getFacilityReferenceDetailDAO().delete(facilityReferenceDetail, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(facilityReferenceDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getFacilityReferenceDetailDAO().delete with
	 * parameters facilityReferenceDetail,"_Temp" 3) Audit the record in to
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

		FacilityReferenceDetail facilityReferenceDetail = (FacilityReferenceDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFacilityReferenceDetailDAO().delete(facilityReferenceDetail, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getFacilityReferenceDetailDAO().getErrorDetail with Error ID and language
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
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FacilityReferenceDetail facilityReferenceDetail = (FacilityReferenceDetail) auditDetail.getModelData();

		FacilityReferenceDetail tempFacilityReferenceDetail = null;
		if (facilityReferenceDetail.isWorkflow()) {
			tempFacilityReferenceDetail = getFacilityReferenceDetailDAO().getFacilityReferenceDetailById(facilityReferenceDetail.getId(), "_Temp");
		}
		FacilityReferenceDetail befFacilityReferenceDetail = getFacilityReferenceDetailDAO().getFacilityReferenceDetailById(facilityReferenceDetail.getId(), "");
		FacilityReferenceDetail oldFacilityReferenceDetail = facilityReferenceDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(facilityReferenceDetail.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_FinRefDetailId") + ":" + valueParm[0];

		if (facilityReferenceDetail.isNew()) { // for New record or new record
			// into work flow

			if (!facilityReferenceDetail.isWorkflow()) {// With out Work flow
				// only new records
				if (befFacilityReferenceDetail != null) { // Record Already
					// Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (facilityReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					// records type is new
					if (befFacilityReferenceDetail != null || tempFacilityReferenceDetail != null) {
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFacilityReferenceDetail == null || tempFacilityReferenceDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!facilityReferenceDetail.isWorkflow()) { // With out Work flow
				// for update and delete

				if (befFacilityReferenceDetail == null) { // if records not
					// exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFacilityReferenceDetail != null && !oldFacilityReferenceDetail.getLastMntOn().equals(befFacilityReferenceDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempFacilityReferenceDetail == null) { // if records not
					// exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFacilityReferenceDetail != null && oldFacilityReferenceDetail != null && !oldFacilityReferenceDetail.getLastMntOn().equals(tempFacilityReferenceDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !facilityReferenceDetail.isWorkflow()) {
			facilityReferenceDetail.setBefImage(befFacilityReferenceDetail);
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
	public List<FacilityReferenceDetail>  getCheckListByFinRef(final String finType){
		logger.debug("Entering ");
		List<FacilityReferenceDetail> finRefDetailList =getFacilityReferenceDetailDAO().getFacilityReferenceDetail(finType,"", "_TQView");
		//		for(FacilityReferenceDetail   finRefDetail:finRefDetailList){
		//			finRefDetail.setLovDesccheckListDetail(getCheckListDetailDAO()
		//					.getCheckListDetailByChkList(finRefDetail.getFinRefId(), "_AView"));
		//
		//		}
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

		return getFinanceCheckListReferenceDAO().getCheckListByFinRef(id,null, type);

	}

	/**
	 * Method for Fetching Eligibility Rule List based upon Finance Type
	 */
	public List<FacilityReferenceDetail> getFinRefDetByRoleAndFinType(final String financeType, 
			String mandInputInStage, String type){
		return getFacilityReferenceDetailDAO().getFinRefDetByRoleAndFinType(financeType, 
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