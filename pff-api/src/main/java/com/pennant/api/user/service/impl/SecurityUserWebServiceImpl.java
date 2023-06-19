package com.pennant.api.user.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.api.user.controller.SecurityUserController;
import com.pennant.api.user.service.SecurityUserRestService;
import com.pennant.api.user.service.SecurityUserSoapService;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.api.service.AbstractService;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;

@Service
public class SecurityUserWebServiceImpl extends AbstractService
		implements SecurityUserRestService, SecurityUserSoapService {

	private SecurityUserService securityUserService;
	private SecurityUserController securityUserController;
	private SecurityUserOperationsDAO securityUserOperationsDAO;

	@Override
	public SecurityUser createSecurityUser(SecurityUser user) throws ServiceException {
		logger.debug(Literal.ENTERING);

		boolean isAllowCluster = SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER);
		LoggedInUser liu = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		user.setRequestSource(RequestSource.API);
		AuditHeader ah = getAuditHeader(user, PennantConstants.TRAN_WF);

		AuditDetail ad = securityUserService.doUserValidation(ah, isAllowCluster, false, liu);

		ah.setAuditDetail(ad);
		ah.setErrorList(ad.getErrorDetails());

		List<ErrorDetail> errors = ah.getErrorMessage();
		if (CollectionUtils.isNotEmpty(errors)) {
			ErrorDetail ed = errors.get(errors.size() - 1);

			SecurityUser secUser = new SecurityUser();
			secUser.setReturnStatus(getFailedStatus(ed.getCode(), ed.getError()));
			return secUser;
		}

		user.setRequestSource(RequestSource.API);
		SecurityUser response = securityUserController.createSecurityUser(user, liu);

		logKeyFields(user.getUsrLogin(), user.getUsrFName());

		if (response.getUsrLogin() != null) {
			logReference(String.valueOf(response.getUsrLogin()));
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus updateSecurityUser(SecurityUser user) throws ServiceException {
		logger.debug(Literal.ENTERING);

		logKeyFields(user.getUsrLogin(), user.getUsrFName());

		if (user.getUsrID() <= 0) {
			return getFailedStatus("90502", "usrId");
		}

		SecurityUser secUser = securityUserService.getApprovedSecurityUserById(user.getUsrID());

		if (secUser == null) {
			return getFailedStatus("93303", String.valueOf(user.getUsrID()));
		}

		if (!StringUtils.equals(secUser.getUsrLogin(), user.getUsrLogin())) {
			return getFailedStatus("93307", String.valueOf(user.getUsrID()), String.valueOf(user.getUsrLogin()));
		}

		boolean isAllowCluster = SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER);
		LoggedInUser liu = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		AuditHeader ah = getAuditHeader(user, PennantConstants.TRAN_WF);

		logReference(String.valueOf(user.getUsrLogin()));

		AuditDetail ad = securityUserService.doUserValidation(ah, isAllowCluster, true, liu);
		ah.setAuditDetail(ad);
		ah.setErrorList(ad.getErrorDetails());

		if (ah.getErrorMessage() != null) {
			ErrorDetail ed = ah.getErrorMessage().get(0);
			return getFailedStatus(ed.getCode(), ed.getError());
		}

		user.setRequestSource(RequestSource.API);

		WSReturnStatus returnStatus = securityUserController.updateSecurityUser(user, isAllowCluster, liu);

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	@Override
	public SecurityUser getSecurityUser(SecurityUser securityUser) {
		logger.debug(Literal.ENTERING);

		SecurityUser response;

		String userName = securityUser.getUsrLogin();

		if (StringUtils.isEmpty(userName)) {
			response = new SecurityUser();
			response.setReturnStatus(getFailedStatus("90502", "Login Name"));
			return response;
		}

		response = securityUserService.getSecurityUserByLogin(userName);

		if (response == null) {
			response = new SecurityUser();
			response.setReturnStatus(getFailedStatus("92021", "There is no approved User with requested Login Name"));
		}

		List<SecurityUserDivBranch> divBranchList = securityUserService.getSecUserDivBrList(response.getUsrID(), "");
		prepareSecurityBranch(divBranchList);
		response.setSecurityUserDivBranchList(divBranchList);
		response.setSecurityUserOperationsList(
				securityUserOperationsDAO.getSecUserOperationsByUsrID(response, "_View"));
		response.setReturnStatus(getSuccessStatus());
		response.setUsrPwd(null);

		return response;
	}

	private void prepareSecurityBranch(List<SecurityUserDivBranch> divBranchList) {
		List<Entity> entities = new ArrayList<>();
		List<Branch> branches = new ArrayList<>();
		List<Cluster> clusters = new ArrayList<>();

		for (SecurityUserDivBranch divBranch : divBranchList) {
			switch (divBranch.getAccessType()) {
			case PennantConstants.ACCESSTYPE_ENTITY:
				Entity entity = new Entity();
				entity.setEntityCode(divBranch.getEntity());
				entities.add(entity);
				divBranch.setEntity(null);
				divBranch.setClusterList(null);
				divBranch.setBranchList(null);
				break;
			case PennantConstants.ACCESSTYPE_CLUSTER:
				Cluster cluster = new Cluster();
				cluster.setCode(divBranch.getClusterCode());
				clusters.add(cluster);
				divBranch.setClusterCode(null);
				divBranch.setEntitiyList(null);
				divBranch.setBranchList(null);
				break;
			case PennantConstants.ACCESSTYPE_BRANCH:
				Branch branch = new Branch();
				branch.setBranchCode(divBranch.getUserBranch());
				branches.add(branch);
				divBranch.setUserBranch(null);
				divBranch.setEntitiyList(null);
				divBranch.setClusterList(null);
				break;
			default:
				break;
			}
		}

		for (SecurityUserDivBranch divBranch : divBranchList) {
			switch (divBranch.getAccessType()) {
			case PennantConstants.ACCESSTYPE_ENTITY:
				divBranch.setEntitiyList(entities.stream().distinct().toList());
				break;
			case PennantConstants.ACCESSTYPE_CLUSTER:
				divBranch.setClusterList(clusters.stream().distinct().toList());
				// list.stream().filter( distinctByKey(p -> p.getFname() + " " + p.getLname()) ).collect(
				// Collectors.toList() )
				break;
			case PennantConstants.ACCESSTYPE_BRANCH:
				divBranch.setBranchList(branches.stream().distinct().toList());
				break;
			default:
				break;
			}
		}

	}

	private AuditHeader getAuditHeader(SecurityUser user, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, user.getBefImage(), user);
		return new AuditHeader(String.valueOf(user.getUsrID()), String.valueOf(user.getUsrID()), null, null,
				auditDetail, user.getUserDetails(), new HashMap<>());
	}

	@Override
	public WSReturnStatus addOperation(SecurityUser user) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = securityUserController.addOperation(user);

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus deleteOperation(SecurityUser user) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = securityUserController.deleteOperation(user);

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus enableUser(SecurityUser user) throws ServiceException {
		return updateUserStatus(user, true, false);
	}

	@Override
	public WSReturnStatus expireUser(SecurityUser user) throws ServiceException {
		return updateUserStatus(user, false, true);
	}

	private WSReturnStatus updateUserStatus(SecurityUser user, boolean isUserEnable, boolean isFromUserExpire) {
		logger.debug(Literal.ENTERING);

		AuditHeader ah = getAuditHeader(user, PennantConstants.TRAN_WF);

		logKeyFields(user.getUsrLogin(), user.getUsrFName());

		logReference(String.valueOf(user.getUsrLogin()));

		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		AuditDetail ad = securityUserService.doValidation(ah, loggedInUser, isFromUserExpire);

		ah.setAuditDetail(ad);

		List<ErrorDetail> errors = ad.getErrorDetails();
		ah.setErrorList(errors);

		if (CollectionUtils.isNotEmpty(errors)) {
			ErrorDetail error = errors.get(0);
			logger.debug(Literal.LEAVING);

			return getFailedStatus(error.getCode(), error.getError());
		}

		logger.debug(Literal.LEAVING);
		return securityUserController.updateUserStatus(user, isUserEnable);
	}

	@Autowired
	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}

	@Autowired
	public void setSecurityUserController(SecurityUserController securityUserController) {
		this.securityUserController = securityUserController;
	}

	@Autowired
	public void setSecurityUserOperationsDAO(SecurityUserOperationsDAO securityUserOperationsDAO) {
		this.securityUserOperationsDAO = securityUserOperationsDAO;
	}
}
