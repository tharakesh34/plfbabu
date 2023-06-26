package com.pennant.api.user.controller;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.APIHeader;
import com.pennant.backend.dao.administration.SecurityOperationDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.administration.SecurityUserOperations;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.administration.SecurityUserOperationsService;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.api.controller.AbstractController;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.AuthenticationType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.util.APIConstants;

public class SecurityUserController extends AbstractController {
	private SecurityUserService securityUserService;
	private SecurityUserOperationsService securityUserOperationsService;
	private SecurityOperationDAO securityOperationDAO;
	private SecurityUserOperationsDAO securityUserOperationsDAO;
	private SecurityUserDAO securityUserDAO;

	public SecurityUser createSecurityUser(SecurityUser user, LoggedInUser ud) {
		logger.debug(Literal.ENTERING);

		prepareRequiredData(user, ud);

		user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		user.setCreatedBy(ud.getUserId());
		user.setApprovedOn(new Timestamp(System.currentTimeMillis()));
		user.setApprovedBy(ud.getUserId());
		user.setPwdExpDt(DateUtil.addDays(new Date(System.currentTimeMillis()), -1));

		if (AuthenticationType.DAO.name().equals(user.getAuthType())) {
			user.setUsrRawPwd(user.getUsrPwd());
			user.setUsrPwd(((PasswordEncoder) SpringBeanUtil.getBean("passwordEncoder")).encode(user.getUsrPwd()));
		}

		AuditHeader ah = getAuditHeader(user, PennantConstants.TRAN_WF);

		securityUserService.doApprove(ah);

		SecurityUser response = new SecurityUser();
		List<ErrorDetail> errors = ah.getErrorMessage();

		if (CollectionUtils.isNotEmpty(errors)) {
			ErrorDetail error = errors.get(errors.size() - 1);
			response.setReturnStatus(getFailedStatus(error.getCode(), error.getError()));

			logger.debug(Literal.LEAVING);
			return response;
		}

		user = (SecurityUser) ah.getAuditDetail().getModelData();
		response.setUsrLogin(user.getUsrLogin());
		response.setUsrID(user.getUsrID());
		response.setReturnStatus(getSuccessStatus());

		doEmptyResponseObject(response);

		logger.debug(Literal.LEAVING);
		return response;
	}

	public WSReturnStatus updateSecurityUser(SecurityUser user, boolean isAllowCluster, LoggedInUser ud) {
		logger.debug(Literal.ENTERING);

		prepareRequiredData(user, ud);

		SecurityUser prvUser = securityUserService.getApprovedSecurityUserById(user.getUsrID());

		user.setRecordType(PennantConstants.RECORD_TYPE_UPD);

		if (!isAllowCluster) {
			setRecordTypeForDivsions(user.getSecurityUserDivBranchList());
		}

		if (!prvUser.getAuthType().equals(user.getAuthType())) {
			return getFailedStatus("92021", "For UpdateSecurityUser User Type cannot be updated");
		}

		user.setCreatedOn(prvUser.getCreatedOn());
		user.setCreatedBy(prvUser.getCreatedBy());
		user.setNewRecord(false);
		user.setVersion(prvUser.getVersion() + 1);
		user.setStatus(PennantConstants.RECORD_TYPE_NEW);
		user.setUsrPwd(prvUser.getUsrPwd());
		user.setApprovedOn(new Timestamp(System.currentTimeMillis()));
		user.setApprovedBy(ud.getUserId());
		if (user.getPwdExpDt() == null) {
			user.setPwdExpDt(prvUser.getPwdExpDt());
		}

		BeanUtils.copyProperties(user, prvUser);

		AuditHeader ah = getAuditHeader(prvUser, PennantConstants.TRAN_WF);

		securityUserService.doApprove(ah);

		List<ErrorDetail> errors = ah.getErrorMessage();
		if (CollectionUtils.isEmpty(ah.getErrorMessage())) {
			logger.debug(Literal.LEAVING);
			return getSuccessStatus();
		}

		ErrorDetail error = errors.get(errors.size() - 1);

		logger.debug(Literal.LEAVING);
		return getFailedStatus(error.getCode(), error.getError());
	}

	public WSReturnStatus addOperation(SecurityUser user) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = validations(user);

		if (returnStatus != null) {
			return returnStatus;
		}

		prepareRequiredData(user);

		long usrID = securityUserDAO.getUserByName(user.getUsrLogin());

		List<Long> operations = securityUserOperationsDAO.getSecUserOperationIdsByUsrID(usrID, "");

		for (SecurityUserOperations operation : user.getSecurityUserOperationsList()) {
			long oprID = securityOperationDAO.getSecurityOperationByCode(operation.getLovDescOprCd());
			if (operations.contains(oprID)) {
				return getFailedStatus("93306", operation.getLovDescOprCd(), user.getUsrLogin());
			} else {
				operation.setUsrID(usrID);
				operation.setOprID(oprID);
				operation.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				operation.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				operation.setNewRecord(true);
				operation.setSourceId(APIConstants.FINSOURCE_ID_API);
			}
		}

		user.setLastMntBy(usrID);

		AuditHeader ah = getAuditHeader(user, PennantConstants.TRAN_WF);

		securityUserOperationsService.doApprove(ah);

		List<ErrorDetail> errors = ah.getErrorMessage();

		if (CollectionUtils.isEmpty(errors)) {
			user.setReturnStatus(getSuccessStatus());
			doEmptyResponseObject(user);

			logger.debug(Literal.LEAVING);
			return user.getReturnStatus();
		}

		ErrorDetail error = errors.get(errors.size() - 1);

		user = new SecurityUser();
		user.setReturnStatus(getFailedStatus(error.getCode(), error.getError()));

		logger.debug(Literal.LEAVING);
		return user.getReturnStatus();
	}

	public WSReturnStatus deleteOperation(SecurityUser user) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = validations(user);

		if (returnStatus != null) {
			return returnStatus;
		}

		long usrID = securityUserDAO.getUserByName(user.getUsrLogin());

		SecurityUser existingUser = securityUserDAO.getSecurityUserByLogin(user.getUsrLogin(), "");

		List<SecurityUserOperations> existingOperations = securityUserOperationsDAO
				.getSecUserOperationsByUsrID(existingUser, "");

		List<SecurityUserOperations> operations = user.getSecurityUserOperationsList();

		for (SecurityUserOperations opr : operations) {
			long oprID = securityOperationDAO.getSecurityOperationByCode(opr.getLovDescOprCd());
			opr.setRecordType(PennantConstants.RCD_DEL);
			opr.setUsrID(usrID);
			opr.setOprID(oprID);

			if (!existingOperations.removeIf(t -> t.getOprID() == oprID)) {
				return getFailedStatus("93305", opr.getLovDescOprCd(), user.getUsrLogin());
			}
		}

		prepareRequiredData(existingUser);

		existingUser.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		existingUser.setNewRecord(false);
		existingUser.setSecurityUserOperationsList(operations);

		try {
			AuditHeader ah = getAuditHeader(existingUser, PennantConstants.TRAN_WF);

			securityUserOperationsService.doApprove(ah);

			List<ErrorDetail> errors = ah.getErrorMessage();

			if (CollectionUtils.isEmpty(errors)) {
				user.setReturnStatus(getSuccessStatus());
				doEmptyResponseObject(user);

				return user.getReturnStatus();
			}

			ErrorDetail error = errors.get(errors.size() - 1);

			user = new SecurityUser();
			user.setReturnStatus(getFailedStatus(error.getCode(), error.getError()));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			setErrorToAPILog(e);
			user = new SecurityUser();
			user.setReturnStatus(getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
		return user.getReturnStatus();
	}

	public WSReturnStatus updateUserStatus(SecurityUser user, boolean isUserEnable) {
		logger.debug(Literal.ENTERING);

		SecurityUser prvUser = securityUserService.getSecurityUserByLogin(user.getUsrLogin());

		if (prvUser == null) {
			return getFailedStatus("90007", String.valueOf(user.getUsrLogin()));
		}

		prvUser.setVersion(prvUser.getVersion() + 1);
		prvUser.setReason(user.getReason());
		prvUser.setDisableReason(user.getDisableReason());

		if (isUserEnable) {
			prvUser.setUsrEnabled(user.isUsrEnabled());
		} else {
			prvUser.setUsrAcExp(user.isUsrAcExp());
		}

		AuditHeader ah = getAuditHeader(prvUser, PennantConstants.TRAN_WF);

		try {
			securityUserService.updateUserStatus(ah);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			setErrorToAPILog(e);
			return getFailedStatus();
		}

		List<ErrorDetail> errors = ah.getErrorMessage();

		if (CollectionUtils.isEmpty(errors)) {
			logger.debug(Literal.LEAVING);
			return getSuccessStatus();
		}

		ErrorDetail error = errors.get(errors.size() - 1);

		logger.debug(Literal.LEAVING);
		return getFailedStatus(error.getCode(), error.getError());
	}

	private WSReturnStatus validations(SecurityUser user) {
		logger.debug(Literal.ENTERING);

		if (user.getUsrID() == 0) {
			return getFailedStatus("90502", "usrID");
		}

		if (!this.securityUserDAO.isUserExist(user.getUsrLogin())) {
			return getFailedStatus("93304", "User");
		}

		for (SecurityUserOperations suo : user.getSecurityUserOperationsList()) {
			if (!this.securityOperationDAO.isOperationExistByOprCode(suo.getLovDescOprCd())) {
				return getFailedStatus("93304", "Operation");
			}
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	private void prepareRequiredData(SecurityUser user, LoggedInUser userDetails) {
		logger.debug(Literal.ENTERING);

		prepareRequiredData(user);

		user.setVersion(1);
		user.setUserDetails(userDetails);
		user.setLastMntBy(userDetails.getUserId());
		user.setUsrDftAppCode(App.CODE);

		if (user.getEmployeeType() == null) {
			user.setEmployeeType(PennantConstants.List_Select);
		}

		if (user.getDisableReason() == null) {
			user.setDisableReason(PennantConstants.List_Select);
		}

		String authType = user.getAuthType();

		if (!authType.isEmpty()) {

			if (authType.equalsIgnoreCase(Labels.getLabel("label_Auth_Type_Internal"))) {
				user.setAuthType(com.pennanttech.pennapps.core.App.AuthenticationType.DAO.name());
			} else if (authType.equalsIgnoreCase(Labels.getLabel("label_Auth_Type_External"))) {
				user.setAuthType(com.pennanttech.pennapps.core.App.AuthenticationType.LDAP.name());
			}
		} else {
			user.setAuthType(com.pennanttech.pennapps.core.App.AuthenticationType.DAO.name());
		}

		logger.debug(Literal.LEAVING);
	}

	private void prepareRequiredData(SecurityUser user) {
		logger.debug(Literal.ENTERING);

		user.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		user.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		user.setNewRecord(true);
		user.setUsrDftAppId(1);
		user.setSourceId(APIConstants.FINSOURCE_ID_API);
		user.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		logger.debug(Literal.LEAVING);
	}

	private void setRecordTypeForDivsions(List<SecurityUserDivBranch> securityUserDivBranchList) {
		securityUserDivBranchList.forEach(sud -> sud.setRecordType(PennantConstants.RECORD_TYPE_UPD));
	}

	private AuditHeader getAuditHeader(SecurityUser user, String tranType) {
		AuditDetail ad = new AuditDetail(tranType, 1, user.getBefImage(), user);

		String usrID = String.valueOf(user.getUsrID());

		AuditHeader ah = new AuditHeader(usrID, usrID, null, null, ad, user.getUserDetails(), new HashMap<>());
		ah.setApiHeader(PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY));

		return ah;
	}

	private void doEmptyResponseObject(SecurityUser response) {
		response.setUserType(null);
		response.setUsrFName(null);
		response.setUsrMName(null);
		response.setUsrLName(null);
		response.setUserStaffID(null);
		response.setUsrMobile(null);
		response.setUsrEmail(null);
		response.setUsrLanguage(null);
		response.setUsrAcExpDt(null);
		response.setUsrBranchCode(null);
		response.setUsrDftAppCode(null);
		response.setUsrDeptCode(null);
		response.setUsrDesg(null);
		response.setEmployeeType(null);
		response.setDisableReason(null);
		response.setBusinessVerticalCode(null);
	}

	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}

	public void setSecurityUserOperationsService(SecurityUserOperationsService securityUserOperationsService) {
		this.securityUserOperationsService = securityUserOperationsService;
	}

	public void setSecurityOperationDAO(SecurityOperationDAO securityOperationDAO) {
		this.securityOperationDAO = securityOperationDAO;
	}

	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	public void setSecurityUserOperationsDAO(SecurityUserOperationsDAO securityUserOperationsDAO) {
		this.securityUserOperationsDAO = securityUserOperationsDAO;
	}

}