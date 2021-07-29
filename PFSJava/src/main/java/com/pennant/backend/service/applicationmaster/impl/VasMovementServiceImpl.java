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
 * FileName    		:  VasMovementServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.VasMovementDAO;
import com.pennant.backend.dao.applicationmaster.VasMovementDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.VasMovement;
import com.pennant.backend.model.finance.VasMovementDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.VasMovementService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>VasMovement</b>.<br>
 * 
 */
public class VasMovementServiceImpl extends GenericService<VasMovement> implements VasMovementService {
	private static final Logger logger = LogManager.getLogger(VasMovementServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private VasMovementDAO vasMovementDAO;
	private VasMovementDetailDAO vasMovementDetailDAO;

	public VasMovementServiceImpl() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * BMTVasMovement/BMTVasMovement_Temp by using VasMovementDAO's save method b) Update the Record in the table. based
	 * on the module workFlow Configuration. by using VasMovementDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtBMTVasMovement by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		VasMovement vasMovement = (VasMovement) auditHeader.getAuditDetail().getModelData();

		if (vasMovement.isWorkflow()) {
			tableType = "_Temp";
		}

		if (vasMovement.isNewRecord()) {
			vasMovement.setVasMovementId(getVasMovementDAO().save(vasMovement, tableType));
			auditHeader.getAuditDetail().setModelData(vasMovement);
			auditHeader.setAuditReference(String.valueOf(vasMovement.getVasMovementId()));
		} else {
			getVasMovementDAO().update(vasMovement, tableType);
		}

		//Retrieving List of Audit Details For check list detail  related modules
		if (vasMovement.getVasMvntList() != null && vasMovement.getVasMvntList().size() > 0) {
			List<AuditDetail> details = vasMovement.getLovDescAuditDetailMap().get("VasMovementDetail");
			details = processingChkListDetailList(details, tableType, vasMovement.getVasMovementId());
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
	 * BMTVasMovement by using VasMovementDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtBMTVasMovement by using auditHeaderDAO.addAudit(auditHeader)
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

		VasMovement vasMovement = (VasMovement) auditHeader.getAuditDetail().getModelData();
		getVasMovementDAO().delete(vasMovement, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(vasMovement, "", auditHeader.getAuditTranType())));
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getVasMovementById fetch the details by using VasMovementDAO's getVasMovementById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VasMovement
	 */
	@Override
	public VasMovement getVasMovementById(String finreference) {
		VasMovement vasMovement = getVasMovementDAO().getVasMovementById(finreference, "_View");
		vasMovement.setVasMvntList(
				getVasMovementDetailDAO().getVasMovementDetailById(vasMovement.getVasMovementId(), "_View"));
		return vasMovement;
	}

	/**
	 * getApprovedVasMovementById fetch the details by using VasMovementDAO's getVasMovementById method . with parameter
	 * id and type as blank. it fetches the approved records from the BMTVasMovement.
	 * 
	 * @param id
	 *            (int)
	 * @return VasMovement
	 */
	public VasMovement getApprovedVasMovementById(String vasReference) {
		return getVasMovementDAO().getVasMovementById(vasReference, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getVasMovementDAO().delete with
	 * parameters vasMovement,"" b) NEW Add new record in to main table by using getVasMovementDAO().save with
	 * parameters vasMovement,"" c) EDIT Update record in the main table by using getVasMovementDAO().update with
	 * parameters vasMovement,"" 3) Delete the record from the workFlow table by using getVasMovementDAO().delete with
	 * parameters vasMovement,"_Temp" 4) Audit the record in to AuditHeader and AdtBMTVasMovement by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtBMTVasMovement by
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

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		VasMovement vasMovement = new VasMovement();
		BeanUtils.copyProperties((VasMovement) auditHeader.getAuditDetail().getModelData(), vasMovement);

		if (vasMovement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getVasMovementDAO().delete(vasMovement, "");
			auditDetails.addAll(listDeletion(vasMovement, "", auditHeader.getAuditTranType()));
		} else {
			vasMovement.setRoleCode("");
			vasMovement.setNextRoleCode("");
			vasMovement.setTaskId("");
			vasMovement.setNextTaskId("");
			vasMovement.setWorkflowId(0);

			if (vasMovement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				vasMovement.setRecordType("");
				getVasMovementDAO().save(vasMovement, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				vasMovement.setRecordType("");
				getVasMovementDAO().update(vasMovement, "");
			}
		}

		getVasMovementDAO().delete(vasMovement, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		//Retrieving List of Audit Details For vasMovement details modules
		if (vasMovement.getVasMvntList() != null && vasMovement.getVasMvntList().size() > 0) {
			List<AuditDetail> details = vasMovement.getLovDescAuditDetailMap().get("VasMovementDetail");
			details = processingChkListDetailList(details, "", vasMovement.getVasMovementId());
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(vasMovement, "_Temp", auditHeader.getAuditTranType())));
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(vasMovement);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getVasMovementDAO().delete with parameters vasMovement,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBMTVasMovement by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		VasMovement vasMovement = (VasMovement) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getVasMovementDAO().delete(vasMovement, "_Temp");
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(vasMovement, "_Temp", auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getVasMovementDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
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
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getVasMovementDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		VasMovement vasMovement = (VasMovement) auditDetail.getModelData();

		VasMovement tempVasMovement = null;
		if (vasMovement.isWorkflow()) {
			tempVasMovement = getVasMovementDAO().getVasMovementById(vasMovement.getFinReference(), "_Temp");
		}
		VasMovement befVasMovement = getVasMovementDAO().getVasMovementById(vasMovement.getFinReference(), "");

		VasMovement oldVasMovement = vasMovement.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(vasMovement.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_VasMovementId") + ":" + valueParm[0];

		if (vasMovement.isNewRecord()) { // for New record or new record into work flow

			if (!vasMovement.isWorkflow()) {// With out Work flow only new records  
				if (befVasMovement != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (vasMovement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befVasMovement != null || tempVasMovement != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befVasMovement == null || tempVasMovement != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!vasMovement.isWorkflow()) { // With out Work flow for update and delete

				if (befVasMovement == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldVasMovement != null
							&& !oldVasMovement.getLastMntOn().equals(befVasMovement.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempVasMovement == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempVasMovement != null && oldVasMovement != null
						&& !oldVasMovement.getLastMntOn().equals(tempVasMovement.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !vasMovement.isWorkflow()) {
			vasMovement.setBefImage(befVasMovement);
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
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		VasMovement vasMovement = (VasMovement) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (vasMovement.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (vasMovement.getVasMvntList() != null && vasMovement.getVasMvntList().size() > 0) {
			auditDetailMap.put("VasMovementDetail", setChkListDetailAuditData(vasMovement, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("VasMovementDetail"));
		}

		vasMovement.setLovDescAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(vasMovement);
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
	private List<AuditDetail> setChkListDetailAuditData(VasMovement vasMovement, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new VasMovementDetail(),
				new VasMovementDetail().getExcludeFields());

		for (int i = 0; i < vasMovement.getVasMvntList().size(); i++) {
			VasMovementDetail vasMovementDetail = vasMovement.getVasMvntList().get(i);

			// Skipping the process of current iteration when the child was not modified to avoid unnecessary processing
			if (StringUtils.isEmpty(vasMovementDetail.getRecordType())) {
				continue;
			}

			vasMovementDetail.setWorkflowId(vasMovement.getWorkflowId());
			vasMovementDetail.setVasMovementId(vasMovement.getVasMovementId());

			boolean isRcdType = false;

			if (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				vasMovementDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				vasMovementDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (vasMovement.isWorkflow()) {
					isRcdType = true;
				}
			} else if (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				vasMovementDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				vasMovementDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			vasMovementDetail.setRecordStatus(vasMovement.getRecordStatus());
			vasMovementDetail.setUserDetails(vasMovement.getUserDetails());
			vasMovementDetail.setLastMntOn(vasMovement.getLastMntOn());
			vasMovementDetail.setLastMntBy(vasMovement.getLastMntBy());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					vasMovementDetail.getBefImage(), vasMovementDetail));
		}

		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Educational expenses
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingChkListDetailList(List<AuditDetail> auditDetails, String type,
			long vasMovementId) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			VasMovementDetail vasMovementDetail = (VasMovementDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			vasMovementDetail.setVasMovementId(vasMovementId);
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				vasMovementDetail.setVersion(vasMovementDetail.getVersion() + 1);
				vasMovementDetail.setRoleCode("");
				vasMovementDetail.setNextRoleCode("");
				vasMovementDetail.setTaskId("");
				vasMovementDetail.setNextTaskId("");
			}

			vasMovementDetail.setWorkflowId(0);

			if (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (vasMovementDetail.isNewRecord()) {
				saveRecord = true;
				if (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					vasMovementDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					vasMovementDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					vasMovementDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (vasMovementDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (vasMovementDetail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = vasMovementDetail.getRecordType();
				recordStatus = vasMovementDetail.getRecordStatus();
				vasMovementDetail.setRecordType("");
				vasMovementDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {

				getVasMovementDetailDAO().save(vasMovementDetail, type);
			}

			if (updateRecord) {
				getVasMovementDetailDAO().update(vasMovementDetail, type);
			}

			if (deleteRecord) {
				getVasMovementDetailDAO().delete(vasMovementDetail, type);
			}

			if (approveRec) {
				vasMovementDetail.setRecordType(rcdType);
				vasMovementDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(vasMovementDetail);
		}
		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Method deletion of VasMovementDetail list with existing fee type
	 * 
	 * @param fee
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(VasMovement vasMovement, String tableType, String auditTranType) {
		logger.debug("Entering ");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (vasMovement.getVasMvntList() != null && vasMovement.getVasMvntList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new VasMovementDetail());
			for (int i = 0; i < vasMovement.getVasMvntList().size(); i++) {
				VasMovementDetail vasMovementDetail = vasMovement.getVasMvntList().get(i);
				if (!StringUtils.isEmpty(vasMovementDetail.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							vasMovementDetail.getBefImage(), vasMovementDetail));
				}
			}
			VasMovementDetail vasMovementDetail = vasMovement.getVasMvntList().get(0);
			getVasMovementDetailDAO().delete(vasMovementDetail.getVasMovementId(), tableType);
		}

		logger.debug("Leaving ");
		return auditList;
	}

	/**
	 * Common Method for VasMovement list validation
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

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				VasMovementDetail vasMovementDetail = (VasMovementDetail) ((AuditDetail) list.get(i)).getModelData();
				rcdType = vasMovementDetail.getRecordType();

				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isNotEmpty(transType)) {
					//check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(),
							vasMovementDetail.getBefImage(), vasMovementDetail));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

	public VasMovementDAO getVasMovementDAO() {
		return vasMovementDAO;
	}

	public void setVasMovementDAO(VasMovementDAO vasMovementDAO) {
		this.vasMovementDAO = vasMovementDAO;
	}

	public VasMovementDetailDAO getVasMovementDetailDAO() {
		return vasMovementDetailDAO;
	}

	public void setVasMovementDetailDAO(VasMovementDetailDAO vasMovementDetailDAO) {
		this.vasMovementDetailDAO = vasMovementDetailDAO;
	}
}