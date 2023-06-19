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
 * * FileName : SecurityUserOperationsServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-06-2011
 * * * Modified Date : 2-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 2-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.administration.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.administration.SecurityOperationDAO;
import com.pennant.backend.dao.administration.SecurityOperationRolesDAO;
import com.pennant.backend.dao.administration.SecurityRoleGroupsDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityOperationRoles;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserOperations;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityUserOperationsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.RequestSource;

public class SecurityUserOperationsServiceImpl extends GenericService<SecurityUserOperations>
		implements SecurityUserOperationsService {
	private static final Logger logger = LogManager.getLogger(SecurityUserOperationsServiceImpl.class);

	private SecurityUserOperationsDAO securityUserOperationsDAO;
	private SecurityRoleGroupsDAO securityRoleGroupsDAO;
	private SecurityOperationRolesDAO securityOperationRolesDAO;
	private SecurityOperationDAO securityOperationDAO;
	private SecurityUserDAO securityUserDAO;
	private AuditHeaderDAO auditHeaderDAO;

	@Override
	public SecurityUserOperations getSecurityUserOperations() {
		return securityUserOperationsDAO.getSecurityUserOperations();
	}

	/**
	 * This method do the following 1)Gets the AuditDetails list by calling businessValidation() method 2)a)it checks
	 * for each AuditDetail if "AuditTranType" is "A" it saves the record by calling SecurityUsersOperationsDAO's save()
	 * method b)if "AuditTranType" is "D" it Deletes the record by calling SecurityUsersOperationsDAO's delete() method
	 * 
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();

		if (securityUser.isWorkflow()) {
			tableType = "_Temp";
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		} else {
			securityUser.setRecordType("");
			securityUser.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			auditHeader.getAuditDetail().setModelData(securityUser);
		}

		if (securityUser.isWorkflow()) {
			if (securityUser.isNewRecord()) {
				securityUserDAO.save(securityUser, "_RTEMP");
				auditHeader.getAuditDetail().setModelData(securityUser);
				auditHeader.setAuditReference(String.valueOf(securityUser.getUsrID()));
			} else {
				securityUserDAO.update(securityUser, "_RTEMP");
			}
		}
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		// PROCESS DETAILS
		if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
			auditDetails.addAll(processingDetailList(auditHeader.getAuditDetails(), tableType, securityUser));
		}
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;

	}

	/**
	 * This method do the following 1)get the AuditDetails list by calling getAuditDetailsList(). 2)It validate each
	 * AuditDetail in the AuditDetails list by calling validate method
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 */
	public AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean online) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,
				online);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = getAuditUserOperations(securityUser, auditHeader.getAuditTranType(), method,
				auditHeader.getUsrLanguage(), online);

		for (AuditDetail detail : auditDetails) {
			auditHeader.addAuditDetail(detail);
			auditHeader.setErrorList(detail.getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getSecurityUserRoleDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, boolean online) {

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		SecurityUser secUser = (SecurityUser) auditDetail.getModelData();

		SecurityUser tempSecurityUser = null;
		if (secUser.isWorkflow()) {
			tempSecurityUser = securityUserDAO.getSecurityUserById(secUser.getId(), "_RTemp");
		}

		SecurityUser befSecurityUser = securityUserDAO.getSecurityUserById(secUser.getId(), "");
		SecurityUser oldSecurityUser = secUser.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(secUser.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_UsrID") + ":" + valueParm[0];

		if (secUser.isNewRecord()) { // for New record or new record into work
										// flow

			if (!secUser.isWorkflow()) { // With out Work flow only new records
				if (befSecurityUser != null) { // Record Already Exists in the
												// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (secUser.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
																						// is new
					if (befSecurityUser != null || tempSecurityUser != null) { // if
																				// records
																				// already
																				// exists
																				// in
																				// the
																				// main
																				// table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befSecurityUser == null || tempSecurityUser != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			if (RequestSource.API.equals(secUser.getRequestSource())) {
				return auditDetail;
			}
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!secUser.isWorkflow()) { // With out Work flow for update and
											// delete

				if (befSecurityUser == null) { // if records not exists in the
												// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldSecurityUser != null
							&& !oldSecurityUser.getLastMntOn().equals(befSecurityUser.getLastMntOn())) {
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

				if (tempSecurityUser == null) { // if records not exists in the
												// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldSecurityUser != null
						&& !oldSecurityUser.getLastMntOn().equals(tempSecurityUser.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		if (securityUserOperationsDAO.getOprById(getSecurityUserOperations().getOprID(), "_View") > 0) {
			throw new WrongValueException(Labels.getLabel("RECORD_IN_USE",
					new String[] { Labels.getLabel("label_SecurityUserOperationsDialog_OperationID.label"),
							Long.toString(getSecurityUserOperations().getOprID()) }));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !secUser.isWorkflow()) {
			auditDetail.setBefImage(befSecurityUser);
		}
		return auditDetail;
	}

	public List<AuditDetail> getAuditUserOperations(SecurityUser securityUser, String auditTranType, String method,
			String language, boolean online) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		boolean delete = false;
		/*
		 * if ((PennantConstants.RECORD_TYPE_DEL.equals(securityUser.getRecordType ()) &&
		 * method.equalsIgnoreCase("doApprove")) || method.equals("delete")) { delete=true; }
		 */

		for (int i = 0; i < securityUser.getSecurityUserOperationsList().size(); i++) {
			SecurityUserOperations securityUserOperations = securityUser.getSecurityUserOperationsList().get(i);
			securityUserOperations.setWorkflowId(securityUser.getWorkflowId());

			boolean isNewRecord = false;

			if (delete) {
				securityUserOperations.setRecordType(PennantConstants.RECORD_TYPE_MDEL);
			} else {
				if (securityUserOperations.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					securityUserOperations.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isNewRecord = true;
				} else if (securityUserOperations.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					securityUserOperations.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isNewRecord = false;
				} else if (securityUserOperations.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					securityUserOperations.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					isNewRecord = false;
				}
			}
			if ("saveOrUpdate".equals(method) && (isNewRecord && securityUserOperations.isWorkflow())) {
				securityUserOperations.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (securityUserOperations.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (securityUserOperations.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			securityUserOperations.setRecordStatus(securityUser.getRecordStatus());
			securityUserOperations.setUserDetails(securityUser.getUserDetails());
			securityUserOperations.setLastMntOn(securityUser.getLastMntOn());
			securityUserOperations.setLastMntBy(securityUser.getLastMntBy());

			if (StringUtils.isNotEmpty(securityUserOperations.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, securityUserOperations.getBefImage(),
						securityUserOperations));
			}
		}

		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for securityUserOperations
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingDetailList(List<AuditDetail> auditDetails, String type,
			SecurityUser securityUser) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		List<AuditDetail> list = new ArrayList<AuditDetail>();

		for (AuditDetail auditDetail : auditDetails) {

			SecurityUserOperations suo = (SecurityUserOperations) auditDetail.getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;

			if (StringUtils.isEmpty(type)) {
				suo.setVersion(suo.getVersion() + 1);
				approveRec = true;
			} else {
				suo.setRoleCode(securityUser.getRoleCode());
				suo.setNextRoleCode(securityUser.getNextRoleCode());
				suo.setTaskId(securityUser.getTaskId());
				suo.setNextTaskId(securityUser.getNextTaskId());
			}

			if (StringUtils.isNotEmpty(type) && suo.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (suo.isNewRecord()) {
				saveRecord = true;
				if (suo.getId() == Long.MIN_VALUE) {
					suo.setId(securityUserOperationsDAO.getNextValue());
				}
				if (suo.getRecordType().equals(PennantConstants.RCD_ADD)) {
					suo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (suo.getRecordType().equals(PennantConstants.RCD_DEL)) {
					suo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (suo.getRecordType().equals(PennantConstants.RCD_UPD)) {
					suo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (suo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (suo.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (suo.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (suo.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			SecurityUserOperations tempDetail = new SecurityUserOperations();
			BeanUtils.copyProperties(suo, tempDetail);

			if (approveRec) {
				suo.setRoleCode("");
				suo.setNextRoleCode("");
				suo.setTaskId("");
				suo.setNextTaskId("");
				suo.setRecordType("");
				suo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {
				securityUserOperationsDAO.save(suo, type);
			}

			if (updateRecord) {
				securityUserOperationsDAO.update(suo, type);
			}

			if (deleteRecord) {
				securityUserOperationsDAO.delete(suo, type);
			}

			if (saveRecord || updateRecord || deleteRecord) {
				if (!suo.isWorkflow()) {
					auditDetail.setModelData(suo);
				} else {
					auditDetail.setModelData(tempDetail);
				}
				list.add(auditDetail);
			}
		}

		logger.debug("Leaving ");
		return list;
	}

	/**
	 * This method fetches List< SecurityRoleGroups > with "RoleId" condition by calling
	 * <code>SecurityRoleGroupsDAO</code>'s <code>getSecRoleGroupsByRoleID()</code>
	 * 
	 * @return List<SecurityRoleGroups>
	 */
	public List<SecurityRoleGroups> getApprovedRoleGroupsByRoleId(long roleId) {
		return securityRoleGroupsDAO.getRoleGroupsByRoleID(roleId, "_AView");
	}

	/**
	 * This method fetches List< SecurityOperationRoles > with "GrpId" condition by calling SecurityOperationRolesDAO's
	 * getSecurityOperationRolesByGrpId()
	 * 
	 * @param securityGroup (SecurityGroup)
	 * @return List<SecurityOperationRoles>
	 */
	public List<SecurityOperationRoles> getOperationRolesByOprId(SecurityUserOperations securityUserOperations) {
		return securityOperationRolesDAO.getSecOprRolesByOprID(securityUserOperations, "_AView");

	}

	/**
	 * This method fetches <code>List< SecurityRole > </code> with "userId" condition by calling
	 * <code>SecurityUsersOperationsDAO</code>'s <code>getOperationsByUserId()</code>
	 * 
	 * @param userId     (long)
	 * @param isAssigned (boolean)
	 */
	public List<SecurityOperation> getOperationsByUserId(long userId, boolean isAssigned) {
		return securityUserOperationsDAO.getOperationsByUserId(userId, isAssigned);
	}

	/**
	 * This method fetches SecurityUserOperations record with "userId" and "RoleId" condition by calling
	 * SecurityUsersOperationsDAO's getUserOperationsByUsrAndRoleIds)
	 */
	public SecurityUserOperations getUserOperationsByUsrAndRoleIds(long userId, long roleId) {
		return securityUserOperationsDAO.getUserOperationsByUsrAndRoleIds(userId, roleId);

	}

	@Override
	public List<SecurityOperation> getApprovedOperations() {
		return securityOperationDAO.getApprovedSecurityOperation();
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using securityUserDAO.delete with parameters
	 * securityUser,"" b) NEW Add new record in to main table by using securityUserDAO.save with parameters
	 * securityUser,"" c) EDIT Update record in the main table by using securityUserDAO.update with parameters
	 * securityUser,"" 3) Delete the record from the workFlow table by using securityUserDAO.delete with parameters
	 * securityUser,"_Temp" 4) Audit the record in to AuditHeader and AdtSecurityUsers by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtSecurityUsers by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		SecurityUser securityUser = new SecurityUser();
		BeanUtils.copyProperties((SecurityUser) auditHeader.getAuditDetail().getModelData(), securityUser);
		tranType = PennantConstants.TRAN_UPD;

		// Retrieving List of Audit Details For Security user Operations details
		// modules
		if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
			auditDetails = processingAuditDetailList(auditHeader.getAuditDetails(), "", securityUser);
		}

		if (!PennantConstants.FINSOURCE_ID_API.equals(securityUser.getSourceId())) {
			securityUserOperationsDAO.deleteById(securityUser.getUsrID(), "_Temp");
			securityUserDAO.delete(securityUser, "_RTEMP");
		}

		auditHeader.setAuditDetail(null);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetails);

		auditHeaderDAO.addAudit(auditHeader);

		auditHeader = resetAuditDetails(auditHeader, securityUser, tranType);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	private List<AuditDetail> processingAuditDetailList(List<AuditDetail> auditDetails, String type,
			SecurityUser secUser) {
		logger.debug("Entering ");

		List<AuditDetail> details = new ArrayList<>();

		for (AuditDetail ad : auditDetails) {
			if (ad.getModelData().getClass().isInstance(new SecurityUserOperations())) {
				details.add(ad);
			}
		}

		return processingDetailList(details, type, secUser);
	}

	private AuditHeader resetAuditDetails(AuditHeader auditHeader, SecurityUser securityUser, String tranType) {

		auditHeader.setAuditTranType(tranType);

		if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
			List<AuditDetail> auditDetails = new ArrayList<>();

			for (AuditDetail detail : auditHeader.getAuditDetails()) {
				SecurityUserOperations user = (SecurityUserOperations) detail.getModelData();
				detail.setAuditTranType(tranType);
				user.setRecordType("");
				user.setRoleCode("");
				user.setNextRoleCode("");
				user.setTaskId("");
				user.setNextTaskId("");
				user.setWorkflowId(0);
				detail.setModelData(user);
				auditDetails.add(detail);
			}
			auditHeader.setAuditDetails(auditDetails);
		}

		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		securityUserDAO.delete(securityUser, "_RTEMP");
		securityUserOperationsDAO.deleteById(securityUser.getUsrID(), "_Temp");
		auditHeader.setAuditDetail(null);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * PDC by using PostDatedChequeDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtPDC by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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

		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		securityUserDAO.delete(securityUser, "_RTEMP");
		securityUserOperationsDAO.deleteById(securityUser.getUsrID(), "_Temp");
		securityUserOperationsDAO.getOprById(getSecurityUserOperations().getOprID(), "_View");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public int getSecurityUserOprInQueue(long oprID, String tableType) {
		logger.debug("Entering");
		return securityUserOperationsDAO.getOprById(oprID, "_View");
	}

	@Override
	public List<String> getUsersByRoles(String[] roleCodes) {
		return securityUserOperationsDAO.getUsersByRoles(roleCodes);
	}

	@Override
	public List<String> getUsersByRoles(String[] roleCodes, String division, String branch) {
		return securityUserOperationsDAO.getUsersByRoles(roleCodes, division, branch);
	}

	@Override
	public List<String> getUsrMailsByRoleIds(String roleCode) {
		return securityUserOperationsDAO.getUsrMailsByRoleIds(roleCode);
	}

	public void setSecurityUserOperationsDAO(SecurityUserOperationsDAO securityUserOperationsDAO) {
		this.securityUserOperationsDAO = securityUserOperationsDAO;
	}

	public void setSecurityRoleGroupsDAO(SecurityRoleGroupsDAO securityRoleGroupsDAO) {
		this.securityRoleGroupsDAO = securityRoleGroupsDAO;
	}

	public void setSecurityOperationRolesDAO(SecurityOperationRolesDAO securityOperationRolesDAO) {
		this.securityOperationRolesDAO = securityOperationRolesDAO;
	}

	public void setSecurityOperationDAO(SecurityOperationDAO securityOperationDAO) {
		this.securityOperationDAO = securityOperationDAO;
	}

	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

}
