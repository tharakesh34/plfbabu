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
 * * FileName : SecurityUserServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * *
 * Modified Date : 30-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.administration.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.QueueAssignmentDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.dao.administration.SecurityUserPasswordsDAO;
import com.pennant.backend.dao.applicationmaster.ReportingManagerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.administration.ReportingManager;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityUserAccessService;
import com.pennant.backend.service.administration.SecurityUserHierarchyService;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.App.AuthenticationType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.lic.License;
import com.pennanttech.pennapps.lic.exception.LicenseException;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>SecurityUsers</b>.<br>
 * 
 */
public class SecurityUserServiceImpl extends GenericService<SecurityUser> implements SecurityUserService {

	private AuditHeaderDAO auditHeaderDAO;
	private SecurityUserDAO securityUsersDAO;
	private SecurityUserAccessService securityUserAccessService;
	private SecurityUserHierarchyService securityUserHierarchyService;
	private SecurityUserPasswordsDAO securityUserPasswordsDAO;
	private QueueAssignmentDAO queueAssignmentDAO;
	private SecurityUserOperationsDAO securityUserOperationsDAO;
	private ReportingManagerDAO reportingManagerDAO;

	private static Logger logger = LogManager.getLogger(SecurityUserServiceImpl.class);

	public SecurityUserServiceImpl() {
		super();
	}

	/**
	 * Saves or updates the record.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TableType tableType = TableType.MAIN_TAB;

		if (securityUser.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		} else {

			if (securityUser.isNewRecord()) {
				auditHeader.setAuditTranType(PennantConstants.TRAN_ADD);
			} else {
				auditHeader.setAuditTranType(PennantConstants.TRAN_UPD);
			}

			securityUser.setRecordStatus("");
			securityUser.setRoleCode("");
			securityUser.setNextRoleCode("");
			securityUser.setTaskId("");
			securityUser.setNextTaskId("");
			securityUser.setRecordType("");
			securityUser.setWorkflowId(0);
		}

		if (securityUser.isNewRecord()) {
			securityUser.setId(securityUsersDAO.save(securityUser, tableType.getSuffix()));
			if (AuthenticationType.DAO.name().equals(securityUser.getAuthType())) {
				getSecurityUserPasswordsDAO().save(securityUser);
			}
			auditHeader.getAuditDetail().setModelData(securityUser);
			auditHeader.setAuditReference(String.valueOf(securityUser.getId()));
		} else {
			securityUsersDAO.update(securityUser, tableType.getSuffix());
		}
		List<AuditDetail> auditDetails = new ArrayList<>();

		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
			saveUserDivisions(securityUser, tableType.getSuffix(), null);
		}

		List<AuditDetail> userDivBranchs = securityUser.getAuditDetailMap().get("UserDivBranchs");
		if (CollectionUtils.isNotEmpty(userDivBranchs)) {
			auditDetails.addAll(processingDetailList(userDivBranchs, tableType.getSuffix(), securityUser));
		}


		List<AuditDetail> reportingManagers = securityUser.getAuditDetailMap().get("reportingManagers");
		if (CollectionUtils.isNotEmpty(reportingManagers)) {
			auditDetails.addAll(processingReportingManangers(securityUser, reportingManagers, tableType));
		}

		auditHeader.setAuditDetails(auditDetails);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * SecUsers by using SecurityUsersDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtSecUsers by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<>();
		SecurityUser securityUsers = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		securityUsersDAO.delete(securityUsers, "");
		auditDetails.addAll(secUserDivBranchDeletion(securityUsers, "", auditHeader.getAuditTranType()));
		auditHeader.setAuditDetails(auditDetails);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getSecurityUsersById fetch the details by using SecurityUsersDAO's getSecurityUsersById method.
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return SecurityUsers
	 */

	public SecurityUser getSecurityUserById(long id) {
		logger.debug(Literal.ENTERING);

		SecurityUser securityUser = securityUsersDAO.getSecurityUserById(id, "_View");

		securityUser.setSecurityUserDivBranchList(securityUsersDAO.getSecUserDivBrList(id, "_View"));

		securityUser.setReportingManagersList(reportingManagerDAO.getReportingManagers(id, "_View"));

		return securityUser;
	}

	private List<AuditDetail> processingDetailList(List<AuditDetail> auditDetails, String type,
			SecurityUser securityUser) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		List<AuditDetail> list = new ArrayList<AuditDetail>();

		for (AuditDetail auditDetail : auditDetails) {

			SecurityUserDivBranch securityUserDivBranch = (SecurityUserDivBranch) auditDetail.getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;

			if (StringUtils.isEmpty(type)) {
				securityUserDivBranch.setVersion(securityUserDivBranch.getVersion() + 1);
				approveRec = true;
			} else {
				securityUserDivBranch.setRoleCode(securityUser.getRoleCode());
				securityUserDivBranch.setNextRoleCode(securityUser.getNextRoleCode());
				securityUserDivBranch.setTaskId(securityUser.getTaskId());
				securityUserDivBranch.setNextTaskId(securityUser.getNextTaskId());
			}

			if (StringUtils.isNotEmpty(type)
					&& securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (securityUserDivBranch.isNewRecord()) {
				saveRecord = true;
				if (securityUserDivBranch.getId() == Long.MIN_VALUE) {
					securityUserDivBranch.setUsrID(securityUser.getUsrID());
				}
				if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (securityUserDivBranch.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			SecurityUserDivBranch tempDetail = new SecurityUserDivBranch();
			BeanUtils.copyProperties(securityUserDivBranch, tempDetail);

			if (approveRec) {
				securityUserDivBranch.setRoleCode("");
				securityUserDivBranch.setNextRoleCode("");
				securityUserDivBranch.setTaskId("");
				securityUserDivBranch.setNextTaskId("");
				securityUserDivBranch.setRecordType("");
				securityUserDivBranch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			SecurityUserDivBranch recordExist = getSecurityUsersDAO().getSecUserDivBrDetailsById(securityUserDivBranch,
					type);
			if (saveRecord) {
				if (recordExist == null) {
					getSecurityUsersDAO().saveDivBranchDetails(securityUserDivBranch, type);
				}
			}

			if (updateRecord) {
				if (recordExist != null) {
					getSecurityUsersDAO().updateDivBranchDetails(securityUserDivBranch, type);
				}
			}

			if (deleteRecord) {
				if (recordExist != null) {
					getSecurityUsersDAO().deleteDivBranchDetails(securityUserDivBranch, type);
				} else if ((securityUserDivBranch.getUserBranch() != null) && (recordExist == null)) {
					String tableType = "";
					securityUsersDAO.deleteDivBranchDetails(securityUserDivBranch, tableType);
				}
			}

			if (saveRecord || updateRecord || deleteRecord) {
				if (!securityUserDivBranch.isWorkflow()) {
					auditDetail.setModelData(securityUserDivBranch);
				} else {
					auditDetail.setModelData(tempDetail);
				}
				list.add(auditDetail);
			}
		}

		logger.debug("Leaving ");
		return list;
	}
	// /**
	// * getApprovedSecurityUsersById fetch the details by using SecurityUsersDAO's getSecurityUsersById method .
	// * with parameter id and type as blank. it fetches the approved records from the SecUsers.
	// * @param id (int)
	// * @return SecurityUsers
	// */
	//
	// public SecurityUser getApprovedSecurityUserById(long id) {
	// logger.debug("Entering ");
	// return securityUsersDAO.getSecurityUserById(id,"_AView");
	// }

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using securityUsersDAO.delete with
	 * parameters secUsers,"" b) NEW Add new record in to main table by using securityUsersDAO.save with parameters
	 * secUsers,"" c) EDIT Update record in the main table by using securityUsersDAO.update with parameters secUsers,""
	 * 3) Delete the record from the workFlow table by using securityUsersDAO.delete with parameters secUsers,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtSecUsers by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5)
	 * Audit the record in to AuditHeader and AdtSecUsers by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<>();
		SecurityUser securityUser = new SecurityUser();
		BeanUtils.copyProperties((SecurityUser) auditHeader.getAuditDetail().getModelData(), securityUser);

		String tranType = "";

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		SecurityUser befSecurityUser = securityUsersDAO.getSecurityUserByLogin(securityUser.getUsrLogin(), "");

		if (!PennantConstants.RECORD_TYPE_NEW.equals(securityUser.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(befSecurityUser);
		}

		if (securityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			securityUser.setUsrEnabled(false);
			securityUser.setDeleted(true);

			securityUsersDAO.markAsDelete(securityUser, "");
		} else {
			securityUser.setRoleCode("");
			securityUser.setNextRoleCode("");
			securityUser.setTaskId("");
			securityUser.setNextTaskId("");
			securityUser.setWorkflowId(0);

			if (securityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				securityUser.setRecordType("");
				securityUser.setAccountUnLockedOn(DateUtil.getSysDate());
				securityUsersDAO.save(securityUser, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				securityUser.setRecordType("");
				if (befSecurityUser != null) {
					if (befSecurityUser.isUsrAcLocked() == true && securityUser.isUsrAcLocked() == false) {
						securityUser.setAccountUnLockedOn(DateUtil.getSysDate());
						securityUser.setAccountLockedOn(null);
					} else if (befSecurityUser.isUsrAcLocked() == false && securityUser.isUsrAcLocked() == true) {
						securityUser.setAccountLockedOn(DateUtil.getSysDate());
						securityUser.setAccountUnLockedOn(null);
					}
				}

				securityUsersDAO.update(securityUser, "");
			}

			List<AuditDetail> userDivBranchs = securityUser.getAuditDetailMap().get("UserDivBranchs");
			if (CollectionUtils.isNotEmpty(userDivBranchs)) {
				securityUsersDAO.deleteBranchs(securityUser, "_temp");
				auditDetails.addAll(processingDetailList(userDivBranchs, "", securityUser));
			}

			List<AuditDetail> reportingManagers = securityUser.getAuditDetailMap().get("reportingManagers");
			if (CollectionUtils.isNotEmpty(reportingManagers)) {
				auditDetails.addAll(processingReportingManangers(securityUser, reportingManagers, TableType.MAIN_TAB));

			}

			refreshUserHierarchy(securityUser);
		}

		securityUsersDAO.delete(securityUser, "_Temp");

		List<ReportingManager> reportingManagers = securityUser.getReportingManagersList();
		if (CollectionUtils.isNotEmpty(reportingManagers)) {
			ReportingManager reportingManager = new ReportingManager();
			String[] fields = PennantJavaUtil.getFieldDetails(reportingManager, reportingManager.getExcludeFields());

			for (int i = 0; i < reportingManagers.size(); i++) {
				ReportingManager customerAddres = reportingManagers.get(i);
				auditDetails.add(new AuditDetail(tranType, i + 1, fields[0], fields[1], customerAddres.getBefImage(),
						customerAddres));
			}

			reportingManagerDAO.deleteByUserId(securityUser.getUsrID(), TableType.TEMP_TAB);

		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(securityUser);
		getAuditHeaderDAO().addAudit(auditHeader);

		// If any records exists in this user queue re-assign them to next available users
		if (!securityUser.isUsrEnabled()) {
			getQueueAssignmentDAO().executeStoredProcedure(securityUser.getUsrID());
		}

		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using securityUsersDAO.delete with parameters securityUsers,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtSecUsers by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditDetails.addAll(
				deleteDivBranchs(securityUser.getSecurityUserDivBranchList(), "_Temp", auditHeader.getAuditTranType()));
		auditDetails.addAll(deleteReportinManagers(securityUser.getReportingManagersList(), "_Temp",
				auditHeader.getAuditTranType()));
		securityUsersDAO.delete(securityUser, "_Temp");
		auditHeader.setAuditDetails(auditDetails);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		String auditTranType = auditHeader.getAuditTranType();
		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = securityUser.getUserDetails().getLanguage();

		// for reporting manager
		List<ReportingManager> reportingmangerlist = securityUser.getReportingManagersList();
		if (CollectionUtils.isNotEmpty(reportingmangerlist)) {
			auditDetails = getAuditUserReportingmanagers(securityUser, auditTranType, method, usrLanguage, false);
			securityUser.getAuditDetailMap().put("reportingManagers", auditDetails);
		}

		List<SecurityUserDivBranch> securityUserDivBranchList = securityUser.getSecurityUserDivBranchList();
		if (securityUserDivBranchList != null && !securityUserDivBranchList.isEmpty()) {
			auditDetails = getAuditUserDivBranchs(securityUser, auditTranType, method, usrLanguage, false);
			securityUser.getAuditDetailMap().put("UserDivBranchs", auditDetails);
		}

		for (AuditDetail detail : auditDetails) {
			auditHeader.addAuditDetail(detail);
			auditHeader.setErrorList(detail.getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from securityUsersDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		SecurityUser securityUser = (SecurityUser) auditDetail.getModelData();

		SecurityUser befSecurityUser = securityUsersDAO.getSecurityUserByLogin(securityUser.getUsrLogin(), "");

		if ("changePassword".equals(StringUtils.trimToEmpty(method))) {
			auditDetail.setBefImage(befSecurityUser);
			logger.debug("Leaving ");
			return auditDetail;
		}

		SecurityUser tempSecurityUser = null;
		if (securityUser.isWorkflow()) {
			tempSecurityUser = securityUsersDAO.getSecurityUserByLogin(securityUser.getUsrLogin(), "_Temp");
		}
		// SecurityUser aBefSecurityUser= securityUsersDAO.getSecurityUserByLogin(securityUser.getUsrLogin(), "");
		SecurityUser oldSecurityUser = securityUser.getBefImage();

		String[] errParm = new String[4];
		errParm[0] = PennantJavaUtil.getLabel("label_UsrLogin");
		errParm[1] = String.valueOf(securityUser.getUsrLogin());

		String[] userLoginExisted = new String[4];
		userLoginExisted[0] = PennantJavaUtil.getLabel("label_UsrLogin");
		userLoginExisted[1] = String.valueOf(securityUser.getUsrLogin());

		String[] parmUserIdAssigned = new String[10];
		parmUserIdAssigned[0] = PennantJavaUtil.getLabel("label_Roles");
		parmUserIdAssigned[1] = PennantJavaUtil.getLabel("label_User");

		if (securityUser.isNewRecord()) { // for New record or new record into work flow

			if (!securityUser.isWorkflow()) {// With out Work flow only new records
				if (befSecurityUser != null) { // Record Already Exists in the table with same userID then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));

				}
				/*
				 * if (aBefSecurityUser !=null){ // Record Already Exists in the table with same userLogin then error
				 * auditDetail.setErrorDetail(new
				 * ErrorDetails(PennantConstants.KEY_FIELD,"41001",userLoginExisted,null));
				 * 
				 * }
				 */
			} else { // with work flow
				if (tempSecurityUser != null) { // if records already exists in the Work flow table
					// auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));

				}

				if (securityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befSecurityUser != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));

					}
				} else { // if records not exists in the Main flow table
					if (befSecurityUser == null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));

					}
					/*
					 * if (aBefSecurityUser !=null){ // Record Already Exists in the table then error
					 * auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					 * 
					 * }
					 */
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!securityUser.isWorkflow()) { // With out Work flow for update and delete

				if (befSecurityUser == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));

				}

				if (befSecurityUser != null && oldSecurityUser != null
						&& !oldSecurityUser.getLastMntOn().equals(befSecurityUser.getLastMntOn())) {
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
							.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
					} else {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
					}

				}

			} else {

				if (tempSecurityUser == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));

				}

				if (tempSecurityUser != null && oldSecurityUser != null
						&& !oldSecurityUser.getLastMntOn().equals(tempSecurityUser.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));

				}
			}
		}

		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, securityUser.getRecordType())) {
			/* check whether userId assigned to any Roles by calling SecurityUsersRolesDAO's getUserIdCount() */
			int roleIdCount = getSecurityUserOperationsDAO().getUserIdCount(securityUser.getUsrID());
			/* if roleId assigned for any user or group show error message */
			if (roleIdCount > 0) {
				auditDetail
						.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "49002", parmUserIdAssigned, null));

			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))) {
			securityUser.setBefImage(befSecurityUser);
			// Validating the active users limit
			checkUserLimit(auditDetail);
		}
		logger.debug("Leaving ");
		return auditDetail;
	}

	/**
	 * Updates the record.
	 * 
	 * @param auditHeader (AuditHeader)
	 * @return auditHeaders (AuditHeader)
	 */
	public AuditHeader changePassword(AuditHeader auditHeader) {
		logger.trace(Literal.ENTERING);
		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		securityUser.getBefImage().getLastMntOn();
		auditHeader = businessValidation(auditHeader, "changePassword");

		if (!auditHeader.isNextProcess()) {
			logger.trace(Literal.LEAVING);
			return auditHeader;
		}

		if (securityUser.getLastMntBy() == securityUser.getUsrID()) {
			// Save to the log for maintaining the history.
			getSecurityUserPasswordsDAO().save(securityUser);
		} else {
			// As the administrator changes, set expire date so that system will force the user to change on his/her
			// next login.
			securityUser.setPwdExpDt(DateUtil.addDays(new Date(System.currentTimeMillis()), -1));
		}

		securityUsersDAO.changePassword(securityUser);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.trace(Literal.LEAVING);
		return auditHeader;
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

	public void setSecurityUserDAO(SecurityUserDAO securityUsersDAO) {
		this.securityUsersDAO = securityUsersDAO;
	}

	public void setSecurityUserAccessService(SecurityUserAccessService securityUserAccessService) {
		this.securityUserAccessService = securityUserAccessService;
	}

	public void setSecurityUserHierarchyService(SecurityUserHierarchyService securityUserHierarchyService) {
		this.securityUserHierarchyService = securityUserHierarchyService;
	}

	public void setSecurityUserPasswordsDAO(SecurityUserPasswordsDAO securityUserPasswordsDAO) {
		this.securityUserPasswordsDAO = securityUserPasswordsDAO;
	}

	public SecurityUserPasswordsDAO getSecurityUserPasswordsDAO() {
		return securityUserPasswordsDAO;
	}

	// Security User Division Branch Details

	/**
	 * This method is to fetch division branch details for current user
	 */
	@Override
	public List<SecurityUserDivBranch> getSecUserDivBrList(long usrID, String type) {
		return securityUsersDAO.getSecUserDivBrList(usrID, type);
	}

	/**
	 * This method is to fetch Entity Details for current user
	 */
	@Override
	public List<Entity> getEntityList(String entity) {
		return securityUsersDAO.getEntityList(entity);
	}

	/**
	 * This method is to Delete division branch details for current user
	 * 
	 * @param securityUsers
	 * @param tableType
	 * @param auditTranType
	 * @return
	 */
	public List<AuditDetail> secUserDivBranchDeletion(SecurityUser securityUsers, String tableType,
			String auditTranType) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<>();

		List<SecurityUserDivBranch> securityUserDivBranchList = securityUsers.getSecurityUserDivBranchList();

		if (securityUserDivBranchList != null && !securityUserDivBranchList.isEmpty()) {
			auditDetails.addAll(deleteDivBranchs(securityUserDivBranchList, tableType, auditTranType));
		}
		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * This method is to Delete division branch details for current user
	 */
	@Override
	public List<AuditDetail> deleteDivBranchs(List<SecurityUserDivBranch> securityUserDivBranchList, String tableType,
			String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (securityUserDivBranchList != null) {
			for (SecurityUserDivBranch securityUserDivBranch : securityUserDivBranchList) {
				SecurityUserDivBranch recordExistsTemp = securityUsersDAO
						.getSecUserDivBrDetailsById(securityUserDivBranch, "_Temp");
				if (recordExistsTemp != null) {
					securityUsersDAO.deleteDivBranchDetails(securityUserDivBranch, "_Temp");
				}

				SecurityUserDivBranch recordExistsMain = securityUsersDAO
						.getSecUserDivBrDetailsById(securityUserDivBranch, tableType);
				if (recordExistsMain != null) {
					securityUsersDAO.deleteDivBranchDetails(securityUserDivBranch, tableType);
				}
				String[] fields = PennantJavaUtil.getFieldDetails(securityUserDivBranch,
						securityUserDivBranch.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						securityUserDivBranch.getBefImage(), securityUserDivBranch));
				if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
					securityUserAccessService.deleteDivBranchDetails(securityUserDivBranch);
				}
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> deleteReportinManagers(List<ReportingManager> reportingManagers, String tableType,
			String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (reportingManagers != null) {
			for (ReportingManager deManager : reportingManagers) {
				ReportingManager recordExistsTemp = reportingManagerDAO.getReportingManager(deManager.getId(), "_Temp");
				if (recordExistsTemp != null) {
					reportingManagerDAO.deleteById(deManager.getId(), TableType.TEMP_TAB);
				}
				String[] fields = PennantJavaUtil.getFieldDetails(deManager, deManager.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						deManager.getBefImage(), deManager));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * This method is to Approve division branch details for current user
	 */
	public List<AuditDetail> doApproveDivBrDetails(SecurityUser securityUser, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<SecurityUserDivBranch> securityUserDivBranchList = securityUser.getSecurityUserDivBranchList();
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		List<SecurityUserDivBranch> secUserDivBranchList = securityUsersDAO.getSecUserDivBrList(securityUser.getUsrID(),
				tableType);
		if (!secUserDivBranchList.isEmpty()) {
			securityUsersDAO.deleteBranchs(securityUser, tableType);
		}

		for (SecurityUserDivBranch securityUserDivBranch : securityUserDivBranchList) {
			if (securityUserDivBranch.getId() == Long.MIN_VALUE) {
				securityUserDivBranch.setUsrID(securityUser.getUsrID());
			}
			SecurityUserDivBranch detail = new SecurityUserDivBranch();
			BeanUtils.copyProperties(securityUserDivBranch, detail);

			securityUserDivBranch.setRoleCode("");
			securityUserDivBranch.setNextRoleCode("");
			securityUserDivBranch.setTaskId("");
			securityUserDivBranch.setNextTaskId("");
			securityUserDivBranch.setWorkflowId(0);

			SecurityUserDivBranch recordExistMain = securityUsersDAO.getSecUserDivBrDetailsById(securityUserDivBranch,
					tableType);
			if (recordExistMain == null) {
				securityUsersDAO.saveDivBranchDetails(securityUserDivBranch, tableType);
			}
			securityUsersDAO.deleteDivBranchDetails(securityUserDivBranch, "_Temp");

			String[] fields = PennantJavaUtil.getFieldDetails(securityUserDivBranch,
					securityUserDivBranch.getExcludeFields());
			auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, auditDetails.size() + 1, fields[0], fields[1],
					detail.getBefImage(), detail));
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
					securityUserDivBranch.getBefImage(), securityUserDivBranch));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * @param securityUser
	 * @param auditTranType
	 * @param method
	 * @param language
	 * @param online
	 * @return
	 */
	public List<AuditDetail> getAuditUserDivBranchs(SecurityUser securityUser, String auditTranType, String method,
			String language, boolean online) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		boolean delete = false;
		/*
		 * if ((PennantConstants.RECORD_TYPE_DEL.equals(securityUser.getRecordType()) &&
		 * method.equalsIgnoreCase("doApprove")) || method.equals("delete")) { delete=true; }
		 */

		for (int i = 0; i < securityUser.getSecurityUserDivBranchList().size(); i++) {
			SecurityUserDivBranch securityUserDivBranch = securityUser.getSecurityUserDivBranchList().get(i);
			securityUserDivBranch.setWorkflowId(securityUser.getWorkflowId());

			boolean isNewRecord = false;

			if (delete) {
				securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_MDEL);
			} else {

				String recordType = securityUserDivBranch.getRecordType();

				if (StringUtils.equals(PennantConstants.RCD_ADD, recordType)) {
					securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isNewRecord = true;
				} else if (StringUtils.equals(PennantConstants.RCD_UPD, recordType)) {
					securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isNewRecord = true;
				} else if (StringUtils.equals(PennantConstants.RCD_DEL, recordType)) {
					securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					isNewRecord = true;
				}
			}
			if ("saveOrUpdate".equals(method) && (isNewRecord && securityUserDivBranch.isWorkflow())) {
				if (!StringUtils.equals(PennantConstants.RCD_DEL, securityUserDivBranch.getRecordType())) {
					securityUserDivBranch.setNewRecord(true);
				}
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, securityUserDivBranch.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL,
						securityUserDivBranch.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			securityUserDivBranch.setRecordStatus(securityUser.getRecordStatus());
			securityUserDivBranch.setUserDetails(securityUser.getUserDetails());
			securityUserDivBranch.setLastMntOn(securityUser.getLastMntOn());
			securityUserDivBranch.setLastMntBy(securityUser.getLastMntBy());

			if (StringUtils.isNotEmpty(securityUserDivBranch.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, securityUserDivBranch.getBefImage(),
						securityUserDivBranch));
			}
		}

		return auditDetails;
	}

	/**
	 * getApprovedSecurityUsersById fetch the details by using SecurityUsersDAO's getSecurityUsersById method . with
	 * parameter id and type as blank. it fetches the approved records from the SecUsers.
	 * 
	 * @param id (int)
	 * @return SecurityUsers
	 */
	@Override
	public SecurityUser getApprovedSecurityUserById(long id) {
		logger.debug("Entering ");

		return securityUsersDAO.getSecurityUserById(id, "_AView");
	}

	/**
	 * Method For fetching SecurityUser detail by userLogin
	 * 
	 * @param userLogin
	 * 
	 * @return SecurityUser
	 */

	@Override
	public SecurityUser getSecurityUserByLogin(String userLogin) {
		return securityUsersDAO.getSecurityUserByLogin(userLogin, "");
	}

	/**
	 * Method For Preparing List of AuditDetails for securityUser
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private void saveUserDivisions(SecurityUser securityUser, String type, String method) {
		logger.debug(Literal.ENTERING);

		for (SecurityUserDivBranch division : securityUser.getSecurityUserDivBranchList()) {
			if (StringUtils.isEmpty(type)) {
				division.setVersion(division.getVersion() + 1);
			} else {
				division.setRoleCode(securityUser.getRoleCode());
				division.setNextRoleCode(securityUser.getNextRoleCode());
				division.setTaskId(securityUser.getTaskId());
				division.setNextTaskId(securityUser.getNextTaskId());
			}

			division.setUsrID(securityUser.getUsrID());
			division.setRecordType(securityUser.getRecordType());

			division.setRoleCode(securityUser.getRoleCode());
			division.setNextRoleCode(securityUser.getNextRoleCode());
			division.setTaskId(securityUser.getTaskId());
			division.setNextTaskId(securityUser.getNextTaskId());
			division.setRecordType(securityUser.getRecordType());
			division.setRecordStatus(securityUser.getRecordStatus());
		}

		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
			securityUserAccessService.saveDivisionBranches(securityUser, method);
		} else {
			securityUsersDAO.deleteBranchs(securityUser, type);
			if (CollectionUtils.isNotEmpty(securityUser.getSecurityUserDivBranchList())) {
				securityUsersDAO.saveDivBranchDetails(securityUser.getSecurityUserDivBranchList(), type);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public QueueAssignmentDAO getQueueAssignmentDAO() {
		return queueAssignmentDAO;
	}

	public void setQueueAssignmentDAO(QueueAssignmentDAO queueAssignmentDAO) {
		this.queueAssignmentDAO = queueAssignmentDAO;
	}

	public SecurityUserOperationsDAO getSecurityUserOperationsDAO() {
		return securityUserOperationsDAO;
	}

	public void setSecurityUserOperationsDAO(SecurityUserOperationsDAO securityUserOperationsDAO) {
		this.securityUserOperationsDAO = securityUserOperationsDAO;
	}

	@Override
	public SecurityUser getSecurityUserOperationsById(long id) {
		logger.debug("Entering ");
		return getSecurityUserOperationById(id, "_RView", true);
	}

	@Override
	public SecurityUser getApprovedSecurityUserOperationsById(long id) {
		logger.debug("Entering ");
		return getSecurityUserOperationById(id, "_AView", true);
	}

	private SecurityUser getSecurityUserOperationById(long id, String type, boolean getOperations) {
		logger.debug("Entering ");
		SecurityUser securityUser = securityUsersDAO.getSecurityUserById(id, type);
		if (securityUser != null && getOperations) {
			if ("_RView".equals(type)) {
				type = "_View";
			}
			securityUser.setSecurityUserOperationsList(
					getSecurityUserOperationsDAO().getSecUserOperationsByUsrID(securityUser, type));
		}

		logger.debug("Leaving ");
		return securityUser;
	}

	private void checkUserLimit(AuditDetail auditDetail) {
		SecurityUser securityUser = (SecurityUser) auditDetail.getModelData();

		if (PennantConstants.RECORD_TYPE_DEL.equals(securityUser.getRecordType())) {
			return;
		}

		if (securityUser.isUsrEnabled()) {
			try {
				// License.validateLicensedUsers(securityUsersDAO.getActiveUsersCount(securityUser.getId()));
			} catch (LicenseException e) {
				auditDetail.setErrorDetail(new ErrorDetail(e.getErrorCode(), e.getErrorMessage(), null));
			}
		}
	}

	public List<AuditDetail> doApproveReportingManagerDetails(SecurityUser securityUser, String tableType,
			String auditTranType) {
		logger.debug("Entering");
		List<ReportingManager> reportmanagerList = securityUser.getReportingManagersList();
		List<AuditDetail> auditDetails = new ArrayList<>();

		List<ReportingManager> list = reportingManagerDAO.getReportingManagers(securityUser.getUsrID(), tableType);
		if (!list.isEmpty()) {
			// getSecurityUserDAO().deleteBranchs(securityUser, tableType);
		}

		for (ReportingManager reportingmanager : reportmanagerList) {
			if (reportingmanager.getId() == Long.MIN_VALUE) {
				reportingmanager.setUserId(securityUser.getUsrID());
			}
			ReportingManager detail = new ReportingManager();
			BeanUtils.copyProperties(reportingmanager, detail);

			reportingmanager.setRoleCode("");
			reportingmanager.setNextRoleCode("");
			reportingmanager.setTaskId("");
			reportingmanager.setNextTaskId("");
			reportingmanager.setWorkflowId(0);

			ReportingManager recordExistMain = reportingManagerDAO.getReportingManager(reportingmanager.getUserId(),
					tableType);
			if (recordExistMain == null) {
				reportingManagerDAO.save(reportingmanager, tableType);
			}
			reportingManagerDAO.deleteByUserId(reportingmanager.getUserId(), TableType.TEMP_TAB);

		}

		logger.debug("Leaving");
		return auditDetails;
	}

	public List<AuditDetail> getAuditUserReportingmanagers(SecurityUser securityUser, String auditTranType,
			String method, String language, boolean online) {
		List<AuditDetail> auditDetails = new ArrayList<>();
		boolean delete = false;

		for (int i = 0; i < securityUser.getReportingManagersList().size(); i++) {
			ReportingManager reportingmanager = securityUser.getReportingManagersList().get(i);
			reportingmanager.setWorkflowId(securityUser.getWorkflowId());

			if (StringUtils.isEmpty(reportingmanager.getRecordType())) {
				continue;
			}

			boolean isNewRecord = false;

			if (delete) {
				reportingmanager.setRecordType(PennantConstants.RECORD_TYPE_MDEL);
			} else {
				if (reportingmanager.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					reportingmanager.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isNewRecord = true;
				} else if (reportingmanager.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					reportingmanager.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					if (securityUser.isWorkflow()) {
						isNewRecord = true;
					}

				} else if (reportingmanager.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					reportingmanager.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}
			}
			if ("saveOrUpdate".equals(method) && (isNewRecord)) {

				reportingmanager.setNewRecord(true);

			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (reportingmanager.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (reportingmanager.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			reportingmanager.setRecordStatus(securityUser.getRecordStatus());
			reportingmanager.setUserDetails(securityUser.getUserDetails());
			reportingmanager.setLastMntOn(securityUser.getLastMntOn());
			reportingmanager.setLastMntBy(securityUser.getLastMntBy());

			if (StringUtils.isNotEmpty(reportingmanager.getRecordType())) {
				AuditDetail auditDetail = new AuditDetail(auditTranType, i + 1, reportingmanager.getBefImage(),
						reportingmanager);
				auditDetail.setModelData(reportingmanager);
				auditDetails.add(auditDetail);

			}
		}

		return auditDetails;
	}

	private void refreshUserHierarchy(SecurityUser securityUser) {
		securityUserHierarchyService.refreshUserHierarchy(securityUser);
	}

	private List<AuditDetail> processingReportingManangers(SecurityUser securityUser, List<AuditDetail> auditDetails,
			TableType type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			ReportingManager reportingManager = (ReportingManager) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;

			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				reportingManager.setRoleCode("");
				reportingManager.setNextRoleCode("");
				reportingManager.setTaskId("");
				reportingManager.setNextTaskId("");
			}

			reportingManager.setWorkflowId(0);
			reportingManager.setUserId(securityUser.getUsrID());

			if (StringUtils.trimToEmpty(reportingManager.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (reportingManager.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (StringUtils.trimToEmpty(reportingManager.getRecordType())
						.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					reportingManager.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (StringUtils.trimToEmpty(reportingManager.getRecordType())
						.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					reportingManager.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (StringUtils.trimToEmpty(reportingManager.getRecordType())
						.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					reportingManager.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (StringUtils.trimToEmpty(reportingManager.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (StringUtils.trimToEmpty(reportingManager.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (StringUtils.trimToEmpty(reportingManager.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (reportingManager.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = StringUtils.trimToEmpty(reportingManager.getRecordType());
				recordStatus = reportingManager.getRecordStatus();
				reportingManager.setRecordType("");
				reportingManager.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				reportingManagerDAO.save(reportingManager, type.getSuffix());
			}

			if (updateRecord) {
				reportingManagerDAO.update(reportingManager, type);
			}

			if (deleteRecord) {
				reportingManagerDAO.deleteByUserId(reportingManager.getUserId(), type);
			}

			if (approveRec) {
				reportingManager.setRecordType(rcdType);
				reportingManager.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(reportingManager);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public void validateLicensedUsers() throws LicenseException {
		License.validateLicensedUsers(securityUsersDAO.getActiveUsersCount());
	}

	@Override
	public long getSecuredUserDetails(String username) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
		return securityUsersDAO.getUserByName(username);
	}

	public void setReportingManagerDAO(ReportingManagerDAO reportingManagerDAO) {
		this.reportingManagerDAO = reportingManagerDAO;
	}

	public SecurityUserDAO getSecurityUsersDAO() {
		return securityUsersDAO;
	}

	public void setSecurityUsersDAO(SecurityUserDAO securityUsersDAO) {
		this.securityUsersDAO = securityUsersDAO;
	}

}