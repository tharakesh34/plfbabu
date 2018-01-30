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
 * FileName    		:  LimitGroupServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.limit.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.dao.limit.LimitGroupDAO;
import com.pennant.backend.dao.limit.LimitGroupLinesDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.limit.LimitReferenceMappingDAO;
import com.pennant.backend.dao.limit.LimitStructureDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.limit.LimitGroup;
import com.pennant.backend.model.limit.LimitGroupLines;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.limit.LimitGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>LimitGroup</b>.<br>
 */
public class LimitGroupServiceImpl extends GenericService<LimitGroup> implements LimitGroupService {
	private static final Logger			logger	= Logger.getLogger(LimitGroupServiceImpl.class);

	private AuditHeaderDAO				auditHeaderDAO;
	private Set<String>					excludeFields;
	private LimitGroupDAO				limitGroupDAO;
	private LimitGroupLinesDAO			limitGroupLinesDAO;
	private LimitStructureDetailDAO		limitStructureDetailDAO;
	private LimitDetailDAO				limitDetailDAO;
	private LimitHeaderDAO				limitHeaderDAO;
	private LimitReferenceMappingDAO	limitReferenceMappingDAO;

	public LimitGroupServiceImpl() {
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
	 * @return the limitGroupDAO
	 */
	public LimitGroupDAO getLimitGroupDAO() {
		return limitGroupDAO;
	}

	/**
	 * @param limitGroupDAO
	 *            the limitGroupDAO to set
	 */
	public void setLimitGroupDAO(LimitGroupDAO limitGroupDAO) {
		this.limitGroupDAO = limitGroupDAO;
	}

	/**
	 * @return the limitGroup
	 */
	@Override
	public LimitGroup getLimitGroup() {
		return getLimitGroupDAO().getLimitGroup();
	}

	/**
	 * @return the limitGroup for New Record
	 */
	@Override
	public LimitGroup getNewLimitGroup() {
		return getLimitGroupDAO().getNewLimitGroup();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table LIMIT_GROUP/LIMIT_GROUP_Temp by
	 * using LimitGroupDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using LimitGroupDAO's update method 3) Audit the record in to AuditHeader and AdtLIMIT_GROUP by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
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
		LimitGroup limitGroup = (LimitGroup) auditHeader.getAuditDetail().getModelData();

		if (limitGroup.isWorkflow()) {
			tableType = "_Temp";
		}

		if (limitGroup.isNew()) {
			getLimitGroupDAO().save(limitGroup, tableType);
		} else {
			getLimitGroupDAO().update(limitGroup, tableType);
		}

		//Retrieving List of Audit Details For libraryArtefact  related modules
		if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
			auditHeader.setAuditDetails(processingLimitGroupList(auditHeader.getAuditDetails(), tableType, limitGroup));
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * LIMIT_GROUP by using LimitGroupDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtLIMIT_GROUP by using auditHeaderDAO.addAudit(auditHeader)
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

		LimitGroup limitGroup = (LimitGroup) auditHeader.getAuditDetail().getModelData();
		getLimitGroupDAO().delete(limitGroup, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getLimitGroupById fetch the details by using LimitGroupDAO's getLimitGroupById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return LimitGroup
	 */

	@Override
	public LimitGroup getLimitGroupById(String id) {
		logger.debug("Entering");
		LimitGroup limitGroup = getLimitGroupDAO().getLimitGroupById(id, "_View");
		if (limitGroup != null) {
			limitGroup.setLimitGroupLinesList(getLimitGroupLinesDAO().getLimitGroupLinesById(id, "_View"));
		}
		logger.debug("Leaving");
		return limitGroup;
	}

	/**
	 * getApprovedLimitGroupById fetch the details by using LimitGroupDAO's getLimitGroupById method . with parameter id
	 * and type as blank. it fetches the approved records from the LIMIT_GROUP.
	 * 
	 * @param id
	 *            (String)
	 * @return LimitGroup
	 */

	public LimitGroup getApprovedLimitGroupById(String id) {
		logger.debug("Entering");
		LimitGroup limitGroup = getLimitGroupDAO().getLimitGroupById(id, "_AView");
		if (limitGroup != null) {
			limitGroup.setLimitGroupLinesList(getLimitGroupLinesDAO().getLimitGroupLinesById(id, "_AView"));
		}
		logger.debug("Leaving");
		return limitGroup;
	}

	/**
	 * 
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getLimitGroupDAO().delete with
	 * parameters limitGroup,"" b) NEW Add new record in to main table by using getLimitGroupDAO().save with parameters
	 * limitGroup,"" c) EDIT Update record in the main table by using getLimitGroupDAO().update with parameters
	 * limitGroup,"" 3) Delete the record from the workFlow table by using getLimitGroupDAO().delete with parameters
	 * limitGroup,"_Temp" 4) Audit the record in to AuditHeader and AdtLIMIT_GROUP by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtLIMIT_GROUP by
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

		List<AuditDetail> auditDetails = auditHeader.getAuditDetails();
		LimitGroup limitGroup = new LimitGroup();
		BeanUtils.copyProperties((LimitGroup) auditHeader.getAuditDetail().getModelData(), limitGroup);

		if (limitGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			if (limitGroup.getLimitGroupLinesList() != null && !limitGroup.getLimitGroupLinesList().isEmpty()) {
				getLimitGroupLinesDAO().delete(limitGroup.getId(), "");
			}
			getLimitGroupDAO().delete(limitGroup, "");

		} else {
			limitGroup.setRoleCode("");
			limitGroup.setNextRoleCode("");
			limitGroup.setTaskId("");
			limitGroup.setNextTaskId("");
			limitGroup.setWorkflowId(0);

			if (limitGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				limitGroup.setRecordType("");
				getLimitGroupDAO().save(limitGroup, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				limitGroup.setRecordType("");
				getLimitGroupDAO().update(limitGroup, "");
			}

			//Retrieving List of Audit Details For LimitGroup  related modules
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				auditDetails = processingLimitGroupList(auditHeader.getAuditDetails(), "", limitGroup);
			}
		}

		getLimitGroupLinesDAO().delete(limitGroup.getId(), "_Temp");
		getLimitGroupDAO().delete(limitGroup, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetails);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(limitGroup);

		auditHeader = resetAuditDetails(auditHeader, limitGroup, tranType);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getLimitGroupDAO().delete with parameters limitGroup,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtLIMIT_GROUP by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		LimitGroup limitGroup = (LimitGroup) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getLimitGroupLinesDAO().delete(limitGroup.getId(), "_Temp");
		getLimitGroupDAO().delete(limitGroup, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) validate the audit detail 2) if any error/Warnings then
	 * assign the to auditHeader 3) identify the next process
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		//auditDetail=validateLimitGroups(auditDetail,auditHeader.getUsrLanguage());
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		LimitGroup aLimitGroup = (LimitGroup) auditHeader.getAuditDetail().getModelData();
		excludeFields = aLimitGroup.getExcludeFields();

		if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
			for (AuditDetail detail : auditHeader.getAuditDetails()) {
				auditHeader.setErrorList(detail.getErrorDetails());
			}
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from getLimitGroupDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		LimitGroup limitGroup = (LimitGroup) auditDetail.getModelData();

		LimitGroup tempLimitGroup = null;
		if (limitGroup.isWorkflow()) {
			tempLimitGroup = getLimitGroupDAO().getLimitGroupById(limitGroup.getId(), "_Temp");
		}
		LimitGroup befLimitGroup = getLimitGroupDAO().getLimitGroupById(limitGroup.getId(), "");

		LimitGroup oldLimitGroup = limitGroup.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = limitGroup.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_GroupCode") + ":" + valueParm[0];

		if (limitGroup.isNew()) { // for New record or new record into work flow

			if (!limitGroup.isWorkflow()) {// With out Work flow only new records  
				if (befLimitGroup != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (limitGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befLimitGroup != null || tempLimitGroup != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befLimitGroup == null || tempLimitGroup != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!limitGroup.isWorkflow()) { // With out Work flow for update and delete

				if (befLimitGroup == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldLimitGroup != null && !oldLimitGroup.getLastMntOn().equals(befLimitGroup.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempLimitGroup == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}

				if (tempLimitGroup != null && oldLimitGroup != null
						&& !oldLimitGroup.getLastMntOn().equals(tempLimitGroup.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !limitGroup.isWorkflow()) {
			auditDetail.setBefImage(befLimitGroup);
		}
		logger.debug("Leaving");
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

		LimitGroup limitGroup = (LimitGroup) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (limitGroup.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		if (limitGroup.getLimitGroupLinesList() != null && limitGroup.getLimitGroupLinesList().size() > 0) {
			auditHeader.setAuditDetails(setLimitGroupLinesAuditData(limitGroup, auditTranType, method));
		}
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param APIChannel
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setLimitGroupLinesAuditData(LimitGroup limitGroup, String auditTranType, String method) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		boolean delete = false;

		if ((PennantConstants.RECORD_TYPE_DEL.equals(limitGroup.getRecordType()) && "doApprove"
				.equalsIgnoreCase(method)) || "delete".equals(method)) {
			delete = true;
		}
		if (limitGroup.getLimitGroupLinesList() != null)
			for (int i = 0; i < limitGroup.getLimitGroupLinesList().size(); i++) {
				LimitGroupLines limitGroupItems = limitGroup.getLimitGroupLinesList().get(i);
				limitGroupItems.setWorkflowId(limitGroup.getWorkflowId());
				excludeFields = limitGroupItems.getExcludeFields();
				String[] fields = PennantJavaUtil.getFieldDetails(new LimitGroupLines(), excludeFields);

				if (StringUtils.isEmpty(limitGroupItems.getRecordType())) {
					continue;
				}

				boolean isRcdType = false;

				if (delete) {
					limitGroupItems.setRecordType(PennantConstants.RECORD_TYPE_MDEL);
				} else {
					if (limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						limitGroupItems.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						isRcdType = true;
					} else if (limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						limitGroupItems.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						isRcdType = true;
					} else if (limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						limitGroupItems.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						isRcdType = true;
					}
				}
				if ("saveOrUpdate".equals(method) && (isRcdType && limitGroupItems.isWorkflow())) {
					//limitGroupItems.setNewRecord(true);
				}

				if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
					if (limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						auditTranType = PennantConstants.TRAN_ADD;
					} else if (limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						auditTranType = PennantConstants.TRAN_DEL;
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
					}
				}
				limitGroupItems.setRecordStatus(limitGroup.getRecordStatus());
				limitGroupItems.setUserDetails(limitGroup.getUserDetails());
				limitGroupItems.setLastMntOn(limitGroup.getLastMntOn());
				limitGroupItems.setLastMntBy(limitGroup.getLastMntBy());
				if (StringUtils.isNotEmpty(limitGroupItems.getRecordType())) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], limitGroupItems
							.getBefImage(), limitGroupItems));
				}
			}
		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for utilityDetail
	 * 
	 * @param auditDetails
	 * @param type
	 * @param beneficiaryId
	 * @return
	 */
	private List<AuditDetail> processingLimitGroupList(List<AuditDetail> auditDetails, String type,
			LimitGroup limitGroup) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (AuditDetail auditDetail : auditDetails) {
			LimitGroupLines limitGroupItems = (LimitGroupLines) auditDetail.getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			limitGroupItems.setLimitGroupCode(limitGroup.getId());
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
			}

			if (limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (limitGroupItems.isNewRecord()) {
				saveRecord = true;
				if (limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					limitGroupItems.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					limitGroupItems.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					limitGroupItems.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (limitGroupItems.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (limitGroupItems.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (StringUtils.isEmpty(type)) {
				limitGroupItems.setRoleCode("");
				limitGroupItems.setNextRoleCode("");
				limitGroupItems.setTaskId("");
				limitGroupItems.setNextTaskId("");
				limitGroupItems.setRecordType("");
				limitGroupItems.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				limitGroupItems.setWorkflowId(0);

			} else {
				limitGroupItems.setRoleCode(limitGroup.getRoleCode());
				limitGroupItems.setNextRoleCode(limitGroup.getNextRoleCode());
				limitGroupItems.setTaskId(limitGroup.getTaskId());
				limitGroupItems.setNextTaskId(limitGroup.getNextTaskId());
			}
			if (approveRec) {
				rcdType = limitGroupItems.getRecordType();
				recordStatus = limitGroupItems.getRecordStatus();
				limitGroupItems.setRecordType("");
				limitGroupItems.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getLimitGroupLinesDAO().save(limitGroupItems, type);

			}
			if (updateRecord) {
				getLimitGroupLinesDAO().update(limitGroupItems, type);
			}
			if (deleteRecord) {
				getLimitGroupLinesDAO().deleteLimitGroupLines(limitGroupItems, type);
			}
			if (approveRec) {

				if (saveRecord) {
					processLineOrGroup(limitGroupItems, limitGroup.getLimitCategory(), false);
				}

				if (deleteRecord) {
					processLineOrGroup(limitGroupItems, limitGroup.getLimitCategory(), true);
				}
				limitGroupItems.setRecordType(rcdType);
				limitGroupItems.setRecordStatus(recordStatus);
			}
			auditDetail.setModelData(limitGroupItems);
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param ChannelDetails
	 * @param tranType
	 * @return
	 */
	private AuditHeader resetAuditDetails(AuditHeader auditHeader, LimitGroup aLimitGroup, String tranType) {
		logger.debug("Entering :");
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(aLimitGroup);

		if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
			List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

			for (AuditDetail detail : auditHeader.getAuditDetails()) {
				LimitGroupLines limitGroupItems = (LimitGroupLines) detail.getModelData();
				detail.setAuditTranType(tranType);
				limitGroupItems.setRecordType("");
				limitGroupItems.setRoleCode("");
				limitGroupItems.setNextRoleCode("");
				limitGroupItems.setTaskId("");
				limitGroupItems.setNextTaskId("");
				limitGroupItems.setWorkflowId(0);
				detail.setModelData(limitGroupItems);
				auditDetails.add(detail);
			}
			auditHeader.setAuditDetails(auditDetails);
		}
		logger.debug("Leaving :");
		return auditHeader;
	}

	@Override
	public boolean validationCheck(String lmtGrp) {
		logger.debug("Entering :");
		int count = getLimitGroupLinesDAO().validationCheck(lmtGrp, "_View");
		if (count == 0) {
			count = getLimitStructureDetailDAO().validationCheck(lmtGrp, "_View");
			if (count == 0) {
				count = getLimitDetailDAO().validationCheck(lmtGrp, "_View");
				if (count == 0) {
					logger.debug("Leaving");
					return true;
				} else {
					logger.debug("Leaving");
					return false;
				}
			} else {
				logger.debug("Leaving");
				return false;
			}
		} else {
			logger.debug("Leaving");
			return false;
		}
	}

	@Override
	public boolean isLineUsingInUtilization(String lmtline) {
		logger.debug("Entering :");
		int count = 0;
		if (lmtline != null)
			count = getLimitReferenceMappingDAO().isLimitLineExist(lmtline);
		if (count == 0) {
			logger.debug("Leaving");
			return true;
		} else {
			logger.debug("Leaving");
			return false;
		}
	}

	@Override
	public boolean limitLineActiveCheck(String ruleCode, String ruleEvent) {
		logger.debug("Entering");
		int count = limitItemCheck(ruleCode, ruleEvent, "_View");
		if (count == 0) {
			count = getLimitStructureDetailDAO().limitItemCheck(ruleCode, ruleEvent, "_View");
			if (count == 0) {
				count = getLimitDetailDAO().limitItemCheck(ruleCode, ruleEvent, "_View");
				if (count == 0) {
					logger.debug("Leaving");
					return true;
				} else {
					logger.debug("Leaving");
					return false;
				}
			} else {
				logger.debug("Leaving");
				return false;
			}
		} else {
			logger.debug("Leaving");
			return false;
		}
	}

	@Override
	public int limitItemCheck(String lmtItem, String limitCategory, String type) {
		return getLimitGroupLinesDAO().limitLineCheck(lmtItem, limitCategory, type);
	}

	@Override
	public String getLimitLines(String groupCode) {
		logger.debug("Entering :");
		String itemCodes = null;
		List<LimitGroupLines> lmtGrpItems = getLimitGroupLinesDAO().getLimitGroupItemById(groupCode, "");
		if (lmtGrpItems != null) {
			for (LimitGroupLines grpItems : lmtGrpItems) {
				if (itemCodes != null) {
					itemCodes = itemCodes.concat("|" + grpItems.getLimitLines());
				} else {
					itemCodes = grpItems.getLimitLines();
				}
			}
		}
		logger.debug("Leaving");
		return itemCodes;
	}

	public String getGroupcodes(String code, boolean limitLine) {
		return getLimitGroupLinesDAO().getGroupcodes(code, limitLine, "_Aview");
	}

	public List<LimitGroupLines> getGroupCodesByLimitGroup(String code, boolean limitLine) {
		return getLimitGroupLinesDAO().getGroupCodesByLimitGroup(code, limitLine, "_AView");
	}

	@Override
	public List<LimitStructureDetail> getStructuredetailsByLimitGroup(String category, String limitgroup,
			boolean isLine, String type) {
		return getLimitStructureDetailDAO().getStructuredetailsByLimitGroup(category, limitgroup, isLine, type);
	}

	/**
	 * @param ngl
	 * @param category
	 * @param delete
	 */
	private void processLineOrGroup(LimitGroupLines ngl, String category, boolean delete) {
		logger.debug(" Entering ");

		String pg = ngl.getLimitGroupCode();

		//get the sub groups of group code
		List<LimitGroupLines> groupLines = getLimitGroupLinesDAO().getGroupCodesByLimitGroup(pg, false, "");
		for (LimitGroupLines groupLine : groupLines) {
			String lines = groupLine.getLimitLines();
			if (delete) {
				lines.replace("|" + ngl.getLimitLines(), "");
				groupLine.setLimitLines(lines);
			} else {
				StringBuilder builder = new StringBuilder();
				builder.append('|');
				builder.append(ngl.getLimitLines());
				groupLine.setLimitLines(builder.toString());
			}
			groupLine.setLastMntBy(ngl.getLastMntBy());
			groupLine.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			groupLine.setVersion(groupLine.getVersion() + 1);
			getLimitGroupLinesDAO().update(groupLine, "");
		}

		// get the Structure's  which are used by the parent group 
		List<LimitStructureDetail> lmtStrDetList = getStructuredetailsByLimitGroup(category, pg, false, "");

		if (delete) {
			processDelete(ngl, lmtStrDetList);
		}
		// we will continue the process to update the sequences even the records are delete we need to update the sequence.
		processAdd(ngl, lmtStrDetList);

		logger.debug(" Leaving ");
	}

	/**
	 * @param ngl
	 * @param lmtStrDetList
	 */
	private void processAdd(LimitGroupLines ngl, List<LimitStructureDetail> lmtStrDetList) {
		//get all the limit line or groups of the parent group
		List<LimitGroupLines> grouplines = getLimitGroupLinesDAO().getAllLimitLinesByGroup(ngl.getLimitGroupCode(), "");
		//loop through the structures
		for (LimitStructureDetail limitStDet : lmtStrDetList) {

			int level = limitStDet.getItemLevel() + 1;
			int priority = limitStDet.getItemPriority();
			int seq = limitStDet.getItemSeq() + 1;

			boolean isgroup = false;
			boolean rebuild = false;
			for (LimitGroupLines limitGroupLines : grouplines) {

				String value = "";

				if (limitGroupLines.getGroupCode() != null) {
					isgroup = true;
					value = limitGroupLines.getGroupCode();
				} else {
					isgroup = false;
					value = limitGroupLines.getLimitLine();
				}

				//check line the record in the group is there or not if found update sequence else add new
				LimitStructureDetail lstd = getLimitStructureDetailDAO().getStructureByLine(
						limitStDet.getLimitStructureCode(), value, isgroup);
				if (lstd != null) {
					lstd.setItemLevel(level);
					lstd.setItemSeq(seq);
					getLimitStructureDetailDAO().updateById(lstd, "");

				} else {
					LimitStructureDetail newStrDet = new LimitStructureDetail();
					newStrDet.setId(Long.MIN_VALUE);
					newStrDet.setLimitCategory(limitStDet.getLimitCategory());
					newStrDet.setLimitStructureCode(limitStDet.getLimitStructureCode());
					newStrDet.setLimitLine(limitGroupLines.getLimitLine());
					newStrDet.setGroupCode(limitGroupLines.getGroupCode());
					newStrDet.setItemSeq(seq);
					newStrDet.setItemPriority(priority);
					newStrDet.setItemLevel(level);
					newStrDet.setLastMntBy(ngl.getLastMntBy());
					newStrDet.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					newStrDet.setRoleCode("");
					newStrDet.setNextRoleCode("");
					newStrDet.setTaskId("");
					newStrDet.setNextTaskId("");
					newStrDet.setRecordType("");
					newStrDet.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					newStrDet.setWorkflowId(0);
					getLimitStructureDetailDAO().save(newStrDet, "");
					rebuild = true;
				}
				seq = seq + 1;
				if (isgroup) {
					level = level + 1;
				} else {
					level = limitStDet.getItemLevel() + 1;
				}
			}

			if (rebuild) {
				markRebuildOnSetUp(limitStDet.getLimitStructureCode());
			}
		}
	}

	/**
	 * @param ngl
	 * @param lmtStrDetList
	 */
	private void processDelete(LimitGroupLines ngl, List<LimitStructureDetail> lmtStrDetList) {

		//check is it group or line should be delete. get all the lines needs be deleted
		List<LimitGroupLines> list = new ArrayList<LimitGroupLines>();
		list.add(ngl);
		if (ngl.getGroupCode() != null) {
			list.addAll(getLimitGroupLinesDAO().getAllLimitLinesByGroup(ngl.getGroupCode(), ""));
		}

		for (LimitStructureDetail lsdd : lmtStrDetList) {

			//loop through the delete list
			for (LimitGroupLines limitGroupLines : list) {

				boolean isgroup = false;
				String value = "";

				if (limitGroupLines.getGroupCode() != null) {
					isgroup = true;
					value = limitGroupLines.getGroupCode();
				} else {
					isgroup = false;
					value = limitGroupLines.getLimitLine();
				}

				//get all the Structure whose using this line or group 
				LimitStructureDetail deleteRecord = getLimitStructureDetailDAO().getStructureByLine(
						lsdd.getLimitStructureCode(), value, isgroup);
				// remove from the maintains as well if it is maintained
				long sdid = deleteRecord.getLimitStructureDetailsID();
				getLimitStructureDetailDAO().deleteBySrtructureId(sdid, "_Temp");
				getLimitStructureDetailDAO().deleteBySrtructureId(sdid, "");
				getLimitDetailDAO().deletebyLimitStructureDetailId(sdid, "_Temp");
				getLimitDetailDAO().deletebyLimitStructureDetailId(sdid, "");
			}
			markRebuildOnSetUp(lsdd.getLimitStructureCode());
		}

	}

	/**
	 * @param limitStrCode
	 */
	public void markRebuildOnSetUp(String limitStrCode) {
		List<LimitHeader> header = getLimitHeaderDAO().getLimitHeaderByStructureCode(limitStrCode, "");
		for (LimitHeader limitHeader : header) {
			getLimitHeaderDAO().updateRebuild(limitHeader.getHeaderId(), true, "");
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public LimitGroupLinesDAO getLimitGroupLinesDAO() {
		return limitGroupLinesDAO;
	}

	public void setLimitGroupLinesDAO(LimitGroupLinesDAO limitGroupItemsDAO) {
		this.limitGroupLinesDAO = limitGroupItemsDAO;
	}

	public LimitStructureDetailDAO getLimitStructureDetailDAO() {
		return limitStructureDetailDAO;
	}

	public void setLimitStructureDetailDAO(LimitStructureDetailDAO limitStructureDetailDAO) {
		this.limitStructureDetailDAO = limitStructureDetailDAO;
	}

	public LimitDetailDAO getLimitDetailDAO() {
		return limitDetailDAO;
	}

	public void setLimitDetailDAO(LimitDetailDAO limitDetailDAO) {
		this.limitDetailDAO = limitDetailDAO;
	}

	public LimitReferenceMappingDAO getLimitReferenceMappingDAO() {
		return limitReferenceMappingDAO;
	}

	public void setLimitReferenceMappingDAO(LimitReferenceMappingDAO limitReferenceMappingDAO) {
		this.limitReferenceMappingDAO = limitReferenceMappingDAO;
	}

	public LimitHeaderDAO getLimitHeaderDAO() {
		return limitHeaderDAO;
	}

	public void setLimitHeaderDAO(LimitHeaderDAO limitHeaderDAO) {
		this.limitHeaderDAO = limitHeaderDAO;
	}

}