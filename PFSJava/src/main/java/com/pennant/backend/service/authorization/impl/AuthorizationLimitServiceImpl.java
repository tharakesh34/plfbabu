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
 * * FileName : AuthorizationLimitServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-04-2018 * *
 * Modified Date : 06-04-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-04-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.authorization.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.authorization.AuthorizationLimitDAO;
import com.pennant.backend.dao.authorization.AuthorizationLimitDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.authorization.AuthorizationLimit;
import com.pennant.backend.model.authorization.AuthorizationLimitDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.authorization.AuthorizationLimitService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>AuthorizationLimit</b>.<br>
 */
public class AuthorizationLimitServiceImpl extends GenericService<AuthorizationLimit>
		implements AuthorizationLimitService {
	private static final Logger logger = LogManager.getLogger(AuthorizationLimitServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AuthorizationLimitDAO authorizationLimitDAO;
	private AuthorizationLimitDetailDAO authorizationLimitDetailDAO;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the authorizationLimitDAO
	 */
	public AuthorizationLimitDAO getAuthorizationLimitDAO() {
		return authorizationLimitDAO;
	}

	/**
	 * @param authorizationLimitDAO the authorizationLimitDAO to set
	 */
	public void setAuthorizationLimitDAO(AuthorizationLimitDAO authorizationLimitDAO) {
		this.authorizationLimitDAO = authorizationLimitDAO;
	}

	/**
	 * @return the authorizationLimitDetailDAO
	 */
	public AuthorizationLimitDetailDAO getAuthorizationLimitDetailDAO() {
		return authorizationLimitDetailDAO;
	}

	/**
	 * @param authorizationLimitDetailDAO the authorizationLimitDetailDAO to set
	 */
	public void setAuthorizationLimitDetailDAO(AuthorizationLimitDetailDAO authorizationLimitDetailDAO) {
		this.authorizationLimitDetailDAO = authorizationLimitDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Auth_Limits/Auth_Limits_Temp by
	 * using Auth_LimitsDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using Auth_LimitsDAO's update method 3) Audit the record in to AuditHeader and AdtAuth_Limits by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean hold) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AuthorizationLimit authorizationLimit = (AuthorizationLimit) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (authorizationLimit.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (authorizationLimit.isNewRecord()) {
			if (hold) {
				getAuthorizationLimitDAO().saveHold(authorizationLimit);
			} else {
				authorizationLimit
						.setId(Long.parseLong(getAuthorizationLimitDAO().save(authorizationLimit, tableType)));
			}
			auditHeader.getAuditDetail().setModelData(authorizationLimit);
			auditHeader.setAuditReference(String.valueOf(authorizationLimit.getId()));
		} else {
			if (hold) {
				getAuthorizationLimitDAO().updateHold(authorizationLimit, tableType);
			} else {
				getAuthorizationLimitDAO().update(authorizationLimit, tableType);
			}
		}

		if (!hold) {
			auditHeader.setAuditDetails(
					processingList(auditHeader.getAuditDetails(), authorizationLimit.getId(), tableType));
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Auth_Limits by using Auth_LimitsDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtAuth_Limits by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AuthorizationLimit authorizationLimit = (AuthorizationLimit) auditHeader.getAuditDetail().getModelData();
		getAuthorizationLimitDAO().delete(authorizationLimit, TableType.MAIN_TAB);
		getAuthorizationLimitDetailDAO().deleteByAuthLimitId(authorizationLimit.getId(), TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getAuth_Limits fetch the details by using Auth_LimitsDAO's getAuth_LimitsById method.
	 * 
	 * @param id id of the AuthorizationLimit.
	 * @return Auth_Limits
	 */
	@Override
	public AuthorizationLimit getAuthorizationLimit(long id) {
		return getAuthorizationLimit(id, "_View");
	}

	/**
	 * getApprovedAuth_LimitsById fetch the details by using Auth_LimitsDAO's getAuth_LimitsById method . with parameter
	 * id and type as blank. it fetches the approved records from the Auth_Limits.
	 * 
	 * @param id id of the AuthorizationLimit. (String)
	 * @return Auth_Limits
	 */
	public AuthorizationLimit getApprovedAuthorizationLimit(long id) {
		return getAuthorizationLimit(id, "_AView");
	}

	private AuthorizationLimit getAuthorizationLimit(long id, String type) {

		AuthorizationLimit authorizationLimit = getAuthorizationLimitDAO().getAuthorizationLimit(id, type);
		if (authorizationLimit != null) {
			authorizationLimit
					.setAuthorizationLimitDetails(getAuthorizationLimitDetailDAO().getListByAuthLimitId(id, type));
		}

		return authorizationLimit;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getAuthorizationLimitDAO().delete with
	 * parameters authorizationLimit,"" b) NEW Add new record in to main table by using getAuthorizationLimitDAO().save
	 * with parameters authorizationLimit,"" c) EDIT Update record in the main table by using
	 * getAuthorizationLimitDAO().update with parameters authorizationLimit,"" 3) Delete the record from the workFlow
	 * table by using getAuthorizationLimitDAO().delete with parameters authorizationLimit,"_Temp" 4) Audit the record
	 * in to AuditHeader and AdtAuth_Limits by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtAuth_Limits by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader, boolean hold) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AuthorizationLimit authorizationLimit = new AuthorizationLimit();
		BeanUtils.copyProperties((AuthorizationLimit) auditHeader.getAuditDetail().getModelData(), authorizationLimit);

		getAuthorizationLimitDAO().delete(authorizationLimit, TableType.TEMP_TAB);
		getAuthorizationLimitDetailDAO().deleteByAuthLimitId(authorizationLimit.getId(), TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(authorizationLimit.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(authorizationLimitDAO.getAuthorizationLimit(authorizationLimit.getId(), ""));
		}

		if (authorizationLimit.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getAuthorizationLimitDAO().delete(authorizationLimit, TableType.MAIN_TAB);
			getAuthorizationLimitDetailDAO().deleteByAuthLimitId(authorizationLimit.getId(), TableType.MAIN_TAB);
		} else {
			authorizationLimit.setRoleCode("");
			authorizationLimit.setNextRoleCode("");
			authorizationLimit.setTaskId("");
			authorizationLimit.setNextTaskId("");
			authorizationLimit.setWorkflowId(0);

			if (authorizationLimit.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				authorizationLimit.setRecordType("");
				getAuthorizationLimitDAO().save(authorizationLimit, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				authorizationLimit.setRecordType("");
				if (hold) {
					getAuthorizationLimitDAO().updateHold(authorizationLimit, TableType.MAIN_TAB);
				} else {
					getAuthorizationLimitDAO().update(authorizationLimit, TableType.MAIN_TAB);
				}
			}

			if (!hold) {
				auditHeader.setAuditDetails(
						processingList(auditHeader.getAuditDetails(), authorizationLimit.getId(), TableType.MAIN_TAB));
			}

		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(authorizationLimit);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getAuthorizationLimitDAO().delete with parameters authorizationLimit,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtAuth_Limits by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AuthorizationLimit authorizationLimit = (AuthorizationLimit) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuthorizationLimitDAO().delete(authorizationLimit, TableType.TEMP_TAB);
		getAuthorizationLimitDetailDAO().deleteByAuthLimitId(authorizationLimit.getId(), TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
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

		AuthorizationLimit authorizationLimit = (AuthorizationLimit) auditHeader.getAuditDetail().getModelData();

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setAuditDetails(getAuditDetails(authorizationLimit, method));
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private List<AuditDetail> getAuditDetails(AuthorizationLimit limitDetail, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		AuthorizationLimitDetail detail = new AuthorizationLimitDetail();

		String[] fields = PennantJavaUtil.getFieldDetails(detail, detail.getExcludeFields());
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (limitDetail.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		for (int i = 0; i < limitDetail.getAuthorizationLimitDetails().size(); i++) {
			AuthorizationLimitDetail authorizationLimitDetail = limitDetail.getAuthorizationLimitDetails().get(i);

			if (StringUtils.isEmpty(authorizationLimitDetail.getRecordType())) {
				continue;
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if ("delete".equals(method)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else if (authorizationLimitDetail.getRecordType()
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (authorizationLimitDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| authorizationLimitDetail.getRecordType()
								.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			authorizationLimitDetail.setUserDetails(limitDetail.getUserDetails());
			authorizationLimitDetail.setLastMntOn(limitDetail.getLastMntOn());
			authorizationLimitDetail.setLastMntBy(limitDetail.getLastMntBy());
			authorizationLimitDetail.setRecordStatus(limitDetail.getRecordStatus());

			auditDetails
					.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], null, authorizationLimitDetail));
		}
		return auditDetails;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getAuthorizationLimitDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		AuthorizationLimit authorizationLimit = (AuthorizationLimit) auditDetail.getModelData();

		// Check the unique keys.
		if (authorizationLimit.isNewRecord()
				&& authorizationLimitDAO.isDuplicateKey(authorizationLimit.getId(), authorizationLimit.getLimitType(),
						authorizationLimit.getUserID(), authorizationLimit.getRoleId(), authorizationLimit.getModule(),
						authorizationLimit.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			if (authorizationLimit.getLimitType() == 1) {
				parameters[0] = PennantJavaUtil.getLabel("AuthorizationLimit");
				parameters[1] = PennantJavaUtil.getLabel("label_UserID") + ": " + authorizationLimit.getUserID();
			} else {
				parameters[0] = PennantJavaUtil.getLabel("AuthorizationLimit");
				parameters[1] = PennantJavaUtil.getLabel("label_RoleId") + ": " + authorizationLimit.getRoleId();
			}

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private List<AuditDetail> processingList(List<AuditDetail> auditDetails, long authLimitId, TableType type) {

		for (int i = 0; i < auditDetails.size(); i++) {

			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;
			AuthorizationLimitDetail detail = (AuthorizationLimitDetail) auditDetails.get(i).getModelData();

			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
			}

			if (detail.getAuthLimitId() == 0) {
				detail.setAuthLimitId(authLimitId);
			}

			if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				if (!approveRec) {
					deleteRecord = true;
				}
			} else if (detail.isNewRecord()) {
				saveRecord = true;
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else {
					updateRecord = true;
				}

			} else {
				updateRecord = true;
			}

			detail.setBefImage(
					getAuthorizationLimitDetailDAO().getAuthorizationLimitDetail(detail.getId(), type.getSuffix()));

			if (approveRec) {
				detail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				detail.setRecordType(null);
			}

			if (saveRecord) {
				getAuthorizationLimitDetailDAO().save(detail, type);
			}

			if (updateRecord) {
				getAuthorizationLimitDetailDAO().update(detail, type);
			}

			if (deleteRecord) {
				getAuthorizationLimitDetailDAO().delete(detail, type);
			}

			auditDetails.get(i).setModelData(detail);
		}
		return auditDetails;
	}

	@Override
	public AuditHeader validateFinanceAuthorizationLimit(AuditHeader auditHeader) {

		// AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		AuditDetail auditDetail = auditHeader.getAuditDetail();
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Date currDate = DateUtil.getSysDate();

		AuthorizationLimit authorizationLimit = getAuthorizationLimitDAO()
				.getLimitForFinanceAuth(financeDetail.getUserDetails().getUserId(), "_AView", true);
		AuthorizationLimitDetail authorizationLimitDetail = null;

		if (authorizationLimit == null) {
			auditDetail.setErrorDetail(new ErrorDetail("93001", null));
			auditHeader.setAuditDetail(auditDetail);
			return auditHeader;
		} else {
			if (!(currDate.compareTo(authorizationLimit.getStartDate()) < 0
					|| currDate.compareTo(authorizationLimit.getExpiryDate()) > 0)) {
				authorizationLimitDetail = authorizationLimitDetailDAO.getAuthorizationLimitDetailByCode(
						authorizationLimit.getId(), financeMain.getFinCategory(), TableType.MAIN_TAB);
			} else {
				auditDetail.setErrorDetail(new ErrorDetail("93003", null));
				auditHeader.setAuditDetail(auditDetail);
				return auditHeader;

			}
		}

		if (authorizationLimitDetail == null) {
			auditDetail.setErrorDetail(new ErrorDetail("93001", null));
			auditHeader.setAuditDetail(auditDetail);
			return auditHeader;
		}

		if (financeMain.getFinAssetValue().compareTo(authorizationLimitDetail.getLimitAmount()) > 0) {
			auditDetail.setErrorDetail(new ErrorDetail("93002", null));
			auditHeader.setAuditDetail(auditDetail);
			return auditHeader;
		}

		return auditHeader;
	}
}