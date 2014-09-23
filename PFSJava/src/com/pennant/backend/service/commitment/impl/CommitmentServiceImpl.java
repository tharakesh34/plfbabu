/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : CommitmentServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-03-2013 * *
 * Modified Date : 25-03-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 25-03-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.service.commitment.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.commitment.CommitmentSummary;
import com.pennant.backend.model.reports.AvailCommitment;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;

/**
 * Service implementation for methods that depends on <b>Commitment</b>.<br>
 * 
 */
public class CommitmentServiceImpl extends GenericService<Commitment> implements CommitmentService {
	private final static Logger logger = Logger.getLogger(CommitmentServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private CommitmentDAO commitmentDAO;
	private CommitmentMovementDAO commitmentMovementDAO;
	private RuleDAO ruleDAO;
	private PostingsPreparationUtil postingsPreparationUtil;

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
	 * @return the commitmentDAO
	 */
	public CommitmentDAO getCommitmentDAO() {
		return commitmentDAO;
	}

	/**
	 * @param commitmentDAO
	 *            the commitmentDAO to set
	 */
	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
		this.commitmentDAO = commitmentDAO;
	}

	public CommitmentMovementDAO getCommitmentMovementDAO() {
		return commitmentMovementDAO;
	}

	public void setCommitmentMovementDAO(CommitmentMovementDAO commitmentMovementDAO) {
		this.commitmentMovementDAO = commitmentMovementDAO;
	}

	/**
	 * @return the commitment
	 */
	@Override
	public Commitment getCommitment() {
		return getCommitmentDAO().getCommitment();
	}

	/**
	 * @return the commitment for New Record
	 */
	@Override
	public Commitment getNewCommitment() {
		return getCommitmentDAO().getNewCommitment();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Commitments/Commitments_Temp by
	 * using CommitmentDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using CommitmentDAO's update method 3) Audit the record in to AuditHeader and AdtCommitments by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Commitments/Commitments_Temp by
	 * using CommitmentDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using CommitmentDAO's update method 3) Audit the record in to AuditHeader and AdtCommitments by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean online) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate", online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		Commitment commitment = (Commitment) auditHeader.getAuditDetail().getModelData();

		if (commitment.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (commitment.isNew()) {
			getCommitmentDAO().save(commitment, tableType);
			auditHeader.getAuditDetail().setModelData(commitment);
			auditHeader.setAuditReference(String.valueOf(commitment.getCmtReference()));
			if (StringUtils.trimToEmpty(commitment.getRecordType()).equals("")
			        || StringUtils.trimToEmpty(commitment.getRecordType()).equals(
			                PennantConstants.RECORD_TYPE_NEW)) {
				commitment.getCommitmentMovement().setMovementType("NC");
			} else {
				commitment.getCommitmentMovement().setMovementType("MC");
			}
			commitment.getCommitmentMovement().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			getCommitmentMovementDAO().save(commitment.getCommitmentMovement(), tableType);
		} else {
			getCommitmentDAO().update(commitment, tableType);
			getCommitmentMovementDAO().update(commitment.getCommitmentMovement(), tableType);
			//			if (commitment.getRecordStatus().equalsIgnoreCase("Approved")
			//			        && commitment.getRecordType() != null) {
			//				commitment.getCommitmentMovement().setMovementType("NC");
			//				getCommitmentMovementDAO().save(commitment.getCommitmentMovement(), tableType);
			//			} else {
			//				commitment.getCommitmentMovement().setMovementType("MC");
			//			}
		}

		//Retrieving List of Audit Details For check list detail  related modules
		if (commitment.getCommitmentMovement() != null) {
			AuditDetail details = commitment.getCommitmentMovement().getLovDescAuditDetailMap()
			        .get("CommitmentMovement");
			details = processingChkListDetailList(details, tableType, 0);
			auditDetails.add(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Commitments by using CommitmentDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtCommitments by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Commitment commitment = (Commitment) auditHeader.getAuditDetail().getModelData();
		getCommitmentDAO().delete(commitment, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(commitment, "",
		        auditHeader.getAuditTranType())));
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCommitmentById fetch the details by using CommitmentDAO's getCommitmentById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Commitment
	 */

	@Override
	public Commitment getCommitmentById(String id) {
		Commitment commitment = getCommitmentDAO().getCommitmentById(id, "_View");
		commitment.setCommitmentMovement(getCommitmentMovementDAO().getCommitmentMovementById(id,
		        "_View"));
		return commitment;
	}

	/**
	 * getApprovedCommitmentById fetch the details by using CommitmentDAO's getCommitmentById method . with parameter id
	 * and type as blank. it fetches the approved records from the Commitments.
	 * 
	 * @param id
	 *            (String)
	 * @return Commitment
	 */

	public Commitment getApprovedCommitmentById(String id) {
		return getCommitmentDAO().getCommitmentById(id, "_AView");
	}

	public int getCmtAmountCount(long custID) {
		return getCommitmentDAO().getCmtAmountCount(custID);
	}

	public int getCmtAmountTotal(long custID) {
		return getCommitmentDAO().getCmtAmountTotal(custID);
	}

	public int getUtilizedAmountTotal(long custID) {
		return getCommitmentDAO().getUtilizedAmountTotal(custID);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Commitment
	 *            (commitment)
	 * @return commitment
	 */
	@Override
	public Commitment refresh(Commitment commitment) {
		logger.debug("Entering");
		getCommitmentDAO().refresh(commitment);
		getCommitmentDAO().initialize(commitment);
		logger.debug("Leaving");
		return commitment;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCommitmentDAO().delete with
	 * parameters commitment,"" b) NEW Add new record in to main table by using getCommitmentDAO().save with parameters
	 * commitment,"" c) EDIT Update record in the main table by using getCommitmentDAO().update with parameters
	 * commitment,"" 3) Delete the record from the workFlow table by using getCommitmentDAO().delete with parameters
	 * commitment,"_Temp" 4) Audit the record in to AuditHeader and AdtCommitments by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtCommitments by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		long linkTranid = 0;
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Commitment commitment = new Commitment();
		BeanUtils.copyProperties((Commitment) auditHeader.getAuditDetail().getModelData(),
		        commitment);

		if (commitment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getCommitmentDAO().delete(commitment, "");
			auditDetails.addAll(listDeletion(commitment, "", auditHeader.getAuditTranType()));

		} else {
			commitment.setRoleCode("");
			commitment.setNextRoleCode("");
			commitment.setTaskId("");
			commitment.setNextTaskId("");
			commitment.setWorkflowId(0);

			if (commitment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				commitment.setRecordType("");
				List<Object> returnList = processPosting(commitment, PennantConstants.NEWCMT);
				if(returnList != null && (returnList.get(3) == null)){
					linkTranid = (Long) returnList.get(1);
					if (commitment.isOpenAccount()) {
						commitment.setCmtAccount((String) returnList.get(2));
                    }
				}else{
					String errorMessage = StringUtils.trimToEmpty(returnList.get(3).toString());
			        auditHeader.setErrorDetails(new ErrorDetails(errorMessage.substring(0, errorMessage.indexOf('-')).trim(),
			        		errorMessage.substring(errorMessage.indexOf('-')+1).trim(), null));
			        return auditHeader;
				}
				getCommitmentDAO().save(commitment, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				commitment.setRecordType("");
				List<Object> returnList  = processPosting(commitment, PennantConstants.MNTCMT);
				if(returnList != null && (returnList.get(3) == null)){
					linkTranid = (Long) returnList.get(1);
				}else{
					String errorMessage = StringUtils.trimToEmpty(returnList.get(3).toString());
			        auditHeader.setErrorDetails(new ErrorDetails(errorMessage.substring(0, errorMessage.indexOf('-')).trim(),
			        		errorMessage.substring(errorMessage.indexOf('-')+1).trim(), null));
			        return auditHeader;
				}
				getCommitmentDAO().update(commitment, "");
			}
		}
		
		getCommitmentDAO().delete(commitment, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.getAuditDetail().setModelData(auditHeader.getAuditDetail().getModelData());
		getAuditHeaderDAO().addAudit(auditHeader);

		//Retrieving List of Audit Details For checkList details modules

		if (commitment.getCommitmentMovement() != null) {
			AuditDetail details = commitment.getCommitmentMovement().getLovDescAuditDetailMap()
			        .get("CommitmentMovement");
			details = processingChkListDetailList(details, "", linkTranid);
			auditDetails.add(details);
		}

		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(commitment, "_TEMP",
		        auditHeader.getAuditTranType())));
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(commitment);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCommitmentDAO().delete with parameters commitment,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtCommitments by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Commitment commitment = (Commitment) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCommitmentDAO().delete(commitment, "_TEMP");
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(commitment, "_TEMP",
		        auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) validate the audit detail 2) if any error/Warnings then
	 * assign the to auditHeader 3) identify the nextprocess
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method,
	        boolean onlineRequest) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
		        auditHeader.getUsrLanguage(), method, onlineRequest);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from getCommitmentDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method,
	        boolean onlineRequest) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		Commitment commitment = (Commitment) auditDetail.getModelData();

		Commitment tempCommitment = null;
		if (commitment.isWorkflow()) {
			tempCommitment = getCommitmentDAO().getCommitmentById(commitment.getId(), "_Temp");
		}
		Commitment befCommitment = getCommitmentDAO().getCommitmentById(commitment.getId(), "");
		Commitment oldCommitment = commitment.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = commitment.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_CmtReference") + ":" + valueParm[0];
		String[] errParmFacRef = new String[1];
		String[] valueParmFacRef = new String[1];
		errParmFacRef[0] = PennantJavaUtil.getLabel("label_FacilityRef")+":"+commitment.getFacilityRef()+" For";
		valueParmFacRef[0] = PennantJavaUtil.getLabel("label_custID")+":"+commitment.getCustCIF();

		if (commitment.isNew()) { // for New record or new record into work flow

			if (!commitment.isWorkflow()) {// With out Work flow only new records  
				if (befCommitment != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (commitment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befCommitment != null || tempCommitment != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						        PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
						        usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befCommitment == null || tempCommitment != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						        PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						        usrLanguage));
					}
				}
			}
			Commitment facilityRef = getCommitmentDAO().getCommitmentByFacilityRef(commitment.getId(), "_AView");
			if(facilityRef != null){
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD, "41001", errParmFacRef , valueParmFacRef), usrLanguage));
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!commitment.isWorkflow()) { // With out Work flow for update and delete

				if (befCommitment == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldCommitment != null
					        && !oldCommitment.getLastMntOn().equals(befCommitment.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
						        .equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							        PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
							        usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							        PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
							        usrLanguage));
						}
					}
				}
			} else {

				if (tempCommitment == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldCommitment != null
				        && !oldCommitment.getLastMntOn().equals(tempCommitment.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(),
		        usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !commitment.isWorkflow()) {
			auditDetail.setBefImage(befCommitment);
		}

		return auditDetail;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, AuditDetail> auditDetailMap = new HashMap<String, AuditDetail>();
		Commitment commitmentmovement = (Commitment) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if (method.equals("saveOrUpdate") || method.equals("doApprove")
		        || method.equals("doReject")) {
			if (commitmentmovement.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (commitmentmovement.getCommitmentMovement() != null) {

			auditDetails = setChkListDetailAuditData(commitmentmovement, auditTranType, method);
			for (AuditDetail auditDetail : auditDetails) {
				auditDetailMap.put("CommitmentMovement", auditDetail);
			}
			//auditDetailMap.put("CommitmentMovement", setChkListDetailAuditData(commitmentmovement,auditTranType,method));
			//auditDetails.add(auditDetailMap.get("" + ""));
		}

		commitmentmovement.getCommitmentMovement().setLovDescAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(commitmentmovement);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param educationalLoan
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setChkListDetailAuditData(Commitment commitment,
	        String auditTranType, String method) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CommitmentMovement());

		CommitmentMovement commitmentMovement = commitment.getCommitmentMovement();
		commitmentMovement.setWorkflowId(commitmentMovement.getWorkflowId());

		boolean isRcdType = false;

		if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			commitmentMovement.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			isRcdType = true;
		} else if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
			commitmentMovement.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			isRcdType = true;
		} else if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
			commitmentMovement.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			isRcdType = true;
		}

		if (method.equals("saveOrUpdate") && (isRcdType == true)) {
			commitmentMovement.setNewRecord(true);
		}

		if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
			if (commitmentMovement.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_NEW)) {
				auditTranType = PennantConstants.TRAN_ADD;
			} else if (commitmentMovement.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_DEL)
			        || commitmentMovement.getRecordType().equalsIgnoreCase(
			                PennantConstants.RECORD_TYPE_CAN)) {
				auditTranType = PennantConstants.TRAN_DEL;
			} else {
				auditTranType = PennantConstants.TRAN_UPD;
			}
		}

		commitmentMovement.setRecordStatus(commitmentMovement.getRecordStatus());
		commitmentMovement.setUserDetails(commitmentMovement.getUserDetails());
		commitmentMovement.setLastMntOn(commitmentMovement.getLastMntOn());
		commitmentMovement.setLastMntBy(commitmentMovement.getLastMntBy());
		if (!commitmentMovement.getRecordType().equals("")) {
			auditDetails.add(new AuditDetail(auditTranType, +1, fields[0], fields[1],
			        "CommitmentMovement", commitmentMovement));
		}

		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Educational expenses
	 * 
	 * @param details
	 * @param type
	 * @param custId
	 * @return
	 */
	private AuditDetail processingChkListDetailList(AuditDetail details, String type,
	        long linkTranid) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		CommitmentMovement commitmentMovement = (CommitmentMovement) details.getModelData();
		saveRecord = false;
		updateRecord = false;
		deleteRecord = false;
		approveRec = false;
		String rcdType = "";
		String recordStatus = "";
		if (type.equals("")) {
			approveRec = true;
			commitmentMovement.setVersion(commitmentMovement.getVersion() + 1);
			commitmentMovement.setMovementOrder(commitmentMovement.getMovementOrder() + 1);
			commitmentMovement.setRoleCode("");
			commitmentMovement.setNextRoleCode("");
			commitmentMovement.setTaskId("");
			commitmentMovement.setNextTaskId("");
		}
		if (linkTranid != 0 && linkTranid != Long.MIN_VALUE) {
			commitmentMovement.setLinkedTranId(linkTranid);
		}

		commitmentMovement.setWorkflowId(0);

		if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
			deleteRecord = true;
		} else if (commitmentMovement.isNewRecord()) {
			saveRecord = true;
			if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				commitmentMovement.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else if (commitmentMovement.getRecordType()
			        .equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				commitmentMovement.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			} else if (commitmentMovement.getRecordType()
			        .equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				commitmentMovement.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			}

		} else if (commitmentMovement.getRecordType().equalsIgnoreCase(
		        PennantConstants.RECORD_TYPE_NEW)) {
			if (approveRec) {
				saveRecord = true;
			} else {
				updateRecord = true;
			}
		} else if (commitmentMovement.getRecordType().equalsIgnoreCase(
		        PennantConstants.RECORD_TYPE_UPD)) {
			updateRecord = true;
		} else if (commitmentMovement.getRecordType().equalsIgnoreCase(
		        PennantConstants.RECORD_TYPE_DEL)) {
			if (approveRec) {
				deleteRecord = true;
			} else if (commitmentMovement.isNew()) {
				saveRecord = true;
			} else {
				updateRecord = true;
			}
		}

		if (approveRec) {
			rcdType = commitmentMovement.getRecordType();
			recordStatus = commitmentMovement.getRecordStatus();
			commitmentMovement.setRecordType("");
			commitmentMovement.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		}
		if (saveRecord) {

			getCommitmentMovementDAO().save(commitmentMovement, type);
		}

		if (updateRecord) {
			getCommitmentMovementDAO().update(commitmentMovement, type);
		}

		if (deleteRecord) {
			getCommitmentMovementDAO().delete(commitmentMovement, type);
		}

		if (approveRec) {
			commitmentMovement.setRecordType(rcdType);
			commitmentMovement.setRecordStatus(recordStatus);
		}
		details.setModelData(commitmentMovement);
		logger.debug("Leaving ");
		return details;
	}

	/**
	 * Method deletion of CommitmentMovement list with existing fee type
	 * 
	 * @param fee
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(Commitment commitment, String tableType,
	        String auditTranType) {
		logger.debug("Entering ");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (commitment.getCommitmentMovement() != null) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CommitmentMovement());
			CommitmentMovement commMovement = commitment.getCommitmentMovement();
			if (!commMovement.getRecordType().equals("") || tableType.equals("")) {
				auditList.add(new AuditDetail(auditTranType, +1, fields[0], fields[1], commMovement
				        .getBefImage(), commMovement));
			}
			CommitmentMovement commitmentMovement = commitment.getCommitmentMovement();
			getCommitmentMovementDAO().delete(commitmentMovement, tableType);
		}

		logger.debug("Leaving ");
		return auditList;
	}

	/**
	 * Common Method for Commitment list validation
	 * 
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null & list.size() > 0) {

			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				CommitmentMovement commMovement = (CommitmentMovement) ((AuditDetail) list.get(i))
				        .getModelData();
				rcdType = commMovement.getRecordType();

				if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
				        || rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (!(transType.equals(""))) {
					//check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i))
					        .getAuditSeq(), commMovement.getBefImage(), commMovement));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

	@Override
	public List<Rule> getRuleByModuleAndEvent(String module, String event) {
		return getRuleDAO().getRuleByModuleAndEvent(module, event, "");
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	@Override
	public Map<String, Object> getAmountSummary(long custID) {
		return getCommitmentDAO().getAmountSummary(custID);
	}
	@Override
	public List<CommitmentSummary> getCommitmentSummary(long custID) {
		return getCommitmentDAO().getCommitmentSummary(custID);
	}

	private List<Object> processPosting(Commitment commitment, String event) {

		List<Object> returnResultList = null;
		try {
			
			//Preparation for Commitment Postings
			Date dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());
			if (PennantConstants.MNTCMT.equals(event)) {
				Commitment prvCommitment =  getCommitmentDAO().getCommitmentById(commitment.getId(), "");
				BigDecimal diffAmount=commitment.getCmtAmount().subtract(prvCommitment.getCmtAmount());
				Commitment tempCommitment=new Commitment();
				BeanUtils.copyProperties(commitment, tempCommitment);
				tempCommitment.setCmtAmount(diffAmount);
				returnResultList =getPostingsPreparationUtil().processCmtPostingDetails(tempCommitment, "Y", dateAppDate, event);
            }else{
            	returnResultList = getPostingsPreparationUtil().processCmtPostingDetails(commitment, "Y", dateAppDate, event);
            }
			
		} catch (AccountNotFoundException e) {
			logger.debug(e);
			returnResultList =new ArrayList<Object>();
			returnResultList.add(false);
			returnResultList.add(0);
			returnResultList.add(e.getErrorMsg());
		} catch (IllegalAccessException e) {
			logger.debug(e);
		} catch (InvocationTargetException e) {
			logger.debug(e);
		}
		return returnResultList;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	@Override
    public List<AvailCommitment> getCommitmentListByCustId(long custId) {
	    return getCommitmentDAO().getCommitmentListByCustId(custId, "_AView");
    }

}