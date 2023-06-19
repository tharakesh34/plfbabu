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

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.dao.QueueAssignmentDAO;
import com.pennant.backend.dao.administration.SecurityOperationDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.dao.administration.SecurityUserPasswordsDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.ClusterDAO;
import com.pennant.backend.dao.applicationmaster.ClusterHierarchyDAO;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.applicationmaster.ReportingManagerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.dao.staticparms.LanguageDAO;
import com.pennant.backend.dao.systemmasters.DepartmentDAO;
import com.pennant.backend.dao.systemmasters.DesignationDAO;
import com.pennant.backend.dao.systemmasters.DivisionDetailDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.ReportingManager;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.administration.SecurityUserOperations;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityUserAccessService;
import com.pennant.backend.service.administration.SecurityUserHierarchyService;
import com.pennant.backend.service.administration.SecurityUserOperationsService;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.constant.LookUpCode;
import com.pennanttech.pennapps.core.App.AuthenticationType;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.lic.License;
import com.pennanttech.pennapps.lic.exception.LicenseException;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.notifications.service.NotificationService;

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
	private static final String ERR_92021 = "92021";

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
	private ClusterHierarchyDAO clusterHierarchyDAO;
	private SecurityOperationDAO securityOperationDAO;
	private SecurityUserOperationsService securityUserOperationsService;
	private MailTemplateDAO mailTemplateDAO;
	private NotificationService notificationService;
	private EmailEngine emailEngine;

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
				securityUserPasswordsDAO.save(securityUser);
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

		auditHeaderDAO.addAudit(auditHeader);

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

		auditHeaderDAO.addAudit(auditHeader);
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
			SecurityUserDivBranch recordExist = securityUsersDAO.getSecUserDivBrDetailsById(securityUserDivBranch,
					type);
			if (saveRecord) {
				if (recordExist == null) {
					securityUsersDAO.saveDivBranchDetails(securityUserDivBranch, type);
				}
			}

			if (updateRecord) {
				if (recordExist != null) {
					securityUsersDAO.updateDivBranchDetails(securityUserDivBranch, type);
				}
			}

			if (deleteRecord) {
				if (recordExist != null) {
					securityUsersDAO.deleteDivBranchDetails(securityUserDivBranch, type);
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
				if (RequestSource.API.equals(securityUser.getRequestSource())
						&& AuthenticationType.DAO.name().equals(securityUser.getAuthType())) {
					securityUserPasswordsDAO.save(securityUser);
				}
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

		if (RequestSource.API.equals(securityUser.getRequestSource())
				&& CollectionUtils.isNotEmpty(securityUser.getSecurityUserOperationsList())) {
			saveUserOperations(securityUser.getUsrID(), auditHeader);
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(securityUser);
		auditHeaderDAO.addAudit(auditHeader);

		// If any records exists in this user queue re-assign them to next available users
		if (!securityUser.isUsrEnabled()) {
			queueAssignmentDAO.executeStoredProcedure(securityUser.getUsrID());
		}

		if (!securityUser.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(securityUser.getRecordType())) {
			return auditHeader;
		}

		// boolean sendCounterSign = App.getBooleanProperty("user.create.notify.pwd");

		// if (!sendCounterSign) {
		// return auditHeader;
		// }

		MailTemplate mailTemplate = mailTemplateDAO.getTemplateByCode("SECURITY_USER_NOTIFY_PWD");

		if (mailTemplate == null) {
			return auditHeader;
		}

		if (securityUser.getUsrEmail() != null && mailTemplate.isEmailTemplate()) {

			try {
				Map<String, Object> map = new HashMap<>();

				map.put("user_name", securityUser.getUsrLogin());
				map.put("su_pwd", securityUser.getUsrRawPwd());

				notificationService.parseMail(mailTemplate, map);

				Notification emailMessage = new Notification();
				emailMessage.setKeyReference(securityUser.getUsrLogin());
				emailMessage.setModule("SECURITY_USER");
				emailMessage.setSubModule("SECURITY_USER");
				emailMessage.setSubject(mailTemplate.getEmailSubject());
				emailMessage.setContent(mailTemplate.getEmailMessage().getBytes(StandardCharsets.UTF_8));

				if (NotificationConstants.TEMPLATE_FORMAT_HTML.equals(mailTemplate.getEmailFormat())) {
					emailMessage.setContentType(EmailBodyType.HTML.getKey());
				} else {
					emailMessage.setContentType(EmailBodyType.PLAIN.getKey());
				}

				MessageAddress address = new MessageAddress();
				address.setEmailId(securityUser.getUsrEmail());
				address.setRecipientType(RecipientType.TO.getKey());
				emailMessage.getAddressesList().add(address);

				try {
					emailEngine.sendEmail(emailMessage);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					throw new AppException("Unable to save the email notification", e);
				}

			} catch (Exception e) {
				throw new AppException("SecurityUserServiceImpl", e);
			}
		}

		logger.debug("Leaving ");
		return auditHeader;
	}

	private void saveUserOperations(long usrID, AuditHeader auditHeader) {
		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		List<SecurityUserOperations> operations = securityUser.getSecurityUserOperationsList();
		List<SecurityUserOperations> secOperations = new ArrayList<>();
		SecurityUserOperations securityUserOperation;

		for (SecurityUserOperations op : operations) {
			long oprID = securityOperationDAO.getSecurityOperationByCode(op.getLovDescOprCd());

			if (oprID == 0) {
				continue;
			}

			securityUserOperation = new SecurityUserOperations();

			securityUserOperation.setUsrID(usrID);
			securityUserOperation.setOprID(oprID);
			if (securityUserOperation.getId() == Long.MIN_VALUE) {
				securityUserOperation.setId(securityUserOperationsDAO.getNextValue());
			}

			switch (op.getMode()) {
			case PennantConstants.RCD_ADD:
				securityUserOperation.setRecordType(PennantConstants.RCD_ADD);
				break;
			case PennantConstants.RCD_EDT:
				securityUserOperation.setRecordType(PennantConstants.RCD_ADD);
				break;
			case PennantConstants.RCD_DEL:
				securityUserOperation.setRecordType(PennantConstants.RCD_DEL);
				break;
			default:
				securityUserOperation.setRecordType(PennantConstants.RCD_ADD);
				break;
			}

			secOperations.add(securityUserOperation);
		}

		securityUser.setSecurityUserOperationsList(secOperations);

		AuditHeader ah = getAuditHeader(securityUser, "");

		securityUserOperationsService.doApprove(ah);

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

		auditHeaderDAO.addAudit(auditHeader);
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
			int roleIdCount = securityUserOperationsDAO.getUserIdCount(securityUser.getUsrID());
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
			securityUserPasswordsDAO.save(securityUser);
		} else {
			// As the administrator changes, set expire date so that system will force the user to change on his/her
			// next login.
			securityUser.setPwdExpDt(DateUtil.addDays(new Date(System.currentTimeMillis()), -1));
		}

		securityUsersDAO.changePassword(securityUser);
		auditHeaderDAO.addAudit(auditHeader);

		logger.trace(Literal.LEAVING);
		return auditHeader;
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
					securityUserOperationsDAO.getSecUserOperationsByUsrID(securityUser, type));
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
			// securityUserDAO.deleteBranchs(securityUser, tableType);
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
			logger.debug(Literal.LEAVING);
			return ad;
		}

		if (StringUtils.isBlank(user.getUsrLogin())) {
			setError(ad, ERR_90502, "usrLogin");
			logger.debug(Literal.LEAVING);
			return ad;
		}

		List<ValueLabel> authTypesList = PennantStaticListUtil.getAuthnticationTypes();

		if (StringUtils.isNotBlank(user.getUserType())) {
			if (!authTypesList.stream().anyMatch(m -> m.getLabel().equalsIgnoreCase(user.getAuthType()))) {
				setError(ad, ERR_90337, "userType", "Internal/External");
				logger.debug(Literal.LEAVING);
				return ad;
			}
		} else {
			user.setUserType(Labels.getLabel("label_Auth_Type_Internal"));
		}

		if (!user.isNotifyUser() && StringUtils.isEmpty(user.getUsrEmail())) {
			setError(ad, ERR_RU0039, "User Email");
			logger.debug(Literal.LEAVING);
			return ad;
		}

		validateAuthTypeandPassword(ad, user, isUpdate);

		userNameValidations(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			logger.debug(Literal.LEAVING);
			return ad;
		}

		userStaffIDValidation(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			logger.debug(Literal.LEAVING);
			return ad;
		}

		languageValidations(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			logger.debug(Literal.LEAVING);
			return ad;
		}

		userDeptValidations(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			logger.debug(Literal.LEAVING);
			return ad;
		}

		userDesignationValidation(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			logger.debug(Literal.LEAVING);
			return ad;
		}

		if (user.getUsrMobile() != null && user.getUsrMobile().length() > 13) {
			setError(ad, ERR_90300, "UsrMobile", "13");
			logger.debug(Literal.LEAVING);
			return ad;
		}

		if (user.getUsrEmail() != null && user.getUsrEmail().length() > 50) {
			setError(ad, ERR_90300, "UsrMobile", "50");
			logger.debug(Literal.LEAVING);
			return ad;
		}

		if (!user.isUsrEnabled()) {
			userDisableValidation(ad, user);
		} else if (user.getDisableReason() != null) {
			setError(ad, ERR_RU0040, "disableReason");
			logger.debug(Literal.LEAVING);
			return ad;
		}

		userEmployeeTypeValidation(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			logger.debug(Literal.LEAVING);
			return ad;
		}

		AuditDetail returnStatus = validateDivisions(ad, user, isAllowCluster, logUsrDtls);

		if (CollectionUtils.isNotEmpty(returnStatus.getErrorDetails())) {
			logger.debug(Literal.LEAVING);
			return returnStatus;
		}
		if (StringUtils.isEmpty(user.getUsrBranchCode())
				&& CollectionUtils.isNotEmpty(user.getSecurityUserDivBranchList())) {
			setUserBranch(user);

			if (StringUtils.isBlank(user.getUsrBranchCode())) {
				setError(ad, "92021", "No branch is configured with the provided division branch list");
				logger.debug(Literal.LEAVING);
				return ad;
			}
		}

		userBranchValidation(ad, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			return ad;
		}

		ErrorDetail error = validateUserOperations(user);

		if (error != null) {
			ad.getErrorDetails().add(error);
			logger.debug(Literal.LEAVING);
			return ad;
		}

		List<SecurityUserOperations> secUserOperations = user.getSecurityUserOperationsList();
		validateOperationMode(isUpdate, ad, secUserOperations, user);

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			logger.debug(Literal.LEAVING);
			return ad;
		}

		logger.debug(Literal.LEAVING);
		return ad;
	}

	private void setUserBranch(SecurityUser user) {
		String entity = null;
		List<String> branches;
		for (SecurityUserDivBranch divBranch : user.getSecurityUserDivBranchList()) {
			switch (divBranch.getAccessType()) {
			case PennantConstants.ACCESSTYPE_ENTITY:
				entity = divBranch.getEntity();
				branches = branchDAO.getBranchCodesByEntity(entity);
				if (CollectionUtils.isEmpty(branches)) {
					continue;
				}
				user.setUsrBranchCode(branches.get(0));
				break;
			case PennantConstants.ACCESSTYPE_CLUSTER:
				entity = divBranch.getEntity();
				branches = branchDAO.getBranchCodesByClusterID(entity, divBranch.getClusterId());
				if (CollectionUtils.isEmpty(branches)) {
					continue;
				}
				user.setUsrBranchCode(branches.get(0));
				break;
			case PennantConstants.ACCESSTYPE_BRANCH:
				user.setUsrBranchCode(divBranch.getUserBranch());
				break;
			default:
				break;
			}

			if (StringUtils.isNotBlank(user.getUsrBranchCode())) {
				break;
			}

		}

	}

	private void validateOperationMode(boolean isUpdate, AuditDetail ad, List<SecurityUserOperations> secUserOperations,
			SecurityUser user) {
		if (!isUpdate) {
			for (SecurityUserOperations userOp : secUserOperations) {
				if (StringUtils.isBlank(userOp.getMode())) {
					userOp.setMode(PennantConstants.RCD_ADD);
				} else if (!("ADD".equals(userOp.getMode()))) {
					setError(ad, "92021", "Allowed Values for Mode during user creation is ADD");
					return;
				}
			}
		}

		if (CollectionUtils.isEmpty(secUserOperations) || !isUpdate) {
			return;
		}

		for (SecurityUserOperations userOp : secUserOperations) {
			if (StringUtils.isBlank(userOp.getMode())) {
				setError(ad, "90502", "Mode");
				return;
			}

			if (!("ADD".equals(userOp.getMode()) || "EDIT".equals(userOp.getMode())
					|| "DELETE".equals(userOp.getMode()))) {
				setError(ad, "92021", "Allowed Values for Mode are ADD, EDIT and DELETE");
			}
		}

		List<SecurityUserOperations> editOperations = secUserOperations.stream()
				.filter(op -> "EDIT".equals(op.getMode())).toList();

		if (editOperations.size() > 1) {
			setError(ad, "92021", "Multiple EDIT operations are not allowed");
			return;
		}

		if (editOperations.size() == 0) {
			return;
		}

		List<SecurityUserOperations> operations = securityUserOperationsDAO.getSecUserOperationsByUsrID(user, "");

		if (CollectionUtils.isEmpty(operations)) {
			setError(ad, "92021", "No operation are configured with the user to perform edit operations");
			return;
		}

		if (operations.size() != 1) {
			setError(ad, "92021", "Multiple EDIT operations are not allowed");
			return;
		}

		SecurityUserOperations op = operations.get(0);
		SecurityOperation secOp = securityOperationDAO.getSecurityOperationById(op.getOprID(), "");
		op.setLovDescOprCd(secOp.getOprCode());
		op.setMode("DELETE");

		secUserOperations.add(op);

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

	private ErrorDetail validateUserOperations(SecurityUser user) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(user.getSecurityUserOperationsList())) {
			return null;
		}

		for (SecurityUserOperations suo : user.getSecurityUserOperationsList()) {
			if (!this.securityOperationDAO.isOperationExistByOprCode(suo.getLovDescOprCd())) {
				return getError("93304", "Operation");
			}

			if (user.getUsrID() > 0 && PennantConstants.RCD_ADD.equals(suo.getMode())
					&& securityUserOperationsDAO.isOpertionExists(suo.getLovDescOprCd(), user.getUsrID())) {
				return getError("92021", "Operation already exists for the given User");
			}

			if (PennantConstants.RCD_DEL.equals(suo.getMode())
					&& !securityUserOperationsDAO.isOpertionExists(suo.getLovDescOprCd(), user.getUsrID())) {
				return getError("92021", "operation is not configured for user to delete");
			}
		}

		logger.debug(Literal.LEAVING);
		return null;
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

		switch (divBranch.getAccessType()) {
		case PennantConstants.ACCESSTYPE_ENTITY:
			accessTypeEntityValidation(ad, divBranch);
			entities = true;
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

		if (CollectionUtils.isNotEmpty(ad.getErrorDetails())) {
			return ad;
		}

		long lastUsr = userDetails.getUserId();
		long currUsr = divBranch.getUsrID();
		String accessType = divBranch.getAccessType();
		String userBranch = divBranch.getUserDivision();

		if (entities) {
			for (Entity entity : divBranch.getEntitiyList()) {
				SecurityUserDivBranch division = new SecurityUserDivBranch();
				division.setAccessType(accessType);
				division.setUsrID(currUsr);
				division.setUserDivision(userBranch);
				division.setEntity(entity.getEntityCode());
				division.setRecordStatus("");
				division.setLastMntBy(lastUsr);
				division.setLastMntOn(lastMntOn);
				division.setUserDetails(userDetails);

				divList.add(division);
			}

		}
		if (clusters) {
			for (Cluster cluster : divBranch.getClusterList()) {
				SecurityUserDivBranch division = new SecurityUserDivBranch();
				division.setAccessType(accessType);
				division.setUsrID(currUsr);
				division.setUserDivision(userBranch);

				division.setEntity(divBranch.getEntity());
				division.setClusterType(divBranch.getClusterType());
				division.setClusterId(clusterDAO.getClusterByCode(cluster.getCode(), divBranch.getClusterType(),
						divBranch.getEntity(), ""));
				division.setRecordStatus("");
				division.setLastMntBy(lastUsr);
				division.setLastMntOn(lastMntOn);
				division.setUserDetails(userDetails);

				divList.add(division);
			}
		}

		if (branches) {
			for (Branch branch : divBranch.getBranchList()) {
				SecurityUserDivBranch division = new SecurityUserDivBranch();
				division.setAccessType(accessType);
				division.setUsrID(currUsr);
				division.setUserDivision(userBranch);
				division.setUserBranch(branch.getBranchCode());
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

	private void accessTypeEntityValidation(AuditDetail ad, SecurityUserDivBranch divBranch) {
		List<Entity> entities = divBranch.getEntitiyList();

		if (CollectionUtils.isEmpty(entities)) {
			setError(ad, ERR_90502, "Entities");
			return;
		}

		List<String> entityCodes = entities.stream().map(Entity::getEntityCode).collect(Collectors.toList());

		for (String entity : entityCodes) {
			if (entityDAO.getEntityCount(entity) == 0) {
				setError(ad, ERR_93304, "Entities");
				return;
			}
		}

		for (String entity : entityCodes) {
			if (!divisionDetailDAO.isValidEntityCode(divBranch.getUserDivision(), entity)) {
				setError(ad, ERR_93304, "Entities");
				return;
			}
		}
	}

	private void accessTypeBranchValidation(AuditDetail ad, SecurityUserDivBranch divBranch) {
		String entityCode = divBranch.getEntity();
		String parentClusterCode = divBranch.getParentClusterCode();
		List<Branch> branches = divBranch.getBranchList();

		if (StringUtils.isEmpty(entityCode)) {
			setError(ad, ERR_90502, "entity");
			return;
		}

		if (StringUtils.isEmpty(parentClusterCode)) {
			setError(ad, ERR_90502, "ParentClusterCode");
			return;
		}

		if (CollectionUtils.isEmpty(branches)) {
			setError(ad, ERR_90502, "branches");
			return;
		}

		if (!divisionDetailDAO.isValidEntityCode(divBranch.getUserDivision(), entityCode)) {
			setError(ad, ERR_93304, "Entities");
			return;
		}

		if (!clusterDAO.isValidClusterCode(parentClusterCode, entityCode)) {
			setError(ad, ERR_93304, "Entities");
			return;
		}

		List<String> branchCodes = branchDAO.getBranchCodes(entityCode, parentClusterCode);

		if (CollectionUtils.isEmpty(branchCodes)) {
			setError(ad, ERR_93304, "Cluster Code");
			return;
		}

		List<String> branchCodesList = branches.stream().map(Branch::getBranchCode).collect(Collectors.toList());

		int size = branchCodesList.stream().filter(branchCodes::contains).collect(Collectors.toList()).size();

		if (size != branches.size()) {
			setError(ad, ERR_93304, "Branches");
			return;
		}

	}

	private void accessTypeClusterTypeValidation(AuditDetail ad, SecurityUserDivBranch divBranch) {
		String entityCode = divBranch.getEntity();
		String clusterType = divBranch.getClusterType();
		String userDivision = divBranch.getUserDivision();
		List<Cluster> clusters = divBranch.getClusterList();

		if (StringUtils.isEmpty(entityCode)) {
			setError(ad, ERR_90502, "Entitiy");
		}

		if (StringUtils.isEmpty(clusterType)) {
			setError(ad, ERR_90502, "Cluster Type");
			return;
		}

		if (CollectionUtils.isEmpty(clusters)) {
			setError(ad, ERR_90502, "Cluster Values");
			return;
		}

		if (!divisionDetailDAO.isValidEntityCode(userDivision, entityCode)) {
			setError(ad, ERR_93304, "Entities");
			return;
		}

		if (!clusterHierarchyDAO.isClusterTypeExists(clusterType, entityCode)) {
			setError(ad, ERR_93304, "Entities");
			return;
		}

		List<String> clusterCodes = clusterDAO.getClusterCodes(clusterType, entityCode);
		List<String> clusterCodesList = clusters.stream().map(Cluster::getCode).collect(Collectors.toList());

		int size = clusterCodesList.stream().filter(clusterCodes::contains).collect(Collectors.toList()).size();

		if (size != clusters.size()) {
			setError(ad, ERR_93304, "Clusters");
			return;
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
			if (!StringUtils.isBlank(UsrPwd)) {
				if (StringUtils.isBlank(user.getConfirmPassword())) {
					setError(ad, ERR_90502, "User Branch");
					return ad;
				}
				if (!UsrPwd.equals(user.getConfirmPassword())) {
					setError(ad, ERR_92021, "Password and Confirm Password should match");
					return ad;
				}
				isPwd = checkPasswordCriteria(user.getUsrLogin(), user.getUsrPwd());
			} else {
				user.setUsrPwd(RandomStringUtils.random(8, true, true));
			}

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
			return;
		} else if (usrLName.length() > 50) {
			setError(ad, ERR_90300, "usrLName", "50");
			return;
		}
	}

	private void userStaffIDValidation(AuditDetail ad, SecurityUser user) {
		if (StringUtils.isBlank(user.getUserStaffID())) {
			setError(ad, ERR_90502, "userStaffID");
			return;
		}

		if (user.getUsrLanguage().length() > 10) {
			setError(ad, ERR_90300, "userStaffID", "10");
			return;
		}
	}

	private void languageValidations(AuditDetail ad, SecurityUser user) {
		String usrLanguage = user.getUsrLanguage();
		String column = "usrLanguage";

		if (StringUtils.isBlank(usrLanguage)) {
			user.setUsrLanguage(PennantConstants.default_Language);
		}
		if (usrLanguage.length() > 4) {
			setError(ad, ERR_90300, column, "4");
			return;
		} else if (!languageDAO.isLanguageValid(usrLanguage)) {
			setError(ad, ERR_93304, column);
			return;
		}
	}

	private void userDeptValidations(AuditDetail ad, SecurityUser user) {
		String usrDeptCode = user.getUsrDeptCode();
		String column = "UsrDeptCode";
		if (StringUtils.isBlank(usrDeptCode)) {
			setError(ad, ERR_90502, column);
			return;
		} else if (usrDeptCode.length() > 8) {
			setError(ad, ERR_90300, column, "8");
			return;
		} else if (!departmentDAO.isDeptValid(usrDeptCode)) {
			setError(ad, ERR_93304, column);
			return;
		}
	}

	private void userDesignationValidation(AuditDetail ad, SecurityUser user) {
		String usrDesg = user.getUsrDesg();
		if (StringUtils.isBlank(usrDesg)) {
			setError(ad, ERR_90502, "UsrDesg");
			return;
		} else if (usrDesg.length() > 50) {
			setError(ad, ERR_90300, "UsrDesg", "50");
			return;
		} else if (!designationDAO.isDesignationValid(usrDesg)) {
			setError(ad, ERR_93304, "Designation");
			return;
		}
	}

	private void userBranchValidation(AuditDetail ad, SecurityUser user) {
		String branchCode = user.getUsrBranchCode();
		if (StringUtils.isBlank(branchCode)) {
			setError(ad, ERR_90502, "UsrBranchCode");
			return;
		} else if (!branchDAO.isActiveBranch(branchCode)) {
			setError(ad, ERR_93304, "Branch");
			return;
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

	private AuditHeader getAuditHeader(SecurityUser SecurityUser, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, SecurityUser.getBefImage(), SecurityUser);
		return new AuditHeader(String.valueOf(SecurityUser.getUsrID()), null, null, null, auditDetail,
				SecurityUser.getUserDetails(), null);
	}

	protected ErrorDetail getError(String errorCode, String... parms) {
		return ErrorUtil.getError(errorCode, parms);
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setSecurityUsersDAO(SecurityUserDAO securityUsersDAO) {
		this.securityUsersDAO = securityUsersDAO;
	}

	@Autowired
	public void setSecurityUserAccessService(SecurityUserAccessService securityUserAccessService) {
		this.securityUserAccessService = securityUserAccessService;
	}

	@Autowired
	public void setSecurityUserHierarchyService(SecurityUserHierarchyService securityUserHierarchyService) {
		this.securityUserHierarchyService = securityUserHierarchyService;
	}

	@Autowired
	public void setSecurityUserPasswordsDAO(SecurityUserPasswordsDAO securityUserPasswordsDAO) {
		this.securityUserPasswordsDAO = securityUserPasswordsDAO;
	}

	@Autowired
	public void setQueueAssignmentDAO(QueueAssignmentDAO queueAssignmentDAO) {
		this.queueAssignmentDAO = queueAssignmentDAO;
	}

	@Autowired
	public void setSecurityUserOperationsDAO(SecurityUserOperationsDAO securityUserOperationsDAO) {
		this.securityUserOperationsDAO = securityUserOperationsDAO;
	}

	@Autowired
	public void setReportingManagerDAO(ReportingManagerDAO reportingManagerDAO) {
		this.reportingManagerDAO = reportingManagerDAO;
	}

	@Autowired
	public void setClusterDAO(ClusterDAO clusterDAO) {
		this.clusterDAO = clusterDAO;
	}

	@Autowired
	public void setDivisionDetailDAO(DivisionDetailDAO divisionDetailDAO) {
		this.divisionDetailDAO = divisionDetailDAO;
	}

	@Autowired
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@Autowired
	public void setLanguageDAO(LanguageDAO languageDAO) {
		this.languageDAO = languageDAO;
	}

	@Autowired
	public void setDesignationDAO(DesignationDAO designationDAO) {
		this.designationDAO = designationDAO;
	}

	@Autowired
	public void setDepartmentDAO(DepartmentDAO departmentDAO) {
		this.departmentDAO = departmentDAO;
	}

	@Autowired
	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	@Autowired
	public void setNotesDAO(NotesDAO notesDAO) {
		this.notesDAO = notesDAO;
	}

	@Autowired
	public void setClusterHierarchyDAO(ClusterHierarchyDAO clusterHierarchyDAO) {
		this.clusterHierarchyDAO = clusterHierarchyDAO;
	}

	@Autowired
	public void setSecurityOperationDAO(SecurityOperationDAO securityOperationDAO) {
		this.securityOperationDAO = securityOperationDAO;
	}

	@Autowired
	public void setSecurityUserOperationsService(SecurityUserOperationsService securityUserOperationsService) {
		this.securityUserOperationsService = securityUserOperationsService;
	}

	@Autowired
	public void setMailTemplateDAO(MailTemplateDAO mailTemplateDAO) {
		this.mailTemplateDAO = mailTemplateDAO;
	}

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Autowired
	public void setEmailEngine(EmailEngine emailEngine) {
		this.emailEngine = emailEngine;
	}

}