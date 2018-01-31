package com.pennanttech.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennanttech.controller.SecRolesController;
import com.pennanttech.pffws.SecRolesRestService;
import com.pennanttech.pffws.SecRolesSoapService;
import com.pennanttech.ws.model.secRoles.SecurityRoleDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class SecRolesWebServiceImpl implements SecRolesSoapService,SecRolesRestService {
	private final Logger logger = Logger.getLogger(SecRolesWebServiceImpl.class);
	
	private SecRolesController secRolesController;

	/**
	 * get the SecRoles.
	 * 
	 * @return SecRoles
	 */
	@Override
	public SecurityRoleDetail getSecRoles(String test) {
		logger.debug("Entering");

		// for logging purpose
		APIErrorHandlerService.logReference("SecRoles_" + test);

		SecurityRoleDetail response = new SecurityRoleDetail();
		response = secRolesController.getSecRoles();
		
		logger.debug("Leaving");
		return response;
	}

	@Autowired
	public void setSecRolesController(SecRolesController secRolesController) {
		this.secRolesController = secRolesController;
	}
}
