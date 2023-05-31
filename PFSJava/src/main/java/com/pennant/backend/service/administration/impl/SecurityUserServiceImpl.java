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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.dao.QueueAssignmentDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.dao.administration.SecurityUserPasswordsDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.ClusterDAO;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.applicationmaster.ReportingManagerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.staticparms.LanguageDAO;
import com.pennant.backend.dao.systemmasters.DepartmentDAO;
import com.pennant.backend.dao.systemmasters.DesignationDAO;
import com.pennant.backend.dao.systemmasters.DivisionDetailDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
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
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.constant.LookUpCode;
import com.pennanttech.pennapps.core.App.AuthenticationType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
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
	private static final String ERR_90502 = "90502";
	private static final String ERR_90300 = "90300";
	private static final String ERR_93304 = "93304";
	private static final String ERR_RU0040 = "RU0040";
	private static final String ERR_90010 = "90010";
	private static final String ERR_RU0039 = "RU0039";
	private static final String ERR_90337 = "90337";

	private AuditHeaderDAO auditHeaderDAO;
	private SecurityUserDAO securityUsersDAO;
	private SecurityUserAccessService securityUserAccessService;
	private SecurityUserHierarchyService securityUserHierarchyService;
	private SecurityUserPasswordsDAO securityUserPasswordsDAO;
	private QueueAssignmentDAO queueAssignmentDAO;
	private SecurityUserOperationsDAO securityUserOperationsDAO;
	private ReportingManagerDAO reportingManagerDAO;
	private ClusterDAO clusterDAO;
	private DivisionDetailDAO divisionDetailDAO;
	private EntityDAO entityDAO;
	private LanguageDAO languageDAO;
	private DesignationDAO designationDAO;
	private DepartmentDAO departmentDAO;
	private BranchDAO branchDAO;
	private NotesDAO notesDAO;

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
		} else {
			List<AuditDetail> userDivBranchs = securityUser.getAuditDetailMap().get("UserDivBranchs");
			if (CollectionUtils.isNotEmpty(userDivBranchs)) {
				auditDetails.addAll(processingDetailList(userDivBranchs, tableType.getSuffix(), securityUser));
			}
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

			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
				saveUserDivisions(securityUser, "", "doApprove");
			} else {
				List<AuditDetail> userDivBranchs = securityUser.getAuditDetailMap().get("UserDivBranchs");
				if (CollectionUtils.isNotEmpty(userDivBranchs)) {
					securityUsersDAO.deleteBranchs(securityUser, "_temp");
					auditDetails.addAll(processingDetailList(userDivBranchs, "", securityUser));
				}
			}

			List<AuditDetail> reportingManagers = securityUser.getAuditDetailMap().get("reportingManagers");
			if (CollectionUtils.isNotEmpty(reportingManagers)) {
				auditDetails.addAll(processingReportingManangers(securityUser, reportingManagers, TableType.MAIN_TAB));

			}

			if (CollectionUtils.isNotEmpty(securityUser.getReportingManagersList())) {
				refreshUserHierarchy(securityUser);
			}
		}

		if (!PennantConstants.FINSOURCE_ID_API.equals(securityUser.getSourceId())) {
			securityUsersDAO.delete(securityUser, "_Temp");
		}

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

	@Override
	public SecurityUser getSecurityUserByLoginId(String userLogin) {
		return securityUsersDAO.getSecurityUserByLogin(userLogin, "_Temp");
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

	public void disableUserAccount() {
		logger.debug(Literal.ENTERING);

		List<SecurityUser> users = securityUsersDAO.getDisableUserAccounts();

		int newUsrDays = SysParamUtil.getValueAsInt(SMTParameterConstants.USR_DISABLE_DAYS_NEW);
		int existingUsrDays = SysParamUtil.getValueAsInt(SMTParameterConstants.USR_DISABLE_DAYS_EXISTING);
		Date sysDate = DateUtil.getSysDate();

		List<SecurityUser> updateList = new ArrayList<>();

		for (SecurityUser user : users) {
			Date lastLoginOn = user.getLastLoginOn();
			Date createdOn = user.getCreatedOn();

			if (lastLoginOn == null && DateUtil.getDaysBetween(createdOn, sysDate) > newUsrDays) {
				setDisableUser(user);
				updateList.add(user);
			}

			if (lastLoginOn != null && DateUtil.getDaysBetween(lastLoginOn, sysDate) > existingUsrDays) {
				setDisableUser(user);
				updateList.add(user);
			}

			if (updateList.size() > 1000) {
				securityUsersDAO.updateDisableUser(updateList);
				updateList.clear();
			}
		}

		if (CollectionUtils.isNotEmpty(updateList)) {
			securityUsersDAO.updateDisableUser(updateList);
		}

		logger.debug(Literal.LEAVING);
	}

	private void setDisableUser(SecurityUser user) {
		user.setUsrEnabled(false);
		user.setDisableReason(LookUpCode.SU_DR_DORMANT);
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

	private void setError(AuditDetail ad, String code, String... parms) {
		ErrorDetail error = ErrorUtil.getError(code, parms);

		StringBuilder logMsg = new StringBuilder();
		logMsg.append("\n");
		logMsg.append("=======================================================\n");
		logMsg.append("Error-Code: ").append(error.getCode()).append("\n");
		logMsg.append("Error-Message: ").append(error.getMessage()).append("\n");
		logMsg.append("=======================================================");
		logMsg.append("\n");

		logger.error(Literal.EXCEPTION, logMsg);

		ad.setErrorDetail(error);
	}

	public AuditDetail doUserValidation(AuditHeader ah, boolean isAllowCluster, boolean isUpdate,
			LoggedInUser logUsrDtls) {
		logger.debug(Literal.ENTERING);

		AuditDetail ad = ah.getAuditDetail();
		SecurityUser user = (SecurityUser) ad.getModelData();

		if (user.getUsrID() != Long.MIN_VALUE && !isUpdate) {
			setError(ad, ERR_RU0039, "UsrID");
			return ad;
		}

		if (StringUtils.isBlank(user.getUsrLogin())) {
			setError(ad, ERR_90502, "usrLogin");
			return ad;
		}

		if (StringUtils.isBlank(user.getUserType())) {
			setError(ad, ERR_90502, "userType");
			return ad;
		}

		List<ValueLabel> authTypesList = PennantStaticListUtil.getAuthnticationTypes();

		if (!authTypesList.stream().anyMatch(m -> m.getLabel().equalsIgnoreCase(user.getAuthType()))) {
			setError(ad, ERR_90337, "userType", "Internal/External");
			return ad;
		}

		validateAuthTypeandPassword(ad, user, isUpdate);

		userNameValidations(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			return ad;
		}

		userStaffIDValidation(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			return ad;
		}

		languageValidations(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			return ad;
		}

		userDeptValidations(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			return ad;
		}

		userDesignationValidation(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			return ad;
		}

		userBranchValidation(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			return ad;
		}

		if (user.getUsrMobile() != null && user.getUsrMobile().length() > 13) {
			setError(ad, ERR_90300, "UsrMobile", "13");
			return ad;
		}

		if (user.getUsrEmail() != null && user.getUsrEmail().length() > 50) {
			setError(ad, ERR_90300, "UsrMobile", "50");
			return ad;
		}

		if (!user.isUsrEnabled()) {
			userDisableValidation(ad, user);
		} else if (user.getDisableReason() != null) {
			setError(ad, ERR_RU0040, "disableReason");
			return ad;
		}

		userEmployeeTypeValidation(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			return ad;
		}

		AuditDetail returnStatus = validateDivisions(ad, user, isAllowCluster, logUsrDtls);

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	public AuditDetail doValidation(AuditHeader ah, LoggedInUser logUsrDtls, boolean isFromUserExpire) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = ah.getAuditDetail();
		SecurityUser user = (SecurityUser) auditDetail.getModelData();

		if (StringUtils.isBlank(user.getUsrLogin()) || StringUtils.equals(null, user.getUsrLogin())) {
			setError(auditDetail, "90502", "usrLogin");
			return auditDetail;
		}

		if ((!user.isUsrEnabled() || user.isUsrAcExp()) && StringUtils.isBlank(user.getReason())) {
			setError(auditDetail, "90502", "reason");
			return auditDetail;
		}

		if (!isFromUserExpire) {

			if (!user.isUsrEnabled()) {
				userDisableValidation(auditDetail, user);
			} else if (user.getDisableReason() != null) {
				setError(auditDetail, "RU0040", "disableReason");
				return auditDetail;
			}
		}

		if (CollectionUtils.isNotEmpty(auditDetail.getErrorDetails())) {
			return auditDetail;
		}

		if (isFromUserExpire && !user.isUsrAcExp()) {
			setError(auditDetail, "90008", "usrAcExp");
			return auditDetail;
		}

		SecurityUser UserDetailTemp = getSecurityUserByLoginId(user.getUsrLogin());
		if (UserDetailTemp != null) {
			setError(auditDetail, "90009", "usrLogin");
			return auditDetail;
		}

		return validateDivisions(auditDetail, user, false, logUsrDtls);
	}

	private AuditDetail validateDivisions(AuditDetail auditDetail, SecurityUser user, boolean isAllowCluster,
			LoggedInUser logUsrDtls) {
		logger.debug(Literal.ENTERING);

		List<SecurityUserDivBranch> newdivList = new ArrayList<>();
		AuditDetail ad = new AuditDetail();
		Timestamp lastMntOn = new Timestamp(System.currentTimeMillis());

		for (SecurityUserDivBranch divBranch : user.getSecurityUserDivBranchList()) {
			if (!isAllowCluster) {
				return validateDivisionBased(auditDetail, newdivList, logUsrDtls, divBranch, lastMntOn);
			} else {
				ad = validateClusterbased(auditDetail, newdivList, logUsrDtls, divBranch, lastMntOn);
				if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
					break;
				}
			}
		}

		if (CollectionUtils.isNotEmpty(newdivList)) {
			user.setSecurityUserDivBranchList(newdivList);
		}

		logger.debug(Literal.LEAVING);
		return ad;
	}

	private AuditDetail validateClusterbased(AuditDetail ad, List<SecurityUserDivBranch> divList,
			LoggedInUser userDetails, SecurityUserDivBranch divBranch, Timestamp lastMntOn) {
		logger.debug(Literal.ENTERING);

		AuditDetail returnStatus = new AuditDetail();

		boolean entities = false;
		boolean clusters = false;
		boolean branches = false;

		if (StringUtils.isEmpty(divBranch.getAccessType())) {
			setError(ad, ERR_90502, "Access Type");
			return ad;
		}

		if (!divisionDetailDAO.isActiveDivision(divBranch.getUserDivision())) {
			setError(ad, ERR_93304, "Division");
			return ad;
		}

		if (entityDAO.getEntityCount(divBranch.getEntitiesValues()) == 0) {
			setError(ad, ERR_93304, "Entities");
			return ad;
		}

		switch (divBranch.getAccessType()) {
		case PennantConstants.ACCESSTYPE_ENTITY:
			entities = true;
			if (StringUtils.isEmpty(divBranch.getEntitiesValues())) {
				setError(ad, ERR_90502, "Entities");
				return ad;
			}

			break;
		case PennantConstants.ACCESSTYPE_CLUSTER:
			accessTypeClusterTypeValidation(ad, divBranch);
			clusters = true;
			break;
		case PennantConstants.ACCESSTYPE_BRANCH:
			accessTypeBranchValidation(ad, divBranch);
			branches = true;
			break;
		default:
			break;
		}

		long lastUsr = userDetails.getUserId();
		long currUsr = divBranch.getUsrID();
		String accessType = divBranch.getAccessType();
		String userBranch = divBranch.getUserDivision();

		if (entities) {
			String[] enties = divBranch.getEntitiesValues().split(",");
			for (String value : enties) {
				SecurityUserDivBranch division = new SecurityUserDivBranch();
				division.setAccessType(accessType);
				division.setUsrID(currUsr);
				division.setUserDivision(userBranch);
				division.setEntity(value);
				division.setRecordStatus("");
				division.setLastMntBy(lastUsr);
				division.setLastMntOn(lastMntOn);
				division.setUserDetails(userDetails);

				divList.add(division);
			}

		}
		if (clusters) {
			String[] clusts = divBranch.getClusterValues().split(",");

			for (String value : clusts) {
				SecurityUserDivBranch division = new SecurityUserDivBranch();
				division.setAccessType(accessType);
				division.setUsrID(currUsr);
				division.setUserDivision(userBranch);

				division.setEntity(divBranch.getEntity());
				division.setClusterType(divBranch.getClusterType());
				division.setClusterId(
						clusterDAO.getClusterByCode(value, divBranch.getClusterType(), divBranch.getEntity(), ""));
				division.setRecordStatus("");
				division.setLastMntBy(lastUsr);
				division.setLastMntOn(lastMntOn);
				division.setUserDetails(userDetails);
				divList.add(division);
			}
		}

		if (branches) {
			String[] branchs = divBranch.getBranchValues().split(",");

			for (String value : branchs) {
				SecurityUserDivBranch division = new SecurityUserDivBranch();
				division.setAccessType(accessType);
				division.setUsrID(currUsr);
				division.setUserDivision(userBranch);
				division.setUserBranch(value);
				division.setEntity(divBranch.getEntity());
				division.setParentCluster(
						clusterDAO.getClusterByCode(divBranch.getParentClusterCode(), "", divBranch.getEntity(), ""));
				division.setRecordStatus("");
				division.setLastMntBy(lastUsr);
				division.setLastMntOn(lastMntOn);
				division.setUserDetails(userDetails);

				divList.add(division);
			}
		}
		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	private void accessTypeBranchValidation(AuditDetail ad, SecurityUserDivBranch divBranch) {
		if (StringUtils.isEmpty(divBranch.getEntity())) {
			setError(ad, ERR_90502, "entity");
		}

		if (StringUtils.isEmpty(divBranch.getParentClusterCode())) {
			setError(ad, ERR_90502, "ParentClusterCode");
		}

		if (StringUtils.isEmpty(divBranch.getBranchValues())) {
			setError(ad, ERR_90502, "branches");
		}
	}

	private void accessTypeClusterTypeValidation(AuditDetail ad, SecurityUserDivBranch divBranch) {
		if (StringUtils.isEmpty(divBranch.getEntity())) {
			setError(ad, ERR_90502, "Entitiy");
		}
		if (StringUtils.isEmpty(divBranch.getClusterType())) {
			setError(ad, ERR_90502, "Cluster Type");
		}
		if (StringUtils.isEmpty(divBranch.getClusterValues())) {
			setError(ad, ERR_90502, "Cluster Values");
		}
	}

	private AuditDetail validateDivisionBased(AuditDetail ad, List<SecurityUserDivBranch> list, LoggedInUser details,
			SecurityUserDivBranch branch, Timestamp lastMntOn) {
		logger.debug(Literal.ENTERING);

		AuditDetail returnStatus = new AuditDetail();

		String userBranch = branch.getUserBranch();

		if (StringUtils.isEmpty(userBranch)) {
			setError(ad, ERR_90502, "User Branch");
			return ad;
		}

		String[] branches = userBranch.split(",");
		for (String divBranch : branches) {
			SecurityUserDivBranch division = new SecurityUserDivBranch();
			division.setUsrID(branch.getUsrID());
			division.setUserDivision(branch.getUserDivision());
			division.setUserBranch(divBranch);
			division.setRecordStatus("");
			division.setLastMntBy(details.getUserId());
			division.setLastMntOn(lastMntOn);
			division.setUserDetails(details);
			division.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			division.setNewRecord(true);

			list.add(division);
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	private AuditDetail validateAuthTypeandPassword(AuditDetail ad, SecurityUser user, boolean isUpdate) {
		logger.debug(Literal.ENTERING);

		String authType = user.getAuthType();
		String inType = Labels.getLabel("label_Auth_Type_Internal");
		String extType = Labels.getLabel("label_Auth_Type_External");
		boolean isPwd = false;
		String UsrPwd = user.getUsrPwd();

		if (StringUtils.equals(authType, extType) && StringUtils.isNotBlank(UsrPwd)) {
			setError(ad, ERR_RU0039, "For userType:External UsrPwd");
			return ad;
		}

		if (isUpdate && StringUtils.equals(authType, inType) && StringUtils.isNotBlank(UsrPwd)) {
			user.setAuthType(AuthenticationType.DAO.name());
			setError(ad, ERR_RU0039, "For updateSecurityUser UsrPwd");
			return ad;

		}

		if (!isUpdate && StringUtils.equals(authType, inType)) {
			user.setAuthType(AuthenticationType.DAO.name());
			if (StringUtils.isBlank(user.getUsrPwd())) {
				setError(ad, ERR_90502, "UsrPwd");
				return ad;
			}
			isPwd = checkPasswordCriteria(user.getUsrLogin(), user.getUsrPwd());

			if (isPwd) {
				String[] valueParm = new String[4];
				valueParm[0] = "Password";
				valueParm[1] = "Not Matched with";
				valueParm[2] = "Criteria";
				valueParm[3] = "";
				ErrorDetail errorDetail = new ErrorDetail();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30550", "", valueParm));
				ad.setErrorDetail(errorDetail);
				return ad;
			}
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	public boolean checkPasswordCriteria(String username, String password) {
		logger.debug(Literal.ENTERING);
		boolean inValid = false;
		String pattern = "";
		int pwdMinLenght = SysParamUtil.getValueAsInt("USR_PWD_MIN_LEN");
		int pwdMaxLenght = SysParamUtil.getValueAsInt("USR_PWD_MAX_LEN");
		pattern = PennantRegularExpressions.PASSWORD_PATTERN + ".{" + String.valueOf(pwdMinLenght) + ","
				+ String.valueOf(pwdMaxLenght) + "})";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(password);
		if (!matcher.matches()) {
			return true;
		}
		if (matcher.matches()) {
			for (String part : getSubstrings(username, 3)) {
				if (StringUtils.containsIgnoreCase(password, part)) {
					return true;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return inValid;
	}

	private List<String> getSubstrings(String string, int partitionSize) {
		logger.debug(Literal.ENTERING);
		List<String> partsList = new ArrayList<String>();
		int len = string.length();
		for (int i = 0; i < len; i += 1) {
			String part = string.substring(i, Math.min(len, i + partitionSize));
			if (part.length() == 3) {
				partsList.add(part);
			}
		}
		logger.debug(Literal.LEAVING);
		return partsList;
	}

	@Override
	public AuditHeader updateUserStatus(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		SecurityUser user = new SecurityUser();
		BeanUtils.copyProperties((SecurityUser) ah.getAuditDetail().getModelData(), user);

		SecurityUser befUser = securityUsersDAO.getSecurityUserByLogin(user.getUsrLogin(), "");

		if (!PennantConstants.RECORD_TYPE_NEW.equals(user.getRecordType())) {
			ah.getAuditDetail().setBefImage(befUser);
		}

		user.setRoleCode("");
		user.setNextRoleCode("");
		user.setTaskId("");
		user.setNextTaskId("");
		user.setWorkflowId(0);

		if (user.isUsrEnabled()) {
			user.setDisableReason(null);
		}

		securityUsersDAO.updateUserStatus(user);

		doSaveNotes(user);

		ah.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(ah);

		ah.setAuditTranType("");
		ah.getAuditDetail().setAuditTranType("");
		ah.getAuditDetail().setModelData(user);
		auditHeaderDAO.addAudit(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	private void userNameValidations(AuditDetail ad, SecurityUser user) {
		String usrFName = user.getUsrFName();
		if (StringUtils.isBlank(usrFName)) {
			setError(ad, ERR_90502, "usrFName");
			return;
		} else if (usrFName.length() > 50) {
			setError(ad, ERR_90300, "usrFName", "50");
			return;
		}

		String usrLName = user.getUsrLName();
		if (StringUtils.isBlank(usrLName)) {
			setError(ad, ERR_90502, "usrLName");
		} else if (usrLName.length() > 50) {
			setError(ad, ERR_90300, "usrLName", "50");
		}
	}

	private void userStaffIDValidation(AuditDetail ad, SecurityUser user) {
		if (StringUtils.isBlank(user.getUserStaffID())) {
			setError(ad, ERR_90502, "userStaffID");
			return;
		}

		if (user.getUsrLanguage().length() > 10) {
			setError(ad, ERR_90300, "userStaffID", "10");
		}
	}

	private void languageValidations(AuditDetail ad, SecurityUser user) {
		String usrLanguage = user.getUsrLanguage();
		String column = "usrLanguage";

		if (!languageDAO.isLanguageValid(usrLanguage)) {
			setError(ad, ERR_93304, column);
		} else if (StringUtils.isBlank(usrLanguage)) {
			setError(ad, ERR_90502, column);
		} else if (usrLanguage.length() > 4) {
			setError(ad, ERR_90300, column, "4");
		}
	}

	private void userDeptValidations(AuditDetail ad, SecurityUser user) {
		String usrDeptCode = user.getUsrDeptCode();
		String column = "UsrDeptCode";
		if (!departmentDAO.isDeptValid(usrDeptCode)) {
			setError(ad, ERR_93304, column);
		} else if (StringUtils.isBlank(usrDeptCode)) {
			setError(ad, ERR_90502, column);
		} else if (usrDeptCode.length() > 8) {
			setError(ad, ERR_90300, column, "8");
		}
	}

	private void userDesignationValidation(AuditDetail ad, SecurityUser user) {
		String usrDesg = user.getUsrDesg();

		if (!designationDAO.isDesignationValid(usrDesg)) {
			setError(ad, ERR_93304, "Designation");
		} else if (StringUtils.isBlank(usrDesg)) {
			setError(ad, ERR_90502, "UsrDesg");
		} else if (usrDesg.length() > 50) {
			setError(ad, ERR_90300, "UsrDesg", "50");
		}
	}

	private void userBranchValidation(AuditDetail ad, SecurityUser user) {
		String branchCode = user.getUsrBranchCode();
		if (!branchDAO.isActiveBranch(branchCode)) {
			setError(ad, ERR_93304, "Branch");
		} else if (StringUtils.isBlank(branchCode)) {
			setError(ad, ERR_90502, "UsrBranchCode");
		}
	}

	private void userDisableValidation(AuditDetail ad, SecurityUser user) {
		List<String> reasons = securityUsersDAO.getLovFieldCodeValues(LookUpCode.SU_DISABLE_REASON);
		String disableReason = user.getDisableReason();
		boolean anyMatch = false;

		if (CollectionUtils.isNotEmpty(reasons)) {
			if (disableReason != null) {
				anyMatch = reasons.stream().anyMatch(reason -> reason.contentEquals(disableReason));
			}

			if (StringUtils.isBlank(disableReason)) {
				setError(ad, ERR_90502, "disable reason");
			} else if (!anyMatch) {
				setError(ad, ERR_93304, "disable reason");
			}
		} else if (user.getDisableReason() == null) {
			user.setDisableReason(PennantConstants.List_Select);
		} else if (user.getDisableReason() != null) {
			setError(ad, ERR_90010, "disable reason");
		}
	}

	private void userEmployeeTypeValidation(AuditDetail ad, SecurityUser user) {
		List<String> employees = securityUsersDAO.getLovFieldCodeValues(LookUpCode.SU_EMP_TYPE);
		String employeeType = user.getEmployeeType();
		boolean anyMatch = false;

		if (CollectionUtils.isNotEmpty(employees)) {
			if (employeeType != null) {
				anyMatch = employees.stream().anyMatch(employee -> employee.contentEquals(employeeType));
			}

			if (StringUtils.isBlank(employeeType)) {
				setError(ad, ERR_90502, "Employee Type");
				return;
			} else if (!anyMatch) {
				setError(ad, ERR_93304, "Employee Type");
				return;
			}
		} else if (user.getEmployeeType() == null) {
			user.setEmployeeType(PennantConstants.List_Select);
		} else if (user.getEmployeeType() != null) {
			setError(ad, ERR_90010, "Employee Type");
		}
	}

	private void doSaveNotes(SecurityUser user) {
		Notes notes = new Notes();

		notes.setModuleName("SecurityUsers");
		notes.setReference(String.valueOf(user.getUsrID()));
		notes.setVersion(user.getVersion());
		notes.setRemarks(user.getReason());
		notes.setInputBy(user.getLastMntBy());
		notes.setInputDate(user.getLastMntOn());
		notes.setRemarkType("N");
		notes.setAlignType("R");
		notes.setRoleCode(user.getRoleCode());

		notesDAO.save(notes);
	}

	public DivisionDetailDAO getDivisionDetailDAO() {
		return divisionDetailDAO;
	}

	public void setDivisionDetailDAO(DivisionDetailDAO divisionDetailDAO) {
		this.divisionDetailDAO = divisionDetailDAO;
	}

	public EntityDAO getEntityDAO() {
		return entityDAO;
	}

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	public LanguageDAO getLanguageDAO() {
		return languageDAO;
	}

	public void setLanguageDAO(LanguageDAO languageDAO) {
		this.languageDAO = languageDAO;
	}

	public DesignationDAO getDesignationDAO() {
		return designationDAO;
	}

	public void setDesignationDAO(DesignationDAO designationDAO) {
		this.designationDAO = designationDAO;
	}

	public DepartmentDAO getDepartmentDAO() {
		return departmentDAO;
	}

	public void setDepartmentDAO(DepartmentDAO departmentDAO) {
		this.departmentDAO = departmentDAO;
	}

	public BranchDAO getBranchDAO() {
		return branchDAO;
	}

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
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

	public ClusterDAO getClusterDAO() {
		return clusterDAO;
	}

	public void setClusterDAO(ClusterDAO clusterDAO) {
		this.clusterDAO = clusterDAO;
	}

	public NotesDAO getNotesDAO() {
		return notesDAO;
	}

	public void setNotesDAO(NotesDAO notesDAO) {
		this.notesDAO = notesDAO;
	}

}