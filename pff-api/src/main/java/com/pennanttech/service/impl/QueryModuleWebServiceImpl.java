/**
 * 
 */
package com.pennanttech.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.validation.QueryDetailGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.QueryModuleController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.QueryModuleRestService;
import com.pennanttech.pffws.QueryModuleSoapService;
import com.pennanttech.ws.service.APIErrorHandlerService;

/**
 * @author srikanth.m
 *
 */
public class QueryModuleWebServiceImpl implements QueryModuleRestService, QueryModuleSoapService {
	private static final Logger logger = Logger.getLogger(FinInstructionServiceImpl.class);
	private QueryModuleController queryModuleController;
	private ValidationUtility validationUtility;
	private QueryDetailService queryDetailService;
	private SecurityUserService securityUserService;

	@Override
	public WSReturnStatus updateQueryRequest(QueryDetail queryDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		QueryDetail qryDetail = null;
		WSReturnStatus returnStatus = null;

		// bean validations
		validationUtility.validate(queryDetail, QueryDetailGroup.class);

		// for logging purpose
		APIErrorHandlerService.logReference(queryDetail.getFinReference());

		if (!StringUtils.equals(queryDetail.getStatus(), "Resolve")) {
			returnStatus = new WSReturnStatus();
			String[] paramValue = new String[2];
			paramValue[0] = "Status: " + queryDetail.getStatus();
			paramValue[1] = "Update";
			return APIErrorHandlerService.getFailedStatus("90204", paramValue);
		}
		qryDetail = queryDetailService.getQueryDetail(queryDetail.getId());

		if (qryDetail == null) {
			returnStatus = new WSReturnStatus();
			String[] paramValue = new String[1];
			paramValue[0] = "Id:" + queryDetail.getId();
			return APIErrorHandlerService.getFailedStatus("90266", paramValue);

		}

		//status of the record validation
		if (!StringUtils.equals(qryDetail.getStatus(), "Open")) {
			returnStatus = new WSReturnStatus();
			String[] paramValue = new String[2];
			paramValue[0] = "Update";
			paramValue[1] = qryDetail.getStatus() + " status record";
			return APIErrorHandlerService.getFailedStatus("90329", paramValue);
		}

		//login user validation
		String userLogin = queryDetail.getUsrLogin();
		if (StringUtils.isNotBlank(userLogin)) {
			SecurityUser securityUser = securityUserService.getSecurityUserByLogin(userLogin.toUpperCase());

			if (securityUser == null) {
				returnStatus = new WSReturnStatus();
				String[] paramValue = new String[2];
				paramValue[0] = "usrLogin";
				paramValue[1] = queryDetail.getUsrLogin();
				return APIErrorHandlerService.getFailedStatus("90224", paramValue);
			}
			//set userId
			queryDetail.setRaisedBy(securityUser.getUsrID());
		}
		//set data to queryDetail for update 
		queryDetail.setCloserNotes(qryDetail.getCloserNotes());
		queryDetail.setCloserBy(qryDetail.getCloserBy());
		queryDetail.setCloserOn(qryDetail.getCloserOn());
		queryDetail.setVersion(qryDetail.getVersion());
		queryDetail.setModule(qryDetail.getModule());
		queryDetail.setReference(qryDetail.getReference());

		logger.debug(Literal.LEAVING);
		return queryModuleController.doQueryUpdate(queryDetail);

	}

	public QueryModuleController getQueryModuleController() {
		return queryModuleController;
	}

	@Autowired
	public void setQueryModuleController(QueryModuleController queryModuleController) {
		this.queryModuleController = queryModuleController;
	}

	public ValidationUtility getValidationUtility() {
		return validationUtility;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	public QueryDetailService getQueryDetailService() {
		return queryDetailService;
	}

	@Autowired
	public void setQueryDetailService(QueryDetailService queryDetailService) {
		this.queryDetailService = queryDetailService;
	}

	@Autowired
	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}
}
