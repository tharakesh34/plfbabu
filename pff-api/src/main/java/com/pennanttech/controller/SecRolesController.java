package com.pennanttech.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.service.administration.SecurityRoleService;
import com.pennanttech.ws.model.secRoles.SecurityRoleDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class SecRolesController extends ExtendedTestClass {
	private final Logger logger = LogManager.getLogger(SecRolesController.class);

	private SecurityRoleService securityRoleService;

	/**
	 * get the SecurityRole Details
	 * 
	 * @return SecurityRole Detail
	 */
	public SecurityRoleDetail getSecRoles() {
		logger.debug("Entering");

		SecurityRoleDetail response = null;
		try {
			List<SecurityRole> secRoleList = securityRoleService.getApprovedRoles();
			if (!secRoleList.isEmpty()) {
				response = new SecurityRoleDetail();
				response.setSecRoleList(secRoleList);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new SecurityRoleDetail();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", "No data available"));
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			SecurityRoleDetail secRoles = new SecurityRoleDetail();
			secRoles.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Entering");

		return response;
	}

	@Autowired
	public void setSecurityRoleService(SecurityRoleService securityRoleService) {
		this.securityRoleService = securityRoleService;
	}

}
