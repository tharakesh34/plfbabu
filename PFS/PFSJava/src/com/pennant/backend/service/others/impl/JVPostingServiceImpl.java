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
 * * FileName : JVPostingServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2013 * * Modified
 * Date : 21-06-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 21-06-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.service.others.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.others.JVPostingDAO;
import com.pennant.backend.dao.others.JVPostingEntryDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>JVPosting</b>.<br>
 * 
 */
public class JVPostingServiceImpl extends GenericService<JVPosting> implements JVPostingService {
	private final static Logger logger = Logger.getLogger(JVPostingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private JVPostingDAO jVPostingDAO;

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
	 * @return the jVPostingDAO
	 */
	public JVPostingDAO getJVPostingDAO() {
		return jVPostingDAO;
	}

	/**
	 * @param jVPostingDAO
	 *            the jVPostingDAO to set
	 */
	public void setJVPostingDAO(JVPostingDAO jVPostingDAO) {
		this.jVPostingDAO = jVPostingDAO;
	}

	/**
	 * @return the jVPosting
	 */
	@Override
	public JVPosting getJVPosting() {
		return getJVPostingDAO().getJVPosting();
	}

	/**
	 * @return the jVPosting for New Record
	 */
	@Override
	public JVPosting getNewJVPosting() {
		return getJVPostingDAO().getNewJVPosting();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table JVPostings/JVPostings_Temp by
	 * using JVPostingDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using JVPostingDAO's update method 3) Audit the record in to AuditHeader and AdtJVPostings by using
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
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table JVPostings/JVPostings_Temp by
	 * using JVPostingDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using JVPostingDAO's update method 3) Audit the record in to AuditHeader and AdtJVPostings by using
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
		JVPosting jVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();

		if (jVPosting.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (jVPosting.isNew()) {
			getJVPostingDAO().save(jVPosting, tableType);
		} else {
			getJVPostingDAO().update(jVPosting, tableType);
		}
		
		if (jVPosting.getJVPostingEntrysList() != null && jVPosting.getJVPostingEntrysList().size() > 0) {
			List<AuditDetail> details = jVPosting.getAuditDetailMap().get("JVPostingEntry");
			details = processJVPostingEntry(details, tableType);
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * JVPostings by using JVPostingDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtJVPostings by using auditHeaderDAO.addAudit(auditHeader)
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

		JVPosting jVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();
		getJVPostingDAO().delete(jVPosting, "");
		
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(jVPosting, "", auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getJVPostingById fetch the details by using JVPostingDAO's getJVPostingById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return JVPosting
	 */

	@Override
	public JVPosting getJVPostingById(String id) {
		JVPosting jvPosting=getJVPostingDAO().getJVPostingById(id, "_View");
		if (jvPosting!=null) {
			jvPosting.setJVPostingEntrysList(getJVPostingEntryDAO().getJVPostingEntryListById(jvPosting.getBatchReference(), "_View"));
        }
		return jvPosting;
	}

	/**
	 * getApprovedJVPostingById fetch the details by using JVPostingDAO's getJVPostingById method . with parameter id
	 * and type as blank. it fetches the approved records from the JVPostings.
	 * 
	 * @param id
	 *            (String)
	 * @return JVPosting
	 */

	public JVPosting getApprovedJVPostingById(String id) {
		JVPosting jvPosting=getJVPostingDAO().getJVPostingById(id, "_AView");
		if (jvPosting!=null) {
			jvPosting.setJVPostingEntrysList(getJVPostingEntryDAO().getJVPostingEntryListById(jvPosting.getBatchReference(), "_AView"));
        }
		return jvPosting;
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param JVPosting
	 *            (jVPosting)
	 * @return jVPosting
	 */
	@Override
	public JVPosting refresh(JVPosting jVPosting) {
		logger.debug("Entering");
		getJVPostingDAO().refresh(jVPosting);
		getJVPostingDAO().initialize(jVPosting);
		logger.debug("Leaving");
		return jVPosting;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getJVPostingDAO().delete with
	 * parameters jVPosting,"" b) NEW Add new record in to main table by using getJVPostingDAO().save with parameters
	 * jVPosting,"" c) EDIT Update record in the main table by using getJVPostingDAO().update with parameters
	 * jVPosting,"" 3) Delete the record from the workFlow table by using getJVPostingDAO().delete with parameters
	 * jVPosting,"_Temp" 4) Audit the record in to AuditHeader and AdtJVPostings by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtJVPostings by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		JVPosting jVPosting = new JVPosting();
		BeanUtils .copyProperties((JVPosting) auditHeader.getAuditDetail().getModelData(), jVPosting);

		if (jVPosting.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(jVPosting, "", auditHeader.getAuditTranType()));
			getJVPostingDAO().delete(jVPosting, "");

		} else {
			jVPosting.setRoleCode("");
			jVPosting.setNextRoleCode("");
			jVPosting.setTaskId("");
			jVPosting.setNextTaskId("");
			jVPosting.setWorkflowId(0);

			if (jVPosting.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				jVPosting.setRecordType("");
				getJVPostingDAO().save(jVPosting, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				jVPosting.setRecordType("");
				getJVPostingDAO().update(jVPosting, "");
			}
			

			if (jVPosting.getJVPostingEntrysList() != null && jVPosting.getJVPostingEntrysList().size() > 0) {
				List<AuditDetail> details = jVPosting.getAuditDetailMap().get("JVPostingEntry");
				details = processJVPostingEntry(details, "");
				auditDetails.addAll(details);
			}
		}

		getJVPostingDAO().delete(jVPosting, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(jVPosting, "_TEMP", auditHeader.getAuditTranType())));
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, jVPosting.getBefImage(), jVPosting));
		getAuditHeaderDAO().addAudit(auditHeader);
		
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(jVPosting);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, jVPosting.getBefImage(), jVPosting));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getJVPostingDAO().delete with parameters jVPosting,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtJVPostings by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		JVPosting jVPosting = (JVPosting) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getJVPostingDAO().delete(jVPosting, "_TEMP");

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, jVPosting.getBefImage(), jVPosting));
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(jVPosting, "_TEMP", auditHeader.getAuditTranType())));
		
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
	 * mismatch conditions Fetch the error details from getJVPostingDAO().getErrorDetail with Error ID and language as
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
		JVPosting jVPosting = (JVPosting) auditDetail.getModelData();

		JVPosting tempJVPosting = null;
		if (jVPosting.isWorkflow()) {
			tempJVPosting = getJVPostingDAO().getJVPostingById(jVPosting.getId(), "_Temp");
		}
		JVPosting befJVPosting = getJVPostingDAO().getJVPostingById(jVPosting.getId(), "");

		JVPosting old_JVPosting = jVPosting.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = jVPosting.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_BatchReference") + ":" + valueParm[0];

		if (jVPosting.isNew()) { // for New record or new record into work flow

			if (!jVPosting.isWorkflow()) {// With out Work flow only new records  
				if (befJVPosting != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (jVPosting.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befJVPosting != null || tempJVPosting != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						        PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
						        usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befJVPosting == null || tempJVPosting != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						        PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						        usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!jVPosting.isWorkflow()) { // With out Work flow for update and delete

				if (befJVPosting == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (old_JVPosting != null
					        && !old_JVPosting.getLastMntOn().equals(befJVPosting.getLastMntOn())) {
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

				if (tempJVPosting == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (old_JVPosting != null
				        && !old_JVPosting.getLastMntOn().equals(tempJVPosting.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(),
		        usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !jVPosting.isWorkflow()) {
			auditDetail.setBefImage(befJVPosting);
		}

		return auditDetail;
	}

	//===================
	private JVPostingEntryDAO jVPostingEntryDAO;

	@Override
	public JVPostingEntry getNewJVPostingEntry() {
		return getJVPostingEntryDAO().getNewJVPostingEntry();
	}

	public void setJVPostingEntryDAO(JVPostingEntryDAO jVPostingEntryDAO) {
		this.jVPostingEntryDAO = jVPostingEntryDAO;
	}

	public JVPostingEntryDAO getJVPostingEntryDAO() {
		return jVPostingEntryDAO;
	}
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		JVPosting accountingSet = (JVPosting) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if (method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject")) {
			if (accountingSet.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (accountingSet.getJVPostingEntrysList() != null && accountingSet.getJVPostingEntrysList().size() > 0) {
			auditDetailMap.put("JVPostingEntry", setJVPostingEntryAuditData(accountingSet, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("JVPostingEntry"));
		}

		accountingSet.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(accountingSet);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}
	
	
	
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setJVPostingEntryAuditData(JVPosting jVPosting, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new JVPostingEntry(),new JVPostingEntry().getExcludeFields());

		for (int i = 0; i < jVPosting.getJVPostingEntrysList().size(); i++) {

			JVPostingEntry jVPostingEntry = jVPosting.getJVPostingEntrysList().get(i);
			jVPostingEntry.setWorkflowId(jVPosting.getWorkflowId());

			boolean isRcdType = false;

			if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				jVPostingEntry.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			jVPostingEntry.setRecordStatus(jVPosting.getRecordStatus());
			jVPostingEntry.setUserDetails(jVPosting.getUserDetails());
			jVPostingEntry.setLastMntOn(jVPosting.getLastMntOn());

			if (!StringUtils.trimToEmpty(jVPostingEntry.getRecordType()).equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], jVPostingEntry.getBefImage(), jVPostingEntry));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processJVPostingEntry(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			JVPostingEntry jVPostingEntry = (JVPostingEntry) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (type.equals("")) {
				approveRec = true;
				jVPostingEntry.setRoleCode("");
				jVPostingEntry.setNextRoleCode("");
				jVPostingEntry.setTaskId("");
				jVPostingEntry.setNextTaskId("");
			}

			jVPostingEntry.setWorkflowId(0);

			if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (jVPostingEntry.isNewRecord()) {
				saveRecord = true;
				if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (jVPostingEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (jVPostingEntry.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = jVPostingEntry.getRecordType();
				recordStatus = jVPostingEntry.getRecordStatus();
				jVPostingEntry.setRecordType("");
				jVPostingEntry.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				jVPostingEntryDAO.save(jVPostingEntry, type);
			}

			if (updateRecord) {
				jVPostingEntryDAO.update(jVPostingEntry, type);
			}

			if (deleteRecord) {
				jVPostingEntryDAO.delete(jVPostingEntry, type);
			}

			if (approveRec) {
				jVPostingEntry.setRecordType(rcdType);
				jVPostingEntry.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(jVPostingEntry);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * Method deletion of feeTier list with existing fee type
	 * 
	 * @param accountingSet
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(JVPosting accountingSet, String tableType, String auditTranType) {

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (accountingSet.getJVPostingEntrysList() != null && accountingSet.getJVPostingEntrysList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new JVPostingEntry(),new JVPostingEntry().getExcludeFields());
			for (int i = 0; i < accountingSet.getJVPostingEntrysList().size(); i++) {
				JVPostingEntry feeTier = accountingSet.getJVPostingEntrysList().get(i);
				if (!feeTier.getRecordType().equals("") || tableType.equals("")) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], feeTier.getBefImage(), feeTier));
				}
			}
			getJVPostingEntryDAO().deleteByBatchRef(accountingSet.getBatchReference(), tableType);
		}
		return auditList;
	}

	/**
	 * Common Method for Customers list validation
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
				JVPostingEntry jVPostingEntry = (JVPostingEntry) ((AuditDetail) list.get(i)).getModelData();

				rcdType = jVPostingEntry.getRecordType();

				if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) || rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (!(transType.equals(""))) {
					// check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), jVPostingEntry.getBefImage(), jVPostingEntry));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}
}